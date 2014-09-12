/**
 * <p>Description:create excel report,bridge pattern</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-18
 */

package com.afunms.report.abstraction;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.jfree.chart.ChartUtilities;

import com.afunms.alarm.service.NodeAlarmService;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.DHCPConfigDao;
import com.afunms.application.dao.Db2spaceconfigDao;
import com.afunms.application.dao.InformixspaceconfigDao;
import com.afunms.application.dao.JBossConfigDao;
import com.afunms.application.dao.MQConfigDao;
import com.afunms.application.dao.OraspaceconfigDao;
import com.afunms.application.dao.SqldbconfigDao;
import com.afunms.application.dao.SybspaceconfigDao;
import com.afunms.application.dao.TomcatPreDao;
import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.manage.IISManager;
import com.afunms.application.manage.TomcatManager;
import com.afunms.application.manage.WeblogicManager;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.DHCPConfig;
import com.afunms.application.model.Db2spaceconfig;
import com.afunms.application.model.IISVo;
import com.afunms.application.model.Informixspaceconfig;
import com.afunms.application.model.JBossConfig;
import com.afunms.application.model.MQConfig;
import com.afunms.application.model.MonitorDBDTO;
import com.afunms.application.model.MonitorMiddlewareDTO;
import com.afunms.application.model.Oraspaceconfig;
import com.afunms.application.model.Sqldbconfig;
import com.afunms.application.model.SybaseVO;
import com.afunms.application.model.Sybspaceconfig;
import com.afunms.application.model.TablesVO;
import com.afunms.application.model.WasConfig;
import com.afunms.application.model.WebConfig;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.util.AssetHelper;
import com.afunms.application.wasmonitor.GetWasInfo;
import com.afunms.application.weblogicmonitor.WeblogicHeap;
import com.afunms.application.weblogicmonitor.WeblogicJdbc;
import com.afunms.application.weblogicmonitor.WeblogicNormal;
import com.afunms.application.weblogicmonitor.WeblogicQueue;
import com.afunms.application.weblogicmonitor.WeblogicServer;
import com.afunms.application.weblogicmonitor.WeblogicSnmp;
import com.afunms.application.weblogicmonitor.WeblogicWeb;
import com.afunms.cabinet.model.Cabinet;
import com.afunms.cabinet.model.EquipmentReport;
import com.afunms.cabinet.model.OperCabinet;
import com.afunms.capreport.model.StatisNumer;
import com.afunms.common.util.Arith;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.DistrictDao;
import com.afunms.config.model.Business;
import com.afunms.config.model.DistrictConfig;
import com.afunms.config.model.Macconfig;
import com.afunms.detail.service.IISInfo.IISInfoService;
import com.afunms.detail.service.jbossInfo.JBossInfoService;
import com.afunms.detail.service.mqInfo.MQInfoService;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.NetSyslog;
import com.afunms.event.model.NetSyslogEvent;
import com.afunms.event.model.NetSyslogViewer;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.IIS;
import com.afunms.polling.node.Tomcat;
import com.afunms.polling.node.Weblogic;
import com.afunms.polling.om.Devicecollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.IpRouter;
import com.afunms.polling.om.Portconfig;
import com.afunms.polling.om.Softwarecollectdata;
import com.afunms.polling.om.Storagecollectdata;
import com.afunms.portscan.model.PortConfig;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.report.base.ImplementorReport1;
import com.afunms.system.model.User;
import com.afunms.temp.model.FdbNodeTemp;
import com.afunms.temp.model.RouterNodeTemp;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.dao.NodeMonitorDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Link;
import com.afunms.topology.model.MonitorNodeDTO;
import com.afunms.topology.model.NetDistrictIpDetail;
import com.afunms.topology.model.NodeMonitor;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

public class ExcelReport3 extends AbstractionReport1 {
	private HttpServletRequest request;
	
	private Hashtable dataHash;
	
	

	public Hashtable getDataHash() {
		return dataHash;
	}

	public void setDataHash(Hashtable dataHash) {
		this.dataHash = dataHash;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}


	public ExcelReport3(ImplementorReport1 impReport) {
		super(impReport);
	}

	public ExcelReport3(ImplementorReport1 impReport, Hashtable hash) {
		super(impReport);
		reportHash = hash;
	}

	@Override
	public void backup_linklist(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReportOra_event(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_OperEquipment(String filename, String opername,
			String contactname, String contactphone) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_SQLServerevent(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_cabinet(String filename, String room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_db2(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_db2all(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_dbevent(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_dbping(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_devicelist(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_equipment(String filename, String roomname,
			String cabinetname, String uselect, String unumber, String rate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_falseLoglist(String filename, String title,
			List list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_faworklist(String filename, User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_faworklistsearch(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_fdb(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_host(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_hostall(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_hostcpu(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_hostdisk(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_hostevent(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_hostmem(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_hostping(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_iis(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_infor(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_ipallotlist(String filename, String[] value,
			Object key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_ipmac(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_ipmacall(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_linklist(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_macconfiglist(String filename, String title,
			List list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_midping(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_monthhostall(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_monthnetworkall(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_monthweball(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_netDistrictIplist(String filename, String title,
			List list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_netevent(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_netlocationevent(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_network(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_networkall(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_networklist(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_oawork(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_oawork(String string, String startdate,
			String todate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_operatorevent(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_ora(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_oraall(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_petrol(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_portscanlist(String filename, String title,
			List list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_softwarelist(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_sql(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_sqlall(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_statistic(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_storagelist(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_syb(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_syball(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_syslog(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_syslogall(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_tomcat(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_tomcatping(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_weball(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReport_weblogic(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReportusa_ora(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createReportusa_oraXls(String filename) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
	
	
	
	 //mq  綜合报表
	public void createReportDoc_mq_per(String file, String type,String id) throws DocumentException, IOException {
		
		  Hashtable rValue = new Hashtable();
		  Vector mqValue = new Vector();//ping信息
	      
	      MQConfig vo = new MQConfig();
	      String runmodel = PollingEngine.getCollectwebflag(); 
		  MQConfigDao configdao = new MQConfigDao();
			
	      try{
	      	vo = (MQConfig)configdao.findByID(id);
	      	String ip = vo.getIpaddress();
	      	if("0".equals(runmodel)){
	             	//采集与访问是集成模式	
		        	Hashtable allMqValues = ShareData.getMqdata();
		        	if (allMqValues != null && allMqValues.size()>0){
						rValue = (Hashtable)allMqValues.get(ip+":"+vo.getManagername());
		        	}
	      	}else{
	      	 	//采集与访问是分离模式
	      		MQInfoService mqInfoService = new MQInfoService();
	      		rValue = mqInfoService.getMQDataHashtable(vo.getId()+"");
	      	}
	      }catch(Exception e){
	      	e.printStackTrace();
	      }finally{
	      	configdao.close();
	      }
		  if(mqValue == null)mqValue = new Vector();
//	      request.setAttribute("mqValue", (Vector)rValue.get("mqValue"));
//	      request.setAttribute("collecttime", (String)rValue.get("collecttime"));
//	      request.setAttribute("basicInfoHashtable", (Hashtable)rValue.get("basicInfoHashtable"));//基本信息
//	      request.setAttribute("chstatusList", (List)rValue.get("chstatusList"));//通道信息
//	      request.setAttribute("localQueueList", (List)rValue.get("localQueueList"));//本地队列信息
//	      request.setAttribute("remoteQueueList", (List)rValue.get("remoteQueueList"));//远程队列信息
		  List channel = (List)rValue.get("chstatusList");//通道信息
		  List local = (List)rValue.get("localQueueList");//本地队列信息
		  List remote = (List)rValue.get("remoteQueueList");//远程队列信息
		  Hashtable monitor = (Hashtable)rValue.get("basicInfoHashtable");//监听信息
	      
//	        String starttime = (String) reportHash.get("starttime");
//			String totime = (String) reportHash.get("totime");
//			String servicename = (String) reportHash.get("servicename");
//			String ip = (String) reportHash.get("ip");
//			String newip = doip(ip);
	      
	      
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   
			
			/* String type = (String)request.getAttribute("type"); */
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			if ("pdf".equals(type)) {
				PdfWriter.getInstance(document, new FileOutputStream(file));
			} else {
				RtfWriter2.getInstance(document, new FileOutputStream(file));
			}
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Paragraph title = new Paragraph("MQ("+vo.getIpaddress()+")综合信息报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			// title.setFont(titleFont);
			document.add(title);
			
			String contextString = "报表生成时间:" + impReport.getTimeStamp() + " \n";// 换行;
	        Paragraph context = new Paragraph(contextString);
			// 正文字体风格
			Font contextFont = new Font(bfChinese, 12, Font.NORMAL);

			Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
			context.setAlignment(Element.ALIGN_LEFT);
			// 离上一段落（标题）空的行数
			context.setSpacingBefore(5);
			// 设置第一行空的列数
			context.setFirstLineIndent(5);
			document.add(context);
			document.add(new Phrase("\n"));
			
			
			
			Table aTable = new Table(7);
			this.setTableFormat(aTable);
			Cell cell = null;
			//----------------------通道信息
			cell = new Cell(new Phrase("通道信息", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("序  号", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("通道名称", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("通道状态", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("接收的缓存数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("发送的缓存数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("接收的字节数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("发送的字节数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			if(channel != null && channel.size()>0){
				for(int i=0;i<channel.size();i++){
					Hashtable tmpHashtable = (Hashtable)channel.get(i);
		 			if (tmpHashtable != null && tmpHashtable.size()>0){
		 				String chstatusname = (String)tmpHashtable.get("chstatusname");
						String bufsrcvd = (String)tmpHashtable.get("bufsrcvd");
						String bufssent = (String)tmpHashtable.get("bufssent");
						String bytsrcvd = (String)tmpHashtable.get("bytsrcvd");
						String bytssent = (String)tmpHashtable.get("bytssent");
						String status = (String)tmpHashtable.get("status");
		 			
						cell = new Cell(new Phrase((i+1)+"", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(chstatusname, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(status, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(bufsrcvd, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(bufssent, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(bytsrcvd, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(bytssent, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
		 			}
				}
			}
            			
			cell = new Cell(new Phrase("", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			//--------------------------监听信息
			cell = new Cell(new Phrase("监听信息", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("序  号", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("名  称", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("监听器支持的并发连接请求的数量", titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("端口", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("状态", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
//			if(monitor != null && monitor.size()>0){
//				for(int i=0;i<monitor.size();i++){
//					Hashtable basicInfoHashtable = (Hashtable)monitor.get(i);
		 			if (monitor != null && monitor.size()>0){
		 				String statu = (String)monitor.get("statu");
						if("null".equals(statu)){statu = "";}
						String listener = (String)monitor.get("listener");
						if("null".equals(listener)){listener = "";}
						String backlog = (String)monitor.get("backlog");
						if("null".equals(backlog)){backlog = "";}
						String port = (String)monitor.get("port");
						if("null".equals(port)){port = "";}
		 			
						cell = new Cell(new Phrase("1", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(listener, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(backlog, titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(port, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(statu, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
		 			}
//				}
//			}
            			
			cell = new Cell(new Phrase("", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			//--------------------------远程队列
			cell = new Cell(new Phrase("远程队列", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("序  号", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("队列名称", titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("队列类型", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("远程队列名称", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("管理器名称", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("传输队列名称", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			if(remote != null && remote.size()>0){
				for(int i=0;i<remote.size();i++){
					Hashtable tmpHashtable = (Hashtable)remote.get(i);
		 			if (tmpHashtable != null && tmpHashtable.size()>0){ 
		 				String queue = (String)tmpHashtable.get("queue");
						String type1 = (String)tmpHashtable.get("type");
						String rqmname = (String)tmpHashtable.get("rqmname");
						String rname = (String)tmpHashtable.get("rname");
						String xmitq = (String)tmpHashtable.get("xmitq");	
		 			
						cell = new Cell(new Phrase((i+1)+"", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(queue, titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("远程队列", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(rqmname, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(rname, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(xmitq, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
		 			}
				}
			}
            			
			cell = new Cell(new Phrase("", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			//--------------------------本地队列
			cell = new Cell(new Phrase("本地队列", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("序  号", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("队列名称", titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("队列类型", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("当前深度", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("最大深度", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("当前深度百分比", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			if(remote != null && remote.size()>0){
				for(int i=0;i<remote.size();i++){
					Hashtable tmpHashtable = (Hashtable)local.get(i);
		 			if (tmpHashtable != null && tmpHashtable.size() > 0){
		 				String queue = (String)tmpHashtable.get("queue");
						String curdepth = (String)tmpHashtable.get("curdepth");
						String maxdepth = (String)tmpHashtable.get("maxdepth");
						String type1 = (String)tmpHashtable.get("type");		
						String percent = (String)tmpHashtable.get("percent");	
		 			
						cell = new Cell(new Phrase((i+1)+"", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(queue, titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("本地队列", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(curdepth, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(maxdepth, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(percent, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
		 			}
				}
			}
            			
			cell = new Cell(new Phrase("", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			document.add(aTable);
			document.close();
		}
	
	
	
	
	 //mq  信息报表
	public void createReportDoc_mq_monitor(String file, String type,String id) throws DocumentException, IOException {
		
		  Hashtable rValue = new Hashtable();
		  Vector mqValue = new Vector();//ping信息
	      
	      MQConfig vo = new MQConfig();
	      String runmodel = PollingEngine.getCollectwebflag(); 
		  MQConfigDao configdao = new MQConfigDao();
			
	      try{
	      	vo = (MQConfig)configdao.findByID(id);
	      	String ip = vo.getIpaddress();
	      	if("0".equals(runmodel)){
	             	//采集与访问是集成模式	
		        	Hashtable allMqValues = ShareData.getMqdata();
		        	if (allMqValues != null && allMqValues.size()>0){
						rValue = (Hashtable)allMqValues.get(ip+":"+vo.getManagername());
		        	}
	      	}else{
	      	 	//采集与访问是分离模式
	      		MQInfoService mqInfoService = new MQInfoService();
	      		rValue = mqInfoService.getMQDataHashtable(vo.getId()+"");
	      	}
	      }catch(Exception e){
	      	e.printStackTrace();
	      }finally{
	      	configdao.close();
	      }
		  if(mqValue == null)mqValue = new Vector();
//	      request.setAttribute("mqValue", (Vector)rValue.get("mqValue"));
//	      request.setAttribute("collecttime", (String)rValue.get("collecttime"));
//	      request.setAttribute("basicInfoHashtable", (Hashtable)rValue.get("basicInfoHashtable"));//基本信息
//	      request.setAttribute("chstatusList", (List)rValue.get("chstatusList"));//通道信息
//	      request.setAttribute("localQueueList", (List)rValue.get("localQueueList"));//本地队列信息
//	      request.setAttribute("remoteQueueList", (List)rValue.get("remoteQueueList"));//远程队列信息
		  List channel = (List)rValue.get("chstatusList");//通道信息
		  List local = (List)rValue.get("localQueueList");//本地队列信息
		  List remote = (List)rValue.get("remoteQueueList");//远程队列信息
		  Hashtable monitor = (Hashtable)rValue.get("basicInfoHashtable");//监听信息
	      
//	        String starttime = (String) reportHash.get("starttime");
//			String totime = (String) reportHash.get("totime");
//			String servicename = (String) reportHash.get("servicename");
//			String ip = (String) reportHash.get("ip");
//			String newip = doip(ip);
	      
	      
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   
			
			/* String type = (String)request.getAttribute("type"); */
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			if ("pdf".equals(type)) {
				PdfWriter.getInstance(document, new FileOutputStream(file));
			} else {
				RtfWriter2.getInstance(document, new FileOutputStream(file));
			}
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Paragraph title = new Paragraph("MQ("+vo.getIpaddress()+")综合信息报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			// title.setFont(titleFont);
			document.add(title);
			
			String contextString = "报表生成时间:" + impReport.getTimeStamp() + " \n";// 换行;
	        Paragraph context = new Paragraph(contextString);
			// 正文字体风格
			Font contextFont = new Font(bfChinese, 12, Font.NORMAL);

			Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
			context.setAlignment(Element.ALIGN_LEFT);
			// 离上一段落（标题）空的行数
			context.setSpacingBefore(5);
			// 设置第一行空的列数
			context.setFirstLineIndent(5);
			document.add(context);
			document.add(new Phrase("\n"));
			
			
			
			Table aTable = new Table(7);
			this.setTableFormat(aTable);
			Cell cell = null;
			//----------------------通道信息
			cell = new Cell(new Phrase("通道信息", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("序  号", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("通道名称", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("通道状态", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("接收的缓存数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("发送的缓存数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("接收的字节数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("发送的字节数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			if(channel != null && channel.size()>0){
				for(int i=0;i<channel.size();i++){
					Hashtable tmpHashtable = (Hashtable)channel.get(i);
		 			if (tmpHashtable != null && tmpHashtable.size()>0){
		 				String chstatusname = (String)tmpHashtable.get("chstatusname");
						String bufsrcvd = (String)tmpHashtable.get("bufsrcvd");
						String bufssent = (String)tmpHashtable.get("bufssent");
						String bytsrcvd = (String)tmpHashtable.get("bytsrcvd");
						String bytssent = (String)tmpHashtable.get("bytssent");
						String status = (String)tmpHashtable.get("status");
		 			
						cell = new Cell(new Phrase((i+1)+"", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(chstatusname, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(status, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(bufsrcvd, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(bufssent, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(bytsrcvd, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(bytssent, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
		 			}
				}
			}
            			
			cell = new Cell(new Phrase("", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			//--------------------------监听信息
			cell = new Cell(new Phrase("监听信息", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("序  号", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("名  称", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("监听器支持的并发连接请求的数量", titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("端口", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("状态", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
//			if(monitor != null && monitor.size()>0){
//				for(int i=0;i<monitor.size();i++){
//					Hashtable basicInfoHashtable = (Hashtable)monitor.get(i);
		 			if (monitor != null && monitor.size()>0){
		 				String statu = (String)monitor.get("statu");
						if("null".equals(statu)){statu = "";}
						String listener = (String)monitor.get("listener");
						if("null".equals(listener)){listener = "";}
						String backlog = (String)monitor.get("backlog");
						if("null".equals(backlog)){backlog = "";}
						String port = (String)monitor.get("port");
						if("null".equals(port)){port = "";}
		 			
						cell = new Cell(new Phrase("1", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(listener, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(backlog, titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(port, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(statu, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
		 			}
//				}
//			}
            			
			cell = new Cell(new Phrase("", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
//			//--------------------------远程队列
//			cell = new Cell(new Phrase("远程队列", titleFont));
//			cell.setColspan(7);
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			
//			cell = new Cell(new Phrase("序  号", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("队列名称", titleFont));
//			cell.setColspan(2);
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("队列类型", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("远程队列名称", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("管理器名称", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("传输队列名称", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			
//			if(remote != null && remote.size()>0){
//				for(int i=0;i<remote.size();i++){
//					Hashtable tmpHashtable = (Hashtable)remote.get(i);
//		 			if (tmpHashtable != null && tmpHashtable.size()>0){ 
//		 				String queue = (String)tmpHashtable.get("queue");
//						String type1 = (String)tmpHashtable.get("type");
//						String rqmname = (String)tmpHashtable.get("rqmname");
//						String rname = (String)tmpHashtable.get("rname");
//						String xmitq = (String)tmpHashtable.get("xmitq");	
//		 			
//						cell = new Cell(new Phrase((i+1)+"", titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(queue, titleFont));
//						cell.setColspan(2);
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase("远程队列", titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(rqmname, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(rname, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(xmitq, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//		 			}
//				}
//			}
//            			
//			cell = new Cell(new Phrase("", titleFont));
//			cell.setColspan(7);
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			
//			//--------------------------本地队列
//			cell = new Cell(new Phrase("本地队列", titleFont));
//			cell.setColspan(7);
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			
//			cell = new Cell(new Phrase("序  号", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("队列名称", titleFont));
//			cell.setColspan(2);
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("队列类型", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("当前深度", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("最大深度", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("当前深度百分比", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			
//			if(remote != null && remote.size()>0){
//				for(int i=0;i<remote.size();i++){
//					Hashtable tmpHashtable = (Hashtable)local.get(i);
//		 			if (tmpHashtable != null && tmpHashtable.size() > 0){
//		 				String queue = (String)tmpHashtable.get("queue");
//						String curdepth = (String)tmpHashtable.get("curdepth");
//						String maxdepth = (String)tmpHashtable.get("maxdepth");
//						String type1 = (String)tmpHashtable.get("type");		
//						String percent = (String)tmpHashtable.get("percent");	
//		 			
//						cell = new Cell(new Phrase((i+1)+"", titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(queue, titleFont));
//						cell.setColspan(2);
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase("本地队列", titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(curdepth, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(maxdepth, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(percent, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//		 			}
//				}
//			}
//            			
//			cell = new Cell(new Phrase("", titleFont));
//			cell.setColspan(7);
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
			
			document.add(aTable);
			document.close();
		}
	
	
	
	 //mq  队列报表
	public void createReportDoc_mq_queue(String file, String type,String id) throws DocumentException, IOException {
		
		  Hashtable rValue = new Hashtable();
		  Vector mqValue = new Vector();//ping信息
	      
	      MQConfig vo = new MQConfig();
	      String runmodel = PollingEngine.getCollectwebflag(); 
		  MQConfigDao configdao = new MQConfigDao();
			
	      try{
	      	vo = (MQConfig)configdao.findByID(id);
	      	String ip = vo.getIpaddress();
	      	if("0".equals(runmodel)){
	             	//采集与访问是集成模式	
		        	Hashtable allMqValues = ShareData.getMqdata();
		        	if (allMqValues != null && allMqValues.size()>0){
						rValue = (Hashtable)allMqValues.get(ip+":"+vo.getManagername());
		        	}
	      	}else{
	      	 	//采集与访问是分离模式
	      		MQInfoService mqInfoService = new MQInfoService();
	      		rValue = mqInfoService.getMQDataHashtable(vo.getId()+"");
	      	}
	      }catch(Exception e){
	      	e.printStackTrace();
	      }finally{
	      	configdao.close();
	      }
		  if(mqValue == null)mqValue = new Vector();
		  List channel = (List)rValue.get("chstatusList");//通道信息
		  List local = (List)rValue.get("localQueueList");//本地队列信息
		  List remote = (List)rValue.get("remoteQueueList");//远程队列信息
		  Hashtable monitor = (Hashtable)rValue.get("basicInfoHashtable");//监听信息
	      
	      
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   
			
			/* String type = (String)request.getAttribute("type"); */
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			if ("pdf".equals(type)) {
				PdfWriter.getInstance(document, new FileOutputStream(file));
			} else {
				RtfWriter2.getInstance(document, new FileOutputStream(file));
			}
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Paragraph title = new Paragraph("MQ("+vo.getIpaddress()+")综合信息报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			// title.setFont(titleFont);
			document.add(title);
			
			String contextString = "报表生成时间:" + impReport.getTimeStamp() + " \n";// 换行;
	        Paragraph context = new Paragraph(contextString);
			// 正文字体风格
			Font contextFont = new Font(bfChinese, 12, Font.NORMAL);

			Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
			context.setAlignment(Element.ALIGN_LEFT);
			// 离上一段落（标题）空的行数
			context.setSpacingBefore(5);
			// 设置第一行空的列数
			context.setFirstLineIndent(5);
			document.add(context);
			document.add(new Phrase("\n"));
			
			
			
			Table aTable = new Table(7);
			this.setTableFormat(aTable);
			Cell cell = null;
			//----------------------通道信息
//			cell = new Cell(new Phrase("通道信息", titleFont));
//			cell.setColspan(7);
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			
//			cell = new Cell(new Phrase("序  号", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("通道名称", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("通道状态", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("接收的缓存数", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("发送的缓存数", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("接收的字节数", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("发送的字节数", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			if(channel != null && channel.size()>0){
//				for(int i=0;i<channel.size();i++){
//					Hashtable tmpHashtable = (Hashtable)channel.get(i);
//		 			if (tmpHashtable != null && tmpHashtable.size()>0){
//		 				String chstatusname = (String)tmpHashtable.get("chstatusname");
//						String bufsrcvd = (String)tmpHashtable.get("bufsrcvd");
//						String bufssent = (String)tmpHashtable.get("bufssent");
//						String bytsrcvd = (String)tmpHashtable.get("bytsrcvd");
//						String bytssent = (String)tmpHashtable.get("bytssent");
//						String status = (String)tmpHashtable.get("status");
//		 			
//						cell = new Cell(new Phrase((i+1)+"", titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(chstatusname, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(status, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(bufsrcvd, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(bufssent, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(bytsrcvd, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(bytssent, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//		 			}
//				}
//			}
//            			
//			cell = new Cell(new Phrase("", titleFont));
//			cell.setColspan(7);
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			//--------------------------监听信息
//			cell = new Cell(new Phrase("监听信息", titleFont));
//			cell.setColspan(7);
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			
//			cell = new Cell(new Phrase("序  号", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("名  称", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("监听器支持的并发连接请求的数量", titleFont));
//			cell.setColspan(2);
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("端口", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("状态", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			cell = new Cell(new Phrase("", titleFont));
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
//			
////			if(monitor != null && monitor.size()>0){
////				for(int i=0;i<monitor.size();i++){
////					Hashtable basicInfoHashtable = (Hashtable)monitor.get(i);
//		 			if (monitor != null && monitor.size()>0){
//		 				String statu = (String)monitor.get("statu");
//						if("null".equals(statu)){statu = "";}
//						String listener = (String)monitor.get("listener");
//						if("null".equals(listener)){listener = "";}
//						String backlog = (String)monitor.get("backlog");
//						if("null".equals(backlog)){backlog = "";}
//						String port = (String)monitor.get("port");
//						if("null".equals(port)){port = "";}
//		 			
//						cell = new Cell(new Phrase("1", titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(listener, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(backlog, titleFont));
//						cell.setColspan(2);
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(port, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase(statu, titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//						cell = new Cell(new Phrase("", titleFont));
//						this.setCellFormat(cell, false);
//						aTable.addCell(cell);
//		 			}
////				}
////			}
//            			
//			cell = new Cell(new Phrase("", titleFont));
//			cell.setColspan(7);
//			this.setCellFormat(cell, false);
//			aTable.addCell(cell);
			//--------------------------远程队列
			cell = new Cell(new Phrase("远程队列", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("序  号", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("队列名称", titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("队列类型", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("远程队列名称", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("管理器名称", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("传输队列名称", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			if(remote != null && remote.size()>0){
				for(int i=0;i<remote.size();i++){
					Hashtable tmpHashtable = (Hashtable)remote.get(i);
		 			if (tmpHashtable != null && tmpHashtable.size()>0){ 
		 				String queue = (String)tmpHashtable.get("queue");
						String type1 = (String)tmpHashtable.get("type");
						String rqmname = (String)tmpHashtable.get("rqmname");
						String rname = (String)tmpHashtable.get("rname");
						String xmitq = (String)tmpHashtable.get("xmitq");	
		 			
						cell = new Cell(new Phrase((i+1)+"", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(queue, titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("远程队列", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(rqmname, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(rname, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(xmitq, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
		 			}
				}
			}
            			
			cell = new Cell(new Phrase("", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			//--------------------------本地队列
			cell = new Cell(new Phrase("本地队列", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("序  号", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("队列名称", titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("队列类型", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("当前深度", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("最大深度", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("当前深度百分比", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			if(remote != null && remote.size()>0){
				for(int i=0;i<remote.size();i++){
					Hashtable tmpHashtable = (Hashtable)local.get(i);
		 			if (tmpHashtable != null && tmpHashtable.size() > 0){
		 				String queue = (String)tmpHashtable.get("queue");
						String curdepth = (String)tmpHashtable.get("curdepth");
						String maxdepth = (String)tmpHashtable.get("maxdepth");
						String type1 = (String)tmpHashtable.get("type");		
						String percent = (String)tmpHashtable.get("percent");	
		 			
						cell = new Cell(new Phrase((i+1)+"", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(queue, titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("本地队列", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(curdepth, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(maxdepth, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(percent, titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
		 			}
				}
			}
            			
			cell = new Cell(new Phrase("", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			document.add(aTable);
			document.close();
		}
		


	public void createReportxls_mq_per(String filename,String id) {
		if (impReport.getTable() == null) {
			fileName = null;
			return;
		}
		  Hashtable rValue = new Hashtable();
		  Vector mqValue = new Vector();//ping信息
	      
	      MQConfig vo = new MQConfig();
	      String runmodel = PollingEngine.getCollectwebflag(); 
		  MQConfigDao configdao = new MQConfigDao();
			
	      try{
	      	vo = (MQConfig)configdao.findByID(id);
	      	String ip = vo.getIpaddress();
	      	if("0".equals(runmodel)){
	             	//采集与访问是集成模式	
		        	Hashtable allMqValues = ShareData.getMqdata();
		        	if (allMqValues != null && allMqValues.size()>0){
						rValue = (Hashtable)allMqValues.get(ip+":"+vo.getManagername());
		        	}
	      	}else{
	      	 	//采集与访问是分离模式
	      		MQInfoService mqInfoService = new MQInfoService();
	      		rValue = mqInfoService.getMQDataHashtable(vo.getId()+"");
	      	}
	      }catch(Exception e){
	      	e.printStackTrace();
	      }finally{
	      	configdao.close();
	      }
		  if(mqValue == null)mqValue = new Vector();
	      List channel = (List)rValue.get("chstatusList");//通道信息
		  List local = (List)rValue.get("localQueueList");//本地队列信息
		  List remote = (List)rValue.get("remoteQueueList");//远程队列信息
		  Hashtable monitor = (Hashtable)rValue.get("basicInfoHashtable");//监听信息
		
		WritableWorkbook wb = null;
		try {
			fileName = ResourceCenter.getInstance().getSysPath() + filename;
			wb = Workbook.createWorkbook(new File(fileName));

			WritableSheet sheet = null;
			sheet = wb.createSheet("MQ综合信息报表", 0);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Label tmpLabel = null;
			tmpLabel = new Label(0, 0, "MQ("+vo.getIpaddress()+")综合信息报表", labelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, 0, 7, 0);
			
			tmpLabel = new Label(0, 1, "报表生成时间:" + impReport.getTimeStamp());
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 3, "通道信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 4, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 4, "通道名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, 4, "通道状态", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, 4, "接收的缓存数", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, 4, "发送的缓存数", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(5, 4, "接收的字节数", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(6, 4, "发送的字节数", _labelFormat);
			sheet.addCell(tmpLabel);
			
			int index = 5 ;
			if (channel != null && channel.size()>0){
		 		for(int i=0;i<channel.size();i++){
		 			Hashtable tmpHashtable = (Hashtable)channel.get(i);
		 			if (tmpHashtable != null && tmpHashtable.size()>0){
		 				String chstatusname = (String)tmpHashtable.get("chstatusname");
						String bufsrcvd = (String)tmpHashtable.get("bufsrcvd");
						String bufssent = (String)tmpHashtable.get("bufssent");
						String bytsrcvd = (String)tmpHashtable.get("bytsrcvd");
						String bytssent = (String)tmpHashtable.get("bytssent");
						String status = (String)tmpHashtable.get("status");
						
						tmpLabel = new Label(0, 5+i, (i+1)+"", b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(1, 5+i, chstatusname, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(2, 5+i, bufsrcvd, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(3, 5+i, bufssent, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(4, 5+i, bytsrcvd, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(5, 5+i, bytssent, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(6, 5+i, status, _labelFormat);
						sheet.addCell(tmpLabel);
						
						index = 5+i;
		 			}
		 		}
			}
		
			index = index+2;
			tmpLabel = new Label(0, index, "监听信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			tmpLabel = new Label(0, index, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, index, "名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, index, "监听器支持的并发连接请求的数量", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, index, "端口", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, index, "状态", _labelFormat);
			sheet.addCell(tmpLabel);
			
			index = index+1;
			if(null != monitor && monitor.size() > 0){
				String statu = (String)monitor.get("statu");
				if("null".equals(statu)){statu = "";}
				String listener = (String)monitor.get("listener");
				if("null".equals(listener)){listener = "";}
				String backlog = (String)monitor.get("backlog");
				if("null".equals(backlog)){backlog = "";}
				String port = (String)monitor.get("port");
				if("null".equals(port)){port = "";}
				
				
				tmpLabel = new Label(0, index, "1", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(1, index, listener, _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(2, index, backlog, _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(3, index, port, _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(4, index, statu, _labelFormat);
				sheet.addCell(tmpLabel);
			}
			index = index+2;

			tmpLabel = new Label(0, index, "远程队列信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			tmpLabel = new Label(0, index, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, index, "队列名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, index, "队列类型", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, index, "远程队列名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, index, "管理器名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(5, index, "传输队列名称", _labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			if (remote != null && remote.size()>0){
		 		for(int i=0;i<remote.size();i++){
		 			Hashtable tmpHashtable = (Hashtable)remote.get(i);
		 			if (tmpHashtable != null && tmpHashtable.size()>0){ 
		 				String queue = (String)tmpHashtable.get("queue");
						String type = (String)tmpHashtable.get("type");
						String rqmname = (String)tmpHashtable.get("rqmname");
						String rname = (String)tmpHashtable.get("rname");
		 				String xmitq = (String)tmpHashtable.get("xmitq");
		 				
		 				tmpLabel = new Label(0, index+i, (i+1)+"", b_labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(1, index+i, queue, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(2, index+i, "远程队列", _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(3, index+i, rqmname, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(4, index+i, rname, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(5, index+i, xmitq, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 			}
		 		}
		 		index = index+remote.size();
			}
			
			index = index+1;
			tmpLabel = new Label(0, index, "本地队列信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			tmpLabel = new Label(0, index, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, index, "队列名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, index, "队列类型", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, index, "当前深度", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, index, "最大深度", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(5, index, "当前深度百分比", _labelFormat);
			sheet.addCell(tmpLabel);
			index=index+1;
			
			if (local != null && local.size()>0){
		 		for(int i=0;i<local.size();i++){
		 			Hashtable tmpHashtable = (Hashtable)local.get(i);
		 			if (tmpHashtable != null && tmpHashtable.size() > 0){
		 				String queue = (String)tmpHashtable.get("queue");
						String curdepth = (String)tmpHashtable.get("curdepth");
						String maxdepth = (String)tmpHashtable.get("maxdepth");
						String type = (String)tmpHashtable.get("type");		
						String percent = (String)tmpHashtable.get("percent");
						
						tmpLabel = new Label(0, index+i, (i+1)+"", b_labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(1, index+i, queue, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(2, index+i, "本地队列", _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(3, index+i, curdepth, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(4, index+i, maxdepth, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(5, index+i, percent, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 			}
		 		}
			}
			
			
			
			wb.write();
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		}
	}
	
	
	
	public void createReportxls_mq_monitor(String filename,String id) {
		if (impReport.getTable() == null) {
			fileName = null;
			return;
		}
		  Hashtable rValue = new Hashtable();
		  Vector mqValue = new Vector();//ping信息
	      
	      MQConfig vo = new MQConfig();
	      String runmodel = PollingEngine.getCollectwebflag(); 
		  MQConfigDao configdao = new MQConfigDao();
			
	      try{
	      	vo = (MQConfig)configdao.findByID(id);
	      	String ip = vo.getIpaddress();
	      	if("0".equals(runmodel)){
	             	//采集与访问是集成模式	
		        	Hashtable allMqValues = ShareData.getMqdata();
		        	if (allMqValues != null && allMqValues.size()>0){
						rValue = (Hashtable)allMqValues.get(ip+":"+vo.getManagername());
		        	}
	      	}else{
	      	 	//采集与访问是分离模式
	      		MQInfoService mqInfoService = new MQInfoService();
	      		rValue = mqInfoService.getMQDataHashtable(vo.getId()+"");
	      	}
	      }catch(Exception e){
	      	e.printStackTrace();
	      }finally{
	      	configdao.close();
	      }
		  if(mqValue == null)mqValue = new Vector();
	      List channel = (List)rValue.get("chstatusList");//通道信息
		  List local = (List)rValue.get("localQueueList");//本地队列信息
		  List remote = (List)rValue.get("remoteQueueList");//远程队列信息
		  Hashtable monitor = (Hashtable)rValue.get("basicInfoHashtable");//监听信息
		
		WritableWorkbook wb = null;
		try {
			fileName = ResourceCenter.getInstance().getSysPath() + filename;
			wb = Workbook.createWorkbook(new File(fileName));

			WritableSheet sheet = null;
			sheet = wb.createSheet("MQ通道和监听信息报表", 0);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Label tmpLabel = null;
			tmpLabel = new Label(0, 0, "MQ("+vo.getIpaddress()+")通道和监听信息报表", labelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, 0, 7, 0);
			
			tmpLabel = new Label(0, 1, "报表生成时间:" + impReport.getTimeStamp());
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 3, "通道信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 4, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 4, "通道名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, 4, "通道状态", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, 4, "接收的缓存数", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, 4, "发送的缓存数", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(5, 4, "接收的字节数", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(6, 4, "发送的字节数", _labelFormat);
			sheet.addCell(tmpLabel);
			
			int index = 5 ;
			if (channel != null && channel.size()>0){
		 		for(int i=0;i<channel.size();i++){
		 			Hashtable tmpHashtable = (Hashtable)channel.get(i);
		 			if (tmpHashtable != null && tmpHashtable.size()>0){
		 				String chstatusname = (String)tmpHashtable.get("chstatusname");
						String bufsrcvd = (String)tmpHashtable.get("bufsrcvd");
						String bufssent = (String)tmpHashtable.get("bufssent");
						String bytsrcvd = (String)tmpHashtable.get("bytsrcvd");
						String bytssent = (String)tmpHashtable.get("bytssent");
						String status = (String)tmpHashtable.get("status");
						
						tmpLabel = new Label(0, 5+i, (i+1)+"", b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(1, 5+i, chstatusname, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(2, 5+i, bufsrcvd, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(3, 5+i, bufssent, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(4, 5+i, bytsrcvd, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(5, 5+i, bytssent, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(6, 5+i, status, _labelFormat);
						sheet.addCell(tmpLabel);
						
						index = 5+i;
		 			}
		 		}
			}
		
			index = index+2;
			tmpLabel = new Label(0, index, "监听信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			tmpLabel = new Label(0, index, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, index, "名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, index, "监听器支持的并发连接请求的数量", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, index, "端口", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, index, "状态", _labelFormat);
			sheet.addCell(tmpLabel);
			
			index = index+1;
			if(null != monitor && monitor.size() > 0){
				String statu = (String)monitor.get("statu");
				if("null".equals(statu)){statu = "";}
				String listener = (String)monitor.get("listener");
				if("null".equals(listener)){listener = "";}
				String backlog = (String)monitor.get("backlog");
				if("null".equals(backlog)){backlog = "";}
				String port = (String)monitor.get("port");
				if("null".equals(port)){port = "";}
				
				
				tmpLabel = new Label(0, index, "1", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(1, index, listener, _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(2, index, backlog, _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(3, index, port, _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(4, index, statu, _labelFormat);
				sheet.addCell(tmpLabel);
			}
//			index = index+2;
//
//			tmpLabel = new Label(0, index, "远程队列信息", b_labelFormat);
//			sheet.addCell(tmpLabel);
//			index = index+1;
//			tmpLabel = new Label(0, index, "序号", b_labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(1, index, "队列名称", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(2, index, "队列类型", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(3, index, "远程队列名称", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(4, index, "管理器名称", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(5, index, "传输队列名称", _labelFormat);
//			sheet.addCell(tmpLabel);
//			index = index+1;
//			if (remote != null && remote.size()>0){
//		 		for(int i=0;i<remote.size();i++){
//		 			Hashtable tmpHashtable = (Hashtable)remote.get(i);
//		 			if (tmpHashtable != null && tmpHashtable.size()>0){ 
//		 				String queue = (String)tmpHashtable.get("queue");
//						String type = (String)tmpHashtable.get("type");
//						String rqmname = (String)tmpHashtable.get("rqmname");
//						String rname = (String)tmpHashtable.get("rname");
//		 				String xmitq = (String)tmpHashtable.get("xmitq");
//		 				
//		 				tmpLabel = new Label(0, index+i, (i+1)+"", b_labelFormat);
//		 				sheet.addCell(tmpLabel);
//		 				tmpLabel = new Label(1, index+i, queue, _labelFormat);
//		 				sheet.addCell(tmpLabel);
//		 				tmpLabel = new Label(2, index+i, "远程队列", _labelFormat);
//		 				sheet.addCell(tmpLabel);
//		 				tmpLabel = new Label(3, index+i, rqmname, _labelFormat);
//		 				sheet.addCell(tmpLabel);
//		 				tmpLabel = new Label(4, index+i, rname, _labelFormat);
//		 				sheet.addCell(tmpLabel);
//		 				tmpLabel = new Label(5, index+i, xmitq, _labelFormat);
//		 				sheet.addCell(tmpLabel);
//		 			}
//		 		}
//		 		index = index+remote.size();
//			}
//			
//			index = index+1;
//			tmpLabel = new Label(0, index, "本地队列信息", b_labelFormat);
//			sheet.addCell(tmpLabel);
//			index = index+1;
//			tmpLabel = new Label(0, index, "序号", b_labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(1, index, "队列名称", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(2, index, "队列类型", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(3, index, "当前深度", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(4, index, "最大深度", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(5, index, "当前深度百分比", _labelFormat);
//			sheet.addCell(tmpLabel);
//			index=index+1;
//			
//			if (local != null && local.size()>0){
//		 		for(int i=0;i<local.size();i++){
//		 			Hashtable tmpHashtable = (Hashtable)local.get(i);
//		 			if (tmpHashtable != null && tmpHashtable.size() > 0){
//		 				String queue = (String)tmpHashtable.get("queue");
//						String curdepth = (String)tmpHashtable.get("curdepth");
//						String maxdepth = (String)tmpHashtable.get("maxdepth");
//						String type = (String)tmpHashtable.get("type");		
//						String percent = (String)tmpHashtable.get("percent");
//						
//						tmpLabel = new Label(0, index+i, (i+1)+"", b_labelFormat);
//		 				sheet.addCell(tmpLabel);
//		 				tmpLabel = new Label(1, index+i, queue, _labelFormat);
//		 				sheet.addCell(tmpLabel);
//		 				tmpLabel = new Label(2, index+i, "本地队列", _labelFormat);
//		 				sheet.addCell(tmpLabel);
//		 				tmpLabel = new Label(3, index+i, curdepth, _labelFormat);
//		 				sheet.addCell(tmpLabel);
//		 				tmpLabel = new Label(4, index+i, maxdepth, _labelFormat);
//		 				sheet.addCell(tmpLabel);
//		 				tmpLabel = new Label(5, index+i, percent, _labelFormat);
//		 				sheet.addCell(tmpLabel);
//		 			}
//		 		}
//			}
			
			
			
			wb.write();
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		}
	}
	
	public void createReportxls_mq_queue(String filename,String id) {
		if (impReport.getTable() == null) {
			fileName = null;
			return;
		}
		  Hashtable rValue = new Hashtable();
		  Vector mqValue = new Vector();//ping信息
	      
	      MQConfig vo = new MQConfig();
	      String runmodel = PollingEngine.getCollectwebflag(); 
		  MQConfigDao configdao = new MQConfigDao();
			
	      try{
	      	vo = (MQConfig)configdao.findByID(id);
	      	String ip = vo.getIpaddress();
	      	if("0".equals(runmodel)){
	             	//采集与访问是集成模式	
		        	Hashtable allMqValues = ShareData.getMqdata();
		        	if (allMqValues != null && allMqValues.size()>0){
						rValue = (Hashtable)allMqValues.get(ip+":"+vo.getManagername());
		        	}
	      	}else{
	      	 	//采集与访问是分离模式
	      		MQInfoService mqInfoService = new MQInfoService();
	      		rValue = mqInfoService.getMQDataHashtable(vo.getId()+"");
	      	}
	      }catch(Exception e){
	      	e.printStackTrace();
	      }finally{
	      	configdao.close();
	      }
		  if(mqValue == null)mqValue = new Vector();
	      List channel = (List)rValue.get("chstatusList");//通道信息
		  List local = (List)rValue.get("localQueueList");//本地队列信息
		  List remote = (List)rValue.get("remoteQueueList");//远程队列信息
		  Hashtable monitor = (Hashtable)rValue.get("basicInfoHashtable");//监听信息
		
		WritableWorkbook wb = null;
		try {
			fileName = ResourceCenter.getInstance().getSysPath() + filename;
			wb = Workbook.createWorkbook(new File(fileName));

			WritableSheet sheet = null;
			sheet = wb.createSheet("MQ队列信息报表", 0);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Label tmpLabel = null;
			tmpLabel = new Label(0, 0, "MQ("+vo.getIpaddress()+")队列信息报表", labelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, 0, 7, 0);
			
			tmpLabel = new Label(0, 1, "报表生成时间:" + impReport.getTimeStamp());
			sheet.addCell(tmpLabel);
			
//			tmpLabel = new Label(0, 3, "通道信息", b_labelFormat);
//			sheet.addCell(tmpLabel);
//			
//			tmpLabel = new Label(0, 4, "序号", b_labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(1, 4, "通道名称", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(2, 4, "通道状态", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(3, 4, "接收的缓存数", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(4, 4, "发送的缓存数", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(5, 4, "接收的字节数", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(6, 4, "发送的字节数", _labelFormat);
//			sheet.addCell(tmpLabel);
			
			int index = 5 ;
//			if (channel != null && channel.size()>0){
//		 		for(int i=0;i<channel.size();i++){
//		 			Hashtable tmpHashtable = (Hashtable)channel.get(i);
//		 			if (tmpHashtable != null && tmpHashtable.size()>0){
//		 				String chstatusname = (String)tmpHashtable.get("chstatusname");
//						String bufsrcvd = (String)tmpHashtable.get("bufsrcvd");
//						String bufssent = (String)tmpHashtable.get("bufssent");
//						String bytsrcvd = (String)tmpHashtable.get("bytsrcvd");
//						String bytssent = (String)tmpHashtable.get("bytssent");
//						String status = (String)tmpHashtable.get("status");
//						
//						tmpLabel = new Label(0, 5+i, (i+1)+"", b_labelFormat);
//						sheet.addCell(tmpLabel);
//						tmpLabel = new Label(1, 5+i, chstatusname, _labelFormat);
//						sheet.addCell(tmpLabel);
//						tmpLabel = new Label(2, 5+i, bufsrcvd, _labelFormat);
//						sheet.addCell(tmpLabel);
//						tmpLabel = new Label(3, 5+i, bufssent, _labelFormat);
//						sheet.addCell(tmpLabel);
//						tmpLabel = new Label(4, 5+i, bytsrcvd, _labelFormat);
//						sheet.addCell(tmpLabel);
//						tmpLabel = new Label(5, 5+i, bytssent, _labelFormat);
//						sheet.addCell(tmpLabel);
//						tmpLabel = new Label(6, 5+i, status, _labelFormat);
//						sheet.addCell(tmpLabel);
//						
//						index = 5+i;
//		 			}
//		 		}
//			}
//		
//			index = index+2;
//			tmpLabel = new Label(0, index, "监听信息", b_labelFormat);
//			sheet.addCell(tmpLabel);
//			index = index+1;
//			tmpLabel = new Label(0, index, "序号", b_labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(1, index, "名称", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(2, index, "监听器支持的并发连接请求的数量", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(3, index, "端口", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(4, index, "状态", _labelFormat);
//			sheet.addCell(tmpLabel);
//			
//			index = index+1;
//			if(null != monitor && monitor.size() > 0){
//				String statu = (String)monitor.get("statu");
//				if("null".equals(statu)){statu = "";}
//				String listener = (String)monitor.get("listener");
//				if("null".equals(listener)){listener = "";}
//				String backlog = (String)monitor.get("backlog");
//				if("null".equals(backlog)){backlog = "";}
//				String port = (String)monitor.get("port");
//				if("null".equals(port)){port = "";}
//				
//				
//				tmpLabel = new Label(0, index, "1", b_labelFormat);
//				sheet.addCell(tmpLabel);
//				tmpLabel = new Label(1, index, listener, _labelFormat);
//				sheet.addCell(tmpLabel);
//				tmpLabel = new Label(2, index, backlog, _labelFormat);
//				sheet.addCell(tmpLabel);
//				tmpLabel = new Label(3, index, port, _labelFormat);
//				sheet.addCell(tmpLabel);
//				tmpLabel = new Label(4, index, statu, _labelFormat);
//				sheet.addCell(tmpLabel);
//			}
//			index = index+2;

			tmpLabel = new Label(0, index, "远程队列信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			tmpLabel = new Label(0, index, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, index, "队列名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, index, "队列类型", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, index, "远程队列名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, index, "管理器名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(5, index, "传输队列名称", _labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			if (remote != null && remote.size()>0){
		 		for(int i=0;i<remote.size();i++){
		 			Hashtable tmpHashtable = (Hashtable)remote.get(i);
		 			if (tmpHashtable != null && tmpHashtable.size()>0){ 
		 				String queue = (String)tmpHashtable.get("queue");
						String type = (String)tmpHashtable.get("type");
						String rqmname = (String)tmpHashtable.get("rqmname");
						String rname = (String)tmpHashtable.get("rname");
		 				String xmitq = (String)tmpHashtable.get("xmitq");
		 				
		 				tmpLabel = new Label(0, index+i, (i+1)+"", b_labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(1, index+i, queue, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(2, index+i, "远程队列", _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(3, index+i, rqmname, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(4, index+i, rname, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(5, index+i, xmitq, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 			}
		 		}
		 		index = index+remote.size();
			}
			
			index = index+1;
			tmpLabel = new Label(0, index, "本地队列信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			tmpLabel = new Label(0, index, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, index, "队列名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, index, "队列类型", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, index, "当前深度", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, index, "最大深度", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(5, index, "当前深度百分比", _labelFormat);
			sheet.addCell(tmpLabel);
			index=index+1;
			
			if (local != null && local.size()>0){
		 		for(int i=0;i<local.size();i++){
		 			Hashtable tmpHashtable = (Hashtable)local.get(i);
		 			if (tmpHashtable != null && tmpHashtable.size() > 0){
		 				String queue = (String)tmpHashtable.get("queue");
						String curdepth = (String)tmpHashtable.get("curdepth");
						String maxdepth = (String)tmpHashtable.get("maxdepth");
						String type = (String)tmpHashtable.get("type");		
						String percent = (String)tmpHashtable.get("percent");
						
						tmpLabel = new Label(0, index+i, (i+1)+"", b_labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(1, index+i, queue, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(2, index+i, "本地队列", _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(3, index+i, curdepth, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(4, index+i, maxdepth, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 				tmpLabel = new Label(5, index+i, percent, _labelFormat);
		 				sheet.addCell(tmpLabel);
		 			}
		 		}
			}
			
			
			
			wb.write();
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		}
	}
	
	
	
	 //was  綜合报表
	public void createReportDoc_was_all(String file, String type,String id) throws DocumentException, IOException {
		
		   String runmodel = PollingEngine.getCollectwebflag(); 
		   
		      WasConfig vo = new WasConfig();
			  WasConfigDao configdao = new WasConfigDao();
				
		      vo = (WasConfig) configdao.findByID(id);
			   if(vo == null){
					vo = new WasConfig();
			   }
			HashMap system = null;
			HashMap jvm = null;
			HashMap cache = null;
			HashMap jdbc = null;
			HashMap Session = null;
			HashMap thread = null;
			HashMap thing = null;
			Hashtable wasIpaddressData = null;
		   String ip = vo.getIpaddress().replace(".","_");
		   if("0".equals(runmodel)){
		   		  //采集与访问是集成模式
				  Hashtable wasHashtables = (Hashtable)com.afunms.common.util.ShareData.getWasdata();
				  wasIpaddressData = (Hashtable)wasHashtables.get(vo.getIpaddress());
				  System.out.println("wasIpaddressData:"+wasIpaddressData);
				  if(wasIpaddressData != null){
				  if(vo.getVersion().equalsIgnoreCase("v5")){
					  Hashtable systemHashtable = (Hashtable)wasIpaddressData.get("systemDatahst");
					  Hashtable cacheHashtable = (Hashtable)wasIpaddressData.get("cachehst");
					  Hashtable jvmHashtable = (Hashtable)wasIpaddressData.get("jvmhst");
					  Hashtable jdbcHashtable = (Hashtable)wasIpaddressData.get("jdbchst");
					  Hashtable servletHashtable = (Hashtable)wasIpaddressData.get("servlethst");
					  Hashtable transHashtable = (Hashtable)wasIpaddressData.get("transhst");
					  Hashtable threadHashtable = (Hashtable)wasIpaddressData.get("threadhst");
				      thread = CommonUtil.converHashTableToHashMap(threadHashtable); 
				      thing = CommonUtil.converHashTableToHashMap(transHashtable); 
				      Session = CommonUtil.converHashTableToHashMap(servletHashtable); 
				      jdbc = CommonUtil.converHashTableToHashMap(jdbcHashtable);
				      jvm = CommonUtil.converHashTableToHashMap(jvmHashtable); 
				      cache = CommonUtil.converHashTableToHashMap(cacheHashtable); 
					  system = CommonUtil.converHashTableToHashMap(systemHashtable); 
				   }else{
					  Hashtable systemHashtable = (Hashtable)wasIpaddressData.get("system7hst");
					  Hashtable cacheHashtable = (Hashtable)wasIpaddressData.get("extension7hst");
					  Hashtable jvmHashtable = (Hashtable)wasIpaddressData.get("jvm7hst");
					  Hashtable jdbcHashtable = (Hashtable)wasIpaddressData.get("jdbc7hst");
					  Hashtable servletHashtable = (Hashtable)wasIpaddressData.get("servlet7hst");
					  Hashtable transHashtable = (Hashtable)wasIpaddressData.get("trans7hst");
					  Hashtable threadHashtable = (Hashtable)wasIpaddressData.get("thread7hst");
				      thread = CommonUtil.converHashTableToHashMap(threadHashtable); 
				      thing = CommonUtil.converHashTableToHashMap(transHashtable); 
				      Session = CommonUtil.converHashTableToHashMap(servletHashtable); 
				      jdbc = CommonUtil.converHashTableToHashMap(jdbcHashtable);
				      jvm = CommonUtil.converHashTableToHashMap(jvmHashtable); 
				      cache = CommonUtil.converHashTableToHashMap(cacheHashtable); 
					  system = CommonUtil.converHashTableToHashMap(systemHashtable); 
					  }
				  }
		  	}else{
		  		 //采集与访问分离模式
		  		 GetWasInfo wasconf = new GetWasInfo();
		  		 try{
				 	 system = wasconf.executeQueryHashMap(vo.getIpaddress(),"wassystem");
				 	 Session = wasconf.executeQueryHashMap(vo.getIpaddress(),"wassession");
				 	 cache = wasconf.executeQueryHashMap(vo.getIpaddress(),"wascache");
				 	 jdbc = wasconf.executeQueryHashMap(vo.getIpaddress(),"wasjdbc");
				 	 thread = wasconf.executeQueryHashMap(vo.getIpaddress(),"wasthread");
				 	 thing = wasconf.executeQueryHashMap(vo.getIpaddress(),"wastrans");
				 	 jvm = wasconf.executeQueryHashMap(vo.getIpaddress(),"wasjvm");
			 	 }catch(Exception e){
				 	e.printStackTrace();
				 }finally{
				 	 wasconf.close();
				 }
		  	}
		    
		   if(system == null)system = new HashMap();
		   if(jvm == null)jvm = new HashMap();
		   if(thing == null)thing = new HashMap();
		   if(Session == null)Session = new HashMap();
		   if(cache == null)cache = new HashMap();
		   if(jdbc == null)jdbc = new HashMap();
		   if(thread == null)thread = new HashMap();
		   
		   String FreeMemory ="";
		   String CPUUsageSinceServerStarted ="";
		   String CPUUsageSinceLastMeasurement ="";
		   
		   
		   String freeMemory = "";
		   String heapSize = "";
		   String upTime = "";
		   String usedMemory = "";
		   String memPer = "";
		   
	      
		   String inMemoryCacheCount = "";
		   String timeoutInvalidationCount = "";
		   String maxInMemoryCacheCount = "";
		   
		   
		   String activeCount = "";
		   String createCount = "";
		   String invalidateCount = "";
		   String lifeTime = "";
		   String liveCount = "";
		   String SessiontimeoutInvalidationCount = "";
		  
		   
		   String freePoolSize = "";
		   String useTime = "";
		   String prepStmtCacheDiscardCount = "";
		   String waitingThreadCount = "";
		   String allocateCount = "";
		   String faultCount = "";
		   String waitTime = "";
		   String createCountjdbc = "";
		   String jdbcTime = "";
		   String percentUsed = "";
		   String poolSize = "";
		   String closeCount = "";
		  
		   
		   String activeCountthread = "";
		   String createCountthread = "";
		   String destroyCount = "";
		   String poolSizethread = "";
		   
		   
		   String activeCountthing = "";
		   String committedCount = "";
		   String globalBegunCount = "";
		   String globalTimeoutCount = "";
		   String globalTranTime = "";
		   String localActiveCount = "";
		   String localBegunCount = "";
		   String localRolledbackCount = "";
		   String localTimeoutCount = "";
		   String localTranTime = "";
		   String rolledbackCount = "";
		   
		   
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   
			
			/* String type = (String)request.getAttribute("type"); */
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			if ("pdf".equals(type)) {
				PdfWriter.getInstance(document, new FileOutputStream(file));
			} else {
				RtfWriter2.getInstance(document, new FileOutputStream(file));
			}
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Paragraph title = new Paragraph("Was  "+vo.getVersion()+"("+vo.getIpaddress()+")综合信息报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			// title.setFont(titleFont);
			document.add(title);
			
			String contextString = "报表生成时间:" + impReport.getTimeStamp() + " \n";// 换行;
	        Paragraph context = new Paragraph(contextString);
			// 正文字体风格
			Font contextFont = new Font(bfChinese, 12, Font.NORMAL);

			Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
			context.setAlignment(Element.ALIGN_LEFT);
			// 离上一段落（标题）空的行数
			context.setSpacingBefore(5);
			// 设置第一行空的列数
			context.setFirstLineIndent(5);
			document.add(context);
			document.add(new Phrase("\n"));
			
			
			
			Table aTable = new Table(4);
			this.setTableFormat(aTable);
			Cell cell = null;
			//----------------------System信息
			if(system != null){
				   FreeMemory = (String) system.get("FreeMemory");
				   CPUUsageSinceServerStarted = (String) system.get("CPUUsageSinceServerStarted");
				   CPUUsageSinceLastMeasurement = (String) system.get("CPUUsageSinceLastMeasurement");
			   }
			cell = new Cell(new Phrase("System信息", titleFont));
			cell.setColspan(4);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("空闲内存的快照(KB)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(FreeMemory, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("自服务器启动以来的CPU平均使用率", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(CPUUsageSinceServerStarted, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("自上次查询以来的平均CPU使用率", titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(CPUUsageSinceLastMeasurement, titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			//----------------------JVM信息
			if(jvm != null){
				   freeMemory = (String) jvm.get("freeMemory");
				   heapSize = (String) jvm.get("heapSize");
				   upTime = (String) jvm.get("upTime");
				   usedMemory = (String) jvm.get("usedMemory");
				   memPer = (String) jvm.get("memPer");
			   }
			cell = new Cell(new Phrase("JVM信息", titleFont));
			cell.setColspan(4);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("Java虚拟机运行时空闲的内存(KB)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(freeMemory, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("Java虚拟机运行时总内存(KB)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(heapSize, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("已经运行的时间数(秒)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(upTime, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("Java虚拟机运行时使用的内存(KB)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(usedMemory, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("Java虚拟机内存利用率", titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(memPer, titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);			
			
			//----------------------cache信息
			if(cache != null){
				   inMemoryCacheCount = (String) cache.get("inMemoryCacheCount");
				   timeoutInvalidationCount = (String) cache.get("timeoutInvalidationCount");
				   maxInMemoryCacheCount = (String) cache.get("maxInMemoryCacheCount");
			   }
			cell = new Cell(new Phrase("cache信息", titleFont));
			cell.setColspan(4);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("内存中的高速缓存条目当前数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(inMemoryCacheCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("高速缓存条目最大数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(timeoutInvalidationCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("超时数", titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(maxInMemoryCacheCount, titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);	
			
			
			//----------------------session信息
			 if(Session != null){
				   activeCount = (String) Session.get("activeCount");
				   createCount = (String) Session.get("createCount");
				   invalidateCount = (String) Session.get("invalidateCount");
				   lifeTime = (String) Session.get("lifeTime");
				   liveCount = (String) Session.get("liveCount");
				   SessiontimeoutInvalidationCount = (String) Session.get("timeoutInvalidationCount");
			   }
			cell = new Cell(new Phrase("session信息", titleFont));
			cell.setColspan(4);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("当前存活的会话总数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(activeCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("创建的会话数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(createCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("失效的会话数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(invalidateCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("平均会话周期（毫秒）", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(lifeTime, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("分配的连接的总数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(liveCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("超时失效的会话数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(SessiontimeoutInvalidationCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			//----------------------jdbc信息
			 if(jdbc != null){
				   freePoolSize = (String) jdbc.get("freePoolSize");
				   useTime = (String) jdbc.get("useTime");
				   prepStmtCacheDiscardCount = (String) jdbc.get("prepStmtCacheDiscardCount");
				   waitingThreadCount = (String) jdbc.get("waitingThreadCount");
				   allocateCount = (String) jdbc.get("allocateCount");
				   faultCount = (String) jdbc.get("faultCount");
				   waitTime = (String) jdbc.get("waitTime");
				   createCountjdbc = (String) jdbc.get("createCount");
				   jdbcTime = (String) jdbc.get("jdbcTime");
				   percentUsed = (String) jdbc.get("percentUsed");
				   poolSize = (String) jdbc.get("poolSize");
				   closeCount = (String) jdbc.get("closeCount");
			   }
			cell = new Cell(new Phrase("jdbc信息", titleFont));
			cell.setColspan(4);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("池中的空闲连接数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(freePoolSize, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("使用连接的平均时间(毫秒)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(useTime, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("高速缓存已满而废弃的语句数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(prepStmtCacheDiscardCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("等待连接的平均并发线程数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(waitingThreadCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("分配的连接的总数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(allocateCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("池中的连接超时数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(faultCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(" 允许连接之前的平均等待时间(毫秒)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(waitTime, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("创建连接的总数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(createCountjdbc, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("运行JDBC平均调用时间(毫秒)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(jdbcTime, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("池的平均使用率", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(percentUsed, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("连接池的大小", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(poolSize, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("已关闭的连接的总数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(closeCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			//----------------------thread信息
			if(thread != null){
				   activeCountthread = (String) thread.get("activeCount");
				   createCountthread = (String) thread.get("createCount");
				   destroyCount = (String) thread.get("destroyCount");
				   poolSizethread = (String) thread.get("poolSize");
			   }
			cell = new Cell(new Phrase("thread信息", titleFont));
			cell.setColspan(4);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("并发活动的线程数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(activeCountthread, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("创建线程的总数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(createCountthread, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("销毁线程的总数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(destroyCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("池中线程的平均数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(poolSizethread, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			//----------------------事物信息
			if(thing != null){
				   activeCountthing = (String) thing.get("activeCount");
				   committedCount = (String) thing.get("committedCount");
				   globalBegunCount = (String) thing.get("globalBegunCount");
				   globalTranTime = (String) thing.get("globalTranTime");
				   localActiveCount = (String) thing.get("localActiveCount");
				   localBegunCount = (String) thing.get("localBegunCount");
				   localRolledbackCount = (String) thing.get("localRolledbackCount");
				   localTimeoutCount = (String) thing.get("localTimeoutCount");
				   localTranTime = (String) thing.get("localTranTime");
				   rolledbackCount = (String) thing.get("rolledbackCount");
				   globalTimeoutCount = (String) thing.get("globalTimeoutCount");
			   }
			cell = new Cell(new Phrase("事物信息", titleFont));
			cell.setColspan(4);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("并发活动的全局事务数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(activeCountthing, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("已提交的全局事务数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(committedCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("在服务器上开始的全局事务数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(globalBegunCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("超时的全局事务数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(globalTimeoutCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("全局事务平均持续时间（毫秒）: ", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(globalTranTime, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("并发活动的本地事务数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(localActiveCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("服务器上开始的本地事务数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(localBegunCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("回滚的本地事务数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(localTimeoutCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(" 超时的本地事务数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(localTimeoutCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("本地事务的平均持续时间（毫秒）: ", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(localTranTime, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("回滚的全局事务数", titleFont));
			this.setCellFormat(cell, false);
			cell.setColspan(2);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(globalTimeoutCount, titleFont));
			this.setCellFormat(cell, false);
			cell.setColspan(2);
			aTable.addCell(cell);
			
			document.add(aTable);
			document.close();
	}
	
	public void createReportxls_was_all(String filename,String id) {
		if (impReport.getTable() == null) {
			fileName = null;
			return;
		}
		  
		
		String runmodel = PollingEngine.getCollectwebflag(); 
		   
		  WasConfig vo = new WasConfig();
		  WasConfigDao configdao = new WasConfigDao();
			
	      	vo = (WasConfig) configdao.findByID(id);
		   if(vo == null){
				vo = new WasConfig();
		   }
			HashMap system = null;
			HashMap jvm = null;
			HashMap cache = null;
			HashMap jdbc = null;
			HashMap Session = null;
			HashMap thread = null;
			HashMap thing = null;
			Hashtable wasIpaddressData = null;
		   String ip = vo.getIpaddress().replace(".","_");
		   if("0".equals(runmodel)){
		   		  //采集与访问是集成模式
				  Hashtable wasHashtables = (Hashtable)com.afunms.common.util.ShareData.getWasdata();
				  wasIpaddressData = (Hashtable)wasHashtables.get(vo.getIpaddress());
				  System.out.println("wasIpaddressData:"+wasIpaddressData);
				  if(wasIpaddressData != null){
				  if(vo.getVersion().equalsIgnoreCase("v5")){
					  Hashtable systemHashtable = (Hashtable)wasIpaddressData.get("systemDatahst");
					  Hashtable cacheHashtable = (Hashtable)wasIpaddressData.get("cachehst");
					  Hashtable jvmHashtable = (Hashtable)wasIpaddressData.get("jvmhst");
					  Hashtable jdbcHashtable = (Hashtable)wasIpaddressData.get("jdbchst");
					  Hashtable servletHashtable = (Hashtable)wasIpaddressData.get("servlethst");
					  Hashtable transHashtable = (Hashtable)wasIpaddressData.get("transhst");
					  Hashtable threadHashtable = (Hashtable)wasIpaddressData.get("threadhst");
				      thread = CommonUtil.converHashTableToHashMap(threadHashtable); 
				      thing = CommonUtil.converHashTableToHashMap(transHashtable); 
				      Session = CommonUtil.converHashTableToHashMap(servletHashtable); 
				      jdbc = CommonUtil.converHashTableToHashMap(jdbcHashtable);
				      jvm = CommonUtil.converHashTableToHashMap(jvmHashtable); 
				      cache = CommonUtil.converHashTableToHashMap(cacheHashtable); 
					  system = CommonUtil.converHashTableToHashMap(systemHashtable); 
				   }else{
					  Hashtable systemHashtable = (Hashtable)wasIpaddressData.get("system7hst");
					  Hashtable cacheHashtable = (Hashtable)wasIpaddressData.get("extension7hst");
					  Hashtable jvmHashtable = (Hashtable)wasIpaddressData.get("jvm7hst");
					  Hashtable jdbcHashtable = (Hashtable)wasIpaddressData.get("jdbc7hst");
					  Hashtable servletHashtable = (Hashtable)wasIpaddressData.get("servlet7hst");
					  Hashtable transHashtable = (Hashtable)wasIpaddressData.get("trans7hst");
					  Hashtable threadHashtable = (Hashtable)wasIpaddressData.get("thread7hst");
				      thread = CommonUtil.converHashTableToHashMap(threadHashtable); 
				      thing = CommonUtil.converHashTableToHashMap(transHashtable); 
				      Session = CommonUtil.converHashTableToHashMap(servletHashtable); 
				      jdbc = CommonUtil.converHashTableToHashMap(jdbcHashtable);
				      jvm = CommonUtil.converHashTableToHashMap(jvmHashtable); 
				      cache = CommonUtil.converHashTableToHashMap(cacheHashtable); 
					  system = CommonUtil.converHashTableToHashMap(systemHashtable); 
					  }
				  }
		  	}else{
		  		 //采集与访问分离模式
		  		 GetWasInfo wasconf = new GetWasInfo();
		  		 try{
				 	 system = wasconf.executeQueryHashMap(vo.getIpaddress(),"wassystem");
				 	 Session = wasconf.executeQueryHashMap(vo.getIpaddress(),"wassession");
				 	 cache = wasconf.executeQueryHashMap(vo.getIpaddress(),"wascache");
				 	 jdbc = wasconf.executeQueryHashMap(vo.getIpaddress(),"wasjdbc");
				 	 thread = wasconf.executeQueryHashMap(vo.getIpaddress(),"wasthread");
				 	 thing = wasconf.executeQueryHashMap(vo.getIpaddress(),"wastrans");
				 	 jvm = wasconf.executeQueryHashMap(vo.getIpaddress(),"wasjvm");
			 	 }catch(Exception e){
				 	e.printStackTrace();
				 }finally{
				 	 wasconf.close();
				 }
		  	}
		    
		   if(system == null)system = new HashMap();
		   if(jvm == null)jvm = new HashMap();
		   if(thing == null)thing = new HashMap();
		   if(Session == null)Session = new HashMap();
		   if(cache == null)cache = new HashMap();
		   if(jdbc == null)jdbc = new HashMap();
		   if(thread == null)thread = new HashMap();
		   
		   String FreeMemory ="";
		   String CPUUsageSinceServerStarted ="";
		   String CPUUsageSinceLastMeasurement ="";
		   
		   
		   String freeMemory = "";
		   String heapSize = "";
		   String upTime = "";
		   String usedMemory = "";
		   String memPer = "";
		   
	      
		   String inMemoryCacheCount = "";
		   String timeoutInvalidationCount = "";
		   String maxInMemoryCacheCount = "";
		   
		   
		   String activeCount = "";
		   String createCount = "";
		   String invalidateCount = "";
		   String lifeTime = "";
		   String liveCount = "";
		   String SessiontimeoutInvalidationCount = "";
		  
		   
		   String freePoolSize = "";
		   String useTime = "";
		   String prepStmtCacheDiscardCount = "";
		   String waitingThreadCount = "";
		   String allocateCount = "";
		   String faultCount = "";
		   String waitTime = "";
		   String createCountjdbc = "";
		   String jdbcTime = "";
		   String percentUsed = "";
		   String poolSize = "";
		   String closeCount = "";
		  
		   
		   String activeCountthread = "";
		   String createCountthread = "";
		   String destroyCount = "";
		   String poolSizethread = "";
		   
		   
		   String activeCountthing = "";
		   String committedCount = "";
		   String globalBegunCount = "";
		   String globalTimeoutCount = "";
		   String globalTranTime = "";
		   String localActiveCount = "";
		   String localBegunCount = "";
		   String localRolledbackCount = "";
		   String localTimeoutCount = "";
		   String localTranTime = "";
		   String rolledbackCount = "";
		
		
		
		WritableWorkbook wb = null;
		try {
			fileName = ResourceCenter.getInstance().getSysPath() + filename;
			wb = Workbook.createWorkbook(new File(fileName));

			WritableSheet sheet = null;
			sheet = wb.createSheet("Was综合信息报表", 0);

			sheet.getSettings().setDefaultColumnWidth(30); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Label tmpLabel = null;
			tmpLabel = new Label(0, 0, "Was "+vo.getVersion()+"("+vo.getIpaddress()+")综合信息报表", labelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, 0, 4, 0);
			
			tmpLabel = new Label(0, 1, "报表生成时间:" + impReport.getTimeStamp());
			sheet.addCell(tmpLabel);
			
			//-----------------System信息
			if(system != null){
				   FreeMemory = (String) system.get("FreeMemory");
				   CPUUsageSinceServerStarted = (String) system.get("CPUUsageSinceServerStarted");
				   CPUUsageSinceLastMeasurement = (String) system.get("CPUUsageSinceLastMeasurement");
			   }
			tmpLabel = new Label(0, 3, "System信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 4, " 空闲内存的快照(KB)", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 4, FreeMemory, _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, 4, "自服务器启动以来的CPU平均使用率", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, 4, CPUUsageSinceServerStarted, _labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 5, "自上次查询以来的平均CPU使用率", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 5, CPUUsageSinceLastMeasurement, _labelFormat);
			sheet.addCell(tmpLabel);
			
			
			//-----------------jvm信息
				if(jvm != null){
					   freeMemory = (String) jvm.get("freeMemory");
					   heapSize = (String) jvm.get("heapSize");
					   upTime = (String) jvm.get("upTime");
					   usedMemory = (String) jvm.get("usedMemory");
					   memPer = (String) jvm.get("memPer");
				   }
			tmpLabel = new Label(0, 7, "Jvm信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 8, "Java虚拟机运行时空闲的内存(KB)", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 8, freeMemory, _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, 8, " Java虚拟机运行时总内存(KB)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, 8, heapSize, _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(0, 9, " 已经运行的时间数(秒)", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 9, upTime, _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, 9, " Java虚拟机运行时使用的内存(KB)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, 9, usedMemory, _labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 10, "Java虚拟机内存利用率", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 10, memPer, _labelFormat);
			sheet.addCell(tmpLabel);
			
			
			//----------------------cache信息
			if(cache != null){
				   inMemoryCacheCount = (String) cache.get("inMemoryCacheCount");
				   timeoutInvalidationCount = (String) cache.get("timeoutInvalidationCount");
				   maxInMemoryCacheCount = (String) cache.get("maxInMemoryCacheCount");
			   }
			
			tmpLabel = new Label(0, 12, "Cache信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 13, "内存中的高速缓存条目当前数", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 13, freeMemory, _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, 13, "高速缓存条目最大数", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, 13, heapSize, _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(0, 14, "超时数", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 14, heapSize, _labelFormat);
			sheet.addCell(tmpLabel);
			
			//----------------------session信息
			 if(Session != null){
				   activeCount = (String) Session.get("activeCount");
				   createCount = (String) Session.get("createCount");
				   invalidateCount = (String) Session.get("invalidateCount");
				   lifeTime = (String) Session.get("lifeTime");
				   liveCount = (String) Session.get("liveCount");
				   SessiontimeoutInvalidationCount = (String) Session.get("timeoutInvalidationCount");
			   }
			 
			    tmpLabel = new Label(0, 16, "Session信息", b_labelFormat);
				sheet.addCell(tmpLabel);
				
				tmpLabel = new Label(0, 17, "当前存活的会话总数", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(1, 17, activeCount, _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(2, 17, "创建的会话数", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(3, 17, createCount, _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(0, 18, "失效的会话数", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(1, 18, invalidateCount, _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(2, 18, "平均会话周期(毫秒)", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(3, 18, lifeTime, _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(0, 19, "分配的连接的总数", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(1, 19, liveCount, _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(2, 19, "超时失效的会话数", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(3, 19, SessiontimeoutInvalidationCount, _labelFormat);
				sheet.addCell(tmpLabel);
			 
			
				//----------------------jdbc信息
				 if(jdbc != null){
					   freePoolSize = (String) jdbc.get("freePoolSize");
					   useTime = (String) jdbc.get("useTime");
					   prepStmtCacheDiscardCount = (String) jdbc.get("prepStmtCacheDiscardCount");
					   waitingThreadCount = (String) jdbc.get("waitingThreadCount");
					   allocateCount = (String) jdbc.get("allocateCount");
					   faultCount = (String) jdbc.get("faultCount");
					   waitTime = (String) jdbc.get("waitTime");
					   createCountjdbc = (String) jdbc.get("createCount");
					   jdbcTime = (String) jdbc.get("jdbcTime");
					   percentUsed = (String) jdbc.get("percentUsed");
					   poolSize = (String) jdbc.get("poolSize");
					   closeCount = (String) jdbc.get("closeCount");
				   }
				
				    tmpLabel = new Label(0, 21, "Jdbc信息", b_labelFormat);
					sheet.addCell(tmpLabel);
					
					tmpLabel = new Label(0, 22, "池中的空闲连接数", b_labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(1, 22, freePoolSize, _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(2, 22, "使用连接的平均时间(毫秒)", _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(3, 22, useTime, _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(0, 23, "高速缓存已满而废弃的语句数", b_labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(1, 23, prepStmtCacheDiscardCount, _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(2, 23, "等待连接的平均并发线程数", _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(3, 23, waitingThreadCount, _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(0, 24, "分配的连接的总数", b_labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(1, 24, allocateCount, _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(2, 24, "池中的连接超时数", _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(3, 24, faultCount, _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(0, 25, "允许连接之前的平均等待时间(毫秒)", b_labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(1, 25, waitTime, _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(2, 25, "创建连接的总数", _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(3, 25, createCountjdbc, _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(0, 26, "运行JDBC平均调用时间(毫秒)", b_labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(1, 26, jdbcTime, _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(2, 26, "池的平均使用率", _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(3, 26, percentUsed, _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(0, 27, "连接池的大小", b_labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(1, 27, poolSize, _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(2, 27, "已关闭的连接的总数", _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(3, 27, closeCount, _labelFormat);
					sheet.addCell(tmpLabel);
				 
			
					//----------------------thread信息
					if(thread != null){
						   activeCountthread = (String) thread.get("activeCount");
						   createCountthread = (String) thread.get("createCount");
						   destroyCount = (String) thread.get("destroyCount");
						   poolSizethread = (String) thread.get("poolSize");
					   }
					 tmpLabel = new Label(0, 29, "Thread信息", b_labelFormat);
						sheet.addCell(tmpLabel);
						
						tmpLabel = new Label(0, 30, "并发活动的线程数", b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(1, 30, activeCountthread, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(2, 30, "创建线程的总数", _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(3, 30, createCountthread, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(0, 31, "销毁线程的总数", b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(1, 31, destroyCount, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(2, 31, "池中线程的平均数", _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(3, 31, poolSizethread, _labelFormat);
						sheet.addCell(tmpLabel);
			
			
						//----------------------事物信息
						if(thing != null){
							   activeCountthing = (String) thing.get("activeCount");
							   committedCount = (String) thing.get("committedCount");
							   globalBegunCount = (String) thing.get("globalBegunCount");
							   globalTranTime = (String) thing.get("globalTranTime");
							   localActiveCount = (String) thing.get("localActiveCount");
							   localBegunCount = (String) thing.get("localBegunCount");
							   localRolledbackCount = (String) thing.get("localRolledbackCount");
							   localTimeoutCount = (String) thing.get("localTimeoutCount");
							   localTranTime = (String) thing.get("localTranTime");
							   rolledbackCount = (String) thing.get("rolledbackCount");
							   globalTimeoutCount = (String) thing.get("globalTimeoutCount");
						   }
						
						tmpLabel = new Label(0, 33, "事物信息", b_labelFormat);
						sheet.addCell(tmpLabel);
						
						tmpLabel = new Label(0, 34, "并发活动的全局事务数", b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(1, 34, activeCountthread, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(2, 34, "已提交的全局事务数", _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(3, 34, createCountthread, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(0, 34, "在服务器上开始的全局事务数", b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(1, 34, activeCountthread, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(2, 34, "超时的全局事务数", _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(3, 34, createCountthread, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(0, 35, "全局事务平均持续时间(毫秒)", b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(1, 35, activeCountthread, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(2, 35, "并发活动的本地事务数", _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(3, 35, createCountthread, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(0, 36, "服务器上开始的本地事务数", b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(1, 36, activeCountthread, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(2, 36, "回滚的本地事务数", _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(3, 36, createCountthread, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(0, 37, "超时的本地事务数", b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(1, 37, activeCountthread, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(2, 37, "本地事务的平均持续时间(毫秒)", _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(3, 37, createCountthread, _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(0, 38, "回滚的全局事务数", b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(1, 38, activeCountthread, _labelFormat);
						sheet.addCell(tmpLabel);
						
			
			
			
			wb.write();
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		}
	}
	
	public void createReportDoc_was_per(String file, String type,String id) throws DocumentException, IOException {
		
		   String runmodel = PollingEngine.getCollectwebflag(); 
		   
		   WasConfig vo = new WasConfig();
			  WasConfigDao configdao = new WasConfigDao();
				
		      	vo = (WasConfig) configdao.findByID(id);
			   if(vo == null){
					vo = new WasConfig();
			   }
			HashMap system = null;
			HashMap jvm = null;
			HashMap cache = null;
			HashMap jdbc = null;
			HashMap Session = null;
			HashMap thread = null;
			HashMap thing = null;
			Hashtable wasIpaddressData = null;
		   String ip = vo.getIpaddress().replace(".","_");
		   if("0".equals(runmodel)){
		   		  //采集与访问是集成模式
				  Hashtable wasHashtables = (Hashtable)com.afunms.common.util.ShareData.getWasdata();
				  wasIpaddressData = (Hashtable)wasHashtables.get(vo.getIpaddress());
				  System.out.println("wasIpaddressData:"+wasIpaddressData);
				  if(wasIpaddressData != null){
				  if(vo.getVersion().equalsIgnoreCase("v5")){
					  Hashtable systemHashtable = (Hashtable)wasIpaddressData.get("systemDatahst");
					  Hashtable cacheHashtable = (Hashtable)wasIpaddressData.get("cachehst");
					  Hashtable jvmHashtable = (Hashtable)wasIpaddressData.get("jvmhst");
					  Hashtable jdbcHashtable = (Hashtable)wasIpaddressData.get("jdbchst");
					  Hashtable servletHashtable = (Hashtable)wasIpaddressData.get("servlethst");
					  Hashtable transHashtable = (Hashtable)wasIpaddressData.get("transhst");
					  Hashtable threadHashtable = (Hashtable)wasIpaddressData.get("threadhst");
				      thread = CommonUtil.converHashTableToHashMap(threadHashtable); 
				      thing = CommonUtil.converHashTableToHashMap(transHashtable); 
				      Session = CommonUtil.converHashTableToHashMap(servletHashtable); 
				      jdbc = CommonUtil.converHashTableToHashMap(jdbcHashtable);
				      jvm = CommonUtil.converHashTableToHashMap(jvmHashtable); 
				      cache = CommonUtil.converHashTableToHashMap(cacheHashtable); 
					  system = CommonUtil.converHashTableToHashMap(systemHashtable); 
				   }else{
					  Hashtable systemHashtable = (Hashtable)wasIpaddressData.get("system7hst");
					  Hashtable cacheHashtable = (Hashtable)wasIpaddressData.get("extension7hst");
					  Hashtable jvmHashtable = (Hashtable)wasIpaddressData.get("jvm7hst");
					  Hashtable jdbcHashtable = (Hashtable)wasIpaddressData.get("jdbc7hst");
					  Hashtable servletHashtable = (Hashtable)wasIpaddressData.get("servlet7hst");
					  Hashtable transHashtable = (Hashtable)wasIpaddressData.get("trans7hst");
					  Hashtable threadHashtable = (Hashtable)wasIpaddressData.get("thread7hst");
				      thread = CommonUtil.converHashTableToHashMap(threadHashtable); 
				      thing = CommonUtil.converHashTableToHashMap(transHashtable); 
				      Session = CommonUtil.converHashTableToHashMap(servletHashtable); 
				      jdbc = CommonUtil.converHashTableToHashMap(jdbcHashtable);
				      jvm = CommonUtil.converHashTableToHashMap(jvmHashtable); 
				      cache = CommonUtil.converHashTableToHashMap(cacheHashtable); 
					  system = CommonUtil.converHashTableToHashMap(systemHashtable); 
					  }
				  }
		  	}else{
		  		 //采集与访问分离模式
		  		 GetWasInfo wasconf = new GetWasInfo();
		  		 try{
				 	 system = wasconf.executeQueryHashMap(vo.getIpaddress(),"wassystem");
				 	 Session = wasconf.executeQueryHashMap(vo.getIpaddress(),"wassession");
				 	 cache = wasconf.executeQueryHashMap(vo.getIpaddress(),"wascache");
				 	 jdbc = wasconf.executeQueryHashMap(vo.getIpaddress(),"wasjdbc");
				 	 thread = wasconf.executeQueryHashMap(vo.getIpaddress(),"wasthread");
				 	 thing = wasconf.executeQueryHashMap(vo.getIpaddress(),"wastrans");
				 	 jvm = wasconf.executeQueryHashMap(vo.getIpaddress(),"wasjvm");
			 	 }catch(Exception e){
				 	e.printStackTrace();
				 }finally{
				 	 wasconf.close();
				 }
		  	}
		    
		   if(system == null)system = new HashMap();
		   if(jvm == null)jvm = new HashMap();
		   if(thing == null)thing = new HashMap();
		   if(Session == null)Session = new HashMap();
		   if(cache == null)cache = new HashMap();
		   if(jdbc == null)jdbc = new HashMap();
		   if(thread == null)thread = new HashMap();
		   
		   String FreeMemory ="";
		   String CPUUsageSinceServerStarted ="";
		   String CPUUsageSinceLastMeasurement ="";
		   
		   
		   String freeMemory = "";
		   String heapSize = "";
		   String upTime = "";
		   String usedMemory = "";
		   String memPer = "";
		   
	      
		   String inMemoryCacheCount = "";
		   String timeoutInvalidationCount = "";
		   String maxInMemoryCacheCount = "";
		   
		   
		   String activeCount = "";
		   String createCount = "";
		   String invalidateCount = "";
		   String lifeTime = "";
		   String liveCount = "";
		   String SessiontimeoutInvalidationCount = "";
		  
		   
		   String freePoolSize = "";
		   String useTime = "";
		   String prepStmtCacheDiscardCount = "";
		   String waitingThreadCount = "";
		   String allocateCount = "";
		   String faultCount = "";
		   String waitTime = "";
		   String createCountjdbc = "";
		   String jdbcTime = "";
		   String percentUsed = "";
		   String poolSize = "";
		   String closeCount = "";
		  
		   
		   String activeCountthread = "";
		   String createCountthread = "";
		   String destroyCount = "";
		   String poolSizethread = "";
		   
		   
		   String activeCountthing = "";
		   String committedCount = "";
		   String globalBegunCount = "";
		   String globalTimeoutCount = "";
		   String globalTranTime = "";
		   String localActiveCount = "";
		   String localBegunCount = "";
		   String localRolledbackCount = "";
		   String localTimeoutCount = "";
		   String localTranTime = "";
		   String rolledbackCount = "";
		   
		   
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   
			
			/* String type = (String)request.getAttribute("type"); */
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			if ("pdf".equals(type)) {
				PdfWriter.getInstance(document, new FileOutputStream(file));
			} else {
				RtfWriter2.getInstance(document, new FileOutputStream(file));
			}
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Paragraph title = new Paragraph("Was  "+vo.getVersion()+"("+vo.getIpaddress()+")性能信息报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			// title.setFont(titleFont);
			document.add(title);
			
			String contextString = "报表生成时间:" + impReport.getTimeStamp() + " \n";// 换行;
	        Paragraph context = new Paragraph(contextString);
			// 正文字体风格
			Font contextFont = new Font(bfChinese, 12, Font.NORMAL);

			Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
			context.setAlignment(Element.ALIGN_LEFT);
			// 离上一段落（标题）空的行数
			context.setSpacingBefore(5);
			// 设置第一行空的列数
			context.setFirstLineIndent(5);
			document.add(context);
			document.add(new Phrase("\n"));
			
			
			
			Table aTable = new Table(4);
			this.setTableFormat(aTable);
			Cell cell = null;
			//----------------------System信息
			if(system != null){
				   FreeMemory = (String) system.get("FreeMemory");
				   CPUUsageSinceServerStarted = (String) system.get("CPUUsageSinceServerStarted");
				   CPUUsageSinceLastMeasurement = (String) system.get("CPUUsageSinceLastMeasurement");
			   }
			cell = new Cell(new Phrase("System信息", titleFont));
			cell.setColspan(4);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("空闲内存的快照(KB)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(FreeMemory, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("自服务器启动以来的CPU平均使用率", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(CPUUsageSinceServerStarted, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("自上次查询以来的平均CPU使用率", titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(CPUUsageSinceLastMeasurement, titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			//----------------------JVM信息
			if(jvm != null){
				   freeMemory = (String) jvm.get("freeMemory");
				   heapSize = (String) jvm.get("heapSize");
				   upTime = (String) jvm.get("upTime");
				   usedMemory = (String) jvm.get("usedMemory");
				   memPer = (String) jvm.get("memPer");
			   }
			cell = new Cell(new Phrase("JVM信息", titleFont));
			cell.setColspan(4);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("Java虚拟机运行时空闲的内存(KB)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(freeMemory, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("Java虚拟机运行时总内存(KB)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(heapSize, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("已经运行的时间数(秒)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(upTime, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("Java虚拟机运行时使用的内存(KB)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(usedMemory, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("Java虚拟机内存利用率", titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(memPer, titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);			
			
			//----------------------cache信息
			if(cache != null){
				   inMemoryCacheCount = (String) cache.get("inMemoryCacheCount");
				   timeoutInvalidationCount = (String) cache.get("timeoutInvalidationCount");
				   maxInMemoryCacheCount = (String) cache.get("maxInMemoryCacheCount");
			   }
			cell = new Cell(new Phrase("cache信息", titleFont));
			cell.setColspan(4);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("内存中的高速缓存条目当前数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(inMemoryCacheCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("高速缓存条目最大数", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(timeoutInvalidationCount, titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase("超时数", titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			cell = new Cell(new Phrase(maxInMemoryCacheCount, titleFont));
			cell.setColspan(2);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);	
			
			
			document.add(aTable);
			document.close();
		}
	
	
	public void createReportxls_was_per(String filename,String id) {
		if (impReport.getTable() == null) {
			fileName = null;
			return;
		}
		  
		
		String runmodel = PollingEngine.getCollectwebflag(); 
		   
		  WasConfig vo = new WasConfig();
		  WasConfigDao configdao = new WasConfigDao();
			
	      	vo = (WasConfig) configdao.findByID(id);
		   if(vo == null){
				vo = new WasConfig();
		   }
			HashMap system = null;
			HashMap jvm = null;
			HashMap cache = null;
			HashMap jdbc = null;
			HashMap Session = null;
			HashMap thread = null;
			HashMap thing = null;
			Hashtable wasIpaddressData = null;
		   String ip = vo.getIpaddress().replace(".","_");
		   if("0".equals(runmodel)){
		   		  //采集与访问是集成模式
				  Hashtable wasHashtables = (Hashtable)com.afunms.common.util.ShareData.getWasdata();
				  wasIpaddressData = (Hashtable)wasHashtables.get(vo.getIpaddress());
				  System.out.println("wasIpaddressData:"+wasIpaddressData);
				  if(wasIpaddressData != null){
				  if(vo.getVersion().equalsIgnoreCase("v5")){
					  Hashtable systemHashtable = (Hashtable)wasIpaddressData.get("systemDatahst");
					  Hashtable cacheHashtable = (Hashtable)wasIpaddressData.get("cachehst");
					  Hashtable jvmHashtable = (Hashtable)wasIpaddressData.get("jvmhst");
					  Hashtable jdbcHashtable = (Hashtable)wasIpaddressData.get("jdbchst");
					  Hashtable servletHashtable = (Hashtable)wasIpaddressData.get("servlethst");
					  Hashtable transHashtable = (Hashtable)wasIpaddressData.get("transhst");
					  Hashtable threadHashtable = (Hashtable)wasIpaddressData.get("threadhst");
				      thread = CommonUtil.converHashTableToHashMap(threadHashtable); 
				      thing = CommonUtil.converHashTableToHashMap(transHashtable); 
				      Session = CommonUtil.converHashTableToHashMap(servletHashtable); 
				      jdbc = CommonUtil.converHashTableToHashMap(jdbcHashtable);
				      jvm = CommonUtil.converHashTableToHashMap(jvmHashtable); 
				      cache = CommonUtil.converHashTableToHashMap(cacheHashtable); 
					  system = CommonUtil.converHashTableToHashMap(systemHashtable); 
				   }else{
					  Hashtable systemHashtable = (Hashtable)wasIpaddressData.get("system7hst");
					  Hashtable cacheHashtable = (Hashtable)wasIpaddressData.get("extension7hst");
					  Hashtable jvmHashtable = (Hashtable)wasIpaddressData.get("jvm7hst");
					  Hashtable jdbcHashtable = (Hashtable)wasIpaddressData.get("jdbc7hst");
					  Hashtable servletHashtable = (Hashtable)wasIpaddressData.get("servlet7hst");
					  Hashtable transHashtable = (Hashtable)wasIpaddressData.get("trans7hst");
					  Hashtable threadHashtable = (Hashtable)wasIpaddressData.get("thread7hst");
				      thread = CommonUtil.converHashTableToHashMap(threadHashtable); 
				      thing = CommonUtil.converHashTableToHashMap(transHashtable); 
				      Session = CommonUtil.converHashTableToHashMap(servletHashtable); 
				      jdbc = CommonUtil.converHashTableToHashMap(jdbcHashtable);
				      jvm = CommonUtil.converHashTableToHashMap(jvmHashtable); 
				      cache = CommonUtil.converHashTableToHashMap(cacheHashtable); 
					  system = CommonUtil.converHashTableToHashMap(systemHashtable); 
					  }
				  }
		  	}else{
		  		 //采集与访问分离模式
		  		 GetWasInfo wasconf = new GetWasInfo();
		  		 try{
				 	 system = wasconf.executeQueryHashMap(vo.getIpaddress(),"wassystem");
				 	 Session = wasconf.executeQueryHashMap(vo.getIpaddress(),"wassession");
				 	 cache = wasconf.executeQueryHashMap(vo.getIpaddress(),"wascache");
				 	 jdbc = wasconf.executeQueryHashMap(vo.getIpaddress(),"wasjdbc");
				 	 thread = wasconf.executeQueryHashMap(vo.getIpaddress(),"wasthread");
				 	 thing = wasconf.executeQueryHashMap(vo.getIpaddress(),"wastrans");
				 	 jvm = wasconf.executeQueryHashMap(vo.getIpaddress(),"wasjvm");
			 	 }catch(Exception e){
				 	e.printStackTrace();
				 }finally{
				 	 wasconf.close();
				 }
		  	}
		    
		   if(system == null)system = new HashMap();
		   if(jvm == null)jvm = new HashMap();
		   if(thing == null)thing = new HashMap();
		   if(Session == null)Session = new HashMap();
		   if(cache == null)cache = new HashMap();
		   if(jdbc == null)jdbc = new HashMap();
		   if(thread == null)thread = new HashMap();
		   
		   String FreeMemory ="";
		   String CPUUsageSinceServerStarted ="";
		   String CPUUsageSinceLastMeasurement ="";
		   
		   
		   String freeMemory = "";
		   String heapSize = "";
		   String upTime = "";
		   String usedMemory = "";
		   String memPer = "";
		   
	      
		   String inMemoryCacheCount = "";
		   String timeoutInvalidationCount = "";
		   String maxInMemoryCacheCount = "";
		   
		   
		   String activeCount = "";
		   String createCount = "";
		   String invalidateCount = "";
		   String lifeTime = "";
		   String liveCount = "";
		   String SessiontimeoutInvalidationCount = "";
		  
		   
		   String freePoolSize = "";
		   String useTime = "";
		   String prepStmtCacheDiscardCount = "";
		   String waitingThreadCount = "";
		   String allocateCount = "";
		   String faultCount = "";
		   String waitTime = "";
		   String createCountjdbc = "";
		   String jdbcTime = "";
		   String percentUsed = "";
		   String poolSize = "";
		   String closeCount = "";
		  
		   
		   String activeCountthread = "";
		   String createCountthread = "";
		   String destroyCount = "";
		   String poolSizethread = "";
		   
		   
		   String activeCountthing = "";
		   String committedCount = "";
		   String globalBegunCount = "";
		   String globalTimeoutCount = "";
		   String globalTranTime = "";
		   String localActiveCount = "";
		   String localBegunCount = "";
		   String localRolledbackCount = "";
		   String localTimeoutCount = "";
		   String localTranTime = "";
		   String rolledbackCount = "";
		
		
		
		WritableWorkbook wb = null;
		try {
			fileName = ResourceCenter.getInstance().getSysPath() + filename;
			wb = Workbook.createWorkbook(new File(fileName));

			WritableSheet sheet = null;
			sheet = wb.createSheet("Was性能信息报表", 0);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Label tmpLabel = null;
			tmpLabel = new Label(0, 0, "Was "+vo.getVersion()+"("+vo.getIpaddress()+")性能信息报表", labelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, 0, 4, 0);
			sheet.getSettings().setDefaultColumnWidth(30); 

			tmpLabel = new Label(0, 1, "报表生成时间:" + impReport.getTimeStamp());
			sheet.addCell(tmpLabel);
			
			//-----------------System信息
			if(system != null){
				   FreeMemory = (String) system.get("FreeMemory");
				   CPUUsageSinceServerStarted = (String) system.get("CPUUsageSinceServerStarted");
				   CPUUsageSinceLastMeasurement = (String) system.get("CPUUsageSinceLastMeasurement");
			   }
			tmpLabel = new Label(0, 3, "System信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 4, " 空闲内存的快照(KB)", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 4, FreeMemory, _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, 4, "自服务器启动以来的CPU平均使用率", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, 4, CPUUsageSinceServerStarted, _labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 5, "自上次查询以来的平均CPU使用率", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 5, CPUUsageSinceLastMeasurement, _labelFormat);
			sheet.addCell(tmpLabel);
			
			
			//-----------------jvm信息
				if(jvm != null){
					   freeMemory = (String) jvm.get("freeMemory");
					   heapSize = (String) jvm.get("heapSize");
					   upTime = (String) jvm.get("upTime");
					   usedMemory = (String) jvm.get("usedMemory");
					   memPer = (String) jvm.get("memPer");
				   }
			tmpLabel = new Label(0, 7, "Jvm信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 8, "Java虚拟机运行时空闲的内存(KB)", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 8, freeMemory, _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, 8, " Java虚拟机运行时总内存(KB)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, 8, heapSize, _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(0, 9, " 已经运行的时间数(秒)", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 9, upTime, _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, 9, " Java虚拟机运行时使用的内存(KB)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, 9, usedMemory, _labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 10, "Java虚拟机内存利用率", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 10, memPer, _labelFormat);
			sheet.addCell(tmpLabel);
			
			
			//----------------------cache信息
			if(cache != null){
				   inMemoryCacheCount = (String) cache.get("inMemoryCacheCount");
				   timeoutInvalidationCount = (String) cache.get("timeoutInvalidationCount");
				   maxInMemoryCacheCount = (String) cache.get("maxInMemoryCacheCount");
			   }
			
			tmpLabel = new Label(0, 12, "Cache信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 13, "内存中的高速缓存条目当前数", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 13, freeMemory, _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, 13, "高速缓存条目最大数", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, 13, heapSize, _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(0, 14, "超时数", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 14, heapSize, _labelFormat);
			sheet.addCell(tmpLabel);
			
			
			wb.write();
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		}
	}
	
	
	public void createReportDoc_vmware_resource(String file, String type,String id) throws DocumentException, IOException {
		
		   String runmodel = PollingEngine.getCollectwebflag(); 
		   
		   List<HashMap<String, Object>> wulist = (List<HashMap<String, Object>>) reportHash.get("wulist");
		   List<HashMap<String, Object>> dslist = (List<HashMap<String, Object>>) reportHash.get("dslist");
		   List<HashMap<String, Object>> crlist = (List<HashMap<String, Object>>) reportHash.get("crlist");
		   List<HashMap<String, Object>> rplist = (List<HashMap<String, Object>>) reportHash.get("rplist");
		   List<HashMap<String, Object>> dclist = (List<HashMap<String, Object>>) reportHash.get("dclist");
		   String servicename = (String) reportHash.get("servicename");
		   String ip = (String) reportHash.get("ip");
		   
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   
			
			/* String type = (String)request.getAttribute("type"); */
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			if ("pdf".equals(type)) {
				PdfWriter.getInstance(document, new FileOutputStream(file));
			} else {
				RtfWriter2.getInstance(document, new FileOutputStream(file));
			}
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Paragraph title = new Paragraph("虚拟机("+ip+")资源信息报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			// title.setFont(titleFont);
			document.add(title);
			
			String contextString = "报表生成时间:" + impReport.getTimeStamp() + " \n";// 换行;
	        Paragraph context = new Paragraph(contextString);
			// 正文字体风格
			Font contextFont = new Font(bfChinese, 12, Font.NORMAL);

			Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
			context.setAlignment(Element.ALIGN_LEFT);
			// 离上一段落（标题）空的行数
			context.setSpacingBefore(5);
			// 设置第一行空的列数
			context.setFirstLineIndent(5);
			document.add(context);
			document.add(new Phrase("\n"));
			
			
			Table aTable = new Table(8);
			this.setTableFormat(aTable);
			Cell cell = null;
			//----------------------物理机信息
			cell = new Cell(new Phrase("物理机信息", titleFont));
			cell.setColspan(8);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("序号", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("物理机名称", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("设备型号", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("CPU(核数)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("主频(GHz)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("网卡(个)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("内存(M)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("状态", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			if(wulist != null && wulist.size()>0){
	              for(int j=0;j<wulist.size();j++){ 
						     String model = wulist.get(j).get("model").toString();
						     String name = wulist.get(j).get("name").toString();
						     //int totalcpu = Integer.parseInt(crlist.get(j).get("totalcpu").toString());
						     String cpumhz = wulist.get(j).get("cpumhz").toString();
						     String memorysizemb = wulist.get(j).get("memorysizemb").toString();
						     String powerstate = wulist.get(j).get("powerstate").toString();// 20: 电源状态,值包括poweredOn,poweredOff,standBy,unknown
						     String numcore = wulist.get(j).get("numcore").toString();
						     String numnics = wulist.get(j).get("numnics").toString();
						     String state = "";
						     if(powerstate.equalsIgnoreCase("poweredOn")){state="通电";}else if(powerstate.equalsIgnoreCase("poweredOff")){state="未通电";}else if(powerstate.equalsIgnoreCase("standBy")){state="待机";}else{state="未知";}
	               
						     cell = new Cell(new Phrase((j+1)+"", titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase(name, titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase(model, titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase(numcore, titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase(cpumhz, titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase(numnics, titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase(memorysizemb, titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase(state, titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
	              }
			}
			
			//资源池信息
			Table bTable = new Table(5);
			this.setTableFormat(bTable);
			//----------------------资源池信息
			cell = new Cell(new Phrase("资源池信息", titleFont));
			cell.setColspan(5);
			this.setCellFormat(cell, false);
			bTable.addCell(cell);
			
			cell = new Cell(new Phrase("序号", titleFont));
			this.setCellFormat(cell, false);
			bTable.addCell(cell);
			
			cell = new Cell(new Phrase("名称", titleFont));
			this.setCellFormat(cell, false);
			bTable.addCell(cell);
			
			cell = new Cell(new Phrase("资源池id", titleFont));
			this.setCellFormat(cell, false);
			bTable.addCell(cell);
			
			cell = new Cell(new Phrase("所属数据中心", titleFont));
			this.setCellFormat(cell, false);
			bTable.addCell(cell);
			
			cell = new Cell(new Phrase("所属集群", titleFont));
			this.setCellFormat(cell, false);
			bTable.addCell(cell);
			
			if(rplist != null && rplist.size()>0){
				for(int i=0;i<rplist.size();i++){
				    String name = rplist.get(i).get("name").toString();
				    String crid = rplist.get(i).get("crid").toString();
					String dcid = rplist.get(i).get("dcid").toString();
					String vid = rplist.get(i).get("vid").toString();
	               
						     cell = new Cell(new Phrase((i+1)+"", titleFont));
								this.setCellFormat(cell, false);
								bTable.addCell(cell);
								
								cell = new Cell(new Phrase(name, titleFont));
								this.setCellFormat(cell, false);
								bTable.addCell(cell);
								
								cell = new Cell(new Phrase(vid, titleFont));
								this.setCellFormat(cell, false);
								bTable.addCell(cell);
								
								cell = new Cell(new Phrase(dcid, titleFont));
								this.setCellFormat(cell, false);
								bTable.addCell(cell);
								
								cell = new Cell(new Phrase(crid, titleFont));
								this.setCellFormat(cell, false);
								bTable.addCell(cell);
	              }
			}
			
			
			//存储池信息
			Table cTable = new Table(4);
			this.setTableFormat(cTable);
			//----------------------存储池信息
			cell = new Cell(new Phrase("存储池信息", titleFont));
			cell.setColspan(4);
			this.setCellFormat(cell, false);
			cTable.addCell(cell);
			
			cell = new Cell(new Phrase("序号", titleFont));
			this.setCellFormat(cell, false);
			cTable.addCell(cell);
			
			cell = new Cell(new Phrase("名称", titleFont));
			this.setCellFormat(cell, false);
			cTable.addCell(cell);
			
			cell = new Cell(new Phrase("存储总量(MB)", titleFont));
			this.setCellFormat(cell, false);
			cTable.addCell(cell);
			
			cell = new Cell(new Phrase("未使用存储(MB)", titleFont));
			this.setCellFormat(cell, false);
			cTable.addCell(cell);
			
			
			if(null != dslist && dslist.size() > 0){
				for(int j=0;j<dslist.size();j++){ 
						String name = dslist.get(j).get("name").toString();
						String capacity = dslist.get(j).get("capacity").toString();
						String freespace = dslist.get(j).get("freespace").toString();
	               
						     cell = new Cell(new Phrase((j+1)+"", titleFont));
								this.setCellFormat(cell, false);
								cTable.addCell(cell);
								
								cell = new Cell(new Phrase(name, titleFont));
								this.setCellFormat(cell, false);
								cTable.addCell(cell);
								
								cell = new Cell(new Phrase(capacity, titleFont));
								this.setCellFormat(cell, false);
								cTable.addCell(cell);
								
								cell = new Cell(new Phrase(freespace, titleFont));
								this.setCellFormat(cell, false);
								cTable.addCell(cell);
								
	              }
			}
			
			//数据中心信息
			Table dTable = new Table(3);
			this.setTableFormat(dTable);
			//----------------------数据中心信息
			cell = new Cell(new Phrase("数据中心信息", titleFont));
			cell.setColspan(3);
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			cell = new Cell(new Phrase("序号", titleFont));
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			cell = new Cell(new Phrase("名称", titleFont));
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			cell = new Cell(new Phrase("数据中心id)", titleFont));
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			if(null != dclist && dclist.size() > 0){
				  for(int j=0;j<dclist.size();j++){
					String name = dclist.get(j).get("name").toString();
					String vid = dclist.get(j).get("vid").toString();
	               
						     cell = new Cell(new Phrase((j+1)+"", titleFont));
								this.setCellFormat(cell, false);
								dTable.addCell(cell);
								
								cell = new Cell(new Phrase(name, titleFont));
								this.setCellFormat(cell, false);
								dTable.addCell(cell);
								
								cell = new Cell(new Phrase(vid, titleFont));
								this.setCellFormat(cell, false);
								dTable.addCell(cell);
								
	              }
			}
			
			
			
			Table eTable = new Table(7);
			this.setTableFormat(eTable);
			//----------------------云资源信息
			cell = new Cell(new Phrase("云资源信息", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			cell = new Cell(new Phrase("序号", titleFont));
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			cell = new Cell(new Phrase("名称", titleFont));
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			cell = new Cell(new Phrase("磁盘总大小(MB)", titleFont));
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			cell = new Cell(new Phrase("CPU使用频率(MHz)", titleFont));
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			cell = new Cell(new Phrase("主机数量", titleFont));
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			cell = new Cell(new Phrase("总内存(MB)", titleFont));
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			cell = new Cell(new Phrase("CPU核数", titleFont));
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			if(null != crlist && crlist.size() > 0){
				for(int j=0;j<crlist.size();j++){ 
								     String totaldssizemb = crlist.get(j).get("totaldssizemb").toString();
								     String name = crlist.get(j).get("name").toString();
								     //int totalcpu = Integer.parseInt(crlist.get(j).get("totalcpu").toString());
								     String totalcpu = crlist.get(j).get("totalcpu").toString();
								     String numhosts = crlist.get(j).get("numhosts").toString();
								     String totalmemory = crlist.get(j).get("totalmemory").toString();
								     String numcpucores = crlist.get(j).get("numcpucores").toString();
	               
						     cell = new Cell(new Phrase((j+1)+"", titleFont));
								this.setCellFormat(cell, false);
								eTable.addCell(cell);
								
								cell = new Cell(new Phrase(name, titleFont));
								this.setCellFormat(cell, false);
								eTable.addCell(cell);
								
								cell = new Cell(new Phrase(totaldssizemb, titleFont));
								this.setCellFormat(cell, false);
								eTable.addCell(cell);
								
								cell = new Cell(new Phrase(totalcpu, titleFont));
								this.setCellFormat(cell, false);
								eTable.addCell(cell);
								
								cell = new Cell(new Phrase(numhosts, titleFont));
								this.setCellFormat(cell, false);
								eTable.addCell(cell);
								
								cell = new Cell(new Phrase(totalmemory, titleFont));
								this.setCellFormat(cell, false);
								eTable.addCell(cell);
								
								cell = new Cell(new Phrase(numcpucores, titleFont));
								this.setCellFormat(cell, false);
								eTable.addCell(cell);
	              }
			}
			
			document.add(aTable);
			document.add(bTable);
			document.add(cTable);
			document.add(dTable);
			document.add(eTable);
			document.close();
		}
	
	
	public void createReportxls_vmware_resource(String filename,String id) {
		if (impReport.getTable() == null) {
			fileName = null;
			return;
		}
		
		List<HashMap<String, Object>> wulist = (List<HashMap<String, Object>>) reportHash.get("wulist");
		List<HashMap<String, Object>> dslist = (List<HashMap<String, Object>>) reportHash.get("dslist");
		List<HashMap<String, Object>> crlist = (List<HashMap<String, Object>>) reportHash.get("crlist");
		List<HashMap<String, Object>> rplist = (List<HashMap<String, Object>>) reportHash.get("rplist");
		List<HashMap<String, Object>> dclist = (List<HashMap<String, Object>>) reportHash.get("dclist");
		String servicename = (String) reportHash.get("servicename");
		String ip = (String) reportHash.get("ip");
		
		WritableWorkbook wb = null;
		try {
			fileName = ResourceCenter.getInstance().getSysPath() + filename;
			wb = Workbook.createWorkbook(new File(fileName));

			WritableSheet sheet = null;
			sheet = wb.createSheet("VMWare资源信息报表", 0);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Label tmpLabel = null;
			tmpLabel = new Label(0, 0, "虚拟机("+ip+")资源信息报表" , labelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, 0, 4, 0);
			sheet.getSettings().setDefaultColumnWidth(20); 

			tmpLabel = new Label(0, 1, "报表生成时间:" + impReport.getTimeStamp());
			sheet.addCell(tmpLabel);
			
			//----------------------物理机信息
			tmpLabel = new Label(0, 3, "物理机信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 4, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 4, "物理机名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, 4, "设备型号", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, 4, "CPU(核数)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, 4, "主频(GHz)", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(5, 4, "网卡(个)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(6, 4, "内存(M)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(7, 4, "状态", _labelFormat);
			sheet.addCell(tmpLabel);
			
			int index = 5;
			if(wulist != null && wulist.size()>0){
	              for(int j=0;j<wulist.size();j++){ 
						     String model = wulist.get(j).get("model").toString();
						     String name = wulist.get(j).get("name").toString();
						     //int totalcpu = Integer.parseInt(crlist.get(j).get("totalcpu").toString());
						     String cpumhz = wulist.get(j).get("cpumhz").toString();
						     String memorysizemb = wulist.get(j).get("memorysizemb").toString();
						     String powerstate = wulist.get(j).get("powerstate").toString();// 20: 电源状态,值包括poweredOn,poweredOff,standBy,unknown
						     String numcore = wulist.get(j).get("numcore").toString();
						     String numnics = wulist.get(j).get("numnics").toString();
						     String state = "";
						     if(powerstate.equalsIgnoreCase("poweredOn")){state="通电";}else if(powerstate.equalsIgnoreCase("poweredOff")){state="未通电";}else if(powerstate.equalsIgnoreCase("standBy")){state="待机";}else{state="未知";}
						     
						        tmpLabel = new Label(0, index, (j+1)+"", b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(1, index, name, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(2, index, model, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(3, index, numcore, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(4, index, cpumhz, b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(5, index, numnics, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(6, index, numnics, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(7, index, state, _labelFormat);
								sheet.addCell(tmpLabel);
								
								index++;
	              }
			}
			index = index+2;
			//----------------------资源池信息
			tmpLabel = new Label(0, index, "资源池信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			tmpLabel = new Label(0, index, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, index, "名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, index, "资源池id", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, index, "所属数据中心", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, index, "所属集群", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			if(rplist != null && rplist.size()>0){
				for(int i=0;i<rplist.size();i++){
				    String name = rplist.get(i).get("name").toString();
				    String crid = rplist.get(i).get("crid").toString();
					String dcid = rplist.get(i).get("dcid").toString();
					String vid = rplist.get(i).get("vid").toString();
						     
						        tmpLabel = new Label(0, index, (i+1)+"", b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(1, index, name, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(2, index, vid, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(3, index, dcid, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(4, index, crid, b_labelFormat);
								sheet.addCell(tmpLabel);
								
								index++;
	              }
			}
			index = index+2;
			//----------------------存储池信息
			tmpLabel = new Label(0, index, "存储池信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			tmpLabel = new Label(0, index, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, index, "名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, index, "存储总量(MB)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, index, "未使用存储(MB)", _labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			if(null != dslist && dslist.size() > 0){
				for(int j=0;j<dslist.size();j++){ 
						String name = dslist.get(j).get("name").toString();
						String capacity = dslist.get(j).get("capacity").toString();
						String freespace = dslist.get(j).get("freespace").toString();
						     
						        tmpLabel = new Label(0, index, (j+1)+"", b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(1, index, name, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(2, index, capacity, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(3, index, freespace, _labelFormat);
								sheet.addCell(tmpLabel);
								
								index++;
	              }
			}
			index = index+2;
			//----------------------数据中心信息
			tmpLabel = new Label(0, index, "数据中心信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			tmpLabel = new Label(0, index, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, index, "名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, index, "数据中心id", _labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			if(null != dclist && dclist.size() > 0){
				  for(int j=0;j<dclist.size();j++){
					String name = dclist.get(j).get("name").toString();
					String vid = dclist.get(j).get("vid").toString();
						     
						        tmpLabel = new Label(0, index, (j+1)+"", b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(1, index, name, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(2, index, vid, _labelFormat);
								sheet.addCell(tmpLabel);
								
								index++;
	              }
			}
			index = index+2;
			//----------------------云资源信息
			tmpLabel = new Label(0, index, "云资源信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			tmpLabel = new Label(0, index, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, index, "名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, index, "磁盘总大小(MB)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, index, "CPU使用频率(MHz)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, index, "主机数量", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(5, index, "总内存(MB)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(6, index, "CPU核数", _labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			if(null != crlist && crlist.size() > 0){
				for(int j=0;j<crlist.size();j++){ 
								     String totaldssizemb = crlist.get(j).get("totaldssizemb").toString();
								     String name = crlist.get(j).get("name").toString();
								     //int totalcpu = Integer.parseInt(crlist.get(j).get("totalcpu").toString());
								     String totalcpu = crlist.get(j).get("totalcpu").toString();
								     String numhosts = crlist.get(j).get("numhosts").toString();
								     String totalmemory = crlist.get(j).get("totalmemory").toString();
								     String numcpucores = crlist.get(j).get("numcpucores").toString();
						     
						        tmpLabel = new Label(0, index, (j+1)+"", b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(1, index, name, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(2, index, totaldssizemb, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(3, index, totalcpu, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(4, index, numhosts, b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(5, index, totalmemory, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(6, index, numcpucores, _labelFormat);
								sheet.addCell(tmpLabel);
								
								index++;
	              }
			}
			
			
			wb.write();
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		}
	}
	
	
	
	public void createReportDoc_vmware_vm(String file, String type,String id) throws DocumentException, IOException {
		
		   String runmodel = PollingEngine.getCollectwebflag(); 
		   List<HashMap<String, Object>> vmlist = (List<HashMap<String, Object>>) reportHash.get("vmlist");
		   String servicename = (String) reportHash.get("servicename");
		   String ip = (String) reportHash.get("ip");
		   
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   
			
			/* String type = (String)request.getAttribute("type"); */
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			if ("pdf".equals(type)) {
				PdfWriter.getInstance(document, new FileOutputStream(file));
			} else {
				RtfWriter2.getInstance(document, new FileOutputStream(file));
			}
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Paragraph title = new Paragraph("虚拟机("+ip+")信息报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			// title.setFont(titleFont);
			document.add(title);
			
			String contextString = "报表生成时间:" + impReport.getTimeStamp() + " \n";// 换行;
	        Paragraph context = new Paragraph(contextString);
			// 正文字体风格
			Font contextFont = new Font(bfChinese, 12, Font.NORMAL);

			Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
			context.setAlignment(Element.ALIGN_LEFT);
			// 离上一段落（标题）空的行数
			context.setSpacingBefore(5);
			// 设置第一行空的列数
			context.setFirstLineIndent(5);
			document.add(context);
			document.add(new Phrase("\n"));
			
			
			Table aTable = new Table(7);
			this.setTableFormat(aTable);
			Cell cell = null;
			//----------------------虚拟机信息
			cell = new Cell(new Phrase("虚拟机信息", titleFont));
			cell.setColspan(7);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("序号", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("所属物理机名称", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("操作系统", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("全名", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("CPU配置", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("内存分配(Mb)", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("电源状态", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			if(vmlist != null && vmlist.size()>0){
				 Hashtable wutable = (Hashtable)reportHash.get("wutable");
	              for(int j=0;j<vmlist.size();j++){ 
	            	    String name = vmlist.get(j).get("name").toString();
						String guestfullname = vmlist.get(j).get("guestfullname").toString();
						String cpu = vmlist.get(j).get("cpu").toString();
						String memorysizemb = vmlist.get(j).get("memorysizemb").toString();
						String powerstate = vmlist.get(j).get("powerstate").toString();
						String state = "";
						if(powerstate.equalsIgnoreCase("poweredOn")){state="通电";}else if(powerstate.equalsIgnoreCase("poweredOff")){state="未通电";}else if(powerstate.equalsIgnoreCase("suspended")){state="挂起";}
	               
						        cell = new Cell(new Phrase((j+1)+"", titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) wutable.get(vmlist.get(j).get("hoid").toString()), titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase(name, titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase(guestfullname, titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase(cpu, titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase(memorysizemb, titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase(state, titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
	              }
			}
			document.add(aTable);
			document.close();
		}
	
	
	public void createReportxls_vmware_vm(String filename,String id) {
		if (impReport.getTable() == null) {
			fileName = null;
			return;
		}
		
		List<HashMap<String, Object>> vmlist = (List<HashMap<String, Object>>) reportHash.get("vmlist");
		String servicename = (String) reportHash.get("servicename");
		String ip = (String) reportHash.get("ip");
		
		WritableWorkbook wb = null;
		try {
			fileName = ResourceCenter.getInstance().getSysPath() + filename;
			wb = Workbook.createWorkbook(new File(fileName));

			WritableSheet sheet = null;
			sheet = wb.createSheet("VMWare信息报表", 0);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Label tmpLabel = null;
			tmpLabel = new Label(0, 0, "虚拟机("+ip+")信息报表" , labelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, 0, 4, 0);
			sheet.getSettings().setDefaultColumnWidth(20); 

			tmpLabel = new Label(0, 1, "报表生成时间:" + impReport.getTimeStamp());
			sheet.addCell(tmpLabel);
			
			//----------------------虚拟机信息
			tmpLabel = new Label(0, 3, "虚拟机信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 4, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 4, "所属物理机名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, 4, "操作系统", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, 4, "全名", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, 4, "CPU配置", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(5, 4, "内存分配(Mb)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(6, 4, "电源状态", _labelFormat);
			sheet.addCell(tmpLabel);
			
			int index = 5;
			if(vmlist != null && vmlist.size()>0){
				 Hashtable wutable = (Hashtable)reportHash.get("wutable");
	              for(int j=0;j<vmlist.size();j++){ 
	            	    String name = vmlist.get(j).get("name").toString();
						String guestfullname = vmlist.get(j).get("guestfullname").toString();
						String cpu = vmlist.get(j).get("cpu").toString();
						String memorysizemb = vmlist.get(j).get("memorysizemb").toString();
						String powerstate = vmlist.get(j).get("powerstate").toString();
						String state = "";
						if(powerstate.equalsIgnoreCase("poweredOn")){state="通电";}else if(powerstate.equalsIgnoreCase("poweredOff")){state="未通电";}else if(powerstate.equalsIgnoreCase("suspended")){state="挂起";}
						     
						        tmpLabel = new Label(0, index, (j+1)+"", b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(1, index, (String)wutable.get(vmlist.get(j).get("hoid").toString()), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(2, index, name, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(3, index, guestfullname, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(4, index, cpu, b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(5, index, memorysizemb, _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(6, index, state, _labelFormat);
								sheet.addCell(tmpLabel);
								
								index++;
	              }
			}
			
			wb.write();
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		}
	}
	
	public void createReportDoc_vmware_performance(String file, String type,String id) throws DocumentException, IOException {
		
		   String runmodel = PollingEngine.getCollectwebflag(); 
		   
		   List<HashMap<String, Object>> wulist = (List<HashMap<String, Object>>) reportHash.get("physical");
		   List<HashMap<String, Object>> dslist = (List<HashMap<String, Object>>) reportHash.get("dslist");
		   List<HashMap<String, Object>> crlist = (List<HashMap<String, Object>>) reportHash.get("crlist");
		   List<HashMap<String, Object>> rplist = (List<HashMap<String, Object>>) reportHash.get("rplist");
		   List<HashMap<String, Object>> vmlist = (List<HashMap<String, Object>>) reportHash.get("vmlist");
		   String servicename = (String) reportHash.get("servicename");
		   String ip = (String) reportHash.get("ip");
		   
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   
			
			/* String type = (String)request.getAttribute("type"); */
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			if ("pdf".equals(type)) {
				PdfWriter.getInstance(document, new FileOutputStream(file));
			} else {
				RtfWriter2.getInstance(document, new FileOutputStream(file));
			}
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Paragraph title = new Paragraph("虚拟机("+ip+")性能信息报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			// title.setFont(titleFont);
			document.add(title);
			
			String contextString = "报表生成时间:" + impReport.getTimeStamp() + " \n";// 换行;
	        Paragraph context = new Paragraph(contextString);
			// 正文字体风格
			Font contextFont = new Font(bfChinese, 12, Font.NORMAL);

			Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
			context.setAlignment(Element.ALIGN_LEFT);
			// 离上一段落（标题）空的行数
			context.setSpacingBefore(5);
			// 设置第一行空的列数
			context.setFirstLineIndent(5);
			document.add(context);
			document.add(new Phrase("\n"));
			
			
			Table aTable = new Table(8);
			this.setTableFormat(aTable);
			Cell cell = null;
			//----------------------物理机信息
			cell = new Cell(new Phrase("物理机性能信息", titleFont));
			cell.setColspan(8);
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("序号", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("物理机名称", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("CPU利用率", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("内存(虚拟增长)MB", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("内存(MBps)换入/换出", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("CPU(MHz)使用", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("内存使用", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			
			cell = new Cell(new Phrase("磁盘(KBps)使用", titleFont));
			this.setCellFormat(cell, false);
			aTable.addCell(cell);
			if(wulist != null && wulist.size()>0){
				int k =1;
				   for(int j=0;j<wulist.size();j++){ 
		                  List phydical = (List) wulist.get(j);
	               
						        cell = new Cell(new Phrase((k++)+"", titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) phydical.get(0), titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) phydical.get(1), titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) phydical.get(2), titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) phydical.get(3), titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) phydical.get(4), titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) phydical.get(5), titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) phydical.get(6), titleFont));
								this.setCellFormat(cell, false);
								aTable.addCell(cell);
	              }
			}
			
			//资源池信息
			Table bTable = new Table(3);
			this.setTableFormat(bTable);
			//----------------------资源池信息
			cell = new Cell(new Phrase("资源池性能信息", titleFont));
			cell.setColspan(3);
			this.setCellFormat(cell, false);
			bTable.addCell(cell);
			
			cell = new Cell(new Phrase("序号", titleFont));
			this.setCellFormat(cell, false);
			bTable.addCell(cell);
			
			cell = new Cell(new Phrase("名称", titleFont));
			this.setCellFormat(cell, false);
			bTable.addCell(cell);
			
			cell = new Cell(new Phrase("cpu使用(MHz)", titleFont));
			this.setCellFormat(cell, false);
			bTable.addCell(cell);
			
			List resource = new ArrayList();
			if(rplist != null && rplist.size()>0){
			int k = 1;
				for(int i=0;i<rplist.size();i++){
				    resource = (List) rplist.get(i);
	               
						       cell = new Cell(new Phrase((k++)+"", titleFont));
								this.setCellFormat(cell, false);
								bTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) resource.get(0), titleFont));
								this.setCellFormat(cell, false);
								bTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) resource.get(1), titleFont));
								this.setCellFormat(cell, false);
								bTable.addCell(cell);
	              }
			}
			
			
			//存储池信息
			Table cTable = new Table(6);
			this.setTableFormat(cTable);
			//----------------------存储池信息
			cell = new Cell(new Phrase("存储池性能信息", titleFont));
			cell.setColspan(6);
			this.setCellFormat(cell, false);
			cTable.addCell(cell);
			
			cell = new Cell(new Phrase("序号", titleFont));
			this.setCellFormat(cell, false);
			cTable.addCell(cell);
			
			cell = new Cell(new Phrase("名称", titleFont));
			this.setCellFormat(cell, false);
			cTable.addCell(cell);
			
			cell = new Cell(new Phrase("已分配", titleFont));
			this.setCellFormat(cell, false);
			cTable.addCell(cell);
			
			cell = new Cell(new Phrase("已使用", titleFont));
			this.setCellFormat(cell, false);
			cTable.addCell(cell);
			
			cell = new Cell(new Phrase("容量", titleFont));
			this.setCellFormat(cell, false);
			cTable.addCell(cell);
			
			cell = new Cell(new Phrase("利用率", titleFont));
			this.setCellFormat(cell, false);
			cTable.addCell(cell);
			
			
			
			if(null != dslist && dslist.size() > 0){
				int k=1;
					for(int j=0;j<dslist.size();j++){ 
							List datastory = (List) dslist.get(j);
	               
						     cell = new Cell(new Phrase((k++)+"", titleFont));
								this.setCellFormat(cell, false);
								cTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) datastory.get(0), titleFont));
								this.setCellFormat(cell, false);
								cTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) datastory.get(1), titleFont));
								this.setCellFormat(cell, false);
								cTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) datastory.get(2), titleFont));
								this.setCellFormat(cell, false);
								cTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) datastory.get(3), titleFont));
								this.setCellFormat(cell, false);
								cTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) datastory.get(4), titleFont));
								this.setCellFormat(cell, false);
								cTable.addCell(cell);
	              }
			}
			
			//虚拟机性能信息
			Table dTable = new Table(9);
			this.setTableFormat(dTable);
			//----------------------数据中心信息
			cell = new Cell(new Phrase("虚拟机性能信息", titleFont));
			cell.setColspan(9);
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			cell = new Cell(new Phrase("序号", titleFont));
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			cell = new Cell(new Phrase("所属物理机名称", titleFont));
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			cell = new Cell(new Phrase("虚拟机名称", titleFont));
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			cell = new Cell(new Phrase("CPU利用率", titleFont));
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			cell = new Cell(new Phrase("CPU(MHz)使用", titleFont));
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			cell = new Cell(new Phrase("内存(MBps)换入/换出", titleFont));
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			cell = new Cell(new Phrase("内存使用", titleFont));
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			cell = new Cell(new Phrase("磁盘(KBps)使用", titleFont));
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			cell = new Cell(new Phrase("网络(KBps)使用", titleFont));
			this.setCellFormat(cell, false);
			dTable.addCell(cell);
			
			 if(vmlist != null && vmlist.size()>0){
			         int k = 1;
			              for(int j=0;j<vmlist.size();j++){ 
			                  List vmware = (List) vmlist.get(j);
	               
						        cell = new Cell(new Phrase((k++)+"", titleFont));
								this.setCellFormat(cell, false);
								dTable.addCell(cell);
								cell = new Cell(new Phrase((String) vmware.get(0), titleFont));
								this.setCellFormat(cell, false);
								dTable.addCell(cell);
								cell = new Cell(new Phrase((String) vmware.get(9), titleFont));
								this.setCellFormat(cell, false);
								dTable.addCell(cell);
								cell = new Cell(new Phrase((String) vmware.get(1), titleFont));
								this.setCellFormat(cell, false);
								dTable.addCell(cell);
								cell = new Cell(new Phrase((String) vmware.get(2), titleFont));
								this.setCellFormat(cell, false);
								dTable.addCell(cell);
								cell = new Cell(new Phrase((String) vmware.get(4)+"/"+(String) vmware.get(5), titleFont));
								this.setCellFormat(cell, false);
								dTable.addCell(cell);
								cell = new Cell(new Phrase((String) vmware.get(6), titleFont));
								this.setCellFormat(cell, false);
								dTable.addCell(cell);
								cell = new Cell(new Phrase((String) vmware.get(7), titleFont));
								this.setCellFormat(cell, false);
								dTable.addCell(cell);
								cell = new Cell(new Phrase((String) vmware.get(8), titleFont));
								this.setCellFormat(cell, false);
								dTable.addCell(cell);
	              }
			}
			
			
			
			Table eTable = new Table(6);
			this.setTableFormat(eTable);
			//----------------------云资源信息
			cell = new Cell(new Phrase("集群性能信息", titleFont));
			cell.setColspan(6);
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			cell = new Cell(new Phrase("序号", titleFont));
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			cell = new Cell(new Phrase("名称", titleFont));
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			cell = new Cell(new Phrase("cpu使用(MHz)", titleFont));
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			cell = new Cell(new Phrase("cpu总计(MHz)", titleFont));
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			cell = new Cell(new Phrase("内存已消耗(MB)", titleFont));
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			cell = new Cell(new Phrase("内存总计(MB)", titleFont));
			this.setCellFormat(cell, false);
			eTable.addCell(cell);
			
			if(null != crlist && crlist.size() > 0){
			    int k=1; 
				for(int j=0;j<crlist.size();j++){ 
				   List yun = (List) crlist.get(j);
	               
						       cell = new Cell(new Phrase((k++)+"", titleFont));
								this.setCellFormat(cell, false);
								eTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) yun.get(0), titleFont));
								this.setCellFormat(cell, false);
								eTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) yun.get(1), titleFont));
								this.setCellFormat(cell, false);
								eTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) yun.get(2), titleFont));
								this.setCellFormat(cell, false);
								eTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) yun.get(3), titleFont));
								this.setCellFormat(cell, false);
								eTable.addCell(cell);
								
								cell = new Cell(new Phrase((String) yun.get(4), titleFont));
								this.setCellFormat(cell, false);
								eTable.addCell(cell);
								
	              }
			}
			
			document.add(aTable);
			document.add(dTable);
			document.add(bTable);
			document.add(cTable);
			document.add(eTable);
			document.close();
		}
	
	
	public void createReportxls_vmware_performance(String filename,String id) {
		if (impReport.getTable() == null) {
			fileName = null;
			return;
		}
		
		List<HashMap<String, Object>> wulist = (List<HashMap<String, Object>>) reportHash.get("physical");
		List<HashMap<String, Object>> dslist = (List<HashMap<String, Object>>) reportHash.get("dslist");
		List<HashMap<String, Object>> crlist = (List<HashMap<String, Object>>) reportHash.get("crlist");
		List<HashMap<String, Object>> rplist = (List<HashMap<String, Object>>) reportHash.get("rplist");
		List<HashMap<String, Object>> vmlist = (List<HashMap<String, Object>>) reportHash.get("vmlist");
		String servicename = (String) reportHash.get("servicename");
		String ip = (String) reportHash.get("ip");
		
		WritableWorkbook wb = null;
		try {
			fileName = ResourceCenter.getInstance().getSysPath() + filename;
			wb = Workbook.createWorkbook(new File(fileName));

			WritableSheet sheet = null;
			sheet = wb.createSheet("VMWare性能信息报表", 0);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Label tmpLabel = null;
			tmpLabel = new Label(0, 0, "虚拟机("+ip+")性能信息报表" , labelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, 0, 4, 0);
			sheet.getSettings().setDefaultColumnWidth(20); 

			tmpLabel = new Label(0, 1, "报表生成时间:" + impReport.getTimeStamp());
			sheet.addCell(tmpLabel);
			
			//----------------------物理机信息
			tmpLabel = new Label(0, 3, "物理机性能信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			
			tmpLabel = new Label(0, 4, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, 4, "物理机名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, 4, "CPU利用率", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, 4, "内存(虚拟增长)MB", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, 4, "内存(MBps)换入/换出", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(5, 4, "CPU(MHz)使用", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(6, 4, "内存使用", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(7, 4, "磁盘(KBps)使用", _labelFormat);
			sheet.addCell(tmpLabel);
			
			   int index = 5;
			   if(wulist != null && wulist.size()>0){
				     int k = 1;
				              for(int j=0;j<wulist.size();j++){ 
				                  List phydical = (List) wulist.get(j);						     
						        tmpLabel = new Label(0, index, (k++)+"", b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(1, index, phydical.get(0).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(2, index, phydical.get(1).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(3, index, phydical.get(2).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(4, index, phydical.get(3).toString(), b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(5, index, phydical.get(4).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(6, index, phydical.get(5).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(7, index, phydical.get(6).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								
								index++;
	              }
			}
			   index = index+2;
				//----------------------虚拟机性能信息
				tmpLabel = new Label(0, index, "虚拟机性能信息", b_labelFormat);
				sheet.addCell(tmpLabel);
				index = index+1;
				tmpLabel = new Label(0, index, "序号", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(1, index, "所属物理机名称", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(2, index, "虚拟机名称", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(3, index, "CPU利用率", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(4, index, "CPU(MHz)使用", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(5, index, "内存(MBps)换入/换出", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(6, index, "内存使用", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(7, index, "磁盘(KBps)使用", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(8, index, "网络(KBps)使用", _labelFormat);
				sheet.addCell(tmpLabel);
				index = index+1;
				 if(vmlist != null && vmlist.size()>0){
				         int k = 1;
				              for(int j=0;j<vmlist.size();j++){ 
				                  List vmware = (List) vmlist.get(j);
							     
							        tmpLabel = new Label(0, index, (k++)+"", b_labelFormat);
									sheet.addCell(tmpLabel);
									tmpLabel = new Label(1, index, vmware.get(0).toString(), _labelFormat);
									sheet.addCell(tmpLabel);
									tmpLabel = new Label(2, index, vmware.get(9).toString(), _labelFormat);
									sheet.addCell(tmpLabel);
									tmpLabel = new Label(3, index, vmware.get(1).toString(), _labelFormat);
									sheet.addCell(tmpLabel);
									tmpLabel = new Label(4, index, vmware.get(2).toString(), _labelFormat);
									sheet.addCell(tmpLabel);
									tmpLabel = new Label(5, index, vmware.get(4).toString()+"/"+vmware.get(5).toString(), _labelFormat);
									sheet.addCell(tmpLabel);
									tmpLabel = new Label(6, index, vmware.get(6).toString(), _labelFormat);
									sheet.addCell(tmpLabel);
									tmpLabel = new Label(7, index, vmware.get(7).toString(), _labelFormat);
									sheet.addCell(tmpLabel);
									tmpLabel = new Label(8, index, vmware.get(8).toString(), _labelFormat);
									sheet.addCell(tmpLabel);
									index++;
		              }
				} 
			  
			   
			index = index+2;
			//----------------------资源池信息
			tmpLabel = new Label(0, index, "资源池信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			tmpLabel = new Label(0, index, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, index, "名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, index, "cpu使用(MHz)", _labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			List resource = new ArrayList();
			if(rplist != null && rplist.size()>0){
			int k = 1;
				for(int i=0;i<rplist.size();i++){
				    resource = (List) rplist.get(i);
						     
						        tmpLabel = new Label(0, index, (k++)+"", b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(1, index, resource.get(0).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(2, index, resource.get(0).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								index++;
	              }
			}
			index = index+2;
			//----------------------存储池信息
			tmpLabel = new Label(0, index, "存储池性能信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			tmpLabel = new Label(0, index, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, index, "名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, index, "已分配", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, index, "已使用", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, index, "容量", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(5, index, "利用率", _labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			if(null != dslist && dslist.size() > 0){
				int k=1;
					for(int j=0;j<dslist.size();j++){ 
							List datastory = (List) dslist.get(j);
						     
						        tmpLabel = new Label(0, index, (k++)+"", b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(1, index, datastory.get(0).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(2, index, datastory.get(1).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(3, index, datastory.get(2).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(4, index, datastory.get(3).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(5, index, datastory.get(4).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								
								index++;
	              }
			}
			
			index = index+2;
			//----------------------云资源信息
			tmpLabel = new Label(0, index, "集群性能信息", b_labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			tmpLabel = new Label(0, index, "序号", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, index, "名称", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, index, "cpu使用(MHz)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, index, "cpu总计(MHz)", _labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(4, index, "内存已消耗(MB)", b_labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(5, index, "内存总计(MB))", _labelFormat);
			sheet.addCell(tmpLabel);
			index = index+1;
			if(null != crlist && crlist.size() > 0){
			    int k=1; 
				for(int j=0;j<crlist.size();j++){ 
				   List yun = (List) crlist.get(j);
						     
						        tmpLabel = new Label(0, index, (k++)+"", b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(1, index, yun.get(0).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(2, index, yun.get(1).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(3, index, yun.get(2).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(4, index, yun.get(3).toString(), b_labelFormat);
								sheet.addCell(tmpLabel);
								tmpLabel = new Label(5, index, yun.get(4).toString(), _labelFormat);
								sheet.addCell(tmpLabel);
								
								index++;
	              }
			}
			
			wb.write();
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		}
	}
	
	
	

	//服务器和网络设备的同比报表
	public void createReportDocTB(String fileName,String DeviceName,String flag,String type) throws DocumentException, IOException {
		
			if (impReport.getTable() == null) {
				fileName = null;
				return;
			}
			int years = Integer.parseInt((String) reportHash.get("yeardate"));
			String starttime = (String) reportHash.get("starttime");
			String totime = (String) reportHash.get("totime");
			List list = (List) reportHash.get("pinglist");
		   
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   
			/* String type = (String)request.getAttribute("type"); */
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			if ("pdf".equals(type)) {
				PdfWriter.getInstance(document, new FileOutputStream(fileName));
			} else {
				RtfWriter2.getInstance(document, new FileOutputStream(fileName));
			}
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Paragraph title = new Paragraph(DeviceName+"("+years+"年)同比报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			// title.setFont(titleFont);
			document.add(title);
			
			String contextString = "报表生成时间:" + impReport.getTimeStamp() + " \n";// 换行;
	        Paragraph context = new Paragraph(contextString);
			// 正文字体风格
			Font contextFont = new Font(bfChinese, 12, Font.NORMAL);

			Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
			context.setAlignment(Element.ALIGN_LEFT);
			// 离上一段落（标题）空的行数
			context.setSpacingBefore(5);
			// 设置第一行空的列数
			context.setFirstLineIndent(5);
			document.add(context);
			document.add(new Phrase("\n"));
			
			Table aTable = new Table(4);
			this.setTableFormat(aTable);
			Cell cell = null;
			
			if(list != null && list.size()>0){
				
			if(flag.equalsIgnoreCase("host")){
					for(int i=0;i<list.size();i++){
						List tblist = new ArrayList();
						tblist = (List) list.get(i);
						HostNode node = (HostNode)tblist.get(6);
						cell = new Cell(new Phrase("序号", titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase((i+1)+"", titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
	
						cell = new Cell(new Phrase("IP地址", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(node.getIpAddress(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("设备名称", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(node.getAlias(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase("操作系统", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(node.getType(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(years+"年cpu平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(1).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase((years-1)+"年cpu平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(2).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("cpu同比", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(0).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase(years+"年内存平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(4).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase((years-1)+"年内存平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(5).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
	
						cell = new Cell(new Phrase("内存同比", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(3).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(years+"年磁盘平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(8).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase((years-1)+"年磁盘平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(9).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("磁盘同比", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(7).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
					}
			  }else{
				  for(int i=0;i<list.size();i++){
						List tblist = new ArrayList();
						tblist = (List) list.get(i);
						HostNode node = (HostNode)tblist.get(6);
						cell = new Cell(new Phrase("序号", titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase((i+1)+"", titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
	
						cell = new Cell(new Phrase("IP地址", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(node.getIpAddress(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("设备名称", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(node.getAlias(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase("操作系统", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(node.getType(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(years+"年cpu平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(1).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase((years-1)+"年cpu平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(2).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(years+"cpu同比", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(0).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase(years+"年内存平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(4).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase((years-1)+"年内存平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(5).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
	
						cell = new Cell(new Phrase("内存同比", titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(3).toString(), titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
					}
			  }
	        }
			document.add(aTable);
			document.close();
		}
	
	
	public void createReportxlsTB(String fileName,String DeviceName,String flag,String type) {
		if (impReport.getTable() == null) {
			fileName = null;
			return;
		}
		
		int years = Integer.parseInt((String) reportHash.get("yeardate"));
		String starttime = (String) reportHash.get("starttime");
		String totime = (String) reportHash.get("totime");
		List list = (List) reportHash.get("pinglist");
		
		WritableWorkbook wb = null;
		try {
			wb = Workbook.createWorkbook(new File(fileName));

			WritableSheet sheet = null;
			sheet = wb.createSheet(DeviceName+"同比报表", 0);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Label tmpLabel = null;
			tmpLabel = new Label(0, 0, DeviceName+"("+years+"年)同比报表", labelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, 0, 4, 0);
			sheet.getSettings().setDefaultColumnWidth(20); 

			tmpLabel = new Label(0, 1, "报表生成时间:" + impReport.getTimeStamp());
			sheet.addCell(tmpLabel);
			
			if(flag.equals("host")){
				tmpLabel = new Label(0, 4, "序号", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(1, 4, "IP地址", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(2, 4, "设备名称", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(3, 4, "操作系统", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(4, 4, years+"年cpu平均值", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(5, 4, (years-1)+"年cpu平均值", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(6, 4, "cpu同比", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(7, 4, years+"年内存平均值", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(8, 4, (years-1)+"年内存平均值", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(9, 4, "内存同比", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(10, 4, years+"年磁盘平均值", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(11, 4, (years-1)+"年磁盘平均值", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(12, 4, "磁盘同比", _labelFormat);
				sheet.addCell(tmpLabel);
			}else{
				tmpLabel = new Label(0, 4, "序号", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(1, 4, "IP地址", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(2, 4, "设备名称", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(3, 4, "操作系统", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(4, 4, years+"年cpu平均值", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(5, 4, (years-1)+"年cpu平均值", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(6, 4, "cpu同比", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(7, 4, years+"年内存平均值", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(8, 4, (years-1)+"年内存平均值", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(9, 4, "内存同比", _labelFormat);
				sheet.addCell(tmpLabel);
			}
			if(list != null && list.size()>0){
			  if(flag.equals("host")){
				for(int i=0;i<list.size();i++){
					List tblist = new ArrayList();
					tblist = (List) list.get(i);
					HostNode node = (HostNode)tblist.get(6);
					tmpLabel = new Label(0, i+5, (i+1)+"", b_labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(1, i+5, node.getIpAddress(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(2, i+5, node.getAlias(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(3, i+5, node.getType(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(4, i+5, tblist.get(1).toString(), b_labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(5, i+5, tblist.get(2).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(6, i+5, tblist.get(0).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(7, i+5, tblist.get(4).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(8, i+5, tblist.get(5).toString(), b_labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(9, i+5,tblist.get(3).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(10, i+5, tblist.get(8).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(11, i+5, tblist.get(9).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(12, i+5, tblist.get(7).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
				}
			  }else{
				  for(int i=0;i<list.size();i++){
						List tblist = new ArrayList();
						tblist = (List) list.get(i);
						HostNode node = (HostNode)tblist.get(6);
						tmpLabel = new Label(0, i+5, (i+1)+"", b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(1, i+5, node.getIpAddress(), _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(2, i+5, node.getAlias(), _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(3, i+5, node.getType(), _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(4, i+5, tblist.get(1).toString(), b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(5, i+5, tblist.get(2).toString(), _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(6, i+5, tblist.get(0).toString(), _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(7, i+5, tblist.get(4).toString(), _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(8, i+5, tblist.get(5).toString(), b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(9, i+5,tblist.get(3).toString(), _labelFormat);
						sheet.addCell(tmpLabel);
					}
			  }
			}
			
			
			wb.write();
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		}
	}
	
	
	
	
	//服务器和网络设备的环比报表
	public void createReportDocHB(String fileName,String DeviceName,String flag,String type) throws DocumentException, IOException {
		
			if (impReport.getTable() == null) {
				fileName = null;
				return;
			}
			int years = Integer.parseInt((String) reportHash.get("yeardate"));
			String starttime = (String) reportHash.get("starttime");
			String totime = (String) reportHash.get("totime");
			List list = (List) reportHash.get("pinglist");
		   
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   
			/* String type = (String)request.getAttribute("type"); */
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			if ("pdf".equals(type)) {
				PdfWriter.getInstance(document, new FileOutputStream(fileName));
			} else {
				RtfWriter2.getInstance(document, new FileOutputStream(fileName));
			}
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Paragraph title = new Paragraph(DeviceName+"("+starttime+")环比报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			// title.setFont(titleFont);
			document.add(title);
			
			String contextString = "报表生成时间:" + impReport.getTimeStamp() + " \n";// 换行;
	        Paragraph context = new Paragraph(contextString);
			// 正文字体风格
			Font contextFont = new Font(bfChinese, 12, Font.NORMAL);

			Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
			context.setAlignment(Element.ALIGN_LEFT);
			// 离上一段落（标题）空的行数
			context.setSpacingBefore(5);
			// 设置第一行空的列数
			context.setFirstLineIndent(5);
			document.add(context);
			document.add(new Phrase("\n"));
			
			Table aTable = new Table(4);
			this.setTableFormat(aTable);
			Cell cell = null;
			
			if(list != null && list.size()>0){
				
			if(flag.equalsIgnoreCase("host")){
					for(int i=0;i<list.size();i++){
						List tblist = new ArrayList();
						tblist = (List) list.get(i);
						HostNode node = (HostNode)tblist.get(6);
						cell = new Cell(new Phrase("序号", titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase((i+1)+"", titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
	
						cell = new Cell(new Phrase("IP地址", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(node.getIpAddress(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("设备名称", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(node.getAlias(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase("操作系统", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(node.getType(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("本月cpu平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(1).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase("上个月cpu平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(2).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("cpu环比", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(0).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase("本月内存平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(4).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("上个月内存平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(5).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
	
						cell = new Cell(new Phrase("内存环比", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(3).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("本月磁盘平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(8).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase("上个月磁盘平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(9).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("磁盘环比", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(7).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
					}
			  }else{
				  for(int i=0;i<list.size();i++){
						List tblist = new ArrayList();
						tblist = (List) list.get(i);
						HostNode node = (HostNode)tblist.get(6);
						cell = new Cell(new Phrase("序号", titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase((i+1)+"", titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
	
						cell = new Cell(new Phrase("IP地址", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(node.getIpAddress(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("设备名称", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(node.getAlias(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase("操作系统", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(node.getType(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("本月cpu平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(1).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase("上个月cpu平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(2).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("cpu同比", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(0).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
						cell = new Cell(new Phrase("本月内存平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(4).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase("上个月内存平均值", titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(5).toString(), titleFont));
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
	
						cell = new Cell(new Phrase("内存同比", titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						cell = new Cell(new Phrase(tblist.get(3).toString(), titleFont));
						cell.setColspan(2);
						this.setCellFormat(cell, false);
						aTable.addCell(cell);
						
					}
			  }
	        }
			document.add(aTable);
			document.close();
		}
	
	
	public void createReportxlsHB(String fileName,String DeviceName,String flag,String type) {
		if (impReport.getTable() == null) {
			fileName = null;
			return;
		}
		
		int years = Integer.parseInt((String) reportHash.get("yeardate"));
		String starttime = (String) reportHash.get("starttime");
		String totime = (String) reportHash.get("totime");
		List list = (List) reportHash.get("pinglist");
		
		WritableWorkbook wb = null;
		try {
			wb = Workbook.createWorkbook(new File(fileName));

			WritableSheet sheet = null;
			sheet = wb.createSheet(DeviceName+"环比报表", 0);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Label tmpLabel = null;
			tmpLabel = new Label(0, 0, DeviceName+"("+starttime+")环比报表", labelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, 0, 4, 0);
			sheet.getSettings().setDefaultColumnWidth(20); 

			tmpLabel = new Label(0, 1, "报表生成时间:" + impReport.getTimeStamp());
			sheet.addCell(tmpLabel);
			
			if(flag.equals("host")){
				tmpLabel = new Label(0, 4, "序号", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(1, 4, "IP地址", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(2, 4, "设备名称", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(3, 4, "操作系统", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(4, 4, "本月cpu平均值", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(5, 4, "上个月cpu平均值", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(6, 4, "cpu同比", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(7, 4, "本月内存平均值", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(8, 4, "上个月内存平均值", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(9, 4, "内存同比", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(10, 4, "本月磁盘平均值", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(11, 4, "上个月磁盘平均值", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(12, 4, "磁盘同比", _labelFormat);
				sheet.addCell(tmpLabel);
			}else{
				tmpLabel = new Label(0, 4, "序号", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(1, 4, "IP地址", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(2, 4, "设备名称", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(3, 4, "操作系统", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(4, 4, "本月cpu平均值", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(5, 4, "上个月cpu平均值", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(6, 4, "cpu同比", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(7, 4, "本月内存平均值", _labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(8, 4, "上个月内存平均值", b_labelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(9, 4, "内存同比", _labelFormat);
				sheet.addCell(tmpLabel);
			}
			if(list != null && list.size()>0){
			  if(flag.equals("host")){
				for(int i=0;i<list.size();i++){
					List tblist = new ArrayList();
					tblist = (List) list.get(i);
					HostNode node = (HostNode)tblist.get(6);
					tmpLabel = new Label(0, i+5, (i+1)+"", b_labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(1, i+5, node.getIpAddress(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(2, i+5, node.getAlias(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(3, i+5, node.getType(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(4, i+5, tblist.get(1).toString(), b_labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(5, i+5, tblist.get(2).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(6, i+5, tblist.get(0).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(7, i+5, tblist.get(4).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(8, i+5, tblist.get(5).toString(), b_labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(9, i+5,tblist.get(3).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(10, i+5, tblist.get(8).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(11, i+5, tblist.get(9).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(12, i+5, tblist.get(7).toString(), _labelFormat);
					sheet.addCell(tmpLabel);
				}
			  }else{
				  for(int i=0;i<list.size();i++){
						List tblist = new ArrayList();
						tblist = (List) list.get(i);
						HostNode node = (HostNode)tblist.get(6);
						tmpLabel = new Label(0, i+5, (i+1)+"", b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(1, i+5, node.getIpAddress(), _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(2, i+5, node.getAlias(), _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(3, i+5, node.getType(), _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(4, i+5, tblist.get(1).toString(), b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(5, i+5, tblist.get(2).toString(), _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(6, i+5, tblist.get(0).toString(), _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(7, i+5, tblist.get(4).toString(), _labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(8, i+5, tblist.get(5).toString(), b_labelFormat);
						sheet.addCell(tmpLabel);
						tmpLabel = new Label(9, i+5,tblist.get(3).toString(), _labelFormat);
						sheet.addCell(tmpLabel);
					}
			  }
			}
			
			
			wb.write();
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		}
	}
	
	
	
	
}