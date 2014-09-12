package com.afunms.detail.host.service;

import java.util.List;

import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.detail.reomte.model.CpuPerfInfo;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.reomte.model.DiskInfo;
import com.afunms.detail.reomte.model.DiskPerfInfo;
import com.afunms.detail.reomte.model.InterfaceInfo;
import com.afunms.detail.reomte.model.MemoryConfigInfo;
import com.afunms.detail.reomte.model.NetmediaConfigInfo;
import com.afunms.detail.reomte.model.PagePerfInfo;
import com.afunms.detail.reomte.model.PageSpaceInfo;
import com.afunms.detail.reomte.model.UserConfigInfo;
import com.afunms.detail.service.configInfo.MemoryConfigInfoService;
import com.afunms.detail.service.configInfo.NetmediaConfigInfoService;
import com.afunms.detail.service.configInfo.UserConfigInfoService;
import com.afunms.detail.service.diskInfo.DiskInfoService;
import com.afunms.detail.service.sysInfo.CpuPerfInfoService;
import com.afunms.detail.service.sysInfo.DiskPerfInfoService;
import com.afunms.detail.service.sysInfo.PagePerfInfoService;
import com.afunms.detail.service.sysInfo.PageSpaceInfoService;




public class LinuxService extends HostService{

	public LinuxService(String nodeid, String type, String subtype) {
		super(nodeid, type, subtype);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 获取 Linux 服务器设备的 tab 页信息
	 * @return
	 */
	public List<DetailTabRemote> getTabInfo(){
		String file = "/detail/host/linuxdetailtab.xml";
		// 调用父类的解析 tab 页信息
		SysLogger.info(file);
		return praseDetailTabXML(file);
	}

	/**
	 * 获取Linux服务器设备的接口的信息
	 * @return
	 */
	public List<InterfaceInfo> getInterfaceInfo(){
		String[] subentities = {"index", "ifDescr", "ifSpeed", "ifOperStatus", 
				"OutBandwidthUtilHdx", "InBandwidthUtilHdx",};
		// 调用父类方法
		return getInterfaceInfo(subentities);
	}
	
	
	/**
	 * 获取Linux服务器设备的磁盘的信息
	 * @return
	 */
	public List<DiskInfo> getDiskInfo(){
		DiskInfoService diskInfoService = new DiskInfoService(this.nodeid, this.type, this.subtype);
		return diskInfoService.getCurrDiskInfo();
	}
	
	/**
	 * 获取Linux服务器设备的磁盘利用率图
	 * @return
	 */
	public String getDiskInfoUtilizationImg(){
		DiskInfoService diskInfoService = new DiskInfoService(this.nodeid, this.type, this.subtype);
		List<DiskInfo> diskInfoList = diskInfoService.getCurrDiskInfo();
		String imgName = CommonUtil.ip2long(this.hostNode.getIpAddress()) + "disk";
		return diskInfoService.getCurrDiskInfoUtilizationImg(diskInfoList, "", imgName, 750, 150);
	}
	
	/**
	 * 获取Linux服务器设备CPU性能信息
	 * @return
	 */
	public List<CpuPerfInfo> getCpuPerfInfo(){
		CpuPerfInfoService cpuPerfInfoService = new CpuPerfInfoService(this.nodeid, this.type, this.subtype);
		return cpuPerfInfoService.getCurrCpuPerfInfo();
	}
	
	/**
	 * 获取Linux服务器设备磁盘性能信息
	 * @return
	 */
	public List<DiskPerfInfo> getDiskPerfInfo(){
		DiskPerfInfoService diskPerfInfoService = new DiskPerfInfoService(this.nodeid, this.type, this.subtype);
		return diskPerfInfoService.getCurrDiskPerfInfo();
	}
	
	/**
	 * 获取Linux服务器设备页面性能信息
	 * @return
	 */
	public List<PagePerfInfo> getPagePerfInfo(){
		PagePerfInfoService pagePerfInfoService = new PagePerfInfoService(this.nodeid, this.type, this.subtype);
		return pagePerfInfoService.getCurrPagePerfInfo();
	}
	
	/**
	 * 获取Linux服务器设备磁盘页面交换信息
	 * @return
	 */
	public List<PageSpaceInfo> getPageSpaceInfo(){
		PageSpaceInfoService pageSpaceInfoService = new PageSpaceInfoService(this.nodeid, this.type, this.subtype);
		return pageSpaceInfoService.getCurrPageSpaceInfo();
	}
	
	/**
	 * 获取Linux服务器设备内存配置信息
	 * @return
	 */
	public List<MemoryConfigInfo> getMemoryConfigInfo(){
		MemoryConfigInfoService memoryConfigInfoService = new MemoryConfigInfoService(this.nodeid, this.type, this.subtype);
		return memoryConfigInfoService.getCurrMemoryConfigInfo();
	}
	
	/**
	 * 获取Linux服务器设备网卡配置信息
	 * @return
	 */
	public List<NetmediaConfigInfo> getNetmediaConfigInfo(){
		NetmediaConfigInfoService netmediaConfigInfoService = new NetmediaConfigInfoService(this.nodeid, this.type, this.subtype);
		return netmediaConfigInfoService.getCurrNetmediaConfigInfo();
	}
	
	
	/**
	 * 获取Linux服务器设备用户配置信息
	 * @return
	 */
	public List<UserConfigInfo> getUserConfigInfo(){
		UserConfigInfoService userConfigInfoService = new UserConfigInfoService(this.nodeid, this.type, this.subtype);
		return userConfigInfoService.getCurrUserConfigInfo();
	}
	
	
	
}
