package com.cab404.libtabun.util;

import com.cab404.libtabun.facility.ResponseFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * U is for the Utils
 * Куча статичных методов и переменных (по идее).
 *
 * @author cab404
 */
public class U {
    public static final String path = "tabun.everypony.ru";
    public static final HttpHost host = new HttpHost(path, 80);

    /**
     * Делает абсолютно то же, что и Log.v("Luna Log", obj.toString()),
     * плюс проверяет на null.
     */
    public static void v(Object obj) {
        try {
            System.out.println(obj == null ? null : obj.toString());
            System.out.flush();
        } catch (NullPointerException e) {
            w(e);
        }
    }

    /**
     * Делает абсолютно то же, что и Log.w("Luna Log", obj.toString()),
     * плюс проверяет на null.
     */
    public static void w(Object obj) {
        System.err.println(obj == null ? null : obj.toString());
        System.err.flush();
    }

    /**
     * Делает абсолютно то же, что и Log.w("Luna Log", obj.toString()),
     * плюс проверяет на null.
     */
    public static void w(Throwable obj) {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        if (obj != null) obj.printStackTrace(out);
        System.err.println(writer.toString());
        System.err.flush();
    }

    public static void v(HttpRequestBase request) {
        v(request.getRequestLine());
        for (Header header : request.getAllHeaders())
            v(header.getName() + ": " + header.getValue());
        v("");
    }

    /**
     * Делает абсолютно то же, что и Log.wtf("Luna Log", obj.toString()),
     * плюс проверяет на null.
     */
    public static void wtf(Object obj) {
        System.err.println("WTF? " + (obj == null ? null : obj.toString()));
        System.err.flush();
    }

    /**
     * Достаёт рандомный элемент из массива. И всё :D
     */
    public static <T> T getRandomEntry(T[] values) {
        return values[(int) Math.floor(Math.random() * values.length)];
    }

    /**
     * Андроид, почему ты не любишь плюсы?
     */
    public static int parseInt(String in) {
        return Integer.parseInt(in.replace("+", ""));
    }

    public static float parseFloat(String in) {
        return Float.parseFloat(in.replace("+", ""));
    }


    public static Calendar convertDatetime(String datetime) {
        String timezone = datetime.substring(18);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));

        int year = Integer.parseInt(datetime.substring(0, 4));
        int month = Integer.parseInt(datetime.substring(5, 7)) - 1;
        int day = Integer.parseInt(datetime.substring(8, 10));

        int hour = Integer.parseInt(datetime.substring(11, 13));
        int minute = Integer.parseInt(datetime.substring(14, 16));
        int second = Integer.parseInt(datetime.substring(17, 19));

        //noinspection MagicConstant
        calendar.set(year, month, day, hour, minute, second);
        return calendar;
    }

    public static String tabs(int num) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < num; i++) tabs.append("\t");
        return tabs.toString();
    }

    public static abstract class TextPartParser implements ResponseFactory.Parser {


        boolean started;
        StringBuilder builder;

        public TextPartParser() {
            builder = new StringBuilder();
        }

        @Override public boolean line(String line) {

            if (!started)
                started = isStart(line);
            if (!started)
                return true;

            builder.append(line).append("\n");

            if (isEnd(line)) {
                process(builder);
                return false;
            }

            return true;
        }

        public abstract void process(StringBuilder out);
        public abstract boolean isStart(String str);
        public abstract boolean isEnd(String str);


    }
}
