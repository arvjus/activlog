package org.zv.activlog;

import java.io.IOException;

import org.zv.activlog.model.dao.DbHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DbAdminActivity extends Activity {
	private Spinner spinner;
	private String [] backupFiles;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dbadmin);

		final DbHelper dbHelper = ActivityLoggerApplication.getInstance().getDbHelper();
		
	    final CheckBox timestampCheckBox = (CheckBox)findViewById(R.id.dbadmin_timestamp_checkBox);
	    timestampCheckBox.setChecked(true);
		
	    Button backup_button = (Button)findViewById(R.id.dbadmin_backup_button);
	    backup_button.setClickable(dbHelper.isAbleToBackup());
	    backup_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					dbHelper.backup(timestampCheckBox.isChecked());
					showToast("Database has been backed up", false);
				} catch (IOException e) {
					showToast("Database backup failed, " + e.getMessage(), true);
				}
				ActivityLoggerApplication.getInstance().cacheDatabase(0, true);
				setupSpinnerAdapter();
			}
	    });

        TextView backupDir = (TextView)findViewById(R.id.dbadmin_backup_dir_textView);
        backupDir.setText(dbHelper.isAbleToBackup() ? dbHelper.getBackupDir().getAbsolutePath() : "N/A");

        spinner = (Spinner)findViewById(R.id.dbadmin_backup_spinner);

		Button restore_button = (Button)findViewById(R.id.dbadmin_restore_button);
	    restore_button.setClickable(dbHelper.isAbleToRestore());
		restore_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final int position = spinner.getSelectedItemPosition();
				if (position == 0) {
					showToast("Backup file has to be selected", true);
					return;
				}
				
				// show confirmation dialog
		        AlertDialog.Builder builder = new AlertDialog.Builder(DbAdminActivity.this);
				builder.setMessage("The content of current database will be deleted and replaced by selected database!").setCancelable(false);
				builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						String filename = DbAdminActivity.this.backupFiles[position];
						try {
							dbHelper.restore(filename);
							showToast("Database has been restored from " + filename, false);
						} catch (IOException e) {
							showToast("Database restoring failed, " + e.getMessage(), true);
						}
						ActivityLoggerApplication.getInstance().cacheDatabase(0, true);
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

		Button reset_button = (Button)findViewById(R.id.dbadmin_reset_button);
		reset_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// show confirmation dialog
		        AlertDialog.Builder builder = new AlertDialog.Builder(DbAdminActivity.this);
				builder.setMessage("The content of current database will be deleted and reset to initial state!").setCancelable(false);
				builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
							dbHelper.remove();
							showToast("Database has been reset", false);
						} catch (IOException e) {
							showToast("Database reset failed, " + e.getMessage(), true);
						}
						ActivityLoggerApplication.getInstance().cacheDatabase(0, true);
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

		setupSpinnerAdapter();
    }

	private void setupSpinnerAdapter() {
		DbHelper dbHelper = ActivityLoggerApplication.getInstance().getDbHelper();
		String [] backupFiles = dbHelper.getBackupFiles();
		this.backupFiles = new String [backupFiles.length + 1];
		this.backupFiles[0] = "< Select File >";
		System.arraycopy(backupFiles, 0, this.backupFiles, 1, backupFiles.length);
		
		ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.backupFiles);
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
