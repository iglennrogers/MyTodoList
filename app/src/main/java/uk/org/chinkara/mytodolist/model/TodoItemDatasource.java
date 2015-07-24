package uk.org.chinkara.mytodolist.model;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by g_rogers on 19/06/2015.
 */
public class TodoItemDatasource {

    private static String TAG = "<<Todo collection>>: ";

    public TodoItemDatasource(Context context) {

        _database = null;
        _helper = new TodoDbHelper(context);
        _items = new ArrayList<>();

        TodoPreferences.SORT_METHOD method = new TodoPreferences(context).get_sort_method();
        set_comparison(method);
    }

    public void open() {

        try {

            _database = _helper.getWritableDatabase();
        }
        catch (SQLException ex) {

            Log.d(TAG, ex.getMessage());
            throw(ex);
        }
    }

    public void close() {

        _helper.close();
        _database = null;
    }

    public ArrayList<TodoItem> raw_items() {

        return _items;
    }

    public int count() {

        return _items.size();
    }

    public TodoItem at(int idx) {

        if (idx < _items.size()) {

            return _items.get(idx);
        }
        return null;
    }

    public void sort() {

        if (_sortmethod != null) {

            Collections.sort(_items, _sortmethod);
        }
    }

    public void select_all_records() {

        Cursor cursor = null;
        _items.clear();
        try {

            cursor = _database.query(TodoDbHelper.TodoSchema.TABLE_NAME, TodoDbHelper.TodoSchema.SQL_COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {

                    TodoItem item = TodoItem.from_record(cursor);
                    _items.add(item);
                    cursor.moveToNext();
                }
                sort();
            }
            else {

                insert_record(new TodoItem("Example"));
            }
        }
        catch (SQLException ex) {

            Log.d(TAG, ex.toString());
            throw(ex);
        }
        finally {

            if (cursor != null) {

                cursor.close();
            }
        }
    }

    public TodoItem select_record(Long uid) {

        TodoItem result = null;
        String[] id_val = { String.valueOf(uid) };
        Cursor cursor = null;
        try {

            cursor = _database.query(TodoDbHelper.TodoSchema.TABLE_NAME, TodoDbHelper.TodoSchema.SQL_COLUMNS,
                    TodoDbHelper.TodoSchema._ID + " = ?", id_val, null, null, null);
            if (cursor.getCount() == 1) {

                cursor.moveToFirst();

                result = TodoItem.from_record(cursor);
                Log.d(TAG, "Read with id : " + id_val[0]);
            }
            else {

                insert_record(new TodoItem("Example"));
            }
        }
        catch (SQLException ex) {

            Log.d(TAG, ex.toString());
            throw(ex);
        }
        finally {

            if (cursor != null) {

                cursor.close();
            }
        }
        return result;
    }

    public void insert_record(TodoItem item) {

        item._id = _database.insert(TodoDbHelper.TodoSchema.TABLE_NAME, null, item.to_record_insert());
        Log.d(TAG, "Insert with id : " + String.valueOf(item._id));
        _items.add(item);
        sort();
    }

    public void update_record(TodoItem item) {

        String[] id_val = { String.valueOf(item._id) };
        int cnt =  _database.update(TodoDbHelper.TodoSchema.TABLE_NAME, item.to_record_insert(), TodoDbHelper.TodoSchema._ID + " = ?", id_val);
        if (cnt != 1) {

            throw new SQLException("Unable to update record, id: " + id_val);
        }
        Log.d(TAG, "Update with id : " + id_val[0]);
        sort();
    }

    public void delete_record(TodoItem item) {

        String[] id_val = { String.valueOf(item._id) };
        int cnt = _database.delete(TodoDbHelper.TodoSchema.TABLE_NAME, TodoDbHelper.TodoSchema._ID + " = ?", id_val);
        if (cnt != 1) {

            throw new SQLException("Unable to delete record, id: " + id_val[0]);
        }
        Log.d(TAG, "Delete with id : " + id_val[0]);
        _items.remove(item);
    }

    private SQLiteDatabase _database;
    private TodoDbHelper _helper;
    private ArrayList<TodoItem> _items;

    private Comparator _sortmethod;

    public void set_comparison(TodoPreferences.SORT_METHOD method)
    {
        switch (method) {

            case LAST_EDIT_DATE:
                _sortmethod = new TodoItem.ByLastEditComparator();
                break;
            case DUE_DATE:
                _sortmethod = new TodoItem.ByDueDateComparator();
                break;
            case LABEL:
                _sortmethod = new TodoItem.ByLabelComparator();
                break;
            case CREATION_DATE:
                _sortmethod = new TodoItem.ByCreationComparator();
                break;
            default:
                _sortmethod = new TodoItem.ByCreationComparator();
                break;
        }
    }
}
