package calendar.service;

import calendar.controller.response.BaseResponse;
import calendar.entities.DTO.NotificationSettingsDTO;
import calendar.entities.DTO.RoleDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.Event;
import calendar.entities.NotificationSettings;
import calendar.entities.Role;
import calendar.entities.User;
import calendar.entities.enums.City;
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
    static User user2;
    static List<User> users;
    static UserDTO userDTO;
    static NotificationSettings notificationSettingsRequest;


    @BeforeEach
    void setup() {
        user = new User("Leon", "Leon@test.com", "leon1234", ProviderType.LOCAL);
        user.setId(0);
        notificationSettingsRequest = new NotificationSettings(user);
        user.setNotificationSettings(notificationSettingsRequest);

        user2 = new User("Leon2", "Leon@test2.com", "leon1234", ProviderType.LOCAL);
        user.setId(1);
        notificationSettingsRequest = new NotificationSettings(user);
        user2.setNotificationSettings(notificationSettingsRequest);

        users = new ArrayList<>();
        users.add(user);

        userDTO = new UserDTO(user);

        user.getUsersWhoSharedTheirCalendarWithMe().add(user2);
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

    @Test
    void Update_City_Successfully() {

        when(userRepository.findById(user.getId())).thenReturn(user);

        User response = userService.updateCity(user.getId(),"LONDON");

        assertEquals(City.LONDON, response.getCity());
    }

    @Test
    void Try_To_Update_City_For_None_Existent_User() {

        when(userRepository.findById(user.getId())).thenReturn(null);

        User response = userService.updateCity(user.getId(),"LONDON");

        assertNull(response);
    }

    @Test
    void Update_City_Default_Value() {

        when(userRepository.findById(user.getId())).thenReturn(user);

        User response = userService.updateCity(user.getId(),"Random String");

        assertEquals(City.JERUSALEM, response.getCity());
    }

    @Test
    void Get_User_Notifications_Successfully() {
        when(userRepository.findById(user.getId())).thenReturn(user);

        NotificationSettings response = userService.getNotificationSettings(user.getId());

        assertEquals(user.getNotificationSettings().getId(), response.getId());
    }

    @Test
    void Try_To_Get_User_Notifications_User_Does_NOt_Exist() {
        when(userRepository.findById(user.getId())).thenReturn(null);

        NotificationSettings response = userService.getNotificationSettings(user.getId());

        assertNull(response);
    }
    @Test
    void Try_To_Get_User_Notifications_User_Has_No_Notification_Settings() {
        when(userRepository.findById(user.getId())).thenReturn(user);

        user.setNotificationSettings(null);

        NotificationSettings response = userService.getNotificationSettings(user.getId());

        assertNull(response);

    }

    @Test
    void Get_Users_Who_Shared_With_Me_Successfully(){
        when(userRepository.findById(user.getId())).thenReturn(user);

        List<User>  response = userService.getUsersWhoSharedWithMe(user.getId());

        assertEquals(1, response.size());
    }

    @Test
    void Try_To_Get_Users_Who_Shared_With_Me_But_User_Does_Not_Exist(){
        when(userRepository.findById(user.getId())).thenReturn(null);

        assertThrows(IllegalArgumentException.class,()->userService.getUsersWhoSharedWithMe(user.getId()));
    }

    @Test
    void Try_To_Get_Users_Who_Shared_With_Me_But_No_One_Shared_With_Me(){
        when(userRepository.findById(user2.getId())).thenReturn(user2);

        List<User>  response = userService.getUsersWhoSharedWithMe(user2.getId());

        assertEquals(0, response.size());
    }

    @Test
    void Share_Calendar_Successfully(){
        assertDoesNotThrow(() -> userService.shareCalendar(user,user2));
    }

    @Test
    void Try_To_Share_Calendar_User_Does_Not_Exist(){
        assertThrows(IllegalArgumentException.class,()-> userService.shareCalendar(null,user2));
    }

    @Test
    void Try_To_Share_Calendar_But_Failed(){
        assertThrows(IllegalArgumentException.class,()-> userService.shareCalendar(user,null));
    }

    @Test
    void Try_To_Share_Calendar_With_Someone_Who_Already_Shared_With(){
        assertThrows(IllegalArgumentException.class,()-> userService.shareCalendar(user2,user));
    }

}