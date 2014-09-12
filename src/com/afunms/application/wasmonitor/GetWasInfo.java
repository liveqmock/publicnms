package com.afunms.application.wasmonitor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysUtil;


public class GetWasInfo extends BaseDao implements DaoInterface{
	public GetWasInfo() {
		super("");
		// TODO Auto-generated constructor stub
	}

	public HashMap executeQueryHashMap(String ip,String tablesub) throws SQLException {
		HashMap hm = new HashMap();
		try {
			if (ip!=null) {						
//				String ip1 ="",ip2="",ip3="",ip4="";
//				String[] ipdot = ip.split(".");	
//				String tempStr = "";
//				String allipstr = "";
//				if (ip.indexOf(".")>0){
//					ip1=ip.substring(0,ip.indexOf("."));
//					ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//					tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//				}
//				ip2=tempStr.substring(0,tempStr.indexOf("."));
//				ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//				allipstr=ip1+ip2+ip3+ip4;
				String allipstr = SysUtil.doip(ip);
				String tablename = "";

				tablename = tablesub+allipstr;
			
			//SysLogger.info();
			 rs = conn.executeQuery("select * from "+tablename);
			// 取得列名,
			ResultSetMetaData rsmd = rs.getMetaData();
			int numCols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= numCols; i++) {
					String key = rsmd.getColumnName(i);
					String value = rs.getString(i);
					if (value == null) {
						value = "";
					}
					hm.put(key, value);
				}
			}
			}
		} catch (SQLException e) {
			//SysLogger.error();
			return null;

		} finally{
			if(rs != null){
				rs.close();
			}
		}
		return hm;
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
			
			public boolean update(BaseVo vo) {
				// TODO Auto-generated method stub
				return false;
			}
	

}
