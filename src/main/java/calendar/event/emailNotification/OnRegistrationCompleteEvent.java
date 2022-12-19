package calendar.event.emailNotification;

import calendar.entities.DTO.UserDTO;
import calendar.entities.User;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

public class OnRegistrationCompleteEvent extends ApplicationEvent {
//    private UserDTO user;
    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OnRegistrationCompleteEvent(String email) {
        super(email);
        this.email = email;

    }


//    public  getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
}