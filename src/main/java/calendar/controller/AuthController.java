package calendar.controller;

import calendar.controller.request.UserRequest;
import calendar.controller.response.BaseResponse;
import calendar.entities.DTO.*;
import calendar.entities.User;
import calendar.entities.enums.ProviderType;
import calendar.eventNotifications.NotificationPublisher;
import calendar.service.AuthService;
import calendar.utils.InputValidation;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLDataException;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    public NotificationPublisher notificationPublisher;
    @Autowired
    private static final Logger logger = LogManager.getLogger(AuthController.class.getName());

    /**
     * Creates a User and saves it to the database (enabled=0).
     * @param userRequest - All the information of the user we wish to register.
     * @return The user in DTO format with his information.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/signup")
    public ResponseEntity<BaseResponse<UserDTO>> register(@RequestBody UserRequest userRequest ) {
        logger.info("in register()");

        if (!InputValidation.isValidEmail(userRequest.getEmail())) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid email address!"));
        }
        if (!InputValidation.isValidName(userRequest.getName())) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid name!"));
        }
        if (!InputValidation.isValidPassword(userRequest.getPassword())) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid password!"));
        }

        try {
            User createdUser = authService.createUser(userRequest, ProviderType.LOCAL);
            notificationPublisher.publishRegistrationNotification(createdUser.getEmail());
            return ResponseEntity.ok(BaseResponse.success(new UserDTO(createdUser)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Email already exists"));
        }
    }

    /**
     * User logs in into our system with his email and password.
     * @param userRequest -  All the information of the user we wish to log in.
     * @return LoginData: Information of the logged-in user.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public  ResponseEntity<BaseResponse<LoginDataDTO>> login(@RequestBody UserRequest userRequest) {
        logger.info("in login()");

        Optional<LoginDataDTO> loginData = authService.login(userRequest);

        if (!InputValidation.isValidEmail(userRequest.getEmail())) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid email address!"));
        }
        if (!InputValidation.isValidPassword(userRequest.getPassword())) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid password!"));
        }

        return loginData.map(value -> ResponseEntity.ok(BaseResponse.success(value))).
                orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("Failed to log in: Wrong Email or Password")));
    }

    /**
     * User logs in into our system with his GitHub.
     * @param code -  A code given by the GitHub API to proceed with the login process.
     * @return LoginData: user id and token.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/loginGithub")
    public ResponseEntity<BaseResponse<LoginDataDTO>> loginGithub(@RequestParam String code) throws SQLDataException {
        logger.info("in loginGithub()");

        Optional<LoginDataDTO> loginData = authService.loginGithub(code);

        if (loginData == null || !loginData.isPresent()) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Failed to log in with github"));
        }

        logger.info("User github has logged in");
        logger.info("login data: " + loginData);

        return ResponseEntity.ok(BaseResponse.success(loginData.get()));
    }
}
