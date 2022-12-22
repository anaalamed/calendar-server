package calendar.service;


import calendar.entities.*;
import calendar.entities.enums.*;
import calendar.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RoleServiceTest {

    @Autowired
    EventService eventService;
    @MockBean
    UserRepository userRepository;
    @MockBean
    EventRepository eventRepository;


    static Role role;
    static Role roleToInvite;
    static Event event;
    static User user;
    static User userToInvite;
    static List<Role> roles;
    static List<User> users;
    static List<Event> events;

    @BeforeEach
     void setup() {
        user = new User();
        user.setId(1);
        users = new ArrayList<>();
        users.add(user);

        userToInvite = new User();
        userToInvite.setId(123);

        role = new Role();
        role.setRoleType(RoleType.GUEST);
        role.setUser(user);
        role.setStatusType(StatusType.APPROVED);

        roles = new ArrayList<>();
        roles.add(role);

        roleToInvite = new Role();
        roleToInvite.setRoleType(RoleType.GUEST);
        roleToInvite.setUser(userToInvite);
        roleToInvite.setStatusType(StatusType.TENTATIVE);

        event = new Event();
        event.setId(1);
        event.getRoles().add(role);
        events = new ArrayList<>();
        events.add(event);
    }


    @Test
    void Get_Specific_Role_Successfully() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));

        Role response = eventService.getSpecificRole(1, 1);

        assertEquals(role, response);
    }

    @Test
    void Try_To_Get_A_Specific_Role_With_Invalid_User_Id() {
        when(eventRepository.findById(1)).thenThrow(NullPointerException.class);

        assertThrows(NullPointerException.class, () -> eventService.getSpecificRole(1, 1));
    }

    @Test
    void Try_To_Get_A_Specific_Role_With_Invalid_Event_Id() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));

        Role response = eventService.getSpecificRole(1, 987);

        assertNull(response);
    }

    @Test
    void Switch_Role_Successfully() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(1)).thenReturn(user);

        Role response = eventService.switchRole(1,1);

        assertEquals(response.getRoleType(), RoleType.ADMIN);
    }

    @Test
    void Try_To_Switch_Role_Of_User_That_Does_Not_Exist() {
        when(eventRepository.findById(1)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> eventService.switchRole(1,1));
    }

    @Test
    void Invite_Guest_Successfully() throws SQLDataException {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.ofNullable(event));

        Role response = eventService.inviteGuest(userToInvite, event.getId());

        assertNotNull(response);
        assertEquals(response.getUser().getId(), userToInvite.getId());
    }


    @Test
    void Try_To_Invite_Guest_Who_Is_Already_In_The_Event() throws SQLDataException {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.ofNullable(event));

        assertThrows(IllegalArgumentException.class, ()-> eventService.inviteGuest(user, event.getId()));
    }

    @Test
    void Remove_Guest_Successfully() throws SQLDataException {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));

        Role response = eventService.removeGuest(1, 1);

        assertNotNull(response);
        assertEquals(response.getId(), role.getId());
    }

    @Test
    void Try_To_Remove_Guest_Who_Is_Not_In_The_Event() throws SQLDataException {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));

        assertThrows(IllegalArgumentException.class, () -> eventService.removeGuest(21, 1));
    }

}