package uk.org.chinkara.mytodolist.activity;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import uk.org.chinkara.mytodolist.R;
import uk.org.chinkara.mytodolist.TodoItemWidget;
import uk.org.chinkara.mytodolist.ctl.SwitchableDueDateCtl;
import uk.org.chinkara.mytodolist.ctl.SwitchableProgressCtl;
import uk.org.chinkara.mytodolist.model.TodoDbHelper;
import uk.org.chinkara.mytodolist.model.TodoItem;
import uk.org.chinkara.mytodolist.model.TodoItemDatasource;
import uk.org.chinkara.mytodolist.adapters.ColourListAdapter;
import uk.org.chinkara.mytodolist.model.TodoPreferences;


public class TodoItemActivity extends Activity implements
        SwitchableProgressCtl.OnProgressInteractionListener,
        SwitchableDueDateCtl.OnDateInteractionListener {

    private static String TAG = "<<Todo edit activity>>: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            Log.d(TAG, "begin onCreate");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_todo_item);

            EditText edTitle = (EditText) findViewById(R.id.edTitleEdit);
            _bg_colour = edTitle.getCurrentTextColor();
            _to_be_deleted = false;

            SwitchableProgressCtl prog = (SwitchableProgressCtl) findViewById(R.id.ctlProgress);
            prog.attach(this);
            SwitchableDueDateCtl duedate = (SwitchableDueDateCtl) findViewById(R.id.ctlDueDate);
            duedate.attach(this, this);

            _col = new ArrayList<>();
            _col.add("Black");
            _col.add("Blue");
            _col.add("Green");
            _col.add("Red");

            Spinner spinner = (Spinner) findViewById(R.id.spLabel);
            ArrayAdapter<String> adapter = new ColourListAdapter(this, _col);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            View delete = findViewById(R.id.bnDelete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onDeleteClick();
                }
            });

            View complete = findViewById(R.id.cbIsComplete);
            complete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    onIsCompleteClick();
                }
            });

            Intent intent = getIntent();
            _uid = intent.getLongExtra(TodoDbHelper.TodoSchema._ID, 0);

            Log.d(TAG, "end onCreate");
        }
        catch (RuntimeException e) {

            Log.d(TAG, e.getMessage());
            throw(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_todo_item, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {

        Log.d(TAG, "begin onStart");
        super.onStart();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        LinearLayout lay = (LinearLayout)findViewById(R.id.layOrientation);
        if (size.x > size.y) {

            lay.setOrientation(LinearLayout.HORIZONTAL);
        }
        else {

            lay.setOrientation(LinearLayout.VERTICAL);
        }

        _source = new TodoItemDatasource(this);
        _source.open();

        Log.d(TAG, "end onStart");
    }

    @Override
    public void onStop() {

        Log.d(TAG, "begin onStop");
        super.onStop();
        _source.close();
        Log.d(TAG, "end onStop");
    }

    @Override
    public void onPause() {

        try {
            Log.d(TAG, "start onPause");
            super.onPause();

            store_item_from_controls(_item);
            if (_item._to_be_deleted) {

                _source.delete_record(_item);
            } else {

                _source.update_record(_item);
            }

            Intent intent = new Intent();
            intent.putExtra(TodoDbHelper.TodoSchema._ID, _item._id);
            setResult(RESULT_OK, intent);

            int widget_id = new TodoPreferences(this).get_widget_id(_item._id);
            if (widget_id != AppWidgetManager.INVALID_APPWIDGET_ID) {

                TodoItemWidget.force_update(this, widget_id);
            }
            Log.d(TAG, "end onPause");
        }
        catch (RuntimeException e) {

            Log.d(TAG, e.getMessage());
            throw(e);
        }
    }

    @Override
    public void onResume() {

        try {

            Log.d(TAG, "begin onResume");
            super.onResume();

            _item = _source.select_record(_uid);
            populate_controls_from_item(_item);

            Log.d(TAG, "end onResume");
        }
        catch (RuntimeException e) {

            Log.d(TAG, e.getMessage());
            throw(e);
        }
    }

    @Override
    public void onProgressChanged(boolean shown, int progress) {

        CheckBox cbComplete = (CheckBox)findViewById(R.id.cbIsComplete);
        cbComplete.setChecked(progress == 100);
        onIsCompleteClick();
    }

    @Override
    public void onDateChanged(boolean on, GregorianCalendar date) {

    }

    public void onIsCompleteClick() {

        EditText edTitle = (EditText)findViewById(R.id.edTitleEdit);
        CheckBox button = (CheckBox)findViewById(R.id.cbIsComplete);
        if (button.isChecked()) {

            edTitle.setPaintFlags(edTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {

            edTitle.setPaintFlags(edTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        Button bnDelete = (Button)findViewById(R.id.bnDelete);
        bnDelete.setVisibility(button.isChecked() ? View.VISIBLE : View.GONE);
    }

    public void onDeleteClick() {

        _to_be_deleted = !_to_be_deleted;
        whenDeleteClick();
    }

    private void whenDeleteClick() {

        EditText edTitle = (EditText)findViewById(R.id.edTitleEdit);
        Button bnDelete = (Button)findViewById(R.id.bnDelete);
        Spinner spLabel = (Spinner)findViewById(R.id.spLabel);
        if (_to_be_deleted) {

            edTitle.setTextColor(Color.RED);
            bnDelete.setText(getResources().getText(R.string.undelete));
            spLabel.setVisibility(View.INVISIBLE);
        }
        else {

            edTitle.setTextColor(_bg_colour);
            bnDelete.setText(getResources().getText(R.string.delete));
            spLabel.setVisibility(View.VISIBLE);
        }

        LinearLayout lyDetailFrame = (LinearLayout)findViewById(R.id.edit_item_layout);
        lyDetailFrame.setVisibility(_to_be_deleted ? View.GONE : View.VISIBLE);
    }

    private void populate_controls_from_item(TodoItem item) {

        EditText edTitle = (EditText)findViewById(R.id.edTitleEdit);
        EditText edDetails = (EditText)findViewById(R.id.edDetails);
        CheckBox cbComplete = (CheckBox)findViewById(R.id.cbIsComplete);
        Spinner spLabel = (Spinner)findViewById(R.id.spLabel);

        SwitchableProgressCtl progress = (SwitchableProgressCtl)findViewById(R.id.ctlProgress);
        SwitchableDueDateCtl duedate = (SwitchableDueDateCtl)findViewById(R.id.ctlDueDate);

        // Populate the data into the template view using the data object
        if (!edTitle.isInEditMode()) {

            edTitle.setText(item._title);
            edDetails.setText(item._details);
            spLabel.setSelection(_col.indexOf(item._label));
            cbComplete.setChecked(item._is_complete);
            onIsCompleteClick();

            progress.set(item._has_progress, item._progress);
            duedate.set(item._has_duedate, item._duedate);

            _to_be_deleted = item._to_be_deleted;
            whenDeleteClick();
        }
        else {

            edTitle.setText("Title");
            edDetails.setText("Details");
            cbComplete.setSelected(false);

            progress.set(true, 40);
            duedate.set(true, TodoItem.date_as_cal("31/12/2015"));

            _to_be_deleted = false;
        }
    }

    private void store_item_from_controls(TodoItem item) {

        Spinner spLabel = (Spinner)findViewById(R.id.spLabel);
        EditText edTitle = (EditText)findViewById(R.id.edTitleEdit);
        EditText edDetails = (EditText)findViewById(R.id.edDetails);
        CheckBox cbComplete = (CheckBox)findViewById(R.id.cbIsComplete);

        SwitchableProgressCtl progress = (SwitchableProgressCtl)findViewById(R.id.ctlProgress);
        SwitchableDueDateCtl duedate = (SwitchableDueDateCtl)findViewById(R.id.ctlDueDate);

        // store the data
        item._title = edTitle.getText().toString();
        item._details = edDetails.getText().toString();
        item._label = _col.get(spLabel.getSelectedItemPosition());
        item._is_complete = cbComplete.isChecked();

        item._has_progress = progress.is_visible();
        item._progress = progress.get();

        item._has_duedate = duedate.is_visible();
        item._duedate = duedate.get();

        item._to_be_deleted = _to_be_deleted;
        item.set_last_edit_date();
    }

    private int _bg_colour;
    private ArrayList<String> _col;
    private boolean _to_be_deleted;

    private Long _uid;
    private TodoItemDatasource _source;
    private TodoItem _item;
}
