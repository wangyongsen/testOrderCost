/**
 * @author LiuMingMing
 * 2015年8月7日 下午12:51:56
 * Version 1.0
 */

public enum DateFormatEnum {

	TIME("HH:mm:ss"),
	YEAR("yyyy"),
	MONTH("MM"),
	DAY("dd"),
	WEEK("E"),
	DATE("yyyy-MM-dd"),
	DATETIME("yyyy-MM-dd HH:mm:ss"),
	TIMESTAMP("yyyy-MM-dd HH:mm:ss.SSS"),
	DATECODE("yyyyMMddHHmmss"),
	SIMPLECHINESE("yyyy年MM月dd日"),

	DEFAULT(DATETIME.value());
 
//	19 , which is the number of characters in yyyy-mm-dd hh:mm:ss 
//	20 + s , which is the number of characters in the yyyy-mm-dd hh:mm:ss.[fff...] and s represents the scale of the given Timestamp, its fractional seconds precision. 
	String format = "";
	
	private DateFormatEnum(String format) {
		this.format = format;
	} 
	
	public String value(){
		return format;
	}
}
