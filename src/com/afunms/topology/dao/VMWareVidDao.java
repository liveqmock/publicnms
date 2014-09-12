package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.polling.base.Node;
import com.afunms.polling.om.VMWareVid;

public class VMWareVidDao extends BaseDao implements DaoInterface {
	
	public VMWareVidDao() {
		super("nms_vmwarevid");
		// TODO Auto-generated constructor stub
    }

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		VMWareVid vmware = new VMWareVid();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			vmware.setId(rs.getLong("id"));
			vmware.setCategory(rs.getString("category"));
			vmware.setFlag(rs.getString("flag"));
			vmware.setHoid(rs.getString("hoid"));
			vmware.setNodeid(rs.getLong("nodeid"));
			vmware.setVid(rs.getString("vid"));
			vmware.setGuestname(rs.getString("guestname"));	
			vmware.setBak(rs.getString("bak"));	
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return vmware;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		VMWareVid vmware = (VMWareVid) vo;
		StringBuffer addsql = new StringBuffer(200);
		addsql.append("insert into nms_vmwarevid(hoid,flag,category,nodeid,vid,guestname,bak)values('");
		addsql.append(vmware.getHoid());
		addsql.append("','");
		addsql.append("1");
		addsql.append("','");
		addsql.append(vmware.getCategory());
		addsql.append("',");
		addsql.append(vmware.getNodeid());
		addsql.append(",'");
		addsql.append(vmware.getVid());
		addsql.append("','");
		addsql.append(vmware.getGuestname());
		addsql.append("','");
		addsql.append(vmware.getBak());
		addsql.append("')");
		return saveOrUpdate(addsql.toString());
	}
	
	public boolean save(List vidList) {
		// TODO Auto-generated method stub
		if(vidList != null && vidList.size()>0){
			try{
				StringBuffer addsql = new StringBuffer(100);
				for(int i=0;i<vidList.size();i++){
					addsql = new StringBuffer(100);
					VMWareVid vmware = (VMWareVid)vidList.get(i);
					addsql.append("insert into nms_vmwarevid(hoid,flag,category,nodeid,vid,guestname,bak)values('");
					addsql.append(vmware.getHoid());
					addsql.append("','");
					addsql.append("1");
					addsql.append("','");
					addsql.append(vmware.getCategory());
					addsql.append("',");
					addsql.append(vmware.getNodeid());
					addsql.append(",'");
					addsql.append(vmware.getVid());
					addsql.append("','");
					addsql.append(vmware.getGuestname());
					addsql.append("','");
					addsql.append(vmware.getBak());
					addsql.append("')");
					
//					System.out.println("---nms_vmwarevid----"+addsql.toString());
					
				    conn.addBatch(addsql.toString());
					
			     }
				conn.executeBatch();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				conn.close();
			}
		}
		return true;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		VMWareVid vmware = (VMWareVid) vo;
		StringBuffer updatesql = new StringBuffer(200);
		updatesql.append("update nms_vmwarevid set nodeid=");
		updatesql.append(vmware.getNodeid());
		updatesql.append(",vid='");
		updatesql.append(vmware.getVid());
		updatesql.append("',guestname='");
		updatesql.append(vmware.getGuestname());
		updatesql.append("',bak='");
		updatesql.append(vmware.getBak());
		updatesql.append("' where id=");
		updatesql.append(vmware.getId());
		return saveOrUpdate(updatesql.toString());
	}
	
	public boolean delete(BaseVo vo)
	{
		VMWareVid vmware = (VMWareVid) vo;
		boolean b = false;
		String delsql = "delete from nms_vmwarevid where id=" + vmware.getId();
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
		String delsql = "delete from nms_vmwarevid where nodeid=" + nodeid;
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
		String queryallsql = "select * from nms_vmwarevid";
		try {
			rs = conn.executeQuery(queryallsql);
			while(rs.next())
			{
				listall.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("list all nms_vmwarevid error");
			e.printStackTrace();
		}
		return listall;
	}
	
	public List getbynodeid(Long nodeid)
	{
		List list = new ArrayList();
		String queryonesql = "select * from nms_vmwarevid where nodeid=" + nodeid;
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
	
	public List getbynodeid1(Long nodeid)
	{
		List list = new ArrayList();
		String queryonesql = "select * from nms_vmwarevid where nodeid=" + nodeid+" and category != 'datacenter'";
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
	
	public List checkVid(Long nodeid,String vid)
	{
		List list = new ArrayList();
		String queryonesql = "select * from nms_vmwarevid where nodeid=" + nodeid+" and vid='"+vid+"'";
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
	
	public List queryVid(Long nodeid,String category)
	{
		List list = new ArrayList();
		String queryonesql = "select * from nms_vmwarevid where nodeid=" + nodeid+" and category='"+category+"'";
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
	
	public List queryVMVid(int nodeid,String category)
	{
		List list = new ArrayList();
		String queryonesql = "select vid from nms_vmwarevid where nodeid=" + nodeid+" and category='"+category+"' and flag ='1'";
		try {
			rs = conn.executeQuery(queryonesql); 
			while(rs.next())
			{
				String vid = rs.getString("vid");
				list.add(vid);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("query cdp vmwareconnectconfig error");
			e.printStackTrace();
		}
		return list;
	}
	
	public List queryVMVidCategory(int nodeid,String category)
	{
		List list = new ArrayList();
		String queryonesql = "select * from nms_vmwarevid where nodeid=" + nodeid+" and category='"+category+"' and flag ='1'";
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
	
	
	public List queryVidFlag(String category,String nodeid,String hoid)
	{
		List list = new ArrayList();
		String queryonesql = "";
		if(category.equalsIgnoreCase("vmware") && !hoid.equalsIgnoreCase("vmware")){
			queryonesql = "select vid from nms_vmwarevid where flag = '1' and hoid = '"+hoid+"' and nodeid="+nodeid+" order by guestname";
		}else if(category.equalsIgnoreCase("vmware") && hoid.equalsIgnoreCase("vmware")){
			queryonesql = "select vid from nms_vmwarevid where flag = '1' and category = '"+category+"' and nodeid="+nodeid+" order by guestname";
		}else{
			queryonesql = "select vid from nms_vmwarevid where flag = '1' and category = '"+category+"' and nodeid="+nodeid+" order by guestname";
		}
		try {
			System.out.println("---sql----"+queryonesql);
			rs = conn.executeQuery(queryonesql); 
			while(rs.next())
			{
				String vid = rs.getString("vid");
				list.add(vid);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("query cdp vmwareconnectconfig error");
			e.printStackTrace();
		}
		return list;
	}
	
	public List queryVid(String nodeid)
	{
		List list = new ArrayList();
		String queryonesql = "select vid from nms_vmwarevid where flag = '0' and nodeid="+nodeid;
		try {
			rs = conn.executeQuery(queryonesql); 
			while(rs.next())
			{
				String vid = rs.getString("vid");
				list.add(vid);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("query cdp vmwareconnectconfig error");
			e.printStackTrace();
		}
		return list;
	}
	
	public List queryAllVid(String category,String nodeid)
	{
		List list = new ArrayList();
		String queryonesql = "select vid from nms_vmwarevid where  category='"+category+"'  and  nodeid="+nodeid+" order by guestname";
		try {
			rs = conn.executeQuery(queryonesql); 
			while(rs.next())
			{
				String vid = rs.getString("vid");
				list.add(vid);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("query cdp vmwareconnectconfig error");
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	public String queryFlag(String nodeid,String vid)
	{
		String flag = "";
		String queryonesql = "select flag from nms_vmwarevid where vid = '"+vid+"' and nodeid="+nodeid;
		try {
			rs = conn.executeQuery(queryonesql); 
			while(rs.next())
			{
				flag = rs.getString("flag");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("query cdp vmwareconnectconfig error");
			e.printStackTrace();
		}
		return flag;
	}
	
	
	public String queryName(String nodeid,String vid)
	{
		String name = "";
		String queryonesql = "select guestname from nms_vmwarevid where vid = '"+vid+"' and nodeid="+nodeid;
		try {
			rs = conn.executeQuery(queryonesql); 
			while(rs.next())
			{
				name = rs.getString("guestname");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("query cdp vmwareconnectconfig error");
			e.printStackTrace();
		}
		return name;
	}
	
	public String queryPhysicalHoid(String nodeid,String vid)
	{
		String name = "";
		String queryonesql = "select hoid from nms_vmwarevid where vid = '"+vid+"' and nodeid="+nodeid;
		try {
			rs = conn.executeQuery(queryonesql); 
			while(rs.next())
			{
				name = rs.getString("hoid");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("query cdp vmwareconnectconfig error");
			e.printStackTrace();
		}
		return name;
	}
	
	public void updateVidFlag(String[] vid,String nodeid)
	{
		if(vid.length>0 && vid != null){
			for(int i=0;i<vid.length;i++){
				String flag = this.queryFlag(nodeid,vid[i]);
				if(flag != null && !flag.equals("")){
				if(flag.equals("1")){
					flag = "0";
				}else{
					flag = "1";
				}
				}
		        String queryonesql = "update nms_vmwarevid set flag='"+flag+"' where nodeid="+nodeid +" and vid = '"+vid[i]+"'";
		try {
			 conn.executeUpdate(queryonesql); 
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("query cdp vmwareconnectconfig error");
			e.printStackTrace();
		}
			}
		}
	}
	

	public void updateVidFlag(List vid,String nodeid)
	{
		if(vid.size()>0 && vid != null){
			for(int i=0;i<vid.size();i++){
		        String queryonesql = "update nms_vmwarevid set flag='0' where nodeid="+nodeid +" and vid = '"+vid.get(i)+"'";
		try {
			 conn.executeUpdate(queryonesql); 
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("query cdp vmwareconnectconfig error");
			e.printStackTrace();
		}
			}
		}
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