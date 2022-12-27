package calendar.utils;

import calendar.entities.DTO.EventDTO;
import calendar.entities.Event;
import calendar.entities.enums.City;
import jdk.jshell.execution.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    private static City city;

    private EventDTO event;

    private List<EventDTO> events;

    @BeforeEach
    void setup(){
        city = City.LONDON;

        event = new EventDTO();
        event.setId(1);
        event.setTime(ZonedDateTime.now());

        events = new ArrayList<>();
        events.add(event);
    }

    @Test
    void generateUniqueToken() {

        assertNotNull(Utils.generateUniqueToken());

    }

    @Test
    void getTimeZoneId() {
        assertEquals(Utils.getTimeZoneId(city),"Europe/London");
    }

    @Test
    void changeEventTimesByTimeZone() {

        List<EventDTO> newEvents = Utils.changeEventTimesByTimeZone(events, City.PARIS);

        assertTrue(newEvents.get(0).getTime().isBefore(ZonedDateTime.now()));
    }

    @Test
    void hashPassword() {
        String password = "Leon1234";

        assertNotEquals(Utils.hashPassword(password),"Leon1234");
    }

    @Test
    void verifyPassword() {
        String passwordInserted = "Leon1234";
        String passwordInDB = Utils.hashPassword("Leon1234");

        assertTrue(Utils.verifyPassword(passwordInserted,passwordInDB));
    }
}