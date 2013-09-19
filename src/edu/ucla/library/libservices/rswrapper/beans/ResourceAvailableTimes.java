package edu.ucla.library.libservices.rswrapper.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class ResourceAvailableTimes {
	
	public ResourceAvailableTimes() {
		super();
	}
	
	@XmlElement(name = "availableTimes")
	private List<Reservation> availableTimes;
	
	@XmlElement(name = "name")
	private String name;
	
	public List<Reservation> getAvailableTimes() {
		return availableTimes;
	}
	public void setAvailableTimes(List<Reservation> availableTimes) {
		this.availableTimes = availableTimes;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
