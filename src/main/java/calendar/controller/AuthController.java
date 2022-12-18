package calendar.controller;

import calendar.controller.request.UserRequest;
import calendar.controller.response.BaseResponse;
import calendar.controller.response.GitToken;
import calendar.entities.DTO.LoginDataDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.enums.ProviderType;
import calendar.service.AuthService;
import calendar.utils.GMailer;
import calendar.utils.InputValidation;
import calendar.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLDataException;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
//    private UserService userService;
    private static final Logger logger = LogManager.getLogger(AuthController.class.getName());

    /**
     * Creates a User and saves it to the database (enabled=0).
     * @param userRequest
     * @return The user
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
            UserDTO createdUser = authService.createUser(userRequest, ProviderType.LOCAL);
//            authService.publishRegistrationEvent(createdUser, request.getLocale(), request.getContextPath());
            return ResponseEntity.ok(BaseResponse.success(createdUser));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Email already exists"));
        }
    }

    /**
     * User logs in to the system with Email and Password
     * @param userRequest
     * @return LoginData: user id and token
     */
    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public  ResponseEntity<BaseResponse<LoginDataDTO>> login(@RequestBody UserRequest userRequest) {
        logger.info("in login()");

        Optional<LoginDataDTO> loginData = authService.login(userRequest);

        logger.info("User with email " + userRequest.getEmail() + " has logged in");

        return loginData.map(value -> ResponseEntity.ok(BaseResponse.success(value))).
                orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("Failed to log in: Wrong Email or Password")));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/testemail")
    public ResponseEntity<BaseResponse<String>> testEmail()  {
        logger.info("in testEmail()");

        try {
            GMailer.sendMail("anaalamed@gmail.com", "Test Email", "testingggggg");
            return ResponseEntity.ok(BaseResponse.noContent(true, "mail sent"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("exception"));
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/getToken")
    public ResponseEntity<BaseResponse<LoginDataDTO>> loginGithub(@RequestParam String code) throws SQLDataException {
        logger.info("in loginGithub()");

        Optional<LoginDataDTO> loginData = authService.loginGithub(code);

        logger.info("User github has logged in");
        logger.info("login data: " + loginData);

        return loginData.map(value -> ResponseEntity.ok(BaseResponse.success(value))).
                orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("Failed to log in: github")));


//        ResponseEntity<GitToken> gitTokenResponseEntity = Utils.sendRequest(foolink);
//        if (gitTokenResponseEntity != null) {
//            String token= gitTokenResponseEntity.getBody().getAccess_token();
//            logger.info(token);
//            return ResponseEntity.ok(BaseResponse.noContent(true, "logged in with git"));
//        }

//        return ResponseEntity.ok(BaseResponse.noContent(true, "logged in with git"));
//        return ResponseEntity.badRequest().body(BaseResponse.failure("log in with git failed"));
    }

}
