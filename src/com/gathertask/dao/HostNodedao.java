package com.gathertask.dao;


import java.util.Hashtable;
import org.apache.log4j.Logger;

//import com.database.DBManager;
import com.afunms.common.util.DBManager;



/**
 * 
 * 
 * 用来查询网元基本信息表
 * 
 * @author Administrator
 *
 */
public class HostNodedao {
	
	
	/**
	 * 
	 * 查询需要管理的网元节点列表
	 * 网元的id做为key，topo_host_node对象为value
	 * @return
	 */
	
	Logger logger=Logger.getLogger(HostNodedao.class);
	
	public Hashtable queryHostmanagedList(){
		
		String sql="select * from topo_host_node where managed='1'";
		DBManager manager=null;
		Hashtable list=new Hashtable();
		try {
			manager=new DBManager();
			list=manager.executeQuerykeyoneListHashMap(sql, "id");
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}finally{
			
			
			if(manager!=null)
				manager.close();
		}
		
		logger.info(list);
		return list;
	}
	
	
	/**
	 * 
	 * 查询出不被管理的列表
	 * 
	 * @return
	 */
	public Hashtable queryHostnotmanagedList(){
		
		String sql="select * from topo_host_node where managed='0' ";
		DBManager manager=null;
		Hashtable list=new Hashtable();
		try {
			manager=new DBManager();
			list=manager.executeQuerykeyoneListHashMap(sql, "id");
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}finally{
			
			
			if(manager!=null)
				manager.close();
		}
		
		logger.info(list);
		return list;
	}
	
	
	public static void main(String[] arg)
	{
		
		HostNodedao dao=new HostNodedao();
		Hashtable table=new Hashtable();
		table=dao.queryHostmanagedList();
		
	}

}
