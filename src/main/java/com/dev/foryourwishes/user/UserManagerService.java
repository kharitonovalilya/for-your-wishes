package com.dev.foryourwishes.user;

import com.dev.foryourwishes.user.exception.EmailAlreadyExistsException;
import com.dev.foryourwishes.user.exception.LoginAlreadyExistsException;
import com.dev.foryourwishes.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserManagerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Transactional
    public User create(String email, String login, String password) {
        if (userRepository.existsByLogin(login)) {
            throw new LoginAlreadyExistsException(login);
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
        String passwordHash = passwordEncoder.encode(password);
        User user = new User(email, login, passwordHash);
        return userRepository.save(user);
    }
}
