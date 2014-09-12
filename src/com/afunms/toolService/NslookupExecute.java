package com.afunms.toolService;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class NslookupExecute {

	private List<String> resulttemp = new ArrayList<String>();

	private static Map<String, List<String>> map = new HashMap<String, List<String>>();

	private String res = null;

	private static Hashtable<String, Process> protable = new Hashtable<String, Process>();

	public synchronized List<String> getResult() {
		return resulttemp;
	}

	public void executeNslookup(String order, String ip, String id) {
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
			protable.put(ip + id, p);
			in = p.getInputStream();
			r = new InputStreamReader(in);
			re = new BufferedReader(r);
			res = null;
			while (true) {
				res = re.readLine();

				if (res == null) {
					overflag.set(0, "true");
					map.put(ip + id + "over", overflag);
					break;
				}
				getResult().add(res);
				map.put(ip + id, getResult());
			}
			if (protable.get(ip + id) != null) {
				protable.get(ip + id).destroy();
				protable.remove(ip + id);
			}
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
			// return result;
		}
	}

	List<String> result = new ArrayList<String>();

	public List<String> readResult(String ip, String id) {
		if (map.get(ip + id) != null) {
			try {
				if (map.get(ip + id + "over").get(0).equals("true")) {
					if (map.get(ip + id).size() == 0) {
						map.get(ip + id).remove(ip + id);
						map.get(ip + id).remove(ip + id + "over");
						return null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			result.clear();
			result.addAll(map.get(ip + id));
			map.get(ip + id).clear();
			return result;
		} else {
			return null;
		}
	}

	public String getRes() {
		return res;
	}

	public void closePing(String ip, String id) {
		Process p = protable.get(ip + id);
		if (protable.get(ip + id) != null) {
			protable.get(ip + id).destroy();
			protable.remove(ip + id);
		}
		if (p != null) {
			p.destroy();
		}
	}
}
