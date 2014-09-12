package com.afunms.polling.snmp;

import java.io.FileInputStream;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import java.util.*;
public class LoadMySqlFile {

	private Element root;

	public LoadMySqlFile(String path) {
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
	
	public Hashtable getMySqlCongfig(){
		Hashtable mysqls=new Hashtable();
		try {
			List databases=XPath.selectNodes(root, "//content/database");
			Iterator it=databases.iterator();
			while(it.hasNext()){
				Hashtable mysql=new Hashtable();
				Element database=(Element)it.next();
				String databaseName=database.getChildText("databaseName");
				Element configVal=database.getChild("configVal");
				List<Element>values=configVal.getChildren();
				Hashtable config=new Hashtable();
				for(Element v:values){
					config.put(v.getName(),v.getValue());
				}
				mysql.put("configVal", config);
				
				List tableDetatilList=new ArrayList();
				Element tablesDetail=database.getChild("tablesDetail");
				List<Element>columns=tablesDetail.getChildren("column");
				for(Element co:columns){
					String[]items=new String[4];
					items[0]=co.getChildText("name");
					items[1]=co.getChildText("rows");
					items[2]=co.getChildText("data_length");
					items[3]=co.getChildText("create_time");
					tableDetatilList.add(items);
				}
				mysql.put("tablesDetail", tableDetatilList);
				
				Element session=database.getChild("sessionsDetail");
				List sessionsDetail=new ArrayList();
				columns=session.getChildren("column");
				for(Element co:columns){
					String[]items=new String[5];
					items[0]=co.getChildText("user");
					items[1]=co.getChildText("host");
					items[2]=co.getChildText("command");
					items[3]=co.getChildText("time");
					items[4]=co.getChildText("db");
					sessionsDetail.add(items);
				}
				mysql.put("sessionsDetail", sessionsDetail);
				
				Element va=database.getChild("val");
				Vector val=new Vector();
				columns=va.getChildren("column");
				for(Element co:columns){
					Hashtable t=new Hashtable();
					List<Element>leafs=co.getChildren();
					for(Element leaf:leafs){
						t.put(leaf.getName(),leaf.getText());
					}
					val.add(t);
				}
				
				
				//取得Variables信息variables
				//List elements=XPath.selectNodes(root, "//content/variables/column");
				Element variables=database.getChild("variables");
				List elements=variables.getChildren("column");
				Iterator itera=elements.iterator();
				Vector vector=new Vector();
				while(itera.hasNext()){
					Hashtable tb=new Hashtable();
					Element el=(Element)itera.next();
					List<Element>children=el.getChildren();
					for(Element child:children){
						tb.put(child.getName().toLowerCase(),child.getValue());
					}
					vector.add(tb);
				}
				
				//取得global_status信息
				 //elements=XPath.selectNodes(root, "//content/global_status/column");
				 variables=database.getChild("global_status");
				 elements=variables.getChildren("column");
				 itera=elements.iterator();
				Vector vector1=new Vector();
				while(itera.hasNext()){
					Hashtable tb=new Hashtable();
					Element el=(Element)itera.next();
					List<Element>children=el.getChildren();
					for(Element child:children){
						tb.put(child.getName().toLowerCase(),child.getValue());
					}
					vector1.add(tb);
				}
				
				
				Vector dispose=new Vector();
				//elements=XPath.selectNodes(root, "//content/dispose/column");
				 variables=database.getChild("dispose");
				 elements=variables.getChildren("column");
				itera=elements.iterator();
				while(itera.hasNext()){
					Hashtable tb=new Hashtable();
					Element el=(Element)itera.next();
					List<Element>children=el.getChildren();
					for(Element child:children){
						tb.put(child.getName().toLowerCase(),child.getValue());
					}
					dispose.add(tb);
				}
				
				Vector dispose1=new Vector();
				//elements=XPath.selectNodes(root, "//content/dispose1/column");
				 variables=database.getChild("dispose1");
				 elements=variables.getChildren("column");
				itera=elements.iterator();
				while(itera.hasNext()){
					Hashtable tb=new Hashtable();
					Element el=(Element)itera.next();
					List<Element>children=el.getChildren();
					for(Element child:children){
						tb.put(child.getName().toLowerCase(),child.getValue());
					}
					dispose1.add(tb);
				}
				
				Vector dispose2=new Vector();
				//elements=XPath.selectNodes(root, "//content/dispose2/column");
				 variables=database.getChild("dispose2");
				 elements=variables.getChildren("column");
				itera=elements.iterator();
				while(itera.hasNext()){
					Hashtable tb=new Hashtable();
					Element el=(Element)itera.next();
					List<Element>children=el.getChildren();
					for(Element child:children){
						tb.put(child.getName().toLowerCase(),child.getValue());
					}
					dispose2.add(tb);
				}
				
				Vector dispose3=new Vector();
				//elements=XPath.selectNodes(root, "//content/dispose3/column");
				 variables=database.getChild("dispose3");
				 elements=variables.getChildren("column");
				itera=elements.iterator();
				while(itera.hasNext()){
					Hashtable tb=new Hashtable();
					Element el=(Element)itera.next();
					List<Element>children=el.getChildren();
					for(Element child:children){
						tb.put(child.getName().toLowerCase(),child.getValue());
					}
					dispose3.add(tb);
				}
				
				mysql.put("dispose",dispose);
				mysql.put("dispose1",dispose1);
				mysql.put("dispose2",dispose2);
				mysql.put("dispose3",dispose3);
				mysql.put("Val", val);
				mysql.put("variables",vector);
				mysql.put("global_status",vector1);
				mysqls.put(databaseName,mysql);
				Element status=(Element)XPath.selectSingleNode(root, "//content/status");
				mysqls.put("status",status.getText());
			
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return mysqls;
	}
	
	public static void main(String[]args){
		LoadMySqlFile load=new LoadMySqlFile("g://mysql.xml");
		load.getMySqlCongfig();
	}
}
