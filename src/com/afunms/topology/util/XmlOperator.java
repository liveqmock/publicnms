/**
 * <p>Description:operate topo xml</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2006-09-25
 */

package com.afunms.topology.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.model.HintNode;
import com.afunms.topology.model.Link;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeEquip;
import com.afunms.topology.model.Relation;
       
public class XmlOperator {      

	private final String headBytes = "<%@page contentType=\"text/html; charset=GB2312\"%>\r\n";

	private SAXBuilder builder;

	private FileInputStream fis;

	private FileOutputStream fos;

	private XMLOutputter serializer;   

	private String fullPath;

	private String xmlName;// yangjun add

	protected Document doc;

	protected Element root;

	protected Element nodes;

	protected Element lines;

	protected Element assistantLines;

	protected Element demoLines;// yangjun add 示意链路

	private List alarmMapList; // 存放所有含有报警设备的拓扑图id
    
	public XmlOperator() {
		alarmMapList = new ArrayList();
		xmlName = "";
	}
	
	public synchronized void setFile(String fileName) {
		xmlName = fileName;
		fullPath = ResourceCenter.getInstance().getSysPath()
				+ "resource/xml/" + fileName;
		//SysLogger.info(fullPath);
	}
	public synchronized void setfile(String fileName) {
		xmlName = fileName;
		fullPath = ResourceCenter.getInstance().getSysPath()
				+ "flex/data/" + fileName;
		SysLogger.info(fullPath);
	}

	/**
	 * 更新所有info项和image项
	 */
	public synchronized void updateInfo(boolean isCustom) {
		List list = nodes.getChildren();
		Calendar date=Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date c1 = date.getTime();
		String recordtime = sdf.format(c1);
		
		Hashtable hinthash = ShareData.getAllhintlinks();
//		HintNodeDao hintNodeDao = new HintNodeDao();
//		try{
//			List hintlist = hintNodeDao.loadAll();
//			if(hintlist != null && hintlist.size()>0){
//				for(int i=0;i<hintlist.size();i++){
//					HintNode hintnode = (HintNode)hintlist.get(i);
//					hinthash.put(hintnode.getNodeId()+":"+hintnode.getXmlfile(), hintnode);
//				}
//			}
//		}catch(Exception e){
//			
//		}finally{
//			hintNodeDao.close();
//		}
		
		Hashtable nodeequiphash = ShareData.getAllnodeequps();
//		NodeEquipDao nodeEquipDao = new NodeEquipDao();// yangjun add
//		try{
//			List nodeequiplist = nodeEquipDao.loadAll();
//			if(nodeequiplist != null && nodeequiplist.size()>0){
//				for(int i=0;i<nodeequiplist.size();i++){
//					NodeEquip nodeequip = (NodeEquip)nodeequiplist.get(i);
//					nodeequiphash.put(nodeequip.getNodeId()+":"+nodeequip.getXmlName(), nodeequip);
//				}
//			}
//		}catch(Exception e){
//			
//		}finally{
//			nodeEquipDao.close();
//		}
		
		for (int i = 0; i < list.size(); i++) {
			try {
				Element eleNode = (Element) list.get(i);
				String Id = eleNode.getChildText("id");
				int id = Integer.valueOf(Id.substring(3)).intValue();
				String category = eleNode.getChild("id").getAttributeValue("category");
				if (Id.indexOf("hin") != -1) {// yangjun add if..else..
					SysLogger.info("发现示意设备，开始信息更新...");
					//HintNodeDao hintNodeDao = new HintNodeDao();
					HintNode vo = null;
					try{
						if(hinthash.containsKey(Id+":"+xmlName)){
							vo = (HintNode)hinthash.get(Id+":"+xmlName);
						}
						//vo = (HintNode) hintNodeDao.findById(Id, xmlName);
					}catch(Exception e){
						
					}finally{
						//hintNodeDao.close();
					}
					if(vo == null)continue;
					String strSign = vo.getAlias();
					int status = 0;
					//SysLogger.info("示意设备告警恢复-------------");
					updateNode(Id, "img", vo.getImage().substring(17));
					updateNode(Id, "info", "示意设备<br>更新时间："+recordtime);

				} else {
					//判断数据库
					com.afunms.polling.base.Node node = null;
					if(Id.indexOf("dbs") != -1){
						node = PollingEngine.getInstance().getNodeByCategory("dbs", id);
					} else {
						node = PollingEngine.getInstance().getNodeByCategory(category, id);
					}
					//

					if (node == null) {
						SysLogger.info("发现一个被删除的节点，ID=" + Id);
						if (isNodeExist(Id)) {
//							deleteNodeByID(Id);
						}
						continue;
					}
					int alarmLevel = 0;
					if(Id.indexOf("net") != -1){
						try {
							NodeUtil nodeUtil = new NodeUtil();
//							System.out.println(node.getAlias()+"===============node.getBid()==================="+node.getBid());
							NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(node);
							if(nodeDTO!=null){
								CheckEventDao checkeventdao = new CheckEventDao();
							    CheckEvent checkEvent = null;
								try {
									checkEvent = checkeventdao.findLikeName(id+":"+nodeDTO.getType()+":%");
								} catch (Exception e1) {
									e1.printStackTrace();
								} finally {
									checkeventdao.close();
								}
								if(checkEvent!=null){
									alarmLevel = checkEvent.getAlarmlevel();
								}
							}
						} catch (Exception e1) {
//							e1.printStackTrace();
						}
					} else {
						if(node.isAlarm())alarmLevel = 1;
					}
					
					// System.out.println(node.getCategory()+"....................."+node.isAlarm());
					eleNode.getChild("alias").setText(node.getAlias());
					eleNode.getChild("ip").setText(node.getIpAddress()==null?node.getAlias():node.getIpAddress());
					// SysLogger.info("IP : " + node.getIpAddress() + " info : "
					// + node.getShowMessage());
					List alarmList = node.getAlarmMessage();
					String alarmmessage = "";
					
					if (alarmList != null && alarmList.size() > 0) {
						for (int k = 0; k < alarmList.size(); k++) {
							alarmmessage = alarmmessage + alarmList.get(k) + "<br>";
						}
					}
//					System.out.println(xmlName+"========"+node.getIpAddress());
//					System.out.println(xmlName+"========"+node.getIpAddress()+"=======alarmmessage======="+alarmmessage);
//					System.out.println(xmlName+"========"+node.getIpAddress()+"=======node.getShowMessage()======="+node.getShowMessage());
					if(node.getIpAddress()==null){
						eleNode.getChild("info").setText(alarmmessage);
					}else {
						eleNode.getChild("info").setText(node.getShowMessage() + "<br>" + alarmmessage);
					}
					// eleNode.getChild("info").setText(node.getShowMessage());
					NodeEquip vo = null;
					//NodeEquipDao nodeEquipDao = new NodeEquipDao();// yangjun add
					try {
						if(nodeequiphash.containsKey(Id+":"+xmlName)){
							vo = (NodeEquip)nodeequiphash.get(Id+":"+xmlName);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						//nodeEquipDao.close();
					}
					if (node.getCategory() == 4) {
						if (vo != null) {
							try {
								if (alarmLevel > 0) {// 报警
									EquipService equipservice = new EquipService();
									eleNode.getChild("img").setText(
											"image/topo/"
													+ equipservice.getAlarmImage(vo
															.getEquipId()));
									if (!alarmMapList.contains(xmlName)) {// 将告警图加入alarmMapList
										alarmMapList.add(xmlName);
									}
								} else {
									EquipService equipservice = new EquipService();
									eleNode.getChild("img").setText(
											"image/topo/"
													+ equipservice.getTopoImage(vo
															.getEquipId()));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							try {
								if (alarmLevel > 0) { // 报警
									eleNode
											.getChild("img")
											.setText(
													NodeHelper
															.getServerAlarmImage(((com.afunms.polling.node.Host) node)
																	.getSysOid()));
									if (!alarmMapList.contains(xmlName)) {// 将告警图加入alarmMapList
										alarmMapList.add(xmlName);
									}
								} else {
									eleNode
											.getChild("img")
											.setText(
													NodeHelper
															.getServerTopoImage(((com.afunms.polling.node.Host) node)
																	.getSysOid()));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					} else {
						if (node.getDiscoverstatus() > 0) {
							// 没有被发现的设备
							eleNode.getChild("img").setText(
									NodeHelper.getLostImage(node.getCategory()));
						} else {
							if (vo != null) {
//								System.out.println(xmlName+"========"+vo+"==========vo.getEquipId()=========="+vo.getEquipId());
//								System.out.println(xmlName+"========"+node.getIpAddress()+"===node.isAlarm()===="+node.isAlarm());
								try {
									if (alarmLevel > 0) { // 报警
										EquipService equipservice = new EquipService();
//										System.out.println("equipservice.getAlarmImage(vo.getEquipId())=========="+equipservice.getAlarmImage(vo.getEquipId()));
										eleNode.getChild("img").setText(
												"image/topo/"
														+ equipservice.getAlarmImage(vo
																.getEquipId()));
										if (!alarmMapList.contains(xmlName)) {// 将告警图加入alarmMapList
											alarmMapList.add(xmlName);
										}
									} else {
										EquipService equipservice = new EquipService();
										eleNode.getChild("img").setText(
												"image/topo/"
														+ equipservice.getTopoImage(vo
																.getEquipId()));
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								try {
//									System.out.println(xmlName+"========"+node.getIpAddress()+"==node.isAlarm()===="+node.isAlarm());
									if (alarmLevel > 0) { // 报警
										eleNode.getChild("img").setText(
												NodeHelper.getAlarmImage(node
														.getCategory()));
										if (!alarmMapList.contains(xmlName)) {// 将告警图加入alarmMapList
											alarmMapList.add(xmlName);
										}
									} else {
										eleNode.getChild("img").setText(
												NodeHelper.getTopoImage(node
														.getCategory()));
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						}
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if (isCustom) {
			writeXml();
			return;
		}
		// ----------------更新链路(2007.2.27)-------------------
		//ManageXmlDao manageXmlDao = new ManageXmlDao();
		ManageXml mvo = null;
		try{
			if(ShareData.getManagexmlhash() != null){
				if(((Hashtable)ShareData.getManagexmlhash()).containsKey(xmlName)){
					mvo = (ManageXml)(((Hashtable)ShareData.getManagexmlhash()).get(xmlName));
				}
				
			}
			//mvo = (ManageXml) manageXmlDao.findByXml(xmlName);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			//manageXmlDao.close();
		}
		
		List linkList = lines.getChildren();
		for (int i = 0; i < linkList.size(); i++) {
			try {
				Element eleLine = (Element) linkList.get(i);
				// SysLogger.info("link id === "+id);
				int id = Integer.valueOf(eleLine.getAttributeValue("id")).intValue();
				com.afunms.polling.base.LinkRoad lr = (com.afunms.polling.base.LinkRoad) PollingEngine.getInstance().getLinkByID(id);
				eleLine.getChild("lineInfo").setText(lr.getShowMessage());// yangjun  //HONGLI调整了更新xml文件节点的先后顺序(此行先执行，先更新链路是否有告警，再画链路)
				int type = lr.getType();
				String linetext = "";
				linetext = lr.getMessage(type);
				eleLine.getChild("alias").setText(linetext);//链路显示信息
				eleLine.getChild("lineWidth").setText(lr.getLinkWidth(Integer.parseInt(mvo.getUtilhdx()),Integer.parseInt(mvo.getUtilhdxperc())));//链路宽度
				String startdescr = "##";
				String enddescr = "##";
				if(lr.getShowinterf()==1){
					try {
						startdescr = getString(lr.getStartDescr());
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						enddescr = getString(lr.getEndDescr());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				eleLine.getChild("start").setText(startdescr);//起点接口描述
				eleLine.getChild("end").setText(enddescr);//终点接口描述
				// 这里主要用TRAP进行告警确定
				if (lr == null)
					continue;
				if (lr.isAlarm()) {
					if(lr.getLevels()>0){
						eleLine.getChild("color").setText("yellow");
	    		    } else if(lr.getLevels()>1){
	    		    	eleLine.getChild("color").setText("red");
	    		    }
					if (!alarmMapList.contains(xmlName)) {// 链路告警时也将告警图加入alarmMapList
						alarmMapList.add(xmlName);
					}
				} else {
					if (lr.getAssistant() == 0)
						eleLine.getChild("color").setText("green");
					else
						eleLine.getChild("color").setText("blue");
				}
				// add获取链路信息
				eleLine.getChild("lineMenu").setText(NodeHelper.getMenuItem(lr.getId() + "", lr.getStartId() + "", lr.getEndId() + ""));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		List alinkList = assistantLines.getChildren();
		for (int i = 0; i < alinkList.size(); i++) {
			try {
				Element eleLine = (Element) alinkList.get(i);
				int id = Integer.valueOf(eleLine.getAttributeValue("id"))
						.intValue();
				com.afunms.polling.base.LinkRoad lr = (com.afunms.polling.base.LinkRoad) PollingEngine.getInstance().getLinkByID(id);
				eleLine.getChild("lineInfo").setText(lr.getShowMessage());// yangjun
				int type = lr.getType();
				String linetext = "";
				linetext = lr.getMessage(type);
				eleLine.getChild("alias").setText(linetext);//链路显示信息
				eleLine.getChild("lineWidth").setText(lr.getLinkWidth(Integer.parseInt(mvo.getUtilhdx()),Integer.parseInt(mvo.getUtilhdxperc())));//链路宽度
				String startdescr = "##";
				String enddescr = "##";
				if(lr.getShowinterf()==1){
					try {
						startdescr = getString(lr.getStartDescr());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						enddescr = getString(lr.getEndDescr());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				eleLine.getChild("start").setText(startdescr);//起点接口描述
				eleLine.getChild("end").setText(enddescr);//终点接口描述
				if (lr.isAlarm()) {
					if(lr.getLevels()>0){
						eleLine.getChild("color").setText("yellow");
	    		    }
					if(lr.getLevels()>1){
	    		    	eleLine.getChild("color").setText("red");
	    		    }
					if (!alarmMapList.contains(xmlName)) {// 链路告警时也将告警图加入alarmMapList
						alarmMapList.add(xmlName);
					}
				} else {
					if (lr.getAssistant() == 0)
						eleLine.getChild("color").setText("green");
					else
						eleLine.getChild("color").setText("blue");
				}
				// add获取链路信息
				eleLine.getChild("lineMenu").setText(NodeHelper.getMenuItem(lr.getId() + "", lr.getStartId() + "", lr.getEndId() + ""));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		writeXml();
	}

	// 根据报警的拓扑子图list将告警状态返回父图的节点
	public void alarmNode(List alarmMapList) {
		//根据告警子图更新父图节点信息
		SysLogger.info("根据告警子图更新父图节点信息......" + alarmMapList.size());
		
		Hashtable hinthash = ShareData.getAllhintlinks();	
		Hashtable nodeequiphash = ShareData.getAllnodeequps();
		
		List alarmFMapList = new ArrayList();
		Calendar date=Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date c1 = date.getTime();
		String recordtime = sdf.format(c1);
		if (alarmMapList.size() > 0) {
			for (int i = 0; i < alarmMapList.size(); i++) {
				String xmlname = (String) alarmMapList.get(i);
				SysLogger.info("拓扑图" + xmlname + "有告警！！！");

//				ManageXmlDao manageXmlDao = new ManageXmlDao();
//				ManageXml mvo = null;
//				try{
//					mvo = (ManageXml) manageXmlDao.findByXml(xmlname);
//				}catch(Exception e){
//					e.printStackTrace();
//				}finally{
//					manageXmlDao.close();
//				}
				ManageXml mvo = null;
				try{
					if(ShareData.getManagexmlhash() != null){
						if(((Hashtable)ShareData.getManagexmlhash()).containsKey(xmlName)){
							mvo = (ManageXml)(((Hashtable)ShareData.getManagexmlhash()).get(xmlName));
						}
						
					}
					//mvo = (ManageXml) manageXmlDao.findByXml(xmlName);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					//manageXmlDao.close();
				}
				if (mvo != null) {
					//RelationDao relationDao = new RelationDao();
					List rvoList = null;
					try{
						if(ShareData.getRelationhash() != null){
							if(((Hashtable)ShareData.getRelationhash()).containsKey(mvo.getId() + "")){
								rvoList = (List)((Hashtable)ShareData.getRelationhash()).get(mvo.getId() + "");
							}
						}
						//rvoList = relationDao.findByMapId(mvo.getId() + "");
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						//relationDao.close();
					}
					//SysLogger.info("rvoList.size()=======" + rvoList.size());
					if (rvoList != null && rvoList.size() > 0) {
						for (int j = 0; j < rvoList.size(); j++) {
							Relation rvo = (Relation) rvoList.get(j);
							String xmlName = rvo.getXmlName();
							String nodeId = rvo.getNodeId();
							String category = rvo.getCategory();
//							SysLogger.info("发现关联此图的节点..." + nodeId + "+"
//									+ category + "+" + xmlName);
							com.afunms.polling.base.Node node = null;
							if(category!=null&&!"null".equalsIgnoreCase(category)&&!"".equalsIgnoreCase(category)){
								node = PollingEngine
								.getInstance().getNodeByCategory(
										category,
										Integer.parseInt(nodeId
												.substring(3)));
							}
							setFile(xmlName);
							init4updateXml();
							if (isNodeExist(nodeId)) {
								if (node == null) {
									SysLogger.error("开始更新示意设备...");
									
//									HintNodeDao hintNodeDao = new HintNodeDao();
//									HintNode vo = (HintNode) hintNodeDao.findById(nodeId, xmlName);
									HintNode vo = null;
									try{
										if(hinthash.containsKey(nodeId+":"+xmlName)){
											vo = (HintNode)hinthash.get(nodeId+":"+xmlName);
										}
									}catch(Exception e){
										
									}finally{
										//hintNodeDao.close();
									}
									if(vo!=null){
										SysLogger.info(vo.getAlias()+"----"+nodeId+"---image/topo/"+vo.getImage().substring(27, vo.getImage().lastIndexOf("/"))+"/alarm.gif");
										updateNode(nodeId, "info", "<font color='red'>--报警信息:--</font><br>子图有告警<br>更新时间："+recordtime);
										updateNode(nodeId, "img", "image/topo/"+vo.getImage().substring(27, vo.getImage().lastIndexOf("/"))+"/alarm.gif");
									}
								} else {
									SysLogger.error("开始更新示实体设备...");

									NodeEquip vo = null;
									//NodeEquipDao nodeEquipDao = new NodeEquipDao();// yangjun add
									try {
										if(nodeequiphash.containsKey(nodeId+":"+xmlName)){
											vo = (NodeEquip)nodeequiphash.get(nodeId+":"+xmlName);
										}
										
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										//nodeEquipDao.close();
									}
//									NodeEquipDao nodeEquipDao = new NodeEquipDao();// yangjun add
//									NodeEquip vo = null;
//									try {
//										vo = (NodeEquip) nodeEquipDao
//												.findByNodeAndXml(nodeId, xmlName);
//									} catch (Exception e) {
//										e.printStackTrace();
//									} finally {
//										nodeEquipDao.close();
//									}			
									if (vo == null) {
//										SysLogger.info("node.getCategory()===="
//												 + node.getCategory());
										updateNode(nodeId, "img", NodeHelper
												.getAlarmImage(node.getCategory()));
									} else {
										EquipService equipservice = new EquipService();
										updateNode(nodeId, "img", "image/topo"+equipservice
												.getAlarmImage(vo.getEquipId()));
									}
									List alarmList = node.getAlarmMessage();
									String alarmmessage = "";
									if (alarmList != null && alarmList.size() > 0) {
										for (int k = 0; k < alarmList.size(); k++) {
											alarmmessage = alarmmessage
													+ alarmList.get(k) + "<br>";
										}
									}
									if (node.isAlarm()) {// 改成从alarmList取数据
										updateNode(
												nodeId,
												"info",
												node.getShowMessage()
														+ "<br>"
														+ alarmmessage
														+ "<br>"
														+ "<font color='red'>子图有告警</font><br>");
									} else {
										//node.setAlarm(true);
										updateNode(
												nodeId,
												"info",
												node.getShowMessage()
														+ "<br>"
														+ "<font color='red'>--报警信息:--</font>"
														+ "<br>"
														+ "<font color='red'>子图有告警</font><br>");
									}
								}
							}
							writeXml();
							alarmFMapList.add(xmlName);
						}
						alarmNode(alarmFMapList);
					}
				}
			}
		}
		SysLogger.info("更新父节点信息结束...");
	}

	// 修改节点信息 yangjun add
	public void updateNode(String nodeId, String tag, String txt) {
		List eleNodes = nodes.getChildren();
		for (int i = 0; i < eleNodes.size(); i++) {
			Element ele = (Element) eleNodes.get(i);
			if (ele.getChildText("id").equals(nodeId)) {
				ele.getChild(tag).setText(txt);
				break;
			}
		}
	}

	/**
	 * 保存xml文件(用于拓扑图上的"保存"按钮)
	 */
	public void saveImage(String content) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(fullPath);
			osw = new OutputStreamWriter(fos, "GB2312");
			osw.write("<%@page contentType=\"text/html; charset=GB2312\"%>\r\n"
					+ content);
		} catch (Exception e) {
			SysLogger.error("XmlOperator.imageToXml()", e);
		} finally {
			try {
				osw.close();
			} catch (Exception ee) {
			}
		}
	}

	/**
	 * 保存文件
	 */
	public void writeXml() {
		try {
			Format format = Format.getCompactFormat();
			format.setEncoding("GB2312");
			format.setIndent("	");
			serializer = new XMLOutputter(format);
			fos = new FileOutputStream(fullPath);
			fos.write(headBytes.getBytes());
			serializer.output(doc, fos);
			fos.close();
		} catch (Exception e) {
			SysLogger.error("Error in XmlOperator.close()", e);
		}
	}

	/**
	 * 准备更新一个新的xml
	 */
	public void init4updateXml() {
		try {
			//判断文件是否存在
			if(new File(fullPath).exists()){
			fis = new FileInputStream(fullPath);
			fis.skip(headBytes.getBytes().length);
			if(fis.available() > 1) {
				builder = new SAXBuilder();
				doc = builder.build(fis);
				root = doc.getRootElement();
				nodes = root.getChild("nodes");
				lines = root.getChild("lines");
				assistantLines = root.getChild("assistant_lines");
				demoLines = root.getChild("demoLines");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error("Error in XmlOperator.init4updateXml(),file="
					+ fullPath);
		}
	}

	/**
	 * 准备创建一个新的xml
	 */
	public void init4createXml() {
		root = new Element("root");
		nodes = new Element("nodes");
		lines = new Element("lines");
		assistantLines = new Element("assistant_lines");
		demoLines = new Element("demoLines");
	}

	/**
	 * 创建一个新的xml
	 */
	public void createXml() {
		root.addContent(nodes);
		root.addContent(lines);
		root.addContent(assistantLines);
		root.addContent(demoLines);
		doc = new Document(root);
		writeXml();
	}

	/**
	 * 删除一个xml
	 */
	public void deleteXml() {
		try {
			File delFile = new File(fullPath);
			delFile.delete();
		} catch (Exception e) {
			SysLogger.error("删除文件操作出错" + fullPath, e);
		}
	}
    //获取图片大小
	private static String getImageSize(String url){
		File file = new File(url);  
		FileInputStream is = null;  
		try{  
			is = new FileInputStream(file);  
		}catch (FileNotFoundException e2){  
			e2.printStackTrace();  
			return "";  
		}  
		BufferedImage sourceImg = null;  
		try{  
			sourceImg = javax.imageio.ImageIO.read(is);  
		}catch (IOException e1){  
			e1.printStackTrace();  
			return "";  
		}  
		System.out.println("width = "+sourceImg.getWidth() + "height = " + sourceImg.getHeight());  

		return sourceImg.getWidth()+":"+sourceImg.getHeight();
		
	}
	public static void main(String[] args){
		String url = "E://Tomcat5.0//webapps//afunms//resource//image//topo//router.png";
		getImageSize(url);
	}
	/**
	 * 增加一个新的节点(用于发现之后，或者手动增加一个节点)
	 */
	public void addNode(String nodeId, int categroy, String image, String ip,
			String alias, String x, String y) {
		Element eleNode = new Element("node");
		Element eleId = new Element("id");
		Element eleImg = new Element("img");
		Element eleX = new Element("x");
		Element eleY = new Element("y");
		Element eleW = new Element("width");
		Element eleH = new Element("height");
		Element eleIp = new Element("ip");
		Element eleAlias = new Element("alias");
		Element eleInfo = new Element("info");
		Element eleMenu = new Element("menu");
		Element relationMap = new Element("relationMap");// 关联拓扑图

		eleId.setText(nodeId);
		eleId.setAttribute("category", NodeHelper.getNodeEnCategory(categroy));
		if (image == null)
			eleImg.setText(NodeHelper.getTopoImage(categroy));
		else
			eleImg.setText(image);
		eleX.setText((Integer.parseInt(x)+50)+"");
		eleY.setText(y);
		SysLogger.info("id: " + nodeId + "  ip---" + ip + " 类型:" + categroy + " 图片:" + image);
		String sizeString = getImageSize(ResourceCenter.getInstance().getSysPath() + "resource/" + image);
		eleW.setText(sizeString.split(":")[0]);
		eleH.setText(sizeString.split(":")[1]);
//		System.out.println(ResourceCenter.getInstance().getSysPath() + "resource/" + image);//
		eleIp.setText(ip);
		eleAlias.setText(alias);
		eleInfo.setText("设备标签:" + alias + "<br>IP地址:" + ip);
		eleMenu.setText(NodeHelper.getMenu(nodeId, ip, NodeHelper.getNodeEnCategory(categroy)));//yangjun xiugai
		relationMap.setText("");
		eleNode.addContent(eleId);
		eleNode.addContent(eleImg);
		eleNode.addContent(eleX);
		eleNode.addContent(eleY);
		eleNode.addContent(eleW);
		eleNode.addContent(eleH);
		eleNode.addContent(eleIp);
		eleNode.addContent(eleAlias);
		eleNode.addContent(eleInfo);
		eleNode.addContent(eleMenu);
		eleNode.addContent(relationMap);
		nodes.addContent(eleNode);
	}
	
	/**
	 * 增加一个新的关机节点
	 */
	public void addPolicyNode(String nodeId, int categroy, String image, String ip,String alias, String x, String y) {
		Element eleNode = new Element("node");
		Element eleId = new Element("id");
		Element eleImg = new Element("img");
		Element eleX = new Element("x");
		Element eleY = new Element("y");
		Element eleIp = new Element("ip");
		Element eleAlias = new Element("alias");
		Element eleInfo = new Element("info");
		Element eleMenu = new Element("menu");
		Element state = new Element("state");

		eleId.setText(nodeId);
		eleId.setAttribute("category", NodeHelper.getNodeEnCategory(categroy));
		if (image == null)
			eleImg.setText(NodeHelper.getTopoImage(categroy));
		else
			eleImg.setText(image);
		eleX.setText((Integer.parseInt(x)+50)+"");
		eleY.setText(y);
		SysLogger.info("id: " + nodeId + "  ip---" + ip + " 类型:" + categroy
				+ " 图片:" + image);
		eleIp.setText(ip);
		eleAlias.setText(alias);
		eleInfo.setText("设备标签:" + alias + "<br>IP地址:" + ip);
		eleMenu.setText(NodeHelper.getMenu(nodeId, ip, NodeHelper
				.getNodeEnCategory(categroy)));//yangjun xiugai
		state.setText("0");
		eleNode.addContent(eleId);
		eleNode.addContent(eleImg);
		eleNode.addContent(eleX);
		eleNode.addContent(eleY);
		eleNode.addContent(eleIp);
		eleNode.addContent(eleAlias);
		eleNode.addContent(eleInfo);
		eleNode.addContent(eleMenu);
		eleNode.addContent(state);
		nodes.addContent(eleNode);
	}
	
	/**
	 * 增加一个新的墙面节点
	 */
	public void addInfoNode(String nodeId, int categroy, String infono, String buildingnumber, int room,int status, String x, String y) {
		Element eleNode = new Element("node");
		Element eleId = new Element("id");
		Element eleImg = new Element("img");
		Element eleX = new Element("x");
		Element eleY = new Element("y");
		Element eleIp = new Element("ip");
		Element eleAlias = new Element("alias");
		Element eleInfo = new Element("info");
		Element eleMenu = new Element("menu");
		Element relationMap = new Element("relationMap");

		eleId.setText(nodeId);
		eleId.setAttribute("category", NodeHelper.getNodeEnCategory(categroy));
		String image = NodeHelper.getInfoStatusImage(status);
		eleImg.setText(image);
		eleX.setText((Integer.parseInt(x)+50)+"");
		eleY.setText(y);
		SysLogger.info("id: " + nodeId + " 类型:" + categroy + " 图片:" + image);
		eleIp.setText(infono);
		eleAlias.setText(infono);
		String str = "";
		if(status==1){
			str = "在用";
		}else if(status==2){
			str = "空闲";
		}else if(status==3){
			str = "不可用";
		}
		eleInfo.setText("办公楼：" +buildingnumber+ "<br>信息点编码：" +infono+ "<br>房间:" + room+ "<br>状态:" + str);
		eleMenu.setText(NodeHelper.getInfoMenuItem(nodeId));
		relationMap.setText("");
		eleNode.addContent(eleId);
		eleNode.addContent(eleImg);
		eleNode.addContent(eleX);
		eleNode.addContent(eleY);
		eleNode.addContent(eleIp);
		eleNode.addContent(eleAlias);
		eleNode.addContent(eleInfo);
		eleNode.addContent(eleMenu);
		eleNode.addContent(relationMap);
		nodes.addContent(eleNode);
	}
	
	/**
	 * 增加一个新的主机节点(用于发现之后，或者手动增加一个主机节点) yangjun add
	 */
	public void addHostNode(String nodeId, int categroy, String image, String ip,
			String alias, String x, String y) {
		Element eleNode = new Element("node");
		Element eleId = new Element("id");
		Element eleImg = new Element("img");
		Element eleX = new Element("x");
		Element eleY = new Element("y");
		Element eleIp = new Element("ip");
		Element eleAlias = new Element("alias");
		Element eleInfo = new Element("info");
		Element eleMenu = new Element("menu");
		Element relationMap = new Element("relationMap");// 关联拓扑图

		eleId.setText(nodeId);
		eleId.setAttribute("category", NodeHelper.getNodeEnCategory(categroy));
		if (image == null)
			eleImg.setText(NodeHelper.getTopoImage(categroy));
		else
			eleImg.setText(image);
		eleX.setText(x);
		eleY.setText(y);
		SysLogger.info("id: " + nodeId + "  ip---" + ip + " 类型:" + categroy
				+ " 图片:" + image);
		eleIp.setText(ip);
		eleAlias.setText(alias);
		eleInfo.setText("设备标签:" + alias + "<br>IP地址:" + ip);
		eleMenu.setText(NodeHelper.getHostMenu(nodeId, ip, NodeHelper
				.getNodeEnCategory(categroy)));//yangjun xiugai
		relationMap.setText("");
		eleNode.addContent(eleId);
		eleNode.addContent(eleImg);
		eleNode.addContent(eleX);
		eleNode.addContent(eleY);
		eleNode.addContent(eleIp);
		eleNode.addContent(eleAlias);
		eleNode.addContent(eleInfo);
		eleNode.addContent(eleMenu);
		eleNode.addContent(relationMap);
		nodes.addContent(eleNode);
	}

	public void addNode(com.afunms.discovery.Host host) {
		String img = null;
		if (host.getCategory() == 4){
			img = NodeHelper.getServerTopoImage(host.getSysOid());
			addHostNode("net" + String.valueOf(host.getId()), host.getCategory(), img,
					host.getIpAddress(), host.getAlias(), "30", "30");// yangjun
		} else {
			img = NodeHelper.getTopoImage(host.getCategory());
			addNode("net" + String.valueOf(host.getId()), host.getCategory(), img,
					host.getIpAddress(), host.getAlias(), "30", "30");// yangjun
		}
	}

	/**
	 * 增加一条链路
	 */
	public void addLine(String lineId, String startId, String endId) {
		String category = endId.substring(0, 3);
		if("dbs".equalsIgnoreCase(category)){
			//接点为DB
			
		}
//		if(PollingEngine.getInstance().getNodeByID(Integer.parseInt(startid)) == null 
//				|| PollingEngine.getInstance().getNodeByID(Integer.parseInt(endid)) == null){ 
//			SysLogger.info("找不到接点====startid:"+startid+"===endid:"+endid);
//			return;
//		}	
		LinkDao dao = new LinkDao();
		Link link = (Link) dao.findByID(lineId);
		Element line = new Element("line");
		Element a = new Element("a");
		Element b = new Element("b");
		Element color = new Element("color");
		Element dash = new Element("dash");
		Element lineWidth = new Element("lineWidth");// yangjun add
		Element alias = new Element("alias");// yangjun add
		Element startDescr = new Element("start");// yangjun add
		Element endDescr = new Element("end");// yangjun add
		Element lineInfo = new Element("lineInfo");// yangjun add
		Element lineMenu = new Element("lineMenu");// yangjun add

		line.setAttribute("id", lineId);
		a.setText(startId);
		b.setText(endId);
		color.setText("green");
		dash.setText("Solid");
		lineWidth.setText("1");
		alias.setText("#.#");
		startDescr.setText("##");
		endDescr.setText("##");
		lineInfo.setText("链路名称: " + link.getLinkName() + "<br>资源类型:" + " 链路"
				+ "<br>链路上行速率:" + " 正在取值" + "<br>链路下行速率:" + " 正在取值"
				+ "<br>链路上行利用率:" + " 正在取值" + "<br>链路下行利用率:" + " 正在取值");
		lineMenu.setText(NodeHelper.getMenuItem(lineId, startId, endId));

		line.addContent(a);
		line.addContent(b);
		line.addContent(color);
		line.addContent(dash);
		line.addContent(lineWidth);
		line.addContent(alias);
		line.addContent(startDescr);
		line.addContent(endDescr);
		line.addContent(lineInfo);
		line.addContent(lineMenu);
		lines.addContent(line);
	}
//	 创建策略
	public void addPolicyLine(int id,String lineId, String id1, String id2,String width) {
		Element demoLine = new Element("demoLine");
		Element a = new Element("a");
		Element b = new Element("b");
		Element color = new Element("color");
		Element dash = new Element("dash");
		Element lineWidth = new Element("lineWidth");// yangjun add
		Element lineInfo = new Element("lineInfo");// yangjun add
		Element lineMenu = new Element("lineMenu");// yangjun add

		demoLine.setAttribute("id", lineId);
		a.setText(id1);
		b.setText(id2);
		color.setText("blue");
		dash.setText("Solid");
		lineWidth.setText(width);
		lineInfo.setText("示意链路");
		lineMenu.setText(NodeHelper.getMenuLine(id,lineId));

		demoLine.addContent(a);
		demoLine.addContent(b);
		demoLine.addContent(color);
		demoLine.addContent(dash);
		demoLine.addContent(lineWidth);
		demoLine.addContent(lineInfo);
		demoLine.addContent(lineMenu);
		demoLines.addContent(demoLine);
	}
	/**
	 * 增加一条链路 yangjun add
	 */
	public void addLine(String lineName, String lineId, String startId,
			String endId) {
		//SysLogger.info("###### 重建拓扑图11 ###### startId:"+startId+"===endId:"+endId);
		String mstartid = startId;
		if(startId.contains("net")){
			mstartid = mstartid.substring(3);
		}
		String mendid = endId;
		if(endId.contains("net")){
			mendid = mendid.substring(3);
		}
		if(PollingEngine.getInstance().getNodeByID(Integer.parseInt(mstartid)) == null)
			SysLogger.info("mstartid:"+mstartid+" node is null &&&&");
		if(PollingEngine.getInstance().getNodeByID(Integer.parseInt(mendid)) == null)
			SysLogger.info("mendid:"+mendid+" node is null &&&&");
		if(PollingEngine.getInstance().getNodeByID(Integer.parseInt(mstartid)) == null 
				|| PollingEngine.getInstance().getNodeByID(Integer.parseInt(mendid)) == null) 
			return;
		
//		LinkDao dao = new LinkDao();
//		String linkAliasName = dao.getLinkAliasName(Integer.parseInt(lineId));
//		dao.close();
//		if (linkAliasName == null || linkAliasName.equals("") || "null".equals(linkAliasName)) {
//			linkAliasName = lineName;
//		}
		
		Element line = new Element("line");
		Element a = new Element("a");
		Element b = new Element("b");
		Element color = new Element("color");
		Element dash = new Element("dash");
		Element alias = new Element("alias");// yangjun add
		Element startDescr = new Element("start");// yangjun add
		Element endDescr = new Element("end");// yangjun add
		Element lineWidth = new Element("lineWidth");// yangjun add
		Element lineInfo = new Element("lineInfo");// yangjun add
		Element lineMenu = new Element("lineMenu");// yangjun add

		line.setAttribute("id", lineId);
		a.setText(startId);
		b.setText(endId);
		color.setText("green");
		dash.setText("Solid");
		alias.setText("#.#");
		startDescr.setText("##");
		endDescr.setText("##");
		lineWidth.setText("1");
		lineInfo.setText("链路名称: " + lineName + "<br>资源类型:" + " 链路"
				+ "<br>链路上行速率:" + " 正在取值" + "<br>链路下行速率:" + " 正在取值"
				+ "<br>链路上行利用率:" + " 正在取值" + "<br>链路下行利用率:" + " 正在取值");
		lineMenu.setText(NodeHelper.getMenuItem(lineId, startId, endId));

		line.addContent(a);
		line.addContent(b);
		line.addContent(color);
		line.addContent(dash);
		line.addContent(alias);
		line.addContent(startDescr);
		line.addContent(endDescr);
		line.addContent(lineWidth);
		line.addContent(lineInfo);
		line.addContent(lineMenu);
		lines.addContent(line);
	}

	/**
	 * 增加一条辅助链路
	 */
	public void addAssistantLine(String lineId, String startId, String endId) {
		String mstartid = startId;
		if(startId.contains("net")){
			mstartid = mstartid.substring(3);
		}
		String mendid = endId;
		if(endId.contains("net")){
			mendid = mendid.substring(3);
		}
		if(PollingEngine.getInstance().getNodeByID(Integer.parseInt(mstartid)) == null 
				|| PollingEngine.getInstance().getNodeByID(Integer.parseInt(mendid)) == null) 
			return;
		Element line = new Element("assistant_line");
		Element a = new Element("a");
		Element b = new Element("b");
		Element color = new Element("color");
		Element dash = new Element("dash");
		Element alias = new Element("alias");// yangjun add
		Element startDescr = new Element("start");// yangjun add
		Element endDescr = new Element("end");// yangjun add
		Element lineWidth = new Element("lineWidth");// yangjun add
		Element lineInfo = new Element("lineInfo");// yangjun add
		Element lineMenu = new Element("lineMenu");// yangjun add

		line.setAttribute("id", lineId);
		a.setText(startId);
		b.setText(endId);
		color.setText("blue"); // 辅助链路用蓝色表示
		dash.setText("Solid");
		alias.setText("#.#");
		startDescr.setText("##");
		endDescr.setText("##");
		lineWidth.setText("1");
		lineInfo.setText("链路名称: " + lineId + "<br>资源类型:" + " 链路"
				+ "<br><br>链路上行速率:" + " 正在取值" + "<br>链路下行速率:" + " 正在取值"
				+ "<br>链路上行利用率:" + " 正在取值" + "<br>链路下行利用率:" + " 正在取值");
		lineMenu.setText(NodeHelper.getMenuItem(lineId, startId, endId));

		line.addContent(a);
		line.addContent(b);
		line.addContent(color);
		line.addContent(dash);
		line.addContent(alias);
		line.addContent(startDescr);
		line.addContent(endDescr);
		line.addContent(lineWidth);
		line.addContent(lineInfo);
		line.addContent(lineMenu);
		assistantLines.addContent(line);
	}

	/**
	 * 增加一条辅助链路 yangjun add
	 */
	public void addAssistantLine(String lineName, String lineId,
			String startId, String endId) {
		String mstartid = startId;
		if(startId.contains("net")){
			mstartid = mstartid.substring(3);
		}
		String mendid = endId;
		if(endId.contains("net")){
			mendid = mendid.substring(3);
		}
		if(PollingEngine.getInstance().getNodeByID(Integer.parseInt(mstartid)) == null 
				|| PollingEngine.getInstance().getNodeByID(Integer.parseInt(mendid)) == null) 
			return;
		Element line = new Element("assistant_line");
		Element a = new Element("a");
		Element b = new Element("b");
		Element color = new Element("color");
		Element dash = new Element("dash");
		Element alias = new Element("alias");// yangjun add
		Element startDescr = new Element("start");// yangjun add
		Element endDescr = new Element("end");// yangjun add
		Element lineWidth = new Element("lineWidth");// yangjun add
		Element lineInfo = new Element("lineInfo");// yangjun add
		Element lineMenu = new Element("lineMenu");// yangjun add

		line.setAttribute("id", lineId);
		a.setText(startId);
		b.setText(endId);
		color.setText("blue"); // 辅助链路用蓝色表示
		dash.setText("Solid");
		alias.setText("#.#");
		startDescr.setText("##");
		endDescr.setText("##");
		lineWidth.setText("1");
		lineInfo.setText("链路名称: " + lineName + "<br>资源类型:" + " 链路"
				+ "<br>链路上行速率:" + " 正在取值" + "<br>链路下行速率:" + " 正在取值"
				+ "<br>链路上行利用率:" + " 正在取值" + "<br>链路下行利用率:" + " 正在取值");
		lineMenu.setText(NodeHelper.getMenuItem(lineId, startId, endId));

		line.addContent(a);
		line.addContent(b);
		line.addContent(color);
		line.addContent(dash);
		line.addContent(alias);
		line.addContent(startDescr);
		line.addContent(endDescr);
		line.addContent(lineWidth);
		line.addContent(lineInfo);
		line.addContent(lineMenu);
		assistantLines.addContent(line);
	}

	/**
	 * 按xmlid删除一个结点
	 */
	public void deleteNodeByID(String nodeId) {
		List eleNodes = nodes.getChildren();
		int len = eleNodes.size() - 1;
		for (int i = len; i >= 0; i--) {
			Element node = (Element) eleNodes.get(i);
			if (node.getChildText("id").equals(nodeId)) {
				node.getParentElement().removeContent(node);
				deleteLineByNodeID(nodeId); // 删除结点,必然删除与它相关的连线
				break;
			}
		}
	}

	/**
	 * delete line whose startid or endid equals "nodeId"
	 */
	public void deleteLineByNodeID(String nodeId) {
		List eleLines = lines.getChildren();
		List asseleLines = assistantLines.getChildren();
		List demoeleLines = demoLines.getChildren();
		if (eleLines.size() > 0) {// 删除实体链路
			int len = eleLines.size() - 1;
			for (int i = len; i >= 0; i--) // 这里只能用降序，升序可能出错
			{
				Element line = (Element) eleLines.get(i);
				if (line.getChildText("a").equals(nodeId))
					line.getParentElement().removeContent(line);
				else if (line.getChildText("b").equals(nodeId))
					line.getParentElement().removeContent(line);
			}
		}
		if (asseleLines.size() > 0) {// 删除辅助链路
			int len = asseleLines.size() - 1;
			for (int i = len; i >= 0; i--) // 这里只能用降序，升序可能出错
			{
				Element line = (Element) asseleLines.get(i);
				if (line.getChildText("a").equals(nodeId))
					line.getParentElement().removeContent(line);
				else if (line.getChildText("b").equals(nodeId))
					line.getParentElement().removeContent(line);
			}
		}
		if (demoeleLines.size() > 0) {// 删除示意链路
			int len = demoeleLines.size() - 1;
			for (int i = len; i >= 0; i--) // 这里只能用降序，升序可能出错
			{
				Element line = (Element) demoeleLines.get(i);
				if (line.getChildText("a").equals(nodeId))
					line.getParentElement().removeContent(line);
				else if (line.getChildText("b").equals(nodeId))
					line.getParentElement().removeContent(line);
			}
		}

	}

	/**
	 * delete line whose id equals "id"(line id)
	 */
	public void deleteLineByID(String id) {
		List eleLines = lines.getChildren();
		int len = eleLines.size() - 1;
		for (int i = len; i >= 0; i--) // 这里只能用降序，升序可能出错
		{
			Element line = (Element) eleLines.get(i);
			if (line.getAttributeValue("id").equals(id))
				line.getParentElement().removeContent(line);
		}
	}

	// 删除assistant_line yangjun add
	public void deleteAssLineByID(String id) {
		List eleLines = assistantLines.getChildren();
		int len = eleLines.size() - 1;
		for (int i = len; i >= 0; i--) // 这里只能用降序，升序可能出错
		{
			Element assistant_line = (Element) eleLines.get(i);
			if (assistant_line.getAttributeValue("id").equals(id))
				assistant_line.getParentElement().removeContent(assistant_line);
		}
	}

	// 删除示意链路 yangjun add
	public void deleteDemoLinesByID(String id) {
		List eleLines = demoLines.getChildren();
		int len = eleLines.size() - 1;
		for (int i = len; i >= 0; i--) // 这里只能用降序，升序可能出错
		{
			Element demoLines = (Element) eleLines.get(i);
			if (demoLines.getAttributeValue("id").equals(id))
				demoLines.getParentElement().removeContent(demoLines);
		}
	}

	public boolean isNodeExist(String nodeId) {
		boolean result = false;
		if(nodes!=null){
		List nodeList = nodes.getChildren();
		for (int i = 0; i < nodeList.size(); i++) {
			Element ele = (Element) nodeList.get(i);
			if (ele.getChildText("id").equals(nodeId)) {
				result = true;
				break;
				}
			}
		}
		return result;
	}

	// 判断链路是否存在 yangjun add
	public boolean isLinkExist(String linkId) {
		boolean result = false;
		List eleLines = lines.getChildren();
		for (int i = 0; i < eleLines.size(); i++) {
			Element ele = (Element) eleLines.get(i);
			if (ele.getAttributeValue("id").equals(linkId)) {
				result = true;
				break;
			}
		}
		return result;
	}

	// 判断链路是否存在 yangjun add
	public boolean isAssLinkExist(String linkId) {
		boolean result = false;
		List asseleLines = assistantLines.getChildren();
		for (int i = 0; i < asseleLines.size(); i++) {
			Element ele = (Element) asseleLines.get(i);
			if (ele.getAttributeValue("id").equals(linkId)) {
				result = true;
				break;
			}
		}
		return result;
	}

	// 判断链路是否存在 yangjun add
	public boolean isDemoLinkExist(String linkId) {
		boolean result = false;
		List eleLines = demoLines.getChildren();
		for (int i = 0; i < eleLines.size(); i++) {
			Element ele = (Element) eleLines.get(i);
			if (ele.getAttributeValue("id").equals(linkId)) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * 按xmlid删除一个示意结点 yangjun add
	 */
	public void deleteNodeById(String nodeId) {
		List eleNodes = nodes.getChildren();
		int len = eleNodes.size() - 1;
		for (int i = len; i >= 0; i--) {
			Element node = (Element) eleNodes.get(i);
			if (node.getChildText("id").equals(nodeId)) {
				node.getParentElement().removeContent(node);
				deleteLineByNodeId(nodeId); // 删除结点,必然删除与它相关的连线
				break;
			}
		}
	}
	
	public List getAllNode() {
		List list = new ArrayList();
		List eleNodes = nodes.getChildren();
		for(int i=0;i<eleNodes.size();i++){
			Element node = (Element) eleNodes.get(i);
			list.add(node.getChildText("id"));
		}
		return list;
	}
	public List getAllNodes() {
		List list = new ArrayList();
		if(nodes!=null){
			List eleNodes = nodes.getChildren();
			if(eleNodes!=null&&eleNodes.size()>0){
				for(int i=0;i<eleNodes.size();i++){
					Element node = (Element) eleNodes.get(i);
					list.add(node.getChildText("id")+":"+node.getChild("id").getAttributeValue("category"));
				}
			}
		}
		return list;
	}
	/**
	 * delete line whose startid or endid equals "nodeId" yangjun add
	 */
	public void deleteLineByNodeId(String nodeId) {
		List eleLines = demoLines.getChildren();
		int len = eleLines.size() - 1;
		for (int i = len; i >= 0; i--) // 这里只能用降序，升序可能出错
		{
			Element line = (Element) eleLines.get(i);
			if (line.getChildText("a").equals(nodeId))
				line.getParentElement().removeContent(line);
			else if (line.getChildText("b").equals(nodeId))
				line.getParentElement().removeContent(line);
		}
	}

	public List getAlarmMapList() {
		return alarmMapList;
	}
	private String getString(String s) {
		String result = "";
		if(s==null||"".equals(s)||s.trim().length()==0){return "##";}
		result = s.substring(0, 1);
		Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]+|\\d+");
		Matcher m = p.matcher(s);
		while(m.find()){
			result = result + m.group() + "/";
		}
//		System.out.println(result.substring(0, result.lastIndexOf("/")));
		if(result.lastIndexOf("/") == -1){
			return s;
		}
		return result.substring(0, result.lastIndexOf("/"));
	}
}
