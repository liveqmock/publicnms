package com.afunms.application.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.print.attribute.standard.Sides;

import com.afunms.application.model.oracleEntitynew;
import com.afunms.application.model.OracleEntity;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class OraclePartsDao extends BaseDao implements DaoInterface {

	public OraclePartsDao() {
		super("nms_oracle_sids");

	}
	
	public oracleEntitynew findByID1(String id) {
		oracleEntitynew vo = null;
	       try
		   {
			   rs = conn.executeQuery("select * from app_db_node where id=" + id); 
			   if(rs.next())
			       vo = dbnode(rs);
		   }    
		   catch(Exception ex)
		   {
			   //ex.printStackTrace();
			   SysLogger.error("BaseDao.findByID()",ex);
		   }finally{
			   if(rs != null){
				   try{
					   rs.close();
				   }catch(Exception e){
				   }
			   }
		   }
	       return vo;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		OracleEntity vo = null;
		try {
			if (rs != null ) {
				vo = new OracleEntity();
				try {
					vo.setDbid(rs.getInt("dbid"));
					vo.setId(rs.getInt("id"));
					vo.setPassword(rs.getString("password"));
					vo.setSid(rs.getString("sid"));
					vo.setUser(rs.getString("users"));
					vo.setGzerid(rs.getString("gzerid"));
					vo.setCollectType(rs.getInt("collecttype"));
					vo.setAlias(rs.getString("alias"));
					vo.setManaged(rs.getInt("managed"));
					vo.setBid(rs.getString("bid"));
				} catch (SQLException e) {
					//e.printStackTrace();
					vo = null;
				}
			}
		} catch (Exception e) {
			vo = null;
			//e.printStackTrace();
		}

		return vo;
	}

	public oracleEntitynew dbnode(ResultSet rs) {
		oracleEntitynew vo = null;
		try {
			if (rs != null ) {
				vo = new oracleEntitynew();
				try {
					vo.setId(rs.getInt("id"));
					vo.setAlias(rs.getString("alias"));
					vo.setIp_address(rs.getString("ip_address"));
					vo.setIp_long(rs.getInt("ip_long"));
					vo.setCategory(rs.getInt("category"));
					vo.setDb_name(rs.getString("db_name"));
					vo.setPort(rs.getString("port"));
					vo.setUsers(rs.getString("users"));
					vo.setPassword(rs.getString("password"));
					vo.setDbuse(rs.getString("dbuse"));
					vo.setSendmobiles(rs.getString("sendmobiles"));
					vo.setSendemail(rs.getString("sendemail"));
					vo.setManaged(rs.getInt("managed"));
					vo.setBid(rs.getString("bid"));
					vo.setDbtype(rs.getInt("dbtype"));
					vo.setSendphone(rs.getString("sendphone"));
					vo.setCollecttype(rs.getInt("collecttype"));
					vo.setSupperid(rs.getInt("supperid"));
				} catch (SQLException e) {
					//e.printStackTrace();
					vo = null;
				}
			}
		} catch (Exception e) {
			vo = null;
			//e.printStackTrace();
		}

		return vo;
	}
	public boolean save(BaseVo vo) {
		OracleEntity oracle = (OracleEntity) vo;
		StringBuilder sql = new StringBuilder(100);
		sql = sql
				.append("insert into nms_oracle_sids (id,dbid,sid,users,password,gzerid,collecttype,alias,managed,bid) values(");
		sql.append(oracle.getId());
		sql.append(",");
		sql.append(oracle.getDbid() + ",");
		sql.append("'");
		sql.append(oracle.getSid());
		sql.append("',");
		sql.append("'");
		sql.append(oracle.getUser());
		sql.append("',");
		sql.append("'");
		sql.append(oracle.getPassword());
		sql.append("',");
		sql.append("'");
		sql.append(oracle.getGzerid());
		sql.append("',");
		sql.append(oracle.getCollectType());
		sql.append(",'");
		sql.append(oracle.getAlias());
		sql.append("',");
		sql.append(oracle.getManaged());
		sql.append(",'");
		sql.append(oracle.getBid());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		OracleEntity oracle = (OracleEntity) vo;
		StringBuilder sql = new StringBuilder();
		sql.append("update nms_oracle_sids set dbid=");
		sql.append(oracle.getDbid());
		sql.append(",sid='");
		sql.append(oracle.getSid());
		sql.append("',users='");
		sql.append(oracle.getUser());
		sql.append("',password='");
		sql.append(oracle.getPassword());
		sql.append("',gzerid='");
		sql.append(oracle.getGzerid());
		sql.append("',collecttype=");
		sql.append(oracle.getCollectType());
		sql.append(",alias='");
		sql.append(oracle.getAlias());
		sql.append("',managed=");
		sql.append(oracle.getManaged());
		sql.append(",bid='");
		sql.append(oracle.getBid());
		sql.append("' where id=");
		sql.append(oracle.getId());
		boolean result = false;
		try {
			conn.executeUpdate(sql.toString());
			result = true;
		} catch (Exception e) {
			result = false;
			SysLogger.error("OraclePatrsDao:update()", e);
		}

		return result;
	}

	public boolean delete(String[] id) {
		boolean result = false;
		if (id != null && id.length > 0) {
			try {
				StringBuilder sql = new StringBuilder();
				sql.append("delete from nms_oracle_sids where id in(");
				for (int i = 0; i < id.length; i++) {
					if (i != 0)
						sql.append(",");
					sql.append(id[i]);
				}
				sql.append(")");
				conn.executeUpdate(sql.toString());
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
				SysLogger.error("OraclePatrsDao:delete()", e);
				result = false;
			}
		}
		return result;
	}

	public List findOracleParts(int dbid,int managed) {
		String sql = "select id,sid,users,password,gzerid,dbid,collecttype,alias,managed,bid from nms_oracle_sids where dbid="
				+ dbid+" and managed="+managed;
		//SysLogger.info(sql);
		return findByCriteria(sql);
	}
	
	public List findAll(){
		String sql = "select id,sid,users,password,gzerid,dbid,collecttype,alias,managed,bid from nms_oracle_sids";
    	return findByCriteria(sql);
	}
	public List findOracleParts(int dbid){
		String sql = "select id,sid,users,password,gzerid,dbid,collecttype,alias,managed,bid from nms_oracle_sids where dbid="
			+ dbid;
		//SysLogger.info(sql);
	return findByCriteria(sql);
	}

	public BaseVo getOracleById(int id) {
		StringBuilder sql = new StringBuilder();
		sql
				.append("select id,dbid,sid,users,password,gzerid,collecttype,alias,managed,bid from nms_oracle_sids where id=");
		sql.append(id);
		ResultSet rs = conn.executeQuery(sql.toString());

		try {
			if (rs.next()) {

				return loadFromRS(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs != null){
				try{
					rs.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}

		return null;

	}

	public BaseVo getOracleBySId(int dbid, String sid) {
		StringBuilder sql = new StringBuilder();
		sql
				.append("select id,users,password,gzerid,dbid,sid,collecttype,alias,managed,bid from nms_oracle_sids where dbid=");
		sql.append(dbid);
		sql.append(" and sid='");
		sql.append(sid + "'");
		ResultSet rs = conn.executeQuery(sql.toString());
		if (rs != null)
			try {
				rs.next();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		return loadFromRS(rs);
	}

	public boolean saveOracles(List<OracleEntity> list, int dbid) {
		if (list != null && list.size() > 0) {
			StringBuilder builder = new StringBuilder();
			builder
					.append("insert into nms_oracle_sids (id,dbid,sid,users,password,gzerid,collecttype,alias,managed,bid) values");
			int i = 0;
			for (OracleEntity oracle : list) {
				if (i > 0)
					builder.append(",");
				builder.append("(null,");
				builder.append(dbid);
				builder.append(",'");
				builder.append(oracle.getSid());
				builder.append("','");
				builder.append(oracle.getUser());
				builder.append("','");
				builder.append(oracle.getPassword());
				builder.append("','");
				builder.append(oracle.getGzerid());
				builder.append("',");
				builder.append(oracle.getCollectType());
				builder.append(",'");
				builder.append(oracle.getAlias());
				builder.append("',");
				builder.append(oracle.getManaged());
				builder.append(",'");
				builder.append(oracle.getSid());
				builder.append("')");
				i++;
			}
			conn.executeUpdate(builder.toString());
		}
		return true;
	}
	public List getOraclesByDbAndBid(int dbid,Vector bids){
		StringBuilder sql =new StringBuilder();
		sql.append("select id,sid,users,password,gzerid,dbid,collecttype,alias,managed,bid from nms_oracle_sids where dbid="
			+ dbid);
		boolean flag=true;
		if(bids==null)
			flag=false;
		if(flag&bids.size()==1){
			String s=(String)bids.get(0);
			if("-1".equals(s)){
				flag=false;
			}
		}
		if(flag){
			String wstr="";
			  if(bids != null && bids.size()>0){
				   for(int i=0;i<bids.size();i++){
					   if(wstr.trim().length()==0){
						   wstr = wstr+" and ( bid like '%,"+bids.get(i)+",%' "; 
					   }else{
						   wstr = wstr+" or bid like '%,"+bids.get(i)+",%' ";
					   }
					   
				   }
				   wstr=wstr+")";
			   }
			  sql.append(wstr);
		}
		
		 
	   return findByCriteria(sql.toString());
	}
	
	
	public List getDbSidByTypeMonFlag(int flag) {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_oracle_sids where managed= " + flag);
		//SysLogger.info(sql.toString());
		return findByCriteria(sql.toString());
	}
	
	/**
	 * 根据ID号（如app_db_node中的ID）得到sid
	 * @param id
	 * @return
	 */
	public int getOracleSidById(int id){
		int sid = -1;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select id from nms_oracle_sids where dbid='");
		sqlBuffer.append(id);
		sqlBuffer.append("'");
		ResultSet rs = null;
		try {
			rs = conn.executeQuery(sqlBuffer.toString());
			while(rs.next()){
				sid =  Integer.valueOf(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(rs != null){
					rs.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return sid;
	}
	
	
	public static void main(String []args){
		//System.out.println(.getName());
	}
}
