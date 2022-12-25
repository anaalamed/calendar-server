package calendar.controller;

import calendar.controller.request.EventRequest;
import calendar.controller.response.BaseResponse;
import calendar.entities.*;
import calendar.entities.DTO.EventDTO;
import calendar.entities.DTO.RoleDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.enums.*;
import calendar.eventNotifications.NotificationPublisher;
import calendar.service.*;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.sql.SQLDataException;
import java.time.LocalDate;
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
    @MockBean
    NotificationPublisher notificationPublisher;

    static Role role;
    static Role roleToInvite;
    static Role switchedRole;
    static Event event;
    static Event eventNoShow;
    static Event updatedEvent;
    static EventRequest eventRequest;
    static User user;
    static User userToInvite;
    static List<Event> events;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1);
        user.setNotificationSettings(new NotificationSettings());

        userToInvite = new User();
        userToInvite.setId(123);
        userToInvite.setNotificationSettings(new NotificationSettings());

        role = new Role();
        role.setRoleType(RoleType.GUEST);
        role.setUser(user);
        role.setShownInMyCalendar(true);
        role.setStatusType(StatusType.APPROVED);

        roleToInvite = new Role();
        roleToInvite.setRoleType(RoleType.GUEST);
        roleToInvite.setUser(userToInvite);
        roleToInvite.setStatusType(StatusType.TENTATIVE);

        switchedRole = new Role();
        switchedRole.setRoleType(RoleType.ADMIN);
        switchedRole.setUser(user);
        switchedRole.setStatusType(StatusType.REJECTED);

        event = Event.getNewEvent(true, null,  3.0f, "location1", "title1", "description1", null);
        event.setId(1);
        event.getRoles().add(role);

        eventNoShow = Event.getNewEvent(true, null, null, 2.0f, "location2", "title2", "description2", null);
        event.setId(2);

        events = new ArrayList<>();
        events.add(event);

        updatedEvent = Event.getNewEvent(true, null,  2.0f, "UpdatedEvent", "UpdatedEvent", "UpdatedEvent", null);

        eventRequest = new EventRequest();
        eventRequest.setTitle("UpdatedEvent");
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
    void Switch_Role_Successfully() throws SQLDataException {
        when(eventService.switchRole(1, 1)).thenReturn(switchedRole);
        //Here because of notifications, cant mock because its void so doing inner mocks
        when(eventService.getEventById(1)).thenReturn(event);
        when(eventService.getSpecificRole(1,1)).thenReturn(role);
        when(userService.getById(1)).thenReturn(user);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.switchRole(1, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData().getRoleType(), RoleType.ADMIN);
    }

    @Test
    void Try_To_Switch_Role_Of_User_That_Does_Not_Exist() {
        when(eventService.switchRole(999, 1)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.switchRole(1, 999);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Switch_Role_Of_User_In_Event_That_Does_Not_Exist() {
        when(eventService.switchRole(1, 999)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.switchRole(999, 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Switch_Status_Successfully() throws SQLDataException {
        when(eventService.switchStatus(1, 1, false)).thenReturn(switchedRole);
        //Here because of notifications, cant mock because its void so doing inner mocks
        when(eventService.getEventById(1)).thenReturn(event);
        when(eventService.getSpecificRole(1,1)).thenReturn(role);
        when(userService.getById(1)).thenReturn(user);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.switchStatus(false, 1, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData().getStatusType(), StatusType.REJECTED);
    }

    @Test
    void Try_To_Switch_Status_Of_User_That_Does_Not_Exist() {
        when(eventService.switchStatus(999, 1, false)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.switchStatus(false, 1, 999);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Switch_Status_Of_User_In_Event_That_Does_Not_Exist() {
        when(eventService.switchStatus(1, 999, false)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.switchStatus(false, 999, 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Invite_Guest_Successfully() throws SQLDataException {
        when(userService.getByEmailNotOptional("leon@invite.com")).thenReturn(userToInvite);
        when(eventService.inviteGuest(userToInvite, event.getId())).thenReturn(roleToInvite);
        //Here because of notifications, cant mock because its void so doing inner mocks
        when(eventService.getEventById(1)).thenReturn(event);
        when(eventService.getSpecificRole(1,1)).thenReturn(role);
        when(userService.getById(1)).thenReturn(user);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.inviteGuest("leon@invite.com", 2);
        RoleDTO roleDTO = new RoleDTO(roleToInvite);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleDTO.getId(), response.getBody().getData().getId());
    }

    @Test
    void Try_To_Invite_Guest_Who_Is_Not_Registered() {
        when(userService.getByEmailNotOptional("leon@notRegistered.com")).thenReturn(null);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.inviteGuest("leon@notRegistered.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Invite_Guest_Who_Is_Already_In_The_Event() throws SQLDataException {
        when(userService.getByEmailNotOptional("leon@invite.com")).thenReturn(userToInvite);
        when(eventService.inviteGuest(userToInvite, event.getId())).thenReturn(null);
        //Here because of notifications, cant mock because its void so doing inner mocks
        when(eventService.getEventById(1)).thenReturn(event);
        when(eventService.getSpecificRole(1,1)).thenReturn(role);
        when(userService.getById(1)).thenReturn(user);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.inviteGuest("leon@invite.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Invite_Guest_To_An_Event_That_Does_Not_Exist() throws SQLDataException {
        when(userService.getByEmailNotOptional("leon@invite.com")).thenReturn(userToInvite);
        when(eventService.inviteGuest(userToInvite, 999)).thenReturn(null);
        //Here because of notifications, cant mock because its void so doing inner mocks
        when(eventService.getEventById(1)).thenReturn(event);
        when(eventService.getSpecificRole(1,999)).thenReturn(role);
        when(userService.getById(1)).thenReturn(user);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.inviteGuest("leon@invite.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Remove_Guest_Successfully() throws SQLDataException {
        when(userService.getByEmailNotOptional("leon@remove.com")).thenReturn(user);
        when(eventService.removeGuest(1, 1)).thenReturn(role);
        //Here because of notifications, cant mock because its void so doing inner mocks
        when(eventService.getEventById(1)).thenReturn(event);
        when(eventService.getSpecificRole(1,1)).thenReturn(role);
        when(userService.getById(1)).thenReturn(user);

        ResponseEntity<BaseResponse<Role>> response = eventController.removeGuest("leon@remove.com", 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The guest was removed successfully!", response.getBody().getMessage());
    }

    @Test
    void Try_To_Remove_Guest_Who_Is_Not_Registered() {
        when(userService.getByEmailNotOptional("leon@notRegistered.com")).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = eventController.removeGuest("leon@notRegistered.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Remove_Guest_Who_Is_Not_In_The_Event() {
        when(userService.getByEmailNotOptional("leon@remove.com")).thenReturn(user);
        when(eventService.removeGuest(1, 1)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<BaseResponse<Role>> response = eventController.removeGuest("leon@remove.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Remove_Guest_From_Event_That_Does_Not_Exist() {
        when(userService.getByEmailNotOptional("leon@remove.com")).thenReturn(user);
        when(eventService.removeGuest(1, 999)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<BaseResponse<Role>> response = eventController.removeGuest("leon@remove.com", 999);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Remove_Guest_Who_Is_An_Organizer() {
        when(userService.getByEmailNotOptional("leon@remove.com")).thenReturn(user);
        role.setRoleType(RoleType.ORGANIZER);
        when(eventService.removeGuest(1, 1)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<BaseResponse<Role>> response = eventController.removeGuest("leon@remove.com", 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Save_Event_Successfully() throws SQLDataException {
        when(userService.getById(1)).thenReturn(user);
        when(eventService.saveEvent(eventRequest, user)).thenReturn(event);

        ResponseEntity<BaseResponse<EventDTO>> response = eventController.saveEvent(1, eventRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData().getId(), 2);
    }

    @Test
    void Try_To_Save_Event_User_Does_Not_Exist() {
        when(userService.getById(1)).thenReturn(null);

        ResponseEntity<BaseResponse<EventDTO>> response = eventController.saveEvent(1, eventRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Delete_Event_Successfully() throws SQLDataException {
        when(userService.getById(1)).thenReturn(user);
        when(eventService.deleteEvent(1)).thenReturn(1);

        ResponseEntity<BaseResponse<String>> response = eventController.deleteEvent(1, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData(), "Event Deleted Successfully");
    }

    @Test
    void Try_To_Delete_Event_User_Does_Not_Exist() {
        when(userService.getById(1)).thenReturn(null);

        ResponseEntity<BaseResponse<String>> response = eventController.deleteEvent(1, 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Delete_Event_failed() throws SQLDataException {
        when(userService.getById(1)).thenReturn(user);
        when(eventService.deleteEvent(1)).thenReturn(0);

        ResponseEntity<BaseResponse<String>> response = eventController.deleteEvent(1, 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Get_Event_By_Id_Successfully() throws SQLDataException {
        when(eventService.getEventById(1)).thenReturn(event);

        ResponseEntity<BaseResponse<EventDTO>> response = eventController.getEventById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData().getId(), 2);
    }

    @Test
    void Try_To_Get_Event_By_Id_That_Does_Not_Exist() throws SQLDataException {
        when(eventService.getEventById(1)).thenReturn(null);

        ResponseEntity<BaseResponse<EventDTO>> response = eventController.getEventById(1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Update_Event_Successfully_Organizer() throws SQLDataException {
        when(eventService.updateEvent(eventRequest, 1)).thenReturn(updatedEvent);

        ResponseEntity<BaseResponse<EventDTO>> response = eventController.updateEvent(RoleType.ORGANIZER, 1, eventRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData().getTitle(), "UpdatedEvent");
    }

    @Test
    void Update_Event_Successfully_Admin() throws SQLDataException {
        when(eventService.updateEventRestricted(eventRequest, 1)).thenReturn(updatedEvent);

        ResponseEntity<BaseResponse<EventDTO>> response = eventController.updateEvent(RoleType.ADMIN, 1, eventRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData().getDescription(), "UpdatedEvent");
    }

    @Test
    void Update_Event_Failed_Organizer() throws SQLDataException {
        when(eventService.updateEvent(eventRequest, 1)).thenReturn(null);

        ResponseEntity<BaseResponse<EventDTO>> response = eventController.updateEvent(RoleType.ORGANIZER, 1, eventRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Update_Event_Failed_Admin() throws SQLDataException {
        when(eventService.updateEventRestricted(eventRequest, 1)).thenReturn(null);

        ResponseEntity<BaseResponse<EventDTO>> response = eventController.updateEvent(RoleType.ADMIN, 1, eventRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Get_Events_By_User_Id() {
        when(userService.getById(1)).thenReturn(user);
        when(eventService.getEventsByUserId(1)).thenReturn(events);

        ResponseEntity<BaseResponse<List<EventDTO>>> response = eventController.getEventsByUserId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData().size(), 1);
    }

    @Test
    void Try_To_Get_Events_By_User_Id_That_Does_Not_Exist() {
        when(userService.getById(1)).thenReturn(null);

        ResponseEntity<BaseResponse<List<EventDTO>>> response = eventController.getEventsByUserId(1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Get_Events_By_User_Has_None() {
        List<Event> emptyList = new ArrayList<>();
        when(userService.getById(1)).thenReturn(user);
        when(eventService.getEventsByUserId(1)).thenReturn(emptyList);

        ResponseEntity<BaseResponse<List<EventDTO>>> response = eventController.getEventsByUserId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData().size(), 0);
    }

    @Test
    void Leave_Event_Successfully(){
        when(userService.getById(1)).thenReturn(user);
        when(eventService.leaveEvent(user.getId(),event.getId())).thenReturn(role);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.leaveEvent(user.getId(),event.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData().getUser().getId(), user.getId());
    }

    @Test
    void Try_To_Leave_Event_User_Does_Not_Exist(){
        when(userService.getById(1)).thenReturn(null);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.leaveEvent(user.getId(),event.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Leave_Event_Role_Does_Not_Exist(){
        when(userService.getById(1)).thenReturn(user);
        when(eventService.leaveEvent(user.getId(),event.getId())).thenReturn(null);

        ResponseEntity<BaseResponse<RoleDTO>> response = eventController.leaveEvent(user.getId(),event.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Get_Events_By_User_Id_Only_Show() {
        when(userService.getById(user.getId())).thenReturn(user);
        when(eventService.getEventsByUserId(1)).thenReturn(events);

        //Adding new role that we dont dont want to show
        Role roleDontShow = new Role();
        roleDontShow.setUser(user);
        roleDontShow.setShownInMyCalendar(false);
        eventNoShow.getRoles().add(roleDontShow);
        events.add(eventNoShow);

        ResponseEntity<BaseResponse<List<EventDTO>>> response = eventController.getEventsByUserIdShowOnly(user.getId());

        System.out.println(events.size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData().size(), 1);
    }

    @Test
    void Try_To_Get_Events_Only_Show_By_User_Id_That_Does_Not_Exist() {
        when(userService.getById(1)).thenReturn(null);

        ResponseEntity<BaseResponse<List<EventDTO>>> response = eventController.getEventsByUserIdShowOnly(1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Try_To_Get_Events_Only_Show_By_User_Has_None_That_He_Wants_To_Not_Show() {
        when(userService.getById(1)).thenReturn(user);
        when(eventService.getEventsByUserId(1)).thenReturn(events);

        ResponseEntity<BaseResponse<List<EventDTO>>> response = eventController.getEventsByUserIdShowOnly(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData().size(), 1);
    }

    @Test
    void Try_To_Get_Events_Only_Show_By_User_Has_None_That_He_Wants_To_Show() {
        when(userService.getById(1)).thenReturn(user);
        when(eventService.getEventsByUserId(1)).thenReturn(events);

        events.get(0).getRoles().get(0).setShownInMyCalendar(false);

        ResponseEntity<BaseResponse<List<EventDTO>>> response = eventController.getEventsByUserIdShowOnly(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getData().size(), 0);
    }
}