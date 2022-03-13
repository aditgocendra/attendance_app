package com.ark.attendanceapp.Model;

public class ModelAttendanceDistance {
    private String min_distance;
    private String max_distance;
    private String math_distance;
    private String key;

    public ModelAttendanceDistance(){

    }

    public ModelAttendanceDistance(String min_distance, String max_distance, String math_distance) {
        this.min_distance = min_distance;
        this.max_distance = max_distance;
        this.math_distance = math_distance;
    }

    public String getMin_distance() {
        return min_distance;
    }

    public void setMin_distance(String min_distance) {
        this.min_distance = min_distance;
    }

    public String getMax_distance() {
        return max_distance;
    }

    public void setMax_distance(String max_distance) {
        this.max_distance = max_distance;
    }

    public String getMath_distance() {
        return math_distance;
    }

    public void setMath_distance(String math_distance) {
        this.math_distance = math_distance;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
