package calendar.controller;

import calendar.controller.request.NotificationSettingsRequest;
import calendar.controller.response.BaseResponse;
import calendar.entities.DTO.UserDTO;
import calendar.entities.NotificationSettings;
import calendar.service.UserService;
import calendar.utils.InputValidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

//    @Autowired
//    private NotificationRepository notificationRepository;

    private static final Logger logger = LogManager.getLogger(UserController.class.getName());

    /**
     * Find user by email
     * @param email
     * @return the User
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<UserDTO>> getUserByEmail(@RequestParam String email) {
        logger.info("in getUserByEmail");

        Optional<UserDTO> user = userService.getDTOByEmail(email);
        return user.map(value -> ResponseEntity.ok(BaseResponse.success(value)))
                .orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found!")));
    }

    /**
     * Delete user
     * @param userId
     * @param token
     * @return String success/failed
     */
    @DeleteMapping("delete")
    public ResponseEntity<BaseResponse<String>> deleteUser(@RequestAttribute("userId") int userId, @RequestHeader String token){
        logger.debug("in deleteUser");

        if (userService.deleteUser(userId)) {
            return ResponseEntity.ok(BaseResponse.noContent(true, "User #" + userId + " was successfully deleted"));
        }

        return ResponseEntity.badRequest().body(BaseResponse.failure("Failed to delete user #" + userId));
    }

    /**
     * Update user's name
     * @param userId
     * @param token
     * @param name
     * @return the User
     */
    @PutMapping(value = "/update", params = "name")
    public ResponseEntity<BaseResponse<UserDTO>> updateName(@RequestAttribute("userId") int userId, @RequestHeader String token,
                                                         @RequestParam String name) {
        logger.debug("in updateName");
        logger.debug("id" + userId);

        if (!InputValidation.isValidName(name)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid name!"));
        }

        Optional<UserDTO> updatedUser = userService.updateName(userId, name);
        return updatedUser.map(value -> ResponseEntity.ok(BaseResponse.success(value))).
                orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found")));
    }

    /**
     * Update user's email
     * @param userId
     * @param token
     * @param email
     * @return the User
     */
    @PutMapping(value = "/update", params = "email")
    public ResponseEntity<BaseResponse<UserDTO>> updateEmail(@RequestAttribute("userId") int userId,
                                                             @RequestHeader String token, @RequestParam String email) {
        logger.debug("in updateEmail");

        if (!InputValidation.isValidEmail(email)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid email!"));
        }

        Optional<UserDTO> updatedUser = userService.updateEmail(userId, email);
        return updatedUser.map(user -> ResponseEntity.ok(BaseResponse.success(user)))
                .orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found")));
    }

    /**
     * Update user's password
     * @param userId
     * @param token
     * @param password
     * @return the User
     */
    @PutMapping(value = "/update", params = "password")
    public ResponseEntity<BaseResponse<UserDTO>> updatePassword(@RequestAttribute("userId") int userId,
                                                                @RequestHeader String token, @RequestParam String password) {
        logger.debug("in updatePassword");

        if (!InputValidation.isValidPassword(password)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid password!"));
        }

        Optional<UserDTO> updatedUser = userService.updatePassword(userId, password);
        return updatedUser.map(user -> ResponseEntity.ok(BaseResponse.success(user)))
                .orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found")));
    }


    @PutMapping(value = "/update", params = "notifications")
    public ResponseEntity<BaseResponse<UserDTO>> updateNotifications(@RequestAttribute("userId") int userId, @RequestBody NotificationSettings notificationSettingsRequest) {
        logger.debug("in updateNotifications");
        UserDTO updatedUser = userService.updateNotificationsSettings(userId, notificationSettingsRequest);
        logger.info(updatedUser);

        if (updatedUser != null) {
            return ResponseEntity.ok(BaseResponse.success(updatedUser));
        }

        return ResponseEntity.badRequest().body(BaseResponse.failure("failed to update!"));
    }

}
