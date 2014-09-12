/**
 * <p>Description:logger,writes error and debug information within system running</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.dao.Urlmonitor_historyDao;
import com.afunms.application.manage.TomcatManager;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.model.SybaseVO;
import com.afunms.application.model.TablesVO;
import com.afunms.application.util.IpTranslation;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.topology.dao.HintNodeDao;
import com.afunms.topology.dao.LineDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.dao.NodeMonitorDao;
import com.afunms.topology.dao.TreeNodeDao;
import com.afunms.topology.model.HintLine;
import com.afunms.topology.model.HintNode;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.model.NodeMonitor;
import com.afunms.topology.model.TreeNode;
import com.afunms.topology.util.NodeHelper;


public class ChartXml {
	private File file;
	private String filename;
	private String path;
	private String commonPath;

	public ChartXml(String type) {
		if (type.equals("pie")) {
			filename = "/ampie_data.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "resource/xml/flush/ampie";
		} else if (type.equals("topbar")) {
			filename = "/topBar_data.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "resource/xml/flush/topbar";
		} else if (type.equals("amcolumn")) {
			filename = "/amcolumn_data.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "resource/xml/flush/amcolumn";
		} else if (type.equals("amcolumn1")) {
			filename = "/amcolumn_data1.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "resource/xml/flush/amcolumn";
		} else if (type.equals("amcolumn2")) {
			filename = "/amcolumn_data2.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "resource/xml/flush/amcolumn";
		} else if (type.equals("amcolumn3")) {
			filename = "/amcolumn_data3.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "resource/xml/flush/amcolumn";
		} else if (type.equals("amcolumn4")) {
			filename = "/amcolumn_data4.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "resource/xml/flush/amcolumn";
		} else if (type.equals("amcolumn5")) {
			filename = "/amcolumn_data5.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "resource/xml/flush/amcolumn";
		} else if (type.equals("line")) {
			filename = "/amline_data.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "resource/xml/flush/amline";
		} else if (type.equals("jianshi")) {
			filename = "/catalog.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "jianshi/data";
		} else if (type.equals("databaseflex")) {
			filename = "/catalog.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "databaseflex/data";
		} else if (type.equals("equip")) {
			filename = "/catalog.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "equip/data";
		} else if (type.equals("yewu")) {
			filename = "/pods.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "flex/data";
		} else if (type.equals("yewu1")) {
			filename = "/pods_a.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "flex/data";
		} else if (type.equals("yewu2")) {
			filename = "/pods_b.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "flex/data";
		} else if (type.equals("yewu3")) {
			filename = "/pods_c.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "flex/data";
		} else if (type.equals("yewu4")) {
			filename = "/pods_d.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "flex/data";
		} else if (type.equals("tree")) {
			filename = "/tree.xml";
			commonPath = ResourceCenter.getInstance().getSysPath() + "flex/data";
		}
		path = commonPath + filename;
		// System.out.println(path);
	}

	public ChartXml(String type, String file) {// yangjun add
		if (type.equals("NetworkMonitor")) {
			filename = file;
			commonPath = ResourceCenter.getInstance().getSysPath() + "flex/data";
		}
		path = commonPath + filename;
		// System.out.println(path);
	}

	public Element doOneTerm(String eventtype, String eventsum, int trueflag) throws Exception {
		Element newelement = new Element("slice");
		newelement.setAttribute("title", eventtype);
		if (trueflag == 0) {
			newelement.setAttribute("pull_out", "false");
		} else {
			newelement.setAttribute("pull_out", "true");
		}
		newelement.setText(eventsum);

		return newelement;
	}

	public Element doPartOneTerm() throws Exception {
		Element newelement = new Element("series");
		Element _newelement = new Element("value");
		_newelement.setAttribute("xid", "0");
		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合
		elements.add(_newelement);
		return newelement;
	}

	public Element doLinePartOneTerm() throws Exception {
		Element newelement = new Element("series");

		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合
		for (int i = 0; i < 23; i++) {
			Element _newelement = new Element("value");
			_newelement.setAttribute("xid", i + "");
			_newelement.addContent(i + "");
			elements.add(_newelement);
		}

		return newelement;
	}

	public Element doPartTwoTerm(String index, String values) throws Exception {
		Element newelement = new Element("graph");
		newelement.setAttribute("gid", index);

		Element _newelement = new Element("value");
		_newelement.setAttribute("xid", "0");
		_newelement.setText(values);
		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合
		elements.add(_newelement);

		return newelement;
	}

	public Element doLinePartTwoTerm(String index, Hashtable lineHash, String values) throws Exception {
		Element newelement = new Element("graph");
		newelement.setAttribute("gid", index);
		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合

		if (lineHash.size() != 0) {
			for (int i = 0; i < 24; i++) {
				Integer _values = (Integer) lineHash.get(i);
				if (_values == null)
					_values = 0;
				Element _newelement = new Element("value");
				_newelement.setAttribute("xid", i + "");
				_newelement.setText(_values + "");
				elements.add(_newelement);
			}
		}

		return newelement;
	}

	public Element doAmcolumnPartOneTerm(List list, int topn) throws Exception {
		Element newelement = new Element("series");

		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合
		for (int i = 0; i < list.size(); i++) {
			if (i >= topn)
				break;
			CPUcollectdata hdata = (CPUcollectdata) list.get(i);
			Element _newelement = new Element("value");
			_newelement.setAttribute("xid", i + "");
			_newelement.addContent(hdata.getIpaddress());
			elements.add(_newelement);
		}
		return newelement;
	}

	public Element doFusioncolumnPartOneTerm(List list, int topn) throws Exception {
		Element newelement = new Element("series");

		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合
		for (int i = 0; i < list.size(); i++) {
			if (i >= topn)
				break;
			CPUcollectdata hdata = (CPUcollectdata) list.get(i);
			Element _newelement = new Element("value");
			_newelement.setAttribute("xid", i + "");
			_newelement.addContent(hdata.getIpaddress());
			elements.add(_newelement);
		}
		return newelement;
	}

	public Element doAmcolumnPartOneTerm1(List list, int topn) throws Exception {
		Element newelement = new Element("series");

		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合
		for (int i = 0; i < list.size(); i++) {
			if (i >= topn)
				break;
			AllUtilHdx hdata = (AllUtilHdx) list.get(i);
			Element _newelement = new Element("value");
			_newelement.setAttribute("xid", i + "");
			_newelement.addContent(hdata.getIpaddress());
			elements.add(_newelement);
		}
		return newelement;
	}

	public Element doAmcolumnPartOneTerm2(List list, int topn) throws Exception {
		Element newelement = new Element("series");

		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合
		for (int i = 0; i < list.size(); i++) {
			if (i >= topn)
				break;
			Memorycollectdata hdata = (Memorycollectdata) list.get(i);
			Element _newelement = new Element("value");
			_newelement.setAttribute("xid", i + "");
			_newelement.addContent(hdata.getIpaddress());
			elements.add(_newelement);
		}
		return newelement;
	}

	public Element doAmcolumnPartOneTerm3(List list, int topn) throws Exception {
		Element newelement = new Element("series");

		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合
		for (int i = 0; i < list.size(); i++) {
			if (i >= topn)
				break;
			Diskcollectdata hdata = (Diskcollectdata) list.get(i);
			Element _newelement = new Element("value");
			_newelement.setAttribute("xid", i + "");
			_newelement.addContent(hdata.getIpaddress() + " " + hdata.getSubentity());
			elements.add(_newelement);
		}
		return newelement;
	}

	public Element doAmcolumnPartTwoTerm(String index, List list, String values, int topn) throws Exception {
		Element newelement = new Element("graph");
		newelement.setAttribute("gid", index);
		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合
		Hashtable colors = new Hashtable();
		colors.put(0, "FF0F00");
		colors.put(1, "FF6600");
		colors.put(2, "FF9E01");
		colors.put(3, "FCD202");
		colors.put(4, "F8FF01");
		colors.put(5, "B0DE09");
		colors.put(6, "04D215");
		colors.put(7, "0D8ECF");
		colors.put(8, "0D52D1");
		colors.put(9, "2A0CD0");
		if (list != null && list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				if (i >= topn)
					break;
				CPUcollectdata hdata = (CPUcollectdata) list.get(i);
				// if(_values == null )_values = 0;
				Element _newelement = new Element("value");
				_newelement.setAttribute("xid", i + "");
				_newelement.setAttribute("color", (String) colors.get(i));
				_newelement.setText(hdata.getThevalue() + "");
				elements.add(_newelement);
			}
		}

		return newelement;
	}

	public Element doFusioncolumnPartTwoTerm(String index, List list, String values, int topn) throws Exception {
		Element newelement = new Element("graph");
		newelement.setAttribute("gid", index);
		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合
		Hashtable colors = new Hashtable();
		colors.put(0, "FF0F00");
		colors.put(1, "FF6600");
		colors.put(2, "FF9E01");
		colors.put(3, "FCD202");
		colors.put(4, "F8FF01");
		colors.put(5, "B0DE09");
		colors.put(6, "04D215");
		colors.put(7, "0D8ECF");
		colors.put(8, "0D52D1");
		colors.put(9, "2A0CD0");
		if (list != null && list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				if (i >= topn)
					break;
				CPUcollectdata hdata = (CPUcollectdata) list.get(i);
				// if(_values == null )_values = 0;
				Element _newelement = new Element("value");
				_newelement.setAttribute("xid", i + "");
				_newelement.setAttribute("color", (String) colors.get(i));
				_newelement.setText(hdata.getThevalue() + "");
				elements.add(_newelement);
			}
		}

		return newelement;
	}

	public Element doAmcolumnPartTwoTerm1(String index, List list, String values, int topn) throws Exception {
		Element newelement = new Element("graph");
		newelement.setAttribute("gid", index);
		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合
		Hashtable colors = new Hashtable();
		colors.put(0, "FF0F00");
		colors.put(1, "FF6600");
		colors.put(2, "FF9E01");
		colors.put(3, "FCD202");
		colors.put(4, "F8FF01");
		colors.put(5, "B0DE09");
		colors.put(6, "04D215");
		colors.put(7, "0D8ECF");
		colors.put(8, "0D52D1");
		colors.put(9, "2A0CD0");
		if (list != null && list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				if (i >= topn)
					break;
				AllUtilHdx hdata = (AllUtilHdx) list.get(i);
				// if(_values == null )_values = 0;
				Element _newelement = new Element("value");
				_newelement.setAttribute("xid", i + "");
				_newelement.setAttribute("color", (String) colors.get(i));
				_newelement.setText(hdata.getThevalue() + "");
				elements.add(_newelement);
				SysLogger.info(hdata.getThevalue() + "====" + hdata.getIpaddress());
			}
		}

		return newelement;
	}

	public Element doAmcolumnPartTwoTerm2(String index, List list, String values, int topn) throws Exception {
		Element newelement = new Element("graph");
		newelement.setAttribute("gid", index);
		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合
		Hashtable colors = new Hashtable();
		colors.put(0, "FF0F00");
		colors.put(1, "FF6600");
		colors.put(2, "FF9E01");
		colors.put(3, "FCD202");
		colors.put(4, "F8FF01");
		colors.put(5, "B0DE09");
		colors.put(6, "04D215");
		colors.put(7, "0D8ECF");
		colors.put(8, "0D52D1");
		colors.put(9, "2A0CD0");
		if (list != null && list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				if (i >= topn)
					break;
				Memorycollectdata hdata = (Memorycollectdata) list.get(i);
				// if(_values == null )_values = 0;
				Element _newelement = new Element("value");
				_newelement.setAttribute("xid", i + "");
				_newelement.setAttribute("color", (String) colors.get(i));
				_newelement.setText((int) Math.rint(new Double(hdata.getThevalue())) + "");
				elements.add(_newelement);
				// SysLogger.info(hdata.getThevalue()+"===="+hdata.getIpaddress());
			}
		}

		return newelement;
	}

	public Element doAmcolumnPartTwoTerm3(String index, List list, String values, int topn) throws Exception {
		Element newelement = new Element("graph");
		newelement.setAttribute("gid", index);
		List elements = newelement.getChildren(); // 得到根元素所有子元素的集合
		Hashtable colors = new Hashtable();
		colors.put(0, "FF0F00");
		colors.put(1, "FF6600");
		colors.put(2, "FF9E01");
		colors.put(3, "FCD202");
		colors.put(4, "F8FF01");
		colors.put(5, "B0DE09");
		colors.put(6, "04D215");
		colors.put(7, "0D8ECF");
		colors.put(8, "0D52D1");
		colors.put(9, "2A0CD0");
		if (list != null && list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				if (i >= topn)
					break;
				Diskcollectdata hdata = (Diskcollectdata) list.get(i);
				// if(_values == null )_values = 0;
				Element _newelement = new Element("value");
				_newelement.setAttribute("xid", i + "");
				_newelement.setAttribute("color", (String) colors.get(i));
				_newelement.setText((int) Math.rint(new Double(hdata.getThevalue())) + "");
				elements.add(_newelement);
				// SysLogger.info(hdata.getThevalue()+"===="+hdata.getIpaddress());
			}
		}

		return newelement;
	}

	public Element doTopBarOneTerm(String eventtype, String eventsum, int trueflag) throws Exception {
		Element newelement = new Element("graphs");
		newelement.setAttribute("title", eventtype);
		if (trueflag == 0) {
			newelement.setAttribute("pull_out", "false");
		} else {
			newelement.setAttribute("pull_out", "true");
		}
		newelement.setText(eventsum);

		return newelement;
	}

	public void AddXML(List list) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("pie");
			Document doc = new Document(root);

			// root.setAttribute("signw","1");
			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			if (list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					Vector eventsum = (Vector) list.get(i);
					if (i == 0) {
						Element newelement = doOneTerm((String) eventsum.get(0), (String) eventsum.get(1), 1);
						elements.add(newelement);// 增加子元素
					} else {
						Element newelement = doOneTerm((String) eventsum.get(0), (String) eventsum.get(1), 0);
						elements.add(newelement);// 增加子元素
					}
				}
			}
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			// root.setAttribute("signw","0");
			outp.output(doc, fo);
		} catch (Exception e) {
			// System.err.println("in infopoint xml add list error "+e);
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 业务视图xml yangjun add
	public void addViewTree(List list) {
//		ServletContext context = FlexContext.getServletContext();
//		String path_ = context.getRealPath("/flex/data/init.properties");
//		Properties props = new Properties();
//		String[] strs = {"map_a","map_b","map_c","map_d"};
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("folder");
			Document doc = new Document(root);
			Element nelement = new Element("folder");
			List elements = nelement.getChildren(); // 得到根元素所有子元素的集合
			nelement.setAttribute("id", "");
			nelement.setAttribute("label", "业务资源一览");
			if(list!=null&&list.size()>0){
				for (int i = 0; i < list.size(); i++) {
					ManageXml vo = (ManageXml) list.get(i);
					
//            		InputStream fis = new FileInputStream(path_);
//                    // 从输入流中读取属性列表（键和元素对）
//                    props.load(fis);
//                    OutputStream fos = new FileOutputStream(path_);
//            		props.setProperty(strs[i], vo.getXmlName().replace("jsp", "xml"));
//                    // 将此 Properties表中的属性列表（键和元素对）写入输出流
//                    props.store(fos, "Update '" + strs[i] + "'value");

					Element newelement = new Element("folder");
					newelement.setAttribute("id", "bussiness"+vo.getId());
					newelement.setAttribute("label", vo.getTopoName());
					newelement.setAttribute("dataSource", "data/"
							+ vo.getXmlName().replace("businessmap", "list").replace("jsp", "xml"));
					newelement.setAttribute("xml",vo.getXmlName().replace("jsp", "xml"));
					
					NodeDependDao nodeDependDao = new NodeDependDao();
					try {
						List nodelist = nodeDependDao.findByXml(vo.getXmlName());
						if(nodelist!=null&&nodelist.size()>0){
						    for(int j=0;j<nodelist.size();j++){
						    	NodeDepend nodevo = (NodeDepend) nodelist.get(j);	
						    	Element newelements = new Element("folder");
						    	newelements.setAttribute("id", nodevo.getNodeId());
						    	Node fnode = null;
								TreeNodeDao ftreeNodeDao = new TreeNodeDao();
								TreeNode fvo = null;
								try {
									fvo = (TreeNode) ftreeNodeDao.findByNodeTag(nodevo.getNodeId().substring(0, 3));
								} catch (RuntimeException e) {
									e.printStackTrace();
								} finally {
									ftreeNodeDao.close();
								}
								if (fvo != null) {
									fnode = PollingEngine.getInstance().getNodeByCategory(fvo.getName(),
											Integer.parseInt(nodevo.getNodeId().substring(3)));
								}
								
						    	if ("".equals(nodevo.getAlias()) || nodevo.getAlias() == null) {
						    		if (fnode != null&&!"".equals(fnode.getType()) && fnode.getType() != null) {
						    			newelements.setAttribute("label", "未命名("+fnode.getType()+")");
									} else {
										newelements.setAttribute("label", "未命名(未知类型)");
									}
								} else {
									if (fnode != null&&!"".equals(fnode.getType()) && fnode.getType() != null) {
						    			newelements.setAttribute("label", nodevo.getAlias()+"("+fnode.getType()+")");
									} else {
										newelements.setAttribute("label", nodevo.getAlias()+"(未知类型)");
									}
								}
						    	newelement.addContent(newelements);
							}
							 
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						nodeDependDao.close();
					}
					
					elements.add(newelement);
					
				}
			}
			root.addContent(nelement);
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void addViewXML(List list) {//yangjun
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("views");
			Document doc = new Document(root);
			Element nelement = new Element("view");
			List elements = nelement.getChildren(); // 得到根元素所有子元素的集合
			nelement.setAttribute("id", "view0");
			nelement.setAttribute("label", "业务视图一览");
			String str = "pod0";
			for (int i = 0; i < list.size(); i++) {
				ManageXml vo = (ManageXml) list.get(i);
				Element newelement = new Element("pod");
				newelement.setAttribute("id", str + (i + 1));
				newelement.setAttribute("type", "list");
				newelement.setAttribute("title", vo.getTopoName());
				newelement.setAttribute("dataSource", "data/"
						+ vo.getXmlName().replace("businessmap", "list").replace("jsp", "xml"));
				elements.add(newelement);
			}
			root.addContent(nelement);
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void addViewXML(String filename, String name, String id) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("views");
			Document doc = new Document(root);
			Element nelement = new Element("view");
			List elements = nelement.getChildren(); // 得到根元素所有子元素的集合
			nelement.setAttribute("id", "view0");
			nelement.setAttribute("label", "业务视图一览");
			Element newelement = new Element("pod");
			newelement.setAttribute("id", id);
			newelement.setAttribute("type", "topo");
			newelement.setAttribute("title", name);
			newelement.setAttribute("dataSource", "data/" + filename);
			elements.add(newelement);
			root.addContent(nelement);
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void addListXML(String title, List list) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("items");
			Document doc = new Document(root);
			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			for (int i = 0; i < list.size(); i++) {
				NodeDepend node = (NodeDepend) list.get(i);
				// 从内存中获取节点信息
				Node fnode = null;
				TreeNodeDao ftreeNodeDao = new TreeNodeDao();
				TreeNode fvo = null;
				try {
					fvo = (TreeNode) ftreeNodeDao.findByNodeTag(node.getNodeId().substring(0, 3));
				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					ftreeNodeDao.close();
				}
				if (fvo != null) {
					fnode = PollingEngine.getInstance().getNodeByCategory(fvo.getName(),
							Integer.parseInt(node.getNodeId().substring(3)));
				}
				Element newelement = new Element("item");

				if ("".equals(node.getAlias()) || node.getAlias() == null) {
					newelement.setAttribute("name", "未命名");
				} else {
					newelement.setAttribute("name", node.getAlias());
				}
				if (fnode != null) {
					if (!"".equals(fnode.getType()) && fnode.getType() != null) {
						newelement.setAttribute("typeName", fnode.getType());
					} else {
						newelement.setAttribute("typeName", "未知类型");
					}
					if (fnode.getCategory() == 1 || fnode.getCategory() == 7 || fnode.getCategory() == 2
							|| fnode.getCategory() == 3 || fnode.getCategory() == 4 || fnode.getCategory() == 8) {
						newelement.setAttribute("ping", getPingData(fnode.getIpAddress()) + "%");
					} else {
						newelement.setAttribute("ping", " ");
					}
					if (fnode.isAlarm()) {
						newelement.setAttribute("image", "images/alert.gif");
					} else {
						newelement.setAttribute("image", "images/status_ok.gif");
					}
					if (fnode.getCategory() == 1 || fnode.getCategory() == 7) {
						newelement.setAttribute("type", "images/router_icon.png");
					} else if (fnode.getCategory() == 2 || fnode.getCategory() == 3) {
						newelement.setAttribute("type", "images/switch_icon.png");
					} else if (fnode.getCategory() == 4) {
						newelement.setAttribute("type", "images/server_icon.png");
					} else if (fnode.getCategory() == 8) {
						newelement.setAttribute("type", "images/firewall_icon.png");
					} else if (fnode.getCategory() == 51) {
						newelement.setAttribute("type", "images/tomcat_icon.png");
						newelement.setAttribute("ping", getTomPing(fnode.getIpAddress()) + "%");
					} else if (fnode.getCategory() == 52) {
						newelement.setAttribute("type", "images/dbs_icon.png");
						newelement.setAttribute("ping", getDbPing(fnode.getId()+"", "MYPing") + "%");
					} else if (fnode.getCategory() == 53) {
						newelement.setAttribute("type", "images/dbs_icon.png");
						newelement.setAttribute("ping", getDbPing(fnode.getId()+"", "ORAPing") + "%");
					} else if (fnode.getCategory() == 54) {
						newelement.setAttribute("type", "images/dbs_icon.png");
						newelement.setAttribute("ping", getDbPing(fnode.getId()+"", "SQLPing") + "%");
					} else if (fnode.getCategory() == 55) {
						newelement.setAttribute("type", "images/dbs_icon.png");
						newelement.setAttribute("ping", getDbPing(fnode.getId()+"", "SYSPing") + "%");
					} else if (fnode.getCategory() == 59) {
						newelement.setAttribute("type", "images/dbs_icon.png");
						newelement.setAttribute("ping", getDbPing(fnode.getId()+"", "DB2Ping") + "%");
					} else if (fnode.getCategory() == 60) {
						newelement.setAttribute("type", "images/dbs_icon.png");
						newelement.setAttribute("ping", getDbPing(fnode.getId()+"", "INFORMIXPing") + "%");
					} else if (fnode.getCategory() == 56) {
						newelement.setAttribute("type", "images/mail_icon.png");
					} else if (fnode.getCategory() == 57) {
						newelement.setAttribute("ping", getWebPing(fnode.getId() + "") + "%");
						newelement.setAttribute("type", "images/wes_icon.png");
					} else if (fnode.getCategory() == 58) {
						newelement.setAttribute("type", "images/ftp_icon.png");
					} else if (fnode.getCategory() == 101) {
						newelement.setAttribute("type", "images/ups_icon.png");
					} else if (fnode.getCategory() == 61) {
						newelement.setAttribute("type", "images/mqs_icon.png");
					} else if (fnode.getCategory() == 62) {
						newelement.setAttribute("type", "images/domino_icon.png");
					} else if (fnode.getCategory() == 63) {
						newelement.setAttribute("type", "images/was_icon.png");
					} else if (fnode.getCategory() == 64) {
						newelement.setAttribute("type", "images/weblogic_icon.png");
					} else if (fnode.getCategory() == 65) {
						newelement.setAttribute("type", "images/cics_icon.png");
					} else if (fnode.getCategory() == 67) {
						newelement.setAttribute("type", "images/iis_icon.png");
					} else if (fnode.getCategory() == 80) {
						newelement.setAttribute("type", "images/bus.gif");
					} else if (fnode.getCategory() == 69) {
						newelement.setAttribute("type", "images/pro.gif");
					} else if (fnode.getCategory() == 81) {
						newelement.setAttribute("type", "images/interface.gif");
					}
				} else {
					newelement.setAttribute("typeName", "示意");
					newelement.setAttribute("ping", "0%");
					newelement.setAttribute("image", "images/status_ok.gif");
					newelement.setAttribute("type", " ");
				}
				elements.add(newelement);
			}
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void addBussinessXML(String title, List list) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("devices");
			root.setAttribute("title", title);
			Document doc = new Document(root);

			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			for (int i = 0; i < list.size(); i++) {
				NodeDepend node = (NodeDepend) list.get(i);
				// 从内存中获取节点信息
				Node fnode = null;
				TreeNodeDao ftreeNodeDao = new TreeNodeDao();
				TreeNode fvo = null;
				try {
					fvo = (TreeNode) ftreeNodeDao.findByNodeTag(node.getNodeId().substring(0, 3));
				} catch (Exception e1) {
					e1.printStackTrace();
				} finally {
					ftreeNodeDao.close();
				}
				if (fvo != null) {
					fnode = PollingEngine.getInstance().getNodeByCategory(fvo.getName(),
							Integer.parseInt(node.getNodeId().substring(3)));
				}
				// 获取子节点
				LineDao lineDao = new LineDao();
				List childList = null;
				try {
					childList = lineDao.getChildList(node.getXmlfile(), node.getNodeId());
				} catch (RuntimeException e1) {
					e1.printStackTrace();
				} finally {
					lineDao.close();
				}
				String child = "";
				if (childList != null && childList.size() > 0) {
					for (int j = 0; j < childList.size(); j++) {
						HintLine vo = (HintLine) childList.get(j);
						child = child + vo.getChildId() + ",";
					}
				}
				Element newelement = new Element("device");
				Element id = new Element("id");
				Element ip = new Element("ip");
				Element name = new Element("name");
				Element type = new Element("type");
				Element iconUrl = new Element("iconUrl");
				Element typeName = new Element("typeName");
				Element cpu = new Element("cpu");
				Element memory = new Element("memory");
				Element incoming = new Element("incoming");
				Element outgoing = new Element("outgoing");
				Element ping = new Element("ping");
				Element x = new Element("x");
				Element y = new Element("y");
				Element element = new Element("connections");
				Element alarmCpu = new Element("alarmCpu");
				Element alarmMemory = new Element("alarmMemory");
				Element alarmIncoming = new Element("alarmIncoming");
				Element alarmOutgoing = new Element("alarmOutgoing");
				Element alarmFlag = new Element("alarmFlag");
				id.setText(node.getNodeId());
				newelement.addContent(id);
				if ("".equals(node.getAlias()) || node.getAlias() == null) {
					name.setText("未命名");
				} else {
					name.setText(node.getAlias());
				}
				newelement.addContent(name);
				if (fnode != null) {
					if (fnode.getIpAddress() == null || "".equals(fnode.getIpAddress())) {
						ip.setText("无IP");
					} else {
						ip.setText(fnode.getIpAddress());
					}
					newelement.addContent(ip);
					if (fnode.getCategory() == 1 || fnode.getCategory() == 7) {
						type.setText("net_router");
					} else if (fnode.getCategory() == 2 || fnode.getCategory() == 3) {
						type.setText("net_switch");
					} else if (fnode.getCategory() == 4) {
						type.setText("net_server");
					} else if (fnode.getCategory() == 8) {
						type.setText("net_firewall");
					} else {
						type.setText(fvo.getName());
					}
					newelement.addContent(type);
					if (!"".equals(fnode.getType()) && fnode.getType() != null) {
						typeName.setText(fnode.getType());
					} else {
						typeName.setText("未知类型");
					}
					newelement.addContent(typeName);
					if (fnode.getCategory() == 1 || fnode.getCategory() == 7 || fnode.getCategory() == 2
							|| fnode.getCategory() == 3 || fnode.getCategory() == 4 || fnode.getCategory() == 8) {
						cpu.setText((int) getCpu(fnode.getIpAddress()) + "");
						memory.setText((int) getMemory(fnode.getIpAddress()) + "");
						ping.setText(getPingData(fnode.getIpAddress()));
						incoming.setText(getIncoming(fnode.getIpAddress()));
						outgoing.setText(getOutgoing(fnode.getIpAddress()));
						NodeMonitorDao nodeMonitorDao = new NodeMonitorDao();
						List mlist = null;
						try {
							mlist = nodeMonitorDao.loadByNodeID(fnode.getId());
						} catch (RuntimeException e) {
							e.printStackTrace();
						} finally {
							nodeMonitorDao.close();
						}
						if (mlist != null && mlist.size() > 0) {
							for (int j = 0; j < mlist.size(); j++) {
								NodeMonitor nodeMonitor = (NodeMonitor) mlist.get(j);
								if ("cpu".equals(nodeMonitor.getCategory())) {
									alarmCpu.setText(nodeMonitor.getLimenvalue0() + "");
								} else if ("memory".equals(nodeMonitor.getCategory())) {
									alarmMemory.setText(nodeMonitor.getLimenvalue0() + "");
								} else if ("interface".equals(nodeMonitor.getCategory())) {
									String pattern = "####0.00";
									DecimalFormat df = new DecimalFormat(pattern);
									alarmIncoming.setText(df.format(nodeMonitor.getLimenvalue0() / 1024.0) + "");
									alarmOutgoing.setText(df.format(nodeMonitor.getLimenvalue0() / 1024.0) + "");
								}
							}
							if (alarmCpu.getText() == null || "".equals(alarmCpu.getText())) {
								alarmCpu.setText("-1");
								cpu.setText("-1");
							}
							if (alarmMemory.getText() == null || "".equals(alarmMemory.getText())) {
								alarmMemory.setText("-1");
								memory.setText("-1");
							}
							if (alarmIncoming.getText() == null || "".equals(alarmIncoming.getText())) {
								alarmIncoming.setText("-1");
								incoming.setText("-1");
							}
							if (alarmOutgoing.getText() == null || "".equals(alarmOutgoing.getText())) {
								alarmOutgoing.setText("-1");
								outgoing.setText("-1");
							}
						} else {
							alarmCpu.setText("-1");
							alarmMemory.setText("-1");
							alarmIncoming.setText("-1");
							alarmOutgoing.setText("-1");

						}
					} else {
						cpu.setText("-1");
						memory.setText("-1");
						incoming.setText("-1");
						outgoing.setText("-1");
						ping.setText("-1");
						alarmCpu.setText("-1");
						alarmMemory.setText("-1");
						alarmIncoming.setText("-1");
						alarmOutgoing.setText("-1");
					}
					if (fnode.getCategory() == 52) {
						ping.setText(getDbPing(fnode.getId()+"", "MYPing"));
					}
					if (fnode.getCategory() == 53) {
						OraclePartsDao partdao = new OraclePartsDao();
						List<OracleEntity> oracleparts = new ArrayList<OracleEntity>();
						try {
							oracleparts = partdao.findOracleParts(fnode.getId(), 1);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (partdao != null)
								partdao.close();
						}
						if(oracleparts!=null&&oracleparts.size()>0){
							OracleEntity oracle = oracleparts.get(0);
							ping.setText(getDbPing(oracle.getId()+"", "ORAPing"));
						} else {
							ping.setText("0");
						}
					}
					if (fnode.getCategory() == 54) {
						ping.setText(getDbPing(fnode.getIpAddress(), "SQLPing"));
					}
					if (fnode.getCategory() == 55) {
						ping.setText(getDbPing(fnode.getIpAddress(), "SYSPing"));
					}
					if (fnode.getCategory() == 59) {
						ping.setText(getDbPing(fnode.getIpAddress(), "DB2Ping"));
					}
					if (fnode.getCategory() == 60) {
						ping.setText(getDbPing(fnode.getIpAddress(), "INFORMIXPing"));
					}
					if (fnode.getCategory() == 51) {
						ping.setText(getTomPing(fnode.getIpAddress()));
					}
					if (fnode.getCategory() == 57) {
						ping.setText(getWebPing(fnode.getId() + ""));
					}
					newelement.addContent(cpu);
					newelement.addContent(memory);
					newelement.addContent(incoming);
					newelement.addContent(outgoing);
					newelement.addContent(alarmCpu);
					newelement.addContent(ping);
					newelement.addContent(alarmMemory);
					newelement.addContent(alarmIncoming);
					newelement.addContent(alarmOutgoing);
					alarmFlag.setText(fnode.isAlarm() + "");
					newelement.addContent(alarmFlag);
				} else {
					type.setText("icon");
					newelement.addContent(type);
					HintNodeDao hintNodeDao = new HintNodeDao();
					HintNode hintNode = null;
					try {
						hintNode = (HintNode) hintNodeDao.findById(node.getNodeId(), node.getXmlfile());
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						hintNodeDao.close();
					}
					if (hintNode != null) {
						iconUrl.setText(hintNode.getImage().replace("/afunms", ".."));
					}
					newelement.addContent(iconUrl);
				}
				x.setText(node.getLocation().split(",")[0].substring(0, node.getLocation().split(",")[0].lastIndexOf("px")));
				y.setText(node.getLocation().split(",")[1].substring(0, node.getLocation().split(",")[1].lastIndexOf("px")));
				newelement.addContent(x);
				newelement.addContent(y);
				String conn[] = child.split(",");
				if (conn != null && conn.length > 0 && !"".equals(conn[0])) {
					for (int j = 0; j < conn.length; j++) {
						Element connection = new Element("connection");
						connection.setText(conn[j]);
						element.addContent(connection);
					}
					newelement.addContent(element);
				}
				elements.add(newelement);
			}

			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getAvgCpu(String ip, String starttime, String endtime) {
		I_HostCollectData hostManager = new HostCollectDataManager();
		Hashtable cpuHash = new Hashtable();
		String avgcpucon = "0%";
		try {
			cpuHash = hostManager.getCategory(ip, "CPU", "Utilization", starttime, endtime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cpuHash.get("avgcpucon") != null) {
			avgcpucon = (String) cpuHash.get("avgcpucon");
		}
		return avgcpucon.replace("%", "");

	}

	public String getAvgMemory(String ip, String starttime, String endtime) {
		I_HostCollectData hostManager = new HostCollectDataManager();
		String avgmemory = "0%";
		Hashtable[] memoryHash = null;
		try {
			memoryHash = hostManager.getMemory(ip, "Memory", starttime, endtime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (memoryHash != null && memoryHash.length > 0) {
			Hashtable physicalMemoryHash = new Hashtable();
			physicalMemoryHash = (Hashtable) memoryHash[2];
			if (physicalMemoryHash.get("PhysicalMemory") != null) {
				avgmemory = (String) physicalMemoryHash.get("PhysicalMemory");
			}
		}
		return avgmemory.replace("%", "");
	}

	public String getAvgPing(String ip) {
		Calendar curDate_s = Calendar.getInstance();
		curDate_s.add(Calendar.HOUR_OF_DAY, -24);
		String start_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(curDate_s.getTime());
		String end_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		I_HostCollectData hostManager = new HostCollectDataManager();
		Hashtable pingHash = new Hashtable();
		String avgpingcon = "0%";
		try {
			pingHash = hostManager.getCategory(ip, "Ping", "ConnectUtilization", start_time, end_time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (pingHash.get("avgpingcon") != null) {
			avgpingcon = (String) pingHash.get("avgpingcon");
		}
		// System.out.println("==========================================="+avgpingcon);
		return avgpingcon.replace("%", "");

	}

	public double getCpu(String ip) {
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		double cpuvalue = -1;
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {
				CPUcollectdata cpu = (CPUcollectdata) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}

		}
		return cpuvalue;
	}

	public String getIncoming(String ip) {
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		String inhdx = "-1";
		String pattern = "####.##";
		DecimalFormat df = new DecimalFormat(pattern);
		if (ipAllData != null) {
			Vector allutil = (Vector) ipAllData.get("allutilhdx");
			if (allutil != null && allutil.size() > 0) {
				for (int si = 0; si < allutil.size(); si++) {
					AllUtilHdx allutilhdx = (AllUtilHdx) allutil.elementAt(si);
					if (allutilhdx.getRestype().equals("dynamic")
							&& allutilhdx.getSubentity().equalsIgnoreCase("AllInBandwidthUtilHdx")) {
						inhdx = df.format(Double.parseDouble(allutilhdx.getThevalue()) / 1024.0) + "";
						return inhdx;
					}
				}
			}
		}
		return inhdx;
	}

	public double getMemory(String ip) {
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		double memo = -1;
		if (ipAllData != null) {
			Vector memoryVector = (Vector) ipAllData.get("memory");
			if (memoryVector != null && memoryVector.size() > 0) {
				for (int si = 0; si < memoryVector.size(); si++) {
					Memorycollectdata memorydata = (Memorycollectdata) memoryVector.get(si);
					if (memorydata.getRestype().equals("dynamic") && memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory")) {
						memo = new Double(memorydata.getThevalue());
						return memo;
					}
				}
			}
		}
		return memo;
	}

	public String getPingData(String ip) {
		Hashtable ipAllData = (Hashtable) ShareData.getPingdata();
		if (ipAllData != null) {
			Vector pingVector = (Vector) ipAllData.get(ip);
			if (pingVector != null && pingVector.size() > 0) {
				Pingcollectdata pingcollectdata = (Pingcollectdata) pingVector.get(0);
				return pingcollectdata.getThevalue();
			}
		}
		return "-1";
	}

	// yangjun add 获取数据库当前连通值
	public String getDbPing(String ip, String type) {
		I_HostCollectData hostmanager = new HostCollectDataManager();
		Hashtable ConnectUtilizationhash = new Hashtable();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		try {
			System.out.println("--------------"+ip);
			ConnectUtilizationhash = hostmanager.getCategory(ip, type, "ConnectUtilization", startTime, endTime, "");
			List pingList = new ArrayList();
			pingList = (List) ConnectUtilizationhash.get("list");
			if (pingList != null && pingList.size() > 0) {
				Vector pingVector = new Vector();
				pingVector = (Vector) pingList.get(pingList.size() - 1);
				return (String) pingVector.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";

	}

	public String getWebPing(String id) {
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Urlmonitor_historyDao historydao = new Urlmonitor_historyDao();
		Hashtable pingHash = new Hashtable();
		try {
			pingHash = historydao.getPingData(Integer.parseInt(id), startTime, endTime, "");
			List pingList = new ArrayList();
			pingList = (List) pingHash.get("list");
			if (pingList != null && pingList.size() > 0) {
				Vector pingVector = new Vector();
				pingVector = (Vector) pingList.get(pingList.size() - 1);
				return Integer.parseInt((String) pingVector.get(0)) * 100 + "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			historydao.close();
		}
		return "0";
	}

	public String getTomPing(String ip) {
		TomcatManager tomcatManager = new TomcatManager();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Hashtable pingHash = new Hashtable();
		try {
			pingHash = tomcatManager.getCategory(ip, "TomcatPing", "ConnectUtilization", startTime, endTime, "");
			List pingList = new ArrayList();
			pingList = (List) pingHash.get("list");
			if (pingList != null && pingList.size() > 0) {
				Vector pingVector = new Vector();
				pingVector = (Vector) pingList.get(pingList.size() - 1);
				return (String) pingVector.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}

	public String getOutgoing(String ip) {
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		String outhdx = "-1";
		String pattern = "####0.00";
		DecimalFormat df = new DecimalFormat(pattern);
		if (ipAllData != null) {
			Vector allutil = (Vector) ipAllData.get("allutilhdx");
			if (allutil != null && allutil.size() > 0) {
				for (int si = 0; si < allutil.size(); si++) {
					AllUtilHdx allutilhdx = (AllUtilHdx) allutil.elementAt(si);
					if (allutilhdx.getRestype().equals("dynamic")
							&& allutilhdx.getSubentity().equalsIgnoreCase("AllOutBandwidthUtilHdx")) {
						outhdx = df.format(Double.parseDouble(allutilhdx.getThevalue()) / 1024.0) + "";
						return outhdx;
					}
				}
			}
		}
		return outhdx;
	}

	public void AddAmcolumsXML(List list, int topn) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("chart");
			Document doc = new Document(root);

			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			Element newelement = doAmcolumnPartOneTerm(list, topn);
			elements.add(newelement);// 增加子元素

			Element graphs_element = new Element("graphs");
			List graphs_elements = graphs_element.getChildren();

			graphs_elements.add(doAmcolumnPartTwoTerm("0", list, "", topn));

			elements.add(graphs_element);
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			// root.setAttribute("signw","0");
			outp.output(doc, fo);
		} catch (Exception e) {
			// System.err.println("in infopoint xml add list error "+e);
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void AddFusioncolumsXML3(List list, int topn) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("graph");
			root.setAttribute("caption", "CPU利用率");
			root.setAttribute("xAxisName", "");
			root.setAttribute("yAxisName", "%");
			root.setAttribute("decimalPrecision", "0");
			root.setAttribute("formatNumberScale", "0");
			Document doc = new Document(root);
			Hashtable colors = new Hashtable();
			colors.put(0, "FF0F00");
			colors.put(1, "FF6600");
			colors.put(2, "FF9E01");
			colors.put(3, "FCD202");
			colors.put(4, "F8FF01");
			colors.put(5, "B0DE09");
			colors.put(6, "04D215");
			colors.put(7, "0D8ECF");
			colors.put(8, "0D52D1");
			colors.put(9, "2A0CD0");
			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			if (list != null && list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					if (i >= topn)
						break;
					CPUcollectdata hdata = (CPUcollectdata) list.get(i);
					Element _newelement = new Element("set");
					_newelement.setAttribute("name", hdata.getIpaddress());
					_newelement.setAttribute("value", hdata.getThevalue());
					_newelement.setAttribute("color", (String) colors.get(i));
					elements.add(_newelement);
				}
			}
			// Format format = Format.getCompactFormat();
			// format.setEncoding("UTF-8");
			// format.setIndent(" ");
			Format format = Format.getCompactFormat();
			format.setEncoding("GBK");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);
			// XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			// System.err.println("in infopoint xml add list error "+e);
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void AddFusioncolumsXML2(List list, int topn) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("graph");
			root.setAttribute("caption", "出口流速");
			root.setAttribute("xAxisName", "");
			root.setAttribute("yAxisName", "b/s");
			root.setAttribute("decimalPrecision", "0");
			root.setAttribute("formatNumberScale", "0");
			Document doc = new Document(root);
			Hashtable colors = new Hashtable();
			colors.put(0, "FF0F00");
			colors.put(1, "FF6600");
			colors.put(2, "FF9E01");
			colors.put(3, "FCD202");
			colors.put(4, "F8FF01");
			colors.put(5, "B0DE09");
			colors.put(6, "04D215");
			colors.put(7, "0D8ECF");
			colors.put(8, "0D52D1");
			colors.put(9, "2A0CD0");
			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			if (list != null && list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					if (i >= topn)
						break;
					AllUtilHdx hdata = (AllUtilHdx) list.get(i);
					Element _newelement = new Element("set");
					_newelement.setAttribute("name", hdata.getIpaddress());
					_newelement.setAttribute("value", hdata.getThevalue());
					_newelement.setAttribute("color", (String) colors.get(i));
					elements.add(_newelement);
				}
			}
			// Format format = Format.getCompactFormat();
			// format.setEncoding("UTF-8");
			// format.setIndent(" ");
			Format format = Format.getCompactFormat();
			format.setEncoding("GBK");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);
			// XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			// System.err.println("in infopoint xml add list error "+e);
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void AddFusioncolumsXML1(List list, int topn) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("graph");
			root.setAttribute("caption", "入口流速");
			root.setAttribute("xAxisName", "");
			root.setAttribute("yAxisName", "b/s");
			root.setAttribute("decimalPrecision", "1");
			root.setAttribute("formatNumberScale", "1");
			Document doc = new Document(root);
			Hashtable colors = new Hashtable();
			colors.put(0, "FF0F00");
			colors.put(1, "FF6600");
			colors.put(2, "FF9E01");
			colors.put(3, "FCD202");
			colors.put(4, "F8FF01");
			colors.put(5, "B0DE09");
			colors.put(6, "04D215");
			colors.put(7, "0D8ECF");
			colors.put(8, "0D52D1");
			colors.put(9, "2A0CD0");
			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			if (list != null && list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					if (i >= topn)
						break;
					AllUtilHdx hdata = (AllUtilHdx) list.get(i);
					Element _newelement = new Element("set");
					_newelement.setAttribute("name", hdata.getIpaddress());
					_newelement.setAttribute("value", hdata.getThevalue());
					_newelement.setAttribute("color", (String) colors.get(i));
					elements.add(_newelement);
				}
			}
			// Format format = Format.getCompactFormat();
			// format.setEncoding("UTF-8");
			// format.setIndent(" ");
			Format format = Format.getCompactFormat();
			format.setEncoding("GBK");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);
			// XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			// System.err.println("in infopoint xml add list error "+e);
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void AddFusioncolumsXML(List list, int topn) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("graph");
			root.setAttribute("caption", "CPU利用率");
			root.setAttribute("xAxisName", "");
			root.setAttribute("yAxisName", "%");
			root.setAttribute("decimalPrecision", "0");
			root.setAttribute("formatNumberScale", "0");
			Document doc = new Document(root);
			Hashtable colors = new Hashtable();
			colors.put(0, "FF0F00");
			colors.put(1, "FF6600");
			colors.put(2, "FF9E01");
			colors.put(3, "FCD202");
			colors.put(4, "F8FF01");
			colors.put(5, "B0DE09");
			colors.put(6, "04D215");
			colors.put(7, "0D8ECF");
			colors.put(8, "0D52D1");
			colors.put(9, "2A0CD0");
			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			if (list != null && list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					if (i >= topn)
						break;
					CPUcollectdata hdata = (CPUcollectdata) list.get(i);
					Element _newelement = new Element("set");
					_newelement.setAttribute("name", hdata.getIpaddress());
					_newelement.setAttribute("value", hdata.getThevalue());
					_newelement.setAttribute("color", (String) colors.get(i));
					elements.add(_newelement);
				}
			}
			// Format format = Format.getCompactFormat();
			// format.setEncoding("UTF-8");
			// format.setIndent(" ");
			Format format = Format.getCompactFormat();
			format.setEncoding("GBK");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);
			// XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			// System.err.println("in infopoint xml add list error "+e);
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void AddFusioncolumsXML5(List list, int topn) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("graph");
			root.setAttribute("caption", "磁盘利用率");
			root.setAttribute("xAxisName", "");
			root.setAttribute("yAxisName", "%");
			root.setAttribute("decimalPrecision", "0");
			root.setAttribute("formatNumberScale", "0");
			Document doc = new Document(root);
			Hashtable colors = new Hashtable();
			colors.put(0, "FF0F00");
			colors.put(1, "FF6600");
			colors.put(2, "FF9E01");
			colors.put(3, "FCD202");
			colors.put(4, "F8FF01");
			colors.put(5, "B0DE09");
			colors.put(6, "04D215");
			colors.put(7, "0D8ECF");
			colors.put(8, "0D52D1");
			colors.put(9, "2A0CD0");
			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			if (list != null && list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					if (i >= topn)
						break;
					Diskcollectdata hdata = (Diskcollectdata) list.get(i);
					Element _newelement = new Element("set");
					_newelement.setAttribute("name", hdata.getIpaddress());
					_newelement.setAttribute("value", hdata.getThevalue());
					_newelement.setAttribute("color", (String) colors.get(i));
					elements.add(_newelement);
				}
			}
			Format format = Format.getCompactFormat();
			format.setEncoding("GBK");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void AddFusioncolumsXML4(List list, int topn) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("graph");
			root.setAttribute("caption", "内存利用率");
			root.setAttribute("xAxisName", "");
			root.setAttribute("yAxisName", "%");
			root.setAttribute("decimalPrecision", "0");
			root.setAttribute("formatNumberScale", "0");
			Document doc = new Document(root);
			Hashtable colors = new Hashtable();
			colors.put(0, "FF0F00");
			colors.put(1, "FF6600");
			colors.put(2, "FF9E01");
			colors.put(3, "FCD202");
			colors.put(4, "F8FF01");
			colors.put(5, "B0DE09");
			colors.put(6, "04D215");
			colors.put(7, "0D8ECF");
			colors.put(8, "0D52D1");
			colors.put(9, "2A0CD0");
			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			if (list != null && list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					if (i >= topn)
						break;
					Memorycollectdata hdata = (Memorycollectdata) list.get(i);
					Element _newelement = new Element("set");
					_newelement.setAttribute("name", hdata.getIpaddress());
					_newelement.setAttribute("value", hdata.getThevalue());
					_newelement.setAttribute("color", (String) colors.get(i));
					elements.add(_newelement);
				}
			}
			Format format = Format.getCompactFormat();
			format.setEncoding("GBK");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void AddAmcolumsXML1(List list) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("chart");
			Document doc = new Document(root);

			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			Element newelement = doAmcolumnPartOneTerm1(list, 10);
			elements.add(newelement);// 增加子元素

			Element graphs_element = new Element("graphs");
			List graphs_elements = graphs_element.getChildren();

			graphs_elements.add(doAmcolumnPartTwoTerm1("0", list, "", 10));

			elements.add(graphs_element);
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			// root.setAttribute("signw","0");
			outp.output(doc, fo);
		} catch (Exception e) {
			// System.err.println("in infopoint xml add list error "+e);
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void AddAmcolumsXML2(List list, int topn) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("chart");
			Document doc = new Document(root);

			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			Element newelement = doAmcolumnPartOneTerm2(list, topn);
			elements.add(newelement);// 增加子元素

			Element graphs_element = new Element("graphs");
			List graphs_elements = graphs_element.getChildren();

			graphs_elements.add(doAmcolumnPartTwoTerm2("0", list, "", topn));

			elements.add(graphs_element);
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			// root.setAttribute("signw","0");
			outp.output(doc, fo);
		} catch (Exception e) {
			// System.err.println("in infopoint xml add list error "+e);
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void AddAmcolumsXML3(List list, int topn) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("chart");
			Document doc = new Document(root);

			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			Element newelement = doAmcolumnPartOneTerm3(list, topn);
			elements.add(newelement);// 增加子元素

			Element graphs_element = new Element("graphs");
			List graphs_elements = graphs_element.getChildren();

			graphs_elements.add(doAmcolumnPartTwoTerm3("0", list, "", topn));

			elements.add(graphs_element);
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			// root.setAttribute("signw","0");
			outp.output(doc, fo);
		} catch (Exception e) {
			// System.err.println("in infopoint xml add list error "+e);
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void AddTopBarXML(Vector vector) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("chart");
			Document doc = new Document(root);

			// root.setAttribute("signw","1");
			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			Element newelement = doPartOneTerm();
			elements.add(newelement);// 增加子元素

			Element graphs_element = new Element("graphs");
			List graphs_elements = graphs_element.getChildren();

			if (vector.size() != 0) {
				for (int i = 0; i < vector.size(); i++) {
					String values = (String) vector.get(i);
					Element _newelement = doPartTwoTerm(i + "", values);
					graphs_elements.add(_newelement);// 增加子元素
				}
			}

			elements.add(graphs_element);
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			// root.setAttribute("signw","0");
			outp.output(doc, fo);
		} catch (Exception e) {
			// System.err.println("in infopoint xml add list error "+e);
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void AddLineXML(Hashtable lineHash) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("chart");
			Document doc = new Document(root);

			// root.setAttribute("signw","1");
			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			Element newelement = doLinePartOneTerm();
			elements.add(newelement);// 增加子元素

			Element graphs_element = new Element("graphs");
			List graphs_elements = graphs_element.getChildren();

			graphs_elements.add(doLinePartTwoTerm("0", lineHash, ""));

			elements.add(graphs_element);
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			// root.setAttribute("signw","0");
			outp.output(doc, fo);
		} catch (Exception e) {
			// System.err.println("in infopoint xml add list error "+e);
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 设备列表xml yangjun add
	public void addequipXML(List list) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("catalog");
			Document doc = new Document(root);

			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			for (int i = 0; i < list.size(); i++) {
				HostNode node = (HostNode) list.get(i);
				Element newelement = new Element("product");
				newelement.setAttribute("productId", node.getId() + "");
				Element name = new Element("name");
				Element description = new Element("description");
				Element price = new Element("price");
				Element image = new Element("image");
				Element isManage = new Element("isManage");
				Element series = new Element("series");
				Element host = new Element("host");
				Element switchs = new Element("switchs");
				Element router = new Element("router");
				Element printer = new Element("printer");
				Element highlight1 = new Element("highlight1");
				Element highlight2 = new Element("highlight2");
				Element qty = new Element("qty");
				Element ip = new Element("ip");
				name.setText(node.getAlias());
				description.setText("设备名称:" + node.getSysName() + "<br/>" + "设备类型:" + node.getType() + "<br/>" + "设备描述:"
						+ node.getSysDescr() + "<br/>" + "子网掩码:" + node.getNetMask() + "<br/>" + "系统OID:" + node.getSysOid());
				price.setText("" + node.getId());
				if (node.getCategory() == 4) {
					host.setText(true + "");
					image.setText("../resource/equip/" + NodeHelper.getServerTopoImage(node.getSysOid()));
				} else if (node.getCategory() == 3) {
					switchs.setText(true + "");
					image.setText("../resource/equip/" + NodeHelper.getTopoImage(node.getCategory()));
				} else if (node.getCategory() == 5) {
					printer.setText(true + "");
					image.setText("../resource/equip/" + NodeHelper.getTopoImage(node.getCategory()));
				} else {
					router.setText(true + "");
					image.setText("../resource/equip/" + NodeHelper.getTopoImage(node.getCategory()));
				}
				if (node.isManaged()) {
					isManage.setText("已监视");
				} else {
					isManage.setText("未监视");
				}
				series.setText(node.getType());
				highlight1.setText("IP   " + node.getIpAddress());
				highlight2.setText("MAC  " + node.getBridgeAddress());
				ip.setText(node.getIpAddress());
				qty.setText("");
				newelement.addContent(name);
				newelement.addContent(description);
				newelement.addContent(price);
				newelement.addContent(image);
				newelement.addContent(isManage);
				newelement.addContent(series);
				newelement.addContent(host);
				newelement.addContent(switchs);
				newelement.addContent(router);
				newelement.addContent(printer);
				newelement.addContent(highlight1);
				newelement.addContent(highlight2);
				newelement.addContent(qty);
				newelement.addContent(ip);
				elements.add(newelement);
			}

			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 被监视对象列表xml yangjun add
	public void addEquipXML(List list) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("catalog");
			Document doc = new Document(root);

			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			for (int i = 0; i < list.size(); i++) {
				Host node = (Host) list.get(i);
				Element newelement = new Element("product");
				newelement.setAttribute("productId", node.getId() + "");
				Element name = new Element("name");
				Element description = new Element("description");
				Element price = new Element("price");
				Element image = new Element("image");
				Element statue = new Element("statue");
				Element isManage = new Element("isManage");
				Element series = new Element("series");
				Element host = new Element("host");
				Element switchs = new Element("switchs");
				Element router = new Element("router");
				Element printer = new Element("printer");
				Element highlight1 = new Element("highlight1");
				Element highlight2 = new Element("highlight2");
				Element qty = new Element("qty");
				Element ip = new Element("ip");
				Element showmessage = new Element("showmessage");
				name.setText(node.getAlias());
				description.setText("设备名称:" + node.getSysName() + "<br/>" + "设备类型:" + node.getType() + "<br/>" + "设备描述:"
						+ node.getSysDescr() + "<br/>" + "子网掩码:" + node.getNetMask() + "<br/>" + "系统OID:" + node.getSysOid());
				price.setText("" + node.getId());
				if (node.getCategory() == 4) {
					host.setText(true + "");
					image.setText("../resource/equip/" + NodeHelper.getServerTopoImage(node.getSysOid()));
				} else if (node.getCategory() == 3) {
					switchs.setText(true + "");
					image.setText("../resource/equip/" + NodeHelper.getTopoImage(node.getCategory()));
				} else if (node.getCategory() == 5) {
					printer.setText(true + "");
					image.setText("../resource/equip/" + NodeHelper.getTopoImage(node.getCategory()));
				} else {
					router.setText(true + "");
					image.setText("../resource/equip/" + NodeHelper.getTopoImage(node.getCategory()));
				}
				if (node.isAlarm()) {
					statue.setText("../resource/image/topo/alert.gif");
				} else {
					statue.setText("../resource/image/topo/status_ok.gif");
				}
				if (node.isManaged()) {
					isManage.setText("已监视");
				} else {
					isManage.setText("未监视");
				}
				series.setText(node.getType());
				highlight1.setText("IP   " + node.getIpAddress());
				highlight2.setText("MAC  " + node.getMac());
				ip.setText(node.getIpAddress());
				showmessage.setText(node.getShowMessage());
				qty.setText("");
				newelement.addContent(name);
				newelement.addContent(description);
				newelement.addContent(price);
				newelement.addContent(image);
				newelement.addContent(statue);
				newelement.addContent(isManage);
				newelement.addContent(series);
				newelement.addContent(host);
				newelement.addContent(switchs);
				newelement.addContent(router);
				newelement.addContent(printer);
				newelement.addContent(highlight1);
				newelement.addContent(highlight2);
				newelement.addContent(qty);
				newelement.addContent(ip);
				newelement.addContent(showmessage);
				elements.add(newelement);
			}

			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void AddXML() {// 添加一个信息点

		FileInputStream fi = null;
		FileOutputStream fo = null;

		try {
			createDir();

			File file = new File(path);

			Document doc;

			if (!file.exists()) {
				file.createNewFile();
				Element root = new Element("pie");
				doc = new Document(root);
			} else {

				fi = new FileInputStream(file);
				SAXBuilder sb = new SAXBuilder();
				doc = sb.build(fi);
				fi.close();
			}

			Element root = doc.getRootElement(); // 得到根元素
			// root.setAttribute("signw","1");
			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			Element newelement = doOneTerm("", "", 0);

			elements.add(newelement);// 增加子元素

			Format format = Format.getCompactFormat();
			format.setEncoding("GBK");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			root.setAttribute("signw", "0");
			outp.output(doc, fo);
			fo.close();
		} catch (Exception e) {
			System.err.println("in infopoint xml add one record error " + e);
		}finally{
			if(fi != null){
				try {
					fi.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void createDir() {
		File dir = new File(commonPath);
		if (!dir.exists()) {// 检查Sub目录是否存在
			dir.mkdir();
		}
	}

	private String emitStr(String num) {
		if (num != null) {
			if (num.indexOf(".") >= 0) {
				if (num.substring(num.indexOf(".") + 1).length() > 7) {
					String tempStr = num.substring(num.indexOf(".") + 1);
					num = num.substring(0, num.indexOf(".") + 1) + tempStr.substring(0, 7);
				}
			}
		}
		return num;
	}

	/**
	 * 数据库图形列表 更新xml操作
	 */
	public void addDbXML(List list) {
		FileOutputStream fo = null;
		try {
			createDir();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = new Element("catalog");
			Document doc = new Document(root);
			DBTypeDao typedao = new DBTypeDao();
			List typelist = new ArrayList();
			try {
				typelist = typedao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			List elements = root.getChildren(); // 得到根元素所有子元素的集合
			for (int i = 0; i < list.size(); i++) {
				DBVo db = (DBVo) list.get(i);
				Element newelement = new Element("product");
				newelement.setAttribute("productId", db.getId() + "");
				Element name = new Element("name");
				Element description = new Element("description");
				Element price = new Element("price");
				Element image = new Element("image");
				Element isManage = new Element("isManage");
				Element series = new Element("series");
				Element mysql = new Element("mysql");
				Element oracle = new Element("oracle");
				Element sqlserver = new Element("sqlserver");
				Element sybase = new Element("sybase");
				Element db2 = new Element("db2");
				Element informix = new Element("informix");
				Element highlight1 = new Element("highlight1");
				Element highlight2 = new Element("highlight2");
				Element qty = new Element("qty");
				Element ip = new Element("ip");
				Element showmessage = new Element("showmessage");
				name.setText(db.getAlias());
				int itype = db.getDbtype();
				String dbType = "";
				for (int j = 0; j < typelist.size(); j++) {
					DBTypeVo typevo = (DBTypeVo) typelist.get(j);
					if (itype == typevo.getId()) {
						dbType = typevo.getDbtype();
					}
				}
				price.setText("" + db.getId());
				if (db.getCategory() == 52) {// mysql
					String runstr = ""; // 运行状态
					String basePath = ""; // 基本路径
					String dataPath = ""; // 数据路径
					String logerrorPath = ""; // 错误日志路径
					String version = ""; // 数据库版本
					String hostOS = ""; // 服务器操作系统
//					Hashtable allData = ShareData.getMySqlmonitordata();
//					Hashtable ipData = null;
//					if (allData != null && allData.size() > 0) {
//						ipData = (Hashtable) allData.get(db.getIpAddress());
					DBDao dao = new DBDao();
					IpTranslation tranfer = new IpTranslation();
					String hex = tranfer.formIpToHex(db.getIpAddress());
					String serverip = hex+":"+db.getId();
					Hashtable ipData = null;;
					try {
						ipData = dao.getMysqlDataByServerip(serverip);
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(dao != null){
							dao.close();
						}
					}
					if (ipData != null && ipData.size() > 0) {
						runstr = (String) ipData.get("runningflag");
						String dbnames = db.getDbName();
						String[] dbs = dbnames.split(",");
						for (int k = 0; k < dbs.length; k++) {
							// 判断是否已经获取了当前的配置信息
							String dbStr = dbs[k];
							if (ipData.containsKey(dbStr)) {
								Hashtable returnValue = new Hashtable();
								returnValue = (Hashtable) ipData.get(dbStr);
								if (returnValue != null && returnValue.size() > 0) {
									// 判断是否已经获取了当前的配置信息
									if (returnValue.containsKey("configVal")) {
										Hashtable configVal = (Hashtable) returnValue.get("configVal");
										if (configVal.containsKey("basePath"))
											basePath = (String) configVal.get("basePath");
										if (configVal.containsKey("dataPath"))
											dataPath = (String) configVal.get("dataPath");
										if (configVal.containsKey("logerrorPath"))
											logerrorPath = (String) configVal.get("logerrorPath");
										if (configVal.containsKey("version"))
											version = (String) configVal.get("version");
										if (configVal.containsKey("hostOS"))
											hostOS = (String) configVal.get("hostOS");
									}
								}
							}
						}
					} else {
						runstr = "停止运行";
					}
					description.setText("数据库类型:" + dbType + "<br/>" + "数据库版本号:" + version + "<br/>" + "服务器操作系统:" + hostOS
							+ "<br/>" + "数据库名称:" + db.getDbName() + "<br/>" + "基本路径:" + basePath + "<br/>" + "数据路径:" + dataPath
							+ "<br/>" + "错误日志路径:" + logerrorPath);
					highlight2.setText("PORT   " + db.getPort() + "<br/><br/>" + "当前状态   " + runstr);

					mysql.setText(true + "");
					image.setText("../resource/databaseflex/" + NodeHelper.getTopoImage(db.getCategory()));
				} else if (db.getCategory() == 53) {// oracle
					String runstr = "未监视";// 运行状态
					DBDao dao = new DBDao();
					// Hashtable allValue = new Hashtable();
					Hashtable sysValue = new Hashtable();
					StringBuffer showspacemem = new StringBuffer();
					try {

						if (db.getManaged() == 1) {
							// 若被监视
							// ********//
							runstr="停止运行";
//							Hashtable alloracledata = ShareData.getAlloracledata();
//							Hashtable iporacledata = new Hashtable();
//							if (alloracledata != null && alloracledata.size() > 0) {
//								if (alloracledata.containsKey(db.getIpAddress().split(":")[0] + ":"
//										+ db.getIpAddress().split(":")[1])) {
//									iporacledata = (Hashtable) alloracledata.get(db.getIpAddress().split(":")[0] + ":"
//											+ db.getIpAddress().split(":")[1]);
//									if (iporacledata.containsKey("status")) {
//										String sta = (String) iporacledata.get("status");
//										if ("1".equals(sta)) {
//											runstr = "正在运行";
//										}else{
//											runstr="停止运行";
//										}
//									}
//
//								}
//								if (iporacledata.containsKey("sysValue")) {
//									sysValue = (Hashtable) iporacledata.get("sysValue");
//								}
//							}
							IpTranslation tranfer = new IpTranslation();
							String hex = tranfer.formIpToHex(db.getIpAddress().split(":")[0]);
							String serverip = hex+":"+db.getIpAddress().split(":")[1];
							Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);//取状态信息
							sysValue = dao.getOracle_nmsorasys(serverip);
							String statusStr = String.valueOf(statusHashtable.get("status"));
							if("1".equals(statusStr)){
								runstr = "正在运行";
							}else{
								runstr="停止运行";
							}
							sysValue = dao.getOracle_nmsorasys(serverip);
							// *******//
							// if(dao.getOracleIsOK(db.getIpAddress().split(":")[0],
							// Integer.parseInt(db.getPort()), db.getDbName(),
							// db.getUser(), db.getPassword())){
							// runstr = "正在运行";
							// }else{
							// runstr = "停止运行";
							// }
							// try{
							// if("正在运行".equals(runstr))
							// sysValue =
							// dao.getOracleSys(db.getIpAddress().split(":")[0],Integer.parseInt(db.getPort()),db.getDbName(),db.getUser(),db.getPassword());
							//						
							// }catch(Exception e){
							// e.printStackTrace();
							// }
							// allValue = ShareData.getAlloracledata();
							// if(allValue != null){
							if (sysValue != null && sysValue.size() > 0) {
								// sysValue = allValue.get(db.getIpAddress());
								description.setText("主机名称:" + sysValue.get("HOST_NAME") + "<br/>" + "DB 名称:"
										+ sysValue.get("DBNAME") + "<br/>" + "DB 版本:" + sysValue.get("VERSION") + "<br/>"
										+ "例程名:" + sysValue.get("INSTANCE_NAME") + "<br/>" + "例程状态:" + sysValue.get("STATUS")
										+ "<br/>" + "例程开始时间:" + sysValue.get("STARTUP_TIME") + "<br/>" + "归档日志模式:"
										+ sysValue.get("ARCHIVER"));
							} else {
								description.setText("");
							}
							highlight2.setText("PORT   " + db.getPort() + "<br/><br/>" + "数据库类型   " + dbType + "<br/><br/>"
									+ "当前状态   " + runstr);
							Vector oraspace = new Vector();
							Double allspace = 100.00;

							try {
								if ("正在运行".equals(runstr))
//									oraspace = (Vector) iporacledata.get("tableinfo_v");
									oraspace = dao.getOracle_nmsoraspaces(serverip);
								;// oraspacedata
							} catch (Exception e) {
								e.printStackTrace();
							}
							for (int k = 0; k < oraspace.size(); k++) {
								Hashtable ht = (Hashtable) oraspace.get(k);
								String tablespace = ht.get("tablespace").toString();
								String percent = ht.get("percent_free").toString();
								Double usedspace = allspace - Double.parseDouble(percent);
								showspacemem.append("表空间:" + tablespace + "    已占用:" + usedspace + "<br/>");
							}
							Hashtable memValue = new Hashtable();
							try {
								if ("正在运行".equals(runstr))
//									memValue = (Hashtable) iporacledata.get("memValue");
									memValue = dao.getOracle_nmsoramemvalue(serverip);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (memValue != null && memValue.size() > 0) {
								showspacemem.append("共享池:" + memValue.get("shared pool") + "MB<br/>");// shared
																										// pool
								showspacemem.append("总计PGA目标:" + memValue.get("aggregate PGA target parameter") + "<br/>");// shared
																															// pool
								showspacemem.append("分配的当前PGA:" + memValue.get("total PGA allocated") + "<br/>");// shared
																													// pool
								showspacemem.append("高速缓存命中百分比:" + memValue.get("cache hit percentage") + "<br/>");// shared
																													// pool
							}

						}

						showmessage.setText(showspacemem.toString());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}
					oracle.setText(true + "");
					image.setText("../resource/databaseflex/" + NodeHelper.getTopoImage(db.getCategory()));
				} else if (db.getCategory() == 54) {// sqlserver
					DBDao dao = new DBDao();
					Hashtable sysValue = new Hashtable();
					Hashtable dbValue = new Hashtable();
					Hashtable alldatabase = new Hashtable();
					StringBuffer showdb = new StringBuffer();
					String runstr = "未监视";

					try {
						if (db.getManaged() == 1) {
							// 若被监视
							// ************************//
							runstr="停止运行";
//							Hashtable allsqlserverdata = ShareData.getSqlserverdata();
//							Hashtable ipsqlserverdata = new Hashtable();
//							if (allsqlserverdata != null && allsqlserverdata.size() > 0) {
//								if (allsqlserverdata.containsKey(db.getIpAddress())) {
//									ipsqlserverdata = (Hashtable) allsqlserverdata.get(db.getIpAddress());
//									if (ipsqlserverdata.containsKey("status")) {
//										String p_status = (String) ipsqlserverdata.get("status");
//										if (p_status != null && p_status.length() > 0) {
//											if ("1".equalsIgnoreCase(p_status)) {
//												runstr = "正在运行";
//											}else{
//												runstr="停止运行";
//											}
//										}
//									}
//
//								}
//
//							}
							DBDao dbDao = new DBDao();
							IpTranslation tranfer = new IpTranslation();
							String hex = tranfer.formIpToHex(db.getIpAddress());
							String serverip = hex+":"+db.getAlias();
							Hashtable statusHash = dbDao.getSqlserver_nmsstatus(serverip);
							Hashtable statisticsHash = dbDao.getSqlserver_nmsstatisticsHash(serverip);
							String p_status = (String)statusHash.get("status");
							if(p_status != null && p_status.length()>0){
								if("1".equalsIgnoreCase(p_status)){
									runstr = "正在运行";
								}
							}
							// **************************//
							// if(dao.getSqlserverIsOk(db.getIpAddress(),
							// db.getUser(), db.getPassword())){
							// runstr = "正在运行";
							// }else{
							// runstr = "停止运行";
							// }
							highlight2.setText("PORT   " + db.getPort() + "<br/><br/>" + "数据库类型   " + dbType + "<br/><br/>"
									+ "当前状态   " + runstr);
							try {
								if ("正在运行".equals(runstr))
//									sysValue = (Hashtable) ipsqlserverdata.get("sysValue");
									sysValue = dbDao.getSqlserver_nmssysvalue(serverip);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (sysValue != null && sysValue.size() > 0) {
								description.setText("主机名称:"
										+ sysValue.get("MACHINENAME")
										+ "<br/>"
										+
										// "版本:"+sysValue.get("VERSION")+"<br/>"+
										"补丁包:" + sysValue.get("productlevel") + "<br/>" + "进程ID:" + sysValue.get("ProcessID")
										+ "<br/>" + "单用户模式下:" + sysValue.get("IsSingleUser") + "<br/>" + "集成安全性模式下:"
										+ sysValue.get("IsIntegratedSecurityOnly") + "<br/>" + "在故障转移群集中配置服务器实例:"
										+ sysValue.get("IsClustered"));
							} else {
								description.setText("");
							}
							try {
								if ("正在运行".equals(runstr))
//									dbValue = (Hashtable)ipsqlserverdata.get("dbValue");
									dbValue = dbDao.getSqlserver_nmsdbvalue(serverip);
							} catch (Exception e) {
								e.printStackTrace();
							}finally{
								dbDao.close();
							}
							alldatabase = (Hashtable) dbValue.get("database");
							Vector names = (Vector) dbValue.get("names");

							if (names != null && names.size() > 0) {
								for (int p = 0; p < names.size(); p++) {
									String key = (String) names.get(p);
									Hashtable data = (Hashtable) alldatabase.get(key);
									if (data != null) {
										showdb.append("数据库:" + data.get("dbname").toString() + "  利用率:"
												+ data.get("usedperc").toString() + "%<br/>");
									}
								}
							}
						}
						showmessage.setText(showdb.toString());

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}
					sqlserver.setText(true + "");
					image.setText("../resource/databaseflex/" + NodeHelper.getTopoImage(db.getCategory()));
				} else if (db.getCategory() == 55) {// sybase
					DBDao dao = new DBDao();
					Hashtable sysValue = new Hashtable();
					Hashtable sValue = new Hashtable();
					String runstr = "未监视";
					StringBuffer showdb = new StringBuffer();
					//*******************************//
					
					//********************************//
					try {
						if (db.getManaged() == 1) {
							// 若被监视
//							if (dao.getSysbaseIsOk(db.getIpAddress(), db.getUser(), db.getPassword(), Integer.parseInt(db
//									.getPort()))) {
//								runstr = "正在运行";
//							} else {
//								runstr = "停止运行";
//							}
							runstr="停止运行";
//							sysValue = ShareData.getSysbasedata();
//							if(sysValue!=null&&sysValue.size()>0){
//								if(sysValue.containsKey(db.getIpAddress())){
//									sValue = (Hashtable)sysValue.get(db.getIpAddress());
//									if(sValue.containsKey("status")){
//										String p_status = (String)sValue.get("status");
//										if(p_status != null && p_status.length()>0){
//											if("1".equalsIgnoreCase(p_status)){
//												runstr = "正在运行";
//											}else{
//												runstr="停止运行";
//											}
//										}
//									}
//								}
//							}
							//获取sybase信息
							SybaseVO sysbaseVO = new SybaseVO();
							IpTranslation tranfer = new IpTranslation();
							String hex = tranfer.formIpToHex(db.getIpAddress());
							dao = new DBDao();
							String serverip = hex+":"+db.getId();
							sysbaseVO = dao.getSybaseDataByServerip(serverip); 
							String status = "0";
							Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
							if(tempStatusHashtable != null && tempStatusHashtable.containsKey("status")){
								status = (String)tempStatusHashtable.get("status");
							}
							if(status.equals("1")){
								runstr = "正在运行";
							}
							if(dao != null){
								dao.close();
							}
							String sysName = "";
//							sysValue = ShareData.getSysbasedata();
//
//							if (sysValue != null && sysValue.size() > 0) {
//								sValue = (Hashtable) sysValue.get(db.getIpAddress());
//								if (sValue != null && sValue.size() > 0) {
//									SybaseVO sysbaseVO = (SybaseVO) sValue.get("sysbaseVO");
//									sysName = sysbaseVO.getServerName();
//									description.setText("CPU率用率:" + sysbaseVO.getCpu_busy_rate() + "%<br/>" + "IO利用率:"
//											+ sysbaseVO.getIo_busy_rate() + "%<br/>" + "输出数据速率(条/秒):" + sysbaseVO.getSent_rate()
//											+ "<br/>" + "输入数据速率(条/秒):" + sysbaseVO.getReceived_rate() + "<br/>" + "写入磁盘速率(条/秒):"
//											+ sysbaseVO.getWrite_rate() + "<br/>" + "读取磁盘速率(条/秒):" + sysbaseVO.getRead_rate()
//											+ "<br/>" + "版本:" + sysbaseVO.getVersion());
//
//									List dbsizelist = (List) sysbaseVO.getDbInfo();
//									if (dbsizelist != null && dbsizelist.size() > 0) {
//										for (int w = 0; w < dbsizelist.size(); w++) {
//											TablesVO tablesVO = (TablesVO) dbsizelist.get(w);
//											showdb.append("数据库:" + tablesVO.getDb_name() + "  利用率:" + tablesVO.getDb_usedperc()
//													+ "%<br/>");
//										}
//									}
//								} else {
//									description.setText("");
//								}
//							}
//							sysValue = ShareData.getSysbasedata();
							if (sysbaseVO != null) {
								sysName = sysbaseVO.getServerName();
								description.setText("CPU率用率:" + sysbaseVO.getCpu_busy_rate() + "%<br/>" + "IO利用率:"
										+ sysbaseVO.getIo_busy_rate() + "%<br/>" + "输出数据速率(条/秒):" + sysbaseVO.getSent_rate()
										+ "<br/>" + "输入数据速率(条/秒):" + sysbaseVO.getReceived_rate() + "<br/>" + "写入磁盘速率(条/秒):"
										+ sysbaseVO.getWrite_rate() + "<br/>" + "读取磁盘速率(条/秒):" + sysbaseVO.getRead_rate()
										+ "<br/>" + "版本:" + sysbaseVO.getVersion());

								List dbsizelist = (List) sysbaseVO.getDbInfo();
								if (dbsizelist != null && dbsizelist.size() > 0) {
									for (int w = 0; w < dbsizelist.size(); w++) {
										TablesVO tablesVO = (TablesVO) dbsizelist.get(w);
										showdb.append("数据库:" + tablesVO.getDb_name() + "  利用率:" + tablesVO.getDb_usedperc()
												+ "%<br/>");
									}
								}
							} else {
								description.setText("");
							}

							highlight2.setText("PORT   " + db.getPort() + "<br/>" + "数据库类型   " + dbType + "<br/>" + "服务器名称   "
									+ sysName + "<br/>" + "当前状态   " + runstr);
						}
						showmessage.setText(showdb.toString());

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}

					sybase.setText(true + "");
					image.setText("../resource/databaseflex/" + NodeHelper.getTopoImage(db.getCategory()));
				} else if (db.getCategory() == 59) {// db2
					Hashtable sysValue = new Hashtable();
					StringBuffer showdb = new StringBuffer();
					boolean db2IsOK = false;
					String runstr = "";
					if (db.getManaged() == 1) {
						// 若被监视
						/*try {
							db2IsOK = dao.getDB2IsOK(db.getIpAddress(), Integer.parseInt(db.getPort()), db.getDbName(), db
									.getUser(), db.getPassword());
						} catch (Exception e) {
							e.printStackTrace();
							db2IsOK = false;
						}
						if (db2IsOK) {
							runstr = "正在运行";
						} else {
							runstr = "停止运行";
						}*/
						//**************//
						runstr="停止运行";
//						Hashtable allDb2data = ShareData.getAlldb2data();
						IpTranslation tranfer = new IpTranslation();
						String hex = tranfer.formIpToHex(db.getIpAddress());
						String sip = hex+":"+db.getId();
						Hashtable monitorValue = null;
						DBDao dao = null;
						try {
							dao = new DBDao();
							monitorValue = dao.getDB2DataByServerip(sip);
						} catch (RuntimeException e1) {
							e1.printStackTrace();
						} finally{
							if(dao != null){
								dao.close();
							}
						}
						Hashtable allDb2data = null;
						if(monitorValue != null && monitorValue.containsKey("allDb2Data")){
							allDb2data = (Hashtable)monitorValue.get("allDb2Data");
						}
						Hashtable ipDb2data = new Hashtable();
						if(allDb2data != null && allDb2data.size()>0){
							if(allDb2data.containsKey(db.getIpAddress())){
								ipDb2data = (Hashtable)allDb2data.get(db.getIpAddress());
								if(ipDb2data.containsKey("status")){
									String p_status = (String)ipDb2data.get("status");
									if(p_status != null && p_status.length()>0){
										if("1".equalsIgnoreCase(p_status)){
											runstr = "正在运行";
										}else{
											runstr="停止运行";
										}
									}
								}
							}
						}
						//**************//
						highlight2.setText("PORT   " + db.getPort() + "<br/>" + "数据库类型   " + dbType + "<br/>" + "当前状态   "
								+ runstr);
						try {
//							sysValue = dao.getDB2Sys(db.getIpAddress(), Integer.parseInt(db.getPort()), db.getDbName(), db
//									.getUser(), db.getPassword());
							if(ipDb2data.containsKey("sysInfo"))
								sysValue=(Hashtable)ipDb2data.get("sysInfo");
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (sysValue != null && sysValue.size() > 0) {
							Enumeration dbsys = sysValue.keys();
							Hashtable<String, Object> returnhash = new Hashtable();
							while (dbsys.hasMoreElements()) {
								String obj = (String) dbsys.nextElement();
								returnhash = (Hashtable) sysValue.get(obj);
								if (returnhash != null) {
									description.setText("系统名称:" + returnhash.get("host_name") + "<br/>" + "数据库名称:"
											+ returnhash.get("db_name") + "<br/>" + "数据库产品:" + returnhash.get("installed_prod")
											+ "<br/>" + "DB2版本:" + returnhash.get("prod_release") + "<br/>" + "路径:"
											+ returnhash.get("db_path") + "<br/>" + "连接时间:" + returnhash.get("db_conn_time"));
								} else {
									description.setText("");
								}
							}

							try {
//								returnhash = dao.getDB2Space(db.getIpAddress(), Integer.parseInt(db.getPort()), db.getDbName(),
//										db.getUser(), db.getPassword());
								if(ipDb2data.containsKey("spaceInfo"))
									returnhash=(Hashtable)ipDb2data.get("spaceInfo");
							} catch (Exception e) {
								e.printStackTrace();
							}

							Enumeration dbs = returnhash.keys();
							List retList = new ArrayList();
							while (dbs.hasMoreElements()) {
								String obj = (String) dbs.nextElement();
								retList = (List) returnhash.get(obj);
								for (int m = 0; m < retList.size(); m++) {
									Hashtable ht = (Hashtable) retList.get(m);
									if (ht == null)
										continue;
									String spacename = "";
									if (ht.get("tablespace_name") != null)
										spacename = ht.get("tablespace_name").toString();
									double percent = 0.0;
									if (ht.get("usableper") != null && ht.get("usableper").toString() != null
											&& !("").equals(ht.get("usableper").toString())
											&& !("null").equals(ht.get("usableper").toString()))
										percent = (100.0 - Double.parseDouble(ht.get("usableper").toString()));
									showdb.append("表空间:" + spacename + "  使用率:" + percent + "%<br/>");
								}
							}
						}
					}
					showmessage.setText(showdb.toString());

					db2.setText(true + "");
					image.setText("../resource/databaseflex/" + NodeHelper.getTopoImage(db.getCategory()));
				} else if (db.getCategory() == 60) {// informix
					Hashtable sysValue = new Hashtable();
					Hashtable sValue = new Hashtable();
					Hashtable dbValue = new Hashtable();
					boolean informixIsOK = false;
					String runstr = "";
					StringBuffer showdb = new StringBuffer();
					if (db.getManaged() == 1) {
						// 若被监视
//						try {
//							informixIsOK = dao.getInformixIsOk(db.getIpAddress(), db.getPort(), db.getUser(), db.getPassword(),
//									db.getDbName(), db.getAlias());
//						} catch (Exception e) {
//							e.printStackTrace();
//							informixIsOK = false;
//						}
						//********************************************//
						runstr="停止运行";
//						Hashtable informixData=new Hashtable();
//						Hashtable mino=new Hashtable();
//						Hashtable sysValue1 = ShareData.getInformixmonitordata();
//						if(sysValue1!=null&&sysValue1.size()>0){
//							if(sysValue1.containsKey(db.getIpAddress())){
//								mino=(Hashtable)sysValue1.get(db.getIpAddress());
//								informixData=(Hashtable)mino.get(db.getDbName());
//								if(informixData.containsKey("status")){
//									String p_status = (String)informixData.get("status");
//									if(p_status != null && p_status.length()>0){
//										if("1".equalsIgnoreCase(p_status)){
//											runstr = "正在运行";
//										}else {
//											runstr = "停止运行";
//										}
//									}
//								}
//							}
//						}
						IpTranslation tranfer = new IpTranslation();
						String hex = tranfer.formIpToHex(db.getIpAddress());
						String serverip = hex+":"+db.getDbName();
						DBDao dao = new DBDao();
						String status = "0";
						try {
							status = String.valueOf(((Hashtable)dao.getInformix_nmsstatus(serverip)).get("status"));
//							List databaseList = dao.getInformix_nmsdatabase(serverip);
//							List sessionList = dao.getInformix_nmssession(serverip);
//							List lockList = dao.getInformix_nmslock(serverip);
//							List logList = dao.getInformix_nmslog(serverip);
//							List aboutList = dao.getInformix_nmsabout(serverip);
//							List spaceList = dao.getInformix_nmsspace(serverip);
//							List configList = dao.getInformix_nmsconfig(serverip);
						} catch (RuntimeException e) {
							e.printStackTrace();
						} finally{
							dao.close();
						}
						if("1".equalsIgnoreCase(status)){
							runstr = "正在运行";
						}else {
							runstr = "停止运行";
						}
						
						//*********************************************//
//						if (informixIsOK) {
//							runstr = "正在运行";
//						} else {
//							runstr = "停止运行";
//						}

						/** ************************************************************************* */
//						try {
//							sysValue = ShareData.getInformixmonitordata();
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						if (sysValue != null && sysValue.size() > 0) {
//							sValue = (Hashtable) sysValue.get(db.getIpAddress());
//							dbValue = (Hashtable) sValue.get(db.getDbName());
//							ArrayList dbdatabase = new ArrayList();
//							ArrayList dbspaces = new ArrayList();
//							dbdatabase = (ArrayList) dbValue.get("databaselist");// 数据库基本信息
//							dbspaces = (ArrayList) dbValue.get("informixspaces");// 数据库空间信息
						if("1".equalsIgnoreCase(status)){
							DBDao dbDao = new DBDao();
							List dbdatabase = null;
							List dbspaces = null;
							try {
								dbdatabase = dbDao.getInformix_nmsdatabase(serverip);
								dbspaces = dbDao.getInformix_nmsspace(serverip);
							} catch (RuntimeException e) {
								e.printStackTrace();
							} finally{
								dbDao.close();
							}
							Hashtable databaseinfo = new Hashtable();
							if(dbdatabase != null && dbdatabase.size() > 0){
								databaseinfo = (Hashtable) dbdatabase.get(0);
							}

							if (databaseinfo != null && !databaseinfo.isEmpty()) {
								description.setText("数据库名称:" + databaseinfo.get("dbname") + "<br/>" + "服务名称:"
										+ databaseinfo.get("dbserver") + "<br/>" + "创建者:" + databaseinfo.get("createuser")
										+ "<br/>" + "创建时间:" + databaseinfo.get("createtime"));
							} else {
								description.setText("");
							}
							if(dbspaces == null) {
								dbspaces = new ArrayList();
							}
							for (int t = 0; t < dbspaces.size(); t++) {
								Hashtable ht = (Hashtable) dbspaces.get(t);
								if (ht == null)
									continue;
								String spacename = "";
								if (ht.get("dbspace") != null)
									spacename = ht.get("dbspace").toString();
								double percent = 0.0;
								if (ht.get("percent_free") != null && ht.get("percent_free").toString() != null
										&& !("").equals(ht.get("percent_free").toString())
										&& !("null").equals(ht.get("percent_free").toString()))
									percent = (100.0 - Double.parseDouble(ht.get("percent_free").toString()));
								String strPercent = percent + "";
								if (strPercent.length() > 5) {
									strPercent = strPercent.substring(0, 4);
								}
								showdb.append("表空间:" + spacename + "  使用率:" + strPercent + "%<br/>");
							}
						}
						showmessage.setText(showdb.toString());
					}
					image.setText("../resource/databaseflex/" + NodeHelper.getTopoImage(db.getCategory()));
					highlight2.setText("PORT   " + db.getPort() + "<br/>" + "数据库类型   " + dbType + "<br/>" + "当前状态   " + runstr);
					informix.setText(true + "");
				}

				if (db.getManaged() == 1) {
					isManage.setText("已监视");
				} else {
					isManage.setText("未监视");
				}
				series.setText(db.getDbName() + "数据库");
				highlight1.setText("IP   " + db.getIpAddress());
				ip.setText(db.getIpAddress());
				qty.setText("");
				newelement.addContent(name);
				newelement.addContent(description);
				newelement.addContent(price);
				newelement.addContent(image);
				newelement.addContent(isManage);
				newelement.addContent(series);
				newelement.addContent(mysql);
				newelement.addContent(oracle);
				newelement.addContent(sqlserver);
				newelement.addContent(sybase);
				newelement.addContent(db2);
				newelement.addContent(informix);
				newelement.addContent(highlight1);
				newelement.addContent(highlight2);
				newelement.addContent(qty);
				newelement.addContent(ip);
				newelement.addContent(showmessage);
				elements.add(newelement);
			}

			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outp = new XMLOutputter(format);

			fo = new FileOutputStream(file);
			outp.output(doc, fo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
