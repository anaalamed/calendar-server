package calendar.service;

import calendar.controller.request.EventRequest;
import calendar.controller.response.BaseResponse;
import calendar.entities.*;
import calendar.entities.DTO.UserDTO;
import calendar.entities.enums.*;
import calendar.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLDataException;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EventServiceTest {

    @Autowired
    EventService eventService;
    @MockBean
    UserRepository userRepository;
    @MockBean
    EventRepository eventRepository;

    static Role role;
    static Role roleToInvite;
    static Event event;
    static Event updatedEvent;
    static EventRequest eventRequest;
    static EventRequest updateEventRequest;
    static User user;
    static User userToInvite;
    static List<Event> events;
    static String[] sharedUsers;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1);
        user.setEmail("Test@Test.com");

        userToInvite = new User();
        userToInvite.setId(123);
        userToInvite.setEmail("Test@Test2.com");
        user.getUsersWhoSharedTheirCalendarWithMe().add(userToInvite);


        sharedUsers =new String[1];
        sharedUsers[0] = user.getEmail();

        role = new Role();
        role.setRoleType(RoleType.GUEST);
        role.setUser(user);
        role.setStatusType(StatusType.APPROVED);
        role.setShownInMyCalendar(true);

        roleToInvite = new Role();
        roleToInvite.setRoleType(RoleType.GUEST);
        roleToInvite.setUser(userToInvite);
        roleToInvite.setStatusType(StatusType.TENTATIVE);

        event = new Event();
        event.setId(1);
        event.setTitle("EventTest");
        event.getRoles().add(role);
        event.setTime(ZonedDateTime.now().plusMinutes(5));
        events = new ArrayList<>();
        events.add(event);

        updatedEvent = new Event();
        updatedEvent.setId(1);
        event.setTitle("UpdatedEvent");
        event.setDescription("UpdatedEvent");

        eventRequest = new EventRequest();
        eventRequest.setTitle("EventTest");

        updateEventRequest = new EventRequest();
        updateEventRequest.setTitle("UpdatedEvent");
        updateEventRequest.setDescription("UpdatedEvent");
        updateEventRequest.setTime(ZonedDateTime.now());
    }


    @Test
    void Get_Specific_Role_Successfully() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));

        Role response = eventService.getSpecificRole(1, 1);

        assertEquals(role, response);
    }

    @Test
    void Try_To_Get_A_Specific_Role_That_Does_Not_Exist() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));

        Role response = eventService.getSpecificRole(123, 1);

        assertNull(response);
    }

    @Test
    void Try_To_Get_A_Specific_Role_Of_Event_That_Does_Not_Exist() {
        when(eventRepository.findById(987)).thenReturn(Optional.ofNullable(null));

        Role response = eventService.getSpecificRole(1, 987);

        assertNull(response);
    }

    @Test
    void Switch_Role_Successfully() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(1)).thenReturn(user);

        Role response = eventService.switchRole(1, 1);

        assertEquals(response.getRoleType(), RoleType.ADMIN);
    }

    @Test
    void Try_To_Switch_Role_Of_User_That_Does_Not_Exist() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(1)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> eventService.switchRole(1, 1));
    }

    @Test
    void Try_To_Switch_Role_Of_User_In_Event_That_Does_Not_Exist() {
        when(eventRepository.findById(1)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> eventService.switchRole(1, 1));
    }

    @Test
    void Try_To_Switch_Role_Of_User_Who_Is_Not_Part_Of_The_Event() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(123)).thenReturn(userToInvite);

        assertThrows(IllegalArgumentException.class, () -> eventService.switchRole(123, 1));
    }

    @Test
    void Switch_Status_Successfully() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(1)).thenReturn(user);

        Role response = eventService.switchStatus(1, 1, false);

        assertEquals(response.getStatusType(), StatusType.REJECTED);
    }

    @Test
    void Try_To_Switch_Status_Of_User_That_Does_Not_Exist() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(1)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> eventService.switchStatus(1, 1, false));
    }

    @Test
    void Try_To_Switch_Status_Of_User_In_Event_That_Does_Not_Exist() {
        when(eventRepository.findById(1)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> eventService.switchStatus(1, 1, false));
    }

    @Test
    void Try_To_Switch_Status_Of_User_Who_Is_Not_Part_Of_The_Event() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(123)).thenReturn(userToInvite);

        assertThrows(IllegalArgumentException.class, () -> eventService.switchStatus(123, 1, false));
    }


    @Test
    void Invite_Guest_Successfully() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));

        Role response = eventService.inviteGuest(userToInvite, 1);

        assertNotNull(response);
        assertEquals(response.getUser().getId(), userToInvite.getId());
    }


    @Test
    void Try_To_Invite_Guest_Who_Is_Already_In_The_Event() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));

        assertThrows(IllegalArgumentException.class, () -> eventService.inviteGuest(user, 1));
    }

    @Test
    void Try_To_Invite_Guest_To_An_Event_That_Does_Not_Exist() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(null));

        assertThrows(NoSuchElementException.class, () -> eventService.inviteGuest(user, 1));
    }

    @Test
    void Remove_Guest_Successfully() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));

        Role response = eventService.removeGuest(1, 1);

        assertNotNull(response);
        assertEquals(response.getId(), role.getId());
    }

    @Test
    void Try_To_Remove_Guest_Who_Is_Not_In_The_Event() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));

        assertThrows(IllegalArgumentException.class, () -> eventService.removeGuest(21, 1));
    }

    @Test
    void Try_To_Remove_Guest_From_Event_That_Does_Not_Exist() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(null));

        assertThrows(NoSuchElementException.class, () -> eventService.removeGuest(1, 1));
    }

    @Test
    void Try_To_Remove_Guest_Who_Is_An_Organizer() {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));
        role.setRoleType(RoleType.ORGANIZER);

        assertThrows(IllegalArgumentException.class, () -> eventService.removeGuest(1, 1));
    }

    @Test
    void Save_Event_Successfully() throws SQLDataException {
        Event eventReq = Event.getNewEvent(eventRequest.isPublic(), eventRequest.getTime(), eventRequest.getDuration(), eventRequest.getLocation(),
                eventRequest.getTitle(), eventRequest.getDescription(), eventRequest.getAttachments());

        when(eventRepository.findById(1)).thenReturn(null);
        when(eventRepository.save(eventReq)).thenReturn(eventReq);

        Event response = eventService.saveEvent(eventRequest, user);

        assertEquals(response.getTitle(), "EventTest");
    }

    @Test
    void Delete_Event_Successfully() {
        assertDoesNotThrow(() -> eventService.deleteEvent(event));
    }


    @Test
    void Get_Event_By_Id_Successfully() throws SQLDataException {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));

        Event response = eventService.getEventById(1);

        assertEquals(response.getId(), 1);
    }

    @Test
    void Try_To_Get_Event_By_Id_That_Does_Not_Exist() throws SQLDataException {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(null));

        assertNull(eventService.getEventById(1));
    }

    @Test
    void Update_Event_Successfully() throws SQLDataException {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));
        when(eventRepository.updateEvent(updateEventRequest.isPublic(), updateEventRequest.getTitle(),
                updateEventRequest.getTime(), updateEventRequest.getDuration(), updateEventRequest.getLocation(),
                updateEventRequest.getDescription(), 1)).thenReturn(1);

        Event response = eventService.updateEvent(updateEventRequest, 1);

        assertEquals(response.getTitle(), "UpdatedEvent");
    }

    @Test
    void Try_To_Update_Event_ThaT_Does_Not_Exist() throws SQLDataException {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(null));

        assertThrows(NoSuchElementException.class, () -> eventService.updateEvent(updateEventRequest, 1));
    }

    @Test
    void Update_Event_Nothing_Changed() throws SQLDataException {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));
        when(eventRepository.updateEvent(eventRequest.isPublic(), eventRequest.getTitle(),
                eventRequest.getTime(), eventRequest.getDuration(), eventRequest.getLocation(),
                eventRequest.getDescription(), 1)).thenReturn(1);

        assertNull(eventService.updateEvent(eventRequest, 1));
    }

    @Test
    void Update_Restricted_Event_Successfully() throws SQLDataException {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));
        when(eventRepository.updateEventRestricted(updateEventRequest.isPublic(), updateEventRequest.getLocation(),
                updateEventRequest.getDescription(), 1)).thenReturn(1);

        Event response = eventService.updateEventRestricted(updateEventRequest, 1);

        assertEquals(response.getDescription(), "UpdatedEvent");
    }

    @Test
    void Try_To_Update_Restricted_Event_ThaT_Does_Not_Exist() throws SQLDataException {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(null));

        assertThrows(NoSuchElementException.class, () -> eventService.updateEventRestricted(updateEventRequest, 1));
    }

    @Test
    void Update_Event_Restricted_Nothing_Changed() throws SQLDataException {
        when(eventRepository.findById(1)).thenReturn(Optional.ofNullable(event));
        when(eventRepository.updateEventRestricted(eventRequest.isPublic(), eventRequest.getLocation(),
                eventRequest.getDescription(), 1)).thenReturn(1);

        assertNull(eventService.updateEventRestricted(eventRequest, 1));
    }

    @Test
    void Get_Events_By_User_Id() {
        when(eventRepository.findAll()).thenReturn(events);

        List<Event> response = eventService.getEventsByUserId(1);

        assertEquals(response.size(), 1);
    }

    @Test
    void Try_To_Get_Events_By_User_Has_None() {
        List<Event> emptyList = new ArrayList<>();
        when(eventRepository.findAll()).thenReturn(emptyList);

        List<Event> response = eventService.getEventsByUserId(1);

        assertEquals(response.size(), 0);
    }

    @Test
    void Leave_Event_Successfully(){
        when(eventRepository.findById(event.getId())).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(user.getId())).thenReturn(user);

        Role response = eventService.leaveEvent(user.getId(),event.getId());

        assertEquals(response.isShownInMyCalendar(), false);
        assertEquals(response.getStatusType(), StatusType.REJECTED);
    }

    @Test
    void Try_To_Leave_Event_User_Does_Not_Exist(){
        when(eventRepository.findById(event.getId())).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(user.getId())).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class,() -> eventService.leaveEvent(user.getId(),event.getId()));
    }

    @Test
    void Try_To_Leave_Event_Event_Does_Not_Exist(){
        when(eventRepository.findById(event.getId())).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class,() -> eventService.leaveEvent(user.getId(),event.getId()));
    }

    @Test
    void Try_To_Leave_Event_Role_Does_Not_Exist(){
        when(eventRepository.findById(event.getId())).thenReturn(Optional.ofNullable(updatedEvent));
        when(userRepository.findById(user.getId())).thenReturn(userToInvite);

        assertThrows(IllegalArgumentException.class,() -> eventService.leaveEvent(userToInvite.getId(),updatedEvent.getId()));
    }

    @Test
    void Try_To_Leave_Event_Role_Is_Organizer(){
        when(eventRepository.findById(event.getId())).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(user.getId())).thenReturn(user);

        role.setRoleType(RoleType.ORGANIZER);

        assertThrows(IllegalArgumentException.class,() -> eventService.leaveEvent(user.getId(),event.getId()));
    }

    @Test
    void Get_Events_In_The_Next_24_Hours_Successfully(){
        when(eventRepository.findAll()).thenReturn(events);

        Event event2 = new Event();
        Event event3 = new Event();
        Event event4 = new Event();
        ZonedDateTime time = ZonedDateTime.now();

        event2.setTime(time.plusHours(1));
        event3.setTime(time.plusHours(26));
        event4.setTime(time.plusHours(44));
        events.add(event2);
        events.add(event3);
        events.add(event4);

        List<Event> response = eventService.getEventsTillNextDay();

        assertEquals(response.size(),2);
    }

    @Test
    void Try_To_Get_Events_In_The_Next_24_Hours_When_There_Are_No_Events(){
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        List<Event> response = eventService.getEventsTillNextDay();

        assertEquals(response.size(),0);
    }

    @Test
    void Try_To_Get_Events_In_The_Next_24_Hours_When_There_Are_No_Events_In_The_next_24_Hours(){
        when(eventRepository.findAll()).thenReturn(events);
        events.get(0).setTime(ZonedDateTime.now().minusHours(1));

        List<Event> response = eventService.getEventsTillNextDay();

        assertEquals(response.size(),0);
    }

    @Test
    void Get_All_Shared_Successfully(){
        when(userRepository.findById(user.getId())).thenReturn(user);
        when(eventRepository.findAll()).thenReturn(events);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));

        List<Event> response = eventService.GetAllShared(user,sharedUsers);

        assertEquals(1, response.size());
    }

    @Test
    void Try_To_Get_All_Shared_User_Does_Not_Exist(){
        assertThrows(IllegalArgumentException.class,()->eventService.GetAllShared(null,sharedUsers));
    }

    @Test
    void Try_To_Get_All_Shared_User_Who_Shared_Does_Not_Exist(){
        assertThrows(NullPointerException.class,()->eventService.GetAllShared(user,null));
    }
}