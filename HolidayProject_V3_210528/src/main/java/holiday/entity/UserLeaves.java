package holiday.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "userLeaves")
public class UserLeaves {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private User user;
	private Integer year;
	private Integer baseLeave;
	private Integer parentalLeave;
	private Integer carriedLeave;
	private Integer otherLeave;

	public UserLeaves(User user, Integer actYear, Integer baseLeave, Integer parentalLeave, Integer carriedLeave, Integer otherLeave) {
		this.user = user;
		this.year = actYear;
		this.baseLeave = baseLeave;
		this.parentalLeave = parentalLeave;
		this.carriedLeave = carriedLeave;
		this.otherLeave = otherLeave;
	}

	public UserLeaves() {
			
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getBaseLeave() {
		return baseLeave;
	}

	public void setBaseLeave(Integer baseLeave) {
		this.baseLeave = baseLeave;
	}

	public Integer getParentalLeave() {
		return parentalLeave;
	}

	public void setParentalLeave(Integer parentalLeave) {
		this.parentalLeave = parentalLeave;
	}

	public Integer getCarriedLeave() {
		return carriedLeave;
	}

	public void setCarriedLeave(Integer carriedLeave) {
		this.carriedLeave = carriedLeave;
	}

	public Integer getOtherLeave() {
		return otherLeave;
	}

	public void setOtherLeave(Integer otherLeave) {
		this.otherLeave = otherLeave;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
	public Integer getSumLeaveFrame() {
		return baseLeave + parentalLeave + carriedLeave + otherLeave;
	}

	@Override
	public String toString() {
		return "UserLeaves [id=" + id + ", user=" + ((user!=null)? user.getName():"null") + ", year=" + year + ", baseLeave=" + baseLeave
				+ ", parentalLeave=" + parentalLeave + ", carriedLeave=" + carriedLeave + ", otherLeave=" + otherLeave
				+ "]";
	}

}
