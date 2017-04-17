package com.frbentes.agendaac.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by frbentes on 11/04/17.
 */
public class DateUtil {

    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";

    public static String stringToDateFormat(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormatUtils.
                ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern(), Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        try {
            Date inputDate = inputFormat.parse(timestamp);
            return outputFormat.format(inputDate);
        } catch (ParseException exception) {
            return null;
        }
    }

    public static String stringToTimeFormat(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormatUtils.
                ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern(), Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        try {
            Date inputDate = inputFormat.parse(timestamp);
            return outputFormat.format(inputDate);
        } catch (ParseException exception) {
            return null;
        }
    }

    public static String stringToISOFormat(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormatUtils.
                ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern(), Locale.getDefault());
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date inputDate = inputFormat.parse(timestamp);
            return outputFormat.format(inputDate);
        } catch (ParseException exception) {
            return null;
        }
    }

    public static String isoToDateTimeFormat(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormatUtils.
                ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern(), Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        try {
            Date inputDate = inputFormat.parse(timestamp);
            return outputFormat.format(inputDate);
        } catch (ParseException exception) {
            return null;
        }
    }

}
