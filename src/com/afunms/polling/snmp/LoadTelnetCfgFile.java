package com.afunms.polling.snmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.config.model.CfgBaseInfo;
import com.afunms.config.model.PolicyInterface;
import com.afunms.config.model.QueueInfo;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;

public class LoadTelnetCfgFile {
//	public String time="";
//	public LoadTelnetCfgFile(){
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		 time = sdf.format(new Date());
//	}
	public Hashtable<String, List> dealCfgData(String result,String filename,String[] commands,String ip,String type){
		Hashtable<String, List> alldata=new Hashtable<String, List>();
		List<CfgBaseInfo> classList=new ArrayList<CfgBaseInfo>();
		 List<PolicyInterface> policyList=new ArrayList<PolicyInterface>();
		 List<QueueInfo> queueList=new ArrayList<QueueInfo>();
		 result=loadFile(filename);
		 if (commands!=null) {
			for (int i = 0; i < commands.length; i++) {
				if (commands[i].trim().equals("sh class-map")) {
					this.parseClass(result, classList);
				}else if (commands[i].trim().equals("sh policy-map")) {
					 this.parsePolicy(result, classList);
				}else if (commands[i].trim().indexOf("sh policy-map interface")>-1) {
					 this.parsePolicyInterface(result, policyList);	
				}else if (commands[i].trim().indexOf("sh queue")>-1) {
					 this.parseQueue(result,queueList,commands[i].trim());
				}
			}
		}
		 
		
		
		
		 alldata.put("cfgbase", classList);
		 alldata.put("policy", policyList);
		 alldata.put("queue", queueList);
		 try{
			 Host node = (Host)PollingEngine.getInstance().getNodeByIP(ip);
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET,type,"droprate");
				for(int i = 0 ; i < list.size(); i++){
					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
					CheckEventUtil checkutil = new CheckEventUtil();
					checkutil.checkData(node,alldata,"net",type,alarmIndicatorsnode);
				}
				List dropBytesList = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET,type,"dropbytes");
				for(int i = 0 ; i < dropBytesList.size(); i++){
					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)dropBytesList.get(i);
					CheckEventUtil checkutil = new CheckEventUtil();
					checkutil.checkData(node,alldata,"net",type,alarmIndicatorsnode);
				}
		    }catch(Exception e)
					{
		    	e.printStackTrace();
		    }
		 return alldata;
	}
	public String loadFile(String filename) {
		File f = new File(filename);
		StringBuffer content = new StringBuffer();
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String s = null;
			while ((s = br.readLine()) != null) {
				content.append(s + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}
	public void parseClass(String content, List<CfgBaseInfo> list) {
		Pattern tmpPt = null;
		Matcher mr = null;
		tmpPt = Pattern
				.compile(
						"(-----------------begin\\(sh class-map\\)-----------------\n)(.*)(-----------------end\\(sh class-map\\)-----------------\n)",
						Pattern.DOTALL);

		mr = tmpPt.matcher(content);
		if (mr.find()) {
			String block = mr.group(2);
			String[] lines = block.split("\n");
			for (int i = 0; i < lines.length; i++) {
				CfgBaseInfo info = new CfgBaseInfo();
				info.setName(lines[i].trim());
				info.setValue(lines[++i].trim());
				info.setType("class");
				//info.setCollecttime(time);
				i++;
				list.add(info);
			}
		}

	}

	public void parsePolicy(String content, List<CfgBaseInfo> list) {
		Pattern tmpPt = null;
		Matcher mr = null;
		tmpPt = Pattern
				.compile(
						"(-----------------begin\\(sh policy-map\\)-----------------\n)(.*)(-----------------end\\(sh policy-map\\)-----------------\n)",
						Pattern.DOTALL);

		mr = tmpPt.matcher(content);
		if (mr.find()) {
			String block = mr.group(2);
			String[] lines = block.split("Class");
			for (int i = 1; i < lines.length; i++) {
				String[] items = lines[i].split("\n");
				CfgBaseInfo info = new CfgBaseInfo();
				if (items.length == 3) {
					info.setName(items[0].trim());
					info.setValue(items[1].trim());
					info.setPolicyName(lines[0].trim());
					info.setType("policy");
					//info.setCollecttime(time);
				} else if (items.length == 4) {
					info.setName(items[0].trim());
					info.setPolicyName(lines[0].trim());
					info.setPriority(items[1].trim());
					info.setValue(items[2].trim());
					info.setType("policy");
					//info.setCollecttime(time);
				}
				list.add(info);
			}
		}

	}

	public void parsePolicyInterface(String content, List<PolicyInterface> list) {
		Pattern tmpPt = null;
		Matcher mr = null;
		tmpPt = Pattern
				.compile(
						"(-----------------begin\\(sh policy-map interface\\)-----------------\n)(.*)(-----------------end\\(sh policy-map interface\\)-----------------\n)",
						Pattern.DOTALL);
		mr = tmpPt.matcher(content);
		if (mr.find()) {
			String block = mr.group(2);
			String[] totalLines = block.split("\n");

			if (totalLines != null) {
				StringBuffer realBlock = new StringBuffer();
				for (int i = 0; i < totalLines.length; i++) {

					realBlock.append(totalLines[i] + "\n");
					if (totalLines[i]
							.indexOf("(total queued/total drops/no-buffer drops)") > -1) {
						String[] lines = realBlock.toString().split(
								"Class-map:");
						String[] serNames = null;
						if (lines != null && lines.length > 1) {
							serNames = lines[0].split("\n");

							for (int j = 1; j < lines.length; j++) {
								PolicyInterface vo = new PolicyInterface();
								if (serNames != null) {

									if (serNames.length == 5)
										vo.setInterfaceName(serNames[0].trim());
									vo.setPolicyName(serNames[2].replace(
											"Service-policy output:", "")
											.trim());
								}
								String[] classLines = lines[j].split("\n");
								if (classLines != null && classLines.length > 0) {
									vo.setClassName(classLines[0].trim());
								}
								tmpPt = Pattern.compile(
										"(5 minute offered rate)(.*)(bps,)",
										Pattern.DOTALL);
								mr = tmpPt.matcher(lines[j]);
								if (mr.find()) {
									String match = mr.group(2);
									vo.setOfferedRate(Integer.parseInt(match
											.trim()));
									// System.out.println(match.trim());
								}else {
									vo.setOfferedRate(-1);
								}
								tmpPt = Pattern.compile(
										"(drop rate)(.*)(bps\n)",
										Pattern.DOTALL);
								mr = tmpPt.matcher(lines[j]);
								if (mr.find()) {
									String match = mr.group(2);
									vo.setDropRate(Integer.parseInt(match
											.trim()));
									// System.out.println(match.trim()+"///");
								}else {
									vo.setDropRate(-1);
								}
								tmpPt = Pattern.compile(
										"(Match:)(.*)(\n      Queueing)",
										Pattern.DOTALL);
								mr = tmpPt.matcher(lines[j]);
								if (mr.find()) {
									String match = mr.group(2);
									vo.setMatchGroup(match.trim());
								}
								tmpPt = Pattern
										.compile(
												"(\\(pkts matched\\/bytes matched\\))(.*)(\n        \\(total drops\\/bytes drops\\))",
												Pattern.DOTALL);
								mr = tmpPt.matcher(lines[j]);
								if (mr.find()) {
									String match = mr.group(2);
									String[] values = match.split("\\/");
									if (values != null && values.length == 2) {
										vo.setMatchedPkts(Integer
												.parseInt(values[0].trim()));
										vo.setMatchedBytes(Integer
												.parseInt(values[1].trim()));
									}

								} else {
									vo.setMatchedPkts(-1);// 代表未找到
									vo.setMatchedBytes(-1);// 代表未找到
								}
								tmpPt = Pattern
										.compile(
												"(\\(total drops\\/bytes drops\\))(.*)(\n\n)",
												Pattern.DOTALL);
								mr = tmpPt.matcher(lines[j]);
								if (mr.find()) {
									String match = mr.group(2);
									String[] values = match.split("\\/");

									if (values != null && values.length == 2) {
										vo.setDropsTotal(Integer
												.parseInt(values[0].trim()));
										vo.setDropsBytes(Integer
												.parseInt(values[1].trim()));
									}

								}else {
									vo.setDropsBytes(-1);
								}
								// //////////////////
								tmpPt = Pattern
										.compile(
												"(\\(depth\\/total drops\\/no-buffer drops\\))(.*)(\n\n)",
												Pattern.DOTALL);
								mr = tmpPt.matcher(lines[j]);
								if (mr.find()) {
									String match = mr.group(2);
									String[] values = match.split("\\/");

									if (values != null && values.length == 3) {
										vo.setDepth(Integer
												.parseInt(values[0].trim()));
										vo.setDropsTotal(Integer
												.parseInt(values[1].trim()));
										vo.setNoBufferDrop(Integer
												.parseInt(values[2].trim()));
									}

								}else {
									vo.setDepth(-1);
									vo.setNoBufferDrop(-1);
								}
								// //////////////////
								tmpPt = Pattern
										.compile(
												"(\\(total queued\\/total drops\\/no-buffer drops\\))(.*)(\n)",
												Pattern.DOTALL);
								mr = tmpPt.matcher(lines[j]);
								if (mr.find()) {
									String match = mr.group(2);
									String[] values = match.split("\\/");

									if (values != null && values.length == 3) {
										vo.setTotalQueued(Integer
												.parseInt(values[0].trim()));
										vo.setDropsTotal(Integer
												.parseInt(values[1].trim()));
										vo.setNoBufferDrop(Integer
												.parseInt(values[2].trim()));
									}

								}else {
									vo.setTotalQueued(-1);
									vo.setNoBufferDrop(-1);
								}
								//vo.setCollecttime(time);
								list.add(vo);
							}
						}
						realBlock = new StringBuffer();
					}
				}

			}
		}
	  
	}

	public void parseQueue(String content, List<QueueInfo> list,String command) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String cmd=command.replaceAll("\\/", "\\\\/");
		tmpPt = Pattern
				.compile(
						"(-----------------begin\\("+cmd+"\\)-----------------\n)(.*)(-----------------end\\("+cmd+"\\)-----------------)",
						Pattern.DOTALL);
		mr = tmpPt.matcher(content);
		if (mr.find()) {
			String availBandwidth = "";
			String block = mr.group(2);
			tmpPt = Pattern
					.compile("(Available Bandwidth)(.*)(kilobits\\/sec)",
							Pattern.DOTALL);
			mr = tmpPt.matcher(block);
			QueueInfo info = new QueueInfo();
			info.setEntity(cmd.replace("sh queue", "").trim());
			boolean isFind=false;
			if (mr.find()) {
				availBandwidth = mr.group(2);
				info.setAvailBandwidth(Integer.parseInt(availBandwidth
						.trim()));
				isFind=true;
			}
			tmpPt = Pattern.compile(
					"(Input queue:)(.*)(\\(size\\/max\\/drops\\/flushes\\))",
					Pattern.DOTALL);
			mr = tmpPt.matcher(block);
			
			if (mr.find()) {
				
				String match = mr.group(2);

				String[] items = match.trim().split("\\/");
				if (items != null && items.length == 4) {
					info.setInputSize(Integer.parseInt(items[0]));
					info.setInputMax(Integer.parseInt(items[1]));
					info.setInputDrops(Integer.parseInt(items[2]));
					info.setInputFlushes(Integer.parseInt(items[3]));
					
				}
				isFind=true;
			}
			tmpPt = Pattern
					.compile(
							"(Output queue:)(.*)(\\(size\\/max total\\/threshold\\/drops\\))",
							Pattern.DOTALL);
			mr = tmpPt.matcher(block);
			if (mr.find()) {
				String match = mr.group(2);
				String[] items = match.trim().split("\\/");
				if (items != null && items.length == 4) {
					info.setOutputSize(Integer.parseInt(items[0]));
					info.setOutputMax(Integer.parseInt(items[1]));
					info.setOutputThreshold(Integer.parseInt(items[2]));
					info.setOutputDrops(Integer.parseInt(items[3]));

				}
				
				isFind=true;
			}
			
			if (isFind) {
				list.add(info);
			}
		}

	}
}
