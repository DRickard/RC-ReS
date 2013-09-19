package edu.ucla.library.libservices.rswrapper.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class ResourceSchedule {
	
	public ResourceSchedule() {
		super();
	}
	
	@XmlElement(name = "schedule")
	private List<Reservation> schedule;
	
	@XmlElement(name = "name")
	private String name;

	public List<Reservation> getSchedule() {
		return schedule;
	}

	public void setSchedule(List<Reservation> schedule) {
		this.schedule = schedule;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
