package calendar.service;

import calendar.entities.*;
import calendar.entities.enums.*;
import calendar.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Role saveRoleInDB(User user, Event event,StatusType statusType,  RoleType roleType) {
        return roleRepository.save(new Role(user, event, statusType, roleType));
    }

    public void deleteRole(Role role) {
         roleRepository.delete(role);
    }
}
