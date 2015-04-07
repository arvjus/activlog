package org.zv.activlog.model.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import org.zv.activlog.ActivityLoggerApplication;
import org.zv.activlog.model.entity.ActivityAttributeLog;

public class ActivityAttributeLogDAO {
	//	activity_attribute_logs (activity_attribute_log_id, activity_log_id, activity_attribute_id, value)
	public static final String TABLE_NAME = "activity_attribute_logs";

	private final DbHelper dbHelper = ActivityLoggerApplication.getInstance().getDbHelper();
	
	public boolean save(ActivityAttributeLog activityAttributeLog) {
		boolean result = false;
		ContentValues values = new ContentValues(); 
		values.put("activity_log_id", activityAttributeLog.activityLogId);
		values.put("activity_attribute_id", activityAttributeLog.activityAttributeId);
		values.put("value", activityAttributeLog.value);
		if (activityAttributeLog.activityAttributeLogId == 0) {
			long id = dbHelper.insert(TABLE_NAME, values);
			if (id != -1) {
				activityAttributeLog.activityAttributeLogId = (int) id;
				result = true; 
			}
		} else {
			long count = dbHelper.update(TABLE_NAME, values, "activity_attribute_log_id = ?", new String [] { "" + activityAttributeLog.activityAttributeLogId });
			if (count > 0) {
				result = true;
			}
		}
		return result;
	}

	public boolean delete(int activityAttributeLogId) {
		return dbHelper.delete(TABLE_NAME, "activity_attribute_log_id = ?", new String [] { "" + activityAttributeLogId }) > 0;
	}

	public ActivityAttributeLog find(int activityAttributeLogId) {
		ActivityAttributeLog activityAttributeLog = null;
		Cursor cursor = dbHelper.query(TABLE_NAME, new String [] { "activity_attribute_log_id", "activity_log_id", "activity_attribute_id", "value" }, "activity_attribute_log_id = ?", new String [] { "" + activityAttributeLogId }, null, null, "activity_attribute_id ASC");
		try {
	        cursor.moveToFirst();
	        if (!cursor.isAfterLast()) {
	        	activityAttributeLog = new ActivityAttributeLog();
	        	activityAttributeLog.activityAttributeLogId = cursor.getInt(0);
	        	activityAttributeLog.activityLogId = cursor.getInt(1);
	        	activityAttributeLog.activityAttributeId = cursor.getInt(2);
	        	activityAttributeLog.value = cursor.getString(3);
	        }
		} finally {
	        cursor.close();
		}

        return activityAttributeLog;
	}

	public List<ActivityAttributeLog> findByActivityLogId(int activityLogId) {
		List<ActivityAttributeLog> activityAttributeLogs = new ArrayList<ActivityAttributeLog>();

		Cursor cursor = dbHelper.query(TABLE_NAME, new String [] { "activity_attribute_log_id", "activity_log_id", "activity_attribute_id", "value" }, "activity_log_id = ?", new String [] { "" + activityLogId }, null, null, "activity_attribute_id ASC");
		try {
	        cursor.moveToFirst();
	        while (!cursor.isAfterLast()) {
	        	ActivityAttributeLog activityAttributeLog = new ActivityAttributeLog();
	        	activityAttributeLog.activityAttributeLogId = cursor.getInt(0);
	        	activityAttributeLog.activityLogId = cursor.getInt(1);
	        	activityAttributeLog.activityAttributeId = cursor.getInt(2);
	        	activityAttributeLog.value = cursor.getString(3);
	        	activityAttributeLogs.add(activityAttributeLog);
	        	cursor.moveToNext();
	        }
		} finally {
	        cursor.close();
		}

        return activityAttributeLogs;
	}
}
