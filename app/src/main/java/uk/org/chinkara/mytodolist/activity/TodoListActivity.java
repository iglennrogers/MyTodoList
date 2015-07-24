package uk.org.chinkara.mytodolist.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import uk.org.chinkara.mytodolist.R;
import uk.org.chinkara.mytodolist.TodoListWidget;
import uk.org.chinkara.mytodolist.adapters.ItemViewAdapter;
import uk.org.chinkara.mytodolist.model.AndroidDatabaseManager;
import uk.org.chinkara.mytodolist.model.TodoDbHelper;
import uk.org.chinkara.mytodolist.model.TodoItem;
import uk.org.chinkara.mytodolist.model.TodoItemDatasource;
import uk.org.chinkara.mytodolist.model.TodoPreferences;


public class TodoListActivity extends Activity {

    private static String TAG = "<<Todo activity>>: ";
    private static int EDIT_REQUEST = 100;
    private static int SETTINGS_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "begin onCreate");
        final Activity activity = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        _items = new TodoItemDatasource(this);
        setupListener();

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                Intent dbmanager = new Intent(activity, AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });

        Log.d(TAG, "end onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            Log.d(TAG, "Got a settings click");

            Intent intent = new Intent(this, UserSettingsActivity.class);
            startActivityForResult(intent, SETTINGS_REQUEST);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {

        Log.d(TAG, "begin onStart");
        super.onStart();
        _items.open();
        _items.select_all_records();

        _items_adapter = new ItemViewAdapter(this, _items);

        ListView lvItems = (ListView)findViewById(R.id.list_items);
        lvItems.setAdapter(_items_adapter);

        _items_adapter.notifyDataSetChanged();
        Log.d(TAG, "end onStart");
    }

    @Override
    public void onStop() {

        Log.d(TAG, "begin onStop");
        super.onStop();
        _items.close();
        Log.d(TAG, "end onStop");
    }

    @Override
    public void onResume() {

        Log.d(TAG, "begin onResume");
        super.onResume();
        Log.d(TAG, "end onResume");
    }

    @Override
    public void onPause() {

        Log.d(TAG, "begin onPause");
        super.onPause();
        TodoListWidget.force_update(this);
        Log.d(TAG, "end onPause");
    }

    @Override
    public void onBackPressed() {

        Log.d(TAG, "begin onBackPressed");
        super.onBackPressed();
        Log.d(TAG, "end onBackPressed");
    }

    private void onAddItem(View v) {

        Log.d(TAG, "begin onAddItem");

        EditText ed = (EditText)findViewById(R.id.new_item_title);
        String text = ed.getText().toString();
        ed.setText("");

        _items.insert_record(new TodoItem(text));
        _items_adapter.notifyDataSetChanged();

        Log.d(TAG, "end onAddItem");
    }

    private void onEditItem(int position) {

        Log.d(TAG, "begin onEditItem");

        TodoItem item = _items_adapter.getItem(position);
        Intent intent = new Intent(this, TodoItemActivity.class);
        intent.putExtra(TodoDbHelper.TodoSchema._ID, item._id);
        Log.d(TAG, "edit item with id : " + String.valueOf(item._id));
        startActivityForResult(intent, EDIT_REQUEST);

        Log.d(TAG, "end onEditItem");
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data) {

        Log.d(TAG, "begin onActivityResult");
        if (result_code == RESULT_OK) {

            if (request_code == EDIT_REQUEST) {

                Log.d(TAG, "edit item");
                _items.sort();
                _items_adapter.notifyDataSetChanged();
            }
            else if (request_code == SETTINGS_REQUEST) {

                Log.d(TAG, "sort method");
                TodoPreferences.SORT_METHOD method = new TodoPreferences(this).get_sort_method();
                _items.set_comparison(method);
                _items.sort();
                _items_adapter.notifyDataSetChanged();
            }
        }
        Log.d(TAG, "end onActivityResult");
    }

    private void setupListener() {

        ListView lvItems = (ListView)findViewById(R.id.list_items);
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                onEditItem(position);
                return true;
            }
        });

        Button bnAdd = (Button)findViewById(R.id.add_item_button);
        bnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onAddItem(v);
            }
        });
    }

    private ArrayAdapter<TodoItem> _items_adapter;
    private TodoItemDatasource _items;
}
