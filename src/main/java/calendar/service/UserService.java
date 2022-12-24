package calendar.service;

import calendar.entities.DTO.UserDTO;
import calendar.entities.NotificationSettings;
import calendar.entities.User;
import calendar.repository.UserRepository;
import calendar.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
     * Get a user by his ID from the DB
     *
     * @param id - the id of the user we want to retrieve
     * @return the User if exists
     */
    public User getById(int id) {
        User user = userRepository.findById(id);
        return user;
    }

    /**
     * Get UserDTO by email
     *
     * @param email - The email of the user we want to retrieve.
     * @return the User if exists
     */
    public Optional<UserDTO> getDTOByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new UserDTO(user.get()));
    }

    /**
     * Get User by email
     *
     * @param email - The email of the user we want to retrieve.
     * @return the User if exists
     */
    public Optional<User> getByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(user.get());
    }

    /**
     * Get User by email
     *
     * @param email - The email of the user we want to retrieve.
     * @return the User if exists
     */
    public User getByEmailNotOptional(String email) {
        return userRepository.findByEmail(email).get();
    }


    /**
     * Get User id by Email
     *
     * @param email
     * @return the id
     */
    public Integer getUserIdByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (!user.isPresent()) {
            return null;
        }

        return user.get().getId();
    }

    public UserDTO updateNotificationsSettings( int userId, NotificationSettings notificationSettingsRequest) {

        User user = userRepository.findById(userId);
        logger.info(user);

        notificationSettingsRequest.setUser(user);
        NotificationSettings notificationSettingsUser = user.getNotificationSettings();
        BeanUtils.copyProperties(notificationSettingsRequest, notificationSettingsUser);
        user.setNotificationSettings(notificationSettingsUser);

        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }
}
