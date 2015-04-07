package org.zv.activlog;

import java.util.ArrayList;
import java.util.List;

import org.zv.activlog.model.dao.ActivityAttributeDAO;
import org.zv.activlog.model.dao.ActivityDAO;
import org.zv.activlog.model.dao.DbHelper;
import org.zv.activlog.model.entity.ActivityAttribute;
import org.zv.activlog.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ActivitiesActivity extends Activity {
	private ActivityDAO activityDAO = new ActivityDAO();
	private ActivityAttributeDAO activityAttributeDAO = new ActivityAttributeDAO();

	private List<org.zv.activlog.model.entity.Activity> activities;
	private List<ActivityAttribute> activityAttributes;

	private Spinner spinnerActivity;
	private EditText nameActivity;
	private CheckBox invisibleActivity;
	private Button saveActivity;
	private Button deleteActivity;

	private TextView separator;
	private LinearLayout layout1Attribute;
	private LinearLayout layout2Attribute;
	
	private Spinner spinnerAttribute;
	private EditText nameAttribute;
	private Spinner typeAttribute;
	private EditText defaultAttribute;
	private CheckBox invisibleAttribute;
	private Button saveAttribute;
	private Button deleteAttribute;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activities);

        spinnerActivity = (Spinner)findViewById(R.id.activities_activity_spinner);
		spinnerActivity.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				org.zv.activlog.model.entity.Activity activity = activities.get(position);
				if (activity.activityId != 0) {
					nameActivity.setText(activity.name);
					invisibleActivity.setChecked(!activity.enabled);
					deleteActivity.setClickable(true);
					
					separator.setVisibility(View.VISIBLE);				
					layout1Attribute.setVisibility(View.VISIBLE);				
					layout2Attribute.setVisibility(View.VISIBLE);				
					layout1Attribute.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
					layout2Attribute.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				} else {
					nameActivity.setText("");
					invisibleActivity.setChecked(false);
					deleteActivity.setClickable(false);
					
					separator.setVisibility(View.INVISIBLE);				
					layout1Attribute.setVisibility(View.INVISIBLE);				
					layout2Attribute.setVisibility(View.INVISIBLE);				
					layout1Attribute.getLayoutParams().height = 1;
					layout2Attribute.getLayoutParams().height = 1;
				}
				setupSpinnerAttributeAdapter(activity);
			}
			public void onNothingSelected(AdapterView<?> parent) {
				setupSpinnerAttributeAdapter(null);
			}
		});
		
		nameActivity = (EditText)findViewById(R.id.activities_activity_name_editText);
		invisibleActivity = (CheckBox)findViewById(R.id.activities_activity_invisible_checkBox);

		saveActivity = (Button)findViewById(R.id.activities_activity_save_button);
		saveActivity.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				saveActivity();
			}
		});

		deleteActivity = (Button)findViewById(R.id.activities_activity_delete_button);
		deleteActivity.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				deleteActivity();
			}
		});

		separator = (TextView)findViewById(R.id.activities_separator_textView);
		layout1Attribute = (LinearLayout)findViewById(R.id.activities_attribute1_linearLayout);
		layout2Attribute = (LinearLayout)findViewById(R.id.activities_attribute2_linearLayout);
		
        spinnerAttribute = (Spinner)findViewById(R.id.activities_attribute_spinner);
		spinnerAttribute.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				ActivityAttribute activityAttribute = activityAttributes.get(position);
				setupActivityAttributeViews(activityAttribute);
			}
			public void onNothingSelected(AdapterView<?> parent) {
				setupActivityAttributeViews(null);
			}
		});
		
		nameAttribute = (EditText)findViewById(R.id.activities_attribute_name_editText);
        
		typeAttribute = (Spinner)findViewById(R.id.activities_attribute_type_spinner);
        typeAttribute.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				int type = position; // it's dirty, but works
				if (type == ActivityAttribute.TYPE_DISTANCE || 
    				type == ActivityAttribute.TYPE_DURATION || 
    				type == ActivityAttribute.TYPE_NUMERIC  ||
    				type == ActivityAttribute.TYPE_RATING) {
					defaultAttribute.setInputType(InputType.TYPE_CLASS_NUMBER);
    			} else {
					defaultAttribute.setInputType(InputType.TYPE_CLASS_TEXT);
    			}
			}
			public void onNothingSelected(AdapterView<?> parent) {
				setupActivityAttributeViews(null);
			}
		});
        ArrayAdapter<CharSequence> typeSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.type_spinner_entries, android.R.layout.simple_spinner_item);
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeAttribute.setAdapter(typeSpinnerAdapter);
        
		defaultAttribute = (EditText)findViewById(R.id.activities_attribute_default_editText);
		invisibleAttribute = (CheckBox)findViewById(R.id.activities_attribute_invisible_checkBox);
		saveAttribute = (Button)findViewById(R.id.activities_attribute_save_button);
		
		saveAttribute.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				saveAttribute();
			}
		});

		deleteAttribute = (Button)findViewById(R.id.activities_attribute_delete_button);
		deleteAttribute.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				deleteAttribute();
			}
		});
		
        registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String event = intent.getStringExtra(Constants.BRADCAST_EVENT); 
				if (Constants.BRADCAST_EVENT_DATABASE.equals(event)) {
					setupSpinnerActivityAdapter();
				} 
			}
		}, new IntentFilter(Constants.BRADCAST_ACTION));
		
		setupSpinnerActivityAdapter();
	}


	private void setupSpinnerActivityAdapter() {
		activities = new ArrayList<org.zv.activlog.model.entity.Activity>();
		org.zv.activlog.model.entity.Activity activity = new org.zv.activlog.model.entity.Activity();
		activity.name = "< New Activity >";
		activities.add(activity);
		activities.addAll(ActivityLoggerApplication.getInstance().getActivities());

		ArrayAdapter<org.zv.activlog.model.entity.Activity> spinner_adapter = new ArrayAdapter<org.zv.activlog.model.entity.Activity>(this, android.R.layout.simple_spinner_item, activities);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivity.setAdapter(spinner_adapter);
	}

	private void setupSpinnerAttributeAdapter(org.zv.activlog.model.entity.Activity activity) {
		activityAttributes = new ArrayList<ActivityAttribute>();
		ActivityAttribute activityAttribute = new ActivityAttribute();
		activityAttribute.name = "< New Attribute >";
		activityAttributes.add(activityAttribute);
		if (activity != null && activity.getActivityAttributes() != null) {
			activityAttributes.addAll(activity.getActivityAttributes());
		}
		ArrayAdapter<ActivityAttribute> spinner_adapter = new ArrayAdapter<ActivityAttribute>(this, android.R.layout.simple_spinner_item, activityAttributes);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAttribute.setAdapter(spinner_adapter);
	}

	private void setupActivityAttributeViews(ActivityAttribute activityAttribute) {
		if (activityAttribute != null && activityAttribute.activityAttributeId != 0) {
	    	nameAttribute.setText(activityAttribute.name);
	    	typeAttribute.setSelection(activityAttribute.type);
	    	defaultAttribute.setText(activityAttribute.defaultValue);
	    	invisibleAttribute.setChecked(!activityAttribute.enabled);
			deleteAttribute.setClickable(true);
		} else {
	    	nameAttribute.setText("");
	    	typeAttribute.setSelection(0);
	    	defaultAttribute.setText("");
	    	invisibleAttribute.setChecked(false);
			deleteAttribute.setClickable(false);
		}
	}

	private void saveActivity() {
		org.zv.activlog.model.entity.Activity activity = activities.get(spinnerActivity.getSelectedItemPosition());
		boolean isNewActivity = activity.activityId == 0; 
		
		String name = nameActivity.getText().toString();
		if (name == null || name.trim().length() == 0) {
			showToast("Name is required", true);
			return;
		} else {
			for (org.zv.activlog.model.entity.Activity activity_ : activities) {
				if (name.equals(activity_.name) && (isNewActivity || activity_.activityId != activity.activityId)) {
					showToast("Name already exist", true);
					return;
				}
			}
		}
		activity.name = name;
		activity.enabled = !invisibleActivity.isChecked();

		// save
		final DbHelper dbHelper = ActivityLoggerApplication.getInstance().getDbHelper();
		try {
			dbHelper.beginTransaction();

			if (activityDAO.save(activity)) {
				showToast("Activity has been successfully " + (isNewActivity ? "saved" : "updated"), false);
			} else {
				showToast("An error occurred", true);
			}

			dbHelper.setTransactionSuccessful();
		} finally {
			dbHelper.endTransaction();
		}

		// re-cache & reset views
		ActivityLoggerApplication.getInstance().cacheDatabase(0, false);
		setupSpinnerActivityAdapter();

		// select current item
		int position = 0;
		for (org.zv.activlog.model.entity.Activity activity_ : activities) {
			if (isNewActivity && name.equals(activity_.name)) {
				spinnerActivity.setSelection(position);
				break;
			} else if (!isNewActivity && activity.activityId == activity_.activityId) {
				spinnerActivity.setSelection(position, true);
				break;
			}
			position ++;
		}
	}
	
	private void deleteActivity() {
		// show confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Selected activity with all referred attributes and log events will be deleted!").setCancelable(false);
		builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// delete
				final DbHelper dbHelper = ActivityLoggerApplication.getInstance().getDbHelper();
				try {
					dbHelper.beginTransaction();

					org.zv.activlog.model.entity.Activity activity = activities.get(spinnerActivity.getSelectedItemPosition());
					if (activityDAO.delete(activity.activityId)) {
						showToast("Activity has been successfully deleted", false);
					} else {
						showToast("An error occurred", true);
					}
					
					dbHelper.setTransactionSuccessful();
				} finally {
					dbHelper.endTransaction();
				}

				// re-cache & reset views
				ActivityLoggerApplication.getInstance().cacheDatabase(0, false);
				setupSpinnerActivityAdapter();
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

	private void saveAttribute() {
		org.zv.activlog.model.entity.Activity activity = activities.get(spinnerActivity.getSelectedItemPosition());
		ActivityAttribute activityAttribute = activityAttributes.get(spinnerAttribute.getSelectedItemPosition());
		boolean isNewAttribute = activityAttribute.activityAttributeId == 0; 
		
		String name = nameAttribute.getText().toString();
		if (name == null || name.trim().length() == 0) {
			showToast("Name is required", true);
			return;
		} else {
			for (ActivityAttribute activityAttribute_ : activityAttributes) {
				if (name.equals(activityAttribute_.name) && (isNewAttribute || activityAttribute_.activityAttributeId != activityAttribute.activityAttributeId)) {
					showToast("Name already exist", true);
					return;
				}
			}
		}
		if (typeAttribute.getSelectedItemPosition() == 0) {
			showToast("Type selection is required", true);
			return;
		}
		
		activityAttribute.activityId = activity.activityId;
		activityAttribute.name = name;
		activityAttribute.type = typeAttribute.getSelectedItemPosition();
		activityAttribute.defaultValue = defaultAttribute.getText().toString();
		activityAttribute.enabled = !invisibleAttribute.isChecked();
		
		if (activityAttribute.type == ActivityAttribute.TYPE_RATING && activityAttribute.defaultValue != null && activityAttribute.defaultValue.length() > 0 && (
			!Utils.isValidNumerical(activityAttribute.defaultValue) || 
			Integer.valueOf(activityAttribute.defaultValue) < 0 ||
			Integer.valueOf(activityAttribute.defaultValue) > 5)) {
			
			showToast("Rating value must be in a range 0-5", true);
			return;
		}

		final DbHelper dbHelper = ActivityLoggerApplication.getInstance().getDbHelper();
		try {
			dbHelper.beginTransaction();

			if (activityAttributeDAO.save(activityAttribute)) {
				showToast("Activity attribute has been successfully " + (isNewAttribute ? "saved" : "updated"), false);
			} else {
				showToast("An error occurred", true);
			}
			
			dbHelper.setTransactionSuccessful();
		} finally {
			dbHelper.endTransaction();
		}

		// re-cache & reset views
		ActivityLoggerApplication.getInstance().cacheDatabase(activity.activityId, false);
		setupSpinnerAttributeAdapter(activity);
		
		// select current item
		int position = 0;
		for (ActivityAttribute activityAttribute_ : activityAttributes) {
			if (isNewAttribute && name.equals(activityAttribute_.name)) {
				spinnerAttribute.setSelection(position);
				break;
			} else if (!isNewAttribute && activityAttribute.activityAttributeId == activityAttribute_.activityAttributeId) {
				spinnerAttribute.setSelection(position, true);
				break;
			}
			position ++;
		}
	}

	private void deleteAttribute() {
		// show confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Selected activity attribute with all referred log events will be deleted!").setCancelable(false);
		builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// delete
				final DbHelper dbHelper = ActivityLoggerApplication.getInstance().getDbHelper();
				try {
					dbHelper.beginTransaction();

					ActivityAttribute activityAttribute = activityAttributes.get(spinnerAttribute.getSelectedItemPosition());
					if (activityAttributeDAO.delete(activityAttribute.activityAttributeId)) {
						showToast("Activity attribute has been successfully deleted", false);
					} else {
						showToast("An error occurred", true);
					}
					
					dbHelper.setTransactionSuccessful();
				} finally {
					dbHelper.endTransaction();
				}

				// re-cache & reset views
				org.zv.activlog.model.entity.Activity activity = activities.get(spinnerActivity.getSelectedItemPosition());
				ActivityLoggerApplication.getInstance().cacheDatabase(activity.activityId, false);
				setupSpinnerAttributeAdapter(activity);
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
	
	private void showToast(String text, boolean isError) {
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
		v.setTextColor(isError ? Color.RED : Color.GREEN);
		toast.show();
	}
}
