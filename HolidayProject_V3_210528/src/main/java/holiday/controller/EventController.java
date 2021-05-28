package holiday.controller;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import holiday.entity.AppPojoList;
import holiday.entity.Event;
import holiday.entity.EventDates;
import holiday.entity.User;
import holiday.entity.UserLeaves;
import holiday.services.EventService;
import holiday.services.EventsDatesService;
import holiday.services.LeaveService;
import holiday.services.UserService;

@Controller
public class EventController {

//	private List<Long> ids = new ArrayList<>();
	private Long eids;
//	private String newEventError = null;
	public String newExceptionError = null;

	private EventService eventService;
	private UserService userService;
	private EventsDatesService eventsDatesService;
	private LeaveService leaveService;

	@Autowired
	public void setLeaveService(LeaveService leaveService) {
		this.leaveService = leaveService;
	}

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
		int year = Year.now().getValue();

		Set<UserLeaves> allLeaveFrame = leaveService.findByUserIdAndYearBetween(authUserId, year - 1, year + 1);

		allLeaveFrame.forEach((leave) -> leave.setUser(null)); // a user eves szabadsag keretei (tavaly, idei, jovo evi
																// - ami letezik, user mezot kiutjuk, ne okozzon
																// gondot JS oldalon

		List<Event> eventList = eventService.getUserEvents(authUserId); // szabadságok
		eventList.forEach(e -> e.setUser(null)); // user objektumot kukázzuk, mert a Javascriptnek átadásnál gond van
													// vele és nem is kell
		List<EventDates> exEventList = eventsDatesService.getAllEvents(year); // kivételnapok

		model.addAttribute("allLeaveFrame", allLeaveFrame);
		model.addAttribute("eventList", eventList);
		model.addAttribute("exEventList", exEventList);

		return "calendar";
	}

	@RequestMapping("/exEventCalendar")
	public String exEventCalendar(Model model, @RequestParam(defaultValue = "0") int page) {
		
		int thisYear=Year.now().getValue();
		List<EventDates> exEventList = eventsDatesService.getAllEvents(thisYear); // kivételnapok
		boolean isExistNextYear=false;
		for(int i=0; exEventList.size()>i; i++) {
			
			EventDates event = exEventList.get(i);
			if (event.getDate().getYear()==thisYear+1) {
				isExistNextYear=true;
				break;
			}
		}
		// ha a következő év üres, feltöltjük az állandó ünnepeket (újév, karácsony, stb)
		if (!isExistNextYear) eventsDatesService.setConstantExEvents(thisYear+1);
		
		model.addAttribute("exEventList", exEventList);
		model.addAttribute("newevent", new EventDates());
		model.addAttribute("currentPage", page);
		model.addAttribute("fevents", eids);
		model.addAttribute("thisYear", thisYear);
		
		eids = -1L;
		return "exEventCalendar";
	}
	
	

	@GetMapping(value = "events/deleteexevent/{anevent.id}")
	public String ExeptionEventDelete(@PathVariable(value = "anevent.id") Long eventId) {

		if (eventsDatesService.findById(eventId) != null)
			eventsDatesService.deleteEventById(eventId);
		return "redirect:/holidayEventCalendar";
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

	@PostMapping(value = "/saveLeavesFrames", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> saveLeavesFrames(@RequestBody UserLeaves userLeavesFrames[],
			Authentication authentication) throws JsonProcessingException {

		UserLeaves actYearLeavesFrame = userLeavesFrames[0];
		UserLeaves nextYearLeavesFrame = userLeavesFrames[1];
		User actUser = userService.findById(actYearLeavesFrame.getId());

		if (actYearLeavesFrame != null) {
			actYearLeavesFrame.setUser(actUser);
			leaveService.saveLeave(actYearLeavesFrame); // az aktualis ev szabi kerete

			if (nextYearLeavesFrame.getSumLeaveFrame() > 0) { // a kovetkezo ev szabi kerete, ha van benne ertek

				nextYearLeavesFrame.setUser(actUser);
				leaveService.saveLeave(nextYearLeavesFrame);
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(toJson("OK"));
	}
	
	
	@PostMapping(value = "/saveExEventList", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> saveExEventList(@RequestBody List<EventDates> exEventList) throws JsonProcessingException {
		Integer thisYear = Year.now().getValue();
		List<EventDates> originalExEventList = eventsDatesService.getAllEvents(thisYear);
		
		exEventList.forEach((event) ->{			
			if (event.getId()==-1L) { // new exDateEvent, save to DB
				event.setId(null);
				eventsDatesService.addNewExceptionEvent(event);
							}			
		});
		
		originalExEventList.forEach((oEvent) ->{
			Boolean isExist = false;
			for (int j = 0; exEventList.size() > j; j++) {
				EventDates event = exEventList.get(j);
				
				if (oEvent.getId().intValue() == event.getId().intValue()) {
					isExist = true; // match 
					oEvent.setIsWorkDay(event.getIsWorkDay());
					oEvent.setNote(event.getNote());
					eventsDatesService.addNewExceptionEvent(oEvent);
				}
			}
			if (!isExist) eventsDatesService.deleteEventById(oEvent.getId()); // deleted event
		});
				
		return ResponseEntity.status(HttpStatus.OK).body(toJson("OK"));
	}

	private String toJson(Object ans) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(ans);
	}

}
