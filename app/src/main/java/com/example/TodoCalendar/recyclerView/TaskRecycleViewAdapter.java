package com.example.TodoCalendar.recyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.TodoCalendar.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class TaskRecycleViewAdapter extends RecyclerView.Adapter<TaskLowViewHolder>{

    private List<TaskRowData> taskList;

    public TaskRecycleViewAdapter(List<TaskRowData>list) {
        this.taskList = list;
    }

    private onItemClickListener listener;

    @Override
    public TaskLowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasklist_row, parent,false);
        TaskLowViewHolder vh = new TaskLowViewHolder(inflate);
        return vh;
    }

    @Override
    public void onBindViewHolder(final TaskLowViewHolder holder, int position) {
        TaskRowData taskRowData = taskList.get(position);
        holder.setIsRecyclable(false);

        holder.tasknameView.setText(taskRowData.getTaskName());
        holder.taskendDateView.setText(taskRowData.getEndDate());

        if (taskRowData.getAchieve() == 1) {
            holder.rowView.setBackgroundColor(Color.parseColor("#a9a9a9"));
        }

        // 重要度を取得
        int severity = taskRowData.getSeverity();

        // 重要度に応じてタスク名の色を変える。2(高)なら赤、1(中)なら青
        if(severity == 2){
            holder.tasknameView.setTextColor(Color.parseColor("#FF0000"));
        } else if (severity == 1) {
            holder.tasknameView.setTextColor(Color.parseColor("#0000FF"));
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view, taskList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public interface onItemClickListener {
        void onClick(View view, TaskRowData taskData);
    }



    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
}
