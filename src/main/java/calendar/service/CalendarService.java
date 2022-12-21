package calendar.service;

import calendar.controller.response.BaseResponse;
import calendar.entities.CalendarShare;
import calendar.entities.User;
import calendar.repository.CalendarRepository;
import calendar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class CalendarService {
    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private UserRepository userRepository;


    /**
     * Share user events with other user
     * Save it in calendarShare DB
     *
     * @param viewerId - who im sharing with
     * @return BaseResponse with a list of shared events
     */
    public CalendarShare shareCalendar(int userId, int viewerId) {

        try {
            User viewer = userRepository.findById(viewerId);
            if (viewer != null){
                CalendarShare calendarShare = new CalendarShare(userId, viewerId);
                CalendarShare res = calendarRepository.save(calendarShare);
                return res;
            }
            return null;

        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
