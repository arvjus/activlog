package org.zv.activlog;

import org.zv.activlog.util.Utils;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends Activity {
	private LocalActivityManager localActivityManager;
	private TabHost tabHost;
	private TabWidget tabWidget;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ActivityLoggerApplication.getInstance().initDbHelper();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String defaultFormat = prefs.getString("date_format", Utils.YYYYMMDD);
		Utils.setDefaultFormat(defaultFormat);
		
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
	    localActivityManager = new LocalActivityManager(this, false);
	    tabHost.setup(localActivityManager);
	    localActivityManager.dispatchCreate(savedInstanceState);
	    tabWidget = (TabWidget) findViewById(android.R.id.tabs);
	    tabWidget.setDividerDrawable(R.drawable.tab_divider);
		
	    View tabview;
	    TextView textView;
	    ImageView imageView;
	    Intent intent;
	    TabSpec spec;
	    
		tabview = LayoutInflater.from(this).inflate(R.layout.tabs_bg, tabWidget, false);
		imageView = (ImageView) tabview.findViewById(R.id.tabImage);
		imageView.setImageResource(R.drawable.logging);
		textView = (TextView) tabview.findViewById(R.id.tabText);
		textView.setText("Logging");
		intent = new Intent().setClass(this, LoggingActivity.class);
	    spec = tabHost.newTabSpec("logging").setIndicator(tabview).setContent(intent);
		tabHost.addTab(spec);
	    
		tabview = LayoutInflater.from(this).inflate(R.layout.tabs_bg, tabWidget, false);
		imageView = (ImageView) tabview.findViewById(R.id.tabImage);
		imageView.setImageResource(R.drawable.statistics);
		textView = (TextView) tabview.findViewById(R.id.tabText);
		textView.setText("Statistics");
		intent = new Intent().setClass(this, StatisticsActivity.class);
	    spec = tabHost.newTabSpec("statistics").setIndicator(tabview).setContent(intent);
		tabHost.addTab(spec);
	    
		tabview = LayoutInflater.from(this).inflate(R.layout.tabs_bg, tabWidget, false);
		imageView = (ImageView) tabview.findViewById(R.id.tabImage);
		imageView.setImageResource(R.drawable.activities);
		textView = (TextView) tabview.findViewById(R.id.tabText);
		textView.setText("Activities");
		intent = new Intent().setClass(this, ActivitiesActivity.class);
	    spec = tabHost.newTabSpec("activities").setIndicator(tabview).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_menu_prefs_item:
	        startActivity(new Intent(this, PreferencesActivity.class));
			return true;
		case R.id.main_menu_dbadmin_item:
	        startActivity(new Intent(this, DbAdminActivity.class));
			return true;
		case R.id.main_menu_about_item:
	        startActivity(new Intent(this, AboutActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDestroy() {
		ActivityLoggerApplication.getInstance().closeDbHelper();

		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		localActivityManager.dispatchPause(isFinishing());
	}

	@Override
	protected void onResume() {
		super.onResume();
		localActivityManager.dispatchResume();	
	}
}