package calendar.repository;

import calendar.entities.CalendarShare;
import calendar.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<CalendarShare, CalendarShare> {


}
