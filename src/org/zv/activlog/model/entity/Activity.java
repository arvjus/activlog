package org.zv.activlog.model.entity;

import java.util.List;

public class Activity {
	public	int		activityId;
	public	int		profileId;
	public	String	name;
	public	boolean	enabled;
	
	private List<ActivityAttribute> activityAttributes;

	public List<ActivityAttribute> getActivityAttributes() {
		return activityAttributes;
	}

	public void setActivityAttributes(List<ActivityAttribute> activityAttributes) {
		this.activityAttributes = activityAttributes;
	}

	@Override
	public String toString() {
		return name;
	}
}
