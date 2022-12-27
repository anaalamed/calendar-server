package calendar.eventNotifications;

import calendar.entities.Event;
import calendar.entities.NotificationSettings;
import calendar.entities.Role;
import calendar.entities.User;
import calendar.entities.enums.City;
import calendar.entities.enums.NotificationType;
import calendar.entities.enums.RoleType;
import calendar.entities.enums.StatusType;
import calendar.eventNotifications.entity.Notification;
import calendar.service.EventService;
import calendar.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class NotificationPublisherTest {

    @Autowired
    private NotificationPublisher notificationPublisher;

    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private UserService userService;

    @MockBean
    private EventService eventService;

    private static Event event;
    private static String email;
    private static User user;
    private static Role role;

    @BeforeEach
    void setUp(){

        event = Event.getNewEvent(true, ZonedDateTime.now(), 3.0f, "location1", "title1", "description1", null);
        event.setId(1);

        email = "leon@test.com";

        user = new User();
        user.setId(1);
        user.setEmail(email);
        user.setNotificationSettings(new NotificationSettings());
        user.setCity(City.JERUSALEM);

        role = new Role();
        role.setRoleType(RoleType.GUEST);
        role.setUser(user);
        role.setShownInMyCalendar(true);
        role.setStatusType(StatusType.APPROVED);
    }


    @Test
    void publishEventChangeNotification() {
        assertDoesNotThrow(() -> notificationPublisher.publishEventChangeNotification(event));
    }

    @Test
    void publishEventChangeNotification_Event_Does_Not_Exist() {
        assertThrows(IllegalArgumentException.class,() -> notificationPublisher.publishEventChangeNotification(null));
    }

    @Test
    void publishInviteGuestNotification() {
        when(eventService.getEventById(event.getId())).thenReturn(event);

        assertDoesNotThrow(() -> notificationPublisher.publishInviteGuestNotification(event.getId(),email));
    }

    @Test
    void publishInviteGuestNotification_Event_Does_Not_Exist() {
        when(eventService.getEventById(event.getId())).thenReturn(null);

        assertThrows(IllegalArgumentException.class,() -> notificationPublisher.publishInviteGuestNotification(event.getId(),email));
    }

    @Test
    void publishRemoveUserFromEventNotification() {
        when(eventService.getEventById(event.getId())).thenReturn(event);

        assertDoesNotThrow(() -> notificationPublisher.publishRemoveUserFromEventNotification(event.getId(),email));
    }

    @Test
    void publishRemoveUserFromEventNotification_Event_Does_Not_Exist() {
        when(eventService.getEventById(event.getId())).thenReturn(null);

        assertThrows(IllegalArgumentException.class,() -> notificationPublisher.publishRemoveUserFromEventNotification(event.getId(),email));
    }


    @Test
    void publishUserStatusChangedNotification() {
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(eventService.getSpecificRole(user.getId(), event.getId())).thenReturn(role);
        when(userService.getById(user.getId())).thenReturn(user);

        assertDoesNotThrow(() -> notificationPublisher.publishUserStatusChangedNotification(event.getId(),user.getId()));
    }

    @Test
    void publishUserStatusChangedNotification_Event_Does_Not_Exist() {
        when(eventService.getEventById(event.getId())).thenReturn(null);

        assertThrows(IllegalArgumentException.class,() -> notificationPublisher.publishUserStatusChangedNotification(
                event.getId(),user.getId()));
    }

    @Test
    void publishUserStatusChangedNotification_Role_Does_Not_Exist() {
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(eventService.getSpecificRole(user.getId(), event.getId())).thenReturn(null);

        assertThrows(NullPointerException.class,() -> notificationPublisher.publishUserStatusChangedNotification(
                event.getId(),user.getId()));
    }

    @Test
    void publishUserStatusChangedNotification_User_Does_Not_Exist() {
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(eventService.getSpecificRole(user.getId(), event.getId())).thenReturn(role);
        when(userService.getById(user.getId())).thenReturn(null);

        assertThrows(NullPointerException.class,() -> notificationPublisher.publishUserStatusChangedNotification(
                event.getId(),user.getId()));
    }


    @Test
    void publishUserRoleChangedNotification() {
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(eventService.getSpecificRole(user.getId(), event.getId())).thenReturn(role);
        when(userService.getById(user.getId())).thenReturn(user);

        assertDoesNotThrow(() -> notificationPublisher.publishUserRoleChangedNotification(event.getId(),user.getId()));
    }

    @Test
    void publishUserRoleChangedNotification_Event_Does_Not_Exist() {
        when(eventService.getEventById(event.getId())).thenReturn(null);

        assertThrows(IllegalArgumentException.class,() -> notificationPublisher.publishUserRoleChangedNotification(
                event.getId(),user.getId()));
    }

    @Test
    void publishUserRoleChangedNotification_Role_Does_Not_Exist() {
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(eventService.getSpecificRole(user.getId(), event.getId())).thenReturn(null);

        assertThrows(NullPointerException.class,() -> notificationPublisher.publishUserRoleChangedNotification(
                event.getId(),user.getId()));
    }

    @Test
    void publishUserRoleChangedNotification_User_Does_Not_Exist() {
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(eventService.getSpecificRole(user.getId(), event.getId())).thenReturn(role);
        when(userService.getById(user.getId())).thenReturn(null);

        assertThrows(NullPointerException.class,() -> notificationPublisher.publishUserRoleChangedNotification(
                event.getId(),user.getId()));
    }

    @Test
    void publishRegistrationNotification() {
        assertDoesNotThrow(() -> notificationPublisher.publishRegistrationNotification(email));
    }
}