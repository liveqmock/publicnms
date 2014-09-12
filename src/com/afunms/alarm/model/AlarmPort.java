package com.afunms.alarm.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

public class AlarmPort extends BaseVo implements Serializable{
	
	private int id;
	
	private String ipaddress;
	
	private int portindex;
	
	private String name;

	private String type;

	private String subtype;
	
	private String enabled;
    
	private int compare;
	
	private int levelinvalue1;

	private int levelinvalue2;

	private int levelinvalue3;
	
	private int leveloutvalue1;

	private int leveloutvalue2;

	private int leveloutvalue3;
	
	private int levelintimes1;

	private int levelintimes2;

	private int levelintimes3;
    
	private int levelouttimes1;

	private int levelouttimes2;

	private int levelouttimes3;
	
	private int smsin1;

	private int smsin2;

	private int smsin3;

	private int smsout1;

	private int smsout2;

	private int smsout3;
	
	private String alarm_info;
	
    private String wayin1;
    private String wayin2;
    private String wayin3;
    private String wayout1;
    private String wayout2;
    private String wayout3;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public int getPortindex() {
		return portindex;
	}

	public void setPortindex(int portindex) {
		this.portindex = portindex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
    

	public int getCompare() {
		return compare;
	}

	public void setCompare(int compare) {
		this.compare = compare;
	}

	public int getLevelinvalue1() {
		return levelinvalue1;
	}

	public void setLevelinvalue1(int levelinvalue1) {
		this.levelinvalue1 = levelinvalue1;
	}

	public int getLevelinvalue2() {
		return levelinvalue2;
	}

	public void setLevelinvalue2(int levelinvalue2) {
		this.levelinvalue2 = levelinvalue2;
	}

	public int getLevelinvalue3() {
		return levelinvalue3;
	}

	public void setLevelinvalue3(int levelinvalue3) {
		this.levelinvalue3 = levelinvalue3;
	}

	public int getLeveloutvalue1() {
		return leveloutvalue1;
	}

	public void setLeveloutvalue1(int leveloutvalue1) {
		this.leveloutvalue1 = leveloutvalue1;
	}

	public int getLeveloutvalue2() {
		return leveloutvalue2;
	}

	public void setLeveloutvalue2(int leveloutvalue2) {
		this.leveloutvalue2 = leveloutvalue2;
	}

	public int getLeveloutvalue3() {
		return leveloutvalue3;
	}

	public void setLeveloutvalue3(int leveloutvalue3) {
		this.leveloutvalue3 = leveloutvalue3;
	}

	public int getLevelintimes1() {
		return levelintimes1;
	}

	public void setLevelintimes1(int levelintimes1) {
		this.levelintimes1 = levelintimes1;
	}

	public int getLevelintimes2() {
		return levelintimes2;
	}

	public void setLevelintimes2(int levelintimes2) {
		this.levelintimes2 = levelintimes2;
	}

	public int getLevelintimes3() {
		return levelintimes3;
	}

	public void setLevelintimes3(int levelintimes3) {
		this.levelintimes3 = levelintimes3;
	}
    
	
	public int getLevelouttimes1() {
		return levelouttimes1;
	}

	public void setLevelouttimes1(int levelouttimes1) {
		this.levelouttimes1 = levelouttimes1;
	}

	public int getLevelouttimes2() {
		return levelouttimes2;
	}

	public void setLevelouttimes2(int levelouttimes2) {
		this.levelouttimes2 = levelouttimes2;
	}

	public int getLevelouttimes3() {
		return levelouttimes3;
	}

	public void setLevelouttimes3(int levelouttimes3) {
		this.levelouttimes3 = levelouttimes3;
	}

	public int getSmsin1() {
		return smsin1;
	}

	public void setSmsin1(int smsin1) {
		this.smsin1 = smsin1;
	}

	public int getSmsin2() {
		return smsin2;
	}

	public void setSmsin2(int smsin2) {
		this.smsin2 = smsin2;
	}

	public int getSmsin3() {
		return smsin3;
	}

	public void setSmsin3(int smsin3) {
		this.smsin3 = smsin3;
	}

	public int getSmsout1() {
		return smsout1;
	}

	public void setSmsout1(int smsout1) {
		this.smsout1 = smsout1;
	}

	public int getSmsout2() {
		return smsout2;
	}

	public void setSmsout2(int smsout2) {
		this.smsout2 = smsout2;
	}

	public int getSmsout3() {
		return smsout3;
	}

	public void setSmsout3(int smsout3) {
		this.smsout3 = smsout3;
	}

	public String getAlarm_info() {
		return alarm_info;
	}

	public void setAlarm_info(String alarm_info) {
		this.alarm_info = alarm_info;
	}

	public String getWayin1() {
		return wayin1;
	}

	public void setWayin1(String wayin1) {
		this.wayin1 = wayin1;
	}

	public String getWayin2() {
		return wayin2;
	}

	public void setWayin2(String wayin2) {
		this.wayin2 = wayin2;
	}

	public String getWayin3() {
		return wayin3;
	}

	public void setWayin3(String wayin3) {
		this.wayin3 = wayin3;
	}

	public String getWayout1() {
		return wayout1;
	}

	public void setWayout1(String wayout1) {
		this.wayout1 = wayout1;
	}

	public String getWayout2() {
		return wayout2;
	}

	public void setWayout2(String wayout2) {
		this.wayout2 = wayout2;
	}

	public String getWayout3() {
		return wayout3;
	}

	public void setWayout3(String wayout3) {
		this.wayout3 = wayout3;
	}

	

}
