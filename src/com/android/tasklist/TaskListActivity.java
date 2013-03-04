package com.android.tasklist;

import java.sql.Date;
import java.util.ArrayList;

import com.android.tasklist.TaskItem.TaskPriority;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class TaskListActivity extends Activity {
	private static final String TEXT_ENTRY_KEY = "TEXT_ENTRY_KEY";
	private static final String ADDING_ITEM_KEY = "ADDING_ITEM_KEY";
	private static final String EDITING_ITEM_KEY = "EDITING_ITEM_KEY";
	private static final String EDITING_ITEM_MEMBER = "EDITING_ITEM_MEMBER";
	private static final String SELECTED_INDEX_KEY = "SELECTED_INDEX_KEY";
	
	private ArrayList<TaskItem> taskItems;
	private TaskItemAdapter taskItemAdapter;
	private ListView listView;
	private EditText editText;
	
	TaskListDBAdapter taskDBAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inflate view
        setContentView(R.layout.main);
        
        // Get references to UI widget
        listView = (ListView)findViewById(R.id.MyListView);
        editText = (EditText)findViewById(R.id.MyEditText);
        
        // Create the array list of to do items
        taskItems = new ArrayList<TaskItem>();
        // Create the array adapter to bind the array to the ListView
        int resID = R.layout.tasklist_item;
        taskItemAdapter = new TaskItemAdapter(this, resID, taskItems);
        // Bind the array adapter to the list view
        listView.setAdapter(taskItemAdapter);
        
        editText.setOnKeyListener(new OnKeyListener() {
        	public boolean onKey(View v, int keyCode, KeyEvent event) {
        		if (event.getAction() == KeyEvent.ACTION_DOWN)
        			if (keyCode == KeyEvent.KEYCODE_ENTER) {
        				if (editText.getText().length() != 0) {
        					TaskItem newItem = new TaskItem(editText.getText().toString());
        					if (!bEditItem) {
        						taskDBAdapter.insertTask(newItem);
        					}
        					else {
        						taskDBAdapter.updateTask(lEditSqlId, newItem);
        					}
        					updateArray();
        					editText.setText(R.string.empty);
        					taskItemAdapter.notifyDataSetChanged();
        				} else {
        					Toast.makeText(getApplicationContext(), R.string.error_empty, Toast.LENGTH_SHORT).show();
        				}
        				cancelAdd();
        				
        				/* Hides soft keyboard */
        				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        				return true;
        			}
        		return false;
        	}
        });
        
        registerForContextMenu(listView);
        restoreUIState();
        
        taskDBAdapter = new TaskListDBAdapter(this);
        
        // Open or create the database
        taskDBAdapter.open();
        
        populateTaskList();
    }
    
    Cursor taskListCursor;
    
    private void populateTaskList() {
    	// Get all the task list items from the database.
    	taskListCursor = taskDBAdapter.getAllTaskItemsCursor();
    	startManagingCursor(taskListCursor);
    	// Update the array.
    	updateArray();
    }
    
    private void updateArray() {
    	taskListCursor.requery();
    	taskItems.clear();
    	if (taskListCursor.moveToFirst())
    		do {
    			long sqlid = taskListCursor.getLong(taskListCursor.getColumnIndex(TaskListDBAdapter.KEY_ID));
    			String task = taskListCursor.getString(taskListCursor.getColumnIndex(TaskListDBAdapter.KEY_TASK));
    			int priority = taskListCursor.getInt(taskListCursor.getColumnIndex(TaskListDBAdapter.KEY_PRIORITY));
    		    long created = taskListCursor.getLong(taskListCursor.getColumnIndex(TaskListDBAdapter.KEY_CREATION_DATE));

    		    if (!(bEditItem && (lEditSqlId == sqlid))) {
    		    	TaskItem newItem = new TaskItem(sqlid, task, TaskPriority.NORMAL_PRIORITY, new Date(created));
    		    	taskItems.add(0, newItem);
    		    }
    	} while(taskListCursor.moveToNext());
    	taskItemAdapter.notifyDataSetChanged();
    }

    private boolean bAddItem = false;
    private boolean bEditItem = false;
    private long lEditSqlId = -1;
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	getMenuInflater().inflate(R.menu.task_menu, menu);

    	return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
    	
    	int idx = listView.getSelectedItemPosition();
    	
    	String removeTitle = getString(bAddItem ? R.string.cancel : R.string.remove);
    	
    	MenuItem mItemAdd = menu.findItem(R.id.task_add);
    	MenuItem mItemEdit = menu.findItem(R.id.task_edit);
    	MenuItem mItemRemove = menu.findItem(R.id.task_remove);
    	mItemEdit.setTitle(R.string.edit);
    	mItemRemove.setTitle(removeTitle);
    	mItemAdd.setVisible(bAddItem == false);
    	mItemEdit.setVisible(bAddItem == false && idx > -1);
    	mItemRemove.setVisible(bAddItem || idx > -1);
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	
    	listView.requestFocus();
    	
    	int index = listView.getSelectedItemPosition();
    	
    	switch (item.getItemId()) {
    		case R.id.task_add: {
    			if (bEditItem) {
    				editText.setText("");
    				cancelAdd();
    			}
    			
    			addNewItem();
    			return true;
    		}
    		case R.id.task_edit: {
    			editItem(index);
    			return true;
    		}
    		case R.id.task_remove: {
    			if (bAddItem) {
    				cancelAdd();
    			}
    			else {
    				removeItem(index);
    			}
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	
    	menu.setHeaderTitle("Selected To Do Item");
    	menu.add(0, R.id.task_edit, Menu.NONE, R.string.edit);
    	menu.add(0, R.id.task_remove, Menu.NONE, R.string.remove);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	super.onContextItemSelected(item);
    	
    	boolean edit = false;
    	
    	if (bAddItem)
    		cancelAdd();
    	
    	switch (item.getItemId()) {
    		case R.id.task_edit:
    			edit = true;
    		case R.id.task_remove: {
    			AdapterView.AdapterContextMenuInfo menuInfo;
    			menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    			int index = menuInfo.position;
    			
    			if (edit) {
    				editItem(index);
    				return true;
    			}
    		
    			removeItem(index);
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    private void addNewItem() {
    	bAddItem = true;
    	editText.setVisibility(View.VISIBLE);
    	editText.requestFocus();
    	
    	/* Shows soft keyboard */
    	InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
    	imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

    }
    
    private void editItem(int index) {
		TaskItem item = taskItems.get(index);
		editText.setText(item.getTask());
		lEditSqlId = item.getId();
		
		taskItems.remove(index);
		
    	if (bAddItem)
    		cancelAdd();
    	
    	addNewItem();
    	bEditItem = true;
    }
    
    private void cancelAdd() {
    	bAddItem = false;
    	bEditItem = false;
    	editText.setText(R.string.empty);
    	editText.setVisibility(View.GONE);
    	updateArray();
    }
    
    private void removeItem(int index) {
    	
    	TaskItem item = taskItems.get(index);
    	taskDBAdapter.removeTask(item.getId());
    	updateArray();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	// Get the activity preference object
    	SharedPreferences uiState = getPreferences(0);
    	// Get the preferences editor
    	SharedPreferences.Editor editor = uiState.edit();
    	
    	// Add the UI state preference values
    	editor.putString(TEXT_ENTRY_KEY, editText.getText().toString());
    	editor.putBoolean(ADDING_ITEM_KEY, bAddItem);
    	editor.putBoolean(EDITING_ITEM_KEY, bEditItem);
    	editor.putLong(EDITING_ITEM_MEMBER, lEditSqlId);
    	// Commit the preferences
    	editor.commit();
    }
    
    private void restoreUIState() {
    	// Get the activity preferences object
    	SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
    	
    	// Read the UI state values, specifying default values
    	String text = settings.getString(TEXT_ENTRY_KEY, "");
    	Boolean adding = settings.getBoolean(ADDING_ITEM_KEY, false);
    	Boolean edit = settings.getBoolean(EDITING_ITEM_KEY, false);
    	Long editSqlId = settings.getLong(EDITING_ITEM_MEMBER, -1);
    	
    	// Restore the UI to the previous state
    	if (adding) {
    		addNewItem();
    		editText.setText(text);
    	}
    	
    	if (edit) {
    		bEditItem = true;
    		lEditSqlId = editSqlId;
    	}
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	savedInstanceState.putInt(SELECTED_INDEX_KEY, listView.getSelectedItemPosition());
    	
    	super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    	int pos = -1;
    	
    	if (savedInstanceState != null)
    		if (savedInstanceState.containsKey(SELECTED_INDEX_KEY))
    			pos = savedInstanceState.getInt(SELECTED_INDEX_KEY, -1);
    	
    	listView.setSelection(pos);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	// Close the database
    	taskDBAdapter.close();
    }
}