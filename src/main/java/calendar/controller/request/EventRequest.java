package calendar.controller.request;


import java.io.File;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class EventRequest {
    private boolean isPublic;
    private Clock time;
    private LocalDate date;
    private float duration;//in hours
    private String location;
    private String title;
    private String description;
    private List<File> attachments;

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Clock getTime() {
        return time;
    }

    public void setTime(Clock time) {
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

    public List<File> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<File> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "EventRequest{" +
                "isPublic=" + isPublic +
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