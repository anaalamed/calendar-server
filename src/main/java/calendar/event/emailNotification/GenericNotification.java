package calendar.event.emailNotification;

import calendar.entities.enums.NotificationType;
import org.springframework.context.ApplicationEvent;

public class GenericNotification<T> extends ApplicationEvent {
    private T what;

    protected NotificationType notificationType;

    public GenericNotification(NotificationType notificationType, T what) {
        super(what);
        this.what = what;
        this.notificationType = notificationType;
    }

    public T getWhat() {
        return what;
    }

    public NotificationType getEventType() {
        return notificationType;
    }
}
