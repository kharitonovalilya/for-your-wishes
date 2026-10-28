package com.dev.foryourwishes.security.auth;

import com.dev.foryourwishes.security.AuthUser;
import com.dev.foryourwishes.security.jwt.TokenService;
import com.dev.foryourwishes.user.RegisterUserRequest;
import com.dev.foryourwishes.user.User;
import com.dev.foryourwishes.user.UserManagerService;
import com.dev.foryourwishes.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

// TODO: Spring Security exception responses with API ProblemDetail format
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserManagerService userManagerService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody RegisterUserRequest request) {
        User user = userManagerService.create(
                request.email(), request.login(), request.password()
        );
        return new UserResponse(user.getId(), user.getEmail(), user.getLogin());
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.login(),
                        request.password()
                );

        Authentication authenticationResponse =
                authenticationManager.authenticate(authenticationRequest);

        AuthUser user = (AuthUser) authenticationResponse.getPrincipal();

        String accessToken = tokenService.generateAccessToken(user);

        return new TokenResponse(accessToken);
    }

}
