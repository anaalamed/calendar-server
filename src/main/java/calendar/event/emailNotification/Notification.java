package calendar.event.emailNotification;

import calendar.entities.Event;
import org.springframework.context.ApplicationEvent;

import javax.persistence.Entity;
import java.util.ArrayList;

public class Notification extends ApplicationEvent {

    private Integer id;

    private String title;

    private String message;

    private Event event;

    private ArrayList<String> emailsToSend;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }


    public Integer getNotificationId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getEmailsToSend() {
        return emailsToSend;
    }

    public void setEmailsToSend(ArrayList<String> emailsToSend) {
        this.emailsToSend = emailsToSend;
    }

    public Notification(String message, String title, Event event, ArrayList<String> emails ){
        super(message);
        this.title = title;
        this.message = message;
        this.event = event;
        this.emailsToSend = emails;
    }


    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", event=" + event +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

}

