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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;
import java.time.ZoneId;
import java.util.ArrayList;
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

    /**
     * Create new event and save it in the DB
     *
     * @param eventRequest - The information of the event we want to save in the DB
     * @return BaseResponse with the created event on success or error message on fail.
     */
    @PostMapping(value = "/saveEvent")
    public ResponseEntity<BaseResponse<EventDTO>> saveEvent(@RequestAttribute("userId") int userId, @RequestBody EventRequest eventRequest) {
        //check token of the user from header
        try {
            User userOfEvent = userService.getById(userId);

            if (userOfEvent == null) {
                return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
            }

            Event createdEvent = eventService.saveEvent(eventRequest, userOfEvent);

            return ResponseEntity.ok(BaseResponse.success(new EventDTO(createdEvent)));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Failed To Create Event"));
        }
    }

    /**
     * Delete an event by id from the DB
     *
     * @param eventId
     * @return BaseResponse with a message (deleted successfully or error)
     */
    @RequestMapping(value = "/deleteEvent", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResponse<String>> deleteEvent(@RequestAttribute("userId") int userId, @RequestParam int eventId) {

        User userOfEvent = userService.getById(userId);

        if (userOfEvent == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        Event event;
        try {
            event = eventService.getEventById(eventId);
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Event %s not exists!", eventId)));
        }

        eventService.deleteEvent(event);
        return ResponseEntity.ok(BaseResponse.success("Event Deleted Successfully"));
    }


    /**
     * get an event in the DB by id if founded
     *
     * @param id
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "/getEventById", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<EventDTO>> getEventById(@RequestParam int id) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.getEventById(id);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(new EventDTO(res)));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("The event %s does not exist!", id)));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }


    /**
     * Update an event in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "/updateEvent/event", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<EventDTO>> updateEvent(@RequestAttribute("roleType") RoleType roleType, @RequestParam int eventId, @RequestBody EventRequest event) {
        Event res = null;
        try {
            if (roleType.equals(RoleType.ORGANIZER))
                res = eventService.updateEvent(event, eventId);

            if (roleType.equals(RoleType.ADMIN))
                res = eventService.updateEventRestricted(event, eventId);

            if (res != null) {
                notificationPublisher.publishEventChangeNotification(res);
                return ResponseEntity.ok(BaseResponse.success(new EventDTO(res)));
            }

            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update the Event %s !", eventId)));

        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }


    /**
     * Update an event title in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "/updateEvent/title", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<EventDTO>> updateEventTitle(@RequestParam int eventId, @RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventTitle(event, eventId);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(new EventDTO(res)));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update Title of Event %s !", eventId)));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }

    /**
     * Update an event Description in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "/updateEvent/description", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<EventDTO>> updateEventDescription(@RequestParam int eventId, @RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDescription(event, eventId);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(new EventDTO(res)));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update Description of Event %s !", eventId)));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }

    }

    /**
     * Update an event Duration in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "/updateEvent/duration", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<EventDTO>> updateEventDuration(@RequestParam int eventId, @RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDuration(event, eventId);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(new EventDTO(res)));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update Duration of Event %s !", eventId)));

        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }

    /**
     * Update an event Time in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "/updateEvent/time", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<EventDTO>> updateEventTime(@RequestParam int eventId, @RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventTime(event, eventId);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(new EventDTO(res)));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update Time of Event %s !", eventId)));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }

    }


    /**
     * Update an event Location in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "/updateEvent/location", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<EventDTO>> updateEventLocation(@RequestParam int eventId, @RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventLocation(event, eventId);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(new EventDTO(res)));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update Location of Event %s !", eventId)));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }


    /**
     * Update an event Accessibility in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "/updateEvent/isPublic", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<EventDTO>> updateEventIsPublic(@RequestParam int eventId, @RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventIsPublic(event, eventId);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(new EventDTO(res)));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update accessibility of Event %s !", eventId)));

        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }

    /**
     * get all events of a user by his id
     *
     * @param userId - the id of the user we want to get all of his events.
     * @return a list of all the events.
     */
    @GetMapping(value = "/getEventsByUserId")
    public ResponseEntity<BaseResponse<List<EventDTO>>> getEventsByUserId(@RequestAttribute("userId") int userId) {

        User userOfEvent = userService.getById(userId);

        if (userOfEvent == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        List<Event> events = eventService.getEventsByUserId(userId);

        List<EventDTO> eventsDTO = EventDTO.convertEventsToEventsDTO(events);

        return ResponseEntity.ok(BaseResponse.success(eventsDTO));
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

        try {
            RoleDTO roleDTO = new RoleDTO(eventService.switchRole(userId, eventId));
            notificationPublisher.publishUserRoleChangedNotification(eventId, userId);
            return ResponseEntity.ok(BaseResponse.success(roleDTO));
        } catch (Exception e) {
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

        try {
            Role role = eventService.switchStatus(userId, eventId, approveOrReject);
            notificationPublisher.publishUserStatusChangedNotification(eventId, userId);
            return ResponseEntity.ok(BaseResponse.success(new RoleDTO(role)));
        } catch (Exception e) {
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
    public ResponseEntity<BaseResponse<Role>> removeGuest(@RequestParam String email, @RequestParam int eventId) {

        User user = userService.getByEmailNotOptional(email);

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user is not registered in our app!"));
        }

        try {
            Role roleToRemove = eventService.removeGuest(user.getId(), eventId);
            notificationPublisher.publishRemoveUserFromEventNotification(eventId, user.getEmail());
            return ResponseEntity.ok(BaseResponse.noContent(true, "The guest was removed successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    @RequestMapping(value = "/leaveEvent", method = RequestMethod.PATCH)
    public ResponseEntity<BaseResponse<RoleDTO>> leaveEvent(@RequestAttribute("userId") int userId, @RequestParam int eventId) {
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

    @GetMapping(value = "/getEventsByUserIdShowOnly")
    public ResponseEntity<BaseResponse<List<EventDTO>>> getEventsByUserIdShowOnly(@RequestAttribute("userId") int userId) {

        User userOfEvent = userService.getById(userId);

        if (userOfEvent == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        List<Event> events = eventService.getEventsByUserIdShowOnly(userId);

        List<EventDTO> eventsDTO = EventDTO.convertEventsToEventsDTO(events);

        for (EventDTO event : eventsDTO) {
            switch (userOfEvent.getCity()) {
                case PARIS:
                    event.setTime(event.getTime().withZoneSameInstant(ZoneId.of("Europe/Paris")));
                    break;
                case LONDON:
                    event.setTime(event.getTime().withZoneSameInstant(ZoneId.of("Europe/London")));
                    break;
                case NEW_YORK:
                    event.setTime(event.getTime().withZoneSameInstant(ZoneId.of("America/New_York")));
                    break;
                default:
                    event.setTime(event.getTime().withZoneSameInstant(ZoneId.of("Asia/Jerusalem")));
            }
        }
        return ResponseEntity.ok(BaseResponse.success(eventsDTO));
    }

    /**
     * Returns a list of all the events I want to display in my calendar which consists of:
     * * All of my events that I want to share (meaning events i did not 'leave')
     * * All the *PUBLIC* events of a user of my choosing who has shared his calendar with me.
     * Returns a 'Set' meaning no duplicate events if someone happened to share an event i am already in.
     *
     * @param sharedEmail - the email of the user who shared his calendar with me.
     * @param userId-     My user id which I get by using the token in the filter.
     * @return The list of all relevant events to show in my calendar.
     */

    @GetMapping(value = "/GetAllShared")
    public ResponseEntity<BaseResponse<List<EventDTO>>> GetAllShared(@RequestAttribute("userId") int userId,
                                                                     @RequestParam String sharedEmail) {

        User user = userService.getById(userId);

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        User sharedUser = userService.getByEmail(sharedEmail).get();

        if (sharedUser == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user i want to share with does not exist!"));
        }

        if(!user.getUsersWhoSharedTheirCalendarWithMe().contains(sharedUser)){
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user did not share his calendar with me!"));
        }

        try {
            return ResponseEntity.ok(BaseResponse.success(EventDTO.convertEventsToEventsDTO(eventService.GetAllShared(user, sharedUser))));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }


}
