/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.manage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.Arith;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.task.CheckLinkTask;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Link;
import com.afunms.topology.model.LinkPerformanceDTO;
import com.afunms.topology.model.MonitorNodeDTO;


/**
 */
public class LinkPerformanceManager extends BaseManager implements ManagerInterface
{
	
	public String execute(String action) {
		// TODO Auto-generated method stub
		if("list".equals(action)){
			return list();
		}
		return null;
	}
	
	public String list(){
		
		List linkList = getList();
		String nameStyle = request.getParameter("nameStyle");
		if (nameStyle == null || !nameStyle.equals("ipAndPort")) {
			nameStyle = "nameAndPort";
		}
		session.setAttribute("nameStyle", nameStyle);
		List linkPerformanceList = new ArrayList();
		DecimalFormat df=new DecimalFormat("#.##");
		String runmodel = PollingEngine.getCollectwebflag(); 
		
		if("0".equals(runmodel)){
	       	//采集与访问是集成模式	
			for(int i = 0 ; i < linkList.size() ; i++){
				Link link = (Link)linkList.get(i);
				if(link.getLinktype()!=-1){
				LinkPerformanceDTO linkPerformanceDTO = getLinkPerformanceDTO(link);
				linkPerformanceList.add(linkPerformanceDTO);
				}
			}
		}else{
		//取端口流速
			I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
			Vector end_vector = new Vector();
			Vector start_vector = new Vector();
			String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed",
					"ifOperStatus", "ifOutBroadcastPkts", "ifInBroadcastPkts",
					"ifOutMulticastPkts", "ifInMulticastPkts",
					"OutBandwidthUtilHdx", "InBandwidthUtilHdx",
					"InBandwidthUtilHdxPerc", "OutBandwidthUtilHdxPerc" };
			Hashtable interfaceHash = CheckLinkTask.getLinknodeInterfaceData(linkList);//先采集所有，避免在for循环中多次采集
			for(int k = 0 ; k < linkList.size() ; k++){
				Link link = (Link)linkList.get(k);
				if(link.getLinktype()!=-1){
				int startId = link.getStartId();
				int endId = link.getEndId();
				String startIndex = link.getStartIndex();
				String endIndex = link.getEndIndex();
				String start_inutilhdx = "0";
				String start_oututilhdx = "0";
				String start_inutilhdxperc = "0";
				String start_oututilhdxperc = "0";
				String end_inutilhdx = "0";
				String end_oututilhdx = "0";
				String end_inutilhdxperc = "0";
				String end_oututilhdxperc = "0";
				String starOper = "";
				String endOper = "";
				String pingValue = "0";
				String allSpeedRate = "0";
				com.afunms.polling.base.Node startnode = (com.afunms.polling.base.Node) PollingEngine
					.getInstance().getNodeByID(startId);
				com.afunms.polling.base.Node endnode = (com.afunms.polling.base.Node) PollingEngine
						.getInstance().getNodeByID(endId);
				if(startnode == null || endnode == null){
					continue;
				}
				try {
//					start_vector = hostlastmanager.getInterface(startnode.getIpAddress(),netInterfaceItem,"index","",""); 
//					end_vector = hostlastmanager.getInterface(endnode.getIpAddress(),netInterfaceItem,"index","","");
					if(interfaceHash != null && interfaceHash.containsKey(startnode.getIpAddress())){
						start_vector = (Vector)interfaceHash.get(startnode.getIpAddress());
					}
					if(interfaceHash != null && interfaceHash.containsKey(endnode.getIpAddress())){
						end_vector = (Vector)interfaceHash.get(endnode.getIpAddress());
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				if (startnode != null) {
					try {
						for (int i = 0; i < start_vector.size(); i++) {
							String[] strs = (String[]) start_vector.get(i);
							String index = strs[0];
							if (index.equalsIgnoreCase(startIndex)) {
								starOper = strs[3].trim();
								start_oututilhdx = strs[8].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");
								start_inutilhdx = strs[9].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");
								start_oututilhdxperc = strs[10].replaceAll("%", "");
								start_inutilhdxperc = strs[11].replaceAll("%", "");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if (endnode != null) {
					try {
						for (int i = 0; i < end_vector.size(); i++) {
							String[] strs = (String[]) end_vector.get(i);
							String index = strs[0];
							if (index.equalsIgnoreCase(endIndex)) {
								endOper = strs[3].trim();;
								end_oututilhdx = strs[8].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");
								end_inutilhdx = strs[9].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");
								end_oututilhdxperc = strs[10].replaceAll("%", "");
								end_inutilhdxperc = strs[11].replaceAll("%", "");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				int downspeed = (Integer.parseInt(start_oututilhdx) + Integer
						.parseInt(end_inutilhdx.replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", ""))) / 2;
				int upspeed = (Integer.parseInt(start_inutilhdx) + Integer
						.parseInt(end_oututilhdx.replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", ""))) / 2;
	
				double upperc = 0;
				try {
					if (start_oututilhdxperc != null
							&& start_oututilhdxperc.trim().length() > 0
							&& end_inutilhdxperc != null
							&& end_inutilhdxperc.trim().length() > 0)
						upperc = Arith.div((Double
								.parseDouble(start_oututilhdxperc) + Double
								.parseDouble(end_inutilhdxperc)), 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				double downperc = 0;
				try {
					if (start_inutilhdxperc != null
							&& start_inutilhdxperc.trim().length() > 0
							&& end_oututilhdxperc != null
							&& end_oututilhdxperc.trim().length() > 0)
						downperc = Arith.div((Double
								.parseDouble(start_inutilhdxperc) + Double
								.parseDouble(end_oututilhdxperc)), 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				int linkflag = 100;
				if ("".equals(starOper.trim()) || "".equals(endOper.trim()) || "down".equalsIgnoreCase(starOper)||"down".equalsIgnoreCase(endOper)) {
					linkflag = 0;
				}
				
				pingValue = String.valueOf(linkflag);
				LinkPerformanceDTO linkPerformanceDTO = new LinkPerformanceDTO();
				String name = link.getLinkName();
				if(nameStyle.equals("nameAndPort")) {
					name = link.getLinkAliasName();
				}
				int id = link.getId();
				allSpeedRate = String.valueOf(df.format(downperc + upperc));
				//组装链路端口流速等信息 
				linkPerformanceDTO.setId(id);
				linkPerformanceDTO.setName(name);
				linkPerformanceDTO.setStartNode(startnode.getIpAddress());
				linkPerformanceDTO.setEndNode(endnode.getIpAddress());
				linkPerformanceDTO.setStratIndex(startIndex);
				linkPerformanceDTO.setEndIndex(endIndex);
				linkPerformanceDTO.setUplinkSpeed(upspeed+"");
				linkPerformanceDTO.setDownlinkSpeed(downspeed+"");
				linkPerformanceDTO.setPingValue(pingValue);
				linkPerformanceDTO.setAllSpeedRate(allSpeedRate);
				linkPerformanceList.add(linkPerformanceDTO);
			    }
			}
		}
		String field = getParaValue("field");
		
		String sorttype = getParaValue("sorttype");
		if(field != null){
			if(sorttype == null || sorttype.trim().length() == 0){
				sorttype = "asc";
			} else if ("asc".equals(sorttype)){
				sorttype = "desc";
			} else if ("desc".equals(sorttype)){
				sorttype = "asc";
			}
			
			linkPerformanceList = linkPerformanceListSort(linkPerformanceList, field, sorttype);
			
			request.setAttribute("field", field);
			request.setAttribute("sorttype", sorttype);
		} 
		
		
		request.setAttribute("list", linkPerformanceList);
		
		return "/topology/linkperformance/list.jsp";
	}
	
	public List getList(){
		List list = null;
		try {
			LinkDao linkDao = new LinkDao();
			list(linkDao);
			list = (List)request.getAttribute("list");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	
	public LinkPerformanceDTO getLinkPerformanceDTO(Link link){
		
		LinkPerformanceDTO linkPerformanceDTO = new LinkPerformanceDTO();
		String nameStyle = (String)request.getSession().getAttribute("nameStyle");
		
		try {
			String name = link.getLinkName();
			String nm[] = name.split("/");
			String nm0 = nm[0].split("_")[0];
			String nm1 = nm[1].split("_")[0];
			com.afunms.polling.base.Node startNode1=PollingEngine.getInstance().getNodeByIP(nm0);
			com.afunms.polling.base.Node endNode1=PollingEngine.getInstance().getNodeByIP(nm1);;
			
			if(startNode1==null||endNode1==null)return null;
			if ("nameAndPort".equals(nameStyle)) {
				name = link.getLinkAliasName();
				//name = PollingEngine.getInstance().getNodeByIP(nm0).getAlias()+"_"+nm[0].split("_")[1]+"/"+PollingEngine.getInstance().getNodeByIP(nm1).getAlias()+"_"+nm[1].split("_")[1];
			}
			
//			System.out.println(name);
			int id = link.getId();
			LinkRoad linkRoad = null;
			linkRoad = PollingEngine.getInstance().getLinkByID(id);
			String stratIndex = linkRoad.getStartDescr();
			String endIndex = linkRoad.getEndDescr();
			String startNode = linkRoad.getStartIp();
			String endNode = linkRoad.getEndIp();
			
			String uplinkSpeed = linkRoad.getUplinkSpeed();
			String downlinkSpeed = linkRoad.getDownlinkSpeed();
			String pingValue = linkRoad.getPing();
			String allSpeedRate = linkRoad.getAllSpeedRate();
			DecimalFormat df=new DecimalFormat("#.##");
			if(allSpeedRate==null)allSpeedRate="0";
			Double allspeed = Double.parseDouble(allSpeedRate);
			allSpeedRate = String.valueOf(df.format(allspeed));
//			String maxSpeed = linkRoad.getMaxSpeed();
			
//			String uplinkSpeedColor = "green";
//			
//			if(uplinkSpeed != null && uplinkSpeed.trim().length() > 0
//					&& maxSpeed != null && !"".equals(maxSpeed)  
//					&& !"null".equals(maxSpeed)){
//				if(Double.valueOf(uplinkSpeed) > Double.valueOf(maxSpeed) ){
//					uplinkSpeedColor = "red";
//				}
//			}
//			
//			String downlinkSpeedColor = "green";
//			
//			if(downlinkSpeedColor != null && downlinkSpeedColor.trim().length() > 0
//					&& maxSpeed != null && !"".equals(maxSpeed)  
//					&& !"null".equals(maxSpeed)){
//				if(Double.valueOf(downlinkSpeedColor) > Double.valueOf(maxSpeed) ){
//					downlinkSpeedColor = "red";
//				}
//			}
			
			linkPerformanceDTO.setId(id);
			linkPerformanceDTO.setName(name);
			linkPerformanceDTO.setStartNode(startNode);
			linkPerformanceDTO.setEndNode(endNode);
			linkPerformanceDTO.setStratIndex(stratIndex);
			linkPerformanceDTO.setEndIndex(endIndex);
			linkPerformanceDTO.setUplinkSpeed((uplinkSpeed==null||"null".equalsIgnoreCase(uplinkSpeed+""))?"0":uplinkSpeed+"");
			linkPerformanceDTO.setDownlinkSpeed((downlinkSpeed==null||"null".equalsIgnoreCase(downlinkSpeed+""))?"0":downlinkSpeed+"");
			linkPerformanceDTO.setPingValue((pingValue==null||"null".equalsIgnoreCase(pingValue))?"0":pingValue);
			linkPerformanceDTO.setAllSpeedRate((allSpeedRate==null||"null".equalsIgnoreCase(allSpeedRate))?"0":allSpeedRate);
//			linkPerformanceDTO.setUplinkSpeedColor(uplinkSpeedColor);
//			linkPerformanceDTO.setDownlinkSpeedColor(downlinkSpeedColor);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return linkPerformanceDTO;
	}
	
	
	public List linkPerformanceListSort(List linkPerformanceList , String field, String sorttype){
		
		if(linkPerformanceList == null || linkPerformanceList.size() < 1){
			return linkPerformanceList;
		}
		
		
		for(int i = 0 ; i < linkPerformanceList.size() -1 ; i ++){
			for(int j = i + 1 ; j < linkPerformanceList.size() ; j ++){
				LinkPerformanceDTO linkPerformanceDTO1 = (LinkPerformanceDTO)linkPerformanceList.get(i);
				LinkPerformanceDTO linkPerformanceDTO2 = (LinkPerformanceDTO)linkPerformanceList.get(j);
				
				String fieldValue = "";
				
				String fieldValue2 = "";
				if("name".equals(field)){
					fieldValue = linkPerformanceDTO1.getName();
					if (fieldValue == null) {
						fieldValue = "";
					}
					fieldValue2 = linkPerformanceDTO2.getName();
					if (fieldValue2 == null) {
						fieldValue2 = "";
					}
					
					if("desc".equals(sorttype)){
						// 如果是降序 则 前一个 小于 后一个 则交换
						if(fieldValue.compareTo(fieldValue2) < 0){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}else if ("asc".equals(sorttype)){
						// 如果是升序 则 前一个 大于 后一个 则交换
						if(fieldValue.compareTo(fieldValue2) > 0){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}
						
				}else if ("startIp".equals(field)){
					fieldValue = linkPerformanceDTO1.getStartNode();
					if (fieldValue == null) {
						fieldValue = "0.0.0.0";
					}
					fieldValue2 = linkPerformanceDTO2.getStartNode();
					if (fieldValue2 == null) {
						fieldValue2 = "0.0.0.0";
					}
					if("desc".equals(sorttype)){
						// 如果是降序 则 前一个 小于 后一个 则交换
						if(ip2long(fieldValue) < ip2long(fieldValue2)){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}else if ("asc".equals(sorttype)){
						// 如果是升序 则 前一个 大于 后一个 则交换
						if(ip2long(fieldValue) > ip2long(fieldValue2)){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}
				}else if ("endIp".equals(field)){
					fieldValue = linkPerformanceDTO1.getEndNode();
					if (fieldValue == null) {
						fieldValue = "0.0.0.0";
					}
					fieldValue2 = linkPerformanceDTO2.getEndNode();
					if (fieldValue2 == null) {
						fieldValue2 = "0.0.0.0";
					}
					if("desc".equals(sorttype)){
						// 如果是降序 则 前一个 小于 后一个 则交换
						if(ip2long(fieldValue) < ip2long(fieldValue2)){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}else if ("asc".equals(sorttype)){
						// 如果是升序 则 前一个 大于 后一个 则交换
						if(ip2long(fieldValue) > ip2long(fieldValue2)){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}
				}else if ("uplinkSpeed".equals(field)){
					fieldValue = linkPerformanceDTO1.getUplinkSpeed();
					if (fieldValue == null) {
						fieldValue = "0.0";
					}
					fieldValue2 = linkPerformanceDTO2.getUplinkSpeed();
					if (fieldValue2 == null) {
						fieldValue2 = "0.0";
					}
					if("desc".equals(sorttype)){
						// 如果是降序 则 前一个 小于 后一个 则交换
						if(Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}else if ("asc".equals(sorttype)){
						// 如果是升序 则 前一个 大于 后一个 则交换
						if(Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}
				}else if ("downlinkSpeed".equals(field)){
					fieldValue = linkPerformanceDTO1.getDownlinkSpeed();
					if (fieldValue == null) {
						fieldValue = "0.0";
					}
					fieldValue2 = linkPerformanceDTO2.getDownlinkSpeed();
					if (fieldValue2 == null) {
						fieldValue2 = "0.0";
					}
					if("desc".equals(sorttype)){
						// 如果是降序 则 前一个 小于 后一个 则交换
						if(Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}else if ("asc".equals(sorttype)){
						// 如果是升序 则 前一个 大于 后一个 则交换
						if(Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}
				}else if ("ping".equals(field)){
					fieldValue = linkPerformanceDTO1.getPingValue();
					if (fieldValue == null) {
						fieldValue = "0.0";
					}
					fieldValue2 = linkPerformanceDTO2.getPingValue();
					
					if (fieldValue2 == null) {
						fieldValue2 = "0.0";
					}
					if("desc".equals(sorttype)){
						// 如果是降序 则 前一个 小于 后一个 则交换
						if(Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}else if ("asc".equals(sorttype)){
						// 如果是升序 则 前一个 大于 后一个 则交换
						if(Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}
				}else if ("allSpeedRate".equals(field)){
					fieldValue = linkPerformanceDTO1.getAllSpeedRate();
					if (fieldValue == null) {
						fieldValue = "0.0";
					}
					fieldValue2 = linkPerformanceDTO2.getAllSpeedRate();
					if (fieldValue2 == null) {
						fieldValue2 = "0.0";
					}
					if("desc".equals(sorttype)){
						// 如果是降序 则 前一个 小于 后一个 则交换
						if(Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}else if ("asc".equals(sorttype)){
						// 如果是升序 则 前一个 大于 后一个 则交换
						if(Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)){
							linkPerformanceList.set(i, linkPerformanceDTO2);
							linkPerformanceList.set(j, linkPerformanceDTO1);
						}
					}
				}
				
			}
		}
		return linkPerformanceList;
	}
	
	
	private long ip2long(String ip) {
		long result = 0;
		try {
			StringTokenizer st = new StringTokenizer(ip, ".");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				int part = Integer.parseInt(token);
				result = result * 256 + part;
			}
		} catch (Exception e) {
			result = 0;
		}
		return result;
	}
    
    
}
