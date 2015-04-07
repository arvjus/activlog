package org.zv.activlog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;

import org.zv.activlog.model.dao.ActivityAttributeLogDAO;
import org.zv.activlog.model.dao.ActivityLogDAO;
import org.zv.activlog.model.dao.DbHelper;
import org.zv.activlog.model.entity.ActivityAttribute;
import org.zv.activlog.model.entity.ActivityLog;
import org.zv.activlog.model.entity.ActivityAttributeLog;
import org.zv.activlog.util.Utils;

public class LoggingActivity extends Activity {
	private static final int DATE_DIALOG_ID = 0;
	
	private List<org.zv.activlog.model.entity.Activity> activities;
	private Spinner spinner;
	private TableLayout tableLayout;
	private Map<Integer, View> views = new HashMap<Integer, View>();

	private TextView date;
	
	private DatePickerDialog.OnDateSetListener dateSetListener;
	private Calendar calendar = Calendar.getInstance();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logging);

        spinner = (Spinner)findViewById(R.id.logging_spinner);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				org.zv.activlog.model.entity.Activity activity = activities.get(position);
				setupAcivityViews(activity);
			}
			public void onNothingSelected(AdapterView<?> parent) {
				setupAcivityViews(null);				
			}
		});
		
        date = (TextView)findViewById(R.id.logging_date_textView);
        date.setText(Utils.dateToString(calendar.getTime()));
        date.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			 	showDialog(DATE_DIALOG_ID);
			}
        });

		dateSetListener = new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		        date.setText(Utils.dateToString(calendar.getTime()));
			}
		};

        Button save = (Button)findViewById(R.id.logging_save_button);
        save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int position = spinner.getSelectedItemPosition();
				if (position == Spinner.INVALID_POSITION) {
					return;
				}

				final org.zv.activlog.model.entity.Activity activity = activities.get(position);
				String string;

				final ActivityLog activityLog = new ActivityLog();
				final List<ActivityAttributeLog> activityAttributeLogs = new ArrayList<ActivityAttributeLog>(); 

				// validate, build log entities  
				string = date.getText().toString();
				if (!Utils.isValidDate(string)) {
					LoggingActivity.this.showToast("Date field is not valid", true);
					return;
				}
				
				activityLog.activityId = activity.activityId;
				activityLog.createDate = Utils.stringToYYYYMMDD(string);
				
				for (Map.Entry<Integer, View> viewsEntry : views.entrySet()) {
					Integer activityAttributeId = viewsEntry.getKey();
					View view = viewsEntry.getValue();
					
					ActivityAttributeLog activityAttributeLog = new ActivityAttributeLog();
					activityAttributeLog.activityAttributeId = activityAttributeId;

					if (view instanceof RatingBar) {
						RatingBar ratingBar = (RatingBar)view;
						activityAttributeLog.value = "" + ratingBar.getRating();
					} else if (view instanceof EditText) {
						EditText editText = (EditText)view;
						activityAttributeLog.value = editText.getText().toString();
					}

					if (activityAttributeLog.value != null && activityAttributeLog.value.length() > 0) {
						activityAttributeLogs.add(activityAttributeLog);
					}
				}

				// show confirmation dialog
		        AlertDialog.Builder builder = new AlertDialog.Builder(LoggingActivity.this);
				builder.setMessage("Activity event will be logged").setCancelable(false);
				builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// save
						final DbHelper dbHelper = ActivityLoggerApplication.getInstance().getDbHelper();
						try {
							dbHelper.beginTransaction();
		
							ActivityLogDAO activityLogDAO = new ActivityLogDAO();
							activityLogDAO.save(activityLog);
							
							ActivityAttributeLogDAO activityAttributeLogDAO = new ActivityAttributeLogDAO();
							for (ActivityAttributeLog activityAttributeLog : activityAttributeLogs) {
								activityAttributeLog.activityLogId = activityLog.activityLogId; 
								activityAttributeLogDAO.save(activityAttributeLog);
							}
							
							dbHelper.setTransactionSuccessful();
						} finally {
							dbHelper.endTransaction();
						}
		
						// reset views
				        date.setText(Utils.dateToString(calendar.getTime()));
						setupAcivityViews(activity);
		
						LoggingActivity.this.showToast("Activity envent was added to database", false);
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
        });

		tableLayout = (TableLayout)findViewById(R.id.logging_tableLayout);
		
        registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String event = intent.getStringExtra(Constants.BRADCAST_EVENT); 
				if (Constants.BRADCAST_EVENT_DATABASE.equals(event) ||
					Constants.BRADCAST_EVENT_ACTIVITY.equals(event) || 
					Constants.BRADCAST_EVENT_ACTIVITY_ATTRIBUTE.equals(event)) {
					setupSpinnerAdapter();
			        setupAcivityViews(null);
				} 
			}
		}, new IntentFilter(Constants.BRADCAST_ACTION));
		
		setupSpinnerAdapter();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_DIALOG_ID:
	    	return new DatePickerDialog(this, dateSetListener, 
	        	calendar.get(Calendar.YEAR), 
	        	calendar.get(Calendar.MONTH), 
	        	calendar.get(Calendar.DAY_OF_MONTH));
	    }
	    return null;
	}

	private void setupAcivityViews(org.zv.activlog.model.entity.Activity activity) {
		for (int i = tableLayout.getChildCount() - 1; i >= 1; i--) {
			tableLayout.removeViewAt(i);
		}
		views.clear();
		
		if (activity == null) {
			return;
		}
		
		for (final ActivityAttribute activityAttribute : activity.getActivityAttributes()) {
			if (activityAttribute.enabled) {
				if (activityAttribute.type == ActivityAttribute.TYPE_RATING) {
					TableRow tableRow = (TableRow) View.inflate(this, R.layout.table_row_rating, null);

			        TextView textView = (TextView)tableRow.findViewById(R.id.table_row_textView);
			        textView.setText(activityAttribute.name);

			        RatingBar ratingBar = (RatingBar)tableRow.findViewById(R.id.table_row_ratingBar);
					if (activityAttribute.defaultValue != null && Utils.isValidNumerical(activityAttribute.defaultValue)) {
						ratingBar.setRating(Float.valueOf(activityAttribute.defaultValue));
					}
			        views.put(activityAttribute.activityAttributeId, ratingBar);

					tableLayout.addView(tableRow);
				} else {
					TableRow tableRow = (TableRow) View.inflate(this, R.layout.table_row_edit, null);

			        TextView textView = (TextView)tableRow.findViewById(R.id.table_row_textView);
			        textView.setText(activityAttribute.name);

			        EditText editText = (EditText)tableRow.findViewById(R.id.table_row_editText);
					editText.setLayoutParams(date.getLayoutParams());
					if (activityAttribute.type == ActivityAttribute.TYPE_DISTANCE || 
						activityAttribute.type == ActivityAttribute.TYPE_DURATION || 
						activityAttribute.type == ActivityAttribute.TYPE_NUMERIC) {
						
						editText.setInputType(InputType.TYPE_CLASS_NUMBER);
						if (activityAttribute.defaultValue != null && Utils.isValidNumerical(activityAttribute.defaultValue)) {
							editText.setText(activityAttribute.defaultValue);
						}
					} else if (activityAttribute.defaultValue != null) {
						editText.setText(activityAttribute.defaultValue);
					}
			        views.put(activityAttribute.activityAttributeId, editText);
					
					tableLayout.addView(tableRow);
				}
			}
		}
	}
	
	private void setupSpinnerAdapter() {
		activities = new ArrayList<org.zv.activlog.model.entity.Activity>(); 
		for (org.zv.activlog.model.entity.Activity activity : ActivityLoggerApplication.getInstance().getActivities()) {
			if (activity.enabled) {
				activities.add(activity);
			}
		}
		ArrayAdapter<org.zv.activlog.model.entity.Activity> spinner_adapter = new ArrayAdapter<org.zv.activlog.model.entity.Activity>(this, android.R.layout.simple_spinner_item, activities);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
	}

	private void showToast(String text, boolean isError) {
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
		v.setTextColor(isError ? Color.RED : Color.GREEN);
		toast.show();
	}
}
