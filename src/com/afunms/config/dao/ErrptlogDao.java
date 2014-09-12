package com.afunms.config.dao;

import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleResultSet;
import oracle.sql.CLOB;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Diskconfig;
import com.afunms.config.model.Errptlog;

public class ErrptlogDao extends BaseDao implements DaoInterface {

	public ErrptlogDao()
  	{
	  	super("nms_errptlog");	  
  	}
	
  	public List loadAll()
  	{
  		List list = new ArrayList(5);
  		try
  		{
  			rs = conn.executeQuery("select * from nms_errptlog order by id");
  			while(rs.next())
  				list.add(loadFromRS(rs)); 
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("ErrptlogDao:loadAll()",e);
  			list = null;
  		}
  		finally
  		{
  			conn.close();
  		}
  		return list;
  	}
	
//	public boolean save(BaseVo baseVo,String desc){
//		boolean flag = true;
//		Writer out = null;
//		Errptlog vo = (Errptlog)baseVo;
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		StringBuffer sql = new StringBuffer(100);
//		Calendar tempCal = (Calendar) vo.getCollettime();
//		Date cc = tempCal.getTime();
//		String time = sdf.format(cc);
//		int id = getNextID();
//			sql.append("insert into nms_errptlog(id,labels,identifier,collettime,seqnumber,nodeid,machineid,errptclass,errpttype,resourcename,resourceclass,resourcetype,locations,vpd,descriptions) values("+id+",'");
//			sql.append(vo.getLabels());
//			sql.append("','");
//			sql.append(vo.getIdentifier());
//			sql.append("',");
//			sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
//			sql.append(",");
//			sql.append(vo.getSeqnumber());
//			sql.append(",'");
//			sql.append(vo.getNodeid());
//			sql.append("','");
//			sql.append(vo.getMachineid());
//			sql.append("','");
//			sql.append(vo.getErrptclass());
//			sql.append("','");
//			sql.append(vo.getErrpttype());
//			sql.append("','");
//			sql.append(vo.getResourcename());
//			sql.append("','");
//			sql.append(vo.getResourceclass());
//			sql.append("','");
//			sql.append(vo.getRescourcetype());
//			sql.append("','");
//			sql.append(vo.getLocations());
//			sql.append("','");
//			sql.append(vo.getVpd());
//			sql.append("',empty_clob())");
//			oracle.jdbc.OracleConnection connection = null;
//			PreparedStatement pstmt = null;
//			PreparedStatement pstmt2 = null;
//			ResultSet rs2 = null;
//			try{
//				OracleJdbcUtil util = new OracleJdbcUtil("jdbc:oracle:thin:@localhost:1521:afunms", "root", "root");
//				connection = (OracleConnection) util.jdbc();
//				connection.setAutoCommit(false);
//				pstmt= connection.prepareStatement(sql.toString());
//				pstmt.executeUpdate();
//				/* 查询此clob对象并锁定 */ 
//				pstmt2= connection.prepareStatement("select descriptions from nms_errptlog where id="+id+" for update");
//				rs2 = pstmt2.executeQuery();
//				while (rs2.next()) { 
//					/* 取出此clob对象 */ 
//					CLOB clob = ((OracleResultSet)rs2).getCLOB(1); 
//					Writer writer = clob.getCharacterOutputStream();
//					writer.write(desc);
//					writer.flush();
//					writer.close();
//					/* 向clob对象中写入数据 */ 
//					/* 正式提交 */ 
//					connection.commit(); 
//					/* 恢复原提交状态 */ 
//				}
//			}catch(Exception e){
//				e.printStackTrace();
//			}finally{
//				try {
//					if(rs2!=null){
//						rs2.close();
//					}
//					if(pstmt!=null){
//						pstmt.close();
//					}
//					if(pstmt2!=null){
//						pstmt2.close();
//					}
//					if(connection!=null){
//						connection.close();
//					}
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//			return flag;
//	}
	public boolean save(BaseVo baseVo){
		boolean flag = true;
		Errptlog vo = (Errptlog)baseVo;
		String desc = vo.getDescriptions();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sql = new StringBuffer(100);
		Calendar tempCal = (Calendar) vo.getCollettime();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		//int id = getNextID();
			sql.append("insert into nms_errptlog(labels,identifier,collettime,seqnumber,nodeid,machineid,errptclass,errpttype,resourcename,resourceclass,resourcetype,locations,vpd,descriptions,hostid) values('");
			sql.append(vo.getLabels());
			sql.append("','");
			sql.append(vo.getIdentifier());
			sql.append("','");
			sql.append(time);
			sql.append("',");
			sql.append(vo.getSeqnumber());
			sql.append(",'");
			sql.append(vo.getNodeid());
			sql.append("','");
			sql.append(vo.getMachineid());
			sql.append("','");
			sql.append(vo.getErrptclass());
			sql.append("','");
			sql.append(vo.getErrpttype());
			sql.append("','");
			sql.append(vo.getResourcename());
			sql.append("','");
			sql.append(vo.getResourceclass());
			sql.append("','");
			sql.append(vo.getRescourcetype());
			sql.append("','");
			sql.append(vo.getLocations());
			sql.append("','");
			sql.append(vo.getVpd());
			sql.append("','");
			sql.append(vo.getDescriptions());
			sql.append("','");
			sql.append(vo.getHostid());
			sql.append("')");
			return saveOrUpdate(sql.toString());
			//return flag;
	}
	public boolean delete(String[] id)
	{
		boolean result = false;
	    try
	    {	    
	        for(int i=0;i<id.length;i++)
	        {
	            conn.addBatch("delete from nms_errptlog where id=" + id[i]);
	        }	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("ErrptlogDao.delete()",e);	        
	    }
	    finally
	    {
	         conn.close();
	    }
	    return result;
	}
	
	public boolean update(BaseVo baseVo)
	  {
		  Diskconfig vo = (Diskconfig)baseVo;
	     boolean result = false;
	     StringBuffer sql = new StringBuffer();
	     sql.append("update nms_errptlog set ipaddress='");
			sql.append(vo.getIpaddress());
			sql.append("',name='");
			sql.append(vo.getName());	
			sql.append("',diskindex=");
			sql.append(vo.getDiskindex());
			sql.append(",linkuse='");
			if(vo.getLinkuse() != null){
				sql.append(vo.getLinkuse());
			}else{
				sql.append("");
			}
			sql.append("',sms=");
			sql.append(vo.getSms());
			sql.append(",bak='");
			sql.append(vo.getBak());
			sql.append("',monflag=");
			sql.append(vo.getMonflag());
			sql.append(",reportflag=");
			sql.append(vo.getReportflag());
			sql.append(",sms1=");
			sql.append(vo.getSms1());
			sql.append(",sms2=");
			sql.append(vo.getSms2());
			sql.append(",sms3=");
			sql.append(vo.getSms3());
			sql.append(",limenvalue=");
			sql.append(vo.getLimenvalue());
			sql.append(",limenvalue1=");
			sql.append(vo.getLimenvalue1());
			sql.append(",limenvalue2=");
			sql.append(vo.getLimenvalue2());	
	     sql.append(" where id=");
	     sql.append(vo.getId());
	     
	     try
	     {
	    	 //SysLogger.info(sql.toString());
	         conn.executeUpdate(sql.toString());
	         result = true;
//	         Hashtable allDiskAlarm = (Hashtable)getByAlarmflag(new Integer(99));
//	 		ShareData.setAlldiskalarmdata(allDiskAlarm);
	     }
	     catch(Exception e)
	     {
	    	 result = false;
	         SysLogger.error("DiskconfigDao:update()",e);
	     }
	     finally
	     {
	    	 conn.close();
	     }     
	     return result;
	  }
//	public static void update(Nms_ErrptlogVO ne,int id){
//		try{
//			conn=DBconn.getConn();
//			sql="update nms_errptlog set labels = ?,identifier = ?,collettime = ?,seqnumber = ?,nodeid = ?,machineid = ?,class = ?,type = ?,resourcename = ?,resourceclass = ?,resourcetype = ?,locations = ?,vdp = ?,descriptions = ? where id = ? ";
//		    pmt=conn.prepareStatement(sql);
//		    pmt.setString(1, ne.getLabels());
//			pmt.setString(2, ne.getIdentifier());
//			pmt.setInt(3, ne.getCollettime());
//			pmt.setInt(4, ne.getSeqnumber());
//			pmt.setString(5, ne.getNodeid());
//			pmt.setString(6, ne.getMachineid());
//			pmt.setString(7, ne.getErrptclass());
//			pmt.setString(8, ne.getErrpttype());
//			pmt.setString(9, ne.getResourcename());
//			pmt.setString(10, ne.getResourceclass());
//			pmt.setString(11, ne.getRecourcetype());
//			pmt.setString(12, ne.getLocations());
//			pmt.setString(13, ne.getVdp());
//			pmt.setString(14, ne.getDescriptions());
//			pmt.setInt(15, ne.getId());
//			conn.commit();
//			DBconn.closeConn(conn, pmt, rs);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	public BaseVo loadFromRS(ResultSet rs){
		Errptlog vo = new Errptlog();
		try {
			vo.setId(rs.getInt("id"));
			vo.setLabels(rs.getString("labels"));
			vo.setIdentifier(rs.getString("identifier"));  
			Calendar cal = Calendar.getInstance();
			Date newdate = new Date();
			newdate.setTime(rs.getTimestamp("collettime").getTime());
			cal.setTime(newdate);
			vo.setCollettime(cal);
			vo.setSeqnumber(rs.getInt("seqnumber"));
			vo.setNodeid(rs.getString("nodeid"));
			vo.setMachineid(rs.getString("machineid"));
			vo.setErrptclass(rs.getString("errptclass"));
			vo.setErrpttype(rs.getString("errpttype"));
			vo.setResourcename(rs.getString("resourcename"));
			vo.setResourceclass(rs.getString("resourceclass"));
			vo.setRescourcetype(rs.getString("resourcetype"));
			vo.setLocations(rs.getString("locations"));
			vo.setVpd(rs.getString("vpd"));
			vo.setDescriptions(rs.getString("descriptions"));
			vo.setHostid(rs.getString("hostid"));
		} catch(Exception e){
			//SysLogger.error("PortconfigDao.loadFromRS()",e);
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}  

}
