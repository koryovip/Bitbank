package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static DateUtil singleton = new DateUtil();

    public static DateUtil me() {
        return singleton;
    }

    private final ThreadLocal<DateFormat> sdf1;
    private final ThreadLocal<DateFormat> sdf2;

    private DateUtil() {
        sdf1 = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat("MM/dd HH:mm:ss");
            }
        };
        sdf2 = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat("HH:mm:ss MM/dd");
            }
        };
    }

    final public String format1(Date date) {
        return sdf1.get().format(date);
    }

    final public String format1(long dateTime) {
        return this.format1(new Date(dateTime));
    }

    final public String format2(Date date) {
        return sdf2.get().format(date);
    }

    final public String format2(long dateTime) {
        return this.format2(new Date(dateTime));
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.me().format1(new Date()));
    }
}
