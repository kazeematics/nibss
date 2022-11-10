/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Date utils.
 */

public class DateUtil {

    /**
     * Get date.
     */
    public static String getDate(Date date, String expectedFormat) {
        SimpleDateFormat format = new SimpleDateFormat(expectedFormat);
        format.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        return format.format(date);
    }

    /**
     * Get current time.
     */
    public static String getCurrentTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date curDate = new Date(System.currentTimeMillis());
        return df.format(curDate);
    }
}
