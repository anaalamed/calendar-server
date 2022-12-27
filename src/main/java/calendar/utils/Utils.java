package calendar.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;
import calendar.controller.response.GitToken;
import calendar.controller.response.GitUser;
import calendar.entities.DTO.EventDTO;
import calendar.entities.enums.City;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;


import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public class Utils {

    private static final Logger logger = LogManager.getLogger(Utils.class.getName());


    /**
     * This method generated a unique token for a user who logged-in to our app.
     * @return the generated token
     */
    public static String generateUniqueToken() {

        StringBuilder token = new StringBuilder();

        long currentTimeInMillisecond = Instant.now().toEpochMilli();

        return token.append(currentTimeInMillisecond).append("-")
                .append(UUID.randomUUID()).toString();
    }

    /**
     * Gets a time zone id of a city
     * @param city - the city of which we want to get a time zone id.
     * @return the time zone id of the city.
     */
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

    /**
     * This method converts the time of all the events to the correct time using the time zone of the
     * user according to the city he chose.
     * @param events - The list of the events we wish to update.
     * @param city - The city of the user.
     * @return The updated list of the events with the correct times.
     */
    public static List<EventDTO> changeEventTimesByTimeZone(List<EventDTO> events, City city){

        for (EventDTO event : events) {
            switch (city) {
                case PARIS:
                    event.setTime(event.getTime().withZoneSameInstant(ZoneId.of("Europe/Paris")));
                    break;
                case LONDON:
                    event.setTime(event.getTime().withZoneSameInstant(ZoneId.of("Europe/London")));
                    break;
                case NEW_YORK:
                    event.setTime(event.getTime().withZoneSameInstant(ZoneId.of("America/New_York")));
                    break;
                default:
                    event.setTime(event.getTime().withZoneSameInstant(ZoneId.of("Asia/Jerusalem")));
            }
        }

        return events;
    }

    //    ------------------------ hash user's password --------------------

    /**
     * Creates a hash for a password to make it more secure.
     * @param password - The password we wish to secure.
     * @return the hashed password.
     */
    public static String hashPassword(String password) {

        if (password == null || password == "") {
            return password;
        }
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    /**
     * Checks if the password of a user matches the password we hold for the same user in our database.
     * @param passwordFromUser - THe password the user inserted.
     * @param passwordFromDB - The password we hold in our Database.
     * @return true or false, matching or not.
     */
    public static boolean verifyPassword(String passwordFromUser, String passwordFromDB) {

        BCrypt.Result result = BCrypt.verifyer().verify(passwordFromUser.toCharArray(),
                passwordFromDB.toCharArray());

        return result.verified;
    }


    //    ------------------------ git login ---------------------------------
    public static ResponseEntity<GitToken> reqGitGetToken(String link) {
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

    public static ResponseEntity<GitUser> reqGitGetUser(String link, String bearerToken) {
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
