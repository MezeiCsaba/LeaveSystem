package holiday.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import holiday.entity.AppPojoList;
import holiday.entity.Event;
import holiday.entity.EventDates;
import holiday.entity.User;
import holiday.services.EventService;
import holiday.services.EventsDatesService;
import holiday.services.UserService;

@Controller
public class EventController {

	private List<Long> ids = new ArrayList<>();
	private Long eids;
	private String newEventError = null;
	public String newExceptionError = null;

	private EventService eventService;
	private UserService userService;
	private EventsDatesService eventsDatesService;

	
	@Autowired
	public void setEventsDatesService(EventsDatesService eventsDatesService) {
		this.eventsDatesService = eventsDatesService;
	}

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	
	
	@GetMapping("/calendar")
	public String calendar(Model model, Authentication authentication) {
		User authUser = userService.findByEmail(authentication.getName());
		Long authUserId = authUser.getId();
		
		List<Event> eventList= eventService.getUserEvents(authUserId); // szabadságok
		eventList.forEach(e -> e.setUser(null));  // user objektumot kukázzuk, mert a Javascriptnek átadásnál gond van vele és nem is kell
		List<EventDates> exEventList = eventsDatesService.getAllEvents();   // kivételnapok
		
		model.addAttribute("eventList", eventList);
		model.addAttribute("exEventList", exEventList);
		model.addAttribute("leaveCounter", eventService.getUserSumLeave(authUserId));
		model.addAttribute("sumLeave", eventService.getSumLeaveDay(authUser));
		
		return "calendar";
	}
	

	@RequestMapping("/holidayEventCalendar")
	public String holidayEventCalendar(Model model, @RequestParam(defaultValue = "0") int page) {
		Page<EventDates> partHolidayEventList = eventsDatesService.getAllEvents(PageRequest.of(page, 4));
		List<EventDates> exEventList = eventsDatesService.getAllEvents();   // kivételnapok
		
		model.addAttribute("exEventList", exEventList);
		model.addAttribute("holidayEvents", partHolidayEventList);
		model.addAttribute("newevent", new EventDates());
		model.addAttribute("currentPage", page);
		model.addAttribute("fevents", eids);
		eids = -1L;
		return "holidayEventCalendar";
	}

	@PostMapping("/newexceptioneventreg")
	public String newexceptioneventreg(@ModelAttribute EventDates event) {

		eids = eventsDatesService.isExceptionEventAlreadyExist(event);
		if (event.getIsWorkDay() == null)
			event.setIsWorkDay(false);
		if (eids < 0)
			eventsDatesService.addNewExceptionEvent(event); // nincs még ilyen dátum a táblában, rögzítjük

		return "redirect:/holidayEventCalendar";
	}

	@GetMapping("/new_userevent")
	public String new_userevent(Model model, Authentication authentication,
			@RequestParam(defaultValue = "0") int page) {

		User authUser = userService.findByEmail(authentication.getName());
		Long authUserId = authUser.getId();
		Page<Event> events = eventService.findAllByUserIdOrderByStartDate(authUserId, PageRequest.of(page, 5));
		model.addAttribute("holidayEvents", eventService.googleEventTable(authUserId));
		model.addAttribute("events", events);
		model.addAttribute("sumLeave", eventService.getSumLeaveDay(authUser));
		model.addAttribute("sumCarrLeave", eventService.getUserSumLeave(authUserId));
		model.addAttribute("fevents", ids);
		model.addAttribute("newEventError", newEventError);
		model.addAttribute("currentPage", page);
		newEventError = null;
		return "new_userevent";
	}

	@PostMapping("/save")
	public String save(Event event, Authentication authentication) {

		User authUser = userService.findByEmail(authentication.getName());
		Long authUserId = authUser.getId();
		newEventError = null;
		Integer actualWorkdayLong = 1;
		Double sumWorkDay = eventService.getUserSumLeave(authUserId);
		Integer sumLeaveDay = eventService.getSumLeaveDay(authUser);
		Double remainingLeave = sumLeaveDay - (sumWorkDay + actualWorkdayLong);
		 if (remainingLeave < 0)
			newEventError = "overLoadLeave"; // nincs elég szabadság az új rögzítéséhez
		if (newEventError == null) {
			eventService.addNewEvent(authUser, event);
		}
		return "redirect:/new_userevent";
	}

	@GetMapping("/delete")
	public String delete(Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		newEventError = null;
		User authUser = userService.findByEmail(authentication.getName());
		Long authUserId = authUser.getId();
		if (id != null)
			eventService.deleteEvent(eventService.findByUserIdByEventId(authUserId, id));
		return "redirect:/new_userevent";
	}

	
	@GetMapping(value = "events/deleteexevent/{anevent.id}")
	public String ExeptionEventDelete(@PathVariable(value = "anevent.id") Long eventId) {

		if (eventsDatesService.findById(eventId) != null)
			eventsDatesService.deleteEventById(eventId);
		return "redirect:/holidayEventCalendar";
	}

	
	@GetMapping(value = "users/deleteevent/{anevent.id}")
	public String eventDelete(@PathVariable(value = "anevent.id") Long eventId, Model model,
			Authentication authentication) {

		newEventError = null;
		User authUser = userService.findByEmail(authentication.getName());
		Long authUserId = authUser.getId();
		eventService.deleteEvent(eventService.findByUserIdByEventId(authUserId, eventId));
		return "redirect:/newuserevent";
	}

	
	@RequestMapping("/approvingPage")
	public String approvingPage(Model model, Authentication authentication) {

		User authUser = userService.findByEmail(authentication.getName());
		Long authUserId = authUser.getId();
		AppPojoList myApPojoList = eventService.findMyApprovList(authUserId);
		model.addAttribute("myApproveList", myApPojoList);
		return "approvingPage";
	}

	
	@PostMapping("/saveapproved")
	public String saveApproved(@ModelAttribute AppPojoList myApproveList) {
		myApproveList.getList().forEach(s -> {
			Event updateEvent = eventService.findByUserIdByEventId(s.getUserId(), s.getEventId());
			updateEvent.setApproved((s.getApproved()));
			Event e = new Event();
			eventService.saveEvent(e);
		});
		return "redirect:/approvingPage";
	}

	
	@GetMapping("/userstable")
	public String usersTable(Model model) {

		List<Integer> lengthOfMonthList = new ArrayList<>();
		List<User> users = userService.getAllUser();
		model.addAttribute("users", users);

		for (int i = 1; i < 13; i++) { // hónapok hossza
			LocalDate month = LocalDate.of(LocalDate.now().getYear(), i, 1);
			int length = month.lengthOfMonth();
			lengthOfMonthList.add(length);
		}
		model.addAttribute("lengthOfMonthList", lengthOfMonthList);

		Map<Long, Map<LocalDate, Integer>> eventMap = new HashMap<>(); // a userek szabadságai
//		User userg = userService.findById(1L);
		
		users.forEach(user -> eventMap.put(user.getId(), eventService.googleEventTable(user.getId())));
		model.addAttribute("usersEvents", eventMap);

		Integer thisYear = LocalDate.now().getYear();
		model.addAttribute("thisYear", thisYear);

		return "userstable";
	}

}
