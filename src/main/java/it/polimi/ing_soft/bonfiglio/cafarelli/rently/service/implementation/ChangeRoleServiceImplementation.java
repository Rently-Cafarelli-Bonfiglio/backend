package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.ChangeRoleResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityModifyException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRole;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRoleStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Role;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.observer.EventManager;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.ChangeRoleRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.ChangeRoleService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChangeRoleServiceImplementation implements ChangeRoleService {

    private final UserRepository userRepository;
    private final ChangeRoleRepository changeRoleRepository;
    private final EventManager eventManager;

    @Override
    public CustomResponse requestChangeRole(String motivation) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Get the user making the request
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        // Check if the user already has a pending request
        Optional<ChangeRole> existingRequest = changeRoleRepository.findByUserAndStatus(user, ChangeRoleStatus.PENDING);
        if (existingRequest.isPresent()) {
            throw new EntityModifyException("You already have a pending role change request.");
        }

        // Check if the user is already a HOST or higher role
        if (user.getRole() != Role.CLIENT) {
            return new CustomResponse("You can only request a role change from CLIENT to HOST.");
        }

        // Create a new change role request
        ChangeRole changeRole = new ChangeRole(user, motivation);
        changeRoleRepository.save(changeRole);

        return new CustomResponse("Role change request submitted successfully.");
    }

    @Override
    public CustomResponse acceptChangeRole(Long requestId) {
        // Get the current user (admin)
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        // Check if the current user is an ADMIN
        if (admin.getRole() != Role.ADMIN) {
            return new CustomResponse("Only ADMIN users can accept role change requests.");
        }

        // Get the change role request
        ChangeRole changeRole = changeRoleRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(ChangeRole.class));

        // Check if the request is in PENDING state
        if (changeRole.getStatus() != ChangeRoleStatus.PENDING) {
            return new CustomResponse("This request has already been " + changeRole.getStatus().name().toLowerCase() + ".");
        }

        // Accept the request
        changeRole.accept();
        changeRole.setFullfilledBy(admin);
        changeRole.setFullfilledAt(LocalDateTime.now());
        changeRoleRepository.save(changeRole);

        // Notify the user about the role change
        eventManager.notify("CHANGEROLE_ACCEPTED", changeRole);

        // Update the user's role
        User user = changeRole.getUser();
        user.setRole(Role.HOST);
        userRepository.save(user);

        return new CustomResponse("Role change request accepted successfully. User " + user.getUsername() + " is now a HOST.");
    }

    @Override
    public CustomResponse rejectChangeRole(Long requestId, String motivation) {
        // Get the current user (admin)
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        // Check if the current user is an ADMIN
        if (admin.getRole() != Role.ADMIN) {
            return new CustomResponse("Only ADMIN users can reject role change requests.");
        }

        // Get the change role request
        ChangeRole changeRole = changeRoleRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(ChangeRole.class));

        // Check if the request is in PENDING state
        if (changeRole.getStatus() != ChangeRoleStatus.PENDING) {
            return new CustomResponse("This request has already been " + changeRole.getStatus().name().toLowerCase() + ".");
        }

        // Reject the request
        changeRole.reject();
        changeRole.setFullfilledBy(admin);
        changeRole.setFullfilledAt(LocalDateTime.now());
        changeRole.setMotivation(motivation); // Update with rejection motivation
        changeRoleRepository.save(changeRole);

        // Notify the user about the rejection
        eventManager.notify("CHANGEROLE_REJECTED", changeRole);

        return new CustomResponse("Role change request rejected successfully.");
    }
    @Override
    public List<ChangeRoleResponse> findAll() {
        List<ChangeRole> pendingRequests = changeRoleRepository.findAll();

        return pendingRequests.stream()
                .map(changeRole -> new ChangeRoleResponse(
                        changeRole.getId(),
                        changeRole.getUser().getUsername(),
                        changeRole.getMotivation(),
                        changeRole.getStatus().toString()
                ))
                .collect(Collectors.toList());
    }

}