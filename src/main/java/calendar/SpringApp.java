package calendar;

import calendar.eventNotifications.NotificationPublisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringApp {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(SpringApp.class, args);

        NotificationPublisher notificationPublisher = applicationContext.getBean(NotificationPublisher.class);
        notificationPublisher.scheduleCheckComingEvents(args);
    }
}