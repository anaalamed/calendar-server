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


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RoleServiceTest {

    @Autowired
    RoleService roleService;
    @MockBean
    RoleRepository roleRepository;
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

    @BeforeAll
    static void setup() {
        event = new Event();
        event.setId(1);
        events = new ArrayList<>();
        events.add(event);

        user = new User();
        user.setId(1);
        users = new ArrayList<>();
        users.add(user);

        userToInvite = new User();
        userToInvite.setId(123);

        role = new Role();
        role.setRoleType(RoleType.GUEST);
        role.setEvent(event);
        role.setUser(user);
        role.setStatusType(StatusType.APPROVED);

        roles = new ArrayList<>();
        roles.add(role);

        roleToInvite = new Role();
        roleToInvite.setRoleType(RoleType.GUEST);
        roleToInvite.setEvent(event);
        roleToInvite.setUser(userToInvite);
        roleToInvite.setStatusType(StatusType.TENTATIVE);
    }

    @Test
    void Save_Role_Successfully() {
        when(roleRepository.save(role)).thenReturn(role);

        Role response = roleService.saveRoleInDB(role);

        assertEquals(role, response);
    }

    @Test
    void Try_To_Save_Role_That_already_Exists() {
        when(roleRepository.save(role)).thenReturn(null);

        Role response = roleService.saveRoleInDB(role);

        assertNull(response);
    }

    @Test
    void Delete_Role_Successfully() {
        when(roleRepository.findByUserId(1)).thenReturn(roles);

        assertDoesNotThrow(() -> roleService.deleteRole(role));
    }

    @Test
    void Try_To_Delete_Role_That_Does_Not_Exist() {
        when(roleService.getSpecificRole(999, 999)).thenReturn(null);

        assertDoesNotThrow(() -> roleService.deleteRole(role));
    }

    @Test
    void Get_All_Roles_With_Same_User_Id_Successfully() {
        when(roleRepository.findByUserId(1)).thenReturn(roles);

        List<Role> response = roleService.getRoleByUserId(1);

        assertEquals(role, response.get(0));
    }

    @Test
    void Try_To_Get_Roles_Of_User_That_Does_Not_Exist() {
        when(roleRepository.findByUserId(1)).thenReturn(null);

        List<Role> response = roleService.getRoleByUserId(1);

        assertNull(response);
    }

    @Test
    void Get_All_Roles_With_Same_Event_Id_Successfully() throws SQLDataException {
        when(roleRepository.findByEventId(1)).thenReturn(roles);

        List<Role> response = roleService.getRoleByEventId(1);

        assertEquals(role, response.get(0));
    }

    @Test
    void Try_To_Get_Roles_Of_Event_That_Does_Not_Exist() throws SQLDataException {
        when(roleRepository.findByEventId(1)).thenReturn(null);

        List<Role> response = roleService.getRoleByEventId(1);

        assertNull(response);
    }

    @Test
    void Get_Specific_Role_Successfully() {
        when(roleRepository.findByUserId(1)).thenReturn(roles);

        Role response = roleService.getSpecificRole(1, 1);

        assertEquals(role, response);
    }

    @Test
    void Try_To_Get_A_Specific_Role_With_Invalid_User_Id() {
        when(roleRepository.findByUserId(1)).thenThrow(NullPointerException.class);

        assertThrows(NullPointerException.class, () -> roleService.getSpecificRole(1, 1));
    }

    @Test
    void Try_To_Get_A_Specific_Role_With_Invalid_Event_Id() {
        when(roleRepository.findByUserId(1)).thenReturn(roles);

        Role response = roleService.getSpecificRole(1, 987);

        assertNull(response);
    }

    @Test
    void Switch_Role_Successfully() {
        when(roleRepository.findByUserId(1)).thenReturn(roles);

        boolean response = roleService.switchRole(role);

        assertTrue(response);
    }

    @Test
    void Try_To_Switch_Role_Of_User_That_Does_Not_Exist() {
        when(roleRepository.findByUserId(1)).thenReturn(null);

        boolean response = roleService.switchRole(role);

        assertFalse(response);
    }

    @Test
    void Invite_Guest_Successfully() throws SQLDataException {
        when(roleRepository.findByUserId(123)).thenReturn(null);

        Role response = roleService.inviteGuest(roleToInvite.getUser(), roleToInvite.getEvent());

        assertNotNull(response);
        assertEquals(response.getUser().getId(), userToInvite.getId());
    }


    @Test
    void Try_To_Invite_Guest_Who_Is_Already_In_The_Event() throws SQLDataException {
        when(roleRepository.findByUserId(1)).thenReturn(roles);

        Role response = roleService.inviteGuest(role.getUser(), role.getEvent());

        assertNull(response);
    }

    @Test
    void Remove_Guest_Successfully() throws SQLDataException {
        when(roleRepository.findByUserId(1)).thenReturn(roles);

        boolean response = roleService.removeGuest(1, 1);

        assertTrue(response);
    }

    @Test
    void Try_To_Remove_Guest_Who_Is_Not_In_The_Event() throws SQLDataException {
        when(roleRepository.findByUserId(1)).thenReturn(null);

        boolean response = roleService.removeGuest(1, 1);

        assertFalse(response);
    }

}