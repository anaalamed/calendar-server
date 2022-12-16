package calendar.controller;

import calendar.controller.response.BaseResponse;
import calendar.entities.DTO.UserDTO;
import calendar.service.UserService;
import calendar.utils.InputValidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

        Optional<UserDTO> user = userService.getByEmail(email);
        return user.map(value -> ResponseEntity.ok(BaseResponse.success(value)))
                .orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found!")));
    }

    /**
     * Delete user by Id
     * @param userId
     * @param token
     * @return String success/failed
     */
//    @RequestMapping(method = RequestMethod.DELETE, value = "delete")
//    public ResponseEntity<BaseResponse<String>> deleteUser(@RequestParam int userId, @RequestHeader String token){
//        logger.debug("in deleteUser");
//
//        if (!authService.isAuthenticated(userId, token)) {
//            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
//        }
//
//        if (userService.deleteUser(userId)) {
//            return ResponseEntity.ok(BaseResponse.noContent(true, "User #" + userId + " was successfully deleted"));
//        }
//
//        return ResponseEntity.badRequest().body(BaseResponse.failure("Failed to delete user #" + userId));
//    }

    /**
     * Update user's name by Id
     * @param userId
     * @param token
     * @param name
     * @return the User
     */
    @RequestMapping(method = RequestMethod.PATCH, value="/update/", params = "name")
    public ResponseEntity<BaseResponse<UserDTO>> updateName(@RequestAttribute("userId") int userId, @RequestHeader String token,
                                                         @RequestParam String name) {
        logger.debug("in updateName");
        logger.debug("id" + userId);

        if (!InputValidation.isValidName(name)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid name!"));
        }

//        if (!authService.isAuthenticated(userId, token)) {
//            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
//        }

        Optional<UserDTO> updatedUser = userService.updateName(userId, name);
        return updatedUser.map(value -> ResponseEntity.ok(BaseResponse.success(value))).
                orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found")));
    }

    /**
     * Update user's email by Id
     * @param userId
     * @param token
     * @param newEmail
     * @return the User
     */
//    @RequestMapping(method = RequestMethod.PATCH, value="/update/{id}", params = "newEmail")
//    public ResponseEntity<BaseResponse<UserDTO>> updateEmail(@PathVariable("id") int userId,
//                                                             @RequestHeader String token, @RequestParam String newEmail) {
//        logger.debug("in updateEmail");
//
//        if (!InputValidation.isValidEmail(newEmail)) {
//            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid email!"));
//        }
//
//        if (!authService.isAuthenticated(userId, token)) {
//            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
//        }
//
//        Optional<UserDTO> updatedUser = userService.updateEmail(userId, newEmail);
//        return updatedUser.map(user -> ResponseEntity.ok(BaseResponse.success(user)))
//                .orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found")));
//    }

    /**
     * Update user's password by Id
     * @param userId
     * @param token
     * @param password
     * @return the User
     */
//    @RequestMapping(method = RequestMethod.PATCH, value="/update/{id}", params = "password")
//    public ResponseEntity<BaseResponse<UserDTO>> updatePassword(@PathVariable("id") int userId,
//                                                                @RequestHeader String token, @RequestParam String password) {
//        logger.debug("in updatePassword");
//
//        if (!InputValidation.isValidPassword(password)) {
//            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid password!"));
//        }
//
//        if (!authService.isAuthenticated(userId, token)) {
//            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
//        }
//
//        Optional<UserDTO> updatedUser = userService.updatePassword(userId, password);
//        return updatedUser.map(user -> ResponseEntity.ok(BaseResponse.success(user)))
//                .orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found")));
//    }
}
