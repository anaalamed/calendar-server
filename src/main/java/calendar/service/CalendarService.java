package calendar.service;

import calendar.entities.DTO.EventDTO;
import calendar.entities.Event;
import calendar.entities.User;
import calendar.repository.EventRepository;
import calendar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalendarService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;


    /**
     * Share user events with other user. This will add the viewer to an array of the user which holds all the users
     * who shared their calendars with him.
     *
     * @param viewerId - who im sharing with.
     * @return BaseResponse The user i added to my shared calendars list.
     */
    public User shareCalendar(int userId, int viewerId) {

        User user= userRepository.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User does not exist!");
        }

        User viewer= userRepository.findById(viewerId);

        if (viewer == null) {
            throw new IllegalArgumentException("User does not exist!");
        }

        if (viewer.getUsersWhoSharedTheirCalendarWithMe().contains(user)) {
            throw new IllegalArgumentException("This viewer is already part of my shared calendars!");
        }

        viewer.addSharedCalendar(user);

        userRepository.save(viewer);

        return viewer;
    }


    public Map<String, List<EventDTO>> GetAllShared(int userId, List<Event> myEventsToShow) {

        Map<String, List<EventDTO>> map = new HashMap<>();

        User user= userRepository.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User does not exist!");
        }

        List<User> sharedUsers = user.getUsersWhoSharedTheirCalendarWithMe();

        map.put(user.getEmail(),EventDTO.convertEventsToEventsDTO(myEventsToShow));

        for (User sharedUser :sharedUsers) {
            List<EventDTO> events = EventDTO.convertEventsToEventsDTO(eventRepository.findAll());
            map.put(sharedUser.getEmail(),events.stream().filter(event -> event.isPublic()).collect(Collectors.toList()));
        }

        return map;
    }
}
