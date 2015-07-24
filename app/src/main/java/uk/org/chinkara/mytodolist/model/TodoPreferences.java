package uk.org.chinkara.mytodolist.model;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;

import uk.org.chinkara.mytodolist.R;

/**
 * Created by g_rogers on 17/07/2015.
 */
public class TodoPreferences {

    public enum SORT_METHOD {

        CREATION_DATE,
        LAST_EDIT_DATE,
        DUE_DATE,
        LABEL
    }

    public static String SORT_CONFIG_KEY = "sort_method";
    public static long INVALID_ITEM_ID = -1;
    public static long WIDGET_LIST_ID = 0;

    public TodoPreferences(Context context) {

        _context = context;
        _prefs = context.getSharedPreferences("uk.org.chinkara.mytodolist_preferences", Context.MODE_PRIVATE);
    }

    public SORT_METHOD get_sort_method() {

        String m = _prefs.getString(SORT_CONFIG_KEY, _context.getResources().getText(R.string.default_sort).toString());
        int method = Integer.valueOf(m);
        return SORT_METHOD.values()[method];
    }

    public void set_widget_ids(int widget_id, long item_id) {

        SharedPreferences.Editor edit = _prefs.edit();
        edit.putLong(widget_key(widget_id), item_id);
        edit.putInt(item_key(item_id), widget_id);
        edit.commit();
    }

    public void remove_widget_id(int widget_id) {

        long item_id = _prefs.getLong(widget_key(widget_id), INVALID_ITEM_ID);

        SharedPreferences.Editor edit = _prefs.edit();
        edit.remove(widget_key(widget_id));
        if (item_id != INVALID_ITEM_ID) {

            edit.remove(item_key(item_id));
        }
        edit.commit();
    }

    public long get_item_id(int widget_id) {

        return _prefs.getLong(widget_key(widget_id), INVALID_ITEM_ID);
    }

    public int get_widget_id(long item_id) {

        return _prefs.getInt(item_key(item_id), AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    private String widget_key(int appid) {

        return "Widget:" + appid;
    }

    private String item_key(long itemid) {

        return "Item:" + itemid;
    }

    Context _context;
    SharedPreferences _prefs;
}
