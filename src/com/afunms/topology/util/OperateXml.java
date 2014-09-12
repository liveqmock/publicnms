package com.afunms.topology.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.afunms.common.util.SysLogger;
import com.afunms.initialize.ResourceCenter;
import com.afunms.topology.model.NodeModel;
import java.io.File;

/**
 * 
 * @description TODO
 * @author wxy
 * @version Dec 15, 2011 2:17:13 PM
 */
public class OperateXml {

	private final String headBytes = "<%@page contentType=\"text/html; charset=GB2312\"%>\r\n";

	private FileInputStream fis;

	private FileOutputStream fos;

	private XMLOutputter serializer;

	private SAXBuilder builder;

	private String fullPath;

	protected Document doc;

	protected Element root;

	protected Element nodes;

	protected Element contents;

	public OperateXml(String filename) {
		 fullPath=ResourceCenter.getInstance().getSysPath()+"/resource/xml/"+filename;
		//fullPath = "D:/" + filename;
	}

	/**
	 * 保存文件
	 */
	public void writeXml() {
		try {
			Format format = Format.getCompactFormat();
			format.setEncoding("GB2312");
			format.setIndent("	");
			serializer = new XMLOutputter(format);
			fos = new FileOutputStream(fullPath);
			fos.write(headBytes.getBytes());
			serializer.output(doc, fos);
			fos.close();
		} catch (Exception e) {
			System.out.println("//" + e);
		}
	}

	/**
	 * 创建一个新的xml
	 */
	public void createXml() {
		root.addContent(nodes);
		doc = new Document(root);
		writeXml();
	}

	public void addContent(String value) {

		Element content = new Element("content");
		content.setText(value);

		contents.addContent(content);
	}

	public void addNode(List<NodeModel> list) {
		Element node = null;
		Element id = null;
		Element x = null;
		Element y = null;
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				NodeModel nodeModel = list.get(i);
				if (nodeModel != null) {
					node = new Element("node");
					id = new Element("id");
					x = new Element("x");
					y = new Element("y");
					id.setText(nodeModel.getId());
					x.setText(nodeModel.getX() + "");
					y.setText(nodeModel.getY() + "");
					node.addContent(id);
					node.addContent(x);
					node.addContent(y);
					nodes.addContent(node);
				}
			}
		}

	}

	public void addNode(NodeModel nodeModel) {
		Element node = null;
		Element id = null;
		Element x = null;
		Element y = null;
		if (nodeModel != null) {
			node = new Element("node");
			id = new Element("id");
			x = new Element("x");
			y = new Element("y");
			id.setText(nodeModel.getId());
			x.setText("3");
			y.setText("2");
			node.addContent(id);
			node.addContent(x);
			node.addContent(y);
			nodes.addContent(node);
		}

	}

	public void buildXml(List<NodeModel> list) {
		root = new Element("root");
		nodes = new Element("nodes");

		if (list != null) {
				addNode(list);
		}
		createXml();
	}

	/**
	 * 准备更新一个新的xml
	 */
	public boolean init4updateXml() {
		File file = new File(fullPath);
		boolean flag = false;
		if (file.exists()) {
			flag = true;
			try {
				fis = new FileInputStream(fullPath);
				fis.skip(headBytes.getBytes().length);
				builder = new SAXBuilder();
				doc = builder.build(fis);
				root = doc.getRootElement();
				nodes = root.getChild("nodes");

			} catch (Exception e) {
				e.printStackTrace();
				SysLogger.error("Error in XmlOperator.init4updateXml(),file=" + fullPath);
			}
		}
		return flag;
	}

	// 修改节点信息
	public void updateNode(String nodeId, String tag, String txt) {
		List eleNodes = nodes.getChildren();
		for (int i = 0; i < eleNodes.size(); i++) {
			Element ele = (Element) eleNodes.get(i);
			if (ele.getChildText("id").equals(nodeId)) {
				ele.getChild(tag).setText(txt);
				break;
			}
		}
	}
	// 修改一组节点信息
	public void updateNodes(List<NodeModel> list) {
		List eleNodes = nodes.getChildren();
		if(list!=null){
		for (int i = 0; i < eleNodes.size(); i++) {
			Element ele = (Element) eleNodes.get(i);
			
			for (int j = 0; j < list.size(); j++) {
				NodeModel nodeModel=list.get(j);
				if (nodeModel!=null) {
					if (ele.getChildText("id").equals(nodeModel.getId())) {
						ele.getChild("x").setText(nodeModel.getX()+"");
						ele.getChild("y").setText(nodeModel.getY()+"");
						break;
					}	
				}
			}
			
		}
		}
	}
	// 追加节点信息
	public void appendNode(NodeModel nodeModel) {
		boolean flag = false;
		List eleNodes = nodes.getChildren();
		for (int i = 0; i < eleNodes.size(); i++) {
			Element ele = (Element) eleNodes.get(i);
			if (ele.getChildText("id").equals(nodeModel.getId())) {
				flag = true;
			}
		}
		if (!flag) {
			addNode(nodeModel);
		}
		writeXml();
	}

	// 追加一组节点信息
	public void appendNode(List<NodeModel> list) {
		boolean flag = false;
		List eleNodes = nodes.getChildren();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				NodeModel nodeModel = list.get(i);
				if (nodeModel != null) {
					addNode(nodeModel);
				}
			}
		}
		writeXml();
	}

	// 节点信息
	public Hashtable<String,NodeModel> showNode() {
		Vector<String> idsVec = new Vector<String>();
		Hashtable<String, NodeModel> hashtable=new Hashtable<String, NodeModel>();
		List eleNodes = nodes.getChildren();
		for (int i = 0; i < eleNodes.size(); i++) {
			Element ele = (Element) eleNodes.get(i);
			String id = ele.getChildText("id");
			String x = ele.getChildText("x");
			String y = ele.getChildText("y");
			NodeModel nodeModel=new NodeModel();
			nodeModel.setId(id);
			nodeModel.setX(Integer.parseInt(x.replace("px", "")));
			nodeModel.setY(Integer.parseInt(y.replace("px", "")));
			hashtable.put(id, nodeModel);
			idsVec.add(id);
		}
		return hashtable;
	}
	/**
	 * 保存xml文件(用于拓扑图上的"保存"按钮)
	 */
	public void saveContent(String content) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(fullPath);
			osw = new OutputStreamWriter(fos, "GB2312");
			osw.write("<%@page contentType=\"text/html; charset=GB2312\"%>\r\n"
					+ content);
		} catch (Exception e) {
			SysLogger.error("XmlOperator.imageToXml()", e);
		} finally {
			try {
				osw.close();
			} catch (Exception ee) {
			}
		}
	}
	public static void main(String[] args) {
		try {

			OperateXml j2x = new OperateXml("aa.jsp");
			NodeModel nodeModel = new NodeModel();
			nodeModel.setId("2");
			nodeModel.setX(3);
			List<NodeModel> list = new ArrayList<NodeModel>();
			list.add(nodeModel);

			boolean flag = j2x.init4updateXml();
			if (flag) {
				j2x.showNode();
			//	j2x.appendNode(list);
			} else {
				j2x.buildXml(list);
			}
			System.out.println("生成 mxl 文件...");
			// j2x.buildXml();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
