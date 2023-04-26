package me.zhengjie.utils;

import cn.hutool.core.date.DateUtil;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils extends DateUtil {

    public static final int YEAR = 1;

    public static final int MONTH = 2;

    public static final int DAY = 3;

    public static final int HOURS = 4;

    public static final int MINIUTE = 5;

    public static final int SECEND = 6;

    public static String nowDate() {
        return formatDate(new Date());
    }

    public static String formatDateTime(Timestamp timestamp){
        return formatDateTime(new Date(timestamp.getTime()));
    }

    public static String formatDate(Timestamp timestamp){
        return formatDate(new Date(timestamp.getTime()));
    }

    public static List<Date> findMonthDates(Date monthDate) {
        Date begin = beginOfMonth(monthDate);
        Date end = endOfMonth(monthDate);
        end = beginOfDay(end);
        List<Date> dateList = new ArrayList<>();
        dateList.add(begin);
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(begin);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(end);
        while (end.after(calBegin.getTime())) {
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(calBegin.getTime());
        }
        return dateList;
    }

    /**
     * 比较date1时间是否小于date2
     * @param date1
     * @param date2
     * @return
     */
    public static boolean lt(Timestamp date1,Timestamp date2){
        return date1.getTime()<date2.getTime();
    }

    /**
     * 比较date1时间是否小于等于date2
     * @param date1
     * @param date2
     * @return
     */
    public static boolean le(Timestamp date1,Timestamp date2){
        return date1.getTime()<=date2.getTime();
    }

    /**
     * 比较date1时间是否大于date2
     * @param date1
     * @param date2
     * @return
     */
    public static boolean gt(Timestamp date1,Timestamp date2){
        return !lt(date1,date2);
    }

    /**
     * 比较date1时间是否大于等于date2
     * @param date1
     * @param date2
     * @return
     */
    public static boolean ge (Timestamp date1,Timestamp date2){
        return !le(date1,date2);
    }

    /**
     * 比较date1时间是否小于date2
     * @param date1
     * @param date2
     * @return
     */
    public static boolean lt(Date date1,Date date2){
        return date1.getTime()<date2.getTime();
    }

    /**
     * 比较date1时间是否小于等于date2
     * @param date1
     * @param date2
     * @return
     */
    public static boolean le(Date date1,Date date2){
        return date1.getTime()<=date2.getTime();
    }

    /**
     * 比较date1时间是否大于date2
     * @param date1
     * @param date2
     * @return
     */
    public static boolean gt(Date date1,Date date2){
        return !lt(date1,date2);
    }

    /**
     * 比较date1时间是否大于等于date2
     * @param date1
     * @param date2
     * @return
     */
    public static boolean ge(Date date1,Date date2){
        return !le(date1,date2);
    }

    public static Timestamp parseDate(String dateStr){
        Date date=DateUtil.parseDate(dateStr);
        return new Timestamp(date.getTime());
    }

    public static List<Date> betweenDates(Date begin, Date end) {
        end = beginOfDay(end);
        List<Date> dateList = new ArrayList<>();
        dateList.add(begin);
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(begin);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(end);
        while (end.after(calBegin.getTime())) {
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(calBegin.getTime());
        }
        return dateList;
    }

    public static List<Date> betweenHours(Date begin, Date end) {
        List<Date> dateList = new ArrayList<>();
        dateList.add(begin);
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(begin);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(end);
        while (end.after(calBegin.getTime())) {
            calBegin.add(Calendar.HOUR_OF_DAY, 1);
            dateList.add(calBegin.getTime());
        }
        return dateList;
    }

    public static boolean timeBefor(Date d1, Date d2) {
        String s1 = format(d1, "HH:mm:ss");
        String s2 = format(d2, "HH:mm:ss");
        d1 = parse(s1);
        d2 = parse(s2);
        return d1.before(d2);
    }

    public static boolean timeAfter(Date d1, Date d2) {
        String s1 = format(d1, "HH:mm:ss");
        String s2 = format(d2, "HH:mm:ss");
        d1 = parse(s1);
        d2 = parse(s2);
        return d1.after(d2);
    }

    /**
     * 是否周末
     * @param d
     * @return
     */
    public static boolean isWeekend(Date d) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    /**
     * 时间相减得天数
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1,Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }


    public static void main(String[] args) {
        int i=DateUtil.compare(DateUtil.parseDateTime("2020-01-01 14:00:00"),DateUtil.parseDateTime("2020-01-01 15:00:00"));
        System.out.println(i);

//        Timestamp ts = new Timestamp(System.currentTimeMillis());
//        String tsStr = "";
//        DateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//            // 方法一
//            tsStr = sdf.format(ts);
//            System.out.println(tsStr);
    }

    public static Timestamp parseDateTime(String datetime){
        return new Timestamp(DateUtil.parseDateTime(datetime).getTime());
    }
    public static Date subtract(Timestamp date,long count,int timeUnit){
        return subtract(new Date(date.getTime()),count,timeUnit);
    }
    public static Date subtract(Date date,long count,int timeUnit){
        switch (timeUnit){
            case YEAR:
                count=count*365*24*3600*1000;
                break;
            case MONTH:
                count=count*31*24*3600*1000;
                break;
            case HOURS:
                count=count*3600*1000;
                break;
            case MINIUTE:
                count=count*60*1000;
            case SECEND:
                count=count*1000;
                break;
            default:
                count=count*24*3600*1000;
                break;
        }
        return new Date(date.getTime()-count);
    }


}
