package calendar.controller;

import calendar.controller.request.EventRequest;
import calendar.controller.response.BaseResponse;
import calendar.entities.*;
import calendar.entities.DTO.RoleDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.enums.*;
import calendar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
//    @Autowired
//    private NotificationPublisher notificationPublisher;

    /**
     * Create new event and save it in the DB
     *
     * @param eventRequest - The information of the event we want to save in the DB
     * @return BaseResponse with the created event on success or error message on fail.
     */
    @PostMapping(value = "/saveEvent")
    public ResponseEntity<BaseResponse<Event>> saveEvent(@RequestAttribute("userId") int userId, @RequestBody EventRequest eventRequest) {
        //check token of the user from header
        try {
            User userOfEvent = userService.getById(userId);

            Event createdEvent = eventService.saveEvent(eventRequest, userOfEvent);

            return ResponseEntity.ok(BaseResponse.success(createdEvent));
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

        try {
            if (eventService.deleteEvent(eventId) > 0)/* if number of deleted rows in DB > 0 */
                return ResponseEntity.ok(BaseResponse.success("Event Deleted Successfully"));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Event %s not exists!", eventId)));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }


    /**
     * get an event in the DB by id if founded
     *
     * @param id
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "/getEventById", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<Event>> getEventById(@RequestParam int id) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.getEventById(id);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
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
    public ResponseEntity<BaseResponse<Event>> updateEvent(@RequestAttribute("roleType") RoleType roleType, @RequestParam int eventId, @RequestBody EventRequest event) {
        Event res = null;
        try {
            if (roleType.equals(RoleType.ORGANIZER))
                res = eventService.updateEvent(event, eventId);

            if (roleType.equals(RoleType.ADMIN))
                res = eventService.updateEventRestricted(event, eventId);

            if (res != null) {
                // notificationPublisher.publishEventChangeNotification(res); ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                return ResponseEntity.ok(BaseResponse.success(res));
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
    public ResponseEntity<BaseResponse<Event>> updateEventTitle(@RequestParam int eventId, @RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventTitle(event, eventId);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
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
    public ResponseEntity<BaseResponse<Event>> updateEventDescription(@RequestParam int eventId, @RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDescription(event, eventId);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update Description of Event %s !", eventId)));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }

    }


    /**
     * Update an event Date in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "/updateEvent/date", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<Event>> updateEventDate(@RequestParam int eventId, @RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDate(event, eventId);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update Date of Event %s !", eventId)));

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
    public ResponseEntity<BaseResponse<Event>> updateEventDuration(@RequestParam int eventId, @RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDuration(event, eventId);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
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
    public ResponseEntity<BaseResponse<Event>> updateEventTime(@RequestParam int eventId, @RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventTime(event, eventId);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
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
    public ResponseEntity<BaseResponse<Event>> updateEventLocation(@RequestParam int eventId, @RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventLocation(event, eventId);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
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
    public ResponseEntity<BaseResponse<Event>> updateEventIsPublic(@RequestParam int eventId, @RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventIsPublic(event, eventId);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
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
    public ResponseEntity<BaseResponse<List<Event>>> getEventsByUserId(@RequestAttribute("userId") int userId) {

        return ResponseEntity.ok(BaseResponse.success(eventService.getEventsByUserId(userId)));
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
     * Promotes a guest to an admin, only an organizer can promote someone.
     *
     * @param eventId - The event id of the event we wish to switch someones role at.
     * @param userId  - The user id of the user we wish to switch his role.
     * @return -a message confirming the removal of the role.
     */
    @RequestMapping(value = "/switchRole", method = RequestMethod.PATCH)
    public ResponseEntity<BaseResponse<Role>> switchRole(@RequestParam("eventId") int eventId, @RequestBody int userId) {

        try {

            return ResponseEntity.ok(BaseResponse.success(eventService.switchRole(userId, eventId)));

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

        User user = userService.getByEmail(email).get();

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user is not registered in our app!"));
        }

        Role RoleToAdd = eventService.inviteGuest(user, eventId);

        if (RoleToAdd == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user is already part of the event!"));
        }

        //notificationPublisher.publishInviteGuestNotification(event, user.get().getEmail());
        return ResponseEntity.ok(BaseResponse.success(new RoleDTO(RoleToAdd)));
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
    public ResponseEntity<BaseResponse<Role>> removeGuest(@RequestParam String email, @RequestParam int eventId){

        User user = userService.getByEmail(email).get();

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user is not registered in our app!"));
        }

        Role roleToRemove = eventService.removeGuest(user.getId(),eventId);

        if(roleToRemove != null){
            //notificationPublisher.publishRemoveUserFromEventNotification(event, user.get().getEmail()); ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            return ResponseEntity.ok(BaseResponse.noContent(true, "The guest was removed successfully!"));
        }else{
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user is not part of the event!"));
        }
    }
}
