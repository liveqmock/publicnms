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

import com.afunms.application.model.PlotConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.*;
import com.afunms.system.model.User;

public class PlotConfigDao extends BaseDao implements DaoInterface {

	public PlotConfigDao() {
		super("nms_plotconfig");
	}
	public boolean delete(String []ids){
		return super.delete(ids);
	}
	
	   public List getGrapesByFlag(int flag){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_plotconfig where mon_flag = "+flag);
		   return findByCriteria(sql.toString());
	   }
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		
		PlotConfig vo=new PlotConfig();
		
		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setSupperdir(rs.getString("supperdir"));
			vo.setSubdir(rs.getString("subdir"));
			vo.setInter(rs.getString("inter"));
			vo.setFilesize(rs.getInt("filesize"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setMon_flag(rs.getInt("mon_flag"));
			vo.setNetid(rs.getString("netid"));
			vo.setSendemail(rs.getString("sendemail"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	
	return vo;
	}

	public boolean save(BaseVo vo) {
		PlotConfig vo1=(PlotConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into nms_plotconfig(name,ipaddress,supperdir,subdir,inter,filesize,sendmobiles,mon_flag,netid,sendemail) values('");
		sql.append(vo1.getName());
		sql.append("','");
		sql.append(vo1.getIpaddress());
		sql.append("','");
		sql.append(vo1.getSupperdir());
		sql.append("','");
		sql.append(vo1.getSubdir());
		sql.append("','");
		sql.append(vo1.getInter());
		sql.append("',");
		sql.append(vo1.getFilesize());
		sql.append(",'");
		sql.append(vo1.getSendmobiles());
		sql.append("','");
		sql.append(vo1.getMon_flag());
		sql.append("','");
		sql.append(vo1.getNetid());
		sql.append("','");
		sql.append(vo1.getSendemail());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		PlotConfig vo1=(PlotConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_plotconfig set name='");
		sql.append(vo1.getName());
		sql.append("',ipaddress='");
		sql.append(vo1.getIpaddress());
		sql.append("',supperdir='");
		sql.append(vo1.getSupperdir());
		sql.append("',subdir='");
		sql.append(vo1.getSubdir());
		sql.append("',inter='");
		sql.append(vo1.getInter());
		sql.append("',filesize=");
		sql.append(vo1.getFilesize());
		sql.append(",sendmobiles='");
		sql.append(vo1.getSendmobiles());
		sql.append("',mon_flag='");
		sql.append(vo1.getMon_flag());
		sql.append("',netid='");
		sql.append(vo1.getNetid());
		sql.append("',sendemail='");
		sql.append(vo1.getSendemail());
		sql.append("' where id="+vo1.getId());
		//SysLogger.info(sql.toString());
		return saveOrUpdate(sql.toString());
	}
	
	   public List getPlotByBID(Vector bids){
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
		   sql.append("select * from nms_plotconfig "+wstr);
		   //SysLogger.info(sql.toString());
		   return findByCriteria(sql.toString());
	   }
	   
	   public PlotConfig getPlotByIp(String ip){
		   PlotConfig config = null;
		   List list = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_plotconfig where ipaddress='"+ip+"'");
		   list = findByCriteria(sql.toString());
		   if(list != null && list.size()>0){
			   config = (PlotConfig)list.get(0);
		   }
		   //SysLogger.info(sql.toString());
		   return config;
	   }

}