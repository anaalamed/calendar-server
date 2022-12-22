package calendar.entities.DTO;

import calendar.entities.NotificationSettings;
import calendar.entities.User;
import calendar.entities.enums.ProviderType;

public class UserDTO {
    private int id;
    private String name;
    private String email;

    private ProviderType provider;

    private NotificationSettingsDTO notificationSettings;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.provider = user.getProvider();
        this.notificationSettings = new NotificationSettingsDTO(user.getNotificationSettings()) ;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public ProviderType getProvider() {
        return provider;
    }

    public NotificationSettingsDTO getNotificationSettings() {
        return notificationSettings;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", provider=" + provider +
                ", notificationSettings=" + notificationSettings +
                '}';
    }
}
