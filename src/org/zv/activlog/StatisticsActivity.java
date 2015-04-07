package org.zv.activlog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import org.zv.activlog.model.dao.StatisticsDAO;
import org.zv.activlog.model.entity.ActivityAttribute;
import org.zv.activlog.service.ChartService;
import org.zv.activlog.util.Utils;

public class StatisticsActivity extends Activity {
	private static final int DATE_FROM_DIALOG_ID = 0;
	private static final int DATE_TO_DIALOG_ID = 1;

	private List<org.zv.activlog.model.entity.Activity> activities;
	private List<ActivityAttribute> activityAttributes;

	private TextView dateFrom;
	private TextView dateTo;
    private DatePickerDialog.OnDateSetListener dateFromSetListener;
    private DatePickerDialog.OnDateSetListener dateToSetListener;
	private Calendar calendarFrom = Calendar.getInstance();
	private Calendar calendarTo = Calendar.getInstance();

	private Spinner spinnerActivity;
	private Spinner spinnerAttribute;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);

		calendarFrom.set(Calendar.DAY_OF_MONTH, 1);

        dateFrom = (TextView)findViewById(R.id.statistics_date_from_textView);
        dateFrom.setText(Utils.dateToString(calendarFrom.getTime()));
        dateFrom.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			 	showDialog(DATE_FROM_DIALOG_ID);
			}
        });
		
        dateFromSetListener = new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				calendarFrom.set(Calendar.YEAR, year);
				calendarFrom.set(Calendar.MONTH, monthOfYear);
				calendarFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		        dateFrom.setText(Utils.dateToString(calendarFrom.getTime()));
			}
		};
        
        dateTo = (TextView)findViewById(R.id.statistics_date_to_textView);
        dateTo.setText(Utils.dateToString(calendarTo.getTime()));
        dateTo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			 	showDialog(DATE_TO_DIALOG_ID);
			}
        });
		
        dateToSetListener = new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				calendarTo.set(Calendar.YEAR, year);
				calendarTo.set(Calendar.MONTH, monthOfYear);
				calendarTo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		        dateTo.setText(Utils.dateToString(calendarTo.getTime()));
			}
		};

        spinnerActivity = (Spinner)findViewById(R.id.statistics_activity_spinner);
		spinnerActivity.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				org.zv.activlog.model.entity.Activity activity = activities.get(position);
				setupSpinnerAttributeAdapter(activity);
			}
			public void onNothingSelected(AdapterView<?> parent) {
				setupSpinnerAttributeAdapter(null);
			}
		});
		
        spinnerAttribute = (Spinner)findViewById(R.id.statistics_attribute_spinner);

		Button table = (Button) this.findViewById(R.id.statistics_table_button);
		table.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				displayTable();
			}
		});

		Button chart = (Button) this.findViewById(R.id.statistics_chart_button);
		chart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				displayChart();
			}
		});
	
        registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String event = intent.getStringExtra(Constants.BRADCAST_EVENT); 
				if (Constants.BRADCAST_EVENT_DATABASE.equals(event) ||
					Constants.BRADCAST_EVENT_ACTIVITY.equals(event) || 
					Constants.BRADCAST_EVENT_ACTIVITY_ATTRIBUTE.equals(event)) {
					setupSpinnerActivityAdapter();
			        setupSpinnerAttributeAdapter(null);
				} 
			}
		}, new IntentFilter(Constants.BRADCAST_ACTION));
		
		setupSpinnerActivityAdapter();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_FROM_DIALOG_ID:
	    	return new DatePickerDialog(this, dateFromSetListener, 
	        	calendarFrom.get(Calendar.YEAR), 
	        	calendarFrom.get(Calendar.MONTH), 
	        	calendarFrom.get(Calendar.DAY_OF_MONTH));
	    case DATE_TO_DIALOG_ID:
	        return new DatePickerDialog(this, dateToSetListener, 
	        	calendarTo.get(Calendar.YEAR), 
	        	calendarTo.get(Calendar.MONTH), 
	        	calendarTo.get(Calendar.DAY_OF_MONTH));
	    }
	    return null;
	}
	
	private void displayTable() {
		String dateFrom = this.dateFrom.getText().toString();
		if (!Utils.isValidDate(dateFrom)) {
			showToast("Date From field is not valid", true);
			return;
		}

		String dateTo = this.dateTo.getText().toString();
		if (!Utils.isValidDate(dateTo)) {
			showToast("Date To field is not valid", true);
			return;
		}

		int dateDiff = Utils.dateDiff(dateFrom, dateTo);
		if (dateDiff < 0) {
			showToast("Date range is not valid", true);
			return;
		}
		
		int pos = spinnerActivity.getSelectedItemPosition();
		if (pos == Spinner.INVALID_POSITION) {
			return;
		}
		org.zv.activlog.model.entity.Activity activity = activities.get(pos);
		int activityId = activity.activityId;
		
		ActivityAttribute activityAttribute = null;
		pos = spinnerAttribute.getSelectedItemPosition();
		if (pos != Spinner.INVALID_POSITION) {
			activityAttribute = activityAttributes.get(pos);
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String measurementUnits = prefs.getString("measurement_units", "Km");
		
		String heading, units = "?", datePattern;
		boolean byDate = dateDiff > 100;
		datePattern = Utils.getDateFormatString(byDate);
		heading = "Date (" + datePattern.toLowerCase() + ")"; 
		
		if (activityAttribute != null) {
			switch (activityAttribute.type) {
			case ActivityAttribute.TYPE_DISTANCE:
				units = measurementUnits; 
				break;
			case ActivityAttribute.TYPE_DURATION:
				units = "Min"; 
				break;
			case ActivityAttribute.TYPE_RATING:	
				units = "Stars"; 
				break;
			case ActivityAttribute.TYPE_NUMERIC:
				units = "X"; 
				break;
			}
		} else {
			units = "Count"; 
		}
		
		List<Date> dates = new ArrayList<Date>(); 
		Map<String, Double []> values = new HashMap<String, Double []>();				

		StatisticsDAO statisticsDAO = new StatisticsDAO();
		if (activityAttribute != null) {
			statisticsDAO.groupByDate(byDate, Utils.stringToYYYYMMDD(dateFrom), Utils.stringToYYYYMMDD(dateTo), activityId, activityAttribute.activityAttributeId, dates, values);
		} else {
			statisticsDAO.groupByDate(byDate, Utils.stringToYYYYMMDD(dateFrom), Utils.stringToYYYYMMDD(dateTo), activityId, dates, values);
		}

		ActivityLoggerApplication.getInstance().setResultsDates(dates);
		ActivityLoggerApplication.getInstance().setResultsValues(values);
		
		Intent intent = new Intent(this, StatsTableActivity.class);
		if (activityAttribute != null) {
			intent.putExtra("title", activityAttribute.name + " values in " + units);
			intent.putExtra("allFields", "true");
		} else {
			intent.putExtra("title", "Count values of " + activity.name);
		}
		intent.putExtra("heading", heading);
		intent.putExtra("datePattern", datePattern);
		startActivity(intent);
	}
	
	private void displayChart() {
		String dateFrom = this.dateFrom.getText().toString();
		if (!Utils.isValidDate(dateFrom)) {
			showToast("Date From field is not valid", true);
			return;
		}

		String dateTo = this.dateTo.getText().toString();
		if (!Utils.isValidDate(dateTo)) {
			showToast("Date To field is not valid", true);
			return;
		}

		int dateDiff = Utils.dateDiff(dateFrom, dateTo);
		if (dateDiff < 0) {
			showToast("Date range is not valid", true);
			return;
		}
		
		int pos = spinnerActivity.getSelectedItemPosition();
		if (pos == Spinner.INVALID_POSITION) {
			return;
		}
		org.zv.activlog.model.entity.Activity activity = activities.get(pos);
		int activityId = activity.activityId;
		
		ActivityAttribute activityAttribute = null;
		pos = spinnerAttribute.getSelectedItemPosition();
		if (pos != Spinner.INVALID_POSITION) {
			activityAttribute = activityAttributes.get(pos);
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String measurementUnits = prefs.getString("measurement_units", "Km");
		
		String xTitle, yTitle = "?", datePattern;
		boolean byDate = dateDiff > 100;
		datePattern = Utils.getDateFormatString(byDate);
		xTitle = "Date (" + datePattern.toLowerCase() + ")"; 
		
		if (activityAttribute != null) {
			switch (activityAttribute.type) {
			case ActivityAttribute.TYPE_DISTANCE:
				yTitle = measurementUnits; 
				break;
			case ActivityAttribute.TYPE_DURATION:
				yTitle = "Min"; 
				break;
			case ActivityAttribute.TYPE_RATING:	
				yTitle = "Stars"; 
				break;
			case ActivityAttribute.TYPE_NUMERIC:
				yTitle = "X"; 
				break;
			}
		} else {
			yTitle = "Count"; 
		}
		
		List<Date> dates = new ArrayList<Date>(); 
		Map<String, Double []> values = new HashMap<String, Double []>();

		StatisticsDAO statisticsDAO = new StatisticsDAO();
		if (activityAttribute != null) {
			statisticsDAO.groupByDate(byDate, Utils.stringToYYYYMMDD(dateFrom), Utils.stringToYYYYMMDD(dateTo), activityId, activityAttribute.activityAttributeId, dates, values);
		} else {
			statisticsDAO.groupByDate(byDate, Utils.stringToYYYYMMDD(dateFrom), Utils.stringToYYYYMMDD(dateTo), activityId, dates, values);
		}
		
		List<ChartService.SeriesData> seriesData = new ArrayList<ChartService.SeriesData>();
		if (activityAttribute != null) {
			seriesData.add(new ChartService.SeriesData("Min", Color.BLUE, values.get("min")));
			seriesData.add(new ChartService.SeriesData("Max", Color.RED, values.get("max")));
			seriesData.add(new ChartService.SeriesData("Avg", Color.MAGENTA, values.get("avg")));
		} else {
			seriesData.add(new ChartService.SeriesData("Cnt", Color.GREEN, values.get("cnt")));
		}

		ChartService chartService = new ChartService(this);
		String title = activityAttribute != null ? activityAttribute.name : activity.name;
		Intent intent = chartService.createTimeChartIntent(title, xTitle, yTitle, dates, seriesData, datePattern);
		startActivity(intent);
	}

	private void setupSpinnerActivityAdapter() {
		activities = new ArrayList<org.zv.activlog.model.entity.Activity>(); 
		for (org.zv.activlog.model.entity.Activity activity : ActivityLoggerApplication.getInstance().getActivities()) {
			if (activity.enabled) {
				activities.add(activity);
			}
		}
		ArrayAdapter<org.zv.activlog.model.entity.Activity> spinner_adapter = new ArrayAdapter<org.zv.activlog.model.entity.Activity>(this, android.R.layout.simple_spinner_item, activities);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivity.setAdapter(spinner_adapter);
	}

	private void setupSpinnerAttributeAdapter(org.zv.activlog.model.entity.Activity activity) {
		activityAttributes = new ArrayList<ActivityAttribute>();
		if (activity != null) {
			for (ActivityAttribute activityAttribute : activity.getActivityAttributes()) {
				if (activityAttribute.enabled) {
					activityAttributes.add(activityAttribute);
				}
			}
		}
		ArrayAdapter<ActivityAttribute> spinner_adapter = new ArrayAdapter<ActivityAttribute>(this, android.R.layout.simple_spinner_item, activityAttributes);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAttribute.setAdapter(spinner_adapter);
	}

	private void showToast(String text, boolean isError) {
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
		v.setTextColor(isError ? Color.RED : Color.GREEN);
		toast.show();
	}
}
