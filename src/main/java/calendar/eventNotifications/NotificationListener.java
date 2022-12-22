package calendar.eventNotifications;

import calendar.entities.NotificationSettings;
import calendar.entities.User;
import calendar.entities.enums.NotificationType;
import calendar.eventNotifications.entity.Notification;
import calendar.service.UserService;
import calendar.utils.GMailer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;


@Component
public class NotificationListener implements ApplicationListener<Notification> {

    @Autowired
    public UserService userService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    private static final Logger logger = LogManager.getLogger(NotificationListener.class.getName());

    public void onApplicationEvent(Notification notification) {
        logger.info("Received generic event - " + notification);
        try {
            this.onGenericEvent(notification);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void onGenericEvent(Notification notification) throws Exception {
        logger.info("onGenericEvent");
        logger.info("event" + notification);

        ArrayList<String> emails = notification.getEmailsToSend();
        logger.info(emails);

        for (String email : emails) {
            Optional<User> user = userService.getByEmail(email);

            if ( !user.isPresent()) {
                return;
            }

            logger.info("user - " + user.get());
            logger.info("settings - " + user.get().getNotificationSettings());

            NotificationSettings notificationSettings = user.get().getNotificationSettings();
            NotificationType notificationType = notification.getNotificationType();
            logger.info(notificationType);

            logger.info("value - " + notificationSettings.getValue(notificationType));
            logger.info("notification - " + notification);

            switch (notificationSettings.getValue(notificationType)) {
                case EMAIL:
                    logger.info("------ send email");
                    GMailer.sendMail(email, notification.getTitle(), notification.getMessage());
                    break;
                case POPUP:
                    logger.info("------- send socket popup");
                    simpMessagingTemplate.convertAndSend("/notifications", notification);
                    break;
                case ALL:
                    logger.info("-------- send email and popup");
                    GMailer.sendMail(email, notification.getTitle(), notification.getMessage());
                    simpMessagingTemplate.convertAndSend("/notifications", notification);
                    break;
                case NONE:
                    logger.info("--------- no notifications ");
                    break;
                default:
                    logger.info("default");
                    break;
            }
        }

    }

}
