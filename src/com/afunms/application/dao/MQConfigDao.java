/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.MQConfig;
import com.afunms.application.model.Tomcat;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.*;
import com.afunms.mq.MqQueue;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.system.model.User;
import com.afunms.util.DataGate;

public class MQConfigDao extends BaseDao implements DaoInterface {

	public MQConfigDao() {
		super("nms_mqconfig");
	}
	public boolean delete(String []ids){
		boolean result = true;
		try{
		if(ids!=null && ids.length>0){
			for(int i=0;i<ids.length;i++){
				   try
				   {
					   MQConfig pvo = (MQConfig)findByID(ids[i]);
					   String ipstr = pvo.getIpaddress();
//						String ip1 ="",ip2="",ip3="",ip4="";
//						String[] ipdot = ipstr.split(".");	
//						String tempStr = "";
//						String allipstr = "";
//						if (ipstr.indexOf(".")>0){
//							ip1=ipstr.substring(0,ipstr.indexOf("."));
//							ip4=ipstr.substring(ipstr.lastIndexOf(".")+1,ipstr.length());			
//							tempStr = ipstr.substring(ipstr.indexOf(".")+1,ipstr.lastIndexOf("."));
//						}
//						ip2=tempStr.substring(0,tempStr.indexOf("."));
//						ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//						allipstr=ip1+ip2+ip3+ip4;
					    String allipstr = SysUtil.doip(ipstr);

						CreateTableManager ctable = new CreateTableManager();
			  			ctable.deleteTable(conn,"mqping",allipstr,"mqping");//Ping
			  			ctable.deleteTable(conn,"mqpinghour",allipstr,"mqpinghour");//Ping
			  			ctable.deleteTable(conn,"mqpingday",allipstr,"mqpingday");//Ping  
			  			conn.addBatch("delete from nms_mqconfig where id=" + ids[i]);
			  			conn.executeBatch();
			  			result = true;
				   }
				   catch(Exception e)
				   {
					   SysLogger.error("TomcatDao.delete()",e); 
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
	
	   public List getMQByFlag(int flag){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_mqconfig where mon_flag = "+flag);
		   return findByCriteria(sql.toString());
	   }
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		
		MQConfig vo=new MQConfig();
		
		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setManagername(rs.getString("managername"));
			vo.setPortnum(rs.getInt("portnum"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setMon_flag(rs.getInt("mon_flag"));
			vo.setNetid(rs.getString("netid"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setSendphone(rs.getString("sendphone"));
			vo.setSupperid(rs.getInt("supperid"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	
	return vo;
	}

	public boolean save(BaseVo vo) {
		boolean flag = true;
		MQConfig vo1=(MQConfig)vo;
		StringBuffer sql=new StringBuffer();
		// snow add supperid at 2010-5-20
		sql.append("insert into nms_mqconfig(id,name,ipaddress,managername,portnum,sendmobiles,mon_flag,netid,sendemail,sendphone,supperid) values(");
		sql.append(vo1.getId());
		sql.append(",'");
		sql.append(vo1.getName());
		sql.append("','");
		sql.append(vo1.getIpaddress());
		sql.append("','");
		sql.append(vo1.getManagername());
		sql.append("','");
		sql.append(vo1.getPortnum());
		sql.append("','");
		sql.append(vo1.getSendmobiles());
		sql.append("','");
		sql.append(vo1.getMon_flag());
		sql.append("','");
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
//			String ip1 ="",ip2="",ip3="",ip4="";
//			String[] ipdot = ip.split(".");	
//			String tempStr = "";
//			String allipstr = "";
//			if (ip.indexOf(".")>0){
//				ip1=ip.substring(0,ip.indexOf("."));
//				ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//				tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//			}
//			ip2=tempStr.substring(0,tempStr.indexOf("."));
//			ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//			allipstr=ip1+ip2+ip3+ip4;
			String allipstr = SysUtil.doip(ip);
			conn = new DBManager();
			ctable.createTable(conn,"mqping",allipstr,"mqping");//Ping
			ctable.createTable(conn,"mqpinghour",allipstr,"mqpinghour");//Ping
			ctable.createTable(conn,"mqpingday",allipstr,"mqpingday");//Ping 
		}catch(Exception e){
			e.printStackTrace();
			flag = false;
		}finally{
			try{
				conn.executeBatch();
			}catch(Exception e){
				e.printStackTrace();
			}
			conn.close();
		}
		return flag;
	}

	public boolean update(BaseVo vo) {
		boolean flag = true;
		MQConfig vo1=(MQConfig)vo;
		MQConfig pvo = (MQConfig)findByID(vo1.getId()+"");
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_mqconfig set name='");
		sql.append(vo1.getName());
		sql.append("',ipaddress='");
		sql.append(vo1.getIpaddress());
		sql.append("',managername='");
		sql.append(vo1.getManagername());
		sql.append("',portnum='");
		sql.append(vo1.getPortnum());
		sql.append("',sendmobiles='");
		sql.append(vo1.getSendmobiles());
		sql.append("',mon_flag='");
		sql.append(vo1.getMon_flag());
		sql.append("',netid='");
		sql.append(vo1.getNetid());
		sql.append("',sendemail='");
		sql.append(vo1.getSendemail());
		sql.append("',sendphone='");
		sql.append(vo1.getSendphone());
		sql.append("',supperid='");
		sql.append(vo1.getSupperid());// snow add supperid at 2010-5-20
		sql.append("' where id="+vo1.getId());
	  
			
    		saveOrUpdate(sql.toString());
		
		if (!vo1.getIpaddress().equals(pvo.getIpaddress())){
           	//修改了IP
				//若IP地址发生改变,先把表删除，然后在重新建立
				String ipstr = pvo.getIpaddress();
//				String ip1 ="",ip2="",ip3="",ip4="";
//				String[] ipdot = ipstr.split(".");	
//				String tempStr = "";
//				String allipstr = "";
//				if (ipstr.indexOf(".")>0){
//					ip1=ipstr.substring(0,ipstr.indexOf("."));
//					ip4=ipstr.substring(ipstr.lastIndexOf(".")+1,ipstr.length());			
//					tempStr = ipstr.substring(ipstr.indexOf(".")+1,ipstr.lastIndexOf("."));
//				}
//				ip2=tempStr.substring(0,tempStr.indexOf("."));
//				ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//				allipstr=ip1+ip2+ip3+ip4;
				String allipstr = SysUtil.doip(ipstr);
				  try {
				conn = new DBManager();
				CreateTableManager ctable = new CreateTableManager();
       			ctable.deleteTable(conn,"mqping",allipstr,"mqping");//Ping
       			ctable.deleteTable(conn,"mqpinghour",allipstr,"mqpinghour");//Ping
       			ctable.deleteTable(conn,"mqpingday",allipstr,"mqpingday");//Ping    
                 			               
           	
				//测试生成表
				String ip = vo1.getIpaddress();
//				ip1 ="";ip2="";ip3="";ip4="";
//				ipdot = ip.split(".");	
//				tempStr = "";
//				allipstr = "";
//				if (ip.indexOf(".")>0){
//					ip1=ip.substring(0,ip.indexOf("."));
//					ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//					tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//				}
//				ip2=tempStr.substring(0,tempStr.indexOf("."));
//				ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//				allipstr=ip1+ip2+ip3+ip4;
				allipstr = SysUtil.doip(ip);
				ctable = new CreateTableManager();
	    		ctable.createTable(conn,"mqping",allipstr,"mqping");//Ping
	    		ctable.createTable(conn,"mqpinghour",allipstr,"mqpinghour");//Ping
	    		ctable.createTable(conn,"mqpingday",allipstr,"mqpingday");//Ping
		} catch (Exception e) {
			flag=false;
		e.printStackTrace();
	}finally{
		try{
			conn.executeBatch();
		}catch(Exception e){
			e.printStackTrace();
		}
		conn.close();
	}
           }
		
	
	return flag;
	}
	
	   public List getMQByBID(Vector bids){
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
		   sql.append("select * from nms_mqconfig "+wstr);
		   //SysLogger.info(sql.toString());
		   return findByCriteria(sql.toString());
	   }
	 //处理Ping得到的数据，放到历史表里
		public synchronized boolean createHostData(Pingcollectdata pingdata) {
			if (pingdata == null )
				return false;	
			//DBManager dbmanager = new DBManager();
			try{			
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				Vector v = new Vector();
				//for (int i = 0; i < hostdatavec.size(); i++) {
					//Pingcollectdata pingdata = (Pingcollectdata)hostdatavec.elementAt(i);	
					String ip = pingdata.getIpaddress();				
					if (pingdata.getRestype().equals("dynamic")) {						
//						String ip1 ="",ip2="",ip3="",ip4="";
//						String[] ipdot = ip.split(".");	
//						String tempStr = "";
//						String allipstr = "";
//						if (ip.indexOf(".")>0){
//							ip1=ip.substring(0,ip.indexOf("."));
//							ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//							tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//						}
//						ip2=tempStr.substring(0,tempStr.indexOf("."));
//						ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//						allipstr=ip1+ip2+ip3+ip4;
						String allipstr = SysUtil.doip(ip);
						Calendar tempCal = (Calendar)pingdata.getCollecttime();							
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						String tablename = "";
						String type=pingdata.getCategory();
						tablename = "mqping"+allipstr;
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
		
		/**
		 * entity:mqValue local remote
		 * 从数据库中得到采集的mq数据信息
		 * @param nodeid
		 * @return
		 */
		public Hashtable getMQDataHashtable(String nodeid) {
			Hashtable rValue = new Hashtable();
			Vector mqValue = new Vector();
			List q_local_ParaValues = new ArrayList();
			List q_remote_ParaValues = new ArrayList();
			Connection conn = null;
			Statement stmt01 = null;
			Statement stmt02 = null;
			ResultSet rs01 = null;
			ResultSet rs02 = null; 
			try {
				conn = DataGate.getCon();
				stmt01 = conn.createStatement(); 
				stmt02 = conn.createStatement(); 
				//SysLogger.info();
				//取出mqValue的sindex
				StringBuffer sqlBuffer = new StringBuffer();
				sqlBuffer.append("select sindex from nms_mq_temp where nodeid='");
				sqlBuffer.append(nodeid);
				sqlBuffer.append("' and entity='mqValue' group by sindex");
				rs01 = stmt01.executeQuery(sqlBuffer.toString());
				if(rs01 != null){
					while (rs01.next()) {
						Hashtable cAttr = new Hashtable();//mqValue
						String sindex = "";
						sindex = rs01.getString("sindex");
						sqlBuffer = new StringBuffer();
						sqlBuffer.append("select subentity,value from nms_mq_temp where nodeid='");
						sqlBuffer.append(nodeid);
						sqlBuffer.append("' and entity='mqValue' and sindex='");
						sqlBuffer.append(sindex);
						sqlBuffer.append("'");
						rs02 = stmt02.executeQuery(sqlBuffer.toString());
						// 取得列名,
						while (rs02.next()) {
							String subentity = rs02.getString("subentity");
							String value = rs02.getString("value");
							cAttr.put(subentity, value);
						}
						rs02.close();
						mqValue.add(cAttr);
					}
				}
				rs01.close();
				//local本地队列 
				sqlBuffer = new StringBuffer();
				sqlBuffer.append("select sindex from nms_mq_temp where nodeid='");
				sqlBuffer.append(nodeid);
				sqlBuffer.append("' and entity='local' group by sindex");
				rs01 = stmt01.executeQuery(sqlBuffer.toString());
				if(rs01 != null){
					while (rs01.next()) { 
						MqQueue qAttr = new MqQueue();
						String sindex = "";
						sindex = rs01.getString("sindex");
						sqlBuffer = new StringBuffer();
						sqlBuffer.append("select subentity,value from nms_mq_temp where nodeid='");
						sqlBuffer.append(nodeid);
						sqlBuffer.append("' and entity='local' and sindex='");
						sqlBuffer.append(sindex);
						sqlBuffer.append("'");
						rs02 = stmt02.executeQuery(sqlBuffer.toString());
						// 取得列名,
						while (rs02.next()) {
							String subentity = rs02.getString("subentity");
							String value = rs02.getString("value");
							if("qname".equals(subentity)){
								qAttr.setQname(value);
							}else if("qtype".equals(subentity)){
								qAttr.setQtype(value);
							}else if("persistent".equals(subentity)){
								qAttr.setPersistent(value);
							}else if("usage".equals(subentity)){
								qAttr.setUsage(value);
							}else if("qdepth".equals(subentity)){
								qAttr.setQdepth(value);
							}else if("remoteQName".equals(subentity)){
								qAttr.setRemoteQName(value);
							}else if("remoteQM".equals(subentity)){
								qAttr.setRemoteQM(value);
							}else if("xmitQName".equals(subentity)){
								qAttr.setXmitQName(value);
							}
						}
						rs02.close();
						q_local_ParaValues.add(qAttr);
					}
				}
				rs01.close();
				//remote远程队列
				sqlBuffer = new StringBuffer();
				sqlBuffer.append("select sindex from nms_mq_temp where nodeid='");
				sqlBuffer.append(nodeid);
				sqlBuffer.append("' and entity='remote' group by sindex");
				rs01 = stmt01.executeQuery(sqlBuffer.toString());
				if(rs01 != null){
					while (rs01.next()) {
						MqQueue qAttr = new MqQueue();
						String sindex = "";
						sindex = rs01.getString("sindex");
						sqlBuffer = new StringBuffer();
						sqlBuffer.append("select subentity,value from nms_mq_temp where nodeid='");
						sqlBuffer.append(nodeid);
						sqlBuffer.append("' and entity='remote' and sindex='");
						sqlBuffer.append(sindex);
						sqlBuffer.append("'");
						rs02 = stmt02.executeQuery(sqlBuffer.toString());
						// 取得列名,
						while (rs02.next()) {
							String subentity = rs02.getString("subentity");
							String value = rs02.getString("value");
							if("qname".equals(subentity)){
								qAttr.setQname(value);
							}else if("qtype".equals(subentity)){
								qAttr.setQtype(value);
							}else if("persistent".equals(subentity)){
								qAttr.setPersistent(value);
							}else if("usage".equals(subentity)){
								qAttr.setUsage(value);
							}else if("qdepth".equals(subentity)){
								qAttr.setQdepth(value);
							}else if("remoteQName".equals(subentity)){
								qAttr.setRemoteQName(value);
							}else if("remoteQM".equals(subentity)){
								qAttr.setRemoteQM(value);
							}else if("xmitQName".equals(subentity)){
								qAttr.setXmitQName(value);
							}
						}
						rs02.close();
						q_remote_ParaValues.add(qAttr);
					}
				}
				rs01.close();
				rValue.put("mqValue", mqValue);
				rValue.put("remote", q_remote_ParaValues);
				rValue.put("local", q_local_ParaValues);
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				if(rs01 != null){
					try {
						rs01.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(rs02 != null){
					try {
						rs02.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				try {
					if(stmt01 != null){
						stmt01.close();
					}
					if(stmt02 != null){
						stmt02.close();
					}
					if(conn != null){
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return rValue;
		}
		
		
		
		
		public Hashtable getPingDataById(String ip,Integer id,String starttime,String endtime) {
		   	   Hashtable hash = new Hashtable();
		   	   if (!starttime.equals("") && !endtime.equals("")) {
		   		   List list1 = new ArrayList();
		   		   String sql = "";
		   		   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
		   			   sql = "select a.thevalue,a.collecttime from mqping"+ip+" a where " +
		   			   	"(a.collecttime >= '"+starttime +"' and  a.collecttime <= '"+endtime+"') order by id";;
		   		   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
		   			   sql = "select a.thevalue from mqping"+ip+" a where " +
		   			   	" (a.collecttime >= "+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')"+" and  a.collecttime <= "+"to_date('"+endtime+"','YYYY-MM-DD HH24:MI:SS')"+") order by id";;
		   		   }
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
		
		
		
		
		
		public String[] getAvailability(String newip,String starttime,String totime)throws Exception{
			String[] value={"",""};
			try {
				String sql = "";
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					String parm=" aa.COLLECTTIME >= '";
					parm=parm+starttime;
					parm=parm+"' and aa.COLLECTTIME <= '";
					parm=parm+totime;
					parm=parm+"'";
					sql = "select sum(aa.THEVALUE) as stype ,COUNT(aa.THEVALUE) as countid from mqping"+newip+" aa where  "+parm;
				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					String parm=" aa.COLLECTTIME >= ";
					parm=parm+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')";
					parm=parm+" and aa.COLLECTTIME <= ";
					parm=parm+"to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
					parm=parm+"";
					sql = "select sum(aa.THEVALUE) as stype ,COUNT(aa.THEVALUE) as countid from mqping"+newip+" aa where "+parm;
				}

				rs = conn.executeQuery(sql);
				while(rs.next()){
					value[0] = rs.getInt("stype")+"";
					value[1] = rs.getInt("countid")+"";
					value[1]=new Integer(new Integer(value[1]).intValue()-new Integer(value[0]).intValue()).toString();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return value;
			}   
}