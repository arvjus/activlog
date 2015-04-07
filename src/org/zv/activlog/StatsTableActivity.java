package org.zv.activlog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class StatsTableActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats_table);

		String title = getIntent().getStringExtra("title"); 
		String heading = getIntent().getStringExtra("heading"); 
		String datePattern = getIntent().getStringExtra("datePattern");
		String allFields = getIntent().getStringExtra("allFields");

		TextView textView = (TextView)findViewById(R.id.stats_table_textView);
		textView.setText(title);
		
		ListView listView = (ListView)findViewById(R.id.stats_table_listView);
		listView.setFocusable(false);
		listView.setClickable(false);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);

		List<Date> resultsDates = ActivityLoggerApplication.getInstance().getResultsDates(); 
		Map<String, Double []> resultsValues = ActivityLoggerApplication.getInstance().getResultsValues(); 				
				
		Double [] cnt = resultsValues.get("cnt");  
		Double [] sum = resultsValues.get("sum");  
		Double [] min = resultsValues.get("min");  
		Double [] max = resultsValues.get("max");  
		Double [] avg = resultsValues.get("avg");  
		
        Map<String,String> item;
        ArrayList<Map<String,String>> items = new ArrayList<Map<String,String>>();

        for (int i = 0; i < resultsDates.size(); i++) {
			if (i > 0) {
	            item = new HashMap<String,String>();
	            item.put("value1", "");
	            item.put("value2", "");
	            items.add(item);
			}
            
			item = new HashMap<String,String>();
            item.put("value1", heading);
            item.put("value2", dateFormat.format(resultsDates.get(i)));
            items.add(item);

            item = new HashMap<String,String>();
            item.put("value1", "Count");
            item.put("value2", "" + cnt[i].longValue());
            items.add(item);

    		if ("true".equals(allFields)) {
                item = new HashMap<String,String>();
                item.put("value1", "Sum");
                item.put("value2", "" + sum[i]);
                items.add(item);

                item = new HashMap<String,String>();
                item.put("value1", "Min");
                item.put("value2", "" + min[i]);
                items.add(item);

                item = new HashMap<String,String>();
                item.put("value1", "Max");
                item.put("value2", "" + max[i]);
                items.add(item);

                item = new HashMap<String,String>();
                item.put("value1", "Avg");
                item.put("value2", "" + avg[i]);
                items.add(item);
    		}
		}
        
        listView.setAdapter(new SimpleAdapter(this, items, R.layout.stats_table_item, 
        		new String[] { "value1", "value2" }, 
				new int[] { R.id.stats_table_item_value1_textView, R.id.stats_table_item_value2_textView }));
	}
}
