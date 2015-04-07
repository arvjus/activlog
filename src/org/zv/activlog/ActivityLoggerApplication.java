package org.zv.activlog;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.zv.activlog.model.dao.ActivityAttributeDAO;
import org.zv.activlog.model.dao.ActivityDAO;
import org.zv.activlog.model.dao.DbHelper;
import org.zv.activlog.model.entity.Activity;
import org.zv.activlog.model.entity.ActivityAttribute;

public class ActivityLoggerApplication extends Application {
	private static ActivityLoggerApplication singleton;
	private DbHelper dbHelper;
	private List<Activity> activities;
	private List<Date> resultsDates; 
	private Map<String, Double []> resultsValues;				
	
	public static ActivityLoggerApplication getInstance() {
		return singleton;
	}
	
	@Override
	public void onCreate() {
		Log.d(getClass().getSimpleName(), "onCreate()..");
		super.onCreate();
		singleton = this;
	}

	public void initDbHelper() {
		closeDbHelper();
		dbHelper = new DbHelper(getApplicationContext());
		dbHelper.open();
		cacheDatabase(0, true);
	}
	
	public void closeDbHelper() {
		if (dbHelper != null) {
			dbHelper.close();
			dbHelper = null;
		}
	}
	
	public DbHelper getDbHelper() {
		return dbHelper;
	}

	/**
	 * Read database and put into cache. if activityId is not 0, then update just that table.
	 * @param activityId
	 */
	public void cacheDatabase(int activityId, boolean bradcastDababaseEvent) {
		// retrieve data
		ActivityDAO activityDAO = new ActivityDAO();
		ActivityAttributeDAO activityAttributeDAO = new ActivityAttributeDAO();

		if (activityId != 0) {
			for (Activity activity : this.activities) {
				if (activity.activityId == activityId) {
					List<ActivityAttribute> activityAttributes = activityAttributeDAO.findByActivityId(activity.activityId);
					activity.setActivityAttributes(activityAttributes);
					break;
				}
			}
		} else {
			List<Activity> activities = activityDAO.findAll();
			for (Activity activity : activities) {
				List<ActivityAttribute> activityAttributes = activityAttributeDAO.findByActivityId(activity.activityId);
				activity.setActivityAttributes(activityAttributes);
			}
			this.activities = activities;
		}
		
		// notify activities
		Intent intent = new Intent();
		intent.setAction(Constants.BRADCAST_ACTION);
		if (bradcastDababaseEvent) {
			intent.putExtra(Constants.BRADCAST_EVENT, Constants.BRADCAST_EVENT_DATABASE);
		} else {
			intent.putExtra(Constants.BRADCAST_EVENT, activityId != 0 ? Constants.BRADCAST_EVENT_ACTIVITY_ATTRIBUTE : Constants.BRADCAST_EVENT_ACTIVITY);
		}
		sendBroadcast(intent);
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public List<Date> getResultsDates() {
		return resultsDates;
	}

	public void setResultsDates(List<Date> resultsDates) {
		this.resultsDates = resultsDates;
	}

	public Map<String, Double[]> getResultsValues() {
		return resultsValues;
	}

	public void setResultsValues(Map<String, Double[]> resultsValues) {
		this.resultsValues = resultsValues;
	}
}
