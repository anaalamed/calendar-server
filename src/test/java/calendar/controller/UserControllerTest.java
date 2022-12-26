package calendar.controller;

import calendar.controller.response.BaseResponse;
import calendar.entities.DTO.NotificationSettingsDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.NotificationSettings;
import calendar.entities.User;
import calendar.entities.enums.City;
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

import java.util.ArrayList;
import java.util.List;
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
    static User updatedUser;
    static UserDTO userDTO;
    static NotificationSettings notificationSettingsRequest;
    static List<User> usersWhosharedWithMe;


    @BeforeEach
    void setup() {
        user = new User("Leon", "Leon@test.com", "leon1234", ProviderType.LOCAL);
        user.setId(0);
        notificationSettingsRequest = new NotificationSettings(user);
        user.setNotificationSettings(notificationSettingsRequest);

        userDTO = new UserDTO(user);

        updatedUser = new User();
        updatedUser.setCity(City.LONDON);
        updatedUser.setNotificationSettings(notificationSettingsRequest);

        usersWhosharedWithMe = new ArrayList<>();
        usersWhosharedWithMe.add(updatedUser);
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

    @Test
    void Update_City_Successfully() {

        when(userService.updateCity(user.getId(),"LONDON")).thenReturn(updatedUser);

        ResponseEntity<BaseResponse<UserDTO>> response = userController.updateCity(user.getId(),"LONDON");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser.getCity(), response.getBody().getData().getCity());
    }

    @Test
    void Try_To_Update_City_For_None_Existent_User() {

        when(userService.updateCity(user.getId(), "LONDON")).thenReturn(null);

        ResponseEntity<BaseResponse<UserDTO>> response = userController.updateCity(user.getId(),"LONDON");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Get_User_Notifications_Successfully() {
        when(userService.getNotificationSettings(user.getId())).thenReturn(user.getNotificationSettings());

        ResponseEntity<BaseResponse<NotificationSettingsDTO>> response = userController.getNotificationSettings(user.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getNotificationSettings().getId(), response.getBody().getData().getId());
    }

    @Test
    void Try_To_Get_User_Notifications_User_Does_NOt_Exist() {
        when(userService.getNotificationSettings(123)).thenReturn(null);

        ResponseEntity<BaseResponse<NotificationSettingsDTO>> response = userController.getNotificationSettings(123);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void Try_To_Get_User_Notifications_User_Has_No_Notification_Settings() {
        when(userService.getNotificationSettings(user.getId())).thenReturn(null);

        ResponseEntity<BaseResponse<NotificationSettingsDTO>> response = userController.getNotificationSettings(user.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Get_Users_Who_Shared_With_Me_Successfully(){
        when(userService.getById(user.getId())).thenReturn(user);
        when(userService.getUsersWhoSharedWithMe(user.getId())).thenReturn(usersWhosharedWithMe);

        ResponseEntity<BaseResponse<List<UserDTO>>> response = userController.getUsersWhoSharedWithMe(user.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    void Try_To_Get_Users_Who_Shared_With_Me_But_User_Does_Not_Exist(){
        when(userService.getById(user.getId())).thenReturn(null);

        ResponseEntity<BaseResponse<List<UserDTO>>> response = userController.getUsersWhoSharedWithMe(user.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Get_Users_Who_Shared_With_Me_But_No_One_Shared_With_Me(){
        when(userService.getById(updatedUser.getId())).thenReturn(updatedUser);

        ResponseEntity<BaseResponse<List<UserDTO>>> response = userController.getUsersWhoSharedWithMe(updatedUser.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getData().size());
    }

}