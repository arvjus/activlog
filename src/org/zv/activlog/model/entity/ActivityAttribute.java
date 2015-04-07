package org.zv.activlog.model.entity;

public class ActivityAttribute {
	public static final int TYPE_UNDEFINED	= 0;
	public static final int TYPE_DISTANCE	= 1;
	public static final int TYPE_DURATION	= 2;
	public static final int TYPE_RATING		= 3;
	public static final int TYPE_NUMERIC	= 4;
	public static final int TYPE_TEXT		= 5;
	
	public	int		activityAttributeId;
	public	int		activityId;
	public	int		type;
	public	String	name;
	public	String	defaultValue;
	public	boolean	enabled;

	@Override
	public String toString() {
		return name;
	}
}
