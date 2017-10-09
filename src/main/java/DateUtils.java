import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author LiuMingMing
 *         2015年8月7日 下午12:54:19
 *         Version 1.0
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 默认格式"yyyy-MM-dd HH:mm:ss"
     *
     * @return
     * @throws ParseException
     */
    public static Date parse(String date) throws ParseException {
        return new SimpleDateFormat(DateFormatEnum.DATETIME.value()).parse(date);
    }

    /**
     * 指定格式format日期
     *
     * @return
     * @throws ParseException
     */
    public static Date parse(String date, DateFormatEnum df) throws ParseException {
        return new SimpleDateFormat(df.value()).parse(date);
    }

    /**
     * 默认格式"yyyy-MM-dd HH:mm:ss"
     *
     * @return
     * @throws ParseException
     */
    public static Timestamp parseTimestamp(String date) throws ParseException {
        return Timestamp.valueOf(date);
    }


    /**
     * 默认格式"yyyy-MM-dd HH:mm:ss"
     *
     * @return
     * @throws ParseException
     */
    public static String format(String date) throws ParseException {
        return new SimpleDateFormat(DateFormatEnum.DATETIME.value()).format(parse(date));
    }

    /**
     * 格式化日期，保留年份时分
     *
     * @param dateStr
     * @return
     * @throws Exception
     */
    public static String formatShort(String dateStr) {
        if (StringUtils.isNotEmpty(dateStr)) {
            try {
                int cYear = Calendar.getInstance().get(Calendar.YEAR);
                Date date = DateUtils.parse(dateStr);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int inYear = calendar.get(Calendar.YEAR);
                if (cYear != inYear) {
                    String cyTime = "yyyy-MM-dd HH:mm";
                    dateStr = new SimpleDateFormat(cyTime).format(date);
                } else {
                    String cTime = "MM-dd HH:mm";
                    dateStr = new SimpleDateFormat(cTime).format(date);
                }
            } catch (ParseException e) {
            }
        }
        return dateStr;
    }

    /**
     * 指定格式format日期
     *
     * @return
     * @throws ParseException
     */
    public static String format(String date, DateFormatEnum df) throws ParseException {
        return new SimpleDateFormat(df.value()).format(parse(date, df));
    }

    /**
     * 默认格式"yyyy-MM-dd HH:mm:ss"
     *
     * @return
     * @throws ParseException
     */
    public static String format(Date date) throws ParseException {
        return new SimpleDateFormat(DateFormatEnum.DATETIME.value()).format(date);
    }

    /**
     * 指定格式format日期
     *
     * @return
     * @throws ParseException
     */
    public static String format(Date date, DateFormatEnum df) throws ParseException {
        return new SimpleDateFormat(df.value()).format(date);
    }

    public static String convert(java.sql.Date date) {
        return new SimpleDateFormat(DateFormatEnum.DATE.value()).format(date);
    }

    public static String convert(java.sql.Time time) {
        return new SimpleDateFormat(DateFormatEnum.DATETIME.value()).format(time);
    }

    public static String convert(Timestamp timestamp) {
        return new SimpleDateFormat(DateFormatEnum.TIMESTAMP.value()).format(timestamp);
    }

    public static Date now() {
        return Calendar.getInstance(Locale.getDefault()).getTime();
    }

    public static String now(DateFormatEnum df) throws ParseException {
        return format(now(), df != null ? df : DateFormatEnum.DEFAULT);
    }

    public static java.sql.Date curDate() {
        return new java.sql.Date(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
    }

    public static Timestamp curTimeStamp() {
        return new Timestamp(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
    }


    ///////////////////////////

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String getDate() {
        return getDate(DateFormatEnum.DATE.value());
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, DateFormatEnum.DATE.value());
        }
        return formatDate;
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, DateFormatEnum.DATETIME.value());
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime() {
        return formatDate(new Date(), DateFormatEnum.TIME.value());
    }

    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String getDateTime() {
        return formatDate(new Date(), DateFormatEnum.DATETIME.value());
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear() {
        return formatDate(new Date(), DateFormatEnum.YEAR.value());
    }

    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String getMonth() {
        return formatDate(new Date(), DateFormatEnum.MONTH.value());
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String getDay() {
        return formatDate(new Date(), DateFormatEnum.DAY.value());
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek() {
        return formatDate(new Date(), DateFormatEnum.WEEK.value());
    }

    /**
     * 日期型字符串转化为日期 格式
     * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
     * "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取过去的天数
     *
     * @param date
     * @return
     */
    public static long pastDays(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的小时
     *
     * @param date
     * @return
     */
    public static long pastHour(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取过去的分钟
     *
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 1000);
    }

    /**
     * 转换为时间（天,时:分:秒.毫秒）
     *
     * @param timeMillis
     * @return
     */
    public static String formatDateTime(long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param before
     * @param after
     * @return
     */
    public static double getDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
    }

    public static Date getBeginDayOfMonth(Date day){
        Calendar c1=Calendar.getInstance();
        c1.setTime(day);
        c1.set(Calendar.DATE,1);
        c1.set(Calendar.HOUR,0);
        c1.set(Calendar.MINUTE,0);
        c1.set(Calendar.SECOND,0);
        c1.set(Calendar.MILLISECOND,0);
        return c1.getTime();
    }

    public static Date getEndDayOfMonth(Date day){
        Date beginDay=getBeginDayOfMonth(day);
        Calendar c1=Calendar.getInstance();
        c1.setTime(beginDay);
        c1.add(Calendar.MONTH,1);
        return c1.getTime();
    }


    /**
     * 获取横跨月度数
     * @param date1
     * @param date2
     * @return
     * @throws ParseException
     */
    public static int getSpanMonths(String date1,String date2,String pattern) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat(pattern);

        Calendar c1=Calendar.getInstance();
        Calendar c2=Calendar.getInstance();

        c1.setTime(sdf.parse(date1));
        c2.setTime(sdf.parse(date2));

        int year =c2.get(Calendar.YEAR)-c1.get(Calendar.YEAR);

        //开始日期若小月结束日期
        if(year<0){
            year=-year;
            return year*12+c1.get(Calendar.MONTH)-c2.get(Calendar.MONTH)+1;
        }

        return year*12+c2.get(Calendar.MONTH)-c1.get(Calendar.MONTH)+1;
    }

    /**
     * 获取日期之间的天数
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int getDaysBetween(Calendar d1, Calendar d2) {
        if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
            Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }
        int days = d2.get(Calendar.DAY_OF_YEAR)
                - d1.get(Calendar.DAY_OF_YEAR);
        int y2 = d2.get(Calendar.YEAR);
        if (d1.get(Calendar.YEAR) != y2) {
            d1 = (Calendar) d1.clone();
            do {
                days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
                d1.add(Calendar.YEAR, 1);
            } while (d1.get(Calendar.YEAR) != y2);
        }
        return days;
    }

    /**
     * 获取工作日
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int getWorkingDay(Calendar d1, Calendar d2) {
        int result = -1;
        if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
            Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }
        int charge_start_date = 0;// 开始日期的日期偏移量
        int charge_end_date = 0;// 结束日期的日期偏移量
        // 日期不在同一个日期内
        int stmp;
        int etmp;
        stmp = 7 - d1.get(Calendar.DAY_OF_WEEK);
        etmp = 7 - d2.get(Calendar.DAY_OF_WEEK);
        if (stmp != 0 && stmp != 6) {// 开始日期为星期六和星期日时偏移量为0
            charge_start_date = stmp - 1;
        }
        if (etmp != 0 && etmp != 6) {// 结束日期为星期六和星期日时偏移量为0
            charge_end_date = etmp - 1;
        }
        // }
        result = (getDaysBetween(getNextMonday(d1), getNextMonday(d2)) / 7)
                * 5 + charge_start_date - charge_end_date;
        return result;
    }

    /**
     * 获取中文日期
     *
     * @param date
     * @return
     */
    public static String getChineseWeek(Calendar date) {
        final String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        // System.out.println(dayNames[dayOfWeek - 1]);
        return dayNames[dayOfWeek - 1];
    }

    /**
     * 获得日期的下一个星期一的日期
     *
     * @param date
     * @return
     */
    public static Calendar getNextMonday(Calendar date) {
        Calendar result = null;
        result = date;
        do {
            result = (Calendar) result.clone();
            result.add(Calendar.DATE, 1);
        } while (result.get(Calendar.DAY_OF_WEEK) != 2);
        return result;
    }

    /**
     * 获取休息日
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int getHolidays(Calendar d1, Calendar d2) {
        return getDaysBetween(d1, d2) - getWorkingDay(d1, d2);
    }


    /**
     * 获取对应时间的为星期几
     * 星期一：1
     * 星期二：2
     * 星期三：3
     * 星期四：4
     * 星期五：5
     * 星期六：6
     * 星期日：0
     *
     * @param dt
     * @return
     */
    public static int getWeekOfDate(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return w;
    }

    /**
     * 两时间做比较
     *
     * @param originalDate
     * @param targetDate
     * @return 当 originalDate 大于等于 targetDate 返回 true
     * 反之 返回false
     */
    public static boolean compareTime(Date originalDate, Date targetDate) {
        if (targetDate == null) {
            targetDate = new Date();
        }
        int ret = originalDate.compareTo(targetDate);
        if (ret != -1) {
            return true;
        }
        return false;
    }

    /**
     * 与当前时间做比较
     *
     * @param date
     * @return 当传入时间小于当前时间时，返回true；
     * 当传入时间大于当天时间时，返回false；
     */
    public static boolean compareCurrentTime(Date date) {
        return compareTime(date, null);
    }

    /**
     * 比较时间是否超过一个月
     *
     * @param date
     * @param nowDate
     * @return
     */
    public static boolean compareIfOutOneMonth(Date date, Date nowDate) {
        if(date.getTime()>nowDate.getTime()){
            return false;
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.MONTH, 1);//把日期往后增加一个月.整数往后推,负数往前移动
        date = calendar.getTime();   //这个时间就是日期往后推一个月的结果
        //nowDate-date>一个月返回true，否则返回false
        return date.getTime()<nowDate.getTime()?true:false;
    }

    public static void main(String[] args) throws Exception{

        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat2.parse("2017-08-04 22:36:01");
        Date nowDate = new Date();
        boolean b = compareIfOutOneMonth(date, nowDate);
        System.out.println(b);

    }



}
