package holiday.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import holiday.entity.Role;
import holiday.entity.User;

public class UserDetailsImpl implements UserDetails {

	
	private static final long serialVersionUID = -2201342291123461216L;
	
	private User user;
	
	public UserDetailsImpl(User user) {
		this.user=user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new HashSet<>();
		Set<Role> roles = user.getRoles();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
		}
		
//		if (user.getRoles()==null) {
//			authorities.add(new SimpleGrantedAuthority("null"));
//		
//		}else {
//			authorities.add(new SimpleGrantedAuthority(user.getRoles()));
//		}
		
			
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
//		System.out.println(user.getEmail());
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.getStatus();
	}

}
