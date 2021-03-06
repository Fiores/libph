package com.cab404.libph.util;

import com.cab404.libph.data.Blog;
import com.cab404.libph.data.PersonalBlog;
import com.cab404.moonlight.util.SU;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author cab404
 */
public class LS {

    public static final List<String> months =
            Collections.unmodifiableList(Arrays.asList(
                    "декабря",
                    "января",
                    "февраля",
                    "марта",
                    "апреля",
                    "мая",
                    "июня",
                    "июля",
                    "августа",
                    "сентября",
                    "октября",
                    "ноября"
            ));


    private static String copyChars(char ch, int num) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++) builder.append(ch);
        return builder.toString();
    }

    private static String fillZeroes(int fill, int len) {
        String data = String.valueOf(fill);
        return copyChars('0', len - data.length()) + data;
    }

    @SuppressWarnings("MagicConstant")
    public static Calendar parseDate(String str) {
        List<String> split = SU.charSplit(str, ' ');
        boolean is_long = split.get(2).endsWith(",");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        int day = Integer.parseInt(split.get(0));
        int month = months.indexOf(split.get(1));
        int year = Integer.parseInt(split.get(2).substring(0, 4));
        int hour = 0;
        int minute = 0;

        if (is_long) {
            List<String> time = SU.charSplit(split.get(3), ':');
            hour = Integer.parseInt(time.get(0));
            minute = Integer.parseInt(time.get(1));
        }
        calendar.set(year, month, day, hour, minute);

        return calendar;
    }

    // 2014-02-12T21:20:13+04:00
    private static final SimpleDateFormat TS_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static Calendar parseSQLDate(String date) {
        try {
            Calendar calendar = Calendar.getInstance();
            StringBuilder builder = new StringBuilder(date);
            builder.deleteCharAt(22);
            calendar.setTime(TS_FORMAT.parse(builder.toString()));
            return calendar;
        } catch (Exception e) {
            return null;
        }
    }


    public static String toSQLDate(Calendar calendar) {
        int rawOffset = calendar.getTimeZone().getRawOffset();
        return "" +
                calendar.get(Calendar.YEAR) + "-" +
                fillZeroes((calendar.get(Calendar.MONTH) + 1), 2) + "-" +
                fillZeroes(calendar.get(Calendar.DAY_OF_MONTH), 2) + "T" +
                fillZeroes(calendar.get(Calendar.HOUR_OF_DAY), 2) + ":" +
                fillZeroes(calendar.get(Calendar.MINUTE), 2) + ":" +
                fillZeroes(calendar.get(Calendar.SECOND), 2) +
                (rawOffset > 0 ? "+" : "-") +
                fillZeroes((int) MILLISECONDS.toHours(rawOffset), 2) + ":" +
                fillZeroes((int) MILLISECONDS.toMinutes(rawOffset % HOURS.toMillis(1)), 2);

    }

    public static Blog blogFromUrl(String url) {
        if (url.contains("/created/topics")) {
            Blog blog = new PersonalBlog();
            blog.url_name = SU.sub(url, "/profile/", "/");
            return blog;
        }
        if (url.contains("/blog/")) {
            Blog blog = new Blog();
            blog.url_name = SU.bsub(url, "/blog/", "");
            return blog;
        }
        System.err.println("Cannot resolve as blog: " + url);
        return null;
    }
}
