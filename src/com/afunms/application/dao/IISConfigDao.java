/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.model.HostApplyModel;
import com.afunms.application.model.IISConfig;
import com.afunms.application.model.IISVo;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ReflactUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.polling.om.Pingcollectdata;

public class IISConfigDao extends BaseDao implements DaoInterface {
	
	
	public IISConfigDao() {
		super("nms_iisconfig");
	}
	public boolean delete(String []ids){
		boolean result = true;
		try{
		if(ids!=null && ids.length>0){
			for(int i=0;i<ids.length;i++){
				   try
				   {
					   IISConfig pvo = (IISConfig)findByID(ids[i]);
					   String ipstr = pvo.getIpaddress();
					    String allipstr = SysUtil.doip(ipstr);
						conn = new DBManager();
						CreateTableManager ctable = new CreateTableManager();
			  			ctable.deleteTable(conn,"iisping",allipstr,"iisping");//Ping
			  			ctable.deleteTable(conn,"iispinghour",allipstr,"iispinghour");//Ping
			  			ctable.deleteTable(conn,"iispingday",allipstr,"iispingday");//Ping 
			  			ctable.deleteTable(conn,"iisconn",allipstr,"iisconn");//Ping 
		       			ctable.deleteTable(conn,"iiserr",allipstr,"iiserr");//Ping 
			  			conn.addBatch("delete from nms_iisconfig where id=" + ids[i]);
			  			conn.executeBatch();
			  			result = true;
				   }
				   catch(Exception e)
				   {
					   SysLogger.error("IISConfigDao.delete()",e); 
				   }
				   finally
				   {
					   //conn.close();
				   }
			}
		}
		}catch(Exception e){			
		}finally{
			conn.close();
		}
		return result;
	}
	
	@Override
	public BaseVo findByID(String id)
	  {
	     BaseVo vo = null;
	     try
	     {
	        rs = conn.executeQuery("select * from nms_iisconfig where id=" + id );
	        if(rs.next())
	           vo = loadFromRS(rs);
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("IISConfigDao.findByID()",e);
	         vo = null;
	     }
	     finally
	     {
	        conn.close();
	     }
	     return vo;
	  } 
	
	
	public BaseVo loadFromRS(ResultSet rs) {
		IISConfig vo=new IISConfig();
		
		
			try {
				vo.setId(rs.getInt("id"));
				vo.setName(rs.getString("name"));
				vo.setIpaddress(rs.getString("ipaddress"));
				vo.setCommunity(rs.getString("community"));
				vo.setSendmobiles(rs.getString("sendmobiles"));
				vo.setMon_flag(rs.getInt("mon_flag"));
				vo.setNetid(rs.getString("netid"));
				vo.setSendemail(rs.getString("sendemail"));
				vo.setSendphone(rs.getString("sendphone"));
				vo.setSupperid(rs.getInt("supperid"));// snow add supperid at 2010-5-20
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return vo;
	}

	public boolean save(BaseVo vo) {
		boolean flag = true;
		IISConfig vo1=(IISConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into nms_iisconfig(id,name,ipaddress,community,sendmobiles,mon_flag,netid,sendemail,sendphone,supperid) values(");
		sql.append(vo1.getId());
		sql.append(",'");
		sql.append(vo1.getName());
		sql.append("','");
		sql.append(vo1.getIpaddress());
		sql.append("','");
		sql.append(vo1.getCommunity());
		sql.append("','");
		sql.append(vo1.getSendmobiles());
		sql.append("',");
		sql.append(vo1.getMon_flag());
		sql.append(",'");
		sql.append(vo1.getNetid());
		sql.append("','");
		sql.append(vo1.getSendemail());
		sql.append("','");
		sql.append(vo1.getSendphone());
		sql.append("','");
		sql.append(vo1.getSupperid());
		sql.append("')");
		try{
			saveOrUpdate(sql.toString());
		   CreateTableManager ctable = new CreateTableManager();         
			//测试生成表
			String ip = vo1.getIpaddress();
			String allipstr = SysUtil.doip(ip);
			conn = new DBManager();
			ctable.createTable(conn,"iisping",allipstr,"iisping");//Ping
			ctable.createTable(conn,"iispinghour",allipstr,"iispinghour");//Ping
			ctable.createTable(conn,"iispingday",allipstr,"iispingday");//Ping 
			
			ctable.createTable(conn,"iisconn",allipstr,"iisconn");//Connection and users
			ctable.createTable(conn,"iiserr",allipstr,"iiserr");//totalNotFoundErrors
		}catch(Exception e){
			flag = false;
		}finally{
			try{
				conn.executeBatch();
			}catch(Exception e){
				
			}
			conn.close();
		}
		return flag;
		
	}

	public boolean update(BaseVo vo) {
		boolean flag = true;
		IISConfig vo1=(IISConfig)vo;
		IISConfig pvo = (IISConfig)findByID(vo1.getId()+"");
		boolean result=false;
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_iisconfig set name='");
		sql.append(vo1.getName());
		sql.append("',ipaddress='");
		sql.append(vo1.getIpaddress());
		sql.append("',community='");
		sql.append(vo1.getCommunity());
		sql.append("',sendmobiles='");
		sql.append(vo1.getSendmobiles());
		sql.append("',mon_flag=");
		sql.append(vo1.getMon_flag());
		sql.append(",netid='");
		sql.append(vo1.getNetid());
		sql.append("',sendemail='");
		sql.append(vo1.getSendemail());
		sql.append("',sendphone='");
		sql.append(vo1.getSendphone());
		sql.append("',supperid='");
		sql.append(vo1.getSupperid());
		sql.append("'where id=");
		sql.append(vo1.getId());
	  
	    	
    		saveOrUpdate(sql.toString());
		
		if (!vo1.getIpaddress().equals(pvo.getIpaddress())){
           	//修改了IP
			//更新nms_host_apply
			  try {
			
			HostApplyDao had = new HostApplyDao();
			try
			  {
				List<HostApplyModel> tmpList = had.findByNodeid(vo1.getId());
				had = new HostApplyDao();
				HostApplyModel ham = tmpList.get(0);
				ham.setIpaddres(vo1.getIpaddress());
    			had.update(ham);
			  }
			catch(Exception e)
			  {		
				  e.printStackTrace();
				  result = false;
				  SysLogger.error("BaseDao.saveOrUpdate()",e); 
		      }
			finally
			  {
				   if(had != null){
					   try{
						   had.close();
					   }catch(Exception e){
					   }
				   }
		      }
			
				//若IP地址发生改变,先把表删除，然后在重新建立
				String ipstr = pvo.getIpaddress();
			    String allipstr = SysUtil.doip(ipstr);
				conn = new DBManager();
				CreateTableManager ctable = new CreateTableManager();
       			ctable.deleteTable(conn,"iisping",allipstr,"iisping");//Ping
       			ctable.deleteTable(conn,"iispinghour",allipstr,"iispinghour");//Ping
       			ctable.deleteTable(conn,"iispingday",allipstr,"iispingday");//Ping    
                 	
       			ctable.deleteTable(conn,"iisconn",allipstr,"iisconn");//Ping 
       			ctable.deleteTable(conn,"iiserr",allipstr,"iiserr");//Ping 
           	
       		//删除原始ip的nms_iislogconfig数据
       			CreateTableManager createTableManager = new CreateTableManager();
       			String[] tablename = {"nms_iislogconfig"};
       			String[] ipaddress = new String[]{pvo.getIpaddress()};
			    createTableManager.clearTablesData(tablename,"ipaddress",ipaddress);
       			
       			//删除该数据库的采集指标
    			NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
    			try {
    				gatherdao.deleteByNodeIdAndTypeAndSubtype(vo1.getId()+"", "middleware", "iis");
    			} catch (RuntimeException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}finally{
    				gatherdao.close();
    			}
    			
    			//删除该数据库的告警阀值
    			AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
    			try {
    				indidao.deleteByNodeId(vo1.getId()+"", "middleware", "iis");
    			} catch (RuntimeException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}finally{
    				indidao.close();
    			}
    			
    			//初始化采集指标
    			try {
    				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
    				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo1.getId()+"", "middleware", "iis","1");
    			} catch (RuntimeException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
    			//初始化指标阀值
    			try {
    				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
    				alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo1.getId()), "middleware", "iis");
    			} catch (RuntimeException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}      			
       			
				//测试生成表
				String ip = vo1.getIpaddress();
			    allipstr = SysUtil.doip(ip);

				ctable = new CreateTableManager();
	    		ctable.createTable(conn,"iisping",allipstr,"iisping");//Ping
	    		ctable.createTable(conn,"iispinghour",allipstr,"iispinghour");//Ping
	    		ctable.createTable(conn,"iispingday",allipstr,"iispingday");//Ping
	    		
	    		ctable.createTable(conn,"iisconn",allipstr,"iisconn");//Connection and users
				ctable.createTable(conn,"iiserr",allipstr,"iiserr");//totalNotFoundErrors
			  } catch (Exception e) {
					flag=false;
					e.printStackTrace();
			    }finally{
					try{
						conn.executeBatch();
					}catch(Exception e){
						
					}
			    	conn.close();
			    }
           }
		
	   
	    return flag;
	}

	   public List getIISByBID(Vector bids){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   String wstr = "";
		   if(bids != null && bids.size()>0){
			   for(int i=0;i<bids.size();i++){
				   if(wstr.trim().length()==0){
					   wstr = wstr+" where ( netid like '%,"+bids.get(i)+",%' "; 
				   }else{
					   wstr = wstr+" or netid like '%,"+bids.get(i)+",%' ";
				   }
				   
			   }
			   wstr=wstr+")";
		   }
		   sql.append("select * from nms_iisconfig "+wstr);
		   //SysLogger.info(sql.toString());
		   return findByCriteria(sql.toString());
	   }
	   
	   public List getIISByFlag(int flag){
		   //List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_iisconfig where mon_flag = "+flag);
		   return findByCriteria(sql.toString());
	   }
	   
	 //处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(Pingcollectdata pingdata) {
		if (pingdata == null )
			return false;	
		//SysLogger.info(pingdata.getIpaddress()+"==============");
		try{			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			Vector v = new Vector();
				String ip = pingdata.getIpaddress();				
				if (pingdata.getRestype().equals("dynamic")) {						
					String allipstr = SysUtil.doip(ip);
					Calendar tempCal = (Calendar)pingdata.getCollecttime();							
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					String tablename = "";
					String type=pingdata.getCategory();
					tablename = "iisping"+allipstr;
					String sql="";
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						sql = "insert into "+tablename+"(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+"values('"+ip+"','"+pingdata.getRestype()+"','"+pingdata.getCategory()+"','"+pingdata.getEntity()+"','"
						+pingdata.getSubentity()+"','"+pingdata.getUnit()+"','"+pingdata.getChname()+"','"+pingdata.getBak()+"',"
						+pingdata.getCount()+",'"+pingdata.getThevalue()+"','"+time+"')";
					}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
						sql = "insert into "+tablename+"(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+"values('"+ip+"','"+pingdata.getRestype()+"','"+pingdata.getCategory()+"','"+pingdata.getEntity()+"','"
						+pingdata.getSubentity()+"','"+pingdata.getUnit()+"','"+pingdata.getChname()+"','"+pingdata.getBak()+"',"
						+pingdata.getCount()+",'"+pingdata.getThevalue()+"',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
					}				
					
					//System.out.println("IISComfigDao.java-------357--------->"+sql);
					conn.executeUpdate(sql);
																									
				}				
				
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
			
		}
		return true;
	}
	
	//通过ip寻找记录
	public List getAllByIp(BaseVo vo) 
	{
		IISConfig _vo = (IISConfig)vo;
		List list = new ArrayList();
		IISConfig iisc;
		String string = "select * from nms_iisconfig where ipaddress =";
		string+="'"+_vo.getIpaddress()+"' or name = '"+_vo.getName()+"'";
		ResultSet rSet = null;
		rSet = conn.executeQuery(string);
		 try {
			while(rSet.next())
			 {
				iisc = new IISConfig();
				iisc.setIpaddress(rSet.getString("ipaddress"));
				iisc.setName(rSet.getString("name"));
				iisc.setId(rSet.getInt("id"));
				list.add(iisc);
			 }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(rSet != null){
				try{
					rSet.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			conn.close();
		}
		return list;
	}
	
	//zhushouzhi-----------------
   public int getidByIp(String ip) 
	{
		String string = "select id from nms_iisconfig where ipaddress ="+"'"+ip+"'";
		int id = 0;
		ResultSet rSet = null;
		rSet = conn.executeQuery(string);
		 try {
			while(rSet.next())
			 {
				 id = rSet.getInt(1);
			 }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(rSet != null){
				try{
					rSet.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			conn.close();
		}
		return id;
	}
	   
   /**
	 * list  IIS
	 * @return
	 */
	
	 public List getAllIIS(){
		   List rlist = new ArrayList();
		   IISConfig vo=null;
		   DBManager dao = new DBManager();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_iisconfig");
		   ResultSet rs=dao.executeQuery(sql.toString());
		   try {
			while(rs.next())
			   {
				vo = new IISConfig();
				vo.setId(rs.getInt("id"));
				vo.setName(rs.getString("name"));
				vo.setIpaddress(rs.getString("ipaddress"));
				vo.setCommunity(rs.getString("community"));
				vo.setSendmobiles(rs.getString("sendmobiles"));
				vo.setMon_flag(rs.getInt("mon_flag"));
				vo.setNetid(rs.getString("netid"));
				vo.setSendemail(rs.getString("sendemail"));
				vo.setSendphone(rs.getString("sendphone"));
				   rlist.add(vo);
			   }
			//System.out.println("rlist="+rlist.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs != null){
				try{
					rs.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			dao.close();
		}
		   return rlist;
	   }

	 
	 /**
	  * 从数据库中得到采集的IIS的数据信息
	  * @param nodeid
	  * @return
	 * @throws Exception 
	  */
   	public List<IISVo> getIISData(String nodeid) throws Exception{
		 List<IISVo> iisVoList = new ArrayList<IISVo>();
   		try {
			//SysLogger.info();
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select * from nms_iis_temp where nodeid = '");
			sqlBuffer.append(nodeid);
			sqlBuffer.append("'");
			 rs = conn.executeQuery(sqlBuffer.toString());
			// 取得列名,
			 IISVo iisVo = new IISVo();
			while (rs.next()) {
				String entity = rs.getString("entity");
				String value = rs.getString("value");
				ReflactUtil.invokeSet(iisVo, entity, value);
			}
			iisVoList.add(iisVo);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs != null){
				rs.close();
			}
		}
   		return iisVoList;
   	}
   	
   	/**
   	 * 得到IIS服务器连通率
   	 * @param ip
   	 * @return
   	 * @throws Exception
   	 */
   	public String getPingvalue(String ip) throws Exception{
   		String pingvalue = "0";
   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
   		Date d = new Date();
   		String date = sdf.format(d);
		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";
		
   		StringBuffer sqlBuffer = new StringBuffer();
   		String allipstr = SysUtil.doip(ip);
   		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
   			sqlBuffer.append("select thevalue from iisping");
   	   		sqlBuffer.append(allipstr);
   	   		sqlBuffer.append(" where collecttime >= '");
   	   		sqlBuffer.append(starttime);
   	   		sqlBuffer.append("' and collecttime <= '");
   	   		sqlBuffer.append(totime);
   	   		sqlBuffer.append("' order by collecttime desc ");
   		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
   			sqlBuffer.append("select thevalue from iisping");
   	   		sqlBuffer.append(allipstr);
   	   		sqlBuffer.append(" where collecttime >= ");
   	   		sqlBuffer.append("to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')");
   	   		sqlBuffer.append(" and collecttime <= ");
   	   		sqlBuffer.append("to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')");
   	   		sqlBuffer.append(" order by collecttime desc ");
   		}
   		
   		try {
			rs = conn.executeQuery(sqlBuffer.toString());
			if (rs.next()) {
				pingvalue = rs.getString("thevalue");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rs.close();
		}
   		return pingvalue;
   	}
   	
   	public Hashtable getPingDataById(String ip,Integer id,String starttime,String endtime) {
	   	   Hashtable hash = new Hashtable();
	   	   if (!starttime.equals("") && !endtime.equals("")) {
	   		   List list1 = new ArrayList();
	   		   String sql = "";
	   		   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
	   			   sql = "select a.thevalue,a.collecttime from iisping"+ip.replace(".", "_")+" a where " +
	   			   	"(a.collecttime >= '"+starttime +"' and  a.collecttime <= '"+endtime+"') order by id";;
	   		   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
	   			   sql = "select a.thevalue from iisping"+ip.replace(".", "_")+" a where " +
	   			   	" (a.collecttime >= "+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')"+" and  a.collecttime <= "+"to_date('"+endtime+"','YYYY-MM-DD HH24:MI:SS')"+") order by id";;
	   		   }
//	   		   SysLogger.info(sql);
	   		   int i = 0;
	   		   double curPing=0;
	   		   double avgPing = 0;
	   		  double  minPing=0;
	   		   rs = conn.executeQuery(sql);
	   		   try {
	   			   while (rs.next()) {
	   				   i = i + 1;
	   				   Vector v = new Vector();
	   				   String thevalue = rs.getString("thevalue");
	   				   String collecttime = rs.getString("collecttime");
	   				  // String reason = rs.getString("reason");
	   				   thevalue=String.valueOf(Integer.parseInt(thevalue));
	   				   v.add(0, thevalue);
	   				   v.add(1, collecttime);
	   				   v.add(2, "%");
	   				   avgPing = avgPing + Float.parseFloat(thevalue);
	   				   curPing=Float.parseFloat(thevalue);
	   				   if (curPing<minPing)
	   					 minPing=curPing;
	   				    list1.add(v);
	   			   }
	   			   
	   		   } catch (SQLException e) {
	   			   e.printStackTrace();
	   		   } finally {
	   			   try {
	   				   if (rs!=null)
	   				   rs.close();
	   				   if (conn!=null)
	   					conn.close();
	   				
	   			   } catch (SQLException e) {
	   				   e.printStackTrace();
	   			   }
	   		   }
	   		   hash.put("list", list1);
	   		   if (list1 != null && list1.size() > 0) {
	   			   hash.put("avgPing", CEIString.round(avgPing/ list1.size(), 2)+"");
	   		   } else {
	   			   hash.put("avgPing", "0");
	   		   }
	   		   hash.put("minPing", minPing+"");
	   		   hash.put("curPing", curPing+"");
	   	   }
	   	   return hash;
	      }
   	
   	
   	
}
