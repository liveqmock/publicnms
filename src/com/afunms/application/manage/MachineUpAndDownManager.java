package com.afunms.application.manage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.ClusterDao;
import com.afunms.application.dao.UpAndDownLogDao;
import com.afunms.application.dao.UpAndDownMachineDao;
import com.afunms.application.model.Cluster;
import com.afunms.application.model.UpAndDownLog;
import com.afunms.application.model.UpAndDownMachine;
import com.afunms.application.util.RemoteClientInfo;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.CfgCmdFileDao;
import com.afunms.config.model.CfgCmdFile;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.task.ThreadPool;
import com.afunms.system.model.User;
import com.afunms.topology.dao.LineDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.model.HintLine;
import com.afunms.topology.util.ManageXmlOperator;
import com.afunms.topology.util.XmlOperator;

public class MachineUpAndDownManager extends BaseManager implements
		ManagerInterface {    

	public String execute(String action) {
		// TODO Auto-generated method stub
		if (action.equals("list")) {
			return list();
		}       
		if (action.equals("deleteLines")){
			return deleteLines(); 
		}
		if(action.equals("to_movement")){    
			return toMovement();
		}
		if(action.equals("to_static")){
			return toStatic();     
		}
		if (action.equals("ready_add")) {
			return readyAdd();
		}     
		if (action.equals("add")) {
			return add();
		}
		if (action.equals("ready_edit")) {
			return ready_edit();
		}
		if (action.equals("reboot")) {
			return reboot();
		}
		if (action.equals("delete")) {
			return delete();
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("refresh")) {
			return refresh();
		}
		if (action.equals("addCluster")) {
			return addCluster();
		}
		if (action.equals("ready_addCluster")) {
			return ready_addCluster();
		}
		if (action.equals("ready_editName")) {
			return ready_editName();
		}
		if (action.equals("clusterRefresh")) {
			return clusterRefresh();
		}
		if (action.equals("shutdown")) {
			return shutdown();
		}
		if (action.equals("saveLinkProperty")){
			return saveLinkProperty();
		}
		if (action.equals("clusterList")) {
			return clusterList();
		}
		//从服务器组中移除
		if (action.equals("delOrAdd")) {
			return delOrAdd();
		}
		//重启服务器组
		if (action.equals("rebootAll")) {
			return rebootAll();
		}
		//关闭服务器组
		if (action.equals("shutdownAll")) {
			return shutdownAll();
		}
		//改变服务器重启和关闭的顺序
		if (action.equals("updateSequence")) {
			return updateSequence();
		}
		if (action.equals("clusterDetailList")) {
			return clusterDetailList();
		}
		// 删除服务器组
		if (action.equals("deleteCluster")) {
			return deleteCluster();
		}
		// 修改服务器组
		if (action.equals("editCluster")) {
			return editCluster();
		}
		// 保存修改服务器组信息
		if (action.equals("saveCluster")) {
			return saveCluster();
		}
		// 操作审计信息
		if(action.equals("logList"))
		{
			return logList();
		}
		if(action.equals("loadFile")){
			return loadFile();
		}
		if (action.equals("save")){
			return save();
		}
		if (action.equals("reBuildSubMap")){
			return reBuildSubMap();			
		}
		
		return null;
	}
	private List<HintLine> digui(List<HintLine> hlist,String fileName){
		List<HintLine> returnlist = new ArrayList();
		for (int i=0; i<hlist.size(); i++){
			HintLine hintLine = hlist.get(i);
			LineDao lineDao = new LineDao();
			List<HintLine> returnlist1 = lineDao.findByFid(hintLine.getChildId()+"", fileName);
			if(returnlist1!=null&&returnlist1.size()>0){
				for (int j=0; j<returnlist1.size(); j++){
					HintLine vo = returnlist1.get(j);
					if(!returnlist.contains(vo)){
						returnlist.add(vo);
					}
				}
			}
		}
		return returnlist;
	}
	private Vector<UpAndDownMachine> getPoVector(List<HintLine> hlist,String fileName){
		Vector<UpAndDownMachine> v = new Vector<UpAndDownMachine>();
		for (int i=0; i<hlist.size(); i++){
			HintLine hintLine = hlist.get(i);
			Node tmp = PollingEngine.getInstance().getNodeByID(Integer.parseInt(hintLine.getChildId().substring(3)));
			if(tmp!=null){
				UpAndDownMachineDao dao = new UpAndDownMachineDao();
				UpAndDownMachine machine = null;
				try {
					machine = dao.findByIP(tmp.getIpAddress());
				} catch (Exception e1) {
					e1.printStackTrace();
				} finally {
					dao.close();
				}
				if(machine!=null){
					v.add(machine);
				}
			}
			//
		}
		return v;
	}
	//清除关机状态
	public String refreshState(String[] nodeArray,String xmlname){
		ShareData.setIsStopMachine("1");
		String returns = "error";
		try {
            if(xmlname!=null&&!"".equals(xmlname)&&!"null".equalsIgnoreCase(xmlname)){
                XmlOperator xopr = new XmlOperator();
                xopr.setFile(xmlname);
                xopr.init4updateXml();
                for(int i=0;i<nodeArray.length;i++){
//        			System.out.println(nodeArray[i]);
        			if(!xopr.isNodeExist(nodeArray[i])){
                    } else {
                    	xopr.updateNode(nodeArray[i], "state", "0");
                    }
        		}
                xopr.writeXml();
            }
        } catch (Exception e) {
            e.printStackTrace();
            returns = "error";
        } finally {
        	returns = "sucess";
        }
		return returns;
	}
//	并行关机异步请求
	public String complicatDown(String[] nodeArray,String fileName){
		String returns = "error";
		ShareData.setIsStopMachine("1");
//		System.out.println(fileName);
		boolean flag = false;
		try {
			Vector<UpAndDownMachine> v = new Vector<UpAndDownMachine>();
			for(int i=0;i<nodeArray.length;i++){
				if(nodeArray[i].indexOf("hin")!=-1)continue;
				updateXml(fileName,nodeArray[i],"1");//正在关机状态
				Node tmp = PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeArray[i].substring(3)));
				if(tmp!=null){
					UpAndDownMachineDao dao = new UpAndDownMachineDao();
					UpAndDownMachine machine = null;
					try {
						machine = dao.findByIP(tmp.getIpAddress());
					} catch (Exception e1) {
						e1.printStackTrace();
					} finally {
						dao.close();
					}
					if(machine!=null){
						v.add(machine);
					}
				}
			}
//			System.out.println("应急并行关机开始v.size()======="+v.size());
			if(v!=null&&v.size()>0){
				SysLogger.info("并行准备关机的设备个数："+v.size());
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				flag = test1(v,fileName);
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//				SysLogger.info("flag========"+flag);
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		if(flag){
			returns = "sucess";
		} else {
			returns = "error";
		}
		return returns;
		
	}
//	 删除策略关系
	private String deleteLines() {
		String id = getParaValue("id");
		String xml = getParaValue("xml");
        LineDao lineDao = new LineDao();
		// 更新xml
		if (!"".equals(id) && !"".equals(xml)) {
			ManageXmlOperator mxmlOpr = new ManageXmlOperator();
			mxmlOpr.setFile(xml);
			mxmlOpr.init4updateXml();
			if(lineDao.delete(id, xml)){
	        	NodeDependDao nodeDependDao = new NodeDependDao();
	        	System.out.println(nodeDependDao.isNodeExist(id, xml)+"==============");
	        	if(nodeDependDao.isNodeExist(id, xml)){
	        		nodeDependDao.deleteByIdXml(id, xml);
	        	} else {
	        		nodeDependDao.close();
	        	}
			mxmlOpr.deleteDemoLinesByID(id);
	        }
			mxmlOpr.writeXml();
		}
		return "/topology/machineUpAndDown/change.jsp?submapview=" + xml;
	}
//	保存链路样式
	private String saveLinkProperty() {

		String lineId = getParaValue("lineId");
		String fileName = getParaValue("xml");
		String link_name = getParaValue("link_name");
		String link_width = getParaValue("link_width");

		ManageXmlOperator mXmlOpr = new ManageXmlOperator();
		mXmlOpr.setFile(fileName);
		mXmlOpr.init4updateXml();
		if(mXmlOpr.isDemoLinkExist(lineId)){
			mXmlOpr.updateDemoLine(lineId, "lineWidth", link_width);
			if(!"".equals(link_name)){
				mXmlOpr.updateDemoLine(lineId, "lineInfo", link_name);	
			}
		} else {
			SysLogger.error("MachineUpAndDownManager.saveLinkProperty:"+"没有该链路");
		}
		mXmlOpr.writeXml();
		return null;
	}
	//终止关机
	public String stopDown(String fileName){
		String returns = "sucess";
		ShareData.setIsStopMachine("0");
		return returns;
	}
	//策略关机异步请求
	public String policyDown(String fileName){
		String returns = "error";
		ShareData.setIsStopMachine("1");
//		SysLogger.info(fileName);
		boolean flag = false;
		try {
			List list = new ArrayList();
			LineDao lineDao = new LineDao();
			List<HintLine> hlist = lineDao.findByFid("hin1", fileName);
			while(hlist!=null&&hlist.size()>0){
				Vector<UpAndDownMachine> v = getPoVector(hlist,fileName);
				list.add(v);
				hlist = digui(hlist,fileName);
			}
//			SysLogger.info("应急策略关机开始list.size()======="+list.size());
			if(list!=null&&list.size()>0){
				for(int i=0;i<list.size();i++){
					Vector<UpAndDownMachine> vector = (Vector)list.get(i);
					SysLogger.info("第"+(i+1)+"组准备关机的设备个数："+vector.size());
					for(int j=0;j<vector.size();j++){
						UpAndDownMachine machine = (UpAndDownMachine) vector.get(j);
						Node tmp = PollingEngine.getInstance().getNodeByIP(machine.getIpaddress());
						if(tmp!=null){
							boolean isExist = updateXml(fileName,"net"+tmp.getId(),"1");//正在关机状态
							if(!isExist){//判定设备是否存在于视图上，如果不存在则不关机
								vector.remove(machine);
							}
						}
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					flag = test1(vector,fileName);
//					System.out.println("flag========"+flag);
					if(!flag){
						break;
					}
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		if(flag){
			returns = "sucess";
		} else {
			returns = "error";
		}
		return returns;
		
	}
	private boolean test1(Vector<UpAndDownMachine> vector,String xmlname) {
		try {
			for (int i=0; i<vector.size(); i++){
				UpAndDownMachine machine = (UpAndDownMachine) vector.get(i);
				downOne(machine,xmlname);
				machine = null;
			} 
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean test(Vector<UpAndDownMachine> vector,String xmlname) {
		try {
			ThreadPool threadPool = new ThreadPool(vector.size());			
			for (int i=0; i<vector.size(); i++){
				UpAndDownMachine machine = (UpAndDownMachine) vector.get(i);
				threadPool.runTask(createTask(machine,xmlname));
				machine = null;
			}  
			threadPool.join();
			threadPool.close();
			threadPool = null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private Runnable createTask(final UpAndDownMachine machine,final String xmlname) {
		return new Runnable() {
	        public void run() {
				try{
//					System.out.println("machine.getIpaddress()==========="+machine.getIpaddress());
					downOne(machine,xmlname);
				}catch(Exception e){
					e.printStackTrace();
				}
	        }
	    };
	}
	private boolean updateXml(String xmlname,String nodeid,String value){
        try {
            if(xmlname!=null&&!"".equals(xmlname)&&!"null".equalsIgnoreCase(xmlname)){
                XmlOperator xopr = new XmlOperator();
                xopr.setFile(xmlname);
                xopr.init4updateXml();
                if(!xopr.isNodeExist(nodeid)){
                	return false;
                } else {
                	xopr.updateNode(nodeid, "state", value);
                }
                xopr.writeXml();
                System.out.println(xmlname+"====="+nodeid+"===state==="+value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
        }
		return true;
	}  
	private void deleteXml(String xmlname,String ip){
        try {
        	Node tmp = PollingEngine.getInstance().getNodeByIP(ip);
            if(xmlname!=null&&!"".equals(xmlname)&&!"null".equalsIgnoreCase(xmlname)){
                XmlOperator xopr = new XmlOperator();
                xopr.setFile(xmlname);
                xopr.init4updateXml();
                if(xopr.isNodeExist("net"+tmp.getId())){
                	xopr.deleteNodeById("net"+tmp.getId());
                }
                xopr.writeXml();
                System.out.println(xmlname+"====="+ip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
	} 
	public void downOne(UpAndDownMachine machine,String xmlname) {
		SysLogger.info("============machine.getIpaddress()==========="+machine.getIpaddress()+"====="+ShareData.getIsStopMachine());
		Node tmp = PollingEngine.getInstance().getNodeByIP(machine.getIpaddress());
		if(tmp!=null&&"1".equals(ShareData.getIsStopMachine())){
			Integer[] packet=null;
			boolean flag=true;
//			PingUtil pingU=new PingUtil(machine.getIpaddress());
//			packet=pingU.ping();
//			if (packet[0]!=null) {
//			}else {
//				updateXml(xmlname,"net"+tmp.getId(),"5");//已关机状态
//			}
			RemoteClientInfo info = ShareData.getIp_clientInfoHash().get(machine.getIpaddress());
			if (info != null) {
				UpAndDownMachineDao udao = new UpAndDownMachineDao();
				try {
					//关机执行前命令操作
					try {
						String powerOffBefore = machine.getPowerOffBefore();
						if(powerOffBefore!=null&&!"".equals(powerOffBefore)&&!"null".equalsIgnoreCase(powerOffBefore)){
							powerOffBefore = powerOffBefore.substring(0,powerOffBefore.lastIndexOf(","));
							String[] offBefore = powerOffBefore.split(",");
							for(int i=0;i<offBefore.length;i++){
								CfgCmdFileDao ccfd = new CfgCmdFileDao();
								CfgCmdFile ccf = (CfgCmdFile)ccfd.findByID(offBefore[i]);
								ccfd.close();
								String filePath = ccf.getFilename();
								FileReader fr=null;
								try {
									fr = new FileReader(filePath);
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
								BufferedReader br = new BufferedReader(fr);
								String lineStr="";
								StringBuffer content = new StringBuffer();
								try {
									while (null != (lineStr = br.readLine())) {
										content.append(lineStr + "\r\n");
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
								
								//execmd content
								info.executeCmd(content+"");
							}
						}
						//关机操作
						String serverType=machine.getServerType();
						if (serverType.equals("windows")) {
							info.executeCmd("shutdown -s -t 1");//shutdown -s –m \\abc -t 30 
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
					} catch (Exception e1) {
						flag = false;
						e1.printStackTrace();
					}
					try {
						Long time=3*60*1000L;	//3分钟
						Date date=new Date();	//当前时间
						while (flag) {
							PingUtil pingU=new PingUtil(machine.getIpaddress());
							packet=pingU.ping();
							if (packet[0]==0) {	//ping不通，关机成功
								break;
							}else {
								Thread.sleep(5000);
							}
							Date currentDate=new Date();
							if(currentDate.getTime()-date.getTime()>time){
								flag=false;	//超过一定时间，认为关机失败
								break;
							}
						}
					} catch (InterruptedException e) {
						flag = false;
						e.printStackTrace();
					}
					
					info.closeConnection();
					
					downLog(machine);
					ShareData.getIp_clientInfoHash().remove(machine.getIpaddress());
				} catch (Exception e) {
					e.printStackTrace();
					flag = false;
					//updateXml(xmlname,"net"+tmp.getId(),"3");//关机失败
				}
//				PingUtil pingU=new PingUtil(machine.getIpaddress());
//				packet=pingU.ping();
//				if (packet[0]==0) {
//					updateXml(xmlname,"net"+tmp.getId(),"3");//关机失败
//				}
				
				if(flag){
					updateXml(xmlname,"net"+tmp.getId(),"2");//正常关机
					machine.setMonitorStatus(0);
				}else{
					updateXml(xmlname,"net"+tmp.getId(),"3");//关机失败
					machine.setMonitorStatus(1);
				}
				machine.setLasttime(new Timestamp(new Date().getTime()));
				
				try {
					udao.addBatchUpdateAllTime(machine);
					udao.executeBatch();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					udao.close();
				}
			} else {
				System.out.println(machine.getIpaddress()+"========设备连接异常");
				updateXml(xmlname,"net"+tmp.getId(),"4");//设备连接异常.....
			}
		} else {
			updateXml(xmlname,"net"+tmp.getId(),"3");//关机失败
		}
//		System.out.println(machine.getIpaddress()+"========");
	}

	private void downLog(UpAndDownMachine machine) {
		UpAndDownLog log = new UpAndDownLog();
		//System.out.println("session:"+request.getSession());
		//User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		log.setOper_user("admin");
		log.setOperid("admin");
		log.setIp_address(machine.getIpaddress());
		log.setOper_time(new Timestamp(new Date().getTime()));
		log.setAction(2);//1重启，2关机
		UpAndDownLogDao logDao = new UpAndDownLogDao();
		try {
			logDao.save(log);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			logDao.close();
		}
	}
	public String reBuildSubMap(){
		String xmlname = getParaValue("xml");
		SysLogger.info("###### 加载策略组设备 ######"+xmlname);
		try {
			createXml(xmlname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private void createXml(String xmlname){

       DBManager db = null;
       ResultSet rs = null;
       db = new DBManager();
       try {
            if(xmlname!=null&&!"".equals(xmlname)&&!"null".equalsIgnoreCase(xmlname)){
                XmlOperator xopr = new XmlOperator();
                xopr.setFile(xmlname);
//                xopr.init4updateXml();
                xopr.init4createXml();
                rs = db.executeQuery("select * from nms_remote_up_down_cluster where xml='"+xmlname+"'");
                if(rs.next()){
                	String id = rs.getString("id");
                	rs = db.executeQuery("select * from nms_remote_up_down_machine where clusterid="+id);
                	int index = 0;
                	while(rs.next()){
                		index++;
                		Host host = (Host) PollingEngine.getInstance().getNodeByIP(rs.getString("ipaddress"));
                		if(host!=null){
                			String alias = host.getAlias()+"("+host.getIpAddress()+")";
    	                    if(!xopr.isNodeExist("net"+host.getId())){
    	                        xopr.addPolicyNode("net"+host.getId(), host.getCategory(), null, host.getIpAddress(), alias,String.valueOf(index * 30), "15");
    	                    } 
                		}
	                }
                	
                	rs = db.executeQuery("select * from nms_hint_line where xmlfile='"+xmlname+"'");
                	while(rs.next()){
                		xopr.addPolicyLine(rs.getInt("id"),rs.getString("line_id"),rs.getString("father_id"), rs.getString("child_id"),rs.getInt("width")+"");
                	}
	                xopr.createXml();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try {
    			ManageXmlOperator mXmlOpr = new ManageXmlOperator();
    			mXmlOpr.setFile(xmlname);
    			mXmlOpr.init4editNodes();   
    			if(!mXmlOpr.isNodeExist("hin1")){
    				mXmlOpr.addPolicyDemoNode("hin1", "begin", "image/topo/begin.gif", "启始节点", String.valueOf(1 * 30), "15");
    			}
    			mXmlOpr.writeXml();
    		} catch (Exception e1) {
    			e1.printStackTrace();
    		}
        	if(rs != null){
 			   try{
 				   rs.close();
 			   }catch(Exception e){
 				   
 			   }
 		   }
            db.close();
        }
	}
//	 子图上的保存按钮，保存子图上图元的位置
	private String save() {
		String fileName = request.getParameter("filename");
		String xmlString = request.getParameter("hidXml");// 从showMap.jsp传入的数据信息字符串

		xmlString = xmlString.replace("<?xml version=\"1.0\"?>",
				"<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		XmlOperator xmlOpr = new XmlOperator();
		xmlOpr.setFile(fileName);
		xmlOpr.saveImage(xmlString);
		request.setAttribute("fresh", "fresh");
		request.setAttribute("xml",fileName);
		return "/topology/machineUpAndDown/save.jsp";
	}
	
    //加载命令文件
	private String loadFile() {
		String scriptType = this.getParaValue("scriptType");
		CfgCmdFileDao dao=new CfgCmdFileDao();
        List list=dao.loadAll();
        request.setAttribute("list", list);
        request.setAttribute("scriptType", scriptType);
		return "/config/vpntelnet/machineUpAndDown/loadFile.jsp";
	}
	
	private String logList()
	{
		UpAndDownLogDao dao = new UpAndDownLogDao();
		this.setTarget("/config/vpntelnet/machineUpAndDown/logList.jsp");
		return list(dao);
	}

	private String shutdown() {
		int id = this.getParaIntValue("id");
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		UpAndDownMachine machine = (UpAndDownMachine) dao.findByID(id + "");
		dao.close();
		RemoteClientInfo info = ShareData.getIp_clientInfoHash().get(machine.getIpaddress());
		if (info != null) {
			//关机执行前命令操作
			String powerOffBefore = machine.getPowerOffBefore();
			if(powerOffBefore!=null&&!powerOffBefore.equals("")){
				powerOffBefore = powerOffBefore.substring(0,powerOffBefore.lastIndexOf(","));
				String[] offBefore = powerOffBefore.split(",");
				for(int i=0;i<offBefore.length;i++){
					CfgCmdFileDao ccfd = new CfgCmdFileDao();
					CfgCmdFile ccf = (CfgCmdFile)ccfd.findByID(offBefore[i]);
					ccfd.close();
					String filePath = ccf.getFilename();
					FileReader fr=null;
					try {
						fr = new FileReader(filePath);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					BufferedReader br = new BufferedReader(fr);
					String lineStr="";
					StringBuffer content = new StringBuffer();
					try {
						while (null != (lineStr = br.readLine())) {
							content.append(lineStr + "\r\n");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					//execmd content
					info.executeCmd(content+"");
				}
			}
			//关机操作
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
		
			info.closeConnection();
			machine.setMonitorStatus(0);
			machine.setLasttime(new Timestamp(new Date().getTime()));
			UpAndDownMachineDao udao = new UpAndDownMachineDao();
			udao.updateWithTime(machine);
			udao.close();
			//gzm add beign
			UpAndDownLog log = new UpAndDownLog();
			User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
			log.setOper_user(user.getName());
			log.setOperid(user.getId()+"");
			log.setIp_address(machine.getIpaddress());
			log.setOper_time(new Timestamp(new Date().getTime()));
			log.setAction(2);//1重启，2关机
			UpAndDownLogDao logDao = new UpAndDownLogDao();
			logDao.save(log);
			logDao.close();
	          //gzm add end 
			ShareData.getIp_clientInfoHash().remove(machine.getIpaddress());
		}
		return list();
	}


/**
 * 改变服务器重启和关闭的顺序
 * @return
 */
	public String updateSequence() {
		DBManager dbManager = new DBManager();
		String sql = "";
		int id = 0;
		int sequence = 0;
		String ids = getParaValue("ids");
		String values = getParaValue("values");
		String[] idsArr = new String[ids.split(".").length];
		String[] valuesArr = new String[values.split(".").length];
		idsArr = ids.split("\\.");
		valuesArr = values.split("\\.");

		try {

			for (int i = 0; i < idsArr.length; i++) {
				id = Integer.parseInt(idsArr[i]);
				sequence = Integer.parseInt(valuesArr[i]);
				sql = "update nms_remote_up_down_machine set sequence="
						+ sequence + " where id=" + id;
				dbManager.addBatch(sql);
			}
			dbManager.executeBatch();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			dbManager.close();
		}

		return clusterDetailList();
	}

	private String refresh() {
		return list();
	}

	private String clusterRefresh() {
		return clusterDetailList();
	}

	private String delete() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			UpAndDownMachineDao dao = new UpAndDownMachineDao();
			dao.delete(ids);
			dao.close();
		}
		return list();
	}

	// 从服务器集群中移到默认组中
	private String delOrAdd() {
		//String type = getParaValue("type");
		String[] ids = getParaArrayValue("checkbox");
		DBManager db = new DBManager();
		if (ids != null && ids.length > 0) {
			boolean result = false;
			try {

				for (int i = 0; i < ids.length; i++)
					db.addBatch("update nms_remote_up_down_machine set clusterId=1 where id=" + ids[i]);

				db.executeBatch();
				result = true;
			} catch (Exception ex) {
				SysLogger.error("BaseDao.delete()", ex);
				result = false;
			}finally{
				db.close();
			}
		}
		return clusterDetailList();
	}

	private String ready_edit() {
		int id = this.getParaIntValue("id");
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		UpAndDownMachine machine = (UpAndDownMachine) dao.findByID(id + "");
		dao.close();
		ClusterDao clusterdao = new ClusterDao();
		List list = clusterdao.loadAll();
		request.setAttribute("clusterList", list);
		request.setAttribute("machine", machine);
		return "/config/vpntelnet/machineUpAndDown/edit.jsp";
	}

	private String update() {
		int id = this.getParaIntValue("id");
		String lasttime = this.getParaValue("lasttime");
		String ipaddress = getParaValue("ipaddress");
		String name = this.getParaValue("name");
		String serverType = this.getParaValue("serverType");
		String username = this.getParaValue("user");
		String passwd = this.getParaValue("password");
		int isJoin = this.getParaIntValue("isJoin");
		String powerOffBefore = this.getParaValue("powerOffBefore");
		String powerOnAfter = this.getParaValue("powerOnAfter");
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		UpAndDownMachine machine = new UpAndDownMachine();
		machine.setId(id);
		machine.setName(name);
		machine.setIpaddress(ipaddress);
		machine.setLasttime(new Timestamp(new Date().getTime()));
		machine.setServerType(serverType);
		machine.setUsername(username);
		machine.setPasswd(passwd);
		machine.setIsJoin(isJoin);
		machine.setPowerOffBefore(powerOffBefore);
		machine.setPowerOnAfter(powerOnAfter);
		dao.update(machine);
		dao.close();
		return list();
	}

	private String edit() {
		return "";
	}

	private String reboot() {
		int id = this.getParaIntValue("id");
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		UpAndDownMachine machine = (UpAndDownMachine) dao.findByID(id + "");
		dao.close();
		RemoteClientInfo info = ShareData.getIp_clientInfoHash().get(
				machine.getIpaddress());
		if (info != null) {
			//关机执行前命令操作
			String powerOffBefore = machine.getPowerOffBefore();
			if(powerOffBefore!=null&&!powerOffBefore.equals("")){
				powerOffBefore = powerOffBefore.substring(0,powerOffBefore.lastIndexOf(","));
				String[] offBefore = powerOffBefore.split(",");
				for(int i=0;i<offBefore.length;i++){
					CfgCmdFileDao ccfd = new CfgCmdFileDao();
					CfgCmdFile ccf = (CfgCmdFile)ccfd.findByID(offBefore[i]);
					ccfd.close();
					String filePath = ccf.getFilename();
					FileReader fr=null;
					try {
						fr = new FileReader(filePath);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					BufferedReader br = new BufferedReader(fr);
					String lineStr="";
					StringBuffer content = new StringBuffer();
					try {
						while (null != (lineStr = br.readLine())) {
							content.append(lineStr);
							info.executeCmd(content+"");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					//execmd content
//					info.executeCmd(content+"");
				}
			}
			//开机操作
			String serverType=machine.getServerType();
			if (serverType.equals("windows")) {
				info.executeCmd("shutdown -r -f -t 0");
			}
			if (serverType.equals("linux")||serverType.equals("unix")) {
				info.executeCmd(" shutdown -r  now");
			}
			if (serverType.equals("aix")) {
				info.executeCmd("shutdown –Fr");
			}
			//开机后操作
			String powerOnAfter = machine.getPowerOnAfter();
			if(powerOnAfter!=null&&!powerOnAfter.equals("")){
				powerOnAfter = powerOnAfter.substring(0,powerOnAfter.lastIndexOf(","));
				String[] onAfter = powerOnAfter.split(",");
				for(int i=0;i<onAfter.length;i++){
					CfgCmdFileDao ccfd = new CfgCmdFileDao();
					CfgCmdFile ccf = (CfgCmdFile)ccfd.findByID(onAfter[i]);
					ccfd.close();
					String filePath = ccf.getFilename();
					FileReader fr=null;
					try {
						fr = new FileReader(filePath);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					BufferedReader br = new BufferedReader(fr);
					String lineStr="";
					StringBuffer content = new StringBuffer();
					try {
						while (null != (lineStr = br.readLine())) {
							content.append(lineStr);
							info.executeCmd(content+"");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					//execmd content
//					info.executeCmd(content+"");
				}
			}
			
			info.closeConnection();
			machine.setMonitorStatus(0);
			machine.setLasttime(new Timestamp(new Date().getTime()));
			UpAndDownMachineDao udao = new UpAndDownMachineDao();
			udao.updateWithTime(machine);
			udao.close();
			//gzm add beign
			UpAndDownLog log = new UpAndDownLog();
			User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
			log.setOper_user(user.getName());
			log.setOperid(user.getId()+"");
			log.setIp_address(machine.getIpaddress());
			log.setOper_time(new Timestamp(new Date().getTime()));
			log.setAction(1);//1重启，2关机
			UpAndDownLogDao logDao = new UpAndDownLogDao();
			logDao.save(log);
			logDao.close();
              //gzm add end 
			ShareData.getIp_clientInfoHash().remove(machine.getIpaddress());
		}
		return list();
	}

	private String rebootAll() {
		
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		int id = getParaIntValue("clusterId");
		List list = dao.loadClusterList(id);
		dao.close();
		UpAndDownMachineDao udao = new UpAndDownMachineDao();
		int second=0;
		for (int j = 0; j < list.size(); j++) {
			UpAndDownMachine machine = (UpAndDownMachine) list.get(j);
			RemoteClientInfo info = ShareData.getIp_clientInfoHash().get(
					machine.getIpaddress());
			 second=j*5;
			if (info != null) {
				//关机执行前命令操作
				String powerOffBefore = machine.getPowerOffBefore();
				if(powerOffBefore!=null&&!powerOffBefore.equals("")){
					powerOffBefore = powerOffBefore.substring(0,powerOffBefore.lastIndexOf(","));
					String[] offBefore = powerOffBefore.split(",");
					for(int i=0;i<offBefore.length;i++){
						CfgCmdFileDao ccfd = new CfgCmdFileDao();
						CfgCmdFile ccf = (CfgCmdFile)ccfd.findByID(offBefore[i]);
						ccfd.close();
						String filePath = ccf.getFilename();
						FileReader fr=null;
						try {
							fr = new FileReader(filePath);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						BufferedReader br = new BufferedReader(fr);
						String lineStr="";
						StringBuffer content = new StringBuffer();
						try {
							while (null != (lineStr = br.readLine())) {
								content.append(lineStr);
								info.executeCmd(content+"");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						//execmd content
//						info.executeCmd(content+"");
					}
				}
				//开机操作
				String serverType=machine.getServerType();
				if (serverType.equals("windows")) {
					info.executeCmd("shutdown -r -f -t 0");
				}
				if (serverType.equals("linux")||serverType.equals("unix")) {
					info.executeCmd(" shutdown -r  now");
				}
				if (serverType.equals("aix")) {
					info.executeCmd("shutdown –Fr");
				}
				//开机后操作
				String powerOnAfter = machine.getPowerOnAfter();
				if(powerOnAfter!=null&&!powerOnAfter.equals("")){
					powerOnAfter = powerOnAfter.substring(0,powerOnAfter.lastIndexOf(","));
					String[] onAfter = powerOnAfter.split(",");
					for(int i=0;i<onAfter.length;i++){
						CfgCmdFileDao ccfd = new CfgCmdFileDao();
						CfgCmdFile ccf = (CfgCmdFile)ccfd.findByID(onAfter[i]);
						ccfd.close();
						String filePath = ccf.getFilename();
						FileReader fr=null;
						try {
							fr = new FileReader(filePath);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						BufferedReader br = new BufferedReader(fr);
						String lineStr="";
						StringBuffer content = new StringBuffer();
						try {
							while (null != (lineStr = br.readLine())) {
								content.append(lineStr + "\r\n");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						//execmd content
						info.executeCmd(content+"");
					}
				}
				
				info.closeConnection();
				machine.setMonitorStatus(0);
				machine.setLasttime(new Timestamp(new Date().getTime()));
				
				udao.addBatchUpdateAllTime(machine);
				
				//gzm add beign
				UpAndDownLog log = new UpAndDownLog();
				User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
				log.setOper_user(user.getName());
				log.setOperid(user.getId()+"");
				log.setIp_address(machine.getIpaddress());
				log.setOper_time(new Timestamp(new Date().getTime()));
				log.setAction(1);//1重启，2关机
				UpAndDownLogDao logDao = new UpAndDownLogDao();
				logDao.save(log);
				logDao.close();
	              //gzm add end
				ShareData.getIp_clientInfoHash().remove(machine.getIpaddress());
			}
		}
	       udao.executeBatch();
	       udao.close();
	       
		return clusterDetailList();
	}
	private String shutdownAll() {
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		int id = getParaIntValue("clusterId");
		List list = dao.loadClusterList(id);
		dao.close();
		UpAndDownMachineDao udao = new UpAndDownMachineDao();
		int second=0;
		boolean flag=true;
		Integer[] packet=null;
		for (int j = 0; j < list.size(); j++) {
			UpAndDownMachine machine = (UpAndDownMachine) list.get(j);
			RemoteClientInfo info = ShareData.getIp_clientInfoHash().get(machine.getIpaddress());
			second=j*5;
			if (info != null) {
				//关机执行前命令操作
				String powerOffBefore = machine.getPowerOffBefore();
				if(powerOffBefore!=null&&!powerOffBefore.equals("")){
					powerOffBefore = powerOffBefore.substring(0,powerOffBefore.lastIndexOf(","));
					String[] offBefore = powerOffBefore.split(",");
					for(int i=0;i<offBefore.length;i++){
						CfgCmdFileDao ccfd = new CfgCmdFileDao();
						CfgCmdFile ccf = (CfgCmdFile)ccfd.findByID(offBefore[i]);
						ccfd.close();
						String filePath = ccf.getFilename();
						FileReader fr=null;
						try {
							fr = new FileReader(filePath);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						BufferedReader br = new BufferedReader(fr);
						String lineStr="";
						StringBuffer content = new StringBuffer();
						try {
							while (null != (lineStr = br.readLine())) {
								content.append(lineStr + "\r\n");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						//execmd content
						info.executeCmd(content+"");
					}
				}
				//关机操作
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
				
				info.closeConnection();
				machine.setMonitorStatus(0);
				machine.setLasttime(new Timestamp(new Date().getTime()));
				
				udao.addBatchUpdateAllTime(machine);
				
				//gzm add beign
				UpAndDownLog log = new UpAndDownLog();
				User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
				log.setOper_user(user.getName());
				log.setOperid(user.getId()+"");
				log.setIp_address(machine.getIpaddress());
				log.setOper_time(new Timestamp(new Date().getTime()));
				log.setAction(2);//1重启，2关机
				UpAndDownLogDao logDao = new UpAndDownLogDao();
				logDao.save(log);
				logDao.close();
		          //gzm add end 
	
				ShareData.getIp_clientInfoHash().remove(machine.getIpaddress());
			}
		}
          udao.executeBatch();
          udao.close();
		return clusterDetailList();
	}
	private String add() {
		String ipaddress = getParaValue("ipaddress");
		String name = this.getParaValue("name");
		String serverType = this.getParaValue("serverType");
		String username = this.getParaValue("user");
		String passwd = this.getParaValue("password");
		int clusterId = this.getParaIntValue("clusterId");
		String powerOffBefore = this.getParaValue("powerOffBefore");
		String powerOnAfter = this.getParaValue("powerOnAfter");
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		UpAndDownMachine machine = new UpAndDownMachine();
		machine.setName(name);
		machine.setIpaddress(ipaddress);
		machine.setLasttime(new Timestamp(new Date().getTime()));
		machine.setServerType(serverType);
		machine.setUsername(username);
		machine.setPasswd(passwd);
		machine.setPowerOffBefore(powerOffBefore);
		machine.setPowerOnAfter(powerOnAfter);
		Object re = ShareData.getIp_clientInfoHash().get(ipaddress);
		if(re == null)
		{
			machine.setMonitorStatus(0);
		}
		else
			machine.setMonitorStatus(1);
		if (clusterId == 0) {
			machine.setClusterId(0);
		} else {
			machine.setClusterId(clusterId);	
		}
		
		dao.save(machine);
		dao.close();
		return list();
	}

	private String addCluster() {
		ClusterDao clusterDao = new ClusterDao();
		String name = getParaValue("name");
		String bid = getParaValue("bid");
		String serverType = this.getParaValue("serverType");
		String xmlName = SysUtil.getCurrentLongTime() + "policy.jsp";
		Cluster cluster = new Cluster();
		cluster.setName(name);
		cluster.setXml(xmlName);
		cluster.setBid(bid);
		if (serverType.equals("mix")) {
			cluster.setServerType("混合");
		} else {
			cluster.setServerType(serverType);
		}
		cluster.setCreatetime(new Timestamp(new Date().getTime()));
		clusterDao.save(cluster);

		try {
			XmlOperator xmlOpr = new XmlOperator();
			xmlOpr.setFile(cluster.getXml());
			xmlOpr.init4createXml();
			xmlOpr.createXml();
			xmlOpr.writeXml();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clusterList();
	}

//	public String toMovement(){
//		UpAndDownMachineDao dao = new UpAndDownMachineDao();
//		List list = dao.loadAll();
//		int[] steps = new int[list.size()];
//		String[] ips=new String[list.size()];
//		for(int i=0;i<list.size();i++){
//			UpAndDownMachine v = (UpAndDownMachine)list.get(i);
//			steps[i] = 0;
//		}
//		session.setAttribute("steps", steps);
//		return "/config/vpntelnet/machineUpAndDown/showMovement.jsp";
//	}
	
	public String toMovement(){
		int id = this.getParaIntValue("id");
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		List list = dao.loadClusterList(id);
		dao.close();
		int[] steps = new int[list.size()];
		for(int i=0;i<list.size();i++){
			UpAndDownMachine v = (UpAndDownMachine)list.get(i);
			steps[i] = 0;
		}
		session.setAttribute("steps", steps);
		session.setAttribute("clusterId", id);
		return "/config/vpntelnet/machineUpAndDown/showMovement.jsp";
	}
	
	public String toStatic(){
		return "/config/vpntelnet/machineUpAndDown/showStatic.jsp";
	}
	
	public String readyAdd() {
		ClusterDao dao = new ClusterDao();
		List list = dao.loadAll();
	//	Cluster cluster=(Cluster)list.get(1);
		request.setAttribute("clusterList", list);
		return "/config/vpntelnet/machineUpAndDown/add.jsp";
	}

	private String list() {
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		List list = dao.loadAll();
		dao.close();
		request.setAttribute("machineList", list);
		return "/config/vpntelnet/machineUpAndDown/list.jsp";
	}

	private String clusterList() {
		ClusterDao dao = new ClusterDao();
		List list = dao.loadAll();
		UpAndDownMachineDao machineDao=new UpAndDownMachineDao();
		HashMap<Integer, Integer> map=machineDao.countById();
		machineDao.close();
		request.setAttribute("totalMap", map);
		request.setAttribute("clusterList", list);
		
		return "/config/vpntelnet/machineUpAndDown/cluster.jsp";
	}

	// 创建服务器名称
	private String ready_addCluster() {
		return "/config/vpntelnet/machineUpAndDown/addCluster.jsp";
	}

	// 修改所属服务器组
	private String ready_editName() {
		int id = getParaIntValue("id");
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		Cluster cluster = dao.findDataById(id);
		ClusterDao clusterDao = new ClusterDao();
		List list = clusterDao.loadAll();
		request.setAttribute("id", id);
		request.setAttribute("cluster", cluster);
		request.setAttribute("clusterList", list);
		return "/config/vpntelnet/machineUpAndDown/editClusterItem.jsp";
	}

	// 服务器组的详细列表
	private String clusterDetailList() {
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		int id = getParaIntValue("id");
		List list = dao.loadClusterList(id);
		request.setAttribute("machineList", list);
		request.setAttribute("clusterId", id);
		return "/config/vpntelnet/machineUpAndDown/clusterList.jsp";
	}

	// 删除服务器组及详细列表
	private String deleteCluster() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			ClusterDao clusterDao = new ClusterDao();
			for(int i=0;i<ids.length;i++){
				try {
					Cluster cluster = (Cluster) clusterDao.findByID(ids[i]);
					XmlOperator xmlOpr = new XmlOperator();
					xmlOpr.setFile(cluster.getXml());
					xmlOpr.deleteXml();// 删除文件夹下对应子图的xml文件
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				clusterDao.delete(ids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				clusterDao.close();
			}
			// 修改服务器组
			UpAndDownMachineDao dao = new UpAndDownMachineDao();
			try {
				dao.updateClusterId(ids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}
		return clusterList();
	}
	//修改服务器组
	public String editCluster(){
		int id = this.getParaIntValue("id");
		ClusterDao dao = new ClusterDao();
		Cluster cluster = (Cluster) dao.findByID(id + "");
		dao.close();
		request.setAttribute("cluster", cluster);
		return "/config/vpntelnet/machineUpAndDown/editCluster.jsp";
	}
	//保存修改服务器组信息
	public String saveCluster(){

		ClusterDao clusterDao = new ClusterDao();
		int id = getParaIntValue("id");
		String name = getParaValue("name");
		String serverType = this.getParaValue("serverType");
		String bid = getParaValue("bid");
		Cluster cluster = new Cluster();
		cluster.setId(id);
		cluster.setName(name);
		cluster.setBid(bid);
		if (serverType.equals("mix")) {
			cluster.setServerType("混合");
		} else {
			cluster.setServerType(serverType);
		}
		cluster.setCreatetime(new Timestamp(new Date().getTime()));
		clusterDao.update(cluster);

		return clusterList();
	
	}
}