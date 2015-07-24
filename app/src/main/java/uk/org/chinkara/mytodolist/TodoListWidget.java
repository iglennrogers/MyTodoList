package uk.org.chinkara.mytodolist;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import uk.org.chinkara.mytodolist.activity.TodoListActivity;
import uk.org.chinkara.mytodolist.model.TodoItemDatasource;
import uk.org.chinkara.mytodolist.model.TodoPreferences;


/**
 * Implementation of App Widget functionality.
 */
public class TodoListWidget extends AppWidgetProvider {

    private static String TAG = "<<TodoListWidget>> :";

    public static void force_update(Context context)
    {
        TodoPreferences prefs = new TodoPreferences(context);
        int widget_id = prefs.get_widget_id(TodoPreferences.WIDGET_LIST_ID);
        Log.d(TAG, "force update" + String.valueOf(widget_id));
        if (widget_id != AppWidgetManager.INVALID_APPWIDGET_ID) {

            Intent intent = new Intent(context, TodoListWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = {widget_id};
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {

            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        TodoPreferences prefs = new TodoPreferences(context);
        for (int id: appWidgetIds) {

            prefs.remove_widget_id(id);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newExtras) {

        Log.d(TAG, "Call widget change");
        TodoPreferences prefs = new TodoPreferences(context);
        prefs.set_widget_ids(appWidgetId, TodoPreferences.WIDGET_LIST_ID);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.todo_widget);
        views.setTextViewText(R.id.appwidget_title, widgetText);

        // Register an onClickListener
        Intent intent = new Intent(context, TodoListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_title, pendingIntent);

        TodoItemDatasource items = new TodoItemDatasource(context);
        items.open();
        items.select_all_records();
        if (items.count() > 0) {

            views.setTextViewText(R.id.appwidget_details, items.at(0)._title);
            if (items.count() > 1) {

                views.setTextViewText(R.id.tvWidget2, items.at(1)._title);
                if (items.count() > 2) {

                    views.setTextViewText(R.id.tvWidget3, items.at(2)._title);
                }
                else {

                    views.setTextViewText(R.id.tvWidget3, "");
                }
            }
            else {

                views.setTextViewText(R.id.tvWidget2, "");
            }
        }
        else {

            views.setTextViewText(R.id.appwidget_details, "<-->");
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        items.close();
    }
}

