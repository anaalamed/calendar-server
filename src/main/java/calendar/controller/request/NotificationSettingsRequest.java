package calendar.controller.request;

import calendar.entities.enums.NotificationGetType;

public class NotificationSettingsRequest {

    private NotificationGetType event_changed, invite_guest, uninvite_guest, user_status, user_role, cancel_event, upcoming_event;

    public NotificationGetType getEvent_changed() {
        return event_changed;
    }

    public void setEvent_changed(NotificationGetType event_changed) {
        this.event_changed = event_changed;
    }

    public NotificationGetType getInvite_guest() {
        return invite_guest;
    }

    public void setInvite_guest(NotificationGetType invite_guest) {
        this.invite_guest = invite_guest;
    }

    public NotificationGetType getUninvite_guest() {
        return uninvite_guest;
    }

    public void setUninvite_guest(NotificationGetType uninvite_guest) {
        this.uninvite_guest = uninvite_guest;
    }

    public NotificationGetType getUser_status() {
        return user_status;
    }

    public void setUser_status(NotificationGetType user_status) {
        this.user_status = user_status;
    }

    public NotificationGetType getUser_role() {
        return user_role;
    }

    public void setUser_role(NotificationGetType user_role) {
        this.user_role = user_role;
    }

    public NotificationGetType getCancel_event() {
        return cancel_event;
    }

    public void setCancel_event(NotificationGetType cancel_event) {
        this.cancel_event = cancel_event;
    }

    public NotificationGetType getUpcoming_event() {
        return upcoming_event;
    }

    public void setUpcoming_event(NotificationGetType upcoming_event) {
        this.upcoming_event = upcoming_event;
    }


    public NotificationSettingsRequest() {
    }

    @Override
    public String toString() {
        return "NotificationSettings{" +
                ", event_changed=" + event_changed +
                ", invite_guest=" + invite_guest +
                ", uninvite_guest=" + uninvite_guest +
                ", user_status=" + user_status +
                ", user_role=" + user_role +
                ", cancel_event=" + cancel_event +
                ", upcoming_event=" + upcoming_event +
                '}';
    }
}









