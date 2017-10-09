import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Title
 * @Description 租车权限枚举：会员租车权限(0:启用,1:禁用)
 * @Author yongSen.wang
 * @Create 2017-04-17 11:00
 * @Version V1.0
 */
public enum DayOrNightRentEnum {
    TRAVEL_ORDER_RENT_DAY(1, "日租"),
    TRAVEL_ORDER_RENT_NIGHT(0, "夜租"),
    TRAVEL_ORDER_RENT_WEEK(2, "周租")
    ;

    DayOrNightRentEnum(int index, String meaning) {
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

    public static DayOrNightRentEnum getEnumByIndex(int index) {
        for (DayOrNightRentEnum t : DayOrNightRentEnum.values()) {
            if (t.getIndex() == index) {
                return t;
            }
        }
        return null;
    }

    public static DayOrNightRentEnum getEnumByMeaning(String meaning) {
        for (DayOrNightRentEnum t : DayOrNightRentEnum.values()) {
            if (t.getMeaning().equals(meaning)) {
                return t;
            }
        }
        return null;
    }

    public static Map<Integer, String> getAllEnumToMap(){
        Map<Integer, String> map = Maps.newHashMap();
        for (DayOrNightRentEnum t : DayOrNightRentEnum.values()) {
            map.put(t.getIndex(), t.getMeaning());
        }
        return map;
    }

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(getAllEnumToMap()));
    }
}
