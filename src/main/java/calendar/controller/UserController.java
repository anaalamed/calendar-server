package calendar.controller;

import calendar.controller.response.BaseResponse;
import calendar.entities.DTO.NotificationSettingsDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.NotificationSettings;
import calendar.entities.User;
import calendar.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Find user by email from the database.
     *
     * @param email - The email of the user we wish to retrieve.
     * @return the User were looking for or bad request if not found.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<UserDTO>> getUserByEmail(@RequestParam String email) {

        logger.info("in getUserByEmail inside UserController");

        Optional<UserDTO> user = userService.getDTOByEmail(email);

        return user.map(value -> ResponseEntity.ok(BaseResponse.success(value)))
                .orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found!")));
    }


    /**
     * Updates the notification settings of a user
     *
     * @param userId                      - The id of the user we wish update.
     * @param notificationSettingsRequest - The notification Settings we wish to insert into the user.
     * @return the updated user or bad request if not found.
     */
    @PutMapping(value = "/update", params = "notifications")
    public ResponseEntity<BaseResponse<UserDTO>> updateNotifications(@RequestAttribute("userId") int userId,
                                                                     @RequestBody NotificationSettings notificationSettingsRequest) {

        logger.debug("in updateNotifications inside UserController");

        UserDTO updatedUser = userService.updateNotificationsSettings(userId, notificationSettingsRequest);
        logger.info(updatedUser);

        if (updatedUser != null) {
            return ResponseEntity.ok(BaseResponse.success(updatedUser));
        }

        return ResponseEntity.badRequest().body(BaseResponse.failure("failed to update!"));
    }


    /**
     * Updates the city of a user, can be JERUSALEM, PARIS, LONDON or NEW_YORK.
     *
     * @param userId  - The id of the user we wish update.
     * @param newCity - The city we wish to update the user information with.
     * @return the updated user or bad request if not found.
     */
    @RequestMapping(value = "/updateCity", method = RequestMethod.PATCH)
    public ResponseEntity<BaseResponse<UserDTO>> updateCity(@RequestAttribute("userId") int userId, @RequestParam String newCity) {

        logger.debug("in update city inside UserController");

        User updatedUser = userService.updateCity(userId, newCity);

        logger.info(updatedUser);

        if (updatedUser != null) {
            return ResponseEntity.ok(BaseResponse.success(new UserDTO(updatedUser)));
        }

        return ResponseEntity.badRequest().body(BaseResponse.failure("failed to update!"));
    }


    /**
     * Gets the notification settings of a user.
     *
     * @param userId - The id of the user we wish update.
     * @return the notification settings of a user.
     */
    @RequestMapping(value = "/getNotificationSettings", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<NotificationSettingsDTO>> getNotificationSettings(@RequestAttribute("userId") int userId) {

        logger.debug("In get notification settings inside UserController");

        NotificationSettings notificationSettings = userService.getNotificationSettings(userId);

        logger.info(notificationSettings);

        if (notificationSettings != null) {
            return ResponseEntity.ok(BaseResponse.success(new NotificationSettingsDTO(notificationSettings)));
        }

        return ResponseEntity.badRequest().body(BaseResponse.failure("failed to get notification settings!"));
    }


    /**
     * Gets a list of users who shared their calendar with me.
     *
     * @param userId - The id of the user we wish to retrieve the list of shared calendars.
     * @return the list of users who shared their calendar with the user.
     */
    @RequestMapping(value = "/getUsersWhoSharedWithMe", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<List<UserDTO>>> getUsersWhoSharedWithMe(@RequestAttribute("userId") int userId) {

        logger.debug("In get users who shared their calendar with me inside UserController.");

        User user = userService.getById(userId);

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        try {
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
     * @param email   - the email of the user I want to share my calendar with.
     * @param userId- My user id which I get by using the token in the filter.
     * @return The user i shared my calendar with.
     */
    @PostMapping(value = "/share")
    public ResponseEntity<BaseResponse<UserDTO>> shareCalendar(@RequestAttribute("userId") int userId,
                                                               @RequestParam String email) {

        logger.debug("In get users who shared their calendar with me inside UserController.");

        User user = userService.getById(userId);

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        User viewer = userService.getByEmailNotOptional(email);

        if (viewer == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The viewer does not exist!"));
        }

        if (viewer.getUsersWhoSharedTheirCalendarWithMe().contains(user)) {
            throw new IllegalArgumentException("This viewer is already part of my shared calendars!");
        }

        try {
            User sharedUser = userService.shareCalendar(user, viewer);

            return ResponseEntity.ok(BaseResponse.success(new UserDTO(sharedUser)));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }
}
