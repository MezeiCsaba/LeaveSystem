package holiday.services;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import holiday.entity.Role;
import holiday.entity.User;
import holiday.repository.RoleRepository;
import holiday.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

	@Value("${webpage.home.path}")
	private String activationLink;
	private UserRepository userRepo;
	private RoleRepository roleRepo;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private ExecutorService emailExecutor = Executors.newFixedThreadPool(10);
	private JavaMailSender javaMailSender;

	@Autowired
	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	public void setRoleRepo(RoleRepository roleRepo) {
		this.roleRepo = roleRepo;
	}

	@Autowired
	public void userService(UserRepository useRepo) {
		this.userRepo = useRepo;
	}

	public List<User> getAllUser() {
		return userRepo.findAllByOrderByName();

	}

	public User getUser(String user) {
		// userRepo.findAllByUserNameIgnorCaseOrderByUserNameDesc(user)

		return userRepo.findFirstByNameIgnoreCase(user);
	}

	public User findByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	public User findById(Long id) {
		return userRepo.findFirstById(id);
	}

	public User findByName(String name) {
		return userRepo.findFirstByNameIgnoreCase(name);
	}

	public Page<User> findByOrderByName(Pageable pageable) {
		return userRepo.findByOrderByName(pageable);
	}

	public Page<User> findByOrderByRole(String name, Pageable pageable) {
		return userRepo.findByNameContainingIgnoreCaseOrderByRoles(name, pageable);
	}

	public Page<User> findByOrderByEmail(String name, Pageable pageable) {
		return userRepo.findByNameContainingIgnoreCaseOrderByEmail(name, pageable);
	}

	public Page<User> findByOrderByStatus(String name, Pageable pageable) {
		return userRepo.findByNameContainingIgnoreCaseOrderByStatus(name, pageable);
	}

	public List<User> findByStatus(Boolean status) {
		return userRepo.findByStatus(status);
	}

	public Page<User> findByNameContaining(String name, Pageable pageable) {
		return userRepo.findByNameContainingIgnoreCaseOrderByName(name, pageable);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}

		return new UserDetailsImpl(user);
	}

	public void checkRoles(User user) {
		for (Role role : user.getRoles()) {
			Role userRole = roleRepo.findByRoleName(role.getRoleName());
			if (userRole != null) {
				user.getRoles().add(userRole);
			} else {
				user.addRole(role.getRoleName());
			}
		}
	}

	public void registerUser(User user) {
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		if (user.getId() == null || user.getId() < 0 ) { // ??jonnan regisztr??lt , aktiv??l??sra v??r?? user
			sendActivationEmail(user);
			user.setStatus(false);
		} else {
			user.setActivationCode("");
			user.setStatus(true);
			}
		
		checkRoles(user);
		User u = userRepo.save(user);
	}
	
	private void sendActivationEmail(User user) {
		user.setActivationCode(generateActivationCode());
		String newActivationLink = activationLink + "activation/" + user.getActivationCode();
		emailExecutor.execute(new EmailService(user,
				"Regisztr??ci??s ??rtes??t??s a Holiday szabads??gnyilv??ntart?? rendszerhez",
				" Sikeresen regisztr??ltak a(z) " + user.getEmail()
						+ " e-mail c??mmel a Holiday szabads??gnyilv??ntart?? rendszerbe. \n\n A regisztr??ci?? aktiv??l??s??hoz l??togass el a k??vetkez?? linkre: "
						+ newActivationLink,
				javaMailSender));
		user.setStatus(false);  // akinek regisztr??ci??s e-mail megy, azt inakt??vra ??ll??tjuk a regisztr??ci?? aktiv??l??s??ig
		log.debug("Email kik??ldve: " + user.getEmail());
	}

	private String generateActivationCode() {
		Random random = new Random();
		char[] code = new char[16];
		for (int i = 0; i < code.length; i++)
			code[i] = (char) ('a' + random.nextInt(26));
		String key = new String(code);
		return new String(key);
	}
	

	public void updateUserAsAdmin(User updateUser, Boolean chgEmail) {

		String updatePassword = null;
		Boolean sendedEmail = false;

		CharSequence thisPassword = updateUser.getPassword(); // password a form-r??l (registration)
		String dbPassword = userRepo.findFirstById(updateUser.getId()).getPassword(); // password a DB-b??l

		if (thisPassword.equals(dbPassword) || passwordEncoder.matches(thisPassword, dbPassword)
				|| (thisPassword == null)) { // ha a kett?? egyezik, nem volt password v??ltoztat??s
			updatePassword = dbPassword;
		} else {
			updatePassword = (passwordEncoder.encode(thisPassword)); // az ??j jelsz??
			sendActivationEmail(updateUser);
			sendedEmail=true;
		}
		updateUser.setPassword(updatePassword);
		checkRoles(updateUser);
		if (chgEmail && !sendedEmail) {	// ??j e-mail c??met adtak meg, ??j aktiv??ci??s email-t k??ld??nk, ??jb??li aktiv??l??sig inakt??v (ha k??zben a jelsz?? is v??tozott, akkor itt m??r nem k??ld??nk emailt
			sendActivationEmail(updateUser);
		}
		
		User u = userRepo.save(updateUser);

//		userRepo.updateUserAsAdmin(updateUser.getName(), updateUser.getEmail(), updatePassword, updateUser.getRoles(),
//				updateUser.getStatus(), updateUser.getId());

	}

	public void updateUserAsHR(User updateUser) {
		userRepo.updateUserAsHR(updateUser.getBaseLeave(), updateUser.getParentalLeave(), updateUser.getCarriedLeave(),
				updateUser.getOtherLeave(), updateUser.getStatus(), updateUser.getId());

	}

	public Long isCodeValid(String code) {   // aktiv??ci??s k??d ellen??rz??se

		User repUser = userRepo.findFirstByActivationCode(code);
		if (repUser == null)
			return -1L;

		return repUser.getId();
	}

}
