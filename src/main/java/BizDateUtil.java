
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * BIZ时间相关工具类
 * Created by xuechao on 2017/5/18.
 */
public class BizDateUtil {

    public static SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");


    // date类型转换为String类型
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        if (formatType == null){
            formatType = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(formatType).format(data);
    }

    // long类型转换为String类型
    // currentTime要转换的long类型的时间
    // formatType要转换的string类型的时间格式
    public static String longToString(long currentTime, String formatType)
            throws ParseException {
        if (formatType == null){
            formatType = "yyyy-MM-dd HH:mm:ss";
        }
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        if (formatType == null){
            formatType = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // long转换为Date类型
    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        if (formatType == null){
            formatType = "yyyy-MM-dd HH:mm:ss";
        }
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    // string类型转换为long类型
    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    /**
     * 判断所给两个时间点是否在同一天
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isInSameDay(long time1, long time2){
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(time2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                &&  c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判断所给两个时间点是否在同一天
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isInSameDay(Date time1, Date time2) {
        return isInSameDay(time1.getTime(), time2.getTime());
    }

        /**
         * 转换为时间（天,时:分:秒.毫秒）
         * 三天内显示“今天10:00”、“昨天10:00”、“前天10:00”；当年内非三天内显示“月+日”；跨年显示“年”；
         * @param checkTime
         * @return
         */

    public static String formatCheckTime(long checkTime) {
        String result = "";
        Calendar c = Calendar.getInstance();
        //巡检时间
        Calendar check = Calendar.getInstance();
        check.setTimeInMillis(checkTime);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND,0);
        long today = c.getTime().getTime();
        int year = c.get(Calendar.YEAR);
        c.add(Calendar.DAY_OF_YEAR, -1);
        long yestoday = c.getTime().getTime();
        c.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        if(checkTime>=today){
            //今天--
            result = "今天";
        }else if(checkTime<today&&checkTime>=yestoday){
            //昨天--
            result = "昨天";
        }else{
            //三天外
            int cyear = check.get(Calendar.YEAR);
            if(year!=cyear){
                //跨年
                sdf = new SimpleDateFormat("yyyy年MM月dd日");
                result = sdf.format(check.getTime());
            }else{
                sdf = new SimpleDateFormat("MM月dd日");
                result = sdf.format(check.getTime());
            }
        }
        return result;
    }

    /**
     * 获取消息是否过期(七天过期)
     * @param addTime
     * @return
     * @throws ParseException
     */
    public static String getOverTime(String addTime) throws ParseException {
        Date date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(addTime);
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 7);
        String is_invalid = "";
        String overTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
        int res = overTime.compareTo(now);
        if(res > 0){
            is_invalid = "0";
        }else{
            is_invalid = "1";
        }
        return is_invalid;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param one 时间参数 1 格式：1990-01-01 12:00:00
     * @param two 时间参数 2 格式：2009-01-01 12:00:00
     * @return 返回值为 x分
     */
    public static long getDistanceTimes(Date one, Date two) {
        long min;
        long time1 = one.getTime();
        long time2 = two.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        min = ((diff / (60 * 1000)));
        return min;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long getDistanceTimes(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long min = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            min = ((diff / (60 * 1000)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return min;
    }

    //时分秒的时间，加上年月日
    public static Date addSpecific(Date time, String oldTime) throws ParseException {
        //获取年月日
        String specificDate = DateUtils.formatDate(time, "yyyy-MM-dd");
        String newTime = specificDate + " " + oldTime;
        return DateUtils.parse(newTime);
    }

    //获得当前时间到晚上24点的秒数
    public static long getNowTimeToFour() {
        //当前时间
        Date nowTime = new Date();
        Date fourTime = null;
        try {
            fourTime = addSpecific(nowTime, "23:59:59");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //计算2个时间的毫秒数
        long time = fourTime.getTime() - nowTime.getTime();
        return time / 1000;
    }

    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) //同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) //闰年
                {
                    timeDistance += 366;
                } else //不是闰年
                {
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else //不同年
        {
            return day2 - day1;
        }
    }

    //获得当前时间到晚上24点的秒数
    public static String isAdjustmentNow(String adjustmentPeriod, Date nowTime) throws ParseException{
        if (StringUtils.isEmpty(adjustmentPeriod)){
            return "";
        }
        //根据；分割字符串
        String[] splitTimes = adjustmentPeriod.split(";");
        //遍历判断是否在时间段内
        for(String splitTime : splitTimes){
            //转成时间格式
            String[] adjustmentTimes = splitTime.split("-");
            Date startTime = stringToDate(dateToString(nowTime,"yyyy-MM-dd") + " " + adjustmentTimes[0] + ":00", "yyyy-MM-dd HH:mm:ss");
            Date endTime = stringToDate(dateToString(nowTime,"yyyy-MM-dd") + " " + adjustmentTimes[1] + ":00", "yyyy-MM-dd HH:mm:ss");
            if (nowTime.compareTo(startTime) >= 0 && nowTime.compareTo(endTime) <= 0) {
                return splitTime;
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Date date =new Date();
//        int exitSeconds = 59-date.getSeconds()+ (59-date.getMinutes())*60+(23-date.getHours())*3600;
//
//        Calendar calendar = Calendar.getInstance();
//        int exitSecondd = 59-calendar.get(Calendar.SECOND)+ (59-calendar.get(Calendar.MINUTE))*60+(23-calendar.get(Calendar.HOUR_OF_DAY))*3600;
//
//        System.out.println(exitSeconds);
//        System.out.println(exitSecondd);
//        System.out.println(getNowTimeToFour());
        splitTimeMethod();
        System.out.println(now().getTime());
        //System.out.println(isAdjustmentNow("08:00-09:00;11:00-19:00", date));
    }

    public static Date now() throws ParseException {
        String nowTime = dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        Date now = stringToDate(nowTime, "yyyy-MM-dd HH:mm:ss");
        return now;
    }


    public static void splitTimeMethod(){
        String sjd = "08:00-09:00;11:00-19:00";
        String newSjd = "10:30-11:01;20:00-21:00";

        String[] ap = sjd.split(";");
        for(int i=0;i<ap.length;i++){
            //再根据-分割成单独的一个个时分 03:20
            String[] pr = ap[i].split("-");
            //把时间段的开始时间，转成分钟数
            String[] hm = pr[0].split(":");
            Integer stMinites = Integer.valueOf(hm[0].toString())*60+Integer.valueOf(hm[1].toString());
            //把时间段的结束时间，转成分钟数
            String[] hms = pr[1].split(":");
            Integer edMinites = Integer.valueOf(hms[0].toString())*60+Integer.valueOf(hms[1].toString());
            //新增或者编辑的的时间段
            String[] newAp = newSjd.split(";");
            for(int j=0;j<newAp.length;j++){
                //再根据-分割成单独的一个个时分 03:20
                String[] newPr = newAp[j].split("-");
                //把时间段的开始时间，转成分钟数
                String[] newHm = newPr[0].split(":");
                Integer newStMinites = Integer.valueOf(newHm[0].toString())*60+Integer.valueOf(newHm[1].toString());
                //把时间段的结束时间，转成分钟数
                String[] newHms = newPr[1].split(":");
                Integer newEdMinites = Integer.valueOf(newHms[0].toString())*60+Integer.valueOf(newHms[1].toString());
                //如果已经存在结束时间大于新增或者编辑的开始时间并且旧开始时间大于新增结束时间，则时间重叠
                //新增结束时间大于旧的开始时间并且新增开始时间小于旧的结束时间，则也为时间重叠
                //新增的开始时间大于旧的结束时间或者新增的结束时间大于旧的的开始时间，则时间不重叠
//                if(newStMinites.compareTo(edMinites)>0 || newEdMinites.compareTo(stMinites)<0){
//                    System.out.println("时间段重叠了");
//                }
//                if(edMinites.compareTo(newStMinites)>0 && stMinites.compareTo(newEdMinites)>0){
//                    System.out.println("q时间段重叠了");
//                }
//                if(edMinites.compareTo(newStMinites)<0 && stMinites.compareTo(newEdMinites)<0){
//                    System.out.println("z时间段重叠了");
//                }
                if(!(stMinites.compareTo(newEdMinites)>=0 || edMinites.compareTo(newStMinites)<=0)){
                    System.out.println("来了一次");
                }
            }
        }
    }
}
