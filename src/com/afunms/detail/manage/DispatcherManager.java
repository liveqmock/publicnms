/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author nielin
 * @project afunms
 * @date 2010-12-08
 */

package com.afunms.detail.manage;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.util.Constant;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

/**
 * 此 manager 用于进入设备详细信息页面跳转
 */
public class DispatcherManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		// TODO Auto-generated method stub
		if("dispatcher".equals(action)){
			return dispatcher();
		}
		return ErrorMessage.getErrorMessage(ErrorMessage.ACTION_NO_FOUND);
	}
	
	private String dispatcher(){
		String nodeid = getParaValue("nodeid");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		return dispatcher(nodeid, type, subtype);
	}
	
	private String dispatcher(String nodeid, String type , String subtype){
		String jsp = null;
		if(Constant.TYPE_NET.equalsIgnoreCase(type)){
			HostNode hostNode = null;
			HostNodeDao hostNodeDao = new HostNodeDao();
			try {
				hostNode = (HostNode)hostNodeDao.findByID(nodeid);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				hostNodeDao.close();
			}
//			System.out.println("-------------------------------------->"+hostNode.getCategory());
			if(hostNode.getCategory() == 6){
				jsp = "/detail/net/fibrenetdetail.jsp";
			} else {
				jsp = "/detail/net/netdetail.jsp";
			}
		} else if(Constant.TYPE_HOST.equalsIgnoreCase(type)){
			if(Constant.TYPE_HOST_SUBTYPE_AIX.equalsIgnoreCase(subtype)){
				System.out.println("==================aix");
				jsp = "/detail/host/aixdetail.jsp";
			} else if(Constant.TYPE_HOST_SUBTYPE_LINUX.equalsIgnoreCase(subtype)){
				System.out.println("==================linux");
				jsp = "/detail/host/linuxdetail.jsp";
			} else if(Constant.TYPE_HOST_SUBTYPE_AS400.equalsIgnoreCase(subtype)){
				System.out.println("==================as400");
				jsp = "/detail/host/as400detail.jsp";
			} else {
				jsp = "/detail/host/hostdetail.jsp";
			}
		}
		
		request.setAttribute("nodeid", nodeid);
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);
		return jsp;
	}
	
}
