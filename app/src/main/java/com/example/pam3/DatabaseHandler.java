package com.example.pam3;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.pam3.TasksRecyclerView.TaskRVData;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, "toDoListDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE category (id INTEGER PRIMARY KEY AUTOINCREMENT, categoryName TEXT)");
        db.execSQL("CREATE TABLE toDO (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, " +
                "description TEXT, categoryId INTEGER, creationDate TEXT, eDate TEXT, " +
                "eTime TEXT, notification INTEGER, status INTEGER, imgPath TEXT, FOREIGN KEY (categoryId) " +
                "REFERENCES category (id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS toDO");
        db.execSQL("DROP TABLE IF EXISTS category");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @SuppressLint("Range")
    public void setDb() {
        db.execSQL("DROP TABLE IF EXISTS toDO");
        db.execSQL("DROP TABLE IF EXISTS category");
        onCreate(db);

        ContentValues cv = new ContentValues();
        cv.put("categoryName", "Programming");
        db.insert("category", null, cv);

        cv = new ContentValues();
        cv.put("categoryName", "Shopping");
        db.insert("category", null, cv);

        cv = new ContentValues();
        cv.put("categoryName", "Other");
        db.insert("category", null, cv);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    @SuppressLint("Range")
    public void insertTask(TaskRVData data) {
        Cursor cursor = null;
        int categoryId = 0;
        db.beginTransaction();
        try {
            String[] selectionArgs = {data.getCategory()};
            cursor = db.rawQuery("SELECT id FROM category WHERE categoryName = ?", selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                categoryId = cursor.getInt(cursor.getColumnIndex("id"));
            }
        }
        finally {
            db.endTransaction();
            cursor.close();
        }

        if (categoryId != 0) {
            ContentValues cv = new ContentValues();
            cv.put("title", data.getTitle());
            cv.put("description", data.getDesc());
            cv.put("categoryId", categoryId);
            cv.put("creationDate", data.getCreationDate());
            cv.put("eDate", data.getExecutionDate());
            cv.put("eTime", data.getExecutionTime());
            cv.put("notification", data.getNotification());
            cv.put("status", data.getStatus());
            cv.put("imgPath", data.getImgPath());
            db.insert("toDo", null, cv);
        }
    }

    @SuppressLint("Range")
    public void getTasks(String category, @Nullable Integer status, @Nullable String title, ArrayList<TaskRVData> data) {
        data.clear();
        Cursor cursor = null;
        db.beginTransaction();
        try {
            String query = "SELECT t.id, title, description, categoryName, creationDate, eDate, " +
                    "eTime, notification, status, imgPath FROM toDo AS t " +
                    "JOIN category AS c ON t.categoryId = c.id WHERE 1=1 ";
            ArrayList<String> selectionArgs = new ArrayList<>();
            if (!category.equals("Any")) {
                query += "AND categoryName = ? ";
                selectionArgs.add(category);
            }
            if (status != null) {
                query += "AND status = ? ";
                selectionArgs.add(String.valueOf(status));
            }
            if (title != null) {
                query += "AND title LIKE ? ";
                selectionArgs.add("%" + title + "%");
            }
            query += "ORDER BY eDate, eTime";
            cursor = db.rawQuery(query, selectionArgs.toArray(new String[0]));
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String taskTitle = cursor.getString(cursor.getColumnIndex("title"));
                    String description = cursor.getString(cursor.getColumnIndex("description"));
                    String taskCategory = cursor.getString(cursor.getColumnIndex("categoryName"));
                    String creationDate = cursor.getString(cursor.getColumnIndex("creationDate"));
                    String executionDate = cursor.getString(cursor.getColumnIndex("eDate"));
                    String executionTime = cursor.getString(cursor.getColumnIndex("eTime"));
                    int notification = cursor.getInt(cursor.getColumnIndex("notification"));
                    int taskStatus = cursor.getInt(cursor.getColumnIndex("status"));
                    String imgPath = cursor.getString(cursor.getColumnIndex("imgPath"));
                    TaskRVData taskRVData = new TaskRVData(id, taskTitle, description, taskCategory, creationDate, executionDate, executionTime, notification, taskStatus, imgPath);
                    data.add(taskRVData);
                } while (cursor.moveToNext());
            }
        }
        finally {
            db.endTransaction();
            cursor.close();
        }
    }

    @SuppressLint("Range")
    public ArrayList<String> getCategories() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Any");

        Cursor cursor = null;
        db.beginTransaction();
        try {
            cursor = db.rawQuery("SELECT * FROM category ORDER BY categoryName", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(cursor.getColumnIndex("categoryName")));
                } while (cursor.moveToNext());
            }
        }
        finally {
            db.endTransaction();
            cursor.close();
        }

        return list;
    }

    public void updateStatus(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        db.update("toDo", cv, "id=?", new String[]{String.valueOf(id)});
    }

    public void updateNotification(int id, int notification) {
        ContentValues cv = new ContentValues();
        cv.put("notification", notification);
        db.update("toDo", cv, "id=?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public void updateTask(TaskRVData data) {
        Cursor cursor = null;
        int categoryId = 0;
        db.beginTransaction();
        try {
            String[] selectionArgs = {data.getCategory()};
            cursor = db.rawQuery("SELECT id FROM category WHERE categoryName = ?", selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                categoryId = cursor.getInt(cursor.getColumnIndex("id"));
            }
        }
        finally {
            db.endTransaction();
            cursor.close();
        }

        if (categoryId != 0) {
            ContentValues cv = new ContentValues();
            cv.put("title", data.getTitle());
            cv.put("description", data.getDesc());
            cv.put("categoryId", categoryId);
            cv.put("eDate", data.getExecutionDate());
            cv.put("eTime", data.getExecutionTime());
            cv.put("imgPath", data.getImgPath());
            db.update("toDo", cv, "id=?", new String[]{String.valueOf(data.getId())});
        }
    }

    public boolean deleteCategory(String categoryName) {
        try {
            int deletedRows = db.delete("category", "categoryName=?", new String[]{categoryName});
            return deletedRows > 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    public void insertCategory(String categoryName) {
        ContentValues cv = new ContentValues();
        cv.put("categoryName", categoryName);
        db.insert("category", null, cv);
    }
}
