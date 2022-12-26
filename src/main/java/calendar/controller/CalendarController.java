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
     * Share my calendar with a different user using his id, I will insert myself into his list of
     * users who shared their calendar with him.
     *
     * @param viewerId - the id of the user I want to share my calendar with.
     * @param userId-  My user id which I get by using the token in the filter.
     * @return The user i shared my calendar with.
     */
    @PostMapping(value = "/share")
    public ResponseEntity<BaseResponse<UserDTO>> shareCalendar(@RequestAttribute("userId") int userId,
                                                               @RequestParam int viewerId) {
        User user = userService.getById(userId);

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        User viewer = userService.getById(viewerId);

        if (viewer == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The viewer does not exist!"));
        }

        try {
            User sharedUser = calendarService.shareCalendar(user, viewer);

            return ResponseEntity.ok(BaseResponse.success(new UserDTO(sharedUser)));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format(e.getMessage())));
        }
    }
}
