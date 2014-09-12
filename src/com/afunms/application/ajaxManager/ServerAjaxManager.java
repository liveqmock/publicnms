package com.afunms.application.ajaxManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;

import net.sf.json.JSONObject;

import com.afunms.application.dao.ClusterDao;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.UpAndDownMachineDao;
import com.afunms.application.manage.DataBaseManager;
import com.afunms.application.model.Cluster;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.MonitorDBDTO;
import com.afunms.application.model.UpAndDownMachine;
import com.afunms.application.util.RemoteClientInfo;
import com.afunms.application.util.ReportHelper;
import com.afunms.capreport.dao.UtilReportDao;
import com.afunms.capreport.model.ReportValue;
import com.afunms.capreport.model.StatisNumer;
import com.afunms.capreport.model.UtilReport;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.CreateMetersPic;
import com.afunms.common.util.CreatePiePicture;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateInformation;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.detail.service.interfaceInfo.InterfaceInfoService;
import com.afunms.detail.service.memoryInfo.MemoryInfoService;
import com.afunms.detail.service.pingInfo.PingInfoService;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.snmp.memory.WindowsPhysicalMemorySnmp;
import com.afunms.polling.snmp.memory.WindowsVirtualMemorySnmp;
import com.afunms.polling.task.ApacheDataCollector;
import com.afunms.system.model.User;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.dao.HostInterfaceDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.manage.PerformanceManager;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.MonitorNodeDTO;
import com.afunms.topology.util.ManageXmlOperator;

public class ServerAjaxManager extends AjaxBaseManager implements AjaxManagerInterface
{

	public void execute(String action) 
	{
		if(action.equals("ajaxUpdate_availability"))
		{
			ajaxUpdate_availability(); 
		}
		if (action.equals("ajaxUpdate_memory")) {
			ajaxUpdate_memory(); 
		}
		if(action.equals("ajaxMemory_fresh")){
			ajaxMemory_fresh();
		}
		if(action.equals("ajaxHostInfolist")){
			ajaxHostInfolist();
		}
		if(action.equals("inSpeedTop5")){
			inSpeedTop5();
		}
		if(action.equals("editSysname")){
			editSysname();
		}
		if(action.equals("editAlias")){
			editAlias();
		}
		if (action.equals("executeReport")) {
			executeReport();
		}
		
		if (action.equals("execMidReport")) {
			execMidReport();
		}
		if (action.equals("updateSequence")) {
			updateSequence();
		}
		if (action.equals("rebootAll")) {
			rebootAll();
		}
		if (action.equals("shutdownAll")) {
			shutdownAll();
		}
		if (action.equals("editClusterName")) {
			editClusterName();
		}
		if(action.equals("showMovement")){
			showMovement();
		}
		if (action.equals("dkdbPreview")) {
			dkdbPreview();
		}
		if (action.equals("synchronization")) {
			synchronization();
		}
	}
	
	/**
	 * 资源|端口对比预览图
	 */
	private void dkdbPreview() {
		String ids = request.getParameter("ids");
		if (ids == null || ids.equals("") || ids.equals("null")) {
			String id = request.getParameter("id");
			if (id.equals("null"))
				return;
			UtilReportDao dao = new UtilReportDao();
//			report = (UtilReport) dao.findByBid(id);
			List<String> list = dao.findIdsByBid("nms_dkdbports", id);
			if (null != list && list.size() > 0) {
				ids = "";
				for (String idsTemp : list) {
					ids += idsTemp + ",";
				}
				if (ids.endsWith(",")) {
					ids = ids.substring(0, ids.length()-1);
				}
			}
		}
		String startTime = request.getParameter("startdate")+" 00:00:00";
		String toTime = request.getParameter("todate")+" 23:59:59";

		List<List> utilInList = new ArrayList<List>();
		List<List> utilOutList = new ArrayList<List>();
		List<String> portipList = new ArrayList<String>();
		StringBuffer portHtml = new StringBuffer();

		ReportHelper helper = new ReportHelper();
		HashMap valueMap = helper.getAllValue(ids, startTime, toTime);
		List<StatisNumer> gridList = new ArrayList<StatisNumer>();
		gridList = (List<StatisNumer>) valueMap.get("gridVlue");
		if (gridList != null && gridList.size() > 0) {
			portHtml.append("<table border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21>端口名称</td><td align='center' class='body-data-title'  height=21>当前流速</td><td align='center' class='body-data-title'  height=21>最大流速</td><td align='center' class='body-data-title'  height=21>最小流速</td><td align='center' class='body-data-title'  height=21>平均流速</td></tr>");

			String ip = "";
			String cur = "0";
			String max = "0";
			String min = "0";
			String avg = "0";
			String sname = "0";

			for (int j = 0; j < gridList.size(); j++) {
				StatisNumer number = new StatisNumer();
				number = (StatisNumer) gridList.get(j);
				String type = number.getType();
				ip = number.getIp();
				cur = number.getCurrent();
				min = number.getMininum();
				max = number.getMaximum();
				avg = number.getAverage();
				sname = number.getName();
				if (type.equals("gridPortIn")) {
					setInnerHtml(portHtml, ip + "(" + sname + "入口)", cur, max, min,
							avg, "kb/s");
				} else if (type.equals("gridPortOut")) {
					setInnerHtml(portHtml, ip + "(" + sname + "出口)", cur, max, min,
							avg, "kb/s");
				}
			}

			portHtml.append("</table>");
		}

		ReportValue portValue = new ReportValue();
		portValue = (ReportValue) valueMap.get("port");
		portipList = portValue.getIpList();
		utilInList = portValue.getListValue();
		utilOutList = portValue.getListTemp();

		// ///////端口//////////
		String portdata = "";
		StringBuffer portsb = new StringBuffer();

		if ((utilInList != null && utilInList.size() > 0)
				|| (utilOutList != null && utilOutList.size() > 0)) {
			for (int k = 0; k < utilInList.size(); k++) {
				List portdataList = (List) utilInList.get(k);
				if (portdataList != null && portdataList.size() > 0) {
					portsb.append("<chart><series>");
					for (int i = 0; i < portdataList.size(); i++) {
						Vector v = new Vector();
						v = (Vector) portdataList.get(i);
						portsb.append("<value xid='");
						portsb.append(i);
						portsb.append("'>");
						portsb.append(v.get(1));
						portsb.append("</value>");
					}

					portsb.append("</series><graphs>");
					break;
				}
			}
			List portdataList = (List) utilInList.get(0);

			if (utilInList != null && utilInList.size() > 0) {
				int count = 1;
				for (int j = 0; j < utilInList.size(); j++) {

					List portdataList1 = (List) utilInList.get(j);
					if (portdataList1 != null && portdataList1.size() > 0) {
						portsb.append("<graph title='"
								+ (String) portipList.get(j)
								+ "-入口' bullet='round' bullet_size='4'>");
						for (int i = 0; i < portdataList1.size(); i++) {

							Vector v = new Vector();
							v = (Vector) portdataList1.get(i);
							portsb.append("<value xid='");
							portsb.append(i);
							portsb.append("'>");
							portsb.append(v.get(0));
							portsb.append("</value>");
						}

						portsb.append("</graph>");
					}
				}
			}
			if (utilOutList != null && utilOutList.size() > 0) {

				for (int j = 0; j < utilOutList.size(); j++) {

					List portdataList1 = (List) utilOutList.get(j);
					if (portdataList1 != null && portdataList1.size() > 0) {
						portsb.append("<graph title='"
								+ (String) portipList.get(j)
								+ "-出口' bullet='round' bullet_size='4'>");
						for (int i = 0; i < portdataList1.size(); i++) {

							Vector v = new Vector();
							v = (Vector) portdataList1.get(i);
							portsb.append("<value xid='");
							portsb.append(i);
							portsb.append("'>");
							portsb.append(v.get(0));
							portsb.append("</value>");
						}

						portsb.append("</graph>");
					}
				}

			}
			if (portsb.toString().equals("") || portsb.toString() == null) {
			} else {
				portsb.append("</graphs></chart>");
				portdata = portsb.toString();
			}

		} else {
			portdata = "0";
		}

		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		//String time = sdFormat.format(cc);
		// ///////////////求当前、平均、最大值////////////
		Map<String, String> map = new HashMap<String, String>();
		map.put("portdata", portdata);

		map.put("portHtml", portHtml.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
	public void showMovement(){
		int id = this.getParaIntValue("clusterId");
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		List list = dao.loadClusterList(id);
		int[] step =new int[list.size()];
		String back = "";
		for(int i=0;i<list.size();i++){
			UpAndDownMachine v=(UpAndDownMachine)list.get(i);
			PingUtil pu = new PingUtil(v.getIpaddress());
			int t = (pu.ping()[0]==100) ? 2 : 1;
			step[i] = t;
			back += step[i]+",";
		}
		back = back.substring(0, back.length()-1);
		out.print(back);
		out.flush();
	}
	public void editClusterName() {
		int id=getParaIntValue("id");
		int clusterId=getParaIntValue("clusterId");
		
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		boolean isSucess=dao.updateClusterIdById(clusterId, id);
		HashMap<String,String> map=new HashMap<String,String>();
		
		ClusterDao clusterDao=new ClusterDao();
		Cluster cluster=(Cluster) clusterDao.findByID(clusterId+"");
		String name=cluster.getName();
		if (isSucess) {
			map.put("value", name);
		}else {
			map.put("value", "0");
		}
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();	
	}
	public void rebootAll() {
		
	UpAndDownMachineDao dao = new UpAndDownMachineDao();
	int id = getParaIntValue("clusterId");
	List list = dao.loadClusterList(id);
	dao.close();
	UpAndDownMachineDao udao = new UpAndDownMachineDao();
	//int second=0;
	boolean flag=true;
	Integer[] packet=null;
	for (int j = 0; j < list.size(); j++) {
		UpAndDownMachine machine = (UpAndDownMachine) list.get(j);
		RemoteClientInfo info = ShareData.getIp_clientInfoHash().get(
				machine.getIpaddress());
	//	 second=j*5;
		
		if (info != null) {
			
			String serverType=machine.getServerType();
			
			if (serverType.equals("windows")) {
				
				info.executeCmd("shutdown -r -f -t 0");
				
			}
			if (serverType.equals("linux")||serverType.equals("unix")) {
				info.executeCmd("shutdown -r  now");
				
			}
			if (serverType.equals("aix")) {
				info.executeCmd("shutdown –Fr");
				
			}
				try {
					while (flag) {
						PingUtil pingU=new PingUtil(machine.getIpaddress());
						 packet=pingU.ping();
						if (packet[0]!=null) {
							flag=false;
						}else {
							Thread.sleep(5000);
						}
					
					}
					flag=true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		
			info.closeConnection();
			machine.setMonitorStatus(0);
			machine.setLasttime(new Timestamp(new Date().getTime()));
			
			udao.addBatchUpdateAllTime(machine);
			

			ShareData.getIp_clientInfoHash().remove(machine.getIpaddress());
		}
	}
       udao.executeBatch();
       udao.close();
       }
	public void shutdownAll() {

		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		int id = getParaIntValue("clusterId");
		List list = dao.loadClusterList(id);
		dao.close();
		UpAndDownMachineDao udao = new UpAndDownMachineDao();
	//	int second=0;
		boolean flag=true;
		Integer[] packet=null;
		for (int j = 0; j < list.size(); j++) {
			UpAndDownMachine machine = (UpAndDownMachine) list.get(j);
			RemoteClientInfo info = ShareData.getIp_clientInfoHash().get(
					machine.getIpaddress());
	//		second=j*5;
			if (info != null) {
				String serverType=machine.getServerType();
				if (serverType.equals("windows")) {
					info.executeCmd("shutdown -f -t 0");
				}
				if (serverType.equals("linux")||serverType.equals("unix")) {
					info.executeCmd(" shutdown  now");
				}
				if (serverType.equals("aix")) {
					info.executeCmd("shutdown -F");
					
				}
				if (serverType.equals("as400")) {
					info.executeCmd("PWRDWNSYS *IMMED");
					
				}
				try {
					while (flag) {
						PingUtil pingU=new PingUtil(machine.getIpaddress());
						 packet=pingU.ping();
						if (packet[0]!=null) {
							flag=false;
						}else {
							Thread.sleep(5000);
						}
					
					}
					flag=true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				
			}
			info.closeConnection();
			machine.setMonitorStatus(0);
			machine.setLasttime(new Timestamp(new Date().getTime()));
			
			udao.addBatchUpdateAllTime(machine);
			

			ShareData.getIp_clientInfoHash().remove(machine.getIpaddress());
		}
          udao.executeBatch();
          udao.close();
	
	
	}
	public void updateSequence(){
		DBManager dbManager=new DBManager();
		String sql="";
		int id=0;
		int sequence=0;
		String ids=getParaValue("ids");
		String values=getParaValue("values");
		String[] idsArr=new String[ids.split(".").length];
		String[] valuesArr=new String[values.split(".").length];
		idsArr=ids.split("\\.");
		valuesArr=values.split("\\.");
	
		try {
			
		
		for (int i = 0; i < idsArr.length; i++) {
			id=Integer.parseInt(idsArr[i]);
			sequence=Integer.parseInt(valuesArr[i]);
			 sql="update nms_remote_up_down_machine set sequence="+sequence +" where id="+id;
			 //System.out.println(sql);
			 dbManager.addBatch(sql);
		}
		dbManager.executeBatch();
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			dbManager.close();
		}
		 HashMap<String,String> map=new HashMap<String,String>();
		map.put("data", "保存成功！！");
		
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();	
	}
	public void execMidReport() {


		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		
		I_HostCollectData hostmanager = new HostCollectDataManager();
		Date d = new Date();
		String startdate = getParaValue("startdate");

		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		String ids = getParaValue("ids");
		if (ids==null||ids.equals("")||ids.equals("null")) {
    		String id = request.getParameter("id");
    		if(id.equals("null"))return;
    		UtilReport report=new UtilReport();
    		UtilReportDao dao=new UtilReportDao();
    		report=(UtilReport) dao.findByBid(id);
    		ids=report.getIds();
		}
		String pingdata = "";
		String jvmdata = "";
		String[] idValue = null;
		if (ids != null && !ids.equals("null") && !ids.equals("")) {
			idValue = new String[ids.split(",").length];
			idValue = ids.split(",");
		}

		Hashtable pinghash = new Hashtable();
		Hashtable jvmhash = new Hashtable();
		Hashtable curhash = new Hashtable();
		List<List> pingList = new ArrayList<List>();
		List<List> jvmList = new ArrayList<List>();
		List<String> pingipList = new ArrayList<String>();
		List<String> jvmipList = new ArrayList<String>();
		StringBuffer pingHtml = new StringBuffer();
		StringBuffer jvmHtml = new StringBuffer();
		
		if (idValue != null && idValue.length > 0) {
			pingHtml.append("<table  border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=100 height=21>IP</td><td  class='body-data-title' height=21>当前连通率</td><td align='center' class='body-data-title'  height=21>最小连通率</td><td align='center' class='body-data-title'  height=21>平均连通率</td></tr>");
			jvmHtml.append("<table  border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=100 height=21>IP</td><td  class='body-data-title' height=21>当前利用率</td><td align='center' class='body-data-title'  height=21>最大利用率</td><td align='center' class='body-data-title'  height=21>平均利用率</td></tr>");
 
			for (int i = 0; i < idValue.length; i++) {

				String realValue = idValue[i];
				String[] idRelValue = new String[realValue.split("\\*").length];
				idRelValue = realValue.split("\\*");
				if(idRelValue.length<4)continue;
				String type = idRelValue[0].trim();
				String item = idRelValue[1].trim();
				String ip = idRelValue[3].trim();
				try {
				 if (type.equalsIgnoreCase("tomcat")) {
					 if (item.equals("ping")) {
						 pinghash = getCategory(ip, "TomcatPing",
									"ConnectUtilization", starttime, totime,"");
							curhash = hostmanager.getCurByCategory(ip, "TomcatPing",
									"ConnectUtilization");
						
						if (pinghash != null && pinghash.size() > 0) {
							List pingDataList = (List) pinghash.get("list");
							String pingCur=(String) curhash.get("pingCur");
							String pingAvg = (String) pinghash.get("avgpingcon");

							String pingMin = (String) pinghash.get("pingmax");

							setInnerHtml(pingHtml,ip,pingCur,pingMin,pingAvg,"%");
							if(pingDataList!=null&&pingDataList.size()>0)
							pingList.add(pingDataList);
						}
						pingipList.add(ip);
					}else if(item.equals("jvm")){
						 jvmhash = getCategory(ip, "tomcat_jvm","jvm_utilization", starttime, totime,"");
						Hashtable curJvm=hostmanager.getCurByCategory(ip, "tomcat_jvm","jvm_utilization");
						if (jvmhash != null && jvmhash.size() > 0) {
							List jvmDataList = (List) jvmhash.get("list");
							String jvmCur=(String) curJvm.get("pingCur");//当前虚拟利用率
							String jvmAvg = (String) jvmhash.get("avg_tomcat_jvm");

							String jvmMin = (String) jvmhash.get("max");

							setInnerHtml(jvmHtml,ip,jvmCur,jvmMin,jvmAvg,"%");
							if(jvmDataList!=null&&jvmDataList.size()>0)
							jvmList.add(jvmDataList);
							
						}
						jvmipList.add(ip);
					}
					 
				 }else if (type.equalsIgnoreCase("iis")) {
					 if (item.equals("ping")) {
						 pinghash = getCategory(ip, "IISPing",
									"ConnectUtilization", starttime, totime,"");
						 curhash = hostmanager.getCurByCategory(ip, "IISPing",
							"ConnectUtilization");	
						
						
						if (pinghash != null && pinghash.size() > 0) {
							List pingDataList = (List) pinghash.get("list");
							String pingCur=(String) curhash.get("pingCur");
							String pingAvg = (String) pinghash.get("avgpingcon");

							String pingMin = (String) pinghash.get("pingmax");

							setInnerHtml(pingHtml,ip,pingCur,pingMin,pingAvg,"%");
							if (pingDataList!=null&&pingDataList.size()>0)
							pingList.add(pingDataList);
						}
						pingipList.add(ip);
					}
				} 
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			//组合amcharts数据
			pingdata=makeAmChartData(pingList, pingipList);

			//jvm
			jvmdata=makeAmChartData(jvmList, jvmipList);

		}
		
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("pingdata", pingdata);
		map.put("pingHtml", pingHtml.toString());
		map.put("jvmdata", jvmdata);
		map.put("jvmHtml", jvmHtml.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();

	
	}
	/**
	 * 以表格的形式显示当前、平均、最大的利用率
	 * @param html
	 * @param ip
	 * @param cur
	 * @param min
	 * @param avg
	 */
	private void setInnerHtml(StringBuffer html,String ip,String cur,String min,String avg,String unit) {
		html.append("<tr bgcolor='#FFFFFF'><td align='center'>"
				+ ip + "</td>");
		html.append("<td align='center' height=21>"+cur.replace("%", "") + unit+"</td>");
		html.append("<td align='center' height=21>"+ min.replace("%", "") + unit+"</td>");
		html.append("<td align='center' height=21>"+ avg.replace("%", "") + unit+"</td></tr>");
	}
	/**
	 * 以表格的形式显示当前、平均、最大、最小的利用率
	 * @param html
	 * @param ip
	 * @param cur
	 * @param man
	 * @param min
	 * @param avg
	 */
	private void setInnerHtml(StringBuffer html,String ip,String cur,String max,String min,String avg,String unit) {
		html.append("<tr bgcolor='#FFFFFF'><td align='center'>"
				+ ip + "</td>");
		html.append("<td align='center' height=21>"+cur.replace("%", "") + unit+"</td>");
		html.append("<td align='center' height=21>"+ max.replace("%", "") + unit+"</td>");
		html.append("<td align='center' height=21>"+ min.replace("%", "") + unit+"</td>");
		html.append("<td align='center' height=21>"+ avg.replace("%", "") + unit+"</td></tr>");
	}	
	/**
	 * 以表格的形式显示当前的利用率
	 * @param html
	 * @param ip
	 * @param cur
	 */
	private void setInnerHtml(StringBuffer html,String ip,String cur,String unit) {
		html.append("<tr bgcolor='#FFFFFF'><td align='center' colspan=2>"
				+ ip + "</td>");
		html.append("<td align='center' height=21 colspan=2>"+cur.replace("%", "") + unit+"</td>");
		}
	/**
	 * 组合amcharts数据格式
	 * @param dataList
	 * @param ipList
	 * @return
	 */
	private String makeAmChartData(List dataList,List ipList){
		StringBuffer sb=new StringBuffer();
		String data="";
		if (dataList != null && dataList.size() > 0) {

			sb.append("<chart><series>");

			List eachDataList = (List) dataList.get(0);
			for (int k = 0; k < eachDataList.size(); k++) {
				Vector v = new Vector();
				v = (Vector) eachDataList.get(k);
				sb.append("<value xid='");
				sb.append(k);
				sb.append("'>");
				sb.append(v.get(1));
				sb.append("</value>");

			}
			
			sb.append("</series><graphs>");
			for (int j = 0; j < dataList.size(); j++) {
				
				List dataList1 = (List) dataList.get(j);
				sb.append("<graph title='" + (String) ipList.get(j)
						+ "' bullet='round_outlined' bullet_size='4'>");
				if (dataList1!=null&&dataList1.size()>0) {
				
				for (int m = 0; m < dataList1.size(); m++) {
					Vector v = new Vector();
					v = (Vector) dataList1.get(m);
					sb.append("<value xid='");
					sb.append(m);
					sb.append("'>");
					sb.append(v.get(0));
					sb.append("</value>");
					
				}
				}
				sb.append("</graph>");
			}

			sb.append("</graphs></chart>");
			
			data = sb.toString();
			
		} else {
			data = "0";
		}
		
		return data;
	}
	 public Hashtable getCategory(String ip,String category,String subentity,String starttime,String endtime,
				String time)
				throws Exception {
				Hashtable hash = new Hashtable();
			 	
			 	DBManager dbmanager = new DBManager();
			 	ResultSet rs = null;
				try{
					if (!starttime.equals("") && !endtime.equals("")) {
						
						String allipstr = SysUtil.doip(ip);
						String sql = "";
						StringBuffer sb = new StringBuffer();
						 if (category.equals("TomcatPing")){
							sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcatping"+time+allipstr+" h where ");
						 }
						 if (category.equals("tomcat_jvm")){
								sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcat_jvm"+allipstr+" h where ");
						}
						 if (category.equals("IISPing")){
								sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from iisping"+allipstr+" h where ");
						}
						sb.append(" h.category='");
						sb.append(category);
						sb.append("' and h.subentity='");
						sb.append(subentity);
						sb.append("' and h.collecttime >= '");
						sb.append(starttime);
						sb.append("' and h.collecttime <= '");
						sb.append(endtime);
						sb.append("' order by h.collecttime");
						sql = sb.toString();
						rs = dbmanager.executeQuery(sql);
						List list1 =new ArrayList();
						String unit = "";
						double tempfloat=0;
						double pingcon = 0;
						double tomcat_jvm_con = 0;
						int downnum = 0;
						int i=0;
				        while (rs.next()) {
				        	i=i+1;
				        	Vector v =new Vector();		        	
				            String thevalue=rs.getString("thevalue");
				            String collecttime = rs.getString("collecttime");		            
				            v.add(0,emitStr(thevalue));
				            v.add(1,collecttime);
				            v.add(2,rs.getString("unit"));
				            if ((category.equals("TomcatPing")||category.equals("IISPing"))&&subentity.equalsIgnoreCase("ConnectUtilization")){
				            	pingcon=pingcon+getfloat(thevalue);
				            	if(thevalue.equals("0")){
				            		downnum = downnum + 1;
				            	}	
				            }
				          
				            if (subentity.equalsIgnoreCase("ConnectUtilization")) {
				            	if (i==1)tempfloat = getfloat(thevalue);
				            	if (tempfloat > getfloat(thevalue))tempfloat = getfloat(thevalue);
				            }else if (category.equalsIgnoreCase("tomcat_jvm")){
				            	tomcat_jvm_con=tomcat_jvm_con+getfloat(thevalue);
				            	if (tempfloat < getfloat(thevalue))tempfloat = getfloat(thevalue);
				            }else{
				            	if (tempfloat < getfloat(thevalue))tempfloat = getfloat(thevalue);
				            }	
				            list1.add(v);	
				    }	
				        rs.close();
				        //stmt.close();
				        
						Integer size = new Integer(0);
						hash.put("list", list1);
						if (list1.size() != 0) {
							size = new Integer(list1.size());
							if (list1.get(0) != null) {
								Vector tempV = (Vector)list1.get(0);
								unit = (String)tempV.get(2);
							}
						}
						if ((category.equals("TomcatPing")||category.equals("IISPing"))&&subentity.equalsIgnoreCase("ConnectUtilization")){
							if (list1 !=null && list1.size()>0){
								hash.put("avgpingcon", CEIString.round(pingcon/list1.size(),2)+unit);						
								hash.put("pingmax", tempfloat+"");
								hash.put("downnum", downnum+"");
							}else{ 
								hash.put("avgpingcon", "0.0");	
								hash.put("pingmax", "0.0");
								hash.put("downnum", "0");
							}
						}
						if (category.equals("tomcat_jvm")){
							if (list1 !=null && list1.size()>0){
								hash.put("avg_tomcat_jvm",CEIString.round(tomcat_jvm_con/list1.size(), 2)+unit);				
							}else{ 
								hash.put("avg_tomcat_jvm", "0.0%");							
							}
						}
						hash.put("size", size);			
						hash.put("max", CEIString.round(tempfloat,2) + unit);
						hash.put("unit", unit);
				        }
					} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if (rs != null)
					rs.close();
					dbmanager.close();
				}
				
				return hash;
			}
	 private String emitStr(String num) {
			if (num != null) {
				if (num.indexOf(".")>=0){				
					if (num.substring(num.indexOf(".")+1).length()>7){
						String tempStr = num.substring(num.indexOf(".")+1);
						num = num.substring(0,num.indexOf(".")+1)+tempStr.substring(0,7);					
					}
				}
			}
			return num;
}
private double getfloat(String num) {
	double snum = 0.0;
	if (num != null) {
		if (num.indexOf(".")>=0){				
			if (num.substring(num.indexOf(".")+1).length()>7){
				String tempStr = num.substring(num.indexOf(".")+1);
				num = num.substring(0,num.indexOf(".")+1)+tempStr.substring(0,7);					
			}
		}
		int inum = (int) (Float.parseFloat(num) * 100);
		snum = new Double(inum/100.0).doubleValue();
	}
	return snum;
}
	
	
	 /**
	  * @author wxy
	  * @date 2011-5-10
	  * 生成网络设备、服务器报表
	  */
	 public void executeReport() {
		
	    	I_HostCollectData hostmanager = new HostCollectDataManager();
	    	String ids = request.getParameter("ids");
	    	if (ids==null||ids.equals("")||ids.equals("null")) {
	    		String id = request.getParameter("id");
	    		if(id.equals("null"))return;
	    		UtilReport report=new UtilReport();
	    		UtilReportDao dao=new UtilReportDao();
	    		report=(UtilReport) dao.findByBid(id);
	    		ids=report.getIds();
			}
	    	String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	    	String startTime = request.getParameter("startdate");
	    	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	    	String toTime = request.getParameter("todate");
	    	int beginHour=getParaIntValue("beginHour");
			int endHour= getParaIntValue("endHour");
			String begin=" "+beginHour+":00:00";
			String end=" "+endHour+":59:59";
			if(beginHour<10){
				begin=" 0"+beginHour+":00:00";
			}
			if (endHour<10) {
				end=" 0"+endHour+":59:59";
			}
			if (startTime == null) {
				startTime = sdf.format(new Date()) + " 00:00:00";
			} else {
				startTime = startTime + begin;
			}
			if (toTime == null) {
				toTime = sdf.format(new Date()) + " 23:59:59";
			} else {
				toTime = toTime + end;
			}
			List<List> pingList=new ArrayList<List>();
			List<List> list=new ArrayList<List>();
			List<List> memList=new ArrayList<List>();
			List<List> utilInList=new ArrayList<List>();
			List<List> utilOutList=new ArrayList<List>();
			List<List> diskList=new ArrayList<List>();
			List<String> pingipList=new ArrayList<String>();
			List<String> ipList=new ArrayList<String>();
			List<String> memipList=new ArrayList<String>();
			List<String> portipList=new ArrayList<String>();
			List<String> diskipList=new ArrayList<String>();
			 StringBuffer pingHtml=new StringBuffer();
			 StringBuffer cpuHtml=new StringBuffer();
			 StringBuffer memHtml=new StringBuffer();
			 StringBuffer portHtml=new StringBuffer();
			 StringBuffer diskHtml=new StringBuffer();
			 Vector<String> diskIpVec=new Vector<String>();
			  List<StatisNumer> diskSta=new ArrayList<StatisNumer>();
			  
	    	ReportHelper helper =new ReportHelper();
	    	HashMap valueMap=helper.getAllValue(ids, startTime, toTime);
	    	List<StatisNumer> gridList=new ArrayList<StatisNumer>();
	    	 gridList=(List<StatisNumer>)valueMap.get("gridVlue");
	    	 if (gridList!=null&&gridList.size()>0) {
	    		    pingHtml.append("<table  border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=100 height=21>IP</td><td  class='body-data-title' height=21>当前连通率</td><td align='center' class='body-data-title'  height=21>最小连通率</td><td align='center' class='body-data-title'  height=21>平均连通率</td></tr>");
					cpuHtml.append("<table   border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21>IP</td><td align='center' class='body-data-title'  height=21>当前利用率</td><td align='center' class='body-data-title'  height=21>最大利用率</td><td align='center' class='body-data-title'  height=21>平均利用率率</td></tr>");
					memHtml.append("<table   border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21>IP</td><td align='center' class='body-data-title'  height=21>当前利用率</td><td align='center' class='body-data-title'  height=21>最大利用率</td><td align='center' class='body-data-title'  height=21>平均利用率</td></tr>");
					portHtml.append("<table   border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21>端口名称</td><td align='center' class='body-data-title'  height=21>当前流速</td><td align='center' class='body-data-title'  height=21>最大流速</td><td align='center' class='body-data-title'  height=21>平均流速</td></tr>");
					diskHtml.append("<table   border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21 colspan=2>IP(磁盘名称)</td><td align='center' class='body-data-title'  height=21 colspan=2>当前利用率</td></tr>");
					  
			  String ip="";
			  String cur="0";
			  String max="0";
			  String min="0";
			  String avg="0";
			  String sname="0";
			 
	    	 for (int j = 0; j < gridList.size(); j++) {
	    		 StatisNumer number=new StatisNumer();
	    		 number=(StatisNumer)gridList.get(j);
	    		String  type=number.getType();
	    		 ip=number.getIp();
	    		 cur=number.getCurrent();
	    		 min=number.getMininum();
	    		 max=number.getMaximum();
	    		 avg=number.getAverage();
	    		 sname=number.getName();
	    		 if (type.equals("gridPing")) {
	    			 
	    			 setInnerHtml(pingHtml, ip, cur, min, avg,"%");
				}else if (type.equals("gridCpu")) {
					setInnerHtml(cpuHtml, ip, cur, max, avg,"%");
				}else if (type.equals("gridMem")) {
					setInnerHtml(memHtml, ip, cur, max, avg,"%");
				}else if (type.equals("gridPortIn")) {
					setInnerHtml(portHtml, ip+"("+sname+"入口)", cur, max, avg,"kb/s");
				}else if (type.equals("gridPortOut")) {
					setInnerHtml(portHtml, ip+"("+sname+"出口)", cur, max, avg,"kb/s");
				}else if (type.equals("gridDisk")) {
					setInnerHtml(diskHtml, ip+"("+sname+")", cur,"%");
					diskSta.add(number);
					if(!diskIpVec.contains(ip))
						diskIpVec.add(ip);
				}
			   }
	    	
	    	    pingHtml.append("</table>");
				cpuHtml.append("</table>");
				memHtml.append("</table>");
				portHtml.append("</table>");
				diskHtml.append("</table>");
	    	 }
	    	/////////////////////////////////
	    	ReportValue pingValue=new ReportValue();
	    	pingValue=(ReportValue)valueMap.get("ping");
	    	
	    	pingipList=pingValue.getIpList();
	    	pingList=pingValue.getListValue();
	    	
	    	ReportValue memValue=new ReportValue();
	    	memValue=(ReportValue)valueMap.get("mem");
	    	memipList=memValue.getIpList();
	    	memList=memValue.getListValue();
	    	
	    	ReportValue cpuValue=new ReportValue();
	    	cpuValue=(ReportValue)valueMap.get("cpu");
	    	ipList=cpuValue.getIpList();
	    	list=cpuValue.getListValue();
	    	
	    	ReportValue portValue=new ReportValue();
	    	portValue=(ReportValue)valueMap.get("port");
	    	portipList=portValue.getIpList();
	    	utilInList=portValue.getListValue();
	    	utilOutList=portValue.getListTemp();
	    	// 磁盘
	    	ReportValue diskValue=new ReportValue();
	    	diskValue=(ReportValue)valueMap.get("disk");
	    	diskipList=diskValue.getIpList();
	    	diskList=diskValue.getListValue();
	    	//////cpu利用率///////////
			 String netdata="";
			 netdata=makeAmChartData(list, ipList);
			
		      /////////连通率//////////	
			 String pingdata="";
			pingdata=makeAmChartData(pingList, pingipList);

	        /////////内存//////////	
			 String memdata=makeAmChartData(memList, memipList);
	
			/////////端口//////////	
			String portdata="";
			StringBuffer portsb=new StringBuffer();
			
			if((utilInList!=null&&utilInList.size()>0)||(utilOutList!=null&&utilOutList.size()>0)){
				
				
				for (int k = 0; k < utilInList.size(); k++) {
					List portdataList=(List)utilInList.get(k);
					if(portdataList!=null&&portdataList.size()>0){
						portsb.append("<chart><series>");
					for(int i=0;i<portdataList.size();i++)
					{
						Vector v = new Vector();
						v=(Vector)portdataList.get(i);
						portsb.append("<value xid='");
						portsb.append(i);
						portsb.append("'>");
						portsb.append(v.get(1));
						portsb.append("</value>");
					}
					
					portsb.append("</series><graphs>");
					break;
					}
				}
				List portdataList=(List)utilInList.get(0);
				
				if (utilInList!=null&&utilInList.size()>0) {
				    int count=1;
					for (int j = 0; j< utilInList.size(); j++) {
					
					List portdataList1=(List)utilInList.get(j);
					if(portdataList1!=null&&portdataList1.size()>0){
					portsb.append("<graph title='"+(String)portipList.get(j)+"-入口' bullet='round' bullet_size='4'>");
					for(int i=0;i<portdataList1.size();i++)
					{
						
						Vector v = new Vector();
						v=(Vector)portdataList1.get(i);
						portsb.append("<value xid='");
						portsb.append(i);
						portsb.append("'>");
						portsb.append(v.get(0));
						portsb.append("</value>");
						 }
					
					portsb.append("</graph>");
					}
				}
				}
			if (utilOutList!=null&&utilOutList.size()>0) {
				
	         for (int j = 0; j< utilOutList.size(); j++) {
	        	 
					List portdataList1=(List)utilOutList.get(j);
					if(portdataList1!=null&&portdataList1.size()>0){
					portsb.append("<graph title='"+(String)portipList.get(j)+"-出口' bullet='round' bullet_size='4'>");
					for(int i=0;i<portdataList1.size();i++)
					{
						
						Vector v = new Vector();
						v=(Vector)portdataList1.get(i);
						portsb.append("<value xid='");
						portsb.append(i);
						portsb.append("'>");
						portsb.append(v.get(0));
						portsb.append("</value>");
						 }
					
					portsb.append("</graph>");
					}
				}
	         
				}
			if (portsb.toString().equals("")||portsb.toString()==null) {
				
			}else {
				portsb.append("</graphs></chart>");
				portdata=portsb.toString();	
			}
				
			}else {
				portdata="0";	
			}
/////////////////磁盘利用率////////////

			 String diskdata=getDiskAmData(diskSta, diskIpVec);
	//		 StringBuffer disksb=new StringBuffer(); 
			 
//			if(diskList!=null&&diskList.size()>0){
//				
//				String temp="";
//				disksb.append("<chart><series>");
//				   List diskdataList=(List)diskList.get(0);
//				   List list3=(List)diskdataList.get(0);
//					   for(int i=0;i<list3.size();i++)
//					   {
//						  
//					   Vector v = new Vector();
//					   v=(Vector)list3.get(i);
//					   
//					   disksb.append("<value xid='");
//					   disksb.append(i);
//					   disksb.append("'>");
//					   disksb.append(v.get(3));
//					   disksb.append("</value>");
//					   }
//					   disksb.append("</series><graphs>");
//					 
//					  
//					   for (int j = 0; j< diskList.size(); j++) {
//						   
//						   List diskdataList1=(List)diskList.get(j);
//						    for(int i=0;i<diskdataList1.size();i++)
//					   {
//						  disksb.append("<graph title='"+(String)diskipList.get(i)+"' bullet='round_outlined' bullet_size='4'>");
//						   List list2=(List) diskdataList.get(i);
//						   for (int k = 0; k < list2.size(); k++) {
//							   Vector v = new Vector();
//							   v=(Vector)list2.get(k);
//							   
//							   disksb.append("<value xid='");
//							   disksb.append(k);
//							   disksb.append("'>");
//							   disksb.append(v.get(0));
//							   disksb.append("</value>");
//						}
//					 
//						   disksb.append("</graph>");
//					  }
//					  
//					  
//					   }
//					  
//					   disksb.append("</graphs></chart>");
//					   diskdata=disksb.toString();
//					
//				}else{
//					diskdata="0";	
//				}
			Calendar tempCal=Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdFormat.format(cc);
			/////////////////求当前、平均、最大值////////////
			Map<String,String> map = new HashMap<String,String>();
			map.put("pingdata", pingdata);
			map.put("netdata", netdata);
			map.put("memdata", memdata);
			map.put("portdata", portdata);
			map.put("diskdata", diskdata);
		
			map.put("pingHtml", pingHtml.toString());
			map.put("cpuHtml", cpuHtml.toString());
			map.put("memHtml", memHtml.toString());
			map.put("portHtml", portHtml.toString());
			map.put("diskHtml", diskHtml.toString());
			JSONObject json = JSONObject.fromObject(map);
			out.print(json);
			out.flush();
		}
private String getDiskAmData(List<StatisNumer> list,Vector<String> ipVector){
	String diskData="0";
	StringBuffer diskBuffer=new StringBuffer();
	HashMap<String, Integer> map=new HashMap<String, Integer>();
	if (ipVector!=null&&ipVector.size()>0) {
	//	diskBuffer.append("<chart><series>");
		for (int i = 0; i < ipVector.size(); i++) {
//			diskBuffer.append("<value xid='"+i+"'>");
//			diskBuffer.append(ipVector.get(i));
//			diskBuffer.append("</value>");
		if (map.get(ipVector.get(i))==null) {
			map.put(ipVector.get(i), i);
		}
		   }
	//	diskBuffer.append("</series>");
	}
   
	String[] colorStr=new String[] {"#FF6600","#FCD202","#B0DE09","#0D8ECF","#A52A2A","#33FF33","#FF0033","#9900FF","#FFFF00","#0000FF","#A52A2A","#23f266"};
		
	if (list!=null&&list.size()>0) {
		diskBuffer.append("<chart><series>");
		for (int i = 0; i < list.size(); i++) {
			StatisNumer numer=new StatisNumer();
			numer=list.get(i);
			String ip1=numer.getIp();
			String name1=numer.getName();
			String cur1=numer.getCurrent().replace("%", "");
			
			diskBuffer.append("<value xid='"+i+"'>");
			diskBuffer.append(name1+"</value>");
			
		}
		diskBuffer.append("</series><graphs>");
		diskBuffer.append("<graph >");
		for (int i = 0; i < list.size(); i++) {
			StatisNumer numer=new StatisNumer();
			numer=list.get(i);
			String ip1=numer.getIp();
			String name1=numer.getName();
			String cur1=numer.getCurrent().replace("%", "");
			
			diskBuffer.append("<value xid='"+i+"' color='"+colorStr[map.get(ip1)]+"'>");
			diskBuffer.append(cur1+"</value>");
			
		}
		diskBuffer.append("</graph>");
		diskData=diskBuffer.toString();
	}else {
		diskData="0";
	}
	return diskData;
}
	 /**
	  * @author wxy
	  * 修改系统别名
	  */
 private void editAlias() {
		
	String flagStr="1";
	HostNode vo = new HostNode();
	
	String tempId=request.getParameter("id");
	
	int id=Integer.parseInt(tempId);
	 vo.setId(id);
    vo.setIpAddress(getParaValue("ip"));
    
    String alias = null;
	try {
		alias = URLDecoder.decode(getParaValue("alias"), "UTF-8");
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	vo.setAlias(alias);
	
    //vo.setAlias(getParaValue("alias"));
       
    //更新数据库
	   HostNodeDao dao = new HostNodeDao();
	   HostNode node = dao.loadHost(id);
	    boolean flag=dao.editAlias(vo);
	    if (flag) {
	    	//更新内存
		    Host host = (Host)PollingEngine.getInstance().getNodeByID(vo.getId()); 
		    host.setAlias(vo.getAlias());
		    host.setIpAddress(vo.getIpAddress());
			flagStr="1";
			
			// 同步更新拓扑图  --by hipo
			ManageXmlOperator mXmlOpr = new ManageXmlOperator();
    		mXmlOpr.setFile("network.jsp");
    		mXmlOpr.init4updateXml();
    		if (mXmlOpr.isNodeExist("net" + vo.getId())) {
    			mXmlOpr.updateNode("net" + vo.getId(), "alias", vo.getAlias());
    			String info = "设备标签:" + vo.getAlias() + "<br>IP地址:" + vo.getIpAddress();
    			mXmlOpr.updateNode("net" + vo.getId(), "info", info);
    		} else {
    			SysLogger.error("ServerAjaxManager.editAlias:"+"拓扑图没有该节点");
    		}
    		mXmlOpr.writeXml();
    		
		}else {
			flagStr="0";
		}
		Map<String,String> map = new HashMap<String,String>();
		map.put("flagStr", flagStr);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
 
	private void editSysname() {
		
		String flagStr="0";
		HostNode vo = new HostNode();
		HostNodeDao dao = new HostNodeDao();
		String tempId=request.getParameter("id");
		int id=Integer.parseInt(tempId);
		try{
			vo = dao.loadHost(id);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
        vo.setId(id);
        vo.setSysName(getParaValue("sysname"));
        vo.setSysContact(getParaValue("syscontact"));
        vo.setSysLocation(getParaValue("syslocation"));
        
        Hashtable mibvalues = new Hashtable();
        mibvalues.put("sysContact", getParaValue("syscontact"));
        mibvalues.put("sysName", getParaValue("sysname"));
        mibvalues.put("sysLocation", getParaValue("syslocation"));
        
        //更新数据库
        //dao.close();
        dao = new HostNodeDao();
 	    boolean flag = true; 
 	    String writeCommunity=vo.getWriteCommunity();
 	    if (writeCommunity!=null&&!writeCommunity.equals("null")&&writeCommunity.trim().length()>0) {
 	    	
 	    	try{
 	 	    	flag = dao.updatesysgroup(vo,mibvalues);
 	 	   }catch(Exception e){
 				e.printStackTrace();
 			}finally{
 				dao.close();
 			}
 			
		}else {
			flagStr="2";//
			flag=false;
		}
 	    
 	    if(flag){
 	    	//更新内存
 	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(vo.getId()); 	  
 	    	host.setSysName(getParaValue("sysname"));
 	    	host.setSysContact(getParaValue("syscontact"));
 	    	host.setSysLocation(getParaValue("syslocation"));
 	    	flagStr="1";
 	    }	   
		Map<String,String> map = new HashMap<String,String>();
		map.put("flagStr", flagStr);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
	private void inSpeedTop5() {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		//ResultSet rs = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());	
		String starttime = time1 + " 00:00:00";
		String totime = time1 + " 23:59:59";
		String ip=request.getParameter("ip");
		String type=request.getParameter("type");
		Vector vec =new Vector();
		Vector vector =new Vector();
		 StringBuffer sb=new StringBuffer();
		 String dataStr="";
		 String[] colorStr = new String[] { "#D4B829", "#F57A29", "#B5DB2F", "#3189B5", "#AE3174","#FFFF00","#333399","#0000FF","#A52A2A","#23f266"};
		 String[] percolorStr = new String[] { "#0000FF", "#36DB43", "#3DA4D8", "#556B2F", "#8470F4","#8A2BE2","#23f266","#F7FD31","#8B4513","FFD700"};

		 I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
	     //   String[] netInterfaceItem={"index","ifDescr","ifSpeed","ifOperStatus","ifOutBroadcastPkts","ifInBroadcastPkts","ifOutMulticastPkts","ifInMulticastPkts","OutBandwidthUtilHdx","InBandwidthUtilHdx"};
        String[] netInterfaceItem={"ifDescr",""}; 
        netInterfaceItem[1]=type;
        try{
        	String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
        	if("0".equals(runmodel)){//采集与访问是集成模式
        		vector = hostlastmanager.getInterface_share(ip,netInterfaceItem,type,starttime,totime);  
        	}else {
        		vector = hostlastmanager.getInterface(ip,netInterfaceItem,type,starttime,totime); 
			}
        	
        	
        }catch(Exception e){
        	e.printStackTrace();
        }	
		 try {
//			
			String allipstr = SysUtil.doip(ip);
			DateInformation di = new DateInformation();
			
			
//			String sql2 = "";
//			
//				sql2 =
//					"select DATE_FORMAT(h.collecttime,'%H:%i:%s') as colltime,h.thevalue,h.sindex from nms_interface_data_temp h where "
//						+ "  h.subentity='"+type
//						+ "' and h.collecttime >='"
//						+ starttime
//						+ "' and h.collecttime <='"
//						+ totime
//						+ "' order by h.thevalue desc,h.sindex asc LIMIT 10";
//			
//			List list = new ArrayList();
//			
//			rs = dbmanager.executeQuery(sql2);			
//	        while (rs.next()) {
//	        	Vector v =new Vector();		        	
//	            String thevalue=rs.getString("thevalue");
//	            String sindex = rs.getString("sindex");		            
//	            v.add(0,thevalue);
//	            v.add(1,sindex);
//	            v.add(2,rs.getString("colltime"));
//	            list.add(v);
//	        }		
//	       
//			sb.append("<chart><series>");
//			
//			 for(int i=0;i<list.size();i++){
//			 vec=(Vector)list.get(i);
//			 sb.append("<value xid='"+i);
//			 sb.append("'>");
//			 sb.append(vec.get(1)+"</value>");
//			 }
//			 sb.append("</series><graphs><graph>");
//			  for(int i=0;i<list.size();i++){
//			 vec=(Vector)list.get(i);
//			 sb.append("<value xid='"+i).append("' color='")
//				.append(colorStr[i]);
//			 sb.append("'>");
//			 sb.append(vec.get(0)+"</value>");
//			 }
//			 sb.append("</graph></graphs></chart>");
			
		
			if (vector!=null&&vector.size()>0) {
				sb.append("<chart><series>");
			 for(int i=0 ;i<5&&i<vector.size() ; i++){
	                String[] strs = (String[])vector.get(i);
	                
	                String ifname = strs[0];
	              
	   			 sb.append("<value xid='"+i);
	   			 sb.append("'>");
	   			 sb.append(ifname+"</value>");
	                }
			 
			 sb.append("</series><graphs><graph>");
			 String speed ="";
			 
			 for(int i=0 ;i<5&&i<vector.size() ; i++){
				 String[] strs = (String[])vector.get(i);
	               if (type.equals("OutBandwidthUtilHdxPerc")||type.equals("InBandwidthUtilHdxPerc")) {
	            	   speed = strs[1].replaceAll("%", "")+"";
	            	   sb.append("<value xid='"+i).append("' color='")
	   				.append(percolorStr[i]);
				}else{
					 speed = strs[1].replaceAll("kb/s", "").replaceAll("kb/秒","");
					 sb.append("<value xid='"+i).append("' color='")
						.append(colorStr[i]);
				}
	              
			
			 sb.append("'>");
			 sb.append(speed+"</value>");
			 }
			 sb.append("</graph></graphs></chart>");
			 dataStr=sb.toString();
			}else{
				dataStr="0";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
//			if (rs != null)
//				try {
//					rs.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}				
			dbmanager.close();
		}
		Map<String,String> map = new HashMap<String,String>();
		map.put("dataStr", dataStr);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
		
	}
	private void ajaxHostInfolist() {
		try {
			String typeString = request.getParameter("type");
			List dbList = null;
			int typeInt = 4;//默认的是服务器
			List monitorDBDTOList = new ArrayList();
			//取出该用户的业务权限id
			User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
			if(current_user == null){
				return;
			}
			String[] bids = null;
			if (current_user.getBusinessids() != null){
				if(current_user.getBusinessids() !="-1"){
					bids = current_user.getBusinessids().split(",");
				}
			}
			if("host".equalsIgnoreCase(typeString)){
				typeInt = 4;
			}else if("net".equalsIgnoreCase(typeString)){
				typeInt = 1;//网络设备
			}else if("db".equalsIgnoreCase(typeString)){
				int flag = 1;//被管理状态的数据库
				DataBaseManager dbManager = new DataBaseManager();
				ServerAjaxManager serverAjaxManager = new ServerAjaxManager();
				dbList = serverAjaxManager.getList(bids);
				DBTypeVo oraVo = null;
				DBTypeDao typedao = new DBTypeDao();
				try {
					oraVo = (DBTypeVo) typedao.findByDbtype("Oracle");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					typedao.close();
				}
				for (int i = 0; i < dbList.size(); i++) {
					DBVo vo = (DBVo) dbList.get(i);
					MonitorDBDTO monitorDBDTO = dbManager.getMonitorDBDTOByDBVo(vo,0);
					monitorDBDTOList.add(monitorDBDTO);
				}
				try {
					Map<String, List<MonitorNodeDTO>> map = new HashMap<String, List<MonitorNodeDTO>>();
					map.put("monitorDBDTOList", monitorDBDTOList);
					JSONObject json = JSONObject.fromObject(map);
					out.print(json);
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
				//数据库的情况  访问结束return  
				return ;
			}
			String start = request.getParameter("start");
			String limit = request.getParameter("limit");
			//网络设备和服务器的情况
			HostNodeDao hostNodeDao = null;
			List<HostNode> monitornodelist = null;
			//int totalRecords = 0;//记录的条数
			try {
				hostNodeDao = new HostNodeDao();
				//totalRecords = hostNodeDao.getMonitorCountByMonCategory(1, typeInt, bids);
				//monitornodelist = hostNodeDao.loadMonitorByMonCategory(1, typeInt, bids, start, limit);//取出被管理状态的服务器列表  查询所有
				monitornodelist = hostNodeDao.loadMonitorByMonCategory(1, typeInt, bids);//取出被管理状态的服务器列表  分段查询
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				if(hostNodeDao != null){
					hostNodeDao.close();
				}
			}
			if (monitornodelist == null || monitornodelist.size() == 0) {
				return ;
			}
			List<MonitorNodeDTO> list = new ArrayList<MonitorNodeDTO>();
			String runmodel = PollingEngine.getCollectwebflag(); 
			PerformanceManager performanceManager = new PerformanceManager();
			
			for(int i=0; i<monitornodelist.size(); i++){
				HostNode hostNode = monitornodelist.get(i);
				MonitorNodeDTO monitorNodeDTO = new MonitorNodeDTO();
				//采集与访问集成模式 
				monitorNodeDTO = performanceManager.getMonitorNodeDTOByHostNode(hostNode);
				list.add(monitorNodeDTO);
			}
			//如果是“采集与访问集成模式”，在上面的for循环中已经做出采集
			if("1".equals(runmodel)){
		       	//采集与访问是分离模式
				//monitornetlist  网络设备监控列表
				//取出可用性
				PingInfoService pingInfoService = new PingInfoService();
				List<NodeTemp> pingInfoList = pingInfoService.getPingInfo(monitornodelist);
				//取出网络设备的CPU利用率信息
				CpuInfoService cpuInfoService = new CpuInfoService();
				List<NodeTemp> cpuInfoList = cpuInfoService.getCpuPerListInfo(monitornodelist);
				//取出网络设备的内存利用率信息
				MemoryInfoService memoryInfoService = new MemoryInfoService();
				List<NodeTemp> memoryList = memoryInfoService.getMemoryInfo(monitornodelist);
				//取出端口流速信息
				InterfaceInfoService interfaceInfoService = new InterfaceInfoService();
				List<NodeTemp> interfaceList = interfaceInfoService.getInterfaceInfo(monitornodelist);
				
				//将取出的信息组合成List<MonitorNodeDTO>
				for(int i=0; i<list.size(); i++){
					MonitorNodeDTO monitorNodeDTO = (MonitorNodeDTO)list.get(i);
					if(pingInfoList != null){
						for(int j=0; j<pingInfoList.size(); j++){
							NodeTemp nodeTemp = pingInfoList.get(j);
							if((monitorNodeDTO.getId()+"").equals(nodeTemp.getNodeid())){
								monitorNodeDTO.setPingValue(nodeTemp.getThevalue());
							}
						}
					}
					if(cpuInfoList != null){
						for(int j=0; j<cpuInfoList.size(); j++){
							NodeTemp nodeTemp = cpuInfoList.get(j);
							if((monitorNodeDTO.getId()+"").equals(nodeTemp.getNodeid())){
								monitorNodeDTO.setCpuValue(nodeTemp.getThevalue());
							}
						}
					}
					if(memoryList != null){
						for(int j=0; j<memoryList.size(); j++){
							NodeTemp nodeTemp = memoryList.get(j);
							if((monitorNodeDTO.getId()+"").equals(nodeTemp.getNodeid())){
								monitorNodeDTO.setMemoryValue(nodeTemp.getThevalue());
							}
						}
					}
					if(interfaceList != null){
						for(int j=0; j<interfaceList.size(); j++){
							NodeTemp nodeTemp = interfaceList.get(j);
							if((monitorNodeDTO.getId()+"").equals(nodeTemp.getNodeid())){
								if(nodeTemp.getSubentity().equalsIgnoreCase("AllInBandwidthUtilHdx")){
									monitorNodeDTO.setInutilhdxValue(nodeTemp.getThevalue());
								}
								if(nodeTemp.getSubentity().equalsIgnoreCase("AllOutBandwidthUtilHdx")){
									monitorNodeDTO.setOututilhdxValue(nodeTemp.getThevalue());
								}
							}
						}
					}
				}
			}
			//取出接口数量 
			HostInterfaceDao hostInterfaceDao = new HostInterfaceDao();
			Hashtable hostInterfacehash = null;
			try{
				 hostInterfacehash = hostInterfaceDao.getHostInterfaceList(monitornodelist);
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				hostInterfaceDao.close();
			}
			//设定cpu和内存的告警颜色
			double cpuValueDouble = 0;
			double memoryValueDouble = 0;
			String cpuValueColor = "green";			// cpu 颜色
			String memoryValueColor = "green";		// memory 颜色
			for(int i=0; i<list.size(); i++){
				MonitorNodeDTO monitorNodeDTO = (MonitorNodeDTO)list.get(i);
				String cpuValueStr = monitorNodeDTO.getCpuValue();
				String memoryValueStr = monitorNodeDTO.getMemoryValue();
				if(cpuValueStr != null){
					cpuValueDouble = Double.parseDouble(cpuValueStr);
				}
				if(memoryValueStr != null){
					memoryValueDouble = Double.parseDouble(memoryValueStr);
				}
				//设定cpu和内存的告警颜色
//				AlarmIndicatorsNodeDao nmDao = new AlarmIndicatorsNodeDao();
//				List nodeMonitorList = null;
//				try {
//					nodeMonitorList = nmDao.findByNodeIdAndTypeAndSubType(nodeId+"",typeString,"");
//				} catch (Exception e) {
//					e.printStackTrace();
//				} finally {
//					nmDao.close();
//				}   
//				if(nodeMonitorList != null){
//					for(int j = 0 ; j < nodeMonitorList.size(); j ++){
//						NodeMonitor nodeMonitor = (NodeMonitor)nodeMonitorList.get(j);
//						if("cpu".equals(nodeMonitor.getCategory())){
//							if(cpuValueDouble > nodeMonitor.getLimenvalue2()){
//								cpuValueColor = "red";
//							}else if(cpuValueDouble > nodeMonitor.getLimenvalue1()){
//								cpuValueColor = "orange";
//							}else if(cpuValueDouble > nodeMonitor.getLimenvalue0()){
//								cpuValueColor = "yellow";
//							}else {
//								cpuValueColor = "green";
//							}
//						}
//						if("memory".equals(nodeMonitor.getCategory())){
//							if(memoryValueDouble > nodeMonitor.getLimenvalue2()){
//								memoryValueColor = "red";
//							}else if(memoryValueDouble > nodeMonitor.getLimenvalue1()){
//								memoryValueColor = "orange";
//							}else if(memoryValueDouble > nodeMonitor.getLimenvalue0()){
//								memoryValueColor = "yellow";
//							}else {
//								memoryValueColor = "green";
//							}
//						}
//					}
//				}
//				monitorNodeDTO.setCpuValueColor(cpuValueColor);
//				monitorNodeDTO.setMemoryValueColor(memoryValueColor);
				//接口数量
				if(hostInterfacehash != null && !hostInterfacehash.isEmpty()){
					Iterator iterator = hostInterfacehash.keySet().iterator();
					while (iterator.hasNext()) {
						String nodeid = (String) iterator.next();
						Integer entityNumber = Integer.parseInt(String.valueOf(hostInterfacehash.get(nodeid))); 
						if((monitorNodeDTO.getId()+"").equals(nodeid)){
							monitorNodeDTO.setEntityNumber(entityNumber); 
						}
					}
				}
			}
			//CpuInfoService cpuInfoService = new CpuInfoService();
			//List<NodeTemp> cpuList = cpuInfoService.getPerCpuList(nodeids.toString());
			Map<String, List<MonitorNodeDTO>> map = new HashMap<String, List<MonitorNodeDTO>>();
			map.put("monitorNodeList", list);
			JSONObject json = JSONObject.fromObject(map);
			//json.put("TotalRecords", totalRecords);
			out.print(json);
			out.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally{
			if(out != null){
				out.close();
			}
		}
	}
	
	private void ajaxUpdate_availability()
	{
		String tmp = "";
		String pingconavg ="0";
		tmp = request.getParameter("tmp");
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		
//		SysLogger.info("ServerAjaxManager----------->"+tmp+"-----------host----------->"+host);
		
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		I_HostCollectData hostmanager=new HostCollectDataManager();
		Hashtable ConnectUtilizationhash = new Hashtable();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			//ex.printStackTrace();
		}
		if (ConnectUtilizationhash.get("avgpingcon")!=null)
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
		if(pingconavg != null){
			pingconavg = pingconavg.replace("%", "");
		}
		int percent1 = Double.valueOf(pingconavg).intValue();
		int percent2 = 100-percent1;
		Map<String,Float> map = new HashMap<String,Float>();
		
		map.put("percent1", Float.parseFloat(percent1+""));
		map.put("percent2", Float.parseFloat(percent2+""));
		String ip = "";
		if(host != null){
			ip=host.getIpAddress();
		}
		 
			
		/*
		String vvalue = "0";
		String pvalue = "0";
		
		float capvalue = 0f;
		float vused = 0f;
		
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		
		List gatherlist = new ArrayList();
		try{
			NodeDTO nodeDTO = null;
			NodeUtil nodeutil= new NodeUtil();
			nodeDTO = nodeutil.creatNodeDTOByHost(host);
			//SysLogger.info(nodeDTO.getSubtype()+"================"+nodeDTO.getType());
			if("windows".equalsIgnoreCase(nodeDTO.getSubtype())){
				//windows服务器重新进行内存采集更新
				gatherlist = indicatorsdao.findByNodeIdAndTypeAndSubtype(host.getId()+"", nodeDTO.getType(), nodeDTO.getSubtype());
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			indicatorsdao.close();
		}
		
		//Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicatorsNode.getNodeid()));
		//if(node != null){
		if(gatherlist != null && gatherlist.size()>0){
			for(int i=0;i<gatherlist.size();i++){
				NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)gatherlist.get(i);
				//SysLogger.info(nodeGatherIndicators.getName()+"================");
				if("virtualmemory".equalsIgnoreCase(nodeGatherIndicators.getName())){
					//进行虚拟内存的采集
					WindowsVirtualMemorySnmp windowsvirtualsnmp = null;
	    			try{
	    				windowsvirtualsnmp = (WindowsVirtualMemorySnmp)Class.forName("com.afunms.polling.snmp.memory.WindowsVirtualMemorySnmp").newInstance();
	        			Hashtable returnHash = windowsvirtualsnmp.collect_Data(nodeGatherIndicators);
	        			HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
	        			hostdataManager.createHostItemData(host.getIpAddress(),returnHash,"host","windows","virtualmemory");
	        			
	        			Vector memoryVector = (Vector) returnHash.get("memory");
						if (memoryVector != null && memoryVector.size() > 0) {
							for (int si = 0; si < memoryVector.size(); si++) {
								Memorycollectdata memorydata = (Memorycollectdata) memoryVector.elementAt(si);
								if(!memorydata.getSubentity().equalsIgnoreCase("VirtualMemory"))continue;
								if (memorydata.getRestype().equals("dynamic")) {
									vvalue = memorydata.getThevalue();
								}
								if("VirtualMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Capability".equalsIgnoreCase(memorydata.getEntity())) {
									capvalue = Float.parseFloat(memorydata.getThevalue());
								}
							}

						}
	        			
	        			
	        		}catch(Exception e){
	        			e.printStackTrace();
	        		}
				}else if("physicalmemory".equalsIgnoreCase(nodeGatherIndicators.getName())){
					//进行物理内存的采集
					WindowsPhysicalMemorySnmp windowsphysicalsnmp = null;
	    			try{
	    				windowsphysicalsnmp = (WindowsPhysicalMemorySnmp)Class.forName("com.afunms.polling.snmp.memory.WindowsPhysicalMemorySnmp").newInstance();
	        			Hashtable returnHash = windowsphysicalsnmp.collect_Data(nodeGatherIndicators);
	        			HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
	        			hostdataManager.createHostItemData(host.getIpAddress(),returnHash,"host","windows","physicalmemory");
	        			
	        			Vector memoryVector = (Vector) returnHash.get("memory");
						if (memoryVector != null && memoryVector.size() > 0) {
							for (int si = 0; si < memoryVector.size(); si++) {
								Memorycollectdata memorydata = (Memorycollectdata) memoryVector.elementAt(si);
								if(!memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory"))continue;
								if (memorydata.getRestype().equals("dynamic")) {
									pvalue = memorydata.getThevalue();
									SysLogger.info("pvalue============"+pvalue);
								}
							}

						}
	        			
	        			
	        		}catch(Exception e){
	        			e.printStackTrace();
	        		}
				}
			}
		}
		DecimalFormat df=new DecimalFormat("#.##");
		vused = Float.parseFloat(capvalue*Float.parseFloat(vvalue)/100+"");
		
		map.put("vvalue", Float.parseFloat(Math.round(Float.parseFloat(vvalue))+""));
		map.put("pvalue", Float.parseFloat(Math.round(Float.parseFloat(pvalue))+""));
		map.put("vused", Float.parseFloat(df.format(vused)+""));
		*/
		
		
		
		//SysLogger.info("vused============"+vused);
		
		double cpuvalue = 0;
		//Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		Hashtable ipAllData = null;
		try{
			 ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		}catch(Exception ex){
			//ex.printStackTrace();
		}
		if(ipAllData != null){
			Vector cpuV = (Vector)ipAllData.get("cpu");
			if(cpuV != null && cpuV.size()>0){
				
				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
		}
		int cpuper = Double.valueOf(cpuvalue).intValue();
		
		String picip = CommonUtil.doip(ip);
		CreatePiePicture _cpp = new CreatePiePicture();
		_cpp.createAvgPingPic(picip,Double.valueOf(pingconavg)); 
		
		//生成CPU仪表盘
		CreateMetersPic cmp = new CreateMetersPic();
		cmp.createCpuPic(picip,cpuper);	
		map.put("cpuper", Float.parseFloat(cpuper+""));
		
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
	
	private void ajaxUpdate_memory() {
		double cpuvalue = 0;
		String tmp = "";
		String pingconavg = "0";
		tmp = request.getParameter("tmp");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));
		String time1 = request.getParameter("begindate");
		if (time1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		I_HostCollectData hostmanager = new HostCollectDataManager();
		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(host
					.getIpAddress(), "Ping", "ConnectUtilization", starttime1,
					totime1);
		} catch (Exception ex) {
		}
		if (ConnectUtilizationhash.get("avgpingcon") != null)
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
		if (pingconavg != null) {
			pingconavg = pingconavg.replace("%", "");
		}
		int percent1 = Double.valueOf(pingconavg).intValue();
		int percent2 = 100 - percent1;
		Map<String, String> map = new HashMap<String, String>();


		String vvalue = "";
		String pvalue = "";
		float capvalue = 0f;
		float fvvalue = 0f;
		String vused = "";
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		Vector memoryVector = (Vector) ipAllData.get("memory");
		if (memoryVector != null && memoryVector.size() > 0) {
			for (int i = 0; i < memoryVector.size(); i++) {
				Memorycollectdata memorydata = (Memorycollectdata) memoryVector
						.get(i);
				if ("VirtualMemory".equalsIgnoreCase(memorydata.getSubentity())
						&& "Utilization".equalsIgnoreCase(memorydata
								.getEntity())) {
					vvalue = Math.round(Float.parseFloat(memorydata
							.getThevalue()))
							+ "";
					fvvalue = Float.parseFloat(memorydata.getThevalue());
				} else if ("PhysicalMemory".equalsIgnoreCase(memorydata
						.getSubentity())
						&& "Utilization".equalsIgnoreCase(memorydata
								.getEntity())) {
					pvalue = Math.round(Float.parseFloat(memorydata
							.getThevalue()))
							+ "";
				}
				if ("VirtualMemory".equalsIgnoreCase(memorydata.getSubentity())
						&& "Capability"
								.equalsIgnoreCase(memorydata.getEntity())) {
					capvalue = Float.parseFloat(memorydata.getThevalue());
				}
			}
			DecimalFormat df = new DecimalFormat("#.##");
			vused = df.format(capvalue * fvvalue / 100) + "";
		}
		map.put("vvalue", vvalue);
		map.put("pvalue",pvalue);
		map.put("vused", vused);

		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
	
	private void ajaxUpdate_cpuUsage()
	{
		double cpuvalue = 0;
		String tmp = "";
		tmp = request.getParameter("tmp");
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData != null){
			Vector cpuV = (Vector)ipAllData.get("cpu");
			if(cpuV != null && cpuV.size()>0){
				
				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
		}
		int cpuper = Double.valueOf(cpuvalue).intValue();
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("cpuper", cpuper);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
	
private void ajaxMemory_fresh() {
		//SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&");
		try{
		double cpuvalue = 0;
		String tmp = "";
		String pingconavg = "0";
		tmp = request.getParameter("tmp");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));
//		String time1 = request.getParameter("begindate");
//		if (time1 == null) {
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			time1 = sdf.format(new Date());
//		}
//		String starttime1 = time1 + " 00:00:00";
//		String totime1 = time1 + " 23:59:59";
		//I_HostCollectData hostmanager = new HostCollectDataManager();

		Map<String, String> map = new HashMap<String, String>();
		
		String vvalue = "";
		String pvalue = "";
		float capvalue = 0f;
		float fvvalue = 0f;
		String vused = "";
		
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();

		List gatherlist = new ArrayList();
		try {
			NodeDTO nodeDTO = null;
			NodeUtil nodeutil = new NodeUtil();
			nodeDTO = nodeutil.creatNodeDTOByNode(host);
			// SysLogger.info(nodeDTO.getSubtype()+"================"+nodeDTO.getType());
			if ("windows".equalsIgnoreCase(nodeDTO.getSubtype())) {
				// windows服务器重新进行内存采集更新
				gatherlist = indicatorsdao.findByNodeIdAndTypeAndSubtype(host
						.getId()
						+ "", nodeDTO.getType(), nodeDTO.getSubtype());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsdao.close();
		}

		// Host node =
		// (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicatorsNode.getNodeid()));
		// if(node != null){
		if (gatherlist != null && gatherlist.size() > 0) {
			for (int i = 0; i < gatherlist.size(); i++) {
				NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) gatherlist
						.get(i);
				// SysLogger.info(nodeGatherIndicators.getName()+"================");
				if ("virtualmemory".equalsIgnoreCase(nodeGatherIndicators
						.getName())) {
					// 进行虚拟内存的采集
					WindowsVirtualMemorySnmp windowsvirtualsnmp = null;
					try {
						windowsvirtualsnmp = (WindowsVirtualMemorySnmp) Class
								.forName(
										"com.afunms.polling.snmp.memory.WindowsVirtualMemorySnmp")
								.newInstance();
						Hashtable returnHash = windowsvirtualsnmp
								.collect_Data(nodeGatherIndicators);
						HostCollectDataManager hostdataManager = new HostCollectDataManager();
						hostdataManager.createHostItemData(host.getIpAddress(),
								returnHash, "host", "windows", "virtualmemory");

						Vector memoryVector = (Vector) returnHash.get("memory");
						if (memoryVector != null && memoryVector.size() > 0) {
							for (int ii = 0; ii< memoryVector.size(); ii++) {
								Memorycollectdata memorydata = (Memorycollectdata) memoryVector
										.get(ii);
								if ("VirtualMemory".equalsIgnoreCase(memorydata.getSubentity())
										&& "Utilization".equalsIgnoreCase(memorydata
												.getEntity())) {
									vvalue = Math.round(Float.parseFloat(memorydata
											.getThevalue()))
											+ "";
									fvvalue = Float.parseFloat(memorydata.getThevalue());
								} else if ("PhysicalMemory".equalsIgnoreCase(memorydata
										.getSubentity())
										&& "Utilization".equalsIgnoreCase(memorydata
												.getEntity())) {
									pvalue = Math.round(Float.parseFloat(memorydata
											.getThevalue()))
											+ "";
								}
								if ("VirtualMemory".equalsIgnoreCase(memorydata.getSubentity())
										&& "Capability"
												.equalsIgnoreCase(memorydata.getEntity())) {
									capvalue = Float.parseFloat(memorydata.getThevalue());
								}
							}
							DecimalFormat df = new DecimalFormat("#.##");
							vused = df.format(capvalue * fvvalue / 100) + "";
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("physicalmemory"
						.equalsIgnoreCase(nodeGatherIndicators.getName())) {
					// 进行物理内存的采集
					WindowsPhysicalMemorySnmp windowsphysicalsnmp = null;
					try {
						windowsphysicalsnmp = (WindowsPhysicalMemorySnmp) Class
								.forName(
										"com.afunms.polling.snmp.memory.WindowsPhysicalMemorySnmp")
								.newInstance();
						Hashtable returnHash = windowsphysicalsnmp
								.collect_Data(nodeGatherIndicators);
						HostCollectDataManager hostdataManager = new HostCollectDataManager();
						hostdataManager
								.createHostItemData(host.getIpAddress(),
										returnHash, "host", "windows",
										"physicalmemory");

						Vector memoryVector = (Vector) returnHash.get("memory");
						if (memoryVector != null && memoryVector.size() > 0) {
							for (int si = 0; si < memoryVector.size(); si++) {
								Memorycollectdata memorydata = (Memorycollectdata) memoryVector
										.elementAt(si);
								if (!memorydata.getSubentity()
										.equalsIgnoreCase("PhysicalMemory"))
									continue;
								if (memorydata.getRestype().equals("dynamic")) {
									pvalue = memorydata.getThevalue();
									SysLogger.info("pvalue============"
											+ pvalue);
								}
							}

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		map.put("vvalue", vvalue);
		map.put("pvalue", pvalue);
		map.put("vused", vused);

		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	/**
	 * 获取 list
	 * @param <code>DBVo</code>
	 * @return
	 */
	public List getList(String[] bids){
		List list = new ArrayList();
		StringBuffer sql2 = new StringBuffer();
		StringBuffer s2 = new StringBuffer();
		int flag = 0;
		if (bids != null && bids.length > 0) {
			for (int i = 0; i < bids.length; i++) {
				if (bids[i].trim().length() > 0) {
					if (flag == 0) {
						//s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
						s2.append(" and ( bid like '%" + bids[i].trim() + "%' ");
						flag = 1;
					} else {
						//flag = 1;
						//s.append(" or bid like '%," + bids[i].trim() + ",%' ");
						s2.append(" or bid like '%" + bids[i].trim() + "%' ");
					}
				}
			}
			s2.append(") ");
		}
		sql2.append("select * from app_db_node where 1=1 " + s2.toString());
		
		DBDao dao = new DBDao();
		try {
			list = dao.findByCriteria(sql2.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		
		return list;
	}
	private void synchronization() {
		String id = request.getParameter("id");
		ApacheDataCollector appache=new ApacheDataCollector();
		appache.collectData(id);
		Map map=new HashMap();
		map.put("data", "");

		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
}
