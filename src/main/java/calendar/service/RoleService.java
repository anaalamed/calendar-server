package calendar.service;

import calendar.controller.response.BaseResponse;
import calendar.entities.*;
import calendar.entities.enums.*;
import calendar.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Role saveRoleInDB(User user, Event event, StatusType statusType, RoleType roleType) {
        return roleRepository.save(new Role(user, event, statusType, roleType));
    }

    public Role saveRoleInDB(Role role) {
        if (getSpecificRole(role.getUser().getId(), role.getEvent().getId()) != null) {
            return null;
        }
        return roleRepository.save(role);
    }

    public boolean deleteRole(Role role) {
        Role roleToDelete = getSpecificRole(role.getUser().getId(), role.getEvent().getId());

        if (roleToDelete == null) {
            return false;
        } else {
            roleRepository.delete(roleToDelete);
            return true;
        }
    }

    public List<Role> getRoleByEventId(int eventId) {

        return roleRepository.findByEventId(eventId);
    }

    public List<Role> getRoleByUserId(int userId) {
        return roleRepository.findByUserId(userId);
    }

    public Role getSpecificRole(int userId, int eventId) {

        List<Role> roles = roleRepository.findByUserId(userId);

        if (roles == null) {
            return null;
        }

        return roles.stream().filter(role -> role.getEvent().getId() == eventId).findFirst()
                .orElse(null);
    }

    public boolean switchRole(int userId,int eventId) {

        Role roleToPromote = getSpecificRole(userId, eventId);

        if (roleToPromote == null) {
            return false;
        }

        if (roleToPromote.getRoleType().equals(RoleType.GUEST)) {
            roleToPromote.setRoleType(RoleType.ADMIN);
        } else if (roleToPromote.getRoleType().equals(RoleType.ADMIN)) {
            roleToPromote.setRoleType(RoleType.GUEST);
        }

        roleRepository.updateRoleType(roleToPromote.getUser(),roleToPromote.getEvent(),roleToPromote.getRoleType());
        return true;
    }

    public Role inviteGuest(User user, Event event) {

        Role roleToAdd = getSpecificRole(user.getId(), event.getId());

        if (roleToAdd != null) {
            return null;
        }

        Role role = new Role(user, event, StatusType.TENTATIVE, RoleType.GUEST);
        roleRepository.save(role);
        return role;
    }

    public boolean removeGuest(int userId, int eventId) {

        Role roleToRemove = getSpecificRole(userId, eventId);

        if (roleToRemove == null) {
            return false;
        } else {
            roleRepository.delete(roleToRemove);
            return true;
        }
    }

    public List<Event> getEventsByUserId(int userId){
       List<Role> roles = getRoleByUserId(userId);

       List<Event> events = new ArrayList<>();

        for (Role role: roles) {
            events.add(role.getEvent());
        }

       return events;
    }
}
