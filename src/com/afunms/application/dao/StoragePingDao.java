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
import java.util.Date;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.polling.om.Pingcollectdata;


public class StoragePingDao extends BaseDao implements DaoInterface {

	public StoragePingDao() {
		super("storageping");
		// TODO Auto-generated constructor stub
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean save(Vector vector){
		boolean result = false;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			Date date = new Date();
			String time = simpleDateFormat.format(date);
			for(int i = 0 ; i < vector.size(); i++){
				Pingcollectdata pingdata = (Pingcollectdata)vector.get(i);
				String sql = "insert into " + "storageping"
				+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
				+ "values('" + pingdata.getIpaddress() + "','" + pingdata.getRestype() + "','" + pingdata.getCategory()
				+ "','" + pingdata.getEntity() + "','" + pingdata.getSubentity() + "','" + pingdata.getUnit() + "','"
				+ pingdata.getChname() + "','" + pingdata.getBak() + "'," + pingdata.getCount() + ",'"
				+ pingdata.getThevalue() + "','" + time + "')";
				System.out.println(sql);	
				conn.addBatch(sql);
			}
			conn.executeBatch();
			result = true;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
						
		return result;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String findAverageByTime(String ipaddress , String starttime , String endtime){
		
		String result = "0";
		
		try {
			String sql = "select avg(thevalue) from storageping where entity='Utilization' and ipaddress='"+ ipaddress + "' and collecttime between '" + starttime + "' and '" + endtime +"'";
			System.out.println(sql);
			rs = conn.executeQuery(sql);
			if(rs!=null&&rs.next()){
				result = rs.getString("avg(thevalue)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = "0";
		} 
		
		return result;
	}

	

}