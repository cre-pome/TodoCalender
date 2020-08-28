package com.example.TodoCalendar.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtill {

    static public Date stringToDate(String dateText){


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try {
            Date date = sdf.parse(dateText);
            return date;
        } catch(ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

}
