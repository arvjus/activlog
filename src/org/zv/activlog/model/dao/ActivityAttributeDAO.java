package org.zv.activlog.model.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;

import org.zv.activlog.ActivityLoggerApplication;
import org.zv.activlog.model.entity.Activity;
import org.zv.activlog.model.entity.ActivityAttribute;

public class ActivityAttributeDAO {
	//	activity_attributes (activity_attribute_id, activity_id, type, name, default_value, enabled)
	public static final String TABLE_NAME = "activity_attributes";

	private final DbHelper dbHelper = ActivityLoggerApplication.getInstance().getDbHelper();
	
	public boolean save(ActivityAttribute activityAttribute) {
		boolean result = false;
		ContentValues values = new ContentValues(); 
		values.put("activity_id", activityAttribute.activityId);
		values.put("type", activityAttribute.type);
		values.put("name", activityAttribute.name);
		values.put("default_value", activityAttribute.defaultValue);
		values.put("enabled", activityAttribute.enabled);
		if (activityAttribute.activityAttributeId == 0) {
			long id = dbHelper.insert(TABLE_NAME, values);
			if (id != -1) {
				activityAttribute.activityAttributeId = (int) id;
				result = true; 
			}
		} else {
			long count = dbHelper.update(TABLE_NAME, values, "activity_attribute_id = ?", new String [] { "" + activityAttribute.activityAttributeId });
			if (count > 0) {
				result = true;
			}
		}
		return result;
	}

	public boolean delete(int activityAttributeId) {
		return dbHelper.delete(TABLE_NAME, "activity_attribute_id = ?", new String [] { "" + activityAttributeId }) > 0;
	}

	public ActivityAttribute find(int activityAttributeId) {
		ActivityAttribute activityAttribute = null;
		Cursor cursor = dbHelper.query(TABLE_NAME, new String [] { "activity_attribute_id", "activity_id", "type", "name", "default_value", "enabled" }, "activity_attribute_id = ?", new String [] { "" + activityAttributeId }, null, null, null);
		try {
	        cursor.moveToFirst();
	        if (!cursor.isAfterLast()) {
	        	activityAttribute = new ActivityAttribute();
	        	activityAttribute.activityAttributeId = cursor.getInt(0);
	        	activityAttribute.activityId = cursor.getInt(1);
	        	activityAttribute.type = cursor.getInt(2);
	        	activityAttribute.name = cursor.getString(3);
	        	activityAttribute.defaultValue = cursor.getString(4);
	        	activityAttribute.enabled = cursor.getInt(5) != 0;
	        }
		} finally {
	        cursor.close();
		}
        
		return activityAttribute;
	}

	public List<ActivityAttribute> findByActivityId(int activityId) {
		List<ActivityAttribute> activityAttributes = new ArrayList<ActivityAttribute>();

		Cursor cursor = dbHelper.query(TABLE_NAME, new String [] { "activity_attribute_id", "activity_id", "type", "name", "default_value", "enabled" }, "activity_id = ?", new String [] { "" + activityId }, null, null, "activity_attribute_id ASC");
		try {
	        cursor.moveToFirst();
	        while (!cursor.isAfterLast()) {
	        	ActivityAttribute activityAttribute = new ActivityAttribute();
	        	activityAttribute.activityAttributeId = cursor.getInt(0);
	        	activityAttribute.activityId = cursor.getInt(1);
	        	activityAttribute.type = cursor.getInt(2);
	        	activityAttribute.name = cursor.getString(3);
	        	activityAttribute.defaultValue = cursor.getString(4);
	        	activityAttribute.enabled = cursor.getInt(5) != 0;
	        	activityAttributes.add(activityAttribute);
	        	cursor.moveToNext();
	        }
		} finally {
	        cursor.close();
		}

		return activityAttributes;
	}
}
