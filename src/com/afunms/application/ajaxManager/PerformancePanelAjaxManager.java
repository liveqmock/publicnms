package com.afunms.application.ajaxManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.afunms.application.dao.PerformancePanelDao;
import com.afunms.application.dao.PerformancePanelIndicatorsDao;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.VMWareConnectConfig;
import com.afunms.polling.om.VMWareVid;
import com.afunms.polling.snmp.LoadVMWare;
import com.afunms.topology.dao.VMWareVidDao;
import com.gatherdb.GathersqlListManager;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfEntityMetricCSV;
import com.vmware.vim25.PerfMetricSeriesCSV;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Aug 18, 2011 10:53:00 AM
 * 类说明
 */
public class PerformancePanelAjaxManager extends AjaxBaseManager implements
		AjaxManagerInterface {

	public void execute(String action) {
//		if("editPanelDevice".equals(action)){
//			editPanelDevice();
//		}else if("editPanelIndicators".equals(action)){
//			editPanelIndicators();
//		}else if("checkPanelName".equals(action)){
//			checkPanelName();
//		}if(action.equals("wulijiAjax")){
//			wulijiAjax();
//		}if(action.equals("synchronization")){
//			synchronization();
//		}if(action.equalsIgnoreCase("perList")){
//			perList();
//		}if(action.equalsIgnoreCase("emcRaid")){
//			emcRaid();
//		}if(action.equalsIgnoreCase("emcDiskCon")){
//			emcDiskCon();
//		}if(action.equalsIgnoreCase("emcLunCon")){
//			emcLunCon();
//		}if(action.equalsIgnoreCase("emcDiskPer")){
//			emcDiskPer();
//		}if(action.equalsIgnoreCase("emcLunPer")){
//			emcLunPer();
//		}if(action.equalsIgnoreCase("emcEnvironment")){
//			emcEnvironment();
//		}if(action.equalsIgnoreCase("synchronizationEmc")){
//			synchronizationEmc();
//		}if(action.equalsIgnoreCase("emcHard")){
//			emcHard();
//		}
		return;
	}
	
	/**
	 * 检测面板名称是否合法（即是存在）
	 */
//	private void checkPanelName(){
//		//性能面板名称
//		String panelName = getParaValue("panelName");
//		try {
//			panelName = new String(panelName.getBytes("iso8859-1"),"utf-8");
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
//		//该面板名称是否已经被占用
//		boolean isExist = false;
//		PerformancePanelDao performancePanelDao = new PerformancePanelDao();
//		try {
//			isExist = performancePanelDao.checkPanelName(panelName);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally{
//			performancePanelDao.close();
//		}
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("isExist",String.valueOf(isExist));
//		JSONObject json = JSONObject.fromObject(map);
//		out.print(json);
//		out.flush();
//		out.close();
//	}
//	
//	/**
//	 * 编辑面板的监控指标
//	 * @return
//	 */
//	private void editPanelIndicators(){
//		//性能面板名称
//		String panelName = getParaValue("panelName"); 
//		String indicatorNames = getParaValue("indicatorNames");
//		try {
//			panelName = URLDecoder.decode(panelName,"UTF-8");
//			indicatorNames = URLDecoder.decode(indicatorNames,"UTF-8");
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		} 
//		//得到所有告警指标名称
//		if(indicatorNames != null && !"null".equals(indicatorNames)){
//			indicatorNames = indicatorNames.replaceAll("root,", "");
//		}
//		//更新指标
//		//更新面板的设备id
//		PerformancePanelIndicatorsDao panelIndicatorsDao = new PerformancePanelIndicatorsDao();
//		try {
//			panelIndicatorsDao.updatePanelIndicators(panelName, indicatorNames);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally{
//			panelIndicatorsDao.close();
//		}
//	}
//	/**
//	 * 编辑面板的设备
//	 * @return
//	 */
//	private void editPanelDevice(){
//		String deviceType = getParaValue("deviceType");
//		String panelName = getParaValue("panelName"); 
//		try {
//			panelName = URLDecoder.decode(panelName,"UTF-8");
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		} 
//		String deviceIds = getParaValue("deviceIds");
//		if(deviceIds != null && !deviceIds.equals("null")){
//			deviceIds = deviceIds.replaceAll("root,", "");
//		}
//		//更新面板的设备id
//		PerformancePanelDao performancePanelDao = new PerformancePanelDao();
//		try {
//			performancePanelDao.updatePanelDevices(panelName, deviceIds, deviceType);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally{
//			performancePanelDao.close();
//		}
//	}
//	
//	
//	public void wulijiAjax(){
//		String select = request.getParameter("vid");
//		String tmp = request.getParameter("id");
//		String flag = request.getParameter("flag");
//		String ip = request.getParameter("name");
//		
//		if(ip!=null){
//			try {
//				ip=new String(ip.getBytes("ISO-8859-1"),"UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		}
//    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
//		StringBuffer sb = new StringBuffer();
//    	HashMap<String,Object> map_host = new HashMap<String,Object>();
//    	Hashtable vmData = new Hashtable();
//    	vmData = (Hashtable) ShareData.getVmdata().get(host.getIpAddress());
//    	
//    	DecimalFormat df = new DecimalFormat("0.0");
////      HashMap<String, Object> map_per = new HashMap<String, Object>();
//        int size = 0;
//        String cpu_using = "";
//        String memory_increse = "";
//        String memory_in="";
//        String memory_out="";
//        String cpu_use="";
//        String memory_use="";
//        String disc = "";
//       if(flag.equals("1")){
//    	   
//    	   List l = new ArrayList();
//    	   List l_vm = new ArrayList();
//    	   List l_vmware = new ArrayList();
//    	   VMWareVidDao wudao = new VMWareVidDao();
//	       List wu_list = wudao.queryVidFlag("physical", host.getId()+"", "");
//           if(wu_list != null  && wu_list.size()>0 ){
////        	for(int i=0;i<wulist.size();i++){
////      		map_per.put(wulist.get(i).get("vid").toString(), ((HashMap<String, Object>)ipAllData.get("wuliji")).get("wuliji_per"));
////      		map_host.put(wulist.get(i).get("vid").toString(), ((HashMap<String, Object>)ipAllData.get("wuliji")).get("wuliji_host"));
//      		  HashMap<String, Object> wuliji = (HashMap<String, Object>) ((HashMap<String, Object>)vmData.get("wuliji")).get("wuliji_per");
//      		  PerfEntityMetricBase[] perfEntityMetricBases = (PerfEntityMetricBase[]) wuliji.get(select);
//      		  ArrayList vmware = (ArrayList)((HashMap<String, Object>)vmData.get("vmware")).get(select);
//      	      //取物理机的性能
//      		  for(int j=0;j<8;j++)
//      		   {
//      		    String kpi =((PerfMetricSeriesCSV) ((PerfEntityMetricCSV) perfEntityMetricBases[0]).getValue()[j]).getId().getCounterId()+"";
//      		    String[] str = ((PerfMetricSeriesCSV) ((PerfEntityMetricCSV) perfEntityMetricBases[0]).getValue()[j]).getValue().toString().split(",");
//      			 if(str != null && str.length>0){
//      			   if(str.length < 288){
//      				  size = str.length-1;
//      			    }else{
//      				  size = 287;
//      			    }
//    				  l.add(str[size]);
//      			 }
////    				  System.out.print("id:"+kpi+"--");
////    				  System.out.print(str[size]);
//      		   }
//      		  
//      	   }
//           List vm_list = new ArrayList();
//           HashMap<String,Object> vm_map = new HashMap<String,Object>();
//           
//           HashMap<String, Object> vm = (HashMap<String, Object>)((HashMap<String, Object>)vmData.get("vmware"));
//           HashMap<String,Object> vm_map1 = new HashMap<String,Object>();
//           VMWareVidDao vmdao = new VMWareVidDao();
//           List vmlist1 = vmdao.queryVidFlag("vmware", host.getId()+"", select);
//           VMWareDao vmwaredao = new VMWareDao();
//           HashMap vo_map = new HashMap();
//	       if( vmlist1!=null && vmlist1.size()>0 ){
//	       	   for(int i=0;i<vmlist1.size();i++){
//	       		       List list_vid = new ArrayList();
//	       		       list_vid.add(vmlist1.get(i).toString());
//	       		       vo_map = (HashMap)((List<HashMap<String,Object>>) vmwaredao.getbyvid(list_vid, "vm_basevmware", host.getIpAddress())).get(0);
//	       			   vm_list.add(vo_map);
//	       			   vm_map1.put(vmlist1.get(i).toString(),vm.get(vmlist1.get(i).toString()));
//	       	   }
//	       }
//           
//           
//         //画table
//        sb.append("<table  class=detail-data-body><tr><td>");
//		sb.append("<div style='background:url(/afunms/img/perfbg.png);background-repeat: no-repeat;align=center;width:680;height=280;'>");
//		sb.append("<table  border=0><tr><td height=35 width=30%></td><td height=35 width=30%></td><td height=35 width=30%></td></tr>");
//		sb.append("<tr><td align=>&nbsp;&nbsp;&nbsp;</td><td align=left>CPU利用率"+df.format(Long.parseLong((String)l.get(0))/100)+"%</td><td align=left height=29>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;cpu(MHz)使用情况："+l.get(5)+"</td></tr>");
//		sb.append("<tr><td height=48 width=30%></td><td height=48 width=30%></td><td height=48 width=30%></td></tr>");
//		sb.append("<tr><td align=right>内存(MBps)换入/换出速率："+l.get(4)+"/"+l.get(3)+"</td> <td align=center>"+ip+"</td><td align=left height=29>内存(虚拟增长)MB:"+l.get(2)+"</td></tr>");
//		sb.append("<tr><td height=50 width=30%></td><td height=50 width=30%></td><td height=50 width=30%></td></tr>");
//		sb.append("<tr><td align=center>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;内存使用情况："+df.format(Long.parseLong((String)l.get(6))/100)+"%</td><td align=right>磁盘(KBPs)使用</td><td align=left height=29>情况:"+l.get(7)+"</td></tr></table></div></td></tr>");
//		sb.append("<tr ><td><table cellspacing='0' cellpadding='0' width='100%' bgcolor='#FFFFFF' border=1><tr align='center' height=28 bgcolor='DEEBF7'>");
//		sb.append("<td width='10%' align='center'>序号</td><td width='30%' align='center'>名称</td><td width='20%' align='center'>cpu利用率</td><td width='20%' align='center'>内存利用率</td><td width='20%' align='center'>电源</td>");
//		if(vm_list != null && vm_list.size()>0){
//			
//			for(int i = 0;i<vm_list.size();i++){
//				List list = new ArrayList();
//				HashMap vo = (HashMap) vm_list.get(i);
//				PerfEntityMetricBase[] perfEntityMetricBases = (PerfEntityMetricBase[]) vm_map1.get(vo.get("vid"));//存的是虚拟机性能信息
//				if (perfEntityMetricBases != null
//							&& perfEntityMetricBases.length > 0) {
//						for (int j = 0; j < 9; j++) {
//							 try{
//			      	    			String kpi = ((PerfMetricSeriesCSV) ((PerfEntityMetricCSV) perfEntityMetricBases[0])
//											.getValue()[j]).getId().getCounterId()
//											+ "";
//									String[] str = ((PerfMetricSeriesCSV) ((PerfEntityMetricCSV) perfEntityMetricBases[0])
//											.getValue()[j]).getValue().toString()
//											.split(",");
//			      	    		 
//			      	    		  
//							       if(str != null && str.length>0){
//								    	if (str.length < 288) {
//										  size = str.length-1;
//									    } else {
//									     	size = 287;
//									    }
//									    list.add(str[size]);
//							       }
//			      	    		 }catch(NullPointerException E){
//			      	    			list.add("0");
//			      	    		 }
//						}
//						 l_vmware.add(list);
//					}
//				
//				String state = "";
//				String powerstate = vo.get("powerstate").toString();
//				if(powerstate.equalsIgnoreCase("poweredOn")){state="通电";}else if(powerstate.equalsIgnoreCase("poweredOff")){state="未通电";}else if(powerstate.equalsIgnoreCase("suspended")){state="挂起";}
//				sb.append("<tr align='center' height=28>");
//				sb.append("<td width='10%' align='center'>"+(i+1)+"</td><td width='30%' align='center'><a href = '#'  onclick=\"show_vm('"+vo.get("vid")+"','"+host.getIpAddress()+"','"+vo.get("name")+"')\">"+vo.get("name")+"</a></td><td width='20%' align='center'>"+df.format(Long.parseLong((String) ((ArrayList)l_vmware.get(i)).get(0))/100)+"%</td><td width='20%' align='center'>"+df.format(Long.parseLong((String)((ArrayList)l_vmware.get(i)).get(6))/100)+"%</td><td width='20%' align='center'>"+state+"</td>");
//				sb.append("<tr align='center' height=28>");
//			}
//		}
//		
//		sb.append("</table></td></tr></table>");
//      }
//      //存储
//       else if(flag.equals("2")){
//    	        ArrayList l = (ArrayList) ((HashMap)vmData.get("ds")).get(select);//集合索引0——》已使用，1——已分配，2——容量
//    	        DecimalFormat df1 = new DecimalFormat("0.0");
//    	        if(l.size() == 3){
//    	        sb.append("<table  class=detail-data-body><tr><td>");
//        		sb.append("<div style='background:url(/afunms/img/datastorebg.png);background-repeat: no-repeat;align=center;width:680;height=280;'>");
//        		sb.append("<table  border=0><tr><td height=35 width=30%></td><td height=35 width=15%></td><td height=35 width=55%></td></tr>");
//        		sb.append("<tr><td align=>&nbsp;&nbsp;&nbsp;</td><td align=center></td><td align=left height=35 width=55%>已分配:&nbsp;&nbsp;"+df.format(Double.parseDouble((String)l.get(1))/(1024*1024))+"GB</td></tr>");
//        		sb.append("<tr><td height=28 width=30%></td><td height=28 width=15%></td><td height=28 width=55%></td></tr>");
//        		sb.append("<tr><td align=right></td> <td align=center></td><td align=left height=35 width=55%>已使用:&nbsp;&nbsp;"+df.format(Double.parseDouble((String)l.get(0))/(1024*1024))+"GB</td></tr>");
//        		sb.append("<tr><td height=25 width=30% align=center valign=top>"+ip+"</td><td height=25 width=15%></td><td height=25 width=55%></td></tr>");
//        		sb.append("<tr><td height=35 width=30%></td><td height=35 width=15% align=center></td><td height=35 width=55% align=left>容&nbsp;&nbsp;&nbsp;量:&nbsp;&nbsp;"+df.format(Double.parseDouble((String)l.get(2))/(1024*1024))+"GB</td></tr>");
//        		sb.append("<tr><td height=25 width=30%></td><td height=25 width=15%></td><td height=25 width=55%></td></tr>");
//        		sb.append("<tr><td height=35 width=30%></td><td height=35 width=15% align=center valign=top></td><td height=35 width=55% align=left >利用率:&nbsp;&nbsp;"+df.format(Long.parseLong((String)l.get(0))*100/Long.parseLong((String)l.get(2)))+"%</td></tr>");
//        		sb.append("<tr><td align=center>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td align=right</td><td align=left height=29></td></tr></table></div></td></tr>");
//    	        }else{
//    	        	sb.append("<table  class=detail-data-body><tr><td>");
//            		sb.append("<div style='background:url(/afunms/img/datastorebg.png);background-repeat: no-repeat;align=center;width:680;height=280;'>");
//            		sb.append("<table  border=3><tr><td height=35 width=30%></td><td height=35 width=30%></td><td height=35 width=30%></td></tr>");
//            		sb.append("<tr><td align=>&nbsp;&nbsp;&nbsp;</td><td align=left>已使用:</td><td align=left height=29>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;已分配:</td></tr>");
//            		sb.append("<tr><td height=48 width=30%></td><td height=48 width=30%></td><td height=48 width=30%></td></tr>");
//            		sb.append("<tr><td align=right>容量:</td> <td align=center>"+ip+"</td><td align=left height=29></td></tr>");
//            		sb.append("<tr><td height=50 width=30%></td><td height=50 width=30%></td><td height=50 width=30%></td></tr>");
//            		sb.append("<tr><td align=center>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td align=right</td><td align=left height=29></td></tr></table></div></td></tr>");
//    	        }
//       }
//     //群集 
//       else if(flag.equals("3")){
////    	System.out.println("----群集   select---->"+select);
////    	System.out.println("----群集   vmData---->"+vmData);
//    	ArrayList l_cr = (ArrayList) ((HashMap)vmData.get("cr")).get(select);//集合索引0——》cpu使用情况，1——cpu总计，2——内存已消耗，3——内存总计
////    	System.out.println("----群集   ArrayList---->"+l_cr);
////    	System.out.println("----群集   ArrayList---->"+l_cr.size());
//    	if(l_cr.size() == 4){
//    		HashMap max = new HashMap();
//    		HashMap avg = new HashMap();
//    		VMWareManager vm = new VMWareManager();
//    		try {
//				max = vm.getMaxOther(host.getIpAddress(), select, "cr");
//			
//				avg = vm.getAvgOther(host.getIpAddress(), select, "cr");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			 DecimalFormat formcat=new DecimalFormat("#0.##");
//     		 
// 			 String value0=l_cr.get(0)+"";
// 			 String avgcpuvalue= avg.get("cpu")+"";
// 			 String maxcpuvalue= max.get("cpu")+"";
// 			 String value1=l_cr.get(1)+"";
// 			 String avgcputotal=avg.get("cputotal")+"";
// 			 String maxcputotal=max.get("cputotal")+"";
// 			 String value2=l_cr.get(3)+"";
// 			String avgmem2=avg.get("mem")+"";
//			 String maxmem2=max.get("mem")+"";
// 			 String value3=l_cr.get(2)+"";
// 			String avgmemtotal3=avg.get("memtotal")+"";
//			 String maxmemtotal3=max.get("memtotal")+"";
// 			 if(!value0.equals("null"))value0=formcat.format(Float.parseFloat(value0)/1024f);
// 			 if(!avgcpuvalue.equals("null"))avgcpuvalue=formcat.format(Float.parseFloat(avgcpuvalue)/1024f);
// 			 if(!maxcpuvalue.equals("null"))maxcpuvalue=formcat.format(Float.parseFloat(maxcpuvalue)/1024f);
// 			 
// 			if(!value1.equals("null"))value1=formcat.format(Float.parseFloat(value1)/1024f);
// 			if(!avgcputotal.equals("null"))avgcputotal=formcat.format(Float.parseFloat(avgcputotal)/1024f);
// 			if(!maxcputotal.equals("null"))maxcputotal=formcat.format(Float.parseFloat(maxcputotal)/1024f);
// 			
// 			if(!value2.equals("null"))value2=formcat.format(Float.parseFloat(value2)/1024f);
// 			if(!avgmem2.equals("null"))avgmem2=formcat.format(Float.parseFloat(avgmem2)/1024f);
// 			if(!maxmem2.equals("null"))maxmem2=formcat.format(Float.parseFloat(maxmem2)/1024f);
// 			
// 			if(!value3.equals("null"))value3=formcat.format(Float.parseFloat(value3)/1024f);
// 			
// 			if(!avgmemtotal3.equals("null"))avgmemtotal3=formcat.format(Float.parseFloat(avgmemtotal3)/1024f);
// 			if(!maxmemtotal3.equals("null"))maxmemtotal3=formcat.format(Float.parseFloat(maxmemtotal3)/1024f);
//    	sb.append("<table  class=detail-data-body><tr><td>");
//   		sb.append("<div style='background:url(/afunms/img/cluster.png);background-repeat: no-repeat;align=center;width:660;height=300;background-position:50% 50%'>");
//   		sb.append("<table  border=0><tr><td height=35 ></td><td height=35  align=right></td><td height=35 align=left>"+ip+"</td><td height=35 ></td><td height=35 ></td><td height=35 ></td><td height=35 ></td></tr>");
//   		sb.append("<tr><td align=>&nbsp;&nbsp;&nbsp;</td><td align=left></td><td align=left height=29></td></tr>");
//   		sb.append("<tr><td height=20 ></td><td height=20  valign=bottom></td><td height=20 valign=bottom>当前(GHz)</td><td height=20  valign=bottom>平均(GHz)</td><td height=20  valign=bottom>最大(GHz)</td><td height=20  valign=bottom></td><td height=20 ></td></tr>");
//   		sb.append("<tr><td height=20 width=42%></td><td height=20  valign=bottom>使用:</td><td  >"+value0+"</td><td  >"+avgcpuvalue+"</td><td  >"+maxcpuvalue+"</td><td   align=left><img src=\"/afunms/resource/image/a_xn.gif\" onclick='showcpu(\""+host.getIpAddress()+"\",\""+select+"\")' width=15 ></td><td  ></td></tr>");
//   		sb.append("<tr><td align=right></td> <td align=left>总计:</td><td  >"+value1+"</td><td  >"+avgcputotal+"</td><td  >"+maxcputotal+"</td><td  align=left><img src=\"/afunms/resource/image/a_xn.gif\" onclick='showcpuuse(\""+host.getIpAddress()+"\",\""+select+"\")' width=15 ></td><td align=left height=35></td></tr>");
//   		sb.append("<tr><td align=>&nbsp;&nbsp;&nbsp;</td><td align=left></td><td align=left height=30></td></tr>");
//   		sb.append("<tr><td height=20 width=42%></td><td height=20  valign=bottom width=7%></td><td height=20 width=10% valign=bottom>当前(GB)</td><td height=20 width=10% valign=bottom>平均(GB)</td><td height=20 width=9% valign=bottom>最大(GB)</td><td height=20 width=9% valign=bottom></td><td height=20 width=13%></td></tr>");
//   		sb.append("<tr><td height=20 ></td><td height=20  valign=bottom>已消耗:</td><td  >"+value2+"</td><td  >"+avgmem2+"</td><td >"+maxmem2+"</td><td height=20  algin=left><img src=\"/afunms/resource/image/a_xn.gif\" onclick='showmem(\""+host.getIpAddress()+"\",\""+select+"\")' width=15 ></td><td  ></td></tr>");
//   		sb.append("<tr><td align=center></td><td align=left >总计:</td><td  >"+value3+"</td><td  >"+avgmemtotal3+"</td><td >"+maxmemtotal3+"</td><td  ><img src=\"/afunms/resource/image/a_xn.gif\" onclick='showmemuse(\""+host.getIpAddress()+"\",\""+select+"\")' width=15 ></td><td align=left height=35></td></tr></table></div></td></tr>");
//    	}else{
//    		sb.append("<table  class=detail-data-body><tr><td>");
//       		sb.append("<div style='background:url(/afunms/img/cluster.png);background-repeat: no-repeat;align=center;width:660;height=300;background-position:50% 50%'>");
//       		sb.append("<table  border=0><tr><td height=35 ></td><td height=35  align=right></td><td height=35 align=left>"+ip+"</td><td height=35 ></td><td height=35 ></td><td height=35 ></td><td height=35 ></td></tr>");
//       		sb.append("<tr><td align=>&nbsp;&nbsp;&nbsp;</td><td align=left></td><td align=left height=29></td></tr>");
//       		sb.append("<tr><td height=20 ></td><td height=20  valign=bottom></td><td height=20 valign=bottom>当前(GHz)</td><td height=20  valign=bottom>平均(GHz)</td><td height=20  valign=bottom>最大(GHz)</td><td height=20  valign=bottom></td><td height=20 ></td></tr>");
//       		sb.append("<tr><td height=20 width=42%></td><td height=20  valign=bottom>使用:</td><td  >0</td><td  >0</td><td  >0</td><td   align=left></td><td  ></td></tr>");
//       		sb.append("<tr><td align=right></td> <td align=left>总计:</td><td  >0</td><td  >0</td><td  >0</td><td  align=left></td><td align=left height=35></td></tr>");
//       		sb.append("<tr><td align=>&nbsp;&nbsp;&nbsp;</td><td align=left></td><td align=left height=30></td></tr>");
//       		sb.append("<tr><td height=20 width=42%></td><td height=20  valign=bottom width=7%></td><td height=20 width=10% valign=bottom>当前(GB)</td><td height=20 width=10% valign=bottom>平均(GB)</td><td height=20 width=9% valign=bottom>最大(GB)</td><td height=20 width=9% valign=bottom></td><td height=20 width=13%></td></tr>");
//       		sb.append("<tr><td height=20 ></td><td height=20  valign=bottom>已消耗:</td><td  >0</td><td  >0</td><td >0</td><td height=20  algin=left></td><td  ></td></tr>");
//       		sb.append("<tr><td align=center></td><td align=left >总计:</td><td  >0</td><td  >0</td><td >0</td><td  ></td><td align=left height=35></td></tr></table></div></td></tr>");
//    	}
//       }
//     //数据中心
//       else if(flag.equals("4")){
////        	   List l = new ArrayList();
////        	   List l_dc = new ArrayList();
////               if(dclist.size()>0 && dclist != null){
////          		  HashMap<String, Object> dc = (HashMap<String, Object>)vmData.get("dc");
////          		  System.out.println("--------shu ju zhong xin---------"+dc);
////          		  if(dc != null && dc.size()>0){
////          		  System.out.println("----shu ju zhong xin   select---->"+select);
////          		  PerfEntityMetricBase[] perfEntityMetricBases = (PerfEntityMetricBase[]) dc.get(select);
////          		  System.out.println("--------shu ju zhong xin---------"+perfEntityMetricBases);
////          		  for(int j=0;j<1;j++)
////          		   {
////          			try{
////          			  System.out.println("----数据中心 的value长度 ---->"+((PerfEntityMetricCSV) perfEntityMetricBases[0]).getValue().length);	
////          		      String kpi =((PerfMetricSeriesCSV) ((PerfEntityMetricCSV) perfEntityMetricBases[0]).getValue()[j]).getId().getCounterId()+"";
////          		      String[] str = ((PerfMetricSeriesCSV) ((PerfEntityMetricCSV) perfEntityMetricBases[0]).getValue()[j]).getValue().toString().split(",");
////          		      System.out.println(kpi+"----zou mei zou ---->"+str);
////          		      System.out.println(str.length+"----zou mei zou ---->");
////          		      if(str != null && str.length>0){
////          			   if(str.length < 288){
////          				  size = str.length-1;
////          			    }else{
////          				  size = 287;
////          			    }
////        				  l_dc.add(str[size]);
////          		    	 }
////          		     System.out.print("id:"+kpi+"--");
////				     System.out.print(str[size]);
////          			 }catch(NullPointerException e){
////          				 l_dc.add("0");
////          				 e.printStackTrace();
////          			 }
////          		   }
////          		  }
////               }
////               sb.append("<table  class=detail-data-body><tr><td>");
////       		sb.append("<div style='background:url(/afunms/img/perfbg.png);background-repeat: no-repeat;align=center;width:680;height=280;'>");
////       		sb.append("<table  border=0><tr><td height=30 width=30%></td><td height=30 width=30%></td><td height=35 width=30%></td></tr>");
////       		sb.append("<tr><td align=>&nbsp;&nbsp;&nbsp;</td><td align=left>CPU利用率</td><td align=left height=29>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;内存(虚拟增长)MB:</td></tr>");
////       		sb.append("<tr><td height=48 width=30%></td><td height=48 width=30%></td><td height=48 width=30%></td></tr>");
////       		sb.append("<tr><td align=right>内存(MBps)换入/换出速率：</td> <td align=center>"+ip+"</td><td align=left height=29>cpu(MHz)使用情况：</td></tr>");
////       		sb.append("<tr><td height=50 width=30%></td><td height=50 width=30%></td><td height=50 width=30%></td></tr>");
////       		sb.append("<tr><td align=center>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;内存使用情况：</td><td align=right>磁盘(KBPs)使用</td><td align=left height=29>情况:</td></tr></table></div></td></tr>");
////    	   
//    	   
//       }
//     //资源池
//       else if(flag.equals("5")){
//    	   ArrayList l = (ArrayList) ((HashMap)vmData.get("rp")).get(select);//集合索引0——》cpu使用情况，
////    	        System.out.println("资源池---------------"+l);
////    	        System.out.println("资源池---------------"+l.size());
//    	        if(l.size()== 1){
//    	        	HashMap max = new HashMap();
//    	    		HashMap avg = new HashMap();
//    	    		VMWareManager vm = new VMWareManager();
//    	    		try {
//    					max = vm.getMaxOther(host.getIpAddress(), select, "rp");
//    				
//    					avg = vm.getAvgOther(host.getIpAddress(), select, "rp");
//    				} catch (Exception e) {
//    					// TODO Auto-generated catch block
//    					e.printStackTrace();
//    				}
//    				 DecimalFormat formcat=new DecimalFormat("#0.##");
//    	     		 
//    			     			 String value0= l.get(0)+"";
//    			     			 String value1= avg.get("cpu")+"";
//    			     			 String value2= max.get("cpu")+"";
//    			     			 if(!value0.equals("null"))value0=formcat.format(Float.parseFloat(value0)/1024f);
//    			     			if(!value1.equals("null"))value1=formcat.format(Float.parseFloat(value1)/1024f);
//    			     			if(!value2.equals("null"))value2=formcat.format(Float.parseFloat(value2)/1024f);
//    	        sb.append("<table  class=detail-data-body><tr><td>");
//        		sb.append("<div style='background:url(/afunms/img/respoolbg.png);background-repeat: no-repeat;align=center;width:660;height=280;background-position:50% 50%'>");
//        		sb.append("<table  border=0><tr><td height=25 ></td><td height=25 ></td><td height=25 ></td><td height=25 ></td><td height=25 ></td><td height=25 ></td><td height=25 ></td></tr>");
//        		sb.append("<tr><td align=></td><td align=right></td><td height=25 >"+ip+"</td><td height=25 ></td><td height=25 ></td><td height=25 ></td><td align=left height=40></td></tr>");
//        		sb.append("<tr valign=bottom><td height=55 width=40%></td><td height=55 width=5%></td><td height=55 width=10%>当前(GHz)</td><td height=55 width=10%>平均(GHz)</td><td height=55 width=10%>最大(GHz)</td><td height=55 width=10% ></td><td height=55 width=15%></td></tr>");
//        		sb.append("<tr><td align=right></td> <td align=right>使用:</td><td height=25 align=center>"+value0+"</td><td height=25 align=center>"+value1+"</td><td height=25 align=center>"+value2+"</td><td height=25 ><img src=\"/afunms/resource/image/a_xn.gif\" onclick='showcpuRP(\""+host.getIpAddress()+"\",\""+select+"\")' width=15 ></td><td align=left height=35></td></tr>");
//        		sb.append("<tr><td height=50 ></td><td height=50 ></td><td height=50></td></tr>");
//        		sb.append("<tr><td align=center></td><td align=right></td><td align=left height=29></td></tr></table></div></td></tr>");
//    	        }else{
//    	        	 sb.append("<table  class=detail-data-body><tr><td>");
//    	        		sb.append("<div style='background:url(/afunms/img/respoolbg.png);background-repeat: no-repeat;align=center;width:660;height=280;background-position:50% 50%'>");
//    	        		sb.append("<table  border=0><tr><td height=25 ></td><td height=25 ></td><td height=25 ></td><td height=25 ></td><td height=25 ></td><td height=25 ></td><td height=25 ></td></tr>");
//    	        		sb.append("<tr><td align=></td><td align=right></td><td height=25 >"+ip+"</td><td height=25 ></td><td height=25 ></td><td height=25 ></td><td align=left height=40></td></tr>");
//    	        		sb.append("<tr valign=bottom><td height=55 width=40%></td><td height=55 width=5%></td><td height=55 width=10%>当前(GHz)</td><td height=55 width=10%>平均(GHz)</td><td height=55 width=10%>最大(GHz)</td><td height=55 width=10% ></td><td height=55 width=15%></td></tr>");
//    	        		sb.append("<tr><td align=right></td> <td align=right>使用:</td><td height=25 align=center>0</td><td height=25 align=center>0</td><td height=25 align=center>0</td><td height=25 ></td><td align=left height=35></td></tr>");
//    	        		sb.append("<tr><td height=50 ></td><td height=50 ></td><td height=50></td></tr>");
//    	        		sb.append("<tr><td align=center></td><td align=right></td><td align=left height=29></td></tr></table></div></td></tr>");
//    	        }
//    	}
//    	
////		System.out.println("物理机"+wulist);
////		System.out.println("虚拟机"+vmlist);
////		System.out.println(sb.toString());
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("option", sb.toString());
//		JSONObject json = JSONObject.fromObject(map);
//		out.print(json);
//		out.flush();
//		out.close();
//	}
//	
//	
//	public void synchronization(){
//		
//		
//		SysLogger.info("**********************VMWare数据同步***************************");
//		String nodeid = request.getParameter("nodeid");
//		Hashtable returnHash = new Hashtable();
//		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid));//Integer.parseInt(alarmIndicatorsNode.getNodeid())
//		
//		String ipaddress = host.getIpAddress();
//		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ipaddress);
//		if(ipAllData == null)ipAllData = new Hashtable();
//		
//		String vid = ""; 
//		String sql = ""; 
//		
//		String url = "https://"+host.getIpAddress()+"/sdk"; // URL
//		String username = "";// 账户
//		String password = "";// 密码
//		if(ShareData.getVmwareConfig() != null && ShareData.getVmwareConfig().containsKey(host.getId()+"")){
//			VMWareConnectConfig vmwareConfig = (VMWareConnectConfig)ShareData.getVmwareConfig().get(host.getId()+"");
//			username = vmwareConfig.getUsername(); 
//			password = vmwareConfig.getPwd(); 
//		}
//		
//		String enpassword = "";
//		try{
//			if(!password.equals(""))
//			{
//				enpassword = EncryptUtil.decode(password);
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		StringBuffer sb = new StringBuffer();	
//		try{
//				SysLogger.info("begin collect vid:"+vid+"  xinxi =============host.getId()"+host.getId());
//				// 获得摘要信息、
//				SysLogger.info(url+"====username:"+username+"===enpassword:"+enpassword+"===vid:"+vid);
//				HashMap<String, Object> summaryresultMap = (HashMap<String, Object>)VIMMgr.syncVIMObjs(url, username, enpassword);
//				
//				if(!(ShareData.getSharedata().containsKey(host.getIpAddress())))
//					{
//						if(ipAllData == null)ipAllData = new Hashtable();
//						if(summaryresultMap != null && summaryresultMap.size()>0)ipAllData.put("summaryresultMap",summaryresultMap);
//					    ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
//					}else
//					 {
//						if(summaryresultMap != null && summaryresultMap.size()>0)((Hashtable)ShareData.getSharedata().get(host.getIpAddress())).put("summaryresultMap",summaryresultMap);
//					 }
//				Map<String, Object> summaryresultMap1 = null;
//				if(ipAllData != null){
//			    	try {
//			    		summaryresultMap1 = (Map<String, Object>)ipAllData.get("summaryresultMap");
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					VMWareVidDao vm_vid = new VMWareVidDao();
//			    	List all_vid = new ArrayList();//存放该节点所有的vid，根据它判断是否是第一次采集
//			    	String ip = ipaddress.replace(".", "_");
//			    	all_vid = vm_vid.getbynodeid(Long.parseLong(host.getId()+""));
//			    	List flag_vid = new ArrayList();
//		    		VMWareVidDao viddao = new VMWareVidDao();
//		    		flag_vid = viddao.queryVid(host.getId()+"");//查出flag=0的  数据的vid
//		    		List sql_list = new ArrayList();
//		    		sql_list.add("delete from nms_vmwarevid where nodeid = "+nodeid);
//		    		
//		    		VMWareDao vmdao = new VMWareDao();
//		    		vmdao.save(sql_list);//清楚基础表的数据，为了下步重新入库。
//		    		
//		    		ArrayList<HashMap<String, Object>> dslist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("Datastore");
//		    		if(dslist != null && dslist.size()>0)
//		    		{
//		    			sql_list.add("truncate table vm_basedatastore"+ip);
//		    			this.saveDatastore(dslist, nodeid,ipaddress);
//		    		}
//		    		ArrayList<HashMap<String, Object>> wulist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("HostSystem");
//	//	    		SysLogger.info("*****************************物理机采集完毕*****************************");
//		    		if(dslist != null && dslist.size()>0)
//		    		{
//		    			sql_list.add("truncate table vm_basephysical"+ip);
//		    			this.savePhysical(wulist, nodeid,ipaddress);
//		    		}
//		    		SysLogger.info("*****************************物理机入库完毕*****************************");
//			    	ArrayList<HashMap<String, Object>> rplist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("ResourcePool");
//	//		    	SysLogger.info("*****************************资源池机采集完毕************************");
//			    	if(rplist != null && rplist.size()>0)
//		    		{
//			    		sql_list.add("truncate table vm_baseresource"+ip);
//		    			this.saveResourcepool(rplist, nodeid,ipaddress);
//		    		}
//			    	SysLogger.info("*****************************资源池入库完毕********************");
//			    	ArrayList<HashMap<String, Object>> dclist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("Datacenter");
//	//		    	SysLogger.info("*****************************数据中心采集完毕*************************");
//			    	if(dclist != null && dclist.size()>0)
//		    		{
//			    		sql_list.add("truncate table vm_basedatacenter"+ip);
//			    		this.saveDatacenter(dclist, nodeid,ipaddress);
//		    		}
//			    	SysLogger.info("*****************************数据中心入库完毕*******************");
//			    	ArrayList<HashMap<String, Object>> crlist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("ComputeResource");//群集
//	//		    	SysLogger.info("*****************************群集采集完毕************************");
//			    	if(crlist != null && crlist.size()>0)
//		    		{
//			    		sql_list.add("truncate table vm_baseyun"+ip);
//			    		this.saveYun(crlist, nodeid,ipaddress);
//		    		}
//			    	SysLogger.info("*****************************群集入库完毕*********************");
//			    	ArrayList<HashMap<String, Object>> vmlist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("VirtualMachine");
//	//		    	SysLogger.info("*****************************虚拟机采集完毕*************************");
//			    	if(vmlist != null && vmlist.size()>0)
//		    		{
//			    		sql_list.add("truncate table vm_basevmware"+ip);
//			    		this.saveVmware(vmlist, nodeid,ipaddress);
//		    		}
//			    	SysLogger.info("*****************************虚拟机入库完毕*******************");
//			    	
//			    	
//			    	//更新nms_vmwarevid标示， 标示代表性能信息是否采集的意思
//			    	if(flag_vid != null && flag_vid.size()>0){
//			        	viddao.updateVidFlag(flag_vid, nodeid);
//			        }
//			        NodeGatherIndicators alarmIndicatorsNode = new NodeGatherIndicators();
//			        alarmIndicatorsNode.setNodeid(nodeid);
//			        LoadVMWare load = new LoadVMWare();
//			        load.collect_Data(alarmIndicatorsNode);
//			        sb.append("VMWare同步完成");
//			}
//		}catch(Exception e){
//			 sb.append("VMWare同步失败");
//			 e.printStackTrace();
//		}
//			SysLogger.info("**********************完成VMWare数据同步***************************");
//			
//			Map<String,String> map = new HashMap<String,String>();
//			map.put("option", sb.toString());
//			JSONObject json = JSONObject.fromObject(map);
//			out.print(json);
//			out.flush();
//			out.close();
//	}
//	
//	
//	
//	
//
//	 //物理机性能入库
//	 public void CreateResultTosqlHost(HashMap<String,Object> host,String ip,String vid)
//		{
//			
//		        	int size=0;
//		        	List l = new ArrayList();
//			        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		        	String allipstr = SysUtil.doip(ip);
//			        DecimalFormat df = new DecimalFormat("0.0");
//					Date cc = new Date();
//					String time = sdf.format(cc);
//					
//					String tablename = "vm_host"+allipstr;
//					
//					PerfEntityMetricBase[] perfEntityMetricBases = (PerfEntityMetricBase[]) host.get(vid);
//					for(int i=0;i<8;i++){
//						 String[] str = ((PerfMetricSeriesCSV) ((PerfEntityMetricCSV) perfEntityMetricBases[0]).getValue()[i]).getValue().toString().split(",");
//		      			 if(str != null && str.length>0){
//		      			   if(str.length < 288){
//		      				  size = str.length-1;
//		      			    }else{
//		      				  size = 287;
//		      			    }
//		    				  l.add(str[size]);
//		      			 }
//					}
////		      		List category = new ArrayList();
////		      		List thevalue = new ArrayList();
////		      		category.add("cpu");
////		      		category.add("cpuuse");
////		      		category.add("meminc");
////		      		category.add("memio");
////		      		category.add("mem");
////		      		category.add("disk");
//		      	    String cpu= df.format(Long.parseLong((String)l.get(0))/100);
//		      	    String cpuuse = l.get(5)+"";
//		      	    String meminc = l.get(2)+"";
//		      	    String memin = l.get(4)+"";
//		      	    String memout= l.get(3)+"";
//		      	    String mem =  df.format(Long.parseLong((String)l.get(6))/100);
//		      	    String disk =  l.get(7)+"";
////		      	    thevalue.add(cpu);thevalue.add(cpuuse);thevalue.add(meminc);thevalue.add(memio);thevalue.add(mem);thevalue.add(disk);
////		      	    for(int j=0;j<category.size();j++){
//		      	    	StringBuffer sBuffer = new StringBuffer();
//					    sBuffer.append("insert into ");
//					    sBuffer.append(tablename);
//				    	sBuffer.append(" (hostid,cpu,cpuuse,meminc,memin,memout,mem,disk,collecttime) ");
//					    sBuffer.append("values('");
//				    	sBuffer.append(vid);
//			    		sBuffer.append("','");
//		     			sBuffer.append(cpu);
//			    		sBuffer.append("','");
//			    		sBuffer.append(cpuuse);
//			    		sBuffer.append("','");
//			    		sBuffer.append(meminc);
//			    		sBuffer.append("','");
//			    		sBuffer.append(memin);
//			    		sBuffer.append("','");
//			    		sBuffer.append(memout);
//			    		sBuffer.append("','");
//			    		sBuffer.append(mem);
//			    		sBuffer.append("','");
//			    		sBuffer.append(disk);
//					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
//						sBuffer.append("','");
//						sBuffer.append(time);
//						sBuffer.append("')");
//					}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
//						sBuffer.append("',");
//						sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
//						sBuffer.append(")");
//					}
//					
//					//System.out.println(sql);
//				    	GathersqlListManager.Addsql(sBuffer.toString());
//				    	sBuffer = null;	
////		      	    }
//		   }
//	 
//	 
//	 //虚拟机性能入库
//	 public void CreateResultTosqlGuesthost(HashMap<String,Object> host,String ip,String hoid,String vid)
//		{
//			
//			int size=0;
//			List l = new ArrayList();
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String allipstr = SysUtil.doip(ip);
//			DecimalFormat df = new DecimalFormat("0.0");
//					Date cc = new Date();
//					String time = sdf.format(cc);
//					
//					String tablename = "vm_guesthost"+allipstr;
//					
//					 PerfEntityMetricBase[] perfEntityMetricBases = (PerfEntityMetricBase[]) host.get(vid);
//					for(int i=0;i<9;i++){
//						try{
//					
//						 String[] str = ((PerfMetricSeriesCSV) ((PerfEntityMetricCSV) perfEntityMetricBases[0]).getValue()[i]).getValue().toString().split(",");
//		      			 if(str != null && str.length>0){
//		      			   if(str.length < 288){
//		      				  size = str.length-1;
//		      			    }else{
//		      				  size = 287;
//		      			    }
//		    				  l.add(str[size]);
//		      			 }
//						}catch(NullPointerException e){
//							l.add("0");
////							e.printStackTrace();
//						}
//					}
////		      			 List category = new ArrayList();
////		      			 List thevalue = new ArrayList();
////		      			category.add("cpu");
////		      			category.add("cpuuse");
////		      			category.add("meminc");
////		      			category.add("memio");
////		      			category.add("mem");
////		      			category.add("disk");
////		      			category.add("net");
//		      	    String cpu= df.format(Long.parseLong((String)l.get(0))/100);
//		      	    String cpuuse = l.get(5)+"";
//		      	    String meminc = l.get(2)+"";
//		      	    String memin = l.get(4)+"";
//		      	    String memout = l.get(3)+"";
//		      	    String mem =  df.format(Long.parseLong((String)l.get(6))/100);
//		      	    String disk =  l.get(7)+"";
//		      	    String net= l.get(8)+"";
////		      	    thevalue.add(cpu);thevalue.add(cpuuse);thevalue.add(meminc);thevalue.add(memio);thevalue.add(mem);thevalue.add(disk);thevalue.add(net);
////		      	    for(int j=0;j<category.size();j++){
//		      	    StringBuffer sBuffer = new StringBuffer();
//					sBuffer.append("insert into ");
//					sBuffer.append(tablename);
//					sBuffer.append(" (vid,hostid,cpu,cpuuse,meminc,memin,memout,mem,disk,net,collecttime) ");
//					sBuffer.append("values('");
//					sBuffer.append(vid);
//					sBuffer.append("','");
//					sBuffer.append(hoid);
//					sBuffer.append("','");
//					sBuffer.append(cpu);
//					sBuffer.append("','");
//					sBuffer.append(cpuuse);
//					sBuffer.append("','");
//					sBuffer.append(meminc);
//					sBuffer.append("','");
//					sBuffer.append(memin);
//					sBuffer.append("','");
//					sBuffer.append(memout);
//					sBuffer.append("','");
//					sBuffer.append(mem);
//					sBuffer.append("','");
//					sBuffer.append(disk);
//					sBuffer.append("','");
//					sBuffer.append(net);
//					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
//						sBuffer.append("','");
//						sBuffer.append(time);
//						sBuffer.append("')");
//					}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
//						sBuffer.append("',");
//						sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
//						sBuffer.append(")");
//					}
//					//System.out.println(sql);
//					GathersqlListManager.Addsql(sBuffer.toString());
//					sBuffer = null;	
////		      	    }
//		   }
//	 
//	 //集群入库
//	 public void CreateResultTosqlCR(List cr_list,String ip,String vid)
//		{
//			
//			int size=0;
//			List l = new ArrayList();
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String allipstr = SysUtil.doip(ip);
//			DecimalFormat df = new DecimalFormat("0.0");
//					Date cc = new Date();
//					String time = sdf.format(cc);
//					
//					String tablename = "vm_cluster"+allipstr;
//					if(cr_list.size() == 4){
//		      	    String cpu= cr_list.get(0)+"";
//		      	    String cputotal = cr_list.get(1)+"";
//		      	    String mem = cr_list.get(2)+"";
//		      	    String memtotal = cr_list.get(3)+"";
////		      	    thevalue.add(cpu);thevalue.add(cpuuse);thevalue.add(meminc);thevalue.add(memio);thevalue.add(mem);thevalue.add(disk);thevalue.add(net);
////		      	    for(int j=0;j<category.size();j++){
//		      	    StringBuffer sBuffer = new StringBuffer();
//					sBuffer.append("insert into ");
//					sBuffer.append(tablename);
//					sBuffer.append(" (vid,cpu,cputotal,mem,memtotal,collecttime) ");
//					sBuffer.append("values('");
//					sBuffer.append(vid);
//					sBuffer.append("','");
//					sBuffer.append(cpu);
//					sBuffer.append("','");
//					sBuffer.append(cputotal);
//					sBuffer.append("','");
//					sBuffer.append(mem);
//					sBuffer.append("','");
//					sBuffer.append(memtotal);
//					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
//						sBuffer.append("','");
//						sBuffer.append(time);
//						sBuffer.append("')");
//					}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
//						sBuffer.append("',");
//						sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
//						sBuffer.append(")");
//					}
//					System.out.println(sBuffer.toString());
//					GathersqlListManager.Addsql(sBuffer.toString());
//					sBuffer = null;	
//		      	    }
//		   }
//	 
//	//存储入库
//	 public void CreateResultTosqlDS(List ds_list,String ip,String vid)
//		{
//			
//			int size=0;
//			List l = new ArrayList();
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String allipstr = SysUtil.doip(ip);
//			DecimalFormat df = new DecimalFormat("0.0");
//					Date cc = new Date();
//					String time = sdf.format(cc);
//					
//					String tablename = "vm_datastore"+allipstr;
//					
//		      	    String used=df.format(Double.parseDouble((String)ds_list.get(0))/(1024*1024))+"";
//		      	    String capacity =df.format(Double.parseDouble((String)ds_list.get(2))/(1024*1024))+"";
//		      	    String assigned =df.format(Double.parseDouble((String)ds_list.get(1))/(1024*1024))+"";
//		      	    String use= df.format(Long.parseLong((String)ds_list.get(0))*100/Long.parseLong((String)ds_list.get(2)));
////		      	    thevalue.add(cpu);thevalue.add(cpuuse);thevalue.add(meminc);thevalue.add(memio);thevalue.add(mem);thevalue.add(disk);thevalue.add(net);
////		      	    for(int j=0;j<category.size();j++){
//		      	    StringBuffer sBuffer = new StringBuffer();
//					sBuffer.append("insert into ");
//					sBuffer.append(tablename);
//					sBuffer.append(" (vid,used,assigned ,capacity,useuse,collecttime) ");
//					sBuffer.append("values('");
//					sBuffer.append(vid);
//					sBuffer.append("','");
//					sBuffer.append(used);
//					sBuffer.append("','");
//					sBuffer.append(assigned);
//					sBuffer.append("','");
//					sBuffer.append(capacity);
//					sBuffer.append("','");
//					sBuffer.append(use);
//					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
//						sBuffer.append("','");
//						sBuffer.append(time);
//						sBuffer.append("')");
//					}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
//						sBuffer.append("',");
//						sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
//						sBuffer.append(")");
//					}
//					System.out.println(sBuffer.toString());
//					GathersqlListManager.Addsql(sBuffer.toString());
//					sBuffer = null;	
////		      	    }
//		   }
//	 
//	 
//	//资源池入库
//	 public void CreateResultTosqlRP(List rp_list,String ip,String vid)
//		{
//			
//			int size=0;
//			List l = new ArrayList();
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String allipstr = SysUtil.doip(ip);
//			DecimalFormat df = new DecimalFormat("0.0");
//					Date cc = new Date();
//					String time = sdf.format(cc);
//					String tablename = "vm_resourcepool"+allipstr;
//					if(rp_list.size() == 1){
//		      	    String cpu=rp_list.get(0)+"";
////		      	    thevalue.add(cpu);thevalue.add(cpuuse);thevalue.add(meminc);thevalue.add(memio);thevalue.add(mem);thevalue.add(disk);thevalue.add(net);
////		      	    for(int j=0;j<category.size();j++){
//		      	    StringBuffer sBuffer = new StringBuffer();
//					sBuffer.append("insert into ");
//					sBuffer.append(tablename);
//					sBuffer.append(" (vid,cpu,collecttime) ");
//					sBuffer.append("values('");
//					sBuffer.append(vid);
//					sBuffer.append("','");
//					sBuffer.append(cpu);
//					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
//						sBuffer.append("','");
//						sBuffer.append(time);
//						sBuffer.append("')");
//					}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
//						sBuffer.append("',");
//						sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
//						sBuffer.append(")");
//					}
//					System.out.println(sBuffer.toString());
//					GathersqlListManager.Addsql(sBuffer.toString());
//					sBuffer = null;	
////		      	    }
//					}
//		   }
//	 
//	 public void savePhysical(List<HashMap<String,Object>> wulist,String nodeid,String ipaddress){
//		 if(wulist != null && wulist.size()>0  ){
//			VMWareVidDao viddao = new VMWareVidDao();
//			VMWareDao vmdao = new VMWareDao();
//			List physical = new ArrayList();
//			List physical_vid = new ArrayList();
//			for(int i=0;i<wulist.size();i++){
//				VMWareVO vmware = new VMWareVO();
//				VMWareVid vmvid = new VMWareVid();
//				String host_vid = wulist.get(i).get("vid").toString();
//				vmware.setVid(wulist.get(i).get("vid").toString());
//				vmware.setName(wulist.get(i).get("name").toString());
//				vmware.setModel(wulist.get(i).get("model").toString());
//				vmware.setCpunum(wulist.get(i).get("numcpu").toString());
//				vmware.setNetnum(wulist.get(i).get("numnics").toString());
//				vmware.setMemory(wulist.get(i).get("memorysizemb").toString());
//				vmware.setGhz(wulist.get(i).get("cpumhz").toString());
//				vmware.setHostpower(wulist.get(i).get("powerstate").toString());
//				physical.add(vmware);
//				
//				vmvid.setVid(host_vid);
//				vmvid.setCategory("physical");
//				vmvid.setNodeid(Long.parseLong(nodeid));
//				vmvid.setGuestname(wulist.get(i).get("name").toString());
//				physical_vid.add(vmvid);
//			}
//			vmdao.savePhysical(physical, ipaddress);
//			viddao.save(physical_vid);
//		}
//	 }
//	 
//public void saveVmware(List<HashMap<String,Object>> vmlist,String nodeid,String ipaddress){
//	 if(vmlist != null && vmlist.size()>0){
//			VMWareDao vmdao = new VMWareDao();
//			List vm = new ArrayList();
//			List vm_id = new ArrayList();
//			for(int i=0;i<vmlist.size();i++){
//				try{
//					VMWareVO vmware = new VMWareVO();
//					VMWareVid vmvid = new VMWareVid();
//					String id = vmlist.get(i).get("vid").toString();
//					vmware.setVid(vmlist.get(i).get("vid").toString());
//					vmware.setName(vmlist.get(i).get("name").toString());
//					vmware.setFullname(vmlist.get(i).get("guestfullname").toString());
//					vmware.setHoid(vmlist.get(i).get("hoid").toString());
//					vmware.setMemoryuse(vmlist.get(i).get("memorysizemb").toString());
//					vmware.setCpu(vmlist.get(i).get("numcpu").toString()+"颗*"+vmlist.get(i).get("numcore").toString()+"核");
//					vmware.setVmpower(vmlist.get(i).get("powerstate").toString());
//					vm.add(vmware);
//					//
//					vmvid.setVid(id);
//					vmvid.setHoid(vmlist.get(i).get("hoid").toString());
//					vmvid.setCategory("vmware");
//					vmvid.setGuestname(vmlist.get(i).get("name").toString());
//					vmvid.setNodeid(Long.parseLong(nodeid));
//					vm_id.add(vmvid);
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//			}
//			vmdao.saveVmware(vm, ipaddress);
//			VMWareVidDao viddao = new VMWareVidDao();
//			viddao.save(vm_id);
//		}
//	 }
//public void saveYun(List<HashMap<String,Object>> crlist,String nodeid,String ipaddress){
//	 if(crlist != null  && crlist.size()>0  ){
//			VMWareVidDao viddao = new VMWareVidDao();
//			VMWareDao vmdao = new VMWareDao();
//			List vm = new ArrayList();
//			List vm_id = new ArrayList();
//			for(int i=0;i<crlist.size();i++){
//				VMWareVO vmware = new VMWareVO();
//				VMWareVid vmvid = new VMWareVid();
//				String id = crlist.get(i).get("vid").toString();
//				vmware.setVid(crlist.get(i).get("vid").toString());
//				vmware.setName(crlist.get(i).get("name").toString());
//				vmware.setDisk(crlist.get(i).get("totaldssizemb").toString());
//				vmware.setCpuuse(crlist.get(i).get("totalcpu").toString());
//				vmware.setHostnum(crlist.get(i).get("numhosts").toString());
//				vmware.setMem(crlist.get(i).get("totalmemory").toString());
//				vmware.setCpunum(crlist.get(i).get("numcpucores").toString());
//				vm.add(vmware);
//				//
//				vmvid.setVid(id);
//				vmvid.setCategory("yun");
//				vmvid.setGuestname(crlist.get(i).get("name").toString());
//				vmvid.setNodeid(Long.parseLong(nodeid));
//				vm_id.add(vmvid);
//			}
//			vmdao.saveYun(vm, ipaddress);
//			viddao.save(vm_id);
//		}
//}
//public void saveDatastore(List<HashMap<String,Object>> dslist,String nodeid,String ipaddress){
//
//		if(dslist != null && dslist.size()>0){
//			VMWareVidDao viddao = new VMWareVidDao();
//			VMWareDao vmdao = new VMWareDao();
//			List vm = new ArrayList();
//			List vm_id = new ArrayList();
//			for(int i=0;i<dslist.size();i++){
//				VMWareVO vmware = new VMWareVO();
//				VMWareVid vmvid = new VMWareVid();
//				String id = dslist.get(i).get("vid").toString();
//				vmware.setVid(dslist.get(i).get("vid").toString());
//				vmware.setName(dslist.get(i).get("name").toString());
//				vmware.setStore(dslist.get(i).get("capacity").toString());
//				vmware.setUnusedstore(dslist.get(i).get("freespace").toString());
//				vm.add(vmware);
//				//
//				vmvid.setVid(id);
//				vmvid.setCategory("datastore");
//				vmvid.setGuestname(dslist.get(i).get("name").toString());
//				vmvid.setNodeid(Long.parseLong(nodeid));
//				vm_id.add(vmvid);
//			}
//			vmdao.saveDatastore(vm, ipaddress);
//			viddao.save(vm_id);
//		}
//}
//public void saveDatacenter(List<HashMap<String,Object>> dclist,String nodeid,String ipaddress){
//	 if(dclist != null && dclist.size()>0  ){
//			VMWareVidDao viddao = new VMWareVidDao();
//			VMWareDao vmdao = new VMWareDao();
//			List vm = new ArrayList();
//			List vm_id = new ArrayList();
//			for(int i=0;i<dclist.size();i++){
//				VMWareVO vmware = new VMWareVO();
//				VMWareVid vmvid = new VMWareVid();
//				String id = dclist.get(i).get("vid").toString();
//				vmware.setVid(dclist.get(i).get("vid").toString());
//				vmware.setName(dclist.get(i).get("name").toString());
//				vmware.setDcid(dclist.get(i).get("vid").toString());
//				vm.add(vmware);
//				//
//				vmvid.setVid(id);
//				vmvid.setCategory("datacenter");
//				vmvid.setGuestname(dclist.get(i).get("name").toString());
//				vmvid.setNodeid(Long.parseLong(nodeid));
//				vm_id.add(vmvid);
//			}
//			vmdao.saveDatacenter(vm, ipaddress);
//			viddao.save(vm_id);
//		}
//}
//public void saveResourcepool(List<HashMap<String,Object>> rplist,String nodeid,String ipaddress){
//	if( rplist != null  && rplist.size()>0  ){
//		VMWareVidDao viddao = new VMWareVidDao();
//		VMWareDao vmdao = new VMWareDao();
//		List vm = new ArrayList();
//		List vm_id = new ArrayList();
//		for(int i=0;i<rplist.size();i++){
//			VMWareVO vmware = new VMWareVO();
//			VMWareVid vmvid = new VMWareVid();
//			String id = rplist.get(i).get("vid").toString();
//			vmware.setVid(rplist.get(i).get("vid").toString());
//			vmware.setName(rplist.get(i).get("name").toString());
//			vmware.setDcid(rplist.get(i).get("dcid").toString());
//			vmware.setCrid(rplist.get(i).get("crid").toString());
//			vm.add(vmware);
//			//
//			vmvid.setVid(id);
//			vmvid.setCategory("resourcepool");
//			vmvid.setGuestname(rplist.get(i).get("name").toString());
//			vmvid.setNodeid(Long.parseLong(nodeid));
//			vm_id.add(vmvid);
//		}
//		vmdao.saveResourcepool(vm, ipaddress);
//		viddao.save(vm_id);
//	}
//}
//
//
//
//
//public void perList(){
//	String tmp = request.getParameter("id");
//	String flag = request.getParameter("flag");
//	
//	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
//	StringBuffer sb = new StringBuffer();
//	HashMap<String,Object> map_host = new HashMap<String,Object>();
//	Hashtable vmData = new Hashtable();
//	vmData = (Hashtable) ShareData.getVmdata().get(host.getIpAddress());
//	
//	DecimalFormat df = new DecimalFormat("0.0");
//    int size = 0;
//    String cpu_using = "";
//    String memory_increse = "";
//    String memory_in="";
//    String memory_out="";
//    String cpu_use="";
//    String memory_use="";
//    String disc = "";
//    if(flag.equals("physical")){
//	   List l = new ArrayList();
//	   List l_vm = new ArrayList();
//	   List l_vmware = new ArrayList();
//	   VMWareVidDao wudao = new VMWareVidDao();
//       List wu_list = wudao.queryVidFlag("physical", host.getId()+"", "");
//       sb.append("<table cellspacing='0' cellpadding='0' width='width:680' bgcolor='#FFFFFF' border=1  class='detail-data-body'>");
//       sb.append("<tr align='center' height=28 class='microsoftLook0'>");
//       sb.append("<th width='5%'  align='center'>&nbsp;序号</th>" +
//       		    "<th width='10%' align='center'>&nbsp;CPU利用率</th>" +
//       	    	"<th width='20%' align='center'>内存(虚拟增长)MB</th>" +
//       		    "<th width='20%' align='center'>&nbsp;内存(MBps)换入/换出</th>"+
//                "<th width='15%' align='center'>&nbsp;CPU(MHz)使用</th>"+
//                "<th width='10%' align='center'>&nbsp;内存使用</th>"+
//                "<th width='15%' align='center'>&nbsp;磁盘(KBps)使用</th></tr>");
//       try{
//       if(wu_list != null && wu_list.size()>0  ){
//  		  HashMap<String, Object> wuliji = (HashMap<String, Object>) ((HashMap<String, Object>)vmData.get("wuliji")).get("wuliji_per");
//  		 for(int i=0;i<wu_list.size();i++){
//  		  PerfEntityMetricBase[] perfEntityMetricBases = (PerfEntityMetricBase[]) wuliji.get(wu_list.get(i));
////  		  ArrayList vmware = (ArrayList)((HashMap<String, Object>)vmData.get("vmware")).get(select);
//  	      //取物理机的性能
//  		  for(int j=0;j<8;j++)
//  		   {
//  		    String kpi =((PerfMetricSeriesCSV) ((PerfEntityMetricCSV) perfEntityMetricBases[0]).getValue()[j]).getId().getCounterId()+"";
//  		    String[] str = ((PerfMetricSeriesCSV) ((PerfEntityMetricCSV) perfEntityMetricBases[0]).getValue()[j]).getValue().toString().split(",");
//  			 if(str != null && str.length>0){
//  			   if(str.length < 288){
//  				  size = str.length-1;
//  			    }else{
//  				  size = 287;
//  			    }
//				  l.add(str[size]);
//  			 }
//  		   }
//  		 sb.append("<tr align='center' height=28 class='microsoftLook'>");
//  		 sb.append("<th width='10%'  align=center>&nbsp;"+(i+1)+"</th>" +
//  		 		 "<th width='10%' align=center>&nbsp;"+(df.format(Long.parseLong((String)l.get(0))/100)+"%")+"</th>" +
//        	     "<th width='15%' align=center>&nbsp;"+l.get(2)+"</th>" +
//        		 "<th width='20%' align=center>&nbsp;"+(l.get(4)+"/"+l.get(3))+"</th>"+
//                 "<th width='15%' align=center>&nbsp;"+(l.get(5))+"</th>"+
//                 "<th width='10%' align=center>&nbsp;"+(df.format(Long.parseLong((String)l.get(6))/100)+"%")+"</th>"+
//                 "<th width='20%' align=center>&nbsp;"+(l.get(7))+"</th></tr>");
//  		  
//  		  
//  	   }
//       }
//       sb.append("</table>");
//       }catch(Exception e){
//       	
//       }
//     }
//   
//    
//     if(flag.equalsIgnoreCase("yun")){ 
//    	 
//      
//   	   VMWareVidDao wudao = new VMWareVidDao();
//          List vm_list = wudao.queryVidFlag("yun", host.getId()+"", "");
//          sb.append("<table cellspacing='0' cellpadding='0' width='width:680' bgcolor='#FFFFFF' border=1  class='detail-data-body'>");
//          sb.append("<tr align='center' height=28 class='microsoftLook0'>");
//          sb.append("<th width='10%'  align='center'>&nbsp;序号</th>" +
//        		  "<th width='20%' align='center'>&nbsp;名称</th>" +
//          	    	"<th width='15%' align='center'>&nbsp;cpu使用(GHz)</th>" +
//          		    "<th width='20%' align='center'>&nbsp;cpu总计(GHz)</th>"+
//                   "<th width='15%' align='center'>&nbsp;内存已消耗(GB)</th>"+
//                   "<th width='20%' align='center'>&nbsp;内存总计(GB)</th></tr>");
//          
//          if(vm_list != null && vm_list.size()>0  ){
//     		  HashMap<String, Object> wuliji = null;
//     		  try{
//     			 wuliji = (HashMap<String, Object>) ((HashMap<String, Object>)vmData.get("cr"));
//     		  }catch(Exception e){
//     			 wuliji = new HashMap<String,Object>();
//     		  }
//     		  VMWareVidDao dao  = new VMWareVidDao();
//     		  String name = "";
//     		  String value0="0";
//     		 String value1="0";
//     		  String value2="0";
//     		  String value3="0";
//     		  DecimalFormat formcat=new DecimalFormat("#0.##");
//     		  try{
//	     		  for(int i=0;i<vm_list.size();i++){
//		     		  List l = new ArrayList();
//		     			  l = (List) wuliji.get(vm_list.get(i));
//		     			  value0= l.get(0)+"";
//		         		  value1= l.get(1)+"";
//		     			  value2 = l.get(2)+"";
//		     			  value3 = l.get(3)+"";
//		     			 if(!value0.equals("null"))value0=formcat.format(Float.parseFloat(value0)/1024f);
//		     			 if(!value1.equals("null"))value1=formcat.format(Float.parseFloat(value1)/1024f);
//		     			  if(!value2.equals("null"))value2=formcat.format(Float.parseFloat(value2)/1024f);
//		     			 if(!value3.equals("null"))value3=formcat.format(Float.parseFloat(value3)/1024f);
//		     		  name = dao.queryName(tmp, vm_list.get(i).toString());
//		     		  sb.append("<tr align='center' height=28 class='microsoftLook'>");
//		     		  sb.append("<th width='10%'  align=center>&nbsp;"+(i+1)+"</th>" +
//		     				 "<th width='20%' align=center>&nbsp;"+name+"</th>" +
//		           	     "<th width='15%' align=center>&nbsp;"+value0+"</th>" +
//		           		 "<th width='20%' align=center>&nbsp;"+value1+"</th>"+
//		                    "<th width='15%' align=center>&nbsp;"+value3+"</th>"+
//		                    "<th width='20%' align=center>&nbsp;"+value2+"</th></tr>");
//		     	   }
//     		  }catch(Exception e){
//     			e.printStackTrace();  
//     		  }
//          }
//          sb.append("</table>");
//  }
//  //存储
//   else if(flag.equals("datastore")){
//	   List l = new ArrayList();
//   	   VMWareVidDao wudao = new VMWareVidDao();
//          List vm_list = wudao.queryVidFlag("datastore", host.getId()+"", "");
//          sb.append("<table cellspacing='0' cellpadding='0' width='width:680' bgcolor='#FFFFFF' border=1  class='detail-data-body'>");
//          sb.append("<tr align='center' height=28 class='microsoftLook0'>");
//          sb.append("<th width='10%'  align=center>&nbsp;序号</th>" +
//        		  "<th width='20%' align=center>&nbsp;名称</th>" +
//          	    	"<th width='15%' align=center>&nbsp;已分配</th>" +
//          		    "<th width='20%' align=center>&nbsp;已使用</th>"+
//                   "<th width='20%' align=center>&nbsp;容量</th>"+
//                   "<th width='15%' align=center>&nbsp;利用率</th></tr>");
//          
//          if(vm_list != null && vm_list.size()>0  ){
//     		 HashMap<String, Object> wuliji = null;
//     		 try{
//     			wuliji = (HashMap<String, Object>) ((HashMap<String, Object>)vmData.get("ds")); 
//     		 }catch(Exception e){
//     			wuliji = new HashMap<String, Object>();
//     		 }
//     		 VMWareVidDao dao  = new VMWareVidDao();
//    		  String name = "";
//     		 for(int i=0;i<vm_list.size();i++){
//     			 try
//     			  {
//     				 l = (List) wuliji.get(vm_list.get(i));
//     			  }catch(Exception e){
//     				  return;
//     			  }
//     			 name = dao.queryName(tmp, vm_list.get(i).toString());
//     			 
//     			 try{
//     		   sb.append("<tr align='center' height=28 class=''>");
//     		   sb.append("<th width='10%'  align=center>&nbsp;"+(i+1)+"</th>" +
//     				  "<th width='20%' align=center>&nbsp;"+name+"</th>" +
//           	          "<th width='15%' align=center>&nbsp;"+(df.format(Double.parseDouble((String)l.get(1))/(1024*1024))+"GB")+"</th>" +
//           		      "<th width='20%' align=center>&nbsp;"+(df.format(Double.parseDouble((String)l.get(0))/(1024*1024))+"GB")+"</th>"+
//                      "<th width='20%' align=center>&nbsp;"+(df.format(Double.parseDouble((String)l.get(2))/(1024*1024))+"GB")+"</th>"+
//                      "<th width='15%' align=center>&nbsp;"+(df.format(Long.parseLong((String)l.get(0))*100/Long.parseLong((String)l.get(2)))+"%")+"</th></tr>");
//     			 }catch(Exception e){
//     				 
//     			 }
//     	   }
//          }
//          sb.append("</table>");
//	   
//   }
//   else if(flag.equalsIgnoreCase("resourcepool")){ 
//	   List l = new ArrayList();
//   	   VMWareVidDao wudao = new VMWareVidDao();
//          List vm_list = wudao.queryVidFlag("resourcepool", host.getId()+"", "");
//          sb.append("<table cellspacing='0' cellpadding='0' width='width:680' bgcolor='#FFFFFF' border=1  class='detail-data-body'>");
//          sb.append("<tr align='center' height=28 class='microsoftLook0'>");
//          sb.append("<th width='10%'  align='center'>&nbsp;序号</th>" +
//        		  "<th width=40%' align='center'>&nbsp;名称</th>" +
//          	    	"<th width='50%' align=center>&nbsp;cpu使用(GHz)</th></tr>");
//          
//          if(vm_list != null &&  vm_list.size()>0 ){
//     		 HashMap<String, Object> wuliji = null;
//     		 try{
//     			wuliji = (HashMap<String, Object>) ((HashMap<String, Object>)vmData.get("rp"));
//     		 }catch(Exception e){
//     			wuliji = new HashMap<String, Object>();
//     		 }
//     		 VMWareVidDao dao  = new VMWareVidDao();
//   		     String name = "";
//     		 for(int i=0;i<vm_list.size();i++){
//     			 try
//     			{
//     				 l = (List) wuliji.get(vm_list.get(i));
//     			}catch(Exception e){
//     				l = new ArrayList();
//     			}
//     			  DecimalFormat formcat=new DecimalFormat("#0.##");
//     			String value ="0";
//     			try{value = l.get(0)+"";
//     			if(!value.equals("null"))value=formcat.format(Float.parseFloat(value)/1024f);
//     			}catch(Exception e){}
//     			 name = dao.queryName(tmp, vm_list.get(i).toString());
//     		    sb.append("<tr align='center' height=28 class='microsoftLook'>");
//     		    sb.append("<th width='10%'  align=center>&nbsp;"+(i+1)+"</th>" +
//     		    		 "<th width='40%' align=center>&nbsp;"+name+"</th>" +
//           	     "<th width='50%' align=center>&nbsp;"+value+"</th></tr>");
//     	   }
//          }
//          sb.append("</table>");
// //资源池
//	}
//	Map<String,String> map = new HashMap<String,String>();
//	map.put("option", sb.toString());
//	JSONObject json = JSONObject.fromObject(map);
//	out.print(json);
//	out.flush();
//	out.close();
//}

//public void emcRaid(){
//	String ip = request.getParameter("ip");
//	String flag = request.getParameter("flag");
//	String nodeid = request.getParameter("nodeid");
//	StringBuffer sb = new StringBuffer();
//	HashMap<String,Object> map_host = new HashMap<String,Object>();
//	Hashtable ipAllData = new Hashtable();
//	ipAllData = (Hashtable) ShareData.getEmcdata().get(ip);
////	ipAllData = null;
//	if(ipAllData == null){
//		raidDao dao = new raidDao();
//		List<RaidGroup> list = dao.query(nodeid);
//		HashMap raid_map = new HashMap();
//		RaidGroup raid = null;
//		if(list != null || list.size()> 0){
//			for(int i=0;i<list.size();i++){
//				raid = list.get(i);
//				raid_map.put(raid.getRid(), raid);
//			}
//		}
//		
//		RaidGroup sys_vo = (RaidGroup) raid_map.get(flag);
//		if(sys_vo != null){
//		sb.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//		sb.append("<tr valign=top>");
//		sb.append("<td align='right' height='24' width='10%'>Raid组ID:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getRid()+"</td>");
//		sb.append("<td align='right' height='24' width='10%'>类型:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getType()+"</td>");
//		sb.append("</tr>");
//		sb.append("<tr style='background-color: #ECECEC;'>");
//		sb.append("<td align='right' height='24' width='10%'>磁盘最大数量:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getMaxNumDisk()+"</td>");
//		sb.append("<td align='right' height='24' width='10%'>LUN最大数量:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getMaxNumLun()+"</td>");
//		sb.append("</tr>");
//		sb.append("</tr>");
//		sb.append("<tr >");
//		sb.append("<td align='right' height='24' width='10%'>原始容量:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getRawCapacity()+"</td>");
//		sb.append("<td align='right' height='24' width='10%'>逻辑容量:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getLogicalCapacity()+"</td>");
//		sb.append("</tr>");
//		sb.append("</tr>");
//		sb.append("<tr style='background-color: #ECECEC;'>");
//		sb.append("<td align='right' height='24' width='10%'>空闲容量:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getFreeCapacity()+"</td>");
//		sb.append("<td align='right' height='24' width='10%'>重建优先级:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getDefragPriority()+"</td>");
//		sb.append("</tr>");
//		
//		sb.append("<tr >");
//		sb.append("<td align='right' height='24' width='10%'>状态:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;");
//		if(sys_vo.getStateStr() != null){
//			String[] state = sys_vo.getStateStr().split(";");
//		for(int i=0;i<state.length;i++)
//		{
//			sb.append(state[i]+"</br>&nbsp;");
//		}
//		}
//		sb.append("</td>");
//		sb.append("<td align='right' height='24' width='10%'>Lun列表:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;");
//		if(sys_vo.getLunlistStr() != null && sys_vo.getLunlistStr().length()>0){
//	      String[] lunlist = sys_vo.getLunlistStr().split(";");
//		  for(int i=0;i<lunlist.length;i++)
//		  {
//			sb.append(lunlist[i]+"</br>&nbsp;");
//		  }
//		}else{
//			 
//			 sb.append("&nbsp;Not Available&nbsp;");
//		}
//		
//		sb.append("</td>");
//		sb.append("</tr>");
//		
////		sb.append("<tr style='background-color: #ECECEC;' >");
////		sb.append("<td align='right' height='24' width='10%'>磁盘列表:&nbsp;</td>	");
////		sb.append("<td width='90%' colspan=4>&nbsp;");
////		if(sys_vo.getDisksList() != null && sys_vo.getDisksList().size()>0){
////		for(int i=0;i<sys_vo.getDisksList().size();i++)
////		{
////			sb.append(sys_vo.getDisksList().get(i)+"</br>&nbsp;");
////		}
////		}
////		sb.append("</td>");
////		sb.append("</tr></table></td></tr></table>");
//		
//		sb.append("<tr><td colspan=4><table id='detail-content-body' class='detail-content-body'>");
//		if(sys_vo.getDisklistStr() != null && sys_vo.getDisklistStr().length()>0){
//			String[] disklist = sys_vo.getDisklistStr().split(";");
//		for(int i=0;i<disklist.length;i++)
//		{
//			if(i==0){
//			sb.append("<tr style='background-color: #ECECEC;'><td align='right' height='24' width='10%'>&nbsp;&nbsp;磁盘列表:&nbsp;</td>	");
//			sb.append("<td width='90%' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//			sb.append(disklist[i]);
//			}else if(i%2 == 1){
//				sb.append("<tr><td align='right' height='20' width='10%'>&nbsp;</td>	");
//				sb.append("<td width='90%' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//				sb.append(disklist[i]);
//			}else{
//				sb.append("<tr style='background-color: #ECECEC;'><td align='right' height='20' width='10%'></td>	");
//				sb.append("<td width='90%' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//				sb.append(disklist[i]);
//			}
//		}
//		}else{
//			sb.append("<tr><td align='right' height='24' width='10%'>磁盘列表:&nbsp;</td>	");
//			sb.append("<td width='90%' >&nbsp;");
//		}
//		sb.append("</td></tr>");
//		sb.append("</table></td>");
//		sb.append("</tr></table></td></tr></table>");
//		
//		
//		
//		}else{
//			sb.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1'>");
//			sb.append("<tr valign=top>");
//			sb.append("<td align='right' height='24' width='10%'>Raid租ID:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("<td align='right' height='24' width='10%'>类型:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("</tr>");
//			sb.append("<tr style='background-color: #ECECEC;'>");
//			sb.append("<td align='right' height='24' width='10%'>磁盘最大数量:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("<td align='right' height='24' width='10%'>LUN最大数量:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("</tr>");
//			sb.append("</tr>");
//			sb.append("<tr >");
//			sb.append("<td align='right' height='24' width='10%'>原始容量:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("<td align='right' height='24' width='10%'>逻辑容量:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("</tr>");
//			sb.append("</tr>");
//			sb.append("<tr style='background-color: #ECECEC;'>");
//			sb.append("<td align='right' height='24' width='10%'>空闲容量:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("<td align='right' height='24' width='10%'>重建优先级:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("</tr>");
//			
//			sb.append("<tr >");
//			sb.append("<td align='right' height='24' width='10%'>状态:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;");
//			
//			sb.append("</br>&nbsp;</td>");
//			sb.append("<td align='right' height='24' width='10%'>Lun列表:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;");
//			sb.append("</br>&nbsp;</td>");
//			sb.append("</tr>");
//			
//			sb.append("<tr style='background-color: #ECECEC;'>");
//			sb.append("<td align='right' height='24' width='10%'>磁盘列表:&nbsp;</td>	");
//			sb.append("<td width='90%' colspan=3>&nbsp;");
//			
//			sb.append("</br>&nbsp;</td>");
//			sb.append("</tr></table></td></tr></table>");
//		}
//	}else{
//		RaidGroup raid = null;
//		List<RaidGroup> list = (List<RaidGroup>) ipAllData.get("raid");
//		HashMap raid_map = new HashMap();
//		if(list != null || list.size()> 0){
//			for(int i=0;i<list.size();i++){
//				raid = list.get(i);
//				raid_map.put(raid.getRid(), raid);
//			}
//		}
//		
//		RaidGroup sys_vo = (RaidGroup) raid_map.get(flag);
//		if(sys_vo != null){
//		sb.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//		sb.append("<tr valign=top>");
//		sb.append("<td align='right' height='24' width='10%'>Raid组ID:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getRid()+"</td>");
//		sb.append("<td align='right' height='24' width='10%'>类型:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getType()+"</td>");
//		sb.append("</tr>");
//		sb.append("<tr style='background-color: #ECECEC;'>");
//		sb.append("<td align='right' height='24' width='10%'>磁盘最大数量:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getMaxNumDisk()+"</td>");
//		sb.append("<td align='right' height='24' width='10%'>LUN最大数量:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getMaxNumLun()+"</td>");
//		sb.append("</tr>");
//		sb.append("</tr>");
//		sb.append("<tr >");
//		sb.append("<td align='right' height='24' width='10%'>原始容量:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getRawCapacity()+"</td>");
//		sb.append("<td align='right' height='24' width='10%'>逻辑容量:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getLogicalCapacity()+"</td>");
//		sb.append("</tr>");
//		sb.append("</tr>");
//		sb.append("<tr style='background-color: #ECECEC;'>");
//		sb.append("<td align='right' height='24' width='10%'>空闲容量:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getFreeCapacity()+"</td>");
//		sb.append("<td align='right' height='24' width='10%'>重建优先级:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;"+sys_vo.getDefragPriority()+"</td>");
//		sb.append("</tr>");
//		
//		sb.append("<tr >");
//		sb.append("<td align='right' height='24' width='10%'>状态:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;");
//		if(sys_vo.getState() != null && sys_vo.getState().length>0){
//		for(int i=0;i<sys_vo.getState().length;i++)
//		{
//			sb.append(sys_vo.getState()[i]+"</br>&nbsp;");
//		}
//		}
//		sb.append("</td>");
//		sb.append("<td align='right' height='24' width='10%'>Lun列表:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;");
//		if(sys_vo.getLunsList() != null && sys_vo.getLunsList().size()>0){
//		  for(int i=0;i<sys_vo.getLunsList().size();i++)
//		  {
//			sb.append(sys_vo.getLunsList().get(i)+"</br>&nbsp;");
//		  }
//		}
//		
//		sb.append("</td>");
//		sb.append("</tr>");
//		
////		sb.append("<tr style='background-color: #ECECEC;' >");
////		sb.append("<td align='right' height='24' width='10%'>磁盘列表:&nbsp;</td>	");
////		sb.append("<td width='90%' colspan=4>&nbsp;");
////		if(sys_vo.getDisksList() != null && sys_vo.getDisksList().size()>0){
////		for(int i=0;i<sys_vo.getDisksList().size();i++)
////		{
////			sb.append(sys_vo.getDisksList().get(i)+"</br>&nbsp;");
////		}
////		}
////		sb.append("</td>");
////		sb.append("</tr></table></td></tr></table>");
//		
//		sb.append("<tr><td colspan=4><table id='detail-content-body' class='detail-content-body'>");
//		if(sys_vo.getDisksList() != null && sys_vo.getDisksList().size()>0){
//		for(int i=0;i<sys_vo.getDisksList().size();i++)
//		{
//			if(i==0){
//			sb.append("<tr style='background-color: #ECECEC;'><td align='right' height='24' width='10%'>&nbsp;&nbsp;磁盘列表:&nbsp;</td>	");
//			sb.append("<td width='90%' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//			sb.append(sys_vo.getDisksList().get(i));
//			}else if(i%2 == 1){
//				sb.append("<tr><td align='right' height='20' width='10%'>&nbsp;</td>	");
//				sb.append("<td width='90%' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//				sb.append(sys_vo.getDisksList().get(i));
//			}else{
//				sb.append("<tr style='background-color: #ECECEC;'><td align='right' height='20' width='10%'></td>	");
//				sb.append("<td width='90%' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//				sb.append(sys_vo.getDisksList().get(i));
//			}
//		}
//		}else{
//			sb.append("<tr><td align='right' height='24' width='10%'>磁盘列表:&nbsp;</td>	");
//			sb.append("<td width='90%' >&nbsp;");
//		}
//		sb.append("</td></tr>");
//		sb.append("</table></td>");
//		sb.append("</tr></table></td></tr></table>");
//		
//		
//		
//		}else{
//			sb.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1'>");
//			sb.append("<tr valign=top>");
//			sb.append("<td align='right' height='24' width='10%'>Raid租ID:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("<td align='right' height='24' width='10%'>类型:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("</tr>");
//			sb.append("<tr style='background-color: #ECECEC;'>");
//			sb.append("<td align='right' height='24' width='10%'>磁盘最大数量:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("<td align='right' height='24' width='10%'>LUN最大数量:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("</tr>");
//			sb.append("</tr>");
//			sb.append("<tr >");
//			sb.append("<td align='right' height='24' width='10%'>原始容量:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("<td align='right' height='24' width='10%'>逻辑容量:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("</tr>");
//			sb.append("</tr>");
//			sb.append("<tr style='background-color: #ECECEC;'>");
//			sb.append("<td align='right' height='24' width='10%'>空闲容量:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("<td align='right' height='24' width='10%'>重建优先级:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;</td>");
//			sb.append("</tr>");
//			
//			sb.append("<tr >");
//			sb.append("<td align='right' height='24' width='10%'>状态:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;");
//			
//			sb.append("</br>&nbsp;</td>");
//			sb.append("<td align='right' height='24' width='10%'>Lun列表:&nbsp;</td>	");
//			sb.append("<td width='30%'>&nbsp;");
//			sb.append("</br>&nbsp;</td>");
//			sb.append("</tr>");
//			
//			sb.append("<tr style='background-color: #ECECEC;'>");
//			sb.append("<td align='right' height='24' width='10%'>磁盘列表:&nbsp;</td>	");
//			sb.append("<td width='90%' colspan=3>&nbsp;");
//			
//			sb.append("</br>&nbsp;</td>");
//			sb.append("</tr></table></td></tr></table>");
//		}
//	}
//	
//	
//	
//	Map<String,String> map = new HashMap<String,String>();
//	map.put("option", sb.toString());
//	JSONObject json = JSONObject.fromObject(map);
//	out.print(json);
//	out.flush();
//	out.close();
//}
//
//
//
//public void emcDiskCon(){
//	String ip = request.getParameter("ip");
//	String flag = request.getParameter("flag");
//	String nodeid = request.getParameter("nodeid");
//	
//	StringBuffer sb = new StringBuffer();
//	HashMap<String,Object> map_host = new HashMap<String,Object>();
//	Hashtable ipAllData = new Hashtable();
//	ipAllData = (Hashtable) ShareData.getEmcdata().get(ip);
//	Disk disk = null;
//	HashMap disk_map = new HashMap();
////	ipAllData = null;
//	if(ipAllData == null){
//		diskConDao dao = new diskConDao();
//		List<Disk> list = (List<Disk>)dao.query(nodeid);
//		if(list != null && list.size()> 0){
//			for(int i=0;i<list.size();i++){
//				disk = list.get(i);
//				disk_map.put(disk.getDid(), disk);
//			}
//		}
//	}else{
//		List<Disk> list = (List<Disk>) ipAllData.get("disk");
//		if(list != null && list.size()> 0){
//			for(int i=0;i<list.size();i++){
//				disk = list.get(i);
//				disk_map.put(disk.getDid(), disk);
//			}
//		}
//	}
//	
//	Disk sys_vo = (Disk) disk_map.get(flag);
//	if(sys_vo != null){
//	sb.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//	sb.append("<tr valign=top>");
//	sb.append("<td align='right' height='24' width='15%'>ID:&nbsp;</td>	");
//	sb.append("<td width='30%'>&nbsp;"+sys_vo.getRid()+"</td>");
//	sb.append("<td align='right' height='24' width='15%'>序列号:&nbsp;</td>	");
//	sb.append("<td width='30%'>&nbsp;"+sys_vo.getSerialNumber()+"</td>");
//	sb.append("</tr>");
//	sb.append("<tr style='background-color: #ECECEC;'>");
//	sb.append("<td align='right' height='24' width='15%'>版本号:&nbsp;</td>	");
//	sb.append("<td width='30%'>&nbsp;"+sys_vo.getRevision()+"</td>");
//	sb.append("<td align='right' height='24' width='15%'>磁盘当前速度:&nbsp;</td>	");
//	sb.append("<td width='30%'>&nbsp;"+sys_vo.getCurrentSpeed()+"</td>");
//	sb.append("</tr>");
//	sb.append("</tr>");
//	sb.append("<tr >");
//	sb.append("<td align='right' height='24' width='15%'>Raid组编号:&nbsp;</td>	");
//	sb.append("<td width='30%'>&nbsp;"+sys_vo.getRaidGroupID()+"</td>");
//	sb.append("<td align='right' height='24' width='15%'>磁盘驱动类型:&nbsp;</td>	");
//	sb.append("<td width='30%'>&nbsp;"+sys_vo.getDriveType()+"</td>");
//	sb.append("</tr>");
//	sb.append("</tr>");
//	sb.append("<tr style='background-color: #ECECEC;'>");
//	sb.append("<td align='right' height='24' width='15%'>磁盘的LUN数:&nbsp;</td>	");
//	sb.append("<td width='30%'>&nbsp;"+sys_vo.getNumberofLuns()+"</td>");
//	sb.append("<td align='right' height='24' width='15%'>容量:&nbsp;</td>	");
//	sb.append("<td width='30%'>&nbsp;"+sys_vo.getCapacity()+"</td>");
//	sb.append("</tr>");
//	
//	sb.append("</tr>");
//	sb.append("<tr >");
//	sb.append("<td align='right' height='24' width='15%' >可运行的最大速度:&nbsp;</td>	");
//	sb.append("<td width='70%' colspan=3>&nbsp;"+sys_vo.getMaximumSpeed()+"</td>");
//	sb.append("</tr>");
//	
//
////	sb.append("</tr>");
////	sb.append("<tr style='background-color: #ECECEC;'>");
////	sb.append("<td align='right' height='24' width='15%' >类型:&nbsp;</td>	");
////	sb.append("<td width='65%' colspan=3>&nbsp;"+sys_vo.getType()+"</td>");
////	sb.append("</tr>");
////	
////	sb.append("</tr>");
////	sb.append("<tr >");
////	sb.append("<td align='right' height='24' width='15%' >磁盘所属Lun编号:&nbsp;</td>	");
////	sb.append("<td width='70%' colspan=3>&nbsp;"+sys_vo.getLun()+"</td>");
////	sb.append("</tr>");
////	
////	sb.append("</tr>");
////	sb.append("<tr style='background-color: #ECECEC;'>");
////	sb.append("<td align='right' height='24' width='15%' >磁盘重建百分比:&nbsp;</td>	");
////	sb.append("<td width='70%' colspan=3>&nbsp;"+sys_vo.getPrctRebuilt()+"</td>");
////	sb.append("</tr>");
////	
////	sb.append("</tr>");
////	sb.append("<tr >");
////	sb.append("<td align='right' height='24' width='15%' >磁盘绑定百分比:&nbsp;</td>	");
////	sb.append("<td width='70%' colspan=3>&nbsp;"+sys_vo.getPrctBound()+"</td>");
////	sb.append("</tr>");
//	 sb.append("</tr>");
//	 sb.append("<td colspan=4 > <table id='detail-content-body' class='detail-content-body'>");
//	 
//	    sb.append("<tr style='background-color: #ECECEC;'>");
//	    sb.append("<td align='center' height='24' width='25%' >&nbsp;磁盘所属Lun编号:&nbsp;</td>	");
//	    sb.append("<td width='25%' align='center'>&nbsp;磁盘重建百分比:&nbsp;</td>");
//	    sb.append("<td align='center' height='24' width='25%' >&nbsp;类型:&nbsp;</td>	");
//	    sb.append("<td width='25%' align='center'>&nbsp;磁盘绑定百分比:&nbsp;</td>");
//	    sb.append("</tr>");
//	    
//	 try{
//   if(!"".equals(sys_vo.getNumberofLuns()) || null != sys_vo.getNumberofLuns() || !"null".equals(sys_vo.getNumberofLuns())){
//	if(Integer.parseInt(sys_vo.getNumberofLuns())>=0){
//	     HashMap type_map = new HashMap();
//		 HashMap built_map = new HashMap();
//		 HashMap bound_map = new HashMap();
//		 int num=0;
//		 int num1=0;
//		 List key = new ArrayList();
//		 List value = new ArrayList();
//		 List built_value = new ArrayList();
//		 List bound_value = new ArrayList();
//		 
//		 
//		 if(Integer.parseInt(sys_vo.getNumberofLuns())>1){
//		  String[] type = sys_vo.getType().split(" ");
//		  String[] number = sys_vo.getLun().split(" ");
//		  String[] build = sys_vo.getPrctRebuilt().split(" ");
//		  String[] bound = sys_vo.getPrctBound().split(" ");
//		  for(int j=0;j<type.length;j++)
//		  {
//			  if(j%2==0){
//				  num=j;
//				  String[] flag1 = type[num].split(":");
//				  key.add(flag1[0]);
//			  }else if(j%2 == 1){
//				  num1=j;
//				  value.add(type[num1]);
//				  built_value.add(build[num1]);
//				  bound_value.add(bound[num1]);
//			  }
//		  }
//		  for(int k=0;k<key.size();k++){
//			type_map.put(key.get(k), value.get(k));
//			if(k%2 == 0){
//		    sb.append("</tr>");
//		    sb.append("<tr >");
//		    sb.append("<td align='center' height='24' width='25%' >&nbsp;"+key.get(k)+":&nbsp;</td>	");
//		    sb.append("<td width='25%' align='center'>&nbsp;"+built_value.get(k)+"&nbsp;</td>");
//		    sb.append("<td align='center' height='24' width='25%' >&nbsp;"+value.get(k)+"&nbsp;</td>	");
//		    sb.append("<td width='25%' align='center'>&nbsp;"+bound_value.get(k)+"&nbsp;</td>");
//		    sb.append("</tr>");
//			}else{
//				sb.append("</tr>");
//			    sb.append("<tr style='background-color: #ECECEC;'>");
//			    sb.append("<td align='center' height='24' width='25%' >&nbsp;"+key.get(k)+":&nbsp;</td>	");
//			    sb.append("<td width='25%' align='center'>&nbsp;"+built_value.get(k)+"&nbsp;</td>");
//			    sb.append("<td align='center' height='24' width='25%' >&nbsp;"+value.get(k)+"&nbsp;</td>	");
//			    sb.append("<td width='25%' align='center'>&nbsp;"+bound_value.get(k)+"&nbsp;</td>");
//			    sb.append("</tr>");
//			}
//		  }
//		 }
//	     else if(sys_vo.getNumberofLuns().equals("1")){
//			  String[] type = sys_vo.getType().split(":");
//			  String[] number = sys_vo.getLun().split(" ");
//			  String[] build = sys_vo.getPrctRebuilt().split(" ");
//			  String[] bound = sys_vo.getPrctBound().split(" ");
//			    sb.append("</tr>");
//			    sb.append("<tr >");
//			    sb.append("<td align='center' height='24' width='25%' >&nbsp;"+sys_vo.getLun()+":&nbsp;</td>	");
//			    sb.append("<td width='25%' align='center'>&nbsp;"+build[1]+"&nbsp;</td>");
//			    sb.append("<td align='center' height='24' width='25%' >&nbsp;"+type[1]+"&nbsp;</td>	");
//			    sb.append("<td width='25%' align='center'>&nbsp;"+build[1]+"&nbsp;</td>");
//			    sb.append("</tr>");
//		 }else{
//			    sb.append("</tr>");
//			    sb.append("<tr >");
//			    sb.append("<td align='center' height='24' width='25%' >&nbsp;"+sys_vo.getLun()+"&nbsp;</td>	");
//			    sb.append("<td width='25%' align='center'>&nbsp;"+sys_vo.getPrctRebuilt()+"</td>");
//			    sb.append("<td align='center' height='24' width='25%' >&nbsp;"+sys_vo.getType()+"&nbsp;</td>	");
//			    sb.append("<td width='25%' align='center'>&nbsp;"+sys_vo.getPrctRebuilt()+"</td>");
//			    sb.append("</tr>");
//	 }
//	}
//	}else{
//		sb.append("</tr>");
//	    sb.append("<tr >");
//	    sb.append("<td align='center' height='24' width='25%' >&nbsp;"+sys_vo.getLun()+"&nbsp;</td>	");
//	    sb.append("<td width='25%' align='center'>&nbsp;"+sys_vo.getPrctRebuilt()+"&nbsp;</td>");
//	    sb.append("<td align='center' height='24' width='25%' >&nbsp;"+sys_vo.getType()+"&nbsp;</td>	");
//	    sb.append("<td width='25%' align='center'>&nbsp;"+sys_vo.getPrctRebuilt()+"&nbsp;</td>");
//	    sb.append("</tr>");
//	}
//	 }catch(Exception e){
//		 sb.append("</tr>");
//		    sb.append("<tr >");
//		    sb.append("<td align='center' height='24' width='25%' >&nbsp;"+sys_vo.getLun()+"&nbsp;</td>	");
//		    sb.append("<td width='25%' align='center'>&nbsp;"+sys_vo.getPrctRebuilt()+"&nbsp;</td>");
//		    sb.append("<td align='center' height='24' width='25%' >&nbsp;"+sys_vo.getType()+"&nbsp;</td>	");
//		    sb.append("<td width='25%' align='center'>&nbsp;"+sys_vo.getPrctRebuilt()+"&nbsp;</td>");
//		    sb.append("</tr>");
//	 }
//	
//	
//	 sb.append("</table></td>");
//	 sb.append("</table></td></tr></table>");
//	
//	
//	
//	}else{
//		sb.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//		sb.append("<tr valign=top>");
//		sb.append("<td align='right' height='24' width='15%'>ID:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("<td align='right' height='24' width='15%'>序列号:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("</tr>");
//		sb.append("<tr style='background-color: #ECECEC;'>");
//		sb.append("<td align='right' height='24' width='15%'>版本号:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("<td align='right' height='24' width='15%'>磁盘当前速度:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("</tr>");
//		sb.append("</tr>");
//		sb.append("<tr >");
//		sb.append("<td align='right' height='24' width='15%'>Raid组编号:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("<td align='right' height='24' width='15%'>磁盘驱动类型:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("</tr>");
//		sb.append("</tr>");
//		sb.append("<tr style='background-color: #ECECEC;'>");
//		sb.append("<td align='right' height='24' width='15%'>绑定至此磁盘的LUN数:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("<td align='right' height='24' width='15%'>容量:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("</tr>");
//		
//		sb.append("</tr>");
//		sb.append("<tr >");
//		sb.append("<td align='right' height='24' width='15%' >可运行的最大速度:&nbsp;</td>	");
//		sb.append("<td width='70%' colspan=3>&nbsp;</td>");
//		sb.append("</tr>");
//		
//
//		sb.append("</tr>");
//		sb.append("<tr style='background-color: #ECECEC;'>");
//		sb.append("<td align='right' height='24' width='15%' >类型:&nbsp;</td>	");
//		sb.append("<td width='65%' colspan=3>&nbsp;</td>");
//		sb.append("</tr>");
//		
//		sb.append("</tr>");
//		sb.append("<tr >");
//		sb.append("<td align='right' height='24' width='15%' >磁盘所属Lun编号:&nbsp;</td>	");
//		sb.append("<td width='70%' colspan=3>&nbsp;</td>");
//		sb.append("</tr>");
//		
//		sb.append("</tr>");
//		sb.append("<tr style='background-color: #ECECEC;'>");
//		sb.append("<td align='right' height='24' width='15%' >磁盘重建百分比:&nbsp;</td>	");
//		sb.append("<td width='70%' colspan=3>&nbsp;</td>");
//		sb.append("</tr>");
//		
//		sb.append("</tr>");
//		sb.append("<tr >");
//		sb.append("<td align='right' height='24' width='15%' >磁盘绑定百分比:&nbsp;</td>	");
//		sb.append("<td width='70%' colspan=3>&nbsp;</td>");
//		
//		sb.append("</tr>");
//		sb.append("<tr >");
//		sb.append("<td align='right' height='24' width='15%' >&nbsp;&nbsp;</td>	");
//		sb.append("<td width='30%' >&nbsp;&nbsp;</td>");
//		sb.append("<td align='right' height='24' width='15%' >&nbsp;&nbsp;</td>	");
//		sb.append("<td width='30%' >&nbsp;&nbsp;</td>");
//		sb.append("</tr></table></td></tr></table>");
//	}
//	
//	
//	Map<String,String> map = new HashMap<String,String>();
//	map.put("option", sb.toString());
//	JSONObject json = JSONObject.fromObject(map);
//	out.print(json);
//	out.flush();
//	out.close();
//}
//
//
//public void emcLunCon(){
//	String ip = request.getParameter("ip");
//	String flag = request.getParameter("flag");
//	String nodeid = request.getParameter("nodeid");
//	
//	StringBuffer sb = new StringBuffer();
//	HashMap<String,Object> map_host = new HashMap<String,Object>();
//	Hashtable ipAllData = new Hashtable();
//	ipAllData = (Hashtable) ShareData.getEmcdata().get(ip);
////	ipAllData = null;
//	if(ipAllData == null){
//		lunConDao dao = new lunConDao();
//		List<Lun> list=dao.query(nodeid);
//		Lun lun = null;
//		HashMap raid_map = new HashMap();
//		if (list != null || list.size() > 0) {
//			for (int i = 0; i < list.size(); i++) {
//				lun = list.get(i);
//				raid_map.put(lun.getName(), lun);
//			}
//		}
//
//		Lun sys_vo = (Lun) raid_map.get(flag);
//		if (sys_vo != null) {
//			sb
//					.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//			sb.append("<tr valign=top>");
//			sb
//					.append("<td align='right' height='24' width='15%'>名称:&nbsp;</td>	");
//			sb
//					.append("<td width='25%'>&nbsp;" + sys_vo.getName()
//							+ "</td>");
//			sb
//					.append("<td align='right' height='24' width='15%'>Raid类型:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;" + sys_vo.getRAIDType()
//					+ "</td>");
//			sb.append("</tr>");
//			sb.append("<tr style='background-color: #ECECEC;'>");
//			sb
//					.append("<td align='right' height='24' width='15%'>所属的Raid组ID:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;" + sys_vo.getRAIDGroupID()
//					+ "</td>");
//			sb
//					.append("<td align='right' height='24' width='15%'>容量:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;" + sys_vo.getLUNCapacity()
//					+ "</td>");
//			sb.append("</tr>");
//			sb.append("</tr>");
//			sb.append("<tr >");
//			sb
//					.append("<td align='right' height='24' width='15%'>当前所有者:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;" + sys_vo.getCurrentOwner()
//					+ "</td>");
//			sb
//					.append("<td align='right' height='24' width='15%'>默认所有者:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;" + sys_vo.getDefaultOwner()
//					+ "</td>");
//			sb.append("</tr>");
//			sb.append("</tr>");
//			sb.append("<tr style='background-color: #ECECEC;'>");
//			sb
//					.append("<td align='right' height='24' width='15%'>写缓存:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;" + sys_vo.getWritecache()
//					+ "</td>");
//			sb
//					.append("<td align='right' height='24' width='15%'>读缓存:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;" + sys_vo.getReadcache()
//					+ "</td>");
//			sb.append("</tr>");
//
//			sb.append("<tr >");
//			sb
//					.append("<td align='right' height='24' width='15%'>磁盘重建百分比:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;" + sys_vo.getPrctRebuilt());
//			sb.append("</td>");
//			sb
//					.append("<td align='right' height='24' width='15%'>绑定磁盘百分比:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;" + sys_vo.getPrctBound());
//			sb.append("</td>");
//			sb.append("</tr>");
//
//			// sb.append("<tr style='background-color: #ECECEC;' >");
//			// sb.append("<td align='right' height='24'
//			// width='10%'>磁盘列表:&nbsp;</td> ");
//			// sb.append("<td width='90%' colspan=4>&nbsp;");
//			// if(sys_vo.getDisksList() != null &&
//			// sys_vo.getDisksList().size()>0){
//			// for(int i=0;i<sys_vo.getDisksList().size();i++)
//			// {
//			// sb.append(sys_vo.getDisksList().get(i)+"</br>&nbsp;");
//			// }
//			// }
//			// sb.append("</td>");
//			// sb.append("</tr></table></td></tr></table>");
//
//			sb
//					.append("<tr><td colspan=4><table id='detail-content-body' class='detail-content-body'>");
//			
//			if (sys_vo.getDisklistStr() != null && sys_vo.getDisklistStr().length()>0) {
//				String[] disklistStr = sys_vo.getDisklistStr().split(";");
//				for (int i = 0; i < disklistStr.length; i++) {
//					if (i == 0) {
//						sb
//								.append("<tr style='background-color: #ECECEC;'><td align='right' height='24' width='15%'>&nbsp;&nbsp;&nbsp;磁盘列表:&nbsp;</td>	");
//						sb
//								.append("<td width='85%' colspan=3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//						sb.append(disklistStr[i]);
//					} else if (i % 2 == 1) {
//						sb
//								.append("<tr><td align='right' height='20' width='15%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>	");
//						sb
//								.append("<td width='85%' colspan=3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//						sb.append(disklistStr[i]);
//					} else {
//						sb
//								.append("<tr style='background-color: #ECECEC;'><td align='right' height='20' width='15%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>	");
//						sb
//								.append("<td width='85%' colspan=3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//						sb.append(disklistStr[i]);
//					}
//				}
//			} else {
//				sb
//						.append("<tr><td align='right' height='24' width='15%'>&nbsp;&nbsp;磁盘列表:&nbsp;</td>	");
//				sb.append("<td width='85%' colspan=3>&nbsp;");
//			}
//			sb.append("</td></tr>");
//			sb.append("</table></td>");
//			sb.append("</tr></table></td></tr></table>");
//
//		} else {
//			sb
//					.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//			sb.append("<tr valign=top>");
//			sb
//					.append("<td align='right' height='24' width='15%'>名称:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;</td>");
//			sb
//					.append("<td align='right' height='24' width='15%'>Raid类型:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;</td>");
//			sb.append("</tr>");
//			sb.append("<tr style='background-color: #ECECEC;'>");
//			sb
//					.append("<td align='right' height='24' width='15%'>所属的Raid组ID:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;</td>");
//			sb
//					.append("<td align='right' height='24' width='15%'>容量:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;</td>");
//			sb.append("</tr>");
//			sb.append("</tr>");
//			sb.append("<tr >");
//			sb
//					.append("<td align='right' height='24' width='15%'>当前所有者:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;</td>");
//			sb
//					.append("<td align='right' height='24' width='15%'>默认所有者:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;</td>");
//			sb.append("</tr>");
//			sb.append("</tr>");
//			sb.append("<tr style='background-color: #ECECEC;'>");
//			sb
//					.append("<td align='right' height='24' width='15%'>写缓存:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;</td>");
//			sb
//					.append("<td align='right' height='24' width='15%'>读缓存:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;</td>");
//			sb.append("</tr>");
//
//			sb.append("<tr >");
//			sb
//					.append("<td align='right' height='24' width='15%'>磁盘重建百分比:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;");
//			sb.append("</td>");
//			sb
//					.append("<td align='right' height='24' width='15%'>绑定磁盘百分比:&nbsp;</td>	");
//			sb.append("<td width='25%'>&nbsp;");
//			sb.append("</td>");
//			sb.append("</tr>");
//
//			sb.append("<tr style='background-color: #ECECEC;' >");
//			sb
//					.append("<td align='right' height='24' width='15%'>磁盘列表:&nbsp;</td>	");
//			sb.append("<td width='85%' colspan=3>&nbsp;");
//			sb.append("</tr></table></td></tr></table>");
//		}
//	}else{
//	        Lun lun = null;
//			List<Lun> list = (List<Lun>) ipAllData.get("lunconfig");
//			HashMap raid_map = new HashMap();
//			if (list != null || list.size() > 0) {
//				for (int i = 0; i < list.size(); i++) {
//					lun = list.get(i);
//					raid_map.put(lun.getName(), lun);
//				}
//			}
//
//			Lun sys_vo = (Lun) raid_map.get(flag);
//			if (sys_vo != null) {
//				sb
//						.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//				sb.append("<tr valign=top>");
//				sb
//						.append("<td align='right' height='24' width='15%'>名称:&nbsp;</td>	");
//				sb
//						.append("<td width='25%'>&nbsp;" + sys_vo.getName()
//								+ "</td>");
//				sb
//						.append("<td align='right' height='24' width='15%'>Raid类型:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;" + sys_vo.getRAIDType()
//						+ "</td>");
//				sb.append("</tr>");
//				sb.append("<tr style='background-color: #ECECEC;'>");
//				sb
//						.append("<td align='right' height='24' width='15%'>所属的Raid组ID:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;" + sys_vo.getRAIDGroupID()
//						+ "</td>");
//				sb
//						.append("<td align='right' height='24' width='15%'>容量:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;" + sys_vo.getLUNCapacity()
//						+ "</td>");
//				sb.append("</tr>");
//				sb.append("</tr>");
//				sb.append("<tr >");
//				sb
//						.append("<td align='right' height='24' width='15%'>当前所有者:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;" + sys_vo.getCurrentOwner()
//						+ "</td>");
//				sb
//						.append("<td align='right' height='24' width='15%'>默认所有者:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;" + sys_vo.getDefaultOwner()
//						+ "</td>");
//				sb.append("</tr>");
//				sb.append("</tr>");
//				sb.append("<tr style='background-color: #ECECEC;'>");
//				sb
//						.append("<td align='right' height='24' width='15%'>写缓存:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;" + sys_vo.getWritecache()
//						+ "</td>");
//				sb
//						.append("<td align='right' height='24' width='15%'>读缓存:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;" + sys_vo.getReadcache()
//						+ "</td>");
//				sb.append("</tr>");
//
//				sb.append("<tr >");
//				sb
//						.append("<td align='right' height='24' width='15%'>磁盘重建百分比:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;" + sys_vo.getPrctRebuilt());
//				sb.append("</td>");
//				sb
//						.append("<td align='right' height='24' width='15%'>绑定磁盘百分比:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;" + sys_vo.getPrctBound());
//				sb.append("</td>");
//				sb.append("</tr>");
//
//				// sb.append("<tr style='background-color: #ECECEC;' >");
//				// sb.append("<td align='right' height='24'
//				// width='10%'>磁盘列表:&nbsp;</td> ");
//				// sb.append("<td width='90%' colspan=4>&nbsp;");
//				// if(sys_vo.getDisksList() != null &&
//				// sys_vo.getDisksList().size()>0){
//				// for(int i=0;i<sys_vo.getDisksList().size();i++)
//				// {
//				// sb.append(sys_vo.getDisksList().get(i)+"</br>&nbsp;");
//				// }
//				// }
//				// sb.append("</td>");
//				// sb.append("</tr></table></td></tr></table>");
//
//				sb
//						.append("<tr><td colspan=4><table id='detail-content-body' class='detail-content-body'>");
//				if (sys_vo.getDisksList() != null
//						&& sys_vo.getDisksList().size() > 0) {
//					for (int i = 0; i < sys_vo.getDisksList().size(); i++) {
//						if (i == 0) {
//							sb
//									.append("<tr style='background-color: #ECECEC;'><td align='right' height='24' width='15%'>&nbsp;&nbsp;&nbsp;磁盘列表:&nbsp;</td>	");
//							sb
//									.append("<td width='85%' colspan=3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//							sb.append(sys_vo.getDisksList().get(i));
//						} else if (i % 2 == 1) {
//							sb
//									.append("<tr><td align='right' height='20' width='15%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>	");
//							sb
//									.append("<td width='85%' colspan=3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//							sb.append(sys_vo.getDisksList().get(i));
//						} else {
//							sb
//									.append("<tr style='background-color: #ECECEC;'><td align='right' height='20' width='15%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>	");
//							sb
//									.append("<td width='85%' colspan=3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//							sb.append(sys_vo.getDisksList().get(i));
//						}
//					}
//				} else {
//					sb
//							.append("<tr><td align='right' height='24' width='15%'>&nbsp;&nbsp;磁盘列表:&nbsp;</td>	");
//					sb.append("<td width='85%' colspan=3>&nbsp;");
//				}
//				sb.append("</td></tr>");
//				sb.append("</table></td>");
//				sb.append("</tr></table></td></tr></table>");
//
//			} else {
//				sb
//						.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//				sb.append("<tr valign=top>");
//				sb
//						.append("<td align='right' height='24' width='15%'>名称:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;</td>");
//				sb
//						.append("<td align='right' height='24' width='15%'>Raid类型:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;</td>");
//				sb.append("</tr>");
//				sb.append("<tr style='background-color: #ECECEC;'>");
//				sb
//						.append("<td align='right' height='24' width='15%'>所属的Raid组ID:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;</td>");
//				sb
//						.append("<td align='right' height='24' width='15%'>容量:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;</td>");
//				sb.append("</tr>");
//				sb.append("</tr>");
//				sb.append("<tr >");
//				sb
//						.append("<td align='right' height='24' width='15%'>当前所有者:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;</td>");
//				sb
//						.append("<td align='right' height='24' width='15%'>默认所有者:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;</td>");
//				sb.append("</tr>");
//				sb.append("</tr>");
//				sb.append("<tr style='background-color: #ECECEC;'>");
//				sb
//						.append("<td align='right' height='24' width='15%'>写缓存:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;</td>");
//				sb
//						.append("<td align='right' height='24' width='15%'>读缓存:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;</td>");
//				sb.append("</tr>");
//
//				sb.append("<tr >");
//				sb
//						.append("<td align='right' height='24' width='15%'>磁盘重建百分比:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;");
//				sb.append("</td>");
//				sb
//						.append("<td align='right' height='24' width='15%'>绑定磁盘百分比:&nbsp;</td>	");
//				sb.append("<td width='25%'>&nbsp;");
//				sb.append("</td>");
//				sb.append("</tr>");
//
//				sb.append("<tr style='background-color: #ECECEC;' >");
//				sb
//						.append("<td align='right' height='24' width='15%'>磁盘列表:&nbsp;</td>	");
//				sb.append("<td width='85%' colspan=3>&nbsp;");
//				sb.append("</tr></table></td></tr></table>");
//			}
//	
//	}
//	Map<String,String> map = new HashMap<String,String>();
//	map.put("option", sb.toString());
//	JSONObject json = JSONObject.fromObject(map);
//	out.print(json);
//	out.flush();
//	out.close();
//}
//
//
//
//
//public void emcDiskPer(){
//	String ip = request.getParameter("ip");
//	String flag = request.getParameter("flag");
//	
//	StringBuffer sb = new StringBuffer();
//	HashMap<String,Object> map_host = new HashMap<String,Object>();
//	Hashtable ipAllData = new Hashtable();
//	ipAllData = (Hashtable) ShareData.getEmcdata().get(ip);
//	if(ipAllData == null)return;
//	Disk disk = null;
//	List<Disk> list = (List<Disk>) ipAllData.get("disk");
//	HashMap disk_map = new HashMap();
//	if(list != null || list.size()> 0){
//		for(int i=0;i<list.size();i++){
//			disk = list.get(i);
//			disk_map.put(disk.getDid(), disk);
//		}
//	}
//	
//	Disk sys_vo = (Disk) disk_map.get(flag);
//	if(sys_vo != null){
//	sb.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//	sb.append("<tr valign=top>");
//	sb.append("<td align='right' height='24' width='15%'>序列号:&nbsp;</td>	");
//	sb.append("<td width='30%' colspan=2>&nbsp;"+sys_vo.getSerialNumber()+"</td>");
//	sb.append("<td align='right' height='24' width='15%'>状态:&nbsp;</td>	");
//	sb.append("<td width='30%' colspan=2>&nbsp;"+sys_vo.getState()+"</td>");
//	sb.append("</tr>");
//	sb.append("<tr style='background-color: #ECECEC;'>");
//	sb.append("<td align='right' height='24' width='15%'>读请求数:&nbsp;</td>	");
//	sb.append("<td width='25%'>&nbsp;"+sys_vo.getNumberofReads()+"</td>");
//	
//	sb.append(" <td align=center width='5%'><img src=\"/afunms/resource/image/a_xn.gif\" onclick='diskread(\""+sys_vo.getSerialNumber()+"\")'  ></td>");
//	
//	sb.append("<td align='right' height='24' width='15%'>写请求数:&nbsp;</td>	");
//	sb.append("<td width='25%'>&nbsp;"+sys_vo.getNumberofWrites()+"</td>");
//	
//	sb.append(" <td align=center width='5%'><img src=\"/afunms/resource/image/a_xn.gif\" onclick='diskwrite(\""+sys_vo.getSerialNumber()+"\")'  ></td>");
//	
//	sb.append("</tr>");
//	sb.append("</tr>");
//	sb.append("</tr>");
//	sb.append("<tr >");
//	sb.append("<td align='right' height='24' width='15%'>软件读错误数:&nbsp;</td>	");
//	sb.append("<td width='25%'>&nbsp;"+sys_vo.getSoftReadErrors()+"</td>");
//	
//	sb.append(" <td align=center width='5%'><img src=\"/afunms/resource/image/a_xn.gif\" onclick='disksoftread(\""+sys_vo.getSerialNumber()+"\")'  ></td>");
//	
//	sb.append("<td align='right' height='24' width='15%'>软件写错误数:&nbsp;</td>	");
//	sb.append("<td width='25%'>&nbsp;"+sys_vo.getSoftWriteErrors()+"</td>");
//	
//	sb.append(" <td align=center width='5%'><img src=\"/afunms/resource/image/a_xn.gif\" onclick='disksoftwrite(\""+sys_vo.getSerialNumber()+"\")'  ></td>");
//	
//	sb.append("</tr>");
//	sb.append("<tr style='background-color: #ECECEC;'>");
//	sb.append("<td align='right' height='24' width='15%'>读KB:&nbsp;</td>	");
//	sb.append("<td width='25%'>&nbsp;"+sys_vo.getKbytesRead()+"</td>");
//	
//	sb.append(" <td align=center width='5%'><img src=\"/afunms/resource/image/a_xn.gif\" onclick='diskreadkb(\""+sys_vo.getSerialNumber()+"\")'  ></td>");
//	
//	sb.append("<td align='right' height='24' width='15%'>写KB:&nbsp;</td>	");
//	sb.append("<td width='25%'>&nbsp;"+sys_vo.getKbytesWritten()+"</td>");
//	
//	sb.append(" <td align=center width='5%'><img src=\"/afunms/resource/image/a_xn.gif\" onclick='diskwritekb(\""+sys_vo.getSerialNumber()+"\")'  ></td>");
//	
//	sb.append("</tr>");
//	sb.append("<tr >");
//	sb.append("<td align='right' height='24' width='15%'>闲状态时间量:&nbsp;</td>	");
//	sb.append("<td width='25%'>&nbsp;"+sys_vo.getIdleTicks()+"</td>");
//	
//	sb.append(" <td align=center width='5%'><img src=\"/afunms/resource/image/a_xn.gif\" onclick='diskfree(\""+sys_vo.getSerialNumber()+"\")'  ></td>");
//	
//	sb.append("<td align='right' height='24' width='15%'>忙状态时间量:&nbsp;</td>	");
//	sb.append("<td width='25%'>&nbsp;"+sys_vo.getBusyTicks()+"</td>");
//	
//	sb.append(" <td align=center width='5%'><img src=\"/afunms/resource/image/a_xn.gif\" onclick='diskbus(\""+sys_vo.getSerialNumber()+"\")'  ></td>");
//	
//	sb.append("</tr>");
//	sb.append("<tr style='background-color: #ECECEC;'>");
//	sb.append("<td align='right' height='24' width='15%'>硬件读错误数:&nbsp;</td>	");
//	sb.append("<td width='25%'>&nbsp;"+sys_vo.getHardReadErrors()+"</td>");
//	
//	sb.append(" <td align=center width='5%'><img src=\"/afunms/resource/image/a_xn.gif\" onclick='diskhardread(\""+sys_vo.getSerialNumber()+"\")'  ></td>");
//	
//	sb.append("<td align='right' height='24' width='15%'>硬件写错误数:&nbsp;</td>	");
//	sb.append("<td width='25%'>&nbsp;"+sys_vo.getHardWriteErrors()+"</td>");
//	
//	sb.append(" <td align=center width='5%'><img src=\"/afunms/resource/image/a_xn.gif\" onclick='diskhardwrite(\""+sys_vo.getSerialNumber()+"\")'  ></td>");
//	
//	sb.append("</tr>");
//	
//	 sb.append("</tr>");
//	 sb.append("<td colspan=6 > <table id='detail-content-body' class='detail-content-body'>");
//	 
//	    sb.append("<tr >");
//	    sb.append("<td align='center' height='24' width='50%' >&nbsp;磁盘所属Lun编号:&nbsp;</td>	");
//	    sb.append("<td align='center' height='24' width='50%' >&nbsp;热备盘状态:&nbsp;</td>	");
//	    sb.append("</tr>");
//	    
//	 try{
//   if(!"".equals(sys_vo.getNumberofLuns()) || null != sys_vo.getNumberofLuns() || !"null".equals(sys_vo.getNumberofLuns())){
//	if(Integer.parseInt(sys_vo.getNumberofLuns())>=0){
//	     HashMap type_map = new HashMap();
//		 HashMap built_map = new HashMap();
//		 HashMap bound_map = new HashMap();
//		 int num=0;
//		 int num1=0;
//		 List key = new ArrayList();
//		 List value = new ArrayList();
//		 List built_value = new ArrayList();
//		 List bound_value = new ArrayList();
//		 
//		 
//		 if(Integer.parseInt(sys_vo.getNumberofLuns())>1){
//		  String[] type = sys_vo.getHotSpare().split(" ");
//		  String[] number = sys_vo.getLun().split(" ");
//		  String[] build = sys_vo.getPrctRebuilt().split(" ");
//		  String[] bound = sys_vo.getPrctBound().split(" ");
//		  for(int j=0;j<type.length;j++)
//		  {
//			  if(j%2==0){
//				  num=j;
//				  String[] flag1 = type[num].split(":");
//				  key.add(flag1[0]);
//			  }else if(j%2 == 1){
//				  num1=j;
//				  value.add(type[num1]);
//				  built_value.add(build[num1]);
//				  bound_value.add(bound[num1]);
//			  }
//		  }
//		  for(int k=0;k<key.size();k++){
//			type_map.put(key.get(k), value.get(k));
//			if(k%2 == 0){
//		    sb.append("</tr>");
//		    sb.append("<tr style='background-color: #ECECEC;'>");
//		    sb.append("<td align='center' height='24' width='50%' >&nbsp;"+key.get(k)+"&nbsp;</td>	");
//		    sb.append("<td align='center' height='24' width='50%' >&nbsp;"+value.get(k)+"&nbsp;</td>	");
//		    sb.append("</tr>");
//			}else{
//				sb.append("</tr>");
//			    sb.append("<tr >");
//			    sb.append("<td align='center' height='24' width='50%' >&nbsp;"+key.get(k)+"&nbsp;</td>	");
//			    sb.append("<td align='center' height='24' width='50%' >&nbsp;"+value.get(k)+"&nbsp;</td>	");
//			    sb.append("</tr>");
//			}
//		  }
//		 }
//	     else if(sys_vo.getNumberofLuns().equals("1")){
//			  String[] type = sys_vo.getType().split(":");
//			  String[] number = sys_vo.getLun().split(" ");
//			  String[] build = sys_vo.getPrctRebuilt().split(" ");
//			  String[] bound = sys_vo.getPrctBound().split(" ");
//			    sb.append("</tr>");
//			    sb.append("<tr style='background-color: #ECECEC;'>");
//			    sb.append("<td align='center' height='24' width='50%' >&nbsp;"+sys_vo.getLun()+"&nbsp;</td>	");
//			    sb.append("<td align='center' height='24' width='50%' >&nbsp;"+type[1]+"&nbsp;</td>	");
//			    sb.append("</tr>");
//		 }else{
//			 sb.append("</tr>");
//			    sb.append("<tr style='background-color: #ECECEC;'>");
//			    sb.append("<td align='center' height='24' width='50%' >&nbsp;"+sys_vo.getLun()+"&nbsp;</td>	");
//			    sb.append("<td align='center' height='24' width='50%' >&nbsp;"+sys_vo.getHotSpare()+"&nbsp;</td>	");
//			    sb.append("</tr>");
//	 }
//	}
//	}else{
//		sb.append("</tr>");
//	    sb.append("<tr style='background-color: #ECECEC;'>");
//	    sb.append("<td align='center' height='24' width='50%' >&nbsp;"+sys_vo.getLun()+"&nbsp;</td>	");
//	    sb.append("<td align='center' height='24' width='50%' >&nbsp;"+sys_vo.getHotSpare()+"&nbsp;</td>	");
//	    sb.append("</tr>");
//	}
//	 }catch(Exception e){
//		 sb.append("</tr>");
//		    sb.append("<tr style='background-color: #ECECEC;'>");
//		    sb.append("<td align='center' height='24' width='50%' >&nbsp;"+sys_vo.getLun()+"&nbsp;</td>	");
//		    sb.append("<td align='center' height='24' width='50%' >&nbsp;"+sys_vo.getHotSpare()+"&nbsp;</td>	");
//		    sb.append("</tr>");
//	 }
//	
//	
//	 sb.append("</table></td>");
//	sb.append("</table></td></tr></table>");
//	
//	
//	
//	}else{
//		sb.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//		sb.append("<tr valign=top>");
//		sb.append("<td align='right' height='24' width='15%'>状态:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("</tr>");
//		sb.append("<tr style='background-color: #ECECEC;'>");
//		sb.append("<td align='right' height='24' width='15%'>读请求数:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("<td align='right' height='24' width='15%'>写请求数:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("</tr>");
//		sb.append("</tr>");
//		sb.append("</tr>");
//		sb.append("<tr >");
//		sb.append("<td align='right' height='24' width='15%'>软件读错误数:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("<td align='right' height='24' width='15%'>软件写错误数:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("</tr>");
//		sb.append("<tr style='background-color: #ECECEC;'>");
//		sb.append("<td align='right' height='24' width='15%'>读KB:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("<td align='right' height='24' width='15%'>写KB:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("</tr>");
//		sb.append("<tr >");
//		sb.append("<td align='right' height='24' width='15%'>闲状态时间量:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("<td align='right' height='24' width='15%'>忙状态时间量:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("</tr>");
//		sb.append("<tr style='background-color: #ECECEC;'>");
//		sb.append("<td align='right' height='24' width='15%'>硬件读错误数:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("<td align='right' height='24' width='15%'>硬件写错误数:&nbsp;</td>	");
//		sb.append("<td width='30%'>&nbsp;</td>");
//		sb.append("</tr>");
//		
//		 sb.append("</tr>");
//		 sb.append("<td colspan=4 > <table id='detail-content-body' class='detail-content-body'>");
//		 
//		    sb.append("<tr >");
//		    sb.append("<td align='center' height='24' width='50%' >&nbsp;磁盘所属Lun编号:&nbsp;</td>	");
//		    sb.append("<td align='center' height='24' width='50%' >&nbsp;热备盘状态:&nbsp;</td>	");
//		    sb.append("</tr>");
//		    sb.append("<tr style='background-color: #ECECEC;'>");
//		    sb.append("<td align='right' height='24' width='15%' >&nbsp;&nbsp;</td>	");
//		    sb.append("<td width='30%' >&nbsp;&nbsp;</td>");
//		    sb.append("<td align='right' height='24' width='15%' >&nbsp;&nbsp;</td>	");
//		    sb.append("<td width='30%' >&nbsp;&nbsp;</td>");
//		sb.append("</tr></table></td></tr></table>");
//	}
//	
//	
//	Map<String,String> map = new HashMap<String,String>();
//	map.put("option", sb.toString());
//	JSONObject json = JSONObject.fromObject(map);
//	out.print(json);
//	out.flush();
//	out.close();
//}
//
//
//
//
//
//public void emcLunPer(){
//	String ip = request.getParameter("ip");
//	String flag = request.getParameter("flag");
//	
//	StringBuffer sb = new StringBuffer();
//	HashMap<String,Object> map_host = new HashMap<String,Object>();
//	Hashtable ipAllData = new Hashtable();
//	ipAllData = (Hashtable) ShareData.getEmcdata().get(ip);
//	if(ipAllData == null)return;
//	Lun lun = null;
//	List<Lun> list = (List<Lun>) ipAllData.get("lunconfig");
//	HashMap raid_map = new HashMap();
//	if(list != null || list.size()> 0){
//		for(int i=0;i<list.size();i++){
//			lun = list.get(i);
//			raid_map.put(lun.getName(), lun);
//		}
//	}
//	
//	Lun sys_vo = (Lun) raid_map.get(flag);
//	if(sys_vo != null){
//	sb.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//	sb.append("<tr valign=top>");
//	sb.append("<td align='right' height='24' width='15%'>名称:&nbsp;</td>	");
//	sb.append("<td width='20%'>&nbsp;"+sys_vo.getName()+"</td>");
//	
//	sb.append(" <td align=right width='5%'></td>");
//	
//	sb.append("<td align='right' height='24' width='15%'>状态:&nbsp;</td>	");
//	sb.append("<td width='20%'>&nbsp;"+sys_vo.getState()+"</td>");
//	sb.append("</tr>");
//	sb.append("<tr style='background-color: #ECECEC;'>");
//	sb.append("<td align='right' height='24' width='15%'>硬件错误总数:&nbsp;</td>	");
//	sb.append("<td width='20%'>&nbsp;"+sys_vo.getTotalHardErrors()+"</td>");
//	
//	sb.append(" <td align=center width='5%'><img src=\"/afunms/resource/image/a_xn.gif\" onclick='harderrors(\""+sys_vo.getName()+"\")'  ></td>");
//	
//	sb.append("<td align='right' height='24' width='15%'>软件错误总数:&nbsp;</td>	");
//	sb.append("<td width='20%'>&nbsp;"+sys_vo.getTotalSoftErrors()+"</td>");
//	
//	sb.append(" <td align=center width='5%'><img src=\"/afunms/resource/image/a_xn.gif\" onclick='softerrors(\""+sys_vo.getName()+"\")'  ></td>");
//	
//	sb.append("</tr>");
//	sb.append("</tr>");
//	sb.append("<tr >");
//	sb.append("<td align='right' height='24' width='15%'>队列总长度:&nbsp;</td>	");
//	sb.append("<td width='20%' >&nbsp;"+sys_vo.getTotalQueueLength()+"</td>");
//	
//	sb.append(" <td align=center width='5%'><img src=\"/afunms/resource/image/a_xn.gif\" onclick='totalLength(\""+sys_vo.getName()+"\")'  ></td>");
//	
//	sb.append("</tr></table></td></tr></table>");
//	
//	
//	
//	}else{
//		sb.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//		sb.append("<tr valign=top>");
//		sb.append("<td align='right' height='24' width='15%'>名称:&nbsp;</td>	");
//		sb.append("<td width='25%'>&nbsp;</td>");
//		sb.append("<td align='right' height='24' width='15%'>状态:&nbsp;</td>	");
//		sb.append("<td width='25%'>&nbsp;</td>");
//		sb.append("</tr>");
//		sb.append("<tr style='background-color: #ECECEC;'>");
//		sb.append("<td align='right' height='24' width='15%'>硬件错误总数:&nbsp;</td>	");
//		sb.append("<td width='25%'>&nbsp;</td>");
//		sb.append("<td align='right' height='24' width='15%'>软件错误总数:&nbsp;</td>	");
//		sb.append("<td width='25%'>&nbsp;</td>");
//		sb.append("</tr>");
//		sb.append("</tr>");
//		sb.append("<tr >");
//		sb.append("<td align='right' height='24' width='15%'>队列总长度:&nbsp;</td>	");
//		sb.append("<td width='65%'>&nbsp;</td>");
//		sb.append("</tr></table></td></tr></table>");
//	}
//	
//	
//	Map<String,String> map = new HashMap<String,String>();
//	map.put("option", sb.toString());
//	JSONObject json = JSONObject.fromObject(map);
//	out.print(json);
//	out.flush();
//	out.close();
//}
//
//
//
//
//public void emcEnvironment(){
//	String ip = request.getParameter("id");
//	String flag = request.getParameter("flag");
//	
//	StringBuffer sb = new StringBuffer();
//	HashMap<String,Object> map_host = new HashMap<String,Object>();
//	Hashtable ipAllData = new Hashtable();
//	ipAllData = (Hashtable) ShareData.getEmcdata().get(ip);
//	if(ipAllData == null)return;
//	Environment list = (Environment) ipAllData.get("environment");
//	Array ay = null;
//	List<MemModel> list_mem= null;
//	List<MemModel> list_power= null;
//	if(list != null)
//	{
//	  ay = list.getArray();
//	  list_mem = list.getMemList();
//	  list_power = list.getBakPowerList();
//	}
//	
//    if(flag.equals("array")){
//       sb.append("<table cellspacing='0' cellpadding='0' width='width:100%' bgcolor='#FFFFFF' border=1  class='detail-data-body'>");
//       sb.append("<tr align='center' height=28 class='microsoftLook0'>");
//       sb.append("<th width='10%'  align='center'>&nbsp;序号</th>" +
//                "<th width='30%' align='center'>&nbsp;状态</th>"+
//                "<th width='30%' align='center'>&nbsp;当前功率(瓦特)</th>"+
//                "<th width='30%' align='center'>&nbsp;平均功率(瓦特)</th></tr>");
//       
//       if(ay != null){
//    	   
//  		 sb.append("<tr align='center' height=28 class='microsoftLook'>");
//  		 sb.append("<th width='10%'  align=center>&nbsp;"+1+"</th>" +
//        	     "<th width='30%' align=center>&nbsp;"+ay.getStatus()+"</th>" +
//                 "<th width='30%' align=center>&nbsp;"+ay.getPresentWatts()+"&nbsp;&nbsp;&nbsp;<img src=\"/afunms/resource/image/a_xn.gif\" onclick='arrayenvwt()'  ></th>"+
//                 "<th width='30%' align=center>&nbsp;"+ay.getAveragewatts()+"&nbsp;&nbsp;&nbsp;<img src=\"/afunms/resource/image/a_xn.gif\" onclick='arrayenvavgwt()'  ></th></tr>");
//  	     }
//       sb.append("</table>");
//    }
//    
//     if(flag.equalsIgnoreCase("memtmp")){ 
//          sb.append("<table cellspacing='0' cellpadding='0' width='width:680' bgcolor='#FFFFFF' border=1  class='detail-data-body'>");
//          sb.append("<tr align='center' height=28 class='microsoftLook0'>");
//          sb.append("<th width='5%'  align='center'>&nbsp;序号</th>" +
//        		   "<th width='30%' align='center'>&nbsp;名称</th>" +
//          	       "<th width='15%' align='center'>&nbsp;状态</th>" +
//          		   "<th width='25%' align='center'>&nbsp;当前温度(C)</th>"+
//                   "<th width='25%' align='center'>&nbsp;平均温度(C)</th></tr>");
//          
//          if(list_mem.size()>0 && list_mem != null){
//            MemModel vo = null;
//        	for(int i=0;i<list_mem.size();i++){
//        		vo=list_mem.get(i);
//        	    sb.append("<tr align='center' height=28 class='microsoftLook'>");
//     		    sb.append("<th width='5%'  align=center>&nbsp;"+(i+1)+"</th>" +
//     		    	 	  "<th width='30%' align=center>&nbsp;"+vo.getName()+"</th>" +
//     				      "<th width='15%' align=center>&nbsp;"+vo.getAirStatus()+"</th>" +
//           		          "<th width='25%' align=center>&nbsp;"+vo.getPresentDegree()+"&nbsp;&nbsp;&nbsp;<img src=\"/afunms/resource/image/a_xn.gif\" onclick='envtmp(\""+vo.getName()+"\")'  ></th>"+
//                          "<th width='25%' align=center>&nbsp;"+vo.getAverageDegree()+"&nbsp;&nbsp;&nbsp;<img src=\"/afunms/resource/image/a_xn.gif\" onclick='envavgtmp(\""+vo.getName()+"\")'  ></th></tr>");
//     	   }
//          }
//          sb.append("</table>");
//  }
//     if(flag.equalsIgnoreCase("memory")){ 
//         sb.append("<table cellspacing='0' cellpadding='0' width='width:680' bgcolor='#FFFFFF' border=1  class='detail-data-body'>");
//         sb.append("<tr align='center' height=28 class='microsoftLook0'>");
//         sb.append("<th width='5%'  align='center'>&nbsp;序号</th>" +
//       		  "<th width='30%' align='center'>&nbsp;名称</th>" +
//         	    	"<th width='15%' align='center'>&nbsp;状态</th>" +
//         		    "<th width='25%' align='center'>&nbsp;当前功率(瓦特)</th>"+
//                  "<th width='25%' align='center'>&nbsp;平均功率(瓦特)</th></tr>");
//         
//         if(list_mem.size()>0 && list_mem != null){
//           MemModel vo = null;
//       	for(int i=0;i<list_mem.size();i++){
//       		vo=list_mem.get(i);
//       	    sb.append("<tr align='center' height=28 class='microsoftLook'>");
//    		    sb.append("<th width='5%'  align=center>&nbsp;"+(i+1)+"</th>" +
//    		    		 "<th width='30%' align=center>&nbsp;"+vo.getName()+"</th>" +
//    				 "<th width='15%' align=center>&nbsp;"+vo.getPowerStatus()+"</th>" +
//          		 "<th width='25%' align=center>&nbsp;"+vo.getPresentWatts()+"&nbsp;&nbsp;&nbsp;<img src=\"/afunms/resource/image/a_xn.gif\" onclick='memenvwt(\""+vo.getName()+"\")'  ></th>"+
//                   "<th width='25%' align=center>&nbsp;"+vo.getAverageWatts()+"&nbsp;&nbsp;&nbsp;<img src=\"/afunms/resource/image/a_xn.gif\" onclick='memenvavgwt(\""+vo.getName()+"\")'  ></th></tr>");
//    	   }
//         }
//         sb.append("</table>");
// }
//     
//   else if(flag.equals("bakPower")){
//          sb.append("<table cellspacing='0' cellpadding='0' width='width:680' bgcolor='#FFFFFF' border=1  class='detail-data-body'>");
//          sb.append("<tr align='center' height=28 class='microsoftLook0'>");
//          sb.append("<th width='5%'  align=center>&nbsp;序号</th>" +
//        		  "<th width='30%' align=center>&nbsp;名称</th>" +
//          	    	"<th width='15%' align=center>&nbsp;状态</th>" +
//          		    "<th width='25%' align=center>&nbsp;当前输出功率(瓦特)</th>"+
//                   "<th width='25%' align=center>&nbsp;平均输出功率(瓦特)</th></tr>");
//          
//          if(list_power.size()>0 && list_power != null){
//     		 MemModel vo = null;
//     		 for(int i=0;i<list_power.size();i++){
//     		   vo = list_power.get(i);
//     		   sb.append("<tr align='center' height=28 class=''>");
//     		   sb.append("<th width='5%'  align=center>&nbsp;"+(i+1)+"</th>" +
//     				     "<th width='30%' align=center>&nbsp;"+vo.getName()+"</th>" +
//           	             "<th width='15%' align=center>&nbsp;"+vo.getPowerStatus()+"</th>" +
//           		         "<th width='25%' align=center>&nbsp;"+vo.getPresentWatts()+"&nbsp;&nbsp;&nbsp;<img src=\"/afunms/resource/image/a_xn.gif\" onclick='bakenvwt(\""+vo.getName()+"\")'  ></th>"+
//                         "<th width='25%' align=center>&nbsp;"+vo.getAverageWatts()+"&nbsp;&nbsp;&nbsp;<img src=\"/afunms/resource/image/a_xn.gif\" onclick='bakenvavgwt(\""+vo.getName()+"\")'  ></th></tr>");
//     	   }
//          }
//          sb.append("</table>");
//	   
//    }
//	Map<String,String> map = new HashMap<String,String>();
//	map.put("option", sb.toString());
//	JSONObject json = JSONObject.fromObject(map);
//	out.print(json);
//	out.flush();
//	out.close();
//}
//
//
//
//public void synchronizationEmc(){
//	
//	
//	SysLogger.info("**********************EMC数据同步***************************");
//	String nodeid = request.getParameter("nodeid");
//	Hashtable returnHash = new Hashtable();
//	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid));//Integer.parseInt(alarmIndicatorsNode.getNodeid())
//	StringBuffer sb = new StringBuffer();
//	String ipaddress = host.getIpAddress();
//	
//	Hashtable ipAllData = (Hashtable) ShareData.getEmcdata().get(host.getIpAddress());
//	if(ipAllData != null){
//	List<Disk> disklist = (List<Disk>) ipAllData.get("disk");
//	List<Lun> lunlist = (List<Lun>) ipAllData.get("lunconfig");
//	Environment env = (Environment) ipAllData.get("environment");
//	List<HardCrus> hard = (List<HardCrus>) ipAllData.get("hardwarestatus");
//	Agent system = (Agent) ipAllData.get("system");
//	List<RaidGroup> raid = (List<RaidGroup>) ipAllData.get("raid");
//	if(disklist != null && lunlist != null && env!= null && system !=null && raid != null){
//		//lun基础信息入库
//		 lunConDao lunsys = new lunConDao();
//		 lunsys = new lunConDao();
//		 lunsys.delete(nodeid);
//		 lunsys = new lunConDao();
//		 lunsys.saveList(lunlist, nodeid);
//		//disk基础信息入库
//		 diskConDao disksys = new diskConDao();
//		 disksys = new diskConDao();
//		 disksys.delete(nodeid);
//		 disksys = new diskConDao();
//		 disksys.saveList(disklist, nodeid);
//		//environment基础信息入库
//		 com.afunms.emc.model.Array array = env.getArray();
//	     List<MemModel> liststore = env.getMemList();
//	     List<MemModel> listbakpower = env.getBakPowerList();
//	    	
//	     envPerDao dao = new envPerDao();
//	    if(array != null)
//	     {
//	    	dao.saveArray(array, host.getIpAddress());
//	     }
//	   if(liststore != null && liststore.size()>0){ 	
//	     dao = new envPerDao();
//	     dao.saveStore(liststore, host.getIpAddress());
//	   }
//	   if(listbakpower != null && listbakpower.size()>0){
//	     dao = new envPerDao();
//	     dao.saveBakPower(listbakpower, host.getIpAddress());
//	   }
//	 //system基础信息入库
//	   sysDao sysdao = new sysDao();
//	   sysdao = new sysDao();
//	   sysdao.delete(nodeid);
//	   sysdao = new sysDao();
//	   sysdao.save(system, nodeid);
//	 //raid基础信息入库
//	   raidDao raidsys = new raidDao();
//	   raidsys = new raidDao();
//	   raidsys.delete(nodeid);
//   	   RaidGroup vo_save = null;
//   	   for(int i=0;i<raid.size();i++)
//     	{
//   		raidsys = new raidDao();
//   		vo_save = raid.get(i);
//   		raidsys.save(vo_save, nodeid);
//     	}
//	   //hard基础信息入库
//   	   hardDao hardsys = new hardDao();
//   	   hardsys = new hardDao();
//       hardsys.delete(nodeid);
//       hardsys = new hardDao();
//   	   hardsys.save(hard, nodeid);
//	   sb.append("EMC存储数据同步成功!");
//	}else{
//		sb.append("EMC存储数据同步失败！");
//	}
//	}else{
//		sb.append("EMC存储数据同步失败！");
//	}
//	Map<String,String> map = new HashMap<String,String>();
//	map.put("option", sb.toString());
//	JSONObject json = JSONObject.fromObject(map);
//	out.print(json);
//	out.flush();
//	out.close();
//	SysLogger.info("**********************完成EMC数据同步***************************");
//}
//
//
//public void emcHard(){
//	String ip = request.getParameter("ip");
//	String flag = request.getParameter("flag");
//	String nodeid = request.getParameter("nodeid");
//	StringBuffer sb = new StringBuffer();
//	HashMap<String,Object> map_host = new HashMap<String,Object>();
//	Hashtable ipAllData = new Hashtable();
//	ipAllData = (Hashtable) ShareData.getEmcdata().get(ip);
//	ipAllData = null;
//	if(ipAllData == null){
//		hardDao dao = new hardDao();
//		List<Crus> list = dao.queryList(nodeid);
//		HashMap raid_map = new HashMap();
//		Crus hard = null;
//		if(list != null || list.size()> 0){
//			for(int i=0;i<list.size();i++){
//				hard = list.get(i);
//				raid_map.put(hard.getName(), hard);
//			}
//		}
//		Crus sys_vo = (Crus) raid_map.get(flag);
//		if(sys_vo != null){
//		 if(sys_vo.getSpStateStr().equals("null") || sys_vo.getSpStateStr() == null){
//		   sb.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//		   sb.append("<tr >");
//		   sb.append("<td align='right' height='29' width='20%'>名称:&nbsp;</td>	");
//		   sb.append("<td width='80%' colspan=3>&nbsp;"+sys_vo.getName()+"</td>");
//		   sb.append("</tr>");
//		   sb.append("<tr style='background-color: #ECECEC;'>");
//		   sb.append("<td align='right' height='29' width='20%'>电源状态:&nbsp;</td>	");
//		   sb.append("<td width='80%' colspan=3><table>");
//		   if(sys_vo.getPowerState() != null){
//			   String[] power = sys_vo.getPowerState().replace("{","").replace("}","").split(",");
//	              if(power != null && power.length>0){
//	                for(int i =0;i<power.length;i++){ 
//	                	 sb.append("<tr ><td>"+power[i]+"</td></tr>");
//	                }
//	              }
//		   }
//		   sb.append("</table></td>");
//		   sb.append("</tr>");
//		   sb.append("<tr >");
//		   sb.append("<td align='right' height='29' width='20%'>备用电源(LCC)状态::&nbsp;</td>	");
//		   sb.append("<td width='80%' colspan=3><table>");
//		   if(sys_vo.getBusLCC() != null){
//			   String[] power = sys_vo.getBusLCC().replace("{","").replace("}","").split(",");
//	              if(power != null && power.length>0){
//	                for(int i =0;i<power.length;i++){ 
//	                	 sb.append("<tr ><td>"+power[i]+"</td></tr>");
//	                }
//	              }
//		   }
//		   sb.append("</table></td>");
//		   sb.append("</tr>");
//		   sb.append("</table></td>");
//		   sb.append("</tr></table></td></tr></table>");
//		 }else{
//			 sb.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//			   sb.append("<tr >");
//			   sb.append("<td align='right' height='29' width='20%'>名称:&nbsp;</td>	");
//			   sb.append("<td width='80%' colspan=3>&nbsp;"+sys_vo.getName()+"</td>");
//			   sb.append("</tr>");
//			   
//			   sb.append("<tr style='background-color: #ECECEC;'>");
//			   sb.append("<td align='right' height='29' width='20%'>存储器(SP)状态: :&nbsp;</td>	");
//			   sb.append("<td width='80%' colspan=3><table>");
//			   if(sys_vo.getSpStateStr() != null){
//				   String[] power = sys_vo.getSpStateStr().replace("{","").replace("}","").split(",");
//		              if(power != null && power.length>0){
//		                for(int i =0;i<power.length;i++){ 
//		                	 sb.append("<tr ><td>"+power[i]+"</td></tr>");
//		                }
//		              }
//			   }
//			   sb.append("</table></td>");
//			   sb.append("</tr>");
//			   
//			   sb.append("<tr style='background-color: #ECECEC;'>");
//			   sb.append("<td align='right' height='29' width='20%'>电源状态:&nbsp;</td>	");
//			   sb.append("<td width='80%' colspan=3><table>");
//			   if(sys_vo.getPowerState() != null){
//				   String[] power = sys_vo.getPowerState().replace("{","").replace("}","").split(",");
//		              if(power != null && power.length>0){
//		                for(int i =0;i<power.length;i++){ 
//		                	 sb.append("<tr ><td>"+power[i]+"</td></tr>");
//		                }
//		              }
//			   }
//			   sb.append("</table></td>");
//			   sb.append("</tr>");
//			   sb.append("<tr >");
//			   sb.append("<td align='right' height='29' width='20%'>备用电源(SPS)状态::&nbsp;</td>	");
//			   sb.append("<td width='80%' colspan=3><table>");
//			   if(sys_vo.getBussps() != null){
//				   String[] power = sys_vo.getBussps().replace("{","").replace("}","").split(",");
//		              if(power != null && power.length>0){
//		                for(int i =0;i<power.length;i++){ 
//		                	 sb.append("<tr ><td>"+power[i]+"</td></tr>");
//		                }
//		              }
//			   }
//			   sb.append("</table></td>");
//			   sb.append("</tr>");
//			   
//			   sb.append("<tr style='background-color: #ECECEC;'>");
//			   sb.append("<td align='right' height='29' width='20%'>缆线连接(Cabling)状态: :&nbsp;</td>	");
//			   sb.append("<td width='80%' colspan=3><table>");
//			   if(sys_vo.getBusCabling() != null){
//				   String[] power = sys_vo.getBusCabling().replace("{","").replace("}","").split(",");
//		              if(power != null && power.length>0){
//		                for(int i =0;i<power.length;i++){ 
//		                	 sb.append("<tr ><td>"+power[i]+"</td></tr>");
//		                }
//		              }
//			   }
//			   sb.append("</table></td>");
//			   sb.append("</tr>");
//			   
//			   sb.append("</table></td>");
//			   sb.append("</tr></table></td></tr></table>");
//		 }
//		/*
//		 * --------------------------------
//		 */
//		
//		}else{
//			   sb.append(" <table id='detail-content-body' class='detail-content-body'><tr><td><table cellspacing='1' class='detail-content-body'>");
//			   sb.append("<tr >");
//			   sb.append("<td align='right' height='29' width='20%'>名称:&nbsp;</td>	");
//			   sb.append("<td width='80%'>&nbsp;</td>");
//			   sb.append("</tr>");
//			   sb.append("<tr style='background-color: #ECECEC;'>");
//			   sb.append("<td align='right' height='29' width='20%'>电源状态:&nbsp;</td>	");
//			   sb.append("<td width='80%'><table>");
//			   sb.append("<tr ><td></td></tr>");
//			   sb.append("</table></td>");
//			   sb.append("</tr>");
//		       sb.append("</tr>");
//			   sb.append("</tr>");
//			   sb.append("<tr >");
//			   sb.append("<td align='right' height='29' width='20%'>备用电源(LCC)状态::&nbsp;</td>	");
//			   sb.append("<td width='80%'><table>");
//		       sb.append("<tr ><td></td></tr>");
//			   sb.append("</table></td>");
//			   sb.append("</tr>");
//			   sb.append("</table></td>");
//			   sb.append("</tr></table></td></tr></table>");
//		}
//	}
//	
//	
//	
//	Map<String,String> map = new HashMap<String,String>();
//	map.put("option", sb.toString());
//	JSONObject json = JSONObject.fromObject(map);
//	out.print(json);
//	out.flush();
//	out.close();
//}
//
//
//


}
