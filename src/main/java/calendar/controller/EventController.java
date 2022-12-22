package calendar.controller;

import calendar.controller.request.EventRequest;
import calendar.controller.response.BaseResponse;
import calendar.entities.Event;
import calendar.entities.User;
import calendar.entities.enums.RoleType;
import calendar.entities.enums.StatusType;
import calendar.eventNotifications.NotificationPublisher;
import calendar.service.EventService;
import calendar.service.RoleService;
import calendar.service.UserService;
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
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @Autowired
    private NotificationPublisher notificationPublisher;

    /**
     * Create new event and save it in the DB
     *
     * @param eventRequest
     * @return BaseResponse with the created event
     */
    @PostMapping(value = "/saveEvent")
    public ResponseEntity<BaseResponse<Event>> saveEvent(@RequestAttribute("userId") int userId,@RequestBody EventRequest eventRequest) {
        //check token of the user from header
        try {
            System.out.println("im here");
            Event createdEvent = eventService.saveEvent(eventRequest);
            System.out.println(createdEvent);
            User userOfEvent = userService.getById(userId);
            System.out.println(userOfEvent);
            roleService.saveRoleInDB(userOfEvent,createdEvent, StatusType.APPROVED, RoleType.ORGANIZER);

            return ResponseEntity.ok(BaseResponse.success(createdEvent));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Failed To Create Event"));
        }
    }

    /**
     * Delete an event by id from the DB
     *
     * @param event
     * @return BaseResponse with a message (deleted successfully or error)
     */
    @RequestMapping(value = "/deleteEvent", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResponse<String>> deleteEvent(@RequestAttribute("userId") int userId,@RequestParam int eventId) {
        System.out.println("*************"+userId);
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
    public ResponseEntity<BaseResponse<Event>> updateEvent(@RequestParam int eventId ,@RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEvent(event,eventId);
            if (res != null){
                notificationPublisher.publishEventChangeNotification(res);
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
    public ResponseEntity<BaseResponse<Event>> updateEventTitle(@RequestParam int eventId ,@RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventTitle(event,eventId);
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
    public ResponseEntity<BaseResponse<Event>> updateEventDescription(@RequestParam int eventId ,@RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDescription(event,eventId);
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
    public ResponseEntity<BaseResponse<Event>> updateEventDate(@RequestParam int eventId ,@RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDate(event,eventId);
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
    public ResponseEntity<BaseResponse<Event>> updateEventDuration(@RequestParam int eventId ,@RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDuration(event,eventId);
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
    public ResponseEntity<BaseResponse<Event>> updateEventTime(@RequestParam int eventId ,@RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventTime(event,eventId);
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
    public ResponseEntity<BaseResponse<Event>> updateEventLocation(@RequestParam int eventId ,@RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventLocation(event,eventId);
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
    public ResponseEntity<BaseResponse<Event>> updateEventIsPublic(@RequestParam int eventId ,@RequestBody EventRequest event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventIsPublic(event,eventId);
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

        return ResponseEntity.ok(BaseResponse.success(roleService.getEventsByUserId(userId)));
    }


}
