package com.afunms.polling.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysUtil;
import com.afunms.config.model.AclBase;
import com.afunms.config.model.AclDetail;
import com.afunms.config.model.CfgBaseInfo;
import com.afunms.config.model.PolicyInterface;
import com.afunms.config.model.QueueInfo;


public class ProcessTelnetCfgData {
	public void analysisCfgData(String result) {

	}

	public boolean processTelnetCfgData(Hashtable<String, List> alldata,
			String ip) {
		DBManager dbmanger = new DBManager();
		String allipstr = SysUtil.doip(ip);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		
		try {
			if (alldata.containsKey("cfgbase")) {
				//类定义和策略定义树据存放在临时表中
				List<CfgBaseInfo> baseInfoList = alldata.get("cfgbase");
				if (baseInfoList != null) {
					StringBuffer sql = new StringBuffer();
					sql.append("delete from baseinfo").append(allipstr);
					dbmanger.addBatch(sql.toString());
					for (int i = 0; i < baseInfoList.size(); i++) {
						CfgBaseInfo info = baseInfoList.get(i);
						if (info != null) {

							sql = new StringBuffer();
							sql.append("insert into baseinfo").append(allipstr).append("(policyName,name,value,type,collecttime) values('");
							sql.append(info.getPolicyName());
							sql.append("','");
							sql.append(info.getName());
							sql.append("','");
							sql.append(info.getValue());
							sql.append("','");
							sql.append(info.getType());
							sql.append("','");
							sql.append(time);
							sql.append("')");
							dbmanger.addBatch(sql.toString());
						}
					}
				}
			}
			if (alldata.containsKey("policy")) {
				List<PolicyInterface> policyList = alldata.get("policy");
				if (policyList != null) {
					for (int i = 0; i < policyList.size(); i++) {
						PolicyInterface vo = policyList.get(i);
						if (vo != null) {
							StringBuffer sql = new StringBuffer();
							sql
									.append("insert into interfacePolicy").append(allipstr).append("(interfaceName,policyName,className,offeredRate,dropRate,matchGroup,matchedPkts,matchedBytes,dropsTotal,dropsBytes,depth,totalQueued,noBufferDrop,collecttime) values('");
							sql.append(vo.getInterfaceName());
							sql.append("','");
							sql.append(vo.getPolicyName());
							sql.append("','");
							sql.append(vo.getClassName());
							sql.append("',");
							sql.append(vo.getOfferedRate());
							sql.append(",");
							sql.append(vo.getDropRate());
							sql.append(",'");
							sql.append(vo.getMatchGroup());
							sql.append("',");
							sql.append(vo.getMatchedPkts());
							sql.append(",");
							sql.append(vo.getMatchedBytes());
							sql.append(",");
							sql.append(vo.getDropsTotal());
							sql.append(",");
							sql.append(vo.getDropsBytes());
							sql.append(",");
							sql.append(vo.getDepth());
							sql.append(",");
							sql.append(vo.getTotalQueued());
							sql.append(",");
							sql.append(vo.getNoBufferDrop());
							sql.append(",'");
							sql.append(time);
							sql.append("')");
							dbmanger.addBatch(sql.toString());
						}
					}
				}
			}
			if (alldata.containsKey("queue")) {
				List<QueueInfo> queueList = alldata.get("queue");
				if (queueList != null) {
					for (int i = 0; i < queueList.size(); i++) {
						QueueInfo vo = queueList.get(i);
						if (vo != null) {
							StringBuffer sql = new StringBuffer();
							sql.append("insert into queueInfo").append(allipstr).append("(entity,inputSize,inputMax,inputDrops,inputFlushes,outputSize,outputMax,outputDrops,outputThreshold,availBandwidth,collecttime) values('");
							sql.append(vo.getEntity());
							sql.append("',");
							sql.append(vo.getInputSize());
							sql.append(",");
							sql.append(vo.getInputMax());
							sql.append(",");
							sql.append(vo.getInputDrops());
							sql.append(",");
							sql.append(vo.getInputFlushes());
							sql.append(",");
							sql.append(vo.getOutputSize());
							sql.append(",");
							sql.append(vo.getOutputMax());
							sql.append(",");
							sql.append(vo.getOutputDrops());
							sql.append(",");
							sql.append(vo.getOutputThreshold());
							sql.append(",");
							sql.append(vo.getAvailBandwidth());
							sql.append(",'");
							sql.append(time);
							sql.append("')");
							dbmanger.addBatch(sql.toString());
						}
					}
				}
			}
			dbmanger.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanger.close();
		}

		return true;
	}
	public boolean processTelnetAclData(Hashtable<String, List<?>> alldata,
			String ip) {
		DBManager dbmanger = new DBManager();
		String allipstr = SysUtil.doip(ip);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		
		try {
			if (alldata.containsKey("base")) {
				//类定义和策略定义树据存放在临时表中
				List<AclBase> baseInfoList = (List<AclBase>) alldata.get("base");
				if (baseInfoList != null) {
					StringBuffer sql = new StringBuffer();
//					sql.append("delete from baseinfo").append(allipstr);
//					dbmanger.addBatch(sql.toString());
					for (int i = 0; i < baseInfoList.size(); i++) {
						AclBase base = baseInfoList.get(i);
						if (base != null) {
							
							sql = new StringBuffer();
							sql.append("insert into sys_gather_aclbase(id,ipaddress,name) values(");
							sql.append(base.getId());
							sql.append(",'");
							sql.append(base.getIpaddress());
							sql.append("','");
							sql.append(base.getName());
							sql.append("')");
							dbmanger.addBatch(sql.toString());
						}
					}
				}
			}
			if (alldata.containsKey("detail")) {
				List<AclDetail> detailList = (List<AclDetail>) alldata.get("detail");
				if (detailList != null) {
					for (int i = 0; i < detailList.size(); i++) {
						AclDetail detail = detailList.get(i);
						if (detail != null) {
							StringBuffer sql = new StringBuffer();
							sql.append("insert into sys_gather_acldetail(baseId,name,value,matches,desciption,status,collecttime) values(");
							sql.append(detail.getBaseId());
							sql.append(",'");
							sql.append(detail.getName());
							sql.append("',");
							sql.append(detail.getValue());
							sql.append(",");
							sql.append(detail.getMatches());
							sql.append(",'");
							sql.append(detail.getDesc());
							sql.append("',");
							sql.append(detail.getStatus());
							sql.append(",'");
							sql.append(detail.getCollecttime());
							sql.append("')");
							dbmanger.addBatch(sql.toString());
						}
					}
				}
			}
			
			dbmanger.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanger.close();
		}
		
		return true;
	}

	public String loadFile() {
		File f = new File(
				"D:/2010-03-17/Tomcat5.0/webapps/afunms/script/cbwfq.log");
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

	public static void main(String[] args) {
		Pattern tmpPt = null;
		Matcher mr = null;
		ProcessTelnetCfgData vo = new ProcessTelnetCfgData();

		String allContent = vo.loadFile();
		List<QueueInfo> queueList = new ArrayList<QueueInfo>();
		vo.parseQueue(allContent, queueList);
		for (int i = 0; i < queueList.size(); i++) {
			QueueInfo info = queueList.get(i);
			System.out.println("bandwidth:" + info.getAvailBandwidth());
		}
		// List<CfgBaseInfo> baseList=new ArrayList<CfgBaseInfo>();
		// List<PolicyInterface> voList = new ArrayList<PolicyInterface>();
		// vo.parseClass(allContent,baseList);
		// vo.parsePolicyInterface(allContent, voList);
		// for (int i = 0; i < voList.size(); i++) {
		// PolicyInterface info = voList.get(i);
		// System.out.println("interfaceName:" + info.getInterfaceName());
		// System.out.println("policyName: " + info.getPolicyName());
		// System.out.println("className: " + info.getClassName());
		// System.out.println("offeredRate: " + info.getOfferedRate());
		// System.out.println("dropRate: " + info.getDropRate());
		// System.out.println("match: " + info.getMatch());
		// System.out.println("matchedPkts: " + info.getMatchedPkts());
		// System.out.println("matchedBytes: " + info.getMatchedBytes());
		// System.out.println("dropsTotal: " + info.getDropsTotal());
		// System.out.println("dropsBytes: " + info.getDropsBytes());
		// System.out.println("/////////////");
		// }
		// String content = "5 minute offered rate 0 bps, drop rate 0 bps\r\n";
		// tmpPt = Pattern.compile("(5 minute offered rate)(.*)(bps,)",
		// Pattern.DOTALL);
		//
		// mr = tmpPt.matcher(allContent);
		// if (mr.find()) {
		// String a = mr.group(2);
		// System.out.println(":" + a.trim() + ":");
		// }
	}

//	public void parseClass(String content, List<CfgBaseInfo> list) {
//		Pattern tmpPt = null;
//		Matcher mr = null;
//		tmpPt = Pattern
//				.compile(
//						"(-----------------begin\\(sh class-map\\)-----------------\n)(.*)(-----------------end\\(sh class-map\\)-----------------\n)",
//						Pattern.DOTALL);
//
//		mr = tmpPt.matcher(content);
//		if (mr.find()) {
//			String block = mr.group(2);
//			String[] lines = block.split("\n");
//			for (int i = 0; i < lines.length; i++) {
//				CfgBaseInfo info = new CfgBaseInfo();
//				info.setName(lines[i].trim());
//				info.setValue(lines[++i].trim());
//				info.setType("class");
//				i++;
//				list.add(info);
//			}
//		}
//
//	}

//	public void parsePolicy(String content, List<CfgBaseInfo> list) {
//		Pattern tmpPt = null;
//		Matcher mr = null;
//		tmpPt = Pattern
//				.compile(
//						"(-----------------begin\\(sh policy-map\\)-----------------\n)(.*)(-----------------end\\(sh policy-map\\)-----------------\n)",
//						Pattern.DOTALL);
//
//		mr = tmpPt.matcher(content);
//		if (mr.find()) {
//			String block = mr.group(2);
//			String[] lines = block.split("Class");
//			for (int i = 1; i < lines.length; i++) {
//				String[] items = lines[i].split("\n");
//				CfgBaseInfo info = new CfgBaseInfo();
//				if (items.length == 3) {
//					info.setName(items[0].trim());
//					info.setValue(items[1].trim());
//					info.setPolicyName(lines[0].trim());
//				} else if (items.length == 4) {
//					info.setName(items[0].trim());
//					info.setPolicyName(lines[0].trim());
//					info.setPriority(items[1].trim());
//					info.setValue(items[2].trim());
//				}
//				list.add(info);
//			}
//		}
//
//	}

//	public void parsePolicyInterface(String content, List<PolicyInterface> list) {
//		Pattern tmpPt = null;
//		Matcher mr = null;
//		tmpPt = Pattern
//				.compile(
//						"(-----------------begin\\(sh policy-map interface\\)-----------------\n)(.*)(-----------------end\\(sh policy-map interface\\)-----------------\n)",
//						Pattern.DOTALL);
//		mr = tmpPt.matcher(content);
//		if (mr.find()) {
//			String block = mr.group(2);
//			String[] totalLines = block.split("\n");
//
//			if (totalLines != null) {
//				StringBuffer realBlock = new StringBuffer();
//				for (int i = 0; i < totalLines.length; i++) {
//
//					realBlock.append(totalLines[i] + "\n");
//					if (totalLines[i]
//							.indexOf("(total queued/total drops/no-buffer drops)") > -1) {
//						String[] lines = realBlock.toString().split(
//								"Class-map:");
//						String[] serNames = null;
//						if (lines != null && lines.length > 1) {
//							serNames = lines[0].split("\n");
//
//							for (int j = 1; j < lines.length; j++) {
//								PolicyInterface vo = new PolicyInterface();
//								if (serNames != null) {
//
//									if (serNames.length == 5)
//										vo.setInterfaceName(serNames[0].trim());
//									vo.setPolicyName(serNames[2].replace(
//											"Service-policy output:", "")
//											.trim());
//								}
//								String[] classLines = lines[j].split("\n");
//								if (classLines != null && classLines.length > 0) {
//									vo.setClassName(classLines[0].trim());
//								}
//								tmpPt = Pattern.compile(
//										"(5 minute offered rate)(.*)(bps,)",
//										Pattern.DOTALL);
//								mr = tmpPt.matcher(lines[j]);
//								if (mr.find()) {
//									String match = mr.group(2);
//									vo.setOfferedRate(Integer.parseInt(match
//											.trim()));
//									// System.out.println(match.trim());
//								}
//								tmpPt = Pattern.compile(
//										"(drop rate)(.*)(bps\n)",
//										Pattern.DOTALL);
//								mr = tmpPt.matcher(lines[j]);
//								if (mr.find()) {
//									String match = mr.group(2);
//									vo.setDropRate(Integer.parseInt(match
//											.trim()));
//									// System.out.println(match.trim()+"///");
//								}
//								tmpPt = Pattern.compile(
//										"(Match:)(.*)(\n      Queueing)",
//										Pattern.DOTALL);
//								mr = tmpPt.matcher(lines[j]);
//								if (mr.find()) {
//									String match = mr.group(2);
//									vo.setMatchGroup(match.trim());
//								}
//								tmpPt = Pattern
//										.compile(
//												"(\\(pkts matched\\/bytes matched\\))(.*)(\n        \\(total drops\\/bytes drops\\))",
//												Pattern.DOTALL);
//								mr = tmpPt.matcher(lines[j]);
//								if (mr.find()) {
//									String match = mr.group(2);
//									String[] values = match.split("\\/");
//									if (values != null && values.length == 2) {
//										vo.setMatchedPkts(Integer
//												.parseInt(values[0].trim()));
//										vo.setMatchedBytes(Integer
//												.parseInt(values[1].trim()));
//									}
//
//								} else {
//									vo.setMatchedPkts(-1);// 代表未找到
//									vo.setMatchedBytes(-1);// 代表未找到
//								}
//								tmpPt = Pattern
//										.compile(
//												"(\\(total drops\\/bytes drops\\))(.*)(\n\n)",
//												Pattern.DOTALL);
//								mr = tmpPt.matcher(lines[j]);
//								if (mr.find()) {
//									String match = mr.group(2);
//									String[] values = match.split("\\/");
//
//									if (values != null && values.length == 2) {
//										vo.setDropsTotal(Integer
//												.parseInt(values[0].trim()));
//										vo.setDropsBytes(Integer
//												.parseInt(values[1].trim()));
//									}
//
//								}
//								// //////////////////
//								tmpPt = Pattern
//										.compile(
//												"(\\(depth\\/total drops\\/no-buffer drops\\))(.*)(\n\n)",
//												Pattern.DOTALL);
//								mr = tmpPt.matcher(lines[j]);
//								if (mr.find()) {
//									String match = mr.group(2);
//									String[] values = match.split("\\/");
//
//									if (values != null && values.length == 3) {
//										vo.setDropsTotal(Integer
//												.parseInt(values[1].trim()));
//										vo.setDropsBytes(Integer
//												.parseInt(values[2].trim()));
//									}
//
//								}
//								// //////////////////
//								tmpPt = Pattern
//										.compile(
//												"(\\(total queued\\/total drops\\/no-buffer drops\\))(.*)(\n)",
//												Pattern.DOTALL);
//								mr = tmpPt.matcher(lines[j]);
//								if (mr.find()) {
//									String match = mr.group(2);
//									String[] values = match.split("\\/");
//
//									if (values != null && values.length == 3) {
//										vo.setDropsTotal(Integer
//												.parseInt(values[1].trim()));
//										vo.setDropsBytes(Integer
//												.parseInt(values[2].trim()));
//									}
//
//								}
//								list.add(vo);
//							}
//						}
//						realBlock = new StringBuffer();
//					}
//				}
//
//			}
//		}
//
//	}

	public void parseQueue(String content, List<QueueInfo> list) {
		Pattern tmpPt = null;
		Matcher mr = null;
		tmpPt = Pattern
				.compile(
						"(-----------------begin\\(sh queue fastEthernet 1\\/0\\)-----------------\n)(.*)(-----------------end\\(sh queue fastEthernet 1\\/0\\)-----------------)",
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
					
					isFind=true;
				}
			}
			tmpPt = Pattern.compile(
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
					info.setAvailBandwidth(Integer.parseInt(availBandwidth.trim()));
					isFind=true;
				}
				
			}
			if(isFind)
			list.add(info);
		}

	}
}
