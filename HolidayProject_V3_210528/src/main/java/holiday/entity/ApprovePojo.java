package holiday.entity;

import java.time.LocalDate;

public class ApprovePojo {
	
	private long eventId;
	private long userId;
	private String userName;
	private String userEmail;
	private LocalDate startDate;
	private Byte approved; // -1: denied, 0: pending approval, 1: approved
	private Byte duration;

	public ApprovePojo() {
		
	}

	public ApprovePojo(long eventId, long userId, String userName, String userEmail, LocalDate startDate,
			 byte approved) {
		
		this.eventId = eventId;
		this.userId = userId;
		this.userName = userName;
		this.userEmail = userEmail;
		this.startDate = startDate;
		this.approved = approved;
	}

	
	
	

	public ApprovePojo(String userName,  LocalDate startDate, Byte approved) {
		this.userName = userName;
		this.startDate = startDate;
		
		this.approved = approved;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	

	public Byte getApproved() {
		return approved;
	}

	public void setApproved(Byte approved) {
		this.approved = approved;
	}
	
	

	public Byte getDuration() {
		return duration;
	}

	public void setDuration(Byte duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "ApprovePojo [eventId=" + eventId + ", userId=" + userId + ", userName=" + userName + ", userEmail="
				+ userEmail + ", startDate=" + startDate + ", approved=" + approved + ", duration=" + duration + "]";
	}

	
	
	

}
