package calendar.service;

import calendar.controller.request.UserRequest;
import calendar.controller.response.BaseResponse;
import calendar.entities.DTO.LoginDataDTO;
import calendar.entities.NotificationSettings;
import calendar.entities.User;
import calendar.entities.enums.ProviderType;
import calendar.repository.UserRepository;
import calendar.utils.Utils;
import com.mysql.cj.log.Log;
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
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthServiceTest {

    @Autowired
    AuthService authService;
    @MockBean
    UserRepository userRepository;

    static User user;

    static UserRequest userRequest;

    static LoginDataDTO loginDataDTO;

    String code;

    @BeforeEach
    void setup() {
        user = new User("Leon", "Leon@test.com", "leon1234", ProviderType.LOCAL);
        user.setNotificationSettings(new NotificationSettings());
        user.setPassword(Utils.hashPassword(user.getPassword()));

        loginDataDTO = new LoginDataDTO(0,"testToken","Leon");

        userRequest = new UserRequest("Leon@test.com", "Leon", "leon1234");

        authService.getUsersTokensMap().put(loginDataDTO.getUserId(),loginDataDTO.getToken());

        code = "GitHubCode";
    }

    @Test
    void Create_User_Successfully() {
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.ofNullable(null));
        when(userRepository.save(any())).thenReturn(user);

        User response = authService.createUser(userRequest,ProviderType.LOCAL);

        assertEquals(response.getId(), user.getId());
    }

    @Test
    void Try_To_Create_User_That_Exists() {
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.ofNullable(user));

        assertThrows(IllegalArgumentException.class, ()-> authService.createUser(userRequest,ProviderType.LOCAL));
    }

    @Test
    void Login_Successfully() {
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.ofNullable(user));

        LoginDataDTO response = authService.login(userRequest).get();

        assertNotNull(response);
    }

    @Test
    void Try_To_Login_When_User_Does_Not_Exist() {
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.ofNullable(null));

        Optional<LoginDataDTO> response = authService.login(userRequest);

        assertEquals(response,Optional.empty());
    }

    @Test
    void Try_To_Login_With_Incorrect_Password() {
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.ofNullable(user));
        user.setPassword("InvalidPassword");

        Optional<LoginDataDTO> response = authService.login(userRequest);

        assertEquals(response,Optional.empty());
    }

    @Test
    void Get_User_Id_By_Token_Successfully() {

        Optional<Integer> userId = authService.getUserIdByToken(loginDataDTO.getToken());

        assertEquals(userId,Optional.of(user.getId()));
    }

    @Test
    void Try_To_Get_User_Id_By_Token_That_Does_Not_Exist() {

        Optional<Integer> userId = authService.getUserIdByToken("invalidToken");

        assertEquals(userId,Optional.empty());
    }

}