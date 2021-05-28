package holiday.services;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Year;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import holiday.entity.EventDates;
import holiday.repository.EventsDateRepository;

@Service
public class EventsDatesService {

	private EventsDateRepository eventsDayRepo;

	@Autowired
	public void setEventsDayRepo(EventsDateRepository eventsDayRepo) {
		this.eventsDayRepo = eventsDayRepo;
	}

	public Page<EventDates> getAllEvents(int actYear, Pageable pageable) {

		LocalDate date = LocalDate.of(actYear, 1, 1);
		Page<EventDates> exEvents = eventsDayRepo.findAllByOrderByDate(pageable);
		Iterator<EventDates> it = exEvents.iterator();
		while (it.hasNext()) {
			EventDates event = it.next();
			if (event.getDate().isBefore(date)) {
				it.remove();
			}
		}
		return exEvents;
	}

	public List<EventDates> getAllEvents(int actYear) {
		LocalDate date = LocalDate.of(actYear, 1, 1);
		List<EventDates> exEvents = eventsDayRepo.findAllByOrderByDate();
		Iterator<EventDates> it = exEvents.iterator();
		while (it.hasNext()) {
			EventDates event = it.next();
			if (event.getDate().isBefore(date)) {
				it.remove();
			}
		}
		return exEvents;
	}

	public EventDates findById(Long id) {
		return eventsDayRepo.findAllById(id);
	}

	public void deleteEventById(Long id) {
		eventsDayRepo.deleteById(id);
	}

	public void addNewExceptionEvent(EventDates event) {
		eventsDayRepo.save(event);
	}

	public Long isExceptionEventAlreadyExist(int actYear, EventDates event) {

		Long ids = -1L;
		List<EventDates> events = getAllEvents(actYear);
		for (EventDates anevent : events) {
			if (event.getDate().compareTo(anevent.getDate()) == 0) {
				ids = anevent.getId();
				return ids;
			}
		}
		return ids;
	}
	
	public void setConstantExEvents(int actYear) {
		
		LocalDate date = LocalDate.of(actYear, 1, 01);
		EventDates eventd = new EventDates(date, "Újév", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(actYear, 03, 15);
		eventd = new EventDates(date, "Nemzeti ünnep", false);
		eventsDayRepo.save(eventd);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(actYear, 05, 01);
		eventd = new EventDates(date, "A munka ünnepe", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(actYear, 05, 24);
		eventd = new EventDates(date, "Pünkösd hétfő", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(actYear, 8, 20);
		eventd = new EventDates(date, "Államalapítás ünnepe", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(actYear, 10, 23);
		eventd = new EventDates(date, "56-os forradalom", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(actYear, 11, 01);
		eventd = new EventDates(date, "Mindenszentek", false);
		eventsDayRepo.save(eventd);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(actYear, 12, 25);
		eventd = new EventDates(date, "Karácsony első napja", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(actYear, 12, 26);
		eventd = new EventDates(date, "Karácsony második napja", false);
		eventsDayRepo.save(eventd);

		
		
		
	}
	
	

//----------------------------------------------------------------------	

//	@PostConstruct
	public void init() throws ParseException {
		
		
		setConstantExEvents(Year.now().getValue());  // állandó ünnepek
		
		LocalDate date = LocalDate.of(2021, 04, 02);
		EventDates eventd = new EventDates(date, "Nagypéntek", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021, 04, 05);
		eventd = new EventDates(date, "húsvét hétfő", false);
		date = LocalDate.of(2021, 12, 11);
		eventd = new EventDates(date, "Munkanap áthelyezés(12.24)", true);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021, 12, 24);
		eventd = new EventDates(date, "Szenteste", false);
	}

}
