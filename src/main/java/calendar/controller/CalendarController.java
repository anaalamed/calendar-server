package calendar.controller;

import calendar.controller.response.BaseResponse;
import calendar.entities.DTO.EventDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.Event;
import calendar.entities.User;
import calendar.service.CalendarService;
import calendar.service.EventService;
import calendar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin
@RequestMapping("/calendar")
public class CalendarController {
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;

    /**
     * Share user events with other user. This will add the viewer to an array of the user which holds all the users
     * who shared their calendars with him.
     *
     * @param viewerId - who im sharing with.
     * @return BaseResponse The user i added to my shared calendars list.
     */
    @PostMapping(value = "/share")
    public ResponseEntity<BaseResponse<UserDTO>> shareCalendar(@RequestAttribute("userId") int userId, @RequestParam int viewerId) {

        try {
            User sharedUser = calendarService.shareCalendar(userId, viewerId);

            if (sharedUser != null)
                return ResponseEntity.ok(BaseResponse.success(new UserDTO(sharedUser)));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed To Share Calendar")));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed To Share Calendar")));
        }
    }

    /**
     * Share user events with other user. This will add the viewer to an array of the user which holds all the users
     * who shared their calendars with him.
     *
     * @param viewerId - who im sharing with.
     * @return BaseResponse The user i added to my shared calendars list.
     */
    @GetMapping(value = "/GetAllShared")
    public ResponseEntity<BaseResponse<Map<String, List<EventDTO>>>> GetAllShared(@RequestAttribute("userId") int userId) {

        User user = userService.getById(userId);

        if(user == null){
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        Map<String, List<EventDTO>> map;

        List<Event> myEventsToShow = eventService.getEventsByUserIdShowOnly(userId);

        map = calendarService.GetAllShared(userId,myEventsToShow);

        return ResponseEntity.ok(BaseResponse.success(map));
    }
}
