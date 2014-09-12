/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.manage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.EncryptUtil;
import com.afunms.topology.dao.ConnectTypeConfigDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.RemotePingHostDao;
import com.afunms.topology.dao.RemotePingNodeDao;
import com.afunms.topology.model.ConnectTypeConfig;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.RemotePingHost;
import com.afunms.topology.model.RemotePingNode;


/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class ConnectTypeConfigManager extends BaseManager implements ManagerInterface
{

	public String execute(String action) {
		// TODO Auto-generated method stub
		if("list".equals(action)){
			return list();
		}if("setCollectionAgreement".equals(action)){
			return setCollectionAgreement();
		}if("updateConnectConfig".equals(action)){
			return updateConnectConfig();
		}if("deleteCollectionAgreement".equals(action)){
			return deleteCollectionAgreement();
		}if("readyConnectConfig".equals(action)){
			return readyConnectConfig();
		}if("showChildNode".equals(action)){
			return showChildNode();
		}if("setChildNode".equals(action)){
			return setChildNode();
		}if("addChildNode".equals(action)){
			return addChildNode();
		}
	    setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
	private String list(){
		List list = new ArrayList();
		
		Hashtable hostNodeHashtable = new Hashtable();
		try {
			ConnectTypeConfigDao connectTypeConfigDao = new ConnectTypeConfigDao();
			try {
				list = connectTypeConfigDao.loadAll();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				connectTypeConfigDao.close();
			}
			List hostNodeList = null;
			HostNodeDao hostNodeDao = new HostNodeDao();
			try {
				hostNodeList = hostNodeDao.loadAll();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				hostNodeDao.close();
			}
			for(int i = 0 ; i < hostNodeList.size() ; i ++){
				HostNode hostNode = (HostNode)hostNodeList.get(i);
				hostNodeHashtable.put(String.valueOf(hostNode.getId()), hostNode);
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("hostNodeHashtable", hostNodeHashtable);
		request.setAttribute("list", list);
		return "/topology/connecttypeconfig/list.jsp";
	}
	
	private String setCollectionAgreement(){
		String node_id = getParaValue("id");
		String endpoint = getParaValue("endpoint");
		HostNode hostNode = null ;
		HostNodeDao dao = new HostNodeDao();
		try {
			hostNode =  (HostNode)dao.findByID(node_id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			dao.close();
		}
		if(hostNode == null){
			throw new NullPointerException("hostNode is null");
		}
		request.setAttribute("node", hostNode);
		request.setAttribute("endpoint", endpoint);
		return "/topology/remoteping/setCollectionAgreement.jsp";
	}
	
	/*
	 * 修改检测配置
	 */
	private String updateConnectConfig(){
		String nodeId = getParaValue("nodeId");
		ConnectTypeConfigDao connectTypeConfigDao = new ConnectTypeConfigDao();
		ConnectTypeConfig connectTypeConfig = null;
		try{
			connectTypeConfig = (ConnectTypeConfig)connectTypeConfigDao.findByNodeId(nodeId);
		}catch(Exception e){
			
		}finally{
			connectTypeConfigDao.close();
		}
		//若没有该记录,返回到列表
		if(connectTypeConfig == null)return list();
		
		//对加密密码进行处理
		String password = getParaValue("password");		
		String enpassword = "";
		try{
			if(password != null && password.trim().length()>0){
				if(connectTypeConfig.getPassword().equals(password)){
					connectTypeConfig.setPassword(password);
				}else{
					//需要加密后替换原来密码
					String newPassword = "";
					try{
						newPassword = EncryptUtil.encode(password);
					}catch(Exception e){
					}
					connectTypeConfig.setPassword(newPassword);
				}	
			}else{
				connectTypeConfig.setPassword(password);
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		connectTypeConfig.setConnecttype(getParaValue("connecttype"));
		connectTypeConfig.setLoginPrompt(getParaValue("loginPrompt"));
		connectTypeConfig.setUsername(getParaValue("userName"));
		connectTypeConfig.setPasswordPrompt(getParaValue("passwordPrompt"));
		connectTypeConfig.setShellPrompt(getParaValue("shellPrompt"));
		connectTypeConfigDao = new ConnectTypeConfigDao();
		try{
			connectTypeConfigDao.update(connectTypeConfig);
		}catch(Exception e){
			
		}finally{
			connectTypeConfigDao.close();
		}	
		return list();
	}
	
	
	private String deleteCollectionAgreement(){
		String node_id = getParaValue("id");
		HostNode hostNode = null ;
		HostNodeDao dao = new HostNodeDao();
		try {
			dao.updateEndPoint(node_id, "0");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			dao.close();
		}
		
		RemotePingHostDao remotePingHostDao = new RemotePingHostDao();
		try {
			remotePingHostDao.deleteByNodeId(node_id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			remotePingHostDao.close();
		}
		
		RemotePingNodeDao remotePingNodeDao = new RemotePingNodeDao();
		try {
			remotePingNodeDao.deleteByNodeId(node_id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			remotePingNodeDao.close();
		}
		
		remotePingNodeDao = new RemotePingNodeDao();
		try {
			remotePingNodeDao.deleteByChildNodeId(node_id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			remotePingNodeDao.close();
		}
		return "/network.do?action=list&jp=1";
		//return "/topology/network/list.jsp";
	}
	
	private String readyConnectConfig(){
		String node_id = getParaValue("id");
		HostNode hostNode = null ;
		HostNodeDao dao = new HostNodeDao();
		try {
			hostNode =  (HostNode)dao.findByID(node_id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			dao.close();
		}
		ConnectTypeConfig connectTypeConfig = null ;
		ConnectTypeConfigDao connectTypeConfigDao = new ConnectTypeConfigDao();
		try {
			connectTypeConfig = connectTypeConfigDao.findByNodeId(String.valueOf(hostNode.getId()));
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("connectTypeConfig", connectTypeConfig);
		request.setAttribute("node", hostNode);
		return "/topology/connecttypeconfig/setConnectTypeConfig.jsp";
	}
	
	
	private String showChildNode(){
		String nodeId = getParaValue("node");
		List remotePingNodeList = new ArrayList();
		RemotePingNodeDao remotePingNodeDao = new RemotePingNodeDao();
		try {
			remotePingNodeList = remotePingNodeDao.findByNodeId(nodeId);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			remotePingNodeDao.close();
		}
		List list = new ArrayList();
		
		HostNodeDao hostNodeDao = new HostNodeDao();
		for(int i = 0 ; i < remotePingNodeList.size(); i++){
			HostNode hostNode = (HostNode)hostNodeDao.findByID(((RemotePingNode)remotePingNodeList.get(i)).getChildNodeId());
			list.add(hostNode);
		}
		request.setAttribute("list", list);
		return "/topology/remoteping/showChildNode.jsp";
	}
	
	
	private String setChildNode(){
		String nodeId = getParaValue("node");
		if(nodeId == null ){
			return null;
		}
		List list = null;
		HostNodeDao hostNodeDao = new HostNodeDao();
		try {
			list = hostNodeDao.loadByPingChildNode();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			hostNodeDao.close();
		}
		List remotePingNodeList = null;
		RemotePingNodeDao remotePingNodeDao = new RemotePingNodeDao();
		try {
			remotePingNodeList = remotePingNodeDao.findByNodeId(nodeId);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			remotePingNodeDao.close();
		}
		request.setAttribute("remotePingNodeList", remotePingNodeList);
		request.setAttribute("list", list);
		request.setAttribute("nodeId", nodeId);
		return "/topology/remoteping/setChildNode.jsp";
	}
	
	private String addChildNode(){
		boolean result = false;
		String[] childNodeIds = getParaArrayValue("checkbox");
		String nodeId = getParaValue("nodeId");
		RemotePingNodeDao remotePingNodeDao = new RemotePingNodeDao();
		try {
			try {
				result = remotePingNodeDao.deleteByNodeId(nodeId);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				remotePingNodeDao.close();
			}
			remotePingNodeDao = new RemotePingNodeDao();
			List list = new ArrayList();
			for(int i = 0 ; i < childNodeIds.length; i++){
				RemotePingNode remotePingNode = new RemotePingNode();
				remotePingNode.setNode_id(nodeId);
				remotePingNode.setChildNodeId(childNodeIds[i]);
				list.add(remotePingNode);
			}
			result = remotePingNodeDao.saveList(list);
			if(result){
				List hostList = new ArrayList();
				for(int i = 0 ; i < childNodeIds.length ; i++){
					List temp = new ArrayList();
					temp.add(childNodeIds[i]);
					temp.add("2");
					hostList.add(temp);
				}
				HostNodeDao hostNodeDao = new HostNodeDao();
				try {
					hostNodeDao.updateEndPoint(hostList);
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					hostNodeDao.close();
				}
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			remotePingNodeDao.close();
		}
		
		return "/";
	}
	
	private RemotePingHost createRemotePingHost(){
		String nodeId = getParaValue("nodeId");
		String userName = getParaValue("userName");
		String password = getParaValue("password");
		String loginPrompt = getParaValue("loginPrompt");
		String passwordPrompt = getParaValue("passwordPrompt");
		String shellPrompt = getParaValue("shellPrompt");
		
		RemotePingHost remotePingHost = new RemotePingHost();
		remotePingHost.setNode_id(nodeId);
		remotePingHost.setUsername(userName);
		remotePingHost.setPassword(password);
		remotePingHost.setLoginPrompt(loginPrompt);
		remotePingHost.setPasswordPrompt(passwordPrompt);
		remotePingHost.setShellPrompt(shellPrompt);
		return remotePingHost;
	}
	
}
