package com.afunms.application.model;

//
//此类为环境温湿度告警阀值的model类, 其对应的数据表名为 system_temper_humi_thresh
//

import com.afunms.common.base.BaseVo;

/**
 * Such as environmental temperature and humidity alarm threshold of the model class, 
 * its corresponding data table is named system_enviroment_temperature_humidity
 * 
 * 
 * @author nielin
 * create at 2010-02-01
 */

public class TemperatureHumidityThresholdConfig extends BaseVo {
	
	/**
	 * Default Primary key
	 */
	private int id ; 
	
	/**
	 * Serial device id 
	 */
	private String node_id;
	
	/**
	 * The minimum temperature
	 */
	private String minTemperature;
	
	/**
	 * The maximum temperature
	 */
	private String maxTemperature;
	
	/**
	 * The minimum humidity
	 */
	private String minHumidity;
	
	/**
	 * The maximum humidity
	 */
	private String maxHumidity;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the node_id
	 */
	public String getNode_id() {
		return node_id;
	}

	/**
	 * @param serialNodeId the serialNodeId to set
	 */
	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}

	/**
	 * @return the minTemperature
	 */
	public String getMinTemperature() {
		return minTemperature;
	}

	/**
	 * @param minTemperature the minTemperature to set
	 */
	public void setMinTemperature(String minTemperature) {
		this.minTemperature = minTemperature;
	}

	/**
	 * @return the maxTemperature
	 */
	public String getMaxTemperature() {
		return maxTemperature;
	}

	/**
	 * @param maxTemperature the maxTemperature to set
	 */
	public void setMaxTemperature(String maxTemperature) {
		this.maxTemperature = maxTemperature;
	}

	/**
	 * @return the minHumidity
	 */
	public String getMinHumidity() {
		return minHumidity;
	}

	/**
	 * @param minHumidity the minHumidity to set
	 */
	public void setMinHumidity(String minHumidity) {
		this.minHumidity = minHumidity;
	}

	/**
	 * @return the maxHumidity
	 */
	public String getMaxHumidity() {
		return maxHumidity;
	}

	/**
	 * @param maxHumidity the maxHumidity to set
	 */
	public void setMaxHumidity(String maxHumidity) {
		this.maxHumidity = maxHumidity;
	}
	
	
}
