package calendar.service;


import calendar.controller.request.UserRequest;
import calendar.controller.response.GitToken;
import calendar.controller.response.GitUser;
import calendar.entities.DTO.LoginDataDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.User;
import calendar.entities.enums.ProviderType;
import calendar.repository.UserRepository;
import calendar.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public UserDTO createUser(UserRequest userRequest, ProviderType provider) throws SQLDataException {
        logger.info("in createUser()");

        if(userRepository.findByEmail(userRequest.getEmail()).isPresent()){
            throw new SQLDataException(String.format("Email %s already exists!", userRequest.getEmail()));
        }

        logger.debug(userRequest);
        User user = userRepository.save(new User(userRequest.getName(), userRequest.getEmail(),
                Utils.hashPassword(userRequest.getPassword()), provider));

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
     *
     * @param token
     * @return userId if is present in usersTokensMap
     */
    public Optional<Integer> getUserIdByToken(String token) {
        return usersTokensMap
                .entrySet()
                .stream()
                .filter(entry -> token.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public Optional<LoginDataDTO> loginGithub(String code) throws SQLDataException {
        logger.info("in loginGithub()");

        String baseLink = "https://github.com/login/oauth/access_token?";
        String clientId = "ed4a65a952cea0d51abe";
        String clientSecret = "163683c64698c1ae1fb7576bde75bb0bd8a97e45";
        String foolink = baseLink + "client_id=" + clientId + "&client_secret=" + clientSecret + "&code=" + code;
        logger.debug(foolink);

        String linkGetUser = "https://api.github.com/user";

        ResponseEntity<GitToken> gitTokenResponseEntity = Utils.sendRequest(foolink);
        if (gitTokenResponseEntity != null) {
            String token= gitTokenResponseEntity.getBody().getAccess_token();
            logger.info("token: " + token);

            GitUser user = Utils.getUser(linkGetUser, token).getBody();
            logger.info("user: " + user);
            if (user != null) {
                if ( !userRepository.findByEmail(user.getEmail()).isPresent() ) {
                    if (user.getName() != "" && user.getName() != null) {
                        UserDTO userCreated = createUser(new UserRequest(user.getEmail(), user.getName(), ""), ProviderType.GITHUB);
                        logger.info(userCreated);
                    } else {
                        UserDTO userCreated = createUser(new UserRequest(user.getEmail(), user.getLogin(), ""), ProviderType.GITHUB);
                        logger.info(userCreated);
                    }
                }

                Optional<LoginDataDTO> login = login(new UserRequest(user.getEmail(), ""));
                logger.info(login);
                return login;
            }
        }
        return null;
    }

}
