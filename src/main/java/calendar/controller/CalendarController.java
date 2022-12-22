package calendar.controller;

import calendar.controller.response.BaseResponse;
import calendar.entities.CalendarShare;
import calendar.entities.User;
import calendar.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/calendar")
public class CalendarController {
    @Autowired
    private CalendarService calendarService;


    /**
     * Share user events with other user
     *
     * @param viewerId - who im sharing with
     * @return BaseResponse with a list of shared events
     */
    @PostMapping(value = "/share")
    public ResponseEntity<BaseResponse<CalendarShare>> shareCalendar(@RequestAttribute("userId") int userId, @RequestBody User viewerId) {
        try {
            CalendarShare calendarShare = calendarService.shareCalendar(userId, viewerId.getId());
            if (calendarShare != null)
                return ResponseEntity.ok(BaseResponse.success(calendarShare));
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed To Share Calendar with %s", viewerId.getId())));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(String.format("Failed To Share Calendar with %s", viewerId.getId())));
        }
    }


}
