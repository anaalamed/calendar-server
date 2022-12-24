package calendar.entities;

import calendar.entities.enums.NotificationGetType;
import calendar.entities.enums.NotificationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@DynamicUpdate
@Entity
@Table(name = "notificationSettings")
public class NotificationSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "EVENT_CHANGED")
    private NotificationGetType event_changed;

    @Enumerated(EnumType.STRING)
    @Column(name = "INVITE_GUEST")
    private NotificationGetType invite_guest;

    @Enumerated(EnumType.STRING)
    @Column(name = "UNINVITE_GUEST")
    private NotificationGetType uninvite_guest;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_STATUS")
    private NotificationGetType user_status;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE")
    private NotificationGetType user_role;

    @Enumerated(EnumType.STRING)
    @Column(name = "CANCEL_EVENT")
    private NotificationGetType cancel_event;

    @Enumerated(EnumType.STRING)
    @Column(name = "UPCOMING_EVENT")
    private NotificationGetType upcoming_event;

    private static final Logger logger = LogManager.getLogger(NotificationSettings.class.getName());


    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public NotificationGetType getValue(NotificationType notificationType) {
        switch (notificationType) {
//            case REGISTER:
//                logger.info("REGISTER");
//                return ;
            case EVENT_CHANGED:
                logger.info("EVENT_CHANGED");
                return getEvent_changed();
            case INVITE_GUEST:
                logger.info("INVITE_GUEST");
                return getInvite_guest();
            case UNINVITE_GUEST:
                logger.info("UNINVITE_GUEST");
                return getUninvite_guest();
            case USER_STATUS_CHANGED:
                logger.info("USER_STATUS_CHANGED");
                return getUser_status();
            case USER_ROLE_CHANGED:
                logger.info("USER_ROLE_CHANGED");
                return getUser_role();
            case CANCEL_EVENT:
                logger.info("CANCEL_EVENT");
                return getCancel_event();
            case UPCOMING_EVENT:
                logger.info("UPCOMING_EVENT");
                return getUpcoming_event();
            default:
                logger.info("default notification type");
                break;
        }
        return null;
    }

    public NotificationSettings() {
    }

    //create
    public NotificationSettings(User user) {
        this.user = user;
        this.event_changed = NotificationGetType.NONE;
        this.invite_guest = NotificationGetType.NONE;
        this.uninvite_guest = NotificationGetType.NONE;
        this.user_status = NotificationGetType.NONE;
        this.user_role = NotificationGetType.NONE;
        this.cancel_event = NotificationGetType.NONE;
        this.upcoming_event = NotificationGetType.NONE;
    }

    // update
    public NotificationSettings(User user, NotificationGetType event_changed, NotificationGetType invite_guest, NotificationGetType uninvite_guest, NotificationGetType user_status, NotificationGetType user_role, NotificationGetType cancel_event, NotificationGetType upcoming_event) {
        this.user = user;
        this.event_changed = event_changed;
        this.invite_guest = invite_guest;
        this.uninvite_guest = uninvite_guest;
        this.user_status = user_status;
        this.user_role = user_role;
        this.cancel_event = cancel_event;
        this.upcoming_event = upcoming_event;
    }

    @Override
    public String toString() {
        return "NotificationSettings{" +
                "id=" + id +
//                ", user=" + user +
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
