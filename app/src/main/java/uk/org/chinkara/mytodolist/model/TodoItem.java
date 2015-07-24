package uk.org.chinkara.mytodolist.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by g_rogers on 09/06/2015.
 */
public class TodoItem {

    public static TodoItem from_record(Cursor cursor) {

        String title = cursor.getString(cursor.getColumnIndex(TodoDbHelper.TodoSchema.COLUMN_TITLE));
        TodoItem item = new TodoItem(title);

        item._id = cursor.getLong(cursor.getColumnIndex(TodoDbHelper.TodoSchema._ID));
        item._last_edit_date = cursor.getLong(cursor.getColumnIndex(TodoDbHelper.TodoSchema.COLUMN_LASTEDIT));

        item._details = cursor.getString(cursor.getColumnIndex(TodoDbHelper.TodoSchema.COLUMN_DETAILS));
        item._is_complete = (cursor.getInt(cursor.getColumnIndex(TodoDbHelper.TodoSchema.COLUMN_IS_COMPLETE)) == 1);

        item._has_progress = (cursor.getInt(cursor.getColumnIndex(TodoDbHelper.TodoSchema.COLUMN_HAS_PROGRESS)) == 1);
        item._progress = cursor.getInt(cursor.getColumnIndex(TodoDbHelper.TodoSchema.COLUMN_PROGRESS));

        item._has_duedate = (cursor.getInt(cursor.getColumnIndex(TodoDbHelper.TodoSchema.COLUMN_HAS_DUEDATE)) == 1);
        item.set_duedate(cursor.getString(cursor.getColumnIndex(TodoDbHelper.TodoSchema.COLUMN_DUEDATE)));

        item._label = cursor.getString(cursor.getColumnIndex(TodoDbHelper.TodoSchema.COLUMN_LABEL));

        return item;
    }

    public ContentValues to_record() {

        ContentValues record = to_record_insert();
        record.put(TodoDbHelper.TodoSchema._ID, _id);

        return record;
    }

    public ContentValues to_record_insert() {

        ContentValues record = new ContentValues();

        record.put(TodoDbHelper.TodoSchema.COLUMN_TITLE, _title);
        record.put(TodoDbHelper.TodoSchema.COLUMN_DETAILS, _details);

        record.put(TodoDbHelper.TodoSchema.COLUMN_IS_COMPLETE, _is_complete);
        record.put(TodoDbHelper.TodoSchema.COLUMN_HAS_PROGRESS, _has_progress);
        record.put(TodoDbHelper.TodoSchema.COLUMN_PROGRESS, _progress);

        record.put(TodoDbHelper.TodoSchema.COLUMN_HAS_DUEDATE, _has_duedate);
        record.put(TodoDbHelper.TodoSchema.COLUMN_DUEDATE, get_duedate());
        record.put(TodoDbHelper.TodoSchema.COLUMN_LASTEDIT, _last_edit_date);
        record.put(TodoDbHelper.TodoSchema.COLUMN_LABEL, _label);

        return record;
    }

    public TodoItem(String s) {

        _id = 0;
        _title = s;
        _details = "";
        _label = "Black";
        _is_complete = false;
        _has_progress = false;
        _has_duedate = false;
        _to_be_deleted = false;
        set_last_edit_date();
    }

    @Override
    public String toString() {

        return _title;
    }

    public void set_duedate(String dt) {

        _duedate = date_as_cal(dt);
    }

    public String get_duedate() {

        return date_as_string(_duedate);
    }

    public void set_last_edit_date() {

        _last_edit_date = new GregorianCalendar().getTime().getTime();
    }

    static public String date_as_string(GregorianCalendar cal) {

        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(cal.getTime());
    }

    static public GregorianCalendar date_as_cal(String dt) {

        GregorianCalendar cal = new GregorianCalendar();

        try {

            Date date = DateFormat.getDateInstance(DateFormat.MEDIUM).parse(dt);
            cal.setTime(date);
        }
        catch (ParseException e) {
        }
        return cal;
    }

    public long _id;
    public String _title;
    public String _details;

    public boolean _is_complete;
    public boolean _to_be_deleted;

    public boolean _has_progress;
    public int _progress;

    public boolean _has_duedate;
    public GregorianCalendar _duedate = new GregorianCalendar();
    private long _last_edit_date;
    public String _label;

    public static class ByCreationComparator implements Comparator<TodoItem> {

        @Override
        public int compare(TodoItem lhs, TodoItem rhs) {

            return (int)(rhs._id - lhs._id);
        }
    }

    public static class ByLastEditComparator implements Comparator<TodoItem> {

        @Override
        public int compare(TodoItem lhs, TodoItem rhs) {

            return (int)(rhs._last_edit_date - lhs._last_edit_date);
        }
    }

    public static class ByLabelComparator implements Comparator<TodoItem> {

        @Override
        public int compare(TodoItem lhs, TodoItem rhs) {

            return String.CASE_INSENSITIVE_ORDER.compare(lhs._label, rhs._label);
        }
    }

    public static class ByDueDateComparator implements Comparator<TodoItem> {

        @Override
        public int compare(TodoItem lhs, TodoItem rhs) {

            if (lhs._has_duedate && rhs._has_duedate) {

                return (int)(lhs._duedate.getTime().getTime() - rhs._duedate.getTime().getTime());
            }
            else if (lhs._has_duedate) {

                return -1;
            }
            else if (rhs._has_duedate) {

                return 1;
            }
            else {

                return 1;
            }
        }
    }
}
