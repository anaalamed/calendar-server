//package calendar.event.emailNotification;
//
//import calendar.service.RoleService;
//import calendar.utils.GMailer;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.stereotype.Component;
//
//
//@Component
//public class NotificationListener implements ApplicationListener<Notification> {
//
//    @Autowired
//    public RoleService roleService;
//    private static final Logger logger = LogManager.getLogger(NotificationListener.class.getName());
//
//    public void onApplicationEvent(Notification notification) {
//        logger.info("Received generic event - " + notification);
//
//        try {
//            this.onGenericEvent(notification);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void onGenericEvent(Notification notification) throws Exception {
//        logger.info("onGenericEvent");
//        logger.info("event" + notification);
//
//        for (String email: notification.getEmailsToSend() ) {
//            GMailer.sendMail(email, notification.getTitle(), notification.getMessage());
//        }
//    }
//
//}
