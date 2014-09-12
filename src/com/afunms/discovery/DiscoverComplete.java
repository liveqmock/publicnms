/**
 * <p>Description:Discover Complete</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-17
 */

package com.afunms.discovery;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


import com.afunms.initialize.*;
import com.afunms.polling.*;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.common.util.*;


/**
 * 发现完成后要做3件事:
 * 1.数据入库,生成拓扑图XML;
 * 2.释放发现程序
 * 3.初始化轮询程序
 */
public class DiscoverComplete
{
	private DiscoverDataHelper helper = new DiscoverDataHelper();
	private List<NodeToNodeLink> links = new ArrayList<NodeToNodeLink>();

	private List<MacToNodeLink> maclinks = new ArrayList<MacToNodeLink>();

	private HashMap<Integer,Host> bridgeNodes = new HashMap<Integer,Host>();

	private List<Host> routerNodes = new ArrayList<Host>();

	private List<Host> cdpNodes = new ArrayList<Host>();
	
	private List<Host> ndpNodes = new ArrayList<Host>();
	
	private List<Host> atNodes = new ArrayList<Host>();

	// this is the list of mac address just parsed by discovery process
	private List<String> macsParsed = new ArrayList<String>();
	
	// this is the list of mac address excluded by discovery process
	private List<String> macsExcluded = new ArrayList<String>();

	// this is tha list of atinterfaces for which to be discovery link
	// here there aren't the bridge identifier becouse they should be discovered
	// by main processes. This is used by addlinks method.
	private Map<String,List<AtInterface>> macToAtinterface = new HashMap<String,List<AtInterface>>();
   
   
   
   
   
   public void completed(boolean discoverOk)
   {	   
//	   if(!discoverOk){
//		   unloadDiscover();
//		   return;
//	   }
//	   analyseLinks();
	   try{
		   analyseTopoLinks();
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   
	   helper.memory2DB();
	   helper.DB2NetworkXml();
	   helper.DB2NetworkVlanXml();
	   helper.DB2ServerXml();
	   
	   updateSystemXml();
	   createHtml();
	   unloadDiscover();
	   SysLogger.info("----发现程序卸载完成----");
	   PollingEngine.getInstance().doPolling();
   }	  
   
   /**
    * 
    */
   private void unloadDiscover()
   {
	   SysLogger.info("----发现程序卸载----");

	   DiscoverEngine.getInstance().unload();
	   DiscoverResource.getInstance().unload();	
	   //需要添加一个把全部的发现线程destroy掉,防止内存溢出	   
	   List threadList = DiscoverEngine.getInstance().getThreadList();
	   if(threadList != null && threadList.size()>0){
		   for(int i=0;i<threadList.size();i++){
	   			BaseThread bt = (BaseThread)threadList.get(i);
	   			bt.setCompleted(true);
	   			bt = null;  
	   		}
	   }
   }
   
   private void createHtml()
   {
	   DiscoverMonitor monitor = DiscoverMonitor.getInstance();
	   
	   StringBuffer htmlFile = new StringBuffer(1000);
	   htmlFile.append("<html><head><title>");
	   htmlFile.append(SysUtil.getCurrentDate());
	   htmlFile.append("</title></head>");
	   htmlFile.append("<body bgcolor='#9FB0C4'>\n");
	   htmlFile.append("<table width='500' align='center'>\n");
	   htmlFile.append("<tr><td align='center'><font color='blue'><b>发现进程监视</b><input type=button class='button' value='停止发现' onclick='/afunms/user.do?action=logout'></font></td></tr>\n");
	   htmlFile.append("<tr><td valign='top' align='center'>\n");
	   htmlFile.append("<table width='100%' border=1 cellspacing=0 cellpadding=0 bordercolorlight='#000000' bordercolordark='#FFFFFF'>");
	   htmlFile.append("<tr><td>开始时间</td><td>");
	   htmlFile.append(monitor.getStartTime());
	   htmlFile.append("</td></tr>");	   
	   htmlFile.append("<tr><td>结束时间</td><td>");
	   htmlFile.append(monitor.getEndTime());
	   htmlFile.append("</td></tr>");	   
	   htmlFile.append("<tr><td>已经耗时</td><td>");
	   htmlFile.append(monitor.getElapseTime());
	   htmlFile.append("</td></tr></table></td></tr>");	   
	   htmlFile.append("<tr><td>\n");
	   htmlFile.append("<table width='100%' border=1 cellspacing=0 cellpadding=0 bordercolorlight='#000000' bordercolordark='#FFFFFF'>");
	   htmlFile.append("<tr bgcolor='#D4E1D5'><td>&nbsp;</td><td><b>总数</b></td></tr>\n");
	   htmlFile.append("<tr><td>设备</td><td>");	   
	   htmlFile.append(monitor.getHostTotal());
	   htmlFile.append("</td></tr>");
	   htmlFile.append("<tr><td>子网</td><td>");
	   htmlFile.append(monitor.getSubNetTotal());
	   htmlFile.append("</tr></table></td></tr>");
	   htmlFile.append("<tr><td align='center'><font color='blue'><b><br>详细</b></font></td></tr><tr><td>");
	   htmlFile.append(monitor.getResultTable());
	   htmlFile.append("</td></tr></table></body></html>");
       FileOutputStream fos = null;
       OutputStreamWriter osw = null;

	   try
	   {
          fos = new FileOutputStream(ResourceCenter.getInstance().getSysPath()+ "topology\\discover\\discover.html");
          osw = new OutputStreamWriter(fos, "GB2312");
          osw.write(htmlFile.toString());
	   }
       catch(Exception e)
       {
    	  SysLogger.error("DiscoverDataHelper.createHtml()",e);
       }
       finally
       {
         try
         {
            osw.close();
         }
         catch(Exception ee)
         {}     
      }   	   	   
   }
   
   private void updateSystemXml()
   {
	   SAXBuilder builder = null;
	   Document doc = null;
	   try
	   {		
		   String fullPath = ResourceCenter.getInstance().getSysPath() + "WEB-INF\\classes\\system-config.xml";			
		   FileInputStream fis = new FileInputStream(fullPath);
		
		   builder = new SAXBuilder();
		   doc = builder.build(fis);
		   Element ele = doc.getRootElement().getChild("has_discoverd");
		   ele.setText("true");
		   
		   Format format = Format.getCompactFormat();
		   format.setEncoding("UTF-8");
		   format.setIndent("	");
		   XMLOutputter serializer = new XMLOutputter(format);
		   FileOutputStream fos = new FileOutputStream(fullPath);		  
		   serializer.output(doc, fos);
		   fos.close();
	   }
	   catch(Exception e)
	   {
		   
		   SysLogger.error("Error in DiscoverComplete.updateSystemXml()",e);
	   }      
   }
   
   /**
    * 找出所有交换机间可能有的链路
    * 2007.3.12衡水信用社
    */
   private void analyseLinks()
   {
	   List list = DiscoverEngine.getInstance().getHostList();

	   int loop = 0;
	   Host firstSwitch = null;
	   for(int i=0;i<list.size();i++)
	   {
		   Host host = (Host)list.get(i); 		   
		   if(host.getCategory()==2 || host.getCategory()==3)
		   {
			   loop++;
			   if(loop==1) firstSwitch = host;
			   else firstSwitch.addSwitchId(host.getId());			   
		   } 
	   }
	   if(firstSwitch!=null)
	   {
 	       LinkProber linkProber = new LinkProber(firstSwitch);
	       List allLinks = linkProber.confirmLinks();
	       DiscoverEngine.getInstance().addLinks(allLinks);
	   }
   }
   
   private synchronized void analyseTopoLinks()
   {	   
	   Iterator<Host> ite = null;
	   List hostlist = DiscoverEngine.getInstance().getHostList();
	   if(hostlist != null && hostlist.size()>0){
		   for(int i=0;i<hostlist.size();i++){
			   try{
			   Host host = (Host)hostlist.get(i);
			   SysLogger.info(host.toString());
			   if (host == null) {
				   SysLogger.error("节点为空值，继续进行下一步操作");
				   continue;
			   }
			   //过滤掉非网络设备
			   if(host.getCategory() != 1 && host.getCategory() != 2 && host.getCategory() != 3 && host.getCategory() != 7)continue;
			   if(host.getBridgestpList() != null && host.getBridgestpList().size()>0){
				   bridgeNodes.put(new Integer(host.getId()), host);
			   }
			   if(host.getCdpList() != null && host.getCdpList().size()>0){
				   cdpNodes.add(host);
			   }
			   if(host.getNdpHash() != null && host.getNdpHash().size()>0){
				   ndpNodes.add(host);
			   }
			   if(host.getRouteList() != null && host.getRouteList().size()>0){
				   routerNodes.add(host);
			   }
			   
				if (host.getAtInterfaces() != null && host.getAtInterfaces().size()>0) {
					List atInterfaces = host.getAtInterfaces();
					//将HOST的ID补充进去
					List atList = new ArrayList();
					for(int k=0;k<atInterfaces.size();k++){
						AtInterface at = (AtInterface)atInterfaces.get(k);
						AtInterface _at = new AtInterface(host.getId(),at.getIpAddress(), at.getMacAddress(),at.getIfindex());
						atList.add(_at);
					}
					if(atList != null && atList.size()>0)
						host.setAtInterfaces(atList);
					atNodes.add(host);
				}
		   }catch(Exception e){
			   e.printStackTrace();
			   SysLogger.error(e.getMessage());
		   }
		   }
	   }
	   
	   SysLogger.info("运行: 用atNodes to populate macToAtinterface");

		ite = atNodes.iterator();
		while (ite.hasNext()) {
			Host host = ite.next();
			List atInterfaces = host.getAtInterfaces();
			if(atInterfaces != null && atInterfaces.size()>0){
				for(int k=0;k<atInterfaces.size();k++){
					try{
					AtInterface at = (AtInterface)atInterfaces.get(k);
					int nodeid = host.getId();
					String ipaddr = at.getIpAddress();
					String macAddress = at.getMacAddress();
					SysLogger.info("解析 at Interface nodeid/ipaddr/macaddr: " + nodeid + "/" + ipaddr +"/" + macAddress);
					//if (!Linkd.getInstance().isInterfaceInPackage(at.getIpAddress(), getPackageName())) {
					//	if (log().isInfoEnabled()) 
					//		log()
					//		.info("run: at interface: " + ipaddr+ " does not belong to package: " + getPackageName()+ "! Not adding to discoverable atinterface.");
					//	macsExcluded.add(macAddress);
					//	continue;
					//}
					if (isMacIdentifierOfBridgeNode(macAddress)) {
						SysLogger.info("运行: at interface "+ macAddress+ " belongs to bridge node! Not adding to discoverable atinterface.");
						macsExcluded.add(macAddress);
						continue;
					}
					List<AtInterface> ats = macToAtinterface.get(macAddress);
					if (ats == null) ats = new ArrayList<AtInterface>();
					SysLogger.info("parseAtNodes: Adding to discoverable atinterface.");
					ats.add(at);
					macToAtinterface.put(macAddress, ats);
					SysLogger.info("parseAtNodes: mac:" + macAddress + " has now atinterface reference: " + ats.size());
					   }catch(Exception e){
						   e.printStackTrace();
						   SysLogger.error(e.getMessage());
					   }
				}
			}		
		}

		SysLogger.info("运行: end populate macToAtinterface");

		/*
		//now perform operation to complete
		if (enableDownloadDiscovery) {
			if (log().isInfoEnabled())
				log().info("run: get further unknown mac address snmp bridge table info");
			snmpParseBridgeNodes();
		} else {
			if (log().isInfoEnabled())
				log().info("run: skipping get further unknown mac address snmp bridge table info");
		}
		*/

		// First of all use quick methods to get backbone ports for speeding
		// up the link discovery
		SysLogger.info("运行: finding links among nodes using Cisco Discovery Protocol");
	
	   
	   
	   SysLogger.info("利用Cisco Discovery Protocol发现节点间的连接");
	   // Try Cisco Discovery Protocol to found link among all nodes
	   // Add CDP info for backbones ports

		ite = cdpNodes.iterator();
		while (ite.hasNext()) {
			try{
			Host host = ite.next();
			int curCdpNodeId = host.getId();
			String curCdpIpAddr = host.getAdminIp();
			List executedPort = new ArrayList();
			Iterator<CdpCachEntryInterface> sub_ite = host.getCdpList().iterator();
			while (sub_ite.hasNext()) {
				try{
				CdpCachEntryInterface cdpIface = sub_ite.next();
				//SysLogger.info(host.getIpAddress()+" remoteip:"+cdpIface.getIp()+" remoteDesc:"+cdpIface.getPortdesc());
				String targetIpAddr = cdpIface.getIp();
				//判断是否是已经存在在host列表里的IP
				Host targetHost = DiscoverEngine.getInstance().getHostByAliasIP(targetIpAddr);
				if( targetHost == null){
					SysLogger.info("IP地址"+targetIpAddr+"不在已发现的网络设备里，跳过");
					continue;
				}

				int targetCdpNodeId = targetHost.getId();
				if (targetCdpNodeId == -1 || targetCdpNodeId == 0) {
					SysLogger.info("没发现网络设备IP "+targetHost.getIpAddress()+"的ID，跳过");
					continue;
				}
				if (targetCdpNodeId == curCdpNodeId) {
					SysLogger.info("运行: 该IP为自身IP "+ targetIpAddr+ " 跳过");
					continue;
				}
				
				//获取cdpIfIndex
				//int cdpIfIndex = Integer.parseInt(cdpIface.getIfindex());
				int cdpIfIndex = -1;
				if(targetHost.getCdpList() != null && targetHost.getCdpList().size()>0){
					Iterator<CdpCachEntryInterface> target_ite = targetHost.getCdpList().iterator();
					while (target_ite.hasNext()) {
						CdpCachEntryInterface targetcdpIface = target_ite.next();
						//SysLogger.info(host.getIpAddress()+" startip:"+targetcdpIface.getIp()+" startDesc:"+targetcdpIface.getPortdesc());
						if(host.getAliasIPs().contains(targetcdpIface.getIp())){
							//需要加如当前PORTDESC是否已经被处理过的条件
							if(executedPort.contains(targetcdpIface.getPortdesc()))continue;
							if(host.getIfEntityByDesc(targetcdpIface.getPortdesc()) != null){
								cdpIfIndex = Integer.parseInt(host.getIfEntityByDesc(targetcdpIface.getPortdesc()).getIndex());
								executedPort.add(targetcdpIface.getPortdesc());
								break;
							}	
						}
					}
				}
				if (cdpIfIndex <= 0) {
					//用逻辑端口代替
					cdpIfIndex = Integer.parseInt(host.getIfEntityByIP(host.getIpAddress()).getIndex());
					SysLogger.info("不是合法的CDP IfIndex，用逻辑端口代替");
					//continue;
				}else{
					SysLogger.info("发现合法的 CDP ifindex " + cdpIfIndex);
				}
				
				SysLogger.info("运行: 发现 nodeid/CDP 目标IP: " + targetCdpNodeId+ ":"+ targetIpAddr);


				int cdpDestIfindex = -1;
				if(targetHost.getIfEntityByDesc(cdpIface.getPortdesc()) != null){
					cdpDestIfindex = Integer.parseInt(targetHost.getIfEntityByDesc(cdpIface.getPortdesc()).getIndex());
					if(cdpDestIfindex < 0){
						SysLogger.info("运行：不合法的CDP destination IfIndex "+cdpDestIfindex+". 跳过");
						continue;
					}
				}else{
					SysLogger.info("运行：不合法的CDP destination. 跳过");
					continue;
				}
				SysLogger.info("运行： 发现 CDP target ifindex " + cdpDestIfindex);

				SysLogger.info("运行: 解析 CDP link: nodeid=" + curCdpNodeId
							+ " ifindex=" + cdpIfIndex + " nodeparentid="
							+ targetCdpNodeId + " parentifindex="
							+ cdpDestIfindex);

				
				boolean add = false;
				/*
				if (host.getBridgestpList()!=null && host.getBridgestpList().size()>0 
						&& targetHost.getBridgestpList() != null && targetHost.getBridgestpList().size()>0) {
					add = parseCdpLinkOn(host, cdpIfIndex,targetHost, cdpDestIfindex);
					SysLogger.info("运行：两个节点都是STP节点！添加ING："+add);
				} else if (host.getBridgestpList()!=null && host.getBridgestpList().size()>0) {
					SysLogger.info("运行: source node is bridge node, target node is not bridge node! Adding: " + add);
					add = parseCdpLinkOn(host,cdpIfIndex,targetCdpNodeId);
				} else if (isBridgeNode(targetCdpNodeId)) {
					SysLogger.info("运行: source node is not bridge node, target node is bridge node! Adding: " + add);
					Host targetNode = bridgeNodes.get(new Integer(targetCdpNodeId));
					add = parseCdpLinkOn(targetNode,cdpDestIfindex,curCdpNodeId);
				} else {
					SysLogger.info("运行: no node is bridge node! Adding CDP link");
						add = true;
				}
				*/
				// now add the cdp link
				SysLogger.info("运行: no node is bridge node! Adding CDP link");
				add = true;
				if (add) {
					NodeToNodeLink lk = new NodeToNodeLink(targetCdpNodeId,cdpDestIfindex);
					lk.setFindtype(SystemConstant.ISCDP);
					lk.setNodeparentid(curCdpNodeId);
					lk.setParentifindex(cdpIfIndex);
					addNodetoNodeLink(lk);
					SysLogger.info("运行: CDP link added: " + lk.toString());
				}
				}catch(Exception e){
					e.printStackTrace();
					SysLogger.error(e.getMessage());
				}
			}
			}catch(Exception e){
				e.printStackTrace();
				SysLogger.error(e.getMessage());
			}
		}

		   SysLogger.info("利用HUAWEI 的 Network Discovery Protocol发现节点间的连接");
		   // Try Cisco Discovery Protocol to found link among all nodes
		   // Add CDP info for backbones ports

			ite = ndpNodes.iterator();
			while (ite.hasNext()) {
				try{
				Host host = ite.next();
				int curNdpNodeId = host.getId();
				String curNdpIpAddr = host.getAdminIp();
				if(host.getNdpHash() != null && host.getNdpHash().size()>0){
				Iterator<String> sub_ite = host.getNdpHash().keySet().iterator();
				while (sub_ite.hasNext()) {
					try{
					String endndpMac = sub_ite.next();
					String endndpDescr = (String)host.getNdpHash().get(endndpMac);
					if("10.10.0.6".equalsIgnoreCase(host.getIpAddress())||"10.10.0.38".equalsIgnoreCase(host.getIpAddress())){
						SysLogger.info("--------------------------------");
						SysLogger.info(host.getIpAddress()+" endndpMac:"+endndpMac+" endndpDescr:"+endndpDescr+"  存在这样的NDP数据");
						SysLogger.info("--------------------------------");							
					}
					Host endNode = getNodeFromMacIdentifierOfNdpNode(endndpMac);
					if(endNode == null){
						SysLogger.info("--------------------------------");
						SysLogger.info(host.getIpAddress()+" endndpMac:"+endndpMac+" endndpDescr:"+endndpDescr+"  找不到MAC地址");
						SysLogger.info("--------------------------------");
						
						SysLogger.info("找不到MAC地址"+endndpMac+",在已发现的网络设备里，跳过");
						continue;
					}
					IfEntity endIfEntity = endNode.getIfEntityByDesc(endndpDescr);
					IfEntity startIfEntity = null;
					if(endIfEntity == null){
						if("10.10.0.6".equalsIgnoreCase(host.getIpAddress())||"10.10.0.38".equalsIgnoreCase(host.getIpAddress())){
							SysLogger.info("--------------------------------");
							SysLogger.info(endNode.getIpAddress()+" endndpMac:"+endndpMac+" endndpDescr:"+endndpDescr+" 找不到端口@@@ :");
							SysLogger.info("--------------------------------");							
						}
						SysLogger.info("找不到端口描述为"+endndpDescr+",在已发现的网络设备里，跳过");
						//continue;
					}
					//寻找开始端的连接
					//默认情况下,endNode的NdpHash不为空
					Hashtable endNodeNdpHash = endNode.getNdpHash();
					if(endNodeNdpHash == null)endNodeNdpHash = new Hashtable();
					if(host.getMac() == null)continue;
					
					if(endNodeNdpHash != null && !endNodeNdpHash.isEmpty()){
			    		Iterator<String> iterator = endNodeNdpHash.keySet().iterator();
			    		while(iterator.hasNext()){
			    			String MAC = iterator.next();
			    			String PORTDESC = (String)endNodeNdpHash.get(MAC);
			    			SysLogger.info(endNode.getIpAddress()+" #### MAC:"+MAC+"     PORT:"+PORTDESC+" # hostMAC:"+host.getMac());
			    		}
					}
					
					if(endNodeNdpHash.containsKey(host.getMac())){
						//存在该IP

						String ndpDescr = (String)endNodeNdpHash.get(host.getMac());
						startIfEntity = host.getIfEntityByDesc(ndpDescr);
						if("10.10.0.6".equalsIgnoreCase(host.getIpAddress())||"10.10.0.38".equalsIgnoreCase(host.getIpAddress())){
							SysLogger.info("--------------------------------");
							SysLogger.info("endndpMac:"+endndpMac+" endndpDescr:"+endndpDescr+" startndpMac:"+host.getMac()+"  startndpDescr:"+ndpDescr);
							SysLogger.info("--------------------------------");							
						}
//						SysLogger.info("--------------------------------");
//						SysLogger.info("endndpMac:"+endndpMac+" endndpDescr:"+endndpDescr+" startndpMac:"+host.getMac()+"  startndpDescr:"+ndpDescr);
//						SysLogger.info("--------------------------------");
					}
					
					if(startIfEntity == null){
						if("10.10.0.6".equalsIgnoreCase(host.getIpAddress())||"10.10.0.38".equalsIgnoreCase(host.getIpAddress())){
							SysLogger.info("--------------------------------");
							SysLogger.info(host.getIpAddress()+" endndpMac:"+endndpMac+" endndpDescr:"+endndpDescr+" startIfEntity is null");
							SysLogger.info("--------------------------------");							
						}
						/*
						//从FDB表里找
						List fdbList = host.getFdbList();
						if(fdbList != null && fdbList.size()>0){
							int flag = 0;
							for(int k=0;k<fdbList.size();k++){
								String[] item = new String[2];
				    			 item = (String[])fdbList.get(k);
				    			 if(item[1] != null && item[1].trim().length()>0){
				    				 if(item[1].equalsIgnoreCase(endNode.getMac())){
				    					 startIfEntity = host.getIfEntityByIndex(item[0]);
				    					 flag = 1;
				    					 break;
				    				 }
				    			 }
				    			 //ipmac.setIfindex(item[0]);
				    			 //ipmac.setMac(item[1]);
							}
							if(flag == 0){
								//通过FDB判断,没有相关连接,用VLAN连接替代
								startIfEntity = host.getIfEntityByIP(host.getIpAddress());
								if(startIfEntity == null)continue;
							}
							
						}else{
							startIfEntity = host.getIfEntityByIP(host.getIpAddress());
							if(startIfEntity == null)continue;
						}
						*/
						startIfEntity = host.getIfEntityByIP(host.getIpAddress());
						
						if(startIfEntity == null){
							continue;
						}
						
					}
					if(endIfEntity == null){
						if("10.10.0.6".equalsIgnoreCase(host.getIpAddress())||"10.10.0.38".equalsIgnoreCase(host.getIpAddress())){
							SysLogger.info("--------------------------------");
							SysLogger.info(endNode.getIpAddress()+" endndpMac:"+endndpMac+" endndpDescr:"+endndpDescr+" endIfEntity is null");
							SysLogger.info("--------------------------------");							
						}
						//从FDB表里找
						/*
						List fdbList = endNode.getFdbList();
						if(fdbList != null && fdbList.size()>0){
							int flag = 0;
							for(int k=0;k<fdbList.size();k++){
								String[] item = new String[2];
				    			 item = (String[])fdbList.get(k);
				    			 if(item[1] != null && item[1].trim().length()>0){
				    				 if(item[1].equalsIgnoreCase(host.getMac())){
				    					 startIfEntity = endNode.getIfEntityByIndex(item[0]);
				    					 flag = 1;
				    					 break;
				    				 }
				    			 }
				    			 //ipmac.setIfindex(item[0]);
				    			 //ipmac.setMac(item[1]);
							}
							if(flag == 0){
								//通过FDB判断,没有相关连接,用VLAN连接替代
								endIfEntity = endNode.getIfEntityByIP(endNode.getIpAddress());
								if(endIfEntity == null)continue;
							}
							
						}else{
							endIfEntity = endNode.getIfEntityByIP(endNode.getIpAddress());
							if(endIfEntity == null)continue;
						}
						*/
						endIfEntity = endNode.getIfEntityByIP(endNode.getIpAddress());
						if(endIfEntity == null)continue;
						
						
						
					}
					if(startIfEntity != null && endIfEntity != null){
						//两个连接都存在
						if (host.getId() == endNode.getId()){
							SysLogger.info("运行: 该连接为自身, 跳过");
							continue;
						}	
					}

					SysLogger.info("运行: 发现 nodeid/NDP 目标IP: " + endNode.getId()+ ":"+ endNode.getIpAddress());

					SysLogger.info("运行: 解析 NDP link: nodeid=" + host.getId()
								+ " ifindex=" + startIfEntity.getIndex() + " nodeparentid="
								+ endNode.getId() + " parentifindex="
								+ endIfEntity.getIndex());

					
					boolean add = false;
					/*
					if (host.getBridgestpList()!=null && host.getBridgestpList().size()>0 
							&& targetHost.getBridgestpList() != null && targetHost.getBridgestpList().size()>0) {
						add = parseCdpLinkOn(host, cdpIfIndex,targetHost, cdpDestIfindex);
						SysLogger.info("运行：两个节点都是STP节点！添加ING："+add);
					} else if (host.getBridgestpList()!=null && host.getBridgestpList().size()>0) {
						SysLogger.info("运行: source node is bridge node, target node is not bridge node! Adding: " + add);
						add = parseCdpLinkOn(host,cdpIfIndex,targetCdpNodeId);
					} else if (isBridgeNode(targetCdpNodeId)) {
						SysLogger.info("运行: source node is not bridge node, target node is bridge node! Adding: " + add);
						Host targetNode = bridgeNodes.get(new Integer(targetCdpNodeId));
						add = parseCdpLinkOn(targetNode,cdpDestIfindex,curCdpNodeId);
					} else {
						SysLogger.info("运行: no node is bridge node! Adding CDP link");
							add = true;
					}
					*/
					// now add the cdp link
					SysLogger.info("运行: no node is bridge node! Adding NDP link");
					add = true;
					if (add) {
						NodeToNodeLink lk = new NodeToNodeLink(endNode.getId(),Integer.parseInt(endIfEntity.getIndex()));
						lk.setFindtype(SystemConstant.ISNDP);
						lk.setNodeparentid(host.getId());
						lk.setParentifindex(Integer.parseInt(startIfEntity.getIndex()));
						addNodetoNodeLink(lk);
						SysLogger.info("运行: NDP link added: " + lk.toString());
					}
					   }catch(Exception e){
						   e.printStackTrace();
						   SysLogger.error(e.getMessage());
					   }
				}
				}
			   }catch(Exception e){
				   e.printStackTrace();
				   SysLogger.error(e.getMessage());
			   }
			}
		// try get backbone links between switches using STP info
		// and store information in Bridge class
		SysLogger.info("运行: try to found backbone ethernet links among bridge nodes using Spanning Tree Protocol");

		ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			try{
			Host curNode = ite.next();
			List curNodeStpList = curNode.getBridgestpList();
			if(curNodeStpList != null && curNodeStpList.size()>0){
				for(int k=0;k<curNodeStpList.size();k++){
					try{
					BridgeStpInterface bstp = (BridgeStpInterface)curNodeStpList.get(k);
					if (curNode.isBridgeIdentifier(bstp.getBridge().substring(5))) {
						SysLogger.info("运行: STP designated root is the bridge itself. Skipping");
						continue;
					}
					Host designatedNode = getNodeFromMacIdentifierOfBridgeNode(bstp.getBridge().substring(5));
					if(designatedNode == null)continue;
					// if port is a backbone port continue
					SysLogger.info(curNode.getIpAddress()+"   Port "+bstp.getPort());
					if (curNode.isBackBoneBridgePort(Integer.parseInt(bstp.getPort()))) {
						SysLogger.info("运行: bridge port " + bstp.getPort()+ " already found .... Skipping");
						continue;
					}
					String stpPortDesignatedPort = bstp.getBridgeport();
					stpPortDesignatedPort = stpPortDesignatedPort.replace(":", "");
					SysLogger.info(curNode.getIpAddress()+"   designatedbridgeport "+Integer.parseInt(stpPortDesignatedPort.substring(1), 16));
					int designatedbridgeport = Integer.parseInt(
							stpPortDesignatedPort.substring(1), 16);
					
					int designatedifindex = -1;
					if(designatedNode.getIfEntityByPort(designatedbridgeport+"") != null){
						designatedifindex = Integer.parseInt(designatedNode.getIfEntityByPort(designatedbridgeport+"").getIndex());
					}else{
						SysLogger.info("运行: got invalid ifindex on designated node");
						continue;
					}
					
					
					if (designatedifindex == -1 || designatedifindex == 0) {
						SysLogger.info("运行: got invalid ifindex on designated node");
						continue;
					}

					SysLogger.info("run: backbone port found for node "
								+ curNode.getId() + ". Adding to bridge"
								+ bstp.getPort());

					curNode.addBackBoneBridgePorts(Integer.parseInt(bstp.getPort()));
					bridgeNodes.put(new Integer(curNode.getId()), curNode);

					SysLogger.info("run: backbone port found for node "
								+ designatedNode.getId()
								+ " .Adding to helper class bb port "
								+ " bridge port " + designatedbridgeport);

					// test if there are other bridges between this link
					// USING MAC ADDRESS FORWARDING TABLE

					if (!isNearestBridgeLink(curNode, Integer.parseInt(bstp.getPort()),
							designatedNode, designatedbridgeport)) {
						SysLogger.info("run: other bridge found between nodes. No links to save!");
						continue; // no saving info if no nodeid
					}
					int curIfIndex = Integer.parseInt(curNode.getIfEntityByPort(bstp.getPort()+"").getIndex());

					if (curIfIndex == -1 || curIfIndex == 0) {
						SysLogger.info("运行: got invalid ifindex");
						continue;
					}
					designatedNode.addBackBoneBridgePorts(designatedbridgeport);
					bridgeNodes.put(new Integer(designatedNode.getId()),designatedNode);
					
					SysLogger.info("run: adding links on bb bridge port " + designatedbridgeport);

					addLinks(getMacsOnBridgeLink(curNode,
							Integer.parseInt(bstp.getPort()), designatedNode,
							designatedbridgeport),curNode.getId(),curIfIndex);

					// writing to db using class
					// DbDAtaLinkInterfaceEntry
					NodeToNodeLink lk = new NodeToNodeLink(curNode.getId(),curIfIndex);
					lk.setFindtype(SystemConstant.ISBridge);
					lk.setNodeparentid(designatedNode.getId());
					lk.setParentifindex(designatedifindex);
					addNodetoNodeLink(lk);
				   }catch(Exception e){
					   e.printStackTrace();
					   SysLogger.error(e.getMessage());
				   }
				}
				
			}
		   }catch(Exception e){
			   e.printStackTrace();
			   SysLogger.error(e.getMessage());
		   }
		}
		
		/*
		while (ite.hasNext()) {
			Host curNode = ite.next();

			int curNodeId = curNode.getId();
			String cupIpAddr = curNode.getAdminIp();
			SysLogger.info("运行: 解析桥节点 " + curNodeId+ " ip address " + cupIpAddr);

			Iterator<Map.Entry<String, List<BridgeStpInterface>>> sub_ite = curNode.getStpInterfaces().entrySet()
					.iterator();

			SysLogger.info("运行: 解析 "+ curNode.getStpInterfaces().size() + " Vlan. ");

			while (sub_ite.hasNext()) {
				Map.Entry<String, List<BridgeStpInterface>> me = sub_ite.next();
				String vlan = (String) me.getKey();
				String curBaseBridgeAddress = curNode
						.getBridgeIdentifier(vlan);

				SysLogger.info("run: found bridge identifier "+ curBaseBridgeAddress);

				String designatedRoot = null;
				
				if (curNode.hasStpRoot(vlan)) {
					designatedRoot = curNode.getStpRoot(vlan);
				} else {
					SysLogger.info("run: desigated root bridge identifier not found. Skipping"
										+ curBaseBridgeAddress);
					continue;
				}

				if (designatedRoot.equals("0000000000000000")) {
					SysLogger.info("run: designated root is invalid. Skipping");
					continue;
				}
				// check if designated
				// bridge is it self
				// if bridge is STP root bridge itself exiting
				// searching on linkablesnmpnodes

				if (curNode.isBridgeIdentifier(designatedRoot.substring(4))) {
					SysLogger.info("run: STP designated root is the bridge itself. Skipping");
					continue;
				}

				// Now parse STP bridge port info to get designated bridge
				SysLogger.info("run: STP designated root is another bridge. " + designatedRoot + " Parsing Stp Interface");

				Iterator<BridgeStpInterface> stp_ite = me.getValue().iterator();
				while (stp_ite.hasNext()) {
					BridgeStpInterface stpIface = stp_ite
							.next();

					// the bridge port number
					int stpbridgeport = stpIface.getBridgeport();
					// if port is a backbone port continue
					if (curNode.isBackBoneBridgePort(stpbridgeport)) {
						SysLogger.info("run: bridge port " + stpbridgeport
									+ " already found .... Skipping");
						continue;
					}

					String stpPortDesignatedPort = stpIface
							.getStpPortDesignatedPort();
					String stpPortDesignatedBridge = stpIface
							.getStpPortDesignatedBridge();

					SysLogger.info("run: parsing bridge port "
								+ stpbridgeport
								+ " with stp designated bridge "
								+ stpPortDesignatedBridge
								+ " and with stp designated port "
								+ stpPortDesignatedPort);

					if (stpPortDesignatedBridge.equals("0000000000000000")) {
						SysLogger.info("run: designated bridge is invalid "
								+ stpPortDesignatedBridge);
						continue;
					}

					if (curNode.isBridgeIdentifier(stpPortDesignatedBridge
							.substring(4))) {
						SysLogger.info("run: designated bridge for port "
									+ stpbridgeport + " is bridge itself ");
						continue;
					}

					if (stpPortDesignatedPort.equals("0000")) {
						SysLogger.info("run: designated port is invalid "
								+ stpPortDesignatedPort);
						continue;
					}

					//A Port Identifier shall be encoded as two octets,
					// taken to represent an unsigned binary number. If two
					// Port
					//Identifiers are numerically compared, the lesser
					// number denotes the Port of better priority. The more
					//significant octet of a Port Identifier is a settable
					// priority component that permits the relative priority
					// of Ports
					//on the same Bridge to be managed (17.13.7 and Clause
					// 14). The less significant twelve bits is the Port
					//Number expressed as an unsigned binary number. The
					// value 0 is not used as a Port Number.
					//NOTE锟絋he number of bits that are considered to be
					// part of the Port Number (12 bits) differs from the
					// 1998 and prior
					//versions of this standard (formerly, the priority
					// component was 8 bits and the Port Number component
					// also 8 bits). This
					//change acknowledged that modern switched LAN
					// infrastructures call for increasingly large numbers
					// of Ports to be
					//supported in a single Bridge. To maintain management
					// compatibility with older implementations, the
					// priority
					//component is still considered, for management
					// purposes, to be an 8-bit value, but the values that
					// it can be set to are
					//restricted to those where the least significant 4
					// bits are zero (i.e., only the most significant 4 bits
					// are settable).
					int designatedbridgeport = Integer.parseInt(
							stpPortDesignatedPort.substring(1), 16);

					// try to see if designated bridge is linkable
					// snmp node

					Host designatedNode = getNodeFromMacIdentifierOfBridgeNode(stpPortDesignatedBridge
							.substring(4));

					if (designatedNode == null) {
						SysLogger.info("run: no nodeid found for stp bridge address "
										+ stpPortDesignatedBridge
										+ " . Nothing to save to db");
						continue; // no saving info if no nodeid
					}
					
					int designatednodeid = designatedNode.getId();

					SysLogger.info("run: found designated nodeid "
								+ designatednodeid);

					// test if there are other bridges between this link
					// USING MAC ADDRESS FORWARDING TABLE

					if (!isNearestBridgeLink(curNode, stpbridgeport,
							designatedNode, designatedbridgeport)) {
						SysLogger.info("run: other bridge found between nodes. No links to save!");
						continue; // no saving info if no nodeid
					}

					// this is a backbone port so try adding to Bridge class
					// get the ifindex on node

					int curIfIndex = curNode.getIfindex(stpbridgeport);

					if (curIfIndex == -1) {
						SysLogger.info("run: got invalid ifindex");
						continue;
					}

					int designatedifindex = designatedNode.getIfindex(designatedbridgeport);
					
					if (designatedifindex == -1) {
						SysLogger.info("run: got invalid ifindex on designated node");
						continue;
					}

					SysLogger.info("run: backbone port found for node "
								+ curNodeId + ". Adding to bridge"
								+ stpbridgeport);

					curNode.addBackBoneBridgePorts(stpbridgeport);
					bridgeNodes.put(new Integer(curNodeId), curNode);

					SysLogger.info("run: backbone port found for node "
								+ designatednodeid
								+ " .Adding to helper class bb port "
								+ " bridge port " + designatedbridgeport);

					designatedNode
							.addBackBoneBridgePorts(designatedbridgeport);
					bridgeNodes.put(new Integer(designatednodeid),
							designatedNode);
					
					SysLogger.info("run: adding links on bb bridge port " + designatedbridgeport);

					addLinks(getMacsOnBridgeLink(curNode,
							stpbridgeport, designatedNode,
							designatedbridgeport),curNodeId,curIfIndex);

					// writing to db using class
					// DbDAtaLinkInterfaceEntry
					NodeToNodeLink lk = new NodeToNodeLink(curNodeId,
							curIfIndex);
					lk.setNodeparentid(designatednodeid);
					lk.setParentifindex(designatedifindex);
					addNodetoNodeLink(lk);

				}
			}
		}
		*/

		/*
		// finding links using mac address on ports
		SysLogger.info("运行: try to found links using Mac Address Forwarding Table");

		ite = bridgeNodes.values().iterator();

		while (ite.hasNext()) {
			Host curNode = ite.next();
			int curNodeId = curNode.getId();
			SysLogger.info("运行: parsing node bridge " + curNodeId);

			Iterator<Integer> sub_ite = curNode.getPortMacs().keySet().iterator();

			while (sub_ite.hasNext()) {
				Integer intePort = sub_ite.next();
				int curBridgePort = intePort.intValue();
				SysLogger.info("运行: parsing bridge port "+ curBridgePort+ " with mac addresses "
						+ curNode.getMacAddressesOnBridgePort(curBridgePort).toString());

				if (curNode.isBackBoneBridgePort(curBridgePort)) {
					SysLogger.info("运行: 解析的是主干桥端口 "+ curBridgePort + " .... 跳过");
					continue;
				}
				
				int curIfIndex = Integer.parseInt(curNode.getIfEntityByPort(curBridgePort+"").getIndex());
				if (curIfIndex == -1) {
					SysLogger.info("运行: 得到一个不合法的 ifindex on bridge port "+ curBridgePort);
					continue;
				}
				// First get the mac addresses on bridge port

				Set<String> macs = curNode.getMacAddressesOnBridgePort(curBridgePort);

				// Then find the bridges whose mac addresses are learned on bridge port
				List<Host> bridgesOnPort = getBridgesFromMacs(macs);
				
				if (bridgesOnPort.isEmpty()) {
					
					SysLogger.info("运行: no bridge info found on port "+ curBridgePort + " .... Saving Macs");
					addLinks(macs, curNodeId, curIfIndex);
				} else {
					// a bridge mac address was found on port so you should analyze what happens
					SysLogger.info("run: bridge info found on port "+ curBridgePort + " .... Finding nearest.");
					Iterator<Host> bridge_ite = bridgesOnPort.iterator();
					// one among these bridges should be the node more close to the curnode, curport
					while (bridge_ite.hasNext()) {
						Host endNode = bridge_ite
								.next();
						
						int endNodeid = endNode.getId();
						
						int endBridgePort = getBridgePortOnEndBridge(
								curNode, endNode);
						//The bridge port should be valid! This control is not properly done
						if (endBridgePort == -1) {
							SysLogger.error("run: no valid port found on bridge nodeid "
												+ endNodeid
												+ " for node bridge identifiers nodeid "
												+ curNodeId
												+ " . .....Skipping");
							continue;
						}
						
						// Try to found a new 
						boolean isTargetNode = isNearestBridgeLink(
								curNode, curBridgePort, endNode,
								endBridgePort);
						if (!isTargetNode)
								continue;

						int endIfindex = Integer.parseInt(endNode.getIfEntityByPort(endBridgePort+"").getIndex());
						if (endIfindex == -1) {
							SysLogger.info("运行: 得到一个不合法的 ifindex o designated bridge port "
											+ endBridgePort);
							break;
						}

						SysLogger.info("运行: backbone port found for node "
									+ curNodeId + ". Adding backbone port "
									+ curBridgePort + " to bridge");

						curNode.addBackBoneBridgePorts(curBridgePort);
						bridgeNodes.put(new Integer(curNodeId), curNode);

						SysLogger.info("运行: backbone port found for node "
									+ endNodeid
									+ " .Adding to helper class bb port "
									+ " bridge port " + endBridgePort);

						endNode.addBackBoneBridgePorts(endBridgePort);
						bridgeNodes.put(new Integer(endNodeid), endNode);

						// finding links between two backbone ports
						addLinks(getMacsOnBridgeLink(curNode,
								curBridgePort, endNode, endBridgePort),curNodeId,curIfIndex);

						NodeToNodeLink lk = new NodeToNodeLink(curNodeId,
								curIfIndex);
						lk.setFindtype(SystemConstant.ISMac);
						lk.setNodeparentid(endNodeid);
						lk.setParentifindex(endIfindex);
						addNodetoNodeLink(lk);
						break;
					}
				}
			}
		}
		*/
		// fourth find inter router links,
		// this part could have several special function to get inter router
		// links, but at the moment we worked much on switches.
		// In future we can try to extend this part.
		SysLogger.info("运行: try to found  not ethernet links on Router nodes");

		List routeLinkList = DiscoverEngine.getInstance().getRouteLinkList();
		if(routeLinkList != null && routeLinkList.size()>0){
			for(int k=0;k<routeLinkList.size();k++){
				try{
				Link link = (Link)routeLinkList.get(k);
				// Saving link also when ifindex = -1 (not found)
				NodeToNodeLink lk = new NodeToNodeLink(link.getEndId(),
						Integer.parseInt(link.getEndIndex()));
				lk.setFindtype(SystemConstant.ISRouter);
				lk.setNodeparentid(link.getStartId());
				lk.setParentifindex(Integer.parseInt(link.getStartIndex()));
				SysLogger.info("添加连接: ##########################");
				SysLogger.info("添加连接: "+link.getStartIp()+" --- "+link.getEndIp());
				addNodetoNodeLink(lk);
				   }catch(Exception e){
					   e.printStackTrace();
					   SysLogger.error(e.getMessage());
				   }
			}
		}
		//将maclinklist中有连接,而在上面的CDP/NDP/STP/ROUTER计算中没有的连接加进去
		List macLinks = DiscoverEngine.getInstance().getMacLinkList();
		if(macLinks != null && macLinks.size()>0){
			for(int k=0;k<macLinks.size();k++){
				try{
				Link maclink = (Link)macLinks.get(k);
				if(!NodeToNodeLinkExist(maclink)){
					//若不存在该连接,则添加进去
					NodeToNodeLink lk = new NodeToNodeLink(maclink.getEndId(),
							Integer.parseInt(maclink.getEndIndex()));
					lk.setFindtype(SystemConstant.ISMac);
					lk.setNodeparentid(maclink.getStartId());
					lk.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
					SysLogger.info("添加连接: ##########################");
					SysLogger.info("添加连接: "+maclink.getStartIp()+" --- "+maclink.getEndIp());
					addNodetoNodeLink(lk);
				}
				   }catch(Exception e){
					   e.printStackTrace();
					   SysLogger.error(e.getMessage());
				   }
			}
		}
		
		
		//List allLinks = DiscoverEngine.getInstance().getLinkList();
		Hashtable existNode = new Hashtable();
		DiscoverEngine.getInstance().getLinkList().clear();;
		if(links != null && links.size()>0){
			for(int i=0;i<links.size();i++){
				try{
				NodeToNodeLink link = (NodeToNodeLink)links.get(i);
				SysLogger.info("连接: "+link.getNodeparentid()+" "+link.getParentifindex()+" "+link.getNodeId()+" "+link.getIfindex());
				Host startNode = DiscoverEngine.getInstance().getHostByID(link.getNodeparentid());
				Host endNode = DiscoverEngine.getInstance().getHostByID(link.getNodeId());
				IfEntity startIfEntity = startNode.getIfEntityByIndex(link.getParentifindex()+"");
				IfEntity endIfEntity = endNode.getIfEntityByIndex(link.getIfindex()+"");
				Link addlink = new Link();
				addlink.setStartId(link.getNodeparentid());
				addlink.setStartIndex(link.getParentifindex()+"");
				addlink.setStartIp(startNode.getIpAddress());
				addlink.setStartDescr(startIfEntity.getDescr());
				addlink.setStartPort(startIfEntity.getPort());
				addlink.setStartPhysAddress(startNode.getBridgeAddress());
				
				addlink.setEndId(link.getNodeId());
				addlink.setEndIndex(link.getIfindex()+"");
				addlink.setEndIp(endNode.getIpAddress());
				addlink.setEndDescr(endIfEntity.getDescr());
				addlink.setEndPort(endIfEntity.getPort());
				addlink.setEndPhysAddress(endNode.getBridgeAddress());
				
				addlink.setAssistant(link.getAssistant());
				addlink.setFindtype(link.getFindtype());
				addlink.setLinktype(0);
				DiscoverEngine.getInstance().getLinkList().add(addlink);
				if(!existNode.containsKey(addlink.getStartId())){
					existNode.put(addlink.getStartId(), addlink.getStartId());
				}
				if(!existNode.containsKey(addlink.getEndId())){
					existNode.put(addlink.getEndId(), addlink.getEndId());
				}
				//allLinks.add(addlink);
			   }catch(Exception e){
				   e.printStackTrace();
				   SysLogger.error(e.getMessage());
			   }
			}
		}
		
		
		
		//判断是否有没有连接的Node
		//将没有产生连接的孤立的接点用逻辑连接代替
		List macLinkList = DiscoverEngine.getInstance().getMacLinkList();
		if(macLinkList != null && macLinkList.size()>0){
			for(int k=0;k<macLinkList.size();k++){
				try{
				Link maclink = (Link)macLinkList.get(k);
				if(!existNode.containsKey(maclink.getStartId()) || !existNode.containsKey(maclink.getEndId())){
					//若有个端点不在已经存在的连接列表里
					// Saving link also when ifindex = -1 (not found)
					NodeToNodeLink link = new NodeToNodeLink(maclink.getEndId(),
							Integer.parseInt(maclink.getEndIndex()));
					link.setFindtype(SystemConstant.ISMac);
					link.setNodeparentid(maclink.getStartId());
					link.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
					SysLogger.info("添加连接: ##########################");
					SysLogger.info("添加连接: "+maclink.getStartIp()+" --- "+maclink.getEndIp());
					addNodetoNodeLink(link);
					//NodeToNodeLink link = (NodeToNodeLink)links.get(i);
					SysLogger.info("连接: "+link.getNodeparentid()+" "+link.getParentifindex()+" "+link.getNodeId()+" "+link.getIfindex());
					Host startNode = DiscoverEngine.getInstance().getHostByID(link.getNodeparentid());
					Host endNode = DiscoverEngine.getInstance().getHostByID(link.getNodeId());
					IfEntity startIfEntity = startNode.getIfEntityByIndex(link.getParentifindex()+"");
					IfEntity endIfEntity = endNode.getIfEntityByIndex(link.getIfindex()+"");
					Link addlink = new Link();
					addlink.setStartId(link.getNodeparentid());
					addlink.setStartIndex(link.getParentifindex()+"");
					addlink.setStartIp(startNode.getIpAddress());
					addlink.setStartDescr(startIfEntity.getDescr());
					addlink.setStartPort(startIfEntity.getPort());
					addlink.setStartPhysAddress(startNode.getBridgeAddress());
					
					addlink.setEndId(link.getNodeId());
					addlink.setEndIndex(link.getIfindex()+"");
					addlink.setEndIp(endNode.getIpAddress());
					addlink.setEndDescr(endIfEntity.getDescr());
					addlink.setEndPort(endIfEntity.getPort());
					addlink.setEndPhysAddress(endNode.getBridgeAddress());
					
					addlink.setAssistant(link.getAssistant());
					addlink.setFindtype(link.getFindtype());
					addlink.setLinktype(0);//物理连接
					DiscoverEngine.getInstance().getLinkList().add(addlink);
					if(!existNode.containsKey(addlink.getStartId())){
						existNode.put(addlink.getStartId(), addlink.getStartId());
					}
					if(!existNode.containsKey(addlink.getEndId())){
						existNode.put(addlink.getEndId(), addlink.getEndId());
					}
				}
			   }catch(Exception e){
				   e.printStackTrace();
				   SysLogger.error(e.getMessage());
			   }
			}
		}

		//将路由连接添加进去
		if(routeLinkList != null && routeLinkList.size()>0){
			for(int k=0;k<routeLinkList.size();k++){
				try{
				Link routelink = (Link)routeLinkList.get(k);
				routelink.setLinktype(-1);//物理连接		
				DiscoverEngine.getInstance().getLinkList().add(routelink);
				   }catch(Exception e){
					   e.printStackTrace();
					   SysLogger.error(e.getMessage());
				   }
			}
		}
		//将VLAN连接添加进去
		if(macLinkList != null && macLinkList.size()>0){
			for(int k=0;k<macLinkList.size();k++){
				try{
				Link maclink = (Link)macLinkList.get(k);
				maclink.setLinktype(-1);//物理连接		
				DiscoverEngine.getInstance().getLinkList().add(maclink);
				   }catch(Exception e){
					   e.printStackTrace();
					   SysLogger.error(e.getMessage());
				   }
			}
		}
		Hashtable exitsnodelink = new Hashtable();
		List linklists = DiscoverEngine.getInstance().getLinkList();
		if(linklists != null && linklists.size()>0){
			for(int i=0;i<linklists.size();i++){
				Link link = (Link)linklists.get(i);
				if(!exitsnodelink.containsKey(link.getStartId()+"")){
					exitsnodelink.put(link.getStartId()+"", link.getStartId()+"");
				}
				if(!exitsnodelink.containsKey(link.getEndId()+"")){
					exitsnodelink.put(link.getEndId()+"", link.getEndId()+"");
				}
			}
		}
		//Iterator<Host> ite = null;
		   //List hostlist = DiscoverEngine.getInstance().getHostList();
		   if(hostlist != null && hostlist.size()>0){
			   for(int i=0;i<hostlist.size();i++){
				   Host host = (Host)hostlist.get(i);
				   //SysLogger.info(host.toString());
				   if (host == null) {
					   SysLogger.error("节点为空值，继续进行下一步操作");
					   continue;
				   }
				   //过滤掉非网络设备
				   if(host.getCategory() != 1 && host.getCategory() != 2 && host.getCategory() != 3 && host.getCategory() != 7)continue;
				   //int flag = 0;
				   if(!exitsnodelink.containsKey(host.getId()+"")){
					   //没有连接关系,需要遍历所有节点,计算连接关系
					   for(int k=0;k<hostlist.size();k++){
						   Host phost = (Host)hostlist.get(k);
						   if(host.getId() == phost.getId())continue;
						   List arplist = phost.getIpNetTable();
						   if(arplist != null && arplist.size()>0){
							   for(int m=0;m<arplist.size();m++){
								   IpAddress ipAddress = (IpAddress)arplist.get(m);
								   //SysLogger.info("$$$$ host ip: "+host.getIpAddress()+"  phost ip:"+phost.getIpAddress()+"  ipAddress:"+ipAddress.getIpAddress());
								   if(host.getIpAddress().equalsIgnoreCase(ipAddress.getIpAddress())){
									   //存在连接关系
									   IfEntity ifentity = host.getIfEntityByIP(host.getIpAddress());
									   if(ifentity == null){
										   //SysLogger.info("@@@@ IP: "+host.getIpAddress()+" 不存在接口IP  @@@");
										   continue;
									   }
									   
									   	Link addlink = new Link();
									   	
										addlink.setStartId(phost.getId());
										addlink.setStartIndex(ipAddress.getIfIndex());
										addlink.setStartIp(phost.getIpAddress());
										addlink.setStartDescr(ipAddress.getIfIndex());
										addlink.setStartPort(ipAddress.getIfIndex());
										addlink.setStartPhysAddress(ipAddress.getPhysAddress());
										
										addlink.setEndId(host.getId());
										addlink.setEndIndex(ifentity.getIndex());
										addlink.setEndIp(ifentity.getIpAddress());
										addlink.setEndDescr(ifentity.getDescr());
										addlink.setEndPort(ifentity.getPort());
										addlink.setEndPhysAddress(host.getBridgeAddress());
										
										addlink.setAssistant(0);
										addlink.setFindtype(1);
										addlink.setLinktype(0);//逻辑连接
										DiscoverEngine.getInstance().getLinkList().add(addlink);
										if(!exitsnodelink.containsKey(addlink.getStartId()+"")){
											exitsnodelink.put(addlink.getStartId()+"", addlink.getStartId()+"");
											break;
										}
										if(!exitsnodelink.containsKey(addlink.getEndId()+"")){
											exitsnodelink.put(addlink.getEndId()+"", addlink.getEndId()+"");
											break;
										}
								   }
							   }
							   
						   }
					   }
				   }
				   
				   if(!exitsnodelink.containsKey(host.getId()+"")){
					   //没有连接关系,需要遍历该IP别名计算连接关系
					   for(int k=0;k<hostlist.size();k++){
						   Host phost = (Host)hostlist.get(k);
						   if(host.getId() == phost.getId())continue;
						   List hostiplist = host.getIfEntityList();
						   if(hostiplist != null && hostiplist.size()>0){
							   for(int j=0;j<hostiplist.size();j++){
								   IfEntity ifentity = (IfEntity)hostiplist.get(j);
								   List arplist = phost.getIpNetTable();
								   if(arplist != null && arplist.size()>0){
									   for(int m=0;m<arplist.size();m++){
										   IpAddress ipAddress = (IpAddress)arplist.get(m);
										   //SysLogger.info("--$$$$ host ip: "+host.getIpAddress()+"  phost ip:"+phost.getIpAddress()+"  ipAddress:"+ipAddress.getIpAddress());
										   if(host.getIpAddress().equalsIgnoreCase(ipAddress.getIpAddress())){
											   //存在连接关系
											   IfEntity if_entity = host.getIfEntityByIP(host.getIpAddress());
											   if(if_entity == null){
												   //SysLogger.info("@@@@ IP: "+host.getIpAddress()+" 不存在接口IP  @@@");
												   continue;
											   }
											   
											   	Link addlink = new Link();
											   	
												addlink.setStartId(phost.getId());
												addlink.setStartIndex(ipAddress.getIfIndex());
												addlink.setStartIp(phost.getIpAddress());
												addlink.setStartDescr(ipAddress.getIfIndex());
												addlink.setStartPort(ipAddress.getIfIndex());
												addlink.setStartPhysAddress(ipAddress.getPhysAddress());
												
												addlink.setEndId(host.getId());
												addlink.setEndIndex(if_entity.getIndex());
												addlink.setEndIp(if_entity.getIpAddress());
												addlink.setEndDescr(if_entity.getDescr());
												addlink.setEndPort(if_entity.getPort());
												addlink.setEndPhysAddress(host.getBridgeAddress());
												
												addlink.setAssistant(0);
												addlink.setFindtype(1);
												addlink.setLinktype(0);//逻辑连接
												DiscoverEngine.getInstance().getLinkList().add(addlink);
												if(!exitsnodelink.containsKey(addlink.getStartId()+"")){
													exitsnodelink.put(addlink.getStartId()+"", addlink.getStartId()+"");
													break;
												}
												if(!exitsnodelink.containsKey(addlink.getEndId()+"")){
													exitsnodelink.put(addlink.getEndId()+"", addlink.getEndId()+"");
													break;
												}
										   }
									   }
									   
								   }
								   
							   }
						   }
						   
						   
					   }
				   }
				   
			   }
		   }
		   
//		   if(hostlist != null && hostlist.size()>0){
//			   for(int i=0;i<hostlist.size();i++){
//				   Host host = (Host)hostlist.get(i);
//				   //SysLogger.info(host.toString());
//				   if (host == null) {
//					   SysLogger.error("节点为空值，继续进行下一步操作");
//					   continue;
//				   }
//				   //过滤掉非网络设备
//				   if(host.getCategory() != 1 && host.getCategory() != 2 && host.getCategory() != 3 && host.getCategory() != 7)continue;
//				   //int flag = 0;
//				   if(!exitsnodelink.containsKey(host.getId()+"")){
//					   SysLogger.info("####不存在连接关系 ### "+host.toString());
//				   }
//			   }
//		   }
		   
		
		
		
   }
   
   
   private synchronized void analyseTopoLinks(Host thost)
   {	   
	   Iterator<Host> ite = null;
	   List hostlist = DiscoverEngine.getInstance().getHostList();
	   if(hostlist != null && hostlist.size()>0){
		   for(int i=0;i<hostlist.size();i++){
			   try{
			   Host host = (Host)hostlist.get(i);
			   SysLogger.info(host.toString());
			   if (host == null) {
				   SysLogger.error("节点为空值，继续进行下一步操作");
				   continue;
			   }
			   //过滤掉非网络设备
			   if(host.getCategory() != 1 && host.getCategory() != 2 && host.getCategory() != 3 && host.getCategory() != 7)continue;
			   if(host.getBridgestpList() != null && host.getBridgestpList().size()>0){
				   bridgeNodes.put(new Integer(host.getId()), host);
			   }
			   if(host.getCdpList() != null && host.getCdpList().size()>0){
				   cdpNodes.add(host);
			   }
			   if(host.getNdpHash() != null && host.getNdpHash().size()>0){
				   ndpNodes.add(host);
			   }
			   if(host.getRouteList() != null && host.getRouteList().size()>0){
				   routerNodes.add(host);
			   }
			   
				if (host.getAtInterfaces() != null && host.getAtInterfaces().size()>0) {
					List atInterfaces = host.getAtInterfaces();
					//将HOST的ID补充进去
					List atList = new ArrayList();
					for(int k=0;k<atInterfaces.size();k++){
						AtInterface at = (AtInterface)atInterfaces.get(k);
						AtInterface _at = new AtInterface(host.getId(),at.getIpAddress(), at.getMacAddress(),at.getIfindex());
						atList.add(_at);
					}
					if(atList != null && atList.size()>0)
						host.setAtInterfaces(atList);
					atNodes.add(host);
				}
		   }catch(Exception e){
			   e.printStackTrace();
			   SysLogger.error(e.getMessage());
		   }
		   }
	   }
	   
	   SysLogger.info("运行: 用atNodes to populate macToAtinterface");

		ite = atNodes.iterator();
		while (ite.hasNext()) {
			Host host = ite.next();
			List atInterfaces = host.getAtInterfaces();
			if(atInterfaces != null && atInterfaces.size()>0){
				for(int k=0;k<atInterfaces.size();k++){
					try{
					AtInterface at = (AtInterface)atInterfaces.get(k);
					int nodeid = host.getId();
					String ipaddr = at.getIpAddress();
					String macAddress = at.getMacAddress();
					SysLogger.info("解析 at Interface nodeid/ipaddr/macaddr: " + nodeid + "/" + ipaddr +"/" + macAddress);
					//if (!Linkd.getInstance().isInterfaceInPackage(at.getIpAddress(), getPackageName())) {
					//	if (log().isInfoEnabled()) 
					//		log()
					//		.info("run: at interface: " + ipaddr+ " does not belong to package: " + getPackageName()+ "! Not adding to discoverable atinterface.");
					//	macsExcluded.add(macAddress);
					//	continue;
					//}
					if (isMacIdentifierOfBridgeNode(macAddress)) {
						SysLogger.info("运行: at interface "+ macAddress+ " belongs to bridge node! Not adding to discoverable atinterface.");
						macsExcluded.add(macAddress);
						continue;
					}
					List<AtInterface> ats = macToAtinterface.get(macAddress);
					if (ats == null) ats = new ArrayList<AtInterface>();
					SysLogger.info("parseAtNodes: Adding to discoverable atinterface.");
					ats.add(at);
					macToAtinterface.put(macAddress, ats);
					SysLogger.info("parseAtNodes: mac:" + macAddress + " has now atinterface reference: " + ats.size());
					   }catch(Exception e){
						   e.printStackTrace();
						   SysLogger.error(e.getMessage());
					   }
				}
			}		
		}

		SysLogger.info("运行: end populate macToAtinterface");

		/*
		//now perform operation to complete
		if (enableDownloadDiscovery) {
			if (log().isInfoEnabled())
				log().info("run: get further unknown mac address snmp bridge table info");
			snmpParseBridgeNodes();
		} else {
			if (log().isInfoEnabled())
				log().info("run: skipping get further unknown mac address snmp bridge table info");
		}
		*/

		// First of all use quick methods to get backbone ports for speeding
		// up the link discovery
		SysLogger.info("运行: finding links among nodes using Cisco Discovery Protocol");
	
	   
	   
	   SysLogger.info("利用Cisco Discovery Protocol发现节点间的连接");
	   // Try Cisco Discovery Protocol to found link among all nodes
	   // Add CDP info for backbones ports

		ite = cdpNodes.iterator();
		while (ite.hasNext()) {
			try{
			Host host = ite.next();
			int curCdpNodeId = host.getId();
			String curCdpIpAddr = host.getAdminIp();
			List executedPort = new ArrayList();
			Iterator<CdpCachEntryInterface> sub_ite = host.getCdpList().iterator();
			while (sub_ite.hasNext()) {
				try{
				CdpCachEntryInterface cdpIface = sub_ite.next();
				//SysLogger.info(host.getIpAddress()+" remoteip:"+cdpIface.getIp()+" remoteDesc:"+cdpIface.getPortdesc());
				String targetIpAddr = cdpIface.getIp();
				//判断是否是已经存在在host列表里的IP
				Host targetHost = DiscoverEngine.getInstance().getHostByAliasIP(targetIpAddr);
				if( targetHost == null){
					SysLogger.info("IP地址"+targetIpAddr+"不在已发现的网络设备里，跳过");
					continue;
				}

				int targetCdpNodeId = targetHost.getId();
				if (targetCdpNodeId == -1 || targetCdpNodeId == 0) {
					SysLogger.info("没发现网络设备IP "+targetHost.getIpAddress()+"的ID，跳过");
					continue;
				}
				if (targetCdpNodeId == curCdpNodeId) {
					SysLogger.info("运行: 该IP为自身IP "+ targetIpAddr+ " 跳过");
					continue;
				}
				
				//获取cdpIfIndex
				//int cdpIfIndex = Integer.parseInt(cdpIface.getIfindex());
				int cdpIfIndex = -1;
				if(targetHost.getCdpList() != null && targetHost.getCdpList().size()>0){
					Iterator<CdpCachEntryInterface> target_ite = targetHost.getCdpList().iterator();
					while (target_ite.hasNext()) {
						CdpCachEntryInterface targetcdpIface = target_ite.next();
						//SysLogger.info(host.getIpAddress()+" startip:"+targetcdpIface.getIp()+" startDesc:"+targetcdpIface.getPortdesc());
						if(host.getAliasIPs().contains(targetcdpIface.getIp())){
							//需要加如当前PORTDESC是否已经被处理过的条件
							if(executedPort.contains(targetcdpIface.getPortdesc()))continue;
							if(host.getIfEntityByDesc(targetcdpIface.getPortdesc()) != null){
								cdpIfIndex = Integer.parseInt(host.getIfEntityByDesc(targetcdpIface.getPortdesc()).getIndex());
								executedPort.add(targetcdpIface.getPortdesc());
								break;
							}	
						}
					}
				}
				if (cdpIfIndex <= 0) {
					//用逻辑端口代替
					cdpIfIndex = Integer.parseInt(host.getIfEntityByIP(host.getIpAddress()).getIndex());
					SysLogger.info("不是合法的CDP IfIndex，用逻辑端口代替");
					//continue;
				}else{
					SysLogger.info("发现合法的 CDP ifindex " + cdpIfIndex);
				}
				
				SysLogger.info("运行: 发现 nodeid/CDP 目标IP: " + targetCdpNodeId+ ":"+ targetIpAddr);


				int cdpDestIfindex = -1;
				if(targetHost.getIfEntityByDesc(cdpIface.getPortdesc()) != null){
					cdpDestIfindex = Integer.parseInt(targetHost.getIfEntityByDesc(cdpIface.getPortdesc()).getIndex());
					if(cdpDestIfindex < 0){
						SysLogger.info("运行：不合法的CDP destination IfIndex "+cdpDestIfindex+". 跳过");
						continue;
					}
				}else{
					SysLogger.info("运行：不合法的CDP destination. 跳过");
					continue;
				}
				SysLogger.info("运行： 发现 CDP target ifindex " + cdpDestIfindex);

				SysLogger.info("运行: 解析 CDP link: nodeid=" + curCdpNodeId
							+ " ifindex=" + cdpIfIndex + " nodeparentid="
							+ targetCdpNodeId + " parentifindex="
							+ cdpDestIfindex);

				
				boolean add = false;
				/*
				if (host.getBridgestpList()!=null && host.getBridgestpList().size()>0 
						&& targetHost.getBridgestpList() != null && targetHost.getBridgestpList().size()>0) {
					add = parseCdpLinkOn(host, cdpIfIndex,targetHost, cdpDestIfindex);
					SysLogger.info("运行：两个节点都是STP节点！添加ING："+add);
				} else if (host.getBridgestpList()!=null && host.getBridgestpList().size()>0) {
					SysLogger.info("运行: source node is bridge node, target node is not bridge node! Adding: " + add);
					add = parseCdpLinkOn(host,cdpIfIndex,targetCdpNodeId);
				} else if (isBridgeNode(targetCdpNodeId)) {
					SysLogger.info("运行: source node is not bridge node, target node is bridge node! Adding: " + add);
					Host targetNode = bridgeNodes.get(new Integer(targetCdpNodeId));
					add = parseCdpLinkOn(targetNode,cdpDestIfindex,curCdpNodeId);
				} else {
					SysLogger.info("运行: no node is bridge node! Adding CDP link");
						add = true;
				}
				*/
				// now add the cdp link
				SysLogger.info("运行: no node is bridge node! Adding CDP link");
				add = true;
				if (add) {
					NodeToNodeLink lk = new NodeToNodeLink(targetCdpNodeId,cdpDestIfindex);
					lk.setFindtype(SystemConstant.ISCDP);
					lk.setNodeparentid(curCdpNodeId);
					lk.setParentifindex(cdpIfIndex);
					addNodetoNodeLink(lk);
					SysLogger.info("运行: CDP link added: " + lk.toString());
				}
				}catch(Exception e){
					e.printStackTrace();
					SysLogger.error(e.getMessage());
				}
			}
			}catch(Exception e){
				e.printStackTrace();
				SysLogger.error(e.getMessage());
			}
		}

		   SysLogger.info("利用HUAWEI 的 Network Discovery Protocol发现节点间的连接");
		   // Try Cisco Discovery Protocol to found link among all nodes
		   // Add CDP info for backbones ports

			ite = ndpNodes.iterator();
			while (ite.hasNext()) {
				try{
				Host host = ite.next();
				int curNdpNodeId = host.getId();
				String curNdpIpAddr = host.getAdminIp();
				if(host.getNdpHash() != null && host.getNdpHash().size()>0){
				Iterator<String> sub_ite = host.getNdpHash().keySet().iterator();
				while (sub_ite.hasNext()) {
					try{
					String endndpMac = sub_ite.next();
					String endndpDescr = (String)host.getNdpHash().get(endndpMac);
					Host endNode = getNodeFromMacIdentifierOfNdpNode(endndpMac);
					if(endNode == null){
						SysLogger.info("找不到MAC地址"+endndpMac+",在已发现的网络设备里，跳过");
						continue;
					}
					IfEntity endIfEntity = endNode.getIfEntityByDesc(endndpDescr);
					IfEntity startIfEntity = null;
					if(endIfEntity == null){
						SysLogger.info("找不到端口描述为"+endndpDescr+",在已发现的网络设备里，跳过");
						//continue;
					}
					//寻找开始端的连接
					//默认情况下,endNode的NdpHash不为空
					Hashtable endNodeNdpHash = endNode.getNdpHash();
					if(endNodeNdpHash == null)endNodeNdpHash = new Hashtable();
					if(host.getMac() == null)continue;
					if(endNodeNdpHash.containsKey(host.getMac())){
						//存在该IP
						String ndpDescr = (String)endNodeNdpHash.get(host.getMac());
						startIfEntity = endNode.getIfEntityByDesc(ndpDescr);
					}
					
					if(startIfEntity == null){
						/*
						//从FDB表里找
						List fdbList = host.getFdbList();
						if(fdbList != null && fdbList.size()>0){
							int flag = 0;
							for(int k=0;k<fdbList.size();k++){
								String[] item = new String[2];
				    			 item = (String[])fdbList.get(k);
				    			 if(item[1] != null && item[1].trim().length()>0){
				    				 if(item[1].equalsIgnoreCase(endNode.getMac())){
				    					 startIfEntity = host.getIfEntityByIndex(item[0]);
				    					 flag = 1;
				    					 break;
				    				 }
				    			 }
				    			 //ipmac.setIfindex(item[0]);
				    			 //ipmac.setMac(item[1]);
							}
							if(flag == 0){
								//通过FDB判断,没有相关连接,用VLAN连接替代
								startIfEntity = host.getIfEntityByIP(host.getIpAddress());
								if(startIfEntity == null)continue;
							}
							
						}else{
							startIfEntity = host.getIfEntityByIP(host.getIpAddress());
							if(startIfEntity == null)continue;
						}
						*/
						startIfEntity = host.getIfEntityByIP(host.getIpAddress());
						if(startIfEntity == null)continue;
						
					}
					if(endIfEntity == null){
						//从FDB表里找
						/*
						List fdbList = endNode.getFdbList();
						if(fdbList != null && fdbList.size()>0){
							int flag = 0;
							for(int k=0;k<fdbList.size();k++){
								String[] item = new String[2];
				    			 item = (String[])fdbList.get(k);
				    			 if(item[1] != null && item[1].trim().length()>0){
				    				 if(item[1].equalsIgnoreCase(host.getMac())){
				    					 startIfEntity = endNode.getIfEntityByIndex(item[0]);
				    					 flag = 1;
				    					 break;
				    				 }
				    			 }
				    			 //ipmac.setIfindex(item[0]);
				    			 //ipmac.setMac(item[1]);
							}
							if(flag == 0){
								//通过FDB判断,没有相关连接,用VLAN连接替代
								endIfEntity = endNode.getIfEntityByIP(endNode.getIpAddress());
								if(endIfEntity == null)continue;
							}
							
						}else{
							endIfEntity = endNode.getIfEntityByIP(endNode.getIpAddress());
							if(endIfEntity == null)continue;
						}
						*/
						endIfEntity = endNode.getIfEntityByIP(endNode.getIpAddress());
						if(endIfEntity == null)continue;
						
						
						
					}
					if(startIfEntity != null && endIfEntity != null){
						//两个连接都存在
						if (host.getId() == endNode.getId()){
							SysLogger.info("运行: 该连接为自身, 跳过");
							continue;
						}	
					}

					SysLogger.info("运行: 发现 nodeid/NDP 目标IP: " + endNode.getId()+ ":"+ endNode.getIpAddress());

					SysLogger.info("运行: 解析 NDP link: nodeid=" + host.getId()
								+ " ifindex=" + startIfEntity.getIndex() + " nodeparentid="
								+ endNode.getId() + " parentifindex="
								+ endIfEntity.getIndex());

					
					boolean add = false;
					/*
					if (host.getBridgestpList()!=null && host.getBridgestpList().size()>0 
							&& targetHost.getBridgestpList() != null && targetHost.getBridgestpList().size()>0) {
						add = parseCdpLinkOn(host, cdpIfIndex,targetHost, cdpDestIfindex);
						SysLogger.info("运行：两个节点都是STP节点！添加ING："+add);
					} else if (host.getBridgestpList()!=null && host.getBridgestpList().size()>0) {
						SysLogger.info("运行: source node is bridge node, target node is not bridge node! Adding: " + add);
						add = parseCdpLinkOn(host,cdpIfIndex,targetCdpNodeId);
					} else if (isBridgeNode(targetCdpNodeId)) {
						SysLogger.info("运行: source node is not bridge node, target node is bridge node! Adding: " + add);
						Host targetNode = bridgeNodes.get(new Integer(targetCdpNodeId));
						add = parseCdpLinkOn(targetNode,cdpDestIfindex,curCdpNodeId);
					} else {
						SysLogger.info("运行: no node is bridge node! Adding CDP link");
							add = true;
					}
					*/
					// now add the cdp link
					SysLogger.info("运行: no node is bridge node! Adding NDP link");
					add = true;
					if (add) {
						NodeToNodeLink lk = new NodeToNodeLink(endNode.getId(),Integer.parseInt(endIfEntity.getIndex()));
						lk.setFindtype(SystemConstant.ISNDP);
						lk.setNodeparentid(host.getId());
						lk.setParentifindex(Integer.parseInt(startIfEntity.getIndex()));
						addNodetoNodeLink(lk);
						SysLogger.info("运行: NDP link added: " + lk.toString());
					}
					   }catch(Exception e){
						   e.printStackTrace();
						   SysLogger.error(e.getMessage());
					   }
				}
				}
			   }catch(Exception e){
				   e.printStackTrace();
				   SysLogger.error(e.getMessage());
			   }
			}
		// try get backbone links between switches using STP info
		// and store information in Bridge class
		SysLogger.info("运行: try to found backbone ethernet links among bridge nodes using Spanning Tree Protocol");

		ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			try{
			Host curNode = ite.next();
			List curNodeStpList = curNode.getBridgestpList();
			if(curNodeStpList != null && curNodeStpList.size()>0){
				for(int k=0;k<curNodeStpList.size();k++){
					try{
					BridgeStpInterface bstp = (BridgeStpInterface)curNodeStpList.get(k);
					if (curNode.isBridgeIdentifier(bstp.getBridge().substring(5))) {
						SysLogger.info("运行: STP designated root is the bridge itself. Skipping");
						continue;
					}
					Host designatedNode = getNodeFromMacIdentifierOfBridgeNode(bstp.getBridge().substring(5));
					if(designatedNode == null)continue;
					// if port is a backbone port continue
					SysLogger.info(curNode.getIpAddress()+"   Port "+bstp.getPort());
					if (curNode.isBackBoneBridgePort(Integer.parseInt(bstp.getPort()))) {
						SysLogger.info("运行: bridge port " + bstp.getPort()+ " already found .... Skipping");
						continue;
					}
					String stpPortDesignatedPort = bstp.getBridgeport();
					stpPortDesignatedPort = stpPortDesignatedPort.replace(":", "");
					SysLogger.info(curNode.getIpAddress()+"   designatedbridgeport "+Integer.parseInt(stpPortDesignatedPort.substring(1), 16));
					int designatedbridgeport = Integer.parseInt(
							stpPortDesignatedPort.substring(1), 16);
					
					int designatedifindex = -1;
					if(designatedNode.getIfEntityByPort(designatedbridgeport+"") != null){
						designatedifindex = Integer.parseInt(designatedNode.getIfEntityByPort(designatedbridgeport+"").getIndex());
					}else{
						SysLogger.info("运行: got invalid ifindex on designated node");
						continue;
					}
					
					
					if (designatedifindex == -1 || designatedifindex == 0) {
						SysLogger.info("运行: got invalid ifindex on designated node");
						continue;
					}

					SysLogger.info("run: backbone port found for node "
								+ curNode.getId() + ". Adding to bridge"
								+ bstp.getPort());

					curNode.addBackBoneBridgePorts(Integer.parseInt(bstp.getPort()));
					bridgeNodes.put(new Integer(curNode.getId()), curNode);

					SysLogger.info("run: backbone port found for node "
								+ designatedNode.getId()
								+ " .Adding to helper class bb port "
								+ " bridge port " + designatedbridgeport);

					// test if there are other bridges between this link
					// USING MAC ADDRESS FORWARDING TABLE

					if (!isNearestBridgeLink(curNode, Integer.parseInt(bstp.getPort()),
							designatedNode, designatedbridgeport)) {
						SysLogger.info("run: other bridge found between nodes. No links to save!");
						continue; // no saving info if no nodeid
					}
					int curIfIndex = Integer.parseInt(curNode.getIfEntityByPort(bstp.getPort()+"").getIndex());

					if (curIfIndex == -1 || curIfIndex == 0) {
						SysLogger.info("运行: got invalid ifindex");
						continue;
					}
					designatedNode.addBackBoneBridgePorts(designatedbridgeport);
					bridgeNodes.put(new Integer(designatedNode.getId()),designatedNode);
					
					SysLogger.info("run: adding links on bb bridge port " + designatedbridgeport);

					addLinks(getMacsOnBridgeLink(curNode,
							Integer.parseInt(bstp.getPort()), designatedNode,
							designatedbridgeport),curNode.getId(),curIfIndex);

					// writing to db using class
					// DbDAtaLinkInterfaceEntry
					NodeToNodeLink lk = new NodeToNodeLink(curNode.getId(),curIfIndex);
					lk.setFindtype(SystemConstant.ISBridge);
					lk.setNodeparentid(designatedNode.getId());
					lk.setParentifindex(designatedifindex);
					addNodetoNodeLink(lk);
				   }catch(Exception e){
					   e.printStackTrace();
					   SysLogger.error(e.getMessage());
				   }
				}
				
			}
		   }catch(Exception e){
			   e.printStackTrace();
			   SysLogger.error(e.getMessage());
		   }
		}
		
		/*
		while (ite.hasNext()) {
			Host curNode = ite.next();

			int curNodeId = curNode.getId();
			String cupIpAddr = curNode.getAdminIp();
			SysLogger.info("运行: 解析桥节点 " + curNodeId+ " ip address " + cupIpAddr);

			Iterator<Map.Entry<String, List<BridgeStpInterface>>> sub_ite = curNode.getStpInterfaces().entrySet()
					.iterator();

			SysLogger.info("运行: 解析 "+ curNode.getStpInterfaces().size() + " Vlan. ");

			while (sub_ite.hasNext()) {
				Map.Entry<String, List<BridgeStpInterface>> me = sub_ite.next();
				String vlan = (String) me.getKey();
				String curBaseBridgeAddress = curNode
						.getBridgeIdentifier(vlan);

				SysLogger.info("run: found bridge identifier "+ curBaseBridgeAddress);

				String designatedRoot = null;
				
				if (curNode.hasStpRoot(vlan)) {
					designatedRoot = curNode.getStpRoot(vlan);
				} else {
					SysLogger.info("run: desigated root bridge identifier not found. Skipping"
										+ curBaseBridgeAddress);
					continue;
				}

				if (designatedRoot.equals("0000000000000000")) {
					SysLogger.info("run: designated root is invalid. Skipping");
					continue;
				}
				// check if designated
				// bridge is it self
				// if bridge is STP root bridge itself exiting
				// searching on linkablesnmpnodes

				if (curNode.isBridgeIdentifier(designatedRoot.substring(4))) {
					SysLogger.info("run: STP designated root is the bridge itself. Skipping");
					continue;
				}

				// Now parse STP bridge port info to get designated bridge
				SysLogger.info("run: STP designated root is another bridge. " + designatedRoot + " Parsing Stp Interface");

				Iterator<BridgeStpInterface> stp_ite = me.getValue().iterator();
				while (stp_ite.hasNext()) {
					BridgeStpInterface stpIface = stp_ite
							.next();

					// the bridge port number
					int stpbridgeport = stpIface.getBridgeport();
					// if port is a backbone port continue
					if (curNode.isBackBoneBridgePort(stpbridgeport)) {
						SysLogger.info("run: bridge port " + stpbridgeport
									+ " already found .... Skipping");
						continue;
					}

					String stpPortDesignatedPort = stpIface
							.getStpPortDesignatedPort();
					String stpPortDesignatedBridge = stpIface
							.getStpPortDesignatedBridge();

					SysLogger.info("run: parsing bridge port "
								+ stpbridgeport
								+ " with stp designated bridge "
								+ stpPortDesignatedBridge
								+ " and with stp designated port "
								+ stpPortDesignatedPort);

					if (stpPortDesignatedBridge.equals("0000000000000000")) {
						SysLogger.info("run: designated bridge is invalid "
								+ stpPortDesignatedBridge);
						continue;
					}

					if (curNode.isBridgeIdentifier(stpPortDesignatedBridge
							.substring(4))) {
						SysLogger.info("run: designated bridge for port "
									+ stpbridgeport + " is bridge itself ");
						continue;
					}

					if (stpPortDesignatedPort.equals("0000")) {
						SysLogger.info("run: designated port is invalid "
								+ stpPortDesignatedPort);
						continue;
					}

					//A Port Identifier shall be encoded as two octets,
					// taken to represent an unsigned binary number. If two
					// Port
					//Identifiers are numerically compared, the lesser
					// number denotes the Port of better priority. The more
					//significant octet of a Port Identifier is a settable
					// priority component that permits the relative priority
					// of Ports
					//on the same Bridge to be managed (17.13.7 and Clause
					// 14). The less significant twelve bits is the Port
					//Number expressed as an unsigned binary number. The
					// value 0 is not used as a Port Number.
					//NOTE锟絋he number of bits that are considered to be
					// part of the Port Number (12 bits) differs from the
					// 1998 and prior
					//versions of this standard (formerly, the priority
					// component was 8 bits and the Port Number component
					// also 8 bits). This
					//change acknowledged that modern switched LAN
					// infrastructures call for increasingly large numbers
					// of Ports to be
					//supported in a single Bridge. To maintain management
					// compatibility with older implementations, the
					// priority
					//component is still considered, for management
					// purposes, to be an 8-bit value, but the values that
					// it can be set to are
					//restricted to those where the least significant 4
					// bits are zero (i.e., only the most significant 4 bits
					// are settable).
					int designatedbridgeport = Integer.parseInt(
							stpPortDesignatedPort.substring(1), 16);

					// try to see if designated bridge is linkable
					// snmp node

					Host designatedNode = getNodeFromMacIdentifierOfBridgeNode(stpPortDesignatedBridge
							.substring(4));

					if (designatedNode == null) {
						SysLogger.info("run: no nodeid found for stp bridge address "
										+ stpPortDesignatedBridge
										+ " . Nothing to save to db");
						continue; // no saving info if no nodeid
					}
					
					int designatednodeid = designatedNode.getId();

					SysLogger.info("run: found designated nodeid "
								+ designatednodeid);

					// test if there are other bridges between this link
					// USING MAC ADDRESS FORWARDING TABLE

					if (!isNearestBridgeLink(curNode, stpbridgeport,
							designatedNode, designatedbridgeport)) {
						SysLogger.info("run: other bridge found between nodes. No links to save!");
						continue; // no saving info if no nodeid
					}

					// this is a backbone port so try adding to Bridge class
					// get the ifindex on node

					int curIfIndex = curNode.getIfindex(stpbridgeport);

					if (curIfIndex == -1) {
						SysLogger.info("run: got invalid ifindex");
						continue;
					}

					int designatedifindex = designatedNode.getIfindex(designatedbridgeport);
					
					if (designatedifindex == -1) {
						SysLogger.info("run: got invalid ifindex on designated node");
						continue;
					}

					SysLogger.info("run: backbone port found for node "
								+ curNodeId + ". Adding to bridge"
								+ stpbridgeport);

					curNode.addBackBoneBridgePorts(stpbridgeport);
					bridgeNodes.put(new Integer(curNodeId), curNode);

					SysLogger.info("run: backbone port found for node "
								+ designatednodeid
								+ " .Adding to helper class bb port "
								+ " bridge port " + designatedbridgeport);

					designatedNode
							.addBackBoneBridgePorts(designatedbridgeport);
					bridgeNodes.put(new Integer(designatednodeid),
							designatedNode);
					
					SysLogger.info("run: adding links on bb bridge port " + designatedbridgeport);

					addLinks(getMacsOnBridgeLink(curNode,
							stpbridgeport, designatedNode,
							designatedbridgeport),curNodeId,curIfIndex);

					// writing to db using class
					// DbDAtaLinkInterfaceEntry
					NodeToNodeLink lk = new NodeToNodeLink(curNodeId,
							curIfIndex);
					lk.setNodeparentid(designatednodeid);
					lk.setParentifindex(designatedifindex);
					addNodetoNodeLink(lk);

				}
			}
		}
		*/

		/*
		// finding links using mac address on ports
		SysLogger.info("运行: try to found links using Mac Address Forwarding Table");

		ite = bridgeNodes.values().iterator();

		while (ite.hasNext()) {
			Host curNode = ite.next();
			int curNodeId = curNode.getId();
			SysLogger.info("运行: parsing node bridge " + curNodeId);

			Iterator<Integer> sub_ite = curNode.getPortMacs().keySet().iterator();

			while (sub_ite.hasNext()) {
				Integer intePort = sub_ite.next();
				int curBridgePort = intePort.intValue();
				SysLogger.info("运行: parsing bridge port "+ curBridgePort+ " with mac addresses "
						+ curNode.getMacAddressesOnBridgePort(curBridgePort).toString());

				if (curNode.isBackBoneBridgePort(curBridgePort)) {
					SysLogger.info("运行: 解析的是主干桥端口 "+ curBridgePort + " .... 跳过");
					continue;
				}
				
				int curIfIndex = Integer.parseInt(curNode.getIfEntityByPort(curBridgePort+"").getIndex());
				if (curIfIndex == -1) {
					SysLogger.info("运行: 得到一个不合法的 ifindex on bridge port "+ curBridgePort);
					continue;
				}
				// First get the mac addresses on bridge port

				Set<String> macs = curNode.getMacAddressesOnBridgePort(curBridgePort);

				// Then find the bridges whose mac addresses are learned on bridge port
				List<Host> bridgesOnPort = getBridgesFromMacs(macs);
				
				if (bridgesOnPort.isEmpty()) {
					
					SysLogger.info("运行: no bridge info found on port "+ curBridgePort + " .... Saving Macs");
					addLinks(macs, curNodeId, curIfIndex);
				} else {
					// a bridge mac address was found on port so you should analyze what happens
					SysLogger.info("run: bridge info found on port "+ curBridgePort + " .... Finding nearest.");
					Iterator<Host> bridge_ite = bridgesOnPort.iterator();
					// one among these bridges should be the node more close to the curnode, curport
					while (bridge_ite.hasNext()) {
						Host endNode = bridge_ite
								.next();
						
						int endNodeid = endNode.getId();
						
						int endBridgePort = getBridgePortOnEndBridge(
								curNode, endNode);
						//The bridge port should be valid! This control is not properly done
						if (endBridgePort == -1) {
							SysLogger.error("run: no valid port found on bridge nodeid "
												+ endNodeid
												+ " for node bridge identifiers nodeid "
												+ curNodeId
												+ " . .....Skipping");
							continue;
						}
						
						// Try to found a new 
						boolean isTargetNode = isNearestBridgeLink(
								curNode, curBridgePort, endNode,
								endBridgePort);
						if (!isTargetNode)
								continue;

						int endIfindex = Integer.parseInt(endNode.getIfEntityByPort(endBridgePort+"").getIndex());
						if (endIfindex == -1) {
							SysLogger.info("运行: 得到一个不合法的 ifindex o designated bridge port "
											+ endBridgePort);
							break;
						}

						SysLogger.info("运行: backbone port found for node "
									+ curNodeId + ". Adding backbone port "
									+ curBridgePort + " to bridge");

						curNode.addBackBoneBridgePorts(curBridgePort);
						bridgeNodes.put(new Integer(curNodeId), curNode);

						SysLogger.info("运行: backbone port found for node "
									+ endNodeid
									+ " .Adding to helper class bb port "
									+ " bridge port " + endBridgePort);

						endNode.addBackBoneBridgePorts(endBridgePort);
						bridgeNodes.put(new Integer(endNodeid), endNode);

						// finding links between two backbone ports
						addLinks(getMacsOnBridgeLink(curNode,
								curBridgePort, endNode, endBridgePort),curNodeId,curIfIndex);

						NodeToNodeLink lk = new NodeToNodeLink(curNodeId,
								curIfIndex);
						lk.setFindtype(SystemConstant.ISMac);
						lk.setNodeparentid(endNodeid);
						lk.setParentifindex(endIfindex);
						addNodetoNodeLink(lk);
						break;
					}
				}
			}
		}
		*/
		// fourth find inter router links,
		// this part could have several special function to get inter router
		// links, but at the moment we worked much on switches.
		// In future we can try to extend this part.
		SysLogger.info("运行: try to found  not ethernet links on Router nodes");

		List routeLinkList = DiscoverEngine.getInstance().getRouteLinkList();
		if(routeLinkList != null && routeLinkList.size()>0){
			for(int k=0;k<routeLinkList.size();k++){
				try{
				Link link = (Link)routeLinkList.get(k);
				// Saving link also when ifindex = -1 (not found)
				NodeToNodeLink lk = new NodeToNodeLink(link.getEndId(),
						Integer.parseInt(link.getEndIndex()));
				lk.setFindtype(SystemConstant.ISRouter);
				lk.setNodeparentid(link.getStartId());
				lk.setParentifindex(Integer.parseInt(link.getStartIndex()));
				SysLogger.info("添加连接: ##########################");
				SysLogger.info("添加连接: "+link.getStartIp()+" --- "+link.getEndIp());
				addNodetoNodeLink(lk);
				   }catch(Exception e){
					   e.printStackTrace();
					   SysLogger.error(e.getMessage());
				   }
			}
		}
		//将maclinklist中有连接,而在上面的CDP/NDP/STP/ROUTER计算中没有的连接加进去
		List macLinks = DiscoverEngine.getInstance().getMacLinkList();
		if(macLinks != null && macLinks.size()>0){
			for(int k=0;k<macLinks.size();k++){
				try{
				Link maclink = (Link)macLinks.get(k);
				if(!NodeToNodeLinkExist(maclink)){
					//若不存在该连接,则添加进去
					NodeToNodeLink lk = new NodeToNodeLink(maclink.getEndId(),
							Integer.parseInt(maclink.getEndIndex()));
					lk.setFindtype(SystemConstant.ISMac);
					lk.setNodeparentid(maclink.getStartId());
					lk.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
					SysLogger.info("添加连接: ##########################");
					SysLogger.info("添加连接: "+maclink.getStartIp()+" --- "+maclink.getEndIp());
					addNodetoNodeLink(lk);
				}
				   }catch(Exception e){
					   e.printStackTrace();
					   SysLogger.error(e.getMessage());
				   }
			}
		}
		
		
		//List allLinks = DiscoverEngine.getInstance().getLinkList();
		Hashtable existNode = new Hashtable();
		DiscoverEngine.getInstance().getLinkList().clear();;
		if(links != null && links.size()>0){
			for(int i=0;i<links.size();i++){
				try{
				NodeToNodeLink link = (NodeToNodeLink)links.get(i);
				SysLogger.info("连接: "+link.getNodeparentid()+" "+link.getParentifindex()+" "+link.getNodeId()+" "+link.getIfindex());
				Host startNode = DiscoverEngine.getInstance().getHostByID(link.getNodeparentid());
				Host endNode = DiscoverEngine.getInstance().getHostByID(link.getNodeId());
				IfEntity startIfEntity = startNode.getIfEntityByIndex(link.getParentifindex()+"");
				IfEntity endIfEntity = endNode.getIfEntityByIndex(link.getIfindex()+"");
				Link addlink = new Link();
				addlink.setStartId(link.getNodeparentid());
				addlink.setStartIndex(link.getParentifindex()+"");
				addlink.setStartIp(startNode.getIpAddress());
				addlink.setStartDescr(startIfEntity.getDescr());
				addlink.setStartPort(startIfEntity.getPort());
				addlink.setStartPhysAddress(startNode.getBridgeAddress());
				
				addlink.setEndId(link.getNodeId());
				addlink.setEndIndex(link.getIfindex()+"");
				addlink.setEndIp(endNode.getIpAddress());
				addlink.setEndDescr(endIfEntity.getDescr());
				addlink.setEndPort(endIfEntity.getPort());
				addlink.setEndPhysAddress(endNode.getBridgeAddress());
				
				addlink.setAssistant(link.getAssistant());
				addlink.setFindtype(link.getFindtype());
				addlink.setLinktype(0);
				DiscoverEngine.getInstance().getLinkList().add(addlink);
				if(!existNode.containsKey(addlink.getStartId())){
					existNode.put(addlink.getStartId(), addlink.getStartId());
				}
				if(!existNode.containsKey(addlink.getEndId())){
					existNode.put(addlink.getEndId(), addlink.getEndId());
				}
				//allLinks.add(addlink);
			   }catch(Exception e){
				   e.printStackTrace();
				   SysLogger.error(e.getMessage());
			   }
			}
		}
		
		
		
		//判断是否有没有连接的Node
		//将没有产生连接的孤立的接点用逻辑连接代替
		List macLinkList = DiscoverEngine.getInstance().getMacLinkList();
		if(macLinkList != null && macLinkList.size()>0){
			for(int k=0;k<macLinkList.size();k++){
				try{
				Link maclink = (Link)macLinkList.get(k);
				if(!existNode.containsKey(maclink.getStartId()) || !existNode.containsKey(maclink.getEndId())){
					//若有个端点不在已经存在的连接列表里
					// Saving link also when ifindex = -1 (not found)
					NodeToNodeLink link = new NodeToNodeLink(maclink.getEndId(),
							Integer.parseInt(maclink.getEndIndex()));
					link.setFindtype(SystemConstant.ISMac);
					link.setNodeparentid(maclink.getStartId());
					link.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
					SysLogger.info("添加连接: ##########################");
					SysLogger.info("添加连接: "+maclink.getStartIp()+" --- "+maclink.getEndIp());
					addNodetoNodeLink(link);
					//NodeToNodeLink link = (NodeToNodeLink)links.get(i);
					SysLogger.info("连接: "+link.getNodeparentid()+" "+link.getParentifindex()+" "+link.getNodeId()+" "+link.getIfindex());
					Host startNode = DiscoverEngine.getInstance().getHostByID(link.getNodeparentid());
					Host endNode = DiscoverEngine.getInstance().getHostByID(link.getNodeId());
					IfEntity startIfEntity = startNode.getIfEntityByIndex(link.getParentifindex()+"");
					IfEntity endIfEntity = endNode.getIfEntityByIndex(link.getIfindex()+"");
					Link addlink = new Link();
					addlink.setStartId(link.getNodeparentid());
					addlink.setStartIndex(link.getParentifindex()+"");
					addlink.setStartIp(startNode.getIpAddress());
					addlink.setStartDescr(startIfEntity.getDescr());
					addlink.setStartPort(startIfEntity.getPort());
					addlink.setStartPhysAddress(startNode.getBridgeAddress());
					
					addlink.setEndId(link.getNodeId());
					addlink.setEndIndex(link.getIfindex()+"");
					addlink.setEndIp(endNode.getIpAddress());
					addlink.setEndDescr(endIfEntity.getDescr());
					addlink.setEndPort(endIfEntity.getPort());
					addlink.setEndPhysAddress(endNode.getBridgeAddress());
					
					addlink.setAssistant(link.getAssistant());
					addlink.setFindtype(link.getFindtype());
					addlink.setLinktype(0);//物理连接
					DiscoverEngine.getInstance().getLinkList().add(addlink);
					if(!existNode.containsKey(addlink.getStartId())){
						existNode.put(addlink.getStartId(), addlink.getStartId());
					}
					if(!existNode.containsKey(addlink.getEndId())){
						existNode.put(addlink.getEndId(), addlink.getEndId());
					}
				}
			   }catch(Exception e){
				   e.printStackTrace();
				   SysLogger.error(e.getMessage());
			   }
			}
		}

		//将路由连接添加进去
		if(routeLinkList != null && routeLinkList.size()>0){
			for(int k=0;k<routeLinkList.size();k++){
				try{
				Link routelink = (Link)routeLinkList.get(k);
				routelink.setLinktype(-1);//物理连接		
				DiscoverEngine.getInstance().getLinkList().add(routelink);
				   }catch(Exception e){
					   e.printStackTrace();
					   SysLogger.error(e.getMessage());
				   }
			}
		}
		//将VLAN连接添加进去
		if(macLinkList != null && macLinkList.size()>0){
			for(int k=0;k<macLinkList.size();k++){
				try{
				Link maclink = (Link)macLinkList.get(k);
				maclink.setLinktype(-1);//物理连接		
				DiscoverEngine.getInstance().getLinkList().add(maclink);
				   }catch(Exception e){
					   e.printStackTrace();
					   SysLogger.error(e.getMessage());
				   }
			}
		}
		Hashtable exitsnodelink = new Hashtable();
		List linklists = DiscoverEngine.getInstance().getLinkList();
		if(linklists != null && linklists.size()>0){
			for(int i=0;i<linklists.size();i++){
				Link link = (Link)linklists.get(i);
				if(!exitsnodelink.containsKey(link.getStartId()+"")){
					exitsnodelink.put(link.getStartId()+"", link.getStartId()+"");
				}
				if(!exitsnodelink.containsKey(link.getEndId()+"")){
					exitsnodelink.put(link.getEndId()+"", link.getEndId()+"");
				}
			}
		}
		//Iterator<Host> ite = null;
		   //List hostlist = DiscoverEngine.getInstance().getHostList();
		   if(hostlist != null && hostlist.size()>0){
			   for(int i=0;i<hostlist.size();i++){
				   Host host = (Host)hostlist.get(i);
				   //SysLogger.info(host.toString());
				   if (host == null) {
					   SysLogger.error("节点为空值，继续进行下一步操作");
					   continue;
				   }
				   //过滤掉非网络设备
				   if(host.getCategory() != 1 && host.getCategory() != 2 && host.getCategory() != 3 && host.getCategory() != 7)continue;
				   //int flag = 0;
				   if(!exitsnodelink.containsKey(host.getId()+"")){
					   //没有连接关系,需要遍历所有节点,计算连接关系
					   for(int k=0;k<hostlist.size();k++){
						   Host phost = (Host)hostlist.get(i);
						   if(host.getId() == phost.getId())continue;
						   List arplist = phost.getIpNetTable();
						   if(arplist != null && arplist.size()>0){
							   for(int m=0;m<arplist.size();i++){
								   IpAddress ipAddress = (IpAddress)arplist.get(m);
								   if(host.getIpAddress().equalsIgnoreCase(ipAddress.getIpAddress())){
									   //存在连接关系
									   IfEntity ifentity = host.getIfEntityByIP(host.getIpAddress());
									   if(ifentity == null)continue;
									   
									   	Link addlink = new Link();
									   	
										addlink.setStartId(phost.getId());
										addlink.setStartIndex(ipAddress.getIfIndex());
										addlink.setStartIp(phost.getIpAddress());
										addlink.setStartDescr(ipAddress.getIfIndex());
										addlink.setStartPort(ipAddress.getIfIndex());
										addlink.setStartPhysAddress(ipAddress.getPhysAddress());
										
										addlink.setEndId(host.getId());
										addlink.setEndIndex(ifentity.getIndex());
										addlink.setEndIp(ifentity.getIpAddress());
										addlink.setEndDescr(ifentity.getDescr());
										addlink.setEndPort(ifentity.getPort());
										addlink.setEndPhysAddress(host.getBridgeAddress());
										
										addlink.setAssistant(0);
										addlink.setFindtype(1);
										addlink.setLinktype(0);//逻辑连接
										DiscoverEngine.getInstance().getLinkList().add(addlink);
										if(!exitsnodelink.containsKey(addlink.getStartId()+"")){
											exitsnodelink.put(addlink.getStartId()+"", addlink.getStartId()+"");
											break;
										}
										if(!exitsnodelink.containsKey(addlink.getEndId()+"")){
											exitsnodelink.put(addlink.getEndId()+"", addlink.getEndId()+"");
											break;
										}
								   }
							   }
							   
						   }
					   }
				   }
				   
				   if(!exitsnodelink.containsKey(host.getId()+"")){
					   //没有连接关系,需要遍历该IP别名计算连接关系
					   for(int k=0;k<hostlist.size();k++){
						   Host phost = (Host)hostlist.get(i);
						   if(host.getId() == phost.getId())continue;
						   List hostiplist = host.getIfEntityList();
						   if(hostiplist != null && hostiplist.size()>0){
							   for(int j=0;j<hostiplist.size();j++){
								   IfEntity ifentity = (IfEntity)hostiplist.get(j);
								   List arplist = phost.getIpNetTable();
								   if(arplist != null && arplist.size()>0){
									   for(int m=0;m<arplist.size();i++){
										   IpAddress ipAddress = (IpAddress)arplist.get(m);
										   if(host.getIpAddress().equalsIgnoreCase(ipAddress.getIpAddress())){
											   //存在连接关系
											   IfEntity if_entity = host.getIfEntityByIP(host.getIpAddress());
											   if(ifentity == null)continue;
											   
											   	Link addlink = new Link();
											   	
												addlink.setStartId(phost.getId());
												addlink.setStartIndex(ipAddress.getIfIndex());
												addlink.setStartIp(phost.getIpAddress());
												addlink.setStartDescr(ipAddress.getIfIndex());
												addlink.setStartPort(ipAddress.getIfIndex());
												addlink.setStartPhysAddress(ipAddress.getPhysAddress());
												
												addlink.setEndId(host.getId());
												addlink.setEndIndex(if_entity.getIndex());
												addlink.setEndIp(if_entity.getIpAddress());
												addlink.setEndDescr(if_entity.getDescr());
												addlink.setEndPort(if_entity.getPort());
												addlink.setEndPhysAddress(host.getBridgeAddress());
												
												addlink.setAssistant(0);
												addlink.setFindtype(1);
												addlink.setLinktype(0);//逻辑连接
												DiscoverEngine.getInstance().getLinkList().add(addlink);
												if(!exitsnodelink.containsKey(addlink.getStartId()+"")){
													exitsnodelink.put(addlink.getStartId()+"", addlink.getStartId()+"");
													break;
												}
												if(!exitsnodelink.containsKey(addlink.getEndId()+"")){
													exitsnodelink.put(addlink.getEndId()+"", addlink.getEndId()+"");
													break;
												}
										   }
									   }
									   
								   }
								   
							   }
						   }
						   
						   
					   }
				   }
				   
			   }
		   }
		   
		
		
		
   }
	private boolean parseCdpLinkOn(Host node1,int ifindex1,
			Host node2,int ifindex2) {
		IfEntity ifEntity = node1.getIfEntityByIndex(ifindex1+"");
		if(ifEntity == null){
			SysLogger.info("运行：找不到ifindex1 "+ifindex1+"对应的接口，跳过");
			return false;
		}
		
		SysLogger.info("运行：ifindex1 "+ifindex1+"对应的 port "+ifEntity.getPort());
		if(ifEntity.getPort() == "")ifEntity.setPort(ifEntity.getIndex());
		int bridgeport1 = Integer.parseInt(ifEntity.getPort());

		if (node1.isBackBoneBridgePort(bridgeport1)) {
			SysLogger.info("方法parseCdpLinkOn: 主干桥端口 "
						+ bridgeport1
						+ " 已经被解吸. Skipping");
			return false;
		}
		
		ifEntity = node2.getIfEntityByIndex(ifindex2+"");
		if(ifEntity == null){
			SysLogger.info("运行：找不到ifindex2 "+ifindex2+"对应的接口，跳过");
			return false;
		}
		if(ifEntity.getPort() == "")ifEntity.setPort(ifEntity.getIndex());
		int bridgeport2 = Integer.parseInt(ifEntity.getPort());

		if (node2.isBackBoneBridgePort(bridgeport2)) {
			SysLogger.info("方法parseCdpLinkOn: 主干桥端口 "
						+ bridgeport2
						+ " 已经被解吸. Skipping");
			return false;
		}


		if (isNearestBridgeLink(node1, bridgeport1,node2, bridgeport2)) {

			node1.addBackBoneBridgePorts(bridgeport1);
			bridgeNodes.put(new Integer(node1.getId()), node1);

			node2.addBackBoneBridgePorts(bridgeport2);
			bridgeNodes.put(new Integer(node2.getId()),node2);

			SysLogger.info("解析CdpLinkOn: 添加节点连接关系.");

			addLinks(getMacsOnBridgeLink(node1,
					bridgeport1, node2, bridgeport2),node1.getId(),ifindex1);
		} else {
			SysLogger.info("解析CdpLinkOn: 没发现最近的连接.跳过");
			return false;
		}
		return true;
	}
	
	private boolean isNearestBridgeLink(Host bridge1, int bp1,
			Host bridge2, int bp2) {

		boolean hasbridge2forwardingRule = false;
		Set<String> macsOnBridge2 = bridge2.getMacAddressesOnBridgePort(bp2);

		Set<String> macsOnBridge1 = bridge1.getMacAddressesOnBridgePort(bp1);

		if (macsOnBridge2 == null || macsOnBridge1 == null)
			return false;

		if (macsOnBridge2.isEmpty() || macsOnBridge1.isEmpty())
			return false;

		Iterator<String> macsonbridge1_ite = macsOnBridge1.iterator();

		while (macsonbridge1_ite.hasNext()) {
			String curMacOnBridge1 = macsonbridge1_ite.next();
			// if mac address is bridge identifier of bridge 2 continue
			
			if (bridge2.isBridgeIdentifier(curMacOnBridge1)) {
				hasbridge2forwardingRule = true;
				continue;
			}
			// if mac address is itself identifier of bridge1 continue
			if (bridge1.isBridgeIdentifier(curMacOnBridge1))
				continue;
			// then no identifier of bridge one no identifier of bridge 2
			// bridge 2 contains  
			if (macsOnBridge2.contains(curMacOnBridge1)
					&& isMacIdentifierOfBridgeNode(curMacOnBridge1))
				return false;
		}

		return hasbridge2forwardingRule;
	}
	
	private boolean isMacIdentifierOfBridgeNode(String macAddress) {
		Iterator<Host> ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			Host curNode = ite.next();
			if (curNode.isBridgeIdentifier(macAddress))
				return true;
		}
		return false;
	}
	private Set<String> getMacsOnBridgeLink(Host bridge1, int bp1,
			Host bridge2, int bp2) {

		Set<String> macsOnLink = new HashSet<String>();

    	Set<String> macsOnBridge1 = bridge1.getMacAddressesOnBridgePort(bp1);

		Set<String> macsOnBridge2 = bridge2.getMacAddressesOnBridgePort(bp2);

		if (macsOnBridge2 == null || macsOnBridge1 == null)
			return null;

		if (macsOnBridge2.isEmpty() || macsOnBridge1.isEmpty())
			return null;

		Iterator<String> macsonbridge1_ite = macsOnBridge1.iterator();

		while (macsonbridge1_ite.hasNext()) {
			String curMacOnBridge1 = macsonbridge1_ite.next();
			if (bridge2.isBridgeIdentifier(curMacOnBridge1))
				continue;
			if (macsOnBridge2.contains(curMacOnBridge1))
				macsOnLink.add(curMacOnBridge1);
		}
		return macsOnLink;
	}
	
	private void addLinks(Set<String> macs,int nodeid,int ifindex) { 
		if (macs == null || macs.isEmpty()) {
			SysLogger.info("addLinks: mac's list on link is empty.");
		} else {
			Iterator<String> mac_ite = macs.iterator();

			while (mac_ite.hasNext()) {
				String curMacAddress = mac_ite.next();
				if (macsParsed.contains(curMacAddress)) {
					SysLogger.info("添加连接: MAC地址"+ curMacAddress+"在其他桥端口发现!跳过...");
					continue;
				}
				
				if (macsExcluded.contains(curMacAddress)) {
					SysLogger.info("添加连接: MAC地址"+ curMacAddress+ " is excluded from discovery package! Skipping...");
					continue;
				}
				
				if (macToAtinterface.containsKey(curMacAddress)) {
					List<AtInterface> ats = macToAtinterface.get(curMacAddress);
					Iterator<AtInterface> ite = ats.iterator();
					while (ite.hasNext()) {
						AtInterface at = ite.next();
						
						NodeToNodeLink lNode = new NodeToNodeLink(at.getNodeId(),at.getIfindex());
						lNode.setNodeparentid(nodeid);
						lNode.setParentifindex(ifindex);
						
						addNodetoNodeLink(lNode);
					}
				} else {
					SysLogger.info("添加连接:not find nodeid for ethernet mac address "+ curMacAddress+ " found on node/ifindex" + nodeid+ "/" +ifindex);
					MacToNodeLink lMac = new MacToNodeLink(curMacAddress);
					lMac.setNodeparentid(nodeid);
					lMac.setParentifindex(ifindex);
					maclinks.add(lMac);
				}
				macsParsed.add(curMacAddress);
			}
		}
	}
	
	private void addNodetoNodeLink(NodeToNodeLink nnlink) {
		if (nnlink == null)
		{
			SysLogger.info("addNodetoNodeLink: node link is null.");
				return;
		}
		if (!links.isEmpty()) {
			Iterator<NodeToNodeLink> ite = links.iterator();
			while (ite.hasNext()) {
				NodeToNodeLink curNnLink = ite.next();
				if (curNnLink.equals(nnlink)) {
					SysLogger.info("添加节点连接: link " + nnlink.toString() + " exists, not adding");
					return;
				}
			}
		}
		
		int assitantLink = countNodetoNodeLink(nnlink);
		if(assitantLink == 0){
			//不存在该连接,则添加
			SysLogger.info("添加节点连接: adding link " + nnlink.toString());
			links.add(nnlink);
		}else if(assitantLink == 1){
			//已经存在一条,则要把该连接设置为辅助连接
			SysLogger.info("添加节点连接: adding link " + nnlink.toString());
			nnlink.setAssistant(1);
			links.add(nnlink);
		}else{
			return;
		}
		//links.add(nnlink);
	}
	
	private int countNodetoNodeLink(NodeToNodeLink nnlink) {
		int counts = 0;
		if (nnlink == null)
		{
			SysLogger.info("addNodetoNodeLink: node link is null.");
				return counts;
		}
		if (!links.isEmpty()) {
			Iterator<NodeToNodeLink> ite = links.iterator();
			while (ite.hasNext()) {
				NodeToNodeLink curNnLink = ite.next();
				if (curNnLink.assistantequals(nnlink)) {
					counts = counts+1;
					SysLogger.info("连接存在: link " + nnlink.toString() + " exists, not adding");
					//return counts;
				}
			}
		}
		SysLogger.info("该连接点有: "+counts+" 条" + nnlink.toString());
		return counts;
	}
	
	private boolean parseCdpLinkOn(Host node1,int ifindex1,int nodeid2) {

		int bridgeport = Integer.parseInt(node1.getIfEntityByIndex(ifindex1+"").getPort());

		if (node1.isBackBoneBridgePort(bridgeport)) {
			SysLogger.info("解析CDPLINK连接: node/backbone bridge port "+ node1.getId() +"/" +bridgeport+ " already parsed. Skipping");
			return false;
		}

		if (isEndBridgePort(node1, bridgeport)) {

			node1.addBackBoneBridgePorts(bridgeport);
			bridgeNodes.put(new Integer(node1.getId()), node1);
			Set<String> macs = node1.getMacAddressesOnBridgePort(bridgeport);
			addLinks(macs,node1.getId(),ifindex1);
		} else {
			SysLogger.info("解析CDPLINK连接: link cannot be saved. Skipping");
			return false;
		}
		return true;
	}
	
	private boolean isEndBridgePort(Host bridge, int bridgeport){

		Set<String> macsOnBridge = bridge.getMacAddressesOnBridgePort(bridgeport);

		if (macsOnBridge == null || macsOnBridge.isEmpty())
			return true;

		Iterator<String> macsonbridge_ite = macsOnBridge.iterator();

		while (macsonbridge_ite.hasNext()) {
			String macaddr = macsonbridge_ite.next();
			if (isMacIdentifierOfBridgeNode(macaddr)) return false;
		}

		return true;
	}
	
	/**
	 * 
	 * @param nodeid
	 * @return LinkableSnmpNode or null if not found
	 */

	boolean isBridgeNode(int nodeid) {

		Iterator<Host> ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			Host curNode = ite.next();
			if (nodeid == curNode.getId())
				return true;
		}
		return false;
	}
	
	private List<Host> getBridgesFromMacs(Set<String> macs) {
		List<Host> bridges = new ArrayList<Host>();
		Iterator<Host> ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			Host curNode = ite.next();

			Iterator<String> sub_ite = curNode.getBridgeIdentifiers().iterator();
			while (sub_ite.hasNext()) {
				String curBridgeIdentifier = (String) sub_ite.next();
				if (macs.contains((curBridgeIdentifier)))
					bridges.add(curNode);
			}
		}
		return bridges;
	}
	
	private int getBridgePortOnEndBridge(Host startBridge,
			Host endBridge) {

		int port = -1;
		Iterator<String> bridge_ident_ite = startBridge.getBridgeIdentifiers()
				.iterator();
		while (bridge_ident_ite.hasNext()) {
			String curBridgeIdentifier = bridge_ident_ite.next();
			SysLogger.info("getBridgePortOnEndBridge: parsing bridge identifier "
								+ curBridgeIdentifier);
			
			if (endBridge.hasMacAddress(curBridgeIdentifier)) {
				List<Integer> ports = endBridge.getBridgePortsFromMac(curBridgeIdentifier);
				Iterator<Integer> ports_ite = ports.iterator();
				while (ports_ite.hasNext()) {
					port = ports_ite.next();
					if (endBridge.isBackBoneBridgePort(port)) {
						SysLogger.info("getBridgePortOnEndBridge: found backbone bridge port "
											+ port
											+ " .... 跳过");
						continue;
					}
					if (port == -1) {
						SysLogger.info("run: no port found on bridge nodeid "
											+ endBridge.getId()
											+ " for node bridge identifiers nodeid "
											+ startBridge.getId()
											+ " . .....Skipping");
						continue;
					}
					SysLogger.info("run: using mac address table found bridge port "
										+ port
										+ " on node "
										+ endBridge.getId());
					return port;
				}
					
			} else {
				SysLogger.info("运行: 1bridge identifier not found on node "
									+ endBridge.getId());
			}
		}
		return -1;
	}
	/**
	 * 
	 * @param stpportdesignatedbridge
	 * @return Bridge Bridge Node if found else null
	 */

	private Host getNodeFromMacIdentifierOfBridgeNode(String macAddress) {
		Iterator<Host> ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			Host curNode = ite.next();
			if (curNode.isBridgeIdentifier(macAddress))
				return curNode;
		}
		return null;
	}
	
	private Host getNodeFromMacIdentifierOfNdpNode(String macAddress) {
		Iterator<Host> ite = ndpNodes.iterator();
		while (ite.hasNext()) {
			Host curNode = ite.next();
			SysLogger.info(curNode.getMac()+"-------"+macAddress);
			if(curNode.getMac() == null)continue;
			if (curNode.getMac().equalsIgnoreCase(macAddress))
				return curNode;
		}
		return null;
	}
	private boolean NodeToNodeLinkExist(Link link){
		boolean flag = false;
		for(int i=0;i<links.size();i++){
			NodeToNodeLink nodelink = (NodeToNodeLink)links.get(i);
			if (nodelink.getNodeId() == link.getStartId()	&&
					nodelink.getNodeparentid() == link.getEndId() ){
				flag = true;
				break;
			}

			if (nodelink.getNodeparentid() == link.getStartId() && 
					nodelink.getNodeId() == link.getEndId() ){
				flag = true;
				break;
			}
		}
		return flag;
	}

}

