package calendar.controller;

import calendar.entities.Role;
import calendar.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleService roleService;


    /**
     * Saves one Role in the DB
     *
     * @param role - The role we wish to save in the DB
     */
    @RequestMapping(value = "/saveRole", method = RequestMethod.POST) // HERE FOR TESTING!! WILL REMOVE LATER
    public void saveRoleInDB(@RequestBody Role role) {
        roleService.saveRoleInDB(role);
    }

    /**
     * Returns a list of all the roles who are a part of the same event.
     * @param eventId - The id of the event which we want to retrieve all roles with the same event id.
     * @return All the Roles we wanted to get from the DB with the same event id.
     */
    @RequestMapping(value = "/getRoleByEventId", method = RequestMethod.POST)
    public List<Role> getRoleByEventId(@RequestParam int eventId) {
        return roleService.getRoleByEventId(eventId);
    }

    /**
     * Returns a list of all the roles a user is part of.
     * @param userId - The id of the event which we want to retrieve all roles with the same user id.
     * @return All the Roles we wanted to get from the DB with the same user ID
     */
    @RequestMapping(value = "/getRoleByUserId", method = RequestMethod.POST)
    public List<Role> getRoleByUserId(@RequestParam int userId) {
        return roleService.getRoleByUserId(userId);
    }

    /**
     * Returns one specific role of a user in an event, User can be part of many events, so he can have many
     * roles, but he can only have one role per event and that's the one we will return here.
     * @param userId - The id of the user which we want to retrieve.
     * @param eventId - The id of the event which we want to retrieve.
     * @return The Role we wanted to get from the DB with the exact user ID and event id combination.
     */
    @RequestMapping(value = "/getSpecificRole", method = RequestMethod.POST)
    public Role getSpecificRole(@RequestParam int userId, @RequestParam int eventId) {
        return roleService.getSpecificRole(userId,eventId);
    }

    /**
     * Deletes the Role from database by id
     *
     * @param role - The role we want to delete
     */
    @RequestMapping(value = "/deleteRole", method = RequestMethod.DELETE)
    public void deleteRole(@RequestBody Role role) {
        Role roleToDelete = getSpecificRole(role.getUser().getId(), role.getEvent().getId());
        roleService.deleteRole(roleToDelete);
    }

    /**
     * Promotes a guest to an admin, only an organizer can promote someone.
     *
     * @param role - The role which holds the user id of the user we want to change his role type.
     */
    @RequestMapping(value = "/switchRole", method = RequestMethod.PATCH)
    public void switchRole(@RequestBody Role role) {
        Role roleToPromote = getSpecificRole(role.getUser().getId(), role.getEvent().getId());
        roleService.switchRole(roleToPromote);
    }
}
