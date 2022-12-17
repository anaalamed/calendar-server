package calendar.service;

import calendar.controller.request.EventRequest;
import calendar.controller.request.UserRequest;
import calendar.entities.DTO.UserDTO;
import calendar.entities.Event;
import calendar.entities.User;
import calendar.repository.EventRepository;
import calendar.repository.UserRepository;
import calendar.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

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
            throw new SQLDataException(String.format("Event %s not exists!",id));
        }
    }

    /**
     * Delete an event from the DB if founded
     *
     * @param event
     * @return the number of deleted rows
     * @throws SQLDataException
     */
    public int deleteEvent(Event event) throws SQLDataException {
        if (eventRepository.findById(event.getId()).isPresent()) {
            return eventRepository.deleteById(event.getId());
        } else {
            throw new SQLDataException(String.format("Event %s not exists!", event.getId()));
        }
    }

    /**
     * Update the event if founded in db
     *
     * @param event
     * @return the Updated Event
     * @throws SQLDataException
     */
    public Event updateEvent(Event event) throws SQLDataException {
        if (eventRepository.findById(event.getId()).isPresent()) {
            updateEventTitle(event);
            updateEventDescription(event);
            updateEventDate(event);
            updateEventDuration(event);
            updateEventTime(event);
            updateEventLocation(event);
            updateEventIsPublic(event);

            if (eventRepository.updateEventIsPublic(event.isPublic(), event.getId()) > 0)
                return eventRepository.findById(event.getId()).get();

        } else {
            throw new SQLDataException(String.format("Event %s not exists!", event.getId()));
        }
        return getEventById(event.getId());
    }

    /**
     * Update Title of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventTitle(Event event) throws SQLDataException {
        if (eventRepository.updateEventTitle(event.getTitle(), event.getId()) > 0)
            return eventRepository.findById(event.getId()).get();
        else {
            throw new SQLDataException(String.format("Failed to update Title of Event %s !", event.getId()));
        }
    }

    /**
     * Update Location of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventLocation(Event event) throws SQLDataException {
        if (eventRepository.updateEventLocation(event.getLocation(), event.getId()) > 0)
            return eventRepository.findById(event.getId()).get();
        else {
            throw new SQLDataException(String.format("Failed to update Location of Event %s !", event.getId()));
        }
    }

    /**
     * Update Time of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventTime(Event event) throws SQLDataException {
        if (eventRepository.updateEventTime(event.getTime(), event.getId()) > 0)
            return eventRepository.findById(event.getId()).get();
        else {
            throw new SQLDataException(String.format("Failed to update Time of Event %s !", event.getId()));
        }
    }

    /**
     * Update IsPublic (accessibility) of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventIsPublic(Event event) throws SQLDataException {
        if (eventRepository.updateEventIsPublic(event.isPublic(), event.getId()) > 0)
            return eventRepository.findById(event.getId()).get();
        else {
            throw new SQLDataException(String.format("Failed to update accessibility of Event %s !", event.getId()));
        }
    }

    /**
     * Update Duration of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventDuration(Event event) throws SQLDataException {
        if (eventRepository.updateEventDuration(event.getDuration(), event.getId()) > 0)
            return eventRepository.findById(event.getId()).get();
        else {
            throw new SQLDataException(String.format("Failed to update Duration of Event %s !", event.getId()));
        }
    }

    /**
     * Update Description of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventDescription(Event event) throws SQLDataException {
        if (eventRepository.updateEventDescription(event.getDescription(), event.getId()) > 0)
            return eventRepository.findById(event.getId()).get();
        else {
            throw new SQLDataException(String.format("Failed to update Description of Event %s !", event.getId()));
        }
    }

    /**
     * Update date of the event
     *
     * @param event
     * @return updated event
     * @throws SQLDataException
     */
    public Event updateEventDate(Event event) throws SQLDataException {
        if (eventRepository.updateEventDate(event.getDate(), event.getId()) > 0)
            return eventRepository.findById(event.getId()).get();
        else {
            throw new SQLDataException(String.format("Failed to update Date of Event %s !", event.getId()));
        }
    }


}
