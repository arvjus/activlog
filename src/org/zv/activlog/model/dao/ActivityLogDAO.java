package org.zv.activlog.model.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import org.zv.activlog.ActivityLoggerApplication;
import org.zv.activlog.model.entity.ActivityLog;

public class ActivityLogDAO {
	// activity_logs (activity_log_id, activity_id, create_date)
	public static final String TABLE_NAME = "activity_logs";

	private final DbHelper dbHelper = ActivityLoggerApplication.getInstance().getDbHelper();
	
	public boolean save(ActivityLog activityLog) {
		boolean result = false;
		ContentValues values = new ContentValues(); 
		values.put("activity_id", activityLog.activityId);
		values.put("create_date", activityLog.createDate);
		if (activityLog.activityLogId == 0) {
			long id = dbHelper.insert(TABLE_NAME, values);
			if (id != -1) {
				activityLog.activityLogId = (int) id;
				result = true; 
			}
		} else {
			long count = dbHelper.update(TABLE_NAME, values, "activity_log_id = ?", new String [] { "" + activityLog.activityLogId });
			if (count > 0) {
				result = true;
			}
		}
		return result;
	}

	public boolean delete(int activityLogId) {
		return dbHelper.delete(TABLE_NAME, "activity_log_id = ?", new String [] { "" + activityLogId }) > 0;
	}
 
	public ActivityLog find(int activityLogId) {
		ActivityLog activityLog = null;
		Cursor cursor = dbHelper.query(TABLE_NAME, new String [] { "activity_log_id", "activity_id", "create_date" }, "activity_log_id = ?", new String [] { "" + activityLogId }, null, null, null);
		try {
	        cursor.moveToFirst();
	        if (!cursor.isAfterLast()) {
	        	activityLog = new ActivityLog();
	        	activityLog.activityLogId = cursor.getInt(0);
	        	activityLog.activityId = cursor.getInt(1);
	        	activityLog.createDate = cursor.getString(2);
	        }
		} finally {
	        cursor.close();
		}

        return activityLog;
	}

	public List<ActivityLog> findAll() {
		List<ActivityLog> activities = new ArrayList<ActivityLog>();

		Cursor cursor = dbHelper.query(TABLE_NAME, new String [] { "activity_log_id", "activity_id", "create_date" }, null, null, null, null, "create_date DESC");
		try {
	        cursor.moveToFirst();
	        while (!cursor.isAfterLast()) {
	        	ActivityLog activityLog = new ActivityLog();
	        	activityLog.activityLogId = cursor.getInt(0);
	        	activityLog.activityId = cursor.getInt(1);
	        	activityLog.createDate = cursor.getString(2);
	        	activities.add(activityLog);
	        	cursor.moveToNext();
	        }
		} finally {
	        cursor.close();
		}

        return activities;
	}
}
