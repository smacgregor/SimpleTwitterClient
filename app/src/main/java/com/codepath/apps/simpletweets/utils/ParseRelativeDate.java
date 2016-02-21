package com.codepath.apps.simpletweets.utils;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by smacgregor on 2/17/16.
 */
public class ParseRelativeDate {
    static public String getRelativeTimeAgo(String rawJsonData) {
        String dateFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        //dateFormatter.setLenient(true);

        String relativeDate = "";
        try {
            long dateMilliSeconds = dateFormatter.parse(rawJsonData).getTime();
            long currentTime = System.currentTimeMillis();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMilliSeconds,
                    currentTime,
                    DateUtils.SECOND_IN_MILLIS).toString();

        } catch (ParseException ex) {}
        return relativeDate;
    }
}
