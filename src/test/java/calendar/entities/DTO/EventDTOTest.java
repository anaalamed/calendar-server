package calendar.entities.DTO;

import calendar.entities.Event;
import calendar.entities.NotificationSettings;
import calendar.entities.Role;
import calendar.entities.User;
import calendar.entities.enums.ProviderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventDTOTest {

    private Role role;
    private List<Role> roles;
    private User user;
    private Event event;
    private List<Event> events;

    @BeforeEach
    void setup() {

        user = new User("Leon","Leon@test.com","leon1234", ProviderType.LOCAL);
        user.setNotificationSettings(new NotificationSettings());

        role = new Role();
        role.setUser(user);

        roles = new ArrayList<>();
        roles.add(role);

        event = new Event();
        event.setId(1);

        events = new ArrayList<>();
        events.add(event);
    }


    @Test
    void Convert_Roles_To_Roles_DTO(){

        List<RoleDTO> rolesToRolesDTO = EventDTO.convertRolesToRolesDTO(roles);

        assertNotNull(rolesToRolesDTO);
        assertEquals(rolesToRolesDTO.get(0).getUser().getName(),user.getName());
    }

    @Test
    void Convert_Events_To_Events_DTO(){
        List<EventDTO> eventDTOList = EventDTO.convertEventsToEventsDTO(events);

        assertNotNull(eventDTOList);
        assertEquals(eventDTOList.get(0).getId(),event.getId());
    }

}