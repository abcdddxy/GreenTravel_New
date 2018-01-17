package com.example.zero.bean;

/**
 * Created by ZERO on 2017/11/20.
 */

public class RouteDetailBean {
    private String station;
    private String line;
    private String final_st;

    public RouteDetailBean(String station, String line, String final_st) {
        this.station = station;
        this.line = line;
        this.final_st = final_st;
    }

    public RouteDetailBean(String line) {
        this.line = line;
        this.station = "abc";
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
}
