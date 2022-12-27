package calendar.controller;

import calendar.eventNotifications.entity.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationSocketController {
    private static Logger logger = LogManager.getLogger(NotificationSocketController.class.getName());

    @MessageMapping("/application")
    @SendTo("/notifications")
    public Notification send(Notification notification ) {
        logger.info("in socket send notification");

        return notification;
    }
}
