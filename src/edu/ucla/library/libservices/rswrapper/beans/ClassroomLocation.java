package edu.ucla.library.libservices.rswrapper.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class ClassroomLocation extends Location{
	
	public ClassroomLocation() {
		super();
	}
	
	static public ClassroomLocation getClassroomLocation(String name) {
		ClassroomLocation loc = new ClassroomLocation();

		if (name.equals("a")) {
			loc.setResourceId(65);
			loc.setMagiLocationId(9);
		}
		if (name.equals("b")) {
			loc.setResourceId(66);
			loc.setMagiLocationId(9);
		}
		if (name.equals("c")) {
			loc.setResourceId(67);
			loc.setMagiLocationId(9);
		}		
		if (name.equals("rc")) {
			loc.setResourceId(208);
			loc.setMagiLocationId(19);
		}
		
		return loc;
	}
	
	public BusinessRules generate(String uid) {
		
		// TODO Get stuff from the database using uid and group
		BusinessRules rules = new BusinessRules();		
		rules.setLimitPerTimePeriod(1);
		rules.setUnitOfTimeInDays(1);
		rules.setStudyRoom(false);
		// 1 advance day, 2 hour time limit
		rules.addRules(this.resourceId, 1, 120);
		return rules;
	}
	
	
	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public boolean isValid() {
		return (resourceId != 0);			
	}
	
	private int resourceId;
}
