package calendar.controller;

import calendar.controller.response.BaseResponse;
import calendar.entities.Event;
import calendar.entities.Role;
import calendar.entities.User;
import calendar.entities.enums.RoleType;
import calendar.entities.enums.StatusType;
import calendar.service.RoleService;
import calendar.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RoleControllerTest {

    @Autowired
    RoleController roleController;
    @MockBean
    UserService userService;
    @MockBean
    RoleService roleService;

    static Role role;
    static Event event;
    static User user;
    static List<Role> roles;
    static List<User> users;

    @BeforeAll
    static void setup() {
        event = new Event();
        event.setId(1);

        user = new User();
        user.setId(1);
        users = new ArrayList<>();
        users.add(user);

        role = new Role();
        role.setRoleType(RoleType.GUEST);
        role.setEvent(event);
        role.setUser(user);
        role.setStatusType(StatusType.APPROVED);

        roles = new ArrayList<>();
        roles.add(role);
    }

    @Test
    void Save_Role_Successfully(){
        when(roleService.saveRoleInDB(role)).thenReturn(role);

        ResponseEntity<BaseResponse<Role>> response = roleController.saveRoleInDB(role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(role, response.getBody().getData());
    }

    @Test
    void Try_To_Save_Role_That_already_Exists(){
        when(roleService.saveRoleInDB(role)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> roleController.saveRoleInDB(role));
    }

    @Test
    void Get_All_Roles_With_Same_User_Id_Successfully(){
        when(roleService.getRoleByUserId(1)).thenReturn(roles);
        when(userService.getById(1)).thenReturn(user);
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<BaseResponse<List<Role>>> response = roleController.getRoleByUserId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(role, response.getBody().getData().get(0));
    }

    @Test
    void Try_To_Get_Roles_Of_User_That_Does_Not_Exist(){
        when(userService.getById(1)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> roleController.getRoleByUserId(1));
    }

    @Test
    void Get_Specific_Role_Successfully(){
        when(roleService.getSpecificRole(1,1)).thenReturn(role);

        ResponseEntity<BaseResponse<Role>> response = roleController.getSpecificRole(1,1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(role, response.getBody().getData());
    }

    @Test
    void Try_To_Get_A_Specific_Role_With_Invalid_User_Id(){
        when(roleService.getSpecificRole(999,1)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> roleController.getSpecificRole(999,1));
    }

    @Test
    void Try_To_Get_A_Specific_Role_With_Invalid_Event_Id(){
        when(roleService.getSpecificRole(1,999)).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = roleController.getSpecificRole(1,999);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Delete_Role_Successfully(){
        when(roleService.getSpecificRole(1,1)).thenReturn(role);

        ResponseEntity<BaseResponse<Role>> response = roleController.deleteRole(role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The role was deleted successfully!", response.getBody().getMessage());
    }

    @Test
    void Try_To_Delete_Role_That_Does_Not_Exist(){
        when(roleService.getSpecificRole(999,999)).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = roleController.deleteRole(role);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Switch_Role_Successfully(){
        when(roleService.getSpecificRole(1,1)).thenReturn(role);

        ResponseEntity<BaseResponse<Role>> response = roleController.switchRole(role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The role type was updated successfully!", response.getBody().getMessage());
    }

    @Test
    void Try_To_Switch_Role_Of_User_That_Does_Not_Exist(){
        when(roleService.getSpecificRole(999,999)).thenReturn(null);

        ResponseEntity<BaseResponse<Role>> response = roleController.switchRole(role);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}