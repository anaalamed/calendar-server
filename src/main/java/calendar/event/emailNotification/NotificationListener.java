package calendar.event.emailNotification;

import calendar.utils.GMailer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener implements ApplicationListener<GenericNotification<String>> {

    private static final Logger logger = LogManager.getLogger(NotificationListener.class.getName());

    @Override
    public void onApplicationEvent(@NonNull GenericNotification<String> event) {
        switch (event.getEventType()) {
            case REGISTRATION:
                try {
                    this.onRegistration(event.getWhat());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }
        logger.info("Received generic event - " + event);
        logger.info("Received generic event - " + event.getWhat());
        logger.info("Received generic event - " + event.getEventType());

    }

    private void onRegistration(String email) throws Exception {
        String subject = "Welcome to Calendar App";
        GMailer.sendMail(email, subject, "You registered to Calendar app. Welcome!");
    }
}
