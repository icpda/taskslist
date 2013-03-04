package com.android.tasklist;

import java.text.SimpleDateFormat;
import android.content.Context;
import java.util.*;
import android.view.*;
import android.widget.*;

public class TaskItemAdapter extends ArrayAdapter<TaskItem> {
	
	int resource;
	
	public TaskItemAdapter(Context _context, int _resource, List<TaskItem> _items) {
		super(_context, _resource, _items);
		resource = _resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout taskView;
		
		TaskItem item = getItem(position);
		
		String taskString = item.getTask();
		Date createdDate = item.getCreated();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateString = sdf.format(createdDate);
		
		if (convertView == null) {
			taskView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, taskView, true);
		} else {
			taskView = (LinearLayout) convertView;
		}
		
		TextView dateView = (TextView)taskView.findViewById(R.id.rowDate);
		TextView textView = (TextView)taskView.findViewById(R.id.rowTask);
		
		dateView.setText(dateString);
		textView.setText(taskString);
		
		return taskView;
	}
}
