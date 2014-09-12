package com.afunms.application.ajaxManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import com.afunms.application.dao.SlaNodeConfigDao;
import com.afunms.application.model.SlaNodeConfig;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.dao.SlaCfgCmdFileDao;
import com.afunms.config.dao.SlaNodePropDao;
import com.afunms.config.model.CiscoSlaCfgCmdFile;
import com.afunms.config.model.CmdResult;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.telnet.CiscoTelnet;
import com.afunms.polling.telnet.H3CTelnet;
import com.afunms.system.model.User;
import com.afunms.topology.model.LinkModel;
import com.afunms.topology.model.NodeModel;
import com.afunms.topology.model.NodeTree;
import com.afunms.topology.util.OperateXml;

public class ServiceQualityAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	public void execute(String action) {
		if (action.equals("checkUser")) {
			checkUser();
		}
		if (action.equals("exeCmd")) {
			exeCmd();
		}
		if (action.equals("showmap")) {
			showmap();
		}
		if (action.equals("saveData")) {
			saveData();
		}

	}
   public void saveData(){
	   String flag = "保存成功";
	   try {
		   String dataXml = getParaValue("data");	
		   String slatype=getParaValue("slatype");
		   dataXml = dataXml.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"GB2312\"?>");	
		   OperateXml operateXml=new OperateXml(slatype+".jsp");
		   operateXml.saveContent(dataXml);
	} catch (Exception e) {
		e.printStackTrace();
		flag="保存失败";
	}
	   Map map = new HashMap();
	   
		map.put("flag", flag);
		JSONObject jso = JSONObject.fromObject(map);
		out.print(jso);
		out.flush();
		out.close();
   }
	public void checkUser() {
		String isSccess = "0";
		String h3c_admin = getParaValue("h3c_admin");
		String h3c_tag = getParaValue("h3c_tag");
		SlaNodePropDao nodepropdao = new SlaNodePropDao();
		try {
			String where = " where adminsign='" + h3c_admin + "' and operatesign='" + h3c_tag + "'";
			List list = nodepropdao.findByCondition(where);
			if (list != null && list.size() > 0) {
				isSccess = "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nodepropdao.close();
		}
		Map map = new HashMap();
		map.put("isSccess", isSccess);
		JSONObject jso = JSONObject.fromObject(map);
		out.print(jso);
		out.flush();
		out.close();

	}

	public void exeCmd() {
		HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
		List deviceList = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		try {
			deviceList = haweitelnetconfDao.getAllTelnetConfig();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			haweitelnetconfDao.close();
		}
		request.setAttribute("list", deviceList);
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String ipString = getParaValue("checkbox");
		String[] ips = null;
		if (ipString != null) {
			ips = new String[ipString.split(",").length];
			ips = ipString.split(",");
		}
		String cmdid = getParaValue("cmdid");
		String slatype = getParaValue("slatype");
		String deviceType = getParaValue("deviceType");
		Hashtable<String, String> slaParamHash = new Hashtable<String, String>();
		if (deviceType != null && slatype != null) {

			if (deviceType.equals("cisco")) {
				if (slatype.equals("icmp")) {
					// ICMP
					String icmp_destip = getParaValue("icmp_destip");
					String icmp_datapacket = getParaValue("icmp_datapacket");
					String icmp_tos = getParaValue("icmp_tos");
					slaParamHash.put("icmp_destip", icmp_destip);
					slaParamHash.put("icmp_datapacket", icmp_datapacket);
					slaParamHash.put("icmp_tos", icmp_tos);
				} else if (slatype.equals("icmppath")) {
					// icmppath
					String icmppath_destip = getParaValue("icmppath_destip");
					String icmppath_rate = getParaValue("icmppath_rate");
					String icmppath_history = getParaValue("icmppath_history");
					String icmppath_buckets = getParaValue("icmppath_buckets");
					String icmppath_life = getParaValue("icmppath_life");
					slaParamHash.put("icmppath_destip", icmppath_destip);
					slaParamHash.put("icmppath_rate", icmppath_rate);
					slaParamHash.put("icmppath_history", icmppath_history);
					slaParamHash.put("icmppath_buckets", icmppath_buckets);
					slaParamHash.put("icmppath_life", icmppath_life);
				} else if (slatype.equals("udp")) {
					// UDP
					String udp_destip = getParaValue("udp_destip");
					String udp_destport = getParaValue("udp_destport");
					slaParamHash.put("udp_destip", udp_destip);
					slaParamHash.put("udp_destport", udp_destport);
				} else if (slatype.equals("jitter")) {
					// Jitter
					String jitter_destip = getParaValue("jitter_destip");
					String jitter_destport = getParaValue("jitter_destport");
					String jitter_numpacket = getParaValue("jitter_numpacket");
					String jitter_interval = getParaValue("jitter_interval");
					slaParamHash.put("jitter_destip", jitter_destip);
					slaParamHash.put("jitter_destport", jitter_destport);
					slaParamHash.put("jitter_numpacket", jitter_numpacket);
					slaParamHash.put("jitter_interval", jitter_interval);
				} else if (slatype.equals("tcpconnectwithresponder")) {
					// tcpconnectwithresponder
					String tcpconnectwithresponder_destip = getParaValue("tcpconnectwithresponder_destip");
					String tcpconnectwithresponder_destport = getParaValue("tcpconnectwithresponder_destport");
					String tcpconnectwithresponder_tos = getParaValue("tcpconnectwithresponder_tos");
					slaParamHash.put("tcpconnectwithresponder_destip", tcpconnectwithresponder_destip);
					slaParamHash.put("tcpconnectwithresponder_destport", tcpconnectwithresponder_destport);
					slaParamHash.put("tcpconnectwithresponder_tos", tcpconnectwithresponder_tos);
				} else if (slatype.equals("tcpconnectnoresponder")) {
					// tcpconnectnoresponder
					String tcpconnectnoresponder_destip = getParaValue("tcpconnectnoresponder_destip");
					String tcpconnectnoresponder_destport = getParaValue("tcpconnectnoresponder_destport");
					slaParamHash.put("tcpconnectnoresponder_destip", tcpconnectnoresponder_destip);
					slaParamHash.put("tcpconnectnoresponder_destport", tcpconnectnoresponder_destport);
				} else if (slatype.equals("http")) {
					// HTTP
					String http_urlconnect = getParaValue("http_urlconnect");
					slaParamHash.put("http_urlconnect", http_urlconnect);
				} else if (slatype.equals("dns")) {

					// DNS
					String dns_destip = getParaValue("dns_destip");
					String dns_dnsserver = getParaValue("dns_dnsserver");
					slaParamHash.put("dns_destip", dns_destip);
					slaParamHash.put("dns_dnsserver", dns_dnsserver);
				}
			} else if (deviceType.equals("h3c")) {
				if (slatype.equals("icmp")) {
					String h3c_icmp_admin = getParaValue("h3c_icmp_admin");
					String h3c_icmp_tag = getParaValue("h3c_icmp_tag");
					String h3c_icmp_destip = getParaValue("h3c_icmp_destip");
					slaParamHash.put("h3c_icmp_admin", h3c_icmp_admin);
					slaParamHash.put("h3c_icmp_tag", h3c_icmp_tag);
					slaParamHash.put("h3c_icmp_destip", h3c_icmp_destip);
				} else if (slatype.equals("http")) {
					String h3c_http_admin = getParaValue("h3c_http_admin");
					String h3c_http_tag = getParaValue("h3c_http_tag");
					String h3c_http_destip = getParaValue("h3c_http_destip");
					String h3c_http_url = getParaValue("h3c_http_url");
					slaParamHash.put("h3c_http_admin", h3c_http_admin);
					slaParamHash.put("h3c_http_tag", h3c_http_tag);
					slaParamHash.put("h3c_http_destip", h3c_http_destip);
					slaParamHash.put("h3c_http_url", h3c_http_url);
				} else if (slatype.equals("udp") || slatype.equals("tcpconnect-noresponder") || slatype.equals("jitter")) {
					String h3c_admin = getParaValue("h3c_admin");
					String h3c_tag = getParaValue("h3c_tag");
					String h3c_destip = getParaValue("h3c_destip");
					String h3c_destport = getParaValue("h3c_destport");
					slaParamHash.put("h3c_admin", h3c_admin);
					slaParamHash.put("h3c_tag", h3c_tag);
					slaParamHash.put("h3c_destip", h3c_destip);
					slaParamHash.put("h3c_destport", h3c_destport);
				}
			}

		}
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = null;
		List<Huaweitelnetconf> list = new ArrayList<Huaweitelnetconf>();
		List<CmdResult> resultList = new ArrayList<CmdResult>();
		CiscoSlaCfgCmdFile slaconfig = null;
		SlaCfgCmdFileDao slaconfigdao = new SlaCfgCmdFileDao();
		try {
			slaconfig = (CiscoSlaCfgCmdFile) slaconfigdao.findByID(cmdid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			slaconfigdao.close();
		}
		if (ips != null && ips.length > 0) {

			try {
				list = (List) dao.loadByIps(ips);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			String[] commStr = null;

			for (int i = 0; i < list.size(); i++) {
				vo = list.get(i);
				if (vo.getDeviceRender().equals("h3c")) {// h3c
					H3CTelnet tvpn = new H3CTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(), vo.getPort(), vo.getSuuser(), vo.getSupassword(), vo.getDefaultpromtp());
					tvpn.getSlaCommantValue(resultList, ips[i], user, slaconfig, slaParamHash);
				} else if (vo.getDeviceRender().equals("cisco")) {// cisco
					CiscoTelnet telnet = new CiscoTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(),vo.getPort());
					if (telnet.login()) {
						telnet.getSlaCommantValue(vo.getSupassword(), commStr, resultList, ips[i], user, slaconfig, slaParamHash);

					} else {
						CmdResult cmdResult = new CmdResult();
						cmdResult.setIp(ips[i]);
						cmdResult.setCommand("------");
						cmdResult.setResult("登录失败!");
						cmdResult.setTime(time);
						resultList.add(cmdResult);
					}

				}
			}
		}
		Map map = new HashMap();
		map.put("list", resultList);
		JSONObject jso = JSONObject.fromObject(map);
		out.print(jso);
		out.flush();
		out.close();

	}

	public void showmap() {
     String slatype=getParaValue("slatype");
		List list = null;
		String imgPath="";
		NodeTree tree = new NodeTree();
        int count=1;
		OperateXml opeXml=new OperateXml(slatype+".jsp");
		boolean flag = opeXml.init4updateXml();
		Hashtable<String,NodeModel> hashtable=new Hashtable<String, NodeModel>();
		if(flag) hashtable=opeXml.showNode();
		NodeModel model=null;
		List<NodeModel> nodeModelList=new ArrayList<NodeModel>();//存放新添加的节点
		SlaNodeConfigDao dao = new SlaNodeConfigDao();
		List icmplist = dao.findByCondition(" where slatype='"+slatype+"'");

//		 Vector<String> sourceIpVec = new Vector<String>();
//   		 Vector<String> desIpVec = new Vector<String>();
		 Vector<String> ipVec = new Vector<String>();
		if (icmplist != null && icmplist.size() > 0) {
			for (Object object : icmplist) {
				SlaNodeConfig slaNode = (SlaNodeConfig) object;
				if (slaNode != null) {
					String desIp=slaNode.getDestip();
					if (desIp!=null) {
						desIp=desIp.replaceAll("\\.", "");
						if (!ipVec.contains(desIp)) {
							ipVec.add(desIp);
						}
					}
					
				}
			}
			////////////////////
			Hashtable<Integer,String> telnetHash = new Hashtable<Integer,String>();
			HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
			List telnetlist = null;
			try {
				telnetlist = haweitelnetconfDao.getAllTelnetConfig();
				if(telnetlist != null && telnetlist.size()>0){
					for(int i=0;i<telnetlist.size();i++){
						Huaweitelnetconf vo = (Huaweitelnetconf)telnetlist.get(i);
						if (vo != null) {
							String desIp=vo.getIpaddress();
							if (desIp!=null) {
								desIp=desIp.replaceAll("\\.", "");
								if (!ipVec.contains(desIp)) {
									ipVec.add(desIp);
								}
							}
							telnetHash.put(vo.getId(), vo.getIpaddress());
						}
						
					}
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				haweitelnetconfDao.close();
			}
			////////////////////////////
			 Hashtable slaHash = ShareData.getSlaHash();
			  if(slaHash == null)slaHash = new Hashtable();
			Vector<String> tempVec=new Vector<String>();
			
			
			for (int k = 0; k < icmplist.size(); k++) {
				SlaNodeConfig slaNode = (SlaNodeConfig)icmplist.get(k);
				if(slaNode!=null){
					String sourceIp="";
				int id=slaNode.getTelnetconfig_id();
				if (telnetHash.containsKey(id)) {
					sourceIp=telnetHash.get(id);
				}else {
					continue;
					
				}
				String desIp = slaNode.getDestip();
				
					imgPath="../../resource/image/topo/serviceQuality/32ip.gif";
					String rrtValue="--";
					String statusValue="0";
					//SysLogger.info("id----------------"+id);
					if (slaHash != null && slaHash.size() > 0) {
						Enumeration newSlaEnu = slaHash.keys();
						while (newSlaEnu.hasMoreElements()) {
							String slaconfigid = (String) newSlaEnu.nextElement();
							//SysLogger.info("slaconfigid======="+slaconfigid);
							//Hashtable sladataHash = (Hashtable) slanodeHash.get(slaconfigid);
						}
					}
					 if(slaHash.containsKey(slaNode.getId()+"")){
						 Hashtable dataHash = (Hashtable)slaHash.get(slaNode.getId()+"");
							Pingcollectdata rttdata =  (Pingcollectdata)dataHash.get("rtt");
							Pingcollectdata statusdata =  (Pingcollectdata)dataHash.get("status");
						   if(rttdata != null)rrtValue = rttdata.getThevalue();
						   if(statusdata != null)statusValue = statusdata.getThevalue();
						   //SysLogger.info("rrtValue==============="+rrtValue);
						   //SysLogger.info("statusValue==============="+statusValue);
						}
					 //源节点
					String sourceNodeId = sourceIp.replaceAll("\\.", "");
					NodeModel sourceNode = new NodeModel();
					sourceNode.setId(sourceNodeId);
				//	sourceNode.setFatherId("1");
					sourceNode.setName(sourceIp);//设备名称
				//	sourceNode.setState(1);
					sourceNode.setUrl(imgPath);
					sourceNode.setDeviceInfo("IP："+sourceIp);//节点提示信息
				//	sourceNode.setLinkInfo("");
				//	sourceNode.setWeight(1);
					if(!tempVec.contains(sourceIp)){
						tempVec.add(sourceIp);
						if (!hashtable.containsKey(sourceNodeId)) {
							model=new NodeModel();
							model.setId(sourceNodeId);
							model.setX(20+count*30);
							model.setY(20+count*28);
							nodeModelList.add(model);
							sourceNode.setX(20+count*30);
							sourceNode.setY(20+count*28);
							count++;
						}else {
							NodeModel model2=hashtable.get(sourceNodeId);
							sourceNode.setX(model2.getX());
							sourceNode.setY(model2.getY());
						}
						tree.getNodeList().add(sourceNode);
						
						
					}
					
                     
					// /////////////目标节点///////////////////
					//int state=0;
					float weight=1f;

					NodeModel desNode = new NodeModel();
                    String desNodeId=desIp.replaceAll("\\.", "");
					StringBuffer infoBuffer = new StringBuffer();
					infoBuffer.append("IP：" + sourceIp).append("<br/>");
					desNode.setId(desNodeId);
				//	desNode.setFatherId(sourceNodeId);
					desNode.setName(desIp);
				//	desNode.setState(state);
					desNode.setUrl(imgPath);
					desNode.setDeviceInfo(infoBuffer.toString());
				//	desNode.setLinkInfo("当前RRT："+rrtValue+"ms"+"<br> 成功率&nbsp;："+statusValue+"%");//链路提示信息
				//	desNode.setWeight(weight);
					if (!tempVec.contains(desIp)) {
						tempVec.add(desIp);
						
						if (!hashtable.containsKey(desNodeId)) {
							model=new NodeModel();
							model.setId(desNodeId);
							model.setX(20+count*30);
							model.setY(20+count*28);
							nodeModelList.add(model);
							desNode.setX(20+count*30);
							desNode.setY(20+count*28);
							count++;
						}else {
							NodeModel model2=hashtable.get(desNodeId);
							desNode.setX(model2.getX());
							desNode.setY(model2.getY());
						}
						tree.getNodeList().add(desNode);
						
					}
					
					/////////////////链路信息//////////////////
					String linkStatus="red";
					if (statusValue!=null&&!statusValue.equalsIgnoreCase("null")&&!statusValue.equals("0")) {
						linkStatus="green";
					}
					Pattern pattern = Pattern.compile("[0-9]*");
					Matcher isNum = pattern.matcher(rrtValue);
					
					if (isNum.matches()) {
						float h = Float.parseFloat(rrtValue);
						if(h<100)weight=0.5f;
						if(h<1000&&h>=100)weight=1f;
						if(h>=1000&&h<5000)weight=2f;
						if(h>=5000)weight=3f;
					} else {
						weight=1;
					}
			       LinkModel link=new LinkModel();
			       link.setId(sourceNodeId+"_"+desNodeId);
			       link.setFrom(sourceNodeId);
			       link.setTo(desNodeId);
			       link.setLinkStatus(linkStatus);
			       link.setLinkWeight(weight);
			       link.setLinkInfo("当前RRT："+rrtValue+"ms"+"<br> 成功率&nbsp;："+statusValue+"%");
			       tree.getLinkList().add(link);
			       if(count>15)count=0;
			}
			}
			
			if (flag) {
				opeXml.appendNode(nodeModelList);
			} else {
				opeXml.buildXml(nodeModelList);
			}
		}
	

		out.print(JSONObject.fromObject(tree).toString());
		out.flush();

	}
}
