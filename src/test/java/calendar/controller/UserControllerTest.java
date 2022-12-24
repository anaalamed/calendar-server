package calendar.controller;

import calendar.controller.response.BaseResponse;
import calendar.entities.DTO.UserDTO;
import calendar.entities.NotificationSettings;
import calendar.entities.User;
import calendar.entities.enums.ProviderType;
import calendar.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserControllerTest {

    @Autowired
    UserController userController;
    @MockBean
    UserService userService;

    static User user;
    static UserDTO userDTO;
    static NotificationSettings notificationSettingsRequest;


    @BeforeEach
    void setup() {
        user = new User("Leon", "Leon@test.com", "leon1234", ProviderType.LOCAL);
        user.setId(0);
        notificationSettingsRequest = new NotificationSettings(user);
        user.setNotificationSettings(notificationSettingsRequest);

        userDTO = new UserDTO(user);
    }

    @Test
    void Get_User_By_Email_Successfully() {
        when(userService.getDTOByEmail(user.getEmail())).thenReturn(Optional.ofNullable(userDTO));

        ResponseEntity<BaseResponse<UserDTO>> response = userController.getUserByEmail(user.getEmail());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getId(), response.getBody().getData().getId());
    }

    @Test
    void Try_To_Get_User_By_Email_That_Does_Not_Exist() {
        when(userService.getDTOByEmail(user.getEmail())).thenReturn(Optional.ofNullable(null));

        ResponseEntity<BaseResponse<UserDTO>> response = userController.getUserByEmail(user.getEmail());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Update_Notifications_Successfully() {

        when(userService.updateNotificationsSettings(user.getId(), notificationSettingsRequest)).thenReturn(userDTO);

        ResponseEntity<BaseResponse<UserDTO>> response = userController.updateNotifications(user.getId(),user.getNotificationSettings());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getId(), response.getBody().getData().getId());
    }

    @Test
    void Try_To_Update_Notifications_For_None_Existent_User() {

        when(userService.updateNotificationsSettings(user.getId(), notificationSettingsRequest)).thenReturn(null);

        ResponseEntity<BaseResponse<UserDTO>> response = userController.updateNotifications(user.getId(),user.getNotificationSettings());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}