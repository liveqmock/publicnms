package com.afunms.topology.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.afunms.common.util.PingUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.DistrictDao;
import com.afunms.config.dao.IPDistrictDao;
import com.afunms.config.dao.MacconfigDao;
import com.afunms.config.model.DistrictConfig;
import com.afunms.config.model.IPDistrictConfig;
import com.afunms.config.model.Macconfig;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.task.ThreadPool;
import com.afunms.topology.model.IpDistrictMatchConfig;
import com.afunms.config.model.IpConfig;
import com.afunms.config.dao.IpConfigDao;

public class IPOnlineDistrictMatchUtil {
	
	private List list;
	
	/**
	 * 
	 */
	public IPOnlineDistrictMatchUtil() {
		// TODO Auto-generated constructor stub
		
		list = new ArrayList();
	}
	
//	public static void main(String[] args){
//		
//		String ipaddress = "10.0.0.2";
//		System.out.println(ip2long(ipaddress)+"===========");
//		System.out.println(iplongToIp(ip2long(ipaddress))+"===========");
//	}
	
	private static long ip2long(String ip) {
		long result = 0;
		try {
			StringTokenizer st = new StringTokenizer(ip, ".");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				int part = Integer.parseInt(token);
				result = result * 256 + part;
			}
		} catch (Exception e) {
			result = 0;
		}
		return result;
	}
	
	
	public static String iplongToIp(long ipaddress) {  
        StringBuffer sb = new StringBuffer("");  
        sb.append(String.valueOf((ipaddress >>> 24)));  
        sb.append(".");  
        sb.append(String.valueOf((ipaddress & 0x00FFFFFF) >>> 16));  
        sb.append(".");  
        sb.append(String.valueOf((ipaddress & 0x0000FFFF) >>> 8));  
        sb.append(".");  
        sb.append(String.valueOf((ipaddress & 0x000000FF)));  
        return sb.toString();  
    } 

	 //string ip to long
	 public  long ipStrToLong(String ipaddress) {  
	        long[] ip = new long[4];
	     int position1 = ipaddress.indexOf(".");  
	        int position2 = ipaddress.indexOf(".", position1 + 1);  
	        int position3 = ipaddress.indexOf(".", position2 + 1);  
	         ip[0] = Long.parseLong(ipaddress.substring(0, position1));  
	         ip[1] = Long.parseLong(ipaddress.substring(position1+1, position2));  
	         ip[2] = Long.parseLong(ipaddress.substring(position2+1, position3));  
	         ip[3] = Long.parseLong(ipaddress.substring(position3+1));  
	        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];  
	     }  


	/*public List pingIpUtil(List ipMacList){
		
		try{
			if(ipMacList != null && ipMacList.size()>0){
				
				List ipDistrictList = new ArrayList();
				
				IPDistrictDao ipDistrictDao = new IPDistrictDao();
				try {
					ipDistrictList = ipDistrictDao.loadAll();
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					ipDistrictDao.close();
				}
				
				int numThreads = ipMacList.size();
				// 生成线程池
		  		ThreadPool threadPool = new ThreadPool(numThreads);	
		  		MacconfigDao macconfigDao = new MacconfigDao();
		  		Hashtable macHash = new Hashtable();
		  		List list = new ArrayList();
		  		try{
		  			list = macconfigDao.loadAll(); 
		  		}catch(Exception e){
		  			
		  		}finally{
		  			macconfigDao.close();
		  		}
		  		if(list != null && list.size()>0){
		  			for(int i=0;i<list.size();i++){
		  				Macconfig vo = (Macconfig)list.get(i);
		  				if(macHash.containsKey(vo.getMac())){
		  					List tempList = (List)macHash.get(vo.getMac());
		  					tempList.add(vo);
		  					macHash.put(vo.getMac(), tempList);
		  				}else{
		  					List tempList = new ArrayList();
		  					tempList.add(vo);
		  					macHash.put(vo.getMac(), tempList);
		  				}
		  			}
		  		}
		  		DistrictDao districtDao = new DistrictDao();
		  		Hashtable disHash = new Hashtable();
		  		List dislist = new ArrayList();
		  		try{
		  			dislist = districtDao.loadAll(); 
		  		}catch(Exception e){
		  			
		  		}finally{
		  			districtDao.close();
		  		}
		  		if(dislist != null && dislist.size()>0){
		  			for(int i=0;i<dislist.size();i++){
		  				DistrictConfig vo = (DistrictConfig)dislist.get(i);
		  				disHash.put(vo.getId(), vo);
		  			}
		  		}
		  		
		  		try{
			  		// 运行任务
			  		for (int i=0; i<ipMacList.size(); i++) {
			      			threadPool.runTask(createTask((Macconfig)ipMacList.get(i) , ipDistrictList,macHash,disHash));
			  		}
			  		//threadPool.runTask(createTask((IpMac)ipMacList.get(0) , ipDistrictList));
			  		// 关闭线程池并等待所有任务完成
			  		threadPool.join();
			  		threadPool.close();
			   		threadPool = null;
		  		}catch(Exception e){
		  			
		  		}finally{
		  			macconfigDao.close();
		  		}
			}
		}
		catch(Exception e){
			SysLogger.info("error in ExecutePing!"+e.getMessage());
	  	}
		
		return list;
	}*/
	
	
	
	
	public List pingIpUtil(List ipMacList){
		
		try{
			if(ipMacList != null && ipMacList.size()>0){
				
				List ipDistrictList = new ArrayList();
				
				IPDistrictDao ipDistrictDao = new IPDistrictDao();
				try {
					ipDistrictList = ipDistrictDao.loadAll();
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					ipDistrictDao.close();
				}
				
				int numThreads = ipMacList.size();
				// 生成线程池
		  		ThreadPool threadPool = new ThreadPool(numThreads);	
		  		IpConfigDao ipconfigDao = new IpConfigDao();
		  		Hashtable macHash = new Hashtable();
		  		List list = new ArrayList();
		  		try{
		  			list = ipconfigDao.loadAll(); 
		  		}catch(Exception e){
		  			
		  		}finally{
		  			ipconfigDao.close();
		  		}
		  		if(list != null && list.size()>0){
		  			for(int i=0;i<list.size();i++){
		  				IpConfig vo = (IpConfig)list.get(i);
		  				if(macHash.containsKey(vo.getIpaddress())){
		  					List tempList = (List)macHash.get(vo.getIpaddress());
		  					tempList.add(vo);
		  					macHash.put(vo.getIpaddress(), tempList);
		  				}else{
		  					List tempList = new ArrayList();
		  					tempList.add(vo);
		  					macHash.put(vo.getIpaddress(), tempList);
		  				}
		  			}
		  		}
		  		DistrictDao districtDao = new DistrictDao();
		  		Hashtable disHash = new Hashtable();
		  		List dislist = new ArrayList();
		  		try{
		  			dislist = districtDao.loadAll(); 
		  		}catch(Exception e){
		  			
		  		}finally{
		  			districtDao.close();
		  		}
		  		if(dislist != null && dislist.size()>0){
		  			for(int i=0;i<dislist.size();i++){
		  				DistrictConfig vo = (DistrictConfig)dislist.get(i);
		  				disHash.put(vo.getId(), vo);
		  			}
		  		}
		  		
		  		try{
			  		// 运行任务
			  		for (int i=0; i<ipMacList.size(); i++) {
			      			threadPool.runTask(createTask((IpConfig)ipMacList.get(i) , ipDistrictList,macHash,disHash));
			  		}
			  		//threadPool.runTask(createTask((IpMac)ipMacList.get(0) , ipDistrictList));
			  		// 关闭线程池并等待所有任务完成
			  		threadPool.join();
			  		threadPool.close();
			   		threadPool = null;
		  		}catch(Exception e){
		  			
		  		}finally{
		  			ipconfigDao.close();
		  		}
			}
		}
		catch(Exception e){
			SysLogger.info("error in ExecutePing!"+e.getMessage());
	  	}
		
		return list;
	}
	
	
	
	
	
	
	public Runnable createTask(final Macconfig ipconfig , final List ipDistrictList,final Hashtable macHash,final Hashtable disHash){
		return new Runnable(){
			
			public synchronized void addipDistrictMatch(IpDistrictMatchConfig ipDistrictMatchConfig ){
				list.add(ipDistrictMatchConfig);
			}

			public void run() {
				// TODO Auto-generated method stub
				String ipaddress = ipconfig.getMac();
				//String mac = ipMac.getMac();
				//String relateipaddr = ipMac.getRelateipaddr();
				
				boolean isOnline = getIsOnline(ipaddress);
				String isOnline_str = "0";
				if(isOnline){
					isOnline_str = "1";
				}
				
				DistrictConfig originalDistrict = getOriginalDistrict(ipaddress,macHash,disHash);
				
				DistrictConfig currentDistrict = getCurrentDistrict(ipaddress, ipDistrictList);
				
				boolean isMatch = getIsMatch(originalDistrict, currentDistrict);
				
				String isMatch_str = "0";
				if(isMatch){
					isMatch_str = "1";
				}
				
				String originalDistrictId = "";
				if(originalDistrict!=null){
					originalDistrictId = String.valueOf(originalDistrict.getId());
				}
				String currentDistrictId = "";
				if(currentDistrict!=null){
					currentDistrictId = String.valueOf(currentDistrict.getId());
				}
				
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String time = simpleDateFormat.format(new Date());
				
				IpDistrictMatchConfig ipDistrictMatchConfig = new IpDistrictMatchConfig();
				//ipDistrictMatch.setId(i);
				ipDistrictMatchConfig.setRelateipaddr(ipaddress);
				ipDistrictMatchConfig.setIsOnline(isOnline_str);
				ipDistrictMatchConfig.setOriginalDistrict(originalDistrictId);
				ipDistrictMatchConfig.setCurrentDistrict(currentDistrictId);
				ipDistrictMatchConfig.setNodeIp(ipaddress);
				ipDistrictMatchConfig.setIsMatch(isMatch_str);
				ipDistrictMatchConfig.setTime(time);
				addipDistrictMatch(ipDistrictMatchConfig);
			}
			
			private boolean getIsOnline(String ipaddress){
		    	boolean isOnline = false;
		    	try {
					if(ipaddress == null){
						System.out.println("IpDistrictMatchManager.getIsOnline()中的参数ipaddress为null");
						return false;
					}
					PingUtil pingU=new PingUtil(ipaddress);
					Integer[] packet=pingU.ping();
					Vector vector=pingU.addhis(packet); 
					//此Vector内 有两个元素 都是 Pingcollectdata 类型
					//第一个元素为连通值 
					//第二个元素为响应时间
					if(vector==null || vector.size()==0){
						return false;
					}
					Pingcollectdata pingcollectdata = (Pingcollectdata)vector.get(0);
					String thevalue = pingcollectdata.getThevalue();
					if(Double.valueOf(thevalue) > 50){
						isOnline = true;
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					SysLogger.info("IpDistrictMatchManager.getIsOnline()判断是否在线中出错！！！");
					e.printStackTrace();
				}
		    	return isOnline;
		    }
			
			
			private DistrictConfig getOriginalDistrict(String mac,Hashtable macHash,Hashtable disHash){
		    	DistrictConfig districtConfig = null;
		    	//MacconfigDao macconfigDao = new MacconfigDao();
				try {
					List MacConfigList = (List)macHash.get(mac);
					if(MacConfigList!=null&&MacConfigList.size()>0){
						
						Macconfig macConfig = (Macconfig)MacConfigList.get(0);
						String districtConfigId = String.valueOf(macConfig.getDiscrictid());
						//DistrictDao districtDao = new DistrictDao();
						try {
							districtConfig = (DistrictConfig)disHash.get(macConfig.getDiscrictid());//(DistrictConfig)districtDao.findByID(districtConfigId);
						} catch (RuntimeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							districtConfig = null;
						} finally{
							//districtDao.close();
						}
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					districtConfig = null ;
				}finally{
					//macconfigDao.close();
				}
				return districtConfig;
		    }
			
			
			private DistrictConfig getCurrentDistrict(String ipaddress , List ipDistrictList){
		    	DistrictConfig districtConfig = null ;
		    	try {
					IPDistrictConfig ipDistrictConfig = getCurrentIPDistrictConfig(ipaddress, ipDistrictList);
					if(ipDistrictConfig == null){
						return districtConfig;
					}
					int districtid = ipDistrictConfig.getDistrictid();
					DistrictDao districtDao = new DistrictDao();
					try {
						districtConfig= (DistrictConfig)districtDao.findByID(String.valueOf(districtid));
					} catch (RuntimeException e) {
						districtConfig = null ;
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally{
						districtDao.close();
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					districtConfig = null ;
				}
		    	return districtConfig;
		    }
			
			
			private IPDistrictConfig getCurrentIPDistrictConfig(String ipaddress , List ipDistrictList){
		    	try {
					long ipaddresslong = ip2long(ipaddress);
					long startiplong = 0L; 
					long endiplong = 0L; 
					for(int i =0 ; i < ipDistrictList.size() ; i ++){
						IPDistrictConfig ipDistrictConfig = (IPDistrictConfig)ipDistrictList.get(i);
						String startip = ipDistrictConfig.getStartip();
						String endip = ipDistrictConfig.getEndip();
						startiplong = ip2long(startip);
						endiplong = ip2long(endip);
						if( startiplong!=0 && endiplong != 0){
							//如果开始网段和结束网段都不为空 则判断该地址是否在网段区域内
							if(ipaddresslong>startiplong && ipaddresslong<endiplong){
								return ipDistrictConfig;
							}
						}else if( startiplong!=0 && endiplong == 0){
							if( ipaddresslong == startiplong ){
								return ipDistrictConfig;
							}
						}
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		    	return null;
		    }
			
			
			private long ip2long(String ip) {
				long result = 0;
				try {
					StringTokenizer st = new StringTokenizer(ip, ".");
					while (st.hasMoreTokens()) {
						String token = st.nextToken();
						int part = Integer.parseInt(token);
						result = result * 256 + part;
					}
				} catch (Exception e) {
					result = 0;
				}
				return result;
			}
			
			
			private boolean getIsMatch(DistrictConfig originalDistrict , DistrictConfig currentDistrict){
		    	boolean isMatch = false;
		    	try {
					if(originalDistrict == null || currentDistrict == null){
						isMatch = false;
						return isMatch;
					}
					int originalDistrictId = originalDistrict.getId();
					int currentDistrictId = currentDistrict.getId();
					
					if(originalDistrictId == currentDistrictId ){
						isMatch = true;
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					isMatch = false;
				}
		    	return isMatch;
		    }
		
		};
	}
	
	public Runnable createTask(final IpConfig ipconfig , final List ipDistrictList,final Hashtable macHash,final Hashtable disHash){
		return new Runnable(){
			
			public synchronized void addipDistrictMatch(IpDistrictMatchConfig ipDistrictMatchConfig ){
				list.add(ipDistrictMatchConfig);
			}

			public void run() {
				// TODO Auto-generated method stub
				String ipaddress = ipconfig.getIpaddress();
				//String mac = ipMac.getMac();
				//String relateipaddr = ipMac.getRelateipaddr();
				
				boolean isOnline = getIsOnline(ipaddress);
				String isOnline_str = "0";
				if(isOnline){
					isOnline_str = "1";
				}
				
				DistrictConfig originalDistrict = getOriginalDistrict(ipaddress,macHash,disHash);
				
				DistrictConfig currentDistrict = getCurrentDistrict(ipaddress, ipDistrictList);
				
				boolean isMatch = getIsMatch(originalDistrict, currentDistrict);
				
				String isMatch_str = "0";
				if(isMatch){
					isMatch_str = "1";
				}
				
				String originalDistrictId = "";
				if(originalDistrict!=null){
					originalDistrictId = String.valueOf(originalDistrict.getId());
				}
				String currentDistrictId = "";
				if(currentDistrict!=null){
					currentDistrictId = String.valueOf(currentDistrict.getId());
				}
				
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String time = simpleDateFormat.format(new Date());
				
				IpDistrictMatchConfig ipDistrictMatchConfig = new IpDistrictMatchConfig();
				//ipDistrictMatch.setId(i);
				ipDistrictMatchConfig.setRelateipaddr(ipaddress);
				ipDistrictMatchConfig.setIsOnline(isOnline_str);
				ipDistrictMatchConfig.setOriginalDistrict(originalDistrictId);
				ipDistrictMatchConfig.setCurrentDistrict(currentDistrictId);
				ipDistrictMatchConfig.setNodeIp(ipaddress);
				ipDistrictMatchConfig.setIsMatch(isMatch_str);
				ipDistrictMatchConfig.setTime(time);
				addipDistrictMatch(ipDistrictMatchConfig);
			}
			
			private boolean getIsOnline(String ipaddress){
		    	boolean isOnline = false;
		    	try {
					if(ipaddress == null){
						System.out.println("IpDistrictMatchManager.getIsOnline()中的参数ipaddress为null");
						return false;
					}
					PingUtil pingU=new PingUtil(ipaddress);
					Integer[] packet=pingU.ping();
					Vector vector=pingU.addhis(packet); 
					//此Vector内 有两个元素 都是 Pingcollectdata 类型
					//第一个元素为连通值 
					//第二个元素为响应时间
					if(vector==null || vector.size()==0){
						return false;
					}
					Pingcollectdata pingcollectdata = (Pingcollectdata)vector.get(0);
					String thevalue = pingcollectdata.getThevalue();
					if(Double.valueOf(thevalue) > 50){
						isOnline = true;
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					SysLogger.info("IpDistrictMatchManager.getIsOnline()判断是否在线中出错！！！");
					e.printStackTrace();
				}
		    	return isOnline;
		    }
			
			
			private DistrictConfig getOriginalDistrict(String mac,Hashtable macHash,Hashtable disHash){
		    	DistrictConfig districtConfig = null;
		    	//MacconfigDao macconfigDao = new MacconfigDao();
				try {
					List MacConfigList = (List)macHash.get(mac);
					if(MacConfigList!=null&&MacConfigList.size()>0){
						
						IpConfig macConfig = (IpConfig)MacConfigList.get(0);
						String districtConfigId = String.valueOf(macConfig.getDiscrictid());
						//DistrictDao districtDao = new DistrictDao();
						try {
							districtConfig = (DistrictConfig)disHash.get(macConfig.getDiscrictid());//(DistrictConfig)districtDao.findByID(districtConfigId);
						} catch (RuntimeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							districtConfig = null;
						} finally{
							//districtDao.close();
						}
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					districtConfig = null ;
				}finally{
					//macconfigDao.close();
				}
				return districtConfig;
		    }
			
			
			private DistrictConfig getCurrentDistrict(String ipaddress , List ipDistrictList){
		    	DistrictConfig districtConfig = null ;
		    	try {
					IPDistrictConfig ipDistrictConfig = getCurrentIPDistrictConfig(ipaddress, ipDistrictList);
					if(ipDistrictConfig == null){
						return districtConfig;
					}
					int districtid = ipDistrictConfig.getDistrictid();
					DistrictDao districtDao = new DistrictDao();
					try {
						districtConfig= (DistrictConfig)districtDao.findByID(String.valueOf(districtid));
					} catch (RuntimeException e) {
						districtConfig = null ;
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally{
						districtDao.close();
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					districtConfig = null ;
				}
		    	return districtConfig;
		    }
			
			
			private IPDistrictConfig getCurrentIPDistrictConfig(String ipaddress , List ipDistrictList){
		    	try {
					long ipaddresslong = ip2long(ipaddress);
					long startiplong = 0L; 
					long endiplong = 0L; 
					for(int i =0 ; i < ipDistrictList.size() ; i ++){
						IPDistrictConfig ipDistrictConfig = (IPDistrictConfig)ipDistrictList.get(i);
						String startip = ipDistrictConfig.getStartip();
						String endip = ipDistrictConfig.getEndip();
						startiplong = ip2long(startip);
						endiplong = ip2long(endip);
						if( startiplong!=0 && endiplong != 0){
							//如果开始网段和结束网段都不为空 则判断该地址是否在网段区域内
							if(ipaddresslong>startiplong && ipaddresslong<endiplong){
								return ipDistrictConfig;
							}
						}else if( startiplong!=0 && endiplong == 0){
							if( ipaddresslong == startiplong ){
								return ipDistrictConfig;
							}
						}
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		    	return null;
		    }
			
			
			private long ip2long(String ip) {
				long result = 0;
				try {
					StringTokenizer st = new StringTokenizer(ip, ".");
					while (st.hasMoreTokens()) {
						String token = st.nextToken();
						int part = Integer.parseInt(token);
						result = result * 256 + part;
					}
				} catch (Exception e) {
					result = 0;
				}
				return result;
			}
			
			
			private boolean getIsMatch(DistrictConfig originalDistrict , DistrictConfig currentDistrict){
		    	boolean isMatch = false;
		    	try {
					if(originalDistrict == null || currentDistrict == null){
						isMatch = false;
						return isMatch;
					}
					int originalDistrictId = originalDistrict.getId();
					int currentDistrictId = currentDistrict.getId();
					
					if(originalDistrictId == currentDistrictId ){
						isMatch = true;
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					isMatch = false;
				}
		    	return isMatch;
		    }
		
		};
	}
}
