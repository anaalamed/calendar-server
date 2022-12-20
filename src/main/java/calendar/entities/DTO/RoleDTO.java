package calendar.entities.DTO;

import calendar.entities.*;
import calendar.entities.enums.*;

public class RoleDTO {

    private Long id;

    private UserDTO user;

    private Event event;

    private StatusType statusType;

    private RoleType roleType;

    public RoleDTO() {
    }

    public RoleDTO(Role role) {
        this.id = role.getId();
        this.user = new UserDTO(role.getUser());
        this.event = role.getEvent();
        this.statusType = role.getStatusType();
        this.roleType = role.getRoleType();
    }

    public Long getId() {
        return id;
    }

    public UserDTO getUser() {
        return user;
    }

    public Event getEvent() {
        return event;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
                "id=" + id +
                ", user=" + user +
                ", event=" + event +
                ", statusType=" + statusType +
                ", roleType=" + roleType +
                '}';
    }
}
