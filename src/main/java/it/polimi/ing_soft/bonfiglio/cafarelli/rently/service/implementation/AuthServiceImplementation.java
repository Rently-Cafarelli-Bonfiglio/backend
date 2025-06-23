package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation.UserBuilderImplementation;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserLoginRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserRegistrationRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.LoginResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityRegistrationException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.UserDisabledException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Role;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.security.JwtService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.AuthService;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class implements the AuthService interface and provides methods for user registration and authentication.
 * It uses the UserRepository to interact with the database and the PasswordEncoder to encode passwords.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LocalStorageService localStorageService;

    @Transactional
    @Override
    public CustomResponse registerUser(@NonNull UserRegistrationRequest userRegistrationRequest, MultipartFile image) {
        if(userRepository.findByUsernameOrEmail(userRegistrationRequest.getUsername(), userRegistrationRequest.getEmail()).isPresent()) {
            throw new EntityRegistrationException("Username or email already in use");
        }

        User newUser = new UserBuilderImplementation()
                .firstname(userRegistrationRequest.getFirstname())
                .lastname(userRegistrationRequest.getLastname())
                .username(userRegistrationRequest.getUsername())
                .email(userRegistrationRequest.getEmail())
                .password(passwordEncoder.encode(userRegistrationRequest.getPassword()))
                .balance(BigDecimal.valueOf(0))
                .role(Role.CLIENT)
                .imageUrl(localStorageService.getDefaultUserPhoto())
                .isActive(true)
                .build();

        // Aggiorna la foto profilo
        if (image != null && !image.isEmpty()) {
            String newPhotoPath = localStorageService.saveUserProfilePhoto(image);
            newUser.setImageUrl(newPhotoPath);
        }

        try {
            userRepository.save(newUser);
        } catch (Exception e) {
           throw new EntityRegistrationException("Could not register user");
        }

        return new CustomResponse("User registered successfully");
    }

    @Override
    public LoginResponse authenticateUser(@NonNull UserLoginRequest userLoginRequest) {
        Optional<User> user = userRepository.findByUsernameOrEmail(userLoginRequest.getUsername(), userLoginRequest.getUsername());

        if(user.isEmpty()) {
            throw new EntityNotFoundException(User.class);
        }

        if(!user.get().isActive()){
            throw new UserDisabledException("The user is disabled");
        }

        if(!passwordEncoder.matches(userLoginRequest.getPassword(), user.get().getPassword())) {
            throw new EntityRegistrationException("Invalid password");
        }

       Map<String, Object> claims = new HashMap<>();
       claims.put("role", user.get().getRole().name());


        String token = jwtService.generateToken(claims, user.get());

        return new LoginResponse(token);
    }
}
