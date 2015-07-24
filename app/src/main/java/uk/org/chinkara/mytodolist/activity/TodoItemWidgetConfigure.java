package uk.org.chinkara.mytodolist.activity;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import uk.org.chinkara.mytodolist.R;
import uk.org.chinkara.mytodolist.TodoItemWidget;
import uk.org.chinkara.mytodolist.fragments.TodoItemWidgetConfigureFragment;
import uk.org.chinkara.mytodolist.model.TodoDbHelper;
import uk.org.chinkara.mytodolist.model.TodoItem;
import uk.org.chinkara.mytodolist.model.TodoPreferences;

public class TodoItemWidgetConfigure extends Activity implements TodoItemWidgetConfigureFragment.OnItemConfigureInteractionListener {

    private static String TAG = "<<TodoItemWidget config a>> :";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "begin onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_item_widget_configure);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        _appwidget_id = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        Intent cancel = new Intent();
        cancel.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _appwidget_id);
        setResult(RESULT_CANCELED, cancel);

        Log.d(TAG, "end onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }

    @Override
    public void onItemConfigureInteraction(TodoItem item) {

        Log.d(TAG, "begin onInteraction");

        TodoPreferences prefs = new TodoPreferences(this);
        prefs.set_widget_ids(_appwidget_id, item._id);

        Intent result = new Intent();
        result.putExtra(TodoDbHelper.TodoSchema._ID, item._id);
        setResult(RESULT_OK, result);
        finish();

        TodoItemWidget.force_update(this, _appwidget_id);

        Log.d(TAG, "end onInteraction");
    }

    private int _appwidget_id;
}
