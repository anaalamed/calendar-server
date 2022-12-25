package calendar.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;
import calendar.controller.response.GitToken;
import calendar.controller.response.GitUser;
import calendar.entities.enums.City;
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

    public static String getTimeZoneId(City city) {
        switch (city) {
            case PARIS:
                return "Europe/Paris";
            case LONDON:
                return "Europe/London";
            case NEW_YORK:
                return "America/New_York";
            default:
                return "Asia/Jerusalem";
        }
    }

    //    ------------------------ hash user's password --------------------

    public static String hashPassword(String password) {
        if (password == null || password == "") {
            return password;
        }
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verifyPassword(String passwordFromUser, String passwordFromDB) {
        BCrypt.Result result = BCrypt.verifyer().verify(passwordFromUser.toCharArray(),
                passwordFromDB.toCharArray());

        return result.verified;
    }


    //    ------------------------ git login ---------------------------------
    public static ResponseEntity<GitToken> reqGitGetToken(String link) {
        logger.info("in reqGitGetToken()");
        logger.debug(link);

//        ResponseEntity<GitToken> response = null;
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

    public static ResponseEntity<GitUser> reqGitGetUser(String link, String bearerToken) {
        logger.info("in reqGitGetUser()");
        logger.info(link);
        logger.info(bearerToken);

//        ResponseEntity<GitUser> response = null;
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
