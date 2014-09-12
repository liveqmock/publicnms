package com.afunms.alarm.manage;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.dao.AlarmPortDao;
import com.afunms.alarm.dao.AlarmWayDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmPort;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;

import com.afunms.system.model.User;
import com.afunms.temp.dao.InterfaceTempDao;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.dao.HostNodeDao;

public class AlarmPortManager extends BaseManager implements ManagerInterface{

	public String execute(String action) {
		if(action.equals("list")){
			return list();
	    }
		if (action.equals("edit")) {
			return edit();
		}
		if (action.equals("update")) {
			return update();
		}
		return null;
	}
   public String list(){
		
		String jsp = "/alarm/port/list.jsp";
		
		String ipaddress = getParaValue("ipaddress");
		String id = getParaValue("nodeid");
		fromLastToAlarmPort();
		AlarmPortDao dao = new AlarmPortDao();
		request.setAttribute("nodeid", id);
		List list = new ArrayList();
		try {
			list = dao.loadByIpaddress(ipaddress);
		} catch (Exception e) {

		} finally {
			dao.close();
		}
		request.setAttribute("list", list);
		request.setAttribute("ipaddress", ipaddress);
		
		
		return jsp;
	}
   
   public String edit() {
	   String jsp = "/alarm/port/edit.jsp";
		String id = getParaValue("id");
		AlarmPortDao dao = new AlarmPortDao();
		AlarmPort alarmPortNode = null;
		try {
			alarmPortNode = (AlarmPort)dao.findByID(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		String nodeid = getParaValue("nodeid");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		request.setAttribute("nodeid", nodeid);
		Hashtable alarmWayHashtable = new Hashtable();
		
		if(alarmPortNode!=null){
		//	nodeid = alarmPortNode.getId();
			type = alarmPortNode.getType();
			subtype = alarmPortNode.getSubtype();
		
			AlarmWayDao alarmWayDao = null;
			if(alarmPortNode.getWayin1() != null && !alarmPortNode.getWayin1().equals("")){
				alarmWayDao = new AlarmWayDao();
				try {
					String wayin = alarmPortNode.getWayin1();
					if (wayin != null && wayin.endsWith(",")) {
						wayin = wayin.substring(0, wayin.length() - 1);
					}
					AlarmWay alarmWayin1 = (AlarmWay)alarmWayDao.findByID(wayin);
					if(alarmWayin1!=null){
						alarmWayHashtable.put("wayin1", alarmWayin1);
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					alarmWayDao.close();
				}
			}
			
			
			if(alarmPortNode.getWayin2() != null && !alarmPortNode.getWayin2().equals("")){
				alarmWayDao = new AlarmWayDao();
				try {
					String wayin = alarmPortNode.getWayin2();
					if (wayin != null && wayin.endsWith(",")) {
						wayin = wayin.substring(0, wayin.length() - 1);
					}
					AlarmWay alarmWayin2 = (AlarmWay)alarmWayDao.findByID(wayin);
					if(alarmWayin2!=null){
						alarmWayHashtable.put("wayin2", alarmWayin2);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					alarmWayDao.close();
				}
			}
			
			if(alarmPortNode.getWayin3() != null && !alarmPortNode.getWayin3().equals("")){
				alarmWayDao = new AlarmWayDao();
				try {
					String wayin = alarmPortNode.getWayin3();
					if (wayin != null && wayin.endsWith(",")) {
						wayin = wayin.substring(0, wayin.length() - 1);
					}
					AlarmWay alarmWayin3 = (AlarmWay)alarmWayDao.findByID(wayin);
					if(alarmWayin3!=null){
						alarmWayHashtable.put("wayin3", alarmWayin3);
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					alarmWayDao.close();
				}
			}
			if(alarmPortNode.getWayout1() != null && !alarmPortNode.getWayout1().equals("")){
				alarmWayDao = new AlarmWayDao();
				try {
					String wayout = alarmPortNode.getWayout1();
					if (wayout != null && wayout.endsWith(",")) {
						wayout = wayout.substring(0, wayout.length() - 1);
					}
					AlarmWay alarmWayout1 = (AlarmWay)alarmWayDao.findByID(wayout);
					if(alarmWayout1!=null){
						alarmWayHashtable.put("wayout1", alarmWayout1);
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					alarmWayDao.close();
				}
			}
			
			
			if(alarmPortNode.getWayout2() != null && !alarmPortNode.getWayout2().equals("")){
				alarmWayDao = new AlarmWayDao();
				try {
					String wayout = alarmPortNode.getWayout2();
					if (wayout != null && wayout.endsWith(",")) {
						wayout = wayout.substring(0, wayout.length() - 1);
					}
					AlarmWay alarmWayout2 = (AlarmWay)alarmWayDao.findByID(wayout);
					if(alarmWayout2!=null){
						alarmWayHashtable.put("wayout2", alarmWayout2);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					alarmWayDao.close();
				}
			}
			
			if(alarmPortNode.getWayout3() != null && !alarmPortNode.getWayout3().equals("")){
				alarmWayDao = new AlarmWayDao();
				try {
					//去掉逗号
					String wayout = alarmPortNode.getWayout3();
					if (wayout != null && wayout.endsWith(",")) {
						wayout = wayout.substring(0, wayout.length() - 1);
					}
					AlarmWay alarmWayout3 = (AlarmWay)alarmWayDao.findByID(wayout);
					if(alarmWayout3!=null){
						alarmWayHashtable.put("wayout3", alarmWayout3);
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					alarmWayDao.close();
				}
			}
		}
		request.setAttribute("alarmWayHashtable", alarmWayHashtable);
		
		request.setAttribute("alarmPortNode", alarmPortNode);
		return jsp;
   }
   public String update() {

		
	   AlarmPort alarmPort = createAlarmPortNode();
		int id = getParaIntValue("id");
		alarmPort.setId(id);
		String nodeid = getParaValue("nodeid");
		
		AlarmPortDao dao = new AlarmPortDao();
		try {
			dao.update(alarmPort); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return list();
	
}
   public AlarmPort createAlarmPortNode() {
	
		String name = getParaValue("name");
		
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		
		
		int compare = getParaIntValue("compare");
		String alarm_info = getParaValue("alarm_info");
		String enabled = getParaValue("enabled");

		
		int invalue1 = getParaIntValue("invalue1");
		int invalue2 = getParaIntValue("invalue2");
		int invalue3 = getParaIntValue("invalue3");
		int outvalue1 = getParaIntValue("outvalue1");
		int outvalue2 = getParaIntValue("outvalue2");
		int outvalue3 = getParaIntValue("outvalue3");
		int intime1 = getParaIntValue("intime1");
		int intime2 = getParaIntValue("intime2");
		int intime3 = getParaIntValue("intime3");
		int outtime1 = getParaIntValue("outtime1");
		int outtime2 = getParaIntValue("outtime2");
		int outtime3 = getParaIntValue("outtime3");
		
		int smsin1 = getParaIntValue("smsin1");
		int smsin2 = getParaIntValue("smsin2");
		int smsin3 = getParaIntValue("smsin3");
		int smsout1 = getParaIntValue("smsout1");
		int smsout2 = getParaIntValue("smsout2");
		int smsout3 = getParaIntValue("smsout3");
		
		String wayin1 = getParaValue("wayin1-id");
		String wayin2 = getParaValue("wayin2-id");
		String wayin3 = getParaValue("wayin3-id");
		String wayout1 = getParaValue("wayout1-id");
		String wayout2 = getParaValue("wayout2-id");
		String wayout3 = getParaValue("wayout3-id");
		
		AlarmPort alarmPortNode = new AlarmPort();
		alarmPortNode.setName(name);
		alarmPortNode.setType(type);
	//	alarmPortNode.setSubtype(subtype);
		
		alarmPortNode.setCompare(compare);
		
		alarmPortNode.setEnabled(enabled);
		
		alarmPortNode.setLevelinvalue1(invalue1);
		alarmPortNode.setLevelinvalue2(invalue2);
		alarmPortNode.setLevelinvalue3(invalue3);
		
		alarmPortNode.setLeveloutvalue1(outvalue1);
		alarmPortNode.setLeveloutvalue2(outvalue2);
		alarmPortNode.setLeveloutvalue3(outvalue3);
		
		alarmPortNode.setLevelintimes1(intime1);
		alarmPortNode.setLevelintimes2(intime2);
		alarmPortNode.setLevelintimes3(intime3);
		
		alarmPortNode.setLevelouttimes1(outtime1);
		alarmPortNode.setLevelouttimes2(outtime2);
		alarmPortNode.setLevelouttimes3(outtime3);
		
		alarmPortNode.setSmsin1(smsin1);
		alarmPortNode.setSmsin2(smsin2);
		alarmPortNode.setSmsin3(smsin3);
		
		alarmPortNode.setSmsout1(smsout1);
		alarmPortNode.setSmsout2(smsout2);
		alarmPortNode.setSmsout3(smsout3);
		alarmPortNode.setAlarm_info(alarm_info);
		
		alarmPortNode.setWayin1(wayin1);
		alarmPortNode.setWayin2(wayin2);
		alarmPortNode.setWayin3(wayin3);
		
		alarmPortNode.setWayout1(wayout1);
		alarmPortNode.setWayout2(wayout2);
		alarmPortNode.setWayout3(wayout3);
		
		return alarmPortNode;
	
}
   public void fromLastToAlarmPort() {
	   

		List list = new ArrayList();
		List list1 = new ArrayList();
		List shareList = new ArrayList();
		Hashtable porthash = new Hashtable();
		// Session session=null;
		AlarmPort alarmPort = null;
		Vector configV = new Vector();
		Hashtable hashSpeed = new Hashtable();
		try {
			// 从内存中得到所有端口的采集信息
			Hashtable sharedata = ShareData.getSharedata();
			// 从数据库得到监视IP列表
			HostNodeDao hostnodedao = new HostNodeDao();
			shareList = hostnodedao.loadMonitorNet();
			User current_user = (User) session
					.getAttribute(SessionConstant.CURRENT_USER);
			StringBuffer s = new StringBuffer();
			int _flag = 0;
			String st = "";
			if (current_user.getBusinessids() != null) {
				if (current_user.getBusinessids() != "-1") {
					String[] bids = current_user.getBusinessids().split(",");
					if (bids.length > 0) {
						for (int i = 0; i < bids.length; i++) {
							if (bids[i].trim().length() > 0) {
								s.append(" bid like '%").append(bids[i])
										.append("%' ");
								if (i != bids.length - 1)
									s.append(" or ");
							}
						}
						// s.append(") ") ;
					}

				}
			}

//			InterfaceTempDao interfaceDao = new InterfaceTempDao();
//			List interfaceList = new ArrayList();
//			try {
//				interfaceList = interfaceDao
//						.findByCondition(" where (subentity = 'ifDescr' or subentity = 'ifSpeed') and nodeid in (select id from topo_host_node where "
//								+ s.toString() + ") ");
//			} catch (Exception e) {
//
//			} finally {
//				interfaceDao.close();
//			}
//			Hashtable interfaceHash = new Hashtable();
//			if (interfaceList != null && interfaceList.size() > 0) {
//				for (int k = 0; k < interfaceList.size(); k++) {
//					NodeTemp vo = (NodeTemp) interfaceList.get(k);
//					if (interfaceHash.containsKey(vo.getIp() + ":"
//							+ vo.getSindex())) {
//						Hashtable _porthash = (Hashtable) interfaceHash.get(vo
//								.getIp()
//								+ ":" + vo.getSindex());
//						if ("ifDescr".equalsIgnoreCase(vo.getSubentity())) {
//							_porthash.put("ifDescr", vo.getThevalue());
//						}
//						
//							_porthash.put("type", vo.getType());
//							_porthash.put("subtype", vo.getSubtype());
//						interfaceHash.put(vo.getIp() + ":" + vo.getSindex(),
//								_porthash);
//					} else {
//
//						Hashtable _porthash = new Hashtable();
//						if ("ifDescr".equalsIgnoreCase(vo.getSubentity())) {
//							_porthash.put("ifDescr", vo.getThevalue());
//						}
//						_porthash.put("type", vo.getType());
//						_porthash.put("subtype", vo.getSubtype());
//						interfaceHash.put(vo.getIp() + ":" + vo.getSindex(),
//								_porthash);
//					}
//
//				}
//			}
			// 从端口配置表里获取列表
			PortconfigDao portDao=null;
			List portList = new ArrayList();
			try {
				portDao=new PortconfigDao();
				portList=portDao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				portDao.close();
			}
			Hashtable interfaceHash = new Hashtable();
			if (portList != null && portList.size() > 0) {
				for (int i = 0; i < portList.size(); i++) {
					Portconfig vo=(Portconfig)portList.get(i);
					
					if (interfaceHash.containsKey(vo.getIpaddress() + ":" + vo.getPortindex())) {
						Hashtable _porthash = (Hashtable) interfaceHash.get(vo.getIpaddress() + ":" + vo.getPortindex());
						
							_porthash.put("ifDescr", vo.getName());

						_porthash.put("type","");//
						_porthash.put("subtype", "");//
						interfaceHash.put(vo.getIpaddress() + ":" + vo.getPortindex(), _porthash);
					} else {

						Hashtable _porthash = new Hashtable();
						_porthash.put("ifDescr", vo.getName());
						_porthash.put("type", "");
						_porthash.put("subtype","");
						interfaceHash.put(vo.getIpaddress() + ":" + vo.getPortindex(), _porthash);
					}
				}
			}
			// 从端口配置表里获取列表
			
			AlarmPortDao dao = new AlarmPortDao();
			try {
				list1 = dao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if (list1 != null && list1.size() > 0) {
				for (int i = 0; i < list1.size(); i++) {
					alarmPort = (AlarmPort) list1.get(i);
					porthash.put(alarmPort.getIpaddress() + ":"
							+ alarmPort.getPortindex(), alarmPort);
				}
			}

			Enumeration portEnu = interfaceHash.keys();
			DBManager dbmanager = new DBManager();
			try {
				while (portEnu.hasMoreElements()) {
					// 判断采集到的端口信息是否已经在端口配置表里已经存在，若不存在则加入
					String portstr = (String) portEnu.nextElement(); // portstr==>ip:index
					Hashtable p = (Hashtable) interfaceHash.get(portstr);

					if (!porthash.containsKey(portstr)) {
						// 若不存在,则添加
						String[] portindex = portstr.split(":");
						alarmPort = new AlarmPort();
						
						alarmPort.setIpaddress(portindex[0]);
						alarmPort.setPortindex(Integer.parseInt(portindex[1]));
						alarmPort.setName((String) p.get("ifDescr"));
						alarmPort.setType((String) p.get("type"));
						alarmPort.setType((String) p.get("subtype"));
						alarmPort.setEnabled("0");
						alarmPort.setCompare(1);
						alarmPort.setLevelinvalue1(200000);
						alarmPort.setLevelinvalue2(300000);
						alarmPort.setLevelinvalue3(400000);
						alarmPort.setLeveloutvalue1(200000);//一级出口阀值
						alarmPort.setLeveloutvalue2(300000);//二级出口阀值
						alarmPort.setLeveloutvalue3(400000);//三级出口阀值
						alarmPort.setLevelintimes1(3);//告警次数
						alarmPort.setLevelintimes2(3);//告警次数
						alarmPort.setLevelintimes3(3);//告警次数
						alarmPort.setLevelouttimes1(3);//告警次数
						alarmPort.setLevelouttimes2(3);//告警次数
						alarmPort.setLevelouttimes3(3);//告警次数
						alarmPort.setSmsin1(1);// 0：不发送短信
						alarmPort.setSmsin2(1);// 0：不发送短信
						alarmPort.setSmsin3(1);// 0：不发送短信
						alarmPort.setSmsout1(1);// 0：不发送短信
						alarmPort.setSmsout2(1);// 0：不发送短信
						alarmPort.setSmsout3(1);// 0：不发送短信
						alarmPort.setAlarm_info("流速超过阀值");
						StringBuffer sql = new StringBuffer(300);
						sql.append("insert into nms_alarm_port_node(ipaddress,portindex,name,enabled,compare,levelinvalue1,levelinvalue2,levelinvalue3,leveloutvalue1,leveloutvalue2,leveloutvalue3,levelintimes1,levelintimes2,levelintimes3,levelouttimes1,levelouttimes2,levelouttimes3,smsin1,smsin2,smsin3,smsout1,smsout2,smsout3,alarm_info)values(");
						sql.append("'");
						sql.append(alarmPort.getIpaddress());
						sql.append("',");
						sql.append(alarmPort.getPortindex());
						sql.append(",'");
						sql.append(alarmPort.getName());
						sql.append("','");
						sql.append(alarmPort.getEnabled());
						sql.append("',");
						sql.append(alarmPort.getCompare());
						sql.append(",");
						sql.append(alarmPort.getLevelinvalue1());
						sql.append(",");
						sql.append(alarmPort.getLevelinvalue2());
						sql.append(",");
						sql.append(alarmPort.getLevelinvalue3());
						sql.append(",");
						sql.append(alarmPort.getLeveloutvalue1());
						sql.append(",");
						sql.append(alarmPort.getLeveloutvalue2());
						sql.append(",");
						sql.append(alarmPort.getLeveloutvalue3());
						sql.append(",");
						sql.append(alarmPort.getLevelintimes1());
						sql.append(",");
						sql.append(alarmPort.getLevelintimes2());
						sql.append(",");
						sql.append(alarmPort.getLevelintimes3());
						sql.append(",");
						sql.append(alarmPort.getLevelouttimes1());
						sql.append(",");
						sql.append(alarmPort.getLevelouttimes2());
						sql.append(",");
						sql.append(alarmPort.getLevelouttimes3());
						sql.append(",");
						sql.append(alarmPort.getSmsin1());
						sql.append(",");
						sql.append(alarmPort.getSmsin2());
						sql.append(",");
						sql.append(alarmPort.getSmsin3());
						sql.append(",");
						sql.append(alarmPort.getSmsout1());
						sql.append(",");
						sql.append(alarmPort.getSmsout2());
						sql.append(",");
						sql.append(alarmPort.getSmsout3());
						sql.append(",'");
						sql.append(alarmPort.getAlarm_info());
						sql.append("')");
					//	System.out.println(sql.toString());
						try {
							dbmanager.addBatch(sql.toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}

			} catch (Exception e) {

			} finally {
				try {
					dbmanager.executeBatch();
				} catch (Exception e) {

				} finally {
					dbmanager.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
	
   }
}