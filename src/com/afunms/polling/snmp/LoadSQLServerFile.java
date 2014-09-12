package com.afunms.polling.snmp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.afunms.common.util.SysLogger;
import java.util.*;
public class LoadSQLServerFile {

	private Element root;

	public LoadSQLServerFile(String path) {
		root = getRoot(path);
	}

	/**
	 * 
	 * @param path
	 *            要解析的sqlServer配置文件的路径
	 * @return xml文件的根元素
	 */
	private Element getRoot(String path) {
		Element root = null;
		SAXBuilder sb = new SAXBuilder();
		try {
			Document dc = sb.build(new FileInputStream(path));
			root = dc.getRootElement();
		} catch (JDOMException e) {
			e.printStackTrace();
			System.out.println("初始化sqlServer文件出错");
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("初始化sqlServer文件出错");
			throw new RuntimeException(e);
		}
		return root;
	}

	/**
	 * 
	 * @return 返回要解析的sql的配置文件的sqlserverProcessesTabInfo信息
	 */
	public Vector<Hashtable<String, String>> getSQLProcessesTabInfo() {
		Vector<Hashtable<String, String>> vector = new Vector<Hashtable<String, String>>();
		try {
			List list = XPath.selectNodes(root,
					"//content/sqlserverProcessesTabInfo/row");
			for (Iterator itr = list.iterator(); itr.hasNext();) {
				Element ele = (Element) itr.next();// 得到所有的子元素
				List<Element> children = ele.getChildren();
				Hashtable<String, String> tab = new Hashtable<String, String>();
				for (Element child : children) {
					tab.put(child.getName(), child.getText());
				}
				vector.add(tab);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return vector;
	}

	/**
	 * 
	 * @return 返回要解析的sql配置文件的
	 */
	public Hashtable<String, Object> getSQLServerDB() {
		Hashtable<String, Object> tables = new Hashtable<String, Object>();
		Vector<String> names = new Vector<String>();
		Hashtable<String, Hashtable<String, String>> datas = new Hashtable<String, Hashtable<String, String>>();
		Hashtable<String, Hashtable<String, String>> logs = new Hashtable<String, Hashtable<String, String>>();
		try {
			List list = XPath.selectNodes(root, "//content/sqlserverDB/row");
			Map<String,Integer>dbnames=new HashMap<String,Integer>();
			Map<String,Integer>lognames=new HashMap<String,Integer>();
			for (Iterator itr = list.iterator(); itr.hasNext();) {
				Element ele = (Element) itr.next();
				if (ele.getChild("dbname") != null) {
					// 数据库信息文件
					Hashtable<String, String> data = new Hashtable<String, String>();
					String name= ele.getChildText("dbname");
					if(dbnames.get(name)==null){
						data.put("dbname",name);
						data.put("size", ele.getChildText("size"));
						if (ele.getChild("usedperc") != null) {
							data.put("usedperc", ele
									.getChildText("usedperc"));
						}
						if (ele.getChild("usedsize") != null)
							data.put("usedsize", ele
									.getChildText("usedsize"));
						names.add(name);
						datas.put(name, data);
						dbnames.put(name,1);
					}else{
						Hashtable<String,String> col=datas.get(name);
						String v_size=col.get("size");
						String v_usedprec=col.get("usedperc");
						String v_usedsize=col.get("usedsize");
						String c_size=ele.getChildText("size");
						String usedperc=ele.getChildText("usedperc");
						String c_usedsize= ele.getChildText("usedsize");
						float f_v_size=0;
						if(v_size!=null&&!"".equals(v_size)){
							f_v_size=Float.parseFloat(v_size);
						}
						float f_v_usedprec=0;
						if(v_usedprec!=null&&!"".equals(v_usedprec)){
							f_v_usedprec=Float.parseFloat(v_usedprec);
						}
						float f_v_usedsize=0;
						if(v_usedsize!=null&&!"".equals(v_usedsize)){
							f_v_usedsize=Float.parseFloat(v_usedsize);
						}
						float f_c_size=0;
						if(c_size!=null&&!"".equals(c_size)){
							f_c_size=Float.parseFloat(c_size);
						}
						float f_c_usedprec=0;
						if(usedperc!=null&&!"".equals(usedperc)){
							f_c_usedprec=Float.parseFloat(usedperc);
						}
						float f_c_usedsize=0;
						if(c_usedsize!=null&&!"".equals(c_usedsize)){
							f_c_usedsize=Float.parseFloat(c_usedsize);
						}
						float total=f_v_size+f_c_size;
						float usedsize=f_c_usedsize+f_v_usedsize;
						col.put("size",String.valueOf(total));
						col.put("usedsize", String.valueOf(usedsize));
						col.put("usedperc",String.valueOf(usedsize/total*100));
					}
					
				} else if (ele.getChild("logname") != null) {// 日志记录信息
					String name=ele.getChildText("logname");
					if(lognames.get(name)==null){
						Hashtable<String, String> data = new Hashtable<String, String>();
						data.put("logname", name);
						data.put("size", ele.getChildText("size"));
						if (ele.getChild("usedperc") != null) {
							data.put("usedperc", ele
									.getChildText("usedperc"));
						}
						if (ele.getChild("usedsize") != null)
							data.put("usedsize", ele
									.getChildText("usedsize"));
						logs.put(name, data);
						lognames.put(name,1);
					}else{
						Hashtable<String,String> col=logs.get(name);
						String v_size=col.get("size");
						String v_usedprec=col.get("usedperc");
						String v_usedsize=col.get("usedsize");
						String c_size=ele.getChildText("size");
						String usedperc=ele.getChildText("usedperc");
						String c_usedsize= ele.getChildText("usedsize");
						float f_v_size=0;
						if(v_size!=null&&!"".equals(v_size)){
							f_v_size=Float.parseFloat(v_size);
						}
						float f_v_usedprec=0;
						if(v_usedprec!=null&&!"".equals(v_usedprec)){
							f_v_usedprec=Float.parseFloat(v_usedprec);
						}
						float f_v_usedsize=0;
						if(v_usedsize!=null&&!"".equals(v_usedsize)){
							f_v_usedsize=Float.parseFloat(v_usedsize);
						}
						float f_c_size=0;
						if(c_size!=null&&!"".equals(c_size)){
							f_c_size=Float.parseFloat(c_size);
						}
						float f_c_usedprec=0;
						if(usedperc!=null&&!"".equals(usedperc)){
							f_c_usedprec=Float.parseFloat(usedperc);
						}
						float f_c_usedsize=0;
						if(c_usedsize!=null&&!"".equals(c_usedsize)){
							f_c_usedsize=Float.parseFloat(c_usedsize);
						}
						float total=f_v_size+f_c_size;
						float usedsize=f_c_usedsize+f_v_usedsize;
						col.put("size",String.valueOf(total));
						col.put("usedsize", String.valueOf(usedsize));
						col.put("usedperc",String.valueOf(usedsize/total*100));
					}
					
				}
			}
			tables.put("database", datas);
			tables.put("logfile", logs);
			tables.put("names", names);
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return tables;
	}

	/**
	 * 
	 * @return 返回要解析的配置sql配置文件的sqlServerSysTabInfo信息
	 */
	public Hashtable<String, String> getSQLServerSysTabInfo() {
		Hashtable<String, String> table = new Hashtable<String, String>();
		try {
			Element element = (Element) XPath.selectSingleNode(root,
					"//content/sqlServerSysTabInfo/row");
			table.put("productlevel", element.getChildText("productlevel"));
			table.put("VERSION", element.getChildText("version"));
			table.put("MACHINENAME", element.getChildText("machineName"));
			table.put("IsSingleUser", element.getChildText("isSingleUser"));
			table.put("ProcessID", element.getChildText("ProcessID"));
			table.put("IsIntegratedSecurityOnly", element
					.getChildText("isIntegratedSecurityOnly"));
			table.put("IsClustered", element.getChildText("isClustered"));
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return table;
	}

	/**
	 * 
	 * @return 得到SQLServer的状态信息
	 */
	public String getStatus() {
		String str = null;
		try {
			Element element = (Element) XPath.selectSingleNode(root,
					"//content/status");
			str = element.getText();
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return str;
	}

	/**
	 * 
	 * @return 获得要解析的xml文件的pages信息
	 */
	public Hashtable<String, String> getPages() {
		Hashtable<String, String> table = new Hashtable<String, String>();
		try {
			Element element = (Element) XPath.selectSingleNode(root,
					"//content/page");
			List<Element> children = element.getChildren();
			for (Element child : children) {
				table.put(child.getName(), child.getText());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return table;
	}

	/**
	 * 
	 * @return 获得要解析的xml文件的pageConnection信息
	 */
	public Hashtable<String, String> getPageConn() {
		Hashtable<String, String> table = new Hashtable<String, String>();
		try {
			Element element = (Element) XPath.selectSingleNode(root,
					"//content/pageConnection");
			List<Element> children = element.getChildren();
			for (Element child : children) {
				table.put(child.getName(), child.getText());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return table;
	}

	/**
	 * 
	 * @return 获得要解析的xml文件的lock信息
	 */
	public Hashtable<String, String> getLocks() {
		Hashtable<String, String> table = new Hashtable<String, String>();
		try {
			Element element = (Element) XPath.selectSingleNode(root,
					"//content/lock");
			List<Element> children = element.getChildren();
			for (Element child : children) {
				table.put(child.getName(), child.getText());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return table;
	}

	/**
	 * 
	 * @return 获得要解析的xml文件的caches信息
	 */
	public Hashtable<String, String> getCaches() {
		Hashtable<String, String> table = new Hashtable<String, String>();
		try {
			Element element = (Element) XPath.selectSingleNode(root,
					"//content/caches");
			List<Element> children = element.getChildren();
			for (Element child : children) {
				table.put(child.getName(), child.getText());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return table;
	}

	/**
	 * 
	 * @return 获得要解析的xml文件的mems信息
	 */
	public Hashtable<String, String> getMems() {
		Hashtable<String, String> table = new Hashtable<String, String>();
		try {
			Element element = (Element) XPath.selectSingleNode(root,
					"//content/mems");
			List<Element> children = element.getChildren();
			for (Element child : children) {
				table.put(child.getName(), child.getText());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return table;
	}

	/**
	 * 
	 * @return 获得要解析的xml文件的sqls信息
	 */
	public Hashtable<String, String> getSqls() {
		Hashtable<String, String> table = new Hashtable<String, String>();
		try {
			Element element = (Element) XPath.selectSingleNode(root,
					"//content/sqls");
			List<Element> children = element.getChildren();
			for (Element child : children) {
				table.put(child.getName(), child.getText());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return table;
	}

	/**
	 * 
	 * @return 获得要解析的xml文件的scans信息
	 */
	public Hashtable<String, String> getScans() {
		Hashtable<String, String> table = new Hashtable<String, String>();
		try {
			Element element = (Element) XPath.selectSingleNode(root,
					"//content/scans");
			List<Element> children = element.getChildren();
			for (Element child : children) {
				table.put(child.getName(), child.getText());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return table;
	}

	/**
	 * 
	 * @return得到SQLServer的配置的所有信息
	 */
	@SuppressWarnings("unchecked")
	public Hashtable getSQLInital() {
		Hashtable table = new Hashtable();
		table.put("info_v", getSQLProcessesTabInfo());
		table.put("dbValue", getSQLServerDB());
		table.put("sysValue", getSQLServerSysTabInfo());
		table.put("status", getStatus());

		Hashtable retValue = new Hashtable();
		retValue.put("pages",getPages());//缓存管理统计
		retValue.put("pageConnection",getPageConn());//数据库页连接统计
		retValue.put("locks",getLocks());//锁明细
		retValue.put("caches",getCaches());//缓存明细
		retValue.put("mems", getMems());//内存利用情况
		retValue.put("sqls",getSqls());//SQL统计
		retValue.put("scans",getScans());//访问方法的明细 
		table.put("retValue", retValue);
		return table;
	}

	public static void main(String[] args) {
		LoadSQLServerFile init = new LoadSQLServerFile("g://SQL2.xml");
		init.getSQLInital();
	}
}
