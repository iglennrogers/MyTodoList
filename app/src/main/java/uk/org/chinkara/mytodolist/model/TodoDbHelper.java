package uk.org.chinkara.mytodolist.model;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by g_rogers on 10/07/2015.
 */
public class TodoDbHelper extends SQLiteOpenHelper {

    private static String TAG = "<<Todo sqlite>>: ";

    public static abstract class TodoSchema implements BaseColumns {

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

        public final static String[] SQL_COLUMNS = {
                _ID,
                COLUMN_TITLE,
                COLUMN_DETAILS,
                COLUMN_IS_COMPLETE,
                COLUMN_HAS_PROGRESS,
                COLUMN_PROGRESS,
                COLUMN_HAS_DUEDATE,
                COLUMN_DUEDATE,
                COLUMN_LASTEDIT,
                COLUMN_LABEL
        };

        public final static String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DETAILS + " TEXT, " +
                COLUMN_IS_COMPLETE + " INTEGER, " +
                COLUMN_HAS_PROGRESS + " INTEGER, " +
                COLUMN_PROGRESS + " INTEGER, " +
                COLUMN_HAS_DUEDATE + " INTEGER, " +
                COLUMN_DUEDATE + " TEXT, " +
                COLUMN_LASTEDIT + " INTEGER, " +
                COLUMN_LABEL + " TEXT );";
        public final static String SQL_DROP = "DELETE TABLE IF EXISTS " + TABLE_NAME;
    }

    public final static int DATABASE_VERSION = 1;
    public final static String DATABASE_NAME = "todo.db";

    public TodoDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "construction");
    }

    public void onCreate(SQLiteDatabase db) {

        try {

            db.execSQL(TodoSchema.SQL_CREATE);
            Log.d(TAG, "onCreate");
        }
        catch (SQLException ex) {

            Log.d(TAG, ex.getMessage());
            throw(ex);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(TodoSchema.SQL_DROP);
        onCreate(db);
        Log.d(TAG, "onUpgrade");
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(TodoSchema.SQL_DROP);
        onCreate(db);
        Log.d(TAG, "onDowngrade");
    }

    public ArrayList<Cursor> getData(String Query){

        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        }
        catch(SQLException sqlEx){

            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
        catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }
}
