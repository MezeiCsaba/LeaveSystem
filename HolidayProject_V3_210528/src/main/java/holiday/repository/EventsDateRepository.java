package holiday.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import holiday.entity.EventDates;

public interface EventsDateRepository extends CrudRepository<EventDates, Long> {
	
		
		Page<EventDates> findAllByOrderByDate(Pageable pageable);
		List<EventDates> findAllByOrderByDate();
//		Page<EventDates> findAllWithDateAfterByOrderByDate(Pageable pageable, LocalDate actYear);
//		List<EventDates> findAllWithDateAfterByOrderByDate(LocalDate actYear);
		
		EventDates findAllById(Long id);
		
		

	}



