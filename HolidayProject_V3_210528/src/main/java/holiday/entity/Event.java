package holiday.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "events")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate startDate;
	@ManyToOne
	private User user;
	private Byte approved; // -1: denied, 0: pending approval, 1: approved
	private Byte duration; // 1: egész nap, 2: délelőtt, 3 : délután

	public Event() {
		
	}

	public Event(Long id, LocalDate startDate, User user, Byte approved, Byte duration) {
		super();
		this.id = id;
		this.startDate = startDate;
		this.user = user;
		this.approved = approved;
		this.duration = duration;
	}

	public Event(LocalDate startDate, User user, Byte approved, Byte duration) {

		this.startDate = startDate;
		this.user = user;
		this.approved = approved;
		this.duration = duration;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
		return "Event [id=" + id + ", startDate=" + startDate + ", user=" + user.getName() + ", approved=" + approved
				+ ", duration=" + duration + "]";
	}

}
