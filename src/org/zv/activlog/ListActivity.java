package org.zv.activlog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zv.activlog.model.dao.ActivityAttributeDAO;
import org.zv.activlog.model.dao.ActivityAttributeLogDAO;
import org.zv.activlog.model.dao.ActivityDAO;
import org.zv.activlog.model.dao.ActivityLogDAO;
import org.zv.activlog.model.dao.StatisticsDAO;
import org.zv.activlog.model.entity.ActivityAttribute;
import org.zv.activlog.model.entity.ActivityAttributeLog;
import org.zv.activlog.model.entity.ActivityLog;
import org.zv.activlog.util.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ListActivity extends Activity {
	private ListView listView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
	    Button activities_button = (Button)findViewById(R.id.statistics_activities_button);
	    activities_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ArrayList<String> list = new ArrayList<String>(); 

				ActivityDAO activityDAO = new ActivityDAO();
				ActivityAttributeDAO activityAttributeDAO = new ActivityAttributeDAO();

				List<org.zv.activlog.model.entity.Activity> activities = activityDAO.findAll();
				for (org.zv.activlog.model.entity.Activity activity : activities) {
					if (list.size() > 0) {
						list.add("");
					}
					list.add("* " + activity.name + ": " + activity.activityId);
					
					List<ActivityAttribute> activityAttributes = activityAttributeDAO.findByActivityId(activity.activityId);
					for (ActivityAttribute activityAttribute : activityAttributes) {
						list.add("- " + activityAttribute.name + ": " + activityAttribute.activityAttributeId);
					}
				}
				
				listView.setAdapter(new ArrayAdapter<String>(ListActivity.this.getApplicationContext(), R.layout.list_item, list.toArray(new String[0])));
			}
	    });
	    
	    Button plain_button = (Button)findViewById(R.id.statistics_plain_button);
	    plain_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ArrayList<String> list = new ArrayList<String>(); 

				ActivityDAO activityDAO = new ActivityDAO();
				ActivityAttributeDAO activityAttributeDAO = new ActivityAttributeDAO();
				ActivityLogDAO activityLogDAO = new ActivityLogDAO();
				ActivityAttributeLogDAO activityAttributeLogDAO = new ActivityAttributeLogDAO();

				List<ActivityLog> activityLogs = activityLogDAO.findAll();
				for (ActivityLog activityLog : activityLogs) {
					if (list.size() > 0) {
						list.add("");
					}
					org.zv.activlog.model.entity.Activity activity = activityDAO.find(activityLog.activityId);
					list.add("* " + activity.name + ": " + activityLog.createDate);
					
					List<ActivityAttributeLog> activityAttributeLogs = activityAttributeLogDAO.findByActivityLogId(activityLog.activityLogId);
					for (ActivityAttributeLog activityAttributeLog : activityAttributeLogs) {
						ActivityAttribute activityAttribute = activityAttributeDAO.find(activityAttributeLog.activityAttributeId);
						list.add("- " + activityAttribute.name + ": " + activityAttributeLog.value);
					}
				}
				
				listView.setAdapter(new ArrayAdapter<String>(ListActivity.this.getApplicationContext(), R.layout.list_item, list.toArray(new String[0])));
			}
	    });
	    
	    Button group_button = (Button)findViewById(R.id.statistics_group_button);
	    group_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ArrayList<String> list = new ArrayList<String>(); 
				
				StatisticsDAO statisticsDAO = new StatisticsDAO();

				List<Date> dates = new ArrayList<Date>(); 
				Map<String, Double []> values = new HashMap<String, Double []>();				

				int count = statisticsDAO.groupByDate(false, "2012-01-01", "2012-03-30", 2, 7, dates, values);
				Double [] cnt = values.get("cnt");  
				Double [] sum = values.get("sum");  
				Double [] min = values.get("min");  
				Double [] max = values.get("max");  
				Double [] avg = values.get("avg");  

				for (int i = 0; i < count; i++) {
					if (i > 0) {
						list.add("");
					}
					list.add("* " + Utils.dateToString(dates.get(i)));
					list.add("- cnt: " + cnt[i]);
					list.add("- sum: " + sum[i]);
					list.add("- min: " + min[i]);
					list.add("- max: " + max[i]);
					list.add("- avg: " + avg[i]);
				}
				
				listView.setAdapter(new ArrayAdapter<String>(ListActivity.this.getApplicationContext(), R.layout.list_item, list.toArray(new String[0])));
			}
	    });
	    
	    listView = (ListView)findViewById(R.id.statistics_listView);
	}
}
