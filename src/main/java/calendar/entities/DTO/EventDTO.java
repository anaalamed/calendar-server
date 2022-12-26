package calendar.entities.DTO;

import calendar.entities.Event;
import calendar.entities.Role;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDTO {


    private int id;
    private boolean isPublic;
//    private LocalDateTime time;
    private ZonedDateTime time;
    private float duration;//in hours
    private String location;
    private String title;
    private String description;
    private ArrayList<File> attachments;
    private List<RoleDTO> roles;

    public EventDTO() {
    }

    public EventDTO(Event event) {
        ZonedDateTime zonedDateTime = event.getTime();
        this.id = event.getId();
        this.isPublic = event.isPublic();
//        this.time = event.getTime();
        this.time = zonedDateTime;
        this.duration = event.getDuration();
        this.location = event.getLocation();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.attachments = event.getAttachments();
        this.roles = convertRolesToRolesDTO(event.getRoles());
    }

    private List<RoleDTO> convertRolesToRolesDTO(List<Role> roles) {

        List<RoleDTO> rolesDTO = new ArrayList<>();

        for (Role role:roles) {
            RoleDTO roleDTO = new RoleDTO(role);
            rolesDTO.add(roleDTO);
        }
        return rolesDTO;
    }

    public static List<EventDTO> convertEventsToEventsDTO(List<Event> events) {

        List<EventDTO> eventsDTO = new ArrayList<>();

        for (Event event:events) {
            EventDTO EventDTO = new EventDTO(event);
            eventsDTO.add(EventDTO);
        }
        return eventsDTO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

//    public LocalDateTime getTime() {
//        return time;
//    }
//
//    public void setTime(LocalDateTime time) {
//        this.time = time;
//    }


    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
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

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "EventDTO{" +
                "id=" + id +
                ", isPublic=" + isPublic +
                ", time=" + time +
//                ", date=" + date +
                ", duration=" + duration +
                ", location='" + location + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", attachments=" + attachments +
                ", roles=" + roles +
                '}';
    }
}
