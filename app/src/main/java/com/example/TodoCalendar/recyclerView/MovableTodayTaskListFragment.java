package com.example.TodoCalendar.recyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.TodoCalendar.R;
import com.example.TodoCalendar.TaskAddActivity;
import com.example.TodoCalendar.db.TaskDBHelper;
import com.example.TodoCalendar.db.TaskDBManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MovableTodayTaskListFragment extends Fragment {
    // DBのクラスとヘルパー
    private TaskDBHelper helper;
    private SQLiteDatabase db;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    // DBから取得した全てのタスクリスト
    ArrayList<TaskRowData> taskList = new ArrayList<TaskRowData>();

    //　RecycleViewのアダプタ
    TaskRecycleViewAdapter adapter;

    // ソートボタンの降順、昇順を決めるフラグ
    int taskNameDesc = 1;
    int taskSeverityDesc = 1;
    int taskAcheveDesc = 1;
    int taskEndtimeDesc = 1;

    // 達成済みタスクを非表示にするか決めるフラグ
    boolean hideAchevedTask = false;

    // アプリの設定ファイルを取得
    SharedPreferences preferences;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);



        if(helper == null){
            helper = new TaskDBHelper(getActivity().getApplicationContext());
        }

        // 前回の非表示フラグを取得
        preferences = getContext().getSharedPreferences("TodoConf", Context.MODE_PRIVATE);
        hideAchevedTask = preferences.getBoolean("hideTodayAchevedTask", false);

        if(db == null){
            db = helper.getReadableDatabase();
        }

        // タスクのリストを取得する
        taskList = TaskDBManager.makeTaskRowDataList(db, true);

        // タスクリストをソートする
        String sortKey = preferences.getString("sortKey", "endDate");
        int descFlag = preferences.getInt("sortDesc", 1);
        sortMapKey(sortKey, descFlag);

        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        adapter =  new TaskRecycleViewAdapter(taskList);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_tasklist_recycle, container, false);

        // ListViewにタスクリストをセットする
        setListView(view);

        // ソートボタンをセットする
        setSortButton(view);

        //　達成済みタスクを非表示にする(前画面で非表示だった場合)
        setInvisibleFlag();

        return view ;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // カラム名をタッチしたらソートされるようにする
    private void setSortButton(View view){

        // タスク名のソートボタンを設定
        final TextView taskNameColumn = view.findViewById(R.id.taskName_colum);
        taskNameColumn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sortMapKey("taskName", taskNameDesc);
                taskNameDesc = taskNameDesc * -1;
                adapter.notifyDataSetChanged();
            }
        });

        // 重要度のソートボタンを設定
        final TextView taskSeverityColumn = view.findViewById(R.id.taskSeverity_colum);
        taskSeverityColumn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sortMapKey("severity", taskSeverityDesc);
                taskSeverityDesc = taskSeverityDesc * -1;
                adapter.notifyDataSetChanged();
            }
        });

        // 達成状況のソートボタンを設定
        final TextView taskAcheveColumn = view.findViewById(R.id.taskAcheve_colum);
        taskAcheveColumn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sortMapKey("achievement", taskAcheveDesc);
                taskAcheveDesc = taskAcheveDesc * -1;
                adapter.notifyDataSetChanged();
            }
        });

        // 締め切り日時のソートボタンを設定
        final TextView taskEndtimeColumn = view.findViewById(R.id.taskEndtime_colum);
        taskEndtimeColumn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sortMapKey("endDate", taskEndtimeDesc);
                taskEndtimeDesc = taskEndtimeDesc * -1;
                adapter.notifyDataSetChanged();
            }
        });
    }

    // ソートするキー名とdescを記録
    public void recordKey(final String keyName, final int descFlg){

        // ソートしたキーを記録
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sortKey",  keyName);
        editor.putInt("sortDesc",  descFlg);
        editor.apply();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.invisible_tasklow) {

            // 達成済みタスクを非表示にする
            setInvisibleFlag();

            // オプションメニューの表示文字列を変更する
            if (hideAchevedTask) {
                item.setTitle(getString(R.string.invisible_tasklow));
            } else {
                item.setTitle(getString(R.string.visible_tasklow));
            }


            return true;
        }

        if (id == R.id.menu_end) {
            // 閉じる
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    // 達成しているタスクに非表示フラグをセットする
    private void setInvisibleFlag(){

        // 設定ファイルに現在の非表示フラグを保存する
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideTodayAchevedTask", hideAchevedTask);
        editor.apply();

        // 達成済みのタスクリストのみを取得する
        taskList.clear();
        if (hideAchevedTask) {
            taskList.addAll(TaskDBManager.readNoAchevedTask(db, true));
        } else {
            taskList.addAll(TaskDBManager.makeTaskRowDataList(db, true));
        }

        // 前のソート条件でソート
        sortByBeforeKey();
        adapter.notifyDataSetChanged();

        // 表示・非表示を切り替える
        hideAchevedTask = !hideAchevedTask;

    }

    // キー名でソートする
    private void sortMapKey(final String keyName, final int descFlg){

        recordKey(keyName, descFlg);

        Collections.sort(taskList, new Comparator<TaskRowData>(){
            @Override
            public int compare(TaskRowData rec1, TaskRowData rec2) {
                String colName1 = String.valueOf(rec1.getMenber(keyName));
                String colName2 = String.valueOf(rec2.getMenber(keyName));
                return colName1.compareTo(colName2) * descFlg;
            }
        });
    }

    // タスクリストをListViewにセットする
    private void setListView(View view){

        LinearLayoutManager lm = new LinearLayoutManager(getContext());

        final RecyclerView listView = view.findViewById(R.id.taskList_recycle);

        listView.setAdapter(adapter);
        listView.setLayoutManager(lm);

        // RecyclerViewに枠線を設定する
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), VERTICAL_LIST);
        listView.addItemDecoration(itemDecoration);

        adapter.setOnItemClickListener(new TaskRecycleViewAdapter.onItemClickListener(){

            @Override
            public void onClick(View view, TaskRowData taskData) {

                // タスク追加画面にタスク情報を渡す
                Intent intent = new Intent(getActivity().getApplication(), TaskAddActivity.class);
                intent.putExtra("new",false);
                intent.putExtra("taskData", taskData);

                // 締め切り日時データを日付と時間に分ける
                String endDate = taskData.getEndDate();
                String[] spritEndDate = endDate.split(" ", 0);
                String[] spritDate = spritEndDate[0].split("/", 0);
                String[] spritTime = spritEndDate[1].split(":", 0);

                // 年月日と時分を取得する
                intent.putExtra("year", Integer.parseInt(spritDate[0]) );
                intent.putExtra("month", Integer.parseInt(spritDate[1]) );
                intent.putExtra("day", Integer.parseInt(spritDate[2]) );
                intent.putExtra("hour", Integer.parseInt(spritTime[0]) );
                intent.putExtra("min", Integer.parseInt(spritTime[1]));

                intent.putExtra("notifyFlag", taskData.getNotifyFlag());
                intent.putExtra("notifyKind", taskData.getNotifiKind());
                intent.putExtra("notifyTime", taskData.getNotifiTime());

                startActivity(intent);
            }
        });


        // 長押しするとリストの項目をドラッグ&ドロップする
        ItemTouchHelper itemDecor = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.ACTION_STATE_IDLE) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        final int fromPos = viewHolder.getAdapterPosition();
                        final int toPos = target.getAdapterPosition();
                        adapter.notifyItemMoved(fromPos, toPos);
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    }
                });

        itemDecor.attachToRecyclerView(listView);
    }

    // 前のソート条件でソートする
    private void sortByBeforeKey(){
        String sortKey = preferences.getString("sortKey", "endDate");
        int descFlag = preferences.getInt("sortDesc", 1);
        sortMapKey(sortKey, descFlag);
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("onStop");
        // 設定ファイルに現在の非表示フラグを保存する
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String jsonInstanceString = gson.toJson(taskList);
        editor.putString("taskList",  jsonInstanceString);
        editor.apply();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("beforeList", taskList);
        outState.putBoolean("hideTodayAchevedTask", hideAchevedTask);
        super.onSaveInstanceState(outState);
    }


}
