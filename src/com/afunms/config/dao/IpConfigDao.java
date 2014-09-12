package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.IpConfig;

public class IpConfigDao extends BaseDao implements DaoInterface{
	
	public IpConfigDao() {
		super("nms_ipconfig");
		// TODO Auto-generated constructor stub
	}	
	
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		IpConfig vo = new IpConfig();
	      try
	      {
	         vo.setId(rs.getInt("id"));
	         vo.setIpaddress(rs.getString("ipaddress"));
	         vo.setDeptid(rs.getInt("deptid"));
	         vo.setEmployeeid(rs.getInt("employeeid"));
	         vo.setIpdesc(rs.getString("ipdesc"));
	         vo.setDiscrictid(rs.getInt("discrictid"));
	        
	      }
	      catch(Exception ex)
	      {
	          SysLogger.error("Error in UserDAO.loadFromRS()",ex);
	          vo = null;
	      }
	      return vo;
	}

	
	/**
	 * 添加记录
	 */
	public boolean save(BaseVo baseVo) {
		// TODO Auto-generated method stub
		
		IpConfig vo = (IpConfig)baseVo;
		//Calendar tempCal = (Calendar)vo.getCollecttime();							
		//Date cc = tempCal.getTime();
		//String recordtime = sdf.format(cc);
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_ipconfig(ipaddress,employeeid,discrictid,deptid,ipdesc)values('");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getEmployeeid());
		sql.append("','");
		sql.append(vo.getDiscrictid());
		sql.append("','");
		sql.append(vo.getDeptid());
		sql.append("','");
		sql.append(vo.getIpdesc());	
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	

	/**
	 * 修改Ip记录
	 */
	public boolean update(BaseVo baseVo) {
		// TODO Auto-generated method stub
		
		IpConfig vo = (IpConfig)baseVo;

		   StringBuffer sql = new StringBuffer(200);
	       sql.append("update nms_ipconfig set ipaddress='");
	       sql.append(vo.getIpaddress());
	       sql.append("',employeeid='");
	       sql.append(vo.getEmployeeid());
	       sql.append("',discrictid='");
	       sql.append(vo.getDiscrictid());
	       sql.append("',deptid='");
	       sql.append(vo.getDeptid());
	       sql.append("',ipdesc='");
	       sql.append(vo.getIpdesc());         
		   sql.append("' where id=");
	       sql.append(vo.getId());
	       return saveOrUpdate(sql.toString());
	}
	
	
	
	
	
	
	/**
	  * 根据id删除这条记录
	  * @param id
	  * @return
	  */
	  public boolean delete(String id)
	   {
		   boolean result = false;
		   try
		   {
			   conn.addBatch("delete from nms_ipconfig where id=" + id);
			   //System.out.println("delete from nms_contract where id=" + id);
			   conn.executeBatch();
			   result = true;
		   }
		   catch(Exception e)
		   {
			   SysLogger.error("IpconfigDao.delete()",e); 
		   }
		   finally
		   {
			   conn.close();
		   }
		   return result;
	   }
	  
	  /**
		  * 根据id删除这条记录
		  * @param id
		  * @return
		  */
		  public boolean deleteByDistrictId(String districtId)
		   {
			   boolean result = false;
			   try
			   {
				   conn.addBatch("delete from nms_ipconfig where discrictid='" + districtId+"'");
				   //System.out.println("delete from nms_contract where id=" + id);
				   conn.executeBatch();
				   result = true;
			   }
			   catch(Exception e)
			   {
				   SysLogger.error("IpConfigDao.delete()",e); 
			   }
			   finally
			   {
				   conn.close();
			   }
			   return result;
		   }
	  
	  
//		  /**
//		  * 
//		  * @param id
//		  * @return
//		  * @modify nielin
//		  */
//		public List findByMac(String mac){
//			String sql = "select * from nms_macconfig where mac='" + mac + "'";
//			return findByCriteria(sql);
//		}
		
		   /**
		    * 按Ip找一条记录
		    */
		   public List findByIp(String ip)
		   {
			   List list = new ArrayList();
		       try
			   {
				   rs = conn.executeQuery("select * from nms_ipconfig where ipaddress='" + ip + "'"); 
				   
				   while(rs.next())
					   list.add(loadFromRS(rs));
			   }    
			   catch(Exception ex)
			   {
				   //ex.printStackTrace();
				   SysLogger.error("BaseDao.findByIp()",ex);
			   }finally{
				   if(rs != null){
					   try{
						   rs.close();
					   }catch(Exception e){
					   }
				   }
			   }
		       return list;
		   }
		
		/**
		  * 
		  * @param id
		  * @return
		  * @modify nielin
		  */
		public boolean deleteAll(){
			String sql = "delete from nms_ipconfig";
			return saveOrUpdate(sql);
		}
		
		/**
		 * 
		 * @param mac
		 * @return
		 * @add nielin
		 */
		public boolean saveBatch(List list){
			boolean result = false;
			if(list!=null && list.size()>0){
				try {
					for(int i = 0 ; i < list.size(); i++){
						IpConfig vo = (IpConfig)list.get(i);
						StringBuffer sql = new StringBuffer(100);
						sql.append("insert into nms_ipconfig(ipaddress,employeeid,discrictid,deptid,ipdesc)values('");
						sql.append(vo.getIpaddress());
						sql.append("','");
						sql.append(vo.getEmployeeid());
						sql.append("','");
						sql.append(vo.getDiscrictid());
						sql.append("','");
						sql.append(vo.getDeptid());
						sql.append("','");
						sql.append(vo.getIpdesc());	
						sql.append("')");
						conn.addBatch(sql.toString());
					}
					conn.executeBatch();
					result = true;
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					result = false;
				}
			}
			
			return result;
		}
	
}
