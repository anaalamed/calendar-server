package calendar.event.emailNotification;

import calendar.entities.enums.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {
    @Autowired
    public ApplicationEventPublisher eventPublisher;

    public void publishRegistrationEvent(String email) {
        eventPublisher.publishEvent(new OnRegistrationNotification( NotificationType.REGISTRATION, email));
    }
}
