package com.elbialy.book.auth;

import com.elbialy.book.exceptions.EmailAlreadyExist;
import com.elbialy.book.projectSecurityConfiguration.JwtService;
import com.elbialy.book.role.RoleRepository;
import com.elbialy.book.user.Token;
import com.elbialy.book.user.TokenRepository;
import com.elbialy.book.user.User;
import com.elbialy.book.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationService {
    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    String activationUrl = "http://localhost:8080/api/v1/auth/activation";

    public void register( RegisterRequest registerRequest) throws MessagingException {
        var roleUser = roleRepository.findByName("USER")
                .orElseThrow(()-> new IllegalStateException("Role does not exist"));
        Optional<User> email = userRepository.findByEmail(registerRequest.getEmail());
        if (email.isPresent()){
            throw new EmailAlreadyExist(registerRequest.getEmail()+" is already registered");
        }
        var user = User.builder()
                .firstName(registerRequest.getFirstname())
                .lastName(registerRequest.getLastname())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(roleUser))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);

    }

    private void sendValidationEmail(User user) throws MessagingException {
        var token = generateAndSaveActivationToken(user);
    }

    private Token generateAndSaveActivationToken(User user) throws MessagingException {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build();
        tokenRepository.save(token);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                generatedToken,
                "Account activation"

        );
        return token;
    }

    private String generateActivationCode(int length) {
        String characters = "01234567890";
        StringBuilder activationCode = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            activationCode.append(characters.charAt(randomIndex));
        }
        return activationCode.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {

            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                            authenticationRequest.getPassword() ));
            var user = (User)auth.getPrincipal();
            Map<String, Object> claims = new HashMap<>();
            claims.put("fullname",user.getFullName());
            String jwt = jwtService.generateToken(claims,user);
            return AuthenticationResponse.builder().token(jwt).build();



    }
}
