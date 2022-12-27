package calendar.service;

import calendar.controller.request.EventRequest;
import calendar.entities.*;
import calendar.entities.enums.*;
import calendar.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    UserRepository userRepository;

    /**
     * Create a new event and save it in the database
     *
     * @param eventRequest - The information of the event we want to save in the database.
     * @return The created event.
     */
    public Event saveEvent(EventRequest eventRequest, User userOfEvent) {

        Event eventReq = Event.getNewEvent(eventRequest.isPublic(), eventRequest.getTime(), eventRequest.getDuration(), eventRequest.getLocation(),
                eventRequest.getTitle(), eventRequest.getDescription(), eventRequest.getAttachments());


        Role organizer = new Role(userOfEvent, StatusType.APPROVED, RoleType.ORGANIZER);

        eventReq.getRoles().add(organizer);

        return eventRepository.save(eventReq);
    }

    /**
     * get an event from the database by an event id if it exists.
     *
     * @param id - the id of the event we wish to retrieve.
     * @return The event we wanted to get or null if not found.
     */
    public Event getEventById(int id) {
        if (eventRepository.findById(id).isPresent()) {
            return eventRepository.findById(id).get();
        } else {
            return null;
        }
    }

    /**
     * Delete an event by id from the database. Only an organizer can delete an event.
     *
     * @param event - The event we wish to delete.
     */
    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }

    /**
     * Update an event in the database if it exists. Only an organizer can access this method.
     *
     * @param event    - The event with the updated information.
     * @param eventId  - The if of the event we wish to update.
     * @return The updated event.
     */
    public Event updateEvent(EventRequest event, int eventId) {

        Event eventDB = eventRepository.findById(eventId).get();

        if (eventDB == null) {
            throw new IllegalArgumentException("Event does not exist!");
        }

        if (!event.isPublic())
            event.setPublic(eventDB.isPublic());

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
                , event.getDuration(), event.getLocation(), event.getDescription(), eventId);

        if (rows > 0) {
            return eventRepository.findById(eventId).get();
        } else {
            return null;
        }
    }


    /**
     * Update an event in the database if it exists. Only an admin can access this method.
     * Unlike the normal update method , this method can update only a restricted amount of fields.
     * @param event    - The event with the updated information.
     * @param eventId  - The if of the event we wish to update.
     * @return The updated event.
     */
    public Event updateEventRestricted(EventRequest event, int eventId) {

        Event eventDB = eventRepository.findById(eventId).get();

        if (eventDB == null) {
            throw new IllegalArgumentException("Event does not exist!");
        }

        if (!event.isPublic())
            event.setPublic(eventDB.isPublic());

        if (event.getDescription() == null)
            event.setDescription(eventDB.getDescription());

        if (event.getAttachments() == null)
            event.setAttachments(eventDB.getAttachments());

        if (event.getLocation() == null)
            event.setLocation(eventDB.getLocation());


        int rows = eventRepository.updateEventRestricted(event.isPublic(), event.getLocation(), event.getDescription(), eventId);

        if (rows > 0) {
            return eventRepository.findById(eventId).get();
        } else {
            return null;
        }
    }

    /**
     * get all events of a user by his id.
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

    /**
     * Allows a user to 'leave' an event which will hide it from his calendar and mark him as rejected in the event guest list.
     * Leaving the event does not remove you from the guest list as intended.
     * @param eventId - The id of the event the user wishes to leave.
     * @param userId- The id of the user who wishes to leave the event.
     * @return The 'Role' representing the user id and event id of the user who left.
     */
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

        if (roleToHide.getRoleType() == RoleType.ORGANIZER) {
            throw new IllegalArgumentException("Organizer cant use this function, use delete event instead!");
        }

        roleToHide.setShownInMyCalendar(false);
        roleToHide.setStatusType(StatusType.REJECTED);

        eventRepository.save(event);

        return roleToHide;
    }

    /**
     * get all the events with a starting time within the next 24 hours.
     * @return a list of all the events with a starting time within the next 24 hours.
     */
    public List<Event> getEventsTillNextDay() {

        List<Event> eventsNext24Hours = eventRepository.findAll();

        ZonedDateTime now = ZonedDateTime.now();

        return eventsNext24Hours.stream()
                .filter(event -> event.getTime().isAfter(now) && event.getTime().isBefore(now.plus(24, ChronoUnit.HOURS)))
                .collect(Collectors.toList());
    }

    /**
     * get all events of a user by his id but only the ones he wishes to show in his calendar (Did not leave the event).
     *
     * @param userId - the id of the user we want to get all of his events.
     * @return a list of all the events a user wishes to show.
     */
    public List<Event> getEventsByUserIdShowOnly(int userId) {

        if (userRepository.findById(userId) == null) {
            return null;
        }

        List<Event> events = getEventsByUserId(userId);

        List<Event> eventsToShow = events.stream()
                .filter(event -> event.getRoles().stream().anyMatch(role -> role.getUser().getId() == userId && role.isShownInMyCalendar()))
                .collect(Collectors.toList());

        return eventsToShow;
    }

    /**
     * Returns a list of all the events I want to display in my calendar which consists of:
     * * All of my events that I want to share (meaning events i did not 'leave')
     * * All the *PUBLIC* events of a user of my choosing who has shared his calendar with me.
     * Returns a 'Set' meaning no duplicate events if someone happened to share an event i am already in.
     *
     * @param user          - my user information.
     * @param sharedEmails- An array of all the emails of the users who shared his calendar with me which i want to see.
     * @return The list of all relevant events to show in my calendar.
     */
    public List<Event> GetAllShared(User user, String[] sharedEmails) {

        if (user == null) {
            throw new IllegalArgumentException("User does not exist!");
        }


        List<User> validUsers = new ArrayList<>();

        for (String email : sharedEmails) {
            User tempUser = userRepository.findByEmail(email).get();
            if (user.getUsersWhoSharedTheirCalendarWithMe().contains(tempUser) || tempUser.equals(user)) {
                validUsers.add(tempUser);
            }
        }

        List<Event> finalList = new ArrayList<>();

        for (User tempUser : validUsers) {
            if (tempUser.getId() == user.getId()) {
                finalList.addAll(getEventsByUserIdShowOnly(user.getId()));
            } else {
                finalList.addAll(getEventsByUserId(tempUser.getId()).stream().filter(event -> event.isPublic())
                        .collect(Collectors.toList()));
            }
        }

        return finalList.stream().distinct().collect(Collectors.toList());
    }
}
