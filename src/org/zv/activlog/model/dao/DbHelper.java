package org.zv.activlog.model.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.zv.activlog.R;

public class DbHelper {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm"); 
	private static final String DATABASE_NAME = "activlog.db";
	private static final int    DATABASE_VERSION = 2;

	private OpenHelper openHelper;
	private String path;
	private SQLiteDatabase	db;

	private static class OpenHelper extends SQLiteOpenHelper {
		private Context context;
		public OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(getClass().getSimpleName(), "create database");
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.create_db)));
				String line;
				while ((line = reader.readLine()) != null) {
					db.execSQL(line);
				}
				Log.i(getClass().getSimpleName(), "create database - done");
			} catch (Exception e) {
				Log.e(getClass().getSimpleName(), e.getMessage());
			}
			Log.i(getClass().getSimpleName(), "create database - done");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldver, int newver) {
			Log.i(getClass().getSimpleName(), "upgrade database");
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.drop_db)));
				String line;
				while ((line = reader.readLine()) != null) {
					try {
						db.execSQL(line);
					} catch (Exception e) {
						Log.e(getClass().getSimpleName(), e.getMessage());
					}
				}
			} catch (Exception e) {
				Log.e(getClass().getSimpleName(), e.getMessage());
			}
			onCreate(db);
			Log.i(getClass().getSimpleName(), "upgrade database - done");
		}
	}
	
	public DbHelper(Context context) {
		openHelper = new OpenHelper(context);
		path = context.getApplicationContext().getDatabasePath(DATABASE_NAME).getPath(); 
	}

	public boolean isAbleToBackup() {
		String extStorageState = Environment.getExternalStorageState();
		return extStorageState.equals(Environment.MEDIA_MOUNTED);
	}
	
	public void backup(boolean isTimeStamped) throws IOException {
		if (!isAbleToBackup()) {
			return;
		}
		
		String filename = DATABASE_NAME;
		if (isTimeStamped) {
			filename = dateFormat.format(new Date()) + ".db";
		}

		close();
		
		File to = new File(getBackupDir(), filename);
		copyFile(new File(path), to);

		open();
	}

	public void makeCorruptBackup() throws IOException {
		if (!isAbleToBackup()) {
			return;
		}
		
		File to = new File(getBackupDir(), "activlog_bad.db");
	    OutputStream output = null;
	    try {
	    	if (to.exists()) {
	    		to.delete();
	    	}
	    	
		    output = new FileOutputStream(to);
		    byte[] buffer = new byte[100];
		    for (int i = 0; i < 100; i++) buffer[i] = (byte)(Math.random()*255);
	        output.write(buffer, 0, 100);
		    output.flush();
	    } finally {
		    if (output != null) {
		    	output.close();
		    }
	    }
	}

	public boolean isAbleToRestore() {
		String extStorageState = Environment.getExternalStorageState();
		return extStorageState.equals(Environment.MEDIA_MOUNTED) || extStorageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
	}
	
	public void restore(String filename) throws IOException {
		if (!isAbleToRestore()) {
			return;
		}
	
		close();
		
		File from = new File(getBackupDir(), filename);
		copyFile(from, new File(path));

		open();
	}

	public File getBackupDir() {
		if (!isAbleToRestore()) {
			return null;
		}

		File extMediaDir = new File(Environment.getExternalStorageDirectory().getPath());
		File backupDir = new File(extMediaDir, "activlog");
		if (!backupDir.exists()) {
			backupDir.mkdir();
		}
		
		return backupDir;
	}

	public void remove() throws IOException {
		close();
		
		File dbfile = new File(path);
		if (dbfile.exists()) {
			dbfile.delete();
		}

		open();
	}
	
	public String [] getBackupFiles() {
		File backupDir = getBackupDir();
		if (backupDir == null) {
			return new String[0];
		}
		
		return backupDir.list(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return (filename.endsWith(".db"));
			}
		});
	}

	private void copyFile(File from, File to) throws IOException {
	    InputStream input = null;
	    OutputStream output = null;
	    try {
	    	if (to.exists()) {
	    		to.delete();
	    	}
	    	
		    input = new FileInputStream(from);
		    output = new FileOutputStream(to);
		    byte[] buffer = new byte[1024];
		    int length;
		    while ((length = input.read(buffer))>0) {
		        output.write(buffer, 0, length);
		    }
		    output.flush();
	    } finally {
		    if (output != null) {
		    	output.close();
		    }
		    if (input != null) {
			    input.close();
		    }
	    }
	}

	public void open() {
		db = openHelper.getWritableDatabase();
		db.execSQL("PRAGMA foreign_keys=ON;");
	}
	
	public void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}
	
	public void beginTransaction() {
		db.beginTransaction();
	}
	
	public void setTransactionSuccessful() {
		db.setTransactionSuccessful();
	}
	
	public void endTransaction() {
		db.endTransaction();
	}
	
	public long insert(String table, ContentValues values) {
		return db.insertOrThrow(table, null, values);
	}

	public long update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		return db.update(table, values, whereClause, whereArgs);
	}
	
	public long delete(String table, String whereClause, String[] whereArgs) {
		return db.delete(table, whereClause, whereArgs);
	}
	
	public Cursor query(String table, String [] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	
	public Cursor rawQuery(String sql, String [] selectionArgs) {
		return db.rawQuery(sql, selectionArgs);
	}
}
