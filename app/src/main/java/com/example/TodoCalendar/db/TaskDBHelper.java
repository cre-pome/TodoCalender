package com.example.TodoCalendar.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskDBHelper extends SQLiteOpenHelper {

    // データーベースのバージョン
    private static final int DATABASE_VERSION = 1;

    // データーベース名
    private static final String DATABASE_NAME = "task_db.db";
    private static final String TABLE_NAME = "task_db";
    private static final String _ID = "_id";
    private static final String COLUMN_NAME_TASKNAME = "name";
    private static final String COLUMN_NAME_EXPLAIN = "exp";
    private static final String COLUMN_NAME_SEVERITY = "severity";
    private static final String COLUMN_NAME_ACHUEVE = "achieve";
    private static final String COLUMN_NAME_INIT_DAY = "init_date";
    private static final String COLUMN_NAME_END_DAY = "end_date";
    private static final String COLUMN_NAME_UPDATE_DAY = "update_date";
    private static final String COLUMN_NAME_NOTIFI = "notifi";
    private static final String COLUMN_NAME_NOTIFI_TIME = "notifi_time";
    private static final String COLUMN_NAME_NOTIFI_KIND = "notifi_kind";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_TASKNAME + " TEXT," +
                    COLUMN_NAME_EXPLAIN + " TEXT," +
                    COLUMN_NAME_SEVERITY + " INT," +
                    COLUMN_NAME_ACHUEVE  + " INT," +
                    COLUMN_NAME_INIT_DAY + " TEXT," +
                    COLUMN_NAME_END_DAY + " TEXT," +
                    COLUMN_NAME_UPDATE_DAY + " TEXT,"+
                    COLUMN_NAME_NOTIFI + " INT,"+
                    COLUMN_NAME_NOTIFI_TIME + " INT,"+
                    COLUMN_NAME_NOTIFI_KIND + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブル作成
        db.execSQL(
                SQL_CREATE_ENTRIES
        );
        Log.d("debug", "onCreate(SQLiteDatabase db)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // アップデートの判別、古いバージョンは削除して新規作成
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db,
                            int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
