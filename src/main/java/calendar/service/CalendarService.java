package calendar.service;

import calendar.entities.DTO.EventDTO;
import calendar.entities.Event;
import calendar.entities.User;
import calendar.repository.EventRepository;
import calendar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     * Share my calendar with a different user using his id, I will insert myself into his list of
     * users who shared their calendar with him.
     * @param viewer - the user I want to share my calendar with.
     * @param user- My user information.
     * @return The user i shared my calendar with.
     */
    public User shareCalendar(User user, User viewer) {


        if (user == null) {
            throw new IllegalArgumentException("User does not exist!");
        }


        if (viewer == null) {
            throw new IllegalArgumentException("Viewer does not exist!");
        }

        if (viewer.getUsersWhoSharedTheirCalendarWithMe().contains(user)) {
            throw new IllegalArgumentException("This viewer is already part of my shared calendars!");
        }

        viewer.addSharedCalendar(user);

        userRepository.save(viewer);

        return viewer;
    }

}
