package holiday.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
public class SecurityConf extends WebSecurityConfigurerAdapter {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Autowired
	private UserDetailsService userService;

	@Autowired
	public void configureAuth(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService);
	}

	@Override
	protected void configure(HttpSecurity httpSec) throws Exception {
		httpSec.authorizeRequests().antMatchers("/css/**", "/js/**", "/images/**", "/pics/**", "/postdata/**").permitAll()
				.antMatchers("/activation/**", "/changepassword", "/setnewpassword").permitAll()
				.antMatchers("/error", "calendar/**").permitAll()
				.antMatchers("/adminuserupdatereg", "/userhandling").hasAnyAuthority("HR", "ADMIN", "USER")
				.antMatchers("/new_userevent", "/newusereventreg").hasAnyAuthority("USER", "ADMIN", "HR")
				.antMatchers("/users/**", "/registration", "/reg", "/userInfoPage/**").hasAnyAuthority("ADMIN", "HR")
				.antMatchers("/events/**").hasAuthority("HR")
				.anyRequest().authenticated()
				.and()
				.formLogin().loginPage("/login").permitAll()
				.and()
				.logout().logoutSuccessUrl("/login?logout").permitAll()
				.and().csrf().disable().cors();

	}

}
