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

    public static class Builder {
        private int id;

        private final LocalDateTime time;
        private final LocalDate date;
        private final String title;

        private boolean isPublic = true;
        private float duration = 1;
        private String location = "location";
        private String description = "description";
        private ArrayList<File> attachments = null;


        public Builder(LocalDateTime time, LocalDate date, String title) {
            this.time = time;
            this.date = date;
            this.title = title;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder duration(float duration) {
            this.duration = duration;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder attachments(ArrayList<File> attachments) {
            this.attachments = attachments;
            return this;
        }

        public Event build() {
            return new Event(this);
        }
    }

    public Event() {

    }

    Event(Builder builder) {
        this.id = builder.id;
        this.date = builder.date;
        this.time = builder.time;
        this.title = builder.title;
        this.isPublic = builder.isPublic;
        this.attachments = builder.attachments;
        this.description = builder.description;
        this.duration = builder.duration;
        this.location = builder.location;
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
