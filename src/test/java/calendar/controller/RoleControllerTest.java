package calendar.controller;

import calendar.controller.response.BaseResponse;
import calendar.entities.*;
import calendar.entities.DTO.UserDTO;
import calendar.entities.enums.*;
import calendar.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RoleControllerTest {

    @Autowired
    RoleController roleController;
    @MockBean
    UserService userService;
    @MockBean
    RoleService roleService;
    @MockBean
    EventService eventService;

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
        when(roleService.saveRoleInDB(role)).thenReturn(role);

        ResponseEntity<BaseResponse<Role>> response = roleController.saveRoleInDB(role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(role, response.getBody().getData());
    }

    @Test
    void Try_To_Save_Role_That_already_Exists() {
        when(roleService.saveRoleInDB(role)).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = roleController.saveRoleInDB(role);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Get_All_Roles_With_Same_User_Id_Successfully() {
        when(roleService.getRoleByUserId(1)).thenReturn(roles);
        when(userService.getById(1)).thenReturn(user);
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<BaseResponse<List<Role>>> response = roleController.getRoleByUserId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(role, response.getBody().getData().get(0));
    }

    @Test
    void Try_To_Get_Roles_Of_User_That_Does_Not_Exist() {
        when(userService.getById(1)).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = roleController.saveRoleInDB(role);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Get_All_Roles_With_Same_Event_Id_Successfully() throws SQLDataException {
        when(roleService.getRoleByEventId(1)).thenReturn(roles);
        when(eventService.getEventById(1)).thenReturn(event);
        when(eventService.getAllEvents()).thenReturn(events);

        ResponseEntity<BaseResponse<List<Role>>> response = roleController.getRoleByEventId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(role, response.getBody().getData().get(0));
    }

    @Test
    void Try_To_Get_Roles_Of_Event_That_Does_Not_Exist() throws SQLDataException {
        when(eventService.getEventById(1)).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = roleController.saveRoleInDB(role);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Get_Specific_Role_Successfully() {
        when(roleService.getSpecificRole(1, 1)).thenReturn(role);

        ResponseEntity<BaseResponse<Role>> response = roleController.getSpecificRole(1, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(role, response.getBody().getData());
    }

    @Test
    void Try_To_Get_A_Specific_Role_With_Invalid_User_Id() {
        when(roleService.getSpecificRole(999, 1)).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = roleController.saveRoleInDB(role);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Get_A_Specific_Role_With_Invalid_Event_Id() {
        when(roleService.getSpecificRole(1, 999)).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = roleController.getSpecificRole(1, 999);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Delete_Role_Successfully() {
        when(roleService.getSpecificRole(1, 1)).thenReturn(role);
        when(roleService.deleteRole(role)).thenReturn(true);

        ResponseEntity<BaseResponse<Role>> response = roleController.deleteRole(role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The role was deleted successfully!", response.getBody().getMessage());
    }

    @Test
    void Try_To_Delete_Role_That_Does_Not_Exist() {
        when(roleService.getSpecificRole(999, 999)).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = roleController.deleteRole(role);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Switch_Role_Successfully() {
        when(roleService.getSpecificRole(1, 1)).thenReturn(role);
        when(roleService.switchRole(2,2)).thenReturn(true);

        ResponseEntity<BaseResponse<Role>> response = roleController.switchRole(2,2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The role type was updated successfully!", response.getBody().getMessage());
    }

    @Test
    void Try_To_Switch_Role_Of_User_That_Does_Not_Exist() {
        when(roleService.getSpecificRole(999, 999)).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = roleController.switchRole(999,999);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Invite_Guest_Successfully() throws SQLDataException {
        when(userService.getByEmail("leon@invite.com")).thenReturn(Optional.of(new UserDTO(userToInvite)));
        when(eventService.getEventById(1)).thenReturn(event);
        when(roleService.getSpecificRole(user.getId(), event.getId())).thenReturn(null);
        when(userService.getById(123)).thenReturn(userToInvite);
        when(roleService.inviteGuest(userToInvite, event)).thenReturn(roleToInvite);

        ResponseEntity<BaseResponse<Role>> response = roleController.inviteGuest("leon@invite.com", 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleToInvite, response.getBody().getData());
    }

    @Test
    void Try_To_Invite_Guest_Who_Is_Not_Registered() throws SQLDataException {
        when(userService.getByEmail("leon@notRegistered.com")).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = roleController.inviteGuest("leon@notRegistered.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Invite_Guest_Who_Is_Already_In_The_Event() throws SQLDataException {
        when(userService.getByEmail("leon@invite.com")).thenReturn(Optional.of(new UserDTO(userToInvite)));
        when(eventService.getEventById(1)).thenReturn(event);
        when(roleService.getSpecificRole(userToInvite.getId(), event.getId())).thenReturn(role);

        ResponseEntity<BaseResponse<Role>> response = roleController.inviteGuest("leon@invite.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Remove_Guest_Successfully() throws SQLDataException {
        when(userService.getByEmail("leon@remove.com")).thenReturn(Optional.of(new UserDTO(user)));
        when(eventService.getEventById(1)).thenReturn(event);
        when(roleService.getSpecificRole(user.getId(), event.getId())).thenReturn(role);
        when(roleService.removeGuest(1, 1)).thenReturn(true);

        ResponseEntity<BaseResponse<Role>> response = roleController.removeGuest("leon@remove.com", 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The guest was removed successfully!", response.getBody().getMessage());
    }

    @Test
    void Try_To_Remove_Guest_Who_Is_Not_Registered() throws SQLDataException {
        when(userService.getByEmail("leon@notRegistered.com")).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = roleController.removeGuest("leon@notRegistered.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Remove_Guest_Who_Is_Not_In_The_Event() throws SQLDataException {
        when(userService.getByEmail("leon@remove.com")).thenReturn(Optional.of(new UserDTO(user)));
        when(eventService.getEventById(1)).thenReturn(event);
        when(roleService.getSpecificRole(user.getId(), event.getId())).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = roleController.removeGuest("leon@remove.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}