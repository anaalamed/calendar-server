package calendar.entities.DTO;
import calendar.entities.NotificationSettings;
import calendar.entities.User;
import calendar.entities.enums.NotificationGetType;
import calendar.entities.enums.NotificationRange;

public class NotificationSettingsDTO {
    private int id;


    private NotificationGetType event_changed, invite_guest, uninvite_guest, user_status,user_role ,cancel_event , upcoming_event;

    private NotificationRange notificationRange;


    public int getId() {
        return id;
    }

    public NotificationGetType getEvent_changed() {
        return event_changed;
    }


    public NotificationGetType getInvite_guest() {
        return invite_guest;
    }


    public NotificationGetType getUninvite_guest() {
        return uninvite_guest;
    }


    public NotificationGetType getUser_status() {
        return user_status;
    }

    public NotificationGetType getUser_role() {
        return user_role;
    }

    public NotificationGetType getCancel_event() {
        return cancel_event;
    }


    public NotificationGetType getUpcoming_event() {
        return upcoming_event;
    }

    public NotificationRange getNotificationRange() {
        return notificationRange;
    }

    public NotificationSettingsDTO() {}

    public NotificationSettingsDTO(NotificationSettings notificationSettings) {
        this.id = notificationSettings.getId();
        this.event_changed = notificationSettings.getEvent_changed();
        this.invite_guest = notificationSettings.getInvite_guest();
        this.uninvite_guest = notificationSettings.getUninvite_guest();
        this.user_status = notificationSettings.getUser_status();
        this.user_role = notificationSettings.getUser_role();
        this.cancel_event = notificationSettings.getCancel_event();
        this.upcoming_event = notificationSettings.getUpcoming_event();
        this.notificationRange = notificationSettings.getNotificationRange();
    }


    @Override
    public String toString() {
        return "NotificationSettingsDTO{" +
                "id=" + id +
                ", event_changed=" + event_changed +
                ", invite_guest=" + invite_guest +
                ", uninvite_guest=" + uninvite_guest +
                ", user_status=" + user_status +
                ", user_role=" + user_role +
                ", cancel_event=" + cancel_event +
                ", upcoming_event=" + upcoming_event +
                ", notificationRange=" + notificationRange +
                '}';
    }
}