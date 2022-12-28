package calendar.service;

import calendar.controller.request.UserRequest;
import calendar.controller.response.GitToken;
import calendar.controller.response.GitUser;
import calendar.entities.DTO.LoginDataDTO;
import calendar.entities.User;
import calendar.entities.enums.ProviderType;
import calendar.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;

import java.util.HashMap;
import java.util.Optional;

@Service
public class GithubAuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private Environment env;

    static HashMap<Integer, String> usersTokensMap = new HashMap<>();

    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());



    /**
     * User logs in into our system with his GitHub.
     * @param code -  A code given by the GitHub API to proceed with the login process.
     * @return LoginData: user id and token.
     */
    public Optional<LoginDataDTO> loginGithub(String code) throws IllegalArgumentException {
        logger.info("in loginGithub()");

        GitUser githubUser = getGithubUser(code);
        logger.info("user: " + githubUser);

        if (githubUser != null && githubUser.getEmail() != null && !githubUser.getEmail().equals("")) {
            Optional<User> userFromDB = userRepository.findByEmail(githubUser.getEmail());

            if (!userFromDB.isPresent()) {
                User userCreated = null;
                String name;

                if (!githubUser.getName().equals("") && githubUser.getName() != null) {
                    name = githubUser.getName();
                    userCreated = authService.createUser(new UserRequest(githubUser.getEmail(), githubUser.getName(), ""), ProviderType.GITHUB);
                    logger.info(userCreated);
                } else {
                    name = githubUser.getLogin();
                    userCreated = authService.createUser(new UserRequest(githubUser.getEmail(), githubUser.getLogin(), ""), ProviderType.GITHUB);
                    logger.info(userCreated);
                }

                User savedUser = userRepository.save(userCreated);
            } else if (userFromDB.get().getProvider() == ProviderType.LOCAL) {
                return null;
            }

            return authService.login(new UserRequest(githubUser.getEmail(), ""));
        }
        return Optional.empty();
    }

    /**
     * Get the details of the user from GitHub API (the second request)
     * @param code
     * @return the relevant details of User
     */
    private GitUser getGithubUser(String code) {
        ResponseEntity<GitToken> gitTokenResponse = getGithubToken(code);

        if (gitTokenResponse != null) {
            String token = gitTokenResponse.getBody().getAccess_token();
            logger.info("token: " + token);

            String linkGetUser = "https://api.github.com/user";
            ResponseEntity<GitUser> gitUserResponseEntity = reqGitGetUser(linkGetUser, token);

            if (gitUserResponseEntity != null) {
                return gitUserResponseEntity.getBody();
            }
        }
        return null;
    }

    /**
     * Get bearer token for user from GitHub API (the first request)
     * @param code
     * @return
     */
    private ResponseEntity<GitToken> getGithubToken(String code) {

        String baseLink = "https://github.com/login/oauth/access_token?";
        String clientId = env.getProperty("spring.security.oauth2.client.registration.github.client-id");
        String clientSecret = env.getProperty("spring.security.oauth2.client.registration.github.client-secret");
        String linkGetToken = baseLink + "client_id=" + clientId + "&client_secret=" + clientSecret + "&code=" + code;

        logger.debug(linkGetToken);

        return reqGitGetToken(linkGetToken);
    }

    /**
     * Generate request for get token request (the first request)
     * @param link
     * @return token
     */
    private ResponseEntity<GitToken> reqGitGetToken(String link) {
        logger.info("in reqGitGetToken()");
        logger.debug(link);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        try {
            return restTemplate.exchange(link, HttpMethod.POST, entity, GitToken.class);
        } catch (Exception e) {
            logger.error("git get token  exception" + e);
            return null;
        }
    }

    /**
     * Generate request for get User request (the second request)
     * @param link
     * @param bearerToken
     * @return
     */
    private ResponseEntity<GitUser> reqGitGetUser(String link, String bearerToken) {
        logger.info("in reqGitGetUser()");
        logger.info(link);
        logger.info(bearerToken);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        try {
            return restTemplate.exchange(link, HttpMethod.GET, entity, GitUser.class);
        } catch (Exception e) {
            logger.error("git get token  exception" + e);
            return null;
        }
    }

}