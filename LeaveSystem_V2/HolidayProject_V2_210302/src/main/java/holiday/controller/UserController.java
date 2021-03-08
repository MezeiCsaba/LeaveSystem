package holiday.controller;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import holiday.entity.Event;
import holiday.entity.EventDates;
import holiday.entity.Role;
import holiday.entity.User;
import holiday.repository.UserRepository;
import holiday.services.EventService;
import holiday.services.EventsDatesService;
import holiday.services.RoleService;
import holiday.services.UserService;

@Controller
public class UserController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private String newUserError = null;
	private String searchText = "";

	private UserRepository userRepo;

	private EventService eventService;
	private UserService userService;
	private RoleService roleService;
	private EventsDatesService eventsDatesService;

	@Autowired
	public void setEventsDatesService(EventsDatesService eventsDatesService) {
		this.eventsDatesService = eventsDatesService;
	}

	@Autowired
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	@Autowired
	public void setUserRepo(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping("/registration")
	public String registration(@ModelAttribute User user, Model model) {

		List<Role> roles = roleService.getAllRoles();
		model.addAttribute("allRoles", roles);

		// jóváhagyói lista összeállítása
		List<User> approvers = roleService.findUsersByRoleName("APPROVER");
		model.addAttribute("approversList", approvers);

		if (user.getId() != null) { // update User
			User originalUser = userService.findById(user.getId());
			model.addAttribute("user", originalUser);
		} else { // new user
			model.addAttribute("user", new User());
		}
		return "registration";
	}

	@PostMapping("/reg")
	public String reg(@ModelAttribute User user, Authentication authentication) {

		newUserError = null;

		if (user.getApproverId() != null && user.getApproverId() < 0)
			user.setApproverId(null);

		if (user.getId() == null) { // new user,
			if (userService.findByEmail(user.getEmail()) != null) { // but exist
				newUserError = "regUnSucces";
			} else { // new user
				userService.registerUser(user);
				newUserError = "regSucces";
				log.debug("új regisztráció:" + user.getEmail());
			}

		} else { // update user

			User authUser = userService.findByEmail(authentication.getName());
			User originalUser = userService.findById(user.getId());
			Boolean chgEmail = false;

			if (user.getName() == null || user.getName().isEmpty())
				user.setName(originalUser.getName());
			if (user.getEmail() == null || user.getEmail().isEmpty()) {
				user.setEmail(originalUser.getEmail());
				
			} else chgEmail=!user.getEmail().equals(originalUser.getEmail());  // ha a régi és az új email cím nem egyezik, akkor változott (chgEmail=true)
			
			if (user.getPassword() == null || user.getPassword().isEmpty())
				user.setPassword(originalUser.getPassword());
			if (user.getRoles() == null || user.getRoles().isEmpty())
				user.setRoles(originalUser.getRoles());
			if (user.getStatus() == null)
				user.setStatus(originalUser.getStatus());
			if (user.getBaseLeave() == null)
				user.setBaseLeave(originalUser.getBaseLeave());
			if (user.getParentalLeave() == null)
				user.setParentalLeave(originalUser.getParentalLeave());
			if (user.getCarriedLeave() == null)
				user.setCarriedLeave(originalUser.getCarriedLeave());
			if (user.getOtherLeave() == null)
				user.setOtherLeave(originalUser.getOtherLeave());
			user.setActivationCode(originalUser.getActivationCode());

			if (roleService.userRoleisExist(authUser.getId(), "ADMIN")) {
				userService.updateUserAsAdmin(user, chgEmail);
			} else {
				userService.updateUserAsHR(user);
			}
			newUserError = "updateSucces"; // sikeres adat frissítés
		}
		return "redirect:/userhandling";
	}

	@GetMapping("/userhandling")
	public String userhandling(@RequestParam(defaultValue = "0") int page, Model model) {

		Page<User> users = userRepo.findByOrderByName(PageRequest.of(page, 7));

		model.addAttribute("updateuser", new User());
		model.addAttribute("users", users);
		model.addAttribute("currentPage", page);
		model.addAttribute("newUserError", newUserError);

		newUserError = null;

		return "userhandling";
	}

	@PostMapping("/search")
	public String search(@RequestParam("searchtext") String fSearchText, Model model) {
		searchText = fSearchText;
		Page<User> users = userService.findByNameContaining(searchText, PageRequest.of(0, 7));

		model.addAttribute("updateuser", new User());
		model.addAttribute("users", users);
		model.addAttribute("currentPage", 0);

		return "userhandling";

	}

	@GetMapping("/orderBy")
	public String orderBy(@RequestParam String orderBy, @RequestParam int currentPage, Model model) {

		Page<User> users;
		switch (orderBy) {

		case "status": {
			users = userService.findByOrderByStatus(searchText, PageRequest.of(currentPage, 7));
			break;
		}
		case "role": { // egyelőre kikötve, a többes jogosultság miatt
			users = userService.findByOrderByRole(searchText, PageRequest.of(currentPage, 7));
			break;
		}
		case "email": {
			users = userService.findByOrderByEmail(searchText, PageRequest.of(currentPage, 7));
			break;
		}
		default: { // orderByName
			users = userService.findByNameContaining(searchText, PageRequest.of(currentPage, 7));
		}
		}
		model.addAttribute("updateuser", new User());
		model.addAttribute("users", users);
		model.addAttribute("currentPage", currentPage);

		return "userhandling";
	}

	@GetMapping("/userInfoPage")
	public String userInfoPage(@RequestParam("user.id") Long userId, Model model) {

		User authUser = userService.findById(userId);
		Long authUserId = userId;
		Double userLeaves[] = new Double[2]; // kivett és összes szabadság napokban
		Arrays.fill(userLeaves, 0D);

		userLeaves[0] = eventService.getUserSumLeave(authUser.getId());
		userLeaves[1] = Double.valueOf(eventService.getSumLeaveDay(authUser));

		String approverName = " nincs";
		if (authUser.getApproverId() != null) {
			User approver = userService.findById(authUser.getApproverId());
			approverName = approver.getName() + " (" + approver.getEmail() + ")"; // jóváhagyó személye és emil címe
		}
		List<Event> eventList = eventService.getUserEvents(authUserId); // szabadságok
		eventList.forEach(e -> e.setUser(null)); // user objektumot kukázzuk, mert a Javascriptnek átadásnál gond van
													// vele és nem is kell
		List<EventDates> exEventList = eventsDatesService.getAllEvents(); // kivételnapok

		model.addAttribute("approverName", approverName);
		model.addAttribute("user", authUser);
		model.addAttribute("userLeave", userLeaves);
		model.addAttribute("eventList", eventList);
		model.addAttribute("exEventList", exEventList);
		
		return "userInfoPage";
	}

	@GetMapping("/changepassword")
	public String changePassword(Model model, Authentication authentication) {

		User actUser = userService.findByEmail(authentication.getName());

		model.addAttribute("user", actUser);
		
		return "changepassword";

	}

	@GetMapping(path = "/activation/{code}")
	public String activation(@PathVariable("code") String code, Model model) {

		Long actUserId = userService.isCodeValid(code);
		if (actUserId >= 0) {
			User actUser = userService.findById(actUserId);
			model.addAttribute("user", actUser);
			return "changepassword";
		}
		model.addAttribute("activation", "unsuccess");
		return "auth/login";
	}

	@PostMapping("/setnewpassword")
	public String setNewPassword(@ModelAttribute User user, Model model) {
		Boolean passChg=user.getPassword()==null;
		User actUser = userService.findById(user.getId());
		actUser.setPassword(user.getPassword());
		userService.registerUser(actUser);
		model.addAttribute("activation", passChg ? "success":"passChg");
		return "auth/login";
	}

}
