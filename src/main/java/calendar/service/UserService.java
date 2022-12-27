package calendar.service;

import calendar.entities.DTO.UserDTO;
import calendar.entities.NotificationSettings;
import calendar.entities.User;
import calendar.entities.enums.City;
import calendar.repository.UserRepository;
import calendar.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    private static final Logger logger = LogManager.getLogger(UserService.class.getName());

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Returns all the users in the user repo , this is used for server side only so no need to use DTO.
     *
     * @return a list of all the users inside the DB.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get a user by his ID from the database.
     *
     * @param id - the id of the user we want to retrieve
     * @return the User if exists
     */
    public User getById(int id) {
        User user = userRepository.findById(id);
        return user;
    }

    /**
     * Get UserDTO by email.
     *
     * @param email - The email of the user we want to retrieve.
     * @return the User if exists.
     */
    public Optional<UserDTO> getDTOByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new UserDTO(user.get()));
    }

    /**
     * Get User by email.
     *
     * @param email - The email of the user we want to retrieve.
     * @return the User if exists.
     */
    public Optional<User> getByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(user.get());
    }

    /**
     * Get User by email.
     *
     * @param email - The email of the user we want to retrieve.
     * @return the User if exists.
     */
    public User getByEmailNotOptional(String email) {
        return userRepository.findByEmail(email).get();
    }


    /**
     * Get User id by Email.
     *
     * @param email - The email of the user we want to retrieve.
     * @return the id of the user.
     */
    public Integer getUserIdByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (!user.isPresent()) {
            return null;
        }

        return user.get().getId();
    }

    /**
     * Updates the notification settings of a user
     *
     * @param userId                      - The id of the user we wish update.
     * @param notificationSettingsRequest - The notification Settings we wish to insert into the user.
     * @return the updated user or bad request if not found.
     */
    public UserDTO updateNotificationsSettings(int userId, NotificationSettings notificationSettingsRequest) {

        User user = userRepository.findById(userId);

        logger.info(user);

        notificationSettingsRequest.setUser(user);
        NotificationSettings notificationSettingsUser = user.getNotificationSettings();
        BeanUtils.copyProperties(notificationSettingsRequest, notificationSettingsUser);

        user.setNotificationSettings(notificationSettingsUser);

        User savedUser = userRepository.save(user);

        return new UserDTO(savedUser);
    }

    /**
     * Updates the city of a user, can be JERUSALEM, PARIS, LONDON or NEW_YORK.
     *
     * @param userId  - The id of the user we wish update.
     * @param newCity - The city we wish to update the user information with.
     * @return the updated user or bad request if not found.
     */
    public User updateCity(int userId, String newCity) {

        User user = userRepository.findById(userId);

        if (user == null) {
            return null;
        }

        switch (newCity) {
            case "PARIS":
                user.setCity(City.PARIS);
                break;
            case "LONDON":
                user.setCity(City.LONDON);
                break;
            case "NEW_YORK":
                user.setCity(City.NEW_YORK);
                break;
            default:
                user.setCity(City.JERUSALEM);
        }

        userRepository.save(user);
        return user;
    }

    /**
     * Gets the notification settings of a user.
     *
     * @param userId - The id of the user we wish update.
     * @return the notification settings of a user.
     */
    public NotificationSettings getNotificationSettings(int userId) {

        User user = userRepository.findById(userId);

        if (user == null) {
            return null;
        }

        NotificationSettings notificationSettings = user.getNotificationSettings();

        if (notificationSettings == null) {
            return null;
        }

        return notificationSettings;
    }

    /**
     * Gets a list of users who shared their calendar with me.
     *
     * @param userId - The id of the user we wish to retrieve the list of shared calendars.
     * @return the list of users who shared their calendar with the user.
     */
    public List<User> getUsersWhoSharedWithMe(int userId) {

        User user = userRepository.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException("The user does not exist!");
        }

        return user.getUsersWhoSharedTheirCalendarWithMe();
    }

    /**
     * Share my calendar with a different user using his id, I will insert myself into his list of
     * users who shared their calendar with him.
     *
     * @param viewer - the user I want to share my calendar with.
     * @param user-  My user information.
     * @return The user i shared my calendar with.
     */
    public User shareCalendar(User user, User viewer) {


        if (user == null) {
            throw new IllegalArgumentException("User does not exist!");
        }


        if (viewer == null) {
            throw new IllegalArgumentException("Viewer does not exist!");
        }

        if (viewer.getUsersWhoSharedTheirCalendarWithMe().contains(user)) {
            throw new IllegalArgumentException("This viewer is already part of my shared calendars!");
        }

        viewer.addSharedCalendar(user);

        userRepository.save(viewer);

        return viewer;
    }
}
