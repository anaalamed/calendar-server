package calendar.event.emailNotification;


import calendar.entities.enums.NotificationType;

public class OnRegistrationNotification extends GenericNotification<String> {
    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OnRegistrationNotification(NotificationType notificationType, String email ) {
        super(notificationType, email);
        this.email = email;
    }

    @Override
    public String toString() {
        return "OnRegistrationEvent{" +
                "email='" + email + '\'' +
                ", eventType=" + notificationType +
                ", source=" + source +
                '}';
    }
}