package com.example.TodoCalendar.recyclerView;

import java.io.Serializable;

public class TaskRowData implements Serializable {
    int id;
    String taskName;
    String explain;
    int severity;
    int achieve;

    String endDate;
    String endTime;

    Boolean invisibleFlag;


    String notifiKind;

    public String getNotifiKind() {
        return notifiKind;
    }

    public void setNotifiKind(String notifiKind) {
        this.notifiKind = notifiKind;
    }

    public int getNotifyFlag() {
        return notifyFlag;
    }

    public void setNotifyFlag(int notifyFlag) {
        this.notifyFlag = notifyFlag;
    }

    public int getNotifiTime() {
        return notifiTime;
    }

    public void setNotifiTime(int notifiTime) {
        this.notifiTime = notifiTime;
    }

    int notifyFlag;
    int notifiTime;

    public void setId(int id) {
        this.id = id;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public void setAchieve(int achieve) {
        this.achieve = achieve;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public void setInvisibleFlag(Boolean invisibleFlag) {
        this.invisibleFlag = invisibleFlag;
    }

    public Boolean getInvisibleFlag() {
        return invisibleFlag;
    }

    public int getId() {
        return id;
    }

    public String getTaskName(){
        return taskName;
    }

    public String getEndDate(){
        return endDate;
    }

    public int getAchieve() {
        return achieve;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getSeverity() {
        return severity;
    }

    public String getExplain() {
        return explain;
    }

    // メンバ名を入れると対応するメンバ変数の値を返す
    public Object getMenber(String menberName){

        switch (menberName) {
            case "taskName":
                return getTaskName();
            case "severity":
                return getSeverity();
            case "achievement" :
                return getAchieve();
            case "endDate" :
                return getEndDate();
        }

        return null;
    }
}
