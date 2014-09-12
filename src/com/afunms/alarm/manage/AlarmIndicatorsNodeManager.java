/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.alarm.manage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.dao.AlarmIndicatorsDao;
import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.dao.AlarmWayDao;
import com.afunms.alarm.dao.IndicatorsTopoRelationDao;
import com.afunms.alarm.model.AlarmIndicators;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.model.IndicatorsTopoRelation;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.Constant;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.VMWareVidDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.ManageXml;


public class AlarmIndicatorsNodeManager extends BaseManager implements ManagerInterface{

	public String execute(String action) {
		// TODO Auto-generated method stub
		
		if("list".equals(action)){
			return list();
		}else if("add".equals(action)){
			return add();
		}else if("edit".equals(action)){
			return edit();
		}else if("save".equals(action)){
			return save();
		}else if("update".equals(action)){
			return update();
		}else if("delete".equals(action)){
			return delete();
		}else if("changeManage".equals(action)){
			return changeManage();
		}else if("showtoaddlist".equals(action)){
			return showtoaddlist();
		}else if("showkeylist".equals(action)){
			return showkeylist();
		}else if("showtosetshowkeylist".equals(action)){
			return showtosetshowkeylist();
		}else if("addselectedshowkey".equals(action)){
			return addselectedshowkey();
		}else if("addselected".equals(action)){
			return addselected();
		}else if("showtomultiaddlist".equals(action)){
			return showtomultiaddlist();
		}else if("multiadd".equals(action)){//阀值配置方法
			return multiadd();
		}else if("showlist".equals(action)){
			return showlist();
		}else if("showAdd".equals(action)){
			return showAdd();
		}else if("showChooseNodeList".equals(action)){
			return showChooseNodeList();
		}else if("showsave".equals(action)){
			return showsave();
		}else if("showEdit".equals(action)){
			return showEdit();
		}else if("showUpdate".equals(action)){
			return showUpdate();
		}else if("showDelete".equals(action)){
			return showDelete();
		}else if("replenish".equals(action)){//补充配置阀值
			return multireplenishadd();
		}else if("changeadd".equals(action)){//应用当前阀值
			return multichangeadd();
		}
		else if("listDelete".equals(action)){
			return listDelete();
		}
		return null;
	}
	
	public String showUpdate(){
		AlarmIndicatorsNode alarmIndicatorsNode = createAlarmIndicatorsNode();
		int id = getParaIntValue("id");
		alarmIndicatorsNode.setId(id);
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNodeDao.update(alarmIndicatorsNode); 
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmIndicatorsNodeDao.close();
		}
		return showlist();
	}
	
	public String showEdit(){
		String jsp = "/topology/threshold/showedit.jsp";
		
		String nodeid = getParaValue("nodeid");
		String ipaddress=getParaValue("ipaddress");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		String id = getParaValue("id");
		AlarmIndicatorsNode alarmIndicatorsNode= null;
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNode = (AlarmIndicatorsNode)alarmIndicatorsNodeDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Hashtable alarmWayHashtable = new Hashtable();
		List away0list = new ArrayList(); 
		List away1list = new ArrayList(); 
		List away2list = new ArrayList(); 
		
		if(alarmIndicatorsNode!=null){
			nodeid = alarmIndicatorsNode.getNodeid();
			type = alarmIndicatorsNode.getType();
			subtype = alarmIndicatorsNode.getSubtype();
			AlarmWayDao alarmWayDao = null;
			if(alarmIndicatorsNode.getWay0()!=null&&!"".equals(alarmIndicatorsNode.getWay0())&&!"null".equalsIgnoreCase(alarmIndicatorsNode.getWay0())){
				String away0[] = alarmIndicatorsNode.getWay0().split(",");
				if(away0.length>0){
					for(int i=0;i<away0.length;i++){
						try {
							alarmWayDao = new AlarmWayDao();
							AlarmWay alarmWay0 = (AlarmWay)alarmWayDao.findByID(away0[i]);
							if(alarmWay0!=null){
								away0list.add(alarmWay0);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							alarmWayDao.close();
						}
					}
					alarmWayHashtable.put("way0", away0list);
				}
			}
			
			if(alarmIndicatorsNode.getWay1()!=null&&!"".equals(alarmIndicatorsNode.getWay1())&&!"null".equalsIgnoreCase(alarmIndicatorsNode.getWay1())){
				String away1[] = alarmIndicatorsNode.getWay1().split(",");
				if(away1.length>0){
					for(int i=0;i<away1.length;i++){
						try {
							alarmWayDao = new AlarmWayDao();
							AlarmWay alarmWay1 = (AlarmWay)alarmWayDao.findByID(away1[i]);
							if(alarmWay1!=null){
								away1list.add(alarmWay1);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							alarmWayDao.close();
						}
					}
					alarmWayHashtable.put("way1", away1list);
				}
			}
			
			if(alarmIndicatorsNode.getWay2()!=null&&!"".equals(alarmIndicatorsNode.getWay2())&&!"null".equalsIgnoreCase(alarmIndicatorsNode.getWay2())){
				String away2[] = alarmIndicatorsNode.getWay2().split(",");
				if(away2.length>0){
					for(int i=0;i<away2.length;i++){
						try {
							alarmWayDao = new AlarmWayDao();
							AlarmWay alarmWay2 = (AlarmWay)alarmWayDao.findByID(away2[i]);
							if(alarmWay2!=null){
								away2list.add(alarmWay2);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							alarmWayDao.close();
						}
					}
					alarmWayHashtable.put("way2", away2list);
				}
			}
		}
		request.setAttribute("alarmWayHashtable", alarmWayHashtable);
		request.setAttribute("alarmIndicatorsNode", alarmIndicatorsNode);
		request.setAttribute("nodeid", nodeid);
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);
		request.setAttribute("ipaddress", ipaddress);
		return jsp;
	}
	
	public String showDelete(){
		String[] ids = getParaArrayValue("checkbox");
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNodeDao.delete(ids); 
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmIndicatorsNodeDao.close();
		}
		
		return showlist();
		
	}
	public String listDelete(){

		String[] ids = getParaArrayValue("checkbox");
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNodeDao.delete(ids); 
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmIndicatorsNodeDao.close();
		}
		
		return list();
		
	}
	
	public String showsave(){
		
		
		String[] nodeids = getParaArrayValue("checkbox");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		String nodeid = getParaValue("nodeid");
		
		//需要批量应用的阀值指标
		String ids = getParaValue("ids");
		
		
		List idlist = new ArrayList();
		List addindilist = new ArrayList();
		if(ids != null && ids.trim().length()>0){
			String[] idsplit = ids.split(",");
			if(idsplit != null && idsplit.length>0){
				for(int i=0;i<idsplit.length;i++){
					if(idsplit[i] != null && idsplit[i].trim().length()>0){
						idlist.add(idsplit[i]);
					}
				}
			}
		}
		
		//System.out.println(ids+"=================");
		
		AlarmIndicatorsDao alarmIndicatorsDao = null;
		
		if(idlist != null && idlist.size()>0){
			alarmIndicatorsDao = new AlarmIndicatorsDao();
			try{
				for(int i=0;i<idlist.size();i++){
					String indicatorid = (String)idlist.get(i); 
					AlarmIndicators innode = (AlarmIndicators)alarmIndicatorsDao.findByID(indicatorid);
					addindilist.add(innode);
				}
			}catch(Exception e){
				
			}finally{
				alarmIndicatorsDao.close();
			}
			
		}
		
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		
		if(nodeids != null && nodeids.length > 0){
			AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = null;
			List updatelist = new ArrayList();
			List savelist = new ArrayList();
			Hashtable nodeindihash = new Hashtable();
			try{
				List list2 = new ArrayList();
				for(int i=0;i<nodeids.length;i++){
					//System.out.println(nodeids[i]+"=================");
					alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
					try{
						list2 = alarmIndicatorsNodeDao.getByNodeIdAndTypeAndSubType(nodeids[i], type, subtype);
					}catch(Exception e){
						
					}finally{
						alarmIndicatorsNodeDao.close();
					}
					
					if(list2 != null && list2.size()>0){
						//System.out.println(list2.size() + "=========list2.size()==============");
						for(int j=0;j<list2.size();j++){
							AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list2.get(j);
							nodeindihash.put(alarmIndicatorsNode.getName()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype(), alarmIndicatorsNode);
						}
						if(addindilist != null && addindilist.size()>0){
							try{
								for(int k=0;k<addindilist.size();k++){
									AlarmIndicators alarmIndicators = (AlarmIndicators)addindilist.get(k); 
									if(nodeindihash.containsKey(alarmIndicators.getName()+":"+alarmIndicators.getType()+":"+alarmIndicators.getSubtype())){
										//若存在,则修改
										AlarmIndicatorsNode alarmIndicatorsNode_update = (AlarmIndicatorsNode)nodeindihash.get(alarmIndicators.getName()+":"+alarmIndicators.getType()+":"+alarmIndicators.getSubtype());
										AlarmIndicatorsNode alarmIndicatorsNode_copy = alarmIndicatorsUtil.createAlarmIndicatorsNodeByAlarmIndicators(alarmIndicators);
										alarmIndicatorsNode_copy.setId(alarmIndicatorsNode_update.getId());
										alarmIndicatorsNode_copy.setNodeid(alarmIndicatorsNode_update.getNodeid());
//										System.out.println(nodeGatherIndicators_copy.getNodeid()+"====update==========nodeGatherIndicators_copy.getNodeid()==================" );
										updatelist.add(alarmIndicatorsNode_copy);
									}else{
										//不存在,则需要添加进去
										AlarmIndicatorsNode alarmIndicatorsNode_copy = alarmIndicatorsUtil.createAlarmIndicatorsNodeByAlarmIndicators(alarmIndicators);
										
										alarmIndicatorsNode_copy.setNodeid(nodeids[i]);
										
//										System.out.println(nodeGatherIndicators_copy.getNodeid()+"====add==========nodeGatherIndicators_copy.getNodeid()==================" );
										savelist.add(alarmIndicatorsNode_copy);
									}
								}
							}catch(Exception e){
								
							}
						}
					}else{
						//没设置任何采集指标,则添加全部
						//SysLogger.info("没设置任何采集指标,则添加全部###");
						if(addindilist != null && addindilist.size()>0){
							try{
								for(int k=0;k<addindilist.size();k++){
									//不存在,则需要添加进去
									AlarmIndicators alarmIndicators = (AlarmIndicators)addindilist.get(k); 
									AlarmIndicatorsNode alarmIndicatorsNode_copy = alarmIndicatorsUtil.createAlarmIndicatorsNodeByAlarmIndicators(alarmIndicators);
									
									alarmIndicatorsNode_copy.setNodeid(nodeids[i]);
									
//									System.out.println(nodeGatherIndicators_copy.getNodeid()+"====add==========nodeGatherIndicators_copy.getNodeid()==================" );
									savelist.add(alarmIndicatorsNode_copy);
								}
							}catch(Exception e){
								
							}
						}
					}
				}
				if(updatelist != null && updatelist.size()>0){
					try{
						alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
						alarmIndicatorsNodeDao.update(updatelist);
						
					}catch(Exception e){
						
					}finally{
						alarmIndicatorsNodeDao.close();
					}
				}
				if(savelist != null && savelist.size()>0){
					try{
						alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
						alarmIndicatorsNodeDao.saveBatch(savelist);
					}catch(Exception e){
						
					}finally{
						alarmIndicatorsNodeDao.close();
					}
				} 
			}catch(Exception e){
				e.printStackTrace();
			}
		} 
		return showlist();
	}

	public String showChooseNodeList(){
		
		String jsp = "/topology/threshold/showchoosenodelist.jsp";
		
		try {
			
			String jspFlag = getParaValue("jspFlag");
			
			String nodeid = getParaValue("nodeid");
			
			String type = getParaValue("type");
			
			String subtype = getParaValue("subtype");
			
			String[] ids = getParaArrayValue("checkbox");
			String idstr = "";
			if(ids != null && ids.length>0){
				for(int i=0;i<ids.length;i++){
					idstr = idstr+ids[i]+",";
				}
			}
			
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil(); 

			List nodeDTOlist = alarmIndicatorsUtil.getNodeListByTypeAndSubtype(type, subtype);
			
			List nodeList = new ArrayList();
			
			if(nodeDTOlist!=null){
				for(int i = 0 ; i < nodeDTOlist.size() ; i++){
					NodeDTO nodeDTO = (NodeDTO)nodeDTOlist.get(i);
					if("multi".equals(jspFlag)){
						if(!String.valueOf(nodeDTO.getId()).equals(nodeid) ){
							nodeList.add(nodeDTO);
						}
					} else {
						if("-1".equals(nodeid) || nodeid == null){
							nodeList.add(nodeDTO);
						}else{
							if(nodeDTO.getNodeid().equals(nodeid) ){
								nodeList.add(nodeDTO);
							}
						}
					}
					
				}
			}
			request.setAttribute("nodeDTOlist", nodeList);
			request.setAttribute("ids", idstr);
			request.setAttribute("nodeid", nodeid);
			request.setAttribute("type", type);			
			request.setAttribute("subtype", subtype);
			request.setAttribute("jspFlag", jspFlag);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsp;
	}
	
	/**
	 * 在 列表中添加
	 * @return
	 */
	public String showAdd(){
		
		
		String jsp = "/topology/threshold/showadd.jsp";
		
		String nodeid = getParaValue("nodeid");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		
		String jspFlag = getParaValue("jspFlag");
		
		Hashtable moidhash = new Hashtable();
		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List gatherIndicatorsList = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(type, subtype);
			request.setAttribute("gatherIndicatorsList", gatherIndicatorsList);
			
			AlarmIndicatorsUtil alarmindicatorsUtil = new AlarmIndicatorsUtil();
			List<AlarmIndicatorsNode> nodeGatherIndicatorsList = alarmindicatorsUtil.getAlarmIndicatorsForNode(nodeid, type, subtype);
			request.setAttribute("nodeGatherIndicatorsList", nodeGatherIndicatorsList);
			
			if(nodeGatherIndicatorsList!=null){
				for(int i = 0 ; i < nodeGatherIndicatorsList.size() ; i ++){
					AlarmIndicatorsNode alarmIndicatorsNode = nodeGatherIndicatorsList.get(i);
					moidhash.put(alarmIndicatorsNode.getName(), alarmIndicatorsNode);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("moidhash", moidhash);
		request.setAttribute("nodeid", nodeid);
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);		
		request.setAttribute("jspFlag", jspFlag);
		return jsp;
	}
	
	public String showlist(){
		String jsp = "/topology/threshold/showlist.jsp";
		String nodeid = getParaValue("nodeid");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		
		if(type == null){
			type = Constant.ALL_TYPE;
		}
		
		if(subtype == null){
			subtype = Constant.ALL_SUBTYPE;
		}
		
		if(nodeid == null){
			nodeid = "-1";
		}
		
		List<NodeDTO> nodeDTOlist = null;
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		
		List allNodeDTOlist = alarmIndicatorsUtil.getNodeListByTypeAndSubtype(Constant.ALL_TYPE, Constant.ALL_SUBTYPE);
		
		
		nodeDTOlist = alarmIndicatorsUtil.getNodeListByTypeAndSubtype(type, subtype);
		
		Hashtable<String , NodeDTO> nodeDTOHashtable = new Hashtable<String , NodeDTO>();
		//为了分页
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		List pagelist = new ArrayList();
		String wherestr = "where 1=1 ";
		try{
			if(!"-1".equalsIgnoreCase(type)){
				wherestr = wherestr +" and type = '"+type+"'";
			}
			if(!"-1".equalsIgnoreCase(subtype)){
				wherestr = wherestr +" and subtype = '"+subtype+"'";
			}
			if(!"-1".equalsIgnoreCase(nodeid)){
				wherestr = wherestr +" and nodeid = '"+nodeid+"'";
			}
			list(alarmIndicatorsNodeDao,wherestr);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			alarmIndicatorsNodeDao.close();
		}
		pagelist = (List)request.getAttribute("list");
		
		List<AlarmIndicatorsNode> list = new ArrayList<AlarmIndicatorsNode>();
		if(nodeDTOlist!=null){
			for(int i = 0 ; i < nodeDTOlist.size() ; i ++){
				NodeDTO nodeDTO = nodeDTOlist.get(i);
				if("-1".equals(nodeid) || nodeDTO.getNodeid().equals(nodeid)){
					//SysLogger.info("nodeDTO.getName()-------"+nodeDTO.getName());
					List<AlarmIndicatorsNode> list2 = alarmIndicatorsUtil.getAlarmIndicatorsForNode(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
					//SysLogger.info(nodeDTO.getId() + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype()+"====="+nodeDTO.getName());
					nodeDTOHashtable.put(String.valueOf(nodeDTO.getId()) + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype(), nodeDTO);
					list.addAll(list2);
				}
				
			}
		}
		//System.out.println(list.size() + "=======list.size()===========");
		//request.setAttribute("list", list);
//		for(NodeDTO l : nodeDTOlist){
//			System.out.println("....................."+l.getType()+l.getBusinessName()+l.getName());
//		}
		request.setAttribute("list", pagelist);
		
		request.setAttribute("allNodeDTOlist", allNodeDTOlist);
		
		request.setAttribute("nodelist", nodeDTOlist);
		request.setAttribute("nodeDTOHashtable", nodeDTOHashtable);
		request.setAttribute("nodeid", nodeid);
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);
		
		return jsp;
	}
	
public String list(){
		
		String jsp = "/alarm/threshold/list.jsp";
		//emc 和 vmware 用的
		String category = getParaValue("category");
		String flag = (String)session.getAttribute("flag");
		
//		System.out.println("------category------:"+category);
//		System.out.println("-------flag---------:"+flag);
		//vmware
		if(request.getParameter("type").equals("virtual")){
			 if(category != null){	
				if(flag != null){request.getSession().removeAttribute("flag");}
				if(category.equalsIgnoreCase("physical")){
					request.setAttribute("category", "physical");
					jsp="/alarm/threshold/listvmphysical.jsp";
				}else if(category.equalsIgnoreCase("vmware")){
					request.setAttribute("category", "vmware");
					jsp="/alarm/threshold/listvmvmware.jsp";
				}else if(category.equalsIgnoreCase("yun")){
					request.setAttribute("category", "yun");
					jsp="/alarm/threshold/listvmyun.jsp";
				}else if(category.equalsIgnoreCase("resource")){
					request.setAttribute("category", "resource");
					jsp="/alarm/threshold/listvmresource.jsp";
				}
			 }else{
				 if(flag.equalsIgnoreCase("physical")){
					    request.getSession().removeAttribute("flag");
						request.setAttribute("category", "physical");
						jsp="/alarm/threshold/listvmphysical.jsp";
					}else if(flag.equalsIgnoreCase("vmware")){
						request.getSession().removeAttribute("flag");
						request.setAttribute("category", "vmware");
						jsp="/alarm/threshold/listvmvmware.jsp";
					}else if(flag.equalsIgnoreCase("yun")){
						request.getSession().removeAttribute("flag");
						request.setAttribute("category", "yun");
						jsp="/alarm/threshold/listvmyun.jsp";
					}else if(flag.equalsIgnoreCase("resource")){
						request.getSession().removeAttribute("flag");
						request.setAttribute("category", "resource");
						jsp="/alarm/threshold/listvmresource.jsp";
					}
			 }
			
		}
		//emc
		if(request.getParameter("type").equals("storage") && request.getParameter("subtype").equals("hds")){
			if(request.getParameter("subtype").equals("emc")){
		 if(category != null){	
			if(flag != null){request.getSession().removeAttribute("flag");}
			if(category.equalsIgnoreCase("lun")){
				request.setAttribute("category", "lun");
				jsp="/alarm/threshold/listemclun.jsp";
			}else if(category.equalsIgnoreCase("disk")){
				request.setAttribute("category", "disk");
				jsp="/alarm/threshold/listemcdisk.jsp";
			}else if(category.equalsIgnoreCase("environment") || category.equalsIgnoreCase("envstore") || category.equalsIgnoreCase("envpower") || category.equalsIgnoreCase("bakpower")){
				request.setAttribute("category", "environment");
				jsp="/alarm/threshold/listemcenv.jsp";
			}
		 }else{
			 if(flag.equalsIgnoreCase("lun")){
				    request.getSession().removeAttribute("flag");
					request.setAttribute("category", "lun");
					jsp="/alarm/threshold/listemclun.jsp";
				}else if(flag.equalsIgnoreCase("disk")){
					request.getSession().removeAttribute("flag");
					request.setAttribute("category", "disk");
					jsp="/alarm/threshold/listemcdisk.jsp";
				}else if(flag.equalsIgnoreCase("environment")){
					request.getSession().removeAttribute("flag");
					request.setAttribute("category", "environment");
					jsp="/alarm/threshold/listemcenv.jsp";
							}
				}
		   }
		}
		String subtype=request.getParameter("subtype");
		String type=request.getParameter("type");
		//结束
		try {
			List list = getList();
			request.setAttribute("type", type);
			request.setAttribute("subtype", subtype);
			request.setAttribute("list", list);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("######################:"+jsp);
		return jsp;
	}
	
	public String showkeylist(){
		String jsp = "/alarm/threshold/showkeylist.jsp";
		String topoid = (String)request.getAttribute("topoid");
		String nodeid = (String)request.getAttribute("nodeid");
		String type = (String)request.getAttribute("type");
		String subtype = (String)request.getAttribute("subtype");
		if(request.getParameter("type").equals("virtual")){
			//jsp="/alarm/threshold/listvm.jsp";
		}
		List indiNodeList = new ArrayList();
		Hashtable moidHash = new Hashtable();
		AlarmIndicatorsNodeDao indiNodeDao = new AlarmIndicatorsNodeDao();
		try{
			indiNodeList = indiNodeDao.findByNodeIdAndTypeAndSubType(nodeid, type, subtype);
			if(indiNodeList != null && indiNodeList.size()>0){
				for(int i=0;i<indiNodeList.size();i++){
					AlarmIndicatorsNode alarmIndi = (AlarmIndicatorsNode)indiNodeList.get(i);
					moidHash.put(alarmIndi.getId()+"", alarmIndi);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			indiNodeDao.close();
		}
		List list = null;
		IndicatorsTopoRelationDao relationdao = new IndicatorsTopoRelationDao();
		try {
			list = relationdao.findByTopoAndNodeId(topoid, nodeid);
			request.setAttribute("list", list);			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			relationdao.close();
		}
		request.setAttribute("moidHash", moidHash);
		return jsp;
	}
	
public String showtoaddlist(){
		
		String jsp = "/alarm/threshold/showtoaddlist.jsp";
		
		try {
			String nodeid = getParaValue("nodeid");
			
			String type = getParaValue("type");
			
			String subtype = getParaValue("subtype");
			
			String category = getParaValue("category");
			if(subtype != null){
			 if("emc_vnx".equalsIgnoreCase(subtype) && category != null){
				 if(category.equalsIgnoreCase("lun")){
					 jsp = "/alarm/threshold/showtoaddlistlun.jsp";
				 }else if(category.equalsIgnoreCase("disk")){
					 jsp = "/alarm/threshold/showtoaddlistdisk.jsp";
				 }else if(category.equalsIgnoreCase("env")){
					 jsp = "/alarm/threshold/showtoaddlistenv.jsp";
				 }
			 }else if("vmware".equalsIgnoreCase(subtype) && category != null){
				 if(category.equalsIgnoreCase("physical")){
					 jsp = "/alarm/threshold/showtoaddlistphysical.jsp";
				 }else if(category.equalsIgnoreCase("vmware")){
					 jsp = "/alarm/threshold/showtoaddlistvmware.jsp";
				 }else if(category.equalsIgnoreCase("yun")){
					 jsp = "/alarm/threshold/showtoaddlistyun.jsp";
				 }else if(category.equalsIgnoreCase("resource")){
					 jsp = "/alarm/threshold/showtoaddlistresource.jsp";
				 }
			 }
			}
			//获取已经实例化的采集指标
			List moidlist = new ArrayList();
			Hashtable moidhash = new Hashtable();
			
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			try {
				moidlist = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeid, type, subtype);
				System.out.println("--------moidlist---------"+type+":::"+subtype);
			    System.out.println("--------moidlist---------"+moidlist.size());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(moidlist != null && moidlist.size()>0){
				for(int i=0;i<moidlist.size();i++){
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)moidlist.get(i);
					moidhash.put(alarmIndicatorsNode.getName(), alarmIndicatorsNode);
				}
			}
			
			AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
			//AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
			//获取采集摸板里对应类型的数据
			List list = new ArrayList();
			try {
//				if(subtype.equalsIgnoreCase("vmware")){
//					VMWareVidDao vmdao = new VMWareVidDao();
//					List alllist = vmdao.queryVMVidCategory(Integer.parseInt(nodeid));
//					if(alllist != null && alllist.size()>0){
//				    		String vid = "";
//				    		String category="";
//				    		VMWareVid vo = null;
//				    		for(int i=0;i<alllist.size();i++){
//				    			vo = (VMWareVid) alllist.get(i);
//				    			vid = vo.getVid();
//				    			category = vo.getCategory();
//				    			AlarmIndicatorsUtil vmalarmIndicatorsUtil = new AlarmIndicatorsUtil();
//				    			vmalarmIndicatorsUtil.VMsaveAlarmInicatorsThresholdForNode(nodeid, AlarmConstant.TYPE_VIRTUAL, "vmware",category,vid);
//				    		}
//				    	}
//					list = alarmIndicatorsNodeDao.getByTypeAndSubType(type, subtype);
//				}else
//				{
					list = alarmIndicatorsDao.getByTypeAndSubType(type, subtype);
//				}
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				alarmIndicatorsDao.close();
			}
			request.setAttribute("category", category);
			request.setAttribute("list", list);
			request.setAttribute("moidhash", moidhash);
			request.setAttribute("nodeid", nodeid);
			request.setAttribute("type", type);			
			request.setAttribute("subtype", subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsp;
	}
	
public String showtosetshowkeylist(){
		
		String jsp = "/alarm/threshold/showtosetshowkeylist.jsp";
		String xmlFile = (String)session.getAttribute(SessionConstant.CURRENT_TOPO_VIEW);	
		SysLogger.info("xmlFile==========="+xmlFile);
		ManageXml topovo = null;
		try{
			
			ManageXmlDao manageXmlDao = new ManageXmlDao();
			try {
				topovo = (ManageXml)manageXmlDao.findByXml(xmlFile);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				manageXmlDao.close();
			}
			
			
			String nodeid = getParaValue("nodeid");
			
			String type = getParaValue("type");
			
			String subtype = getParaValue("subtype");
			
			//获取已经实例化的指标
			List moidlist = new ArrayList();
			Hashtable moidhash = new Hashtable();			
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			try {
				//moidlist = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeid, type, subtype);
				moidlist = alarmIndicatorsUtil.getShowIndicatorsByTopoidAndNodeId(topovo.getId()+"",nodeid);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(moidlist != null && moidlist.size()>0){
				for(int i=0;i<moidlist.size();i++){
					IndicatorsTopoRelation alarmIndicatorsNode = (IndicatorsTopoRelation)moidlist.get(i);
					moidhash.put(alarmIndicatorsNode.getIndicatorsId(), alarmIndicatorsNode);
				}
			}
			
			AlarmIndicatorsNodeDao alarmIndicatorsDao = new AlarmIndicatorsNodeDao();
			//AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
			//获取采集摸板里对应类型的数据
			List list = new ArrayList();
			try {
				//AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				try {
					list = alarmIndicatorsDao.findByNodeIdAndTypeAndSubType(nodeid, type, subtype);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				if(list != null && list.size()>0){
//					for(int i=0;i<moidlist.size();i++){
//						AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)moidlist.get(i);
//						moidhash.put(alarmIndicatorsNode.getMoid(), alarmIndicatorsNode);
//					}
//				}
					//list = alarmIndicatorsDao.getByTypeAndSubType(type, subtype);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				alarmIndicatorsDao.close();
			}
			request.setAttribute("list", list);
			request.setAttribute("moidhash", moidhash);
			request.setAttribute("nodeid", nodeid);
			request.setAttribute("type", type);			
			request.setAttribute("subtype", subtype);
			request.setAttribute("topoid", topovo.getId()+"");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsp;
	}
	
public String showtomultiaddlist(){
		
		String jsp = "/alarm/threshold/showtomultiaddlist.jsp";
		
		try {
			String nodeid = getParaValue("nodeid");
			
			String type = getParaValue("type");
			
			String subtype = getParaValue("subtype");
			
			String[] ids = getParaArrayValue("checkbox");
			String idstr = "";
			if(ids != null && ids.length>0){
				for(int i=0;i<ids.length;i++){
					idstr = idstr+ids[i]+",";
				}
			}

			List hostnodelist = new ArrayList();
			
			if(type.equalsIgnoreCase("host")){
				HostNodeDao dao = new HostNodeDao();
				//获取被监视的服务器列表
				List nodelist = new ArrayList();
				try{
					nodelist = dao.loadMonitorByMonCategory(1, 4);
				}catch(Exception e){					
				}finally{
					dao.close();
				}
				if(nodelist != null && nodelist.size()>0){
					for(int i=0;i<nodelist.size();i++){
						HostNode hostnode = (HostNode)nodelist.get(i);
						if(hostnode.getId() != Integer.parseInt(nodeid)){
							hostnodelist.add(hostnode);
						}
					}
				}
				System.out.println("hostnodelist="+hostnodelist.size());
			}
			if(type.equalsIgnoreCase("net")){
				HostNodeDao dao = new HostNodeDao();
				//获取被监视的服务器列表
				List nodelist = new ArrayList();
				try{
					nodelist = dao.loadNetwork(1);
					System.out.println("nodelist="+nodelist.size());
				}catch(Exception e){
				}finally{
					dao.close();
				}
				if(nodelist != null && nodelist.size()>0){
					for(int i=0;i<nodelist.size();i++){
						HostNode hostnode = (HostNode)nodelist.get(i);
						if(hostnode.getId() != Integer.parseInt(nodeid)){
							hostnodelist.add(hostnode);
						}
					}
				}
				System.out.println("hostnodelist="+hostnodelist.size());
			}
			
			request.setAttribute("hostnodelist", hostnodelist);
			request.setAttribute("ids", idstr);
			request.setAttribute("nodeid", nodeid);
			request.setAttribute("type", type);			
			request.setAttribute("subtype", subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsp;
	}
	
	public List getList(){
		
		String nodeid = getParaValue("nodeid");
		
		String type = getParaValue("type");
		String ipaddress=getParaValue("ipaddress");
		String subtype = getParaValue("subtype");
		List list = null;
//		System.out.println(nodeid+"---nodeid--------subtype-------------------"+subtype);
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		try {
				list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid)); 
		
		if(null!=host)
		{
		NodeUtil nodeUtil = new NodeUtil();
        NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
        subtype = nodedto.getSubtype();
        type = nodedto.getType();
		}
		
		request.setAttribute("nodeid", nodeid);
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("type", type);
		
		request.setAttribute("subtype", subtype);
		return list;
	}
	
//	public String getSQLQueryForList(){
//		
//		return "";
//	}
	
	public String add(){
		String jsp = "/alarm/indicators/add.jsp";
		return jsp;
	}
	
	public String edit(){
		String jsp = "/alarm/threshold/edit.jsp";
		String id = getParaValue("id");
		String flag = getParaValue("flag");
		if("vmware".equals(flag)){
			jsp = "/alarm/threshold/VMedit.jsp";
		}
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		AlarmIndicatorsNode alarmIndicatorsNode = null;
		try {
			alarmIndicatorsNode = (AlarmIndicatorsNode)alarmIndicatorsNodeDao.findByID(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmIndicatorsNodeDao.close();
		}
		
		String nodeid = getParaValue("nodeid");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		String ipaddress=getParaValue("ipaddress");
		Hashtable alarmWayHashtable = new Hashtable();
		List away0list = new ArrayList(); 
		List away1list = new ArrayList(); 
		List away2list = new ArrayList(); 
		if(alarmIndicatorsNode!=null){
			nodeid = alarmIndicatorsNode.getNodeid();
			type = alarmIndicatorsNode.getType();
			subtype = alarmIndicatorsNode.getSubtype();
			AlarmWayDao alarmWayDao = null;
			if(alarmIndicatorsNode.getWay0()!=null&&!"".equals(alarmIndicatorsNode.getWay0())&&!"null".equalsIgnoreCase(alarmIndicatorsNode.getWay0())){
				String away0[] = alarmIndicatorsNode.getWay0().split(",");
				if(away0.length>0){
					for(int i=0;i<away0.length;i++){
						try {
							alarmWayDao = new AlarmWayDao();
							AlarmWay alarmWay0 = (AlarmWay)alarmWayDao.findByID(away0[i]);
							if(alarmWay0!=null){
								away0list.add(alarmWay0);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							alarmWayDao.close();
						}
					}
					alarmWayHashtable.put("way0", away0list);
				}
			}
			
			if(alarmIndicatorsNode.getWay1()!=null&&!"".equals(alarmIndicatorsNode.getWay1())&&!"null".equalsIgnoreCase(alarmIndicatorsNode.getWay1())){
				String away1[] = alarmIndicatorsNode.getWay1().split(",");
				if(away1.length>0){
					for(int i=0;i<away1.length;i++){
						try {
							alarmWayDao = new AlarmWayDao();
							AlarmWay alarmWay1 = (AlarmWay)alarmWayDao.findByID(away1[i]);
							if(alarmWay1!=null){
								away1list.add(alarmWay1);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							alarmWayDao.close();
						}
					}
					alarmWayHashtable.put("way1", away1list);
				}
			}
			
			if(alarmIndicatorsNode.getWay2()!=null&&!"".equals(alarmIndicatorsNode.getWay2())&&!"null".equalsIgnoreCase(alarmIndicatorsNode.getWay2())){
				String away2[] = alarmIndicatorsNode.getWay2().split(",");
				if(away2.length>0){
					for(int i=0;i<away2.length;i++){
						try {
							alarmWayDao = new AlarmWayDao();
							AlarmWay alarmWay2 = (AlarmWay)alarmWayDao.findByID(away2[i]);
							if(alarmWay2!=null){
								away2list.add(alarmWay2);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							alarmWayDao.close();
						}
					}
					alarmWayHashtable.put("way2", away2list);
				}
			}
		}
		request.setAttribute("alarmWayHashtable", alarmWayHashtable);
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("alarmIndicatorsNode", alarmIndicatorsNode);
		return jsp;
	}
	
	public String update(){
		
		AlarmIndicatorsNode alarmIndicatorsNode = createAlarmIndicatorsNode();
		int id = getParaIntValue("id");
		alarmIndicatorsNode.setId(id);
		String nodeid = getParaValue("nodeid");
		alarmIndicatorsNode.setNodeid(nodeid);
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNodeDao.update(alarmIndicatorsNode); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmIndicatorsNodeDao.close();
		}
		
		
		return list();
	}
	
public String addselected(){
		
		String[] ids = getParaArrayValue("moid");
		String type = getParaValue("type");
		String ip = getParaValue("ip");
		String subtype = getParaValue("subtype");
		System.out.println("---subtype-------------->"+subtype);
		String nodeid = getParaValue("nodeid");
		if(ids != null && ids.length > 0){
			AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
			try{
				List list2 = new ArrayList();
				for(int i=0;i<ids.length;i++){
					AlarmIndicators alarmIndicators = (AlarmIndicators)alarmIndicatorsDao.findByID(ids[i]);
					if(subtype.equalsIgnoreCase("vmware"))
					{
						if(alarmIndicators.getName().equalsIgnoreCase("ping")){
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						    AlarmIndicatorsNode alarmIndicatorsNode = alarmIndicatorsUtil.createAlarmIndicatorsNodeByAlarmIndicators(nodeid, alarmIndicators);
						    list2.add(alarmIndicatorsNode);
						}else{
							String category = alarmIndicators.getCategory();
							VMWareVidDao viddao = new VMWareVidDao();
							List l = viddao.queryVMVid(Integer.parseInt(nodeid), category);
							if(l != null && l.size()>0){
								for(int j=0;j<l.size();j++){
									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
									AlarmIndicatorsNode alarmIndicatorsNode = alarmIndicatorsUtil.VMcreateAlarmIndicatorsNodeByAlarmIndicators(nodeid, alarmIndicators,l.get(j).toString());
									list2.add(alarmIndicatorsNode);
								}
							}	
						}

					}else if(subtype.equalsIgnoreCase("emc_vnx")){
//						if(alarmIndicators.getName().equalsIgnoreCase("ping")){
//							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//						    AlarmIndicatorsNode alarmIndicatorsNode = alarmIndicatorsUtil.createAlarmIndicatorsNodeByAlarmIndicators(nodeid, alarmIndicators);
//						    list2.add(alarmIndicatorsNode);
//						}else{
//							String category = alarmIndicators.getCategory();
//							
////							System.out.println("---category-------------->"+category);
//							
////							VMWareVidDao viddao = new VMWareVidDao();
////							List l = viddao.queryVMVid(Integer.parseInt(nodeid), category);
//							Hashtable ipAllData = new Hashtable();
//							ipAllData = (Hashtable) ShareData.getEmcdata().get(ip);
//							List<Lun> lunlist = (List<Lun>) ipAllData.get("lunconfig");
//							Environment envlist = (Environment) ipAllData.get("environment");
//							List<Disk> disklist = (List<Disk>) ipAllData.get("disk");
//							List name = new ArrayList();
//							if(category.equals("lun")){
//								if(lunlist != null && lunlist.size()>0){
//									Lun lun_vo = null;
//									for(int j=0;j<lunlist.size();j++){
//										lun_vo = lunlist.get(j);
////										System.out.println(lunlist.size()+"----"+lun_vo.getName());
//										if(!"null".equalsIgnoreCase(lun_vo.getName()) && lun_vo.getName() != null){
//											name.add(lun_vo.getName());
//										}
//									}
//								}
//							}else if(category.equals("disk")){
//								if(disklist != null && disklist.size()>0){
//									Disk disk_vo = null;
//									for(int j=0;j<disklist.size();j++){
//										disk_vo = disklist.get(j);
//										if(disk_vo.getSerialNumber() != null){
//											name.add(disk_vo.getSerialNumber());
//										}
//									}
//								}
//							}else {
//								if(envlist != null){
//									Array ar = envlist.getArray();
//									List<MemModel> lm1 = envlist.getMemList();
//									List<MemModel> lm2 = envlist.getBakPowerList();
//									MemModel mem1 = null;
//									MemModel mem2 = null;
//									if(category.equals("envpower")){
//									  if(ar != null){
//										name.add(ip);
//									  }
//									}else if(category.equals("envstore")){
//									  if(lm1 != null && lm1.size()>0){
//										for(int j=0;j<lm1.size();j++){
//											mem1 = lm1.get(j);
//											if(!"null".equalsIgnoreCase(mem1.getName()) && mem1.getName() != null){
//												name.add(mem1.getName());
//											}
//										}
//									}
//									}else if(category.equals("bakpower")){
//									  if(lm2 != null && lm2.size()>0){
//										for(int j=0;j<lm2.size();j++){
//											mem2 = lm2.get(j);
//											if(!"null".equalsIgnoreCase(mem2.getName()) && mem2.getName() != null){
//												name.add(mem2.getName());
//											}
//										}
//									}
//									}
//								}
//							}
//							
//							
//							
//							if(name != null && name.size()>0){
//								for(int j=0;j<name.size();j++){
//									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//									AlarmIndicatorsNode alarmIndicatorsNode = alarmIndicatorsUtil.VMcreateAlarmIndicatorsNodeByAlarmIndicators(nodeid, alarmIndicators,name.get(j).toString());
//									list2.add(alarmIndicatorsNode);
//								}
//							}else{
//								return list();
//							}
//						}
//
					}else{
					
					    AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					    AlarmIndicatorsNode alarmIndicatorsNode = alarmIndicatorsUtil.createAlarmIndicatorsNodeByAlarmIndicators(nodeid, alarmIndicators);
					    list2.add(alarmIndicatorsNode);
					}
					
				}
				AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
				try {
					alarmIndicatorsNodeDao.saveBatch(list2); 
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					alarmIndicatorsNodeDao.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				alarmIndicatorsDao.close();
			}
		}
		return list();
	}
	
public String addselectedshowkey(){
		
		String[] ids = getParaArrayValue("moid");
		String type = getParaValue("type");		
		String subtype = getParaValue("subtype");		
		String nodeid = getParaValue("nodeid");
		String topoid = getParaValue("topoid");		
		List moidlist = new ArrayList();
		if(ids != null && ids.length > 0){
			//删除原来的指标
			IndicatorsTopoRelationDao relationdao = new IndicatorsTopoRelationDao();
			try{
				relationdao.deleteByTopoIdAndNodeId(topoid, nodeid);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				relationdao.close();
			}
			try{
				List doList = new ArrayList();
				String moid = "";
				for(int i=0;i<ids.length;i++){
					moid = ids[i];
					//新建
					IndicatorsTopoRelation relation = new IndicatorsTopoRelation();
					relation.setIndicatorsId(moid);
					relation.setTopoId(topoid);
					relation.setSIndex("");
					relation.setNodeid(nodeid);
					doList.add(relation);
				}
				if(doList != null && doList.size()>0){
					relationdao = new IndicatorsTopoRelationDao();
					try{
						relationdao.save(doList);
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						relationdao.close();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
			}
			
			IndicatorsTopoRelationDao indicatorsTopoRelationDao = new IndicatorsTopoRelationDao();   
			Hashtable tophash = new Hashtable();
			try{
				List list = indicatorsTopoRelationDao.findByTopoAndNodeId(topoid, nodeid);
				tophash = ShareData.getToprelation();
				tophash.put(topoid+":"+nodeid, list);
				ShareData.setToprelation(tophash);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				indicatorsTopoRelationDao.close();
			}
		}
		request.setAttribute("topoid", topoid);
		request.setAttribute("nodeid", nodeid);
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);
		return showkeylist();
	}
	
	
/**
 * 
 * 
 *补充设置阀值 
 * @return
 */	
public String multireplenishadd(){
		
		String[] nodeids = getParaArrayValue("checkbox");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		String nodeid = getParaValue("nodeid");
		
		//需要批量应用的指标
		String ids = getParaValue("ids");
		
		if(nodeids != null && nodeids.length > 0){
			
			
			List idlist = new ArrayList();
			List addindilist = new ArrayList();
			if(ids != null && ids.trim().length()>0){
				String[] idsplit = ids.split(",");
				if(idsplit != null && idsplit.length>0){
					for(int i=0;i<idsplit.length;i++){
						if(idsplit[i] != null && idsplit[i].trim().length()>0){
							idlist.add(idsplit[i]);
						}
					}
				}
			}
			//从数据库中查询出需要需要修改的值对象
			if(idlist != null && idlist.size()>0){
				AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
				try{
					for(int i=0;i<idlist.size();i++){
						String indicatorid = (String)idlist.get(i); 
						AlarmIndicatorsNode innode = (AlarmIndicatorsNode)alarmIndicatorsNodeDao.findByID(indicatorid);
						addindilist.add(innode);
					}
				}catch(Exception e){
					
				}finally{
					alarmIndicatorsNodeDao.close();
				}
				
			}
			
			//建立阀值操作对象
			
			AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
			
			
			//根据nodeids 删除对象对应的指标
			alarmIndicatorsNodeDao.deletenametypenodeid(nodeids, addindilist);
			//批量添加阀值指标
			alarmIndicatorsNodeDao.addBatch(nodeids, addindilist); 
		}
		return list();
		//return list();
	}


/**
 * 
 * 应用当前阀值配置
 * @return
 */	
public String multichangeadd(){
		
		String[] nodeids = getParaArrayValue("checkbox");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		String nodeid = getParaValue("nodeid");
		
		//需要批量应用的指标
		String ids = getParaValue("ids");
		
		if(nodeids != null && nodeids.length > 0){
			
			
			List idlist = new ArrayList();
			List addindilist = new ArrayList();
			if(ids != null && ids.trim().length()>0){
				String[] idsplit = ids.split(",");
				if(idsplit != null && idsplit.length>0){
					for(int i=0;i<idsplit.length;i++){
						if(idsplit[i] != null && idsplit[i].trim().length()>0){
							idlist.add(idsplit[i]);
						}
					}
				}
			}
			//从数据库中查询出需要需要修改的值对象
			if(idlist != null && idlist.size()>0){
				AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
				try{
					for(int i=0;i<idlist.size();i++){
						String indicatorid = (String)idlist.get(i); 
						AlarmIndicatorsNode innode = (AlarmIndicatorsNode)alarmIndicatorsNodeDao.findByID(indicatorid);
						addindilist.add(innode);
					}
					
					
					
					//根据nodeids 删除对象对应的指标
					alarmIndicatorsNodeDao.deletenodeid(nodeids);
					//批量添加阀值指标
					alarmIndicatorsNodeDao.addBatch(nodeids, addindilist);
					 
					
					alarmIndicatorsNodeDao.close();
					
				}catch(Exception e){
					
				}finally{
					alarmIndicatorsNodeDao.close();
				}
				
			}
			
			
		}
		return list();
	}



/***
 * 
 * 配置阀值原方法
 * @return
 */
public String multiadd(){
	
	String[] nodeids = getParaArrayValue("checkbox");
	String type = getParaValue("type");
	String subtype = getParaValue("subtype");
	String nodeid = getParaValue("nodeid");
	
	//需要批量应用的指标
	String ids = getParaValue("ids");
	
	if(nodeids != null && nodeids.length > 0){
		
		
		List idlist = new ArrayList();
		List addindilist = new ArrayList();
		if(ids != null && ids.trim().length()>0){
			String[] idsplit = ids.split(",");
			if(idsplit != null && idsplit.length>0){
				for(int i=0;i<idsplit.length;i++){
					if(idsplit[i] != null && idsplit[i].trim().length()>0){
						idlist.add(idsplit[i]);
					}
				}
			}
		}
		//从数据库中查询出需要需要修改的值对象
		if(idlist != null && idlist.size()>0){
			AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
			try{
				for(int i=0;i<idlist.size();i++){
					String indicatorid = (String)idlist.get(i); 
					AlarmIndicatorsNode innode = (AlarmIndicatorsNode)alarmIndicatorsNodeDao.findByID(indicatorid);
					addindilist.add(innode);
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				alarmIndicatorsNodeDao.close();
			}
			
		}
		
		//建立阀值操作对象
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			//根据nodeids 删除对象对应的指标
			if(nodeids!=null&&addindilist!=null&&addindilist.size()>0&&nodeids.length>0){
				alarmIndicatorsNodeDao.deletenametypenodeid(nodeids, addindilist);
			}
			//批量添加阀值指标
			alarmIndicatorsNodeDao.addBatch(nodeids, addindilist);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			alarmIndicatorsNodeDao.close();
		}
	}


	return showlist();

  }



	
	public String save(){
		
		AlarmIndicatorsNode alarmIndicatorsNode = createAlarmIndicatorsNode();
		
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNodeDao.save(alarmIndicatorsNode); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmIndicatorsNodeDao.close();
		}	
		return list();
	}
	
	public String delete(){
		
		String id = getParaValue("id");
		String[] ids = {id}; 
		String value = getParaValue("value");
		
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNodeDao.delete(ids); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmIndicatorsNodeDao.close();
		}
		return list();
	}
	
	
	public AlarmIndicatorsNode createAlarmIndicatorsNode(){
		String name = getParaValue("name");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		String datatype = getParaValue("datatype");
		String nodeid = getParaValue("nodeid");
		String moid = getParaValue("moid");
		int threshold = getParaIntValue("threshold");
		String threshold_unit = getParaValue("threshold_unit");
		int compare = getParaIntValue("compare");
		int compare_type = getParaIntValue("compare_type");
		String alarm_times = getParaValue("alarm_times");
		String alarm_info = getParaValue("alarm_info");
		String alarm_level = getParaValue("alarm_level");
		String enabled = getParaValue("enabled");
//		String poll_interval = getParaValue("poll_interval");
//		String interval_unit = getParaValue("interval_unit");
		String subentity = getParaValue("subentity");
		String limenvalue0 = getParaValue("limenvalue0");
		String limenvalue1 = getParaValue("limenvalue1");
		String limenvalue2 = getParaValue("limenvalue2");
		String time0 = getParaValue("time0");
		String time1 = getParaValue("time1");
		String time2 = getParaValue("time2");
		String sms0 = getParaValue("sms0");
		String sms1 = getParaValue("sms1");
		String sms2 = getParaValue("sms2");
		String way0 = getParaValue("way0-id");
		String way1 = getParaValue("way1-id");
		String way2 = getParaValue("way2-id");
		String category = getParaValue("category");
		String descr = getParaValue("descr");
		String unit = getParaValue("unit");
		
		threshold = 1;
		//compare = 1;
		compare_type = 1;
		
		AlarmIndicatorsNode alarmIndicatorsNode = new AlarmIndicatorsNode();
		alarmIndicatorsNode.setName(name);
		alarmIndicatorsNode.setType(type);
		alarmIndicatorsNode.setSubtype(subtype);
		alarmIndicatorsNode.setDatatype(datatype);
		alarmIndicatorsNode.setNodeid(nodeid);
		alarmIndicatorsNode.setMoid(moid);
		alarmIndicatorsNode.setThreshlod(threshold);
		alarmIndicatorsNode.setThreshlod_unit(threshold_unit);
		alarmIndicatorsNode.setCompare(compare);
		alarmIndicatorsNode.setCompare_type(compare_type);
		alarmIndicatorsNode.setAlarm_times(alarm_times);
		alarmIndicatorsNode.setAlarm_info(alarm_info);
		alarmIndicatorsNode.setAlarm_level(alarm_level);
		alarmIndicatorsNode.setEnabled(enabled);
//		String[] interstr = poll_interval.split("-");
//		alarmIndicatorsNode.setPoll_interval(interstr[0]);
//		alarmIndicatorsNode.setInterval_unit(interstr[1]);
		alarmIndicatorsNode.setSubentity(subentity);
		alarmIndicatorsNode.setLimenvalue0(limenvalue0);
		alarmIndicatorsNode.setLimenvalue1(limenvalue1);
		alarmIndicatorsNode.setLimenvalue2(limenvalue2);
		alarmIndicatorsNode.setTime0(time0);
		alarmIndicatorsNode.setTime1(time1);
		alarmIndicatorsNode.setTime2(time2);
		alarmIndicatorsNode.setSms0(sms0);
		alarmIndicatorsNode.setSms1(sms1);
		alarmIndicatorsNode.setSms2(sms2);
		alarmIndicatorsNode.setWay0(way0);
		alarmIndicatorsNode.setWay1(way1);
		alarmIndicatorsNode.setWay2(way2);
		alarmIndicatorsNode.setCategory(category);
		alarmIndicatorsNode.setDescr(descr);
		alarmIndicatorsNode.setUnit(unit);
		return alarmIndicatorsNode;
	}
	
	
	
	
	public String changeManage(){
		
		String id = getParaValue("id");
		
		String value = getParaValue("value");
		
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNodeDao.changeMonfalgById(id, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmIndicatorsNodeDao.close();
		}
		
		return list();
	}
	
	
	public String setDefaultValue(){
		
		return null;
	}
	
	
	
	
}