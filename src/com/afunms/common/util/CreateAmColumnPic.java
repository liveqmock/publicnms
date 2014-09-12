package com.afunms.common.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.afunms.application.model.DominoDisk;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.system.vo.FlexVo;
import com.afunms.topology.manage.ServiceForFlex;

public class CreateAmColumnPic {
	String[] colorStr = { "#0000FF", "#36DB43", "#3DA4D8", "#556B2F",
			"#8470F4", "#8A2BE2", "#23f266", "#FFA500", "#8B4513", "FFB6C1",
			"#FFF800", "#728699", "#9F005F", "#A52A2A","#0000FF", "#36DB43", "#3DA4D8", "#556B2F", "#23f266" };

	/**
	 * wxy create 临时制作
	 * 
	 * @param ip
	 * @return
	 */
	public String createCpuChartLastWeek(String ip) {

		List<FlexVo> list = new ArrayList<FlexVo>();
		ServiceForFlex service = new ServiceForFlex();
		StringBuffer cpuStr = new StringBuffer();
		list = (ArrayList<FlexVo>) service.getCPUByWeek(ip);
		String cpuData = "";
		if (list != null && list.size() > 0) {
			cpuStr.append("<chart><series>");
			FlexVo fVo = new FlexVo();
			for (int i = 3; i < list.size(); i++) {
				fVo = list.get(i);
				String name = fVo.getObjectName();
				cpuStr.append("<value xid='").append(i).append("'>").append(
						name).append("</value>");
			}
			cpuStr.append("</series><graphs><graph>");
			for (int i = 3; i < list.size(); i++) {
				fVo = list.get(i);
				String value = fVo.getObjectNumber();
				cpuStr.append("<value xid='" + i).append("' color='").append(
						colorStr[i]);
				cpuStr.append("'>");
				cpuStr.append(value + "</value>");
			}
			cpuStr.append("</graph></graphs></chart>");
			cpuData = cpuStr.toString();
		} else {
			cpuData = "0";
		}
		return cpuData;
	}

	/**
	 * wxy add 内存最大、平均利用率 amcharts 统计图
	 * 
	 * @param
	 * @return
	 */
	public String createAmMemoryChart(String ip, Hashtable memhash) {

		Hashtable[] memoryhash = null;
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();
		I_HostCollectData hostmanager = new HostCollectDataManager();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		String starttime = time + " 00:00:00";
		String totime = time + " 23:59:59";
		String[] diskItem = { "AllSize", "UsedSize", "Utilization" };
		String[] diskItemch = { "总容量", "已用容量", "利用率" };

		String[] memoryItem = { "Capability", "Utilization" };
		String[] memoryItemch = { "容量", "当前", "最大", "平均" };
		try {
			memoryhash = hostmanager.getMemory(ip, "Memory", starttime, totime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 各memory最大值
		memmaxhash = memoryhash[1];
		memavghash = memoryhash[2];

		String pmem = "";
		String vmem = "";
		String pcurmem = "";
		String pmaxmem = "";
		String pavgmem = "";
		String vcurmem = "";
		String vmaxmem = "";
		String vavgmem = "";

		for (int k = 0; k < memhash.size(); k++) {
			Hashtable mhash = (Hashtable) (memhash.get(new Integer(k)));
			String name = (String) mhash.get("name");

			for (int j = 0; j < memoryItem.length; j++) {
				String value = "";
				if (mhash.get(memoryItem[j]) != null) {
					value = (String) mhash.get(memoryItem[j]);
					if (j == 0) {
						if ("PhysicalMemory".equals(name))
							pmem = value;
						if ("VirtualMemory".equals(name))
							vmem = value;
					} else {
						if ("PhysicalMemory".equals(name))
							pcurmem = value;
						if ("VirtualMemory".equals(name))
							vcurmem = value;
					}
				}
			}
			String value = "";
			if (memmaxhash.get(name) != null) {
				value = (String) memmaxhash.get(name);
				if ("PhysicalMemory".equals(name))
					pmaxmem = value;
				if ("VirtualMemory".equals(name))
					vmaxmem = value;
			}
			String avgvalue = "";
			if (memavghash.get(name) != null) {
				avgvalue = (String) memavghash.get(name);
				if ("PhysicalMemory".equals(name))
					pavgmem = avgvalue;
				if ("VirtualMemory".equals(name))
					vavgmem = avgvalue;
			}
		}
		if (pcurmem == null || pcurmem.equals(""))
			pcurmem = "0";
		if (pmaxmem == null || pmaxmem.equals(""))
			pmaxmem = "0";
		if (pavgmem == null || pavgmem.equals(""))
			pavgmem = "0";
		if (vcurmem == null || vcurmem.equals(""))
			vcurmem = "0";
		if (vmaxmem == null || vmaxmem.equals(""))
			vmaxmem = "0";
		if (vavgmem == null || vavgmem.equals(""))
			vavgmem = "0";
		pcurmem = pcurmem.replaceAll("%", "");
		pmaxmem = pmaxmem.replaceAll("%", "");
		pavgmem = pavgmem.replaceAll("%", "");
		vcurmem = vcurmem.replaceAll("%", "");
		vmaxmem = vmaxmem.replaceAll("%", "");
		vavgmem = vavgmem.replaceAll("%", "");

		double dpcurmem = new Double(pcurmem);
		double dpmaxmem = new Double(pmaxmem);
		double dpavgmem = new Double(pavgmem);

		double dvcurmem = new Double(vcurmem);
		double dvmaxmem = new Double(vmaxmem);
		double dvavgmem = new Double(vavgmem);

		double[] d_data1 = { dpcurmem, 100 - dpcurmem };
		double[] d_data2 = { dvcurmem, 100 - dvcurmem };

		double[] dmax_data1 = { dpmaxmem, 100 - dpmaxmem };
		double[] dmax_data2 = { dvmaxmem, 100 - dvmaxmem };

		double[] davg_data1 = { dpavgmem, 100 - dpavgmem };
		double[] davg_data2 = { dvavgmem, 100 - dvavgmem };

		StringBuffer xmlStr = new StringBuffer();
		xmlStr.append("<?xml version='1.0' encoding='gb2312'?>");
		xmlStr.append("<chart><series>");
		String[] titleStr = new String[] { "当前物理", "当前虚拟", "平均物理", "平均虚拟",
				"最大物理", "最大虚拟" };
		String[] title = new String[] { "当前已用", "当前未用", "平均已用", "平均未用", "最大已用",
				"最大未用" };

		for (int i = 0; i < 6; i++) {
			xmlStr.append("<value xid='").append(i).append("'>").append(
					titleStr[i]).append("</value>");

		}
		xmlStr.append("</series><graphs>");
		long curp = Math.round(d_data1[0]);
		long curv = Math.round(d_data2[0]);
		long maxp = Math.round(dmax_data1[0]);
		long maxv = Math.round(dmax_data2[0]);
		long avgp = Math.round(davg_data1[0]);
		long avgv = Math.round(davg_data2[0]);

		long[] data = { curp, curv, 100 - curp, 100 - curv, avgp, avgv,
				100 - avgp, 100 - avgv, maxp, maxv, 100 - maxp, 100 - maxv };
		int tempInt = 0, tempId = 0;
		for (int i = 0; i < 6; i++) {
			if (i == 1)
				tempId = 0;
			if (i == 3)
				tempId = 2;
			if (i == 5)
				tempId = 4;
			xmlStr.append("<graph gid='").append(i).append("' title='").append(
					title[i]).append("'>").append(
					"<value xid='" + tempId + "'> " + data[tempInt]).append(
					"</value>");
			xmlStr.append("<value xid='" + (++tempId) + "'>" + data[++tempInt]
					+ "</value>");
			xmlStr.append("</graph>");
			tempId++;
			tempInt++;
		}

		xmlStr.append("</graphs></chart>");
		String temp = xmlStr.toString();

		return temp;
	}
/**
 * cpu利用率详情（aix）
 * @author wxy 
 * @param cpuperfhash
 * @param cpudetailhash
 * @return
 */
	public String createCpuDetailAmChart(Hashtable cpuperfhash,
			Hashtable cpudetailhash) {
		double[] cpu_data1 = null;
		double[] cpu_data2 = null;
		String dataStr="";
		String[] title = new String[] { "usr", "sys", "wio", "idle" };
		String[] cpu_labels = { "当前", "平均" };
		if (cpuperfhash != null && cpuperfhash.size() > 0) {
			cpu_data1 = new double[4];
			cpu_data2 = new double[4];

			String usr = "0";
			String sys = "0";
			String wio = "0";
			String idle = "0";
			if (cpuperfhash != null) {
				usr = (String) cpuperfhash.get("%usr");
				sys = (String) cpuperfhash.get("%sys");
				wio = (String) cpuperfhash.get("%wio");
				idle = (String) cpuperfhash.get("%idle");
			}

			String avgusr = "0";
			String avgsys = "0";
			String avgwio = "0";
			String avgidle = "0";
			cpu_data1[0] = Double.parseDouble(usr);
			cpu_data1[1] = Double.parseDouble(sys);
			cpu_data1[2] = Double.parseDouble(wio);
			cpu_data1[3] = Double.parseDouble(idle);
			
			if (cpudetailhash != null) {
				if (cpudetailhash.containsKey("usr")) {
					Hashtable usrhash = (Hashtable) cpudetailhash.get("usr");
					if (usrhash != null) {
						if (usrhash.containsKey("avgvalue")) {
							avgusr = (String) usrhash.get("avgvalue");
							cpu_data2[0] = Double.parseDouble(avgusr);
						}
						;
					}
				}
				if (cpudetailhash.containsKey("sys")) {
					Hashtable syshash = (Hashtable) cpudetailhash.get("sys");
					if (syshash != null) {
						if (syshash.containsKey("avgvalue")) {
							avgsys = (String) syshash.get("avgvalue");
							cpu_data2[1] = Double.parseDouble(avgsys);
						}
					}
				}
				if (cpudetailhash.containsKey("wio")) {
					Hashtable wiohash = (Hashtable) cpudetailhash.get("wio");
					if (wiohash != null) {
						if (wiohash.containsKey("avgvalue")) {
							avgwio = (String) wiohash.get("avgvalue");
							cpu_data2[2] = Double.parseDouble(avgwio);
						}
					}
				}
				if (cpudetailhash.containsKey("idle")) {
					Hashtable idlehash = (Hashtable) cpudetailhash.get("idle");
					if (idlehash != null) {
						if (idlehash.containsKey("avgvalue")) {
							avgidle = (String) idlehash.get("avgvalue");
							cpu_data2[3] = Double.parseDouble(avgidle);
						}
					}
				}
			}
			StringBuffer xmlStr = new StringBuffer();
			xmlStr.append("<?xml version='1.0' encoding='gb2312'?>");
			xmlStr.append("<chart><series>");

			for (int i = 0; i <cpu_labels.length; i++) {
				xmlStr.append("<value xid='").append(i).append("'>").append(
						cpu_labels[i]).append("</value>");

			}
			xmlStr.append("</series><graphs>");

			for (int i = 0; i <title.length; i++) {
				
				xmlStr.append("<graph gid='"+title[i]+"' title='").append(
						title[i]).append("'>").append(
						"<value xid='0'> " + cpu_data1[i]).append(
						"</value>");
				xmlStr.append("<value xid='1'>" + cpu_data2[i]
						+ "</value>");
				xmlStr.append("</graph>");
				
			}

			xmlStr.append("</graphs></chart>");
			dataStr = xmlStr.toString();
		}else {
			dataStr="0";
		}
		 
		return dataStr;
	}
	/**
	 * cpu利用率详情（linux）
	 * @author wxy 
	 * @param cpuperfhash
	 * @param cpudetailhash
	 * @return
	 */
		public String createLinuxCpuDetailAmChart(Hashtable cpuperfhash,
				Hashtable cpudetailhash) {
			double[] cpu_data1 = null;
			double[] cpu_data2 = null;
			String dataStr="";
			String[] title = new String[] { "user","nice", "system", "iowait","steal", "idle" };
			String[] cpu_labels = { "当前", "平均" };
			if (cpuperfhash != null && cpuperfhash.size() > 0) {
				cpu_data1 = new double[6];
				cpu_data2 = new double[6];

				String user = "0";
				String nice="0";
				String system = "0";
				String iowait = "0";
				String steal="0";
				String idle = "0";
				if (cpuperfhash != null) {
					if(cpuperfhash.containsKey("%user")){
						user = (String) cpuperfhash.get("%user");
					}
					if(cpuperfhash.containsKey("%nice")){
						nice = (String) cpuperfhash.get("%nice");
					}
					if(cpuperfhash.containsKey("%system")){
						system = (String) cpuperfhash.get("%system");
					}
					if(cpuperfhash.containsKey("%iowait")){
						iowait = (String) cpuperfhash.get("%iowait");	
					}
					if(cpuperfhash.containsKey("%steal")){
						steal = (String) cpuperfhash.get("%steal");
					}
					if(cpuperfhash.containsKey("%idle")){
						idle = (String) cpuperfhash.get("%idle");
					}
				}

				String avgusr = "0";
				String avgnice = "0";
				String avgsys = "0";
				String avgwio = "0";
				String avgsteal = "0";
				String avgidle = "0";
				if(null!=user)
				cpu_data1[0] = Double.parseDouble(user);
				if(null!=nice)
				cpu_data1[1] = Double.parseDouble(nice);
				if(null!=system)
				cpu_data1[2] = Double.parseDouble(system);
				if(null!=iowait)
				cpu_data1[3] = Double.parseDouble(iowait);
				if(null!=steal)
				cpu_data1[4] = Double.parseDouble(steal);
				if(null!=idle)
				cpu_data1[5] = Double.parseDouble(idle);
				
				if (cpudetailhash != null) {
					if (cpudetailhash.containsKey("user")) {
						Hashtable usrhash = (Hashtable) cpudetailhash.get("user");
						if (usrhash != null) {
							if (usrhash.containsKey("avgvalue")) {
								avgusr = (String) usrhash.get("avgvalue");
								cpu_data2[0] = Double.parseDouble(avgusr);
							}
							
						}
					}
					if (cpudetailhash.containsKey("nice")) {
		 				Hashtable nicehash = (Hashtable) cpudetailhash
		 						.get("nice");
		 				if (nicehash != null) {
		 					
		 					if (nicehash.containsKey("avgvalue"))
		 						avgnice = (String) nicehash.get("avgvalue");
		 					cpu_data2[1] = Double.parseDouble(avgnice);
		 				}
		 			}
					if (cpudetailhash.containsKey("system")) {
						Hashtable syshash = (Hashtable) cpudetailhash.get("system");
						if (syshash != null) {
							if (syshash.containsKey("avgvalue")) {
								avgsys = (String) syshash.get("avgvalue");
								cpu_data2[2] = Double.parseDouble(avgsys);
							}
						}
					}
					if (cpudetailhash.containsKey("iowait")) {
						Hashtable wiohash = (Hashtable) cpudetailhash.get("iowait");
						if (wiohash != null) {
							if (wiohash.containsKey("avgvalue")) {
								avgwio = (String) wiohash.get("avgvalue");
								cpu_data2[3] = Double.parseDouble(avgwio);
							}
						}
					}
					if (cpudetailhash.containsKey("steal")) {
		 				Hashtable stealhash = (Hashtable) cpudetailhash
		 						.get("steal");
		 				if (stealhash != null) {
		 					
		 					if (stealhash.containsKey("avgvalue"))
		 						avgsteal = (String) stealhash.get("avgvalue");
		 					cpu_data2[4] = Double.parseDouble(avgsteal);
		 				}
		 			}
					if (cpudetailhash.containsKey("idle")) {
						Hashtable idlehash = (Hashtable) cpudetailhash.get("idle");
						if (idlehash != null) {
							if (idlehash.containsKey("avgvalue")) {
								avgidle = (String) idlehash.get("avgvalue");
								cpu_data2[5] = Double.parseDouble(avgidle);
							}
						}
					}
				}
				StringBuffer xmlStr = new StringBuffer();
				xmlStr.append("<?xml version='1.0' encoding='gb2312'?>");
				xmlStr.append("<chart><series>");

				for (int i = 0; i <cpu_labels.length; i++) {
					xmlStr.append("<value xid='").append(i).append("'>").append(
							cpu_labels[i]).append("</value>");

				}
				xmlStr.append("</series><graphs>");

				for (int i = 0; i <title.length; i++) {
					
					xmlStr.append("<graph gid='"+title[i]+"' title='").append(
							title[i]).append("'>").append(
							"<value xid='0'> " + cpu_data1[i]).append(
							"</value>");
					xmlStr.append("<value xid='1'>" + cpu_data2[i]
							+ "</value>");
					xmlStr.append("</graph>");
					
				}

				xmlStr.append("</graphs></chart>");
				dataStr = xmlStr.toString();
			}else {
				dataStr="0";
			}
			 
			return dataStr;
		}
public String createWinDiskChart(Hashtable diskhash) {
	String[] diskItem = { "Utilization" };
	String[] title = { "已使用", "未使用" };
	String dataStr="";
	if (diskhash!=null) {
	
	double[] disk_data1 = new double[diskhash.size()];
	double[] disk_data2 = new double[diskhash.size()];
	String[] disk_labels = new String[diskhash.size()];

	for (int k = 0; k < diskhash.size(); k++) {
		Hashtable dhash = (Hashtable) (diskhash.get(new Integer(k)));

		String name = "";
		if (dhash.get("name") != null) {
			name = (String) dhash.get("name");
		}
		
			String value = "";
			if (dhash.get(diskItem[0]) != null) {

				value = (String) dhash.get(diskItem[0]);
				disk_data1[k] = new Double(value.replaceAll("%", ""));
				disk_data2[k] = 100 - disk_data1[k];
				disk_labels[k] = name;

		}
	}
	StringBuffer xmlStr = new StringBuffer();
	xmlStr.append("<?xml version='1.0' encoding='gb2312'?>");
	xmlStr.append("<chart><series>");

	for (int i = 0; i <disk_labels.length; i++) {
		xmlStr.append("<value xid='").append(i).append("'>").append(
				disk_labels[i]).append("</value>");

	}
	xmlStr.append("</series><graphs>");

	for (int i = 0; i <title.length; i++) {
		xmlStr.append("<graph gid='"+title[i]+"' title='").append(
				title[i]).append("'>");
		if (i==0) {
		for (int j = 0; j < disk_data1.length; j++) {
			xmlStr.append("<value xid='"+j+"'> " + disk_data1[j]).append(
					"</value>");	
			
		}
		}else if (i==1) {
			for (int j = 0; j < disk_data2.length; j++) {
				xmlStr.append("<value xid='"+j+"'> " + disk_data2[j]).append(
						"</value>");	
				
			}
		}
		xmlStr.append("</graph>");
		
	}

	xmlStr.append("</graphs></chart>");
	dataStr = xmlStr.toString();
	}else {
		dataStr="0";
	}
	
	return dataStr;
}
	/**
	 * wxy add 主机磁盘利用率图
	 * 
	 * @param diskhash
	 * @return
	 */
	public String createDiskChart(Hashtable diskhash) {
		String[] diskItem = { "AllSize", "UsedSize", "Utilization" };
		StringBuffer dataStr = new StringBuffer();
		String valueStr = "";
		dataStr.append("<chart><series>");

		if (diskhash != null && diskhash.size() > 0) {

			for (int i = 0; i < diskhash.size(); i++) {
				Hashtable diskhash1 = (Hashtable) (diskhash.get(new Integer(i)));
				String name = (String) diskhash1.get("name");
				dataStr.append("<value xid='").append(i).append("'>").append(
						name).append("</value>");

			}
			dataStr.append("</series><graphs><graph>");

			String value = "0";
			for (int i = 0, j = 0; i < diskhash.size(); i++, j++) {
				Hashtable diskhash1 = (Hashtable) (diskhash.get(new Integer(i)));

				if (diskhash1.get(diskItem[2]) != null) {
					value = (String) diskhash1.get(diskItem[2]);
					value = value.replaceAll("%", "");

				}
				if ((i % colorStr.length) == 0)
					j = 0;
				dataStr.append("<value xid='" + i).append("' color='").append(
						colorStr[j]);
				dataStr.append("'>");
				dataStr.append(value + "</value>");

			}
			dataStr.append("</graph></graphs></chart>");
			valueStr = dataStr.toString();
		} else {
			valueStr = "0";
		}
		return valueStr;
	}

	/**
	 * wxy add 主机磁盘利用率图Top5
	 * 
	 * @param diskhash
	 * @return
	 */
	public String createDiskChartTop5(Hashtable diskhashtable) {
		String[] diskItem = { "AllSize", "UsedSize", "Utilization" };
		StringBuffer dataStr = new StringBuffer();
		String valueStr = "";
		dataStr.append("<chart><series>");
		Hashtable<String, String> diskhash = new Hashtable<String, String>();

		if (diskhashtable != null && diskhashtable.size() > 0) {

			diskhash = this.dealHashTable(diskhashtable);
			int k = 0;
			for (Iterator itr = diskhash.keySet().iterator(); itr.hasNext();) {
				String name = (String) itr.next();

				dataStr.append("<value xid='").append(k++).append("'>").append(
						name).append("</value>");

			}

			dataStr.append("</series><graphs><graph>");

			String value = "0";
			int i = 0, j = 0;
			for (Iterator itr = diskhash.keySet().iterator(); itr.hasNext();) {
				String key = (String) itr.next();
				value = (String) diskhash.get(key);
				if ((i % colorStr.length) == 0)
					j = 0;
				dataStr.append("<value xid='" + i).append("' color='").append(
						colorStr[j]);
				dataStr.append("'>");
				dataStr.append(value + "</value>");
				i++;
				j++;
			}
			dataStr.append("</graph></graphs></chart>");
			valueStr = dataStr.toString();
		} else {
			valueStr = "0";
		}
		return valueStr;
	}

	public Map.Entry<String, String>[] getSortedHashtable(
			Hashtable<String, String> h) {

		Set set = h.entrySet();
		Map.Entry<String, String>[] entries = (Map.Entry[]) set
				.toArray(new Map.Entry[set.size()]);
		Arrays.sort(entries, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				float key1 = Float.parseFloat(((Map.Entry) arg0).getValue()
						.toString());
				float key2 = Float.parseFloat(((Map.Entry) arg1).getValue()
						.toString());
				return ((Comparable) new Float(key2))
						.compareTo(new Float(key1)); // ---当然为了降序排只要把key1和key2互换一下
			}
		});

		return entries;
	}

	public Hashtable<String, String> dealHashTable(Hashtable diskhash) {
		int index = 1;
		String value = "";
		Hashtable<String, String> temp = new Hashtable<String, String>();
		Hashtable<String, String> table = new Hashtable<String, String>();
		for (int i = 0; i < diskhash.size(); i++) {
			Hashtable diskhash1 = (Hashtable) (diskhash.get(new Integer(i)));
			String name = (String) diskhash1.get("name");
			if (diskhash1.get("Utilization") != null) {
				value = (String) diskhash1.get("Utilization");
				value = value.replaceAll("%", "");

			}
			temp.put(name, value);
		}
		for (Map.Entry<String, String> ent : getSortedHashtable(temp)) {

			table.put(ent.getKey(), ent.getValue());
			if (index == 5)
				break;
			index++;
		}
		return table;
	}
	/**
	 * @author wxy
	 * 创建sqlserver各种命中率的柱状图
	 * @param oramem
	 * @return
	 */
public String createSqlUtilChart(String[] data,String[] items) {
	StringBuffer sb = new StringBuffer();
	
	//String[] items = { "缓冲区命中率 ","planCache命中率", "CursorManagerByType命中率","CatalogMetadata命中率" };
	
	sb.append("<chart><series>");
	for (int k = 0; k < items.length; k++) {
		sb.append("<value xid='").append(k).append("'>").append(
				items[k]).append("</value>");
	}
	sb.append("</series><graphs><graph gid='0'>");
	for (int i = 0; i < data.length; i++) {
		sb.append("<value xid='").append(i).append("' color='")
		.append(colorStr[i]).append("'>" + data[i]).append(
				"</value>");
	}
	sb.append("</graph></graphs></chart>");
	String dbdata = sb.toString();
	return dbdata;
}
/**
 * @author wxy
 * 创建sqlserver表空间利用率率的柱状图
 * @param oramem
 * @return
 */
public String createTableSpaceUtilChart(Hashtable dataValue) {
	 String[] usedperc=null;
     String[] usedsize=null;
     String[] size=null;
     String[] dbname=null;
     String dataStr="";
     Hashtable database=new Hashtable();
     String[] dbItems={"usedperc","usedsize","size","dbname"};
     if(dataValue!=null&&dataValue.size()>0){
         database=(Hashtable)dataValue.get("database");
     }
    	 
    
	 if (database != null && database.size() > 0) {
		
            int n=0;
            usedperc=new String[database.size()];
				usedsize=new String[database.size()];
				size=new String[database.size()];
				dbname=new String[database.size()];
      for(Iterator itr = database.keySet().iterator(); itr.hasNext();){ 
         String key = (String) itr.next(); 
         Hashtable tablespace = (Hashtable) database.get(key); 
          if(tablespace!= null && tablespace.size() > 0){
           
				usedperc[n]=(String)tablespace.get(dbItems[0]);
			    usedsize[n]=(String)tablespace.get(dbItems[1]);
			    size[n]=(String)tablespace.get(dbItems[2]);
			    dbname[n]=(String)tablespace.get(dbItems[3]);
			     n++;
							 }
		        }
      StringBuffer sb=new StringBuffer();
      
      String[] colorStr=new String[] {"#33FF33","#FF0033","#9900FF","#FFFF00","#33CCFF","#003366","#33FF33","#FF0033","#9900FF","#FFFF00","#333399","#0000FF","#A52A2A","#800080"};
      sb.append("<chart><series>");
      if(dbname!=null){
      for(int k=0;k<dbname.length;k++){
			sb.append("<value xid='").append(k).append("'>").append(dbname[k]).append("</value>");
			
			}
			}
			sb.append("</series><graphs><graph gid='0'>");
			if(usedperc!=null){
			for(int i=0,j=0;i<usedperc.length;i++,j++){
			if(i/colorStr.length==1)j=0;
			int perInt=Math.round(Float.parseFloat(usedperc[i]));
			sb.append("<value xid='").append(i).append("' color='").append(colorStr[j]).append("'>"+perInt).append("</value>");
			}
			}
			sb.append("</graph></graphs></chart>");
			dataStr=sb.toString();
		        }else {
				dataStr="0";
				 System.out.println("..................");
				}
		        
return dataStr;
}
/**
 * @author wxy
 * 创建sqlserver日志空间利用率率的柱状图
 * @param oramem
 * @return
 */
public String createLogSpaceUtilChart(Hashtable dataValue) {
String[] logname=null;
String logStr="";
String[] usedperc=null;
String[] usedsize=null;
String[] size=null;
String[] dbname=null;
String[] dbItems={"usedperc","usedsize","size","dbname"};
Hashtable    logfile=null;
if(dataValue!=null&&dataValue.size()>0)
    logfile=(Hashtable)dataValue.get("logfile");
      if (logfile != null && logfile.size() > 0) {
    	 
       int i=0;
       usedperc=new String[logfile.size()];
			usedsize=new String[logfile.size()];
			size=new String[logfile.size()];
			logname=new String[logfile.size()];
 for(Iterator itr = logfile.keySet().iterator(); itr.hasNext();){ 
    String key = (String) itr.next(); 
    Hashtable tablespace = (Hashtable) logfile.get(key); 
     if(tablespace!= null && tablespace.size() > 0){
      
			usedperc[i]=(String)tablespace.get(dbItems[0]);
		    usedsize[i]=(String)tablespace.get(dbItems[1]);
		    size[i]=(String)tablespace.get(dbItems[2]);
		    logname[i]=(String)tablespace.get("logname");
		     i++;
						 }
	        }
	        }
	         StringBuffer logBuffer=new StringBuffer();
     
       logBuffer.append("<chart><series>");
       if(dbname!=null){
       for(int k=0;k<logname.length;k++){
			logBuffer.append("<value xid='").append(k).append("'>").append(logname[k]).append("</value>");
			
			}
			}
			logBuffer.append("</series><graphs><graph gid='0'>");
			if(usedperc!=null){
			for(int i=0,j=0;i<usedperc.length;i++,j++){
			if(i/colorStr.length==1)j=0;
			int perInt=Math.round(Float.parseFloat(usedperc[i]));
			logBuffer.append("<value xid='").append(i).append("' color='").append(colorStr[j]).append("'>"+perInt).append("</value>");
			}
			}
			logBuffer.append("</graph></graphs></chart>");
			logStr=logBuffer.toString();
      
	return logStr;	
}
public String createOraTableSpaceUtilChart(Vector tableinfo) {
	String tabledata = "";
	try {
		StringBuffer tablespace = new StringBuffer();
		DecimalFormat df=new DecimalFormat("#.##");
		tabledata = "";
		tablespace.append("<chart><series>");

		String[] filenames = null;
		String[] tablespaces = null;
		String[] tspercents = null;
		String[] size_mbs = null;
		String[] free_mbs = null;
		String[] status = null;
		Hashtable tableHt = null;
		if (tableinfo != null&&tableinfo.size()>0) {
			filenames = new String[tableinfo.size()];
			tablespaces = new String[tableinfo.size()];
			tspercents = new String[tableinfo.size()];
			size_mbs = new String[tableinfo.size()];
			free_mbs = new String[tableinfo.size()];
			status = new String[tableinfo.size()];

			for (int i = 0; i < tableinfo.size(); i++) {
				tableHt = new Hashtable();
				tableHt = (Hashtable) tableinfo.get(i);
				if (tableHt.get("file_name") != null)
					filenames[i] = (String) tableHt.get("file_name");
				if (tableHt.get("tablespace") != null)
					tablespaces[i] = (String) tableHt.get("tablespace");
				if (tableHt.get("size_mb") != null)
					size_mbs[i] = (String) tableHt.get("size_mb");
				if (tableHt.get("free_mb") != null)
					free_mbs[i] = (String) tableHt.get("free_mb");
				if (tableHt.get("status") != null)
					status[i] = (String) tableHt.get("status");

				String value = "0";
				if (tableHt.get("percent_free") != null) {
					value = ((String) tableHt.get("percent_free")).trim();
				}
				value = value.trim();
				String data=df.format(100-Float.parseFloat(value));
				tspercents[i] = data;
				
			}
			for (int i = 0; i < tablespaces.length; i++) {
			tablespace.append("<value xid='").append(i).append("'>")
					.append(tablespaces[i]).append("</value>");
		}
		tablespace.append("</series><graphs><graph gid='0'>");
		for (int i = 0,j=0; i < tspercents.length; i++,j++) {
		  if((Double.valueOf(i) % Double.valueOf(colorStr.length)==0)){
			  j=0;
		  }
			tablespace.append("<value xid='").append(i).append("' color='")
					.append(colorStr[j]).append("'>" + tspercents[i])
					.append("</value>");
			

		}
		tablespace.append("</graph></graphs></chart>");
		tabledata = tablespace.toString();
		}else{
		tabledata="0";
		}
	} catch (Exception e) {
		e.printStackTrace();
	} 
return tabledata;
}
/**
 * SGA
 * @return
 */
public String  createSGAChart(Hashtable oramem) {
	String sgadata = "";
	try {
		String[] sysItem1 = { "shared_pool", "large_pool",
				"DEFAULT_buffer_cache", "java_pool" };
		String[] sysItemch1 = { "共享池", "大型池", "缓冲区高速缓存", "Java池" };
		StringBuffer sga = new StringBuffer();
		sgadata = "";
		sga.append("<chart><series>");
		for (int k = 0; k < sysItem1.length; k++) {
			sga.append("<value xid='").append(k).append("'>").append(
					sysItemch1[k]).append("</value>");
		}

		sga.append("</series><graphs><graph gid='0'>");
		if (oramem != null) {

			for (int i = 0; i < sysItem1.length; i++) {

				String value = "0";
				if (oramem.get(sysItem1[i]) != null) {
					value = (String) oramem.get(sysItem1[i]);
				}
				value = value.replace("MB", "");
				if(value == null || value.equals("null")){
					value = "0";
				}
				int data = Math.round(Float.parseFloat(value));
				sga.append("<value xid='").append(i).append("' color='")
						.append(colorStr[i]).append("'>" + data).append(
								"</value>");
			}
		} else {
			sga.append("</series><graphs><graph gid='0'>");
		}
		sga.append("</graph></graphs></chart>");
		sgadata = sga.toString();
	} catch (Exception e) {
		e.printStackTrace();
	}
	return sgadata;
}
public String  createDominoMem(String cur,String max,String avg) {
	StringBuffer sb=new StringBuffer();
	String netdata="";
	String[] items={"当前利用率","最大利用率","平均利用率"};
	if (cur.equals("")||cur==null) {
		cur="0";
	}
	if (max.equals("")||max==null) {
		max="0";
	}
	if (avg.equals("")||avg==null) {
		avg="0";
	}
	String[] itemValue={cur,max,avg};
		sb.append("<chart><series>");
		for (int i = 0; i < items.length; i++) {
		sb.append("<value xid='").append(i).append("'>").append(items[i]).append("</value>");
		}
		
		sb.append("</series><graphs><graph gid='0'>");
		for (int i = 0; i < itemValue.length; i++) {
			sb.append("<value xid='").append(i).append("' color='").append(colorStr[i]).append("'>"+itemValue[i]).append("</value>");
				
		}
		
		sb.append("</graph></graphs></chart>");
		netdata=sb.toString();
	return netdata;
}
public String createDominoDiskPic(List diskList) {

	String[] diskItem = { "Utilization" };
	String[] title = { "已使用", "未使用" };
	String dataStr="";
	if (diskList!=null&&diskList.size()>0) {
	
	
	
	StringBuffer xmlStr = new StringBuffer();
	xmlStr.append("<?xml version='1.0' encoding='gb2312'?>");
	xmlStr.append("<chart><series>");
String value="0";
double data1[] =new double[diskList.size()];
double data2[] =new double[diskList.size()];
	for (int i = 0; i <diskList.size(); i++) {
		DominoDisk disk=(DominoDisk)diskList.get(i);
		value=disk.getDiskusedpctutil().replaceAll("%", "");
		xmlStr.append("<value xid='").append(i).append("'>").append(
				disk.getDiskname()).append("</value>");
        data1[i]=Double.parseDouble(value);
       data2[i]=100-data1[i];
	}
	xmlStr.append("</series><graphs>");

	for (int i = 0; i <title.length; i++) {
		xmlStr.append("<graph gid='"+title[i]+"' title='").append(title[i]).append("'>");
		if(i==0){
		for (int j = 0; j < data1.length; j++) {
			xmlStr.append("<value xid='"+j+"'> " +data1[j]).append("</value>");
		}
		}else if (i==1) {
			for (int j = 0; j < data1.length; j++) {
				xmlStr.append("<value xid='"+j+"'> " +data2[j]).append("</value>");
			}	
		}	
		xmlStr.append("</graph>");
		
	}

	xmlStr.append("</graphs></chart>");
	dataStr = xmlStr.toString();
	}else {
		dataStr="0";
	}
	return dataStr;

}



/**
 * aix ，linux ，sun 
 * @param ip
 * @param memhash
 * @return
 */
public String getAmMemoryChartStr(String ip, Hashtable memhash) {

	Hashtable[] memoryhash = null;
	Hashtable memmaxhash = new Hashtable();// mem--max
	Hashtable memavghash = new Hashtable();
	I_HostCollectData hostmanager=  new HostCollectDataManager();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String time = sdf.format(new Date());
	String starttime = time + " 00:00:00";
	String totime = time + " 23:59:59";

	String[] memoryItem = { "AllSize", "UsedSize", "Utilization" };
	String[] memoryItemch = { "容量", "当前", "最大", "平均" };
	try {
		memoryhash = hostmanager.getMemory(ip, "Memory", starttime, totime);
	} catch (Exception e) {
		e.printStackTrace();
	}
	// 各memory最大值
	memmaxhash = memoryhash[1];
	memavghash = memoryhash[2];

	String pmem = "";
	String vmem = "";
	String pcurmem = "";
	String pmaxmem = "";
	String pavgmem = "";
	String vcurmem = "";
	String vmaxmem = "";
	String vavgmem = "";

	for (int k = 0; k < memhash.size(); k++) {
		Hashtable mhash = (Hashtable) (memhash.get(new Integer(k)));
		String name = (String) mhash.get("name");

		for (int j = 0; j < memoryItem.length; j++) {
			String value = "";
			if (mhash.get(memoryItem[j]) != null) {
				value = (String) mhash.get(memoryItem[j]);
				if (j == 0) {
					if ("PhysicalMemory".equals(name))
						pmem = value;
					if ("SwapMemory".equals(name))
						vmem = value;
				} else {
					if ("PhysicalMemory".equals(name))
						pcurmem = value;
					if ("SwapMemory".equals(name))
						vcurmem = value;
				}
			}
		}
		String value = "";
		if (memmaxhash.get(name) != null) {
			value = (String) memmaxhash.get(name);
			if ("PhysicalMemory".equals(name))
				pmaxmem = value;
			if ("SwapMemory".equals(name))
				vmaxmem = value;
		}
		String avgvalue = "";
		if (memavghash.get(name) != null) {
			avgvalue = (String) memavghash.get(name);
			if ("PhysicalMemory".equals(name))
				pavgmem = avgvalue;
			if ("SwapMemory".equals(name))
				vavgmem = avgvalue;
		}
	}
	if (pcurmem == null || pcurmem.equals(""))
		pcurmem = "0";
	if (pmaxmem == null || pmaxmem.equals(""))
		pmaxmem = "0";
	if (pavgmem == null || pavgmem.equals(""))
		pavgmem = "0";
	if (vcurmem == null || vcurmem.equals(""))
		vcurmem = "0";
	if (vmaxmem == null || vmaxmem.equals(""))
		vmaxmem = "0";
	if (vavgmem == null || vavgmem.equals(""))
		vavgmem = "0";
	pcurmem = pcurmem.replaceAll("%", "");
	pmaxmem = pmaxmem.replaceAll("%", "");
	pavgmem = pavgmem.replaceAll("%", "");
	vcurmem = vcurmem.replaceAll("%", "");
	vmaxmem = vmaxmem.replaceAll("%", "");
	vavgmem = vavgmem.replaceAll("%", "");

	double dpcurmem = new Double(pcurmem);
	double dpmaxmem = new Double(pmaxmem);
	double dpavgmem = new Double(pavgmem);

	double dvcurmem = new Double(vcurmem);
	double dvmaxmem = new Double(vmaxmem);
	double dvavgmem = new Double(vavgmem);

	double[] d_data1 = { dpcurmem, 100 - dpcurmem };
	double[] d_data2 = { dvcurmem, 100 - dvcurmem };

	double[] dmax_data1 = { dpmaxmem, 100 - dpmaxmem };
	double[] dmax_data2 = { dvmaxmem, 100 - dvmaxmem };

	double[] davg_data1 = { dpavgmem, 100 - dpavgmem };
	double[] davg_data2 = { dvavgmem, 100 - dvavgmem };

	StringBuffer xmlStr = new StringBuffer();
	xmlStr.append("<?xml version='1.0' encoding='gb2312'?>");
	xmlStr.append("<chart><series>");
	String[] titleStr = new String[] { "当前物理", "当前虚拟", "平均物理", "平均虚拟",
			"最大物理", "最大虚拟" };
	String[] title = new String[] { "当前已用", "当前未用", "平均已用", "平均未用", "最大已用",
			"最大未用" };

	for (int i = 0; i < 6; i++) {
		xmlStr.append("<value xid='").append(i).append("'>").append(
				titleStr[i]).append("</value>");

	}
	xmlStr.append("</series><graphs>");
	long curp = Math.round(d_data1[0]);
	long curv = Math.round(d_data2[0]);
	long maxp = Math.round(dmax_data1[0]);
	long maxv = Math.round(dmax_data2[0]);
	long avgp = Math.round(davg_data1[0]);
	long avgv = Math.round(davg_data2[0]);

	long[] data = { curp, curv, 100 - curp, 100 - curv, avgp, avgv,
			100 - avgp, 100 - avgv, maxp, maxv, 100 - maxp, 100 - maxv };
	int tempInt = 0, tempId = 0;
	for (int i = 0; i < 6; i++) {
		if (i == 1)
			tempId = 0;
		if (i == 3)
			tempId = 2;
		if (i == 5)
			tempId = 4;
		xmlStr.append("<graph gid='").append(i).append("' title='").append(
				title[i]).append("'>").append(
				"<value xid='" + tempId + "'> " + data[tempInt]).append(
				"</value>");
		xmlStr.append("<value xid='" + (++tempId) + "'>" + data[++tempInt]
				+ "</value>");
		xmlStr.append("</graph>");
		tempId++;
		tempInt++;
	}

	xmlStr.append("</graphs></chart>");
	String temp = xmlStr.toString();

	return temp;
}





	public static void main(String[] args) {
		Hashtable<String, String> temp = new Hashtable<String, String>();
		temp.put("1AA", "1");
		temp.put("4C1BB", "89");
		temp.put("3C", "12");
		temp.put("2AAAAAAAAA", "6");

		System.out.println(Float.parseFloat("2.0"));
		// CreateAmColumnPic aixColumnPic=new CreateAmColumnPic();
		//	
		// for(Map.Entry<String,String>
		// ent:aixColumnPic.getSortedHashtable(temp)){
		// System.out.println(ent.getKey()+":"+ent.getValue());
		//		  
		// }
	}
}
