import java.math.BigDecimal;
import java.util.Date;

/**
 * @Title 行程订单计费明细
 * @Description
 * @Author yongSen.wang
 * @Create 2017-05-11 15:24
 * @Version V1.0
 */
public class TravelOrderDetailEntity extends BaseEntity {

    /**
     * 主键
     */
    private Long detailId;
    /**
     * 订单主表主键
     */
    private Long orderId;
    /**
     * 会员业务id
     */
    private Long userId;
    /**
     * 费用配置业务id
     */
    private Integer configId;
    /**
     *当前用车开始时间
     */
    private Date startDate;
    /**
     * 当前用车结束时间
     */
    private Date endDate;
    /**
     * 是否日租 1日租 0夜租 2周租
     */
    private Integer rentalType;
    /**
     * 批次（第几个24小时时段）
     */
    private Integer batch;
    /**
     * 时长（单位毫秒）
     */
    private Long duration;
    /**
     * 金额(元）
     */
    private BigDecimal cost;
    /**
     * 当前用车时长（单位分钟）
     */
    private Long usecount;
    /**
     * 当前里程数（单位米）
     */
    private Integer mileages;
    /**
     *最后总价
     */
    private BigDecimal total;
    /**
     * 备注
     */
    private String remark;

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getRentalType() {
        return rentalType;
    }

    public void setRentalType(Integer rentalType) {
        this.rentalType = rentalType;
    }

    public Integer getBatch() {
        return batch;
    }

    public void setBatch(Integer batch) {
        this.batch = batch;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Long getUsecount() {
        return usecount;
    }

    public void setUsecount(Long usecount) {
        this.usecount = usecount;
    }

    public Integer getMileages() {
        return mileages;
    }

    public void setMileages(Integer mileages) {
        this.mileages = mileages;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
