package calendar.entities;

import javax.persistence.*;

@Entity
@Table(name = "calendarShare")
public class CalendarShare {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int userId;

    private int viewerId;


    public CalendarShare() {

    }

    public CalendarShare(int userId, int viewerId) {
        this.userId = userId;
        this.viewerId = viewerId;
    }

    public int getId() {
        return id;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getViewerId() {
        return viewerId;
    }

    public void setViewerId(int viewerId) {
        this.viewerId = viewerId;
    }
}
