/**
 * 系统快照,用于首页
 */

package com.afunms.inform.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.afunms.common.util.ShareData;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;

public class SystemSnap
{
	private SystemSnap()
	{
	}
	
	/**
	 * 网络设备的状态
	 * @auth 胡可磊
	 * date 2010-01-11
	 */
    public static int getNetworkStatus()
    {
         List nodeList = PollingEngine.getInstance().getNodeList();
         int status = 0; //初始化状态
         boolean hasAlarm = false; //有没报警的
         for(int i=0;i<nodeList.size();i++)
         {
        	 Node node = (Node)nodeList.get(i);        	 
        	 if(node.getCategory()>3) continue;

        	    if(node.isAlarm()){
           	    	hasAlarm = true; 
           	    	if(node.getStatus()>status)status = node.getStatus();
            	}
         }
         return status; 
    }
    
    public static int getUpsStatus(String nodeid)  
    {
    	CheckEventDao checkEventDao = new CheckEventDao();
         List nodeList = null;
		try {
			nodeList = checkEventDao.findByCondition(" where type = 'ups' and nodeid = '" + nodeid + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			checkEventDao.close();
		}
         int status = 0; //初始化状态
         if(nodeList!=null&&nodeList.size()>0){
        	 for(int i=0;i<nodeList.size();i++)
             {
                 CheckEvent node = (CheckEvent)nodeList.get(i);        	 
        	     if(node!=null){
           	    	 if(node.getAlarmlevel()>status)status = node.getAlarmlevel();
            	 }
             }
         }
         return status; 
    }
    public static int getAirConditionStatus(String nodeid)  
    {
    	CheckEventDao checkEventDao = new CheckEventDao();
         List nodeList = null;
		try {
			nodeList = checkEventDao.findByCondition(" where type = 'air' and nodeid = '" + nodeid + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			checkEventDao.close();
		}
         int status = 0; //初始化状态
         if(nodeList!=null&&nodeList.size()>0){
        	 for(int i=0;i<nodeList.size();i++)
             {
                 CheckEvent node = (CheckEvent)nodeList.get(i);        	 
        	     if(node!=null){
           	    	 if(node.getAlarmlevel()>status)status = node.getAlarmlevel();
            	 }
             }
         }
         return status; 
    }
    public static int getUpsStatus()
    {
    	CheckEventDao checkEventDao = new CheckEventDao();
         List nodeList = null;
		try {
			nodeList = checkEventDao.findByCondition(" where type = 'ups'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			checkEventDao.close();
		}
         int status = 0; //初始化状态
         if(nodeList!=null&&nodeList.size()>0){
        	 for(int i=0;i<nodeList.size();i++)
             {
                 CheckEvent node = (CheckEvent)nodeList.get(i);        	 
        	     if(node!=null){
           	    	 if(node.getAlarmlevel()>status)status = node.getAlarmlevel();
            	 }
             }
         }
         return status; 
    }
    
    public static int getNetworkStatus(String nodeid){
        List nodeList = PollingEngine.getInstance().getNodeList();
        int status = 0; //初始化状态
        boolean hasAlarm = false; //有没报警的
        for(int i=0;i<nodeList.size();i++) {
       	    Node node = (Node)nodeList.get(i);   
       	    if(nodeid.equals(node.getId()+"")){
       	    	if(node.isAlarm()){
          	    	hasAlarm = true; 
          	    	if(node.getStatus()>status)status = node.getStatus();
           	    }
       	    }
        }
        return status; 
    }
	/**
	 * 网络路由器设备的状态
	 * @auth 胡可磊---wxy update
	 * date 2010-01-11
	 */
   
    	public static int getRouterStatus(String bids) {
    		CheckEventDao checkEventDao = new CheckEventDao();
    		List nodeList = null;
    		StringBuffer bidBuffer=new StringBuffer();
    		if (bids != null && bids.length() > 0) {
    			String[] bid=null;
    			if(bids!=null){
    				bid=new String[bids.split(",").length];
    				bid=bids.split(",");
    				for (int i = 0; i < bid.length; i++) {
    					bidBuffer.append("',").append(bid[i]).append(",'");
    					if(i!= bid.length-1){
    						bidBuffer.append(",");
    					}
    				}
    			}
    		}
    		try {
    			nodeList = checkEventDao.findByCondition(" where type = 'net' and bid in("+bidBuffer.toString()+")");
    		} catch (Exception e) {
    			e.printStackTrace();
    		} finally {
    			checkEventDao.close();
    		}
        int status = 0; //初始化状态
        boolean hasAlarm = false; //有没报警的
        if(nodeList!=null&&nodeList.size()>0){
            for(int i=0;i<nodeList.size();i++)
            {
        		 CheckEvent node = (CheckEvent)nodeList.get(i);      
        		 if(node!=null){
        			 Node node_ = (Node)PollingEngine.getInstance().getNodeByID(Integer.parseInt(node.getNodeid()));
        			 hasAlarm = true;
        			 if(node_!=null&&node_.getCategory()==1&&node_.getEndpoint()!=2){
                	    	if(node.getAlarmlevel()>status)status = node.getAlarmlevel();
                 	 }
        		 }
            }
        }
        return status; 
    }
    /**
	 * 设备的状态
	 * @auth 杨军
	 * date 2012-10-21
	 */
    public static int getNodeStatus(Node node)
    {
    	int status = 0; //初始化状态
    	Hashtable checkEventHashtable = ShareData.getCheckEventHash();
    	NodeUtil nodeUtil = new NodeUtil();
    	NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(node);
    	String chexkname = nodeDTO.getId()+":"+nodeDTO.getType()+":"+nodeDTO.getSubtype()+":";
		for(Iterator it = checkEventHashtable.keySet().iterator();it.hasNext();){ 
	        String key = (String)it.next(); 
	        if(key.startsWith(chexkname)){
	        	if(status < (Integer) checkEventHashtable.get(key)){
	        		status = (Integer) checkEventHashtable.get(key); 
	        	}
	        }
		}
        return status; 
    }
    /**
	 * 设备的状态
	 * @auth 杨军
	 * date 2012-10-21
	 */
    public static int getNodeStatus(String id,String type,String subtype)
    {
    	int status = 0; //初始化状态
    	Hashtable checkEventHashtable = ShareData.getCheckEventHash();
    	String chexkname = id+":"+type+":"+subtype+":";
		for(Iterator it = checkEventHashtable.keySet().iterator();it.hasNext();){ 
	        String key = (String)it.next(); 
	        if(key.startsWith(chexkname)){
	        	if(status < (Integer) checkEventHashtable.get(key)){
	        		status = (Integer) checkEventHashtable.get(key); 
	        	}
	        }
		}
        return status; 
    }
	/**
	 * 网络交换机设备的状态
	 * @auth 胡可磊 --wxy update
	 * date 2010-01-11
	 */
    public static int getSwitchStatus(String bids)
    {
    	CheckEventDao checkEventDao = new CheckEventDao();
		List nodeList = null;
		StringBuffer bidBuffer=new StringBuffer();
		if (bids != null && bids.length() > 0) {
			String[] bid=null;
			if(bids!=null){
				bid=new String[bids.split(",").length];
				bid=bids.split(",");
				for (int i = 0; i < bid.length; i++) {
					bidBuffer.append("',").append(bid[i]).append(",'");
					if(i!= bid.length-1){
						bidBuffer.append(",");
					}
				}
			}
		}
		try {
			nodeList = checkEventDao.findByCondition(" where type = 'net' and bid in ("+bidBuffer.toString()+")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			checkEventDao.close();
		}
        int status = 0; //初始化状态
        boolean hasAlarm = false; //有没报警的
        if(nodeList!=null&&nodeList.size()>0){
        	for(int i=0;i<nodeList.size();i++){
        		CheckEvent node = (CheckEvent)nodeList.get(i);  
        		if(node!=null){
       			    Node node_ = (Node)PollingEngine.getInstance().getNodeByID(Integer.parseInt(node.getNodeid()));
    			    hasAlarm = true;
    			    if(node_!=null&&(node_.getCategory()==2||node_.getCategory()==3||node_.getCategory()==6)){
            	    	if(node.getAlarmlevel()>status)status = node.getAlarmlevel();
             	    }
    		    }
        	}
        }
        return status; 
    }
    public static int getStorageStatus(String bids)
    {
    	CheckEventDao checkEventDao = new CheckEventDao();
		List nodeList = null;
		StringBuffer bidBuffer=new StringBuffer();
		if (bids != null && bids.length() > 0) {
			String[] bid=null;
			if(bids!=null){
				bid=new String[bids.split(",").length];
				bid=bids.split(",");
				for (int i = 0; i < bid.length; i++) {
					bidBuffer.append("',").append(bid[i]).append(",'");
					if(i!= bid.length-1){
						bidBuffer.append(",");
					}
				}
			}
		}
		try {
			nodeList = checkEventDao.findByCondition(" where type = 'storage' and bid in ("+bidBuffer.toString()+")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			checkEventDao.close();
		}
        int status = 0; //初始化状态
        boolean hasAlarm = false; //有没报警的
        if(nodeList!=null&&nodeList.size()>0){
        	for(int i=0;i<nodeList.size();i++){
        		CheckEvent node = (CheckEvent)nodeList.get(i);  
        		if(node!=null){
       			    Node node_ = (Node)PollingEngine.getInstance().getNodeByID(Integer.parseInt(node.getNodeid()));
    			    hasAlarm = true;
    			    if(node_!=null&&(node_.getCategory()==14)){
            	    	if(node.getAlarmlevel()>status)status = node.getAlarmlevel();
             	    }
    		    }
        	}
        }
        return status; 
    }
	/**
	 * 网络交换机设备的状态
	 * @auth 胡可磊---wxy update
	 * date 2010-01-11
	 */
    public static int getFirewallStatus(String bids)
    {
		CheckEventDao checkEventDao = new CheckEventDao();
		List nodeList = null;
		StringBuffer bidBuffer=new StringBuffer();
		if (bids != null && bids.length() > 0) {
			String[] bid=null;
			if(bids!=null){
				bid=new String[bids.split(",").length];
				bid=bids.split(",");
				for (int i = 0; i < bid.length; i++) {
					bidBuffer.append("',").append(bid[i]).append(",'");
					if(i!= bid.length-1){
						bidBuffer.append(",");
					}
				}
			}
		}
		try {
			nodeList = checkEventDao.findByCondition(" where type in( 'firewall','gateway') and bid in ("+bidBuffer.toString()+")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			checkEventDao.close();
		}
        int status = 0; //初始化状态
        boolean hasAlarm = false; //有没报警的
        if(nodeList!=null&&nodeList.size()>0){
        	for(int i=0;i<nodeList.size();i++){
        		CheckEvent node = (CheckEvent)nodeList.get(i);  
        		if(node!=null){
       			    Node node_ = (Node)PollingEngine.getInstance().getNodeByID(Integer.parseInt(node.getNodeid()));
    			    hasAlarm = true;
    			    if(node_!=null&&node_.getCategory()==8){
            	    	if(node.getAlarmlevel()>status)status = node.getAlarmlevel();
             	    }
    		    }
        	}
        }
        return status; 
    }
    
	/**
	 * FTP的状态
	 * @auth 胡可磊---wxy update
	 * date 2010-01-11
	 */
    public static int getFtpStatus(String bids)
    {
		CheckEventDao checkEventDao = new CheckEventDao();
		List nodeList = null;
		StringBuffer bidBuffer=new StringBuffer();
		if (bids != null && bids.length() > 0) {
			String[] bid=null;
			if(bids!=null){
				bid=new String[bids.split(",").length];
				bid=bids.split(",");
				for (int i = 0; i < bid.length; i++) {
					bidBuffer.append("',").append(bid[i]).append(",'");
					if(i!= bid.length-1){
						bidBuffer.append(",");
					}
				}
			}
		}
		try {
			nodeList = checkEventDao.findByCondition(" where subtype = 'ftp' and bid in ("+bidBuffer.toString()+")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			checkEventDao.close();
		}
        int status = 0; //初始化状态
        boolean hasAlarm = false; //有没报警的
        if(nodeList!=null&&nodeList.size()>0){
        	for(int i=0;i<nodeList.size();i++){
        		CheckEvent node = (CheckEvent)nodeList.get(i);  
        		if(node!=null){
       			    Node node_ = (Node)PollingEngine.getInstance().getFtpByID(Integer.parseInt(node.getNodeid()));
    			    hasAlarm = true;
    			    if(node_!=null){
            	    	if(node.getAlarmlevel()>status)status = node.getAlarmlevel();
             	    }
    		    }
        	}
        }
        return status; 
    }
	/**
	 * 服务器的状态
	 * @auth 胡可磊----wxy update
	 * date 2010-01-11
	 */
    public static int getServerStatus(String bids)
    {
    	CheckEventDao checkEventDao = new CheckEventDao();
		List nodeList = null;
		StringBuffer bidBuffer=new StringBuffer();
		if (bids != null && bids.length() > 0) {
			String[] bid=null;
			if(bids!=null){
				bid=new String[bids.split(",").length];
				bid=bids.split(",");
				for (int i = 0; i < bid.length; i++) {
					bidBuffer.append("',").append(bid[i]).append(",'");
					if(i!= bid.length-1){
						bidBuffer.append(",");
					}
				}
			}
		}
		try {
			nodeList = checkEventDao.findByCondition(" where type = 'host' and bid in("+bidBuffer.toString()+")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			checkEventDao.close();
		}
        int status = 0; //初始化状态
        boolean hasAlarm = false; //有没报警的
        if(nodeList!=null&&nodeList.size()>0){
       	    for(int i=0;i<nodeList.size();i++)
            {
           	    CheckEvent node = (CheckEvent)nodeList.get(i);        	 
       	        if(node!=null){
          	        hasAlarm = true; 
          	    	if(node.getAlarmlevel()>status)status = node.getAlarmlevel();
           	    }
            }
        }
        return status; 
    } 
    
	/**
	 * 数据库的状态
	 * @auth 胡可磊---wxy update
	 * date 2010-01-11
	 */
    public static int getDbStatus(String bids)
    {
//    	List nodeList = PollingEngine.getInstance().getDbList();
//		boolean hasAlarm = false; // 有没报警的
//		int status = 0; // 初始化状态
//		String[] bid=null;
//		if(bids!=null){
//			bid=new String[bids.split(",").length];
//			bid=bids.split(",");
//		}
//		for (int i = 0; i < nodeList.size(); i++) {
//			Node node = (Node) nodeList.get(i);
//			String nodeBid=node.getBid();
//			if(bid!=null&&nodeBid!=null){
//				for (int j = 0; j < bid.length; j++) {
//					if(nodeBid.equals(bid[j])){
//						if (node.isAlarm()) {
//							hasAlarm = true;
//							if (node.getStatus() > status)
//								status = node.getStatus();
//						}
//						break;
//					}
//						
//				}
//			}
//			
//		}
//		return status;
    	CheckEventDao checkEventDao = new CheckEventDao();
		List nodeList = null;
		StringBuffer bidBuffer=new StringBuffer();
		if (bids != null && bids.length() > 0) {
			String[] bid=null;
			if(bids!=null){
				bid=new String[bids.split(",").length];
				bid=bids.split(",");
				for (int i = 0; i < bid.length; i++) {
					bidBuffer.append("',").append(bid[i]).append(",'");
					if(i!= bid.length-1){
						bidBuffer.append(",");
					}
				}
			}
		}
		try {
			nodeList = checkEventDao.findByCondition(" where type = 'db' and bid in("+bidBuffer.toString()+")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			checkEventDao.close();
		}
        int status = 0; //初始化状态
        boolean hasAlarm = false; //有没报警的
        if(nodeList!=null&&nodeList.size()>0){
       	    for(int i=0;i<nodeList.size();i++)
            {
           	    CheckEvent node = (CheckEvent)nodeList.get(i);        	 
       	        if(node!=null){
          	        hasAlarm = true; 
          	    	if(node.getAlarmlevel()>status)status = node.getAlarmlevel();
           	    }
            }
        }
       
        return status; 
    }
    
	/**
	 * 中间件的状态
	 * @auth 胡可磊
	 * date 2010-01-11
	 */
//    public static int getMiddleStatus()
//    {
//         List cicsList = PollingEngine.getInstance().getCicsList();
//         List dominoList = PollingEngine.getInstance().getDominoList();
//         List iisList = PollingEngine.getInstance().getIisList();
//         List mqList = PollingEngine.getInstance().getMqList();
//         List tomcatList = PollingEngine.getInstance().getTomcatList();
//         List wasList = PollingEngine.getInstance().getWasList();
//         List weblogicList = PollingEngine.getInstance().getWeblogicList();
//         
//         boolean hasDown = false; //有没关机的
//         boolean hasAlarm = false; //有没报警的
//         int status = 0; //初始化状态
//         
//         for(int i=0;i<cicsList.size();i++)
//         {
//        	 Node node = (Node)cicsList.get(i);
//        	    if(node.isAlarm()){
//           	    	hasAlarm = true; 
//           	    	if(node.getStatus()>status)status = node.getStatus();
//            	}      	 
//         }
//         for(int i=0;i<dominoList.size();i++)
//         {
//        	 Node node = (Node)dominoList.get(i);
//        	    if(node.isAlarm()){
//           	    	hasAlarm = true; 
//           	    	if(node.getStatus()>status)status = node.getStatus();
//            	}      	 
//         }
//    	 for(int i=0;i<iisList.size();i++)
//         {
//        	 Node node = (Node)iisList.get(i);
//        	    if(node.isAlarm()){
//           	    	hasAlarm = true; 
//           	    	if(node.getStatus()>status)status = node.getStatus();
//            	}      	 
//         }
//    	 for(int i=0;i<mqList.size();i++)
//         {
//        	 Node node = (Node)mqList.get(i);
//        	    if(node.isAlarm()){
//           	    	hasAlarm = true; 
//           	    	if(node.getStatus()>status)status = node.getStatus();
//            	}      	 
//         }
//    	 for(int i=0;i<tomcatList.size();i++)
//         {
//        	 Node node = (Node)tomcatList.get(i);
//        	    if(node.isAlarm()){
//           	    	hasAlarm = true; 
//           	    	if(node.getStatus()>status)status = node.getStatus();
//            	}      	 
//         }
//         for(int i=0;i<wasList.size();i++)
//         {
//        	 Node node = (Node)wasList.get(i);
//        	    if(node.isAlarm()){
//           	    	hasAlarm = true; 
//           	    	if(node.getStatus()>status)status = node.getStatus();
//            	}      	 
//         }
//    	 for(int i=0;i<weblogicList.size();i++)
//         {
//        	 Node node = (Node)weblogicList.get(i);
//        	    if(node.isAlarm()){
//           	    	hasAlarm = true; 
//           	    	if(node.getStatus()>status)status = node.getStatus();
//            	}      	 
//         }
//         return status;
//    }
    public static int getMiddleStatus(String bids){
    	CheckEventDao checkEventDao = new CheckEventDao();
        List nodeList = null;
        StringBuffer bidBuffer=new StringBuffer();
		if (bids != null && bids.length() > 0) {
			String[] bid=null;
			if(bids!=null){
				bid=new String[bids.split(",").length];
				bid=bids.split(",");
				for (int i = 0; i < bid.length; i++) {
					bidBuffer.append("',").append(bid[i]).append(",'");
					if(i!= bid.length-1){
						bidBuffer.append(",");
					}
				}
			}
		}
		try {
			nodeList = checkEventDao.findByCondition(" where type = 'middleware' and bid in("+bidBuffer.toString()+")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			checkEventDao.close();
		}
        int status = 0; //初始化状态
        boolean hasAlarm = false; //有没报警的
        if(nodeList!=null&&nodeList.size()>0){
       	    for(int i=0;i<nodeList.size();i++)
            {
           	    CheckEvent node = (CheckEvent)nodeList.get(i);        	 
       	        if(node!=null){
          	        hasAlarm = true; 
          	    	if(node.getAlarmlevel()>status)status = node.getAlarmlevel();
           	    }
            }
        }
        return status; 
    }
	/**
	 * 服务的状态
	 * @auth 胡可磊
	 * date 2010-01-11 
	 */
//    public static int getServiceStatus()
//    {
//         List ftpList = PollingEngine.getInstance().getFtpList();
//         List mailList = PollingEngine.getInstance().getMailList();
//         List socketList = PollingEngine.getInstance().getSocketList();
//         List webList = PollingEngine.getInstance().getWebList();
//         
//         boolean hasDown = false; //有没关机的
//         boolean hasAlarm = false; //有没报警的
//         int status = 0; //初始化状态
//         
//         for(int i=0;i<ftpList.size();i++)
//         {
//        	 Node node = (Node)ftpList.get(i);
//        	    if(node.isAlarm()){
//           	    	hasAlarm = true; 
//           	    	if(node.getStatus()>status)status = node.getStatus();
//            	}      	 
//         }
//         for(int i=0;i<mailList.size();i++)
//         {
//        	 Node node = (Node)mailList.get(i);
//        	    if(node.isAlarm()){
//           	    	hasAlarm = true; 
//           	    	if(node.getStatus()>status)status = node.getStatus();
//            	}      	 
//         }
//    	 for(int i=0;i<socketList.size();i++)
//         {
//        	 Node node = (Node)socketList.get(i);
//        	    if(node.isAlarm()){
//           	    	hasAlarm = true; 
//           	    	if(node.getStatus()>status)status = node.getStatus();
//            	}      	 
//         }
//    	 for(int i=0;i<webList.size();i++)
//         {
//        	 Node node = (Node)webList.get(i);
//        	    if(node.isAlarm()){
//           	    	hasAlarm = true; 
//           	    	if(node.getStatus()>status)status = node.getStatus();
//            	}      	 
//         }
//         return status;
//    }
    public static int getServiceStatus(String bids){
    	CheckEventDao checkEventDao = new CheckEventDao();
        List nodeList = null;
        StringBuffer bidBuffer=new StringBuffer();
		if (bids != null && bids.length() > 0) {
			String[] bid=null;
			if(bids!=null){
				bid=new String[bids.split(",").length];
				bid=bids.split(",");
				for (int i = 0; i < bid.length; i++) {
					bidBuffer.append("',").append(bid[i]).append(",'");
					if(i!= bid.length-1){
						bidBuffer.append(",");
					}
				}
			}
		}
		try {
			nodeList = checkEventDao.findByCondition(" where type = 'service' and bid in("+bidBuffer.toString()+")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			checkEventDao.close();
		}
        int status = 0; //初始化状态
        boolean hasAlarm = false; //有没报警的
        if(nodeList!=null&&nodeList.size()>0){
       	    for(int i=0;i<nodeList.size();i++)
            {
           	    CheckEvent node = (CheckEvent)nodeList.get(i);        	 
       	        if(node!=null){
          	        hasAlarm = true; 
          	    	if(node.getAlarmlevel()>status)status = node.getAlarmlevel();
           	    }
            }
        }
        return status; 
    }
    /**
     * 应用状态
     */
    public static int getAppStatus()
    {    	
        List nodeList = PollingEngine.getInstance().getNodeList();
        boolean hasDown = false; //有没关机的
        boolean hasAlarm = false; //有没报警的
        for(int i=0;i<nodeList.size();i++)
        {
            Node node = (Node)nodeList.get(i);
       	    if(node.getCategory()<50) continue;

       	    if(node.isAlarm()){
       	    	hasAlarm = true; 
       	    	break;
        	}
       	    
//       	    if(node.getStatus()==3)
//       	    {
//       		    hasDown = true;
//       		    break;
//       	     }
        }
        if(hasDown) //取级别最高的
       	   return 3;
        else if(hasAlarm)
       	   return 2;
        else
       	   return 1; 
    }   
    
    public static String getTableItem(String id,String ip,double value)
    {
		StringBuffer sb = new StringBuffer(500);
    	sb.append("<tr><td align=left width=70% height=20><a href=\"#\" onclick=\"javascript:window.open('");
    	sb.append("/afunms/detail/dispatcher.jsp?id="); 
    	sb.append(id);
    	sb.append("','window', 'toolbar=no,height=700,width=820,scrollbars=yes,center=yes,screenY=0')\">");
    	sb.append(ip); 
    	sb.append("</a></td><td align=right width='30%'>&nbsp;");
    	sb.append(value);
    	sb.append("%</td></tr>");

    	return sb.toString();
    }
}