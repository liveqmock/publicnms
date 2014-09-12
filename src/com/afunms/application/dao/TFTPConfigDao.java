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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DominoConfig;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.TFTPConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.*;
import com.afunms.system.model.User;

/**
 * 
 * @author GANYI
 * @since 2012-04-23 10:30:00
 */
public class TFTPConfigDao extends BaseDao implements DaoInterface {
	
	
	public TFTPConfigDao() {
		super("nms_tftpmonitorconfig");
	}
	
	public BaseVo loadFromRS(ResultSet rs) {
		TFTPConfig vo=new TFTPConfig();
			
		vo = createTFTPConfig(rs);
		
		return vo;
	}

	public boolean save(BaseVo vo) {
		TFTPConfig vo1=(TFTPConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into nms_tftpmonitorconfig(id,name,username,password,timeout,monflag,ipaddress,filename,bid,sendmobiles,sendemail,sendphone,supperid) values('");
		sql.append(vo1.getId());
		sql.append("','");
		sql.append(vo1.getName());
		sql.append("','");
		sql.append(vo1.getUsername());
		sql.append("','");
		sql.append(vo1.getPassword());
		sql.append("','");
		sql.append(vo1.getTimeout());
		sql.append("','");
		sql.append(vo1.getMonflag());
		sql.append("','");
		sql.append(vo1.getIpaddress());
		sql.append("','");
		sql.append(vo1.getFilename());
		sql.append("','");
		sql.append(vo1.getBid());
		sql.append("','");
		sql.append(vo1.getSendmobiles());
		sql.append("','");
		sql.append(vo1.getSendemail());
		sql.append("','");
		sql.append(vo1.getSendphone());
		sql.append("','");
		sql.append(vo1.getSupperid());//snow add supperid at 2010-5-21
		sql.append("')");
		return saveOrUpdate(sql.toString());
		
	}

	public boolean update(BaseVo vo) {		
		TFTPConfig vo1=(TFTPConfig)vo;
		StringBuffer sql=new StringBuffer();
		boolean result=false;
		sql.append("update nms_tftpmonitorconfig set name='");
		sql.append(vo1.getName());
		sql.append("',username='");
		sql.append(vo1.getUsername());
		sql.append("',password='");
		sql.append(vo1.getPassword());
		sql.append("',timeout='");
		sql.append(vo1.getTimeout());
		sql.append("',monflag=");
		sql.append(vo1.getMonflag());
		sql.append(",ipaddress='");
		sql.append(vo1.getIpaddress());
		sql.append("',filename='");
		sql.append(vo1.getFilename());
		sql.append("',bid='");
		sql.append(vo1.getBid());
		sql.append("',sendmobiles='");
		sql.append(vo1.getSendmobiles());
		sql.append("',sendemail='");
		sql.append(vo1.getSendemail());
		sql.append("',sendphone='");
		sql.append(vo1.getSendphone());
		sql.append("',supperid='");
		sql.append(vo1.getSupperid());//snow add supperid at 2010-5-21
		sql.append("'where id=");
		sql.append(vo1.getId());
		try {
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

	public List<TFTPConfig> getTFTPConfigListByMonFlag(Integer flag){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_tftpmonitorconfig where monflag= ");
		sql.append(flag);
		return findByCriteria(sql.toString());
	}
	
	   public List getTftpByBID(Vector bids){
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
		   sql.append("select * from nms_tftpmonitorconfig "+wstr);
//		   SysLogger.info(sql.toString());
		   return findByCriteria(sql.toString());
	   }
	   
	   public TFTPConfig createTFTPConfig(ResultSet rs){
		   TFTPConfig vo = new TFTPConfig();
			try {
				vo.setId(rs.getInt("id"));
				vo.setName(rs.getString("name"));
				vo.setUsername(rs.getString("username"));
				vo.setPassword(rs.getString("password"));				
				vo.setTimeout(rs.getInt("timeout"));
				vo.setMonflag(rs.getInt("monflag"));
				vo.setIpaddress(rs.getString("ipaddress"));
				vo.setFilename(rs.getString("filename"));
				vo.setBid(rs.getString("bid"));
				vo.setSendmobiles(rs.getString("sendmobiles"));
				vo.setSendemail(rs.getString("sendemail"));
				vo.setSendphone(rs.getString("sendphone"));
				vo.setSupperid(rs.getInt("supperid"));//snow add supperid at 2010-5-21
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return vo;
	   }
}
