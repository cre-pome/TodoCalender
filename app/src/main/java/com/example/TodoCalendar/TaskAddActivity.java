package com.example.TodoCalendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.TodoCalendar.db.TaskDBHelper;
import com.example.TodoCalendar.db.TaskDBManager;
import com.example.TodoCalendar.recyclerView.TaskRowData;
import com.example.TodoCalendar.util.DateUtill;
import com.example.TodoCalendar.util.convertUtil;

import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class TaskAddActivity extends AppCompatActivity {

    // DBのクラスとヘルパー
    private TaskDBHelper helper ;
    private SQLiteDatabase db ;

    // 日付入力欄を取得
    EditText selectDate ;

    // 時刻入力欄を取得
    EditText selectTime;

    // タスクが新規登録か編集かを決める
    Intent i = getIntent();
    boolean isNewTask;


    // 前画面から渡された年月日を取得
    int year ;
    int month ;
    int day;
    int hour ;
    int min ;

    // 変更するタスク情報を取得する
    TaskRowData taskData;

    // タスク一覧画面から非表示フラグを取得
    boolean visibleFrag;


    // 前の通知許可フラグ
    int isNotifyBefore;

    // 現在の通知許可フラグ
    int isNotifyNow ;

    Spinner spinner;

    // 前の締め切り日時
    String beforeDate;

    // 前の通知種別
    String beforeNotifiKind;

    // 前の通知時間
    int beforeNotifiTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);

        if (savedInstanceState != null) {
            System.out.println("onCreate");
        }

        // 日付入力欄を取得
        selectDate = findViewById(R.id.dateSelect);

        // 時刻入力欄を取得
        selectTime = findViewById(R.id.timeSelect);

        Intent i = getIntent();
        isNewTask = i.getBooleanExtra("new",false);

        // 年月日時分を取得
        year    = i.getIntExtra("year",1);
        month   = i.getIntExtra("month",1);
        day     = i.getIntExtra("day",1);
        hour    = i.getIntExtra("hour",0);
        min     = i.getIntExtra("min",0);

        // 前の日時を取得
        beforeDate = String.format("%d/%02d/%02d", year, month, day) + " " + String.format("%02d:%02d", hour, min) + ":00";

        // タスク一覧画面から非表示フラグを取得
        visibleFrag = i.getBooleanExtra("visibleFrag", false);

        // 前に通知が設定されているかどうか
        isNotifyBefore = i.getIntExtra("notifyFlag", 1);
        isNotifyNow = isNotifyBefore;

        // 前の通知種別を取得する
        beforeNotifiKind = i.getStringExtra("notifyKind");
        if (beforeNotifiKind == null) {
            beforeNotifiKind = "分前";
        }

        // 前の通知時間を取得する
        beforeNotifiTime = i.getIntExtra("notifyTime", 10);

        // 変更するタスク情報を取得する
        taskData = (TaskRowData) i.getSerializableExtra("taskData");

        if(helper == null){
            helper = new TaskDBHelper(getApplicationContext());
        }

        db = helper.getWritableDatabase();

        // 日付・時間ピッカーを設定する
        setDatePicker(year, month, day);


        // 新規作成か変更かで表示する画面を変える
        if(isNewTask){
            setTaskInsertView();        // タスク新規追加画面を作成する
        } else {
            setTaskUpdateView(taskData);  // タスク変更画面を作成する
        }

        setBackButton(); //戻るボタンを設定

        //　スピナーを設定
        setSpiner();

        // チェックボックスを設定
        setCheckBox();

        setNotifyTimeText(beforeNotifiTime);

    }

    // タスク新規追加画面を作成する
    private void setTaskInsertView(){

        final Button commitButton = findViewById(R.id.commitButton);    //　登録ボタンを設定

        // 登録ボタンを押したらDBにタスクを登録する
        commitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                confirmTaskAdd();
            }
        });

        // 新規登録なら達成状況を非表示にする
        TextView txt = findViewById(R.id.achieve_explain);
        txt.setVisibility(View.GONE);

        // 新規登録なら達成度選択ラジオボタンを非表示にする
        RadioGroup rg = findViewById(R.id.radioGroup_achieve);
        rg.setVisibility(View.GONE);

    }


    // 戻るボタンを作成する
    private void setBackButton(){

        // 戻るボタンを取得
        final Button backButton = findViewById(R.id.backButton);

        // 戻るボタンを押すと前画面に戻る
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    // 日付・時間ピッカーを設定する
    private void setDatePicker(final int year,final int month,final int day){

        // 日付と時刻の初期値を入力
        selectDate.setText(String.format("%d/%02d/%02d", year, month, day));
        selectTime.setText(String.format("%02d:%02d", hour, min));

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //DatePickerDialogインスタンスを取得
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TaskAddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                //setした日付を取得して表示
                                selectDate.setText(String.format("%d/%02d/%02d", year, month + 1, dayOfMonth));
                            }
                        },
                        year,
                        month - 1,
                        day
                );

                // 日付選択ダイアログを表示
                datePickerDialog.show();

            }
        });

        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialogFragment = new TimePickerDialog(
                        TaskAddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        },
                        hour,
                        min,
                        true
                );

                timePickerDialogFragment.show();

            }

        });


    }


    //　タスク変更画面を作成する
    private void setTaskUpdateView(TaskRowData taskData ){

        //　登録ボタンを設定
        final Button commitButton = findViewById(R.id.commitButton);

        // ボタンの文字列を変更する
        commitButton.setText("変更");

        // タスク情報のIDを取得する
        final int id = taskData.getId();

        commitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                confirmTaskUpdate(id);
            }
        });

        //　タスク名前を画面に埋め込む
        EditText taskNameText = findViewById(R.id.taskName);
        taskNameText.setText(taskData.getTaskName());

        //　タスク詳細を画面に埋め込む
        EditText taskExplainText = findViewById(R.id.taskExplain);
        taskExplainText.setText(taskData.getExplain());

        // 重要度に応じてラジオボタンをチェック
        RadioButton severityRadioButton;
        int severity = taskData.getSeverity();
        switch (severity){
            case 2:
                severityRadioButton = findViewById(R.id.severityHigh);
                severityRadioButton.setChecked(true);
                break;
            case 1:
                severityRadioButton = findViewById(R.id.severityMid);
                severityRadioButton.setChecked(true);
                break;
            case 0:
                severityRadioButton = findViewById(R.id.severityLow);
                severityRadioButton.setChecked(true);
                break;

        }

        // 達成度に応じてラジオボタンをチェック
        RadioButton achieveRadioButton;
        int achievement = taskData.getAchieve();
        switch (achievement){
            case 1:
                achieveRadioButton = findViewById(R.id.achieve_yes);
                achieveRadioButton.setChecked(true);
                break;
            case 0:
                achieveRadioButton = findViewById(R.id.achieve_no);
                achieveRadioButton.setChecked(true);
                break;
        }

        // 日時はスペースで区切られているので日付と時刻で分ける
        String str = taskData.getEndDate();
        String[] dateTime = str.split(" ");  //
        String date = dateTime[0];
        String time = dateTime[1];

        EditText dateText = findViewById(R.id.dateSelect);
        EditText timeText = findViewById(R.id.timeSelect);

        dateText.setText(date);
        timeText.setText(time);

    }

    //　登録ボタンが押された時にダイアログを表示する
    public void confirmTaskAdd() {

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskAddActivity.this);
        builder.setMessage("タスクを登録しますか")
                .setPositiveButton("登録する", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //　タスク情報を登録する
                        taskRegist();
                    }
                })
                .setNegativeButton("戻る", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });

        builder.show();
    }

    //　変更ボタンが押された時にダイアログを表示する
    public void confirmTaskUpdate(final int taskId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskAddActivity.this);
        builder.setMessage("タスクを変更しますか")
                .setPositiveButton("変更する", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //　タスク情報を変更する
                        taskUpdate(taskId);
                    }
                })
                .setNegativeButton("戻る", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        builder.show();
    }

    // タスク情報を登録する
    public void taskRegist(){

        // タスク名を取得
        final String taskName = getStringByText(R.id.taskName);

        // タスク詳細を取得
        final String explain = getStringByText(R.id.taskExplain);

        // チェックされている重要度ラジオボタンを取得
        RadioButton severityRadioButton = getRadioButtonByGroup((R.id.radioGroup_severity));

        // 重要度を取得して数値に変換する
        final int severity = convertUtil.convertSeveritytoNum( getValueByRadioButton(severityRadioButton) );

        // チェックされている達成度ラジオボタンを取得
        RadioButton achieveRadioButton = getRadioButtonByGroup((R.id.radioGroup_achieve));

        // 達成状況を取得する(DBに入れるときは数値にする)
        final int achieve = convertUtil.convertAchievetoNum( getValueByRadioButton(achieveRadioButton) );

        // 日付入力欄と時間入力欄の値を取得する
        final String endDate = getStringByText(R.id.dateSelect);
        final String endTime = getStringByText(R.id.timeSelect);

        // 通知種目を取得する
        String notifyKind = (String)spinner.getSelectedItem();

        //　何分前に通知するかを取得する
        int notifiTime = Integer.valueOf(getStringByText(R.id.notifyTime));

        // 締め切り日をDateデータに変換して通知日を取得する
        String dateText = endDate + " " + endTime + ":00";
        Date endNotifyDate = DateUtill.stringToDate(dateText);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endNotifyDate);


        if (notifyKind.equals("分前")) {
            calendar.add(Calendar.MINUTE, -notifiTime);
        } else if (notifyKind.equals("時間前")) {
            calendar.add(Calendar.HOUR, -notifiTime);

        } else if (notifyKind.equals("日前")) {
            calendar.add(Calendar.DAY_OF_MONTH, -notifiTime);
        }


        // バリデーションチェックが問題ないならDBに登録する
        if (validationCheck(taskName, endDate, endTime)) {

            // DBにタスク情報を登録し、IDを取得
            int insertId = (int) TaskDBManager.insertData(db, taskName, explain,severity, achieve, endDate, endTime, isNotifyNow, notifiTime, notifyKind);

            // 通知を設定する

            if (isNotifyNow == 1) {
                createNotify(insertId, calendar, taskName, notifiTime, notifyKind);
            }
            // 確認ダイアログを表示する
            confirmOkDialog("タスクを登録しました");
        }


    }

    // 通知を設定する
    private void createNotify(int requestCode, Calendar calendar, String taskName, int notifyTime, String notifyKind){

        Intent intent = new Intent(getApplicationContext(), AlarmNotification.class);

        // 通知メッセージ
        String message = "締め切りまであと" +  notifyTime + notifyKind + "です";

        intent.putExtra("RequestCode", requestCode);
        intent.putExtra("taskName", taskName);
        intent.putExtra("message", message);
        intent.putExtra("notifyTime", notifyTime);
        intent.putExtra("notifyKind", notifyKind);

        PendingIntent pending = PendingIntent.getBroadcast(
                getApplicationContext(), requestCode, intent, 0);

        // アラームをセットする
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (am != null) {
            am.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pending);

        }



    }


    //　通知を取り消す
    private void cancelNotify(int requestCode){
        Intent indent = new Intent(getApplicationContext(), AlarmNotification.class);
        PendingIntent pending = PendingIntent.getBroadcast(
                getApplicationContext(), requestCode, indent, 0);



        // アラームを解除する
        AlarmManager am = (AlarmManager)TaskAddActivity.this.
                getSystemService(ALARM_SERVICE);
        if (am != null) {
            pending.cancel();
            am.cancel(pending);

            Log.d("debug", "cancel");
        }
        else{
            Log.d("debug", "null");
        }
    }


    // タスク情報を変更する
    public void taskUpdate(int id){

        // タスク名を取得
        final String taskName = getStringByText(R.id.taskName);

        // タスク詳細を取得
        final String explain = getStringByText(R.id.taskExplain);

        // チェックされている重要度ラジオボタンを取得
        RadioButton severityRadioButton = getRadioButtonByGroup(R.id.radioGroup_severity);

        // 重要度を取得して数値に変換する
        final int severity = convertUtil.convertSeveritytoNum( getValueByRadioButton(severityRadioButton) );

        // チェックされている達成度ラジオボタンを取得
        RadioButton achieveRadioButton = getRadioButtonByGroup(R.id.radioGroup_achieve);

        // 達成状況を取得する(DBに入れるのでintにする)
        final int achieve = convertUtil.convertAchievetoNum( getValueByRadioButton(achieveRadioButton) );

        // 日付入力欄と時間入力欄の値を取得する
        final String endDate = getStringByText(R.id.dateSelect);
        final String endTime = getStringByText(R.id.timeSelect);

        // 通知種目を取得する
        String notifyKind = (String)spinner.getSelectedItem();

        //　何分前に通知するかを取得する
        int notifiTime = Integer.valueOf(getStringByText(R.id.notifyTime));

        // 締め切り日をDateデータに変換して通知日を取得する
        String dateText = endDate + " " + endTime + ":00";
        Date endNotifyDate = DateUtill.stringToDate(dateText);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endNotifyDate);

        if (notifyKind.equals("分前")) {
            calendar.add(Calendar.MINUTE, -notifiTime);
        } else if (notifyKind.equals("時間前")) {
            calendar.add(Calendar.HOUR, -notifiTime);

        } else if (notifyKind.equals("日前")) {
            calendar.add(Calendar.DAY_OF_MONTH, -notifiTime);
        }


        // バリデーションチェックが問題ないならDBに登録する
        if (validationCheck(taskName, endDate, endTime)) {

            // DBにタスク情報を登録
            TaskDBManager.updateData(db, taskName, explain,severity, achieve, endDate, endTime, id, isNotifyNow, notifiTime, notifyKind);

            // 通知なしから通知ありに変わったら通知をセットする
            if (achieve == 1 ) {
                cancelNotify(id);
            } else if (isNotifyNow == 0 && isNotifyNow != isNotifyBefore) {
                cancelNotify(id);
            } else if (!beforeDate.equals(dateText)) {
                cancelNotify(id);
                createNotify(id, calendar, taskName, notifiTime, notifyKind);
            } else if (!beforeNotifiKind.equals(notifyKind)) {
                cancelNotify(id);
                createNotify(id, calendar, taskName, notifiTime, notifyKind);
            } else if (beforeNotifiTime != notifiTime) {
                cancelNotify(id);
                createNotify(id, calendar, taskName, notifiTime, notifyKind);
            }


            // 確認ダイアログを表示する
            confirmOkDialog("タスクを変更しました");
        }


    }

    //　タスク情報のバリデーションチェックを行う
    private Boolean validationCheck(String taskName, String endDate, String endTime){

        if (taskName.isEmpty()) {
            confirmDialog("タスク名が未入力です");
            return false;
        }

        if (endDate.isEmpty()) {
            confirmDialog("締め切り日が未入力です");
            return false;
        }

        if (endTime.isEmpty()) {
            confirmDialog("締め切り時間が未入力です");
            return false;
        }

        return true;
    }

    //　ダイアログ
    private void confirmDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskAddActivity.this);
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        builder.show();
    }

    //　OKボタンがあるダイアログ
    private void confirmOkDialog(String message) {

        // 確認ダイアログを表示する
        new AlertDialog.Builder(TaskAddActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //  MainActivityに遷移する際どのタブに遷移するか決める
                        final Intent intent = new Intent(getApplication(), MainActivity.class);

                        if (isNewTask) {
                            intent.putExtra("tab_pos", 0);       // タスク新規作成画面ならOKを押すとカレンダー画面に遷移する
                        } else {
                            intent.putExtra("tab_pos", 1);       // タスク新規作成画面ならOKを押すとタスクリスト画面に遷移する
                        }


                        startActivity(intent);
                    }
                })
                .show();
    }

    /* ラジオグループIDからチェックされているラジオボタンのIDを取得する */
    public RadioButton getRadioButtonByGroup(int rgId){


        // 重要度ラジオグループのオブジェクトを取得
        RadioGroup rg = findViewById(rgId);

        // チェックされている重要度ラジオボタンのIDを取得
        int severityId = rg.getCheckedRadioButtonId();

        // チェックされている重要度ラジオボタンオブジェクトを取得
        return findViewById(severityId);
    }

    /* エディットテキストIDからチェックされているラジオボタンのIDを取得する */
    public String getStringByText(int txId){

        // タスク名のテキストデータを取得
        EditText taskNameText = findViewById(txId);

        // チェックされている重要度ラジオボタンオブジェクトを取得
        return taskNameText.getText().toString();
    }

    /* ラジオボタンから値を取得する  */
    public String getValueByRadioButton(RadioButton rb ){
        return rb.getText().toString();
    }

    // スピナーを設置する
    private void setSpiner(){

        String[] spinnerItems = {"分前", "時間前", "日前"};

        spinner = findViewById(R.id.notify_kind2);

        // ArrayAdapter
        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerItems);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // spinner に adapter をセット
        spinner.setAdapter(adapter);

        EditText et = (EditText)findViewById(R.id.notifyTime);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);

        if (beforeNotifiKind.equals("分前")) {
            spinner.setSelection(0);
        } else if (beforeNotifiKind.equals("時間前")) {
            spinner.setSelection(1);
        } else if (beforeNotifiKind.equals("日前")) {
            spinner.setSelection(2);
        } else {
            spinner.setSelection(0);
        }



        // リスナーを登録
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {

                String item = (String)spinner.getSelectedItem();
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

    }

    // チェックボックスを設定する
    private void setCheckBox() {
        final CheckBox checkBox = (CheckBox) findViewById(R.id.notify_set);


        if (isNotifyBefore == 1) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            // タップされると呼び出される
            @Override
            public void onClick(View v) {
                // チェックステータス取得
                boolean check = checkBox.isChecked();
                if(check){
                    isNotifyNow = 1;
                }
                else{
                    isNotifyNow = 0;
                }
            }
        });

    }

    private void setNotifyTimeText(int notifyTime){
        //　タスク名前を画面に埋め込む
        EditText taskNameText = findViewById(R.id.notifyTime);
        taskNameText.setText(String.valueOf(beforeNotifiTime));
    }

}
