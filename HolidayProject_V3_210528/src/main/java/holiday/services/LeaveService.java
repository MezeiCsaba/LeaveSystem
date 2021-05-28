package holiday.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import holiday.entity.User;
import holiday.entity.UserLeaves;
import holiday.repository.LeaveRepository;

@Service
public class LeaveService {

	private LeaveRepository leaveRepo;

	@Autowired
	public void setLeaveRepo(LeaveRepository leaveRepo) {
		this.leaveRepo = leaveRepo;
	}

	public UserLeaves getUserLeavesByYear(Integer actYear, User user) {
		return leaveRepo.findByUserIdAndYear(user.getId(), actYear);
	}
	
	public Set<UserLeaves> getAllUserLeaves(Long userId){
		return leaveRepo.findAllByUserId(userId);
	}
	public Set<UserLeaves> findByUserIdAndYearBetween(Long userId, int startDate, int endDate){
		return leaveRepo.findByUserIdAndYearBetween(userId, startDate, endDate);
	}
	
	

	public void saveLeave(UserLeaves userLeaves) {
		
		leaveRepo.save(userLeaves);
	}

}
