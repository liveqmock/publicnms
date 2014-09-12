package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Aug 16, 2011 3:26:49 PM
 * 类说明 :性能面板和指标中间表的模型类
 */
public class PerformancePanelIndicatorsModel extends BaseVo{

	private String id;
	/**
	 * 性能面板名称
	 */
	private String panelName;
	
	/**
	 * 指标的名称
	 */
	private String indicatorName;
	
	/**
	 * 指标的描述
	 */
	private String indicatorDesc;
	
	public String getIndicatorName() {
		return indicatorName;
	}

	public void setIndicatorName(String indicatorName) {
		this.indicatorName = indicatorName;
	}

	public String getIndicatorDesc() {
		return indicatorDesc;
	}

	public void setIndicatorDesc(String indicatorDesc) {
		this.indicatorDesc = indicatorDesc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPanelName() {
		return panelName;
	}

	public void setPanelName(String panelName) {
		this.panelName = panelName;
	}
}

