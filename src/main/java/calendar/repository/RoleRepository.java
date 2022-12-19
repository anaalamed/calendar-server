package calendar.repository;

import calendar.entities.Event;
import calendar.entities.Role;
import calendar.entities.User;
import calendar.entities.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findById(int id);

    List<Role> findByEventId(int eventId);

    List<Role> findByUserId(int userID);

    @Transactional
    @Modifying
    @Query("UPDATE Role r SET r.roleType =:roleType WHERE r.event =:event AND r.user =:user")
    int updateRoleType(@Param("user") User user, @Param("event") Event event, @Param("roleType") RoleType roleType);

    Role save(Role role);

}
