package calendar.eventNotifications;

import calendar.entities.Event;
import calendar.entities.Role;
import calendar.entities.enums.NotificationType;
import calendar.entities.enums.RoleType;
import calendar.entities.enums.StatusType;
import calendar.eventNotifications.entity.Notification;
import calendar.service.EventService;
import calendar.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationPublisher {
    @Autowired
    public ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserService userService;

    @Autowired
    public EventService eventService;

    private static final Logger logger = LogManager.getLogger(NotificationPublisher.class.getName());


    public void publishEventChangeNotification(Event event) {
        // get the changed event for now !

        String title = "Event Changed";
        String message = "Event '"+ event.getTitle() +"' at "+ event.getDate() +" was changed!";

        List<Role> roles = event.getRoles();

        ArrayList<String> emails = new ArrayList<>();
        for (Role role: roles ) {
            String email = role.getUser().getEmail();
            emails.add(email);
        }

        logger.info(emails);
        eventPublisher.publishEvent(new Notification(message, title, emails, NotificationType.EVENT_CHANGED));
    }

    public void publishInviteGuestNotification(int eventId, String email)  {
        String title = "New Event Invitation";

        Event event = null;
        try {
            event = eventService.getEventById(eventId);
        } catch (SQLDataException e) {
            logger.error("event was not found");
            return;
        }
        String message = "You were invited to Event '"+ event.getTitle() +"' at "+ event.getDate() +" !";
        ArrayList<String> emails = new ArrayList<>(List.of(email));

        eventPublisher.publishEvent(new Notification(message, title, emails, NotificationType.INVITE_GUEST));
    }

    public void publishRemoveUserFromEventNotification(int eventId, String email) {
        String title = "UnInvitation from Event";

        Event event = null;
        try {
            event = eventService.getEventById(eventId);
        } catch (SQLDataException e) {
            logger.error("event was not found");
            return;
        }
        String message = "You were uninvited from Event '"+ event.getTitle() +"' at "+ event.getDate() +" !";
        ArrayList<String> emails = new ArrayList<>(List.of(email));

        eventPublisher.publishEvent(new Notification(message, title, emails, NotificationType.UNINVITE_GUEST));
    }

    public void publishUserStatusChangedNotification(int eventId, int userId)  {
        String title = "User status";

        Event event = null;
        try {
            event = eventService.getEventById(eventId);
        } catch (SQLDataException e) {
            throw new RuntimeException(e);
        }
        StatusType statusType = eventService.getSpecificRole(userId, eventId).getStatusType();
        logger.info(statusType);

        String message = "";
        if (statusType == StatusType.APPROVED) {
            message = "User "+userService.getById(userId).getName()+" approved event '"+ event.getTitle() +"' at "+ event.getDate() +" !";
        } else if (statusType == StatusType.REJECTED){
            message = "User "+userService.getById(userId).getName()+" rejected event '"+ event.getTitle() +"' at "+ event.getDate() +" !";
        }

        List<Role> roles = event.getRoles();
        ArrayList<String> emails = new ArrayList<>();
        for (Role role : roles ) {
            if (role.getRoleType() == RoleType.ADMIN || role.getRoleType() == RoleType.ORGANIZER)
                emails.add(role.getUser().getEmail());
        }

        eventPublisher.publishEvent(new Notification(message, title, emails, NotificationType.USER_STATUS_CHANGED));
    }


    // optional - not in requirements
    public void publishUserRoleChangedNotification(int eventId, int userId)  {
        String title = "User role";

        Event event = null;
        try {
            event = eventService.getEventById(eventId);
        } catch (SQLDataException e) {
            throw new RuntimeException(e);
        }
        RoleType roleType = eventService.getSpecificRole(userId, eventId).getRoleType();
        logger.info(roleType);

        String message = "";
        if (roleType == RoleType.ADMIN) {
            message = "You are now admin at Event '"+ event.getTitle() +"' at "+ event.getDate() +" !";
        } else if (roleType == RoleType.GUEST){
            message = "You are now guest at Event '"+ event.getTitle() +"' at "+ event.getDate() +" !";
        }

        ArrayList<String> emails = new ArrayList<>(List.of(userService.getById(userId).getEmail()));

        eventPublisher.publishEvent(new Notification(message, title, emails, NotificationType.USER_ROLE_CHANGED));
    }


    // optional - not in requirements
    public void publishRegistrationNotification(String email) {
    String title = "Welcome to Calendar App";
    String message = "You registered to Calendar App \n" +
            "\n Welcome! " +
            "\n Visit us at : https://lam-calendar-client.web.app ";
    ArrayList<String> emails = new ArrayList<>(List.of(email));

    Notification notification = new Notification(message, title, emails, NotificationType.REGISTER);

    eventPublisher.publishEvent( notification );
}

}
