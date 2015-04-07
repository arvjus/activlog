package org.zv.activlog;

import org.zv.activlog.util.Utils;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        //setTheme(R.style.preferences);
        //findViewById(android.R.id.list).setBackgroundColor(Color.WHITE);        
        
    	SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
    		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    			if ("date_format".equals(key)) {
        			String value = sharedPreferences.getString(key, null);
        			if (value != null) {
            			Utils.setDefaultFormat(value);
        			}
    			}
    		}
    	};
		
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(listener);
	}
}
