package com.afunms.application.ajaxManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sf.json.JSONObject;

import org.jfree.data.general.DefaultPieDataset;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.util.IpTranslation;
import com.afunms.application.util.ReportHelper;
import com.afunms.capreport.dao.UtilReportDao;
import com.afunms.capreport.model.ReportValue;
import com.afunms.capreport.model.StatisNumer;
import com.afunms.capreport.model.UtilReport;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.report.jfree.ChartCreator;

public class OracleAjaxManager extends AjaxBaseManager implements
		AjaxManagerInterface {

	public void execute(String action) {
		if (action.equals("ajaxUpdate_availability")) {
			ajaxUpdate_availability();
		}
		if (action.equals("dbReport")) {
			dbReport();
		}
	}

	private void dbReport() {

		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		I_HostCollectData hostmanager = new HostCollectDataManager();
		Date d = new Date();
		String startdate = getParaValue("startdate");
		
		String todate = getParaValue("todate");
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
		if (startdate == null) {
			startdate = sdf0.format(new Date()) + " 00:00:00";
		} else {
			startdate = startdate + begin;
		}
		if (todate == null) {
			todate = sdf0.format(new Date()) + " 23:59:59";
		} else {
			todate = todate + end;
		}
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
		String[] idValue = null;
		if (ids != null && !ids.equals("null") && !ids.equals("")) {
			idValue = new String[ids.split(",").length];
			idValue = ids.split(",");
		}
		ReportHelper helper =new ReportHelper();
    	HashMap valueMap=helper.getDbValue(ids, startdate, todate);
    	List<StatisNumer> gridList=new ArrayList<StatisNumer>();
    	 gridList=(List<StatisNumer>)valueMap.get("gridVlue");
    	 List<StatisNumer> valList=new ArrayList<StatisNumer>();
    	 valList=(List<StatisNumer>)valueMap.get("val");
    	 
		Hashtable pinghash = new Hashtable();
		Hashtable curhash = new Hashtable();
		List<List> pingList = new ArrayList<List>();
		List<String> pingipList = new ArrayList<String>();
		List<List> tableList = new ArrayList<List>();
		List<String> tableipList = new ArrayList<String>();
		StringBuffer pingHtml = new StringBuffer();
		boolean valFlag=true;
		boolean tableFlag=true;
		String tabledata="";
		StringBuffer val=new StringBuffer();
		StringBuffer tableHtml=new StringBuffer();
		if (idValue != null && idValue.length > 0) {
			 String ip="";
			  String cur="0";
			  String max="0";
			  String min="0";
			  String avg="0";
			  String sname="0";
			 if (gridList!=null&&gridList.size()>0) {
	    		    pingHtml.append("<table  border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=100 height=21>IP</td><td  class='body-data-title' height=21>当前连通率</td><td align='center' class='body-data-title'  height=21>最小连通率</td><td align='center' class='body-data-title'  height=21>平均连通率</td></tr>");
					
			 
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
				}else if (type.equals("gridTableSpace")) {
					
				}
			   }
	    	 
	    	    pingHtml.append("</table>");
				
	    	 }
			 if (valList!=null&&valList.size()>0) {
				 val.append("<table  border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=100 height=21>IP</td><td  class='body-data-title' height=21>名称</td><td align='center' class='body-data-title'  height=21>性能指标</td><td align='center' class='body-data-title'  height=21></td></tr>");
					
				 for (int j = 0; j < valList.size(); j++) {
		    		 StatisNumer number=new StatisNumer();
		    		 number=(StatisNumer)valList.get(j);
		    		String  type=number.getType();
		    		 ip=number.getIp();
		    		 cur=number.getCurrent();
		    		
		    		 sname=number.getName();
		    		  if (type.equals("valInfo")) {
						setInnerHtml(val, ip, sname, cur, null);
					}
				   }
				 val.append("</table>");
			}
			 ////////////////amcharts////////////////////
			StringBuffer pingsb = new StringBuffer();
			ReportValue pingValue=new ReportValue();
	    	pingValue=(ReportValue)valueMap.get("ping");
	    	
	    	pingipList=pingValue.getIpList();
	    	pingList=pingValue.getListValue();
	    	
	    	ReportValue tableValue=new ReportValue();
	    	tableValue=(ReportValue)valueMap.get("tablespace");
	    	
	    	tableipList=tableValue.getIpList();
	    	tableList=tableValue.getListValue();
			if (pingList != null && pingList.size() > 0) {

				pingsb.append("<chart><series>");

				List dataList = (List) pingList.get(0);
				for (int k = 0; k < dataList.size(); k++) {
					Vector v = new Vector();
					v = (Vector) dataList.get(k);
					pingsb.append("<value xid='");
					pingsb.append(k);
					pingsb.append("'>");
					pingsb.append(v.get(1));
					pingsb.append("</value>");

				}

				pingsb.append("</series><graphs>");
				for (int j = 0; j < pingList.size(); j++) {
					
					List dataList1 = (List) pingList.get(j);
					pingsb.append("<graph title='" + (String) pingipList.get(j)
							+ "' bullet='round_outlined' bullet_size='4'>");
					
					for (int m = 0; m < dataList1.size(); m++) {
						Vector v = new Vector();
						v = (Vector) dataList1.get(m);
						pingsb.append("<value xid='");
						pingsb.append(m);
						pingsb.append("'>");
						pingsb.append(v.get(0));
						pingsb.append("</value>");
						
					}
					pingsb.append("</graph>");
				}

				pingsb.append("</graphs></chart>");
				
				pingdata = pingsb.toString();
				
			} else {
				pingdata = "0";
			}
			
			 StringBuffer tablesb=new StringBuffer(); 
			 
			if(tableList!=null&&tableList.size()>0){
				String temp="";
				tablesb.append("<chart><series>");
				
				   List tabledataList=(List)tableList.get(0);
				   List list3=(List)tabledataList.get(0);
				  
					   for(int i=0;i<list3.size();i++)
					   {
					   Vector v = new Vector();
					 
					   v=(Vector)list3.get(i);
					  
					   tablesb.append("<value xid='");
					   tablesb.append(i);
					   tablesb.append("'>");
					   tablesb.append(v.get(2));
					   tablesb.append("</value>");
						
					   }
					   tablesb.append("</series><graphs>");
					 
					  
					   for (int j = 0; j< tableList.size(); j++) {
						   
						   List tabledataList1=(List)tableList.get(j);
						    for(int i=0;i<tabledataList1.size();i++)
					   {
						    	tablesb.append("<graph title='"+(String)tableipList.get(i)+"' bullet='round_outlined' bullet_size='4'>");
						   List list2=(List) tabledataList.get(i);
						   for (int k = 0; k < list2.size(); k++) {
							   Vector v = new Vector();
							   v=(Vector)list2.get(k);
							   
							   tablesb.append("<value xid='");
							   tablesb.append(k);
							   tablesb.append("'>");
							   tablesb.append(v.get(0));
							   tablesb.append("</value>");
						}
					 
						   tablesb.append("</graph>");
					  }
					  
					  
					   }
					  
					   tablesb.append("</graphs></chart>");
					   tabledata=tablesb.toString();
					
				}else {
					tabledata="0";	
				}
		}
		
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("pingdata", pingdata);
		map.put("pingHtml", pingHtml.toString());
		map.put("valHtml", val.toString());
		map.put("tabledata", tabledata);
		map.put("tableHtml", tableHtml.toString());
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
	 * 以表格的形式显示当前、平均、最大的利用率
	 * @param html
	 * @param ip
	 * @param cur
	 * @param min
	 * @param avg
	 */
	private void setInnerHtml(StringBuffer valHtml,String ip,String name,String value,String unit) {
		valHtml.append("<tr bgcolor='#FFFFFF'><td align='center'>"
				+ ip + "</td>");
		valHtml.append("<td align='center' height=21>"+name+"</td>");
		valHtml.append("<td align='center' height=21>"
		+ value + "</td>");
		valHtml.append("<td align='center' height=21></td></tr>");
	}
private StringBuffer getVal(String ip,String id) {
	   
	DBVo vo = new DBVo();
	
	
	int doneFlag = 0;
	Vector val = new Vector();
	
	try{
		DBDao dao = new DBDao();
		try{
			vo = (DBVo)dao.findByID(id);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(vo.getIpAddress());
		String serverip = hex+":"+vo.getId();
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(dbDao != null){
				dbDao.close();
			}
		}
		if(ipData != null && ipData.size()>0){
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for(int k=0;k<dbs.length;k++){
				//判断是否已经获取了当前的配置信息
				//if(doneFlag == 1)break;
				String dbStr = dbs[k];
				if(ipData.containsKey(dbStr)){
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable)ipData.get(dbStr);
					if(returnValue != null && returnValue.size()>0){
						if(doneFlag == 0){
							
							if(returnValue.containsKey("Val")){
								val = (Vector)returnValue.get("Val");
							}
						}
					}
				}
			}
		}
//		}
	
		
	}catch(Exception e){
		e.printStackTrace();
	}
	StringBuffer valHtml=new StringBuffer();
	if(val != null && val.size()>0){
		
		for(int ii=0;ii<val.size();ii++){
			Hashtable return_value = (Hashtable)val.get(ii);
			if(return_value != null && return_value.size()>0){
				String name=return_value.get("variable_name").toString();
				String value=return_value.get("value").toString();
				if(name.equalsIgnoreCase("Max_used_connections"))
		{
		 name="服务器相应的最大连接数";
		}
		if(name.equalsIgnoreCase("Handler_read_first"))
		{
		 name="索引中第一条被读的次数";
		}
		if(name.equalsIgnoreCase("Handler_read_key"))
		{
		 name="根据键读一行的请求数";
		}
		if(name.equalsIgnoreCase("Handler_read_next"))
		{
		 name="按照键顺序读下一行的请求数";
		}
		if(name.equalsIgnoreCase("Handler_read_prev"))
		{
		 name="按照键顺序读前一行的请求数";
		}
		if(name.equalsIgnoreCase("Handler_read_rnd"))
		{
		 name="H根据固定位置读一行的请求数";
		}
		if(name.equalsIgnoreCase("Handler_read_rnd_next"))
		{
		 name="在数据文件中读下一行的请求数";
		}
		if(name.equalsIgnoreCase("Open_tables"))
		{
		 name="当前打开的表的数量";
		}
		if(name.equalsIgnoreCase("Opened_tables"))
		{
		 name="已经打开的表的数量";
		}
		if(name.equalsIgnoreCase("Threads_cached"))
		{
		 name="线程缓存内的线程的数量";
		}
		if(name.equalsIgnoreCase("Threads_connected"))
		{
		 name="当前打开的连接的数量";
		}
		if(name.equalsIgnoreCase("Threads_created"))
		{
		 name="创建用来处理连接的线程数";
		}
		if(name.equalsIgnoreCase("Threads_running"))
		{
		 name="激活的非睡眠状态的线程数";
		}
		if(name.equalsIgnoreCase("Table_locks_immediate"))
		{
		 name="立即获得的表的锁的次数";
		}
		if(name.equalsIgnoreCase("Table_locks_waited"))
		{
		 name="不能立即获得的表的锁的次数";
		}
		if(name.equalsIgnoreCase("Key_read_requests"))
		{
		 name="从缓存读键的数据块的请求数";
		}
		if(name.equalsIgnoreCase("Key_reads"))
		{
		 name="从硬盘读取键的数据块的次数";
		}
		if(name.equalsIgnoreCase("log_slow_queries"))
		{
		 name="是否记录慢查询";
		}
		if(name.equalsIgnoreCase("slow_launch_time"))
		{
		 name="创建线程的时间超过该秒数，服务器增加Slow_launch_threads状态变量";
		}
		valHtml.append("<tr bgcolor='#FFFFFF'><td align='center'>"
				+ ip + "</td>");
		valHtml.append("<td align='center' height=21>"+name+"</td>");
		valHtml.append("<td align='center' height=21>"
		+ value + "</td>");
		valHtml.append("<td align='center' height=21></td></tr>");
         }
		}
	
	}
	return valHtml;

}
	private void ajaxUpdate_availability() {
		String buffercache = null;
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		String id = getParaValue("id");
		try {
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {

		} finally {
			dao.close();
		}
		String sid = getParaValue("sid");
		String pingconavg = "0";
		double avgpingcon = 0;
		Hashtable ConnectUtilizationhash = new Hashtable();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		I_HostCollectData hostmanager = new HostCollectDataManager();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(id, "ORAPing", "ConnectUtilization", starttime1,
					totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (ConnectUtilizationhash.get("avgpingcon") != null)
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
		if (pingconavg != null) {
			pingconavg = pingconavg.replace("%", "");
		}
		avgpingcon = new Double(pingconavg + "").doubleValue();
		int percent1 = Double.valueOf(avgpingcon).intValue();
		int percent2 = 100 - percent1;
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("percent1", percent1);
		map.put("percent2", percent2);
		// System.out.println("percent1:"+percent1);
		// System.out.println("percent2:"+percent2);

		// Hashtable alloracledata = ShareData.getAlloracledata();
		// Hashtable iporacledata = new Hashtable();
		// Hashtable memPerfValue = new Hashtable();
		// int intcpuper = 0;
		// int intbuffercache = 0;
		// if(alloracledata != null && alloracledata.size()>0)
		// {
		// if(alloracledata.containsKey(vo.getIpAddress()+":"+sid)){
		// iporacledata =
		// (Hashtable)alloracledata.get(vo.getIpAddress()+":"+sid);
		// }
		// if(iporacledata.containsKey("memPerfValue")){
		// memPerfValue=(Hashtable)iporacledata.get("memPerfValue");
		// buffercache = (String)memPerfValue.get("buffercache");
		// intbuffercache = Integer.parseInt(buffercache);
		// }
		//			
		// }
		// 2010-HONGLI
		Hashtable memPerfValue = new Hashtable();
		dao = new DBDao();
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(vo.getIpAddress());
		String serverip = hex + ":" + sid;
		try {
			memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		int intbuffercache = 0;
		int intcpuper = 0;
		if (memPerfValue.containsKey("buffercache")) {
			buffercache = (String) memPerfValue.get("buffercache");
			intbuffercache = Integer.parseInt(buffercache);
		}
		if (memPerfValue.containsKey("buffercache")
				&& memPerfValue.get("buffercache") != null) {
			buffercache = (String) memPerfValue.get("buffercache");
		}
		String dictionarycache = "0";
		int intdictionarycache = 0;
		if (memPerfValue.containsKey("dictionarycache")
				&& memPerfValue.get("dictionarycache") != null) {
			dictionarycache = (String) memPerfValue.get("dictionarycache");
			intdictionarycache = (int) (Float.parseFloat(buffercache));
		}
		String librarycache = "0";
		int intlibrarycache = 0;
		if (memPerfValue.containsKey("librarycache")
				&& memPerfValue.get("librarycache") != null) {
			librarycache = (String) memPerfValue.get("librarycache");
			intlibrarycache = (int) (Float.parseFloat(librarycache));
		}
		String pctmemorysorts = "0";
		int intpctmemorysorts = 0;
		if (memPerfValue.containsKey("pctmemorysorts")
				&& memPerfValue.get("pctmemorysorts") != null) {
			pctmemorysorts = (String) memPerfValue.get("pctmemorysorts");
			intpctmemorysorts = (int) (Float.parseFloat(pctmemorysorts));
		}
		String pctbufgets = "0";
		int intpctbufgets = 0;
		if (memPerfValue.containsKey("pctbufgets")
				&& memPerfValue.get("pctbufgets") != null) {
			pctbufgets = (String) memPerfValue.get("pctbufgets");
			intpctbufgets = (int) (Float.parseFloat(pctbufgets));
		}
		map.put("pctbufgets", intpctbufgets);
		// System.out.println("pctbufgets:"+intpctbufgets);
		map.put("pctmemorysorts", intpctmemorysorts);
		// System.out.println("pctmemorysorts:"+intpctmemorysorts);
		map.put("librarycache", intlibrarycache);
		map.put("dictionarycache", intdictionarycache);
		// System.out.println("dictionarycache:"+intdictionarycache);
		map.put("buffercache", intbuffercache);
		// System.out.println("buffercache:"+intbuffercache);
		map.put("cpuper", intbuffercache);
		// System.out.println("intcpuper:"+intcpuper);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

}
