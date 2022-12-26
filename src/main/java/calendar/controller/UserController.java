package calendar.controller;

import calendar.controller.request.NotificationSettingsRequest;
import calendar.controller.response.BaseResponse;
import calendar.entities.DTO.NotificationSettingsDTO;
import calendar.entities.DTO.RoleDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.NotificationSettings;
import calendar.entities.User;
import calendar.service.UserService;
import calendar.utils.InputValidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @RequestMapping(value = "/updateCity", method = RequestMethod.PATCH)
    public ResponseEntity<BaseResponse<UserDTO>> updateCity(@RequestAttribute("userId") int userId, @RequestParam String newCity) {
        logger.debug("in update city");

        User updatedUser = userService.updateCity(userId, newCity);

        logger.info(updatedUser);

        if (updatedUser != null) {
            return ResponseEntity.ok(BaseResponse.success(new UserDTO(updatedUser)));
        }

        return ResponseEntity.badRequest().body(BaseResponse.failure("failed to update!"));
    }

    @RequestMapping(value = "/getNotificationSettings", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<NotificationSettingsDTO>> getNotificationSettings(@RequestAttribute("userId") int userId) {

        logger.debug("In get notification settings");

        NotificationSettings notificationSettings = userService.getNotificationSettings(userId);

        logger.info(notificationSettings);

        if (notificationSettings != null) {
            return ResponseEntity.ok(BaseResponse.success(new NotificationSettingsDTO(notificationSettings)));
        }

        return ResponseEntity.badRequest().body(BaseResponse.failure("failed to get notification settings!"));
    }

    @RequestMapping(value = "/getUsersWhoSharedWithMe", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<List<UserDTO>>> getUsersWhoSharedWithMe(@RequestAttribute("userId") int userId) {

        logger.debug("In get users who shared their calendar with me.");

        User user = userService.getById(userId);
        if(user == null){
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        try{
            List<UserDTO> usersWhoSharedWithMe = UserDTO.convertUsersToUsersDTO(userService.getUsersWhoSharedWithMe(userId));
            return ResponseEntity.ok(BaseResponse.success(usersWhoSharedWithMe));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    /**
     * Share my calendar with a different user using his id, I will insert myself into his list of
     * users who shared their calendar with him.
     *
     * @param viewerId - the id of the user I want to share my calendar with.
     * @param userId-  My user id which I get by using the token in the filter.
     * @return The user i shared my calendar with.
     */
    @PostMapping(value = "/share")
    public ResponseEntity<BaseResponse<UserDTO>> shareCalendar(@RequestAttribute("userId") int userId,
                                                               @RequestParam int viewerId) {
        User user = userService.getById(userId);

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        User viewer = userService.getById(viewerId);

        if (viewer == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The viewer does not exist!"));
        }

        try {
            User sharedUser = userService.shareCalendar(user, viewer);

            return ResponseEntity.ok(BaseResponse.success(new UserDTO(sharedUser)));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }
}
