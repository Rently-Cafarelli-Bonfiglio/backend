package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserLoginRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserRegistrationRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.LoginResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * This interface defines the contract for authentication services.
 * It includes methods for user registration and authentication.
 */
public interface AuthService {

    /**
     * Registers a new user with the provided registration request and image.
     *
     * @param userRegistrationRequest the request containing user registration details
     * @param image the profile image of the user
     * @return a CustomResponse indicating the result of the registration
     */

    CustomResponse registerUser(UserRegistrationRequest userRegistrationRequest, MultipartFile image);

    /**
     * Authenticates a user with the provided login request.
     *
     * @param userLoginRequest the request containing user login details
     * @return a LoginResponse containing authentication details
     */

    LoginResponse authenticateUser(UserLoginRequest userLoginRequest);
}
