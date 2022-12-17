package calendar.controller;

import calendar.controller.response.BaseResponse;
import calendar.entities.*;
import calendar.entities.DTO.UserDTO;
import calendar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleService roleService;
    @Autowired
    UserService userService;
    @Autowired
    EventService eventService;

    /**
     * Saves one Role in the DB
     *
     * @param role - The role we wish to save in the DB
     * @return
     */
    @RequestMapping(value = "/saveRole", method = RequestMethod.POST) // HERE FOR TESTING!! WILL REMOVE LATER
    public ResponseEntity<BaseResponse<Role>> saveRoleInDB(@RequestBody Role role) {

        if (roleService.getSpecificRole(role.getUser().getId(), role.getEvent().getId()) != null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("A user cant have more than one role in the same event!"));
        }

        return ResponseEntity.ok(BaseResponse.success(roleService.saveRoleInDB(role)));
    }

    /**
     * Returns a list of all the roles who are a part of the same event.
     *
     * @param eventId - The id of the event which we want to retrieve all roles with the same event id.
     * @return All the Roles we wanted to get from the DB with the same event id.
     */
    @RequestMapping(value = "/getRoleByEventId", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<List<Role>>> getRoleByEventId(@RequestParam int eventId) {

        Event event = null;

        try {
            event = eventService.getEventById(eventId);
        } catch (SQLDataException e) {
            throw new RuntimeException(e);
        }

        if (!eventService.getAllEvents().contains(event)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The event does not exist!"));
        }

        return ResponseEntity.ok(BaseResponse.success(roleService.getRoleByEventId(eventId)));
    }

    /**
     * Returns a list of all the roles a user is part of.
     *
     * @param userId - The id of the event which we want to retrieve all roles with the same user id.
     * @return All the Roles we wanted to get from the DB with the same user ID
     */
    @RequestMapping(value = "/getRoleByUserId", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<List<Role>>> getRoleByUserId(@RequestParam int userId) {

        User user = userService.getById(userId);

        if (!userService.getAllUsers().contains(user)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user does not exist!"));
        }

        return ResponseEntity.ok(BaseResponse.success(roleService.getRoleByUserId(userId)));
    }

    /**
     * Returns one specific role of a user in an event, User can be part of many events, so he can have many
     * roles, but he can only have one role per event and that's the one we will return here.
     *
     * @param userId  - The id of the user which we want to retrieve.
     * @param eventId - The id of the event which we want to retrieve.
     * @return The Role we wanted to get from the DB with the exact user ID and event id combination.
     */
    @RequestMapping(value = "/getSpecificRole", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<Role>> getSpecificRole(@RequestParam int userId, @RequestParam int eventId) {

        Role role = roleService.getSpecificRole(userId, eventId);

        if (role == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The role does not exist!"));
        }

        return ResponseEntity.ok(BaseResponse.success(role));
    }

    /**
     * Deletes the Role from database by id
     *
     * @param role - The role we want to delete
     * @return
     */
    @RequestMapping(value = "/deleteRole", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResponse<Role>> deleteRole(@RequestBody Role role) {

        Role roleToDelete = roleService.getSpecificRole(role.getUser().getId(), role.getEvent().getId());

        if (roleToDelete == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The role does not exist!"));
        }

        roleService.deleteRole(roleToDelete);
        return ResponseEntity.ok(BaseResponse.noContent(true, "The role was deleted successfully!"));
    }

    /**
     * Promotes a guest to an admin, only an organizer can promote someone.
     *
     * @param role - The role which holds the user id of the user we want to change his role type.
     * @return -a message confirming the removal of the role.
     */
    @RequestMapping(value = "/switchRole", method = RequestMethod.PATCH)
    public ResponseEntity<BaseResponse<Role>> switchRole(@RequestBody Role role) {

        Role roleToPromote = roleService.getSpecificRole(role.getUser().getId(), role.getEvent().getId());

        if (roleToPromote == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The role does not exist!"));
        }

        roleService.switchRole(roleToPromote);
        return ResponseEntity.ok(BaseResponse.noContent(true, "The role type was updated successfully!"));
    }

    /**
     * Invites a user to be a guest in an event, only admins and organizers can invite people.
     * A role will be created with a GUEST type and TENTATIVE status.
     *
     * @param email   - The email of the user we wish to invite (must be registered).
     * @param eventId -The id of the event we wish to add the guest to.
     * @return the invited user role.
     */
    @RequestMapping(value = "/inviteGuest", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Role>> inviteGuest(@RequestParam String email, @RequestParam int eventId) throws SQLDataException {

        Optional<UserDTO> user = userService.getByEmail(email);
        Event event = eventService.getEventById(eventId);

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user is not registered in our app!"));
        }

        if (event == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The event does not exist!"));
        }

        Role roleToAdd = roleService.getSpecificRole(user.get().getId(), eventId);

        if (roleToAdd != null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user is already part of the event!"));
        }

        return ResponseEntity.ok(BaseResponse.success(roleService.inviteGuest(userService.getById(user.get().getId()),
                eventService.getEventById(eventId))));
    }

    /**
     * Removes a user from an event, only admins and organizers can remove people.
     * The role that represent the combination of the user id and event id will be removed from the DB
     *
     * @param email   - The email of the user we wish to delete (must be registered).
     * @param eventId -The id of the event we wish to remove the guest from.
     * @return a message confirming the removal of the guest.
     */
    @RequestMapping(value = "/removeGuest", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResponse<Role>> removeGuest(@RequestParam String email, @RequestParam int eventId) throws SQLDataException {

        Optional<UserDTO> user = userService.getByEmail(email);
        Event event = eventService.getEventById(eventId);

        if (user == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user is not registered in our app!"));
        }

        if (event == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The event does not exist!"));
        }

        Role roleToRemove = roleService.getSpecificRole(user.get().getId(), eventId);

        if (roleToRemove == null) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The user is not part of the event!"));
        }

        roleService.deleteRole(roleToRemove);
        return ResponseEntity.ok(BaseResponse.noContent(true, "The guest was removed successfully!"));
    }
}
