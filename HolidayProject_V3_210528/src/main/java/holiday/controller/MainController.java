package holiday.controller;

import java.time.Year;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import holiday.entity.Event;
import holiday.entity.EventDates;
import holiday.entity.User;
import holiday.entity.UserLeaves;
import holiday.services.EventService;
import holiday.services.EventsDatesService;
import holiday.services.LeaveService;
import holiday.services.UserService;

@Controller
public class MainController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private EventService eventService;
	private UserService userService;
	private LeaveService leaveService;
	private EventsDatesService eventsDatesService;

	@Autowired
	public void setEventsDatesService(EventsDatesService eventsDatesService) {
		this.eventsDatesService = eventsDatesService;
	}

	@Autowired
	public void setLeaveService(LeaveService leaveService) {
		this.leaveService = leaveService;
	}

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping("/")
	public String index(Authentication authentication, Model model) {

		User authUser = userService.findByEmail(authentication.getName());
		Long authUserId = authUser.getId();

		// ez a kód ugyanaz, mint a UserController userInfoPage metódusa, később refakt!
		Integer thisYear = Year.now().getValue();
		UserLeaves userLeaves = leaveService.getUserLeavesByYear(thisYear, authUser);

		Double userSumLeaves[] = new Double[2]; // kivett és összes szabadság napokban
		Arrays.fill(userSumLeaves, 0D);

		userSumLeaves[0] = eventService.getUserSumLeave(authUserId, thisYear);
		userSumLeaves[1] = Double.valueOf(userLeaves.getSumLeaveFrame());

		String approverName = " nincs";
		if (authUser.getApproverId() != null) {
			User approver = userService.findById(authUser.getApproverId());
			approverName = approver.getName() + " (" + approver.getEmail() + ")"; // jóváhagyó személye és emil címe
		}
		List<Event> eventList = eventService.getUserEvents(authUserId); // szabadságok
		eventList.forEach(e -> e.setUser(null)); // user objektumot kukázzuk, mert a Javascriptnek átadásnál gond van
													// vele és nem is kell
		List<EventDates> exEventList = eventsDatesService.getAllEvents(thisYear); // kivételnapok

		model.addAttribute("approverName", approverName);
		model.addAttribute("user", authUser);
		model.addAttribute("userSumLeaves", userSumLeaves);
		model.addAttribute("userLeaves", userLeaves);
		model.addAttribute("eventList", eventList);
		model.addAttribute("exEventList", exEventList);

		return "index";
	}

}
