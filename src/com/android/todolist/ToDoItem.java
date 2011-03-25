package com.android.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ToDoItem {
	long sqlid;
	String task;
	Date created;
	
	public long getId() {
		return sqlid;
	}
	
	public String getTask() {
		return task;
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
		created = _created;
	}
	
	public ToDoItem(long _sqlid, String _task, Date _created) {
		sqlid = _sqlid;
		task = _task;
		created = _created;
	}
	
	@Override
	public String toString() {
		//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		//String dateString = sdf.format(created);
		//return "(" + dateString + ") " + task;
		return task;
	}
}