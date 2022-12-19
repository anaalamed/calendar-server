package calendar.event.emailNotification;

import calendar.service.AuthService;
import calendar.utils.GMailer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private AuthService authService;

    private static final Logger logger = LogManager.getLogger(RegistrationListener.class.getName());


    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        try {
            logger.info("onApplicationEvent");
            logger.info(event);
            logger.info(event.getEmail());
            this.onRegistration(event.getEmail());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send mail to user's email - welcome
     * @param email
     * @throws Exception
     */
    private void onRegistration(String email) throws Exception {
        String subject = "Welcome to Calendar App";
        GMailer.sendMail(email, subject, "You registered to Calendar app. Welcome!");
    }
}
