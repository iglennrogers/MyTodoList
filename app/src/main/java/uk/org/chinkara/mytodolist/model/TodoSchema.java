package uk.org.chinkara.mytodolist.model;

import android.provider.BaseColumns;

/**
 * Created by g_rogers on 10/07/2015.
 */
public final class TodoSchema {

    public TodoSchema() {}

    public static abstract class TodoTable implements BaseColumns {

        public final static String TABLE_NAME = "todoitem";

        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_DETAILS = "details";
        public final static String COLUMN_IS_COMPLETE = "is_complete";
        public final static String COLUMN_HAS_PROGRESS = "has_progress";
        public final static String COLUMN_PROGRESS = "progress";
        public final static String COLUMN_HAS_DUEDATE = "has_duedate";
        public final static String COLUMN_DUEDATE = "duedate";
        public final static String COLUMN_LASTEDIT = "lastedit";
        public final static String COLUMN_LABEL = "label";

        public final static String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DETAILS + " TEXT, " +
                COLUMN_IS_COMPLETE + " TEXT, " +
                COLUMN_HAS_PROGRESS + " INTEGER, " +
                COLUMN_PROGRESS + " INTEGER, " +
                COLUMN_HAS_DUEDATE + " INTEGER, " +
                COLUMN_DUEDATE + " TEXT, " +
                COLUMN_LASTEDIT + " TEXT, " +
                COLUMN_LABEL + " TEXT, ";
        public final static String SQL_DELETE = "DELETE TABLE IF EXISTS " + TABLE_NAME;
    }
}
