package calendar.controller;

import calendar.controller.response.BaseResponse;
import calendar.entities.*;
import calendar.entities.DTO.RoleDTO;
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

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EventControllerTest {

    @Autowired
    EventController eventController;
    @MockBean
    UserService userService;
    @MockBean
    EventService eventService;

    static Role role;
    static Role roleToInvite;
    static Role switchedRole;
    static Event event;
    static User user;
    static User userToInvite;
    static List<Role> roles;
    static List<User> users;
    static List<Event> events;

    @BeforeAll
    static void setup() {
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

        switchedRole = new Role();
        switchedRole.setRoleType(RoleType.ADMIN);

        event = new Event();
        event.setId(1);
        events = new ArrayList<>();
        events.add(event);
    }


    @Test
    void Get_Specific_Role_Successfully() {
        when(eventService.getSpecificRole(1, 1)).thenReturn(role);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.getSpecificRole(1, 1);
        RoleDTO roleDTO = new RoleDTO(role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleDTO.getId(), response.getBody().getData().getId());
    }

    @Test
    void Try_To_Get_A_Specific_Role_With_Invalid_User_Id() {
        when(eventService.getSpecificRole(999, 1)).thenReturn(null);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.getSpecificRole(999, 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Get_A_Specific_Role_With_Invalid_Event_Id() {
        when(eventService.getSpecificRole(1, 999)).thenReturn(null);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.getSpecificRole(1, 999);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Switch_Role_Successfully() {
        when(eventService.switchRole(1, 1)).thenReturn(switchedRole);

        ResponseEntity<BaseResponse<Role>> response = eventController.switchRole(1, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData().getRoleType(), RoleType.ADMIN);
    }

    @Test
    void Try_To_Switch_Role_Of_User_That_Does_Not_Exist() {
        when(eventService.switchRole(999, 1)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<BaseResponse<Role>> response = eventController.switchRole(1, 999);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Switch_Role_Of_User_In_Event_That_Does_Not_Exist() {
        when(eventService.switchRole(1, 999)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<BaseResponse<Role>> response = eventController.switchRole(999, 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Invite_Guest_Successfully() {
        when(userService.getByEmailNotOptional("leon@invite.com")).thenReturn(userToInvite);
        when(eventService.inviteGuest(userToInvite, event.getId())).thenReturn(roleToInvite);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.inviteGuest("leon@invite.com", 1);
        RoleDTO roleDTO = new RoleDTO(roleToInvite);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleDTO.getId(), response.getBody().getData().getId());
    }

    @Test
    void Try_To_Invite_Guest_Who_Is_Not_Registered(){
        when(userService.getByEmailNotOptional("leon@notRegistered.com")).thenReturn(null);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.inviteGuest("leon@notRegistered.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Invite_Guest_Who_Is_Already_In_The_Event() {
        when(userService.getByEmailNotOptional("leon@invite.com")).thenReturn(userToInvite);
        when(eventService.inviteGuest(userToInvite, event.getId())).thenReturn(null);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.inviteGuest("leon@invite.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Invite_Guest_To_An_Event_That_Does_Not_Exist() {
        when(userService.getByEmailNotOptional("leon@invite.com")).thenReturn(userToInvite);
        when(eventService.inviteGuest(userToInvite, event.getId())).thenReturn(null);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.inviteGuest("leon@invite.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Remove_Guest_Successfully(){
        when(userService.getByEmailNotOptional("leon@remove.com")).thenReturn(user);
        when(eventService.removeGuest(1, 1)).thenReturn(role);

        ResponseEntity<BaseResponse<Role>> response = eventController.removeGuest("leon@remove.com", 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The guest was removed successfully!", response.getBody().getMessage());
    }

    @Test
    void Try_To_Remove_Guest_Who_Is_Not_Registered(){
        when(userService.getByEmailNotOptional("leon@notRegistered.com")).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = eventController.removeGuest("leon@notRegistered.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Remove_Guest_Who_Is_Not_In_The_Event() throws SQLDataException {
        when(userService.getByEmailNotOptional("leon@remove.com")).thenReturn(user);
        when(eventService.removeGuest(1, 1)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<BaseResponse<Role>> response = eventController.removeGuest("leon@remove.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Remove_Guest_From_Event_That_Does_Not_Exist() throws SQLDataException {
        when(userService.getByEmailNotOptional("leon@remove.com")).thenReturn(user);
        when(eventService.removeGuest(1, 999)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<BaseResponse<Role>> response = eventController.removeGuest("leon@remove.com", 999);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void Try_To_Remove_Guest_Who_Is_An_Organizer() throws SQLDataException {
        when(userService.getByEmailNotOptional("leon@remove.com")).thenReturn(user);
        role.setRoleType(RoleType.ORGANIZER);
        when(eventService.removeGuest(1, 1)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<BaseResponse<Role>> response = eventController.removeGuest("leon@remove.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}