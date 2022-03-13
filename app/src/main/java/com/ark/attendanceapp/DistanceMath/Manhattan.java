package com.ark.attendanceapp.DistanceMath;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Manhattan {

    public static double manhattan(double latUserLocation, double longUserLocation, double latOfficeLocation, double longOfficeLocation) {
        return Math.abs((latOfficeLocation - latUserLocation) + (longOfficeLocation - longUserLocation));
    }

}
