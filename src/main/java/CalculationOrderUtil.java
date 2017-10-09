
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import static com.alibaba.fastjson.serializer.SerializerFeature.WriteDateUseDateFormat;

/**
 * @Title 行程订单计算价格
 * @Description
 * @Author yongSen.wang
 * @Create 2017-05-08 19:35
 * @Version V1.0
 */
public class CalculationOrderUtil {

    private static final Logger logger = LoggerFactory.getLogger(CalculationOrderUtil.class);

    //获取订单的总金额（不包含能耗费）
    public static BigDecimal getOrderPay(TravelOrderConfigEntity entity, TravelOrderEntity travelOrderEntity, BigDecimal premiumRatio){
        //获取时长费用
        BigDecimal orderPay = BigDecimal.valueOf(0);
        List<TravelOrderDetailEntity> orderDetailEntities = calculationOrderAll(entity, travelOrderEntity, premiumRatio);
        for(TravelOrderDetailEntity travelOrderDetailEntity : orderDetailEntities){
            orderPay = orderPay.add(travelOrderDetailEntity.getCost());
        }
        return orderPay;
    }
    //这个是计算价格分段的详细值
    public static List<TravelOrderDetailEntity> calculationOrderAll(TravelOrderConfigEntity entity, TravelOrderEntity travelOrderEntity, BigDecimal premiumRatio) {
        Map<Object, BigDecimal> resultMap = Maps.newHashMap();
        List<TravelOrderDetailEntity> orderDetailEntities = Lists.newArrayList();
        Date troStartTime = travelOrderEntity.getStartTime();
        Date troEndTime = travelOrderEntity.getEndTime();
        Integer batch = 1;//批次（第几个24小时时段）
        //订单类型 0 普通订单  1 周租订单
        Integer orderType = travelOrderEntity.getOrderType();
        //预付周期个数(如果各位为0就表示这个订单是普通订单，如果大于0就表示这个订单是周租订单)
        Integer prepayPeriod = travelOrderEntity.getPrepayPeriod();
        try {
            if (orderType == OrderTypeEnum.WEEKLY.getIndex()){
                //如果订单是周租订单，如果时间是在周租的时间内，那么就直接返回周租的费用，如果不是就先记录周租的费用，剩余时间继续计算
                if (troEndTime.compareTo(getNumTime(troStartTime, prepayPeriod * 7)) <= 0){
                    //在周租内，直接返回周租的计费
                    orderDetailEntities.addAll(getWeekOrder(entity, travelOrderEntity, troEndTime));
                    return orderDetailEntities;
                }else {
                    orderDetailEntities.addAll(getWeekOrder(entity, travelOrderEntity, getNumTime(travelOrderEntity.getStartTime(), travelOrderEntity.getPrepayPeriod() * 7)));
                    batch = (travelOrderEntity.getPrepayPeriod() * 7) + 1;
                    troStartTime = getNumTime(travelOrderEntity.getStartTime(), travelOrderEntity.getPrepayPeriod() * 7);
                }
            }

            //开始时间后一天的时间
            Date startTime = getSecondTime(troStartTime);
            Date endTime = troEndTime;

            //一个时间段一个时间段的遍历，第一天的时间，第二天的时间，等等等等
            while (startTime.compareTo(troEndTime) < 0){
                //判断订单的结束时间跟开始时间加2天得时间比较，如果大于就说明这个订单的时间过了一天没结束，如果小于就说明是2天内的时间
                if(troEndTime.compareTo(getSecondTime(startTime)) < 0){
                    //判断订单时间是否大于一天
                    if(troEndTime.compareTo(startTime) >= 0){
                        orderDetailEntities.addAll(calculationOrder(entity, getFristTime(startTime), startTime, batch, premiumRatio));
                    }else {
                        orderDetailEntities.addAll(calculationOrder(entity, getFristTime(startTime), endTime, batch, premiumRatio));
                    }

                }else {
                    orderDetailEntities.addAll(calculationOrder(entity, getFristTime(startTime), startTime, batch, premiumRatio));
                }
                startTime = getSecondTime(startTime);
                batch++;
            }

            if(startTime.compareTo(troEndTime) >= 0){
                orderDetailEntities.addAll(calculationOrder(entity, getFristTime(startTime), troEndTime, batch, premiumRatio));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("计算价格异常");
            throw new RuntimeException("计算价格异常");
        }
        return orderDetailEntities;
    }
    //这个是24小时内的原子服务
    private static List<TravelOrderDetailEntity> calculationOrder(TravelOrderConfigEntity entity, Date troStartTime, Date troEndTime, Integer batch, BigDecimal premiumRatio) throws ParseException {
        //返回结果的map集合，key值为行程开始时间和结束时间的中间值，比如，如果时间段在日租或夜租内，那么返回的key是结束时间，value为价格
        //比如跨越了规则的时间，那么第一个值就是规则的开始或结束时间，第二个值有可能是行程的结束时间，value为价格，以此类推
        //新增一个逻辑，24小时也就是一天内的封顶价格为日租价格加夜租价格
        List<TravelOrderDetailEntity> orderDetailEntities = Lists.newArrayList();
        Map<Object, BigDecimal> resultMap = Maps.newHashMap();
        BigDecimal oldDailyPrice = entity.getDailyPrice();//老日租分钟价格
        BigDecimal oldNightPrice = entity.getNightPrice();//老夜租分钟价格
        BigDecimal dailyPrice = entity.getDailyPrice().multiply(premiumRatio).setScale(2, BigDecimal.ROUND_HALF_UP);//日租分钟价格
        BigDecimal nightPrice = entity.getNightPrice().multiply(premiumRatio).setScale(2, BigDecimal.ROUND_HALF_UP);//夜租分钟价格
        Date dailyStart = addSpecific(troStartTime, entity.getDailyStart());//日租开始时间（夜租结束时间）默认先加上当天的年月日
        Date dailyEnd = addSpecific(troStartTime, entity.getDailyEnd());//日租结束时间（夜租开始时间）默认先加上当天的年月日
        BigDecimal dailyTopPrice = entity.getDailyTopPrice();//日租封顶价
        BigDecimal nightTopPrice = entity.getNightTopPrice();//夜租封顶价
        Integer trcoStartTime = entity.getStartTime();//起步时长（单位分钟）
        //计算已经使用的租金，因为要考虑到24小时内的封顶价格，24小时内最多只有（日租封顶价+夜租封顶价）
        BigDecimal dailyUsePrice = BigDecimal.valueOf(0);
        BigDecimal nightUsePrice = BigDecimal.valueOf(0);
        //设置一个计算时间，一段一段的循环，直至最后大于等于行程的结束时间
        Date calculationTime = troStartTime;
        Integer number = 0;//是否需要进1
        //先判断是否在封顶时间段内，如果是在的话就直接返回时间段的map和价格,计算时间就等于行程结束时间
        if (batch == 1){
            if (getTwoTimeMinute(troStartTime, troEndTime) <= trcoStartTime){
                //到这里说明，这个行程是在起步时长之内,直接计算价格，并返回
                if (troStartTime.compareTo(dailyStart) >= 0  && troStartTime.compareTo(dailyEnd) < 0){
                    BigDecimal newDailyPrice = getPrice(dailyPrice, trcoStartTime, dailyTopPrice);
                    //如果系数大于1就按照正常逻辑计算，如果小于1就按照老价格
                    if (premiumRatio.compareTo(BigDecimal.valueOf(1)) < 0 ){
                        newDailyPrice = getPrice(oldDailyPrice, trcoStartTime, dailyTopPrice);
                    }
                    //到这里说明是日租的单价
                    TravelOrderDetailEntity travelOrderDetailEntity = getOrderDetailEntity(calculationTime, troEndTime, DayOrNightRentEnum.TRAVEL_ORDER_RENT_DAY.getIndex(),
                            batch, newDailyPrice, getTwoTimeMinute(troStartTime, troEndTime));
                    orderDetailEntities.add(travelOrderDetailEntity);
                    return orderDetailEntities;
                }else {
                    BigDecimal newNightPrice = getPrice(nightPrice, trcoStartTime, nightTopPrice);
                    //如果系数大于1就按照正常逻辑计算，如果小于1就按照老价格
                    if (premiumRatio.compareTo(BigDecimal.valueOf(1)) < 0 ){
                        newNightPrice = getPrice(oldNightPrice, trcoStartTime, nightTopPrice);
                    }
                    //到这里说明是夜租的单价
                    TravelOrderDetailEntity travelOrderDetailEntity = getOrderDetailEntity(calculationTime, troEndTime, DayOrNightRentEnum.TRAVEL_ORDER_RENT_NIGHT.getIndex(),
                            batch, newNightPrice, getTwoTimeMinute(troStartTime, troEndTime));
                    orderDetailEntities.add(travelOrderDetailEntity);
                    return orderDetailEntities;
                }
            }else {
                //记录一次起步价格，因为价格是：起步价格+起步时长之后，到这里肯定是大于30分钟，结束时间是开始时间加上30分钟
                if (troStartTime.compareTo(dailyStart) >= 0  && troStartTime.compareTo(dailyEnd) < 0){
                    BigDecimal newDailyPrice = getPrice(dailyPrice, trcoStartTime, dailyTopPrice);
                    //如果系数大于1就按照正常逻辑计算，如果小于1就按照老价格
                    if (premiumRatio.compareTo(BigDecimal.valueOf(1)) < 0 ){
                        newDailyPrice = getPrice(oldDailyPrice, trcoStartTime, dailyTopPrice);
                    }
                    //到这里说明是日租的单价
                    TravelOrderDetailEntity travelOrderDetailEntity = getOrderDetailEntity(calculationTime, getThritTime(troStartTime), DayOrNightRentEnum.TRAVEL_ORDER_RENT_DAY.getIndex(),
                            batch, newDailyPrice, 30L);
                    orderDetailEntities.add(travelOrderDetailEntity);
                    calculationTime = getThritTime(troStartTime);
                    //这里有一种情况，就是正好卡在夜租点，第一天的夜租封顶价格就需要减去封顶价格的价格
                    long numbMinber = getTwoTimeMinute(troStartTime, dailyEnd);
                    if (numbMinber >= 30){
                        dailyTopPrice = dailyTopPrice.subtract(newDailyPrice);
                    }else if (numbMinber >= 0 && numbMinber < 30){
                        //日租封顶价减去起步价时候使用的日租 ，夜租封顶价减去剩余的
                        dailyTopPrice = dailyTopPrice.subtract(dailyPrice.multiply(BigDecimal.valueOf(numbMinber)));
                        nightTopPrice = nightTopPrice.subtract(newDailyPrice.subtract(dailyPrice.multiply(BigDecimal.valueOf(numbMinber))));
                    }
                }else {
                    BigDecimal newNightPrice = getPrice(nightPrice, trcoStartTime, nightTopPrice);
                    //如果系数大于1就按照正常逻辑计算，如果小于1就按照老价格
                    if (premiumRatio.compareTo(BigDecimal.valueOf(1)) < 0 ){
                        newNightPrice = getPrice(oldNightPrice, trcoStartTime, nightTopPrice);
                    }
                    //到这里说明是夜租的单价
                    TravelOrderDetailEntity travelOrderDetailEntity = getOrderDetailEntity(calculationTime, getThritTime(troStartTime), DayOrNightRentEnum.TRAVEL_ORDER_RENT_NIGHT.getIndex(),
                            batch, newNightPrice, 30L);
                    orderDetailEntities.add(travelOrderDetailEntity);
                    calculationTime = getThritTime(troStartTime);
                    nightTopPrice = nightTopPrice.subtract(newNightPrice);
                    //这里有一种情况，就是正好卡在夜租点，第一天的夜租封顶价格就需要减去封顶价格的价格
                    long numbMinber = getTwoTimeMinute(troStartTime, dailyStart);
                    if (numbMinber >= 30){
                        dailyTopPrice = dailyTopPrice.subtract(newNightPrice);
                    }else if (numbMinber >= 0 && numbMinber < 30){
                        //夜租封顶价减去起步价时候使用的夜租 ，日租封顶价减去剩余的
                        nightTopPrice = nightTopPrice.subtract(nightPrice.multiply(BigDecimal.valueOf(numbMinber)));
                        dailyTopPrice = dailyTopPrice.subtract(newNightPrice.subtract(nightPrice.multiply(BigDecimal.valueOf(numbMinber))));
                    }

                }
                //这里还要考虑到跨天的情况，如果起步时长跨天了，那么封顶时间需要同步的跨天
                long numbSecondMinber = getTwoTimeMinute(troStartTime, getSecondSpecific(troStartTime));
                if (numbSecondMinber <= 30 && numbSecondMinber >= 0){
                    dailyStart = getSecondTime(dailyStart);
                    dailyEnd = getSecondTime(dailyEnd);
                }
            }
        }

        //因为不知道行程的时间有多长，也就是说不知道循环几次计算，那么就使用while循环，注意要跳出循环
        //后面的时间超过了起步时长，那么就按照分钟数来计算，但是要注意日租封顶价格和夜租封顶价格
        while (calculationTime.compareTo(troEndTime) < 0){
            //计算行程订单的开始时间和结束时间，是在哪个时间段内
            //如果计算时间大于日租开始时间，并且小于等于日租结束时间，说明这个订单是日租开始的，反之是在夜租开始的
            //注意要考虑到跨天的情况
            if (calculationTime.compareTo(dailyStart) >= 0  && calculationTime.compareTo(dailyEnd) < 0){
                //先判断结束时间是否在当天的日租结束时间之内
                if (troEndTime.compareTo(dailyEnd) <= 0){
                    //到这里的情况是：结束时间还是在日租结束时间之内，这里是终结的过程
                    dailyUsePrice = dailPrice(dailyUsePrice, dailyTopPrice, dailyPrice, calculationTime, troEndTime, batch);
                    TravelOrderDetailEntity travelOrderDetailEntity = getOrderDetailEntity(calculationTime, troEndTime, DayOrNightRentEnum.TRAVEL_ORDER_RENT_DAY.getIndex(),
                            batch, dailyUsePrice, getTwoTimeMinute(calculationTime, troEndTime));

                    //在日租结束时间之内，key值为行程结束时间
                    orderDetailEntities.add(travelOrderDetailEntity);
                    return orderDetailEntities;
                }else{
                    dailyUsePrice = dailPrice(dailyUsePrice, dailyTopPrice, dailyPrice, calculationTime, dailyEnd, batch);
                    TravelOrderDetailEntity travelOrderDetailEntity = getOrderDetailEntity(calculationTime, dailyEnd, DayOrNightRentEnum.TRAVEL_ORDER_RENT_DAY.getIndex(),
                            batch, dailyUsePrice, getTwoTimeMinute(calculationTime, dailyEnd));

                    //不在日租结束时间之内,把计算时间更新为日租结束时间,并且在map中插入一个值，key值为日租结束时间
                    resultMap.put(DateUtils.format(calculationTime) + "~" + DateUtils.format(dailyEnd), dailyUsePrice);
                    orderDetailEntities.add(travelOrderDetailEntity);
                    number++;
                    calculationTime = dailyEnd;

                }
            }else {
                //夜租计算开始，这时计算时间是在夜租时段范围内，夜租有可能会出现跨天的情况
                //夜租的时间段分2个时间段，一个是一天的上半部分，一个是下半部分
                //判断计算时间是否是在上半部分
                if(isFirstHalfDay(calculationTime, dailyStart)){
                    //在上半部分时，先判断结束时间是否在当天的夜租时间之内
                    if (troEndTime.compareTo(dailyStart) <= 0){
                        nightUsePrice = nightPrice(nightUsePrice, nightTopPrice, nightPrice, calculationTime, troEndTime);
                        //在夜租结束时间之内，key值为行程结束时间
                        TravelOrderDetailEntity travelOrderDetailEntity = getOrderDetailEntity(calculationTime, dailyEnd, DayOrNightRentEnum.TRAVEL_ORDER_RENT_NIGHT.getIndex(),
                                batch, nightUsePrice, isAddAndSubtract(troStartTime, troEndTime, getTwoTimeMinute(calculationTime, troEndTime), number));
                        orderDetailEntities.add(travelOrderDetailEntity);
                        return orderDetailEntities;
                    }else{
                        nightUsePrice = nightPrice(nightUsePrice, nightTopPrice, nightPrice, calculationTime, dailyStart);
                        //不在夜租结束时间之内,把计算时间更新为日租结束时间,并且在map中插入一个值，key值为日租结束时间
                        TravelOrderDetailEntity travelOrderDetailEntity = getOrderDetailEntity(calculationTime, dailyStart, DayOrNightRentEnum.TRAVEL_ORDER_RENT_NIGHT.getIndex(),
                                batch, nightUsePrice, getTwoTimeMinute(calculationTime, dailyStart));
                        orderDetailEntities.add(travelOrderDetailEntity);
                        calculationTime = dailyStart;
//                        //这里要注意，第二天的日租时间也变成了第二天的时间
//                        dailyEnd = getSecondTime(dailyEnd);//日租结束时间（夜租开始间）
                        number++;
                    }
                }else {
                    Date secondDate = getSecondDate(calculationTime, entity.getDailyStart());
                    //在下半部分时，夜租的结束时间是在第二天的日租开始时间
                    if (troEndTime.compareTo(secondDate) <= 0){
                        nightUsePrice = nightPrice(nightUsePrice, nightTopPrice, nightPrice, calculationTime, troEndTime);
                        //在夜租结束时间之内，key值为行程结束时间
                        TravelOrderDetailEntity travelOrderDetailEntity = getOrderDetailEntity(calculationTime, troEndTime, DayOrNightRentEnum.TRAVEL_ORDER_RENT_NIGHT.getIndex(),
                                batch, nightUsePrice, isAddAndSubtract(troStartTime, troEndTime, getTwoTimeMinute(calculationTime, troEndTime), number));
                        orderDetailEntities.add(travelOrderDetailEntity);
                        return orderDetailEntities;
                    }else{
                        dailyEnd = getSecondTime(dailyEnd);//日租结束时间（夜租开始时间）
                        nightUsePrice = nightPrice(nightUsePrice, nightTopPrice, nightPrice, calculationTime, secondDate);
                        //不在夜租结束时间之内,把计算时间更新为日租结束时间,并且在map中插入一个值，key值为日租结束时间
                        TravelOrderDetailEntity travelOrderDetailEntity = getOrderDetailEntity(calculationTime, secondDate, DayOrNightRentEnum.TRAVEL_ORDER_RENT_NIGHT.getIndex(),
                                batch, nightUsePrice, getTwoTimeMinute(calculationTime, secondDate));
                        orderDetailEntities.add(travelOrderDetailEntity);
                        calculationTime = secondDate;
                        //这里要注意，第二天的日租时间也变成了第二天的时间
                        dailyStart = getSecondTime(dailyStart);//日租开始时间（夜租结束时间）
                        number++;
                    }
                }
            }
        }
        return orderDetailEntities;
    }

    //计算2个时间段之间的分钟数，其实是先用秒计算，然后除以60，余数直接进1，但是要注意使用的时候，
    // 如果是跟规则的开始时间或结束时间计算的时候是不需要进1的
    private static long getTwoTimeMinute(Date fristTime, Date secondTime){
        long t = secondTime.getTime() - fristTime.getTime();
        return t % (60 * 1000) != 0 ? t / (60 * 1000) + 1 : t / (60 * 1000);
    }

    //获得30分后的时间
    private static Date getThritTime(Date time) throws ParseException{
        Date newTime = BizDateUtil.longToDate((time.getTime() + 30 * 60 * 1000), null);
        return newTime;
    }

    //判断是否需要加一分钟或减一分钟
    private static long isAddAndSubtract(Date fristTime, Date secondTime, long minu, Integer number){
        //判断两个时间，判断秒数，如果秒数后面的大于等于前面的就不变，如果小于就加1 Calendar.get(Calendar.SECOND);
        // 创建 Calendar 对象
        Calendar fristCalendar = Calendar.getInstance();
        fristCalendar.setTime(fristTime);
        Calendar secondCalendar = Calendar.getInstance();
        secondCalendar.setTime(secondTime);
//        Integer num = secondTime.getSeconds() - fristTime.getSeconds();
        Integer num = secondCalendar.get(Calendar.SECOND) - fristCalendar.get(Calendar.SECOND);
        if(num >= 0 && number > 0){
            minu--;
        }
        return minu;
    }

    //计算价格,入参为分钟数和分钟价格
    private static BigDecimal getPrice(BigDecimal price, Integer trcoLength, BigDecimal topPrice){
        BigDecimal result = price.multiply(BigDecimal.valueOf(trcoLength));
        return result.compareTo(topPrice) > 0 ? topPrice : result;
    }

    //时分秒的时间，加上年月日
    private static Date addSpecific(Date troStartTime, String oldTime) throws ParseException {
        //获取年月日
        String specificDate = DateUtils.formatDate(troStartTime, "yyyy-MM-dd");
        String newTime = specificDate + " " + oldTime;
        return DateUtils.parse(newTime);
    }

    //计算出当天的晚上24点的时间，也就是第二天0点的时间
    private static Date getSecondSpecific(Date troStartTime) throws ParseException {
        //获取年月日
        String specificDate = DateUtils.formatDate(troStartTime, "yyyy-MM-dd");
        String newTime = specificDate + " " + "00:00:00";
        return getSecondTime(DateUtils.parse(newTime));
    }

    //判断计算时间是否是在上半部分
    private static Boolean isFirstHalfDay(Date calculationTime, Date dailyStart) throws ParseException {
        //获取年月日
        String dailyStartDate = DateUtils.formatDate(dailyStart, "yyyy-MM-dd");
        //获取当天零点的时间
        String zeroDate = dailyStartDate + " " + "00:00:00";
        Date zeroTime = DateUtils.parse(zeroDate);
        return calculationTime.compareTo(zeroTime) >= 0 && calculationTime.compareTo(dailyStart) < 0;
    }

    //获取第二天的同一时刻的日期entity.getDailyStart()
    private static Date getSecondDate(Date calculationTime, String dailyStart) throws ParseException {
        //获取年月日
        String calculationDate = DateUtils.formatDate(calculationTime, "yyyy-MM-dd");
        String calculation = calculationDate + " " + dailyStart;
        calculationTime =  DateUtils.parse(calculation);
        return getSecondTime(calculationTime);
    }

    //获取第num天的同一时刻的日期
    private static Date getNumTime(Date changeTime, Integer num){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(changeTime);
        calendar.add(calendar.DATE,num);//把日期往后增加num天.整数往后推,负数往前移动
        return calendar.getTime(); //这个时间就是日期往后推num天的结果
    }

    //获取第二天的同一时刻的日期
    private static Date getSecondTime(Date changeTime){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(changeTime);
        calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
        return calendar.getTime(); //这个时间就是日期往后推一天的结果
    }
    //获取前一天的同一时刻的日期
    private static Date getFristTime(Date changeTime){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(changeTime);
        calendar.add(calendar.DATE,-1);//把日期往后增加一天.整数往后推,负数往前移动
        return calendar.getTime(); //这个时间就是日期往后推一天的结果
    }


    //计算日租
    private static BigDecimal dailPrice(BigDecimal dailyUsePrice, BigDecimal dailyTopPrice, BigDecimal dailyPrice, Date calculationTime, Date troEndTime, Integer batch){
        if (dailyUsePrice.compareTo(dailyTopPrice) == 0){
            dailyUsePrice = BigDecimal.valueOf(0);
        }else if (dailyUsePrice.compareTo(BigDecimal.valueOf(0)) > 0){
            BigDecimal linPrice = getPrice(dailyPrice, Math.toIntExact(getTwoTimeMinute(calculationTime, troEndTime)), dailyTopPrice);
            dailyUsePrice = linPrice.add(dailyUsePrice).compareTo(dailyTopPrice) >= 0 ? dailyTopPrice.subtract(dailyUsePrice) : linPrice;
        }else {
            dailyUsePrice = getPrice(dailyPrice, Math.toIntExact(getTwoTimeMinute(calculationTime, troEndTime)), dailyTopPrice);
        }

        return dailyUsePrice;
    }
    //计算夜租
    private static BigDecimal nightPrice(BigDecimal nightUsePrice, BigDecimal nightTopPrice, BigDecimal nightPrice, Date calculationTime, Date dailyEnd){
        if (nightUsePrice.compareTo(nightTopPrice) == 0){
            nightUsePrice = BigDecimal.valueOf(0);
        }else if (nightUsePrice.compareTo(BigDecimal.valueOf(0)) > 0){
            BigDecimal linPrice = getPrice(nightPrice, Math.toIntExact(getTwoTimeMinute(calculationTime, dailyEnd)), nightTopPrice);
            nightUsePrice = linPrice.add(nightUsePrice).compareTo(nightTopPrice) >= 0 ? nightTopPrice.subtract(nightUsePrice) : linPrice;
        }else {
            nightUsePrice = getPrice(nightPrice, Math.toIntExact(getTwoTimeMinute(calculationTime, dailyEnd)), nightTopPrice);
        }
        return nightUsePrice;
    }

    //周租
    private static List<TravelOrderDetailEntity> getWeekOrder(TravelOrderConfigEntity entity, TravelOrderEntity travelOrderEntity, Date endTime) throws ParseException{
        List<TravelOrderDetailEntity> list = Lists.newArrayList();
        TravelOrderDetailEntity travelOrderDetailEntity = getOrderDetailEntity(travelOrderEntity.getStartTime(), endTime, DayOrNightRentEnum.TRAVEL_ORDER_RENT_WEEK.getIndex(),
                travelOrderEntity.getPrepayPeriod() * 7, travelOrderEntity.getPrepayAmount(), getTwoTimeMinute(travelOrderEntity.getStartTime(), endTime));
        list.add(travelOrderDetailEntity);
        return list;
    }


    //存进entity
    private static TravelOrderDetailEntity getOrderDetailEntity(Date startDate, Date endDate,
                                                                Integer rentalType, Integer batch,
                                                                BigDecimal cost, Long usecount) throws ParseException {
        TravelOrderDetailEntity entity = new TravelOrderDetailEntity();
        entity.setStartDate(startDate);//当前用车开始时间DateUtils.format(dailyEnd)
        entity.setEndDate(endDate);//当前用车结束时间
        entity.setRentalType(rentalType);//是否日租 1日租 0夜租 2周租
        entity.setBatch(batch);//批次（第几个24小时时段）
        entity.setCost(cost);//金额(元）
        entity.setUsecount(usecount);//当前用车时长（单位分钟）
        return entity;
    }


    public static void main(String[] args) throws ParseException {
        TravelOrderConfigEntity entity = new TravelOrderConfigEntity();
        entity.setDailyPrice(BigDecimal.valueOf(0.30));//日租分钟价格
        entity.setNightPrice(BigDecimal.valueOf(0.30));//夜租分钟价格
        entity.setDailyStart("09:00:00");//日租开始时间（夜租结束时间）
        entity.setDailyEnd("21:00:00");//日租结束时间（夜租开始时间）
        entity.setDailyTopPrice(BigDecimal.valueOf(99));//日租封顶价
        entity.setNightTopPrice(BigDecimal.valueOf(49));//夜租封顶价
        entity.setStartTime(30);//起步时长（单位分钟）
        entity.setWeeklyTopPrice(BigDecimal.valueOf(1000));
        String DateStr1 = "2017-10-06 23:30:00";
        String DateStr2 = "2017-10-07 23:39:50";
        Date dateTime1 = DateUtils.parse(DateStr1);
        Date dateTime2 = DateUtils.parse(DateStr2);
        TravelOrderEntity travelOrderEntity = new TravelOrderEntity();
        travelOrderEntity.setStartTime(dateTime1);
        travelOrderEntity.setEndTime(dateTime2);
        travelOrderEntity.setOrderType(0);
        travelOrderEntity.setPrepayAmount(BigDecimal.valueOf(2000));
        travelOrderEntity.setPrepayPeriod(0);
//        System.out.println("分钟数：" + getTwoTimeMinute(dateTime1, dateTime2));
//        System.out.println("计算价格:" + getPrice(BigDecimal.valueOf(0.30), 13, BigDecimal.valueOf(0.30)));
//        System.out.println("日租不到起步时长：" + JSON.toJSONString(calculationOrder(entity, DateUtils.parse("2017-05-09 10:20:16"), DateUtils.parse("2017-05-09 10:28:32"))));
//        System.out.println("夜租不到起步时长：" + JSON.toJSONString(calculationOrder(entity, DateUtils.parse("2017-05-09 19:20:16"), DateUtils.parse("2017-05-09 19:28:32"))));
//        System.out.println("日租超过起步时长：" + JSON.toJSONString(calculationOrder(entity, DateUtils.parse("2017-05-09 10:20:16"), DateUtils.parse("2017-05-09 15:28:32"))));
//        System.out.println("夜租在上半时段超过起步时长，超过包夜时间，但不到结束时间：" + JSON.toJSONString(calculationOrder(entity, DateUtils.parse("2017-05-09 01:20:16"), DateUtils.parse("2017-05-09 08:28:32"))));
//        System.out.println("夜租在上半时段超过起步时长，不超过包夜时间，但不到结束时间：" + JSON.toJSONString(calculationOrder(entity, DateUtils.parse("2017-05-09 01:20:16"), DateUtils.parse("2017-05-09 02:28:32"))));
//        System.out.println("夜租在上半时段不超过起步时长，不超过包夜时间，但不到结束时间：" + JSON.toJSONString(calculationOrder(entity, DateUtils.parse("2017-05-09 01:20:16"), DateUtils.parse("2017-05-09 01:28:32"))));
//        System.out.println("夜租在上半时段超过起步时长，超过结束时间：" + JSON.toJSONString(calculationOrder(entity, DateUtils.parse("2017-05-09 01:20:16"), DateUtils.parse("2017-05-09 13:28:32"))));
//        System.out.println("夜租在上半时段超过起步时长，超过结束时间，并且超过日租结束时间：" + JSON.toJSONString(calculationOrder(entity, DateUtils.parse("2017-05-09 01:20:16"), DateUtils.parse("2017-05-09 19:28:32"))));
//        System.out.println("夜租在下半时段超过起步时长，超过包夜时间，但不到结束时间：" + JSON.toJSONString(calculationOrder(entity, DateUtils.parse("2017-05-09 19:20:16"), DateUtils.parse("2017-05-09 19:28:32"))));
//        System.out.println("夜租在下半时段超过起步时长，不超过包夜时间，但不到结束时间：" + JSON.toJSONString(calculationOrder(entity, DateUtils.parse("2017-05-09 18:20:16"), DateUtils.parse("2017-05-10 02:28:32"))));
        System.out.println("时长：" + JSON.toJSONString(calculationOrderAll(entity, travelOrderEntity, BigDecimal.valueOf(1)),WriteDateUseDateFormat));
        System.out.println("金额：" + JSON.toJSONString(getOrderPay(entity, travelOrderEntity, BigDecimal.valueOf(1)),WriteDateUseDateFormat));

//        System.out.println("日租超过起步时长：" + JSON.toJSONString(getOrderPay(entity, DateUtils.parse("2017-05-09 20:20:10"), DateUtils.parse("2017-05-10 20:20:16"))));
//JSON.toJSONString(t,WriteNonStringValueAsString,WriteDateUseDateFormat)
//        System.out.println("计算时间：" + JSON.toJSONString(isAddAndSubtract(DateUtils.parse("2017-05-09 20:20:16"), DateUtils.parse("2017-05-10 20:20:16"), 20)));
        System.out.println(2>1 ? (3>1 ? 222 : 333) : 444);
        System.exit(0);
    }

}
