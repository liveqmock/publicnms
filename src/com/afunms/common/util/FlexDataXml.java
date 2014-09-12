package com.afunms.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.afunms.topology.util.NodeHelper;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.initialize.ResourceCenter;
import com.afunms.system.vo.FlexVo;

/**
 * 提供flex页面的xml数据文件
 * 
 * @Author DHCC-huangguolong
 * @Date 2009-12-17
 */
public class FlexDataXml {
	private File file;
	private String fileName;
	private String fileDir;
	private String filePath;

	public FlexDataXml(String dir, String type) {
		if (dir != null) {
			this.fileDir = ResourceCenter.getInstance().getSysPath() + "flex/data/" + dir + "/";
		} else {
			this.fileDir = ResourceCenter.getInstance().getSysPath() + "flex/data/";
		}
		this.filePath =  this.fileDir + type + ".xml";
	}

	/**
	 * 提供flex页面的xml图表数据文件
	 * 
	 * @Author DHCC-huangguolong
	 * @Date 2009-12-17
	 */
	@SuppressWarnings("unchecked")
	public void buildXml(List list, int topNum) {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			this.createDir();
			File file = new File(this.filePath);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = this.setXmlParam(list, topNum);
			Document doc = new Document(root);
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outputter = new XMLOutputter(format);

			out = new FileOutputStream(file);
			outputter.output(doc, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提供flex页面的xml图表数据文件具体方法
	 * 
	 * @Author DHCC-huangguolong
	 * @Date 2009-12-17
	 */
	@SuppressWarnings("unchecked")
	public void buildDBListXml(List list) {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			this.createDir();
			File file = new File(this.filePath);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			Element root = this.setDBListXmlParam(list);
			Document doc = new Document(root);
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("   ");
			XMLOutputter outputter = new XMLOutputter(format);

			out = new FileOutputStream(file);
			outputter.output(doc, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createDir() {
		File dir = new File(this.fileDir);
		if (!dir.exists()) { // 检查目录是否存在
			dir.mkdir();
		}
	}

	/**
	 * 提供flex页面的xml数据库监视数据文件
	 * 
	 * @Author DHCC-huangguolong
	 * @Date 2009-12-20
	 */
	@SuppressWarnings( { "unchecked", "unused" })
	private Element setXmlParam(List list, int topNum) throws Exception {
		Element root = new Element("data");
		List elements = root.getChildren(); // 得到根元素所有子元素的集合
		for (int i = 0; i < list.size(); i++) {
			if (i >= topNum) {
				break;
			}
			Element newElement = new Element("item");
			List childElements = newElement.getChildren();

			FlexVo vo = (FlexVo) list.get(i);
			newElement.setAttribute("name", vo.getObjectName());
			Element _newelement = new Element("value");
			_newelement.addContent(vo.getObjectNumber());
			childElements.add(_newelement);
			elements.add(newElement);
		}
		return root;
	}

	/**
	 * 提供flex页面的xml数据库监视数据文件具体方法
	 * 
	 * @Author DHCC-huangguolong
	 * @Date 2009-12-20
	 */
	@SuppressWarnings("unchecked")
	private Element setDBListXmlParam(List list) throws Exception {
		Element root = new Element("data");
		List elements = root.getChildren(); // 得到根元素所有子元素的集合
		for (int i = 0; i < list.size(); i++) {
			Element newElement = new Element("item");
			List childElements = newElement.getChildren();

			DBVo dBVo = (DBVo) list.get(i);
			String manageFlag = (dBVo.getManaged() == 0 ? "未监视" : "已监视");

			DBTypeDao typedao = new DBTypeDao();
			int dbtype = dBVo.getDbtype();
			DBTypeVo typevo = (DBTypeVo) typedao.findByID(dbtype + "");

			String imgFile = "/resource/" + NodeHelper.getStatusImage(dBVo.getStatus());

			childElements.add(new Element("id").addContent(dBVo.getId() + ""));
			childElements.add(new Element("no").addContent(i + 1 + ""));
			childElements.add(new Element("alias").addContent(dBVo.getAlias()));
			childElements.add(new Element("dbType").addContent(typevo.getDbtype()));
			childElements.add(new Element("dbName").addContent(dBVo.getDbName()));
			childElements.add(new Element("ipAddress").addContent(dBVo.getIpAddress()));
			childElements.add(new Element("port").addContent(dBVo.getPort()));
			childElements.add(new Element("imgSrc").addContent(imgFile));
			childElements.add(new Element("manageFlag").addContent(manageFlag));

			elements.add(newElement);
		}
		return root;
	}

	/**
	 * xml转换成字符串方法
	 * 
	 * @Author DHCC-huangguolong
	 * @Date 2009-12-20
	 */
	private String outputToString(Document document) {
		ByteArrayOutputStream byteRep = new ByteArrayOutputStream();
		Format format = Format.getCompactFormat();
		format.setEncoding("UTF-8");
		format.setIndent("   ");
		XMLOutputter docWriter = new XMLOutputter(format);
		try {
			docWriter.output(document, byteRep);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String testXML = byteRep.toString();
		return byteRep.toString();
	}
}