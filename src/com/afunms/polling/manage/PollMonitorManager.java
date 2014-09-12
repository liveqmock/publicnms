/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.polling.manage;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYDataset;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;
import org.jrobin.core.Sample;
import org.jrobin.core.Util;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.Arith;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.CreateBarPic;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpService;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.common.util.TitleModel;
import com.afunms.config.dao.AclBaseDao;
import com.afunms.config.dao.AclDetailDao;
import com.afunms.config.dao.CfgBaseInfoDao;
import com.afunms.config.dao.ErrptlogDao;
import com.afunms.config.dao.GatherTelnetConfigDao;
import com.afunms.config.dao.NodeconfigDao;
import com.afunms.config.dao.PolicyInterfaceDao;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.dao.QueueInfoDao;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.AclBase;
import com.afunms.config.model.Errptlog;
import com.afunms.config.model.GatherTelnetConfig;
import com.afunms.config.model.Nodeconfig;
import com.afunms.config.model.Procs;
import com.afunms.config.model.Supper;
import com.afunms.detail.service.OtherInfo.OtherInfoService;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.detail.service.systemInfo.SystemInfoService;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.EventReportDao;
import com.afunms.event.dao.SyslogDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.EventReport;
import com.afunms.event.model.Syslog;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostCollectDataDay;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataDayManager;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Devicecollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.ProcessInfo;
import com.afunms.polling.om.Softwarecollectdata;
import com.afunms.polling.om.Storagecollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.snmp.WindowsSnmp;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.report.jfree.ChartCreator;
import com.afunms.report.jfree.JFreeChartBrother;
import com.afunms.system.model.User;
import com.afunms.topology.dao.DiskForAS400Dao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.JobForAS400Dao;
import com.afunms.topology.dao.SystemPoolForAS400Dao;
import com.afunms.topology.dao.SystemValueForAS400Dao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.SubsystemForAS400;
import com.afunms.topology.model.SystemValueForAS400;
import com.afunms.topology.util.TopoHelper;
import com.afunms.topology.util.XmlOperator;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.rtf.RtfWriter2;

/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class PollMonitorManager extends BaseManager implements ManagerInterface
{
	
	private static Hashtable device_Status = null;
	static {
		device_Status = new Hashtable();
		device_Status.put("1", "未知");
		device_Status.put("2", "运行");
		device_Status.put("3", "告警");
		device_Status.put("4", "测试");
		device_Status.put("5", "停止");
	};
	private static Hashtable device_Type = null;
	static {
		device_Type = new Hashtable();
		device_Type.put("1.3.6.1.2.1.25.3.1.1", "其他");
		device_Type.put("1.3.6.1.2.1.25.3.1.2", "未知");
		device_Type.put("1.3.6.1.2.1.25.3.1.3", "CPU");
		device_Type.put("1.3.6.1.2.1.25.3.1.4", "网络");
		device_Type.put("1.3.6.1.2.1.25.3.1.5", "打印机");
		device_Type.put("1.3.6.1.2.1.25.3.1.6", "磁盘");
		device_Type.put("1.3.6.1.2.1.25.3.1.10", "显卡");
		device_Type.put("1.3.6.1.2.1.25.3.1.11", "声卡");
		device_Type.put("1.3.6.1.2.1.25.3.1.12", "协处理器");
		device_Type.put("1.3.6.1.2.1.25.3.1.13", "键盘");
		device_Type.put("1.3.6.1.2.1.25.3.1.14", "调制解调器");
		device_Type.put("1.3.6.1.2.1.25.3.1.15", "并口");
		device_Type.put("1.3.6.1.2.1.25.3.1.16", "打印口");
		device_Type.put("1.3.6.1.2.1.25.3.1.17", "串口");
		device_Type.put("1.3.6.1.2.1.25.3.1.18", "磁带");
		device_Type.put("1.3.6.1.2.1.25.3.1.19", "时钟");
		device_Type.put("1.3.6.1.2.1.25.3.1.20", "动态内存");
		device_Type.put("1.3.6.1.2.1.25.3.1.21", "固定内存");
	};
	
	private static Hashtable storage_Type = null;
	static {
		storage_Type = new Hashtable();
		storage_Type.put("1.3.6.1.2.1.25.2.1.1", "其他");
		storage_Type.put("1.3.6.1.2.1.25.2.1.2", "物理内存");
		storage_Type.put("1.3.6.1.2.1.25.2.1.3", "虚拟内存");
		storage_Type.put("1.3.6.1.2.1.25.2.1.4", "硬盘");
		storage_Type.put("1.3.6.1.2.1.25.2.1.5", "移动硬盘");
		storage_Type.put("1.3.6.1.2.1.25.2.1.6", "软盘");
		storage_Type.put("1.3.6.1.2.1.25.2.1.7", "光盘");
		storage_Type.put("1.3.6.1.2.1.25.2.1.8", "内存盘");
	};
	DateE datemanager = new DateE();
	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
	I_HostCollectData hostmanager=new HostCollectDataManager();
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String list()
	{
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list",dao.loadNetwork(0));	     
	    return "/topology/network/list.jsp";
	}
	
	private String speed(){
		String id = request.getParameter("id");
		String flag = request.getParameter("flag");
		request.setAttribute("id", id);
		request.setAttribute("flag", flag);
		return "/topology/network/netspeed.jsp";
	}
	
    private String netif()
    {
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
    	String tmp = request.getParameter("id");
    	HostNodeDao hostdao = new HostNodeDao();
    	List hostlist = hostdao.loadHost();
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp)); 
    	String orderflag = getParaValue("orderflag");
    	if(orderflag == null || orderflag.trim().length()==0)
    		orderflag="index";
        String[] time = {"",""};
        getTime(request,time);
        String starttime = time[0];
        String endtime = time[1];
        
        I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
        
        Vector vector = new Vector();
        String[] netInterfaceItem={"index","ifDescr","ifSpeed","ifOperStatus","OutBandwidthUtilHdx","InBandwidthUtilHdx"};
        try{
        	vector = hostlastmanager.getInterface_share(host.getIpAddress(),netInterfaceItem,orderflag,starttime,endtime); 
        }catch(Exception e){
        	e.printStackTrace();
        }
        
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData != null){
			Vector cpuV = (Vector)ipAllData.get("cpu");
			if(cpuV != null && cpuV.size()>0){
				
				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			//得到系统启动时间
			Vector systemV = (Vector)ipAllData.get("system");
			if(systemV != null && systemV.size()>0){
				for(int i=0;i<systemV.size();i++){
					Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
					if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
						sysuptime = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
						sysservices = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());
								
	
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		
		Hashtable ConnectUtilizationhash = new Hashtable();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if (ConnectUtilizationhash.get("avgpingcon")!=null){
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}
		
		
		Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
		if(pingData != null && pingData.size()>0){
			Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
			Calendar tempCal = (Calendar)pingdata.getCollecttime();							
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}
		
        request.setAttribute("vector", vector);
        request.setAttribute("id", tmp);
        request.setAttribute("ipaddress", host.getIpAddress());
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
	    DaoInterface dao = new HostNodeDao();
	    //setTarget("/topology/network/read.jsp");
	    return "/detail/net_if.jsp";
    }
    
    
    private String netdetail()
    {
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
    	String tmp = request.getParameter("id");
    	HostNodeDao hostdao = new HostNodeDao();
    	List hostlist = hostdao.loadHost();
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp)); 
    	String orderflag = getParaValue("orderflag");
    	if(orderflag == null || orderflag.trim().length()==0)
    		orderflag="index";
        String[] time = {"",""};
        getTime(request,time);
        String starttime = time[0];
        String endtime = time[1];
        
        I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
        
        Vector vector = new Vector();
        String[] netInterfaceItem={"index","ifDescr","ifSpeed","ifOperStatus","OutBandwidthUtilHdx","InBandwidthUtilHdx"};
        try{
        	vector = hostlastmanager.getInterface_share(host.getIpAddress(),netInterfaceItem,orderflag,starttime,endtime); 
        }catch(Exception e){
        	e.printStackTrace();
        }
        
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData != null){
			Vector cpuV = (Vector)ipAllData.get("cpu");
			if(cpuV != null && cpuV.size()>0){
				
				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			//得到系统启动时间
			Vector systemV = (Vector)ipAllData.get("system");
			if(systemV != null && systemV.size()>0){
				for(int i=0;i<systemV.size();i++){
					Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
					if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
						sysuptime = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
						sysservices = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());
								
	
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		
		Hashtable ConnectUtilizationhash = new Hashtable();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if (ConnectUtilizationhash.get("avgpingcon")!=null){
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}
		
		
		Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
		if(pingData != null && pingData.size()>0){
			Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
			Calendar tempCal = (Calendar)pingdata.getCollecttime();							
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}
		
        request.setAttribute("vector", vector);
        request.setAttribute("id", tmp);
        request.setAttribute("ipaddress", host.getIpAddress());
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
	    DaoInterface dao = new HostNodeDao();
	    return "/detail/net_infodetail.jsp";
    }
    
    private String netinterface()
    {
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
    	String tmp = request.getParameter("id");
    	HostNodeDao hostdao = new HostNodeDao();
    	List hostlist = hostdao.loadHost();
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp)); 
    	String orderflag = getParaValue("orderflag");
    	if(orderflag == null || orderflag.trim().length()==0)
    		orderflag="index";
        String[] time = {"",""};
        getTime(request,time);
        String starttime = time[0];
        String endtime = time[1];
        
        I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
        
        Vector vector = new Vector();
        String[] netInterfaceItem={"index","ifDescr","ifSpeed","ifOperStatus","OutBandwidthUtilHdx","InBandwidthUtilHdx"};
        try{
        	vector = hostlastmanager.getInterface_share(host.getIpAddress(),netInterfaceItem,orderflag,starttime,endtime); 
        }catch(Exception e){
        	e.printStackTrace();
        }
        
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData != null){
			Vector cpuV = (Vector)ipAllData.get("cpu");
			if(cpuV != null && cpuV.size()>0){
				
				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			//得到系统启动时间
			Vector systemV = (Vector)ipAllData.get("system");
			if(systemV != null && systemV.size()>0){
				for(int i=0;i<systemV.size();i++){
					Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
					if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
						sysuptime = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
						sysservices = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());
								
	
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		
		Hashtable ConnectUtilizationhash = new Hashtable();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if (ConnectUtilizationhash.get("avgpingcon")!=null){
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}
		
		
		Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
		if(pingData != null && pingData.size()>0){
			Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
			Calendar tempCal = (Calendar)pingdata.getCollecttime();							
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}
		
        request.setAttribute("vector", vector);
        request.setAttribute("id", tmp);
        request.setAttribute("ipaddress", host.getIpAddress());
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		
	    DaoInterface dao = new HostNodeDao();
	    setTarget("/topology/network/read.jsp");
        //return readyEdit(dao);
	    return "/detail/net_interface.jsp";
    }
    
    
    
    private String netenv()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
    	String tmp = request.getParameter("id");
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	
		String newip=doip(host.getIpAddress());
		
		/*
		CategoryDataset dataset = getDataSet(); 
        JFreeChart chart = ChartFactory.createBarChart3D("测试条形图", "横轴显示标签", "竖轴显示标签", dataset, PlotOrientation.VERTICAL, true, false, false); 
        FileOutputStream jpg = null; 
        try { 
        	jpg = new FileOutputStream("D:\\test.jpg"); 
        	try { 
        		ChartUtilities.writeChartAsJPEG(jpg,1.0f,chart,400,300,null); 
            } catch (IOException e) { 
            	e.printStackTrace(); 
            } 
        } catch (FileNotFoundException e) { 
        	e.printStackTrace(); 
        }
        */
		
		
		
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
	    return "/detail/net_env.jsp";
    }
    
//    private String hostcpu()
//    {
//		Hashtable imgurlhash=new Hashtable();
//		Hashtable hash = new Hashtable();//"Cpu"--current
//		Hashtable maxhash = new Hashtable();//"Cpu"--max
//		Hashtable memhash = new Hashtable();//mem--current
//		Hashtable diskhash = new Hashtable();
//		Hashtable memmaxhash = new Hashtable();//mem--max
//		Hashtable memavghash = new Hashtable();
//		
//		double cpuvalue = 0;
//		String pingconavg ="0";
//		String collecttime = null;
//		String sysuptime = null;
//		String sysservices = null;
//		String sysdescr = null;
//		
//    	String tmp = request.getParameter("id");
//    	HostNodeDao hostdao = new HostNodeDao();
//    	List hostlist = hostdao.loadHost();
//    	
//    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
//    	
//		String newip=doip(host.getIpAddress());
//		String[] time = {"",""};
//		getTime(request,time);
//		String starttime = time[0];
//		String endtime = time[1];	
//		String time1 = request.getParameter("begindate");
//		if(time1 == null){
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			time1 = sdf.format(new Date());
//		}							
//		
//		String starttime1 = time1 + " 00:00:00";
//		String totime1 = time1 + " 23:59:59";						
//		String[] item = {"CPU"};
//		
//		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
//		try{
//			//hash = hostlastmanager.getbyCategories(host.getIpAddress(),item,starttime,endtime);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		try{
//			memhash = hostlastmanager.getMemory_share(host.getIpAddress(),"Memory",starttime,endtime);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		//memhash = hostlastmanager.getMemory(ip,"Memory",starttime,endtime);
//		try{
//			diskhash = hostlastmanager.getDisk_share(host.getIpAddress(),"Disk",starttime,endtime);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		//从collectdata取cpu,内存的历史数据
//		Hashtable cpuhash = new Hashtable();
//		try{
//			cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		Hashtable[] memoryhash =null;
//		try{
//			memoryhash = hostmanager.getMemory(host.getIpAddress(),"Memory",starttime1,totime1);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
////各memory最大值								
//		memmaxhash = memoryhash[1];
//		memavghash = memoryhash[2];
//		//String pingconavg ="";
//		//cpu最大值
//		maxhash = new Hashtable();
//		String cpumax="";
//		if(cpuhash.get("max")!=null){
//				cpumax = (String)cpuhash.get("max");
//		}
//		maxhash.put("cpu",cpumax);
//		//cpu平均值
//		//maxhash = new Hashtable();
//		String cpuavg="";
//		if(cpuhash.get("avgcpucon")!=null){
//				cpuavg = (String)cpuhash.get("avgcpucon");
//		}
//		maxhash.put("cpuavg",cpuavg);
//		//画图
//		p_draw_line(cpuhash,"",newip+"cpu",740,120);
//		//imgurlhash
//		
//		//画图（磁盘是显示最新数据的柱状图）
//		//p_draw_line(cpuhash,"",newip+"cpu",750,150);
//		draw_column(diskhash,"",newip+"disk",750,150);
//		p_drawchartMultiLine(memoryhash[0],"",newip+"memory",750,150);
////imgurlhash
//		//imgurlhash.put("cpu","../images/jfreechart/"+newip+"cpu"+".png");
//		imgurlhash.put("cpu","resource\\image\\jfreechart\\"+newip+"cpu"+".png");
//		imgurlhash.put("memory","resource\\image\\jfreechart\\"+newip+"memory"+".png");
//		imgurlhash.put("disk","resource\\image\\jfreechart\\"+newip+"disk"+".png");
//		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
//		if(ipAllData != null){
//			Vector cpuV = (Vector)ipAllData.get("cpu");
//			if(cpuV != null && cpuV.size()>0){
//				
//				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
//				cpuvalue = new Double(cpu.getThevalue());
//			}
//			//得到系统启动时间
//			Vector systemV = (Vector)ipAllData.get("system");
//			if(systemV != null && systemV.size()>0){
//				for(int i=0;i<systemV.size();i++){
//					Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
//					if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
//						sysuptime = systemdata.getThevalue();
//					}
//					if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
//						sysservices = systemdata.getThevalue();
//					}
//					if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
//						sysdescr = systemdata.getThevalue();
//					}
//				}
//			}
//		}
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		//String time1 = sdf.format(new Date());
//								
//	
//		//String starttime1 = time1 + " 00:00:00";
//		//String totime1 = time1 + " 23:59:59";
//		
//		Hashtable ConnectUtilizationhash = new Hashtable();
//		try{
//			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		
//		if (ConnectUtilizationhash.get("avgpingcon")!=null){
//			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
//			pingconavg = pingconavg.replace("%", "");
//		}
//		
//		
//		Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
//		if(pingData != null && pingData.size()>0){
//			Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
//			Calendar tempCal = (Calendar)pingdata.getCollecttime();							
//			Date cc = tempCal.getTime();
//			collecttime = sdf1.format(cc);
//		}
//		
//		/*
//		CategoryDataset dataset = getDataSet(); 
//        JFreeChart chart = ChartFactory.createBarChart3D("测试条形图", "横轴显示标签", "竖轴显示标签", dataset, PlotOrientation.VERTICAL, true, false, false); 
//        FileOutputStream jpg = null; 
//        try { 
//        	jpg = new FileOutputStream("D:\\test.jpg"); 
//        	try { 
//        		ChartUtilities.writeChartAsJPEG(jpg,1.0f,chart,400,300,null); 
//            } catch (IOException e) { 
//            	e.printStackTrace(); 
//            } 
//        } catch (FileNotFoundException e) { 
//        	e.printStackTrace(); 
//        }
//        */
//		
//		
//		   request.setAttribute("memmaxhash",memmaxhash);
//		   request.setAttribute("memavghash",memavghash);
//		   request.setAttribute("diskhash",diskhash);
//		   request.setAttribute("memhash",memhash);
//		   
//		request.setAttribute("imgurl",imgurlhash);
//		request.setAttribute("hash",hash);
//		request.setAttribute("max",maxhash);
//		request.setAttribute("id", tmp);
//		request.setAttribute("cpuvalue", cpuvalue);
//		request.setAttribute("collecttime", collecttime);
//		request.setAttribute("sysuptime", sysuptime);
//		request.setAttribute("sysservices", sysservices);
//		request.setAttribute("sysdescr", sysdescr);
//		request.setAttribute("pingconavg", new Double(pingconavg));
//	    return "/detail/host_cpu.jsp";
//    }
    
    private String hostproc()
    {
    	Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		Hashtable processhash = new Hashtable();
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
    	String tmp = request.getParameter("id");
    	String flag = request.getParameter("flag");
    	HostNodeDao hostdao = new HostNodeDao();
    	List hostlist = hostdao.loadHost();
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	NodeUtil nodeUtil = new NodeUtil();
    	NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host); 
		String newip=doip(host.getIpAddress());
		String[] time = {"",""};
		getTime(request,time);
		String starttime = time[0];
		String endtime = time[1];	
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}							
		
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		String[] item = {"CPU"};
		
		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
		try{
			//hash = hostlastmanager.getbyCategories(host.getIpAddress(),item,starttime,endtime);
		}catch(Exception e){
			e.printStackTrace();
		}
		//从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try{
			cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
		}catch(Exception e){
			e.printStackTrace();
		}
		//String pingconavg ="";
		//cpu最大值
		maxhash = new Hashtable();
		String cpumax="";
		if(cpuhash.get("max")!=null){
				cpumax = (String)cpuhash.get("max");
		}
		maxhash.put("cpu",cpumax);
		//cpu平均值
		//maxhash = new Hashtable();
		String cpuavg="";
		if(cpuhash.get("avgcpucon")!=null){
				cpuavg = (String)cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg",cpuavg);

		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData != null){
			Vector cpuV = (Vector)ipAllData.get("cpu");
			if(cpuV != null && cpuV.size()>0){
				
				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			//得到系统启动时间
			Vector systemV = (Vector)ipAllData.get("system");
			if(systemV != null && systemV.size()>0){
				for(int i=0;i<systemV.size();i++){
					Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
					if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
						sysuptime = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
						sysservices = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		  //按order将进程信息排序							  
		  String order = "MemoryUtilization";
		  ((HostLastCollectDataManager)hostlastmanager).setHost(host);
		  if((request.getParameter("orderflag") != null) &&(!request.getParameter("orderflag").equals(""))){
			order = request.getParameter("orderflag");
		  }
		  if(order.equalsIgnoreCase("CpuTime"))
		  {
			  ((HostLastCollectDataManager)hostlastmanager).setCpuTime(true);
		  }
		  String runmodel = PollingEngine.getCollectwebflag();
		  try{
			  if("0".equals(runmodel)){
				  //采集与访问是集成模式
				  processhash = hostlastmanager.getProcess_share(host.getIpAddress(),"Process",order,starttime,endtime);
				  Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
				  if(pingData != null && pingData.size()>0){
					  Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
					  Calendar tempCal = (Calendar)pingdata.getCollecttime();							
					  Date cc = tempCal.getTime();
					  collecttime = sdf1.format(cc);
				  }
			  }else{
				  //采集与访问是分离模式
				  processhash = hostlastmanager.getProcess(host.getIpAddress(),"Process",order,starttime,endtime); 
				  //ProcessInfoService processInfoService = new ProcessInfoService(nodedto.getId()+"",nodedto.getType(),nodedto.getSubtype());
				  // processhash = processInfoService.getProcessInfo(order);
				  OtherInfoService otherInfoService = new OtherInfoService(nodedto.getId()+"",nodedto.getType(),nodedto.getSubtype());
				  collecttime = otherInfoService.getCollecttime();
			  }
		  }catch(Exception ex){
			  ex.printStackTrace();
		  }
		  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Hashtable ConnectUtilizationhash = new Hashtable();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if (ConnectUtilizationhash.get("avgpingcon")!=null){
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}
		
		//
		//gzm
		Hashtable phash;//原，用于计算的中间变量
		Hashtable newPHash = new Hashtable();//中间变量，用于存储自己写的代码
		Hashtable newProcessHash = new Hashtable();//存储排序后的hashtable
		//Hashtable sumProcessHash = new Hashtable();
		Hashtable detailHash = new Hashtable();
		//String[] processItem={"pid","Name","Type",7"CpuTime","MemoryUtilization","Memory","Status"};
		int num = 0;
//		Pattern p1 = Pattern.compile("(\\d+):(\\d+)秒");
		Pattern p1 = Pattern.compile("(\\d+):(\\d+)");
		if(processhash!=null){
			for(int m=0;m<processhash.size();m++)
			{
				phash=(Hashtable)processhash.get(new Integer(m));
				//newPHash.put("Name", (String)phash.get("Name"));
				if(phash!=null){
					String Name = ((String)phash.get("Name")).trim();
					if(newProcessHash.containsKey(Name))
					{
						ProcessInfo totalProcess = (ProcessInfo)newProcessHash.get(Name);
						String CpuTime = (String)phash.get("CpuTime");
						String CpuUtilization = (String)phash.get("CpuUtilization");
						String Memory = (String)phash.get("Memory");
						String MemoryUtilization=(String)phash.get("MemoryUtilization");
						String threadCount = (String)phash.get("ThreadCount");
						String handleCount = (String)phash.get("HandleCount");
						if(CpuTime!=null){
							if(CpuTime.indexOf(":")!=-1){
								Matcher matcher = p1.matcher(CpuTime);
								if(matcher.find())  
								{
									String t1 = matcher.group(1);
									String t2 = matcher.group(2);
									float sumOfCPU = Float.parseFloat(t1)*60 + Float.parseFloat(t2);
									totalProcess.setCpuTime(sumOfCPU + (Float)totalProcess.getCpuTime());
								}
							} else {
								float sumOfCPU = Float.parseFloat(CpuTime.replace("秒", ""));
								totalProcess.setCpuTime(sumOfCPU + (Float)totalProcess.getCpuTime());
							}
						}
						Float sumOfCpuUtilization = 0f;
						if(CpuUtilization != null && CpuUtilization.trim().length()>0){
							sumOfCpuUtilization = Float.parseFloat(CpuUtilization.substring(0, CpuUtilization.length()-1));
							totalProcess.setCpuUtilization(sumOfCpuUtilization + (Float)totalProcess.getCpuUtilization());
						}else{
							totalProcess.setCpuUtilization("-");
						}
						
						Float sumOfMem = Float.valueOf("0"); 
						if(Memory.trim().length() > 1){
							 sumOfMem = Float.parseFloat(Memory.substring(0, Memory.length()-1));
						}
						//Float sumOfMem = Float.parseFloat(Memory.substring(0, Memory.length()-1));
						totalProcess.setMemory(sumOfMem + (Float)totalProcess.getMemory());
						NumberFormat numberFormat = new DecimalFormat();
						numberFormat.setMaximumFractionDigits(0);
						
						Float sumOfMemUtilization = Float.valueOf("0"); 
						if(MemoryUtilization.trim().length() > 1){
							sumOfMemUtilization = Float.parseFloat(MemoryUtilization.substring(0, MemoryUtilization.length()-1));
						}
						//Float sumOfMemUtilization = Float.parseFloat(MemoryUtilization.substring(0, MemoryUtilization.length()-1));
						Float memoryUtilization = sumOfMemUtilization + (Float)totalProcess.getMemoryUtilization();
						//memoryUtilization = Float.parseFloat(numberFormat.format(memoryUtilization));

						totalProcess.setMemoryUtilization(memoryUtilization);
						
						if(threadCount!=null){
							totalProcess.setThreadCount(threadCount);
						}
						if(handleCount!=null){
							totalProcess.setHandleCount(handleCount);
						}
						ProcessInfo processInfo = new ProcessInfo();
						processInfo.setName((String)phash.get("Name"));
						processInfo.setType((String)phash.get("Type"));
						processInfo.setStatus((String)phash.get("Status"));
						if(CpuTime!=null){
							if(CpuTime.indexOf(":")!=-1){
								Matcher matcher2 = p1.matcher(CpuTime);
								if(matcher2.find())
								{
									String t1 = matcher2.group(1);
									String t2 = matcher2.group(2);
									float sumOfCPU = Float.parseFloat(t1)*60 + Float.parseFloat(t2);
									processInfo.setCpuTime(sumOfCPU);
								}
							} else {
								float sumOfCPU = Float.parseFloat(CpuTime.replace("秒", ""));
								processInfo.setCpuTime(sumOfCPU);
							}
						}
						
						processInfo.setUSER((String)phash.get("USER"));
						processInfo.setStartTime((String)phash.get("StartTime"));

						processInfo.setCpuUtilization(sumOfCpuUtilization);
						
						processInfo.setPid((String)phash.get("process_id"));
						processInfo.setMemoryUtilization(sumOfMemUtilization);
						processInfo.setMemory(sumOfMem);
						processInfo.setThreadCount(threadCount);
						processInfo.setHandleCount(handleCount);
						//processInfo.setPid((String)phash.get("process_id"));
						((Vector)detailHash.get(((String)phash.get("Name")).trim())).add(processInfo);
						
						
					}
					else
					{
						ProcessInfo processInfo = new ProcessInfo();
						//String Name = (String)phash.get("Name");
						processInfo.setName(Name);
						processInfo.setUSER((String)phash.get("USER"));
						processInfo.setType((String)phash.get("Type"));
						processInfo.setStatus((String)phash.get("Status"));
						
						String CpuTime = (String)phash.get("CpuTime");
						if(CpuTime != null){
							if(CpuTime.indexOf(":")!=-1){
								Matcher matcher = p1.matcher(CpuTime);
								if(matcher.find())
								{
									String t1 = matcher.group(1);
									String t2 = matcher.group(2);
									float sumOfCPU = Float.parseFloat(t1)*60 + Float.parseFloat(t2);
									processInfo.setCpuTime(sumOfCPU);
								}
							} else {
								float sumOfCPU = Float.parseFloat(CpuTime.replace("秒", ""));
								processInfo.setCpuTime(sumOfCPU);
							}
						}
						
						String MemoryUtilization = (String)phash.get("MemoryUtilization");
						Float sumOfMemUtilization = Float.valueOf("0"); 
						if(MemoryUtilization.trim().length() >1){
							sumOfMemUtilization = Float.parseFloat(MemoryUtilization.substring(0, MemoryUtilization.length()-1));
						}
						processInfo.setMemoryUtilization(sumOfMemUtilization);
						
						String Memory = (String)phash.get("Memory");
						Float sumOfMem = Float.valueOf("0"); 
						if(Memory.trim().length() > 1){
							 sumOfMem = Float.parseFloat(Memory.substring(0, Memory.length()-1));
						}
						processInfo.setMemory(sumOfMem);
						
						//processInfo.setUSER((String)phash.get("USER"));
						String CpuUtilization = (String)phash.get("CpuUtilization");
						if(CpuUtilization != null && CpuUtilization.trim().length()>0){
							Float sumOfCpuUtilization = Float.parseFloat(CpuUtilization.substring(0, CpuUtilization.length()-1));
							processInfo.setCpuUtilization(sumOfCpuUtilization);
						}else{
							processInfo.setCpuUtilization("-");
						}
						processInfo.setPid((String)phash.get("process_id"));
						String threadCount = (String)phash.get("ThreadCount");
						processInfo.setThreadCount(threadCount);
						String handleCount = (String)phash.get("HandleCount");
						processInfo.setHandleCount(handleCount);
						ProcessInfo newProcessInfo = processInfo.clone();
						newProcessHash.put(Name, processInfo);
						Vector detailVect = new Vector();
						detailVect.add(newProcessInfo);
						detailHash.put(Name, detailVect);
					}
				}
			}
		}
		Enumeration newProEnu = newProcessHash.keys();
		while(newProEnu.hasMoreElements())
		{
			String processName = (String)newProEnu.nextElement();
			ProcessInfo p = (ProcessInfo)newProcessHash.get(processName);
			Vector v = (Vector)detailHash.get(processName);
			p.setCount(v.size());
		}
		
		String processName = this.getParaValue("processName");
		
		Vector detailVect = null;
		String layer = this.getParaValue("layer");
		if(processName!=null)
		{
			if(detailHash.containsKey(processName))
			{
				detailVect = (Vector)detailHash.get(processName);
			}
		}
		
		List list = new ArrayList();
		String orderflag = this.getParaValue("orderflag");
		if(detailVect == null)
		{
			Collection collection = newProcessHash.values();
	    	Iterator it = collection.iterator();
	    	
	    	while(it.hasNext())
	    	{
	    		ProcessInfo info = (ProcessInfo)it.next();
	    		list.add(info);
	    	}
		}
		else
		{
			list = detailVect;
		}
                String orderflag1 = this.getParaValue("orderflag1");
                boolean orderflagBoolean = true;
                System.out.println(orderflag1);
                if ("0".equals(orderflag1)) {
                    orderflagBoolean = false;
                    orderflag1 = "1";
                } else {
                    orderflag1 = "0";
                }
		sort(list,orderflag, orderflagBoolean);
		dateFormat(list);
		//gzm//
		request.setAttribute("flag", flag);
		request.setAttribute("orderflag1", orderflag1);
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash",hash);
		request.setAttribute("userhash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("processhash",processhash);
		request.setAttribute("newProcessHash", newProcessHash);
		request.setAttribute("detailVect", detailVect);
		request.setAttribute("list", list);
		request.setAttribute("layer", layer);
		
		//String tmp = request.getParameter("id");
    	//Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		if(host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI && host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")){
			//WINDOWS服务器,跳转到WMI采集展示页面
			return "/detail/host_wmiproc.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxproc.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxproc.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxproc.jsp";
		}else if((host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL || host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET || host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH) && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aixproc.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.42")>=0){
			//Solaris服务器,跳转到Solaris采集展示页面
			return "/detail/host_solarisproc.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.11")>=0){
			//hpunix服务器,跳转到hpunix采集展示页面
			return "/detail/host_hpproc.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && (host.getSysOid().indexOf("scounix")>=0 || host.getSysOid().indexOf("scoopenserver")>=0)){
			//scounixware服务器,跳转到scounixware采集展示页面
			return "/detail/host_scounixproc.jsp";
		}else
			return "/detail/host_proc.jsp";  
    }
    
    private void dateFormat(List list)
    {
    	for(int i = 0; i < list.size(); i++)
    	{
    		ProcessInfo processInfo = (ProcessInfo)list.get(i);
    		Float CpuTime = (Float)processInfo.getCpuTime();
//    		System.out.println("CpuTime====="+CpuTime);
    		int int_CpuTime = CpuTime.intValue();
    		int fenzhong = int_CpuTime/60;
    		int miaozhong = int_CpuTime%60;
    		String s_CpuTime = fenzhong + ":" + miaozhong + "秒";
    		processInfo.setCpuTime(s_CpuTime);
    		processInfo.setCpuUtilization(processInfo.getCpuUtilization().toString() + "%");
    		processInfo.setMemory(processInfo.getMemory() + "K");
    		processInfo.setMemoryUtilization(processInfo.getMemoryUtilization() + "%");
    	}
    }
    
    private List sort(List list, String type, final boolean order)
    {
    	if(type.equals("CpuTime"))
		{
        	Collections.sort(list, new Comparator(){
        		public int compare(Object o1, Object o2) {
        			ProcessInfo p1 = (ProcessInfo)o1;
        			ProcessInfo p2 = (ProcessInfo)o2;
        			if((Float)p1.getCpuTime() <= (Float)p2.getCpuTime() && order)
        				return 1;
        			else
        				return -1;
        		}
        	});
		}
		if(type.equals("CpuUtilization"))
		{
			Collections.sort(list, new Comparator(){
        		public int compare(Object o1, Object o2) {
        			ProcessInfo p1 = (ProcessInfo)o1;
        			ProcessInfo p2 = (ProcessInfo)o2;
        			if((Float)p1.getCpuUtilization() <= (Float)p2.getCpuUtilization() && order)
        				return 1;
        			else
        				return -1;
        		}
        	});
		}
		if(type.equals("Memory") || type.equals(""))
		{
			Collections.sort(list, new Comparator(){
        		public int compare(Object o1, Object o2) {
        			ProcessInfo p1 = (ProcessInfo)o1;
        			ProcessInfo p2 = (ProcessInfo)o2;
        			if((Float)p1.getMemory() <= (Float)p2.getMemory() && order)
        				return 1;
        			else
        				return -1;
        		}
        	});
		}
		if(type.equals("MemoryUtilization"))
		{
			Collections.sort(list, new Comparator(){
        		public int compare(Object o1, Object o2) {
        			ProcessInfo p1 = (ProcessInfo)o1;
        			ProcessInfo p2 = (ProcessInfo)o2;
        			if((Float)p1.getMemoryUtilization() <= (Float)p2.getMemoryUtilization() && order)
        				return 1;
        			else
        				return -1;
        		}
        	});
		}
		return list;
    }
    
    private String hostservice()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		Hashtable processhash = new Hashtable();
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
    	String tmp = request.getParameter("id");
    	HostNodeDao hostdao = new HostNodeDao();
    	List hostlist = hostdao.loadHost();
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	
		String newip=doip(host.getIpAddress());
		String[] time = {"",""};
		getTime(request,time);
		String starttime = time[0];
		String endtime = time[1];	
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}							
		
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		String[] item = {"CPU"};
		
		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
		try{
			//hash = hostlastmanager.getbyCategories(host.getIpAddress(),item,starttime,endtime);
		}catch(Exception e){
			e.printStackTrace();
		}
		//从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try{
			cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
		}catch(Exception e){
			e.printStackTrace();
		}
		//String pingconavg ="";
		//cpu最大值
		maxhash = new Hashtable();
		String cpumax="";
		if(cpuhash.get("max")!=null){
				cpumax = (String)cpuhash.get("max");
		}
		maxhash.put("cpu",cpumax);
		//cpu平均值
		//maxhash = new Hashtable();
		String cpuavg="";
		if(cpuhash.get("avgcpucon")!=null){
				cpuavg = (String)cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg",cpuavg);

		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData != null){
			Vector cpuV = (Vector)ipAllData.get("cpu");
			if(cpuV != null && cpuV.size()>0){
				
				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			//得到系统启动时间
			Vector systemV = (Vector)ipAllData.get("system");
			if(systemV != null && systemV.size()>0){
				for(int i=0;i<systemV.size();i++){
					Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
					if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
						sysuptime = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
						sysservices = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		  //按order将进程信息排序							  
		  String order = "MemoryUtilization";
		  if((request.getParameter("orderflag") != null) &&(!request.getParameter("orderflag").equals(""))){
			order = request.getParameter("orderflag");
		  }
		  try{
			  processhash = hostlastmanager.getProcess_share(host.getIpAddress(),"Process",order,starttime,endtime);
		  }catch(Exception ex){
			  ex.printStackTrace();
		  }
		  String[] _item = {"User"};
		  try{
			  userhash = hostlastmanager.getbyCategories_share(host.getIpAddress(),_item,starttime,endtime);
		  }catch(Exception ex){
			  ex.printStackTrace();
		  }
		  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Hashtable ConnectUtilizationhash = new Hashtable();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if (ConnectUtilizationhash.get("avgpingcon")!=null){
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}
		
		
		Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
		if(pingData != null && pingData.size()>0){
			Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
			Calendar tempCal = (Calendar)pingdata.getCollecttime();							
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}
		   
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash",hash);
		request.setAttribute("userhash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("processhash",processhash);
		
		//String tmp = request.getParameter("id");
    	//Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		if(host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI && host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")){
			//WINDOWS服务器,跳转到WMI采集展示页面
			return "/detail/host_wmiservice.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxservice.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxservice.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxservice.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aixservice.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aixservice.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.42")>=0){
			//Solaris服务器,跳转到Solaris采集展示页面
			return "/detail/host_solarisservice.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.11")>=0){
			//hpunix服务器,跳转到hpunix采集展示页面
			return "/detail/host_hpunixservice.jsp";
		}else
			return "/detail/host_service.jsp";  
    }
    
    private String hostarp()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		Hashtable processhash = new Hashtable();
		Vector vector = new Vector();
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
    	String tmp = request.getParameter("id");
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	
//		String newip=doip(host.getIpAddress());
//		String[] time = {"",""};
//		getTime(request,time);
//		String starttime = time[0];
//		String endtime = time[1];	
//		String time1 = request.getParameter("begindate");
//		if(time1 == null){
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			time1 = sdf.format(new Date());
//		}							
//		
//		String starttime1 = time1 + " 00:00:00";
//		String totime1 = time1 + " 23:59:59";						
//		String[] item = {"CPU"};
//		
//		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
//		try{
//			//hash = hostlastmanager.getbyCategories(host.getIpAddress(),item,starttime,endtime);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		//从collectdata取cpu,内存的历史数据
//		Hashtable cpuhash = new Hashtable();
//		try{
//			cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		//String pingconavg ="";
//		//cpu最大值
//		maxhash = new Hashtable();
//		String cpumax="";
//		if(cpuhash.get("max")!=null){
//				cpumax = (String)cpuhash.get("max");
//		}
//		maxhash.put("cpu",cpumax);
//		//cpu平均值
//		//maxhash = new Hashtable();
//		String cpuavg="";
//		if(cpuhash.get("avgcpucon")!=null){
//				cpuavg = (String)cpuhash.get("avgcpucon");
//		}
//		maxhash.put("cpuavg",cpuavg);

		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData != null){
			vector = (Vector)ipAllData.get("ipmac");
		}
		  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		   
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash",hash);
		request.setAttribute("vector",vector);
		session.setAttribute("ipmacV", vector);
		request.setAttribute("userhash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		//request.setAttribute("processhash",processhash);
		
		//String tmp = request.getParameter("id");
    	//Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		if(host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI && host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")){
			//WINDOWS服务器,跳转到WMI采集展示页面
			return "/detail/host_wmiarp.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxarp.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxarp.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxarp.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aixarp.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.42")>=0){
			//Solaris服务器,跳转到Solaris采集展示页面
			return "/detail/host_solarisarp.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.11")>=0){
			//hpunix服务器,跳转到hpunix采集展示页面
			return "/detail/host_hpunixarp.jsp";
		}else
			return "/detail/host_arp.jsp";  
    }
    
    private String hostdevice()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		Hashtable processhash = new Hashtable();
		Vector vector = new Vector();
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
    	String tmp = request.getParameter("id");
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	
		String newip=doip(host.getIpAddress());
		String[] time = {"",""};
		getTime(request,time);
		String starttime = time[0];
		String endtime = time[1];	
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}							
		
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		
		//从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try{
			cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
		}catch(Exception e){
			e.printStackTrace();
		}
		//String pingconavg ="";
		//cpu最大值
		maxhash = new Hashtable();
		String cpumax="";
		if(cpuhash.get("max")!=null){
				cpumax = (String)cpuhash.get("max");
		}
		maxhash.put("cpu",cpumax);
		//cpu平均值
		//maxhash = new Hashtable();
		String cpuavg="";
		if(cpuhash.get("avgcpucon")!=null){
				cpuavg = (String)cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg",cpuavg);

		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData != null){
			vector = (Vector)ipAllData.get("device");
		}
		   
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash",hash);
		request.setAttribute("vector",vector);
		request.setAttribute("userhash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		session.setAttribute("deviceV", vector);
		//request.setAttribute("processhash",processhash);
		
		//String tmp = request.getParameter("id");
    	//Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")){
			//WINDOWS服务器,跳转到WMI采集展示页面
			return "/detail/host_wmidevice.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxdevice.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxdevice.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxdevice.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aixdevice.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.42")>=0){
			//Solaris服务器,跳转到Solaris采集展示页面
			return "/detail/host_solarisdevice.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.11")>=0){
			//hpunix服务器,跳转到hpunix采集展示页面
			return "/detail/host_hpunixdevice.jsp";
		}else
			return "/detail/host_device.jsp";  
    }
    
    private String hoststorage()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		Hashtable processhash = new Hashtable();
		Vector vector = new Vector();
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
    	String tmp = request.getParameter("id");
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	
		String newip=doip(host.getIpAddress());
		String[] time = {"",""};
		getTime(request,time);
		String starttime = time[0];
		String endtime = time[1];	
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}							
		
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		//从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try{
			cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
		}catch(Exception e){
			e.printStackTrace();
		}
		//String pingconavg ="";
		//cpu最大值
		maxhash = new Hashtable();
		String cpumax="";
		if(cpuhash.get("max")!=null){
				cpumax = (String)cpuhash.get("max");
		}
		maxhash.put("cpu",cpumax);
		//cpu平均值
		//maxhash = new Hashtable();
		String cpuavg="";
		if(cpuhash.get("avgcpucon")!=null){
				cpuavg = (String)cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg",cpuavg);

		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData != null){
			vector = (Vector)ipAllData.get("storage");
		}
		session.setAttribute("storageV",vector);
		  //按order将进程信息排序							  
		  String order = "MemoryUtilization";
		  if((request.getParameter("orderflag") != null) &&(!request.getParameter("orderflag").equals(""))){
			order = request.getParameter("orderflag");
		  }
		   
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash",hash);
		request.setAttribute("vector",vector);
		request.setAttribute("userhash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		//request.setAttribute("processhash",processhash);
		
		//String tmp = request.getParameter("id");
    	//Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		if(host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI && host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")){
			//WINDOWS服务器,跳转到WMI采集展示页面
			return "/detail/host_wmistorage.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxstorage.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxstorage.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxstorage.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aixstorage.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.42")>=0){
			//Solaris服务器,跳转到Solaris采集展示页面
			return "/detail/host_solarisstorage.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.11")>=0){
			//hpunix服务器,跳转到hpunix采集展示页面
			return "/detail/host_hpunixstorage.jsp";
		}else
			return "/detail/host_storage.jsp";  
    }
    
    private String hostsyslog()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		List list = new ArrayList();
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
		String b_time ="";
		String t_time = "";
		
    	String tmp = request.getParameter("id");
    	HostNodeDao hostdao = new HostNodeDao();
    	List hostlist = hostdao.loadHost();
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	
		String newip=doip(host.getIpAddress());
		String[] time = {"",""};
		getTime(request,time);
		String starttime = time[0];
		String endtime = time[1];	
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}							
		
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		String[] item = {"CPU"};
		
		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
		try{
			//hash = hostlastmanager.getbyCategories(host.getIpAddress(),item,starttime,endtime);
		}catch(Exception e){
			e.printStackTrace();
		}
		//从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try{
			cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
		}catch(Exception e){
			e.printStackTrace();
		}
		//String pingconavg ="";
		//cpu最大值
		maxhash = new Hashtable();
		String cpumax="";
		if(cpuhash.get("max")!=null){
				cpumax = (String)cpuhash.get("max");
		}
		maxhash.put("cpu",cpumax);
		//cpu平均值
		//maxhash = new Hashtable();
		String cpuavg="";
		if(cpuhash.get("avgcpucon")!=null){
				cpuavg = (String)cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg",cpuavg);
		
	    	b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			String starttime2 = b_time + " 00:00:00";
			String totime2 = t_time + " 23:59:59";
			String priorityname = getParaValue("priorityname");
			if(priorityname == null)priorityname = "all";
		  SyslogDao syslogdao = new SyslogDao();
		  try{
			  list = syslogdao.getQuery(host.getIpAddress(), starttime2, totime2, priorityname);
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		   
		request.setAttribute("list",list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		
		request.setAttribute("hash",hash);
		request.setAttribute("userhash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		if(host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI && host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")){
			//WINDOWS服务器,跳转到WMI采集展示页面
			return "/detail/host_wmisyslog.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxsyslog.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxsyslog.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxsyslog.jsp";
		}else if((host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL || host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET || host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH) && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aixsyslog.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.42")>=0){
			//Solaris服务器,跳转到Solaris采集展示页面
			return "/detail/host_solarissyslog.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.11")>=0){
			//hpunix服务器,跳转到hpunix采集展示页面
			return "/detail/host_hpsyslog.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && (host.getSysOid().indexOf("scounix")>=0 || host.getSysOid().indexOf("scoopenserver")>=0)){
			//scounixware服务器,跳转到scounixware采集展示页面
			return "/detail/host_scounixsyslog.jsp";
		}else
			return "/detail/host_syslog.jsp";
	    
    }
    
    private String hosterrpt()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		List list = new ArrayList();
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		String sysname = "";
		
		String b_time ="";
		String t_time = "";

		Hashtable pagehash = new Hashtable();
		Hashtable paginghash = new Hashtable();
		int pageingused = 0;
		String totalpageing = "";
		Nodeconfig nodeconfig = new Nodeconfig();
		Vector cpuV = null;
		
    	String tmp = request.getParameter("id");
    	HostNodeDao hostdao = new HostNodeDao();
    	List hostlist = hostdao.loadHost();
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	NodeUtil nodeUtil = new NodeUtil();
        NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host); 
    	
		String newip=doip(host.getIpAddress());
		String[] time = {"",""};
		getTime(request,time);
		String starttime = time[0];
		String endtime = time[1];	
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}							
		
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		String[] item = {"CPU"};
		
		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
		try{
			//hash = hostlastmanager.getbyCategories(host.getIpAddress(),item,starttime,endtime);
		}catch(Exception e){
			e.printStackTrace();
		}
		//从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try{
			cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
		}catch(Exception e){
			e.printStackTrace();
		}
		//String pingconavg ="";
		//cpu最大值
		maxhash = new Hashtable();
		String cpumax="";
		if(cpuhash.get("max")!=null){
				cpumax = (String)cpuhash.get("max");
		}
		maxhash.put("cpu",cpumax);
		//cpu平均值
		//maxhash = new Hashtable();
		String cpuavg="";
		if(cpuhash.get("avgcpucon")!=null){
				cpuavg = (String)cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg",cpuavg);

		String runmodel = PollingEngine.getCollectwebflag(); 
		if("0".equals(runmodel)){
	       	//采集与访问是集成模式
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData != null){
				if(ipAllData.containsKey("cpu")){
					cpuV = (Vector)ipAllData.get("cpu");
					if(cpuV != null && cpuV.size()>0){
						CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
						if(cpu != null && cpu.getThevalue() != null){
							cpuvalue = new Double(cpu.getThevalue());
						}
					}
				}
				//得到系统启动时间
				if(ipAllData.containsKey("system")){
					Vector systemV = (Vector)ipAllData.get("system");
					if(systemV != null && systemV.size()>0){
						for(int i=0;i<systemV.size();i++){
							Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
							if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
								sysuptime = systemdata.getThevalue();
							}
							if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
								sysservices = systemdata.getThevalue();
							}
							if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
								sysdescr = systemdata.getThevalue();
							}
							if(systemdata.getSubentity().equalsIgnoreCase("SysName")){
								sysname = systemdata.getThevalue();
							}
						}
					}
				}
				if(ipAllData.containsKey("pagehash")){
					pagehash = (Hashtable)ipAllData.get("pagehash");
				}
				if(ipAllData.containsKey("paginghash")){
					paginghash = (Hashtable)ipAllData.get("paginghash");
				}
				if(ipAllData.containsKey("nodeconfig")){
					nodeconfig = (Nodeconfig) ipAllData.get("nodeconfig"); 
				}
				if(ipAllData.containsKey("collecttime")){
					collecttime = (String)ipAllData.get("collecttime");
				}
//				if(ipAllData.containsKey("errptlog")){
//					Vector errVector = new Vector();
//					errVector = (Vector)ipAllData.get("errptlog");
//					if(errVector != null && errVector.size()>0){
//						for(int i=0;i<errVector.size();i++){
//							Errptlog errptlog = (Errptlog)errVector.get(i);
//							list.add(errptlog);
//						}
//					}
//				}
			}
			//Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
			//if(pingData != null && pingData.size()>0){
				//Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
				//Calendar tempCal = (Calendar)pingdata.getCollecttime();							
				//Date cc = tempCal.getTime();
				//collecttime = sdf1.format(cc);
			//}
			//得到数据采集时间
//			collecttime = (String)ipAllData.get("collecttime");
		}else{
			//采集与访问是分离模式
//			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
//			if(ipAllData != null){
//				Vector cpuV = (Vector)ipAllData.get("cpu");
			CpuInfoService cpuInfoService = new CpuInfoService(nodedto.getId()+"",nodedto.getType(),nodedto.getSubtype()); 
			cpuV = cpuInfoService.getCpuInfo();  
			if(cpuV != null && cpuV.size()>0){
				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
				if(cpu != null && cpu.getThevalue() != null){
					cpuvalue = new Double(cpu.getThevalue());
				}
			}
			//得到系统启动时间
//				Vector systemV = (Vector)ipAllData.get("system");
			SystemInfoService systemInfoService = new SystemInfoService(nodedto.getId()+"",nodedto.getType(),nodedto.getSubtype()); 
			Vector systemV = systemInfoService.getSystemInfo();
			if(systemV != null && systemV.size()>0){
				for(int i=0;i<systemV.size();i++){
					Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
					if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
						sysuptime = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
						sysservices = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
						sysdescr = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("SysName")){
						sysname = systemdata.getThevalue();
					}
				}
			}
//			}
			OtherInfoService otherInfoService = new OtherInfoService(nodedto.getId()+"",nodedto.getType(),nodedto.getSubtype());
			collecttime = otherInfoService.getCollecttime();
			paginghash = otherInfoService.getPaginghash();
	   		pagehash = otherInfoService.getPagehash();
	   		NodeconfigDao nodeconfigDao = new NodeconfigDao();
			try{
				nodeconfig = nodeconfigDao.getByNodeID(nodedto.getId()+"");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				nodeconfigDao.close();
			}
//			list = getHosterrptList(tmp);
		}
		list = getHosterrptList(tmp);
		  //按order将进程信息排序							  
		  String order = "MemoryUtilization";
		  if((request.getParameter("orderflag") != null) &&(!request.getParameter("orderflag").equals(""))){
			order = request.getParameter("orderflag");
		  }
		  String[] _item = {"User"};
		  try{
			  userhash = hostlastmanager.getbyCategories_share(host.getIpAddress(),_item,starttime,endtime);
		  }catch(Exception ex){
			  ex.printStackTrace();
		  }
	    	b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			String starttime2 = b_time + " 00:00:00";
			String totime2 = t_time + " 23:59:59";
			String priorityname = getParaValue("priorityname");
			if(priorityname == null)priorityname = "all";
			
			
		  
		  
		  
		  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Hashtable ConnectUtilizationhash = new Hashtable();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if (ConnectUtilizationhash.get("avgpingcon")!=null){
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}
		request.setAttribute("ConnectUtilizationhash", ConnectUtilizationhash);
		request.setAttribute("nodeconfig", nodeconfig);
		request.setAttribute("cpuV", cpuV);
		request.setAttribute("pagehash", pagehash);
		request.setAttribute("paginghash", paginghash);
		request.setAttribute("list",list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		
		request.setAttribute("hash",hash);
		request.setAttribute("userhash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("SysName", sysname);
		request.setAttribute("pingconavg", new Double(pingconavg));
		//Aix服务器,跳转到Aix采集展示页面
		return "/detail/host_aixerrpt.jsp";
		
	    
    }
    
    /**
     * 获取 Hosterrpt 的列表
     */
    private List getHosterrptList(String id){
    	List list = null;
    	ErrptlogDao errptlogDao = new ErrptlogDao();
		  try{
			  //System.out.println(getHosterrptSQL());
//			  list = errptlogDao.findByCondition(getHosterrptSQL() + " and nodeid='" + id + "'");
			  list = errptlogDao.findByCondition(getHosterrptSQL(id));
		  }catch(Exception e){
			  e.printStackTrace();
		  } finally {
			  errptlogDao.close();
		  }
		  return list;
    }
    
    /**
     * 获取 Hosterrpt 列表 的 SQL 语句
     * @return
     */
    private String getHosterrptSQL(String id){
    	String startdate = getParaValue("startdate");
    	String todate = getParaValue("todate");
    	String errpttype = getParaValue("errpttype");
    	String errptclass = getParaValue("errptclass");
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	if(startdate == null){
    		startdate = sdf.format(new Date());
    	}
    	String startTime = startdate + " 00:00:00";
    	if(todate == null){
    		todate = sdf.format(new Date());
    	}
    	String toTime = todate + " 23:59:59";
    	
    	String sql ="";
    	if(SystemConstant.DBType.equals("mysql")){
    		sql= " where collettime>='" + startTime + "' and collettime<='" + toTime + "' ";
    	}else if(SystemConstant.DBType.equalsIgnoreCase("oracle")){
    		sql= " where collettime>=to_date('" + startTime + "','yyyy-mm-dd hh24:mi:ss') and collettime<=to_date('" + toTime + "','yyyy-mm-dd hh24:mi:ss') ";
    	}
    	if(errpttype==null || "all".equals(errpttype)){
    		errpttype = "all";
    	}else {
    		sql += " and errpttype='" + errpttype.toUpperCase() + "'";
    	}
    	
    	if(errptclass==null || "all".equals(errptclass)){
    		errptclass = "all";
    	}else {
    		sql += " and errptclass='" + errptclass.toUpperCase() + "'";
    	}
    	sql = sql +" and hostid='"+id+"'";
    	request.setAttribute("startdate", startdate);
    	request.setAttribute("todate", todate);
    	request.setAttribute("errpttype", errpttype);
    	request.setAttribute("errptclass", errptclass);
    	return sql;
    }
    
    private String hosterrptDetail(){
    	String errptlogId = getParaValue("errptlogId");
    	Errptlog errptlog = null;
    	ErrptlogDao errptlogDao = new ErrptlogDao();
    	try {
    		//System.out.println(errptlogId);
    		errptlog = (Errptlog)errptlogDao.findByID(errptlogId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			errptlogDao.close();
		}
    	request.setAttribute("errptlog", errptlog);
    	return "/detail/host_aixerrpt_detail.jsp";
    }
    
    private String hostsw()
    {
    	Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		Hashtable processhash = new Hashtable();
		Hashtable softwarehash = new Hashtable();
		Vector softwareV = null;
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
//		String name = null;
//		String swid = null;
//		String type = null;
//		String insdate = null;
		
    	String tmp = request.getParameter("id");
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	
		String newip=doip(host.getIpAddress());
		String[] time = {"",""};
		getTime(request,time);
		String starttime = time[0];
		String endtime = time[1];	
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}							
		
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		
		//从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try{
			cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
		}catch(Exception e){
			e.printStackTrace();
		}
		//String pingconavg ="";
		//cpu最大值
		maxhash = new Hashtable();
		String cpumax="";
		if(cpuhash.get("max")!=null){
				cpumax = (String)cpuhash.get("max");
		}
		maxhash.put("cpu",cpumax);
		//cpu平均值
		//maxhash = new Hashtable();
		String cpuavg="";
		if(cpuhash.get("avgcpucon")!=null){
				cpuavg = (String)cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg",cpuavg);

		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		
		if(ipAllData != null){
			try{
			    softwareV = (Vector)ipAllData.get("software");
				
			  }catch(Exception ex){
				  ex.printStackTrace();
			  }
		}
		   
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash",hash);
		request.setAttribute("userhash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("processhash",processhash);

		request.setAttribute("softwareV",softwareV);
		
		session.setAttribute("softwareV",softwareV);
		return "/detail/host_sw.jsp";
    }
    
    private String hostroute()
    {
    	Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		Hashtable processhash = new Hashtable();
		Hashtable softwarehash = new Hashtable();
		List routelist = new ArrayList();
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = "";
		String sysuptime = "";
		String sysservices = "";
		String sysdescr = "";
		
    	String tmp = request.getParameter("id");
    	HostNodeDao hostdao = new HostNodeDao();
    	List hostlist = hostdao.loadHost();
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	
    	SupperDao supperdao = new SupperDao();
    	Supper supper = null;
    	try{
    		supper = (Supper)supperdao.findByID(host.getSupperid()+"");
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		supperdao.close();
    	}
    	request.setAttribute("supper", supper);
    	
		String newip=doip(host.getIpAddress());
		String[] time = {"",""};
		getTime(request,time);
		String starttime = time[0];
		String endtime = time[1];	
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}							
		
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		String[] item = {"CPU"};
		
		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
		try{
			//hash = hostlastmanager.getbyCategories(host.getIpAddress(),item,starttime,endtime);
		}catch(Exception e){
			e.printStackTrace();
		}
		//从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try{
			cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
		}catch(Exception e){
			e.printStackTrace();
		}
		//String pingconavg ="";
		//cpu最大值
		maxhash = new Hashtable();
		String cpumax="";
		if(cpuhash.get("max")!=null){
				cpumax = (String)cpuhash.get("max");
		}
		maxhash.put("cpu",cpumax);
		//cpu平均值
		//maxhash = new Hashtable();
		String cpuavg="";
		if(cpuhash.get("avgcpucon")!=null){
				cpuavg = (String)cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg",cpuavg);

		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		
		if(ipAllData != null){
			
			Vector cpuV = (Vector)ipAllData.get("cpu");
			if(cpuV != null && cpuV.size()>0){
				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
				if(cpu != null && cpu.getThevalue() != null){
					cpuvalue = new Double(cpu.getThevalue());
				}
			}
			//得到系统启动时间
			Vector systemV = (Vector)ipAllData.get("system");
			if(systemV != null && systemV.size()>0){
				for(int i=0;i<systemV.size();i++){
					Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
					if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
						sysuptime = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
						sysservices = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
						sysdescr = systemdata.getThevalue();
					}
				}
			}
			try{
//				Vector softwareV = (Vector)ipAllData.get("software");
//				for(int i=0;i<softwareV.size();i++){
//					Softwarecollectdata swdata = (Softwarecollectdata)softwareV.get(i);
//					name = swdata.getName();
//					swid = swdata.getSwid();
//					type = swdata.getType();
//					insdate = swdata.getInsdate();
//			}
				//softwarehash = (Hashtable) ipAllData.get("software");
				routelist = (List)ipAllData.get("routelist");
				
			  }catch(Exception ex){
				  ex.printStackTrace();
			  }
		}
		  
		  String[] _item = {"User"};
		  try{
			  userhash = hostlastmanager.getbyCategories_share(host.getIpAddress(),_item,starttime,endtime);
		  }catch(Exception ex){
			  ex.printStackTrace();
		  }
		  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Hashtable ConnectUtilizationhash = new Hashtable();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if (ConnectUtilizationhash.get("avgpingcon")!=null){
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}
		
		
		Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
		if(pingData != null && pingData.size()>0){
			Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
			Calendar tempCal = (Calendar)pingdata.getCollecttime();							
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}
		   
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash",hash);
		request.setAttribute("userhash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("processhash",processhash);
//		request.setAttribute("name",name);
//		request.setAttribute("swid",swid);
//		request.setAttribute("type",type);
//		request.setAttribute("insdate",insdate);
		request.setAttribute("routelist",routelist);
		
		//session.setAttribute("softwareV",softwareV);
		return "/detail/host_aixroute.jsp";
    }
    
    
    private String hostwinservice()
    {
    	Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		Hashtable processhash = new Hashtable();
		Hashtable softwarehash = new Hashtable();
		Vector serviceV = null;
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
//		String name = null;
//		String swid = null;
//		String type = null;
//		String insdate = null;
		
    	String tmp = request.getParameter("id");
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	
		String newip=doip(host.getIpAddress());
		String[] time = {"",""};
		getTime(request,time);
		String starttime = time[0];
		String endtime = time[1];	
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}							
		
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		//从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try{
			cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
		}catch(Exception e){
			e.printStackTrace();
		}
		//String pingconavg ="";
		//cpu最大值
		maxhash = new Hashtable();
		String cpumax="";
		if(cpuhash.get("max")!=null){
				cpumax = (String)cpuhash.get("max");
		}
		maxhash.put("cpu",cpumax);
		//cpu平均值
		//maxhash = new Hashtable();
		String cpuavg="";
		if(cpuhash.get("avgcpucon")!=null){
				cpuavg = (String)cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg",cpuavg);

		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		
		if(ipAllData != null){
			try{
				serviceV = (Vector)ipAllData.get("winservice");
				
			  }catch(Exception ex){
				  ex.printStackTrace();
			  }
		}
		   
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash",hash);
		request.setAttribute("userhash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("processhash",processhash);
//		request.setAttribute("name",name);
//		request.setAttribute("swid",swid);
//		request.setAttribute("type",type);
//		request.setAttribute("insdate",insdate);
		request.setAttribute("serviceV",serviceV);
		return "/detail/host_winservice.jsp";
    }
    
    private String refresh()
	{		
		Vector softwareVector = new Vector();
		WindowsSnmp wins = new WindowsSnmp();
		String id = getParaValue("id"); 
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
		String[] oids = null;
		int[] intvalues = null;
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());

		SnmpService snmp = new SnmpService();
		softwareVector = new Vector();
		 try{
			  oids = new String[] {               
					  "1.3.6.1.2.1.25.6.3.1.2",  //名称
					  "1.3.6.1.2.1.25.6.3.1.3",  //id
					  "1.3.6.1.2.1.25.6.3.1.4",    //类别
					  "1.3.6.1.2.1.25.6.3.1.5" };   //安装日期

			  String[][] valueArray = null;   	  
				try {
					valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
				} catch(Exception e){
					valueArray = null;
					SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
				}	
				for(int i=0;i<valueArray.length;i++){
					Softwarecollectdata softwaredata = new Softwarecollectdata();	
					String name = valueArray[i][0];
					String swid = valueArray[i][1];
					String type = valueArray[i][2];
					if(type.equalsIgnoreCase("4")){
						type="应用软件";
					}else{
						type="系统软件";
					}
				    String insdate = valueArray[i][3];
				    String swdate = wins.getDate(insdate);
				    softwaredata.setIpaddress(host.getIpAddress());
				    softwaredata.setName(name);
				    softwaredata.setSwid(swid);
				    softwaredata.setType(type);
				    softwaredata.setInsdate(swdate);
				    softwareVector.addElement(softwaredata);
					//System.out.println(name+"######"+id+"######"+type+"######"+dates);
				    
				}	  
		   }catch(Exception e){
				  //e.printStackTrace();
			  }
	    ipAllData.put("softwareV", softwareVector);
	    ShareData.getSharedata().put(host.getIpAddress(), ipAllData);			
		return "/monitor.do?action=hostsw&id="+id+"&ipaddress="+host.getIpAddress();
	}
    
    private String refreshhostarp()
	{		
		Vector ipmacVector = new Vector();
		//WindowsSnmp wins = new WindowsSnmp();
		String id = getParaValue("id"); 
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
		String[] oids = null;
		int[] intvalues = null;
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());

		SnmpService snmp = new SnmpService();
		try
	     {
	        oids = new String[]
	                    {"1.3.6.1.2.1.4.22.1.1",   //1.ifIndex
	        		     "1.3.6.1.2.1.4.22.1.2",   //2.mac
	                     "1.3.6.1.2.1.4.22.1.3",   //3.ip
	                     "1.3.6.1.2.1.4.22.1.4"};  //4.type
			String[][] valueArray = null;   	  
			try {
				valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
			} catch(Exception e){
				valueArray = null;
				e.printStackTrace();
				SysLogger.error(host.getIpAddress() + "_CiscoSnmp");
			}
		   	  for(int i=0;i<valueArray.length;i++)
		   	  {
		   		  IpMac ipmac = new IpMac();
		   		  for(int j=0;j<4;j++){
		   			String sValue = valueArray[i][j];
		   			//SysLogger.info("MAC===="+sValue);
		   			if(sValue == null)continue;
					if(j==0){
						ipmac.setIfindex(sValue);
					}else if (j==1){
						ipmac.setMac(sValue);
					}else if (j==2){
						ipmac.setIpaddress(sValue);									
					}
		   		 }
		   		ipmac.setIfband("0");
		   		ipmac.setIfsms("0");
				ipmac.setCollecttime(new GregorianCalendar());
				ipmac.setRelateipaddr(host.getIpAddress());
				ipmacVector.addElement(ipmac);
				//SysLogger.info("ARP hostip==>"+host.getIpAddress()+"=="+ipmac.getMac()+"====="+ipmac.getIpaddress());
				//MACVSIP.put(ipmac.getMac(), ipmac.getIpaddress());
		   	  }	
	    }
	    catch (Exception e)
	    {
	    	//SysLogger.error("getIpNetToMediaTable(),ip=" + address + ",community=" + community);
	        //tableValues = null;
	        e.printStackTrace();
	    }
	    ipAllData.put("ipmac", ipmacVector);
	    ShareData.getSharedata().put(host.getIpAddress(), ipAllData);			
		return "/monitor.do?action=hostarp&id="+id+"&ipaddress="+host.getIpAddress();
	}
    
    private String refreshhostdevice()
	{		
		Vector deviceVector = new Vector();
		//WindowsSnmp wins = new WindowsSnmp();
		String id = getParaValue("id"); 
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
		String[] oids = null;
		int[] intvalues = null;
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());

		SnmpService snmp = new SnmpService();
		try
	     {
	        oids = new String[] {               
					  "1.3.6.1.2.1.25.3.2.1.1",  //hrDeviceIndex
					  "1.3.6.1.2.1.25.3.2.1.2",  //hrDeviceType
					  "1.3.6.1.2.1.25.3.2.1.3",    //hrDeviceDescr
					  "1.3.6.1.2.1.25.3.2.1.5" };   //hrDeviceStatus
			String[][] valueArray = null;   	  
			try {
				valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
			} catch(Exception e){
				valueArray = null;
				e.printStackTrace();
				SysLogger.error(host.getIpAddress() + "_HostSnmp");
			}
			Devicecollectdata devicedata = null;
			for(int i=0;i<valueArray.length;i++){
				devicedata = new Devicecollectdata();
				String devindex = valueArray[i][0];
				String type = valueArray[i][1];
				String name = valueArray[i][2];
				String status = valueArray[i][3];
				if(status == null)status="";
				if(device_Status.containsKey(status))status = (String)device_Status.get(status);
				devicedata.setDeviceindex(devindex);
				devicedata.setIpaddress(host.getIpAddress());
				devicedata.setName(name);
				devicedata.setStatus(status);
				devicedata.setType((String)device_Type.get(type));
				deviceVector.addElement(devicedata);
				//SysLogger.info(name+"######"+devindex+"######"+(String)device_Type.get(type)+"######"+status);
			    
			}	
	    }
	    catch (Exception e)
	    {
	    	//SysLogger.error("getIpNetToMediaTable(),ip=" + address + ",community=" + community);
	        //tableValues = null;
	        e.printStackTrace();
	    }
	    ipAllData.put("device", deviceVector);
	    ShareData.getSharedata().put(host.getIpAddress(), ipAllData);			
		return "/monitor.do?action=hostdevice&id="+id+"&ipaddress="+host.getIpAddress();
	}
    
    private String refreshhoststorage()
	{		
		Vector storageVector = new Vector();
		//WindowsSnmp wins = new WindowsSnmp();
		String id = getParaValue("id"); 
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
		String[] oids = null;
		int[] intvalues = null;
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());

		SnmpService snmp = new SnmpService();
		try
	     {
	        oids = new String[] {               
					  "1.3.6.1.2.1.25.2.3.1.1",  //hrStorageIndex
					  "1.3.6.1.2.1.25.2.3.1.2",  //hrStorageType
					  "1.3.6.1.2.1.25.2.3.1.3",    //hrStorageDescr
					  "1.3.6.1.2.1.25.2.3.1.4",    //hrStorageAllocationUnits
					  "1.3.6.1.2.1.25.2.3.1.5" };   //hrStorageSize
			String[][] valueArray = null;   	  
			try {
				valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
			} catch(Exception e){
				valueArray = null;
				e.printStackTrace();
				SysLogger.error(host.getIpAddress() + "_HostSnmp");
			}
			Storagecollectdata storagedata = null;
			for(int i=0;i<valueArray.length;i++){
				storagedata = new Storagecollectdata();
				String storageindex = valueArray[i][0];
				String type = valueArray[i][1];
				String name = valueArray[i][2];
				String byteunit = valueArray[i][3];
				String cap = valueArray[i][4];
				int allsize=Integer.parseInt(cap.trim());
				
				float size=0.0f;
				  size=allsize*Long.parseLong(byteunit)*1.0f/1024/1024;
				  String unit = ""; 
				  if(size>=1024.0f){
					  size=size/1024;
					  //diskdata.setUnit("G");
					  unit = "G"; 
				  }
				  else{
				  	//diskdata.setUnit("M");
				  	unit = "M"; 
				  }
				storagedata.setStorageindex(storageindex);
				storagedata.setIpaddress(host.getIpAddress());
				storagedata.setName(name);
				storagedata.setCap(Arith.floatToStr(size+"", 1, 0)+unit);
				storagedata.setType((String)storage_Type.get(type));
				storageVector.addElement(storagedata);
				//SysLogger.info(name+"######"+storageindex+"######"+(String)storage_Type.get(type)+"######"+size+unit);					    
			}
	    }
	    catch (Exception e)
	    {
	    	//SysLogger.error("getIpNetToMediaTable(),ip=" + address + ",community=" + community);
	        //tableValues = null;
	        e.printStackTrace();
	    }
	    ipAllData.put("storage", storageVector);
	    ShareData.getSharedata().put(host.getIpAddress(), ipAllData);			
		return "/monitor.do?action=hoststorage&id="+id+"&ipaddress="+host.getIpAddress();
	}
    
    
    private String insertdb()
	{		
		Vector softwareVector = new Vector();
		DBManager dbmanager = new DBManager();
		WindowsSnmp wins = new WindowsSnmp();
		String id = getParaValue("id"); 
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
		String[] oids = null;
		int[] intvalues = null;
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		String ip = host.getIpAddress();
//		String ip1 ="",ip2="",ip3="",ip4="";
//		String[] ipdot = ip.split(".");	
//		String tempStr = "";
//		String allipstr = "";
//		
//		if (ip.indexOf(".")>0){
//			ip1=ip.substring(0,ip.indexOf("."));
//			ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//			tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//		}
//		ip2=tempStr.substring(0,tempStr.indexOf("."));
//		ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//		allipstr=ip1+ip2+ip3+ip4;
		String allipstr = SysUtil.doip(ip);
		String tablename = "software" + allipstr;
		String delsql ="delete from "+tablename;
		try {
			dbmanager.executeUpdate(delsql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SnmpService snmp = new SnmpService();
		softwareVector = new Vector();
		 try{
			  oids = new String[] {               
					  "1.3.6.1.2.1.25.6.3.1.2",  //名称
					  "1.3.6.1.2.1.25.6.3.1.3",  //id
					  "1.3.6.1.2.1.25.6.3.1.4",    //类别
					  "1.3.6.1.2.1.25.6.3.1.5" };   //安装日期

			  String[][] valueArray = null;   	  
				try {
					valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
				} catch(Exception e){
					valueArray = null;
					SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
				}	
				for(int i=0;i<valueArray.length;i++){
					Softwarecollectdata softwaredata = new Softwarecollectdata();	
					String name = valueArray[i][0];
					String swid = valueArray[i][1];
					String type = valueArray[i][2];
					if(type.equalsIgnoreCase("4")){
						type="应用软件";
					}else{
						type="系统软件";
					}
				    String insdate = valueArray[i][3];
				    String swdate = wins.getDate(insdate);
				    softwaredata.setIpaddress(host.getIpAddress());
				    softwaredata.setName(name);
				    softwaredata.setSwid(swid);
				    softwaredata.setType(type);
				    softwaredata.setInsdate(swdate);
				    softwareVector.addElement(softwaredata);
					//System.out.println(name+"######"+id+"######"+type+"######"+dates);
				    String sql ="insert into "+tablename+" (ipaddress,name,swid,type,insdate) "
				    + "values(\"" + ip + "\",\"" + name + "\",\"" + swid + "\",\""
					+ type + "\",\"" + swdate + "\")";
				    try {
						dbmanager.executeUpdate(sql);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}	  
		   }catch(Exception e){
				  //e.printStackTrace();
			  }finally{
				  dbmanager.close();
						  
			  }
	    ipAllData.put("softwareV", softwareVector);
	    ShareData.getSharedata().put(host.getIpAddress(), ipAllData);			
		return "/monitor.do?action=hostsw&id="+id+"&ipaddress="+host.getIpAddress();
	}
    
    private String insertarpdb()
	{		
		Vector softwareVector = new Vector();
		DBManager dbmanager = new DBManager();
		WindowsSnmp wins = new WindowsSnmp();
		String id = getParaValue("id"); 
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
		String[] oids = null;
		int[] intvalues = null;
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		String ip = host.getIpAddress();
//		String ip1 ="",ip2="",ip3="",ip4="";
//		String[] ipdot = ip.split(".");	
//		String tempStr = "";
//		String allipstr = "";
//		
//		if (ip.indexOf(".")>0){
//			ip1=ip.substring(0,ip.indexOf("."));
//			ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//			tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//		}
//		ip2=tempStr.substring(0,tempStr.indexOf("."));
//		ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//		allipstr=ip1+ip2+ip3+ip4;
		String allipstr = SysUtil.doip(ip);
		String tablename = "software" + allipstr;
		String delsql ="delete from "+tablename;
		try {
			dbmanager.executeUpdate(delsql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SnmpService snmp = new SnmpService();
		softwareVector = new Vector();
		 try{
			  oids = new String[] {               
					  "1.3.6.1.2.1.25.6.3.1.2",  //名称
					  "1.3.6.1.2.1.25.6.3.1.3",  //id
					  "1.3.6.1.2.1.25.6.3.1.4",    //类别
					  "1.3.6.1.2.1.25.6.3.1.5" };   //安装日期

			  String[][] valueArray = null;   	  
				try {
					valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
				} catch(Exception e){
					valueArray = null;
					SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
				}	
				for(int i=0;i<valueArray.length;i++){
					Softwarecollectdata softwaredata = new Softwarecollectdata();	
					String name = valueArray[i][0];
					String swid = valueArray[i][1];
					String type = valueArray[i][2];
					if(type.equalsIgnoreCase("4")){
						type="应用软件";
					}else{
						type="系统软件";
					}
				    String insdate = valueArray[i][3];
				    String swdate = wins.getDate(insdate);
				    softwaredata.setIpaddress(host.getIpAddress());
				    softwaredata.setName(name);
				    softwaredata.setSwid(swid);
				    softwaredata.setType(type);
				    softwaredata.setInsdate(swdate);
				    softwareVector.addElement(softwaredata);
					//System.out.println(name+"######"+id+"######"+type+"######"+dates);
				    String sql ="insert into "+tablename+" (ipaddress,name,swid,type,insdate) "
				    + "values('" + ip + "','" + name + "','" + swid + "','"
					+ type + "','" + swdate + "')";
				    try {
						dbmanager.executeUpdate(sql);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}	  
		   }catch(Exception e){
				  //e.printStackTrace();
			  }finally{
				  dbmanager.close();
					System.gc();		  
			  }
	    ipAllData.put("softwareV", softwareVector);
	    ShareData.getSharedata().put(host.getIpAddress(), ipAllData);			
		return "/monitor.do?action=hostarp&id="+id+"&ipaddress="+host.getIpAddress();
	}
    
    private String hostsyslogdetail()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		List list = new ArrayList();
		try{
			String ip=getParaValue("ipaddress");
			int id = getParaIntValue("id");
			Syslog syslog = new Syslog();
			SyslogDao dao = new SyslogDao();
			syslog = dao.getSyslogData(id, ip);								
			request.setAttribute("syslog", syslog);	
		}catch(Exception e){
			e.printStackTrace();
		}
	    return "/detail/host_syslogdetail.jsp";
    }
    private String hostevent()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		Hashtable processhash = new Hashtable();
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		//String time = null;
		List list = new ArrayList();
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		tmp = request.getParameter("id");
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		
		try{
    	status = getParaIntValue("status");
    	level1 = getParaIntValue("level1");
    	if(status == -1)status=99;
    	if(level1 == -1)level1=99;
    	request.setAttribute("status", status);
    	request.setAttribute("level1", level1);
    	
    	b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
		if (b_time == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
    	
    	HostNodeDao hostdao = new HostNodeDao();
    	List hostlist = hostdao.loadHost();
    	
    	
    	
		String newip=doip(host.getIpAddress());
		String[] time = {"",""};
		getTime(request,time);
		String starttime = time[0];
		String endtime = time[1];	
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}							
		
		String starttime2 = b_time + " 00:00:00";
		String totime2 = t_time + " 23:59:59";
		
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		String[] item = {"CPU"};
		
		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
		try{
			//hash = hostlastmanager.getbyCategories(host.getIpAddress(),item,starttime,endtime);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(!NodeUtil.isOnlyCollectPing(host)){
			//从collectdata取cpu,内存的历史数据
			Hashtable cpuhash = new Hashtable();
			try{
				cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
			}catch(Exception e){
				e.printStackTrace();
			}
			//String pingconavg ="";
			String cpumax="";
			if(cpuhash.get("max")!=null){
					cpumax = (String)cpuhash.get("max");
			}
		}
		EventListDao dao = new EventListDao();
		try{
			User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
			//SysLogger.info("user businessid===="+vo.getBusinessids());
			list = dao.getQuery(starttime2,totime2,status+"",level1+"",
					vo.getBusinessids(),host.getId());
			//ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			dao.close();
		}

		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData != null){
			Vector cpuV = (Vector)ipAllData.get("cpu");
			if(cpuV != null && cpuV.size()>0){
				
				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			//得到系统启动时间
			Vector systemV = (Vector)ipAllData.get("system");
			if(systemV != null && systemV.size()>0){
				for(int i=0;i<systemV.size();i++){
					Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
					if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
						sysuptime = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
						sysservices = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		  //按order将进程信息排序							  
		  String order = "MemoryUtilization";
		  if((request.getParameter("orderflag") != null) &&(!request.getParameter("orderflag").equals(""))){
			order = request.getParameter("orderflag");
		  }
		  
//		  try{
//			  processhash = hostlastmanager.getProcess_share(host.getIpAddress(),"Process",order,starttime,endtime);
//		  }catch(Exception ex){
//			  ex.printStackTrace();
//		  }
		  String[] _item = {"User"};
		  try{
			  userhash = hostlastmanager.getbyCategories_share(host.getIpAddress(),_item,starttime,endtime);
		  }catch(Exception ex){
			  ex.printStackTrace();
		  }
		  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Hashtable ConnectUtilizationhash = new Hashtable();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if (ConnectUtilizationhash.get("avgpingcon")!=null){
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}
		
		
		Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
		if(pingData != null && pingData.size()>0){
			Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
			Calendar tempCal = (Calendar)pingdata.getCollecttime();							
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}
		
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("list",list);
		
		request.setAttribute("hash",hash);
		request.setAttribute("userhash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("processhash",processhash);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI && host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")){
			//WINDOWS服务器,跳转到WMI采集展示页面
			return "/detail/host_wmievent.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxevent.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxevent.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxevent.jsp";
		}else if((host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL || host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET || host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH) && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aixevent.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.42")>=0){
			//Solaris服务器,跳转到Solaris采集展示页面
			return "/detail/host_solarisevent.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.11")>=0){
			//hpunix服务器,跳转到hpunix采集展示页面
			return "/detail/host_hpevent.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && (host.getSysOid().indexOf("scounix")>=0 || host.getSysOid().indexOf("scoopenserver")>=0)){
			//scounixware服务器,跳转到scounixware采集展示页面
			return "/detail/host_scounixevent.jsp";
		}else if(NodeUtil.isOnlyCollectPing(host)){
			//只采集连通率的信息时,屏蔽cpu、内存等数据
			return "/detail/host_event_onlyping.jsp";
		}
			return "/detail/host_event.jsp";
	    
    }
    /**
     * 
     * @description 告警新窗口
     * @author wangxiangyong
     * @date Aug 24, 2012 10:07:43 AM
     * @return String  
     * @return
     */
    private String hosteventlist()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		Hashtable processhash = new Hashtable();
		String ip="";
		int id =0;
		double cpuvalue = 0;
		String pingconavg ="0";
		//String time = null;
		List list = new ArrayList();
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		id = getParaIntValue("id");
		String nodetype="";
		nodetype=getParaValue("nodetype");
//		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		try{
    	status = getParaIntValue("status");
    	level1 = getParaIntValue("level1");
    	if(status == -1)status=99;
    	if(level1 == -1)level1=99;
    	request.setAttribute("status", status);
    	request.setAttribute("level1", level1);
    	
    	b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
		if (b_time == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
    	
    	HostNodeDao hostdao = new HostNodeDao();
    	List hostlist = hostdao.loadHost();
    	
    	
    	
//		String newip=doip(host.getIpAddress());
		String[] time = {"",""};
		getTime(request,time);
		String starttime = time[0];
		String endtime = time[1];	
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}							
		
		String starttime2 = b_time + " 00:00:00";
		String totime2 = t_time + " 23:59:59";
		
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		
		
		EventListDao dao = new EventListDao();
		try{
			User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
			if(nodetype!=null){
				if(nodetype.equals("net")||nodetype.equals("host")){
			         list = dao.getQuery(starttime2,totime2,status+"",level1+"", user.getBusinessids(),id);
				}else if (nodetype.equals("db")) {
					list = dao.getQuery(starttime1,totime1,"db",status+"",level1+"",user.getBusinessids(),id);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			dao.close();
		}
 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("list",list);
		request.setAttribute("nodetype", nodetype);
		request.setAttribute("id", id+"");
	
		}catch(Exception e){
			e.printStackTrace();
		}
			return "/detail/host_event_list.jsp";
	    
    }
//    private String netping()
//    {
//    	
//		Hashtable imgurlhash=new Hashtable();
//		Hashtable hash1 = new Hashtable();//"System","Ping"--current
//		Hashtable maxhash = new Hashtable();//"Cpu"--max
//		Hashtable maxhash1 = new Hashtable();//"Cpu"--max
//		String routeconfig="";
//		String tmp = "";
//		
//		double cpuvalue = 0;
//		String pingconavg ="0";
//		String collecttime = null;
//		String sysuptime = null;
//		String sysservices = null;
//		String sysdescr = null;
//		
//		try {
//			tmp = request.getParameter("id");
//			HostNodeDao hostdao = new HostNodeDao();
//			List hostlist = hostdao.loadHost();
//    	
//			Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
//    	
//			String newip=doip(host.getIpAddress());
//			String[] time = {"",""};
//			getTime(request,time);
//			String starttime = time[0];
//			String endtime = time[1];	
//			String time1 = request.getParameter("begindate");
//			if(time1 == null){
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				time1 = sdf.format(new Date());
//			}
//									
//		
//			String starttime1 = time1 + " 00:00:00";
//			String totime1 = time1 + " 23:59:59";						
//			String[] item1 = {"System","Ping"};
//			I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
//			try{
//				hash1 = hostlastmanager.getbyCategories_share(host.getIpAddress(),item1,starttime,endtime);
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
//			Hashtable ConnectUtilizationhash = new Hashtable();
//			try{
//				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
//
//			if (ConnectUtilizationhash.get("avgpingcon")!=null)
//				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
//			//画图
//			//p_draw_line(responsehash,"连通率",newip+"response",750,200);
//			p_draw_line(ConnectUtilizationhash,"连通率",newip+"ConnectUtilization",740,150);
//
//			//ping平均值
//			maxhash = new Hashtable();
//			maxhash.put("avgpingcon",pingconavg);
//
//			//imgurlhash
//			imgurlhash.put("ConnectUtilization","resource\\image\\jfreechart\\"+newip+"ConnectUtilization"+".png");
//
//			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
//			if(ipAllData != null){
//				Vector cpuV = (Vector)ipAllData.get("cpu");
//				if(cpuV != null && cpuV.size()>0){
//					
//					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
//					cpuvalue = new Double(cpu.getThevalue());
//				}
//				//得到系统启动时间
//				Vector systemV = (Vector)ipAllData.get("system");
//				if(systemV != null && systemV.size()>0){
//					for(int i=0;i<systemV.size();i++){
//						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
//						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
//							sysuptime = systemdata.getThevalue();
//						}
//						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
//							sysservices = systemdata.getThevalue();
//						}
//						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
//							sysdescr = systemdata.getThevalue();
//						}
//					}
//				}
//			}
//			if(pingconavg != null){
//				pingconavg = pingconavg.replace("%", "");
//			}
//			
//			Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
//			if(pingData != null && pingData.size()>0){
//				Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
//				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
//				Date cc = tempCal.getTime();
//				collecttime = sdf1.format(cc);
//			}
//			
//			Hashtable ResponseTimehash = new Hashtable();
//			try{
//				ResponseTimehash = hostmanager.getCategory(host.getIpAddress(),"Ping","ResponseTime",starttime1,totime1);
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
//
//			if (ResponseTimehash.get("avgpingcon")!=null)
//				pingconavg = (String)ResponseTimehash.get("avgpingcon");
//			//画图
//			p_draw_line(ResponseTimehash,"响应时间",newip+"ResponseTime",740,180);
//			//p_draw_line(ResponseTimehash,"响应时间",newip+"ResponseTime",740,150);
//
//			//ping平均值
//			maxhash1 = new Hashtable();
//			maxhash1.put("avgpingcon",pingconavg);
//
//			//imgurlhash
//			imgurlhash.put("ResponseTime","resource\\image\\jfreechart\\"+newip+"ResponseTime"+".png");
//
//			Hashtable ipAllData1 = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
//			if(ipAllData1 != null){
//				Vector cpuV = (Vector)ipAllData1.get("cpu");
//				if(cpuV != null && cpuV.size()>0){
//					
//					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
//					cpuvalue = new Double(cpu.getThevalue());
//				}
//				//得到系统启动时间
//				Vector systemV = (Vector)ipAllData1.get("system");
//				if(systemV != null && systemV.size()>0){
//					for(int i=0;i<systemV.size();i++){
//						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
//						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
//							sysuptime = systemdata.getThevalue();
//						}
//						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
//							sysservices = systemdata.getThevalue();
//						}
//						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
//							sysdescr = systemdata.getThevalue();
//						}
//					}
//				}
//			}
//			if(pingconavg != null){
//				pingconavg = pingconavg.replace("%", "");
//			}
//			
//			Vector pingData1 = (Vector)ShareData.getPingdata().get(host.getIpAddress());
//			if(pingData1 != null && pingData1.size()>0){
//				Pingcollectdata pingdata = (Pingcollectdata)pingData1.get(0);
//				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
//				Date cc = tempCal.getTime();
//				collecttime = sdf1.format(cc);
//			}
//			
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		request.setAttribute("imgurl",imgurlhash);
//		request.setAttribute("hash1",hash1);
//		request.setAttribute("max",maxhash);   	
//		request.setAttribute("id", tmp);
//		
//		request.setAttribute("cpuvalue", cpuvalue);
//		request.setAttribute("collecttime", collecttime);
//		request.setAttribute("sysuptime", sysuptime);
//		request.setAttribute("sysservices", sysservices);
//		request.setAttribute("sysdescr", sysdescr);
//		
//		request.setAttribute("pingconavg", new Double(pingconavg));
//		
//	    return "/detail/net_ping.jsp";
//    }
    
    private String hostutilhdx()
    {
		String tmp = "";
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		tmp = request.getParameter("id");
		String flag = request.getParameter("flag"); 
		Host host = null;
		try {
			host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		}
		catch (Exception e) {
			e.printStackTrace();
		}  	
		request.setAttribute("id", tmp);
		request.setAttribute("flag", flag);
		
		if(host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI && host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")){
			//WINDOWS服务器,跳转到WMI采集展示页面
			return "/detail/host_wmiutilhdx.jsp?id="+tmp;
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxutilhdx.jsp?id="+tmp;
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/topology/network/host_aixutilhdx.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/topology/network/host_aixutilhdx.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.11")>=0){
			//hpunix服务器,跳转到hpunix采集展示页面
			return "/detail/host_hputilhdx.jsp?id="+tmp;
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.42.2.1.1")>=0)
			//SUN服务器,跳转到SUN采集展示页面
			return "/detail/host_solarisutilhdx.jsp";
		else
			return "/detail/hostutilhdx.jsp?id="+tmp;
	    
    }
    
//    private String hostping()
//    {
//    	
//		Hashtable imgurlhash=new Hashtable();
//		Hashtable hash1 = new Hashtable();//"System","Ping"--current
//		Hashtable maxhash = new Hashtable();//"Cpu"--max
//		String routeconfig="";
//		String tmp = "";
//		
//		double cpuvalue = 0;
//		String pingconavg ="0";
//		String collecttime = null;
//		String sysuptime = null;
//		String sysservices = null;
//		String sysdescr = null;
//		
//		try {
//			tmp = request.getParameter("id");
//			HostNodeDao hostdao = new HostNodeDao();
//			List hostlist = hostdao.loadHost();
//    	
//			Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
//    	
//			String newip=doip(host.getIpAddress());
//			String[] time = {"",""};
//			getTime(request,time);
//			String starttime = time[0];
//			String endtime = time[1];	
//			String time1 = request.getParameter("begindate");
//			if(time1 == null){
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				time1 = sdf.format(new Date());
//			}
//									
//		
//			String starttime1 = time1 + " 00:00:00";
//			String totime1 = time1 + " 23:59:59";						
//			String[] item1 = {"System","Ping"};
//			I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
//			try{
//				hash1 = hostlastmanager.getbyCategories_share(host.getIpAddress(),item1,starttime,endtime);
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
//			Hashtable ConnectUtilizationhash = new Hashtable();
//			try{
//				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
//
//			if (ConnectUtilizationhash.get("avgpingcon")!=null)
//				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
//			//画图
//			//p_draw_line(responsehash,"连通率",newip+"response",750,200);
//			p_draw_line(ConnectUtilizationhash,"连通率",newip+"ConnectUtilization",740,150);
//
//			//ping平均值
//			maxhash = new Hashtable();
//			maxhash.put("avgpingcon",pingconavg);
//
//			//imgurlhash
//			imgurlhash.put("ConnectUtilization","resource\\image\\jfreechart\\"+newip+"ConnectUtilization"+".png");
//
//			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
//			if(ipAllData != null){
//				Vector cpuV = (Vector)ipAllData.get("cpu");
//				if(cpuV != null && cpuV.size()>0){
//					
//					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
//					cpuvalue = new Double(cpu.getThevalue());
//				}
//				//得到系统启动时间
//				Vector systemV = (Vector)ipAllData.get("system");
//				if(systemV != null && systemV.size()>0){
//					for(int i=0;i<systemV.size();i++){
//						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
//						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
//							sysuptime = systemdata.getThevalue();
//						}
//						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
//							sysservices = systemdata.getThevalue();
//						}
//						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
//							sysdescr = systemdata.getThevalue();
//						}
//					}
//				}
//				
//			}
//			if(pingconavg != null){
//				pingconavg = pingconavg.replace("%", "");
//			}
//			
//			Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
//			if(pingData != null && pingData.size()>0){
//				Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
//				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
//				Date cc = tempCal.getTime();
//				collecttime = sdf1.format(cc);
//			}
//			Hashtable ResponseTimehash = new Hashtable();
//			try{
//				ResponseTimehash = hostmanager.getCategory(host.getIpAddress(),"Ping","ResponseTime",starttime1,totime1);
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
//
//			if (ResponseTimehash.get("avgpingcon")!=null)
//				pingconavg = (String)ResponseTimehash.get("avgpingcon");
//			//画图
//			//p_draw_line(responsehash,"连通率",newip+"response",750,200);
//			p_draw_line(ResponseTimehash,"响应时间",newip+"ResponseTime",740,180);
//
//			//ping平均值
//			maxhash = new Hashtable();
//			maxhash.put("avgpingcon",pingconavg);
//
//			//imgurlhash
//			imgurlhash.put("ResponseTime","resource\\image\\jfreechart\\"+newip+"ResponseTime"+".png");
//
//			Hashtable ipAllData1 = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
//			if(ipAllData1 != null){
//				Vector cpuV = (Vector)ipAllData1.get("cpu");
//				if(cpuV != null && cpuV.size()>0){
//					
//					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
//					cpuvalue = new Double(cpu.getThevalue());
//				}
//				//得到系统启动时间
//				Vector systemV = (Vector)ipAllData1.get("system");
//				if(systemV != null && systemV.size()>0){
//					for(int i=0;i<systemV.size();i++){
//						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
//						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
//							sysuptime = systemdata.getThevalue();
//						}
//						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
//							sysservices = systemdata.getThevalue();
//						}
//						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
//							sysdescr = systemdata.getThevalue();
//						}
//					}
//				}
//				
//			}
//			if(pingconavg != null){
//				pingconavg = pingconavg.replace("%", "");
//			}
//			
//			Vector pingData1 = (Vector)ShareData.getPingdata().get(host.getIpAddress());
//			if(pingData1 != null && pingData1.size()>0){
//				Pingcollectdata pingdata = (Pingcollectdata)pingData1.get(0);
//				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
//				Date cc = tempCal.getTime();
//				collecttime = sdf1.format(cc);
//			}
//			
//			
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		request.setAttribute("imgurl",imgurlhash);
//		request.setAttribute("hash1",hash1);
//		request.setAttribute("max",maxhash);   	
//		request.setAttribute("id", tmp);
//		
//		request.setAttribute("cpuvalue", cpuvalue);
//		request.setAttribute("collecttime", collecttime);
//		request.setAttribute("sysuptime", sysuptime);
//		request.setAttribute("sysservices", sysservices);
//		request.setAttribute("sysdescr", sysdescr);
//		request.setAttribute("pingconavg", new Double(pingconavg));
//		
//	    return "/topology/network/hostview.jsp";
//    }
    private String netarp()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		try {
			
	    	tmp = request.getParameter("id");
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();		
								
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
//		request.setAttribute("cpuvalue", cpuvalue);
//		request.setAttribute("collecttime", time);
//		request.setAttribute("sysuptime", sysuptime);
//		request.setAttribute("sysservices", sysservices);
//		request.setAttribute("sysdescr", sysdescr);
//		request.setAttribute("pingconavg", new Double(pingconavg));
	    return "/detail/net_arp.jsp";
    }
    private String firewallarp()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Host host = null;
		try {
			
	    	tmp = request.getParameter("id");
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();				
									
			//从内存中获得当前的跟此IP相关的IP-MAC的ARP表信息
			Hashtable _relateIpMacHash = ShareData.getRelateipmacdata();
			//vector = (Vector)_relateIpMacHash.get(ip);
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData != null){
				Vector cpuV = (Vector)ipAllData.get("cpu");
				if(cpuV != null && cpuV.size()>0){
					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				//得到系统启动时间
				Vector systemV = (Vector)ipAllData.get("system");
				if(systemV != null && systemV.size()>0){
					for(int i=0;i<systemV.size();i++){
						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
							sysuptime = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
							sysservices = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector)ipAllData.get("ipmac");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
									
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			if (ConnectUtilizationhash.get("avgpingcon")!=null){
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}
			
			
			Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
			if(pingData != null && pingData.size()>0){
				Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
				Date cc = tempCal.getTime();
				time = sdf1.format(cc);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
//		if(host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.14331.1.4")){
//			return "/detail/firewalltos_arp.jsp";
//		}else
			return "/detail/firewall_arp.jsp";
	    
    }
    
    private String firewallarpproxcy()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Host host = null;
		try {
			
	    	tmp = request.getParameter("id");
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();				
									
			//从内存中获得当前的跟此IP相关的IP-MAC的ARP表信息
			Hashtable _relateIpMacHash = ShareData.getRelateipmacdata();
			//vector = (Vector)_relateIpMacHash.get(ip);
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData != null){
				Vector cpuV = (Vector)ipAllData.get("cpu");
				if(cpuV != null && cpuV.size()>0){
					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				//得到系统启动时间
				Vector systemV = (Vector)ipAllData.get("system");
				if(systemV != null && systemV.size()>0){
					for(int i=0;i<systemV.size();i++){
						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
							sysuptime = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
							sysservices = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector)ipAllData.get("arpproxy");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
									
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			if (ConnectUtilizationhash.get("avgpingcon")!=null){
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}
			
			
			Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
			if(pingData != null && pingData.size()>0){
				Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
				Date cc = tempCal.getTime();
				time = sdf1.format(cc);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
//		if(host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.14331.1.4")){
//			return "/detail/firewalltos_arpproxcy.jsp";
//		}else
			return "/detail/firewall_arpproxcy.jsp";
    }
    
    private String firewallvlan()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Host host = null;
		try {
			
	    	tmp = request.getParameter("id");
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();				
									
			//从内存中获得当前的跟此IP相关的IP-MAC的ARP表信息
			Hashtable _relateIpMacHash = ShareData.getRelateipmacdata();
			//vector = (Vector)_relateIpMacHash.get(ip);
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData != null){
				Vector cpuV = (Vector)ipAllData.get("cpu");
				if(cpuV != null && cpuV.size()>0){
					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				//得到系统启动时间
				Vector systemV = (Vector)ipAllData.get("system");
				if(systemV != null && systemV.size()>0){
					for(int i=0;i<systemV.size();i++){
						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
							sysuptime = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
							sysservices = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector)ipAllData.get("vlans");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
									
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			if (ConnectUtilizationhash.get("avgpingcon")!=null){
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}
			
			
			Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
			if(pingData != null && pingData.size()>0){
				Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
				Date cc = tempCal.getTime();
				time = sdf1.format(cc);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
//		if(host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.14331.1.4")){
//			return "/detail/firewalltos_vlan.jsp";
//		}else
			return "/detail/firewall_vlan.jsp";
    }
    
    private String firewalllogin()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Host host = null;
		try {
			
	    	tmp = request.getParameter("id");
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();				
									
			//从内存中获得当前的跟此IP相关的IP-MAC的ARP表信息
			Hashtable _relateIpMacHash = ShareData.getRelateipmacdata();
			//vector = (Vector)_relateIpMacHash.get(ip);
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData != null){
				Vector cpuV = (Vector)ipAllData.get("cpu");
				if(cpuV != null && cpuV.size()>0){
					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				//得到系统启动时间
				Vector systemV = (Vector)ipAllData.get("system");
				if(systemV != null && systemV.size()>0){
					for(int i=0;i<systemV.size();i++){
						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
							sysuptime = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
							sysservices = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector)ipAllData.get("userlogin");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
									
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			if (ConnectUtilizationhash.get("avgpingcon")!=null){
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}
			
			
			Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
			if(pingData != null && pingData.size()>0){
				Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
				Date cc = tempCal.getTime();
				time = sdf1.format(cc);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
//		if(host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.14331.1.4")){
//			return "/detail/firewalltos_login.jsp";
//		}else
			return "/detail/firewall_login.jsp";
    }
    
    private String netfdb()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		try {
			
	    	tmp = request.getParameter("id");
	    			
									
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
	    return "/detail/net_fdb.jsp";
    }
    
    //quzhi
    private String f5poolinfo()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Host host = null;
		try {
			
	    	tmp = request.getParameter("id");
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();				
									
			//从内存中获得当前的跟此IP相关的IP-MAC的ARP表信息
			Hashtable _relateIpMacHash = ShareData.getRelateipmacdata();
			//vector = (Vector)_relateIpMacHash.get(ip);
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData != null){
				Vector cpuV = (Vector)ipAllData.get("cpu");
				if(cpuV != null && cpuV.size()>0){
					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				//得到系统启动时间
				Vector systemV = (Vector)ipAllData.get("system");
				if(systemV != null && systemV.size()>0){
					for(int i=0;i<systemV.size();i++){
						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
							sysuptime = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
							sysservices = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector)ipAllData.get("vlans");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
									
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			if (ConnectUtilizationhash.get("avgpingcon")!=null){
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}
			
			
			Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
			if(pingData != null && pingData.size()>0){
				Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
				Date cc = tempCal.getTime();
				time = sdf1.format(cc);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		if(host.getSysOid().startsWith("1.3.6.1.4.1.3375.2.1.3.4.")||host.getSysOid().startsWith("1.3.6.1.4.1.7564")){
			return "/detail/f5poolinfo.jsp";
		}else
			return "/detail/firewall_vlan.jsp";
    }
    
    
    private String f5rulesinfo()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Host host = null;
		try {
			
	    	tmp = request.getParameter("id");
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();				
									
			//从内存中获得当前的跟此IP相关的IP-MAC的ARP表信息
			Hashtable _relateIpMacHash = ShareData.getRelateipmacdata();
			//vector = (Vector)_relateIpMacHash.get(ip);
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData != null){
				Vector cpuV = (Vector)ipAllData.get("cpu");
				if(cpuV != null && cpuV.size()>0){
					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				//得到系统启动时间
				Vector systemV = (Vector)ipAllData.get("system");
				if(systemV != null && systemV.size()>0){
					for(int i=0;i<systemV.size();i++){
						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
							sysuptime = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
							sysservices = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector)ipAllData.get("userlogin");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
									
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			if (ConnectUtilizationhash.get("avgpingcon")!=null){
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}
			
			
			Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
			if(pingData != null && pingData.size()>0){
				Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
				Date cc = tempCal.getTime();
				time = sdf1.format(cc);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		if(host.getSysOid().startsWith("1.3.6.1.4.1.3375.2.1.3.4.")||host.getSysOid().startsWith("1.3.6.1.4.1.7564")){
			return "/detail/f5rulesinfo.jsp";
		}else
			return "/detail/firewall_login.jsp";
    }
    
    
    
    
    
    private String netiplist()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		try {
			
	    	tmp = request.getParameter("id");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
	    return "/detail/net_iplist.jsp";
    }
    private String telnetCfg()
    {
    	
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());
     try {
			
	    	tmp = request.getParameter("id");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		ip=host.getIpAddress();	
		
		
		String startdate=getParaValue("startdate");
		String todate=getParaValue("todate");
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		if(startdate==null){
			starttime=time1+" 00:00:00";
			startdate=time1;
		}
		if(todate==null){
			totime=time1+ " 23:59:59";
			todate=time1;
		}
		CfgBaseInfoDao baseInfoDao=null;
		PolicyInterfaceDao interfaceDao=null;
		QueueInfoDao queueInfoDao=null;
		List policyInterfaceList=null;
		List queueList=null;
		
		List classList=null;
		List policyList=null;
		GatherTelnetConfigDao cfgDao=new GatherTelnetConfigDao();
		List<GatherTelnetConfig> cfgList=null;
		try {
			cfgList=cfgDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			cfgDao.close();
		}
		Vector<String> ipVector=new Vector<String>();
		if(cfgList!=null&&cfgList.size()>0){
			
			for (int i = 0; i < cfgList.size(); i++) {
				GatherTelnetConfig config=cfgList.get(i);
				if(config!=null){
					String[] ips=config.getTelnetIps().split(",");
					if(ips!=null){
						for (int j = 0; j < ips.length; j++) {
							if(ips[j]!=null&&!ips[j].trim().equals("")){
								ipVector.add(ips[j]);
							}
						}
						
					}
				}
				
			}
			
		}
		if(ipVector.contains(ip)){
		try {
			String allipstr = SysUtil.doip(ip);
			 baseInfoDao=new CfgBaseInfoDao(allipstr);
			 interfaceDao=new PolicyInterfaceDao(allipstr);
			 queueInfoDao=new QueueInfoDao(allipstr);
			 classList=baseInfoDao.findByCondition(" where type='class'");
			 baseInfoDao=new CfgBaseInfoDao(allipstr);
			 policyList=baseInfoDao.findByCondition(" where type='policy' ");
			 policyInterfaceList=interfaceDao.findByCondition(" where collecttime>='"+starttime+"' and collecttime<='"+totime+"' group by interfaceName,policyName,className,collecttime");
			 queueList=queueInfoDao.findByCondition(" where collecttime>='"+starttime+"' and collecttime<='"+totime+"'");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
		baseInfoDao.close();
		interfaceDao.close();
		queueInfoDao.close();
		}
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("classList", classList);
		request.setAttribute("policyList", policyList);
		request.setAttribute("interfaceList", policyInterfaceList);
		request.setAttribute("queueList", queueList);
		
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
	    return "/detail/net_telnetCfg.jsp";
    }
    private String telnetAcl()
    {
    	
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());
     try {
			
	    	tmp = request.getParameter("id");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		ip=host.getIpAddress();	
		
		
		String startdate=getParaValue("startdate");
		String todate=getParaValue("todate");
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		if(startdate==null){
			starttime=time1+" 00:00:00";
			startdate=time1;
		}
		if(todate==null){
			totime=time1+ " 23:59:59";
			todate=time1;
		}
		AclDetailDao detailDao=null;
		List detailList=null;
		AclBaseDao baseDao=null;
		List<AclBase> baseList=null;
		try {
			baseDao=new AclBaseDao();
			baseList=baseDao.findByCondition(" where ipaddress='"+ip+"'");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			baseDao.close();
		}
		StringBuffer ids=new StringBuffer();
		if (baseList!=null&&baseList.size()>0) {
			for (int i = 0; i < baseList.size(); i++) {
				AclBase base=baseList.get(i);
				if (base!=null) {
					ids.append(base.getId()).append(",");
				}
			}
		}
		String baseIds="";
		if(ids.toString().length()>1)
		 baseIds=ids.toString().substring(0,ids.toString().length()-1);
		try {
			String allipstr = SysUtil.doip(ip);
			detailDao=new AclDetailDao();
			if(!baseIds.equals(""))
			 detailList=detailDao.findByCondition(" where collecttime>='"+starttime+"' and collecttime<='"+totime+"' and status=1 and baseId in("+baseIds+") group by baseId,name,collecttime");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			detailDao.close();
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		
		request.setAttribute("detailList", detailList);
		
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
	    return "/detail/net_telnetAcl.jsp";
    }
    private String firewalliplist()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		try {
			
	    	tmp = request.getParameter("id");
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();				
									
			//从内存中获得当前的跟此IP相关的IP-MAC的ARP表信息
			Hashtable _relateIpMacHash = ShareData.getRelateipmacdata();
			//vector = (Vector)_relateIpMacHash.get(ip);
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData != null){
				Vector cpuV = (Vector)ipAllData.get("cpu");
				if(cpuV != null && cpuV.size()>0){
					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				//得到系统启动时间
				Vector systemV = (Vector)ipAllData.get("system");
				if(systemV != null && systemV.size()>0){
					for(int i=0;i<systemV.size();i++){
						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
							sysuptime = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
							sysservices = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector)ipAllData.get("ipmac");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
									
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			if (ConnectUtilizationhash.get("avgpingcon")!=null){
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}
			
			
			Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
			if(pingData != null && pingData.size()>0){
				Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
				Date cc = tempCal.getTime();
				time = sdf1.format(cc);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
	    return "/detail/firewall_iplist.jsp";
    }
    
    private String netevent()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		List list = new ArrayList();
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		try {
			
	    	tmp = request.getParameter("id");
	    	status = getParaIntValue("status");
	    	level1 = getParaIntValue("level1");
	    	if(status == -1)status=99;
	    	if(level1 == -1)level1=99;
	    	request.setAttribute("status", status);
	    	request.setAttribute("level1", level1);
	    	
	    	b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
	    	
			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();				
			
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//String time1 = sdf.format(new Date());
									
		
			
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
				//SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime1,totime1,status+"",level1+"",
						vo.getBusinessids(),host.getId());
				//ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
	    return "/detail/net_event.jsp";
    }
    private String firewallevent()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		List list = new ArrayList();
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		Host host = null;
		try {
			
	    	tmp = request.getParameter("id");
	    	status = getParaIntValue("status");
	    	level1 = getParaIntValue("level1");
	    	if(status == -1)status=99;
	    	if(level1 == -1)level1=99;
	    	request.setAttribute("status", status);
	    	request.setAttribute("level1", level1);
	    	
	    	b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
	    	
			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();	
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime1,totime1,status+"",level1+"",vo.getBusinessids(),host.getId());				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
//		if(host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.14331.1.4")){
//			return "/detail/firewalltos_event.jsp";
//		}else
			return "/detail/firewall_event.jsp";
	    
    }
    
    
    private String firewallpolicy()
    {	
		String ip="";
		String tmp ="";
		int trustflag = 0;
		try {
			
	    	tmp = request.getParameter("id");
	    	trustflag = getParaIntValue("trustflag");
	    	request.setAttribute("trustflag", trustflag);
	    	//Integer.parseInt(arg0)
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();				

			Hashtable policys = ShareData.getPolicydata();
        	Hashtable itValue = new Hashtable();

        	
        	if (policys != null && policys.size()>0){
        		itValue = (Hashtable)policys.get(ip);
        	}
        	if(trustflag == 0){
                request.setAttribute("trustList", (List)itValue.get("untotrust"));          			            	            		
        	}else{
                request.setAttribute("trustList", (List)itValue.get("trusttoun"));          			            	            		
        	}
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);

	    return "/detail/firewall_policy.jsp";
    }
    
    private String accit()
    {
		Hashtable bandhash = new Hashtable();
		String eventid = "";
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		List list = new ArrayList();
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		EventList event = null;
		try {
			eventid = getParaValue("eventid");
			EventListDao dao = new EventListDao();
			event = (EventList)dao.findByID(eventid);
			
	    	tmp = request.getParameter("id");
	    	status = getParaIntValue("status");
	    	level1 = getParaIntValue("level1");
	    	if(status == -1)status=99;
	    	if(level1 == -1)level1=99;
	    	request.setAttribute("status", status);
	    	request.setAttribute("level1", level1);
	    	
	    	b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
	    	
			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();				
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("eventdetail", event);
	    return "/detail/net_accitevent.jsp";
    }
    
    private String accfi()
    {
		Hashtable bandhash = new Hashtable();
		String eventid = "";
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		List list = new ArrayList();
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		EventList event = null;
		try {
			eventid = getParaValue("eventid");
			EventListDao dao = new EventListDao();
			event = (EventList)dao.findByID(eventid);
			event.setManagesign(new Integer(1));
			dao = new EventListDao();
			dao.update(event);
	    	tmp = request.getParameter("id");
	    	status = getParaIntValue("status");
	    	level1 = getParaIntValue("level1");
	    	if(status == -1)status=99;
	    	if(level1 == -1)level1=99;
	    	request.setAttribute("status", status);
	    	request.setAttribute("level1", level1);
	    	
	    	b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
	    	
			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
									
		
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";
			
			try{
				User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
				//SysLogger.info("user businessid===="+vo.getBusinessids());
				dao = new EventListDao();
				list = dao.getQuery(starttime1,totime1,status+"",level1+"",
						vo.getBusinessids(),host.getId());
			}catch(Exception ex){
				ex.printStackTrace();
			}				
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("eventdetail", event);
	    return "/detail/net_event.jsp";
    }
    
    private String fireport()
    {
		Hashtable bandhash = new Hashtable();
		String eventid = "";
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		List list = new ArrayList();
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		EventList event = null;
		try {
			eventid = getParaValue("eventid");
			EventListDao dao = new EventListDao();
			event = (EventList)dao.findByID(eventid);
			//event.setManagesign(new Integer(1));
			Integer nowstatus = event.getManagesign();
			dao = new EventListDao();
			dao.update(event);
	    	tmp = request.getParameter("id");
	    	status = getParaIntValue("status");
	    	level1 = getParaIntValue("level1");
	    	if(status == -1)status=99;
	    	if(level1 == -1)level1=99;
	    	request.setAttribute("status", status);
	    	request.setAttribute("level1", level1);
	    	request.setAttribute("nowstatus", nowstatus);
	    	
	    	b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
	    	
			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
									
		
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";				
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("eventdetail", event);
	    return "/detail/net_accitevent.jsp";
    }
    
    private String doreport()
    {
		Hashtable bandhash = new Hashtable();
		String eventid = "";
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		List list = new ArrayList();
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		EventList event = null;
		try {
			eventid = getParaValue("eventid");
			EventListDao dao = new EventListDao();
			event = (EventList)dao.findByID(eventid);
			event.setManagesign(new Integer(2));
			dao = new EventListDao();
			dao.update(event);
			EventReport eventreport = new EventReport();
			Date d = sdf0.parse(getParaValue("deal_time"));
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			eventreport.setDeal_time(c);
			eventreport.setReport_content(getParaValue("report_content"));
			eventreport.setReport_man(getParaValue("report_man"));
			eventreport.setReport_time(Calendar.getInstance());
			eventreport.setEventid(Integer.valueOf(eventid));			
			EventReportDao reportdao = new EventReportDao();
			reportdao.save(eventreport);
	    	tmp = request.getParameter("id");
	    	status = getParaIntValue("status");
	    	level1 = getParaIntValue("level1");
	    	if(status == -1)status=99;
	    	if(level1 == -1)level1=99;
	    	request.setAttribute("status", status);
	    	request.setAttribute("level1", level1);
	    	
	    	b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
	    	
			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
									
		
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";				
			try{
				User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
				//SysLogger.info("user businessid===="+vo.getBusinessids());
				dao = new EventListDao();
				list = dao.getQuery(starttime1,totime1,status+"",level1+"",
						vo.getBusinessids(),host.getId());
				
				//ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("eventdetail", event);
	    return "/detail/net_event.jsp";
    }
    
    private String viewreport()
    {
		Hashtable bandhash = new Hashtable();
		String eventid = "";
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		List list = new ArrayList();
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		EventList event = null;
		String deal_time = "";
		String report_time = "";
		EventReport eventreport = null;
		try {
			eventid = getParaValue("eventid");
			EventListDao dao = new EventListDao();
			event = (EventList)dao.findByID(eventid);
			//取得报告详细信息
			EventReportDao rdao = new EventReportDao();
			eventreport = (EventReport)rdao.findByEventId(eventid);
			
			deal_time = sdf0.format(eventreport.getDeal_time().getTime());
			report_time = sdf0.format(eventreport.getReport_time().getTime());
			
	    	tmp = request.getParameter("id");
	    	status = getParaIntValue("status");
	    	level1 = getParaIntValue("level1");
	    	if(status == -1)status=99;
	    	if(level1 == -1)level1=99;
	    	request.setAttribute("status", status);
	    	request.setAttribute("level1", level1);
	    	
	    	b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
	    	
			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
									
		
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";				
			try{
				User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
				//SysLogger.info("user businessid===="+vo.getBusinessids());
				dao = new EventListDao();
				list = dao.getQuery(starttime1,totime1,status+"",level1+"",
						vo.getBusinessids(),host.getId());
				
				//ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("eventdetail", event);
		request.setAttribute("eventreport", eventreport);
		request.setAttribute("deal_time", deal_time);
		request.setAttribute("report_time", report_time);
	    return "/detail/net_accitevent.jsp";
    }
    
    private String showutilhdx()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		List yearlist = new ArrayList();
		List monthlist = new ArrayList();
		
		Hashtable value = new Hashtable();
		
		String ip="";
		String index="";
		String ifname="";
		String time1 ="";
		String b_time = "";
		String t_time = "";
		String perelement ="";

		I_HostCollectDataDay daymanager = new HostCollectDataDayManager();						
		//I_HostCollectDataHour hourmanager = new HostCollectDataHourManager();
		
		HostNode hostnode = null;
		try {
			ip = getParaValue("ipaddress");//request.getParameter("ipaddress");
			index = getParaValue("ifindex");//request.getParameter("index");
			ifname = getParaValue("ifname");
			time1 = getParaValue("begindate");
			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
			perelement = getParaValue("perelement");
			if (perelement == null || perelement.trim().length()==0)perelement="minutes";
			//SysLogger.info("perelement----"+perelement);
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadNetwork(2);
	    	if(hostlist != null && hostlist.size()>0){
	    		for(int i=0;i<hostlist.size();i++){
	    			HostNode tempHost = (HostNode)hostlist.get(i);
	    			if(tempHost.getIpAddress().equalsIgnoreCase(ip)){
	    				hostnode = tempHost;
	    				break;
	    			}
	    		}
	    	}
	    	hostdao.close();

			

			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
				//mForm.setStartdate(time1);
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
				//mForm.setTodate(t_time);
			}
			
			if(time1 == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
				//mForm.setBegindate(time1);
			}							
			
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			String category = "UtilHdx";
			
			String newip=doip(ip)+index;
			//ifip = infopointmanager.getBySwitchIf(ip,index);
			
			String[] netIfdetail={"index","ifDescr","ifname","ifType","InBandwidthUtilHdx","OutBandwidthUtilHdx"};

			
			String unit = "kb/s";
			String title = "当天24小时端口流速归档";
			String[] banden3 = {"InBandwidthUtilHdx","OutBandwidthUtilHdx"};
			String[] bandch3 = {"入口流速","出口流速"};
	        String reportname = title + "日报表";

		
			String year = request.getParameter("year");
			String month = request.getParameter("month");
			int index1 = 0;
			Calendar now = Calendar.getInstance();
			SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int yearint = now.get(Calendar.YEAR);
			int monthint = now.get(Calendar.MONTH);
			      
			for (int i = 4; i >= 0; i--) {
				String tmp = String.valueOf(yearint - i);
				yearlist.add(index1, tmp);
				index1++;
			}
			for (int i = 0; i < 12; i++) {
				monthlist.add(i, String.valueOf(i+1));
			}
			if(year==null){
				year = new Integer(yearint).toString();
				month = new Integer(monthint+1).toString();
			}
			
			unit = "kb/s";
			title = "端口流速";	

			
			//山西移动IDC去掉月报表2008-04-30
			if(perelement.equalsIgnoreCase("minutes")){
				//按分钟显示报表
				value = daymanager.getmultiHisHdx(ip,"ifspeed",index,banden3,bandch3,b_time,t_time,"UtilHdx");
				reportname = title+b_time+"至"+t_time+"报表(按分钟显示)";
		        p_drawchartMultiLineMonth(value,reportname,newip+category+"_month",800,200,"UtilHdx");							
				String url1 = "resource/image/jfreechart/"+newip+category+"_month.png";
				imgurlhash.put("bandmonthspeed",url1);
			}else if(perelement.equalsIgnoreCase("hours")){
				//按小时显示报表
				/*山西移动IDC去掉年报表2008-04-30*/
				value = daymanager.getmultiHisHdx(ip,"ifspeed",index,banden3,bandch3,b_time,t_time,"utilhdxhour");
		        reportname = title+b_time+"至"+t_time+"报表(按小时归档显示)";
				p_drawchartMultiLineYear(value,reportname,newip+"ifspeed_year",800,200,"UtilHdx");
				String url1 = "resource/image/jfreechart/"+newip+"ifspeed"+"_year.png";
				imgurlhash.put("bandmonthspeed",url1);
			}else{
				//按天显示报表
				/*山西移动IDC去掉年报表2008-04-30*/
				value = daymanager.getmultiHisHdx(ip,"ifspeed",index,banden3,bandch3,b_time,t_time,"utilhdxday");
		        reportname = title+b_time+"至"+t_time+"报表(按天归档显示)";
				p_drawchartMultiLineYear(value,reportname,newip+"ifspeed_year",800,200,"UtilHdx");
				String url1 = "resource/image/jfreechart/"+newip+"ifspeed"+"_year.png";
				imgurlhash.put("bandmonthspeed",url1);								
			}			
								
			imgurlhash.put("status","resource/image/jfreechart/"+newip+"IfStatus"+".png");
			
			
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("ifip",ifip);
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("index",index);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("hostname",hostnode.getAlias());
		request.setAttribute("ifname",ifname);
		
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("begindate", time1);
		request.setAttribute("perelement", perelement);
		
	    return "/detail/net_utilhdx.jsp";
    }
    
    private String showhostutilhdx()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		List yearlist = new ArrayList();
		List monthlist = new ArrayList();
		
		Hashtable value = new Hashtable();
		
		String ip="";
		String index="";
		String ifname="";
		String time1 ="";
		String b_time = "";
		String t_time = "";
		String perelement ="";

		I_HostCollectDataDay daymanager = new HostCollectDataDayManager();						
		//I_HostCollectDataHour hourmanager = new HostCollectDataHourManager();
		
		HostNode hostnode = null;
		try {
			ip = getParaValue("ipaddress");//request.getParameter("ipaddress");
			index = getParaValue("ifindex");//request.getParameter("index");
			ifname = getParaValue("ifname");
			time1 = getParaValue("begindate");
			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
			perelement = getParaValue("perelement");
			if (perelement == null || perelement.trim().length()==0)perelement="minutes";
			//SysLogger.info("perelement----"+perelement);
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadNetwork(2);
	    	if(hostlist != null && hostlist.size()>0){
	    		for(int i=0;i<hostlist.size();i++){
	    			HostNode tempHost = (HostNode)hostlist.get(i);
	    			if(tempHost.getIpAddress().equalsIgnoreCase(ip)){
	    				hostnode = tempHost;
	    				break;
	    			}
	    		}
	    	}
	    	hostdao.close();

			

			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
				//mForm.setStartdate(time1);
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
				//mForm.setTodate(t_time);
			}
			
			if(time1 == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
				//mForm.setBegindate(time1);
			}							
			
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			String category = "UtilHdx";
			
			String newip=doip(ip)+index;
			//ifip = infopointmanager.getBySwitchIf(ip,index);
			
			String[] netIfdetail={"index","ifDescr","ifname","ifType","InBandwidthUtilHdx","OutBandwidthUtilHdx"};

			
			String unit = "kb/s";
			String title = "当天24小时端口流速归档";
			String[] banden3 = {"InBandwidthUtilHdx","OutBandwidthUtilHdx"};
			String[] bandch3 = {"入口流速","出口流速"};
	        String reportname = title + "日报表";

			
			String year = request.getParameter("year");
			String month = request.getParameter("month");
			int index1 = 0;
			Calendar now = Calendar.getInstance();
			SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int yearint = now.get(Calendar.YEAR);
			int monthint = now.get(Calendar.MONTH);
			      
			for (int i = 4; i >= 0; i--) {
				String tmp = String.valueOf(yearint - i);
				yearlist.add(index1, tmp);
				index1++;
			}
			for (int i = 0; i < 12; i++) {
				monthlist.add(i, String.valueOf(i+1));
			}
			if(year==null){
				year = new Integer(yearint).toString();
				month = new Integer(monthint+1).toString();
			}
			
			unit = "kb/s";
			title = "端口流速";	

			
			//山西移动IDC去掉月报表2008-04-30
			if(perelement.equalsIgnoreCase("minutes")){
				//按分钟显示报表
				value = daymanager.getmultiHisHdx(ip,"ifspeed",index,banden3,bandch3,b_time,t_time,"UtilHdx");
				reportname = title+b_time+"至"+t_time+"报表(按分钟显示)";
		        p_drawchartMultiLineMonth(value,reportname,newip+category+"_month",800,200,"UtilHdx");							
				String url1 = "resource/image/jfreechart/"+newip+category+"_month.png";
				imgurlhash.put("bandmonthspeed",url1);
			}else if(perelement.equalsIgnoreCase("hours")){
				//按小时显示报表
				/*山西移动IDC去掉年报表2008-04-30*/
				value = daymanager.getmultiHisHdx(ip,"ifspeed",index,banden3,bandch3,b_time,t_time,"utilhdxhour");
		        reportname = title+b_time+"至"+t_time+"报表(按小时归档显示)";
				p_drawchartMultiLineYear(value,reportname,newip+"ifspeed_year",800,200,"UtilHdx");
				String url1 = "resource/image/jfreechart/"+newip+"ifspeed"+"_year.png";
				imgurlhash.put("bandmonthspeed",url1);
			}else{
				//按天显示报表
				/*山西移动IDC去掉年报表2008-04-30*/
				value = daymanager.getmultiHisHdx(ip,"ifspeed",index,banden3,bandch3,b_time,t_time,"utilhdxday");
		        reportname = title+b_time+"至"+t_time+"报表(按天归档显示)";
				p_drawchartMultiLineYear(value,reportname,newip+"ifspeed_year",800,200,"UtilHdx");
				String url1 = "resource/image/jfreechart/"+newip+"ifspeed"+"_year.png";
				imgurlhash.put("bandmonthspeed",url1);								
			}			
								
			imgurlhash.put("status","resource/image/jfreechart/"+newip+"IfStatus"+".png");
			
			
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("ifip",ifip);
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("index",index);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("hostname",hostnode.getAlias());
		request.setAttribute("ifname",ifname);
		
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("begindate", time1);
		request.setAttribute("perelement", perelement);
		
	    return "/detail/host_utilhdx.jsp";
    }
    
    private String showdiscardsperc()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();
		Hashtable bandhash = new Hashtable();
		
		List yearlist = new ArrayList();
		List monthlist = new ArrayList();
		
		Hashtable value = new Hashtable();
		
		String ip="";
		String index="";
		String ifname="";
		String time1 ="";
		

		I_HostCollectDataDay daymanager = new HostCollectDataDayManager();						
		//I_HostCollectDataHour hourmanager = new HostCollectDataHourManager();
		
		HostNode hostnode = null;
		try {
			ip = getParaValue("ipaddress");//request.getParameter("ipaddress");
			index = getParaValue("ifindex");//request.getParameter("index");
			ifname = getParaValue("ifname");
			time1 = getParaValue("begindate");
			if(time1 == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
				//mForm.setBegindate(time1);
			}
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadNetwork(1);
	    	if(hostlist != null && hostlist.size()>0){
	    		for(int i=0;i<hostlist.size();i++){
	    			HostNode tempHost = (HostNode)hostlist.get(i);
	    			if(tempHost.getIpAddress().equalsIgnoreCase(ip)){
	    				hostnode = tempHost;
	    				break;
	    			}
	    		}
	    	}
	    	hostdao.close();							
			
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			String newip=doip(ip)+index;
			I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();	
			String[] netIfdetail={"index","ifDescr","ifname","ifType","InDiscardsPerc","OutDiscardsPerc"};
			//丢包率
			String[] banden2 = {"InDiscardsPerc","OutDiscardsPerc"};
			String[] bandch2 = {"入口丢包率","出口丢包率"};
			Hashtable[] bandhashtable2 = hostmanager.getDiscardsPerc(ip,index,banden2,bandch2,starttime1,totime1);

			p_drawchartMultiLine(bandhashtable2[0],"丢包率",newip+"ifdescperc",800,200,"DiscardsPerc");
			
			//String url1 =               ResourceCenter.getInstance().getSysPath()+"resource\\image\\jfreechart\\"+newip+category+"_month.png";
			imgurlhash.put("ifdescperc","resource/image/jfreechart/"+newip+"ifdescperc"+".png");		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("ifip",ifip);
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("index",index);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("hostname",hostnode.getAlias());
		request.setAttribute("ifname",ifname);		
		request.setAttribute("begindate", time1);		
	    return "/detail/net_discardsperc.jsp";
    }
    
    private String showerrorsperc()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();
		Hashtable bandhash = new Hashtable();
		
		List yearlist = new ArrayList();
		List monthlist = new ArrayList();
		
		Hashtable value = new Hashtable();
		
		String ip="";
		String index="";
		String ifname="";
		String time1 ="";
		

		I_HostCollectDataDay daymanager = new HostCollectDataDayManager();						
		//I_HostCollectDataHour hourmanager = new HostCollectDataHourManager();
		
		HostNode hostnode = null;
		try {
			ip = getParaValue("ipaddress");//request.getParameter("ipaddress");
			index = getParaValue("ifindex");//request.getParameter("index");
			ifname = getParaValue("ifname");
			time1 = getParaValue("begindate");
			if(time1 == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
				//mForm.setBegindate(time1);
			}
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadNetwork(1);
	    	if(hostlist != null && hostlist.size()>0){
	    		for(int i=0;i<hostlist.size();i++){
	    			HostNode tempHost = (HostNode)hostlist.get(i);
	    			if(tempHost.getIpAddress().equalsIgnoreCase(ip)){
	    				hostnode = tempHost;
	    				break;
	    			}
	    		}
	    	}
	    	hostdao.close();							
			
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			String newip=doip(ip)+index;
			I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();	
			String[] netIfdetail={"index","ifDescr","ifname","ifType","InDiscardsPerc","OutDiscardsPerc","InErrorsPerc","OutErrorsPerc"};
			hash = hostlastmanager.getIfdetail(ip,index,netIfdetail,starttime1,totime1);
//带宽利用率，流速图
			String[] banden1 = {"InErrorsPerc","OutErrorsPerc"};
			String[] bandch1 = {"入口错误率","出口错误率"};
			Hashtable[] bandhashtable1 = hostmanager.getErrorsPerc(ip,index,banden1,bandch1,starttime1,totime1);
			p_drawchartMultiLine(bandhashtable1[0],"端口错误率",newip+"errorperc",800,200,"ErrorsPerc");

			imgurlhash.put("errorperc","resource/image/jfreechart/"+newip+"errorperc"+".png");
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("ifip",ifip);
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("index",index);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("hostname",hostnode.getAlias());
		request.setAttribute("ifname",ifname);		
		request.setAttribute("begindate", time1);		
	    return "/detail/net_iferrorperc.jsp";
    }
    
    private String showpacks()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();
		Hashtable bandhash = new Hashtable();
		
		List yearlist = new ArrayList();
		List monthlist = new ArrayList();
		
		Hashtable value = new Hashtable();
		
		String ip="";
		String index="";
		String ifname="";
		String time1 ="";
		

		I_HostCollectDataDay daymanager = new HostCollectDataDayManager();						
		//I_HostCollectDataHour hourmanager = new HostCollectDataHourManager();
		
		HostNode hostnode = null;
		try {
			ip = getParaValue("ipaddress");//request.getParameter("ipaddress");
			index = getParaValue("ifindex");//request.getParameter("index");
			ifname = getParaValue("ifname");
			time1 = getParaValue("begindate");
			if(time1 == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
				//mForm.setBegindate(time1);
			}
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadNetwork(1);
	    	if(hostlist != null && hostlist.size()>0){
	    		for(int i=0;i<hostlist.size();i++){
	    			HostNode tempHost = (HostNode)hostlist.get(i);
	    			if(tempHost.getIpAddress().equalsIgnoreCase(ip)){
	    				hostnode = tempHost;
	    				break;
	    			}
	    		}
	    	}
	    	hostdao.close();							
			
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			String newip=doip(ip)+index;
			I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();	
			String[] netIfdetail={"index","ifDescr","ifname","ifType","InCastPkts","OutCastPkts"};
			hash = hostlastmanager.getIfdetail(ip,index,netIfdetail,starttime1,totime1);							
			String[] banden2 = {"InCastPkts","OutCastPkts"};
			String[] bandch2 = {"入口数据包","出口数据包"};							
			Hashtable[] bandhashtable2 = hostmanager.getIfBand_Packs(ip,index,banden2,bandch2,starttime1,totime1);
			
			p_drawchartMultiLine(bandhashtable2[0],"收发信息包数",newip+"ifpacks",800,200,"Packs");
			imgurlhash.put("ifpacks","resource/image/jfreechart/"+newip+"ifpacks"+".png");
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("ifip",ifip);
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("index",index);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("hostname",hostnode.getAlias());
		request.setAttribute("ifname",ifname);		
		request.setAttribute("begindate", time1);		
	    return "/detail/net_ifpacks.jsp";
    }
    
    private String showinpacks()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();
		Hashtable bandhash = new Hashtable();
		
		List yearlist = new ArrayList();
		List monthlist = new ArrayList();
		
		Hashtable value = new Hashtable();
		
		String ip="";
		String index="";
		String ifname="";
		String time1 ="";
		

		I_HostCollectDataDay daymanager = new HostCollectDataDayManager();						
		//I_HostCollectDataHour hourmanager = new HostCollectDataHourManager();
		
		HostNode hostnode = null;
		try {
			ip = getParaValue("ipaddress");//request.getParameter("ipaddress");
			index = getParaValue("ifindex");//request.getParameter("index");
			ifname = getParaValue("ifname");
			time1 = getParaValue("begindate");
			if(time1 == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
				//mForm.setBegindate(time1);
			}
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadNetwork(1);
	    	if(hostlist != null && hostlist.size()>0){
	    		for(int i=0;i<hostlist.size();i++){
	    			HostNode tempHost = (HostNode)hostlist.get(i);
	    			if(tempHost.getIpAddress().equalsIgnoreCase(ip)){
	    				hostnode = tempHost;
	    				break;
	    			}
	    		}
	    	}
	    	hostdao.close();							
			
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			String newip=doip(ip)+index;
			I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();	
			String[] netIfdetail={"index","ifDescr","ifname","ifType","InCastPkts","OutCastPkts"};
			hash = hostlastmanager.getIfdetail(ip,index,netIfdetail,starttime1,totime1);							
			String[] banden2 = {"ifInMulticastPkts","ifInBroadcastPkts"};
			String[] bandch2 = {"入口多播数据包","入口广播数据包"};							
			Hashtable[] bandhashtable2 = hostmanager.getIfBand_InPacks(ip,index,banden2,bandch2,starttime1,totime1);
			p_drawchartMultiLine(bandhashtable2[0],"入口数据包",newip+"ifpacks",800,200,"Packs");
			imgurlhash.put("ifpacks","resource/image/jfreechart/"+newip+"ifpacks"+".png");
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("ifip",ifip);
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("index",index);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("hostname",hostnode.getAlias());
		request.setAttribute("ifname",ifname);		
		request.setAttribute("begindate", time1);		
	    return "/detail/net_ifinpacks.jsp";
    }
    private String showoutpacks()
    {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();
		Hashtable bandhash = new Hashtable();
		
		List yearlist = new ArrayList();
		List monthlist = new ArrayList();
		
		Hashtable value = new Hashtable();
		
		String ip="";
		String index="";
		String ifname="";
		String time1 ="";
		

		I_HostCollectDataDay daymanager = new HostCollectDataDayManager();						
		//I_HostCollectDataHour hourmanager = new HostCollectDataHourManager();
		
		HostNode hostnode = null;
		try {
			ip = getParaValue("ipaddress");//request.getParameter("ipaddress");
			index = getParaValue("ifindex");//request.getParameter("index");
			ifname = getParaValue("ifname");
			time1 = getParaValue("begindate");
			if(time1 == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
				//mForm.setBegindate(time1);
			}
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadNetwork(1);
	    	if(hostlist != null && hostlist.size()>0){
	    		for(int i=0;i<hostlist.size();i++){
	    			HostNode tempHost = (HostNode)hostlist.get(i);
	    			if(tempHost.getIpAddress().equalsIgnoreCase(ip)){
	    				hostnode = tempHost;
	    				break;
	    			}
	    		}
	    	}
	    	hostdao.close();							
			
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			String newip=doip(ip)+index;
			I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();	
			String[] netIfdetail={"index","ifDescr","ifname","ifType","InCastPkts","OutCastPkts"};
			hash = hostlastmanager.getIfdetail(ip,index,netIfdetail,starttime1,totime1);							
			String[] banden2 = {"ifOutMulticastPkts","ifOutBroadcastPkts"};
			String[] bandch2 = {"出口多播数据包","出口广播数据包"};							
			Hashtable[] bandhashtable2 = hostmanager.getIfBand_OutPacks(ip,index,banden2,bandch2,starttime1,totime1);
			p_drawchartMultiLine(bandhashtable2[0],"出口数据包",newip+"ifpacks",800,200,"Packs");
			imgurlhash.put("ifpacks","resource/image/jfreechart/"+newip+"ifpacks"+".png");
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("ifip",ifip);
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("index",index);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("hostname",hostnode.getAlias());
		request.setAttribute("ifname",ifname);		
		request.setAttribute("begindate", time1);		
	    return "/detail/net_ifoutpacks.jsp";
    }
    
    private String ifdetail()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		
		List yearlist = new ArrayList();
		List monthlist = new ArrayList();
		
		Hashtable value = new Hashtable();
		HostNode hostnode = null;
		
		String ip="";
		String index="";
		String ifname="";

		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();						

		String orderflag = getParaValue("orderflag");
    	if(orderflag == null || orderflag.trim().length()==0)
    		orderflag="index";
        String[] time = {"",""};
        getTime(request,time);
        String starttime = time[0];
        String endtime = time[1];
        
        Hashtable hash = new Hashtable();
        
        String[] netInterfaceItem={"index","ifDescr","ifSpeed","ifOperStatus","OutBandwidthUtilHdx","InBandwidthUtilHdx"};
        try{
        	ip = getParaValue("ipaddress");//request.getParameter("ipaddress");
			index = getParaValue("ifindex");//request.getParameter("index");
			ifname = getParaValue("ifname");
			
			String[] netIfdetail={"index","ifDescr","ifname","ifType","ifMtu","ifSpeed","ifPhysAddress","ifOperStatus"};
			hash = hostlastmanager.getIfdetail_share(ip,index,netIfdetail,starttime,endtime);
			
			HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadNetwork(1);
	    	if(hostlist != null && hostlist.size()>0){
	    		for(int i=0;i<hostlist.size();i++){
	    			HostNode tempHost = (HostNode)hostlist.get(i);
	    			if(tempHost.getIpAddress().equalsIgnoreCase(ip)){
	    				hostnode = tempHost;
	    				break;
	    			}
	    		}
	    	}
	    	hostdao.close();
	    	
        	//vector = hostlastmanager.getInterface_share(hostnode.getIpAddress(),netInterfaceItem,orderflag,starttime,endtime); 
        }catch(Exception e){
        	e.printStackTrace();
        }
		request.setAttribute("index",index);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("hostname",hostnode.getAlias());
		request.setAttribute("ifname",ifname);
		
		request.setAttribute("hash", hash);
		
	    return "/detail/net_ifdetail.jsp";
    }
    
    private String netroute()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
		String ip="";
		String tmp ="";
		try {
			
	    	tmp = request.getParameter("id");
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();				
									
			//从内存中获得当前的跟此IP相关的IP-ROUTE的ARP表信息				
			Hashtable _IpRouterHash = ShareData.getIprouterdata();
			vector = (Vector)_IpRouterHash.get(ip);	
			
			
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData != null){
				Vector cpuV = (Vector)ipAllData.get("cpu");
				if(cpuV != null && cpuV.size()>0){
					
					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				//得到系统启动时间
				Vector systemV = (Vector)ipAllData.get("system");
				if(systemV != null && systemV.size()>0){
					for(int i=0;i<systemV.size();i++){
						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
							sysuptime = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
							sysservices = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
							sysdescr = systemdata.getThevalue();
						}
					}
				}
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
									
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			if (ConnectUtilizationhash.get("avgpingcon")!=null){
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}
			
			
			Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
			if(pingData != null && pingData.size()>0){
				Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
				Date cc = tempCal.getTime();
				collecttime = sdf1.format(cc);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		
	    return "/detail/net_route.jsp";
    }
    
    private String firewallroute()
    {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
		String ip="";
		String tmp ="";
		Host host = null;
		try {
			
	    	tmp = request.getParameter("id");
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();				
									
			//从内存中获得当前的跟此IP相关的IP-ROUTE的ARP表信息				
			Hashtable _IpRouterHash = ShareData.getIprouterdata();
			vector = (Vector)_IpRouterHash.get(ip);	
			
			
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData != null){
				Vector cpuV = (Vector)ipAllData.get("cpu");
				if(cpuV != null && cpuV.size()>0){
					
					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				//得到系统启动时间
				Vector systemV = (Vector)ipAllData.get("system");
				if(systemV != null && systemV.size()>0){
					for(int i=0;i<systemV.size();i++){
						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
							sysuptime = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
							sysservices = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
							sysdescr = systemdata.getThevalue();
						}
					}
				}
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
									
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			if (ConnectUtilizationhash.get("avgpingcon")!=null){
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}
			
			
			Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
			if(pingData != null && pingData.size()>0){
				Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
				Date cc = tempCal.getTime();
				collecttime = sdf1.format(cc);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
//		if(host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.14331.1.4")){
//			return "/detail/firewalltos_route.jsp";
//		}else
			return "/detail/firewall_route.jsp";
	    
    }
    
    
    
    private void draw_blank(String title1,String title2,int w,int h){
    	ChartGraph cg = new ChartGraph();
    	TimeSeries ss = new TimeSeries(title1,Minute.class);
    	TimeSeries[] s = {ss};
    	try{
    		Calendar temp = Calendar.getInstance();
    		Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
    		ss.addOrUpdate(minute,null);
    		SysLogger.info(title1+"==&&&&&&&&&&=="+title2+"=="+w+"=="+h);
    		cg.timewave(s,"x(时间)","y",title1,title2,w,h);
    	}
    	catch(Exception e){e.printStackTrace();}
    }
	private String readyEdit()
	{
	    DaoInterface dao = new HostNodeDao();
	    setTarget("/topology/network/edit.jsp");
        return readyEdit(dao);
	}
	
	private String update()
	{         	  
		HostNode vo = new HostNode();
        vo.setId(getParaIntValue("id"));
        vo.setAlias(getParaValue("alias"));
        vo.setManaged(getParaIntValue("managed")==1?true:false);
        
        //更新内存
 	    Host host = (Host)PollingEngine.getInstance().getNodeByID(vo.getId()); 	   
 	    host.setAlias(vo.getAlias());
 	    host.setManaged(vo.isManaged());
 	     	    
        //更新数据库
 	    DaoInterface dao = new HostNodeDao(); 	    
	    setTarget("/network.do?action=list");
        return update(dao,vo);
    } 
	
	private String refreshsysname()
	{         	  
		HostNodeDao dao = new HostNodeDao();
		String sysName = "";
		sysName = dao.refreshSysName(getParaIntValue("id"));
 	    
        //更新内存
 	    Host host = (Host)PollingEngine.getInstance().getNodeByID(getParaIntValue("id")); 	   
 	    if(host != null){
 	    	host.setSysName(sysName);
 	    	host.setAlias(sysName);
 	    }

 	   return "/network.do?action=list";
    }
	
    private String delete()
    {
        String id = getParaValue("radio"); 
        
        PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));        
        HostNodeDao dao = new HostNodeDao();
        dao.delete(id);       
        return "/network.do?action=list";
    }

	private String neteventdelete() {

		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			// 进行删除
			EventListDao edao = new EventListDao();
			edao.delete(ids);
			edao.close();

		}

		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		EventListDao dao = new EventListDao();
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");
		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;
		request.setAttribute("status", status);
		request.setAttribute("level1", level1);

		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");

		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";
		String sql = "";
		try {
			User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			StringBuffer s = new StringBuffer();
//			s.append("where recordtime>= '" + starttime1 + "' "
//					+ "and recordtime<='" + totime1 + "'");
			if(SystemConstant.DBType.equalsIgnoreCase("mysql")){
				s.append("where recordtime>= '" + starttime1 + "' "
						+ "and recordtime<='" + totime1 + "'");
			}else if(SystemConstant.DBType.equalsIgnoreCase("oracle")){
				s.append("where recordtime>=to_date( '" + starttime1 + "','yyyy-mm-dd hh24:mi:ss') "
						+ "and recordtime<=to_date('" + totime1 + "','yyyy-mm-dd hh24:mi:ss')");
			}
			if (!"99".equals(level1 + "")) {
				s.append(" and level1=" + level1);
			}
			if (!"99".equals(status + "")) {
				s.append(" and managesign=" + status);
			}
			String businessid = vo.getBusinessids();
			int flag = 0;
			if (businessid != null) {
				if (businessid != "-1") {
					String[] bids = businessid.split(",");
					if (bids.length > 0) {
						for (int i = 0; i < bids.length; i++) {
							if (bids[i].trim().length() > 0) {
								if (flag == 0) {
									s.append(" and ( businessid = ',"
											+ bids[i].trim() + ",' ");
									flag = 1;
								} else {
									// flag = 1;
									s.append(" or businessid = ',"
											+ bids[i].trim() + ",' ");
								}
							}
						}
						s.append(") ");
					}

				}
			}
			sql = s.toString();
			sql = sql + " order by id desc";
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		
		setTarget("/monitor.do?action=netevent");
		return list(dao, sql);
	}
    private String add() 
    {  	 	  
	    String ipAddress = getParaValue("ip_address");
	    String alias = getParaValue("alias");
	    String community = getParaValue("community");
	    String writecommunity = getParaValue("writecommunity");
	    int type = getParaIntValue("type");
	    
	    TopoHelper helper = new TopoHelper(); //包括更新数据库和更新内存
	    int addResult = helper.addHost(ipAddress,alias,community,writecommunity,type); //加入一台服务器
	    if(addResult==0)
	    {	  
	        setErrorCode(ErrorMessage.ADD_HOST_FAILURE);
	        return null;      
	    }   
	    if(addResult==-1)
	    {	  
	        setErrorCode(ErrorMessage.IP_ADDRESS_EXIST);
	        return null;      
	    }   	    
	    if(addResult==-2)
	    {	  
	        setErrorCode(ErrorMessage.PING_FAILURE);
	        return null;      
	    }   	    
	    if(addResult==-3)
	    {	  
	        setErrorCode(ErrorMessage.SNMP_FAILURE);
	        return null;      
	    }      
	    
 	    //2.更新xml
	    XmlOperator opr = new XmlOperator();
	    opr.setFile("network.jsp");
	    opr.init4updateXml();
	    opr.addNode(helper.getHost());   
        opr.writeXml();
        
        return "/network.do?action=list";
    }  

    private String find()
    {
	   String key = getParaValue("key");
	   String value = getParaValue("value");	 
	   HostNodeDao dao = new HostNodeDao();
       request.setAttribute("list",dao.findByCondition(key,value));
     
       return "/topology/network/find.jsp";
   }

   private String save()
   {
	   String xmlString = request.getParameter("hidXml");	
	   String vlanString = request.getParameter("vlan");	
	   xmlString = xmlString.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"GB2312\"?>");		
	   XmlOperator xmlOpr = new XmlOperator();
	   if(vlanString != null && vlanString.equals("1")){
		   xmlOpr.setFile("networkvlan.jsp");
	   }else
		   xmlOpr.setFile("network.jsp");
	   xmlOpr.saveImage(xmlString);
	   
	   return "/topology/network/save.jsp";   
   }
   
   private String savevlan()
   {
	   String xmlString = request.getParameter("hidXml");			
	   xmlString = xmlString.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"GB2312\"?>");		
	   XmlOperator xmlOpr = new XmlOperator();
	   xmlOpr.setFile("networkvlan.jsp");
	   xmlOpr.saveImage(xmlString);
	   
	   return "/topology/network/save.jsp";   
   }
   
   private String look_prohis(){
		  
	   String ip=request.getParameter("ipaddress");
	   String pid=request.getParameter("pid");
	   String pname=request.getParameter("pname");
	  
	   	Hashtable imgurlhash=new Hashtable();
//		try {
//			String newip=doip(ip);
//			String[] time = {"",""};
//			getTime(request,time);
//			String starttime = time[0];
//			String endtime = time[1];	
//			String time1 = request.getParameter("begindate");
//			if(time1 == null){
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				time1 = sdf.format(new Date());
//			}
//			String starttime1 = time1 + " 00:00:00";
//			String totime1 = time1 + " 23:59:59";						
//			
//			I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
//			try{
//				Hashtable hash = hostmanager.getCategory(ip,"Process",pid,starttime1,totime1);						
//				p_draw_line(hash,"进程"+pname,newip+"Process",740,150);
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
//			//imgurlhash
//			imgurlhash.put("Process","resource/image/jfreechart/"+newip+"Process"+".png");
//			
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("ip",ip);//yangjun add
		request.setAttribute("pid",pid);//yangjun add
		request.setAttribute("pname",pname);//yangjun add
	   return "/detail/look_prohis.jsp";
   }
   
   private String addHostprocMonflag(){
   	Procs vo=createProcess();
		ProcsDao dao = new ProcsDao();
       try{
       	dao.save(vo);
       }catch(Exception e){
       	e.printStackTrace();
       }finally{
       	dao.close();
       }
       	return hostproc();
   }
   
   private Procs createProcess(){
		
		
		Procs vo = new Procs();
		Calendar cal = new GregorianCalendar();
		
		String flag = getParaValue("flag");
		if(flag == null){
			flag = "1";
		}
		vo.setFlag(Integer.valueOf(flag));
		
		String wbstatus = getParaValue("wbstatus");
		
		if( wbstatus == null){
			wbstatus = flag;
		}
		
		String id = getParaValue("id");
		HostNode hostNode = null;
		HostNodeDao hostNodeDao = new HostNodeDao();
		try {
			hostNode = (HostNode)hostNodeDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			hostNodeDao.close();
		}
		
		vo.setNodeid(hostNode.getId());
		
		vo.setWbstatus(Integer.valueOf(wbstatus));
		
		vo.setIpaddress(hostNode.getIpAddress());
		
		vo.setProcname(getParaValue("procsname"));
		vo.setBak(getParaValue("procsbak"));
		vo.setCollecttime(cal);
		
		return vo;
	}
   
   private String changeHostprocWbstatus()
   {   
		Procs vo=new Procs();
		ProcsDao dao = null;
       try{
       	String id = getParaValue("procid");
       	int wbstatus = getParaIntValue("wbstatus");
       	dao =new ProcsDao();
       	vo = (Procs)dao.findByID(id);
       	vo.setWbstatus(wbstatus);
       	dao =new ProcsDao();
       	dao.update(vo);
       }catch(Exception e){
       	e.printStackTrace();
       }finally{
       	dao.close();
       }
       	return hostproc();
   }
   
   private String changeHostprocMonflag()
   {   
		Procs vo=new Procs();
		ProcsDao dao = null;
       try{
       	String id = getParaValue("procid");
       	int monflag = getParaIntValue("monflag");
       	dao =new ProcsDao();
       	vo = (Procs)dao.findByID(id);
       	vo.setFlag(monflag);
       	vo.setWbstatus(monflag);
       	dao =new ProcsDao();
       	dao.update(vo);
       }catch(Exception e){
       	e.printStackTrace();
       }finally{
       	dao.close();
       }
       	return hostproc();
   }
   
   private String downloadsoftwarereport()
   {
	   Vector softwareV = (Vector) session.getAttribute("softwareV");
	   
		Hashtable reporthash = new Hashtable();
		if (softwareV!=null) {
			reporthash.put("softwareV", softwareV);
		}
		else {
			softwareV = new Vector();
		}
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		
		report.createReport_softwarelist("/temp/softwarelist_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/topology/network/downloadreport.jsp";
   }
   private String downloaddevicereport()
   {
	   Vector deviceV = (Vector) session.getAttribute("deviceV");
	   
		Hashtable reporthash = new Hashtable();
		if (deviceV!=null) {
			reporthash.put("deviceV", deviceV);
		}
		else {
			deviceV = new Vector();
		}
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		
		report.createReport_devicelist("/temp/devicelist_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/topology/network/downloadreport.jsp";
   }
   private String downloadstoragereport()
   {
	   Vector storageV = (Vector) session.getAttribute("storageV");
	   
		Hashtable reporthash = new Hashtable();
		if (storageV!=null) {
			reporthash.put("storageV", storageV);
		}
		else {
			storageV = new Vector();
		}
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		
		report.createReport_storagelist("/temp/storagelist_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/topology/network/downloadreport.jsp";
   }
   
   private String downloadipmacreport()
   {
	   Vector ipmacV = (Vector) session.getAttribute("ipmacV");
	   
		Hashtable reporthash = new Hashtable();
		if (ipmacV!=null) {
			reporthash.put("list", ipmacV);
		}
		else {
			ipmacV = new Vector();
		}
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		report.createReport_ipmacall("/temp/ipmaclist_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/topology/network/downloadreport.jsp";
   }
    
   public String execute(String action){
		if (action.equals("createImage"))
			return createImage();
		if(action.equals("speed")){
			return speed();
		}if(action.equals("hostwindows")){
			return hostwindows();
		}
		if (action.equals("createWord"))
			return createWord();
	  if(action.equals("list"))
	     return list();     
      if(action.equals("netif"))
         return netif(); 
      if(action.equals("netdetail"))
          return netdetail();
      if(action.equals("netinterface"))
          return netinterface();
      if(action.equals("netcpu"))
          return netcpu(); 
      if(action.equals("cpudetail"))//add by zhangcw 2011-3-7 15:29
          return cpudetail(); 
      if(action.equals("memorydetail"))//add by zhangcw 2011-3-10 12:59
          return memorydetail(); 
      if(action.equals("interfacedetail"))//add by zhangcw 2011-3-14 9:27
          return interfacedetail(); 
      if(action.equals("portdetail"))//add by zhangcw 2011-3-15 16:16
          return portdetail(); 
      if(action.equals("bandwidthdetail"))
          return bandwidthdetail(); 
      if(action.equals("netfdb"))
          return netfdb();
      if(action.equals("netenv"))
          return netenv();
      if(action.equals("hostcpu"))
          return hostcpu();
      if(action.equals("hostconfig"))
          return hostconfig();
      if(action.equals("hostutilhdx"))
          return hostutilhdx();
      if(action.equals("hostproc"))
          return hostproc();
      if(action.equals("hostservice"))
          return hostservice();
      if(action.equals("hostsyslog"))
          return hostsyslog();
      if(action.equals("hosterrpt"))
          return hosterrpt();
      if(action.equals("hosterrptDetail"))
          return hosterrptDetail(); 
      if(action.equals("hostarp"))
          return hostarp();
      if(action.equals("hostdevice"))
          return hostdevice();
      if(action.equals("hoststorage"))
          return hoststorage();
      if(action.equals("hostsw"))
          return hostsw();
      if(action.equals("downloadsoftwarereport"))			
		  return downloadsoftwarereport();
      if(action.equals("downloadipmacreport"))
    	  return downloadipmacreport();
      if(action.equals("downloaddevicereport"))
    	  return downloaddevicereport();
      if(action.equals("downloadstoragereport"))
    	  return downloadstoragereport();
      if(action.equals("hostwinservice"))
          return hostwinservice();
      if (action.equals("refresh"))
          return refresh();
      if (action.equals("refreshhostarp"))
          return refreshhostarp();
      if (action.equals("refreshhostdevice"))
          return refreshhostdevice();
      if (action.equals("refreshhoststorage"))
          return refreshhoststorage();
      if(action.equals("insertdb"))
          return insertdb();
      if(action.equals("insertarpdb"))
          return insertarpdb();      
      if(action.equals("hostsyslogdetail"))
          return hostsyslogdetail();
      if(action.equals("netarp"))
          return netarp();
      if(action.equals("netevent"))
          return netevent();
      if(action.equals("hostevent"))
          return hostevent();
      if(action.equals("hosteventlist"))
          return hosteventlist();
      if(action.equals("accit"))
          return accit();
      if(action.equals("accfi"))
          return accfi();
      if(action.equals("fireport"))
          return fireport();
      if(action.equals("doreport"))
          return doreport();
      if (action.equals("gatewayqueue"))
    	  return gatewayqueue();
      if (action.equals("gatewayraid"))
    	  return gatewayraid();
      if (action.equals("gatewayenv"))
    	  return gatewayenv();
      if (action.equals("gatewayevent"))
    	  return gatewayevent();
      if (action.equals("nokiaimage"))
    	  return nokiaimage();
      if (action.equals("nokiaprocess"))
    	  return nokiaprocess();
      if (action.equals("nokiamirror"))
    	  return nokiamirror();
      if (action.equals("nokiaenv"))
    	  return nokiaenv();
      if (action.equals("nokiaevent"))
    	  return nokiaevent();
      if(action.equals("viewreport"))
          return viewreport();
      if(action.equals("show_hostutilhdx"))
          return showhostutilhdx();
      if(action.equals("show_utilhdx"))
          return showutilhdx();
      if(action.equals("read_detail"))
          return ifdetail();
      if(action.equals("show_discardsperc"))
          return showdiscardsperc();
      if(action.equals("show_errorsperc"))
          return showerrorsperc();
      if(action.equals("show_packs"))
          return showpacks();
      if(action.equals("show_inpacks"))
          return showinpacks();
      if(action.equals("show_outpacks"))
          return showoutpacks();
      if(action.equals("netroute"))
          return netroute();
      if(action.equals("netiplist"))
          return netiplist();
      if(action.equals("netping"))
          return netping();
      if(action.equals("hostping"))
          return hostping();
      if(action.equals("hostroute"))
          return hostroute();
	  if(action.equals("ready_edit"))
         return readyEdit();
      if(action.equals("update"))
         return update();  
      if(action.equals("refreshsysname"))
          return refreshsysname();
	  if(action.equals("delete"))
	     return delete();     
	  if(action.equals("neteventdelete"))
	     return neteventdelete();     
      if(action.equals("find"))
         return find();           
	  if(action.equals("ready_add"))
	     return "/topology/network/add.jsp";
      if(action.equals("add"))
         return add();
      if(action.equals("save"))
         return save();
      if(action.equals("netresponsetime_report")){
    	  return netresponsetime_report();
      }
      if(action.equals("hostresponsetime_report")){
    	  return hostResponseTime_report();
      }
      if(action.equals("look_prohis"))
       	 return look_prohis();
      if(action.equals("netcpu_report")){
    	  return netcpu_report();
      }
      if(action.equals("netping_report")){
    	  return netping_report();
      }
      if(action.equals("hostping_report")){
    	  return hostping_report();
      }
      if(action.equals("hostcpu_report")){
    	  return hostcpu_report();
      }
      if(action.equals("hostmemory_report")){
    	  return hostmemory_report();
      }
      if(action.equals("firewallpolicy")){
    	  return firewallpolicy();
      }
      if(action.equals("firewallcpu")){
    	  return firewallcpu();
      }
      if(action.equals("firewallarp")){
    	  return firewallarp();
      }
      if(action.equals("firewallarpproxcy")){
    	  return firewallarpproxcy();
      }
      if(action.equals("firewallvlan")){
    	  return firewallvlan();
      }
      if(action.equals("firewallroute")){
    	  return firewallroute();
      }
      if(action.equals("firewallping")){
    	  return firewallping();
      }
      if(action.equals("firewalliplist")){
    	  return firewalliplist();
      }
      if(action.equals("firewalllogin")){
    	  return firewalllogin();
      }
      if(action.equals("firewallevent")){
    	  return firewallevent();
      }
      if(action.equals("f5poolinfo")){
    	  return f5poolinfo();
      }
      if(action.equals("f5rulesinfo")){
    	  return f5rulesinfo();
      }
      if(action.equals("changeHostprocMonflag")){
    	  return changeHostprocMonflag();
      }
      if(action.equals("changeHostprocWbstatus")){
    	  return changeHostprocWbstatus();
      }
      if(action.equals("addHostprocMonflag")){
    	  return addHostprocMonflag();
      }
      if(action.equals("AS400PoolDetail")){
    	  return AS400PoolDetail();
      
      }
      
      if(action.equals("AS400SystemStatusDetail")){
    	  return AS400SystemStatusDetail();
      
      }
      
      if(action.equals("AS400DiskDetail")){
    	  return AS400DiskDetail();
      
      }
      
      if(action.equals("AS400JobsDetail")){
    	  return AS400JobsDetail();     
      }
      
      if(action.equals("AS400NetworkDetail")){
    	  return AS400NetworkDetail();     
      }
      
      if(action.equals("AS400HardwareDetail")){
    	  return AS400HardwareDetail();     
      }
      
      if(action.equals("AS400ServiceDetail")){
    	  return AS400ServiceDetail();     
      }
      
      if(action.equals("AS400EventDetail")){
    	  return AS400EventDetail();     
      }
      
      if(action.equals("AS400SubsystemDetail")){
    	  return AS400SubstystemDetail();
      
      }
      
      
      if(action.equals("AS400JobsInSubsystemDetail")){
    	  return AS400JobsInSubsystemDetail();
      
      }
      
      if(action.equals("networkview")){
    	  return networkview();
      
      }
      if(action.equals("telnetCfg")){
    	  return telnetCfg();
    	  
      }
      if(action.equals("telnetAcl")){
    	  return telnetAcl();
    	  
      }
      if(action.equals("vpncpu")){
    	  return vpncpu();
      }
      if(action.equals("vpnarp")){
    	Hashtable imgurlhash=new Hashtable();
  		Vector vector = new Vector();
  		Hashtable bandhash = new Hashtable();
  		
  		String ip="";
  		String tmp ="";
  		double cpuvalue = 0;
  		String pingconavg ="0";
  		String time = null;
  		String sysuptime = null;
  		String sysservices = null;
  		String sysdescr = null;
  		try {
  			
  	    	tmp = request.getParameter("id");
  	    	
  	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
  			ip=host.getIpAddress();		
  								
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  		}
  		request.setAttribute("ipaddress",ip);
  		request.setAttribute("id", tmp);
    	  return vpnarp();
      }
      if (action.equals("vpnevent")){
    	  return vpnevent();
      }
      if (action.equals("vpnconfig")){
    	  Hashtable imgurlhash=new Hashtable();
    		Vector vector = new Vector();
    		Hashtable bandhash = new Hashtable();
    		
    		String ip="";
    		String tmp ="";
    		double cpuvalue = 0;
    		String pingconavg ="0";
    		String time = null;
    		String sysuptime = null;
    		String sysservices = null;
    		String sysdescr = null;
    		try {
    			
    	    	tmp = request.getParameter("id");
    	    	
    	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    			ip=host.getIpAddress();		
    								
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		request.setAttribute("ipaddress",ip);
    		request.setAttribute("id", tmp);
    	  return vpnconfig();
      }
      if (action.equals("vpnsession")){
    	  Hashtable imgurlhash=new Hashtable();
    		Vector vector = new Vector();
    		Hashtable bandhash = new Hashtable();
    		
    		String ip="";
    		String tmp ="";
    		double cpuvalue = 0;
    		String pingconavg ="0";
    		String time = null;
    		String sysuptime = null;
    		String sysservices = null;
    		String sysdescr = null;
    		try {
    			
    	    	tmp = request.getParameter("id");
    	    	
    	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    			ip=host.getIpAddress();		
    								
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		request.setAttribute("ipaddress",ip);
    		request.setAttribute("id", tmp);
    	  return vpnsession();
      }
      if (action.equals("vpnconn")){
    	  Hashtable imgurlhash=new Hashtable();
    		Vector vector = new Vector();
    		Hashtable bandhash = new Hashtable();
    		
    		String ip="";
    		String tmp ="";
    		double cpuvalue = 0;
    		String pingconavg ="0";
    		String time = null;
    		String sysuptime = null;
    		String sysservices = null;
    		String sysdescr = null;
    		try {
    			
    	    	tmp = request.getParameter("id");
    	    	
    	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    			ip=host.getIpAddress();		
    								
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		request.setAttribute("ipaddress",ip);
    		request.setAttribute("id", tmp);
    	  return vpnconn();
      }
      if (action.equals("vpnweb")){
    	  Hashtable imgurlhash=new Hashtable();
    		Vector vector = new Vector();
    		Hashtable bandhash = new Hashtable();
    		
    		String ip="";
    		String tmp ="";
    		double cpuvalue = 0;
    		String pingconavg ="0";
    		String time = null;
    		String sysuptime = null;
    		String sysservices = null;
    		String sysdescr = null;
    		try {
    			
    	    	tmp = request.getParameter("id");
    	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    			ip=host.getIpAddress();		
    								
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		request.setAttribute("ipaddress",ip);
    		request.setAttribute("id", tmp);
    	  return vpnweb();
      }
      if(action.equals("datapacket"))//add by jiruifei 2011-3-15 16:16
          return datapacket(); 
         if(action.equals("multicastpacket"))//add by jiruifei 2011-3-15 16:16
          return multicastpacket();
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }
   
   private String vpnconn(){
	   return "/detail/vpn_conn.jsp";
   }
   
   private String vpnsession(){
	   return "/detail/vpn_session.jsp";
   }
   
   private String vpnconfig(){
	   return "/detail/vpn_config.jsp";
   }
   
   private String vpnweb(){
	   return "/detail/vpn_web.jsp";
   }
   private String vpncpu(){
	   return "/detail/vpn_cpu.jsp";
   }

   private String vpnarp(){ 
	   return "/detail/vpn_arp.jsp";
   }
   /*       */   private String gatewayenv() {
	   /*  6516 */     Hashtable imgurlhash = new Hashtable();
	   /*  6517 */     Hashtable hash = new Hashtable();
	   /*  6518 */     Hashtable maxhash = new Hashtable();
	   /*       */ 
	   /*  6520 */     double cpuvalue = 0.0D;
	   /*  6521 */     String pingconavg = "0";
	   /*  6522 */     String collecttime = null;
	   /*  6523 */     String sysuptime = null;
	   /*  6524 */     String sysservices = null;
	   /*  6525 */     String sysdescr = null;
	   /*       */ 
	   /*  6527 */     String tmp = this.request.getParameter("id");
	   /*       */ 
	   /*  6529 */     Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	   /*       */ 
	   /*  6531 */     String newip = doip(host.getIpAddress());
	   /*       */ 
	   /*  6533 */     this.request.setAttribute("imgurl", imgurlhash);
	   /*  6534 */     this.request.setAttribute("hash", hash);
	   /*  6535 */     this.request.setAttribute("max", maxhash);
	   /*  6536 */     this.request.setAttribute("id", tmp);
	   /*  6537 */     this.request.setAttribute("cpuvalue", Double.valueOf(cpuvalue));
	   /*  6538 */     this.request.setAttribute("collecttime", collecttime);
	   /*  6539 */     this.request.setAttribute("sysuptime", sysuptime);
	   /*  6540 */     this.request.setAttribute("sysservices", sysservices);
	   /*  6541 */     this.request.setAttribute("sysdescr", sysdescr);
	   /*  6542 */     this.request.setAttribute("pingconavg", new Double(pingconavg));
	   /*  6543 */     return "/detail/gateway_env.jsp";
	   /*       */   }
	   /*       */ 
	   /*       */   private String gatewayraid() {
	   /*  6547 */     String ip = "";
	   /*  6548 */     String tmp = "";
	   /*  6549 */     double cpuvalue = 0.0D;
	   /*  6550 */     String pingconavg = "0";
	   /*  6551 */     String time = null;
	   /*  6552 */     List list = new ArrayList();
	   /*       */     try
	   /*       */     {
	   /*  6555 */       tmp = this.request.getParameter("id");
	   /*  6556 */       HostNodeDao hostdao = new HostNodeDao();
	   /*  6557 */       List hostlist = hostdao.loadHost();
	   /*       */ 
	   /*  6559 */       Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	   /*  6560 */       ip = host.getIpAddress();
	   /*       */     }
	   /*       */     catch (Exception e) {
	   /*  6563 */       e.printStackTrace();
	   /*       */     }
	   /*  6565 */     this.request.setAttribute("ipaddress", ip);
	   /*  6566 */     this.request.setAttribute("id", tmp);
	   /*  6567 */     return "/detail/gateway_raid.jsp";
	   /*       */   }
	   /*       */ 
	   /*       */   private String nokiaevent() {
	   /*  6571 */     Hashtable imgurlhash = new Hashtable();
	   /*  6572 */     Vector vector = new Vector();
	   /*  6573 */     Hashtable bandhash = new Hashtable();
	   /*       */ 
	   /*  6575 */     String ip = "";
	   /*  6576 */     String tmp = "";
	   /*  6577 */     double cpuvalue = 0.0D;
	   /*  6578 */     String pingconavg = "0";
	   /*  6579 */     String time = null;
	   /*  6580 */     List list = new ArrayList();
	   /*  6581 */     int status = 99;
	   /*  6582 */     int level1 = 99;
	   /*  6583 */     String b_time = "";
	   /*  6584 */     String t_time = "";
	   /*       */     try
	   /*       */     {
	   /*       */       SimpleDateFormat sdf;
	   /*  6587 */       tmp = this.request.getParameter("id");
	   /*  6588 */       status = getParaIntValue("status");
	   /*  6589 */       level1 = getParaIntValue("level1");
	   /*  6590 */       if (status == -1) status = 99;
	   /*  6591 */       if (level1 == -1) level1 = 99;
	   /*  6592 */       this.request.setAttribute("status", Integer.valueOf(status));
	   /*  6593 */       this.request.setAttribute("level1", Integer.valueOf(level1));
	   /*       */ 
	   /*  6595 */       b_time = getParaValue("startdate");
	   /*  6596 */       t_time = getParaValue("todate");
	   /*       */ 
	   /*  6598 */       if (b_time == null) {
	   /*  6599 */         sdf = new SimpleDateFormat("yyyy-MM-dd");
	   /*  6600 */         b_time = sdf.format(new Date());
	   /*       */       }
	   /*  6602 */       if (t_time == null) {
	   /*  6603 */         sdf = new SimpleDateFormat("yyyy-MM-dd");
	   /*  6604 */         t_time = sdf.format(new Date());
	   /*       */       }
	   /*  6606 */       String starttime1 = b_time + " 00:00:00";
	   /*  6607 */       String totime1 = t_time + " 23:59:59";
	   /*       */ 
	   /*  6609 */       HostNodeDao hostdao = new HostNodeDao();
	   /*  6610 */       List hostlist = hostdao.loadHost();
	   /*       */ 
	   /*  6612 */       Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	   /*  6613 */       ip = host.getIpAddress();
	   /*  6614 */       Hashtable ConnectUtilizationhash = new Hashtable();
	   /*       */       try {
	   /*  6616 */         User vo = (User)this.session.getAttribute("current_user");
	   /*       */ 
	   /*  6618 */         EventListDao dao = new EventListDao();
	   /*  6619 */         list = dao.getQuery(starttime1, totime1, status+"", level1+"", vo.getBusinessids(), Integer.valueOf(host.getId()));
	   /*       */       } catch (Exception ex) {
	   /*  6621 */         ex.printStackTrace();
	   /*       */       }
	   /*       */     }
	   /*       */     catch (Exception e) {
	   /*  6625 */       e.printStackTrace();
	   /*       */     }
	   /*  6627 */     this.request.setAttribute("vector", vector);
	   /*  6628 */     this.request.setAttribute("ipaddress", ip);
	   /*  6629 */     this.request.setAttribute("id", tmp);
	   /*  6630 */     this.request.setAttribute("list", list);
	   /*  6631 */     this.request.setAttribute("startdate", b_time);
	   /*  6632 */     this.request.setAttribute("todate", t_time);
	   /*  6633 */     return "/detail/nokia_event.jsp";
	   /*       */   }
	   /*       */ 
	   /*       */   private String nokiaenv() {
	   /*  6637 */     Hashtable imgurlhash = new Hashtable();
	   /*  6638 */     Hashtable hash = new Hashtable();
	   /*  6639 */     Hashtable maxhash = new Hashtable();
	   /*       */ 
	   /*  6641 */     double cpuvalue = 0.0D;
	   /*  6642 */     String pingconavg = "0";
	   /*  6643 */     String collecttime = null;
	   /*  6644 */     String sysuptime = null;
	   /*  6645 */     String sysservices = null;
	   /*  6646 */     String sysdescr = null;
	   /*       */ 
	   /*  6648 */     String tmp = this.request.getParameter("id");
	   /*       */ 
	   /*  6650 */     Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	   /*       */ 
	   /*  6652 */     String newip = doip(host.getIpAddress());
	   /*       */ 
	   /*  6654 */     this.request.setAttribute("imgurl", imgurlhash);
	   /*  6655 */     this.request.setAttribute("hash", hash);
	   /*  6656 */     this.request.setAttribute("max", maxhash);
	   /*  6657 */     this.request.setAttribute("id", tmp);
	   /*  6658 */     this.request.setAttribute("cpuvalue", Double.valueOf(cpuvalue));
	   /*  6659 */     this.request.setAttribute("collecttime", collecttime);
	   /*  6660 */     this.request.setAttribute("sysuptime", sysuptime);
	   /*  6661 */     this.request.setAttribute("sysservices", sysservices);
	   /*  6662 */     this.request.setAttribute("sysdescr", sysdescr);
	   /*  6663 */     this.request.setAttribute("pingconavg", new Double(pingconavg));
	   /*  6664 */     return "/detail/nokia_env.jsp";
	   /*       */   }
	   /*       */ 
	   /*       */   private String nokiamirror() {
	   /*  6668 */     String ip = "";
	   /*  6669 */     String tmp = "";
	   /*  6670 */     double cpuvalue = 0.0D;
	   /*  6671 */     String pingconavg = "0";
	   /*  6672 */     String time = null;
	   /*  6673 */     List list = new ArrayList();
	   /*       */     try
	   /*       */     {
	   /*  6676 */       tmp = this.request.getParameter("id");
	   /*  6677 */       HostNodeDao hostdao = new HostNodeDao();
	   /*  6678 */       List hostlist = hostdao.loadHost();
	   /*       */ 
	   /*  6680 */       Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	   /*  6681 */       ip = host.getIpAddress();
	   /*       */     }
	   /*       */     catch (Exception e) {
	   /*  6684 */       e.printStackTrace();
	   /*       */     }
	   /*  6686 */     this.request.setAttribute("ipaddress", ip);
	   /*  6687 */     this.request.setAttribute("id", tmp);
	   /*  6688 */     return "/detail/nokia_mirror.jsp";
	   /*       */   }
	   /*       */ 
	   /*       */   private String nokiaprocess() {
	   /*  6692 */     String ip = "";
	   /*  6693 */     String tmp = "";
	   /*  6694 */     double cpuvalue = 0.0D;
	   /*  6695 */     String pingconavg = "0";
	   /*  6696 */     String time = null;
	   /*  6697 */     List list = new ArrayList();
	   /*       */     try
	   /*       */     {
	   /*  6700 */       tmp = this.request.getParameter("id");
	   /*  6701 */       HostNodeDao hostdao = new HostNodeDao();
	   /*  6702 */       List hostlist = hostdao.loadHost();
	   /*       */ 
	   /*  6704 */       Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	   /*  6705 */       ip = host.getIpAddress();
	   /*       */     }
	   /*       */     catch (Exception e) {
	   /*  6708 */       e.printStackTrace();
	   /*       */     }
	   /*  6710 */     this.request.setAttribute("ipaddress", ip);
	   /*  6711 */     this.request.setAttribute("id", tmp);
	   /*  6712 */     return "/detail/nokia_process.jsp";
	   /*       */   }
	   /*       */ 
	   /*       */   private String nokiaimage() {
	   /*  6716 */     String ip = "";
	   /*  6717 */     String tmp = "";
	   /*  6718 */     double cpuvalue = 0.0D;
	   /*  6719 */     String pingconavg = "0";
	   /*  6720 */     String time = null;
	   /*  6721 */     List list = new ArrayList();
	   /*       */     try
	   /*       */     {
	   /*  6724 */       tmp = this.request.getParameter("id");
	   /*  6725 */       HostNodeDao hostdao = new HostNodeDao();
	   /*  6726 */       List hostlist = hostdao.loadHost();
	   /*       */ 
	   /*  6728 */       Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	   /*  6729 */       ip = host.getIpAddress();
	   /*       */     }
	   /*       */     catch (Exception e) {
	   /*  6732 */       e.printStackTrace();
	   /*       */     }
	   /*  6734 */     this.request.setAttribute("ipaddress", ip);
	   /*  6735 */     this.request.setAttribute("id", tmp);
	   /*  6736 */     return "/detail/nokia_image.jsp";
	   /*       */   }
	   /*       */ 
	   /*       */   private String gatewayevent() {
	   /*  6740 */     Hashtable imgurlhash = new Hashtable();
	   /*  6741 */     Vector vector = new Vector();
	   /*  6742 */     Hashtable bandhash = new Hashtable();
	   /*       */ 
	   /*  6744 */     String ip = "";
	   /*  6745 */     String tmp = "";
	   /*  6746 */     double cpuvalue = 0.0D;
	   /*  6747 */     String pingconavg = "0";
	   /*  6748 */     String time = null;
	   /*  6749 */     List list = new ArrayList();
	   /*  6750 */     int status = 99;
	   /*  6751 */     int level1 = 99;
	   /*  6752 */     String b_time = "";
	   /*  6753 */     String t_time = "";
	   /*       */     try
	   /*       */     {
	   /*       */       SimpleDateFormat sdf;
	   /*  6756 */       tmp = this.request.getParameter("id");
	   /*  6757 */       status = getParaIntValue("status");
	   /*  6758 */       level1 = getParaIntValue("level1");
	   /*  6759 */       if (status == -1) status = 99;
	   /*  6760 */       if (level1 == -1) level1 = 99;
	   /*  6761 */       this.request.setAttribute("status", Integer.valueOf(status));
	   /*  6762 */       this.request.setAttribute("level1", Integer.valueOf(level1));
	   /*       */ 
	   /*  6764 */       b_time = getParaValue("startdate");
	   /*  6765 */       t_time = getParaValue("todate");
	   /*       */ 
	   /*  6767 */       if (b_time == null) {
	   /*  6768 */         sdf = new SimpleDateFormat("yyyy-MM-dd");
	   /*  6769 */         b_time = sdf.format(new Date());
	   /*       */       }
	   /*  6771 */       if (t_time == null) {
	   /*  6772 */         sdf = new SimpleDateFormat("yyyy-MM-dd");
	   /*  6773 */         t_time = sdf.format(new Date());
	   /*       */       }
	   /*  6775 */       String starttime1 = b_time + " 00:00:00";
	   /*  6776 */       String totime1 = t_time + " 23:59:59";
	   /*       */ 
	   /*  6778 */       HostNodeDao hostdao = new HostNodeDao();
	   /*  6779 */       List hostlist = hostdao.loadHost();
	   /*       */ 
	   /*  6781 */       Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	   /*  6782 */       ip = host.getIpAddress();
	   /*  6783 */       Hashtable ConnectUtilizationhash = new Hashtable();
	   /*       */       try {
	   /*  6785 */         User vo = (User)this.session.getAttribute("current_user");
	   /*       */ 
	   /*  6787 */         EventListDao dao = new EventListDao();
	   /*  6788 */         list = dao.getQuery(starttime1, totime1, status+"", level1+"", vo.getBusinessids(), Integer.valueOf(host.getId()));
	   /*       */       } catch (Exception ex) {
	   /*  6790 */         ex.printStackTrace();
	   /*       */       }
	   /*       */     }
	   /*       */     catch (Exception e) {
	   /*  6794 */       e.printStackTrace();
	   /*       */     }
	   /*  6796 */     this.request.setAttribute("vector", vector);
	   /*  6797 */     this.request.setAttribute("ipaddress", ip);
	   /*  6798 */     this.request.setAttribute("id", tmp);
	   /*  6799 */     this.request.setAttribute("list", list);
	   /*  6800 */     this.request.setAttribute("startdate", b_time);
	   /*  6801 */     this.request.setAttribute("todate", t_time);
	   /*  6802 */     return "/detail/gateway_event.jsp";
	   /*       */   }
	   /*       */ 
	   /*       */   private String gatewayqueue() {
	   /*  6806 */     String ip = "";
	   /*  6807 */     String tmp = "";
	   /*  6808 */     double cpuvalue = 0.0D;
	   /*  6809 */     String pingconavg = "0";
	   /*  6810 */     String time = null;
	   /*  6811 */     List list = new ArrayList();
	   /*       */     try
	   /*       */     {
	   /*  6814 */       tmp = this.request.getParameter("id");
	   /*  6815 */       HostNodeDao hostdao = new HostNodeDao();
	   /*  6816 */       List hostlist = hostdao.loadHost();
	   /*       */ 
	   /*  6818 */       Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	   /*  6819 */       ip = host.getIpAddress();
	   /*       */     }
	   /*       */     catch (Exception e) {
	   /*  6822 */       e.printStackTrace();
	   /*       */     }
	   /*  6824 */     this.request.setAttribute("ipaddress", ip);
	   /*  6825 */     this.request.setAttribute("id", tmp);
	   /*  6826 */     return "/detail/gateway_queue.jsp";
	   /*       */   }
	   /*       */ 
   private String vpnevent()
   {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		List list = new ArrayList();
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		try {
			
	    	tmp = request.getParameter("id");
	    	status = getParaIntValue("status");
	    	level1 = getParaIntValue("level1");
	    	if(status == -1)status=99;
	    	if(level1 == -1)level1=99;
	    	request.setAttribute("status", status);
	    	request.setAttribute("level1", level1);
	    	
	    	b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
	    	
			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";
			
	    	HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadHost();
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();				
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//String time1 = sdf.format(new Date());
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
				//SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime1,totime1,status+"",level1+"",
						vo.getBusinessids(),host.getId());
				
				//ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
	    return "/detail/vpn_event.jsp";
   }
   
   private String networkview(){
	   String flag = request.getParameter("flag"); 
	   String tmp1 = request.getParameter("id"); 
	   Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp1)); 
	   if(host.getSysOid().startsWith("1.3.6.1.4.1.1588.2.1.1")){//博科交换机
		   return "/topology/network/networkview_brocade.jsp?flag="+flag;
	   }
	   return "/topology/network/networkview.jsp?flag="+flag;
   }
   /**
	 * @author zhubinhua
	 * @return
	 * 用createImage()方法生成的图片，来生成word文档，然后提供给客户端下载
	 */
	private String createWord() {
		//确定路径
		String tempDir = "";
		if(request.getParameter("whattype").equals("net")) {
			tempDir = "/FlexImage/net/";
		} else {
			tempDir = "/FlexImage/host/";
		}
		File dirFile = new File(request.getSession().getServletContext().getRealPath("") + tempDir);
		
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.valueOf(getParaValue("id")));
		
		try {
			File file  = new File(dirFile, "Flex.doc");
//System.out.println("dirFile:" + dirFile.getCanonicalPath());
//System.out.println(file.getCanonicalPath());
			
			// 设置纸张大小
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			RtfWriter2.getInstance(document, new FileOutputStream(file));
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("Times-Roman", "",
					BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			com.lowagie.text.Font titleFont = new com.lowagie.text.Font(bfChinese, 24, com.lowagie.text.Font.BOLD);
			// 正文字体风格
			com.lowagie.text.Font contextFont = new com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.BOLD);
			com.lowagie.text.Font contextFont1 = new com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);
			Paragraph title = new Paragraph(host.getAlias() + "性能报表", titleFont);
			
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);
			
			//用于产生报表时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String toTime = sdf.format(date);
			String startTime = sdf1.format(date) + " 00:00:00";
			String reportTime = startTime+ " 到 " + toTime;
			
			//设备的相关信息
			Chunk chunkName = new Chunk("设备名称：", contextFont);
			Chunk chunkIp = new Chunk("设备IP：", contextFont);
			Chunk chunkTime = new Chunk("报表时间：", contextFont);
			Chunk chunkName1 = new Chunk(host.getSysName(), contextFont1);
			Chunk chunkIp1 = new Chunk(host.getIpAddress(), contextFont1);
			Chunk chunkTime1 = new Chunk(reportTime, contextFont1);
			
			Paragraph name = new Paragraph();
			Paragraph ip = new Paragraph();
			Paragraph time = new Paragraph();
			Paragraph nullLine = new Paragraph();
			
			name.add(chunkName);
			name.add(chunkName1);
			ip.add(chunkIp);
			ip.add(chunkIp1);
			time.add(chunkTime);
			time.add(chunkTime1);
			
			//添加到文档中去
			document.add(name);
			document.add(ip);
			document.add(time);
			document.add(nullLine);
			
			String[] imgFileNames = dirFile.list(new FilenameFilter() {
				//过滤掉其他文件
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".jpg");
				}
				
			});
			
			//把图片写入word文档
			Image imgs[] = new Image[imgFileNames.length];
			for(int i=0; i<imgFileNames.length; i++) {
				imgs[i] = Image.getInstance(dirFile.getCanonicalPath() + "/" + imgFileNames[i]);
				document.add(imgs[i]);
			}
			
			document.close();
			
			//转到下载页面
			System.out.println(file.getCanonicalPath());
			request.setAttribute("filename", file.getCanonicalPath());
			return "/topology/network/downloadreport.jsp";
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @author zhubinhua
	 * @return
	 * 在服务器上生成一张Flex图片
	 */
	private String createImage() {
		
		//确定路径
		String tempDir = "";
		if(request.getParameter("whattype").equals("net")) {
			tempDir = "/FlexImage/net/";
		} else {
			tempDir = "/FlexImage/host/";
		}
		String serverFileName = request.getSession().getServletContext()
				.getRealPath("") + tempDir + request.getParameter("name") + ".jpg";
//System.out.println(serverFileName);
		
		//开始生成jpeg文件
		try {
			InputStream is = request.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			BufferedImage bufferedImage = ImageIO.read(bis);
			if (bufferedImage != null) {
				ImageIO.write(bufferedImage, "jpeg", new File(serverFileName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
   
   private String AS400PoolDetail(){
   	hostpingUtil("minute");
	   
	   
   	String id = getParaValue("id");
  	
   	List list =null;
  		SystemPoolForAS400Dao systemPoolForAS400Dao = new SystemPoolForAS400Dao();
  		try {
  			list = systemPoolForAS400Dao.findByNodeid(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			systemPoolForAS400Dao.close();
			   
		}
		
		request.setAttribute("list", list);
	   return "/detail/host_telnet_AS400pool.jsp";
  }
  
   private String AS400SystemStatusDetail(){
   	hostpingUtil("minute");
	   
   	String id = getParaValue("id");
   	
   	List list =null;
   	SystemValueForAS400Dao systemValueForAS400Dao = new SystemValueForAS400Dao();
   	try {
   		list = systemValueForAS400Dao.findByNodeid(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			systemValueForAS400Dao.close();
			   
		}
		Hashtable systemStatushashtable = new Hashtable();
		if(list != null){
			for(int i = 0 ; i < list.size() ; i++){
				SystemValueForAS400 systemValueForAS400 = (SystemValueForAS400)list.get(i);
				systemStatushashtable.put(systemValueForAS400.getCategory(), systemValueForAS400.getValue());
			}
		}
		request.setAttribute("systemStatushashtable", systemStatushashtable);
		return "/detail/host_telnet_AS400SystemStatus.jsp";
   }
  
   private String AS400DiskDetail(){
   	hostpingUtil("minute");
   	
   	String id = getParaValue("id");
   	List list = null;
   	DiskForAS400Dao diskForAS400Dao = new DiskForAS400Dao();
   	try {
   		list = diskForAS400Dao.findByNodeid(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			diskForAS400Dao.close();
		}
		request.setAttribute("list", list);
   	
   	return "/detail/host_telnet_AS400Disk.jsp";
   }
   
   private String AS400ServiceDetail(){
	   	hostpingUtil("minute");
	   	
	   	String id = getParaValue("id");
	   	List list = null;
//	   	DiskForAS400Dao diskForAS400Dao = new DiskForAS400Dao();
//	   	try {
//	   		list = diskForAS400Dao.findByNodeid(id);
//			} catch (RuntimeException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally {
//				diskForAS400Dao.close();
//			}
			request.setAttribute("list", list);
	   	
	   	return "/detail/host_telnet_AS400Service.jsp";
	   }
    private String AS400HardwareDetail(){
	   	hostpingUtil("minute");
	   	
	   	String id = getParaValue("id");
	   	List list = null;
//	   	DiskForAS400Dao diskForAS400Dao = new DiskForAS400Dao();
//	   	try {
//	   		list = diskForAS400Dao.findByNodeid(id);
//			} catch (RuntimeException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally {
//				diskForAS400Dao.close();
//			}
			request.setAttribute("list", list);
	   	
	   	return "/detail/host_telnet_AS400Hardware.jsp";
    }
    private String AS400NetworkDetail(){
	   	hostpingUtil("minute");
	   	
	   	String id = getParaValue("id");
	   	List list = null;
//	   	DiskForAS400Dao diskForAS400Dao = new DiskForAS400Dao();
//	   	try {
//	   		list = diskForAS400Dao.findByNodeid(id);
//			} catch (RuntimeException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally {
//				diskForAS400Dao.close();
//			}
			request.setAttribute("list", list);
	   	
	   	return "/detail/host_telnet_AS400Network.jsp";
    }
   private String AS400JobsDetail(){
//	   hostpingUtil("minute");
	   
	   String id = getParaValue("id");
	   
	   List jobList = getAS400JobList(id);
	   
	   request.setAttribute("jobList", jobList);
	   
	   return "/detail/host_telnet_AS400Jobs.jsp";
    }
   
    private List getAS400JobList(String nodeid){
    	List list = null;
    	JobForAS400Dao jobForAS400Dao = new JobForAS400Dao();
    	try {
    		list = jobForAS400Dao.findByCondition(" where nodeid='" + nodeid + "'" + getAS400JobListSQL());
    	} catch (RuntimeException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
    	} finally {
    		jobForAS400Dao.close();
    	}
    	return list;
   }
   
    private String getAS400JobListSQL(){
	   
	   String name = getParaValue("jobName");
	   
	   String jobSubsystem = (String)request.getAttribute("jobSubsystem");
	   
	   String user = getParaValue("jobUser");
	   
	   String type = getParaValue("jobType");
	   
	   String subtype = getParaValue("jobSubtype");
	   
	   String activestatus = getParaValue("jobActivestatus");
	   
	   String sortField = getParaValue("jobSortField");
	   
	   String sortType = getParaValue("jobSortType");
	   
	   
	   String where = "";
	   
	   if(name != null && name.trim().length() > 0){
		   where = where + " and name like '%" + name + "%'";
	   } 
	   
	   if(user != null && user.trim().length() > 0){
		   where = where + " and user like '%" + user + "%'";
	   }
	   
	   if(jobSubsystem != null && jobSubsystem.trim().length() > 0){
		   where = where + " and subsystem='" + jobSubsystem + "'";
	   }
	   
	   if (type == null || "-1".equals(type)){
		   type = "-1";
	   }else {
		   where = where + " and type='" + type + "'";
	   }
	   
	   if (subtype == null || "-1".equals(subtype)){
		   subtype = "-1";
	   }else {
		   where = where + " and subtype='" + subtype + "'";
	   }
	   
	   if (activestatus == null || "-1".equals(activestatus)){
		   activestatus = "-1";
	   }else {
		   where = where + " and active_status='" + activestatus + "'";
	   }
	   
	   if(sortField == null || sortField.trim().length() == 0){
		   sortField = "name";
	   }
	   
	   if(sortType == null || sortType.trim().length() == 0){
		   sortType = "asc";
	   }
	   String sortField_sql = sortField;
	   if("cpu_used_time".equals(sortField)){
		   sortField_sql = "CONVERT(" + sortField + ", SIGNED)";
	   }
	   
	   where = where + " order by " + sortField_sql + " " + sortType;
	   
	   request.setAttribute("jobName", name);
	   request.setAttribute("jobUser", user);
	   request.setAttribute("jobType", type);
	   request.setAttribute("jobSubtype", subtype);
	   request.setAttribute("jobActivestatus", activestatus);
	   request.setAttribute("jobSortField", sortField);
	   request.setAttribute("jobSortType", sortType);
	   request.setAttribute("jobSubsystem", jobSubsystem);
	   
	   return where;
	} 
   
   
    private String AS400SubstystemDetail(){
    	String id = getParaValue("id");
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.valueOf(id));
    	Hashtable hashtable= (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
    	Hashtable jobHashtable = new Hashtable();
    	if(hashtable!=null){
			List list = (List)hashtable.get("subSystem");
			if(list!=null){
				System.out.println(list.size()+"============list.size(0================");
				for(int i = 0 ; i < list.size(); i++){
					SubsystemForAS400 subsystemForAS400 = (SubsystemForAS400)list.get(i);
					JobForAS400Dao jobForAS400Dao = new JobForAS400Dao();
					List joblist = null;
					try {
						joblist = jobForAS400Dao.findByNodeidAndPath(id, subsystemForAS400.getPath());
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(joblist == null){
						joblist = new ArrayList();
					}
					jobHashtable.put(subsystemForAS400, joblist);
				}
			}
			request.setAttribute("list", list);
		}
    	request.setAttribute("jobHashtable", jobHashtable);
    	return "/detail/host_telnet_AS400Subsystem.jsp";
    }
    
    private String AS400JobsInSubsystemDetail(){
// 	   hostpingUtil("minute");
 	   
 	   String id = getParaValue("id");
 	   
 	   request.setAttribute("jobSubsystem", getParaValue("jobSubsystem"));
 	   
 	   List jobList = getAS400JobList(id);
 	   
 	   request.setAttribute("jobList", jobList);
 	   
 	   return "/detail/host_telnet_AS400JobsInSubsystem.jsp";
     }
   
   private String AS400EventDetail()
   {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		Hashtable processhash = new Hashtable();
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		//String time = null;
		List list = new ArrayList();
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		tmp = request.getParameter("id");
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		
		try{
   	status = getParaIntValue("status");
   	level1 = getParaIntValue("level1");
   	if(status == -1)status=99;
   	if(level1 == -1)level1=99;
   	request.setAttribute("status", status);
   	request.setAttribute("level1", level1);
   	
   	b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
		if (b_time == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
   	
   	HostNodeDao hostdao = new HostNodeDao();
   	List hostlist = hostdao.loadHost();
   	
   	
   	
		String newip=doip(host.getIpAddress());
		String[] time = {"",""};
		getTime(request,time);
		String starttime = time[0];
		String endtime = time[1];	
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}							
		
		String starttime2 = b_time + " 00:00:00";
		String totime2 = t_time + " 23:59:59";
		
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		String[] item = {"CPU"};
		
		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
		try{
			//hash = hostlastmanager.getbyCategories(host.getIpAddress(),item,starttime,endtime);
		}catch(Exception e){
			e.printStackTrace();
		}
		//从collectdata取cpu,内存的历史数据
//		Hashtable cpuhash = new Hashtable();
//		try{
//			cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		//String pingconavg ="";
//		String cpumax="";
//		if(cpuhash.get("max")!=null){
//				cpumax = (String)cpuhash.get("max");
//		}
		try{
			User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
			//SysLogger.info("user businessid===="+vo.getBusinessids());
			EventListDao dao = new EventListDao();
			list = dao.getQuery(starttime2,totime2,status+"",level1+"",
					vo.getBusinessids(),host.getId());
			
			//ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}

		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData != null){
			Vector cpuV = (Vector)ipAllData.get("cpu");
			if(cpuV != null && cpuV.size()>0){
				
				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			//得到系统启动时间
			Vector systemV = (Vector)ipAllData.get("system");
			if(systemV != null && systemV.size()>0){
				for(int i=0;i<systemV.size();i++){
					Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
					if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
						sysuptime = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
						sysservices = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		  //按order将进程信息排序							  
		  String order = "MemoryUtilization";
		  if((request.getParameter("orderflag") != null) &&(!request.getParameter("orderflag").equals(""))){
			order = request.getParameter("orderflag");
		  }
		  try{
			  processhash = hostlastmanager.getProcess_share(host.getIpAddress(),"Process",order,starttime,endtime);
		  }catch(Exception ex){
			  ex.printStackTrace();
		  }
		  String[] _item = {"User"};
		  try{
			  userhash = hostlastmanager.getbyCategories_share(host.getIpAddress(),_item,starttime,endtime);
		  }catch(Exception ex){
			  ex.printStackTrace();
		  }
		  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Hashtable ConnectUtilizationhash = new Hashtable();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if (ConnectUtilizationhash.get("avgpingcon")!=null){
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}
		
		
		Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
		if(pingData != null && pingData.size()>0){
			Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
			Calendar tempCal = (Calendar)pingdata.getCollecttime();							
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}
		
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("list",list);
		
		request.setAttribute("hash",hash);
		request.setAttribute("userhash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("processhash",processhash);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "/detail/host_telnet_AS400Event.jsp";
	    
   }
   
   private String netcpu()
   {
   	
   	netcpuUtil("minute");
   	
	    return "/detail/net_cpu.jsp";
   }
   
   private String cpudetail()
   {
	    return "/detail/cpudetail.jsp";
   }
   private String memorydetail()
   {
	    return "/detail/memorydetail.jsp";
   }
   private String interfacedetail()
   {
	    return "/detail/interfacedetail.jsp";
   }
   private String portdetail()
   {
	    return "/detail/portdetail.jsp";
   }
   private String bandwidthdetail()
   {
	    return "/detail/bandwidthdetail.jsp";
   }
   //追加多播数据包
   private String multicastpacket()
   {
	    return "/detail/multicastpacket.jsp";
   }
   //追加广播数据包
   private String datapacket()
   {
	    return "/detail/datapacket.jsp";
   }
   private String firewallcpu()
   {
   	
   		netcpuUtil("minute");
   		String tmp = request.getParameter("id");
   		Host host = (Host)getHostById(tmp);
//   		if(host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.14331.1.4")){
//   			//天融信防火墙
//   			return "/detail/firewalltos_cpu.jsp";
//   		}else{
   			return "/detail/firewall_cpu.jsp";
   		//}
	    
   }
   
   private String netcpu_report(){
   	String timeType = getParaValue("timeType");
   	if(timeType==null){
   		timeType = "minute";
   	}
   	netcpuUtil(timeType);
   	request.setAttribute("timeType", timeType);
   	return "/detail/netcpu_report.jsp";
   }
   
   private String netresponsetime_report(){
   	String timeType = request.getParameter("timeType");
   	if(timeType == null){
   		timeType = "minute";
   	}
   	netpingUtil(timeType);
   	return "/detail/netresponsetime_report.jsp";
   }
   
   private String hostResponseTime_report(){
   	String timeType = request.getParameter("timeType");
   	if(timeType == null){
   		timeType = "minute";
   	}
   	hostpingUtil(timeType);
   	return "/detail/hostresponsetime_report.jsp";
   }
   
   private void netcpuUtil(String timeType){
	   String returnStr = "";
	   String showpngPath = "";
	   String showcpupngPath = "";
	   String showresponsepngPath = "";
	   String showrpingpngPath = "";
	   
   		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		
		String pingconavg ="0";
		String collecttime = null;
		
		String tmp = request.getParameter("id");
   		Host host = (Host)getHostById(tmp);
   	
		String newip=doip(host.getIpAddress());

		
		Hashtable value = new Hashtable();
		I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
		//获取上下行流量的最大值和平均口值
		Hashtable allavgandmaxHash = new Hashtable();
		try{
			if(host.getCollecttype() != 3 && host.getCategory() != 9)
			allavgandmaxHash = daymanager.getAllAvgAndMaxHisHdx(host.getIpAddress(),getStartTime(),getToTime());
		}catch(Exception e){
			
		}
		
		String unit = "kb/s";
		String title = "当天24小时端口流速归档";
		String[] banden3 = {"AllInBandwidthUtilHdx","AllOutBandwidthUtilHdx"};
		String[] bandch3 = {"入口流速","出口流速"};
		String[] bandch4 = {"入口流速(kb/s)","出口流速(kb/s)"};
        String reportname = title + "日报表";
        String category = "AllUtilHdx";
        String url1 = "";
        String allutilStr = "";
//		try{
//			//按分钟显示报表
//			value = daymanager.getmultiHisHdx(host.getIpAddress(),"all","",banden3,bandch3,getStartTime(),getToTime(),"AllUtilHdx");
//			reportname = title+getStartTime()+"至"+getToTime()+"报表(按分钟显示)";
//	        p_drawchartMultiLineMonth(value,reportname,newip+category+"_month",800,200,"AllUtilHdx");							
//			url1 = "resource/image/jfreechart/"+newip+category+"_month.png";
//			allutilStr = area_chooseDrawMultiLineType(timeType,value, "综合流速",bandch3,bandch4, 350, 200,100000,100000);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		imgurlhash.put("allutilhdx",url1);
		
		//从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try{
			if(host.getCollecttype() != 3 && host.getCategory() != 9)
				cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",getStartTime(),getToTime());
		}catch(Exception e){
			e.printStackTrace();
		}
		//String pingconavg ="";
		
		//cpu最大值
		maxhash = new Hashtable();
		String cpumax="";
		if(cpuhash.get("max")!=null){
				cpumax = (String)cpuhash.get("max");
		}
		maxhash.put("cpu",cpumax);
		//cpu平均值
		//maxhash = new Hashtable();
		String cpuavg="";
		if(cpuhash.get("avgcpucon")!=null){
				cpuavg = (String)cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg",cpuavg);
		
		//画图
//		try{
//			chooseDrawLineType(timeType ,cpuhash,"",newip+"cpuBy"+timeType,740,120);
//		}catch(Exception e){
//			//e.printStackTrace();
//		}
		p_draw_line(cpuhash,"CPU利用率曲线图",newip+"cpuhistory",300,200);
		imgurlhash.put("cpu", "resource/image/jfreechart/"+newip+"cpuBy"+timeType+".png");
		
		collecttime = getCollectTime(host);
		
		
//		Hashtable ConnectUtilizationhash = new Hashtable();
//		try{
//			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",getStartTime(),getToTime());
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
		
//		Hashtable ResponseTimehash = new Hashtable();
//		try{
//			ResponseTimehash = hostmanager.getCategory(host.getIpAddress(),"Ping","ResponseTime",getStartTime(),getToTime());
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
		
//		String[] bandch = {"连通率","响应时间"};
//		String[] bandch1 = {"连通率(%)","响应时间(ms)"};
//		returnStr = area_chooseDrawLineType(timeType,ConnectUtilizationhash,ResponseTimehash, bandch, bandch1, 350, 200,110,20);
		
		//生成连通率图形
		try{
//			RrdDb.setLockMode(RrdDb.NO_LOCKS);
//			long START = Util.getTimestamp(2010, 9, 16);
//			long END = Util.getTimestamp(2010, 9, 17);
//			long startMillis = System.currentTimeMillis();
//			long start = START;
//			long end = END;
//			//String rrdPath = Util.getJRobinDemoPath(FILE + ".rrd");
//			String rrdPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/ping.rrd";
//			SysLogger.info(rrdPath);
//			String xmlPath = Util.getJRobinDemoPath("image/jfreechart/"+newip+"pingBy"+timeType+".xml");
//			SysLogger.info(xmlPath);
//			String rrdRestoredPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/ping_restored.rrd";
//			String pngPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"ping"+timeType+".png";
//			//String jpegPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"response"+timeType+".jpeg";
//			//String gifPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"response"+timeType+".gif";		
//			showrpingpngPath = "resource/image/jfreechart/"+newip+"ping"+timeType+".png";	
//			Hashtable valueHash = new Hashtable();
//			Hashtable inHash = new Hashtable();
//			Hashtable outHash = new Hashtable();
//			List inkeylist = new ArrayList();
//			List outkeylist = new ArrayList();
//			//设置图形的开始时间,默认情况是今天零点开始
//			List list = new ArrayList();
//			if(ConnectUtilizationhash != null && ConnectUtilizationhash.size()>0 && ConnectUtilizationhash.containsKey("list")){
//				list = (List)ConnectUtilizationhash.get("list");
//				if(list != null && list.size()>0){
//					Vector v = (Vector)list.get(0);
//					Double	d=new Double((String)v.get(0));			
//					String dt = (String)v.get(1);
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					Date time1 = sdf.parse(dt);				
//					Calendar temp = Calendar.getInstance();
//					temp.setTime(time1);
//					start = (temp.getTimeInMillis()-500L)/1000L;
//					SimpleDateFormat ssdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					Date stime = ssdf.parse(getStartTime());				
//					Calendar stemp = Calendar.getInstance();
//					stemp.setTime(stime);
//					start = (stemp.getTimeInMillis()-500L)/1000L;
//				}
//			}
//			if(list == null)list = new ArrayList();
//
//			// creation
//			RrdDef rrdDef = new RrdDef(rrdPath, start - 1, 300);
//			//结束时间设置为当前时间
//			end = org.jrobin.core.Util.getTimestamp();
//			rrdDef.addDatasource("ping", "GAUGE", 600, 0, Double.NaN);
//			//rrdDef.addDatasource("out", "GAUGE", 600, 0, Double.NaN);
//			//rrdDef.addDatasource("lim1", "GAUGE", 600, 0, Double.NaN);		
//			rrdDef.addArchive("AVERAGE", 0.5, 1, 288);
//			rrdDef.addArchive("AVERAGE", 0.5, 7, 288);
//			rrdDef.addArchive("AVERAGE", 0.5, 24, 775);
//			rrdDef.addArchive("AVERAGE", 0.5, 288, 797);
//			rrdDef.addArchive("MAX", 0.5, 1, 288);
//			rrdDef.addArchive("MAX", 0.5, 6, 288);
//			rrdDef.addArchive("MAX", 0.5, 24, 775);
//			rrdDef.addArchive("MAX", 0.5, 288, 797);
//			RrdDb rrdDb = new RrdDb(rrdDef);
//
//			long t = start; int n = 0;
//			
//			Sample responsesample = rrdDb.createSample();
//
//			if(list.size()!=0){
//				try{
//					try{
//						//long ti = org.jrobin.core.Util.getTimestamp();
//					//if(inHash != null && inHash.size()>0){
//						for(int i=0;i<list.size();i++){
//							Vector v = (Vector)list.get(i);
//			    			Double	d=new Double((String)v.get(0));			
//			    			String dt = (String)v.get(1);
//			    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			    			Date time1 = sdf.parse(dt);				
//			    			Calendar temp = Calendar.getInstance();
//			    			temp.setTime(time1);
//			    			responsesample.setTime(temp.getTimeInMillis()/1000L);
//			    			responsesample.setValue("ping", d.doubleValue());
//							//SysLogger.info(responsesample.getTime()+"====="+d.doubleValue());
//							responsesample.update();
//						}
//					//}
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//			}
//			rrdDb.close();
//			// create graph
//			RrdGraphDef gDef = new RrdGraphDef();
//			gDef.setTimePeriod(start, end);
//			gDef.setCanvasColor(Color.WHITE);
//			gDef.setBackColor(Color.WHITE);
//			gDef.setFrameColor(Color.WHITE);
//			gDef.setImageBorder(Color.LIGHT_GRAY, 2);
//	        gDef.setTitle("连通率");
//			gDef.setVerticalLabel("连通率(%)");
//			gDef.datasource("ping", rrdPath, "ping", "AVERAGE");
//			//gDef.datasource("out", rrdPath, "out", "AVERAGE");
//			gDef.area("ping", new Color(0, 206,209), "连通率");
//			//gDef.line("out", Color.BLUE, "下行\n");
//			//gDef.line("lim1", Color.YELLOW, "一级阀值\n");
//			gDef.setChartLeftPadding(30);
//			
//			gDef.gprint("ping", "MAX", "最大 = @3@s");
//			gDef.gprint("ping", "AVERAGE", "平均 = @3@S\n");
//			//gDef.gprint("out", "MAX", "最大 = @3@S");
//			//gDef.gprint("out", "AVERAGE", "平均 = @3@S");
//			//如果需要显示中文，以下两条语句比较重要
//			gDef.setDefaultFont(new Font("Monospaced", Font.PLAIN, 11));
//			gDef.setTitleFont(new Font("Monospaced", Font.BOLD, 14)); 
//			// create graph finally
//			RrdGraph graph = new RrdGraph(gDef);
//			graph.saveAsPNG(pngPath, 300, 150);
//			//graph.saveAsJPEG(jpegPath, 300, 150, 0.5F);
//			//graph.saveAsGIF(gifPath, 300, 150);
//			// demo ends
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//生成响应时间图形
		try{
//			RrdDb.setLockMode(RrdDb.NO_LOCKS);
//			long START = Util.getTimestamp(2010, 9, 16);
//			long END = Util.getTimestamp(2010, 9, 17);
//			long startMillis = System.currentTimeMillis();
//			long start = START;
//			long end = END;
//			//String rrdPath = Util.getJRobinDemoPath(FILE + ".rrd");
//			String rrdPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/response.rrd";
//			SysLogger.info(rrdPath);
//			String xmlPath = Util.getJRobinDemoPath("image/jfreechart/"+newip+"responseBy"+timeType+".xml");
//			SysLogger.info(xmlPath);
//			String rrdRestoredPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/response_restored.rrd";
//			String pngPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"response"+timeType+".png";
//			//String jpegPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"response"+timeType+".jpeg";
//			//String gifPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"response"+timeType+".gif";		
//			showresponsepngPath = "resource/image/jfreechart/"+newip+"response"+timeType+".png";	
//			Hashtable valueHash = new Hashtable();
//			Hashtable inHash = new Hashtable();
//			Hashtable outHash = new Hashtable();
//			List inkeylist = new ArrayList();
//			List outkeylist = new ArrayList();
//			//设置图形的开始时间,默认情况是今天零点开始
//			List list = new ArrayList();
//			if(ResponseTimehash != null && ResponseTimehash.size()>0 && ResponseTimehash.containsKey("list")){
//				list = (List)ResponseTimehash.get("list");
//				if(list != null && list.size()>0){
//					Vector v = (Vector)list.get(0);
//					Double	d=new Double((String)v.get(0));			
//					String dt = (String)v.get(1);
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					Date time1 = sdf.parse(dt);				
//					Calendar temp = Calendar.getInstance();
//					temp.setTime(time1);
//					start = (temp.getTimeInMillis()-500L)/1000L;
//					SimpleDateFormat ssdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					Date stime = ssdf.parse(getStartTime());				
//					Calendar stemp = Calendar.getInstance();
//					stemp.setTime(stime);
//					start = (stemp.getTimeInMillis()-500L)/1000L;
//				}
//			}
//			if(list == null)list = new ArrayList();
//
//			// creation
//			RrdDef rrdDef = new RrdDef(rrdPath, start - 1, 300);
//			//结束时间设置为当前时间
//			end = org.jrobin.core.Util.getTimestamp();
//			rrdDef.addDatasource("response", "GAUGE", 600, 0, Double.NaN);
//			//rrdDef.addDatasource("out", "GAUGE", 600, 0, Double.NaN);
//			//rrdDef.addDatasource("lim1", "GAUGE", 600, 0, Double.NaN);		
//			rrdDef.addArchive("AVERAGE", 0.5, 1, 288);
//			rrdDef.addArchive("AVERAGE", 0.5, 7, 288);
//			rrdDef.addArchive("AVERAGE", 0.5, 24, 775);
//			rrdDef.addArchive("AVERAGE", 0.5, 288, 797);
//			rrdDef.addArchive("MAX", 0.5, 1, 288);
//			rrdDef.addArchive("MAX", 0.5, 6, 288);
//			rrdDef.addArchive("MAX", 0.5, 24, 775);
//			rrdDef.addArchive("MAX", 0.5, 288, 797);
//			RrdDb rrdDb = new RrdDb(rrdDef);
//
//			long t = start; int n = 0;
//			
//			Sample responsesample = rrdDb.createSample();
//
//			if(list.size()!=0){
//				try{
//					try{
//						//long ti = org.jrobin.core.Util.getTimestamp();
//					//if(inHash != null && inHash.size()>0){
//						for(int i=0;i<list.size();i++){
//							Vector v = (Vector)list.get(i);
//			    			Double	d=new Double((String)v.get(0));			
//			    			String dt = (String)v.get(1);
//			    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			    			Date time1 = sdf.parse(dt);				
//			    			Calendar temp = Calendar.getInstance();
//			    			temp.setTime(time1);
//			    			responsesample.setTime(temp.getTimeInMillis()/1000L);
//			    			responsesample.setValue("response", d.doubleValue());
//							SysLogger.info(responsesample.getTime()+"====="+d.doubleValue());
//							responsesample.update();
//						}
//					//}
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//			}
//			rrdDb.close();
//			// create graph
//			RrdGraphDef gDef = new RrdGraphDef();
//			gDef.setTimePeriod(start, end);
//			gDef.setCanvasColor(Color.WHITE);
//			gDef.setBackColor(Color.WHITE);
//			gDef.setFrameColor(Color.WHITE);
//			gDef.setImageBorder(Color.LIGHT_GRAY, 2);
//	        gDef.setTitle("响应时间");
//			gDef.setVerticalLabel("毫秒");
//			gDef.datasource("response", rrdPath, "response", "AVERAGE");
//			//gDef.datasource("out", rrdPath, "out", "AVERAGE");
//			gDef.area("response", new Color(0, 206,209), "响应时间");
//			//gDef.line("out", Color.BLUE, "下行\n");
//			//gDef.line("lim1", Color.YELLOW, "一级阀值\n");
//			gDef.setChartLeftPadding(30);
//			
//			gDef.gprint("response", "MAX", "最大 = @3@s");
//			gDef.gprint("response", "AVERAGE", "平均 = @3@S\n");
//			//gDef.gprint("out", "MAX", "最大 = @3@S");
//			//gDef.gprint("out", "AVERAGE", "平均 = @3@S");
//			//如果需要显示中文，以下两条语句比较重要
//			gDef.setDefaultFont(new Font("Monospaced", Font.PLAIN, 11));
//			gDef.setTitleFont(new Font("Monospaced", Font.BOLD, 14)); 
//			// create graph finally
//			RrdGraph graph = new RrdGraph(gDef);
//			graph.saveAsPNG(pngPath, 300, 150);
//			//graph.saveAsJPEG(jpegPath, 300, 150, 0.5F);
//			//graph.saveAsGIF(gifPath, 300, 150);
//			// demo ends
			
		}catch(Exception e){
			e.printStackTrace();
		}
		

		try{
			
//		RrdDb.setLockMode(RrdDb.NO_LOCKS);
//		long START = Util.getTimestamp(2010, 9, 16);
//		long END = Util.getTimestamp(2010, 9, 17);
//		long startMillis = System.currentTimeMillis();
//		long start = START;
//		long end = END;
//		//String rrdPath = Util.getJRobinDemoPath(FILE + ".rrd");
//		String rrdPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/demo.rrd";
//		SysLogger.info(rrdPath);
//		String xmlPath = Util.getJRobinDemoPath("image/jfreechart/"+newip+"utilBy"+timeType+".xml");
//		SysLogger.info(xmlPath);
//		String rrdRestoredPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/demo_restored.rrd";
//		String pngPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"util"+timeType+"demo.png";
//		String jpegPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"util"+timeType+"demo.jpeg";
//		String gifPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"util"+timeType+"demo.gif";
//		
//		showpngPath = "resource/image/jfreechart/"+newip+"util"+timeType+"demo.png";
//		
//		
//		Hashtable valueHash = new Hashtable();
//		Hashtable inHash = new Hashtable();
//		Hashtable outHash = new Hashtable();
//		List inkeylist = new ArrayList();
//		List outkeylist = new ArrayList();
//		
//		String[] keys = (String[])value.get("key");
//		ChartGraph cg = new ChartGraph();
//		for(int i=0; i<keys.length; i++){
//			String key = keys[i];
//
//			String[] values = (String[])value.get(key);	
//			//流速
//			for(int j=0; j<values.length; j++){			
//				String val = values[j];
//				if (val!=null && val.indexOf("&")>=0){	
//					try{
//					String[] splitstr = val.split("&");
//					String splittime = splitstr[0];				
//					Double	v=new Double(splitstr[1]);			
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					Date da = sdf.parse(splittime);
//					Calendar tempCal = Calendar.getInstance();
//					tempCal.setTime(da);
//					if(key.equalsIgnoreCase("入口流速")){
//						inHash.put(tempCal.getTimeInMillis()/1000L+"", v);
//						inkeylist.add(tempCal.getTimeInMillis()/1000L+"");
//					}
//					if(key.equalsIgnoreCase("出口流速")){
//						outHash.put(tempCal.getTimeInMillis()/1000L+"", v);
//						outkeylist.add(tempCal.getTimeInMillis()/1000L+"");
//					}
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//			}
//		}

//		// creation
//		RrdDef rrdDef = new RrdDef(rrdPath, Long.parseLong((String)inkeylist.get(0)) - 1, 30);
//		SimpleDateFormat ssdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date stime = ssdf.parse(getStartTime());				
//		Calendar stemp = Calendar.getInstance();
//		stemp.setTime(stime);
//		start = (stemp.getTimeInMillis()-500L)/1000L;
//		
//		//start = Long.parseLong((String)inkeylist.get(0));
//		end = org.jrobin.core.Util.getTimestamp();
//		rrdDef.addDatasource("in", "GAUGE", 30000, 0, Double.NaN);
//		rrdDef.addDatasource("out", "GAUGE", 30000, 0, Double.NaN);
//		//rrdDef.addDatasource("lim1", "GAUGE", 600, 0, Double.NaN);		
//		rrdDef.addArchive("AVERAGE", 0.5, 1, 288);
//		rrdDef.addArchive("AVERAGE", 0.5, 7, 288);
//		rrdDef.addArchive("AVERAGE", 0.5, 24, 775);
//		rrdDef.addArchive("AVERAGE", 0.5, 288, 797);
//		rrdDef.addArchive("MAX", 0.5, 1, 288);
//		rrdDef.addArchive("MAX", 0.5, 6, 288);
//		rrdDef.addArchive("MAX", 0.5, 24, 775);
//		rrdDef.addArchive("MAX", 0.5, 288, 797);
//		RrdDb rrdDb = new RrdDb(rrdDef);
//
//		long t = start; int n = 0;
//		
//		Sample sample = rrdDb.createSample();
//
//		if(value.size()!=0){
//			try{
//				try{
//					long ti = org.jrobin.core.Util.getTimestamp();
//				if(inHash != null && inHash.size()>0){
//					for(int i=0;i<inkeylist.size();i++){
//						String timestr = (String)inkeylist.get(i);
//						long times = Long.parseLong(timestr);
//						sample.setTime(times);
//						sample.setValue("in", ((Double)inHash.get(timestr)).doubleValue()*1000L);//sunSource.getValue());
//						sample.setValue("out", ((Double)outHash.get(timestr)).doubleValue()*1000L);//shadeSource.getValue());
//						SysLogger.info(sample.getTime()+"=====in: "+((Double)inHash.get(timestr)).doubleValue()+"  out: "+((Double)outHash.get(timestr)).doubleValue());
//						//sample.setValue("lim1", 1500L);//shadeSource.getValue());
//						sample.update();
//					}
//				}
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
//		rrdDb.close();
//		// create graph
//		RrdGraphDef gDef = new RrdGraphDef();
//		gDef.setTimePeriod(start, end);
//		gDef.setCanvasColor(Color.WHITE);
//		gDef.setBackColor(Color.WHITE);
//		gDef.setFrameColor(Color.WHITE);
//		gDef.setImageBorder(Color.LIGHT_GRAY, 2);
//        gDef.setTitle("综合流速");
//		gDef.setVerticalLabel("流 速");
//		gDef.datasource("in", rrdPath, "in", "AVERAGE");
//		gDef.datasource("out", rrdPath, "out", "AVERAGE");
//		gDef.area("in", new Color(0, 206,209), "上行");
//		gDef.line("out", Color.BLUE, "下行\n");
//		//gDef.line("lim1", Color.YELLOW, "一级阀值\n");
//		gDef.setChartLeftPadding(30);
//		
//		gDef.gprint("in", "MAX", "最大 = @3@s");
//		gDef.gprint("in", "AVERAGE", "平均 = @3@S\n");
//		gDef.gprint("out", "MAX", "最大 = @3@S");
//		gDef.gprint("out", "AVERAGE", "平均 = @3@S");
//		//如果需要显示中文，以下两条语句比较重要
//		gDef.setDefaultFont(new Font("Monospaced", Font.PLAIN, 11));
//		gDef.setTitleFont(new Font("Monospaced", Font.BOLD, 14)); 
//		// create graph finally
//		RrdGraph graph = new RrdGraph(gDef);
//		graph.saveAsPNG(pngPath, 300, 150);
//		//graph.saveAsJPEG(jpegPath, 300, 150, 0.5F);
//		//graph.saveAsGIF(gifPath, 300, 150);
//		// demo ends
//		
//		
//		
//		
//		//生成CPU图形
//		//RrdDb.setLockMode(RrdDb.NO_LOCKS);
//		rrdPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/cpudemo.rrd";
//		SysLogger.info(rrdPath);
//		xmlPath = Util.getJRobinDemoPath("image/jfreechart/"+newip+"cpuBy"+timeType+".xml");
//		SysLogger.info(xmlPath);
//		rrdRestoredPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/cpu_restored.rrd";
//		pngPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"cpu"+timeType+"demo.png";
////		String jpegPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"util"+timeType+"demo.jpeg";
////		String gifPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"util"+timeType+"demo.gif";
//		
//		//pngPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"cpu"+timeType+"demo.png";
//		//String jpegPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"util"+timeType+"demo.jpeg";
//		//String gifPath = ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/"+newip+"util"+timeType+"demo.gif";
//		
//		showcpupngPath = "resource/image/jfreechart/"+newip+"cpu"+timeType+"demo.png";
//		
//		//String[] keys = (String[])value.get("key");
//		//cg = new ChartGraph();
//		List list = (List)cpuhash.get("list");
//		if(list!=null && list.size()>0){
//			Vector v = (Vector)list.get(0);
//			Double	d=new Double((String)v.get(0));			
//			String dt = (String)v.get(1);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Date time1 = sdf.parse(dt);				
//			Calendar temp = Calendar.getInstance();
//			temp.setTime(time1);
//			//start = (temp.getTimeInMillis()-500L)/1000L;
//			ssdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			stime = ssdf.parse(getStartTime());				
//			stemp = Calendar.getInstance();
//			stemp.setTime(stime);
//			start = (stemp.getTimeInMillis()-500L)/1000L;
//		}
//		// creation
//		RrdDef rrdDef1 = new RrdDef(rrdPath, start - 1, 300);
//		end = org.jrobin.core.Util.getTimestamp();
//		rrdDef1.addDatasource("cpu", "GAUGE", 600, 0, Double.NaN);		
//		rrdDef1.addArchive("AVERAGE", 0.5, 1, 288);
//		rrdDef1.addArchive("AVERAGE", 0.5, 7, 288);
//		rrdDef1.addArchive("AVERAGE", 0.5, 24, 775);
//		rrdDef1.addArchive("AVERAGE", 0.5, 288, 797);
//		rrdDef1.addArchive("MAX", 0.5, 1, 288);
//		rrdDef1.addArchive("MAX", 0.5, 6, 288);
//		rrdDef1.addArchive("MAX", 0.5, 24, 775);
//		rrdDef1.addArchive("MAX", 0.5, 288, 797);
//		RrdDb rrdDb1 = new RrdDb(rrdDef1);
//		
//		//sample = null;
//		Sample sample1 = rrdDb1.createSample();
//		
//		
//    	try{
//	    	if(list==null || list.size()==0){
//	    	}
//	    	else{
//		    	unit="%";
//		    	for(int j=0; j<list.size(); j++){
//		    			Vector v = (Vector)list.get(j);
//		    			Double	d=new Double((String)v.get(0));			
//		    			String dt = (String)v.get(1);
//		    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		    			Date time1 = sdf.parse(dt);				
//		    			Calendar temp = Calendar.getInstance();
//		    			temp.setTime(time1);
//		    			sample1.setTime(temp.getTimeInMillis()/1000L);
//						sample1.setValue("cpu", d.doubleValue());
//						SysLogger.info(sample1.getTime()+"====="+d.doubleValue());
//						sample1.update();		
//		    	}
//	    	}
//	    	hash = null;
//	    }catch(Exception e){
//    		e.printStackTrace();
//    	}
//	    
//		rrdDb1.close();
//		// create graph
//		gDef = new RrdGraphDef();
//		gDef.setTimePeriod(start, end);
//		gDef.setCanvasColor(Color.WHITE);
//		gDef.setBackColor(Color.WHITE);
//		gDef.setFrameColor(Color.WHITE);
//		gDef.setImageBorder(Color.LIGHT_GRAY, 2);
//        gDef.setTitle("CPU利用率");
//		gDef.setVerticalLabel("值");
//		gDef.datasource("cpu", rrdPath, "cpu", "AVERAGE");
//		gDef.area("cpu", new Color(0, 206,209), "CPU利用率");
//		gDef.setChartLeftPadding(30);
//		
//		gDef.gprint("cpu", "MAX", "最大 = @3@s");
//		gDef.gprint("cpu", "AVERAGE", "平均 = @3@S\n");
//		//如果需要显示中文，以下两条语句比较重要
//		gDef.setDefaultFont(new Font("Monospaced", Font.PLAIN, 11));
//		gDef.setTitleFont(new Font("Monospaced", Font.BOLD, 14)); 
//		// create graph finally
//		graph = new RrdGraph(gDef);
//		graph.saveAsPNG(pngPath, 300, 150);
//		
//
		}catch(Exception e){
			e.printStackTrace();
		}
		
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("startdate", getStartDate());
		request.setAttribute("todate", getToDate());
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("chartstr", returnStr);
		request.setAttribute("allutilStr", allutilStr);
		request.setAttribute("rrdstr", showpngPath);
		request.setAttribute("cpurrdstr", showcpupngPath);
		request.setAttribute("showresponsepngPath", showresponsepngPath);
		request.setAttribute("showrpingpngPath", showrpingpngPath);
		request.setAttribute("allavgandmaxHash", allavgandmaxHash);
   }

//	static class GaugeSource {
//		private double value;
//		private double step;
//
//		GaugeSource(double value, double step) {
//			this.value = value;
//			this.step = step;
//		}
//	}
	private void getTime(HttpServletRequest request,String[] time){		
		  Calendar current = new GregorianCalendar();
		  String key = getParaValue("beginhour");
		  if(getParaValue("beginhour") == null){
			  Integer hour = new Integer(current.get(Calendar.HOUR_OF_DAY));
			  request.setAttribute("beginhour", new Integer(hour.intValue()-1));
			  request.setAttribute("endhour", hour);
			  //mForm.setBeginhour(new Integer(hour.intValue()-1));
			  //mForm.setEndhour(hour);
		  }
		  if(getParaValue("begindate") == null){
			  current.set(Calendar.MINUTE,59);
			  current.set(Calendar.SECOND,59);
			  time[1] = datemanager.getDateDetail(current);
			  current.add(Calendar.HOUR_OF_DAY,-1);
			  current.set(Calendar.MINUTE,0);
			  current.set(Calendar.SECOND,0);
			  time[0] = datemanager.getDateDetail(current);

			  java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			  String begindate = "";
			  begindate = timeFormatter.format(new java.util.Date());
			  request.setAttribute("begindate", begindate);
			  request.setAttribute("enddate", begindate);
			  //mForm.setBegindate(begindate);
			  //mForm.setEnddate(begindate);
		 }
		 else{
			  String temp = getParaValue("begindate");
			  time[0] = temp+" "+getParaValue("beginhour")+":00:00";
			  temp = getParaValue("enddate");
			  time[1] = temp+" "+getParaValue("endhour")+":59:59";
		 }
		  if(getParaValue("startdate") == null){
			  current.set(Calendar.MINUTE,59);
			  current.set(Calendar.SECOND,59);
			  time[1] = datemanager.getDateDetail(current);
			  current.add(Calendar.HOUR_OF_DAY,-1);
			  current.set(Calendar.MINUTE,0);
			  current.set(Calendar.SECOND,0);
			  time[0] = datemanager.getDateDetail(current);

			  java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			  String startdate = "";
			  startdate = timeFormatter.format(new java.util.Date());
			  request.setAttribute("startdate", startdate);
			  request.setAttribute("todate", startdate);
			  //mForm.setStartdate(startdate);
			  //mForm.setTodate(startdate);
		 }
		 else{
			  String temp = getParaValue("startdate");
			  time[0] = temp+" "+getParaValue("beginhour")+":00:00";
			  temp = getParaValue("todate");
			  time[1] = temp+" "+getParaValue("endhour")+":59:59";
		 }
		  
	}
	
	private String doip(String ip){
//		  String newip="";
//		  for(int i=0;i<3;i++){
//			int p=ip.indexOf(".");
//			newip+=ip.substring(0,p);
//			ip=ip.substring(p+1);
//		  }
//		 newip+=ip;
//		 //System.out.println("newip="+newip);
//		 return newip;
		String allipstr = SysUtil.doip(ip);
		return allipstr;
	}
	
	private void p_drawchartMultiLineMonth(Hashtable hash,String title1,String title2,int w,int h,String flag){
		if(hash.size()!=0){
			String unit="";
			String[] keys = (String[])hash.get("key");
			ChartGraph cg = new ChartGraph();
			TimeSeries[] s=new TimeSeries[keys.length];
			try{
				for(int i=0; i<keys.length; i++){
					String key = keys[i];
					//TimeSeries ss = new TimeSeries(key,Hour.class);
					TimeSeries ss = new TimeSeries(key,Minute.class);
					String[] value = (String[])hash.get(key);		
					if (flag.equals("AllUtilHdx")){
						unit="y(kb/s)";
					}else{
						unit="y(%)";
					}
					//流速
					for(int j=0; j<value.length; j++){			
						String val = value[j];
						if (val!=null && val.indexOf("&")>=0){									
							String[] splitstr = val.split("&");
							String splittime = splitstr[0];				
							Double	v=new Double(splitstr[1]);			
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date da = sdf.parse(splittime);
							Calendar tempCal = Calendar.getInstance();
							tempCal.setTime(da);						
							Minute minute=new Minute(tempCal.get(Calendar.MINUTE),tempCal.get(Calendar.HOUR_OF_DAY),tempCal.get(Calendar.DAY_OF_MONTH),tempCal.get(Calendar.MONTH)+1,tempCal.get(Calendar.YEAR));
							ss.addOrUpdate(minute,v);							
						}
					}
					s[i]=ss;
				}
				cg.timewave(s,"x(时间)",unit,title1,title2,w,h);
				hash = null;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		else{
			draw_blank(title1,title2,w,h);
		}
	}
	
	


	private void drawchartMultiLineMonth(Hashtable hash,String title1,String title2,int w,int h,String flag){
		if(hash.size()!=0){
		//String unit = (String)hash.get("unit");
		//hash.remove("unit");
		String[] keys = (String[])hash.get("key");
		ChartGraph cg = new ChartGraph();
		TimeSeries[] s=new TimeSeries[keys.length];
		try{
	      for(int i=0; i<keys.length; i++){
			String key = keys[i];
			//TimeSeries ss = new TimeSeries(key,Hour.class);
			TimeSeries ss = new TimeSeries(key,Minute.class);
			String[] value = (String[])hash.get(key);						
			if (flag.equals("UtilHdx")){
	      	//流速
			for(int j=0; j<value.length; j++){			
				String val = value[j];
				if (val!=null && val.indexOf("&")>=0){									
				String[] splitstr = val.split("&");
				String splittime = splitstr[0];				
				Double	v=new Double(splitstr[1]);			
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date da = sdf.parse(splittime);
				Calendar tempCal = Calendar.getInstance();
				tempCal.setTime(da);						
				//UtilHdx obj = (UtilHdx)vector.get(j);
				//Double	v=new Double(obj.getThevalue());
				//Calendar temp = obj.getCollecttime();
				//new org.jfree.data.time.Hour(newTime)
				
				//Hour hour=new Hour(tempCal.get(Calendar.HOUR_OF_DAY),tempCal.get(Calendar.DAY_OF_MONTH),tempCal.get(Calendar.MONTH)+1,tempCal.get(Calendar.YEAR));
				//Day day=new Day(tempCal.get(Calendar.DAY_OF_MONTH),tempCal.get(Calendar.MONTH)+1,tempCal.get(Calendar.YEAR));
				//ss.addOrUpdate(new org.jfree.data.time.Day(da),v);
				//ss.addOrUpdate(hour,v);
				Minute minute=new Minute(tempCal.get(Calendar.MINUTE),tempCal.get(Calendar.HOUR_OF_DAY),tempCal.get(Calendar.DAY_OF_MONTH),tempCal.get(Calendar.MONTH)+1,tempCal.get(Calendar.YEAR));
				ss.addOrUpdate(minute,v);							
			}
			}
	      }
			s[i]=ss;
			}
			cg.timewave(s,"x(时间)","y(kb/s)",title1,title2,w,h);
			hash = null;
			}catch(Exception e){
			e.printStackTrace();
		}
		}
		else{
			draw_blank(title1,title2,w,h);
		}
	}

	private void p_drawchartMultiLine(Hashtable hash,String title1,String title2,int w,int h,String flag){
		if(hash.size()!=0){
		String unit = (String)hash.get("unit");
		hash.remove("unit");
		String[] keys = (String[])hash.get("key");	
		if (keys == null){		
			draw_blank(title1,title2,w,h);
			return;
		}
		ChartGraph cg = new ChartGraph();
		TimeSeries[] s=new TimeSeries[keys.length];
		try{
	      for(int i=0; i<keys.length; i++){
			String key = keys[i];
			TimeSeries ss = new TimeSeries(key,Minute.class);
			Vector vector=(Vector)(hash.get(key));
			if (flag.equals("AllUtilHdxPerc")){
				//综合带宽利用率
				for(int j=0; j<vector.size(); j++){
					/*
					//if (title1.equals("带宽利用率")||title1.equals("端口流速")){
					AllUtilHdxPerc obj = (AllUtilHdxPerc)vector.get(j);
					Double	v=new Double(obj.getThevalue());
					Calendar temp = obj.getCollecttime();
					Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute,v);				
					//}
					*/
				}			
	      }else if (flag.equals("AllUtilHdx")){
			//综合流速
			for(int j=0; j<vector.size(); j++){
				//if (title1.equals("带宽利用率")||title1.equals("端口流速")){
				AllUtilHdx obj = (AllUtilHdx)vector.get(j);
				Double	v=new Double(obj.getThevalue());
				Calendar temp = obj.getCollecttime();
				Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
				ss.addOrUpdate(minute,v);				
				//}
			}			      	
	      }else if (flag.equals("UtilHdxPerc")){
	      	//带宽利用率
				for(int j=0; j<vector.size(); j++){
					Vector obj = (Vector)vector.get(j);
					Double	v=new Double((String)obj.get(0));
					String dt = (String)obj.get(1);			
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);				
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);
					Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute,v);				
				}
						      	
	      }else if (flag.equals("UtilHdx")){
	      	//流速
			for(int j=0; j<vector.size(); j++){
				Vector obj = (Vector)vector.get(j);
				Double	v=new Double((String)obj.get(0));
				String dt = (String)obj.get(1);			
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");			
				Date time1 = sdf.parse(dt);				
				Calendar temp = Calendar.getInstance();
				temp.setTime(time1);
				Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
				ss.addOrUpdate(minute,v);				
			}
	      }else if (flag.equals("ErrorsPerc")){
	      	//流速      	
			for(int j=0; j<vector.size(); j++){
				Vector obj = (Vector)vector.get(j);
				Double	v=new Double((String)obj.get(0));
				String dt = (String)obj.get(1);				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date time1 = sdf.parse(dt);				
				Calendar temp = Calendar.getInstance();
				temp.setTime(time1);
				Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
				ss.addOrUpdate(minute,v);				
			}
	      }else if (flag.equals("DiscardsPerc")){
	      	//流速
			for(int j=0; j<vector.size(); j++){
				Vector obj = (Vector)vector.get(j);
				Double	v=new Double((String)obj.get(0));
				String dt = (String)obj.get(1);			
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date time1 = sdf.parse(dt);				
				Calendar temp = Calendar.getInstance();
				temp.setTime(time1);
				Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
				ss.addOrUpdate(minute,v);				
			}
	      }else if (flag.equals("Packs")){
	      	//数据包
			for(int j=0; j<vector.size(); j++){
				Vector obj = (Vector)vector.get(j);
				Double	v=new Double((String)obj.get(0));
				String dt = (String)obj.get(1);			
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date time1 = sdf.parse(dt);				
				Calendar temp = Calendar.getInstance();
				temp.setTime(time1);
				Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
				ss.addOrUpdate(minute,v);				
			}
	      }
			s[i]=ss;
			}
			cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
			hash = null;
			}catch(Exception e){
			e.printStackTrace();
		}
		}
		else{
			draw_blank(title1,title2,w,h);
		}
	}
	
	private static CategoryDataset getDataSet(){ 
		DefaultCategoryDataset dataset = new DefaultCategoryDataset(); 
		        dataset.addValue(10, "", "values1"); 
		        dataset.addValue(20, "", "values2"); 
		        dataset.addValue(30, "", "values3"); 
		        dataset.addValue(40, "", "values4"); 
		        dataset.addValue(50, "", "values5"); 
		return dataset; 
		} 
	public void draw_column(Hashtable bighash,String title1,String title2,int w,int h){
		if(bighash.size()!=0){
			ChartGraph cg = new ChartGraph();
			int size = bighash.size();
			double[][] d= new double[1][size];
			String c[]= new String[size];
			double[] data1 = new double[size];
			double[] data2 = new double[size];
			String[] labels = new String[size];
			Hashtable hash;
			for(int j=0; j<size; j++){
				hash = (Hashtable)bighash.get(new Integer(j));
				c[j] = (String)hash.get("name");
				//SysLogger.info("&&&&&&&&&&&&&"+hash.get("Utilization"+"value"));
				if(hash.get("Utilization"+"value") == null)continue;
				d[0][j]=Double.parseDouble((String)hash.get("Utilization"+"value"));
				data1[j] = Double.parseDouble((String)hash.get("Utilization"+"value"));
				data2[j] = 100-Double.parseDouble((String)hash.get("Utilization"+"value"));
				labels[j] = (String)hash.get("name");
			}
			String rowKeys[]={"磁盘利用率"};
			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rowKeys,c,d);//.createCategoryDataset(rowKeys, columnKeys, data);
			cg.zhu(title1,title2,dataset,w,h);
			
//			CreateBarPic cbp = new CreateBarPic();
//			//double[] data1 = {40, 30, 50, 55, 63,60,70};
//			//double[] data2 = {60, 70, 50, 45, 37,40,30};
//			 //String[] labels = {"中国", "韩国", "朝鲜", "新加坡", "日本","俄国","德国"};
//			 TitleModel tm = new TitleModel();
//			 tm.setBgcolor(0xffffff);
//			 tm.setXpic(600);
//			 tm.setYpic(170);
//			 tm.setX1(30);
//			 tm.setX2(20);
//			 tm.setX3(560);
//			 tm.setX4(100);
//			 tm.setX5(280);
//			 tm.setX6(140);
//			 tm.setXpoint(100);
//			 int color1 = 0x80ff80;
//			 int color2 = 0x8080ff;
//			cbp.createCylindricalPic(data1,data2,labels,tm,"中国","日本",color1,color2);
			
			
		}
		else{
			draw_blank(title1,title2,w,h);
		}
		bighash = null;
	}

	//add by nielin
	public Hashtable createHashtable(){
		Hashtable hashtable = new Hashtable();
		return hashtable;
	}
	
	public String getStartDate(){
		String startdate = request.getParameter("startdate");
		if(startdate == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startdate = sdf.format(new Date());
		}
		return startdate;
	}
	
	public String getToDate(){
		String toDate = request.getParameter("todate");
		if(toDate == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			toDate = sdf.format(new Date());
		}
		return toDate;
	}
	
	public String getStartTime(){
		String startTime = request.getParameter("startTime");
		if(startTime == null){
			startTime = getStartDate() + " 00:00:00";
		}
		return startTime;
	}
	
	public String getToTime(){
		String toTime = request.getParameter("toTime");
		if(toTime == null){
			toTime = getToDate() + " 23:59:59";
		}
		return toTime;
	}
	
	public Host getHostById(String id){
		if(id == null){
			id = request.getParameter("id");
		}
    	
    	HostNodeDao hostdao = new HostNodeDao();
    	List hostlist = hostdao.loadHost();
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
    	
		return host;
	}
	
	public void setParmarValue(Host host){
		double cpuvalue = 0;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData != null){
			Vector cpuV = (Vector)ipAllData.get("cpu");
			if(cpuV != null && cpuV.size()>0){
				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			//得到系统启动时间
			Vector systemV = (Vector)ipAllData.get("system");
			if(systemV != null && systemV.size()>0){
				for(int i=0;i<systemV.size();i++){
					Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
					if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
						sysuptime = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
						sysservices = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
	}
	
	public String getCollectTime(Host host){
		String collecttime = null;
		Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
		if(pingData != null && pingData.size()>0){
			Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
			Calendar tempCal = (Calendar)pingdata.getCollecttime();							
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}
		return collecttime;
	}
	
	public void choose_p_drawchartMultiLine_type(String timeType ,Hashtable hash,String title1,String title2,int w,int h){
		if(timeType==null){
    		timeType = "minute";
    	}
    	if("minute".equals(timeType)){
    		p_drawchartMultiLine(hash,title1,title2,w,h);
    	}else if("hour".equals(timeType)){
    		p_drawchartMultiLineByHour(hash, title1, title2, w, h);
    	}else if("day".equals(timeType)){
    		p_drawchartMultiLineByDay(hash, title1, title2, w, h);
    	}else if("month".equals(timeType)){
    		p_drawchartMultiLineByMonth(hash,title1,title2,w,h);
    	}else if("year".equals(timeType)){
    		p_drawchartMultiLine(hash,title1,title2,w,h);
    	}else{
    		p_drawchartMultiLine(hash,title1,title2,w,h);
    	}
	}
	public void p_drawchartMultiLine(Hashtable hash,String title1,String title2,int w,int h){
		if(hash.size()!=0){
		String unit = (String)hash.get("unit");
		hash.remove("unit");
		String[] keys = (String[])hash.get("key");
		if (keys == null){
			draw_blank(title1,title2,w,h);
			return;
		}
		ChartGraph cg = new ChartGraph();
		TimeSeries[] s=new TimeSeries[keys.length];
		try{
	      for(int i=0; i<keys.length; i++){
			String key = keys[i];
			String namestr = "";
			if("PhysicalMemory".equalsIgnoreCase(key)){
				namestr = "物理内存";
			}else if("VirtualMemory".equalsIgnoreCase(key)){
				namestr = "虚拟内存";
			}else if("SwapMemory".equalsIgnoreCase(key)){
				namestr = "交换内存";
			}else
				namestr = key;
			TimeSeries ss = new TimeSeries(namestr,Minute.class);
			Vector vector=(Vector)(hash.get(key));
			for(int j=0; j<vector.size(); j++){
				//if (title1.equals("内存利用率")){
					Vector obj = (Vector)vector.get(j);
					//Memorycollectdata obj = (Memorycollectdata)vector.get(j);
					Double	v=new Double((String)obj.get(0));
					String dt = (String)obj.get(1);			
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);				
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);				
					//Calendar temp = obj.getCollecttime();
					Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute,v);				
				//}
				}
			s[i]=ss;
			}
			cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
			hash = null;
			}catch(Exception e){
			e.printStackTrace();
		}
		}
		else{
			draw_blank(title1,title2,w,h);
		}
	}
	
	private void p_drawchartMultiLineByHour(Hashtable hash,String title1,String title2,int w,int h){
		if(hash.size()!=0){
		String unit = (String)hash.get("unit");
		hash.remove("unit");
		String[] keys = (String[])hash.get("key");
		if (keys == null){
			draw_blank(title1,title2,w,h);
			return;
		}
		ChartGraph cg = new ChartGraph();
		TimeSeries[] s=new TimeSeries[keys.length];
		try{
	      for(int i=0; i<keys.length; i++){
			String key = keys[i];
			TimeSeries ss = new TimeSeries(key,Hour.class);
			Vector vector=(Vector)(hash.get(key));
			for(int j=0; j<vector.size(); j++){
				//if (title1.equals("内存利用率")){
					Vector obj = (Vector)vector.get(j);
					//Memorycollectdata obj = (Memorycollectdata)vector.get(j);
					Double	v=new Double((String)obj.get(0));
					String dt = (String)obj.get(1);			
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);				
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);				
					//Calendar temp = obj.getCollecttime();
					Hour hour=new Hour(temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
					ss.addOrUpdate(hour,v);				
				//}
				}
			s[i]=ss;
			}
			cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
			hash = null;
			}catch(Exception e){
			e.printStackTrace();
		}
		}
		else{
			draw_blank(title1,title2,w,h);
		}
	}
	
	private void p_drawchartMultiLineByDay(Hashtable hash,String title1,String title2,int w,int h){
		if(hash.size()!=0){
		String unit = (String)hash.get("unit");
		hash.remove("unit");
		String[] keys = (String[])hash.get("key");
		if (keys == null){
			draw_blank(title1,title2,w,h);
			return;
		}
		ChartGraph cg = new ChartGraph();
		TimeSeries[] s=new TimeSeries[keys.length];
		try{
	      for(int i=0; i<keys.length; i++){
			String key = keys[i];
			TimeSeries ss = new TimeSeries(key,Day.class);
			Vector vector=(Vector)(hash.get(key));
			for(int j=0; j<vector.size(); j++){
				//if (title1.equals("内存利用率")){
					Vector obj = (Vector)vector.get(j);
					//Memorycollectdata obj = (Memorycollectdata)vector.get(j);
					Double	v=new Double((String)obj.get(0));
					String dt = (String)obj.get(1);			
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);				
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);				
					//Calendar temp = obj.getCollecttime();
					Day day=new Day(temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
					ss.addOrUpdate(day,v);				
				//}
				}
			s[i]=ss;
			}
			cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
			hash = null;
			}catch(Exception e){
			e.printStackTrace();
		}
		}
		else{
			draw_blank(title1,title2,w,h);
		}
	}
	
	private void p_drawchartMultiLineByMonth(Hashtable hash,String title1,String title2,int w,int h){
		if(hash.size()!=0){
		String unit = (String)hash.get("unit");
		hash.remove("unit");
		String[] keys = (String[])hash.get("key");
		if (keys == null){
			draw_blank(title1,title2,w,h);
			return;
		}
		ChartGraph cg = new ChartGraph();
		TimeSeries[] s=new TimeSeries[keys.length];
		try{
	      for(int i=0; i<keys.length; i++){
			String key = keys[i];
			TimeSeries ss = new TimeSeries(key,Month.class);
			Vector vector=(Vector)(hash.get(key));
			for(int j=0; j<vector.size(); j++){
				//if (title1.equals("内存利用率")){
					Vector obj = (Vector)vector.get(j);
					//Memorycollectdata obj = (Memorycollectdata)vector.get(j);
					Double	v=new Double((String)obj.get(0));
					String dt = (String)obj.get(1);			
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);				
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);				
					//Calendar temp = obj.getCollecttime();
					Month month=new Month(temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
					ss.addOrUpdate(month,v);				
				//}
				}
			s[i]=ss;
			}
			cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
			hash = null;
			}catch(Exception e){
			e.printStackTrace();
		}
		}
		else{
			draw_blank(title1,title2,w,h);
		}
	}
	
	
	
	private void p_drawchartMultiLineYear(Hashtable hash,String title1,String title2,int w,int h,String flag){
		if(hash.size()!=0){
		//String unit = (String)hash.get("unit");
		//hash.remove("unit");
		String unit="";
		String[] keys = (String[])hash.get("key");
		ChartGraph cg = new ChartGraph();
		TimeSeries[] s=new TimeSeries[keys.length];
		try{
	      for(int i=0; i<keys.length; i++){
			String key = keys[i];
			TimeSeries ss = new TimeSeries(key,Hour.class);
			//TimeSeries ss = new TimeSeries(key,Minute.class);
			String[] value = (String[])hash.get(key);						
			if (flag.equals("UtilHdx")){
				unit="y(kb/s)";
			}else{
				unit="y(%)";
			}
	      	//流速
			for(int j=0; j<value.length; j++){			
				String val = value[j];
				if (val!=null && val.indexOf("&")>=0){									
				String[] splitstr = val.split("&");
				String splittime = splitstr[0];				
				Double	v=new Double(splitstr[1]);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date da = sdf.parse(splittime);
				Calendar tempCal = Calendar.getInstance();
				tempCal.setTime(da);						
				//UtilHdx obj = (UtilHdx)vector.get(j);
				//Double	v=new Double(obj.getThevalue());
				//Calendar temp = obj.getCollecttime();
				//new org.jfree.data.time.Hour(newTime)
				
				//Hour hour=new Hour(tempCal.get(Calendar.HOUR_OF_DAY),tempCal.get(Calendar.DAY_OF_MONTH),tempCal.get(Calendar.MONTH)+1,tempCal.get(Calendar.YEAR));
				//Day day=new Day(tempCal.get(Calendar.DAY_OF_MONTH),tempCal.get(Calendar.MONTH)+1,tempCal.get(Calendar.YEAR));
				ss.addOrUpdate(new org.jfree.data.time.Hour(da),v);
				//Minute minute=new Minute(tempCal.get(Calendar.MINUTE),tempCal.get(Calendar.HOUR_OF_DAY),tempCal.get(Calendar.DAY_OF_MONTH),tempCal.get(Calendar.MONTH)+1,tempCal.get(Calendar.YEAR));
				//ss.addOrUpdate(day,v);							
			}
			}
	      //}
			s[i]=ss;
			}
			cg.timewave(s,"x(时间)",unit,title1,title2,w,h);
			hash = null;
			}catch(Exception e){
			e.printStackTrace();
		}
		}
		else{
			draw_blank(title1,title2,w,h);
		}
	}

	public void chooseDrawLineType(String timeType ,Hashtable hash,String title1,String title2,int w,int h){
		SysLogger.info("$$$$$$$$$$$$$$$$$$ 开始画图 $$$$$$$$$$$$$$$$$$$");
    	if(timeType==null){
    		timeType = "minute";
    	}
    	if("minute".equals(timeType)){
    		p_draw_line(hash,title1,title2,w,h);
    	}else if("hour".equals(timeType)){
    		p_draw_lineByHour(hash, title1, title2, w, h);
    	}else if("day".equals(timeType)){
    		p_draw_lineByDay(hash, title1, title2, w, h);
    	}else if("month".equals(timeType)){
    		p_draw_lineByMonth(hash,title1,title2,w,h);
    	}else if("year".equals(timeType)){
    		p_draw_line(hash,title1,title2,w,h);
    	}else{
    		p_draw_line(hash,title1,title2,w,h);
    	}
    }
	
	private String area_chooseDrawLineType(String timeType ,Hashtable hash,Hashtable hash1,String[] brand1,String[] brand2,int w,int h,int range1,int range2){
		String returnStr = "";
    	if(timeType==null){
    		timeType = "minute";
    	}
    	if("minute".equals(timeType)){
    		returnStr = area_p_draw_line(hash,hash1,brand1,brand2,w,h,range1,range2);
    	}else if("hour".equals(timeType)){
    		//p_draw_lineByHour(hash, title1, title2, w, h);
    	}else if("day".equals(timeType)){
    		//p_draw_lineByDay(hash, title1, title2, w, h);
    	}else if("month".equals(timeType)){
    		//p_draw_lineByMonth(hash,title1,title2,w,h);
    	}else if("year".equals(timeType)){
    		//p_draw_line(hash,title1,title2,w,h);
    	}else{
    		//p_draw_line(hash,title1,title2,w,h);
    	}
    	//SysLogger.info("###########"+returnStr);
    	return returnStr;
    }
	
	
	private String area_chooseDrawMultiLineType(String timeType ,Hashtable hash,String title,String[] bandch,String[] bandch1,int w,int h,int range1,int range2){
		String returnStr = "";
    	if(timeType==null){
    		timeType = "minute";
    	}
    	if("minute".equals(timeType)){
    		returnStr = area_p_draw_multiline(hash,title,bandch,bandch1,w,h,range1,range2);
    	}else if("hour".equals(timeType)){
    		//p_draw_lineByHour(hash, title1, title2, w, h);
    	}else if("day".equals(timeType)){
    		//p_draw_lineByDay(hash, title1, title2, w, h);
    	}else if("month".equals(timeType)){
    		//p_draw_lineByMonth(hash,title1,title2,w,h);
    	}else if("year".equals(timeType)){
    		//p_draw_line(hash,title1,title2,w,h);
    	}else{
    		//p_draw_line(hash,title1,title2,w,h);
    	}
    	//SysLogger.info("###########"+returnStr);
    	return returnStr;
    }
	
	private String area_chooseDrawLineType(String timeType ,Hashtable hash,String title1,String title2,int w,int h){
		String returnStr = "";
    	if(timeType==null){
    		timeType = "minute";
    	}
    	if("minute".equals(timeType)){
    		returnStr = ChartCreator.area_p_draw_line(hash,title1,title2,w,h);
    	}else if("hour".equals(timeType)){
    		p_draw_lineByHour(hash, title1, title2, w, h);
    	}else if("day".equals(timeType)){
    		p_draw_lineByDay(hash, title1, title2, w, h);
    	}else if("month".equals(timeType)){
    		p_draw_lineByMonth(hash,title1,title2,w,h);
    	}else if("year".equals(timeType)){
    		p_draw_line(hash,title1,title2,w,h);
    	}else{
    		p_draw_line(hash,title1,title2,w,h);
    	}
    	//SysLogger.info("###########"+returnStr);
    	return returnStr;
    }
    
    public void p_draw_line(Hashtable hash,String title1,String title2,int w,int h){
    	List list = (List)hash.get("list");
    	try{
	    	if(list==null || list.size()==0){
	    		draw_blank(title1,title2,w,h);
	    	}
	    	else{
		    	String unit = (String)hash.get("unit");
		    	if (unit == null)unit="%";
		    	ChartGraph cg = new ChartGraph();
		    	TimeSeries ss = new TimeSeries(title1,Minute.class);
		    	TimeSeries[] s = {ss};
		    	for(int j=0; j<list.size(); j++){
		    			Vector v = (Vector)list.get(j);
		    			Double	d=new Double(String.valueOf(v.get(0)));			
		    			String dt = (String)v.get(1);
		    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    			Date time1 = sdf.parse(dt);				
		    			Calendar temp = Calendar.getInstance();
		    			temp.setTime(time1);
		    			Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
		    			ss.addOrUpdate(minute,d);		
		    	}
	    	cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
	    	}
	    	hash = null;
	    }catch(Exception e){
    		e.printStackTrace();
    	}
    }
    public void p_draw_line(List list,String title1,String title2,int w,int h){
    	
    	try{
	    	if(list==null || list.size()==0){
	    		draw_blank(title1,title2,w,h);
	    	}
	    	else{
		    	String unit="";
		    	ChartGraph cg = new ChartGraph();
		    	
		    	TimeSeries ss = new TimeSeries(title1,Minute.class);
		    	TimeSeries[] s = {ss};
		    	for(int j=0; j<list.size(); j++){
		    			Vector v = (Vector)list.get(j);
		    			Double	d=new Double((String)v.get(0));			
		    			String dt = (String)v.get(1);
		    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    			Date time1 = sdf.parse(dt);				
		    			Calendar temp = Calendar.getInstance();
		    			temp.setTime(time1);
		    			Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
		    			ss.addOrUpdate(minute,d);
		    			unit=(String)v.get(2);
		    	}
	    	cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
	    	}
	    	
	    }catch(Exception e){
    		e.printStackTrace();
    	}
    }
    public String area_p_draw_line(Hashtable hash,Hashtable hash1,String[] brand1,String[] brand2,int w,int h,int range1,int range2){
    	String seriesKey = SysUtil.getLongID();
    	List list = (List)hash.get("list");
    	try{
	    	if(list==null || list.size()==0){
	    		//draw_blank(title1,title2,w,h);
	    		seriesKey = "";
	    	}
	    	else{
	    		//初始化数据
	            final JFreeChart chart = ChartFactory.createTimeSeriesChart(
	            		brand1[0]+brand1[1]+"趋势图", 
	                "时间", 
	                brand2[0],
	                ChartCreator.createForceDataset(hash,brand1[0]),
	                true,
	                true,
	                false
	            );
	            
	            //设置全图背景色为白色
		        chart.setBackgroundPaint(Color.WHITE);
		        
	            final XYPlot plot = chart.getXYPlot();
	            plot.getDomainAxis().setLowerMargin(0.0);
	            plot.getDomainAxis().setUpperMargin(0.0);
	            plot.setRangeCrosshairVisible(true);
	            plot.setDomainCrosshairVisible(true);
	            plot.setBackgroundPaint(Color.WHITE);
	     	    plot.setForegroundAlpha(0.8f);
	     	    plot.setRangeGridlinesVisible(true);
	     	    plot.setRangeGridlinePaint(Color.darkGray);
	     	    plot.setDomainGridlinesVisible(true);
	     	    plot.setDomainGridlinePaint(new Color(139,69,19));
	     	    XYLineAndShapeRenderer render0 = (XYLineAndShapeRenderer)plot.getRenderer(0);
	     	    render0.setSeriesPaint(0, Color.BLUE);
	           
	           // configure the range axis to display directions...
	           final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	           rangeAxis.setAutoRangeIncludesZero(true);
	           rangeAxis.setRange(0,range1);
	           
	           plot.setRangeAxis(rangeAxis);
	           final XYItemRenderer renderer2 = new XYAreaRenderer();
	           final ValueAxis axis2 = new NumberAxis(brand2[1]);
	           axis2.setRange(0.0, range2);
	           
	           //设置面积图颜色
	           XYAreaRenderer xyarearenderer = new XYAreaRenderer();
	           xyarearenderer.setSeriesPaint(1, new Color(0,204,0));   
	           xyarearenderer.setSeriesFillPaint(1, new Color(0, 206,209));
	           xyarearenderer.setPaint(new Color(0, 206,209));
	           plot.setDataset(1, ChartCreator.createForceDataset(hash1,brand1[1]));
	           plot.setRenderer(1, xyarearenderer);
	           plot.setRangeAxis(1, axis2);
	           plot.mapDatasetToRangeAxis(1, 1);
	           
	           LegendTitle legend = chart.getLegend();
	           legend.setItemFont(new Font("Verdena", 0, 9));
	           JFreeChartBrother jfb = new JFreeChartBrother();
	           jfb.setChart(chart);
	           jfb.setWidth(w);
	           jfb.setHeight(h);
	           
	           ResourceCenter.getInstance().getChartStorage().put(seriesKey,jfb);
	    	}
	    	hash = null;
	    }catch(Exception e){
    		e.printStackTrace();
    	}
	    return seriesKey;
    }
    
    public String area_p_draw_multiline(Hashtable hash,String title,String[] bandch,String[] bandch1,int w,int h,int range1,int range2){
    	String seriesKey = SysUtil.getLongID();
    	Vector datasetV = ChartCreator.createMultiDataset(hash, bandch,bandch1);
    	try{
	    	if(datasetV==null || datasetV.size()==0){
	    		seriesKey = "";
	    	}
	    	else{
	    		//初始化数据
	            final JFreeChart chart = ChartFactory.createTimeSeriesChart(
	            	title+"趋势图", 
	                "时间", 
	                bandch1[0],
	                (XYDataset)datasetV.get(0),
	                true,
	                true,
	                false
	            );
	            
	            //设置全图背景色为白色
		        chart.setBackgroundPaint(Color.WHITE);
		        chart.setBorderPaint(new Color(30,144,255));
		        chart.setBorderVisible(true);
		        
	            final XYPlot plot = chart.getXYPlot();
	            plot.getDomainAxis().setLowerMargin(0.0);
	            plot.getDomainAxis().setUpperMargin(0.0);
	            plot.setRangeCrosshairVisible(true);
	            plot.setDomainCrosshairVisible(true);
	            plot.setBackgroundPaint(Color.WHITE);
	     	    plot.setForegroundAlpha(0.8f);
	     	    plot.setRangeGridlinesVisible(true);
	     	    plot.setRangeGridlinePaint(Color.darkGray);
	     	    plot.setDomainGridlinesVisible(true);
	     	    plot.setDomainGridlinePaint(new Color(139,69,19));
	     	    XYLineAndShapeRenderer render0 = (XYLineAndShapeRenderer)plot.getRenderer(0);
	     	    render0.setSeriesPaint(0, Color.BLUE);
	           
	           // configure the range axis to display directions...
	           final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	           rangeAxis.setAutoRangeIncludesZero(true);
	           rangeAxis.setRange(0,range1);
	           
	           plot.setRangeAxis(rangeAxis);
	           final XYItemRenderer renderer2 = new XYAreaRenderer();
	           final ValueAxis axis2 = new NumberAxis(bandch1[1]);
	           axis2.setRange(0.0, range2);
	           
	           //设置面积图颜色
	           XYAreaRenderer xyarearenderer = new XYAreaRenderer();
	           xyarearenderer.setSeriesPaint(1, new Color(0,204,0));   
	           xyarearenderer.setSeriesFillPaint(1, Color.GREEN);
	           xyarearenderer.setPaint(Color.GREEN);
	           plot.setDataset(1, (XYDataset)datasetV.get(1));
	           plot.setRenderer(1, xyarearenderer);
	           plot.setRangeAxis(1, axis2);
	           plot.mapDatasetToRangeAxis(1, 1);
	           
	           LegendTitle legend = chart.getLegend();
	           legend.setItemFont(new Font("Verdena", 0, 9));
	           //legend.setMargin(0, 0, 0, 0);
	           JFreeChartBrother jfb = new JFreeChartBrother();
	           jfb.setChart(chart);
	           jfb.setWidth(w);
	           jfb.setHeight(h);
	           
	           ResourceCenter.getInstance().getChartStorage().put(seriesKey,jfb);
	    	}
	    	hash = null;
	    }catch(Exception e){
    		e.printStackTrace();
    	}
	    return seriesKey;
    }
    
    private void p_draw_lineByHour(Hashtable hash,String title1,String title2,int w,int h){
    	List list = (List)hash.get("list");
    	try{
	    	if(list==null || list.size()==0){
	    		draw_blank(title1,title2,w,h);
	    	}
	    	else{
		    	String unit = (String)hash.get("unit");
		    	if (unit == null)unit="%";
		    	ChartGraph cg = new ChartGraph();
		    	TimeSeries ss = new TimeSeries(title1,Hour.class);
		    	TimeSeries[] s = {ss};
		    	System.out.println(list.size());
		    	Vector v0 = (Vector)list.get(0);
		    	String dt0 = (String)v0.get(1);
		    	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date time0 = sdf0.parse(dt0);				
				Calendar temp0 = Calendar.getInstance();
				temp0.setTime(time0);
		    	for(int j=0; j<list.size(); j++){
		    			Vector v = (Vector)list.get(j);
		    			Double	d=new Double((String)v.get(0));			
		    			String dt = (String)v.get(1);
		    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    			Date time1 = sdf.parse(dt);				
		    			Calendar temp = Calendar.getInstance();
		    			temp.setTime(time1);
		    			
		    			Hour hour = new Hour(temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
		    			if(temp.get(Calendar.HOUR_OF_DAY)>temp0.get(Calendar.HOUR_OF_DAY)){
		    				ss.addOrUpdate(hour,d);
		    			}
		    			
		//    			ss.addOrUpdate(hour,d);
		    }
    	
		    cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
    	}
    	hash = null;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    private void p_draw_lineByDay(Hashtable hash,String title1,String title2,int w,int h){
    	List list = (List)hash.get("list");
    	try{
	    	if(list==null || list.size()==0){
	    		draw_blank(title1,title2,w,h);
	    	}
	    	else{
		    	String unit = (String)hash.get("unit");
		    	if (unit == null)unit="%";
		    	ChartGraph cg = new ChartGraph();
		    	TimeSeries ss = new TimeSeries(title1,Day.class);
		    	TimeSeries[] s = {ss};
		    	
		    	for(int j=0; j<list.size(); j++){
		    			Vector v = (Vector)list.get(j);
		    			Double	d=new Double((String)v.get(0));			
		    			String dt = (String)v.get(1);
		    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    			Date time1 = sdf.parse(dt);				
		    			Calendar temp = Calendar.getInstance();
		    			temp.setTime(time1);
		    			
		    			Day day = new Day(temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
		    			
		    			
		 			ss.addOrUpdate(day,d);
		    }
    	
		    cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
    	}
    	hash = null;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    private void p_draw_lineByMonth(Hashtable hash,String title1,String title2,int w,int h){
    	List list = (List)hash.get("list");
    	try{
	    	if(list==null || list.size()==0){
	    		draw_blank(title1,title2,w,h);
	    	}
	    	else{
		    	String unit = (String)hash.get("unit");
		    	if (unit == null)unit="%";
		    	ChartGraph cg = new ChartGraph();
		    	TimeSeries ss = new TimeSeries(title1,Month.class);
		    	TimeSeries[] s = {ss};
		    	
		    	for(int j=0; j<list.size(); j++){
		    			Vector v = (Vector)list.get(j);
		    			Double	d=new Double((String)v.get(0));			
		    			String dt = (String)v.get(1);
		    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    			Date time1 = sdf.parse(dt);				
		    			Calendar temp = Calendar.getInstance();
		    			temp.setTime(time1);
		    			
		    			Month month = new Month(temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
		    			
		    			
		 			ss.addOrUpdate(month,d);
		    }
    	
		    cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
    	}
    	hash = null;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    private String hostping_report(){
    	String timeType = request.getParameter("timeType");
    	if(timeType == null){
    		timeType = "minute";
    	}
    	hostpingUtil(timeType);
    	return "/detail/hostping_report.jsp";
    }
    
    private String hostping()
    {
    	try{
    		hostpingUtil("minute");
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	String tmp = request.getParameter("id");
    	
//    	Hashtable diskhash = new Hashtable();
	
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		
//		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
//    	Date d = new Date();
//    	String startdate = sdf0.format(d);
//		String todate =  sdf0.format(d);
//    	String starttime = startdate + " 00:00:00";
//		String totime = todate + " 23:59:59";
//    	try {
//			diskhash = hostlastmanager.getDisk_share(host.getIpAddress(), "Disk", starttime,
//					totime);
//		} catch (Exception e) {
//			e.printStackTrace(); 
//		}

//		String newip = doip(host.getIpAddress());
//		//画图（磁盘是显示最新数据的柱状图）
//		try{
//			draw_column(diskhash,"磁盘利用率",newip+"disk",750,150);
//		}catch(Exception e){
//			
//		}
		
//		request.setAttribute("newip", newip);
//		request.setAttribute("Disk", diskhash);
		
    	Hashtable diskhash = new Hashtable();
    	I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
    	Date d = new Date();
    	String startdate = sdf0.format(d);
		String todate =  sdf0.format(d);
    	String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
    	try {
			diskhash = hostlastmanager.getDisk_share(host.getIpAddress(), "Disk", starttime,
					totime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String newip = doip(host.getIpAddress());
		
		//画图（磁盘是显示最新数据的柱状图）
		try{
			draw_column(diskhash,"磁盘利用率",newip+"disk",750,150);
		}catch(Exception e){
			
		}		
		Hashtable imgurlhash=new Hashtable();
		imgurlhash.put("disk","resource/image/jfreechart/"+newip+"disk"+".png");
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("newip", newip);
		request.setAttribute("Disk", diskhash);		
		
		if(host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI && host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")){
			//WINDOWS服务器,跳转到WMI采集展示页面
			return "/topology/network/hostwmiview.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//Linux服务器,跳转到Linux采集展示页面
			return "/topology/network/hostlinuxview.jsp";
//		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SNMP && host.getSysOid().indexOf("1.3.6.1.4.1.8072")>=0){
//			//Linux服务器,跳转到Linux采集展示页面
//			return "/topology/network/hostlinuxview.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//Linux服务器,跳转到Linux采集展示页面
			return "/topology/network/hostlinuxview.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//Linux服务器,跳转到Linux采集展示页面
			return "/topology/network/hostlinuxview.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/topology/network/hostaixview.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/topology/network/hostaixview.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/topology/network/hostaixview.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.42")>=0){
			//Solaris服务器,跳转到Solaris采集展示页面
			return "/topology/network/hostsolarisview.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.11")>=0){
			//hpunix服务器,跳转到hpunix采集展示页面
			return "/topology/network/hosthpview.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("scounix")>=0){
			//scounixware服务器,跳转到scounixware采集展示页面
			return "/topology/network/hostscounixview.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("scoopenserver")>=0){
			//scoopenserver服务器,跳转到scoopenserver采集展示页面
			return "/topology/network/hostscounixview.jsp";
		}else if((host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET || host.getCollecttype() == SystemConstant.COLLECTTYPE_PING) && host.getOstype()==15){
			//AS400服务器
			return "/topology/network/host_telnet_AS400view.jsp";
		}else
			return "/topology/network/hostview.jsp";
		
    }
    
    private void hostpingUtil(String timeType){
    	Hashtable imgurlhash=new Hashtable();
		Hashtable hash1 = new Hashtable();//"System","Ping"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		Hashtable memhash = new Hashtable();//mem--current
		Hashtable memmaxhash = new Hashtable();//mem--max
		Hashtable memavghash = new Hashtable();
		String routeconfig="";
		String tmp = "";
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String pingrespavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
		try {
			tmp = request.getParameter("id");
			//HostNodeDao hostdao = new HostNodeDao();
			//List hostlist = hostdao.loadHost();
    	
			Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	
			
			String newip=doip(host.getIpAddress());
			String[] time = {"",""};
			getTime(request,time);
			String starttime = time[0];
			String endtime = time[1];	
			String time1 = request.getParameter("begindate");
			if(time1 == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}					
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";						
			String[] item1 = {"System","Ping"};
			I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
			try{
				hash1 = hostlastmanager.getbyCategories_share(host.getIpAddress(),item1,starttime,endtime);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon")!=null)
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			//画图
			//p_draw_line(responsehash,"连通率",newip+"response",750,200);
			//p_draw_line(ConnectUtilizationhash,"连通率",newip+"ConnectUtilization",740,150);
//			chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip+"ConnectUtilization", 740, 150);
			//ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon",pingconavg);
			
			//从collectdata取cpu,内存的历史数据
			Hashtable cpuhash = new Hashtable();
			try{
				cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
			}catch(Exception e){
				e.printStackTrace();
			}
			try{
				memhash = hostlastmanager.getMemory_share(host.getIpAddress(),"Memory",starttime,endtime);
			}catch(Exception e){
				e.printStackTrace();
			}
			Hashtable[] memoryhash =null;
			try{
				memoryhash = hostmanager.getMemory(host.getIpAddress(),"Memory",starttime1,totime1);
			}catch(Exception e){
				e.printStackTrace();
			}
			//各memory最大值								
			memmaxhash = memoryhash[1];
			memavghash = memoryhash[2];
			
			
			
//			//各memory最大值								
//			memmaxhash = memoryhash[1];
//			memavghash = memoryhash[2];
			//String pingconavg ="";
			//cpu最大值
			//maxhash = new Hashtable();
			String cpumax="";
			if(cpuhash.get("max")!=null){
					cpumax = (String)cpuhash.get("max");
			}
			maxhash.put("cpu",cpumax);
			//cpu平均值
			//maxhash = new Hashtable();
			String cpuavg="";
			if(cpuhash.get("avgcpucon")!=null){
					cpuavg = (String)cpuhash.get("avgcpucon");
			}
			maxhash.put("cpuavg",cpuavg);
			
			

			//imgurlhash
			imgurlhash.put("ConnectUtilization","resource/image/jfreechart/"+newip+"ConnectUtilization"+".png");

			Hashtable ResponseTimehash = new Hashtable();
			try{
				ResponseTimehash = hostmanager.getCategory(host.getIpAddress(),"Ping","ResponseTime",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}

			if (ResponseTimehash.get("avgpingcon")!=null)
				pingrespavg = (String)ResponseTimehash.get("avgpingcon");
			//画图
			//p_draw_line(responsehash,"连通率",newip+"response",750,200);
			//p_draw_line(ResponseTimehash,"响应时间",newip+"ResponseTime",740,180);
			try{
				//SysLogger.info(timeType+"======="+newip+"ResponseTime##############$$$$$$$$$$$");
				//chooseDrawLineType(timeType, ResponseTimehash, "响应时间", newip+"ResponseTime", 740, 150);
			}catch(Exception e){
				e.printStackTrace();
			}

			//Response平均值
			//maxhash = new Hashtable();
			maxhash.put("avgrespcon",pingrespavg);

			//imgurlhash
			imgurlhash.put("ResponseTime","resource/image/jfreechart/"+newip+"ResponseTime"+".png");
			
			
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData != null){
				Vector cpuV = (Vector)ipAllData.get("cpu");
				if(cpuV != null && cpuV.size()>0){
					
					CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				//得到系统启动时间
				Vector systemV = (Vector)ipAllData.get("system");
				if(systemV != null && systemV.size()>0){
					for(int i=0;i<systemV.size();i++){
						Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
						if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
							sysuptime = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
							sysservices = systemdata.getThevalue();
						}
						if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
							sysdescr = systemdata.getThevalue();
						}
					}
				}
			}
			if(pingconavg != null){
				pingconavg = pingconavg.replace("%", "");
			}
			
			Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
			if(pingData != null && pingData.size()>0){
				Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
				Date cc = tempCal.getTime();
				collecttime = sdf1.format(cc);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("memmaxhash",memmaxhash);
		request.setAttribute("memavghash",memavghash);
		request.setAttribute("memhash",memhash);
		   
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash1",hash1);
		request.setAttribute("max",maxhash);   	
		request.setAttribute("id", tmp);
		request.setAttribute("startdate", getStartDate());
		request.setAttribute("todate", getToDate());
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("timeType", timeType);
	    
    }
    
    private String hostcpu_report(){
    	String timeType = request.getParameter("timeType");
    	if(timeType == null){
    		timeType = "minute";
    	}
    	hostcpuUtil(timeType);
    	return "/detail/hostcpu_report.jsp";
    }
    
    private String hostmemory_report(){
    	String timeType = request.getParameter("timeType");
    	if(timeType == null){
    		timeType = "minute";
    	}
    	hostcpuUtil(timeType);
    	return "/detail/hostmemory_report.jsp";
    }
    
    private String hostcpu()
    {
    	String tmp = request.getParameter("id");
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	if(!NodeUtil.isOnlyCollectPing(host)){
    		hostcpuUtil("minute");
    	}
    	Hashtable diskhash = new Hashtable();
    	I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
    	Date d = new Date();
    	String startdate = sdf0.format(d);
		String todate =  sdf0.format(d);
    	String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
    	try {
			diskhash = hostlastmanager.getDisk_share(host.getIpAddress(), "Disk", starttime,
					totime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String newip = doip(host.getIpAddress());
//		System.out.println(diskhash+"------------nei cun----------------"+diskhash.size());
		request.setAttribute("newip", newip);
		request.setAttribute("Disk", diskhash);
		//SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$3333");
	    if(NodeUtil.isOnlyCollectPing(host)){
			//只采集连通率的信息时,屏蔽cpu、内存等数据
			return "/detail/host_cpu_onlyping.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI && host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")){
			//WINDOWS服务器,跳转到WMI采集展示页面
			return "/detail/host_wmicpu.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && ( host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0||host.getSysOid().indexOf("1.3.6.1.4.1.8072.3.2.10")>=0)){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxcpu.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET &&( host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0||host.getSysOid().indexOf("1.3.6.1.4.1.8072.3.2.10")>=0)){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxcpu.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH &&( host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0||host.getSysOid().indexOf("1.3.6.1.4.1.8072.3.2.10")>=0)){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxcpu.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aixcpu.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aixcpu.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aixcpu.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.42")>=0){
			//Solaris服务器,跳转到Solaris采集展示页面
			return "/detail/host_solariscpu.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.11")>=0){
			//hpunix服务器,跳转到hpunix采集展示页面
			return "/detail/host_hpcpu.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("scounix")>=0){
			//scounixware服务器,跳转到scounixware采集展示页面
			return "/detail/host_scounixcpu.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("scoopenserver")>=0){
			//scoopenserver服务器,跳转到scoopenserver采集展示页面
			return "/detail/host_scounixcpu.jsp";
		}else{
			//SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$444444");
			return "/detail/host_cpu.jsp";
		}
    	
    }
    
    private String hostwindows()
    {
    	String tmp = request.getParameter("id");
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	if(!NodeUtil.isOnlyCollectPing(host)){
    		
    		hostcpuUtil("minute");
    	}
    	Hashtable diskhash = new Hashtable();
    	I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
    	Date d = new Date();
    	String startdate = sdf0.format(d);
		String todate =  sdf0.format(d);
    	String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
    	try {
			diskhash = hostlastmanager.getDisk_share(host.getIpAddress(), "Disk", starttime,
					totime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String newip = doip(host.getIpAddress());
		request.setAttribute("newip", newip);
		request.setAttribute("Disk", diskhash);
		//SysLogger.info((host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0)+"$$$$$$$$$$$$$$$$$$$$$$$$$3333"+(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0));
	    if(NodeUtil.isOnlyCollectPing(host)){
			//只采集连通率的信息时,屏蔽cpu、内存等数据
			return "/detail/host_cpu_onlyping.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI && host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")){
			//WINDOWS服务器,跳转到WMI采集展示页面
			return "/detail/host_wmicpu.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linux.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxcpu.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
			//linux服务器,跳转到LINUX采集展示页面
			return "/detail/host_linuxcpu.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aix.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aix.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服务器,跳转到Aix采集展示页面
			return "/detail/host_aix.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.42")>=0){
			//Solaris服务器,跳转到Solaris采集展示页面
			return "/detail/host_solaris.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.11")>=0){
			//hpunix服务器,跳转到hpunix采集展示页面
			return "/detail/host_hpunix.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("scounix")>=0){
			//scounixware服务器,跳转到scounixware采集展示页面
			return "/detail/host_scounixware.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("scoopenserver")>=0){
			//scounixware服务器,跳转到scounixware采集展示页面
			return "/detail/host_scounixware.jsp";
		}else{
			//SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$444444");
			return "/detail/host_windows.jsp";
		}
    	
    }
    
    
    
    private String hostconfig()
    {
    	hostcpuUtil("minute");
    	String tmp = request.getParameter("id");
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	if(host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI && host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")){
			//WINDOWS服u21153 器跳u-28820 到MI采u-26938 展u31034 页u-26782 
			return "/detail/host_wmiconfig.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
    		//linux服u21153 器跳u-28820 到INUX采u-26938 展u31034 页u-26782 
    		return "/detail/host_linuxconfig.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
    		//linux服u21153 器跳u-28820 到INUX采u-26938 展u31034 页u-26782 
    		return "/detail/host_linuxconfig.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2021")>=0){
    		//linux服u21153 器跳u-28820 到INUX采u-26938 展u31034 页u-26782 
    		return "/detail/host_linuxconfig.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服u21153 器跳u-28820 到ix采u-26938 展u31034 页u-26782 
			return "/detail/host_aixconfig.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服u21153 器跳u-28820 到ix采u-26938 展u31034 页u-26782 
			return "/detail/host_aixconfig.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH && host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1")>=0){
			//Aix服u21153 器跳u-28820 到ix采u-26938 展u31034 页u-26782 
			return "/detail/host_aixconfig.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.42")>=0){
			//Solaris服u21153 器跳u-28820 到olaris采u-26938 展u31034 页u-26782 
			return "/detail/host_solarisconfig.jsp";
		}else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.11")>=0){
			//hpunix服u21153 器跳u-28820 到punix采u-26938 展u31034 页u-26782 
			return "/detail/host_hpunixconfig.jsp";
    	}else
    		return "/detail/hostwmiconfig.jsp";
    }
    
    private void hostcpuUtil(String timeType){
    	
    	//SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$1111");
    	Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		Hashtable memhash = new Hashtable();//mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();//mem--max
		Hashtable memavghash = new Hashtable();
		
		double cpuvalue = 0;
		String pingconavg ="0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		
    	String tmp = request.getParameter("id");
    	
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    	
		String newip=doip(host.getIpAddress());
		String[] time = {"",""};
		getTime(request,time);
		String starttime = time[0];
		String endtime = time[1];	
		String time1 = request.getParameter("begindate");
		if(time1 == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}							
		
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";						
		String[] item = {"CPU"};
		
		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
		try{
			//hash = hostlastmanager.getbyCategories(host.getIpAddress(),item,starttime,endtime);
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			memhash = hostlastmanager.getMemory_share(host.getIpAddress(),"Memory",starttime,endtime);
		}catch(Exception e){
			e.printStackTrace();
		}
		//memhash = hostlastmanager.getMemory(ip,"Memory",starttime,endtime);
		try{
			diskhash = hostlastmanager.getDisk_share(host.getIpAddress(),"Disk",starttime,endtime);
		}catch(Exception e){
			e.printStackTrace();
		}
		//画图（磁盘是显示最新数据的柱状图）
		try{
			draw_column(diskhash,"",newip+"disk",750,150);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try{
			cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",starttime1,totime1);
		}catch(Exception e){
			e.printStackTrace();
		}
		Hashtable[] memoryhash =null;
		try{
			memoryhash = hostmanager.getMemory(host.getIpAddress(),"Memory",starttime1,totime1);
		}catch(Exception e){
			e.printStackTrace();
		}
		//各memory最大值								
		memmaxhash = memoryhash[1];
		memavghash = memoryhash[2];
		//String pingconavg ="";
		//cpu最大值
		maxhash = new Hashtable();
		String cpumax="";
		if(cpuhash.get("max")!=null){
				cpumax = (String)cpuhash.get("max");
		}
		maxhash.put("cpu",cpumax);
		//cpu平均值
		//maxhash = new Hashtable();
		String cpuavg="";
		if(cpuhash.get("avgcpucon")!=null){
				cpuavg = (String)cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg",cpuavg);
		//画图
		p_draw_line(cpuhash,"",newip+"cpu",740,120);
		//chooseDrawLineType(timeType, cpuhash,"",newip+"cpu",740,120);
		//imgurlhash
		
		
		
		//p_drawchartMultiLine(memoryhash[0],"",newip+"memory",750,150);
		
		//choose_p_drawchartMultiLine_type(timeType, memoryhash[0],"",newip+"memory",750,150);
//imgurlhash
		//imgurlhash.put("cpu","../images/jfreechart/"+newip+"cpu"+".png");
		
		imgurlhash.put("cpu","resource/image/jfreechart/"+newip+"cpu"+".png");
		imgurlhash.put("memory","resource/image/jfreechart/"+newip+"memory"+".png");
		imgurlhash.put("disk","resource/image/jfreechart/"+newip+"disk"+".png");
		
		
		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData != null){
			Vector cpuV = (Vector)ipAllData.get("cpu");
			if(cpuV != null && cpuV.size()>0){
				CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
				if(cpu != null && cpu.getThevalue() != null){
					cpuvalue = new Double(cpu.getThevalue());
				}
			}
			//得到系统启动时间
			Vector systemV = (Vector)ipAllData.get("system");
			if(systemV != null && systemV.size()>0){
				for(int i=0;i<systemV.size();i++){
					Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
					if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
						sysuptime = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysServices")){
						sysservices = systemdata.getThevalue();
					}
					if(systemdata.getSubentity().equalsIgnoreCase("sysDescr")){
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//String time1 = sdf.format(new Date());
								
	
		//String starttime1 = time1 + " 00:00:00";
		//String totime1 = time1 + " 23:59:59";
		
		Hashtable ConnectUtilizationhash = new Hashtable();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if (ConnectUtilizationhash.get("avgpingcon")!=null){
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}
		
		
		Vector pingData = (Vector)ShareData.getPingdata().get(host.getIpAddress());
		if(pingData != null && pingData.size()>0){
			Pingcollectdata pingdata = (Pingcollectdata)pingData.get(0);
			Calendar tempCal = (Calendar)pingdata.getCollecttime();							
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}
		
		/*
		CategoryDataset dataset = getDataSet(); 
        JFreeChart chart = ChartFactory.createBarChart3D("测试条形图", "横轴显示标签", "竖轴显示标签", dataset, PlotOrientation.VERTICAL, true, false, false); 
        FileOutputStream jpg = null; 
        try { 
        	jpg = new FileOutputStream("D:\\test.jpg"); 
        	try { 
        		ChartUtilities.writeChartAsJPEG(jpg,1.0f,chart,400,300,null); 
            } catch (IOException e) { 
            	e.printStackTrace(); 
            } 
        } catch (FileNotFoundException e) { 
        	e.printStackTrace(); 
        }
        */
		
		//SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$11112222");
		   request.setAttribute("memmaxhash",memmaxhash);
		   request.setAttribute("memavghash",memavghash);
		   request.setAttribute("diskhash",diskhash);
		   request.setAttribute("memhash",memhash);
		request.setAttribute("startdate", getStartDate());  
		request.setAttribute("todate", getToDate());  
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash",hash);
		request.setAttribute("max",maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
	    request.setAttribute("timeType", timeType);
    }
    
    private String netping_report(){
    	String timeType = request.getParameter("timeType");
    	if(timeType == null){
    		timeType = "minute";
    	}
    	netpingUtil(timeType);
    	return "/detail/netping_report.jsp";
    }
    
    private String netping(){
    	String timeType = "minute";
    	netpingUtil(timeType);
    	return "/detail/net_ping.jsp";
    }
    private String firewallping(){
    	String timeType = "minute";
    	netpingUtil(timeType);
    	String tmp = request.getParameter("id");
    	Host host = getHostById(tmp);
//    	if(host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.14331.1.4")){
//    		return "/detail/firewalltos_ping.jsp";
//		}else
			return "/detail/firewall_ping.jsp";
    	
    }
    
    private void netpingUtil(String timeType){
    	Hashtable imgurlhash=new Hashtable();
		Hashtable hash1 = new Hashtable();//"System","Ping"--current
		Hashtable maxhash = new Hashtable();//"Ping"--max
		Hashtable cpumaxhash = new Hashtable();//"CPU"--max
		Hashtable memorymaxhash = new Hashtable();//"Memory"--max
		String routeconfig="";
		String tmp = "";
		String pingconavg ="0";
		String pingrespavg = "0";
		String collecttime = null;
		String returnStr = "";
		String returnStr1 = "";
		String allutilStr = "";
		String memoryStr = "";
		try {
			tmp = request.getParameter("id");
			Host host = getHostById(tmp);
			String newip=doip(host.getIpAddress());
			String[] time = {"",""};
			getTime(request,time);
			String starttime = time[0];
			String endtime = time[1];						
			String[] item1 = {"System","Ping"};
			I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
			try{
				hash1 = hostlastmanager.getbyCategories_share(host.getIpAddress(),item1,starttime,endtime);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			//从collectdata取cpu,内存的历史数据
			Hashtable cpuhash = new Hashtable();
			try{
				cpuhash = hostmanager.getCategory(host.getIpAddress(),"CPU","Utilization",getStartTime(),getToTime());
			}catch(Exception e){
				e.printStackTrace();
			}
			//cpu最大值
			String cpumax="";
			if(cpuhash.get("max")!=null){
					cpumax = (String)cpuhash.get("max");
			}
			cpumaxhash.put("cpu",cpumax);
			//cpu平均值
			String cpuavg="";
			if(cpuhash.get("avgcpucon")!=null){
					cpuavg = (String)cpuhash.get("avgcpucon");
			}
			cpumaxhash.put("cpuavg",cpuavg);
			
			//从collectdata取内存的历史数据
			Hashtable memoryhash = new Hashtable();
			try{
				memoryhash = hostmanager.getCategory(host.getIpAddress(),"Memory","Utilization",getStartTime(),getToTime());
			}catch(Exception e){
				e.printStackTrace();
			}
			//memory最大值
			String memerymax="";
			if(memoryhash.get("max")!=null){
				memerymax = (String)memoryhash.get("max");
			}
			memorymaxhash.put("memory",memerymax);
			//cpu平均值
			String memoryavg="";
			if(memoryhash.get("avgmemory")!=null){
					cpuavg = (String)memoryhash.get("avgmemory");
			}
			memorymaxhash.put("memoryavg",memoryavg);
			//生成Memory图片
			memoryStr = area_chooseDrawLineType(timeType,memoryhash, "内存利用率", "内存利用率(%)", 350, 200);
			
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			try{
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",getStartTime(),getToTime());
			}catch(Exception ex){
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon")!=null)
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			//画图
			//p_draw_line(responsehash,"连通率",newip+"response",750,200);
			//area_chooseDrawLineType(timeType,ConnectUtilizationhash,"连通率",newip+"ConnectUtilization"+"by"+timeType,740,150);
			imgurlhash.put("ConnectUtilization","resource/image/jfreechart/"+newip+"ConnectUtilization"+"by"+timeType+".png");
			//ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon",pingconavg);

			Hashtable ResponseTimehash = new Hashtable();
			try{
				ResponseTimehash = hostmanager.getCategory(host.getIpAddress(),"Ping","ResponseTime",getStartTime(),getToTime());
			}catch(Exception ex){
				ex.printStackTrace();
			}

			if (ResponseTimehash.get("avgpingcon")!=null)
				pingrespavg = (String)ResponseTimehash.get("avgpingcon");
			//SysLogger.info("#######################"+pingrespavg);
			//画图
			//p_draw_line(responsehash,"连通率",newip+"response",750,200);
			//p_draw_line(ResponseTimehash,"响应时间",newip+"ResponseTime",740,180);
			String[] bandch = {"连通率","响应时间"};
			String[] bandch1 = {"连通率(%)","响应时间(ms)"};
			returnStr = area_chooseDrawLineType(timeType,ConnectUtilizationhash,ResponseTimehash, bandch, bandch1, 350, 200,110,20);
			
			returnStr1 = area_chooseDrawLineType(timeType,cpuhash, "CPU利用率", "CPU利用率(%)", 350, 200);

			//Response平均值
			//maxhash = new Hashtable();
			maxhash.put("avgrespcon",pingrespavg);

			//imgurlhash
			imgurlhash.put("ResponseTime","resource/image/jfreechart/"+newip+"ResponseTime"+".png");
			
			//imgurlhash
			

			setParmarValue(host);
			if(pingconavg != null){
				pingconavg = pingconavg.replace("%", "");
			}
			
			collecttime = getCollectTime(host);
			
			
			Hashtable value = new Hashtable();
			I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
			String unit = "kb/s";
			String title = "当天24小时端口流速归档";
			String[] banden3 = {"AllInBandwidthUtilHdx","AllOutBandwidthUtilHdx"};
			String[] bandch3 = {"入口流速","出口流速"};
			String[] bandch4 = {"入口流速(kb/s)","出口流速(kb/s)"};
	        String reportname = title + "日报表";
	        String category = "AllUtilHdx";
	        String url1 = "";
			try{
				//按分钟显示报表
				value = daymanager.getmultiHisHdx(host.getIpAddress(),"all","",banden3,bandch3,getStartTime(),getToTime(),"AllUtilHdx");
				reportname = title+getStartTime()+"至"+getToTime()+"报表(按分钟显示)";
				allutilStr = area_chooseDrawMultiLineType(timeType,value, "综合流速",bandch3,bandch4, 350, 200,100000,100000);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash1",hash1);
		request.setAttribute("max",maxhash); 
		request.setAttribute("cpumax",cpumaxhash); 
		request.setAttribute("id", tmp);
		request.setAttribute("startdate", getStartDate());
		request.setAttribute("todate", getToDate());
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("timeType", timeType);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("chartstr", returnStr);
		request.setAttribute("chartstr1", returnStr1);
		request.setAttribute("allutilStr", allutilStr);
		request.setAttribute("memoryStr", memoryStr);
    }
    
    
    private static final String filename = "complexdemo";

	private static String getPath(String ext) {
		return Util.getJRobinDemoPath(filename + "." + ext);
	}

	private static String getPath(int version, String ext) {
		return Util.getJRobinDemoPath(filename + version + "." + ext);
	}

	private static void println(String msg) {
		System.out.println(msg);
	}

	private static void createDatabase(String xmlPath) throws IOException, RrdException {
		// Import database from XML
		String rrdPath = getPath("rrd");
		println("-- Importing XML file: " + xmlPath);
		println("-- to RRD file: " + rrdPath);
//		RrdDbPool pool = RrdDbPool.getInstance();
//		RrdDb rrd = pool.requestRrdDb(rrdPath, xmlPath);
//		println("-- RRD file created");
//		pool.release(rrd);
	}

	private static void createGraphs() throws RrdException, IOException {
		GregorianCalendar start, stop;
		RrdGraph graph = new RrdGraph(true);
		String rrdPath = getPath("rrd");
		String rrdFile = "demo.rrd";
		long _end = Util.getTime(), _start = _end - 1 * 86400; // 截至时间为当前时间，起始时间为一周前
		
		// Create traffic overview of a week
		println("-- Creating graph 1");
		start = new GregorianCalendar(2003, 7, 20);
		stop = new GregorianCalendar(2003, 7, 27);

		RrdGraphDef def = new RrdGraphDef(start, stop);
	  
	    // 创建数据文件定义，保存日数据与周数据   

	
	        	   // 创建数据文件定义，保存日数据与周数据
	        	   RrdDef rrdDef = new RrdDef(rrdFile, _start, 300);                         //数据间隔为300秒
	        	   rrdDef.addDatasource("value1", "GAUGE", 600, Double.NaN, Double.NaN);   //定义数据源，可以定义多个
	        	   //rrdDef.addDatasource("value2", "GAUGE", 600, Double.NaN, Double.NaN);   //定义数据源，可以定义多个
	
	        	//以下定义归档数据，即如何保存数据
	        	   rrdDef.addArchive("AVERAGE", 0.5, 1, 288);             //输入给数据源的数据每一个都保存下来，保存288笔数据，即保存最近一天的数据
	        	   rrdDef.addArchive("AVERAGE", 0.5, 7, 288);             //每7笔数据，取平均值，然后保存，保存288笔数据，即保存最近一周的数据
	        	   RrdDb rrdDb = new RrdDb(rrdDef);                             //根据数据定义创建数据文件
	
	        	   for (long t = _start; t < _end; t += 300) {
	        	    Sample sample = rrdDb.createSample(t);
	        	    sample.setValue("value1", Math.random()*100);
	        	    //sample.setValue("value2", Math.random()*50);
	        	    sample.update();
	        	   }
	        	   String pngFile = "";
		
		
		def.setImageBorder(Color.GRAY, 1);
		def.setTitle("JRobinComplexDemo@Ldemo graph 1@r\nNetwork traffic overview");
		def.setVerticalLabel("bits per second");
		
		def.datasource("demo", rrdFile, "value1", "AVERAGE");
		//def.datasource("demo1", rrdFile, "value2", "AVERAGE");
		
		
		def.datasource("ifInOctets", rrdPath, "ifInOctets", "AVERAGE");
		def.datasource("ifOutOctets", rrdPath, "ifOutOctets", "AVERAGE");
		def.datasource("bitIn", "ifInOctets,8,*");
		def.datasource("bitOut", "ifOutOctets,8,*");
		def.comment(" ");
		def.area("bitIn", new Color(0x00, 0xFF, 0x00), "Incoming traffic ");
		def.line("bitOut", new Color(0x00, 0x00, 0x33), "Outgoing traffic\n\n");
		def.gprint("bitIn", "MAX", "Max:   @6.1 @sbit/s");
		def.gprint("bitOut", "MAX", "      @6.1 @sbit/s\n");
		def.gprint("bitIn", "MIN", "Min:   @6.1 @sbit/s");
		def.gprint("bitOut", "MIN", "      @6.1 @sbit/s");
		def.comment("       Connection:  100 Mbit/s\n");
		def.gprint("bitIn", "AVG", "Avg:   @6.1 @sbit/s");
		def.gprint("bitOut", "AVG", "      @6.1 @sbit/s");
		def.comment("       Duplex mode: FD - fixed\n\n");
		def.gprint("bitIn", "LAST", "Cur:   @6.1 @sbit/s");
		def.gprint("bitOut", "LAST", "      @6.1 @sbit/s\n\n");
		def.comment("[ courtesy of www.cherrymon.org ]@L");
		def.comment("Generated: " + timestamp() + "  @r");

		graph.setGraphDef(def);
		pngFile = getPath(1, "png");
		graph.saveAsPNG(pngFile);
		String gifFile = getPath(1, "gif");
		graph.saveAsGIF(gifFile);
		String jpgFile = getPath(1, "jpg");
		graph.saveAsJPEG(jpgFile, 0.6F);

		// Create server load and cpu usage of a day
		println("-- Creating graph 2");
		start = new GregorianCalendar(2003, 7, 19);
		stop = new GregorianCalendar(2003, 7, 20);

		def = new RrdGraphDef(start, stop);
		def.setImageBorder(Color.GRAY, 1);
		def.setTitle("JRobinComplexDemo@Ldemo graph 2@r\nServer load and CPU utilization");
		def.datasource("load", rrdPath, "serverLoad", "AVERAGE");
		def.datasource("user", rrdPath, "serverCPUUser", "AVERAGE");
		def.datasource("nice", rrdPath, "serverCPUNice", "AVERAGE");
		def.datasource("system", rrdPath, "serverCPUSystem", "AVERAGE");
		def.datasource("idle", rrdPath, "serverCPUIdle", "AVERAGE");
		def.datasource("total", "user,nice,+,system,+,idle,+");
		def.datasource("busy", "user,nice,+,system,+,total,/,100,*");
		def.datasource("p25t50", "busy,25,GT,busy,50,LE,load,0,IF,0,IF");
		def.datasource("p50t75", "busy,50,GT,busy,75,LE,load,0,IF,0,IF");
		def.datasource("p75t90", "busy,75,GT,busy,90,LE,load,0,IF,0,IF");
		def.datasource("p90t100", "busy,90,GT,load,0,IF");
		def.comment("CPU utilization (%)\n ");
		def.area("load", new Color(0x66, 0x99, 0xcc), " 0 - 25%");
		def.area("p25t50", new Color(0x00, 0x66, 0x99), "25 - 50%@L");
		def.gprint("busy", "MIN", "Minimum:@5.1@s%");
		def.gprint("busy", "MAX", "Maximum:@5.1@s% @r ");
		def.area("p50t75", new Color(0x66, 0x66, 0x00), "50 - 75%");
		def.area("p75t90", new Color(0xff, 0x66, 0x00), "75 - 90%");
		def.area("p90t100", new Color(0xcc, 0x33, 0x00), "90 - 100%@L");
		def.gprint("busy", "AVG", " Average:@5.1@s%");
		def.gprint("busy", "LAST", "Current:@5.1@s% @r ");
		def.comment("\nServer load\n ");
		def.line("load", new Color(0x00, 0x00, 0x00), "Load average (5 min)@L");
		def.gprint("load", "MIN", "Minimum:@5.2@s%");
		def.gprint("load", "MAX", "Maximum:@5.2@s% @r ");
		def.gprint("load", "AVG", "Average:@5.2@s%");
		def.gprint("load", "LAST", "Current:@5.2@s% @r");
		def.comment("\n\n[ courtesy of www.cherrymon.org ]@L");
		def.comment("Generated: " + timestamp() + "  @r");

		graph.setGraphDef(def);
		pngFile = getPath(2, "png");
		graph.saveAsPNG(pngFile);
		gifFile = getPath(2, "gif");
		graph.saveAsGIF(gifFile);
		jpgFile = getPath(2, "jpg");
		graph.saveAsJPEG(jpgFile, 0.6F);

		// Create ftp graph for a month
		println("-- Creating graph 3");
		start = new GregorianCalendar(2003, 7, 19, 12, 00);
		stop = new GregorianCalendar(2003, 7, 20, 12, 00);

		def = new RrdGraphDef(start, stop);
		def.setImageBorder(Color.GRAY, 1);
		def.setFrontGrid(false);
		def.setTitle("JRobinComplexDemo@Ldemo graph 3@r\nFTP Usage");
		def.datasource("ftp", rrdPath, "ftpUsers", "AVERAGE");
		def.line("ftp", new Color(0x00, 0x00, 0x33), "FTP connections");
		def.gprint("ftp", "AVG", "( average: @0,");
		def.gprint("ftp", "MIN", "never below: @0 )\n\n");
		def.comment("  Usage spread:");
		def.area(new GregorianCalendar(2003, 7, 19, 17, 00), Double.MIN_VALUE,
			new GregorianCalendar(2003, 7, 19, 23, 00), Double.MAX_VALUE,
			Color.RED, "peak period");
		def.area(new GregorianCalendar(2003, 7, 20, 5, 00), Double.MIN_VALUE,
			new GregorianCalendar(2003, 7, 20, 8, 30), Double.MAX_VALUE,
			Color.LIGHT_GRAY, "quiet period\n");
		def.comment("  Rise/descend:");
		def.area("ftp", new Color(0x00, 0x00, 0x33), null);
		def.line(new GregorianCalendar(2003, 7, 19, 12, 00), 110,
			new GregorianCalendar(2003, 7, 19, 20, 30), 160,
			Color.PINK, "climb slope", 2);
		def.line(new GregorianCalendar(2003, 7, 19, 20, 30), 160,
			new GregorianCalendar(2003, 7, 20, 8, 00), 45,
			Color.CYAN, "fall-back slope\n", 2);
		def.vrule(new GregorianCalendar(2003, 7, 20), Color.YELLOW, null);
		def.comment("\n\n[ courtesy of www.cherrymon.org ]@L");
		def.comment("Generated: " + timestamp() + "  @r");

		graph.setGraphDef(def);
		pngFile = getPath(3, "png");
		graph.saveAsPNG(pngFile, 500, 300);
		gifFile = getPath(3, "gif");
		graph.saveAsGIF(gifFile, 500, 300);
		jpgFile = getPath(3, "jpg");
		graph.saveAsJPEG(jpgFile, 500, 300, 0.6F);
		println("-- Finished");
		println("**************************************");
		println("Check your " + Util.getJRobinDemoDirectory() + " directory.");
		println("You should see nine nice looking graphs starting with [" + filename + "],");
		println("three different graphs, each in three different image formats");
		println("**************************************");
	}

	private static String timestamp() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return df.format(new Date());
	}

	public static void main(String[] args) throws IOException, RrdException {
		if(args.length == 0) {
			println("Usage: ComplexDemo [path to rrdtool_dump.xml file]");
			println("You can download separate rrdtool_dump.xml file from:");
			println("http://www.sourceforge.net/projects/jrobin");
			System.exit(-1);
		}
		long start = System.currentTimeMillis();

		println("********************************************************************");
		println("* JRobinComplexDemo                                                *");
		println("*                                                                  *");
		println("* This demo creates 3 separate graphs and stores them under        *");
		println("* several formats in 9 files.  Values are selected from a large    *");
		println("* RRD file that will be created by importing an XML dump           *");
		println("* of approx. 7 MB.                                                 *");
		println("*                                                                  *");
		println("* Graphs are created using real-life values, original RRD file     *");
		println("* provided by www.cherrymon.org. See the ComplexDemo               *");
		println("* sourcecode on how to create the graphs generated by this demo.   *");
		println("********************************************************************");

		createDatabase(args[0]);
		createGraphs();

		long stop = System.currentTimeMillis();
		println("-- Demo finished in " + ((stop - start) / 1000.0) + " seconds.");
	}
}
