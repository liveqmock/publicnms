package com.afunms.topology.manage;

import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.OthersNode;
import com.afunms.system.model.User;
import com.afunms.topology.dao.OtherNodeDao;
import com.afunms.topology.model.OtherNode;

public class OtherManager extends BaseManager implements ManagerInterface{

	public String execute(String action) {	  
        if(action.equals("list"))
            return list();    
        if(action.equals("ready_add"))
        	return "/topology/other/add.jsp";
        if(action.equals("add"))
        	return add();
        if(action.equals("delete"))
            return delete();
        if(action.equals("ready_edit"))
        {	  
 		    DaoInterface dao = new OtherNodeDao();
    	    setTarget("/topology/other/edit.jsp");
            return readyEdit(dao);
        }
        if(action.equals("update"))
            return update();
        if(action.equals("addalert"))
            return addalert();
        if(action.equals("cancelalert"))
            return cancelalert();
        if(action.equals("detail"))
            return detail();
        if(action.equals("topo_detail"))
            return topo_detail();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String detail() {  
		String id = getParaValue("id");
		OtherNode vo = new OtherNode();
		OtherNodeDao otherNodeDao = new OtherNodeDao();
		try {
			vo = (OtherNode) otherNodeDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			otherNodeDao.close();
		}
		String type = "";
		OthersNode others = null;
		if(vo.getCategory()==91){
			type = "GGSN";
			others = (OthersNode) PollingEngine.getInstance().getGgsnByID(Integer.parseInt(id)); 
		}else if(vo.getCategory()==92){
			type = "SGSN";
			others = (OthersNode) PollingEngine.getInstance().getSgsnByID(Integer.parseInt(id)); 
		}
		request.setAttribute("others", others);
		request.setAttribute("type", type);
		request.setAttribute("keys", vo.getName()+"_"+type);
		return "/topology/other/detail.jsp";
	}
	
	private String topo_detail(){  
		String id = getParaValue("id");
		OtherNode vo = new OtherNode();
		OtherNodeDao otherNodeDao = new OtherNodeDao();
		try {
			vo = (OtherNode) otherNodeDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			otherNodeDao.close();
		}
		String type = "";
		OthersNode others = null;
		if(vo.getCategory()==91){
			type = "GGSN";
			others = (OthersNode) PollingEngine.getInstance().getGgsnByID(Integer.parseInt(id)); 
		}else if(vo.getCategory()==92){
			type = "SGSN";
			others = (OthersNode) PollingEngine.getInstance().getSgsnByID(Integer.parseInt(id)); 
		}
		request.setAttribute("others", others);
		request.setAttribute("keys", vo.getName()+"_"+type);
		return "/topology/other/detail1.jsp";
	}

	private String cancelalert() {
		OtherNode vo = new OtherNode();
		OtherNodeDao otherNodeDao = new OtherNodeDao();
		try {
			vo = (OtherNode) otherNodeDao.findByID(getParaValue("id"));
			vo.setManaged(0);
			otherNodeDao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			otherNodeDao.close();
		}
		return list();
	}

	private String addalert() {
		OtherNode vo = new OtherNode();
		OtherNodeDao otherNodeDao = new OtherNodeDao();
		try {
			vo = (OtherNode) otherNodeDao.findByID(getParaValue("id"));
			vo.setManaged(1);
			otherNodeDao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			otherNodeDao.close();
		}
		return list();
	}

	private String update() {
		OtherNode vo = new OtherNode();
		boolean issave = false;
		OtherNodeDao otherNodeDao = new OtherNodeDao();
		vo.setId(getParaIntValue("id"));
    	vo.setAlais(getParaValue("alias"));
    	vo.setName(getParaValue("name"));
    	vo.setCategory(getParaIntValue("category"));
    	vo.setIpAddress("");
    	if(getParaValue("sendmobiles")!=null){
    		vo.setSendmobiles(getParaValue("sendmobiles"));
    	} else {
    		vo.setSendmobiles("");
    	}
		if(getParaValue("sendemail")!=null){
			vo.setSendemail(getParaValue("sendemail"));
		} else {
			vo.setSendemail("");
    	}
		if(getParaValue("sendphone")!=null){
			vo.setSendphone(getParaValue("sendphone"));
		} else {
			vo.setSendphone("");
    	}
        vo.setBid(getParaValue("bid"));
        vo.setManaged(getParaIntValue("managed"));
        try {
        	issave = otherNodeDao.update(vo);	
        } catch(Exception e) {
        	e.printStackTrace();
        } finally {
        	otherNodeDao.close();
        }
        if(issave){
        	OthersNode others = null;
        	if(vo.getCategory()==91){
	    		others = (OthersNode) PollingEngine.getInstance().getGgsnByID(vo.getId()); 
	    		others.setType("GGSN");
	    	}
	    	if(vo.getCategory()==92){
	    		others = (OthersNode) PollingEngine.getInstance().getSgsnByID(vo.getId()); 
	    		others.setType("SGSN");
	    	}
	    	others.setAlias(vo.getName());
	    	others.setIpAddress("");
	    	others.setName(vo.getAlais());
	    	others.setManaged(vo.getManaged());
	    	others.setSendphone(vo.getSendphone());
	    	others.setSendemail(vo.getSendemail());
	    	others.setSendmobiles(vo.getSendmobiles());
	    	others.setBid(vo.getBid());
	    	others.setCategory(vo.getCategory());
	    	others.setStatus(0);
		}
		return list();
	}

	private String delete() {
		String[] ids = getParaArrayValue("checkbox");
    	if(ids != null && ids.length > 0){		
    		for(int i=0;i<ids.length;i++){
    			boolean isdelete = false;
    			OtherNodeDao otherNodeDao = new OtherNodeDao();
    			OtherNode vo = null;
				try {
					vo = (OtherNode)otherNodeDao.findByID(ids[i]);
				} catch (Exception e) {
					e.printStackTrace();
				} 
    			try {
					isdelete = otherNodeDao.delete(ids[i]);
				} catch (Exception e) {
					e.printStackTrace();
				} 
				otherNodeDao.close();
				if(isdelete&&vo!=null){
	    	    	if(vo.getCategory()==91){
	    	    		PollingEngine.getInstance().deleteGgsnByID(vo.getId());
	    	    	}else if(vo.getCategory()==92){
	    	    		PollingEngine.getInstance().deleteSgsnByID(vo.getId());
	    	    	}
	    		}
    		}
//    		for(int i=0;i<ids.length;i++){
//    			//更新业务视图
//    			String id = ids[i];
//    			NodeDependDao nodedependao = new NodeDependDao();
//    			List weslist = nodedependao.findByNode("jbo"+id);
//    			if(weslist!=null&&weslist.size()>0){
//    				for(int j = 0; j < weslist.size(); j++){
//    					NodeDepend wesvo = (NodeDepend)weslist.get(j);
//    					if(wesvo!=null){
//    						LineDao lineDao = new LineDao();
//    		    			lineDao.deleteByidXml("jbo"+id, wesvo.getXmlfile());
//    		    			NodeDependDao nodeDependDao = new NodeDependDao();
//    		    			if(nodeDependDao.isNodeExist("jbo"+id, wesvo.getXmlfile())){
//    		            		nodeDependDao.deleteByIdXml("jbo"+id, wesvo.getXmlfile());
//    		            	} else {
//    		            		nodeDependDao.close();
//    		            	}
//    		    			
//    		    			//yangjun
//    		    			User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
//    		    			ManageXmlDao mXmlDao =new ManageXmlDao();
//    		    			List xmlList = new ArrayList();
//    		    			try{
//    		    				xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
//    		    			}catch(Exception e){
//    		    				e.printStackTrace();
//    		    			}finally{
//    		    				mXmlDao.close();
//    		    			}
//    		    			try{
//    		    				ChartXml chartxml;
//    		    			    chartxml = new ChartXml("tree");
//    		    			    chartxml.addViewTree(xmlList);
//    		    		    }catch(Exception e){
//    		    			    e.printStackTrace();   	
//    		    		    }
//    		                
//    		                ManageXmlDao subMapDao = new ManageXmlDao();
//    		    			ManageXml manageXml = (ManageXml) subMapDao.findByXml(wesvo.getXmlfile());
//    		    			if(manageXml!=null){
//    		    				NodeDependDao nodeDepenDao = new NodeDependDao();
//    		    				try{
//    		    				    List lists = nodeDepenDao.findByXml(wesvo.getXmlfile());
//    		    				    ChartXml chartxml;
//    		    					chartxml = new ChartXml("NetworkMonitor","/"+wesvo.getXmlfile().replace("jsp", "xml"));
//    		    					chartxml.addBussinessXML(manageXml.getTopoName(),lists);
//    		    					ChartXml chartxmlList;
//    		    					chartxmlList = new ChartXml("NetworkMonitor","/"+wesvo.getXmlfile().replace("jsp", "xml").replace("businessmap", "list"));
//    		    					chartxmlList.addListXML(manageXml.getTopoName(),lists);
//    		    				}catch(Exception e){
//    		    				    e.printStackTrace();   	
//    		    				}finally{
//    		    					nodeDepenDao.close();
//    		                    }
//    		    			}
//    					}
//    				}
//    			}
//    		}
    	}
		return list();
	}

	private String add() {   
		boolean issave = false;
		OtherNodeDao dao=null;
		dao = new OtherNodeDao();
		OtherNode node = (OtherNode) dao.findByType_Name(getParaIntValue("category")+"",getParaValue("name"));
		if(node!=null){
			setErrorCode(ErrorMessage.SERVICE_EXIST);
			return null;
		}
		OtherNode vo = new OtherNode();
		vo.setAlais(getParaValue("alias"));
		vo.setBid(getParaValue("bid"));
    	vo.setCategory(getParaIntValue("category"));
    	vo.setIpAddress("");
    	vo.setName(getParaValue("name"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setManaged(getParaIntValue("managed"));
    	try {
            dao = new OtherNodeDao();
            issave = dao.save(vo);
        } catch(Exception e) {
			
		} finally {
			dao.close();
		} 
		if(issave){
			dao = new OtherNodeDao();
			OtherNode otherNode = (OtherNode) dao.findByType_Name(vo.getCategory()+"",vo.getName());
			if(otherNode!=null){
				OthersNode others = new OthersNode();
		    	others.setId(otherNode.getId());
		    	others.setAlias(otherNode.getName());
		    	others.setIpAddress(otherNode.getIpAddress());
		    	others.setName(otherNode.getAlais());
		    	others.setManaged(otherNode.getManaged());
		    	others.setSendphone(otherNode.getSendphone());
		    	others.setSendemail(otherNode.getSendemail());
		    	others.setSendmobiles(otherNode.getSendmobiles());
		    	others.setBid(otherNode.getBid());
		    	others.setCategory(otherNode.getCategory());
		    	others.setStatus(0);
		    	if(otherNode.getCategory()==91){
		    		others.setType("GGSN");
		    		PollingEngine.getInstance().addGgsn(others);
		    	}
		    	if(otherNode.getCategory()==92){
		    		others.setType("SGSN");
		    		PollingEngine.getInstance().addSgsn(others);
		    	}
			}
		}
        return "/other.do?action=list&jp=1";
    }

	private String list() {
		User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		String bids = operator.getBusinessids();
		if(bids == null)bids = "";
		List list = null;
		OtherNodeDao otherNodeDao = new OtherNodeDao();
		try
		{
			if(operator.getRole() == 0){
				list = otherNodeDao.loadAll();
			}else{
				list = otherNodeDao.loadByPerAll(bids);
			}
		request.setAttribute("list",list);
		}catch(Exception e)
		{
			e.printStackTrace();
		} 
	    finally
	    {
	    	otherNodeDao.close();
	    }
	    return "/topology/other/list.jsp";
	}

}
