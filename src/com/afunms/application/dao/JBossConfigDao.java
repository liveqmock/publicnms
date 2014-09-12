package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DnsConfig;
import com.afunms.application.model.JBossConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;

public class JBossConfigDao extends BaseDao implements DaoInterface{

	public JBossConfigDao() {
		super("nms_jbossconfig");
		// TODO Auto-generated constructor stub
	}

	 /**
	    * 
	    * 取出的数值放到vo里
	    */
		public BaseVo loadFromRS(ResultSet rs) {
			JBossConfig vo=new JBossConfig();
			
			try {
				vo.setId(rs.getInt("id"));
				vo.setAlias(rs.getString("alias"));
				vo.setUsername(rs.getString("username"));
				vo.setPassword(rs.getString("password"));
				vo.setIpaddress(rs.getString("ipaddress"));
				vo.setPort(rs.getInt("port"));
				vo.setFlag(rs.getInt("flag"));
				vo.setSendmobiles(rs.getString("sendmobiles"));
				vo.setSendemail(rs.getString("sendemail"));
				vo.setNetid(rs.getString("netid"));
				vo.setSendphone(rs.getString("sendphone"));
				
			} catch (SQLException e) {
				
				SysLogger.error("JBossConfigDao.loadFromRS()",e);
			}
			
			return vo;
		}
	    /**
	     * DNS
	     * 添加一条记录
	     */
		public boolean save(BaseVo vo) {
			JBossConfig vo1=(JBossConfig)vo;
			StringBuffer sql=new StringBuffer();
			sql.append("insert into nms_jbossconfig(id,alias,username,password,ipaddress,port,flag,sendmobiles,sendemail,sendphone,netid) values('");
			sql.append(vo1.getId());
            sql.append("','");
			sql.append(vo1.getAlias());
			sql.append("','");
			sql.append(vo1.getUsername());
			sql.append("','");
			sql.append(vo1.getPassword());
			sql.append("','");
			sql.append(vo1.getIpaddress());
			sql.append("',");
			sql.append(vo1.getPort());
			sql.append(",'");
			sql.append(vo1.getFlag());
			sql.append("','");
			sql.append(vo1.getSendmobiles());
			sql.append("','");
			sql.append(vo1.getSendemail());
			sql.append("','");
			sql.append(vo1.getSendphone());
			sql.append("','");
			sql.append(vo1.getNetid());
			sql.append("')");
		    System.out.println(sql);
			return saveOrUpdate(sql.toString());
			
		}  
		  /**
		   * DNS
		   * 根据id 查处所有
		   * @param bids
		   * @return
		   */
		   public List getJBossByBID(Vector bids){
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
			   sql.append("select * from nms_jbossconfig "+wstr);
			 
			   return findByCriteria(sql.toString());
		   }
		   /**
		    * DNS
		    * 根据flag查询一条记录
		    * @param flag
		    * @return
		    */
		   public List getJBossByFlag(int flag){
			   List rlist = new ArrayList();
			   StringBuffer sql = new StringBuffer();
		
			   sql.append("select * from nms_jbossconfig where flag = "+flag);
			  
			   return findByCriteria(sql.toString());
		   }
		   /**
		    * DNS
		    * 根据id 查询某条记录
		    * @param id
		    * @return
		    */
		   public List getJBossById(int id){
			   List rlist = new ArrayList();
			   StringBuffer sql = new StringBuffer();
		
			   sql.append("select * from nms_jbossconfig where id = "+id);
			   
			   return findByCriteria(sql.toString());
		   }
		/**
		 * DNS
		 * 修改一条记录
		 */
		public boolean update(BaseVo vo) {
			JBossConfig vo1=(JBossConfig)vo;
			StringBuffer sql=new StringBuffer();
		
			sql.append("update nms_jbossconfig set alias ='");
			sql.append(vo1.getAlias());
			sql.append("',username='");
			sql.append(vo1.getUsername());
			sql.append("',password='");
			sql.append(vo1.getPassword());
			sql.append("',ipaddress='");
			sql.append(vo1.getIpaddress());
			sql.append("',port=");
			sql.append(vo1.getPort());;
			sql.append(",flag='");
			sql.append(vo1.getFlag());
			sql.append("',sendmobiles='");
			sql.append(vo1.getSendmobiles());
			sql.append("',sendemail='");
			sql.append(vo1.getSendemail());
			sql.append("',sendphone='");
			sql.append(vo1.getSendphone());
			sql.append("',netid='");
			sql.append(vo1.getNetid());
			sql.append("' where id="+vo1.getId());
		    System.out.println(sql.toString());
			return saveOrUpdate(sql.toString());
			
		}
		
		public List<JBossConfig> getJBossConfigListByMonFlag(Integer flag){
			StringBuffer sql = new StringBuffer();
			sql.append("select * from nms_jbossconfig where flag= ");
			sql.append(flag);
			return findByCriteria(sql.toString());
		}

		/**
		 * 获取数据库中存储的采集的JBoss数据信息
		 * @param nodeid
		 * @return
		 * @throws SQLException
		 */
		public Hashtable<String, String> getJBossData(String nodeid) throws SQLException {
			Hashtable hm = new Hashtable();
			try {
				//SysLogger.info();
				StringBuffer sqlBuffer = new StringBuffer();
				sqlBuffer.append("select * from nms_jboss_temp where nodeid = '");
				sqlBuffer.append(nodeid);
				sqlBuffer.append("'");
				 rs = conn.executeQuery(sqlBuffer.toString());
				// 取得列名,
				while (rs.next()) {
					String entity = rs.getString("entity");
					String value = rs.getString("value");
					hm.put(entity, value);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
				if(rs != null){
					rs.close();
				}
			}
			return hm;
		}
		
		
		
		public Hashtable getPingDataById(String ip,Integer id,String starttime,String endtime) {
		   	   Hashtable hash = new Hashtable();
		   	   if (!starttime.equals("") && !endtime.equals("")) {
		   		   List list1 = new ArrayList();
		   		   String sql = "";
		   		   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
		   			   sql = "select a.thevalue,a.collecttime from jbossping"+id+" a where " +
		   			   	"(a.collecttime >= '"+starttime +"' and  a.collecttime <= '"+endtime+"') order by id";;
		   		   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
		   			   sql = "select a.thevalue from jbossping"+id+" a where " +
		   			   	" (a.collecttime >= "+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')"+" and  a.collecttime <= "+"to_date('"+endtime+"','YYYY-MM-DD HH24:MI:SS')"+") order by id";;
		   		   }
		   		   SysLogger.info("JBOSS-----------:"+sql);
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
