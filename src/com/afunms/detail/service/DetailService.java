package com.afunms.detail.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.afunms.common.base.BaseVo;
import com.afunms.common.util.SysLogger;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.reomte.model.DetailTitleRemote;
import com.afunms.detail.reomte.model.InterfaceInfo;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.detail.service.envInfo.ENVInfoService;
import com.afunms.detail.service.eventListInfo.EventListInfoService;
import com.afunms.detail.service.interfaceInfo.InterfaceInfoService;
import com.afunms.detail.service.memoryInfo.FlashInfoService;
import com.afunms.detail.service.memoryInfo.MemoryInfoService;
import com.afunms.detail.service.pingInfo.PingInfoService;
import com.afunms.detail.service.syslogInfo.SyslogInfoService;
import com.afunms.detail.service.systemInfo.SystemInfoService;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Syslog;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.temp.model.NodeTemp;

public class DetailService {

	protected NodeDTO nodeDTO;
	
	protected String nodeid;
	
	protected String type;
	
	protected String subtype;
	
	

	public DetailService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
		init();
	}
	
	/**
	 * @return the nodeDTO
	 */
	public NodeDTO getNodeDTO() {
		return nodeDTO;
	}

	/**
	 * @param nodeDTO the nodeDTO to set
	 */
	public void setNodeDTO(NodeDTO nodeDTO) {
		this.nodeDTO = nodeDTO;
	}
	
	protected void init(){
		init(null);
	}

	protected void init(BaseVo baseVo) {
		NodeUtil nodeUtil = new NodeUtil();
		if(baseVo == null){
			List<BaseVo> list = nodeUtil.getNodeByTyeAndSubtype(type, subtype);
			List<NodeDTO> nodeDTOList = nodeUtil.conversionToNodeDTO(list);
			Iterator<NodeDTO> iterator = nodeDTOList.iterator();
			while (iterator.hasNext()) {
				NodeDTO elem = (NodeDTO) iterator.next();
				if (String.valueOf(elem.getId()).equals(nodeid)) {
					setNodeDTO(elem);
					break;
				}
			}
		} else {
			setNodeDTO(nodeUtil.conversionToNodeDTO(baseVo));
		}
	}

	/**
	 * 用于解析 详细信息的 tab 页
	 * 该方法应由子类来调用
	 * @param file   tab.xml 文件 格式为 应用路径下 /.../...
	 * @return
	 */
	protected List<DetailTabRemote> praseDetailTabXML(String file) {
		List<DetailTabRemote> tabList = new ArrayList<DetailTabRemote>();
		SAXBuilder builder = new SAXBuilder();
		try {
			// 项目路径 /afunms
			String sysPath = ResourceCenter.getInstance().getSysPath();
			Document doc = builder.build(new File(sysPath + file));
			List allTab = doc.getRootElement().getChildren("tab");
			Iterator tabIterator = allTab.iterator();
			while (tabIterator.hasNext()) {
				// 循环每个标签页来获取每个标签页的标题
				Element tabElement = (Element) tabIterator.next();
				// 标签页 id
				String tabId = (String)tabElement.getAttributeValue("id");
				// 标签页 name
				String tabName =(String)tabElement.getAttributeValue("title");
				// 标签页 action
				String tabAction = (String)tabElement.getAttributeValue("action");
				
				// 标签页 标题列表
				List<DetailTitleRemote> titleList = new ArrayList<DetailTitleRemote>();
				List allTitleTr = tabElement.getChildren();
				Iterator<Element> titleTrIterator = allTitleTr.iterator();
				int rowNum = 0;			// 标题行号
				while (titleTrIterator.hasNext()) {
					Element titleTrElement = (Element) titleTrIterator.next();
					List titleTr_TitleList = titleTrElement.getChildren();
					Iterator<Element>  titleTr_TitleIterator = titleTr_TitleList.iterator();
					while (titleTr_TitleIterator.hasNext()) {
						// 循环每一个 title-tr
						Element titleElement = (Element) titleTr_TitleIterator.next();
						
						String titleName = titleElement.getText();
						String titleId = titleElement.getAttributeValue("id");
						String cols = titleElement.getAttributeValue("cols");
						if(cols == null || "".equals(cols.trim())){
							cols = "1";
						}
						
						DetailTitleRemote detailTitleRemote = new DetailTitleRemote();
						detailTitleRemote.setId(titleId);
						detailTitleRemote.setContent(titleName);
						detailTitleRemote.setCols(cols);
						detailTitleRemote.setRowNum(rowNum+"");
						titleList.add(detailTitleRemote);
					}
					rowNum++;
				}
				DetailTabRemote detailTabRemote = new DetailTabRemote();
				detailTabRemote.setId(tabId);
				detailTabRemote.setName(tabName);
				detailTabRemote.setAction(tabAction);
				detailTabRemote.setTitleList(titleList);
				tabList.add(detailTabRemote);
			}
		} catch (Exception e) {
			SysLogger.error("DetailService.praseDetailTabXML()", e);
		}
		
		for(int i = 0 ; i< tabList.size(); i++){
			System.out.println(tabList.get(i).getName());
		}
		return tabList;
	}
	
	/**
	 * 获取设备系统信息
	 * @return 返回 NodeTemp 列表
	 */
	public List<NodeTemp> getSystemInfo(){
		SystemInfoService systemInfoService = new SystemInfoService(this.nodeid, this.type, this.subtype);
		return systemInfoService.getCurrAllSystemInfo();
	}
	
	/**
	 * 获取设备PING信息
	 * @return 返回 NodeTemp 列表
	 */
	public List<NodeTemp> getCurrPingInfo(){
		PingInfoService pingInfoService = new PingInfoService(this.nodeid, this.type, this.subtype);
		return pingInfoService.getCurrPingInfo(null);
	}
	
	/**
	 * 获取当天连通率平均值的信息
	 * @return	返回 连通率平均值的 String 形式
	 */
	public String getCurrDayPingAvgInfo(){
		PingInfoService pingInfoService = new PingInfoService(this.nodeid, this.type, this.subtype);
		return pingInfoService.getCurrDayPingAvgInfo(this.nodeDTO.getIpaddress());
	}
	
	/**
	 * 获取当前内存值的信息
	 * @return	返回 NodeTemp 列表
	 */
	public List<NodeTemp> getCurrMemoryInfo(){
		MemoryInfoService memoryInfoService = new MemoryInfoService(this.nodeid, this.type, this.subtype);
		return memoryInfoService.getCurrPerMemoryListInfo();
	}
	
	/**
	 * 获取当前闪存值的信息
	 * @return	返回 NodeTemp 列表
	 */
	public List<NodeTemp> getCurrFlashInfo(){
		FlashInfoService flashInfoService = new FlashInfoService(this.nodeid, this.type, this.subtype);
		return flashInfoService.getCurrPerFlashListInfo();
	}
	
	/**
	 * 获取当前动力环境值的信息
	 * @return	返回 NodeTemp 列表
	 */
	public List<NodeTemp> getCurrENVInfo(){
		ENVInfoService envInfoService = new ENVInfoService(this.nodeid, this.type, this.subtype);
		return envInfoService.getCurrPerENVListInfo();
	}
	
	/**
	 * 获取当前所有 CPU 平均值的信息
	 * @return	返回 当前所有 CPU 平均值的 String 形式
	 */
	public String getCurrCpuAvgInfo(){
		CpuInfoService cpuInfoService = new CpuInfoService(this.nodeid, this.type, this.subtype);
		return cpuInfoService.getCurrCpuAvgInfo();
	}
	
	/**
	 * 获取当前状态的信息
	 * @return	返回 状态的 String 形式
	 */
	public String getStautsInfo(){
		SystemInfoService systemInfoService = new SystemInfoService(this.nodeid, this.type, this.subtype);
		return systemInfoService.getStautsInfo();
	}
	
	/**
	 * 返回设备类别的信息，该方法只有网络设备和服务器需要重写
	 * @param category	--- 设备类别
	 * @return
	 */
	public String getCategoryInfo(String category){
		SystemInfoService systemInfoService = new SystemInfoService(this.nodeid, this.type, this.subtype);
		return systemInfoService.getCategoryInfo(Integer.valueOf(category));
	}
	
	/**
	 * 获取供应商信息
	 * @param supperId --- 供应商id
	 * @return 返回供应商名称
	 */
	public String getSupperInfo(String supperId){
		SystemInfoService systemInfoService = new SystemInfoService(this.nodeid, this.type, this.subtype);
		return systemInfoService.getSupperInfo(supperId);
	}
	
	/**
	 * 获取设备接口的信息
	 * @param subentities	--- 接口的类别的数组，如果未空，则取全部信息
	 * @return
	 */
	public List<InterfaceInfo> getInterfaceInfo(String[] subentities){
		InterfaceInfoService interfaceInfoService = new InterfaceInfoService(this.nodeid, this.type, this.subtype);
		return interfaceInfoService.getCurrAllInterfaceInfo(subentities);
	}
	
	/**
	 * * 获取设备接口的信息
	 * @param subentities	--- 接口的类别的数组，如果未空，则取全部信息
	 * @param sindexs       --- 索引组
	 * @param starttime     --- 开始日期
	 * @param totime        --- 结束日期
	 * @return
	 */
	public List<InterfaceInfo> getInterfaceInfo(String[] subentities,String[] sindexs,String starttime,String totime){
		InterfaceInfoService interfaceInfoService = new InterfaceInfoService(this.nodeid, this.type, this.subtype);
		return interfaceInfoService.getCurrAllInterfaceInfo(subentities,sindexs,starttime,totime);
	}
	
	/**
	 * 得到已知索引的接口信息
	 * @param sindexs       --- 索引组
	 * @param starttime     --- 开始日期
	 * @param totime        --- 结束日期
	 * @return
	 */
	public List<InterfaceInfo> getInterfaceInfoBySindes(String[] sindexs,String starttime, String totime){
		InterfaceInfoService interfaceInfoService = new InterfaceInfoService(this.nodeid, this.type, this.subtype);
		return interfaceInfoService.getInterfaceInfoBySindes(sindexs,starttime,totime);
	}
	
	/**
	 * 获取Syslog信息
	 * @param startdate --- 开始日期
	 * @param todate    --- 结束日期
	 * @param level1	--- 日志等级
	 * @return 返回Syslog列表 Syslog
	 */
	public List<Syslog> getSyslogInfo(String startdate, String todate, String priorityname){
		return new SyslogInfoService(this.nodeid, this.type, this.subtype).getSyslogInfo(this.nodeDTO.getIpaddress(), startdate, todate, priorityname);
	}
	
	/**
	 * 获取告警信息
	 * @param startdate --- 开始日期
	 * @param todate    --- 结束日期
	 * @param level1	--- 告警等级
	 * @param status	--- 处理状态
	 * @return 返回告警列表 EventList
	 */
	public List<Object> getAlarmInfo(String startdate, String todate, String level1, String status){
		return new EventListInfoService(this.nodeid, this.type, this.subtype).getCurrSummaryEventListInfo(startdate, todate, level1, status);
	}
	
	/**
	 * 获取告警详细信息
	 * @param startdate --- 开始日期
	 * @param todate    --- 结束日期
	 * @param level1	--- 告警等级
	 * @param status	--- 处理状态
	 * @return 返回告警列表 EventList
	 */
	public List<EventList> getAlarmDetailInfo(String startdate, String todate, String level1, String eventlocation, String subentity, String status){
		return new EventListInfoService(this.nodeid, this.type, this.subtype).getEventListInfo(startdate, todate, level1, eventlocation, subentity, status);
	}
	
	

}
