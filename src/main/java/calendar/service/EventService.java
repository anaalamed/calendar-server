package calendar.service;

import calendar.controller.request.EventRequest;
import calendar.entities.*;
import calendar.entities.DTO.RoleDTO;
import calendar.entities.enums.*;
import calendar.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    UserRepository userRepository;

    /**
     * Create new event if isn't already exist
     *
     * @param eventRequest
     * @param userOfEvent
     * @return the created Event
     * @throws SQLDataException
     */
    public Event saveEvent(EventRequest eventRequest, User userOfEvent) throws SQLDataException {

        Event eventReq = Event.getNewEvent(eventRequest.isPublic(), eventRequest.getTime(),  eventRequest.getDuration(), eventRequest.getLocation(),
                eventRequest.getTitle(), eventRequest.getDescription(), eventRequest.getAttachments());


        Role organizer = new Role(userOfEvent, StatusType.APPROVED, RoleType.ORGANIZER);

        eventReq.getRoles().add(organizer);

        return eventRepository.save(eventReq);
    }

    /**
     * get event by id if it exists in db
     *
     * @param id - the id of the event we want to retrieve.
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
     * Delete an event from the DB if founded
     *
     * @param eventId
     * @return the number of deleted rows
     * @throws SQLDataException
     */
    public int deleteEvent(int eventId) throws SQLDataException {
        return eventRepository.deleteById(eventId);
    }

    /**
     * Update the event if founded in db
     * For ORGANIZER
     *
     * @param event
     * @return the Updated Event
     * @throws SQLDataException
     */
    public Event updateEvent(EventRequest event, int id) throws SQLDataException {

        Event eventDB = eventRepository.findById(id).get();

        if (eventDB == null) {
            throw new SQLDataException("Event does not exist!");
        }

        if (!event.isPublic())
            event.setPublic(eventDB.isPublic());

//        if (event.getDate() == null)
//            event.setDate(eventDB.getDate());

        if (event.getTime() == null)
            event.setTime(eventDB.getTime());

        if (event.getTitle() == null)
            event.setTitle(eventDB.getTitle());

        if (event.getDuration() == 0)
            event.setDuration(eventDB.getDuration());

        if (event.getDescription() == null)
            event.setDescription(eventDB.getDescription());

        if (event.getAttachments() == null)
            event.setAttachments(eventDB.getAttachments());

        if (event.getLocation() == null)
            event.setLocation(eventDB.getLocation());

        int rows = eventRepository.updateEvent(event.isPublic(), event.getTitle(), event.getTime()
                , event.getDuration(), event.getLocation(), event.getDescription(), id);
        if (rows > 0) {
            return eventRepository.findById(id).get();
        } else {
            return null;
        }
    }


    /**
     * Update the event if founded in db
     * For ADMIN
     *
     * @param event
     * @return the Updated Event
     * @throws SQLDataException
     */
    public Event updateEventRestricted(EventRequest event, int id) throws SQLDataException {

        Event eventDB = eventRepository.findById(id).get();

        if (!event.isPublic())
            event.setPublic(eventDB.isPublic());

        if (event.getDescription() == null)
            event.setDescription(eventDB.getDescription());

        if (event.getAttachments() == null)
            event.setAttachments(eventDB.getAttachments());

        if (event.getLocation() == null)
            event.setLocation(eventDB.getLocation());


        int rows = eventRepository.updateEventRestricted(event.isPublic(), event.getLocation(), event.getDescription(), id);
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
    public Event updateEventTitle(EventRequest event, int id) throws SQLDataException {
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
    public Event updateEventLocation(EventRequest event, int id) throws SQLDataException {
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
    public Event updateEventTime(EventRequest event, int id) throws SQLDataException {
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
    public Event updateEventIsPublic(EventRequest event, int id) throws SQLDataException {
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
    public Event updateEventDuration(EventRequest event, int id) throws SQLDataException {
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
    public Event updateEventDescription(EventRequest event, int id) throws SQLDataException {
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
    public Event updateEventDate(EventRequest event, int id) throws SQLDataException {
//        if (eventRepository.updateEventDate(event.getDate(), id) > 0)
//            return eventRepository.findById(id).get();
//        else {
//            return null;
//        }
        return null;
    }

    /**
     * get all events of a user by his id
     *
     * @param userId - the id of the user we want to get all of his events.
     * @return a list of all the events.
     */
    public List<Event> getEventsByUserId(int userId) {

        List<Event> allEvents = eventRepository.findAll();

        List<Event> eventsOfUser = new ArrayList<>();

        for (Event event : allEvents) {

            for (Role role : event.getRoles()) {
                if (role.getUser().getId() == userId) {
                    eventsOfUser.add(event);
                }
            }
        }
        return eventsOfUser;
    }

    /**
     * Returns one specific role of a user in an event, User can be part of many events, so he can have many
     * roles, but he can only have one role per event and that's the one we will return here.
     *
     * @param userId  - The id of the user which we want to retrieve.
     * @param eventId - The id of the event which we want to retrieve.
     * @return The Role we wanted to get from the DB with the exact user ID and event id combination.
     */
    public Role getSpecificRole(int userId, int eventId) {

        Event event;
        Role role = null;

        if (eventRepository.findById(eventId).isPresent()) {
            event = eventRepository.findById(eventId).get();
            role = event.getUserRole(userId);
        } else { //Event does not exist!
            return null;
        }

        if (role == null) {
            return null;
        }
        return role;
    }

    /**
     * Removes a user from an event, only admins and organizers can remove people.
     * The role that represent the combination of the user id and event id will be removed from the DB
     *
     * @param userId  - The id of the user we want to remove.
     * @param eventId -The id of the event we wish to remove the guest from.
     * @return a message confirming the removal of the guest.
     */
    public Role removeGuest(int userId, int eventId) {

        Event event = eventRepository.findById(eventId).get();

        if (event == null) {
            throw new IllegalArgumentException("The event does not exist!");
        }

        Role roleToRemove = getSpecificRole(userId, eventId);

        if (roleToRemove == null) {
            throw new IllegalArgumentException("The user is not part of the event!");
        }

        if (roleToRemove.getRoleType() == RoleType.ORGANIZER) {
            throw new IllegalArgumentException("Cannot remove an organizer from an event!");
        }

        event.removeRole(roleToRemove);
        eventRepository.save(event);

        return roleToRemove;

    }

    /**
     * Invites a user to be a guest in an event, only admins and organizers can invite people.
     * A role will be created with a GUEST type and TENTATIVE status.
     *
     * @param user    - The user we wish to invite to the event.
     * @param eventId - The id of the event to which we want to invite the user.
     * @return the invited user role.
     */
    public Role inviteGuest(User user, int eventId) {

        Event event = eventRepository.findById(eventId).get();

        if (event == null) {
            throw new IllegalArgumentException("The event does not exist!");
        }

        Role roleToAdd = getSpecificRole(user.getId(), eventId);

        if (roleToAdd != null) {
            throw new IllegalArgumentException("The user is already part of this event!");
        }

        Role role = new Role(user, StatusType.TENTATIVE, RoleType.GUEST);

        event.getRoles().add(role);
        eventRepository.save(event);

        return role;
    }

    /**
     * Promotes a guest to an admin, only an organizer can promote someone.
     *
     * @param eventId - The event id of the event we wish to switch someones role at.
     * @param userId  - The user id of the user we wish to switch his role.
     * @return -a message confirming the removal of the role.
     */
    public Role switchRole(int userId, int eventId) {

        Event event = eventRepository.findById(eventId).get();

        if (event == null) {
            throw new IllegalArgumentException("Event does not exist!");
        }

        User user = userRepository.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User does not exist!");
        }

        Role roleToPromote = getSpecificRole(userId, eventId);

        if (roleToPromote == null) {
            throw new IllegalArgumentException("The user is not part of the event!");
        }

        if (roleToPromote.getRoleType().equals(RoleType.GUEST)) {
            roleToPromote.setRoleType(RoleType.ADMIN);
        } else if (roleToPromote.getRoleType().equals(RoleType.ADMIN)) {
            roleToPromote.setRoleType(RoleType.GUEST);
        }

        eventRepository.save(event);

        return roleToPromote;
    }

    /**
     * Changed the status of a guest can be APPROVED or REJECTED.
     *
     * @param eventId         - The event id of the event we wish to switch someones role at.
     * @param userId          - The user id of the user we wish to switch his role.
     * @param approveOrReject - A boolean value true if approved false if rejected.
     * @return -the role after the changes.
     */
    public Role switchStatus(int userId, int eventId, boolean approveOrReject) {

        Event event = eventRepository.findById(eventId).get();

        if (event == null) {
            throw new IllegalArgumentException("Event does not exist!");
        }

        User user = userRepository.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User does not exist!");
        }

        Role roleToUpdate = getSpecificRole(userId, eventId);

        if (roleToUpdate == null) {
            throw new IllegalArgumentException("The user is not part of the event!");
        }

        if (approveOrReject) {
            roleToUpdate.setStatusType(StatusType.APPROVED);
        } else {
            roleToUpdate.setStatusType(StatusType.REJECTED);
        }

        eventRepository.save(event);

        return roleToUpdate;
    }

    public Role leaveEvent(int userId, int eventId) {

        Event event = eventRepository.findById(eventId).get();

        if (event == null) {
            throw new IllegalArgumentException("Event does not exist!");
        }

        User user = userRepository.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User does not exist!");
        }

        Role roleToHide = getSpecificRole(userId, eventId);

        if (roleToHide == null) {
            throw new IllegalArgumentException("The user is not part of the event!");
        }

        if(roleToHide.getRoleType() == RoleType.ORGANIZER){
            throw new IllegalArgumentException("Organizer cant use this function, use delete event instead!");
        }

        roleToHide.setShownInMyCalendar(false);
        roleToHide.setStatusType(StatusType.REJECTED);

        eventRepository.save(event);

        return roleToHide;
    }
}
