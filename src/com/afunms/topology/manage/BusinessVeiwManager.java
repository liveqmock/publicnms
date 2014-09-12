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
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.ManagerInterface;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.dao.TreeNodeDao;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.model.TreeNode;


/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class BusinessVeiwManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		// TODO Auto-generated method stub
		
		if("list".equals(action)){
			System.out.println(action + "=========================");
			return list();
		} else if("showViewNode".equals(action)){
			System.out.println(action + "=========================");
			return showViewNode();
		}
		
		return null;
	}
	
	private String list(){
		String bid = request.getParameter("treeBid");
		int topotype = 1;	// 业务视图
		List list = null;
		ManageXmlDao manageXmlDao = new ManageXmlDao();
		try {
			list = manageXmlDao.findByTopoTypeAndBid(topotype, bid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manageXmlDao.close();
		}
		
		Hashtable  nodeDependListHashtable = new Hashtable();
		
		for (Object object : list) {
			ManageXml manageXml = (ManageXml)object;
			NodeDependDao nodeDependDao = new NodeDependDao(); 
			List<NodeDepend> nodeDependList = null;
			try {
				nodeDependList = nodeDependDao.findByXml(manageXml.getXmlName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				nodeDependDao.close();
			}
			nodeDependListHashtable.put(manageXml, nodeDependList);
		}
		
		request.setAttribute("nodeDependListHashtable", nodeDependListHashtable);
		request.setAttribute("list", list);
		request.setAttribute("bid", bid);
		return "/performance/businessview/list.jsp";
	}
	
	private String showViewNode(){
		String jsp = "/performance/businessview/showviewnode.jsp";
		// 视图ID
		String viewId = request.getParameter("viewId");
		ManageXml manageXml = null;
		ManageXmlDao manageXmlDao = new ManageXmlDao();
		try {
			manageXml = (ManageXml)manageXmlDao.findByID(viewId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manageXmlDao.close();
		}
		if(manageXml == null){
			return list();
		}
		
		
		NodeDependDao nodeDependDao = new NodeDependDao(); 
		List<NodeDepend> list = null;
		try {
			list = nodeDependDao.findByXml(manageXml.getXmlName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			nodeDependDao.close();
		}
		
		List<NodeDTO> nodeDTOList = new ArrayList<NodeDTO>();
		List<Node> nodeList = new ArrayList<Node>();
		Hashtable<Node, String> nodeTagHash = new Hashtable<Node, String>();
		Hashtable<Node, TreeNode> treeNodeHash = new Hashtable<Node, TreeNode>();
		if(list!=null){
			
			for(int i = 0 ; i < list.size(); i++){
				NodeDepend nodeDepend = list.get(i);
				String nodeId = nodeDepend.getNodeId();
				String nodeTag = nodeId.substring(0, 3);
				String node_id = nodeId.substring(3);
				
				TreeNodeDao treeNodeDao = new TreeNodeDao();
				TreeNode vo = null;
				try {
					vo = (TreeNode) treeNodeDao.findByNodeTag(nodeTag);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					treeNodeDao.close();
				}
				Node node = null;
				if (vo != null && vo.getName() != null && !"".equals(vo.getName())) {
					node = PollingEngine.getInstance().getNodeByCategory(
							vo.getName(), Integer.parseInt(node_id));
				}
				if(node != null){
					nodeList.add(node);
					nodeTagHash.put(node, nodeTag);
					treeNodeHash.put(node, vo);
				}
				
				// 此处为 从数据库表里获取数据 暂未使用 但功能已完成
				NodeUtil nodeUtil = new NodeUtil();
				List<BaseVo> baseVolist = nodeUtil.getByNodeTag(nodeTag, vo.getCategory());
				List<NodeDTO> AllNodeDTOList = nodeUtil.conversionToNodeDTO(baseVolist);
				if(AllNodeDTOList != null){
					for (NodeDTO nodeDTO : AllNodeDTOList) {
						if(nodeDTO.getNodeid().equalsIgnoreCase(node_id)){
							nodeDTOList.add(nodeDTO);
						}
					}
				}
			}
		}
		
		request.setAttribute("manageXml", manageXml);
		request.setAttribute("nodeList", nodeList);
		request.setAttribute("list", nodeDTOList);
		request.setAttribute("nodeTagHash", nodeTagHash);
		request.setAttribute("treeNodeHash", treeNodeHash);
		return jsp;
	}
    
}
