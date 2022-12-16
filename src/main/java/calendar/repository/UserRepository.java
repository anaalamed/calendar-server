package calendar.repository;

import calendar.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
        Optional<User> findByEmail(String email);

        @Transactional
        @Modifying
        @Query("delete from User u where u.id = ?1")
        int deleteById(int id);

        @Transactional
        @Modifying
        @Query("update User u set u.name = ?2 where u.id = ?1")
        int updateUserNameById(int id, String name);

        @Transactional
        @Modifying
        @Query("update User u set u.email = ?2 where u.id = ?1")
        int updateUserEmailById(int id, String email);

        @Transactional
        @Modifying
        @Query("update User u set u.password = ?2 where u.id = ?1")
        int updateUserPasswordById(int id, String password);

}
