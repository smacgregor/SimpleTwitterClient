package com.codepath.apps.simpletweets.utils;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by smacgregor on 2/17/16.
 */
public class ParseRelativeDate {
    public String getRelativeTimeAgo(String rawJsonData) {
        String dateFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        dateFormatter.setLenient(true);

        String relativeDate = "";
        try {
            long dateMilliSeconds = dateFormatter.parse(rawJsonData).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMilliSeconds,
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS).toString();

        } catch (ParseException ex) {}
        return relativeDate;
    }
}
