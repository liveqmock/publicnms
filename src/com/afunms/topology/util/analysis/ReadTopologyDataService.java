package com.afunms.topology.util.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.afunms.initialize.ResourceCenter;

public class ReadTopologyDataService {
//	public static final String xmlFilePath = "C:\\Documents and Settings\\moonshine\\My Documents\\Google Talk Received Files\\topologyData_theta-1.xml";
//	public static final String xmlFilePath = "e:\\topologyData_theta-1.xml";
//	public static final String xmlFilePath = "e:\\topologyData_theta.xml";
	public static final String xmlFilePath = ResourceCenter.getInstance().getSysPath() + "/linuxserver/topologyData_theta-1.xml";
	
	public static List<String> fetchAllNameList(String sgsnOrggsn){
		SAXBuilder sb = new SAXBuilder();
		Document doc = null;
		try {
			doc = sb.build(xmlFilePath);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element root = doc.getRootElement();
		List dataXMLList = root.getChildren();
		int flag = 1;
		if("GGSN".equals(sgsnOrggsn) && dataXMLList.size()>=2)
			flag = 2;
		List<Element> sgsnGroupList = ((Element) dataXMLList.get(flag)).getChildren();
		List<String> allSGSNnameList = new ArrayList<String>();
		for(Element sgsnGroup:sgsnGroupList){
			List<Attribute> groupAttributeList = sgsnGroup.getAttributes();
			for(Attribute groupAttribute:groupAttributeList){
				if("name".equals(groupAttribute.getName())){
					allSGSNnameList.add(groupAttribute.getValue());
				}
			}
		}
		return allSGSNnameList;
	}
	
	public static List<IpData> fetchAllIpListbyName(String name,String sgsnOrggsn){
		SAXBuilder sb = new SAXBuilder();
		Document doc = null;
		try {
			doc = sb.build(xmlFilePath);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element root = doc.getRootElement();
		List dataXMLList = root.getChildren();
		int flag = 1;
		if("GGSN".equals(sgsnOrggsn) && dataXMLList.size()>=2)
			flag = 2;
		List<Element> sgsnGroupList = ((Element) dataXMLList.get(flag)).getChildren();
		
		//List<String> ipList = new ArrayList<String>();
		List<IpData> ipDataList = new ArrayList<IpData>(); 
		for(Element sgsnGroup:sgsnGroupList){
			List<Attribute> groupAttributeList = sgsnGroup.getAttributes();
			for(Attribute groupAttribute:groupAttributeList){
				if("name".equals(groupAttribute.getName())){
					if(name.equals(groupAttribute.getValue())){
						List<Element> ipItemList = ((Element)sgsnGroup.getChildren().get(0)).getChildren();
						for(Element ipItem:ipItemList){
							IpData ipData = new IpData();
							ipData.setAddr(ipItem.getAttributeValue("addr"));
							ipData.setAverageRate(ipItem.getAttributeValue("AverageRate"));
							ipData.setBusinessRate(ipItem.getAttributeValue("BusinessRate"));
							ipData.setFlow(ipItem.getAttributeValue("flow"));
							ipData.setPacketLossRate(ipItem.getAttributeValue("PacketLossRate"));
							ipData.setRetransmission(ipItem.getAttributeValue("retransmission"));
							ipData.setPDPRate(ipItem.getAttributeValue("PDPRate"));
							ipDataList.add(ipData);
						}
					}
				}
			}
		}
		return ipDataList;
	}
	
	public static List<LACData> fetchAllLacListbyName(String name,String sgsnOrggsn){
		SAXBuilder sb = new SAXBuilder();
		Document doc = null;
		try {
			doc = sb.build(xmlFilePath);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element root = doc.getRootElement();
		List dataXMLList = root.getChildren();
		int flag = 1;
		if("GGSN".equals(sgsnOrggsn) && dataXMLList.size()>=2)
			flag = 2;
		List<Element> sgsnGroupList = ((Element) dataXMLList.get(flag)).getChildren();
		
		//List<String> lacList = new ArrayList<String>();
		List<LACData> lacDataList = new ArrayList<LACData>();
		for(Element sgsnGroup:sgsnGroupList){
			List<Attribute> groupAttributeList = sgsnGroup.getAttributes();
			for(Attribute groupAttribute:groupAttributeList){
				if("name".equals(groupAttribute.getName())){
					if(name.equals(groupAttribute.getValue())){
						List<Element> LacItemList = ((Element)sgsnGroup.getChildren().get(1)).getChildren();
						for(Element lacItem:LacItemList){
							LACData lacData = new LACData();
							lacData.setValue(lacItem.getAttributeValue("value"));
							lacData.setAverageRate(lacItem.getAttributeValue("AverageRate"));
							lacData.setBusinessRate(lacItem.getAttributeValue("BusinessRate"));
							lacData.setFlow(lacItem.getAttributeValue("flow"));
							lacData.setPacketLossRate(lacItem.getAttributeValue("PacketLossRate"));
							lacData.setRetransmission(lacItem.getAttributeValue("retransmission"));
							lacData.setPDPRate(lacItem.getAttributeValue("PDPRate"));
							lacDataList.add(lacData);
						}
					}
				}
			}
		}
		return lacDataList;
	}
	
	public static GroupData fetchDetailInfobyName(String name,String sgsnOrggsn){
		
		//List<GroupData> groupDataList = new ArrayList<GroupData>();
		
		SAXBuilder sb = new SAXBuilder();
		Document doc = null;
		try {
			doc = sb.build(xmlFilePath);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element root = doc.getRootElement();
		List dataXMLList = root.getChildren();
		int flag = 1;
		if("GGSN".equals(sgsnOrggsn) && dataXMLList.size()>=2)
			flag = 2;
		List<Element> sgsnGroupList = ((Element) dataXMLList.get(flag)).getChildren();
		GroupData groupData = new GroupData();
		for(Element sgsnGroup:sgsnGroupList){
			groupData.setName(sgsnGroup.getAttributeValue("name"));
			groupData.setPDPRate(sgsnGroup.getAttributeValue("PDPRate"));
			groupData.setAverageRate(sgsnGroup.getAttributeValue("AverageRate"));
			groupData.setBusinessRate(sgsnGroup.getAttributeValue("BusinessRate"));
			groupData.setFlow(sgsnGroup.getAttributeValue("flow"));
			groupData.setPacketLossRate(sgsnGroup.getAttributeValue("PacketLossRate"));
			groupData.setRetransmission(sgsnGroup.getAttributeValue("retransmission"));
			groupData.setIpDatas(fetchAllIpListbyName(name,sgsnOrggsn));
			groupData.setLACDatas(fetchAllLacListbyName(name,sgsnOrggsn));
			//groupDataList.add(groupData);
		}
		
		return groupData;
	}
	
	public static void main(String[] args){
		System.out.println("-------------------------");
		for(String temp:fetchAllNameList("SGSN")){
			System.out.println(temp);
		}
		System.out.println("-------------------------");
		for(String temp:fetchAllNameList("GGSN")){
			System.out.println(temp);
		}
		System.out.println("-------------------------");
		for(IpData ipdata:fetchAllIpListbyName("HZSGSN2","SGSN")){
			System.out.println(ipdata.getAddr());
		}
		System.out.println("-------------------------");
		for(LACData temp:fetchAllLacListbyName("HZSGSN2","SGSN")){
			System.out.println(temp.getValue());
		}
		System.out.println("-------------------------");
		
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getName());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getFlow());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getPDPRate());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getBusinessRate());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getAverageRate());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getPacketLossRate());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getRetransmission());
		
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getIpDatas().size());
		
		
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getIpDatas().get(1).getAddr());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getIpDatas().get(1).getFlow());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getIpDatas().get(1).getPDPRate());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getIpDatas().get(1).getBusinessRate());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getIpDatas().get(1).getAverageRate());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getIpDatas().get(1).getPacketLossRate());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getIpDatas().get(1).getRetransmission());
		
		
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getLACDatas().get(1).getValue());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getLACDatas().get(1).getFlow());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getLACDatas().get(1).getPDPRate());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getLACDatas().get(1).getBusinessRate());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getLACDatas().get(1).getAverageRate());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getLACDatas().get(1).getPacketLossRate());
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getLACDatas().get(1).getRetransmission());
		
		
		System.out.println(fetchDetailInfobyName("HZSGSN2","SGSN").getLACDatas().size());
		
		
		System.out.println("-------------------------");
	}
}
