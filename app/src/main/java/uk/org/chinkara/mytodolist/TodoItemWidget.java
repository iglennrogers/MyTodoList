package uk.org.chinkara.mytodolist;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import uk.org.chinkara.mytodolist.activity.TodoItemActivity;
import uk.org.chinkara.mytodolist.model.TodoDbHelper;
import uk.org.chinkara.mytodolist.model.TodoItem;
import uk.org.chinkara.mytodolist.model.TodoItemDatasource;
import uk.org.chinkara.mytodolist.model.TodoPreferences;


/**
 * Implementation of App Widget functionality.
 */
public class TodoItemWidget extends AppWidgetProvider {

    private static String TAG = "<<TodoItemWidget>> :";

    public static void force_update(Context context, int widget_id)
    {
        Intent intent = new Intent(context, TodoItemWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = {widget_id};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        TodoItemDatasource items = new TodoItemDatasource(context);
        items.open();

        TodoPreferences prefs = new TodoPreferences(context);

        for (int id: appWidgetIds) {

            Log.d(TAG, "Widget " + String.valueOf(id));

            Long item_id = prefs.get_item_id(id);
            if (item_id != TodoPreferences.INVALID_ITEM_ID) {

                TodoItem item = items.select_record(item_id);
                if (item != null) {

                    updateAppWidget(context, appWidgetManager, item, id);
                }
            }
        }
        items.close();
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(TAG, "Enabled");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d(TAG, "Disabled");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Enter relevant functionality for when a widget is deleted
        Log.d(TAG, "Deleted");

        TodoPreferences prefs = new TodoPreferences(context);
        for (int id: appWidgetIds) {

            Log.d(TAG, "Widget " + String.valueOf(id));
            prefs.remove_widget_id(id);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                TodoItem item, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.todoitem_widget);

        populate_from_item(context, views, item);

        // Register an onClickListener
        Intent intent = new Intent(context, TodoItemActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(TodoDbHelper.TodoSchema._ID, item._id);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_title, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void populate_from_item(final Context context, final RemoteViews widget, final TodoItem item) {

        // Populate the data into the template view using the data object
        widget.setTextViewText(R.id.appwidget_title, item._title);

        if (!item._is_complete) {

            if (item._details.length() > 0) {

                widget.setTextViewText(R.id.appwidget_details, item._details);
            }
            widget.setViewVisibility(R.id.appwidget_details, (item._details.length() > 0) ? View.VISIBLE : View.GONE);

            if (item._has_progress) {

                widget.setProgressBar(R.id.appwidget_progress, 100, item._progress, false);
            }
            widget.setViewVisibility(R.id.appwidget_progress, item._has_progress ? View.VISIBLE : View.GONE);

            if (item._has_duedate) {

                widget.setTextViewText(R.id.appwidget_duedate, item.get_duedate());
            }
            widget.setViewVisibility(R.id.appwidget_duedate, item._has_duedate ? View.VISIBLE : View.GONE);
        }
        else {

            widget.setTextViewText(R.id.appwidget_details, context.getResources().getText(R.string.completeb));
            widget.setViewVisibility(R.id.appwidget_details, View.VISIBLE);
            widget.setViewVisibility(R.id.appwidget_progress, View.GONE);
            widget.setViewVisibility(R.id.appwidget_duedate, View.GONE);
        }
    }
}

