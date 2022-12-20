package calendar.service;

import calendar.controller.request.EventRequest;
import calendar.controller.request.UserRequest;
import calendar.entities.DTO.UserDTO;
import calendar.entities.Event;
import calendar.entities.User;
import calendar.repository.EventRepository;
import calendar.repository.RoleRepository;
import calendar.repository.UserRepository;
import calendar.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.List;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    /**
     * Create new event if isn't already exist
     *
     * @param eventRequest
     * @return the created Event
     * @throws SQLDataException
     */
    public Event saveEvent(EventRequest eventRequest) throws SQLDataException {
        Event eventReq = new Event.Builder(eventRequest.getTime(), eventRequest.getDate(), eventRequest.getTitle()).build();
        if (eventRepository.findById(eventReq.getId()).isPresent()) {
            throw new SQLDataException(String.format("Event %s already exists!", eventReq.getId()));
        }
        return eventRepository.save(eventReq);
    }

    /**
     * get event by id if founded in db
     *
     * @param id
     * @return the Updated Event
     * @throws SQLDataException
     */
    public Event getEventById(int id) throws SQLDataException {
        if (eventRepository.findById(id).isPresent()) {
            return eventRepository.findById(id).get();
        } else {
            return null;
        }
    }

    /**
     * Returns all the events in the user repo , this is used for server side only so no need to use DTO.
     *
     * @return a list of all the events inside the DB.
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Delete an event from the DB if founded
     *
     * @param eventId
     * @return the number of deleted rows
     * @throws SQLDataException
     */
    public int deleteEvent(int eventId) throws SQLDataException {
        Event e = eventRepository.findById(eventId).get();
        if (eventRepository.findById(eventId).isPresent()) {
            roleRepository.deleteById(e);
            return eventRepository.deleteById(eventId);
        } else {
            return 0;
        }
    }

    /**
     * Update the event if founded in db
     *
     * @param event
     * @return the Updated Event
     * @throws SQLDataException
     */
    public Event updateEvent(EventRequest event,int id) throws SQLDataException {
        int rows = eventRepository.updateEvent(event.isPublic(), event.getTitle(), event.getDate(), event.getTime()
                , event.getDuration(), event.getLocation(), event.getDescription(), id);
        if (rows > 0) {
            return eventRepository.findById(id).get();
        } else {
            return null;
        }
    }

    /**
     * Update Title of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventTitle(EventRequest event,int id) throws SQLDataException {
        int rows = eventRepository.updateEventTitle(event.getTitle(), id);
        if (rows > 0)//number of updated rows in db
        {
            return eventRepository.findById(id).get();
        } else {
            return null;
        }
    }

    /**
     * Update Location of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventLocation(EventRequest event,int id) throws SQLDataException {
        if (eventRepository.updateEventLocation(event.getLocation(), id) > 0)
            return eventRepository.findById(id).get();
        else {
            return null;
        }
    }

    /**
     * Update Time of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventTime(EventRequest event ,int id) throws SQLDataException {
        if (eventRepository.updateEventTime(event.getTime(), id) > 0)
            return eventRepository.findById(id).get();
        else {
            return null;
        }
    }

    /**
     * Update IsPublic (accessibility) of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventIsPublic(EventRequest event,int id) throws SQLDataException {
        if (eventRepository.updateEventIsPublic(event.isPublic(), id) > 0)
            return eventRepository.findById(id).get();
        else {
            return null;
        }
    }

    /**
     * Update Duration of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventDuration(EventRequest event,int id) throws SQLDataException {
        if (eventRepository.updateEventDuration(event.getDuration(), id) > 0)
            return eventRepository.findById(id).get();
        else {
            return null;
        }
    }

    /**
     * Update Description of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventDescription(EventRequest event,int id) throws SQLDataException {
        if (eventRepository.updateEventDescription(event.getDescription(), id) > 0)
            return eventRepository.findById(id).get();
        else {
            return null;
        }
    }

    /**
     * Update date of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventDate(EventRequest event,int id) throws SQLDataException {
        if (eventRepository.updateEventDate(event.getDate(), id) > 0)
            return eventRepository.findById(id).get();
        else {
            return null;
        }
    }


}
