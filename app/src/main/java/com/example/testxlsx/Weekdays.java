package com.example.testxlsx;

import java.util.ArrayList;
import java.util.List;

public class Weekdays {
    private String weekday;
//    private Lessons lessons;
    private List<String> lessons;

    public Weekdays() {
        lessons = new ArrayList<>();
    }

    public List<String> getLessons() {
        return lessons;
    }

    public void setLessons(List<String> lessons) {
        this.lessons = lessons;
    }


    //    public Lessons getLessons() {
//        return lessons;
//    }
//
//    public void setLessons(Lessons lessons) {
//        this.lessons = lessons;
//    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

}
