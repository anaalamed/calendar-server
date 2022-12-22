package calendar.eventNotifications;

import calendar.entities.DTO.UserDTO;
import calendar.entities.NotificationSettings;
import calendar.entities.enums.NotificationGetType;
import calendar.entities.enums.NotificationType;
import calendar.eventNotifications.entity.Notification;
//import calendar.repository.NotificationRepository;
import calendar.service.UserService;
import calendar.utils.GMailer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;


@Component
public class NotificationListener implements ApplicationListener<Notification> {

    @Autowired
    public UserService userService;
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

//        for (String email : emails) {
//            Optional<UserDTO> user = userService.getByEmail(email);
//            logger.info("user - " + user.get());
////            NotificationSettings notificationSettings = notificationRepository.findByUserId(user.get().getId());
////            NotificationSettings notificationSettings = user.get().getNotificationSettings();
//            logger.info("settings - " + notificationSettings);
////            logger.info(notificationSettings.getEvent_changed());
//
//            NotificationType notificationType = notification.getNotificationType();
//
//            switch (notificationSettings.getEvent_changed()) {
//                case EMAIL:
//                    logger.info("email");
//                    break;
//                case POPUP:
//                    logger.info("popup");
//                    break;
//                case NONE:
//                    logger.info("none");
//                    break;
//                case ALL:
//                    logger.info("all");
//                    break;
//                default:
//                    logger.info("default");
//                    break;
//
//            }
//        }


//        GMailer.sendMail(email, notification.getTitle(), notification.getMessage());

    }

}
