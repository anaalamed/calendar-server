# Calendar API
This is the server-side Spring Boot REST API for our calendar application.

## Capabilities
* Users can register to the app using their email or login using their GitHub account.

* In the calendar, a user can create an event which holds various information.
  * The event can be either public or private.
  * The event has the following data: time, date, duration, location, and title.
  * The event also holds a list of guests who can have different roles (Organizer, Admin, Guest) and different statuses (Approved, Rejected, Tentative).
  * Any user who is not an organizer of the event can 'leave' the event, which will immediately hide the event from their calendar.

* The roles are divided by their permissions.
  * Organizer - The one who created the event.
    * Can update all of the information in the event.
    * Can delete the event.
    * Can invite and remove guests.
    * Can change the role of other users from guests to admins and the other way around.
  * Admin - Promoted by the organizer of an event.
    * Can update event data except date, time, duration, title.
    * Can invite or remove guests.
  * Guest - can only respond to invites or leave events.
 
 
 * Calendar sharing
   * Users can share their calendars with other users.
   * Users can see multiple calendars on top of each other.
 
 * Notifications
   * Users can choose their notification settings.
   * The system offers email notifications and in-app notifications via pop-ups.
   * Notification types:
     * New event invitation.
     * User status changed.
     * Event data changed.
     * Event canceled.
     * User was uninvited from event.
     * Notification for upcoming events (10 min before/1 day before/etc).

* Time Zones  
  * Users can see the calendar according to their time zone.
  * Users can change their time zone via the settings page.


## Prerequisites
* Java 9 or higher
* Maven
* A MySQL database
