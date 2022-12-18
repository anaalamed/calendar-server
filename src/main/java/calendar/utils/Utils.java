package calendar.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;
import calendar.controller.response.GitToken;
import calendar.controller.response.GitUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;


import java.time.Instant;
import java.util.UUID;

public class Utils {

    private static final Logger logger = LogManager.getLogger(Utils.class.getName());

    public static String generateUniqueToken() {
        StringBuilder token = new StringBuilder();
        long currentTimeInMillisecond = Instant.now().toEpochMilli();

        return token.append(currentTimeInMillisecond).append("-")
                .append(UUID.randomUUID()).toString();
    }

    public static String hashPassword(String password) {
        if (password == null || password == "") {
            return password;
        }
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verifyPassword(String passwordFromUser, String passwordFromDB) {
        if (passwordFromUser == null || passwordFromUser == "" || passwordFromDB == null || passwordFromDB == "") {
            return true; // github
        }
        BCrypt.Result result = BCrypt.verifyer().verify(passwordFromUser.toCharArray(),
                passwordFromDB.toCharArray());

        return result.verified;
    }

    public static ResponseEntity<GitToken> sendRequest(String link) {
        logger.info("in sendRequest()");
        logger.debug(link);

        ResponseEntity<GitToken> response = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        try {
            response = restTemplate.exchange(link, HttpMethod.POST, entity, GitToken.class);
            logger.info(response);
            return response;
//            logger.info(response.getBody().getAccess_token());
        } catch (Exception e) {
            logger.error("git get token  exception" + e);
        }
        return null;
    }

    public static ResponseEntity<GitUser> getUser(String link, String bearerToken) {
        logger.info("in getUser()");
        logger.info(link);
        logger.info(bearerToken);

        ResponseEntity<GitUser> response = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        try {
            response = restTemplate.exchange(link, HttpMethod.GET, entity, GitUser.class);
            logger.info(response);
            return response;
//            logger.info(response.getBody().getAccess_token());
        } catch (Exception e) {
            logger.error("git get token  exception" + e);
        }
        return null;
    }


}
