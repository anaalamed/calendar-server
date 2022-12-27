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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusType statusType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column(nullable = false)
    private boolean isShownInMyCalendar;

    public Role() {
    }

    public Role(User user, StatusType statusType, RoleType roleType) {
        this.user = user;
        this.statusType = statusType;
        this.roleType = roleType;
        this.isShownInMyCalendar = true;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Long getId() {
        return id;
    }

    public boolean isShownInMyCalendar() {
        return isShownInMyCalendar;
    }

    public void setShownInMyCalendar(boolean shownInMyCalendar) {
        isShownInMyCalendar = shownInMyCalendar;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", user=" + user +
                ", statusType=" + statusType +
                ", roleType=" + roleType +
                ", isShownInMyCalendar=" + isShownInMyCalendar +
                '}';
    }
}
