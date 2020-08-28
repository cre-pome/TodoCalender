package com.example.TodoCalendar.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.TodoCalendar.recyclerView.TaskRowData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TaskDBManager {


    // DBにタスク情報を登録する
    static public long insertData(SQLiteDatabase db, String taskName, String explain, int severity, int achieve,
                                  String endDate, String endTime, int notify, int notifiTime, String notifiKind){

        //　日時データを作成
        String dateString = endDate + " " + endTime;

        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        String initDate = format.format( dateObj );

        ContentValues values = new ContentValues();
        values.put("name", taskName);
        values.put("exp", explain);
        values.put("severity", severity);
        values.put("achieve", achieve);
        values.put("init_date", initDate);
        values.put("end_date", dateString);
        values.put("update_date",  new Date().toString());
        values.put("notifi", notify);
        values.put("notifi_time", notifiTime);
        values.put("notifi_kind", notifiKind);

       return db.insert("task_db", null, values);

    }

    // DBのタスク情報を更新する
    static public void updateData(SQLiteDatabase db, String taskName, String explain, int severity, int achieve, String endDate,
                                  String endTime, int id, int notify, int notifiTime, String notifiKind){

        //　日時データを作成
        String dateString = endDate + " " + endTime;

        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        String initDate = format.format( dateObj );

        ContentValues values = new ContentValues();
        values.put("name", taskName);
        values.put("exp", explain);
        values.put("severity", severity);
        values.put("achieve", achieve);
        values.put("init_date", initDate);
        values.put("end_date", dateString);
        values.put("notifi", notify);
        values.put("notifi_time", notifiTime);
        values.put("notifi_kind", notifiKind);

        Date myDate = new Date();
        long timeMilliseconds = myDate.getTime();

        //To convert back to Date
        Date myDateNew = new Date(timeMilliseconds);

        values.put("update_date", myDateNew.toString());

        db.update("task_db",  values, "_id = ?", new String[]{String.valueOf(id)});

    }

    // DBからタスクデータを取得してTaskRowDateのリストを返す
    static public ArrayList<TaskRowData> makeTaskRowDataList(SQLiteDatabase db){
        ArrayList<TaskRowData> taskRowDataList = new ArrayList<TaskRowData>();

        String order_by = "update_date DESC";

        Cursor cursor = db.query(
                "task_db",
                new String[] {"_id", "name", "exp", "severity" , "achieve", "end_date", "notifi", "notifi_time", "notifi_kind"},
                null,
                null,
                null,
                null,
                order_by
        );

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            // DBから１行分の各カラムのデータを取得
            int id = cursor.getInt(0);
            String taskName = cursor.getString(1);
            String exp = cursor.getString(2);
            int severity = cursor.getInt(3);
            int achievement = cursor.getInt(4);
            String endDate = cursor.getString(5);
            int notifi = cursor.getInt(6);
            int notifiTime = cursor.getInt(7);
            String notifiKind = cursor.getString(8);

            TaskRowData taskRowData = new TaskRowData();

            taskRowData.setId(id);
            taskRowData.setTaskName(taskName);
            taskRowData.setExplain(exp);
            taskRowData.setSeverity(severity);
            taskRowData.setAchieve(achievement);
            taskRowData.setEndDate(endDate);
            taskRowData.setInvisibleFlag(false);
            taskRowData.setNotifyFlag(notifi);
            taskRowData.setNotifiTime(notifiTime);
            taskRowData.setNotifiKind(notifiKind);

            taskRowDataList.add(taskRowData);

            cursor.moveToNext();
        }

        return taskRowDataList;
    }


    // DBから達成していないタスクデータを取得する
    static public ArrayList<TaskRowData> readNoAchevedTask(SQLiteDatabase db){

        // ListViewに表示するリスト項目をArrayListで準備する
        ArrayList<TaskRowData> taskRowDataList = new ArrayList<TaskRowData>();

        Log.d("debug","**********Cursor");
        String selection = "achieve = ?";
        String[] selectionArgs = {"0"};

        Cursor cursor = db.query(
                "task_db",
                new String[] {"_id", "name", "exp", "severity" , "achieve", "end_date", "notifi", "notifi_time", "notifi_kind"},
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            // DBから１行分の各カラムのデータを取得
            int id = cursor.getInt(0);
            String taskName = cursor.getString(1);
            String exp = cursor.getString(2);
            int severity = cursor.getInt(3);
            int achievement = cursor.getInt(4);
            String endDate = cursor.getString(5);
            int notifi = cursor.getInt(6);
            int notifiTime = cursor.getInt(7);
            String notifiKind = cursor.getString(8);

            TaskRowData taskRowData = new TaskRowData();

            taskRowData.setId(id);
            taskRowData.setTaskName(taskName);
            taskRowData.setExplain(exp);
            taskRowData.setSeverity(severity);
            taskRowData.setAchieve(achievement);
            taskRowData.setEndDate(endDate);
            taskRowData.setInvisibleFlag(false);
            taskRowData.setNotifyFlag(notifi);
            taskRowData.setNotifiTime(notifiTime);
            taskRowData.setNotifiKind(notifiKind);

            taskRowDataList.add(taskRowData);

            cursor.moveToNext();
        }

        // 忘れずに！
        cursor.close();

        return taskRowDataList;
    }

    // IDに一致するデータを削除する
    static public void deleteData(SQLiteDatabase db, int id) {
        db.delete("task_db", "_id = ?", new String[]{String.valueOf(id)});
    }


}
