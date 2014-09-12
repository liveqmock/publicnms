package com.afunms.application.model;

//
//此类为环境温湿度的model类, 其对应的数据表名为 system_temperature_humidity
//

import com.afunms.common.base.BaseVo;

/**
 * 
 * This class of environmental temperature and humidity of the model class,
 * its corresponding database table name is 
 * system_enviroment_temperature_humidity
 * 
 * @author nielin 
 * create at 2010-01-28
 *
 */


public class TemperatureHumidityConfig extends BaseVo {
	
	/**
	 * Default Primary key
	 */
	private int id;
	
	/**
	 * Serial device id 
	 */
	private String node_id;
	
	/**
	 * Test the temperature. the units is degrees celsius.
	 */
	private String temperature;
	
	/**
	 * Test the humidity.
	 */
	private String humidity;
	
	/**
	 * Detection time
	 */
	private String time;
	
	/**
	 * Constructors
	 */
	public TemperatureHumidityConfig() {
		// TODO Auto-generated constructor stub
	}

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
	 * @param node_id the node_id to set
	 */
	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}

	/**
	 * @return the temperature
	 */
	public String getTemperature() {
		return temperature;
	}

	/**
	 * @param temperature the temperature to set
	 */
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	/**
	 * @return the humidity
	 */
	public String getHumidity() {
		return humidity;
	}

	/**
	 * @param humidity the humidity to set
	 */
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

}
