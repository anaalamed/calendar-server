package calendar.entities;

import javax.persistence.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

@Entity
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private boolean isPublic;
    private LocalDateTime time;
    private LocalDate date;
    private float duration;//in hours
    private String location;
    private String title;
    private String description;
    private ArrayList<File> attachments;

    public Event() {

    }
    private  Event(boolean isPublic,LocalDateTime time,LocalDate date, float duration,
                   String location,String title,String description,ArrayList<File> attachments) {
        this.isPublic = isPublic;
        this.time = time;
        this.date = date;
        this.duration = duration;
        this.location = location;
        this.title = title;
        this.description = description;
        this.attachments = attachments;
    }

    public static Event getNewEvent(boolean isPublic,LocalDateTime time,LocalDate date, float duration,
                                 String location,String title,String description,ArrayList<File> attachments) {
        return new Event(isPublic,time,date,duration,location,title,description,attachments);
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    } // Here for testing only!


    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<File> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<File> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return getId() == event.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", isPublic=" + isPublic +
                ", time=" + time +
                ", date=" + date +
                ", duration=" + duration +
                ", location='" + location + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", attachments=" + attachments +
                '}';
    }

}