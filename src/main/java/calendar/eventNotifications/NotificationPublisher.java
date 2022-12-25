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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLDataException;
import java.time.*;
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
        String message = "Event '"+ event.getTitle() +"' at "+ event.getTime() +" was changed!";

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
        String message = "You were invited to Event '"+ event.getTitle() +"' at "+ event.getTime() +" !";
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
        String message = "You were uninvited from Event '"+ event.getTitle() +"' at "+ event.getTime() +" !";
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
            message = "User "+userService.getById(userId).getName()+" approved event '"+ event.getTitle() +"' at "+ event.getTime() +" !";
        } else if (statusType == StatusType.REJECTED){
            message = "User "+userService.getById(userId).getName()+" rejected event '"+ event.getTitle() +"' at "+ event.getTime() +" !";
        }

        List<Role> roles = event.getRoles();
        ArrayList<String> emails = new ArrayList<>();
        for (Role role : roles ) {
            if (role.getRoleType() == RoleType.ADMIN || role.getRoleType() == RoleType.ORGANIZER)
                emails.add(role.getUser().getEmail());
        }

        eventPublisher.publishEvent(new Notification(message, title, emails, NotificationType.USER_STATUS_CHANGED));
    }

    int fixedSchedule = 1000 * 60; // 1min
    @Scheduled(fixedRate = 1000 * 60) // minute
//    public void scheduleCheckComingEvents(String[] args) {   // debug from main
    public void scheduleCheckComingEvents() {
        logger.info("---------- in scheduleCheckComingEvents-------------");
        ZonedDateTime now  = ZonedDateTime.now();
        String title = "Upcoming event";

        try {
            Event event = eventService.getEventById(3);
            String message = "Event '"+ event.getTitle() +"' at "+ event.getTime() +" is coming";

            List<Role> roles = event.getRoles();
            logger.info("event roles: " + roles);

            logger.info("now: " + now);

            LocalDateTime eventTime = event.getTime();
            logger.info("event date: " + eventTime);

            ZonedDateTime zonedEventTime = eventTime.atZone(ZoneId.of("Asia/Jerusalem"));
            logger.info("event date: " + zonedEventTime);

            for (Role role :roles) {
                int userId = role.getUser().getId();
                int seconds =  600;     // 10min - 10*60
                int seconds2 =  1800;   // 30min - 30*60
                int seconds3 =  3600;   // 60min - 60*60
                Duration duration = Duration.between(now, zonedEventTime);
                logger.info("duration sec : " + duration.getSeconds());
                logger.info("duration min : " + duration.getSeconds() / 60);
//                logger.info("duration hours : " + duration.getSeconds() / 60 / 60);

                int notSet = seconds;
                int range = (fixedSchedule / 1000) / 2 ; // 3000
                int start = notSet-range;
                int end = notSet+range;

                logger.info("range : " + start + ", " + end);

                if (duration.getSeconds() < notSet + range && duration.getSeconds() > notSet - range ) {

                    ArrayList<String> email = new ArrayList<>(List.of(role.getUser().getEmail()));
                    logger.info("senddddd not");
                    eventPublisher.publishEvent(new Notification(message, title, email, NotificationType.UPCOMING_EVENT));
                }





            }
        } catch (SQLDataException e) {
            throw new RuntimeException(e);
        }

//        List<Event> events = new ArrayList<>();
//        events.add(new Event());
//        events.add(new Event());
//        logger.info(events);
//
//
//        for (Event event: events) {
//            List<Role> roles = event.getRoles();
//
//            for (Role role :roles) {
//                int userId = role.getUser().getId();
//                int minutes =  5;
//
//                LocalDate date = event.getDate();
//                logger.info(date);
//
//
//            }
////            deleteVerificationToken(token.getToken());
////            userRepository.deleteById(token.getUser().getId());
////            logger.debug("Verification token for user_id#" + token.getUser().getId() + " and non activated user was deleted");
//        }
    }



//    ------------------------ optional - not in requirements ------------------------
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
            message = "You are now admin at Event '"+ event.getTitle() +"' at "+ event.getTime() +" !";
        } else if (roleType == RoleType.GUEST){
            message = "You are now guest at Event '"+ event.getTitle() +"' at "+ event.getTime() +" !";
        }

        ArrayList<String> emails = new ArrayList<>(List.of(userService.getById(userId).getEmail()));

        eventPublisher.publishEvent(new Notification(message, title, emails, NotificationType.USER_ROLE_CHANGED));
    }


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
