package calendar.service;

import calendar.controller.response.BaseResponse;
import calendar.entities.DTO.RoleDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.Event;
import calendar.entities.NotificationSettings;
import calendar.entities.Role;
import calendar.entities.User;
import calendar.entities.enums.ProviderType;
import calendar.repository.EventRepository;
import calendar.repository.UserRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;
    @MockBean
    UserRepository userRepository;

    static User user;
    static List<User> users;
    static UserDTO userDTO;
    static NotificationSettings notificationSettingsRequest;


    @BeforeEach
    void setup() {
        user = new User("Leon", "Leon@test.com", "leon1234", ProviderType.LOCAL);
        user.setId(0);
        notificationSettingsRequest = new NotificationSettings(user);
        user.setNotificationSettings(notificationSettingsRequest);

        users = new ArrayList<>();
        users.add(user);

        userDTO = new UserDTO(user);
    }


    @Test
    void Get_User_By_Id_Successfully(){
        when(userRepository.findById(user.getId())).thenReturn(user);

        User response = userService.getById(user.getId());

        assertEquals(response.getId(),user.getId());
    }

    @Test
    void Get_User_By_Id_ThaT_Does_Not_Exist(){
        when(userRepository.findById(user.getId())).thenReturn(null);

        User response = userService.getById(user.getId());

        assertNull(response);
    }

    @Test
    void Get_DTO_User_By_Email(){
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));

        UserDTO response = userService.getDTOByEmail(user.getEmail()).get();

        assertEquals(response.getId(),user.getId());
    }

    @Test
    void Try_To_Get_DTO_User_By_Email_That_Does_Not_Exist(){
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(null));

        Optional<UserDTO> response = userService.getDTOByEmail(user.getEmail());

        assertEquals(response,Optional.empty());
    }

    @Test
    void Get_User_By_Email_Optional(){
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));

        User response = userService.getByEmail(user.getEmail()).get();

        assertEquals(response.getId(),user.getId());
    }

    @Test
    void Try_To_Get_User_By_Email_Optional_That_Does_Not_Exist(){
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(null));

        Optional<User> response = userService.getByEmail(user.getEmail());

        assertEquals(response,Optional.empty());
    }

    @Test
    void Get_User_By_Email(){
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));

        User response = userService.getByEmailNotOptional(user.getEmail());

        assertEquals(response.getId(),user.getId());
    }

    @Test
    void Get_All_Users_Successfully(){
        when(userRepository.findAll()).thenReturn(users);

        List<User> response = userService.getAllUsers();

        assertEquals(response.size(),1);
    }

    @Test
    void Get_All_Users_When_There_Are_No_Users(){
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> response = userService.getAllUsers();

        assertEquals(response.size(),0);
    }

    @Test
    void Get_User_Id_By_Email_Successfully(){
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));

        Integer response = userService.getUserIdByEmail(user.getEmail());

        assertEquals(response,user.getId());
    }

    @Test
    void Try_To_Get_User_Id_By_Email_That_Does_Not_Exist(){
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(null));

        Integer response = userService.getUserIdByEmail(user.getEmail());

        assertNull(response);
    }

    @Test
    void Update_Notifications_Settings_Successfully(){
        when(userRepository.findById(user.getId())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        UserDTO response = userService.updateNotificationsSettings(user.getId(),user.getNotificationSettings());

        assertEquals(response.getId(),user.getId());
    }

}