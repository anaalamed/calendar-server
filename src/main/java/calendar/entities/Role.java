package calendar.entities;

import calendar.entities.enums.RoleType;
import calendar.entities.enums.StatusType;

import javax.persistence.*;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne
    private User user;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Event event;

    @Column(nullable = false)
    private StatusType statusType;

    @Column(nullable = false)
    private RoleType roleType;

    public Role() {
    }

    public Role(User user, Event event, StatusType statusType, RoleType roleType) {
        this.user = user;
        this.event = event;
        this.statusType = statusType;
        this.roleType = roleType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }
}
