package calendar.service;


import calendar.controller.request.UserRequest;
import calendar.entities.DTO.LoginDataDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.User;
import calendar.repository.UserRepository;
import calendar.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.*;



@Service
public class AuthService {
    @Autowired
    private final UserRepository userRepository;

    static HashMap<Integer, String> usersTokensMap = new HashMap<>();

    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());

    public AuthService(UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    /**
     * Create user if email isn't already exist
     * @param userRequest
     * @return the created User
     * @throws SQLDataException
     */
    public UserDTO createUser(UserRequest userRequest) throws SQLDataException {
        logger.info("in createUser()");

        if(userRepository.findByEmail(userRequest.getEmail()).isPresent()){
            throw new SQLDataException(String.format("Email %s already exists!", userRequest.getEmail()));
        }

        logger.debug(userRequest);
        User user = userRepository.save(new User(userRequest.getName(), userRequest.getEmail(),
                Utils.hashPassword(userRequest.getPassword())));

        return new UserDTO(user);
    }

    /**
     * Log In user to the system
     * @param userRequest
     * @return LoginData (user id, token) if successes
     */
    public Optional<LoginDataDTO> login(UserRequest userRequest) {
        logger.info("in login()");

        Optional<User> user = userRepository.findByEmail(userRequest.getEmail());

        if (user.isPresent() && Utils.verifyPassword(userRequest.getPassword(), user.get().getPassword())) {
            Optional<String> token = Optional.of(Utils.generateUniqueToken());
            usersTokensMap.put(user.get().getId(), token.get());
            return Optional.of(new LoginDataDTO(user.get().getId(), token.get()));
        }

        return Optional.empty();
    }


    /**
     * Check if user is authenticated
     * @param userId
     * @param token
     * @return true if user is authenticated, otherwise - false
     */
//    public boolean isAuthenticated(int userId, String token) {
//        return usersTokensMap.containsKey(userId) && usersTokensMap.get(userId).equals(token);
//    }


}
