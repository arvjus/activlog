package org.zv.activlog.model.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.database.Cursor;

import org.zv.activlog.ActivityLoggerApplication;
import org.zv.activlog.util.Utils;

public class StatisticsDAO {
	private final DbHelper dbHelper = ActivityLoggerApplication.getInstance().getDbHelper();
	
	public int groupByDate(boolean byMonths, String dateFrom, String dateTo, int activityId, int activityAttributeId, List<Date> dates, Map<String, Double []> values) {
		String sql = "SELECT SUBSTR(al.create_date, 1, ?) AS date, " +
					 "       COUNT(aal.value) AS count, " +
					 "       SUM(aal.value) AS sum, " +
					 "       MIN(aal.value) AS min, " +
					 "       MAX(aal.value) AS max, " +
					 "       AVG(aal.value) AS avg " +
					 "  FROM activity_logs al " +
					 " INNER JOIN activity_attribute_logs aal ON (al.activity_log_id = aal.activity_log_id) " +
					 " WHERE al.create_date >= ? " +
					 "   AND al.create_date <= ? " +
					 "   AND al.activity_id = ? " +
					 "   AND aal.activity_attribute_id = ? " +
					 " GROUP BY date " +
					 " ORDER BY date "; 
		Cursor cursor = dbHelper.rawQuery(sql, new String [] { byMonths ? "7" : "10", dateFrom, dateTo, "" + activityId, "" + activityAttributeId });
		int count = cursor.getCount();
		Double [] cnt = new Double [count];  
		Double [] sum = new Double [count];  
		Double [] min = new Double [count];  
		Double [] max = new Double [count];  
		Double [] avg = new Double [count];  
		try {
	        int i = 0;
			cursor.moveToFirst();
	        while (!cursor.isAfterLast()) {
	        	dates.add(Utils.stringYYYYMMDDToDate(cursor.getString(0)));
	        	cnt[i] = (double)Math.round(cursor.getDouble(1));
	        	sum[i] = (double)Math.round(cursor.getDouble(2) * 100) / 100;
	        	min[i] = (double)Math.round(cursor.getDouble(3) * 100) / 100;
	        	max[i] = (double)Math.round(cursor.getDouble(4) * 100) / 100;
	        	avg[i] = (double)Math.round(cursor.getDouble(5) * 100) / 100;
	        	i ++;
	        	cursor.moveToNext();
	        }
		} finally {
	        cursor.close();
		}
		values.put("cnt", cnt);
		values.put("sum", sum);
		values.put("min", min);
		values.put("max", max);
		values.put("avg", avg);
        return count;
	}

	public int groupByDate(boolean byMonths, String dateFrom, String dateTo, int activityId, List<Date> dates, Map<String, Double []> values) {
		String sql = "SELECT SUBSTR(al.create_date, 1, ?) AS date, " +
					 "       COUNT(al.activity_log_id) AS count " +
					 "  FROM activity_logs al " +
					 " WHERE al.create_date >= ? " +
					 "   AND al.create_date <= ? " +
					 "   AND al.activity_id = ? " +
					 " GROUP BY date " +
					 " ORDER BY date "; 
		Cursor cursor = dbHelper.rawQuery(sql, new String [] { byMonths ? "7" : "10", dateFrom, dateTo, "" + activityId });
		int count = cursor.getCount();
		Double [] cnt = new Double [count];  
		try {
	        int i = 0;
			cursor.moveToFirst();
	        while (!cursor.isAfterLast()) {
	        	dates.add(Utils.stringYYYYMMDDToDate(cursor.getString(0)));
	        	cnt[i] = (double)Math.round(cursor.getDouble(1));
	        	i ++;
	        	cursor.moveToNext();
	        }
		} finally {
	        cursor.close();
		}
		values.put("cnt", cnt);
        return count;
	}
}
