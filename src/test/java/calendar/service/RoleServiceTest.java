package calendar.service;

import calendar.controller.response.BaseResponse;
import calendar.entities.Event;
import calendar.entities.Role;
import calendar.entities.User;
import calendar.entities.enums.RoleType;
import calendar.entities.enums.StatusType;
import calendar.repository.EventRepository;
import calendar.repository.RoleRepository;
import calendar.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RoleServiceTest {

    @Autowired
    RoleService roleService;
    @MockBean
    RoleRepository roleRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    EventRepository eventRepository;



    static Role role;
    static Role roleToInvite;
    static Event event;
    static User user;
    static User userToInvite;
    static List<Role> roles;
    static List<User> users;
    static List<Event> events;

    @BeforeAll
    static void setup() {
        event = new Event();
        event.setId(1);
        events = new ArrayList<>();
        events.add(event);

        user = new User();
        user.setId(1);
        users = new ArrayList<>();
        users.add(user);

        userToInvite = new User();
        userToInvite.setId(123);

        role = new Role();
        role.setRoleType(RoleType.GUEST);
        role.setEvent(event);
        role.setUser(user);
        role.setStatusType(StatusType.APPROVED);

        roles = new ArrayList<>();
        roles.add(role);

        roleToInvite = new Role();
        roleToInvite.setRoleType(RoleType.GUEST);
        roleToInvite.setEvent(event);
        roleToInvite.setUser(userToInvite);
        roleToInvite.setStatusType(StatusType.TENTATIVE);
    }

    @Test
    void Save_Role_Successfully(){
        when(roleRepository.save(role)).thenReturn(role);

        Role response = roleService.saveRoleInDB(role);

        assertEquals(role, response);
    }

    @Test
    void Try_To_Save_Role_That_already_Exists(){
        when(roleRepository.save(role)).thenReturn(null);

        Role response = roleService.saveRoleInDB(role);

        assertEquals(null, response);
    }

    @Test
    void Delete_Role_Successfully(){
        when(roleRepository.findByUserId(1)).thenReturn(roles);

        assertDoesNotThrow(() -> roleService.deleteRole(role));
    }

    @Test
    void Try_To_Delete_Role_That_Does_Not_Exist(){
        when(roleService.getSpecificRole(999,999)).thenReturn(null);

        assertDoesNotThrow(() -> roleService.deleteRole(role));
    }


}