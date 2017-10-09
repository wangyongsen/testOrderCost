/**
 * 订单类型枚举类
 * Created by wang.aoyu on 2017/9/5.
 */
public enum OrderTypeEnum {
    NORMAL(0, "普通订单"),
    WEEKLY(2, "周租订单"),
    ;

    OrderTypeEnum(int index, String meaning){
        this.index = index;
        this.meaning = meaning;
    }

    private Integer index;

    private String meaning;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

}
