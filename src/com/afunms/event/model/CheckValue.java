/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.model;

import com.afunms.common.base.BaseVo;

public class CheckValue extends BaseVo implements java.io.Serializable
{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8123891007872257586L;

	/**
	 * 设备id
	 */
	private String nodeid;
	
	/**
	 * 指标名称
	 */
    private String indicatorsName;
    
    /**
     * sIndex
     */
    private String sindex;
    
    /**
     * 类型
     */
    private String type;
    
    /**
     * 子类型
     */
    private String subtype;
    
    /**
     * 告警等级
     */
    private int alarmlevel;
    
    /**
     * 告警内容
     */
    private String content;
    
    /**
     * 告警值
     */
    private String thevalue;
    
    /**
     * 拓扑图显示
     */
    private String topoShow;

	/**
	 * @return the nodeid
	 */
	public String getNodeid() {
		return nodeid;
	}

	/**
	 * @param nodeid the nodeid to set
	 */
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	/**
	 * @return the indicatorsName
	 */
	public String getIndicatorsName() {
		return indicatorsName;
	}

	/**
	 * @param indicatorsName the indicatorsName to set
	 */
	public void setIndicatorsName(String indicatorsName) {
		this.indicatorsName = indicatorsName;
	}

	/**
	 * @return the sindex
	 */
	public String getSindex() {
		return sindex;
	}

	/**
	 * @param sindex the sindex to set
	 */
	public void setSindex(String sindex) {
		this.sindex = sindex;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return subtype;
	}

	/**
	 * @param subtype the subtype to set
	 */
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	/**
	 * @return the alarmlevel
	 */
	public int getAlarmlevel() {
		return alarmlevel;
	}

	/**
	 * @param alarmlevel the alarmlevel to set
	 */
	public void setAlarmlevel(int alarmlevel) {
		this.alarmlevel = alarmlevel;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the thevalue
	 */
	public String getThevalue() {
		return thevalue;
	}

	/**
	 * @param thevalue the thevalue to set
	 */
	public void setThevalue(String thevalue) {
		this.thevalue = thevalue;
	}

	/**
	 * @return the topoShow
	 */
	public String getTopoShow() {
		return topoShow;
	}

	/**
	 * @param topoShow the topoShow to set
	 */
	public void setTopoShow(String topoShow) {
		this.topoShow = topoShow;
	}

	/**
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

    
}
