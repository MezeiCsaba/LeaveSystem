package holiday.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import holiday.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
	
	List<Event> findAll();
	
	List<Event> findAllByUserIdOrderByStartDate(Long id);
	
	Event findByUserIdAndId(Long userId, Long eventId);

	Page<Event> findAllByUserIdOrderByStartDate(Long authUserId, Pageable pageable);


}
