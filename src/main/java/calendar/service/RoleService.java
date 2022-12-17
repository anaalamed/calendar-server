package calendar.service;

import calendar.entities.*;
import calendar.entities.enums.*;
import calendar.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Role saveRoleInDB(User user, Event event, StatusType statusType, RoleType roleType) {
        return roleRepository.save(new Role(user, event, statusType, roleType));
    }

    public Role saveRoleInDB(Role role) {
        return roleRepository.save(role);
    }

    public void deleteRole(Role role) {
        roleRepository.delete(role);
    }

    public List<Role> getRoleByEventId(int eventId) {
        return roleRepository.findByEventId(eventId);
    }

    public List<Role> getRoleByUserId(int userId) {
        return roleRepository.findByUserId(userId);
    }

    public Role getSpecificRole(int userId, int eventId) {
        List<Role>  roles = roleRepository.findByUserId(userId);
        return  roles.stream().filter(role -> role.getEvent().getId() == eventId).findFirst()
                .orElse(null);
    }

    public void switchRole(Role roleToPromote) {

        if(roleToPromote.getRoleType().equals(RoleType.GUEST)){
            roleToPromote.setRoleType(RoleType.ADMIN);
        }else if(roleToPromote.getRoleType().equals(RoleType.ADMIN)){
            roleToPromote.setRoleType(RoleType.GUEST);
        }
        roleRepository.save(roleToPromote);
    }

    public Role inviteGuest(User user, Event event) {
        Role role = new Role(user,event,StatusType.TENTATIVE,RoleType.GUEST);
        roleRepository.save(role);
        return role;
    }
}
