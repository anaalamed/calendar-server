package calendar.eventNotifications;

import calendar.entities.Event;
import calendar.entities.Role;
import calendar.entities.User;
import calendar.entities.enums.*;
import calendar.eventNotifications.entity.Notification;
import calendar.service.EventService;
import calendar.service.UserService;
import calendar.utils.Utils;
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

    private static final int SCHEDULE = 1000 * 60; // 1min

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


    @Scheduled(fixedRate = SCHEDULE) // minute
    public void scheduleCheckComingEvents() {
        logger.info("---------- in scheduleCheckComingEvents-------------");
        List<Event> eventsTillNextDay = eventService.getEventsTillNextDay();
        logger.info(eventsTillNextDay);

        for (Event event: eventsTillNextDay ) {
            logger.info("--------------------- event "+event.getId()+"-----------------------------");
            List<Role> roles = event.getRoles();
            logger.info("event roles: " + roles);

            for (Role role :roles) {
                User user = role.getUser();
                logger.info("------- event "+user.getEmail()+ " - " +user.getId()+"-------------");

                if (isInterestedInNotification(user)) {
                    NotificationRange notificationRange = user.getNotificationSettings().getNotificationRange();
                    int secondsRange = getNotificationRangeInSeconds(notificationRange);
                    ZonedDateTime eventTime = event.getTime();

                    if (isEventInNotificationRange(eventTime, secondsRange, user.getCity())) {
                        String title = "Upcoming event";
                        String message = "Event '"+ event.getTitle() +"' at "+ event.getTime().withZoneSameInstant(ZoneId.of(Utils.getTimeZoneId(user.getCity()))) +" is coming";
                        ArrayList<String> email = new ArrayList<>(List.of(role.getUser().getEmail()));

                        logger.info("send notification!");
                        eventPublisher.publishEvent(new Notification(message, title, email, NotificationType.UPCOMING_EVENT));
                    }
                }
            }
        }
    }

    private boolean isInterestedInNotification(User user) {
        NotificationGetType notificationGetType = user.getNotificationSettings().getValue(NotificationType.UPCOMING_EVENT);
        if (notificationGetType == NotificationGetType.NONE) {
            return false;
        }
        return true;
    }

    private int getNotificationRangeInSeconds(NotificationRange notificationRange) {
        int secondsRange;
        switch (notificationRange) {
            case TEN_MINUTES:
                logger.info("------ 10 min");
                secondsRange = 600;    // 10min - 10*60
                break;
            case THIRTY_MINUTES:
                logger.info("------ 30 min");
                secondsRange = 1800;   // 30min - 30*60
                break;
            case ONE_HOUR:
                logger.info("------ 1 hour");
                secondsRange = 3600;   // 60min - 60*60
                break;
            case ONE_DAY:
                logger.info("------ 1 day");
                secondsRange = 86400;   // 60min - 60*60*24
                break;
            default:
                logger.info("default");
                secondsRange = 0;
                break;
        }
        return secondsRange;
    }

    private boolean isEventInNotificationRange(ZonedDateTime eventTime, int secondsRange, City city) {
        logger.info("---------- in isEventInNotificationRange-------------");

        ZonedDateTime zonedEventTime = eventTime.withZoneSameInstant(ZoneId.of(Utils.getTimeZoneId(city)));
        ZonedDateTime zonedNow = ZonedDateTime.now().withZoneSameInstant(ZoneId.of(Utils.getTimeZoneId(city)));
        logger.info("zoned now: " + zonedNow);
        logger.info("zoned event date: " + zonedEventTime);

        Duration duration = Duration.between(zonedNow, zonedEventTime);
        logger.info("duration sec : " + duration.getSeconds());

        int range, startRange, endRange;
        if (secondsRange == 86400 ) {
            range = (SCHEDULE / 1000)  ; // 60sec
            startRange = secondsRange-range;
            endRange = secondsRange;
        } else {
            range = (SCHEDULE / 1000) / 2 ; // 30sec
            startRange = secondsRange-range;
            endRange = secondsRange+range;
        }
        logger.info("range : " + startRange + ", " + endRange);

        if (duration.getSeconds() > startRange && duration.getSeconds() < endRange ) {
            logger.info("in the range!");
            return true;
        }
        return false;
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
