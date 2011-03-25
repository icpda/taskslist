package com.android.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ToDoItem {
	
	public enum ToDoPriority {
		LOW_PRIORITY(1),
		NORMAL_PRIORITY(2),
		HIGH_PRIORITY(3);
		
		private int priority;
		
		private ToDoPriority(int p) {
			priority = p;
		}
		
		public int getPriority() {
			return priority;
		}		
	}
	
	long sqlid;
	String task;
	ToDoPriority priority;
	Date created;
	
	public long getId() {
		return sqlid;
	}
	
	public String getTask() {
		return task;
	}
	
	public int getPriority() {
		return priority.getPriority();
	}
		
	public Date getCreated() {
		return created;
	}
	
	public ToDoItem(String _task) {
		this(_task, new Date(java.lang.System.currentTimeMillis()));
	}
	
	public ToDoItem(String _task, Date _created) {
		sqlid = -1;
		task = _task;
		priority = ToDoPriority.NORMAL_PRIORITY;
		created = _created;
	}
	
	public ToDoItem(long _sqlid, String _task, ToDoPriority _priority, Date _created) {
		sqlid = _sqlid;
		task = _task;
		priority = _priority;
		created = _created;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateString = sdf.format(created);
		return "[" + getPriorityKey() + "] " + task + " (" + dateString + ")";
	}
	
	public static String getPriorityValue(ToDoPriority _priority) {
		if (_priority == ToDoPriority.LOW_PRIORITY)
			return "Low";
		if (_priority == ToDoPriority.HIGH_PRIORITY)
			return "High";
		return "Normal";
	}

	public String getPriorityKey() {
		if (priority == ToDoPriority.LOW_PRIORITY)
			return "L";
		if (priority == ToDoPriority.HIGH_PRIORITY)
			return "H";
		return "N";
	}
}