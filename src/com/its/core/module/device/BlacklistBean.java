/**
 * 
 */
package com.its.core.module.device;

import java.io.Serializable;

/**
 * 创建日期 2013-1-30 下午03:24:44
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class BlacklistBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9024148651092877446L;
	
	//黑名单ID
	private String id;
	
	//号牌号码
	private String plate;
	
	//号牌颜色
	private String plateColorCode;
	
	//所有可以号牌号码匹配的号牌,其中容易识别错误的地方用@符号代替
	private String matchPlate;		
	
	//黑名单类型ID
	private String typeId;
	
	//黑名单类型名称
	private String typeName;
	
	//布控开始时间
	private Long startTime;	
	
	//布控截止时间
	private Long endTime;
	
	//最少匹配精度（有多少位相同才算匹配成功） 本属性属于保留字段，暂时未使用
	private Integer matchNumber;

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Integer getMatchNumber() {
		return matchNumber;
	}

	public void setMatchNumber(Integer matchNumber) {
		this.matchNumber = matchNumber;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public String getMatchPlate() {
		return matchPlate;
	}

	public void setMatchPlate(String matchPlate) {
		this.matchPlate = matchPlate;
	}

	public String getPlateColorCode() {
		return plateColorCode;
	}

	public void setPlateColorCode(String plateColorCode) {
		this.plateColorCode = plateColorCode;
	}

}
