package com.example.zero.bean;

/**
 * Created by ZERO on 2017/11/20.
 */

public class ScheduleBean {
    private String station;
    private String line;
    private String final_st;
    private String arr_time_str;
    private boolean toggle;
    private boolean isStation;

    public ScheduleBean(String station, String line, String final_st, String arr_time_str) {
        this.station = station;
        this.line = line;
        this.final_st = final_st;
        this.arr_time_str = arr_time_str;
        this.isStation = false;
    }

    public ScheduleBean(String station, String line, String final_st) {
        this.station = station;
        this.line = line;
        this.final_st = final_st;
        this.isStation = true;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getFinal_st() {
        return final_st;
    }

    public void setFinal_st(String final_st) {
        this.final_st = final_st;
    }

    public String getArr_time_str() {
        return arr_time_str;
    }

    public void setArr_time_str(String arr_time_str) {
        this.arr_time_str = arr_time_str;
    }

    public boolean getToggle() {
        return toggle;
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    public boolean isStation() {
        return isStation;
    }
}
