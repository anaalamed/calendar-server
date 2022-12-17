package calendar.controller;

import calendar.controller.request.EventRequest;
import calendar.controller.response.BaseResponse;
import calendar.entities.Event;
import calendar.service.EventService;
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
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
        return null;
    }


    /**
     * Update an event in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "updateEvent", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Event>> updateEvent(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEvent(event);
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
        return ResponseEntity.ok(BaseResponse.success(res));
    }



    /**
     * get an event in the DB by id if founded
     *
     * @param id
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "getEventById", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Event>> getEventById(@RequestBody int id) {
        //check token of the user from header
        System.out.println("**********************"+id+"************************");
        Event res;
        try {
            res = eventService.getEventById(id);
            return ResponseEntity.ok(BaseResponse.success(res));
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
    @RequestMapping(value = "updateEvent/title", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Event>> updateEventTitle(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventTitle(event);
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
        return ResponseEntity.ok(BaseResponse.success(res));
    }

    /**
     * Update an event Description in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "updateEvent/description", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Event>> updateEventDescription(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDescription(event);
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
        return ResponseEntity.ok(BaseResponse.success(res));
    }


    /**
     * Update an event Date in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "updateEvent/date", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Event>> updateEventDate(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDate(event);
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
        return ResponseEntity.ok(BaseResponse.success(res));
    }


    /**
     * Update an event Duration in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "updateEvent/duration", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Event>> updateEventDuration(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventDuration(event);
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
        return ResponseEntity.ok(BaseResponse.success(res));
    }

    /**
     * Update an event Time in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "updateEvent/time", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Event>> updateEventTime(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventTime(event);
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
        return ResponseEntity.ok(BaseResponse.success(res));
    }



    /**
     * Update an event Location in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "updateEvent/location", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Event>> updateEventLocation(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventLocation(event);
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
        return ResponseEntity.ok(BaseResponse.success(res));
    }


    /**
     * Update an event Accessibility in the DB if founded
     *
     * @param event
     * @return BaseResponse with a data of the Updated Event
     */
    @RequestMapping(value = "updateEvent/isPublic", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Event>> updateEventIsPublic(@RequestBody Event event) {
        //check token of the user from header
        Event res;
        try {
            res = eventService.updateEventIsPublic(event);
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
        return ResponseEntity.ok(BaseResponse.success(res));
    }


}
