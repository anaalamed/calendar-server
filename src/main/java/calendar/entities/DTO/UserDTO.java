package calendar.entities.DTO;

import calendar.entities.NotificationSettings;
import calendar.entities.User;
import calendar.entities.enums.City;
import calendar.entities.enums.ProviderType;

public class UserDTO {
    private int id;
    private String name;
    private String email;
    private ProviderType provider;
    private City city;

    private NotificationSettingsDTO notificationSettings;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.provider = user.getProvider();
        this.notificationSettings = new NotificationSettingsDTO(user.getNotificationSettings());
        this.city = user.getCity();
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

    public City getCity() {
        return city;
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
