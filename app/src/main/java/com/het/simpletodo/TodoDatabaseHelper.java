package com.het.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TodoDatabaseHelper extends SQLiteOpenHelper {
    private static TodoDatabaseHelper sInstance;
    // Database Info
    private static final String DATABASE_NAME = "todoDatabase14";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    private static final String TABLE_TODO = "todo";

    // Todo Table Columns
    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_TEXT = "text";
    private static final String KEY_TODO_DATE = "date";
    private static final String KEY_TODO_COMMENTS = "comments";
    private static final String KEY_TODO_LEVEL = "level";

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO +
                "(" +
                KEY_TODO_ID + " INTEGER PRIMARY KEY autoincrement," + // Define a primary key
                KEY_TODO_TEXT + " TEXT," +
                KEY_TODO_DATE + " TEXT," +
                KEY_TODO_COMMENTS + " TEXT," +
                KEY_TODO_LEVEL + " TEXT" +
                ")";

        db.execSQL(CREATE_TODO_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
            onCreate(db);
        }
    }

    public static synchronized TodoDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TodoDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Add a todo into the database
    public void addTodo(Todo todo) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TODO_TEXT, todo.text);
            values.put(KEY_TODO_DATE, todo.date);
            values.put(KEY_TODO_COMMENTS, todo.comments);
            values.put(KEY_TODO_LEVEL, todo.level);

            db.insertOrThrow(TABLE_TODO, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("addTodo", "Error while trying to add todo to database");
        } finally {
            db.endTransaction();
        }
    }


//    public void updateTodo(Todo todoOld, Todo todoNew) {
//
//        SQLiteDatabase db = getWritableDatabase();
//
//        db.beginTransaction();
//        try {
//            ContentValues values = new ContentValues();
//            values.put(KEY_TODO_TEXT, todoNew.text);
//            values.put(KEY_TODO_DATE, todoNew.date);
//            values.put(KEY_TODO_COMMENTS, todoNew.comments);
//
//
//            int rows = db.update(TABLE_TODO, values, KEY_TODO_TEXT + " = " + todoOld.text +
//                    " and " + KEY_TODO_DATE + " = " + todoOld.date, null);
////            long rows = db.replace(TABLE_TODO,null,values);
//
//            db.setTransactionSuccessful();
//
//        } catch (Exception e) {
//            Log.d("addorUpdateUser", "Error while trying to add or update user");
//        } finally {
//            db.endTransaction();
//        }
//
//    }

    public List<Todo> getAllTodo() {
        List<Todo> todo = new ArrayList<Todo>();

        String TODO_SELECT_QUERY = String.format("SELECT * FROM %s ", TABLE_TODO);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODO_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Todo newTodo = new Todo();
                    newTodo.text = cursor.getString(cursor.getColumnIndex(KEY_TODO_TEXT));
                    newTodo.date = cursor.getString(cursor.getColumnIndex(KEY_TODO_DATE));
                    newTodo.comments = cursor.getString(cursor.getColumnIndex(KEY_TODO_COMMENTS));
                    newTodo.level = cursor.getString(cursor.getColumnIndex(KEY_TODO_LEVEL));
                    todo.add(newTodo);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("getAllTodo", "Error while trying to get todo from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return todo;
    }


    public void deleteAllTodo() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TODO, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("deleteAllTodo", "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteTodo(Todo todoDel) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TODO, KEY_TODO_TEXT + " = '" + todoDel.text + "'"
                    + " and " + KEY_TODO_DATE + " = '" + todoDel.date + "'"
                    , null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("deleteTodo", "Error while trying to delete todo" + e);
            Log.d("deleteTodo", KEY_TODO_TEXT + " = " + todoDel.text + " and " +
                    KEY_TODO_DATE + " = " + todoDel.date);

        } finally {
            db.endTransaction();
        }
    }
}
