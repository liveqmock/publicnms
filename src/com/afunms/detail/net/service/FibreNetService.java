package com.afunms.detail.net.service;

import java.util.List;

import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.reomte.model.FibreCapabilityInfo;
import com.afunms.detail.reomte.model.FibreConfigInfo;
import com.afunms.detail.reomte.model.LightInfo;
import com.afunms.detail.reomte.model.ProcessInfo;
import com.afunms.detail.service.fibreInfo.FibreCapabilityInfoService;
import com.afunms.detail.service.fibreInfo.FibreConfigInfoService;
import com.afunms.detail.service.fibreInfo.LightInfoService;
import com.afunms.detail.service.processInfo.ProcessInfoService;


public class FibreNetService extends NetService{
	
	public FibreNetService(String nodeid, String type, String subtype) {
		super(nodeid, type, subtype);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 获取网络设备的 tab 页信息
	 * @return
	 */
	public List<DetailTabRemote> getTabInfo(){
		String file = "/detail/net/fibrenetdetailtab.xml";
		// 调用父类的解析 tab 也信息
		return praseDetailTabXML(file);
	}
	
	
	/**
	 * 获取进程信息
	 * @return 返回 当前进程 的信息
	 */
	public List<ProcessInfo> getProcessInfo(){
		ProcessInfoService processInfoService = new ProcessInfoService(this.nodeid, this.type, this.subtype);
		return processInfoService.getCurrProcessInfo();
	}
	
	
	/**
	 * 获取信号灯信息
	 * @return 返回 当前信号灯列表 的信息
	 */
	public List<LightInfo> getLightInfo(){
		LightInfoService lightInfoService = new LightInfoService(this.nodeid, this.type, this.subtype);
		return lightInfoService.getCurrLightInfo();
	}
	
	
	/**
	 * 获取光口配置信息
	 * @return 返回 当前光口配置列表 的信息
	 */
	public List<FibreConfigInfo> getFibreConfigInfo(){
		FibreConfigInfoService fibreConfigInfoService = new FibreConfigInfoService(this.nodeid, this.type, this.subtype);
		return fibreConfigInfoService.getFibreConfigInfo();
	}
	
	/**
	 * 获取光口配置信息
	 * @return 返回 当前光口性能列表 的信息
	 */
	public List<FibreCapabilityInfo> getFibreCapabilityInfo(){
		FibreCapabilityInfoService fibrecCapabilityInfoService = new FibreCapabilityInfoService(this.nodeid, this.type, this.subtype);
		return fibrecCapabilityInfoService.getFibreCapabilityInfo();
	}


}
