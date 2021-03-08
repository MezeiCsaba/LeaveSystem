package holiday.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	@Column(unique = true, nullable = false)
	private String email;
	@Column(nullable = false)
	private String password;
	
	@ManyToMany( cascade = CascadeType.PERSIST ,fetch = FetchType.EAGER)  //cascade = CascadeType.ALL,
	@JoinTable(name = "users_roles", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
			@JoinColumn(name = "role_id") })
	private Set<Role> roles = new HashSet<Role>();
	
	private Boolean status;
	private Integer baseLeave;
	private Integer parentalLeave;
	private Integer carriedLeave;
	private Integer otherLeave;
	private Long approverId;    // jóváhagyó user Id-ja
	private String activationCode;

	@JsonBackReference
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER) // cascade = CascadeType.ALL,
	private List<Event> events;

	public User() {
	};
	
	
	public User(String name, String email, String password, String role, Boolean status, Integer baseLeave) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.addRole(role);
		this.status = status;
		this.baseLeave = baseLeave;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void addRole(String role) {

		if (this.roles == null || this.roles.isEmpty())
			this.roles = new HashSet<Role>();
		this.roles.add(new Role(role));
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Integer getParentalLeave() {
		return parentalLeave;
	}

	public void setParentalLeave(Integer parentalLeave) {
		this.parentalLeave = parentalLeave;
	}

	public Integer getBaseLeave() {
		return baseLeave;
	}

	public void setBaseLeave(Integer baseLeave) {
		this.baseLeave = baseLeave;
	}

	public Integer getOtherLeave() {
		return otherLeave;
	}

	public void setOtherLeave(Integer otherLeave) {
		this.otherLeave = otherLeave;
	}

	public Integer getCarriedLeave() {
		return carriedLeave;
	}

	public void setCarriedLeave(Integer carriedLeave) {
		this.carriedLeave = carriedLeave;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
	
	public Long getApproverId() {
		return approverId;
	}

	public void setApproverId(Long approverId) {
		this.approverId = approverId;
	}
	
	

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", baseLeave=" + baseLeave + ", parentalLeave=" + parentalLeave
				+ ", carriedLeave=" + carriedLeave + ", otherLeave=" + otherLeave + "]";
	}

}
