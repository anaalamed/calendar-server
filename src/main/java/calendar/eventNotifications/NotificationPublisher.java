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


    /**
     * This method is called when change an event and want to send notifications to all the users informing
     * them of the changes. We gather some information like the title of the notification and all the roles
     * we need to share with and create a new Notification.
     * Upon creating a notification it invokes the onApplicationEvent method in NotificationListener because
     * notification extends the ApplicationEvent abstract class.
     *
     * @param event - The event that was changed.
     */
    public void publishEventChangeNotification(Event event) {

        String title = "Event Changed";
        String message = "Event '" + event.getTitle() + "' at " + event.getTime() + " was changed!";

        List<Role> roles = event.getRoles();

        ArrayList<String> emails = new ArrayList<>();

        for (Role role : roles) {
            String email = role.getUser().getEmail();
            emails.add(email);
        }

        logger.info(emails);

        eventPublisher.publishEvent(new Notification(message, title, emails, NotificationType.EVENT_CHANGED));
    }

    /**
     * This method is called when we invite a user to an event informing him of the invitation.
     * Upon creating a notification it invokes the onApplicationEvent method in NotificationListener because
     * notification extends the ApplicationEvent abstract class.
     *
     * @param eventId - The event id to which we wish to invite someone. (Only organizer and admins can invite guests).
     * @param email   - The email of the user we wish to invite. (Must be part of our database).
     */
    public void publishInviteGuestNotification(int eventId, String email) {
        String title = "New Event Invitation";

        Event event = eventService.getEventById(eventId);

        if (event == null) {
            throw new IllegalArgumentException();
        }

        String message = "You were invited to Event '" + event.getTitle() + "' at " + event.getTime() + " !";

        ArrayList<String> emails = new ArrayList<>(List.of(email));

        eventPublisher.publishEvent(new Notification(message, title, emails, NotificationType.INVITE_GUEST));
    }

    /**
     * This method is called when we remove a guest from an event informing him of the removal.
     * Upon creating a notification it invokes the onApplicationEvent method in NotificationListener because
     * notification extends the ApplicationEvent abstract class.
     *
     * @param eventId - The event id to which we wish to invite someone. (Only organizer and admins can invite guests).
     * @param email   - The email of the user we wish to invite. (Must be part of our database).
     */
    public void publishRemoveUserFromEventNotification(int eventId, String email) {
        String title = "UnInvitation from Event";

        Event event = eventService.getEventById(eventId);

        if (event == null) {
            throw new IllegalArgumentException();
        }

        String message = "You were uninvited from Event '" + event.getTitle() + "' at " + event.getTime() + " !";

        ArrayList<String> emails = new ArrayList<>(List.of(email));

        eventPublisher.publishEvent(new Notification(message, title, emails, NotificationType.UNINVITE_GUEST));
    }

    /**
     * This method is called when a guest approves or rejects an invitation that was sent to him.
     * The notification it sent to the organizer of the event and to all the admins.
     * Upon creating a notification it invokes the onApplicationEvent method in NotificationListener because
     * notification extends the ApplicationEvent abstract class.
     *
     * @param eventId - The event id to which we wish to invite someone. (Only organizer and admins can invite guests).
     * @param userId  - The user id of the user who changes his status.
     */
    public void publishUserStatusChangedNotification(int eventId, int userId) {
        String title = "User status";

        Event event = eventService.getEventById(eventId);

        if (event == null) {
            throw new IllegalArgumentException();
        }

        StatusType statusType = eventService.getSpecificRole(userId, eventId).getStatusType();
        logger.info(statusType);

        String message = "";
        if (statusType == StatusType.APPROVED) {
            message = "User " + userService.getById(userId).getName() + " approved event '" + event.getTitle() + "' at " + event.getTime() + " !";
        } else if (statusType == StatusType.REJECTED) {
            message = "User " + userService.getById(userId).getName() + " rejected event '" + event.getTitle() + "' at " + event.getTime() + " !";
        }

        List<Role> roles = event.getRoles();
        ArrayList<String> emails = new ArrayList<>();
        for (Role role : roles) {
            if (role.getRoleType() == RoleType.ADMIN || role.getRoleType() == RoleType.ORGANIZER)
                emails.add(role.getUser().getEmail());
        }

        eventPublisher.publishEvent(new Notification(message, title, emails, NotificationType.USER_STATUS_CHANGED));
    }


    /**
     * This is a method which runs on a scheduled time once every minute.
     * It gets the events that occur in the next 24 hours and checks each event for users who need to be
     * notified about an upcoming event (Only those who chose to receive the notification).
     */
    @Scheduled(fixedRate = SCHEDULE) // minute
    public void scheduleCheckComingEvents() {

        logger.info("---------- in scheduleCheckComingEvents-------------");

        List<Event> eventsTillNextDay = eventService.getEventsTillNextDay();

        logger.info(eventsTillNextDay);

        for (Event event : eventsTillNextDay) {

            logger.info("--------------------- event " + event.getId() + "-----------------------------");

            List<Role> roles = event.getRoles();

            logger.info("event roles: " + roles);

            for (Role role : roles) {
                User user = role.getUser();

                logger.info("------- event " + user.getEmail() + " - " + user.getId() + "-------------");

                if (isInterestedInNotification(user)) {

                    NotificationRange notificationRange = user.getNotificationSettings().getNotificationRange();

                    int secondsRange = getNotificationRangeInSeconds(notificationRange);

                    ZonedDateTime eventTime = event.getTime();

                    if (isEventInNotificationRange(eventTime, secondsRange, user.getCity())) {

                        String title = "Upcoming event";
                        String message = "Event '" + event.getTitle() + "' at " + event.getTime().
                                withZoneSameInstant(ZoneId.of(Utils.getTimeZoneId(user.getCity()))) + " is starting soon!";
                        ArrayList<String> email = new ArrayList<>(List.of(role.getUser().getEmail()));

                        logger.info("send notification!");

                        eventPublisher.publishEvent(new Notification(message, title, email, NotificationType.UPCOMING_EVENT));
                    }
                }
            }
        }
    }


    /**
     * Checks if a user wants to receive a notification about an upcoming event.
     *
     * @param user - The user we wish to check about.
     * @return - True or false , whether he wants to receive the notification or not.
     */
    private boolean isInterestedInNotification(User user) {

        NotificationGetType notificationGetType = user.getNotificationSettings().getValue(NotificationType.UPCOMING_EVENT);

        if (notificationGetType == NotificationGetType.NONE) {
            return false;
        }

        return true;
    }

    /**
     * Returns the amount of seconds of the notification in regard to the event.
     *
     * @param notificationRange - The request notification time of the user.
     * @return the amount of seconds.
     */
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

    /**
     * Checks if a notification is close enough to the event time to see if it needs to be sent.
     *
     * @param eventTime    - The time of the event
     * @param secondsRange - The amount of seconds before the notification needs to be sent.
     * @param city         - The city of the user , user to get the timezone.
     * @return returns true or false whether we need to send the notification or not.
     */
    private boolean isEventInNotificationRange(ZonedDateTime eventTime, int secondsRange, City city) {

        logger.info("---------- in isEventInNotificationRange-------------");

        ZonedDateTime zonedEventTime = eventTime.withZoneSameInstant(ZoneId.of(Utils.getTimeZoneId(city)));
        ZonedDateTime zonedNow = ZonedDateTime.now().withZoneSameInstant(ZoneId.of(Utils.getTimeZoneId(city)));
        logger.info("zoned now: " + zonedNow);
        logger.info("zoned event date: " + zonedEventTime);

        Duration duration = Duration.between(zonedNow, zonedEventTime);
        logger.info("duration sec : " + duration.getSeconds());

        int range, startRange, endRange;

        if (secondsRange == 86400) {
            range = (SCHEDULE / 1000); // 60sec
            startRange = secondsRange - range;
            endRange = secondsRange;
        } else {
            range = (SCHEDULE / 1000) / 2; // 30sec
            startRange = secondsRange - range;
            endRange = secondsRange + range;
        }

        logger.info("range : " + startRange + ", " + endRange);

        if (duration.getSeconds() > startRange && duration.getSeconds() < endRange) {
            logger.info("in the range!");
            return true;
        }
        return false;
    }

//    ------------------------ optional - not in requirements ------------------------

    /**
     * This method is called when an organizer changes a role of a guest in the event.
     * The notification it sent to the guest informing him of the change..
     * Upon creating a notification it invokes the onApplicationEvent method in NotificationListener because
     * notification extends the ApplicationEvent abstract class.
     *
     * @param eventId - The event id to which we wish to invite someone. (Only organizer and admins can invite guests).
     * @param userId  - The user id of the user who changes his role.
     */
    public void publishUserRoleChangedNotification(int eventId, int userId) {

        String title = "User role";

        Event event = eventService.getEventById(eventId);

        if (event == null) {
            throw new IllegalArgumentException();
        }

        RoleType roleType = eventService.getSpecificRole(userId, eventId).getRoleType();
        logger.info(roleType);

        String message = "";

        if (roleType == RoleType.ADMIN) {
            message = "You are now admin at Event '" + event.getTitle() + "' at " + event.getTime() + " !";
        } else if (roleType == RoleType.GUEST) {
            message = "You are now guest at Event '" + event.getTitle() + "' at " + event.getTime() + " !";
        }

        ArrayList<String> emails = new ArrayList<>(List.of(userService.getById(userId).getEmail()));

        eventPublisher.publishEvent(new Notification(message, title, emails, NotificationType.USER_ROLE_CHANGED));
    }


    /**
     * A notification that is sent to users who register to our app.
     *
     * @param email - the email of the user who jus registered.
     */
    public void publishRegistrationNotification(String email) {

        String title = "Welcome to Calendar App";

        String message = "You registered to Calendar App \n" +
                "\n Welcome! " +
                "\n Visit us at : https://lam-calendar-client.web.app ";

        ArrayList<String> emails = new ArrayList<>(List.of(email));

        Notification notification = new Notification(message, title, emails, NotificationType.REGISTER);

        eventPublisher.publishEvent(notification);
    }
}
