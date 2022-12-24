package calendar.controller;

import calendar.controller.request.UserRequest;
import calendar.controller.response.BaseResponse;
import calendar.entities.DTO.LoginDataDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.NotificationSettings;
import calendar.entities.User;
import calendar.entities.enums.ProviderType;
import calendar.service.AuthService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLDataException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthControllerTest {

    @Autowired
    AuthController authController;
    @MockBean
    AuthService authService;

    static User user;

    static UserRequest userRequest;

    static LoginDataDTO loginDataDTO;

    @BeforeEach
    void setup() {
        user = new User("Leon", "Leon@test.com", "leon1234", ProviderType.LOCAL);
        user.setId(1);
        user.setNotificationSettings(new NotificationSettings());

        loginDataDTO = new LoginDataDTO(1,"testToken");

        userRequest = new UserRequest("Leon@test.com", "Leon", "leon1234");
    }


    @Test
    void register_Successfully() {
        when(authService.createUser(userRequest, ProviderType.LOCAL)).thenReturn(user);

        ResponseEntity<BaseResponse<UserDTO>> response = authController.register(userRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getId(), response.getBody().getData().getId());
    }

    @Test
    void Try_To_register_When_User_Already_Exists() {
        when(authService.createUser(userRequest, ProviderType.LOCAL)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<BaseResponse<UserDTO>> response = authController.register(userRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_register_With_Invalid_Email() {
        userRequest.setEmail("invalidEmail");

        ResponseEntity<BaseResponse<UserDTO>> response = authController.register(userRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_register_With_Invalid_Password() {
        userRequest.setPassword("invalidPassword");

        ResponseEntity<BaseResponse<UserDTO>> response = authController.register(userRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_register_With_Invalid_Name() {
        userRequest.setName("invalidName!@#15125");

        ResponseEntity<BaseResponse<UserDTO>> response = authController.register(userRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void login_Successfully() {
        when(authService.login(userRequest)).thenReturn(Optional.ofNullable(loginDataDTO));

        ResponseEntity<BaseResponse<LoginDataDTO>> response = authController.login(userRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().getUserId());
    }

    @Test
    void Try_To_Login_With_Wrong_Email() {
        when(authService.login(userRequest)).thenReturn(Optional.ofNullable(null));
        userRequest.setEmail("invalidEmail");

        ResponseEntity<BaseResponse<LoginDataDTO>> response = authController.login(userRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Login_With_Wrong_Password() {
        when(authService.login(userRequest)).thenReturn(Optional.ofNullable(null));
        userRequest.setPassword("invalidEmail");

        ResponseEntity<BaseResponse<LoginDataDTO>> response = authController.login(userRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}