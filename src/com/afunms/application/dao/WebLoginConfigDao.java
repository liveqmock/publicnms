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

import com.afunms.application.model.DominoConfig;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.webloginConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.*;
import com.afunms.system.model.User;

public class WebLoginConfigDao extends BaseDao implements DaoInterface {
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public WebLoginConfigDao() {
		super("nms_weblogin");
	}
	
	public BaseVo loadFromRS(ResultSet rs) {
		webloginConfig vo=new webloginConfig();
		try{
			vo.setId(rs.getInt("id"));
			vo.setAlias(rs.getString("alias"));
			vo.setUrl(rs.getString("url"));
			vo.setOutflag(rs.getInt("outflag"));
			vo.setOuturl(rs.getString("outurl"));
			vo.setName_flag(rs.getString("name_flag"));
			vo.setPassword_flag(rs.getString("password_flag"));
			vo.setCode_flag(rs.getString("code_flag"));
			vo.setUser_name(rs.getString("user_name"));
			vo.setUser_password(rs.getString("user_password"));
		    vo.setUser_code(rs.getString("user_code"));
		    vo.setTimeout(rs.getString("timeout"));
		    vo.setFlag(rs.getString("flag"));
		    vo.setKeyword(rs.getString("keyword"));
		    vo.setBid(rs.getString("bid"));
		    vo.setSupperid(rs.getInt("supperid"));
		    
		}catch(Exception e){
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo voo) {
		webloginConfig vo=(webloginConfig)voo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into nms_weblogin(id,alias,url,name_flag,user_name,password_flag,user_password,code_flag,user_code,timeout,flag,keyword,bid,supperid,outflag,outurl) values(");
		sql.append(vo.getId());
		sql.append(",'");
		sql.append(vo.getAlias());
		sql.append("','");
		sql.append(vo.getUrl());
		sql.append("','");
		sql.append(vo.getName_flag());
		sql.append("','");
		sql.append(vo.getUser_name());
		sql.append("','");
		sql.append(vo.getPassword_flag());
		sql.append("','");
		sql.append(vo.getUser_password());
		sql.append("','");
		sql.append(vo.getCode_flag());
		sql.append("','");
		sql.append(vo.getUser_code());
		sql.append("','");
		sql.append(vo.getTimeout());
		sql.append("','");
		sql.append(vo.getFlag());
		sql.append("','");
		sql.append(vo.getKeyword());
		sql.append("','");
		sql.append(vo.getBid());
		sql.append("',");
		sql.append(vo.getSupperid());
		sql.append(",");
		sql.append(vo.getOutflag());
		sql.append(",'");
		sql.append(vo.getOuturl());
		sql.append("')");
		System.out.println(sql.toString());
		return saveOrUpdate(sql.toString());
		
	}

	public boolean update(BaseVo vo) {		
		webloginConfig vo1=(webloginConfig)vo;
		StringBuffer sql=new StringBuffer();
		boolean result=false;
		sql.append("update nms_weblogin set alias='");
		sql.append(vo1.getAlias());
		sql.append("',url='");
		sql.append(vo1.getUrl());
		sql.append("',name_flag='");
		sql.append(vo1.getName_flag());
		sql.append("',user_name='");
		sql.append(vo1.getUser_name());
		sql.append("',password_flag='");
		sql.append(vo1.getPassword_flag());
		sql.append("',user_password='");
		sql.append(vo1.getUser_password());
		sql.append("',code_flag='");
		sql.append(vo1.getCode_flag());
		sql.append("',user_code='");
		sql.append(vo1.getUser_code());
		sql.append("',timeout='");
		sql.append(vo1.getTimeout());
		sql.append("',flag='");
		sql.append(vo1.getFlag());
		sql.append("',keyword='");
		sql.append(vo1.getKeyword());
		sql.append("',bid='");
		sql.append(vo1.getBid());
		sql.append("',supperid=");
		sql.append(vo1.getSupperid());
		sql.append(",outflag=");
		sql.append(vo1.getOutflag());
		sql.append(",outurl='");
		sql.append(vo1.getOuturl());
		sql.append("'  where id=");
		sql.append(vo1.getId());
		try {
			SysLogger.info(sql.toString());
			conn.executeUpdate(sql.toString());
			result=true;
			
		} catch (Exception e) {
			result=false;
			SysLogger.error("DominoConfigDao:update", e);
		}
		finally{
			conn.close();
		}
		return result;
	}

	public List<webloginConfig> getWebLoginConfigListByMonFlag(Integer flag){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_weblogin where flag= '");
		sql.append(flag);
		sql.append("'");
		return findByCriteria(sql.toString());
	}
	
	   public List getWebLoginByBID(Vector bids){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   String wstr = "";
		   if(bids != null && bids.size()>0){
			   for(int i=0;i<bids.size();i++){
				   if(wstr.trim().length()==0){
					   wstr = wstr+" where ( bid like '%,"+bids.get(i)+",%' "; 
				   }else{
					   wstr = wstr+" or bid like '%,"+bids.get(i)+",%' ";
				   }
				   
			   }
			   wstr=wstr+")";
		   }
		   sql.append("select * from nms_weblogin "+wstr);
//		   SysLogger.info(sql.toString());
		   return findByCriteria(sql.toString());
	   }
	   
	   
	   public List getWebLoginByID(int id){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_weblogin_ping where weblogin_id = "+id);
		   return findByCriteria(sql.toString());
	   }
	   
	   
	   
	   public Vector getByWebLoginId(Integer id,String starttime,String totime,Integer isconnected) throws Exception{
			List list = new ArrayList();
	    	Vector returnVal = new Vector();
	    	try{
	    		String sql = "";
	    		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){

	    			sql="select a.is_connected,a.is_response,a.mon_time from weblogin_ping"+id+" a where " +
	    			" (a.mon_time >= '"+starttime +"' and  a.mon_time <= '"+totime+"')";
	    		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
	    			sql="select a.is_connected,a.is_response,a.mon_time from weblogin_ping"+id+" a where " +
					"  (a.mon_time >= "+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')"+" and  a.mon_time <= "+"to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS'))";
	    		}
//				System.out.println(sql);
//				SysLogger.info(sql);
				rs = conn.executeQuery(sql);
				while(rs.next()){
					Object[] obj = new Object[3];
					obj[0] = rs.getString("is_connected");
					Hashtable ht = new Hashtable();
					/*
					if("0".equals(obj[0].toString())){
						obj[0] = "服务失败";
					}else{
						obj[0] = "服务成功";
					}
					*/
					obj[1] = rs.getString("is_response");	
					if(obj[1] == null) {
						obj[1] = "";
					}
					
					Calendar cal = Calendar.getInstance();
				       Date newdate = new Date();
				       newdate.setTime(rs.getTimestamp("mon_time").getTime());
				       cal.setTime(newdate);
				       obj[2] = sdf.format(cal.getTime());
					
					
					//Calendar c = (Calendar)obj[2];
					//obj[2] = sdf.format(c.getTime());
					ht.put("conn",obj[0]);
					ht.put("response",obj[1]);
					ht.put("mon_time",obj[2]);
					returnVal.addElement(ht);
					ht = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return returnVal;
		}
	   
	   
	   
	   
	   public String[] getAvailability(Integer weblogin_id,String starttime,String totime,String type)throws Exception{
			String[] value={"",""};
			try {
				String sql = "";
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					String parm=" aa.mon_time >= '";
					parm=parm+starttime;
					parm=parm+"' and aa.mon_time <= '";
					parm=parm+totime;
					parm=parm+"'";
					sql = "select sum(aa."+type+") as stype ,COUNT(*) as countid from weblogin_ping"+weblogin_id+" aa where  "+parm;
				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					String parm=" aa.mon_time >= ";
					parm=parm+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')";
					parm=parm+" and aa.mon_time <= ";
					parm=parm+"to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
					parm=parm+"";
					sql = "select sum(aa."+type+") as stype ,COUNT(*) as countid from eblogin_ping"+weblogin_id+" aa where  "+parm;

				}
//				System.out.println(sql);
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
