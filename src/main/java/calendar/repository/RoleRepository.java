package calendar.repository;

import calendar.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findById(int id);

    Role[] findByEventId(int eventId);

    Role[] findByUserId(int userID);

    Role save(Role role);
}
