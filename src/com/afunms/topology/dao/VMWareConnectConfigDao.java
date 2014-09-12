package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.VMWareConnectConfig;

public class VMWareConnectConfigDao extends BaseDao implements DaoInterface {
	
	public VMWareConnectConfigDao() {
		super("nms_vmwareconnectconfig");
		// TODO Auto-generated constructor stub
    }

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		VMWareConnectConfig vmware = new VMWareConnectConfig();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			vmware.setId(rs.getLong("id"));
			vmware.setNodeid(rs.getLong("nodeid"));
			vmware.setUsername(rs.getString("username"));
			vmware.setPwd(rs.getString("pwd"));		
			vmware.setHosturl(rs.getString("hosturl"));	
			vmware.setBak(rs.getString("bak"));	
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return vmware;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		VMWareConnectConfig vmware = (VMWareConnectConfig) vo;
		StringBuffer addsql = new StringBuffer(200);
		addsql.append("insert into nms_vmwareconnectconfig(nodeid,username,pwd,hosturl,bak)values(");
		addsql.append(vmware.getNodeid());
		addsql.append(",'");
		addsql.append(vmware.getUsername());
		addsql.append("','");
		addsql.append(vmware.getPwd());
		addsql.append("','");
		addsql.append(vmware.getHosturl());
		addsql.append("','");
		addsql.append(vmware.getBak());
		addsql.append("')");
		return saveOrUpdate(addsql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		VMWareConnectConfig vmware = (VMWareConnectConfig) vo;
		StringBuffer updatesql = new StringBuffer(200);
		updatesql.append("update nms_vmwareconnectconfig set nodeid=");
		updatesql.append(vmware.getNodeid());
		updatesql.append(",username='");
		updatesql.append(vmware.getUsername());
		updatesql.append("',pwd='");
		updatesql.append(vmware.getPwd());
		updatesql.append("',hosturl='");
		updatesql.append(vmware.getHosturl());
		updatesql.append("',bak='");
		updatesql.append(vmware.getBak());
		updatesql.append("' where id=");
		updatesql.append(vmware.getId());
		return saveOrUpdate(updatesql.toString());
	}
	
	public boolean delete(BaseVo vo)
	{
		VMWareConnectConfig vmware = (VMWareConnectConfig) vo;
		boolean b = false;
		String delsql = "delete from nms_vmwareconnectconfig where id=" + vmware.getId();
		try {
			conn.executeUpdate(delsql);
			b = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return b;
	}
	
	public boolean delete(Long nodeid)
	{
		boolean b = false;
		String delsql = "delete from nms_vmwareconnectconfig where nodeid=" + nodeid;
		try {
			conn.executeUpdate(delsql);
			b = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return b;
	}
	
	public List queryall()
	{
		List listall = new ArrayList();
		String queryallsql = "select * from nms_vmwareconnectconfig";
		try {
			rs = conn.executeQuery(queryallsql);
			while(rs.next())
			{
				listall.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("list all vmwareconnectconfig error");
			e.printStackTrace();
		}
		return listall;
	}
	
	public List getbynodeid(Long nodeid)
	{
		List list = new ArrayList();
		String queryonesql = "select * from nms_vmwareconnectconfig where nodeid=" + nodeid;
		try {
			rs = conn.executeQuery(queryonesql);
			while(rs.next())
			{
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("query cdp vmwareconnectconfig error");
			e.printStackTrace();
		}
		return list;
	}

	
	public Hashtable getPingDataById(String ip,Integer id,String starttime,String endtime) {
	   	   Hashtable hash = new Hashtable();
	   	   if (!starttime.equals("") && !endtime.equals("")) {
	   		   List list1 = new ArrayList();
	   		   String sql = "";
	   		   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
	   			   sql = "select a.thevalue,a.collecttime from ping"+ip+" a where " +
	   			   	"(a.collecttime >= '"+starttime +"' and  a.collecttime <= '"+endtime+"') and subentity = 'ConnectUtilization' order by id";;
	   		   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
	   			   sql = "select a.thevalue from ping"+ip+" a where " +
	   			   	" (a.collecttime >= "+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')"+" and  a.collecttime <= "+"to_date('"+endtime+"','YYYY-MM-DD HH24:MI:SS')"+") and subentity = 'ConnectUtilization' order by id";;
	   		   }
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
	
	
	public static void main(String[] args) {
//		NDP ndp = new NDP();
//		ndp.setNodeid(new Long(1));
//		ndp.setDeviceId("ssss");
//		ndp.setPortName("portname");
//		ndp.setCollecttime(Calendar.getInstance());
//		NDPDao nd = new NDPDao();
//		nd.save(ndp);		
	}

}