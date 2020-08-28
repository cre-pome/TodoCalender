package com.example.TodoCalendar.recyclerView;

import android.view.View;
import android.widget.TextView;

import com.example.TodoCalendar.R;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class TaskLowViewHolder extends RecyclerView.ViewHolder {
    public TextView tasknameView;
    public TextView taskendDateView;
    final  ConstraintLayout linearLayout;
    public View  rowView ;

    public TaskLowViewHolder(View itemView) {
        super(itemView);
        tasknameView = (TextView) itemView.findViewById(R.id.item_taskname);
        taskendDateView = (TextView) itemView.findViewById(R.id.item_taskendDate);
        linearLayout =  (ConstraintLayout) itemView.findViewById(R.id.linearLayout);
        rowView = itemView;
    }
}
