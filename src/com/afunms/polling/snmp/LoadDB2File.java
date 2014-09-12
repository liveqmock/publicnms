package com.afunms.polling.snmp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import org.jdom.xpath.XPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import java.util.*;

public class LoadDB2File {

	private Element root;

	public LoadDB2File(String path) {
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
	 * @return得到sysinfo信息
	 */
	public Hashtable getDB2Sys(){
		Hashtable table = new Hashtable();
		List list;
		try {
			list = XPath.selectNodes(root,"//content/database");
			Iterator it=list.iterator();
			while(it.hasNext()){
				Element database=(Element)it.next();
				String name=database.getChildText("databaseName");
				Element ele=database.getChild("sysInfo");
				List<Element> children=ele.getChildren();
				Hashtable sysInfo=new Hashtable();
				for(Element child:children){
					sysInfo.put(child.getName(),child.getText());
				}
				table.put(name,sysInfo);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		
		return table;
	}
	
	/**
	 * 
	 * @return space信息
	 */
	public Hashtable getDB2Space(){
		Hashtable table=new Hashtable();
		List list;
		try {
			list = XPath.selectNodes(root,"//content/database");
			Iterator it=list.iterator();
			while(it.hasNext()){
				Element database=(Element)it.next();
				String name=database.getChildText("databaseName");
				Element el=database.getChild("spaceinfo");
				List<Element>children=el.getChildren("column");
				List retList=new ArrayList();
				Map<String,Integer>names=new HashMap<String,Integer>();
				for(Element child:children){
					List<Element>chs=child.getChildren();
					Hashtable column=new Hashtable();
					for(Element ch:chs){
						column.put(ch.getName(),ch.getText());
					}
					String nam=(String)column.get("tablespace_name");
					if(names.get(nam)==null){
						retList.add(column);
						names.put(nam,1);
					}else{
						for(int i=0;i<retList.size();i++){
							Hashtable col=(Hashtable)retList.get(i);
							if(nam.equals(col.get("tablespace_name"))){
								String v_total=(String)col.get("totalspac");
								String v_userspace=(String)col.get("usablespac");
								String c_total=(String)column.get("totalspac");
								String c_userspace=(String)column.get("usablespac");
								float f_v_total=0;
								if(v_total!=null&&!"".equals(v_total))
									f_v_total=Float.parseFloat(v_total);
								float f_v_userspace=0;
								if(v_userspace!=null&&!"".equals(v_userspace))
									f_v_userspace=Float.parseFloat(v_userspace);
								float f_c_total=0;
								if(c_total!=null&&!"".equals(c_total))
									f_c_total=Float.parseFloat(c_total);
								float f_c_userspace=0;
								if(c_userspace!=null&&!"".equals(c_userspace))
									f_c_userspace=Float.parseFloat(c_userspace);
								float total=f_v_total+f_c_total;
								float userspace=f_c_userspace+f_v_userspace;
								col.put("totalspac",String.valueOf(total));
								col.put("usablespac",String.valueOf(userspace));
								if(total>0){
				       				if((total-userspace)>0){
				       					float usedperc = 100*userspace/total;
				       					col.put("usableper", new Float(usedperc).intValue()+"");
				       				}    					
				   				}else{
				   					col.put("usableper", "");
				   				}
								break;
							}
						}
					}
				}
				table.put(name,retList);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return table;
	}
	
	/**
	 * 
	 * @return 得到db2的pool信息
	 */
	 public Hashtable getDB2Pool(){
		 Hashtable table=new Hashtable();
		 try {
			List list = XPath.selectNodes(root,"//content/database");
			Iterator it=list.iterator();
			while(it.hasNext()){
				Element database=(Element)it.next();
				String name=database.getChildText("databaseName");
				Element pools=database.getChild("poolInfo");
				Element poolValue=pools.getChild("poolValue");
				List<Element>children=poolValue.getChildren("column");
				List<Hashtable>retList=new ArrayList<Hashtable>();
				List<Hashtable>readList=new ArrayList<Hashtable>();
				List<Hashtable>writeList=new ArrayList<Hashtable>();
				for(Element child:children){
					Hashtable ss_h=new Hashtable();
					List<Element> colums_s=child.getChildren();
					for(Element co:colums_s){
						ss_h.put(co.getName(),co.getText());
					}
					retList.add(ss_h);
				}
				
				Element readValue=pools.getChild("readValue");
				children=readValue.getChildren("column");
				for(Element child:children){
					Hashtable ss_h=new Hashtable();
					List<Element> colums_s=child.getChildren();
					for(Element co:colums_s){
						ss_h.put(co.getName(),co.getText());
					}
					readList.add(ss_h);
				}
				
				Element writeValue=pools.getChild("writeValue");
				children=writeValue.getChildren("column");
				for(Element child:children){
					Hashtable ss_h=new Hashtable();
					List<Element> colums_s=child.getChildren();
					for(Element co:colums_s){
						ss_h.put(co.getName(),co.getText());
					}
					writeList.add(ss_h);
				}
				Hashtable rethash=new Hashtable();
				rethash.put("poolValue", retList);
	   			rethash.put("readValue", readList);
	   			rethash.put("writeValue", writeList);
	   			table.put(name,rethash);
			}		
			
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		 return table;
	 }
	 
	 public Hashtable getDB2Session(){
		 Hashtable table=new Hashtable();
		 try {
			List list = XPath.selectNodes(root,"//content/database");
			Iterator it=list.iterator();
			while(it.hasNext()){
				Element database=(Element)it.next();
				String name=database.getChildText("databaseName");
				Element session=database.getChild("session");
				List<Element>children=session.getChildren("column");
				List<Hashtable>retList=new ArrayList();
				for(Element child:children){
					Hashtable column=new Hashtable();
					List<Element>cos=child.getChildren();
					for(Element co:cos){
						column.put(co.getName(),co.getText());
					}
					retList.add(column);
				}
				table.put(name,retList);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}	 
		 return table;
	 }
	 
	 /**
	  * 
	  * @return 得到db2的lock信息
	  */
	 public Hashtable<String,List> getDB2Lock(){
		 Hashtable<String,List>table=new Hashtable();
		 try {
			List databases = XPath.selectNodes(root,"//content/database");
			Iterator it=databases.iterator();
			while(it.hasNext()){
				List<Hashtable> list=new ArrayList<Hashtable>();
				Element database=(Element)it.next();
				String name=database.getChildText("databaseName");
				Element lockInfo=database.getChild("lock");
				List<Element>children=lockInfo.getChildren("column");
				for(Element child:children){
					Hashtable t=new Hashtable();
					List<Element>los=child.getChildren();
					for(Element lo:los){
						t.put(lo.getName(),lo.getText());
					}
					list.add(t);
				}
				table.put(name,list);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
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
		 * @return 得到要解析的xml文件的所有信息
		 */
		public Hashtable getDB2Init(){
			Hashtable table=new Hashtable();
			table.put("status",getStatus());
			table.put("sysInfo",getDB2Sys());
			table.put("spaceInfo",getDB2Space());
			table.put("poolInfo",getDB2Pool());
			table.put("session",getDB2Session());
			table.put("lock",getDB2Lock());
			return table;
		}
		
	 public static void main(String[]args){
		 
		 LoadDB2File load=new LoadDB2File("g://db2.xml");
		 load.getDB2Init();
	 }
}
