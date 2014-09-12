package com.afunms.application.ajaxManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sf.json.JSONObject;

import com.afunms.application.model.SlaNodeConfig;
import com.afunms.application.util.VpnHelper;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.dao.VpnCfgFileDao;
import com.afunms.config.dao.VpnDao;
import com.afunms.config.model.CmdResult;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.config.model.Vpn;
import com.afunms.config.model.VpnCfgCmdFile;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.telnet.CiscoTelnet;
import com.afunms.polling.telnet.H3CTelnet;
import com.afunms.polling.telnet.ZteTelnet;
import com.afunms.system.model.User;
import com.afunms.topology.model.LinkModel;
import com.afunms.topology.model.NodeModel;
import com.afunms.topology.model.NodeTree;
import com.afunms.topology.util.OperateXml;

/**
 * @description vpn
 * @author wangxiangyong
 * @date Feb 29, 2012 11:42:32 AM
 */
public class VpnAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	public void execute(String action) {
		if (action.equals("loadFile")) {// 加载配置文件命令
			loadFile();
		}
		if (action.equals("saveFile")) {// 保存配置文件
			saveFile();
		}
		if (action.equals("saveCmdCfg")) {// 保存配置命令文件
			saveCmdCfg();
		}
		if (action.equals("isExistFileName")) {// 文件名是否已存在
			isExistFileName();
		}

		if (action.equals("exeVpnCmd")) {// 执行配置vpn命令
			exeVpnCmd();
		}

		if (action.equals("showvpnmap")) {// vpn拓扑图
			showvpnmap();
		}
	}

	/*
	 * 加载文件
	 */
	public void loadFile() {
		String id = (String) getParaValue("id");
		VpnCfgFileDao dao = new VpnCfgFileDao();
		VpnCfgCmdFile vpncfg = null;
		FileReader fr = null;
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\", "/");
		vpncfg = (VpnCfgCmdFile) dao.findByID(id);
		StringBuffer content = new StringBuffer();
		File f = new File(prefix + vpncfg.getFilename());
		if (f.exists()) {
			try {
				fr = new FileReader(prefix + vpncfg.getFilename());
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			BufferedReader br = new BufferedReader(fr);
			String lineStr = "";
			try {
				while (null != (lineStr = br.readLine())) {
					content.append(lineStr + "\r\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("value", content.toString());
		map.put("vpnType", vpncfg.getVpnType());
		map.put("cmdId", vpncfg.getId() + "");
		map.put("deviceType", vpncfg.getDeviceType());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();

	}

	private String saveFile() {
		String commands = getParaValue("commands");
		String type = getParaValue("type");
		String deviceType = getParaValue("deviceType");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
		String b_time = sdf.format(new Date());

		String fileName = b_time + ".log";
		String filePath = "script\\\\" + fileName;
		request.setAttribute("fileName", fileName);
		request.setAttribute("commands", commands);
		return "/config/ciscosla/saveFile.jsp";
	}

	public void saveCmdCfg() {
		String type = getParaValue("type");
		String commands = getParaValue("commands");
		String name = "";
		String devicetype = getParaValue("devicetype");
		name = type;
		String result = commands.replaceAll(";;", "\r\n");
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\", "/");
		String filePath = "slascript/" + devicetype + "/" + name + ".log";
		File f = new File(prefix + filePath);
		String alert = "保存失败";
		boolean flag = true;
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f);
			fw.write(result);
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			if (flag) {
				alert = "保存成功";
			}
			Map map = new HashMap();
			map.put("isSucess", alert);
			JSONObject jso = JSONObject.fromObject(map);
			out.print(jso);
			out.flush();
			out.close();
		}

	}

	/**
	 * 判断配置文件是否已存在
	 */
	public void isExistFileName() {
		String name = getParaValue("name");
		try {
			name = new String(name.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String alert = "0";
		boolean flag = false;
		VpnCfgFileDao dao = null;
		try {
			dao = new VpnCfgFileDao();
			List list = dao.findByCondition(" where name='" + name + "'");
			if (list != null && list.size() > 0) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			alert = "2";
		} finally {
			dao.close();
			if (flag) {
				alert = "1";
			}
			Map map = new HashMap();
			map.put("isSucess", alert);
			JSONObject jso = JSONObject.fromObject(map);
			out.print(jso);
			out.flush();
			out.close();
		}

	}

	/**
	 * 执行配置vpn命令
	 */
	public void exeVpnCmd() {
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
		String[] ips = null;
		String id = getParaValue("id");
		String type = getParaValue("type");
		String deviceType = getParaValue("deviceType");
		String command = getParaValue("command");
		String[] commands = null;
		if (command != null) {
			commands = new String[command.split(";").length];
			commands = command.split(";");
		}
		Hashtable<String, String> slaParamHash = new Hashtable<String, String>();
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = null;
		List<Huaweitelnetconf> list = new ArrayList<Huaweitelnetconf>();
		List<CmdResult> resultList = new ArrayList<CmdResult>();
		SlaNodeConfig slaconfig = null;
		if (id != null) {
			try {
				vo = (Huaweitelnetconf) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			if (vo != null) {
				if (vo.getDeviceRender().equals("h3c")) {// h3c
					H3CTelnet tvpn = new H3CTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(), vo.getPort(), vo.getSuuser(), vo.getSupassword(), vo.getDefaultpromtp());
				} else if (vo.getDeviceRender().equals("cisco")) {// cisco
					CiscoTelnet telnet = new CiscoTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(),vo.getPort());

				} else if (vo.getDeviceRender().equals("zte")) {
					ZteTelnet telnet = new ZteTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(), vo.getPort(), vo.getSuuser(), vo.getSupassword(), vo.getDefaultpromtp());
					telnet.executeZteCommands(resultList, vo.getIpaddress(), user, commands);
				} else {
					CmdResult cmdResult = new CmdResult();

					cmdResult.setIp(vo.getIpaddress());
					cmdResult.setCommand("------");
					cmdResult.setResult("登录失败!");
					cmdResult.setTime(time);
					resultList.add(cmdResult);
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

	/**
	 * vpn拓扑图
	 */
	public void showvpnmap() {
		List list = null;
		String type = getParaValue("type");
		String imgPath = "";
		String nodeMenuInfo = "";
		String lineMenuInfo = "";
		NodeTree tree = new NodeTree();
		int count = 1;
		OperateXml opeXml = new OperateXml(type + ".jsp");
		boolean flag = opeXml.init4updateXml();
		Hashtable<String, NodeModel> hashtable = new Hashtable<String, NodeModel>();
		if (flag)
			hashtable = opeXml.showNode();
		NodeModel model = null;
		List<NodeModel> nodeModelList = new ArrayList<NodeModel>();// 存放新添加的节点
		VpnDao dao = new VpnDao();
		List vpnlist = dao.loadAll();
		Vector<String> linkIdVector = new Vector<String>();
		// Vector<String> sourceIpVec = new Vector<String>();
		// Vector<String> desIpVec = new Vector<String>();

		Hashtable<String, Hashtable<String, String>> hash = new Hashtable<String, Hashtable<String, String>>();
		Vector<String> ipVec = new Vector<String>();
		if (vpnlist != null && vpnlist.size() > 0) {
			for (Object object : vpnlist) {
				Vpn vpn = (Vpn) object;
				if (vpn != null) {
					String desIp = vpn.getDesIp();
					if (desIp != null) {
						desIp = desIp.replaceAll("\\.", "");
						if (!ipVec.contains(desIp)) {
							ipVec.add(desIp);
						}
					}

				}
			}
			// //////////////////
			Hashtable<Integer, String> telnetHash = new Hashtable<Integer, String>();
			HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
			List telnetlist = null;
			try {
				telnetlist = haweitelnetconfDao.getAllTelnetConfig();
				if (telnetlist != null && telnetlist.size() > 0) {
					for (int i = 0; i < telnetlist.size(); i++) {
						Huaweitelnetconf vo = (Huaweitelnetconf) telnetlist.get(i);
						if (vo != null) {
							String desIp = vo.getIpaddress();
							if (desIp != null) {
								desIp = desIp.replaceAll("\\.", "");
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
			// //////////////////////////
			Hashtable slaHash = ShareData.getSlaHash();
			if (slaHash == null)
				slaHash = new Hashtable();
			Vector<String> tempVec = new Vector<String>();

			for (int k = 0; k < vpnlist.size(); k++) {
				Vpn vpn = (Vpn) vpnlist.get(k);
				if (vpn != null) {
					String sourceIp = "";
					int id = vpn.getSourceId();
					if (telnetHash.containsKey(id)) {
						sourceIp = telnetHash.get(id);
					} else {
						continue;

					}
					String desIp = vpn.getDesIp();
					int desId = vpn.getDesId();

					imgPath = "../../resource/image/topo/serviceQuality/32ip.gif";
					// String rrtValue = "--";
					String statusValue = "0";
					if (slaHash.containsKey(id + "")) {
						Hashtable dataHash = (Hashtable) slaHash.get(id + "");
					}
					// 源节点
					int sourceId = id;
					Host sourceHost = (Host) PollingEngine.getInstance().getNodeByID(sourceId);
					
					String deviceType = "zte";
					if (sourceHost != null) {
						
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(sourceHost);
						deviceType =nodedto.getSubtype();
//						if (sourceHost.getOstype() == 16){
//							deviceType = "zte";
//						}else {
//							continue;
//						}
					} else {
						continue;
					}
					String prefix = ResourceCenter.getInstance().getSysPath().replace("\\", "/");
					String[] names = new String[prefix.split("/").length];
					String sysName = "";
					if (names != null && prefix.split("/").length > 0) {
						names = prefix.split("/");
						sysName = names[names.length - 1];
					}
					nodeMenuInfo = "<a class='vpn_menu_out' onclick=\"createWindow('exeScript.jsp?id=" + id + "&type=createPolicy&devicetype=" + deviceType
							+ "')\">&nbsp;&nbsp;&nbsp;VPN命令配置</a><br></br><a class='vpn_menu_out' onclick=\"createAuitWindow('" + sysName + "/vpn.do?action=auditList&id=" + id
							+ "')\">&nbsp;&nbsp;&nbsp;操作审计</a>";

					StringBuffer sourceInfoBuffer = new StringBuffer();
					sourceInfoBuffer.append("设备标签：").append(sourceHost.getAlias()).append("<br/>").append("IP：" + sourceIp);

					NodeModel sourceNode = new NodeModel();
					sourceNode.setId(sourceId + "");
					sourceNode.setName(sourceHost.getAlias());// 设备名称
					sourceNode.setUrl(imgPath);
					sourceNode.setDeviceInfo(sourceInfoBuffer.toString());// 节点提示信息
					sourceNode.setNodeMenuInfo(nodeMenuInfo);
					VpnHelper helper = new VpnHelper();
					Hashtable<String, String> sourceHash = null;
					Hashtable<String, String> desHash = null;
					if (!tempVec.contains(sourceIp)) {
						sourceHash = helper.getInterface(sourceHost);
						hash.put(sourceIp, sourceHash);
						tempVec.add(sourceIp);
						if (!hashtable.containsKey(sourceId + "")) {
							model = new NodeModel();
							model.setId(sourceId + "");
							model.setX(20 + count * 30);
							model.setY(20 + count * 28);
							nodeModelList.add(model);
							sourceNode.setX(20 + count * 30);
							sourceNode.setY(20 + count * 28);
							count++;
						} else {
							NodeModel model2 = hashtable.get(sourceId + "");
							sourceNode.setX(model2.getX());
							sourceNode.setY(model2.getY());
						}
						tree.getNodeList().add(sourceNode);

					} else {
						sourceHash = hash.get(sourceIp);
					}

					// /////////////目标节点///////////////////
					String desNodeId = desId + "";
					Host desHost = (Host) PollingEngine.getInstance().getNodeByID(desId);
					if (desHost != null) {
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(sourceHost);
						deviceType =nodedto.getSubtype();
//						if (desHost.getOstype()== 16){
//							deviceType="zte";
//						}else {
//							continue;
//						}
					} else {
						continue;
					}
					float weight = 1f;

					nodeMenuInfo = "<a class='vpn_menu_out' onclick=\"createWindow('exeScript.jsp?id=" + desId + "&type=createPolicy&devicetype=" + deviceType
							+ "')\">&nbsp;&nbsp;&nbsp;VPN命令配置</a></br></br><a class='vpn_menu_out' onclick=\"createAuitWindow('" + sysName + "/vpn.do?action=auditList&id=" + desId
							+ "')\">&nbsp;&nbsp;&nbsp;操作审计</a>";

					NodeModel desNode = new NodeModel();

					StringBuffer infoBuffer = new StringBuffer();
					infoBuffer.append("设备标签：").append(desHost.getAlias()).append("<br/>").append("IP：" + desIp);
					desNode.setId(desNodeId);
					desNode.setName(desHost.getAlias());
					desNode.setUrl(imgPath);
					desNode.setDeviceInfo(infoBuffer.toString());
					desNode.setNodeMenuInfo(nodeMenuInfo);
					if (!tempVec.contains(desIp)) {
						tempVec.add(desIp);
						desHash = helper.getInterface(desHost);
						hash.put(desIp, desHash);
						if (!hashtable.containsKey(desNodeId)) {
							model = new NodeModel();
							model.setId(desNodeId);
							model.setX(20 + count * 30);
							model.setY(20 + count * 28);
							nodeModelList.add(model);
							desNode.setX(20 + count * 30);
							desNode.setY(20 + count * 28);
							count++;
						} else {
							NodeModel model2 = hashtable.get(desNodeId);
							desNode.setX(model2.getX());
							desNode.setY(model2.getY());
						}
						tree.getNodeList().add(desNode);

					} else {
						desHash = hash.get(desIp);
					}

					// ///////////////链路信息//////////////////
					String linkStatus = "red";
					

					List linkList = tree.getLinkList();
					// lineMenuInfo = "<a class='vpn_menu_out'
					// onclick=\"createWindow('" + sysName +
					// "/vpn.do?action=deleteLink')\">&nbsp;&nbsp;&nbsp;删除链路</a><br>";
					lineMenuInfo = "";
					String sourceIfOperStatus = "";
					String desIfOperStatus = "";

					String sourceIfOperStatusKey = "";
					String desIfOperStatusKey = "";
					// String sourceIn = "";
					// String sourceOut = "";
					// String sourceIfSpeed = "正在获取";
					// String desIn = "";
					// String desOut = "";
					String desIfSpeed = "正在获取";
					// String key2 = "";
					// String key3 = "";
					String sourceIfSpeedKey = "";
					String desIfSpeedKey = "";
					// String desInKey = "";
					// String desOutKey = "";
					// String sourcePortName = "";
					// String desPortName = "";
					if (sourceHash != null) {
						if (sourceHost != null && vpn != null) {
							sourceIfOperStatusKey = sourceHost.getId() + "_" + vpn.getSourcePortIndex() + "_ifOperStatus";
							// key2 = sourceHost.getId() + "_" +
							// vpn.getSourcePortIndex() + "_InBandwidthUtilHdx";
							// key3 = sourceHost.getId() + "_" +
							// vpn.getSourcePortIndex() +
							// "_OutBandwidthUtilHdx";
							sourceIfSpeedKey = sourceHost.getId() + "_" + vpn.getSourcePortIndex() + "_ifSpeed";

						}

						// if (sourceHash.containsKey(key2)) {
						// sourceIn = sourceHash.get(key2);
						// }
						// if (sourceHash.containsKey(key3)) {
						// sourceOut = sourceHash.get(key3);
						// }
						// if (sourceHash.containsKey(sourceIfSpeedKey)) {
						// sourceIfSpeed = sourceHash.get(sourceIfSpeedKey) +
						// "kb/s";
						//						}
						if (sourceHash.containsKey(sourceIfOperStatusKey)) {
							sourceIfOperStatus = sourceHash.get(sourceIfOperStatusKey);
							if (sourceIfOperStatus != null) {
								if (sourceIfOperStatus.equals("down")) {
									sourceIfOperStatus = "(<font color='red'>down</font>)";
									linkStatus = "red";
								} else if (sourceIfOperStatus.equals("up")) {
									sourceIfOperStatus = "(<font color='green'>up</font>)";
									linkStatus = "green";
								}
							}
						}
					}
					if (desHash != null) {
						if (desHost != null && vpn != null) {
							desIfOperStatusKey = desHost.getId() + "_" + vpn.getDesPortIndex() + "_ifOperStatus";
							// desInKey = desHost.getId() + "_"
							// +vpn.getDesPortIndex() + "_InBandwidthUtilHdx";
							// desOutKey = desHost.getId() + "_"
							// +vpn.getDesPortIndex() + "_OutBandwidthUtilHdx";
							desIfSpeedKey = desHost.getId() + "_" + vpn.getDesPortIndex() + "_ifSpeed";
						}

						if (desHash.containsKey(desIfOperStatusKey)) {
							desIfOperStatus = desHash.get(desIfOperStatusKey);
							if (desIfOperStatus != null) {
								if (desIfOperStatus.equals("down")) {
									desIfOperStatus = "(<font color='red'>down</font>)";
									linkStatus = "red";
								} else if (desIfOperStatus.equals("up")) {
									desIfOperStatus = "(<font color='green'>up</font>)";
									if(linkStatus.equals("red"))
										linkStatus = "green";
								}
							}
						}
						// if (desHash.containsKey(desInKey)) {
						// desIn = desHash.get(desInKey);
						// }
						// if (desHash.containsKey(desOutKey)) {
						// desOut = desHash.get(desOutKey);
						// }
						if (desHash.containsKey(desIfSpeedKey)) {
							desIfSpeed = desHash.get(desIfSpeedKey) + "kb/s";
						}
					}

					String linkId = "line_" + sourceId + "_" + desNodeId;
					StringBuffer linkInfo = new StringBuffer();
					
					if (linkIdVector.contains(linkId)) {
						if (linkList != null && linkList.size() > 0) {
							
							for (int j = 0; j < linkList.size(); j++) {
								LinkModel model2 = (LinkModel) linkList.get(j);

								if (model2 != null) {
									StringBuffer info = new StringBuffer();
									if (model2.getId().equals(linkId)) {
										tree.getLinkList().remove(j);
										if(linkStatus.equals("red")){
											model2.setLinkStatus(linkStatus);
										}
										info.append(model2.getLinkInfo()).append("<br><br>起点端口：").append(vpn.getSourcePortName()).append(sourceIfOperStatus).append("<br>终点端口：").append(
												vpn.getDesPortName()).append(desIfOperStatus);
										info.append("<br>链路流速：").append(desIfSpeed);
										
										model2.setLinkInfo(info.toString());
										tree.getLinkList().add(model2);
										break;
									}
								}
							}

						}
					} else {
					
						linkInfo.append("起点IP：" + sourceIp + "<br>终点IP：" + desIp + "<br><br>" + "起点端口：" + vpn.getSourcePortName()).append(sourceIfOperStatus)
								.append("<br>终点端口：" + vpn.getDesPortName()).append(desIfOperStatus);
						linkInfo.append("<br>链路流速：").append(desIfSpeed);
						LinkModel link = new LinkModel();
						link.setId("line_" + sourceId + "_" + desNodeId);
						link.setFrom(sourceId + "");
						link.setTo(desNodeId);
						link.setLinkStatus(linkStatus);
						link.setLinkWeight(weight);
						link.setLinkInfo(linkInfo.toString());
						link.setLinkMenuInfo(lineMenuInfo);
						tree.getLinkList().add(link);
						linkIdVector.add(linkId);
					}
				}
			}
		}

		if (flag) {
			opeXml.appendNode(nodeModelList);
		} else {
			opeXml.buildXml(nodeModelList);
		}
		out.print(JSONObject.fromObject(tree).toString());
		out.flush();
	}
}
