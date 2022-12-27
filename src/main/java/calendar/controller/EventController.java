package calendar.controller;

import calendar.controller.request.EventRequest;
import calendar.controller.response.BaseResponse;
import calendar.entities.*;
import calendar.entities.DTO.EventDTO;
import calendar.entities.DTO.RoleDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.enums.*;
import calendar.eventNotifications.NotificationPublisher;
import calendar.service.*;
import calendar.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationPublisher notificationPublisher;

    private static final Logger logger = LogManager.getLogger(EventController.class.getName());

    /**
     * Create a new event and save it in the database
     *
     * @param eventRequest - The information of the event we want to save in the database.
     * @return BaseResponse with the created event on success or error message on fail.
     */
    @PostMapping(value = "/saveEvent")
    public ResponseEntity<BaseResponse<EventDTO>> saveEvent(@RequestAttribute("userId") int userId, @RequestBody EventRequest eventRequest) {

        logger.info("In Save event inside EventController");

        User userOfEvent = userService.getById(userId);

        if (userOfEvent == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        Event createdEvent = eventService.saveEvent(eventRequest, userOfEvent);

        if (createdEvent != null) {
            return ResponseEntity.ok(BaseResponse.success(new EventDTO(createdEvent)));
        } else {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Failed to create event!"));
        }
    }


    /**
     * Delete an event by id from the database. Only an organizer can delete an event.
     *
     * @param eventId - The id of the event we wish to delete.
     * @param userId  - The id of the user who wishes to delete the event.
     * @return BaseResponse with a message (deleted successfully or error)
     */
    @RequestMapping(value = "/deleteEvent", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResponse<String>> deleteEvent(@RequestAttribute("userId") int userId, @RequestParam int eventId) {

        logger.info("In delete event inside EventController");

        User userOfEvent = userService.getById(userId);

        if (userOfEvent == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        Event event = eventService.getEventById(eventId);

        if (event == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Event %s not exists!", eventId)));
        }

        eventService.deleteEvent(event);

        return ResponseEntity.ok(BaseResponse.success("Event Deleted Successfully"));
    }


    /**
     * get an event from the database by an event id if it exists.
     *
     * @param id - the id of the event we wish to retrieve.
     * @return The event in DTO format we wanted to get or an error message if not found.
     */
    @RequestMapping(value = "/getEventById", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<EventDTO>> getEventById(@RequestParam int id) {

        logger.info("in get event by id inside EventController");

        Event event = eventService.getEventById(id);

        if (event != null) {
            return ResponseEntity.ok(BaseResponse.success(new EventDTO(event)));
        } else {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("The event %s does not exist!", id)));
        }
    }


    /**
     * Update an event in the database if it exists. Only an admin or an organizer can access this method.
     *
     * @param event    - The event with the updated information.
     * @param roleType - The event with the updated information.
     * @param eventId  - The event with the updated information.
     * @return BaseResponse with a data of the Updated Event or error message on fail.
     */
    @RequestMapping(value = "/updateEvent/event", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<EventDTO>> updateEvent(@RequestAttribute("roleType") RoleType roleType,
                                                              @RequestParam int eventId, @RequestBody EventRequest event) {

        logger.info("in update event inside EventController");

        Event res = null;

        try {
            if (roleType.equals(RoleType.ORGANIZER)) {
                res = eventService.updateEvent(event, eventId);
            }

            if (roleType.equals(RoleType.ADMIN)) {
                res = eventService.updateEventRestricted(event, eventId);
            }

            if (res != null) {
                notificationPublisher.publishEventChangeNotification(res);
                return ResponseEntity.ok(BaseResponse.success(new EventDTO(res)));
            }

            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update the Event %s !", eventId)));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }


    /**
     * Returns one specific role of a user in an event, User can be part of many events, so he can have many
     * roles, but he can only have one role per event and that's the one we will return here.
     *
     * @param userId  - The id of the user which we want to retrieve.
     * @param eventId - The id of the event which we want to retrieve.
     * @return The Role we wanted to get from the DB with the exact user ID and event id combination.
     */
    @RequestMapping(value = "/getSpecificRole", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<RoleDTO>> getSpecificRole(@RequestParam int userId, @RequestParam int eventId) {

        logger.info("in get specific role inside EventController");


        Role role = eventService.getSpecificRole(userId, eventId);

        if (role == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The role does not exist!"));
        }

        return ResponseEntity.ok(BaseResponse.success(new RoleDTO(role)));
    }


    /**
     * Switches the role of a guest to an admin or an admin to a guest.
     *
     * @param eventId - The event id of the event we wish to switch someones role at.
     * @param userId  - The user id of the user we wish to switch his role.
     * @return -The role after the changes
     */
    @RequestMapping(value = "/switchRole", method = RequestMethod.PATCH)
    public ResponseEntity<BaseResponse<RoleDTO>> switchRole(@RequestParam("eventId") int eventId, @RequestBody int userId) {

        logger.info("in switch role inside EventController");

        try {

            RoleDTO roleDTO = new RoleDTO(eventService.switchRole(userId, eventId));
            notificationPublisher.publishUserRoleChangedNotification(eventId, userId);

            return ResponseEntity.ok(BaseResponse.success(roleDTO));
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    /**
     * Switches the status of a guest,  can be APPROVED or REJECTED.
     *
     * @param eventId         - The event id of the event we wish to switch someones role at.
     * @param userId          - The user id of the user we wish to switch his status.
     * @param approveOrReject - A boolean value true if approved false if rejected.
     * @return -the role after the changes.
     */
    @RequestMapping(value = "/switchStatus", method = RequestMethod.PATCH)
    public ResponseEntity<BaseResponse<RoleDTO>> switchStatus(@RequestParam("booleanValue") boolean approveOrReject,
                                                              @RequestParam("eventId") int eventId,
                                                              @RequestAttribute("userId") int userId) {

        logger.info("in switch status inside EventController");

        try {

            Role role = eventService.switchStatus(userId, eventId, approveOrReject);
            notificationPublisher.publishUserStatusChangedNotification(eventId, userId);

            return ResponseEntity.ok(BaseResponse.success(new RoleDTO(role)));
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }


    /**
     * Invites a user to be a guest in an event, only admins and organizers can invite people.
     * A role will be created with a GUEST type and TENTATIVE status.
     *
     * @param email   - The email of the user we wish to invite (must be registered).
     * @param eventId -The id of the event we wish to add the guest to.
     * @return the invited user role.
     */
    @RequestMapping(value = "/inviteGuest", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<RoleDTO>> inviteGuest(@RequestParam String email, @RequestParam int eventId) {

        logger.info("in invite guest inside EventController");

        User user = userService.getByEmailNotOptional(email);

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user is not registered in our app!"));
        }

        try {

            Role roleToAdd = eventService.inviteGuest(user, eventId);
            notificationPublisher.publishInviteGuestNotification(eventId, user.getEmail());

            if (roleToAdd != null) {
                return ResponseEntity.ok(BaseResponse.success(new RoleDTO(roleToAdd)));
            }

            return ResponseEntity.badRequest().body(BaseResponse.failure("The role does not exist!")); // Here for Controller tests only!
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    /**
     * Removes a user from an event, only admins and organizers can remove people.
     * The role that represent the combination of the user id and event id will be removed from the DB
     *
     * @param email   - The email of the user we wish to delete (must be registered).
     * @param eventId -The id of the event we wish to remove the guest from.
     * @return a message confirming the removal of the guest.
     */
    @RequestMapping(value = "/removeGuest", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResponse<RoleDTO>> removeGuest(@RequestParam String email, @RequestParam int eventId) {

        logger.info("in remove guest inside EventController");

        User user = userService.getByEmailNotOptional(email);

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user is not registered in our app!"));
        }

        try {

            Role roleToRemove = eventService.removeGuest(user.getId(), eventId);
            notificationPublisher.publishRemoveUserFromEventNotification(eventId, user.getEmail());

            return ResponseEntity.ok(BaseResponse.success(new RoleDTO(roleToRemove)));
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    /**
     * Allows a user to 'leave' an event which will hide it from his calendar and mark him as rejected in the event guest list.
     * Leaving the event does not remove you from the guest list as intended.
     * @param eventId - The id of the event the user wishes to leave.
     * @param userId- The id of the user who wishes to leave the event.
     * @return The 'Role' representing the user id and event id of the user who left.
     */
    @RequestMapping(value = "/leaveEvent", method = RequestMethod.PATCH)
    public ResponseEntity<BaseResponse<RoleDTO>> leaveEvent(@RequestAttribute("userId") int userId, @RequestParam int eventId) {

        logger.info("in leave event inside EventController");

        User user = userService.getById(userId);

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user is not registered in our app!"));
        }

        try {
            Role roleToHide = eventService.leaveEvent(userId, eventId);

            if (roleToHide != null) {
                return ResponseEntity.ok(BaseResponse.success(new RoleDTO(roleToHide)));
            }

            return ResponseEntity.badRequest().body(BaseResponse.failure("The role does not exist!")); // Here for Controller tests only!
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    /**
     * Returns a list of all my events I want to display in my calendar. (Meaning events i did not leave).
     * @param userId-      My user id which I get by using the token in the filter.
     * @return The list of all relevant events to show in my calendar.
     */
    @GetMapping(value = "/getEventsByUserIdShowOnly")
    public ResponseEntity<BaseResponse<List<EventDTO>>> getEventsByUserIdShowOnly(@RequestAttribute("userId") int userId) {

        logger.info("in get events by user id show only inside EventController");

        User userOfEvent = userService.getById(userId);

        if (userOfEvent == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        List<Event> events = eventService.getEventsByUserIdShowOnly(userId);

        List<EventDTO> eventsDTO = EventDTO.convertEventsToEventsDTO(events);

        eventsDTO = Utils.changeEventTimesByTimeZone(eventsDTO,userOfEvent.getCity());

        return ResponseEntity.ok(BaseResponse.success(eventsDTO));
    }

    /**
     * get all events of a user by his id.
     *
     * @param userId - the id of the user we want to get all of his events.
     * @return a list of all the events.
     */
    @GetMapping(value = "/getEventsByUserId")
    public ResponseEntity<BaseResponse<List<EventDTO>>> getEventsByUserId(@RequestAttribute("userId") int userId) {

        logger.info("in get events by user id inside EventController");

        User userOfEvent = userService.getById(userId);

        if (userOfEvent == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        List<Event> events = eventService.getEventsByUserId(userId);

        List<EventDTO> eventsDTO = EventDTO.convertEventsToEventsDTO(events);

        eventsDTO = Utils.changeEventTimesByTimeZone(eventsDTO, userOfEvent.getCity());

        return ResponseEntity.ok(BaseResponse.success(eventsDTO));
    }

    /**
     * Returns a list of all the events I want to display in my calendar which consists of:
     * * All of my events that I want to share (meaning events i did not 'leave')
     * * All the *PUBLIC* events of a user of my choosing who has shared his calendar with me.
     * Returns a 'Set' meaning no duplicate events if someone happened to share an event i am already in.
     *
     * @param sharedEmails - An array of all the emails of the users who shared his calendar with me which i want to see.
     * @param userId-      My user id which I get by using the token in the filter.
     * @return The list of all relevant events to show in my calendar.
     */

    @GetMapping(value = "/GetAllShared")
    public ResponseEntity<BaseResponse<List<EventDTO>>> GetAllShared(@RequestAttribute("userId") int userId,
                                                                     @RequestBody String[] sharedEmails) {
        User user = userService.getById(userId);

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        if (sharedEmails.length == 0) {
            ResponseEntity.ok(BaseResponse.success(Collections.emptyList()));
        }

        try {

           List<EventDTO> events =  EventDTO.convertEventsToEventsDTO(eventService.GetAllShared(user, sharedEmails));

           events = Utils.changeEventTimesByTimeZone(events,user.getCity());

            return ResponseEntity.ok(BaseResponse.success(events));
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }
}
