package holiday.entity;

import java.util.ArrayList;
import java.util.List;

public class AppPojoList {
	
	private List<ApprovePojo> list=new ArrayList<>();
	
	public AppPojoList() {
		
	}
	

	public AppPojoList(List<ApprovePojo> list) {
		this.list = list;
	}


	public List<ApprovePojo> getList() {
		return list;
	}


	public void setList(List<ApprovePojo> list) {
		this.list = list;
	}

	public void add(ApprovePojo aPojo) {
		this.list.add(aPojo);
		
	}

	@Override
	public String toString() {
		return "AppPojoList [list=" + list + "]";
	}



	
	
	

}
