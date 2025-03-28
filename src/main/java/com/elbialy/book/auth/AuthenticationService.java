package com.elbialy.book.auth;

import com.elbialy.book.user.User;
import com.elbialy.book.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationService {
    private final UserRepository  userRepository;

    public void register(@Valid RegisterRequest registerRequest) {
        User user =
    }
}
