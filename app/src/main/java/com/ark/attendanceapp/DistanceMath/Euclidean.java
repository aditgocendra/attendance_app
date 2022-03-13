package com.ark.attendanceapp.DistanceMath;

public class Euclidean {

    public static double euclidean(double latUserLocation, double longUserLocation, double latOfficeLocation, double longOfficeLocation) {
        double dx = Math.abs(latOfficeLocation - latUserLocation);
        double dy = Math.abs(longOfficeLocation - longUserLocation);
        return Math.sqrt(dx * dx + dy * dy);
    }
}
