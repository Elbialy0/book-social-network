package com.elbialy.book.auth;

import com.elbialy.book.exceptions.EmailAlreadyExist;
import com.elbialy.book.role.RoleRepository;
import com.elbialy.book.user.Token;
import com.elbialy.book.user.TokenRepository;
import com.elbialy.book.user.User;
import com.elbialy.book.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationService {
    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;

    public void register( RegisterRequest registerRequest) {
        var roleUser = roleRepository.findByName("USER")
                .orElseThrow(()-> new IllegalStateException("Role does not exist"));
        Optional<User> email = userRepository.findByEmail(registerRequest.getEmail());
        if (email.isPresent()){
            throw new EmailAlreadyExist(registerRequest.getEmail()+" is already registered");
        }
        var user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(roleUser))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);

    }

    private void sendValidationEmail(User user) {
        var token = generateAndSaveActivationToken(user);
    }

    private Token generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .activatedAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build();
        tokenRepository.save(token);
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
}
