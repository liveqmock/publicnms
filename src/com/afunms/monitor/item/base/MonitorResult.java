/**
 * <p>Description:monitor result value</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-17
 */

package com.afunms.monitor.item.base;

import java.io.Serializable;

public class MonitorResult implements Serializable
{   	
	private static final long serialVersionUID = 864902268157L; 
	private String entity;
   	private double value;
   	private double percentage;
   	
	public String getEntity() {
		return entity;
	}
	
	public void setEntity(String entity) {
		this.entity = entity;
	}
	
	public double getPercentage() {
		return percentage;
	}
	
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}   	
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer(100);
		sb.append(entity);		
		sb.append(":,value=");
		sb.append(value);
		sb.append(",percentage=");
		sb.append(percentage);
		return sb.toString();		
	}
}