package calendar.service;


import calendar.controller.request.UserRequest;
import calendar.controller.response.GitToken;
import calendar.controller.response.GitUser;
import calendar.entities.DTO.LoginDataDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.NotificationSettings;
import calendar.entities.User;
import calendar.entities.enums.ProviderType;
import calendar.repository.UserRepository;
import calendar.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.*;


@Service
public class AuthService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private Environment env;

    static HashMap<Integer, String> usersTokensMap = new HashMap<>();

    public AuthService(UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());

    /**
     * Create a user if he is not already part of our system.
     * @param userRequest - All the information of the user we wish to register.
     * @return the created User
     * @throws SQLDataException - If the user is already registered in our DB.
     */
    public User createUser(UserRequest userRequest, ProviderType provider) throws IllegalArgumentException {
        logger.info("in createUser()");

        if(userRepository.findByEmail(userRequest.getEmail()).isPresent()){
            throw new IllegalArgumentException(String.format("Email %s already exists!", userRequest.getEmail()));
        }

        logger.debug(userRequest);

        User createdUser = new User(userRequest.getName(), userRequest.getEmail(), Utils.hashPassword(userRequest.getPassword()), provider);

        NotificationSettings notificationSettings = new NotificationSettings(createdUser);
        createdUser.setNotificationSettings(notificationSettings);

        User savedUser = userRepository.save(createdUser);
        return savedUser;
    }
    /**
     * User logs in into our system with his email and password.
     * @param userRequest -  All the information of the user we wish to log in.
     * @return LoginData: user id and token (if log in is successful)
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
     * @param token - The token we wish to compare with our local token storage.
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

    public Optional<LoginDataDTO> loginGithub(String code) throws IllegalArgumentException {
        logger.info("in loginGithub()");

        GitUser githubUser = getGithubUser(code);
        logger.info("user: " + githubUser);

        if (githubUser != null) {
            if ( !userRepository.findByEmail(githubUser.getEmail()).isPresent() ) {
                if (githubUser.getName() != "" && githubUser.getName() != null) {
                    User userCreated = createUser(new UserRequest(githubUser.getEmail(), githubUser.getName(), ""), ProviderType.GITHUB);
                    logger.info(userCreated);
                } else {
                    User userCreated = createUser(new UserRequest(githubUser.getEmail(), githubUser.getLogin(), ""), ProviderType.GITHUB);
                    logger.info(userCreated);
                }
            }

            // if user with email created and has password ??
            Optional<LoginDataDTO> login = login(new UserRequest(githubUser.getEmail(), ""));
            logger.info(login);
            return login;
        }
        return null;
    }

    public ResponseEntity<GitToken> getGithubToken(String code) {
        String baseLink = "https://github.com/login/oauth/access_token?";
        String clientId = env.getProperty("spring.security.oauth2.client.registration.github.client-id");
        String clientSecret = env.getProperty("spring.security.oauth2.client.registration.github.client-secret");
        String linkGetToken = baseLink + "client_id=" + clientId + "&client_secret=" + clientSecret + "&code=" + code;
        logger.debug(linkGetToken);

        return Utils.reqGitGetToken(linkGetToken);
    }

    public GitUser getGithubUser( String code ) {
        ResponseEntity<GitToken> gitTokenResponse = getGithubToken(code);

        if (gitTokenResponse != null) {
            String token = gitTokenResponse.getBody().getAccess_token();
            logger.info("token: " + token);

            String linkGetUser = "https://api.github.com/user";
            return Utils.reqGitGetUser(linkGetUser, token).getBody();
        }

        return null;
    }

    public static HashMap<Integer, String> getUsersTokensMap() {
        return usersTokensMap;
    }
}
