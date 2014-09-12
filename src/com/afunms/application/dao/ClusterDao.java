package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.afunms.application.model.Cluster;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;

public class ClusterDao extends BaseDao implements DaoInterface{
    public ClusterDao(){
    	super("nms_remote_up_down_cluster");
    }
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Cluster cluster=new Cluster();
		try {
			cluster.setId(rs.getInt("id"));
			cluster.setName(rs.getString("name"));
			cluster.setServerType(rs.getString("serverType"));
			cluster.setBid(rs.getString("bid"));
			cluster.setCreatetime(rs.getTimestamp("createtime"));
			cluster.setXml(rs.getString("xml"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return cluster;
	}
	
	public BaseVo findByXml(String xmlname)
    {
	    BaseVo vo = null;
        try
	    {
		    rs = conn.executeQuery("select * from nms_remote_up_down_cluster where xml='" + xmlname + "'"); 
		    if(rs.next())
		        vo = loadFromRS(rs);
	    }    
	    catch(Exception ex)
	    {
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

	public boolean save(BaseVo vo) {
		Cluster cluster=(Cluster)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into nms_remote_up_down_cluster(name,serverType,xml,bid,createtime) values('");
		sql.append(cluster.getName());
		sql.append("','");
		sql.append(cluster.getServerType());
		sql.append("','");
		sql.append(cluster.getXml());
		sql.append("','");
		sql.append(cluster.getBid());
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			sql.append("','");
			sql.append(cluster.getCreatetime());	
			sql.append("')");
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			Date cc = cluster.getCreatetime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String recordtime = sdf.format(cc);
			sql.append("',");
			sql.append("to_date('"+recordtime+"','YYYY-MM-DD HH24:MI:SS')");	
			sql.append(")");
		}
		

		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		Cluster cluster = (Cluster)vo;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_remote_up_down_cluster set name='");
		sql.append(cluster.getName());
		
		sql.append("',serverType='");
		sql.append(cluster.getServerType());
		sql.append("',bid='");
		sql.append(cluster.getBid());
		sql.append("',createtime=");
		sql.append("to_date('"+sdf.format(cluster.getCreatetime())+"','YYYY-MM-DD HH24:MI:SS')");
		sql.append(" where id=");
		sql.append(cluster.getId());
		System.out.println(sql.toString());
		return this.saveOrUpdate(sql.toString());
	}

}
