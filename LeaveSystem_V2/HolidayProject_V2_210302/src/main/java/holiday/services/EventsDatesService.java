package holiday.services;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.PostConstruct;

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
	
	public Page<EventDates> getAllEvents(Pageable pageable){
		return eventsDayRepo.findAllByOrderByDate(pageable);
	}
	
	public List<EventDates> getAllEvents() {
		return eventsDayRepo.findAllByOrderByDate();
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
	

	public Long isExceptionEventAlreadyExist(EventDates event) {
		
		Long ids = -1L;
		List<EventDates> events = getAllEvents();
		for (EventDates anevent: events) {
			if(event.getDate().compareTo(anevent.getDate())==0) {
				ids=anevent.getId();
				return ids;
			}
		}
		return ids;
	}
	
	
	
//----------------------------------------------------------------------	

//	@PostConstruct
	public void init() throws ParseException {
		
		
		LocalDate  date = LocalDate.of(2021, 1, 01);
		EventDates eventd=new EventDates(date, "Újév", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021, 03, 15);
		eventd=new EventDates(date, "nemzeti ünnep", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021, 04, 02); 
		eventd=new EventDates(date, "Nagypéntek", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021, 04, 05); 
		eventd=new EventDates(date, "húsvét hétfő", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021, 05, 01); 
		eventd=new EventDates(date, "munka ünnepe", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021, 05, 24); 
		eventd=new EventDates(date, "Pünkösd hétfő", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021, 8, 20); 
		eventd=new EventDates(date, "Államalapítás ünnepe", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021, 10, 23); 
		eventd=new EventDates(date, "56-os forradalom", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021,11, 01); 
		eventd=new EventDates(date, "Mindenszentek", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021, 12, 11); 
		eventd=new EventDates(date, "Munkanap áthelyezés(12.24)", true);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021,12, 24); 
		eventd=new EventDates(date, "Szenteste", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021, 12, 25);
		eventd=new EventDates(date, "Karácsony első napja", false);
		eventsDayRepo.save(eventd);
		date = LocalDate.of(2021, 12, 26);
		eventd=new EventDates(date, "Karácsony második napja", false);
		eventsDayRepo.save(eventd);
		
		
	}



}
