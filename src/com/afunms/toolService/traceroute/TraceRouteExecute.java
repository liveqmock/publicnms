package com.afunms.toolService.traceroute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class TraceRouteExecute {
	private List<String> resulttemp = new ArrayList<String>();
	private static Map<String, List<String>> map = new HashMap<String, List<String>>();
	private String res = null;
	private static Hashtable<String,Process> protable=new Hashtable<String,Process>();


	@SuppressWarnings("static-access")
	public synchronized List<String> getResult() {
		return resulttemp;
	}

	public void executeTracert(String order, String ip, String id) {
		//logger.info("执行TRACEROUTE命令:" + order);
		System.out.println("tracert order==>"+order);
		List<String> overflag = new ArrayList<String>();
		overflag.add("true");
		overflag.set(0, "false");
		map.put(ip + id + "over", overflag);
		getResult().clear();
		Runtime t = Runtime.getRuntime();
		InputStream in = null;
		InputStreamReader r = null;
		BufferedReader re = null;
		try {
			Process p = t.exec(order);
			protable.put(ip+id, p);
			in = p.getInputStream();
			r = new InputStreamReader(in);
			re = new BufferedReader(r);
			res = null;
			while (true) {
				res = re.readLine();
				System.out.println("res=====>"+res);
				if (res == null) {
					overflag.set(0, "true");
					map.put(ip + id + "over", overflag);
					break;
				}
				getResult().add(res);
				map.put(ip + id, getResult());
			}
			if(protable.get(ip+id)!=null){
				protable.get(ip+id).destroy();
				protable.remove(ip+id);
			}
//			protable.get(ip+id).destroy();
//			protable.remove(ip+id);
			if (in != null) {
				in.close();
			}
			if (re != null) {
				re.close();
			}
			if (r != null) {
				r.close();
			}

		} catch (IOException e) {
			
		} finally {
			//return result;
		}
	}
	
	public List executeTracert(String order, String ip) {
		//logger.info("执行TRACEROUTE命令:" + order);
		System.out.println("tracert order==>"+order);
		List<String> overflag = new ArrayList<String>();
		overflag.add("true");
		overflag.set(0, "false");
		//map.put(ip + id + "over", overflag);
		List retlist = new ArrayList();
		//getResult().clear();
		Runtime t = Runtime.getRuntime();
		InputStream in = null;
		InputStreamReader r = null;
		BufferedReader re = null;
		try {
			Process p = t.exec(order);
			//protable.put(ip+id, p);
			in = p.getInputStream();
			r = new InputStreamReader(in);
			re = new BufferedReader(r);
			res = null;
			while (true) {
				res = re.readLine();
				System.out.println("res=====>"+res);
				if (res == null) {
					overflag.set(0, "true");
					//map.put(ip + id + "over", overflag);
					break;
				}
				retlist.add(res);
				//getResult().add(res);
				//map.put(ip + id, getResult());
			}
//			if(protable.get(ip+id)!=null){
//				protable.get(ip+id).destroy();
//				protable.remove(ip+id);
//			}
//			protable.get(ip+id).destroy();
//			protable.remove(ip+id);
			if (in != null) {
				in.close();
			}
			if (re != null) {
				re.close();
			}
			if (r != null) {
				r.close();
			}

		} catch (IOException e) {
			
		} finally {
			//return result;
		}
		return retlist;
	}

	List<String> result = new ArrayList<String>();

	public List<String> readResult(String ip, String id) {
		//System.out.println("准备读数据!");
		if (map.get(ip + id) != null) {
			//System.out.println("MAP中有数据,数据SIZE为"+map.get(ip+id).size()+"!");
			try {
				if (map.get(ip + id + "over").get(0).equals("true")) {
					//System.out.println("PING命令执行完毕!");
					if (map.get(ip + id).size() == 0) {
						//System.out.println("MAP读取完毕!");
						map.get(ip + id).remove(ip + id);
						map.get(ip + id).remove(ip + id + "over");
						//System.out.println("删除MAP中的对象!");	 
						return null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			//System.out.println("开始读取数据!");
			result.clear();
			result.addAll(map.get(ip + id));
			map.get(ip + id).clear();
			return result;
		} else {
			//System.out.println("PING空闲,可以读数据!");
			return null;
		}
	}

	public String getRes() {
		return res;
	}
	
	public void closeTracert(String ip,String id){
		Process p=protable.get(ip+id);
//		System.out.println("---------tracert----关闭线程==>"+p);
		if(protable.get(ip+id)!=null){
			protable.get(ip+id).destroy();
			protable.remove(ip+id);
		}
//		protable.get(ip+id).destroy();
//		protable.remove(ip+id);
		if(p!=null){
//			System.out.println("----tracert-------关闭成功------------------");
			p.destroy();
		}
	}
}
