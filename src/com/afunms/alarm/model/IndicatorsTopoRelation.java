package com.afunms.alarm.model;

import com.afunms.common.base.BaseVo;

public class IndicatorsTopoRelation extends BaseVo{
	
	/** 拓扑图id */
	private String topoId;
	
	/** 设备的告警指标 id */
	private String indicatorsId;
	
	/** 设备的告警指标 id 的索引号 比如 磁盘的盘符 id 号 交换机流速的各个端口号 */
	private String sIndex;

	private String nodeid;
	
	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	/**
	 * @return the topoId
	 */
	public String getTopoId() {
		return topoId;
	}

	/**
	 * @param topoId the topoId to set
	 */
	public void setTopoId(String topoId) {
		this.topoId = topoId;
	}

	/**
	 * @return the indicatorsId
	 */
	public String getIndicatorsId() {
		return indicatorsId;
	}

	/**
	 * @param indicatorsId the indicatorsId to set
	 */
	public void setIndicatorsId(String indicatorsId) {
		this.indicatorsId = indicatorsId;
	}

	/**
	 * @return the sIndex
	 */
	public String getSIndex() {
		return sIndex;
	}

	/**
	 * @param index the sIndex to set
	 */
	public void setSIndex(String index) {
		sIndex = index;
	}
	
	
	
	
}
