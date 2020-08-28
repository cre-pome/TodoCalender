package com.example.TodoCalendar.util;

public class convertUtil {

    // 重要度を数値に変換する
    static public int convertSeveritytoNum(String severity){

        switch (severity){
            case "高":
                return 2;
            case "中":
                return 1;
            default:
                return 0;
        }

    }

    // 達成状況を数値に変換する
    static public int convertAchievetoNum(String severity){

        switch (severity) {
            case "達成":
                return 1;
            default:
                return 0;
        }
    }


    //　数値を重要度に変換する
    static public  String numberToAchievement(int achievement){
        switch (achievement){
            case 1:
                return "達成";

            default:
                return "未達成";
        }
    }


    //　数値を重要度に変換する
    static public String numberToSeverity(int severity){
        switch (severity){
            case 2:
                return "高";

            case 1:
                return "中";

            default:
                return "低";

        }
    }

}
