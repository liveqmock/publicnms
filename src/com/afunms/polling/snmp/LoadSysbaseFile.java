package com.afunms.polling.snmp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.afunms.application.model.SybaseVO;
import com.afunms.application.model.TablesVO;
import java.util.*;

public class LoadSysbaseFile {


	private Element root;

	public LoadSysbaseFile(String path) {
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
	
	
	public SybaseVO getSysbaseVo(){
		SybaseVO vo=new SybaseVO();
		try {
			Element element = (Element)XPath.selectSingleNode(root,"//content");
			vo.setCpu_busy(element.getChildText("cpu_busy"));	
			vo.setCpu_busy_rate(element.getChildText("cpu_busy_rate"));
			vo.setData_hitrate(element.getChildText("data_hitrate"));
			vo.setDisk_count(element.getChildText("disk_count"));
			vo.setIdle(element.getChildText("idle"));
			vo.setIo_busy(element.getChildText("io_busy"));
			vo.setIo_busy_rate(element.getChildText("io_busy_rate"));
			vo.setLocks_count(element.getChildText("locks_count"));
			vo.setMetadata_cache(element.getChildText("metadata_cache"));
			vo.setProcedure_cache(element.getChildText("procedure_cache"));
			vo.setProcedure_hitrate(element.getChildText("procedure_hitrate"));
			vo.setRead_rate(element.getChildText("read_rate"));
			vo.setReceived_rate(element.getChildText("received_rate"));
			vo.setSent_rate(element.getChildText("sent_rate"));
			vo.setServerName(element.getChildText("serverName"));
			vo.setTotal_dataCache(element.getChildText("total_dataCache"));
			vo.setTotal_logicalMemory(element.getChildText("total_logicalMemory"));
			vo.setTotal_physicalMemory(element.getChildText("total_physicalMemory"));
			vo.setVersion(element.getChildText("version"));
			vo.setWrite_rate(element.getChildText("write_rate"));
			vo.setXact_count(element.getChildText("xact_count"));
			List list=XPath.selectNodes(root, "//content/deviceInfo/tables");
			Iterator itr=list.iterator();
			
			//取得deviceInfo
			for(;itr.hasNext();){
				Element ele=(Element)itr.next();
				TablesVO table=new TablesVO();
				table.setDevice_description(ele.getChildText("device_description"));
				table.setDevice_name(ele.getChildText("deviceInfo_device_name"));
				table.setDevice_physical_name(ele.getChildText("device_physical_name"));
				vo.getDeviceInfo().add(table);
			}
			
			//取得userInfo
			 list = XPath.selectNodes(root,"//content/userInfo/tables");
			 itr=list.iterator();
			 for(;itr.hasNext();){
					Element ele=(Element)itr.next();
					TablesVO table=new TablesVO();
					table.setUsers_name(ele.getChildText("users_name"));
					table.setID_in_db(ele.getChildText("ID_in_db"));
					table.setLogin_name(ele.getChildText("login_name"));
					table.setGroup_name(ele.getChildText("group_name"));
					vo.getUserInfo().add(table);
				}
			 
			 //取得serversInfo
			 list = XPath.selectNodes(root,"//content/serversInfo/tables");
			 itr=list.iterator();
			 for(;itr.hasNext();){
					Element ele=(Element)itr.next();
					TablesVO table=new TablesVO();
					table.setServer_class(ele.getChildText("server_class"));
					table.setServer_name(ele.getChildText("server_name"));
					table.setServer_network_name(ele.getChildText("server_network_name"));
					table.setServer_status(ele.getChildText("server_status"));
					vo.getServersInfo().add(table);
				}
			 
			 //取得dbInfo
			 list = XPath.selectNodes(root,"//content/dbInfo/tables");
			 itr=list.iterator();
			 Map<String,Integer>names=new HashMap<String,Integer>();
			 for(;itr.hasNext();){
					Element ele=(Element)itr.next();
					String name=ele.getChildText("db_name");
					if(names.get(name)==null){
						TablesVO table=new TablesVO();
						table.setDb_freesize(ele.getChildText("db_freesize"));
						table.setDb_name(name);	
						table.setDb_size(ele.getChildText("db_size"));
						table.setDb_usedperc(ele.getChildText("db_usedperc"));
						vo.getDbInfo().add(table);
						names.put(name,1);
					}else{
						List<TablesVO>tabs=vo.getDbInfo();
						for(TablesVO tab:tabs){
							String voname=tab.getDb_name();
							if(voname.equals(name)){
								String vofree=tab.getDb_freesize();
								String dbsize=tab.getDb_size();
								String userpred=tab.getDb_usedperc();
								String esize=ele.getChildText("db_size");
								String useprec=ele.getChildText("db_usedperc");
								String cfree=ele.getChildText("db_freesize");
								float fdbsize=0;
								if(dbsize!=null&&!"".equals(dbsize))
									fdbsize=Float.parseFloat(dbsize);
								float fesize=0;
								if(esize!=null&&!"".equals(esize))
									fesize=Float.parseFloat(esize);
								float fvofree=0;
								if(vofree!=null&&!"".equals(vofree))
									fvofree=Float.parseFloat(vofree);
								float fcfree=0;
								if(cfree!=null&&!"".equals(cfree))
									fcfree=Float.parseFloat(cfree);
								float total=fdbsize+fesize;
								//float userd=(fdbsize*fvoused/100+fesize*feused/100)/(fdbsize+fesize)*100;
								float free=fcfree+fvofree;
								tab.setDb_size(String.valueOf(total));
								tab.setDb_freesize(String.valueOf(free));
								tab.setDb_usedperc(String.valueOf((total-free)*100/total));
								break;
							}
						}
					}
					
				}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return vo;
	}
	
	
	/**
	 *  
	 * @return xml的status状态
	 */
	public String getSysbaseStatus(){
		
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
	
	public Hashtable getSysbaseConfig(){
		Hashtable table=new Hashtable();
		table.put("sysbase",getSysbaseVo());
		table.put("status",getSysbaseStatus());
		return table;
	}
	public static void main(String[]args){
		LoadSysbaseFile load=new LoadSysbaseFile("g://Sysbase.xml");
		load.getSysbaseVo();
	}
}
