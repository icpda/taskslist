package com.android.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ToDoItem {
	
	public enum ToDoPriority {
		LOW_PRIORITY(1),
		NORMAL_PRIORITY(2),
		HIGH_PRIORITY(3);
		
		private int _priority;
		
		private ToDoPriority(int p) {
			_priority = p;
		}
		
		public int getPriority() {
			return _priority;
		}		
	}
	
	private long _sqlid;
	private String _task;
	private ToDoPriority _priority;
	private Date _created;
	
	public long getId() {
		return _sqlid;
	}
	
	public String getTask() {
		return _task;
	}
	
	public int getPriority() {
		return _priority.getPriority();
	}
		
	public Date getCreated() {
		return _created;
	}
	
	public ToDoItem(String task) {
		this(task, new Date(java.lang.System.currentTimeMillis()));
	}
	
	public ToDoItem(String task, Date created) {
		_sqlid = -1;
		_task = task;
		_priority = ToDoPriority.NORMAL_PRIORITY;
		_created = created;
	}
	
	public ToDoItem(long sqlid, String task, ToDoPriority priority, Date created) {
		_sqlid = sqlid;
		_task = task;
		_priority = priority;
		_created = created;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateString = sdf.format(_created);
		return "[" + getPriorityKey() + "] " + _task + " (" + dateString + ")";
	}
	
	public static String getPriority(ToDoPriority priority) {
		if (priority == ToDoPriority.LOW_PRIORITY)
			return "Low";
		if (priority == ToDoPriority.HIGH_PRIORITY)
			return "High";
		return "Normal";
	}

	public String getPriorityKey() {
		if (_priority == ToDoPriority.LOW_PRIORITY)
			return "L";
		if (_priority == ToDoPriority.HIGH_PRIORITY)
			return "H";
		return "N";
	}
}