package com.android.tasklist;

import java.sql.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class TaskListDBAdapter {
	private static final String DATABASE_NAME = "TaskListDB.db";
	private static final String DATABASE_TABLE_CATEGORIES = "TaskCategories";
	private static final String DATABASE_TABLE_ITEMS = "TaskItems";
	private static final int DATABASE_VERSION = 4;
	
	private SQLiteDatabase _db;
	private TaskListDBOpenHelper _dbHelper;
	
	public TaskListDBAdapter(Context context) {
		_dbHelper = new TaskListDBOpenHelper(context, DATABASE_NAME,
				null, DATABASE_VERSION);
	}
	
	public static final String KEY_ID = "_id";
	public static final String KEY_TASK = "task";
	public static final String KEY_CATEGORY ="category";
	public static final String KEY_PRIORITY = "priority";
	public static final String KEY_CREATION_DATE = "creation_date";

	public void close() {
		_db.close();
	}
	
	public void open() throws SQLiteException {
		try {
			_db = _dbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			_db = _dbHelper.getReadableDatabase();
		}
	}

	// Insert a new task
	public long insertTask(TaskItem task) {
		// Create a new row of values to insert.
		ContentValues newTaskValues = new ContentValues();
		// Assign values for each row.
		newTaskValues.put(KEY_TASK, task.getTask());
		newTaskValues.put(KEY_PRIORITY, task.getPriority());
		newTaskValues.put(KEY_CREATION_DATE, task.getCreated().getTime());
		// Insert the row.
		return _db.insert(DATABASE_TABLE_ITEMS, null, newTaskValues);
	}
	
	// Remove a task based on its index
	public boolean removeTask(long _rowIndex) {
		return _db.delete(DATABASE_TABLE_ITEMS, KEY_ID + "=" + _rowIndex, null) > 0;
	}
	
	// Update a task
	public boolean updateTask(long _rowIndex, TaskItem _task) {
		ContentValues newValue = new ContentValues();
		newValue.put(KEY_TASK, _task.getTask());
		newValue.put(KEY_PRIORITY, _task.getPriority());
		return _db.update(DATABASE_TABLE_ITEMS, newValue, KEY_ID + "=" + _rowIndex, null) > 0;
	}
	
	public Cursor getAllTaskItemsCursor() {
		return _db.query(DATABASE_TABLE_ITEMS,
				new String[] { KEY_ID, KEY_TASK, KEY_PRIORITY, KEY_CREATION_DATE},
				null, null, null, null, null);
	}
	
	public Cursor setCursorToTaskItem(long _rowIndex) throws SQLException {
		Cursor result = _db.query(true, DATABASE_TABLE_ITEMS,
				new String[] {KEY_ID, KEY_TASK},
				KEY_ID + "=" + _rowIndex, null, null, null,
				null, null);
		
		if ((result.getCount() == 0) || !result.moveToFirst()) {
			throw new SQLException("No to do items found for row: " + _rowIndex);
		}
		
		return result;
	}
	
	public TaskItem getTaskItem(long _rowIndex) throws SQLException {
		Cursor cursor = _db.query(true, DATABASE_TABLE_ITEMS,
				new String[] {KEY_ID, KEY_TASK},
				KEY_ID + "=" + _rowIndex, null, null, null,
				null, null);
		
		if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
			throw new SQLException("No to do item found for row: " + _rowIndex);
		}
		
		String task = cursor.getString(cursor.getColumnIndex(KEY_TASK));
		int priority = cursor.getInt(cursor.getColumnIndex(KEY_PRIORITY));
		long created = cursor.getLong(cursor.getColumnIndex(KEY_CREATION_DATE));
		TaskItem result = new TaskItem(task, new Date(created));
		return result;
	}

	private static class TaskListDBOpenHelper extends SQLiteOpenHelper {
		public TaskListDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		
		// SQL Statement to create a new database.
		private static final String DATABASE_CREATE = "create table " +
		DATABASE_TABLE_ITEMS + " (" + KEY_ID + " integer primary key autoincrement, " +
		KEY_TASK + " text not null, " + KEY_PRIORITY + " int, " + KEY_CREATION_DATE + " long);";
		
		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			Log.w("TaskDBAdapter", "Upgrading from version " +
					_oldVersion + " to " +
					_newVersion + ", which will destroy all old data");
			// Drop the old table.
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ITEMS);
			// Create a new one.
			onCreate(_db);
		}		
	}
}
