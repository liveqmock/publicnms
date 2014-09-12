/**
 * <p>Description:operate table NMS_TOPO_LINK</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-10-12
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.om.UtilHdx;
import com.afunms.topology.model.Link;

public class LinkDao extends BaseDao implements DaoInterface
{
   public LinkDao()
   {
	   super("topo_network_link");	   	  
   }

   public List<Link> loadNetLinks()
   {
	   return loadByTpye(1);
   }

   public List<Link> loadServerLinks()
   {
	   return loadByTpye(2);
   }
   
   public List<Link> loadAll()
   {
	   return loadByTpye(0);
   }
   
   public List<Link> loadByTpye(int type)
   {
	   List list = new ArrayList();
	   
	   String subsql = null;
	   if(type==0) subsql = "";
	   else if(type==1) subsql = " and a.type=1";
	   else if(type==2) subsql = " and a.type=2";
	   
	   String sql = "select a.*,b.alias start_alias,c.alias end_alias from topo_network_link a,"
	              + "topo_host_node b,topo_host_node c where a.start_id=b.id and a.end_id=c.id"
	              + subsql + " order by a.id";
	   try
	   {
		   rs = conn.executeQuery(sql);
		   while(rs.next())
		     list.add(loadFromRS(rs));
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LinkDao.loadAll()",e); 
	   }
	   finally
	   {
		   conn.close();//yangjun
	   }
	   return list;
   }
   
   public boolean saveLinkOnly(Link vo)
   {
       int assistant = 0;
	   String temSql = "select * from topo_network_link where (start_id=" + vo.getStartId() 
	              + " and end_id=" + vo.getEndId() +") or (start_id=" + vo.getEndId() 
	              + " and end_id=" + vo.getStartId() + ")";
	   try
	   {
          rs = conn.executeQuery(temSql);
          if(rs.next()){
        	  Link vo1 = new Link();
			  vo1 = (Link) loadFromRS(rs);
			  if(vo1.getAssistant()==0){
				  assistant = 1;
			  }else{
				  assistant = 0;
			  }
          }
	   }
	   catch(Exception e){}
	   
	   int id = getNextID();	   
	   // sql有变动
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("insert into topo_network_link(id,link_name,start_id,start_index,start_ip,start_descr,");
	   sql.append("end_id,end_index,end_ip,end_descr,assistant,type,findtype,linktype,max_speed,max_per)values(");
	   sql.append(id);
	   sql.append(",'");
	   sql.append(vo.getLinkName());
	   sql.append("',");
	   
	   sql.append(vo.getStartId());
	   sql.append(",'");
	   sql.append(vo.getStartIndex());
	   sql.append("','");
	   sql.append(vo.getStartIp());
	   sql.append("','");
	   sql.append(vo.getStartDescr());
	   sql.append("',");
	   sql.append(vo.getEndId());
	   sql.append(",'");
	   sql.append(vo.getEndIndex());
	   sql.append("','");
	   sql.append(vo.getEndIp());
	   sql.append("','");
	   sql.append(vo.getEndDescr());
	   sql.append("',");	   
	   sql.append(assistant);
	   sql.append(",");
	   sql.append(vo.getType());
	   sql.append(",");
	   sql.append(vo.getFindtype());
	   sql.append(",");
	   sql.append(vo.getLinktype());
	   sql.append(",'");
	   sql.append(vo.getMaxSpeed());
	   sql.append("','");
	   sql.append(vo.getMaxPer());
	   sql.append("')");
	   //SysLogger.info("########################");
	   SysLogger.info(sql.toString());
	   boolean flag = false;

	   try{
		   flag = saveOrUpdate(sql.toString());
	   }catch(Exception e){
		   
	   }
	   return flag; 
   }

   
   //yangjun add
   public List<Link> findByIP(String ip){
	   List list = new ArrayList();
	   String sql = "select * from topo_network_link where start_ip='" + ip + "' or end_ip='" + ip + "'";
	   try
	   {
		   rs = conn.executeQuery(sql);
		   while(rs.next())
		     list.add(loadFromRS(rs));
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LinkDao.findByIP()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return list;
   }
   
   //yangjun add
   public List<Link> findByNodeId(String nodeid){
	   List list = new ArrayList();
	   String sql = "select * from topo_network_link where start_id=" + nodeid + " or end_id=" + nodeid ;
	   try
	   {
		   rs = conn.executeQuery(sql);
		   while(rs.next())
		     list.add(loadFromRS(rs));
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LinkDao.findByNodeId()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return list;
   }
   public String linkExists(int startId,String startIndex,int endId,String endIndex)
   {	   
	   String sql = null;
	   try
	   {
		   sql = "select * from topo_network_link where (start_id=" + startId + " and start_index='" + startIndex 
		       + "') or (start_id=" + endId + " and start_index='" + endIndex + "') or " +
		       	 "(end_id=" + endId + " and end_index='" + endIndex + "') or (end_id=" + startId + " and end_index='" + startIndex + "')";
		   rs = conn.executeQuery(sql);
		   if(rs.next()){
			   Link vo = new Link();
			   vo = (Link) loadFromRS(rs);
			   return vo.getId()+":"+1+":"+vo.getAssistant();
		   }
		   sql = "select * from topo_network_link where (start_id=" + startId + " and end_id=" + endId 
		       + ") or (start_id=" + endId + " and end_id=" + startId + ")";		   
		   rs = conn.executeQuery(sql);
		   int i =0;
		   String result = "";
		   while(rs.next()){
			   Link vo = new Link();
			   vo = (Link) loadFromRS(rs);
			   i++;
			   result = result+":"+vo.getId()+":"+vo.getAssistant();
		   }
		   return i+":"+2+result;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LinkDao.linkExist()",e); 
	   }
	   return ""+0;	      
   }

   public boolean linkExists2(int startId,String startDescr,int endId,String endDescr)
   {	   
	   String sql = null;
	   try
	   {
		   sql = "select * from topo_network_link where (start_id=" + startId + " and start_descr='" + startDescr 
		       + "' and end_id=" + endId + " and end_descr='" + endDescr + "') or " +
		       	 "(start_id=" + endId + " and start_descr='" + endDescr + "' and end_id=" + startId + " and end_descr='" + startDescr + "')";
		   rs = conn.executeQuery(sql);
		   if(rs.next()){
			   Link vo = new Link();
			   vo = (Link) loadFromRS(rs);
			   return true;
		   }
		   return false;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LinkDao.linkExist()",e); 
	   }
	   return false;	      
   }
   
   public int linkExist(int startId,String startIndex,int endId,String endIndex)
   {	   
	   String sql = null;
	   try
	   {
		   sql = "select * from topo_network_link where start_id=" + startId + " and end_id=" + endId 
		       + " and start_index='" + startIndex + "' and end_index='" + endIndex + "'";
		   rs = conn.executeQuery(sql);
		   if(rs.next())
			 return 1;
		   
		   sql = "select * from topo_network_link where start_id=" + startId + " and end_id=" + endId 
		       + " and assistant=1";		   
		   rs = conn.executeQuery(sql);
		   if(rs.next())
			 return 2;          		   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LinkDao.linkExist()",e); 
	   }
	   return 0;	      
   }

   public int linkExist(int startId,int endId)
   {	   
	   String sql = null;
	   try
	   {
		   sql = "select * from topo_network_link where start_id=" + startId + " and end_id=" + endId;		   
		   rs = conn.executeQuery(sql);
		   if(rs.next())
			 return 1;          		   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LinkDao.linkExist()",e); 
	   }
	   return 0;	      
   }
   
   public boolean save(BaseVo baseVo)
   {
	   return false;
   }
   
   public Link save(Link vo)
   {
       int assistant = 0;
	   String temSql = "select * from topo_network_link where (start_id=" + vo.getStartId() 
	              + " and end_id=" + vo.getEndId() +") or (start_id=" + vo.getEndId() 
	              + " and end_id=" + vo.getStartId() + ")";
	   try
	   {
          rs = conn.executeQuery(temSql);
          if(rs.next()){
        	  Link vo1 = new Link();
			  vo1 = (Link) loadFromRS(rs);
			  if(vo1.getAssistant()==0){
				  assistant = 1;
			  }else{
				  assistant = 0;
			  }
          }
	   }
	   catch(Exception e){}
	   
	   int id = getNextID();	   
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("insert into topo_network_link(id,link_name,link_alias_name,start_id,start_index,start_ip,start_descr,");
	   sql.append("end_id,end_index,end_ip,end_descr,assistant,type,findtype,linktype,max_speed,max_per)values(");
	   sql.append(id);
	   sql.append(",'");
	   sql.append(vo.getLinkName()); 
	   sql.append("','");
	   sql.append(vo.getLinkAliasName());
	   sql.append("','");
	   sql.append(vo.getStartId());
	   sql.append("','");
	   sql.append(vo.getStartIndex());
	   sql.append("','");
	   sql.append(vo.getStartIp());
	   sql.append("','");
	   sql.append(vo.getStartDescr());
	   sql.append("',");
	   sql.append(vo.getEndId());
	   sql.append(",'");
	   sql.append(vo.getEndIndex());
	   sql.append("','");
	   sql.append(vo.getEndIp());
	   sql.append("','");
	   sql.append(vo.getEndDescr());
	   sql.append("',");	   
	   sql.append(assistant);
	   sql.append(",");
	   sql.append(vo.getType());
	   sql.append(",");
	   sql.append(vo.getFindtype());
	   sql.append(",");
	   sql.append(vo.getLinktype());
	   sql.append(",'");
	   sql.append(vo.getMaxSpeed());
	   sql.append("','");
	   sql.append(vo.getMaxPer());
	   sql.append("')");
	   SysLogger.info("########################");
	   SysLogger.info(sql.toString());
	   boolean flag = true;
	   CreateTableManager ctable = new CreateTableManager();
	   try{
		   flag = saveOrUpdate(sql.toString());
		   conn = new DBManager();
		   vo.setId(id);
		   try{
			   	ctable.createTable(conn,"lkping",vo.getId()+"","lkping");//链路状态
			    
				ctable.createTable(conn,"lkpinghour",vo.getId()+"","lkpinghour");//链路状态按小时
			    
				ctable.createTable(conn,"lkpingday",vo.getId()+"","lkpingday");//链路状态按天
				
				
				ctable.createTable(conn,"lkuhdx",vo.getId()+"","lkuhdx");//链路流速
			    
				ctable.createTable(conn,"lkuhdxhour",vo.getId()+"","lkuhdxhour");//链路流速
			    
				ctable.createTable(conn,"lkuhdxday",vo.getId()+"","lkuhdxday");//链路流速
				
				ctable.createTable(conn,"lkuhdxp",vo.getId()+"","lkuhdxp");//链路带宽利用率
			    
				ctable.createTable(conn,"lkuhdxphour",vo.getId()+"","lkuhdxphour");//链路带宽利用率
			    
				ctable.createTable(conn,"lkuhdxpday",vo.getId()+"","lkuhdxpday");//链路带宽利用率
		   }catch(Exception e){
			   e.printStackTrace();
		   }finally{
		  		try{
					conn.executeBatch();
				}catch(Exception e){
					
				}
			   conn.close();
		   }
		   
		   
	   }catch(Exception e){
		   
	   }
	   if(flag)
	   {
		   vo.setId(id);
		   vo.setAssistant(assistant);
	   }		  
	   return vo; 
   }
   
   /**
    * 批处理保存 saveutil /savelinkstatus/ savelinkutilperc  
    * @param v
    * @param linkstatusv
    * @param utilhdxpercv
    * @param linkid
    * @return
    */
   public boolean processlinkData(){
	   Hashtable allLinkData = ShareData.getAllLinkData();
	   if(allLinkData == null || allLinkData.isEmpty()){
		   return false;
	   }
	   //SysLogger.info(" 1##### 开始处理链路入库 ##############");
	   Iterator iterator = allLinkData.keySet().iterator();
	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   while (iterator.hasNext()) {
		   String linkid = String.valueOf(iterator.next()); 
		   Hashtable linkData = (Hashtable)allLinkData.get(Integer.parseInt(linkid));
		   if(linkData == null || linkData.isEmpty()){
			   //SysLogger.info(" 4##### 开始处理链路入库 ##############");
			   continue;
		   }
		   Vector v = (Vector)linkData.get("util");
		   Vector linkstatusv = (Vector)linkData.get("linkstatus");
		   Vector utilhdxpercv = (Vector)linkData.get("linkutilperc");
		   //Vector v,Vector linkstatusv,Vector utilhdxpercv, int linkid
		   if(v == null && linkstatusv == null && utilhdxpercv == null){
			   //SysLogger.info(" 2##### 开始处理链路入库 ##############");
			   return false;
		   }
		   //SysLogger.info(" 3##### 开始处理链路入库 ##############");
		    //saveutil
			if(v != null && v.size()>0){
				   for(int i=0;i<v.size();i++){
					   UtilHdx vo = (UtilHdx)v.get(i);
					   Calendar tempCal = (Calendar) vo.getCollecttime();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						String tablename = "lkuhdx" + linkid;
		
						String sql ="";
//							"insert into " + tablename
//								+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
//								+ "values('','" + vo.getRestype() + "','" + vo.getCategory() + "','"
//								+ vo.getEntity() + "','" + vo.getSubentity() + "','" + vo.getUnit()
//								+ "','" + vo.getChname() + "','" + vo.getBak() + "'," + vo.getCount()
//								+ ",'" + vo.getThevalue() + "','" + time + "')";
						if(SystemConstant.DBType.equals("mysql")){
							sql = "insert into " + tablename
							+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
							+ "values('','" + vo.getRestype() + "','" + vo.getCategory() + "','"
							+ vo.getEntity() + "','" + vo.getSubentity() + "','" + vo.getUnit()
							+ "','" + vo.getChname() + "','" + vo.getBak() + "'," + vo.getCount()
							+ ",'" + vo.getThevalue() + "','" + time + "')";
						}else if (SystemConstant.DBType.equals("oracle")){
							sql = "insert into " + tablename
							+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
							+ "values('','" + vo.getRestype() + "','" + vo.getCategory() + "','"
							+ vo.getEntity() + "','" + vo.getSubentity() + "','" + vo.getUnit()
							+ "','" + vo.getChname() + "','" + vo.getBak() + "'," + vo.getCount()
							+ ",'" + vo.getThevalue() + "',to_date('" + time + "','yyyy-mm-dd hh24:mi:ss'))";
						}
				        conn.addBatch(sql);
				   }
			   }
			   //savelinkstatus
			   if(linkstatusv != null && linkstatusv.size()>0){
				   for(int i=0;i<linkstatusv.size();i++){
					   Interfacecollectdata vo = (Interfacecollectdata)linkstatusv.get(i);
					   Calendar tempCal = (Calendar) vo.getCollecttime();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						String tablename = "lkping" + linkid;
		
						String sql ="";
						if(SystemConstant.DBType.equals("mysql")){
							sql = "insert into " + tablename
							+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
							+ "values('','" + vo.getRestype() + "','" + vo.getCategory() + "','"
							+ vo.getEntity() + "','" + vo.getSubentity() + "','" + vo.getUnit()
							+ "','" + vo.getChname() + "','" + vo.getBak() + "'," + vo.getCount()
							+ ",'" + vo.getThevalue() + "','" + time + "')";
						}else if (SystemConstant.DBType.equals("oracle")){
							sql = "insert into " + tablename
							+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
							+ "values('','" + vo.getRestype() + "','" + vo.getCategory() + "','"
							+ vo.getEntity() + "','" + vo.getSubentity() + "','" + vo.getUnit()
							+ "','" + vo.getChname() + "','" + vo.getBak() + "'," + vo.getCount()
							+ ",'" + vo.getThevalue() + "',to_date('" + time + "','yyyy-mm-dd hh24:mi:ss'))";
						}
						//SysLogger.info(sql);
						conn.addBatch(sql);
				   }
			   }
			   //savelinkutilperc
			   if(utilhdxpercv != null && utilhdxpercv.size()>0){
				   for(int i=0;i<utilhdxpercv.size();i++){
					   Interfacecollectdata vo = (Interfacecollectdata)utilhdxpercv.get(i);
					   Calendar tempCal = (Calendar) vo.getCollecttime();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						String tablename = "lkuhdxp" + linkid;
		
						String sql = "";
						if(SystemConstant.DBType.equals("mysql")){
							sql = "insert into " + tablename
							+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
							+ "values('','" + vo.getRestype() + "','" + vo.getCategory() + "','"
							+ vo.getEntity() + "','" + vo.getSubentity() + "','" + vo.getUnit()
							+ "','" + vo.getChname() + "','" + vo.getBak() + "'," + vo.getCount()
							+ ",'" + vo.getThevalue() + "','" + time + "')";
						}else if (SystemConstant.DBType.equals("oracle")){
							sql = "insert into " + tablename
							+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
							+ "values('','" + vo.getRestype() + "','" + vo.getCategory() + "','"
							+ vo.getEntity() + "','" + vo.getSubentity() + "','" + vo.getUnit()
							+ "','" + vo.getChname() + "','" + vo.getBak() + "'," + vo.getCount()
							+ ",'" + vo.getThevalue() + "',to_date('" + time + "','yyyy-mm-dd hh24:mi:ss'))";
						}
//						System.out.println("LinkDao.java-----------500行=======>>>"+sql);
						conn.addBatch(sql);
				   }
			   }
	   }
	   try {
		   conn.executeBatch();
	   } catch (RuntimeException e) {
		   e.printStackTrace();
	   } finally{
		   conn.close();
	   }
	   return true;
   }
   
   public boolean saveutil(Vector v, int linkid)
   {
	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   
	   
	   if(v != null && v.size()>0){
		   for(int i=0;i<v.size();i++){
			   UtilHdx vo = (UtilHdx)v.get(i);
			   Calendar tempCal = (Calendar) vo.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "lkuhdx" + linkid;

				String sql = "insert into " + tablename
						+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+ "values('','" + vo.getRestype() + "','" + vo.getCategory() + "','"
						+ vo.getEntity() + "','" + vo.getSubentity() + "','" + vo.getUnit()
						+ "','" + vo.getChname() + "','" + vo.getBak() + "'," + vo.getCount()
						+ ",'" + vo.getThevalue() + "','" + time + "')";
		        conn.executeUpdate(sql);
		   }
	   }
		  
	   return true; 
   }
   
   public boolean savelinkstatus(Vector v, int linkid)
   {
	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   
	   
	   if(v != null && v.size()>0){
		   for(int i=0;i<v.size();i++){
			   Interfacecollectdata vo = (Interfacecollectdata)v.get(i);
			   Calendar tempCal = (Calendar) vo.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "lkping" + linkid;

				String sql = "insert into " + tablename
						+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+ "values('','" + vo.getRestype() + "','" + vo.getCategory() + "','"
						+ vo.getEntity() + "','" + vo.getSubentity() + "','" + vo.getUnit()
						+ "','" + vo.getChname() + "','" + vo.getBak() + "'," + vo.getCount()
						+ ",'" + vo.getThevalue() + "','" + time + "')";
		       conn.executeUpdate(sql);
		   }
	   }
		  
	   return true; 
   }
   
   public boolean savelinkutilperc(Vector v, int linkid)
   {
	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   
	   
	   if(v != null && v.size()>0){
		   for(int i=0;i<v.size();i++){
			   Interfacecollectdata vo = (Interfacecollectdata)v.get(i);
			   Calendar tempCal = (Calendar) vo.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "lkuhdxp" + linkid;

				String sql = "insert into " + tablename
						+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+ "values('','" + vo.getRestype() + "','" + vo.getCategory() + "','"
						+ vo.getEntity() + "','" + vo.getSubentity() + "','" + vo.getUnit()
						+ "','" + vo.getChname() + "','" + vo.getBak() + "'," + vo.getCount()
						+ ",'" + vo.getThevalue() + "','" + time + "')";
		        conn.executeUpdate(sql);
		   }
	   }
		  
	   return true; 
   }
   
   public boolean deleteutils(List list)
   {
	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   
	   
	   if(list != null && list.size()>0){
		   for(int i=0;i<list.size();i++){
			   UtilHdx vo = (UtilHdx)list.get(i);
				CreateTableManager ctable = new CreateTableManager();
			    try{
				    ctable.createTable(conn,"lkping",vo.getId()+"","lkping");//链路状态
					ctable.createTable(conn,"lkpinghour",vo.getId()+"","lkpinghour");//链路状态按小时
					ctable.createTable(conn,"lkpingday",vo.getId()+"","lkpingday");//链路状态按天
					
					
					ctable.createTable(conn,"lkuhdx",vo.getId()+"","lkuhdx");//链路流速
					ctable.createTable(conn,"lkuhdxhour",vo.getId()+"","lkuhdxhour");//链路流速
					ctable.createTable(conn,"lkuhdxday",vo.getId()+"","lkuhdxday");//链路流速
					
					ctable.createTable(conn,"lkuhdxp",vo.getId()+"","lkuhdxp");//链路带宽利用率
					ctable.createTable(conn,"lkuhdxphour",vo.getId()+"","lkuhdxphour");//链路带宽利用率
					ctable.createTable(conn,"lkuhdxpday",vo.getId()+"","lkuhdxpday");//链路带宽利用率
			    }catch(Exception e){
			    	e.printStackTrace();
			    }finally{
			  		try{
						conn.executeBatch();
					}catch(Exception e){
						
					}
			    	//conn.close();
			    }
		   }
	   }
		  
	   return true; 
   }
   
   public boolean update(Link vo)
   {   
	   //int id = getNextID();	   
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("update topo_network_link set ");
	   sql.append(" link_name = '"+vo.getLinkName()+"'");
	   sql.append(", link_alias_name = '"+vo.getLinkAliasName() + "'");
	   sql.append(", start_id = "+vo.getStartId());
	   sql.append(", start_index='"+vo.getStartIndex()+"'");
	   sql.append(", start_ip='"+vo.getStartIp()+"'");
	   sql.append(", start_descr='"+vo.getStartDescr()+"'");
	   sql.append(", end_id="+vo.getEndId());
	   sql.append(", end_index='"+vo.getEndIndex()+"'");
	   sql.append(", end_ip='"+vo.getEndIp()+"'");
	   sql.append(", end_descr='"+vo.getEndDescr()+"'");
	   sql.append(", assistant="+vo.getAssistant());
	   sql.append(", type="+vo.getType());
	   sql.append(", findtype="+vo.getFindtype());
	   sql.append(", linktype="+vo.getLinktype());
	   sql.append(", max_speed='"+vo.getMaxSpeed()+"'");
	   sql.append(", max_per='"+vo.getMaxPer()+"'");
	   sql.append(", showinterf='"+vo.getShowinterf()+"'");
	   sql.append(" where id = "+vo.getId());
	   
	   return saveOrUpdate(sql.toString()); 
   }
   
   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {
		   conn.executeUpdate("delete from topo_network_link where id=" + id);
		   SysLogger.info("delete from topo_network_link where id=" + id);
			CreateTableManager ctable = new CreateTableManager();
		    try{
			    ctable.deleteTable(conn,"lkping",id+"","lkping");//链路状态
				ctable.deleteTable(conn,"lkpinghour",id,"lkpinghour");//链路状态按小时
				ctable.deleteTable(conn,"lkpingday",id,"lkpingday");//链路状态按天
				
				
				ctable.deleteTable(conn,"lkuhdx",id,"lkuhdx");//链路流速
				ctable.deleteTable(conn,"lkuhdxhour",id,"lkuhdxhour");//链路流速
				ctable.deleteTable(conn,"lkuhdxday",id,"lkuhdxday");//链路流速
				
				ctable.deleteTable(conn,"lkuhdxp",id,"lkuhdxp");//链路带宽利用率
				ctable.deleteTable(conn,"lkuhdxphour",id,"lkuhdxphour");//链路带宽利用率
				ctable.deleteTable(conn,"lkuhdxpday",id,"lkuhdxpday");//链路带宽利用率
		    }catch(Exception e){
		    	e.printStackTrace();
		    }finally{
		    	//conn.close();
		    }
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LinkDao.delete()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return result;
   }
   
   public boolean delete(List list)
   {
	   boolean result = false;
	   try
	   {
		   if(list != null && list.size()>0){
			   Link link = null;
			   for(int i=0;i<list.size();i++){
				   link = (Link)list.get(i);
				   conn.executeUpdate("delete from topo_network_link where id=" + link.getId());
				   SysLogger.info("delete from topo_network_link where id=" + link.getId());
					CreateTableManager ctable = new CreateTableManager();
				    try{
					    ctable.deleteTable(conn,"lkping",link.getId()+"","lkping");//链路状态
						ctable.deleteTable(conn,"lkpinghour",link.getId()+"","lkpinghour");//链路状态按小时
						ctable.deleteTable(conn,"lkpingday",link.getId()+"","lkpingday");//链路状态按天
						
						
						ctable.deleteTable(conn,"lkuhdx",link.getId()+"","lkuhdx");//链路流速
						ctable.deleteTable(conn,"lkuhdxhour",link.getId()+"","lkuhdxhour");//链路流速
						ctable.deleteTable(conn,"lkuhdxday",link.getId()+"","lkuhdxday");//链路流速
						
						ctable.deleteTable(conn,"lkuhdxp",link.getId()+"","lkuhdxp");//链路带宽利用率
						ctable.deleteTable(conn,"lkuhdxphour",link.getId()+"","lkuhdxphour");//链路带宽利用率
						ctable.deleteTable(conn,"lkuhdxpday",link.getId()+"","lkuhdxpday");//链路带宽利用率
				    }catch(Exception e){
				    	e.printStackTrace();
				    }finally{
				    	//conn.close();
				    }
			   }
			   
		   }

		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LinkDao.delete()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return result;
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   Link vo = new Link();
	   try
	   {
		   vo.setId(rs.getInt("id"));
		   vo.setLinkName(rs.getString("link_name"));
		   vo.setLinkAliasName(rs.getString("link_alias_name"));
		   vo.setStartId(rs.getInt("start_id"));
		   vo.setEndId(rs.getInt("end_id"));
		   vo.setStartPort(rs.getString("start_port"));
		   vo.setEndPort(rs.getString("end_port"));
		   vo.setStartIndex(rs.getString("start_index"));
		   vo.setEndIndex(rs.getString("end_index"));
		   vo.setStartDescr(rs.getString("start_descr"));
		   vo.setEndDescr(rs.getString("end_descr"));
		   vo.setStartIp(rs.getString("start_ip"));
		   vo.setEndIp(rs.getString("end_ip"));
		   vo.setShowinterf(rs.getInt("showinterf"));
		   try{
			   vo.setStartAlias(rs.getString("start_alias"));
			   vo.setEndAlias(rs.getString("end_alias"));
		   }catch(Exception e){
			   
		   }
		   vo.setStartMac(rs.getString("start_mac"));
		   vo.setEndMac(rs.getString("end_mac"));
		   vo.setAssistant(rs.getInt("assistant"));
		   vo.setType(rs.getInt("type"));
		   vo.setFindtype(rs.getInt("findtype"));
		   vo.setLinktype(rs.getInt("linktype"));
		   vo.setMaxSpeed(rs.getString("max_speed"));
		   vo.setMaxPer(rs.getString("max_per"));
	   }
       catch(Exception e)
       {
    	   e.printStackTrace();
 	       SysLogger.error("LinkDao.loadFromRS()",e); 
       }	   
       return vo;
   }
   
   public boolean update(BaseVo baseVo)
   {
	   return false;   
   }
   public String loadOneColFromRS(ResultSet rs){
	   return "";
   }

   /**
    * 根据链路列表得到其链路的历史数据信息列表 String:链路ID  List<Systemcollectdata>：链路临时数据列表
    * @param linkList
    * @return
    */
	public Hashtable<String,List<Systemcollectdata>> getLinkDataByLinkId(List linkList, String startdate, String todate) {
		Hashtable<String, List<Systemcollectdata>> hashtable = new Hashtable<String, List<Systemcollectdata>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			for (int i = 0; i < linkList.size(); i++) {
				List<Systemcollectdata> list = new ArrayList<Systemcollectdata>();
				Link link = (Link) linkList.get(i);
				StringBuffer sBuffer = new StringBuffer();
				sBuffer.append("select id,entity,thevalue,unit, collecttime from lkuhdx");
				sBuffer.append(link.getId());
				sBuffer.append(" where collecttime between '");
				sBuffer.append(startdate);
				sBuffer.append("' and '");
				sBuffer.append(todate);
				sBuffer.append("'");
//				System.out.println(sBuffer.toString());
				rs = conn.executeQuery(sBuffer.toString());
				while (rs.next()) {
					Systemcollectdata systemcollectdata = new Systemcollectdata();
					systemcollectdata.setId(Long.parseLong(rs.getString("id")));
					systemcollectdata.setEntity(rs.getString("entity"));
					systemcollectdata.setThevalue(rs.getString("thevalue"));
					systemcollectdata.setUnit(rs.getString("unit"));
					Date date = sdf.parse(rs.getString("collecttime"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					systemcollectdata.setCollecttime(cal);
					list.add(systemcollectdata);
				}
				hashtable.put(link.getId() + "", list);
				sBuffer = null;
			}
		} catch (Exception e) {
			SysLogger.error("LinkDao.getLinkDataByLinkId()", e);
		} finally {
			conn.close();
		}
		return hashtable;
	}
	
	/**
    * 根据链路列表得到其链路的历史数据信息列表 String:链路ID  List<Systemcollectdata>：链路临时数据列表
    * @param linkList
    * @return
    */
	public Hashtable<String,List<Systemcollectdata>> getLinkBandwidthDataByLinkId(List linkList, String startdate, String todate) {
		Hashtable<String, List<Systemcollectdata>> hashtable = new Hashtable<String, List<Systemcollectdata>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			for (int i = 0; i < linkList.size(); i++) {
				List<Systemcollectdata> list = new ArrayList<Systemcollectdata>();
				Link link = (Link) linkList.get(i);
				StringBuffer sBuffer = new StringBuffer();
				sBuffer.append("select id,entity,thevalue,unit, collecttime from lkuhdxp");
				sBuffer.append(link.getId());
				sBuffer.append(" where collecttime between '");
				sBuffer.append(startdate);
				sBuffer.append("' and '");
				sBuffer.append(todate);
				sBuffer.append("'");
//					System.out.println(sBuffer.toString());
				rs = conn.executeQuery(sBuffer.toString());
				while (rs.next()) {
					Systemcollectdata systemcollectdata = new Systemcollectdata();
					systemcollectdata.setId(Long.parseLong(rs.getString("id")));
					systemcollectdata.setEntity(rs.getString("entity"));
					systemcollectdata.setThevalue(rs.getString("thevalue"));
					systemcollectdata.setUnit(rs.getString("unit"));
					Date date = sdf.parse(rs.getString("collecttime"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					systemcollectdata.setCollecttime(cal);
					list.add(systemcollectdata);
				}
				hashtable.put(link.getId() + "", list);
				sBuffer = null;
			}
		} catch (Exception e) {
			SysLogger.error("LinkDao.getLinkBandwidthDataByLinkId()", e);
		} finally {
			conn.close();
		}
		return hashtable;
	}

	public List loadListByIds(String linkids) {
		StringBuffer condition = new StringBuffer();
		if (linkids == null || linkids.equals("")) {
			condition.append("");
		} else {
			String[] linkidStrings = linkids.split(",");
			condition.append("and a.id in ('");
			for (int i = 0; i < linkidStrings.length; i++) {
				String linkid = linkidStrings[i];
				if (linkid == null && linkid.trim().equals("")) {
					continue;
				}
				condition.append(linkid);
				if (i != linkidStrings.length - 1) {
					condition.append("','");
				}
			}
			condition.append("')");
		}
		List list = new ArrayList();
		String sql = "select a.*,b.alias start_alias,c.alias end_alias from topo_network_link a,"
				+ "topo_host_node b,topo_host_node c where a.start_id=b.id and a.end_id=c.id "
				+ condition + " order by a.id";
		try {
			rs = conn.executeQuery(sql);
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			SysLogger.error("LinkDao.loadListByIds()", e);
		} finally {
			conn.close();//yangjun
		}
		return list;
	}
	
	/**
	 * 批量更新链路阀值
	 * @param linkids
	 * @param maxPer    带宽利用率阀值(%)
	 * @param maxSpeed  流量阀值（KB/S）
	 * @return
	 */
	public boolean batchUpdateThresholdLevel(String linkids, String maxPer, String maxSpeed) {
		String[] linkidsArray = linkids.split(",");
		StringBuffer sql = new StringBuffer();
		sql.append("update topo_network_link set max_speed='");
		sql.append(maxSpeed);
		sql.append("', max_per='");
		sql.append(maxPer);
		sql.append("' where id in ('");
		if (linkidsArray != null) {
			for (int i = 0; i < linkidsArray.length; i++) {
				sql.append(linkidsArray[i]);
				if(i != linkidsArray.length-1){
					sql.append("','");
				}
			}
		}
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	/**
	 * 保存链路节点
	 * @param vo
	 * @return
	 */
	public boolean saveLink(Link vo){
		 int assistant = 0;
		   String temSql = "select * from topo_network_link where (start_id=" + vo.getStartId() 
		              + " and end_id=" + vo.getEndId() +")";
		   try
		   {
	          rs = conn.executeQuery(temSql);
	          if(rs!=null){
	        	return true;  
	          }
		   }
		   catch(Exception e){
			   e.printStackTrace();
		   }
		   
		   int id = getNextID();	   
		   StringBuffer sql = new StringBuffer(200);
		   sql.append("insert into topo_network_link(id,link_name,start_id,start_index,start_ip,start_descr,");
		   sql.append("end_id,end_index,end_ip,end_descr,assistant,type,findtype,linktype,max_speed,max_per)values(");
		   sql.append(id);
		   sql.append(",'");
		   sql.append(vo.getLinkName());
		   sql.append("',");
		   sql.append(vo.getStartId());
		   sql.append(",'");
		   sql.append(vo.getStartIndex());
		   sql.append("','");
		   sql.append(vo.getStartIp());
		   sql.append("','");
		   sql.append(vo.getStartDescr());
		   sql.append("',");
		   sql.append(vo.getEndId());
		   sql.append(",'");
		   sql.append(vo.getEndIndex());
		   sql.append("','");
		   sql.append(vo.getEndIp());
		   sql.append("','");
		   sql.append(vo.getEndDescr());
		   sql.append("',");	   
		   sql.append(assistant);
		   sql.append(",");
		   sql.append(vo.getType());
		   sql.append(",");
		   sql.append(vo.getFindtype());
		   sql.append(",");
		   sql.append(vo.getLinktype());
		   sql.append(",'");
		   sql.append(vo.getMaxSpeed());
		   sql.append("','");
		   sql.append(vo.getMaxPer());
		   sql.append("')");
		   SysLogger.info("########################");
		   SysLogger.info(sql.toString());
		   boolean flag = true;
		   try{
			   flag = saveOrUpdate(sql.toString());
		   }catch(Exception e){
			 e.printStackTrace();  
		   }
		   	  
		   return flag; 
	}
	
	public int getNextId() {
		return getNextID();
	}
	
	public String getLinkAliasName(int id) {
		String linkaliasName = "";
		String sql = "select link_alias_name from topo_network_link where id='" + id +"'";
		rs = conn.executeQuery(sql);
		try {
			while(rs.next()) {
				linkaliasName = rs.getString("link_alias_name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return linkaliasName;
	}
}
