package calendar.controller;

import calendar.controller.request.EventRequest;
import calendar.controller.response.BaseResponse;
import calendar.entities.Event;
import calendar.event.emailNotification.NotificationPublisher;
import calendar.service.EventService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;

@RestController
@CrossOrigin
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;

    @Autowired
    private NotificationPublisher notificationPublisher;

    /**
     * Create new event and save it in the DB
     *
     * @param eventRequest
     * @return BaseResponse with the created event
     */
    @RequestMapping(value = "saveEvent", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Event>> saveEvent(@RequestBody EventRequest eventRequest) {
        //check token of the user from header
        try {
            Event createEvent = eventService.saveEvent(eventRequest);
            return ResponseEntity.ok(BaseResponse.success(createEvent));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Event already exists"));
        }
    }

    /**
     * Delete an event by id from the DB
     *
     * @param event
     * @return BaseResponse with a message (deleted successfully or error)
     */
    @RequestMapping(value = "deleteEvent", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResponse<String>> deleteEvent(@RequestBody Event event) {
        //check token of the user from header
        try {
            if (eventService.deleteEvent(event) > 0)/* if number of deleted rows in DB > 0 */
                return ResponseEntity.ok(BaseResponse.success("Event Deleted Successfully"));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Event %s not exists!", event.getId())));
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
    @RequestMapping(value = "/updateEvent", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<Event>> updateEvent(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEvent(event);
            if (res != null){
                notificationPublisher.publishEventChangeNotification(res);
                return ResponseEntity.ok(BaseResponse.success(res));
            }

            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update the Event %s !", event.getId())));

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
    @RequestMapping(value = "updateEvent/title", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<Event>> updateEventTitle(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventTitle(event);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update Title of Event %s !", event.getId())));
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
    @RequestMapping(value = "updateEvent/description", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<Event>> updateEventDescription(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDescription(event);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update Description of Event %s !", event.getId())));
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
    @RequestMapping(value = "updateEvent/date", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<Event>> updateEventDate(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDate(event);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update Date of Event %s !", event.getId())));

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
    @RequestMapping(value = "updateEvent/duration", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<Event>> updateEventDuration(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDuration(event);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update Duration of Event %s !", event.getId())));

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
    @RequestMapping(value = "updateEvent/time", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<Event>> updateEventTime(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventTime(event);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update Time of Event %s !", event.getId())));
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
    @RequestMapping(value = "updateEvent/location", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<Event>> updateEventLocation(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventLocation(event);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update Location of Event %s !", event.getId())));
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
    @RequestMapping(value = "updateEvent/isPublic", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse<Event>> updateEventIsPublic(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventIsPublic(event);
            if (res != null)
                return ResponseEntity.ok(BaseResponse.success(res));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed to update accessibility of Event %s !", event.getId())));

        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }


}
