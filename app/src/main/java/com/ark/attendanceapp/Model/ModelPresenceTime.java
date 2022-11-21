package com.ark.attendanceapp.Model;

public class ModelPresenceTime {
    private String time_in;
    private String time_out;

    public ModelPresenceTime() {
    }

    public ModelPresenceTime(String time_in, String time_out) {
        this.time_in = time_in;
        this.time_out = time_out;
    }

    public String getTime_in() {
        return time_in;
    }

    public void setTime_in(String time_in) {
        this.time_in = time_in;
    }

    public String getTime_out() {
        return time_out;
    }

    public void setTime_out(String time_out) {
        this.time_out = time_out;
    }
}
