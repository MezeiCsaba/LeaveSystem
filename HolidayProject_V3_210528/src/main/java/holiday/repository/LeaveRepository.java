package holiday.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import holiday.entity.UserLeaves;

public interface LeaveRepository  extends JpaRepository<UserLeaves, Long> {

	Set<UserLeaves> findAllByUserId(Long userId);
	Set<UserLeaves> findByUserIdAndYearBetween(Long userId, int startYear, int endYear);

	UserLeaves findByUserIdAndYear(Long id, Integer actYear);




}
