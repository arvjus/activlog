package org.zv.activlog.model.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import org.zv.activlog.ActivityLoggerApplication;
import org.zv.activlog.model.entity.Activity;

public class ActivityDAO {
	// activities (activity_id, profile_id, name, enabled)
	public static final String TABLE_NAME = "activities";

	private final DbHelper dbHelper = ActivityLoggerApplication.getInstance().getDbHelper();
	
	public boolean save(Activity activity) {
		boolean result = false;
		ContentValues values = new ContentValues(); 
		values.put("profile_id", activity.profileId);
		values.put("name", activity.name);
		values.put("enabled", activity.enabled);
		if (activity.activityId == 0) {
			long id = dbHelper.insert(TABLE_NAME, values);
			if (id != -1) {
				activity.activityId = (int) id;
				result = true; 
			}
		} else {
			long count = dbHelper.update(TABLE_NAME, values, "activity_id = ?", new String [] { "" + activity.activityId });
			if (count > 0) {
				result = true;
			}
		}
		return result;
	}

	public boolean delete(int activityId) {
		return dbHelper.delete(TABLE_NAME, "activity_id = ?", new String [] { "" + activityId }) > 0;
	}

	public Activity find(int activityId) {
		Activity activity = null;
		Cursor cursor = dbHelper.query(TABLE_NAME, new String [] { "activity_id", "profile_id", "name", "enabled" }, "activity_id = ?", new String [] { "" + activityId }, null, null, null);
		try {
	        cursor.moveToFirst();
	        if (!cursor.isAfterLast()) {
	        	activity = new Activity();
	        	activity.activityId = cursor.getInt(0);
	        	activity.profileId = cursor.getInt(1);
	        	activity.name = cursor.getString(2);
	        	activity.enabled = cursor.getInt(3) != 0;
	        }
		} finally {
	        cursor.close();
		}

        return activity;
	}

	public List<Activity> findAll() {
		List<Activity> activities = new ArrayList<Activity>();

		Cursor cursor = dbHelper.query(TABLE_NAME, new String [] { "activity_id", "profile_id", "name", "enabled" }, null, null, null, null, "name ASC");
		try {
	        cursor.moveToFirst();
	        while (!cursor.isAfterLast()) {
	        	Activity activity = new Activity();
	        	activity.activityId = cursor.getInt(0);
	        	activity.profileId = cursor.getInt(1);
	        	activity.name = cursor.getString(2);
	        	activity.enabled = cursor.getInt(3) != 0;
	        	activities.add(activity);
	        	cursor.moveToNext();
	        }
		} finally {
	        cursor.close();
		}

        return activities;
	}
}
