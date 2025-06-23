package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserModifyRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserPasswordChangeRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.*;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.PropertyRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * This class implements the UserService interface and provides methods for managing users.
 * It uses the UserRepository and PropertyRepository to interact with the database.
 */
@Service
@AllArgsConstructor
public class UserServiceImplementation implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final LocalStorageService localStorageService;

    @Override
    public CustomResponse modify(@NonNull UserModifyRequest userModifyRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isActive()) {
            throw new EntityNotFoundException(User.class);
        }

        if (!passwordEncoder.matches(userModifyRequest.getPassword(), user.getPassword())) {
            throw new DataValidationException("The password entered is incorrect");
        }

        // Verifica che username e email non siano già utilizzati da altri utenti
        if (!userModifyRequest.getUsername().equals(user.getUsername()) &&
                userRepository.findByUsername(userModifyRequest.getUsername()).isPresent()) {
            throw new EntityModifyException("Username already in use");
        }

        if (!userModifyRequest.getEmail().equals(user.getEmail()) &&
                userRepository.findByEmail(userModifyRequest.getEmail()).isPresent()) {
            throw new EntityModifyException("Email already in use");
        }

        user.setUsername(userModifyRequest.getUsername());
        user.setEmail(userModifyRequest.getEmail());

        try {
            userRepository.save(user);
        } catch (Exception e) {
            return new CustomResponse("Error while modifying user");
        }

        return new CustomResponse("User modified successfully");
    }

    @Override
    public CustomResponse modifyWithImage(@NonNull UserModifyRequest userModifyRequest, MultipartFile image) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isActive()) {
            throw new EntityNotFoundException(User.class);
        }

        if (!passwordEncoder.matches(userModifyRequest.getPassword(), user.getPassword())) {
            throw new DataValidationException("The password entered is incorrect");
        }

        // Verifica che username e email non siano già utilizzati da altri utenti
        if (!userModifyRequest.getUsername().equals(user.getUsername()) &&
                userRepository.findByUsername(userModifyRequest.getUsername()).isPresent()) {
            throw new EntityModifyException("Username already in use");
        }

        if (!userModifyRequest.getEmail().equals(user.getEmail()) &&
                userRepository.findByEmail(userModifyRequest.getEmail()).isPresent()) {
            throw new EntityModifyException("Email already in use");
        }

        user.setUsername(userModifyRequest.getUsername());
        user.setEmail(userModifyRequest.getEmail());

        // Aggiorna la foto profilo
        if (image != null && !image.isEmpty()) {
            String newPhotoPath = localStorageService.updateUserProfilePhoto(image, user.getImageUrl());
            user.setImageUrl(newPhotoPath);
        }

        try {
            userRepository.save(user);
        } catch (Exception e) {
            return new CustomResponse("Error while modifying user with image");
        }

        return new CustomResponse("User modified successfully with image");
    }

    @Override
    public CustomResponse changePassword(@NonNull UserPasswordChangeRequest userPasswordChangeRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isActive()) {
            throw new EntityNotFoundException(User.class);
        }

        // Rimuovere .get() poiché user è già un'istanza di User
        if (!passwordEncoder.matches(userPasswordChangeRequest.getCurrentPassword(), user.getPassword())) {
            return new CustomResponse("The old password is not correct");
        }

        if (!userPasswordChangeRequest.getNewPassword().equals(userPasswordChangeRequest.getRepeatNewPassword())) {
            return new CustomResponse("The passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(userPasswordChangeRequest.getNewPassword()));

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new EntityModifyException(User.class);
        }

        return new CustomResponse("Password changed successfully");
    }

    @Override
    public CustomResponse disable(@NonNull Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new EntityNotFoundException(User.class);
        }

        if(!user.get().isActive()) {
            throw new UserDisabledException("This user is already disabled");
        }

        user.get().setActive(false);

        try {
            userRepository.save(user.get());
        } catch (Exception e) {
            throw new EntityNotFoundException(User.class);
        }

        return new CustomResponse("User disabled successfully");
    }

    @Override
    public CustomResponse enable(@NonNull Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new EntityNotFoundException(User.class);
        }

        if (user.get().isActive()) {
            throw new UserEnabledException("This user is already enabled");
        }

        user.get().setActive(true);

        try {
            userRepository.save(user.get());
        } catch (Exception e) {
            throw new EntityNotFoundException(User.class);
        }

        return new CustomResponse("User enabled successfully");
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(User.class));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(User.class));
    }

    @Override
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public User addFavoriteProperty(String username, Long propertyId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(User.class));
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new EntityNotFoundException(Property.class));
        if (!user.getFavorite().contains(property)) {
            user.getFavorite().add(property);
        }
        return userRepository.save(user);
    }

    @Override
    public User removeFavoriteProperty(String username, Long propertyId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(User.class));
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new EntityNotFoundException(Property.class));
        user.getFavorite().remove(property);
        return userRepository.save(user);
    }

    @Override
    public List<Property> getAllFavoriteProperties(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(User.class));
        return user.getFavorite();
    }

    @Transactional
    public User rechargeBalance(String username, BigDecimal amount) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(User.class));

        user.setBalance(user.getBalance().add(amount));

        return userRepository.save(user);
    }

    @Transactional
    public boolean deductBalance(String username, BigDecimal amount) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(User.class));

        if (user.getBalance().compareTo(amount) < 0) {
            return false;
        }

        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);
        return true;
    }
}