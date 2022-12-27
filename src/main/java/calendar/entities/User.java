package calendar.entities;

import calendar.entities.enums.City;
import calendar.entities.enums.ProviderType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private NotificationSettings notificationSettings;

    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private ProviderType provider;
    @Enumerated(EnumType.STRING)
    private City city;

    @ManyToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private List<User> usersWhoSharedTheirCalendarWithMe;

    public User() {
        usersWhoSharedTheirCalendarWithMe = new ArrayList<>();
    }

    public User(String name, String email, String password, ProviderType provider) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.city = City.JERUSALEM;
        usersWhoSharedTheirCalendarWithMe = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    } // This is here for testing only!

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ProviderType getProvider() {
        return provider;
    }

    public void setProvider(ProviderType provider) {
        this.provider = provider;
    }

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<User> getUsersWhoSharedTheirCalendarWithMe() {
        return this.usersWhoSharedTheirCalendarWithMe;
    }

    public void addSharedCalendar(User user) {
        this.usersWhoSharedTheirCalendarWithMe.add(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(notificationSettings, user.notificationSettings)
                && Objects.equals(name, user.name) && Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) && provider == user.provider && city == user.city;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, notificationSettings, name, email, password, provider, city);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", notificationSettings=" + notificationSettings +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", provider=" + provider +
                ", city=" + city +
                '}';
    }
}

