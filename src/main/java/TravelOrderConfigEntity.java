
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Title 行程订单计费规则entity
 * @Description
 * @Author yongSen.wang
 * @Create 2017-05-08 19:02
 * @Version V1.0
 */
public class TravelOrderConfigEntity extends BaseEntity {

    /**
     * 费率表主键
     */
    private Integer configId;
    /**
     * 适用城市（为ALL则适用所有城市）
     */
    private String cityRange;
    /**
     * 适用站点（为ALL则适用所有站点）
     */
    private String stationRange;
    /**
     * 适用车型（为ALL则适用所有车型）
     */
    private String catagRange;
    /**
     * 计费方式 1分时租赁
     */
    private Integer rateType;
    /**
     * 起步时长（单位分钟）
     */
    private Integer startTime;
    /**
     * 分钟价格
     */
    private BigDecimal minutePrice;
    /**
     * 日租分钟价格
     */
    private BigDecimal dailyPrice;
    /**
     * 夜租分钟价格
     */
    private BigDecimal nightPrice;
    /**
     * 限驶里程（单位公里）
     */
    private BigDecimal limitMileages;
    /**
     * 超里程的单价（按公里）
     */
    private BigDecimal mileagesPrice;
    /**
     * 备注
     */
    private String remark;
    /**
     * 日租开始时间（夜租结束时间）
     */
    private String dailyStart;
    /**
     * 日租结束时间（夜租开始时间）
     */
    private String dailyEnd;
    /**
     * 日租封顶价
     */
    private BigDecimal dailyTopPrice;
    /**
     * 夜租封顶价
     */
    private BigDecimal nightTopPrice;
    /**
     * 日租每小时单价
     */
    private BigDecimal dailyHourPrice;
    /**
     * 夜租每小时单价
     */
    private BigDecimal nightHourPrice;
    /**
     * 生效时间（开始时间大于等于生效时间，该条记录才有效）
     */
    private Date effectTime;
    /**
     * 周租封顶价
     */
    private BigDecimal weeklyTopPrice;
    /**
     * 基础计费名称
     */
    private String configName;

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public String getCityRange() {
        return cityRange;
    }

    public void setCityRange(String cityRange) {
        this.cityRange = cityRange;
    }

    public String getStationRange() {
        return stationRange;
    }

    public void setStationRange(String stationRange) {
        this.stationRange = stationRange;
    }

    public String getCatagRange() {
        return catagRange;
    }

    public void setCatagRange(String catagRange) {
        this.catagRange = catagRange;
    }

    public Integer getRateType() {
        return rateType;
    }

    public void setRateType(Integer rateType) {
        this.rateType = rateType;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getMinutePrice() {
        return minutePrice;
    }

    public void setMinutePrice(BigDecimal minutePrice) {
        this.minutePrice = minutePrice;
    }

    public BigDecimal getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(BigDecimal dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public BigDecimal getNightPrice() {
        return nightPrice;
    }

    public void setNightPrice(BigDecimal nightPrice) {
        this.nightPrice = nightPrice;
    }

    public BigDecimal getLimitMileages() {
        return limitMileages;
    }

    public void setLimitMileages(BigDecimal limitMileages) {
        this.limitMileages = limitMileages;
    }

    public BigDecimal getMileagesPrice() {
        return mileagesPrice;
    }

    public void setMileagesPrice(BigDecimal mileagesPrice) {
        this.mileagesPrice = mileagesPrice;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDailyStart() {
        return dailyStart;
    }

    public void setDailyStart(String dailyStart) {
        this.dailyStart = dailyStart;
    }

    public String getDailyEnd() {
        return dailyEnd;
    }

    public void setDailyEnd(String dailyEnd) {
        this.dailyEnd = dailyEnd;
    }

    public BigDecimal getDailyTopPrice() {
        return dailyTopPrice;
    }

    public void setDailyTopPrice(BigDecimal dailyTopPrice) {
        this.dailyTopPrice = dailyTopPrice;
    }

    public BigDecimal getNightTopPrice() {
        return nightTopPrice;
    }

    public void setNightTopPrice(BigDecimal nightTopPrice) {
        this.nightTopPrice = nightTopPrice;
    }

    public BigDecimal getDailyHourPrice() {
        return dailyHourPrice;
    }

    public void setDailyHourPrice(BigDecimal dailyHourPrice) {
        this.dailyHourPrice = dailyHourPrice;
    }

    public BigDecimal getNightHourPrice() {
        return nightHourPrice;
    }

    public void setNightHourPrice(BigDecimal nightHourPrice) {
        this.nightHourPrice = nightHourPrice;
    }

    public Date getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(Date effectTime) {
        this.effectTime = effectTime;
    }

    public BigDecimal getWeeklyTopPrice() {
        return weeklyTopPrice;
    }

    public void setWeeklyTopPrice(BigDecimal weeklyTopPrice) {
        this.weeklyTopPrice = weeklyTopPrice;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }
}
