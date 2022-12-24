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
