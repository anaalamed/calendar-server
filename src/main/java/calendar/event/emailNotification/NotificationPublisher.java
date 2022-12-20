package calendar.event.emailNotification;

import calendar.entities.Event;
import calendar.entities.Role;
import calendar.service.RoleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationPublisher {
    @Autowired
    public ApplicationEventPublisher eventPublisher;

    @Autowired
    public RoleService roleService;

    private static final Logger logger = LogManager.getLogger(NotificationPublisher.class.getName());


    public void publishRegistrationNotification(String email) {
        String title = "Welcome to Calendar App";
        String message = "You registered to Calendar App \n" +
                "\n Welcome! " +
                "\n Visit us at : https://lam-calendar-client.web.app ";
        ArrayList<String> emails = new ArrayList<>(List.of(email));

        eventPublisher.publishEvent(  new Notification(message, title, null, emails));
    }


    public void publishEventChangeNotification(Event event) {
        String title = "Event Changed";
        String message = "Event '"+ event.getTitle() +"' at "+ event.getDate() +" was changed!";

        List<Role> roles = roleService.getRoleByEventId(event.getId());

        ArrayList<String> emails = new ArrayList<>();
        for (Role role: roles ) {
            String email = role.getUser().getEmail();
            emails.add(email);
        }

        logger.info(emails);
        eventPublisher.publishEvent(new Notification(message, title, event, emails));
    }

    public void publishEventInviteNotification(Event event, String email) {
        String title = "New Event Invitation";
        String message = "You were invited to Event '"+ event.getTitle() +"' at "+ event.getDate() +" !";
        ArrayList<String> emails = new ArrayList<>(List.of(email));

        eventPublisher.publishEvent(new Notification(message, title, event, emails));
    }

}
