package holiday.entity;

import java.util.ArrayList;
import java.util.List;

public class EventListWrapper {
	private List<Event> list = new ArrayList<>();

	public EventListWrapper() {
	}

	public EventListWrapper(List<Event> list) {
		this.list = list;
	}

	public List<Event> getList() {
		return list;
	}

	public void setList(List<Event> list) {
		this.list = list;
	}

	public void add(Event event) {
		this.list.add(event);
	}

	@Override
	public String toString() {
		return "EventListWrapper [list=" + list + "]";
	}

}
