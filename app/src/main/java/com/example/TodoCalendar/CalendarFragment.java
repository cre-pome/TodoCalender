package com.example.TodoCalendar;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;

import com.example.TodoCalendar.db.TaskDBHelper;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CalendarFragment extends Fragment implements View.OnClickListener, OnDateChangeListener {

    private TaskDBHelper helper;
    private SQLiteDatabase db;
    public CalendarFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // クリックイベントを取得したいボタン
        Button btnSend = view.findViewById(R.id.easyTaskAdd);
        CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendarView);

        // Listenerをボタンオブジェクトに追加
        btnSend.setOnClickListener(this);
        calendarView.setOnDateChangeListener(this);

        if(helper == null){
            helper = new TaskDBHelper(getActivity().getApplicationContext());
        }

        if(db == null){
            db = helper.getWritableDatabase();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity().getApplication(), TaskAddActivity.class);

        //　現在時刻を取得
        final Calendar date = Calendar.getInstance();
        final int year = date.get(Calendar.YEAR);
        final int month = date.get(Calendar.MONTH);
        final int day = date.get(Calendar.DATE);

        intent.putExtra("new",true);
        intent.putExtra("year", year);
        intent.putExtra("month", month + 1);
        intent.putExtra("day", day);
        intent.putExtra("hour", 23);
        intent.putExtra("min", 59);

        startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onSelectedDayChange (CalendarView view, int year, int month,
                                    int dayOfMonth) {

        Intent intent = new Intent(getActivity().getApplication(), TaskAddActivity.class);

        intent.putExtra("new",true);
        intent.putExtra("year", year);
        intent.putExtra("month", month + 1);
        intent.putExtra("day", dayOfMonth);
        intent.putExtra("hour", 23);
        intent.putExtra("min", 59);

        startActivity(intent);
    }



}
