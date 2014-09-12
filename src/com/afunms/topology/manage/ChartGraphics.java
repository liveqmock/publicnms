/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.manage;

import com.afunms.common.util.SysLogger;
import com.afunms.discovery.DiscoverDataHelper;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.topology.util.NodeHelper;
import com.afunms.topology.util.XmlOperator;
import com.sun.image.codec.jpeg.JPEGCodec; 
import com.sun.image.codec.jpeg.JPEGImageEncoder; 

import java.io.BufferedOutputStream; 
import java.io.FileInputStream;
import java.io.FileOutputStream; 
import java.util.List;
import java.awt.image.BufferedImage; 
import java.awt.*; 
import javax.swing.ImageIcon; 
import java.util.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class ChartGraphics { 

	private final String headBytes = "<%@page contentType=\"text/html; charset=GB2312\"%>\r\n";
	private SAXBuilder builder;
	private FileInputStream fis;
	private FileOutputStream fos;	
	private XMLOutputter serializer;
	private String fullPath;
	
	protected Document doc;
	protected Element root;
	protected Element nodes;
	protected Element lines;
	protected Element assistantLines;
	
	private DiscoverDataHelper helper = new DiscoverDataHelper();
	
	
	BufferedImage image; 

	void createImage(String fileLocation) { 
		try { 
			FileOutputStream fos = new FileOutputStream(fileLocation); 
			BufferedOutputStream bos = new BufferedOutputStream(fos); 
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos); 
			encoder.encode(image); 
			bos.close(); 
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
	} 

	public void graphicsGeneration(String flag) { 

		int imageWidth = 500;//图片的宽度 

		int imageHeight = 400;//图片的高度 


		//改成这样: 
		BufferedImage bimg = null;
		
	 	   try
		   {				
			   fis = new FileInputStream(fullPath = ResourceCenter.getInstance().getSysPath() + "resource/xml/network.jsp");				   
			   fis.skip(headBytes.getBytes().length);			
			   builder = new SAXBuilder();
			   doc = builder.build(fis);
			   
			   root = doc.getRootElement();
			   nodes = root.getChild("nodes");		
			   lines = root.getChild("lines");	
			   assistantLines = root.getChild("assistant_lines");		
				
				//找出最大宽度与最大高度
				List list = nodes.getChildren();
				Hashtable<Integer,Vector<Integer>> nodesxy = new Hashtable();
				for (int i = 0; i < list.size(); i++)
				{
					Element eleNode = (Element) list.get(i);
					String x = eleNode.getChildText("x");
					x = x.replace("px", "");
					String y = eleNode.getChildText("y");
					y = y.replace("px", "");
					if(imageWidth < Integer.parseInt(x))imageWidth = Integer.parseInt(x);
					if(imageHeight < Integer.parseInt(y))imageHeight = Integer.parseInt(y);
					Vector<Integer> xy = new Vector();
					xy.add(0, Integer.parseInt(x));
					xy.add(1, Integer.parseInt(y));
					int nodeid = Integer.valueOf(eleNode.getChildText("id").substring(3)).intValue();//yangjun xiugai 
					nodesxy.put(nodeid, xy);
				}
				image = new BufferedImage(imageWidth+100, imageHeight+100, BufferedImage.TYPE_INT_RGB); 
				Graphics graphics = image.getGraphics(); 
				graphics.setColor(Color.WHITE);
				graphics.fillRect(0, 0, imageWidth+100, imageHeight+100); 
				graphics.setColor(Color.BLACK);
			   
				List linkList = lines.getChildren();
				for (int i = 0; i < linkList.size(); i++)
				{			
					Element eleLine = (Element) linkList.get(i);
					int a = Integer.parseInt(eleLine.getChildText("a").substring(3));//yangjun
					int b = Integer.parseInt(eleLine.getChildText("b").substring(3));//yangjun
					Vector aXY = (Vector)nodesxy.get(a);
					int ax = Integer.parseInt(aXY.get(0)+"");
					int ay = Integer.parseInt(aXY.get(1)+"");
					Vector bXY = (Vector)nodesxy.get(b);
					int bx = Integer.parseInt(bXY.get(0)+"");
					int by = Integer.parseInt(bXY.get(1)+"");
					graphics.setColor(Color.GREEN);
					graphics.drawLine(ax+10, ay+10, bx+10, by+10);
				}
				
				List alinkList = assistantLines.getChildren();
				for (int i = 0; i < alinkList.size(); i++)
				{			
					Element eleLine = (Element) alinkList.get(i);
					int a = Integer.parseInt(eleLine.getChildText("a").substring(3));//yangjun
					int b = Integer.parseInt(eleLine.getChildText("b").substring(3));//yangjun
					Vector aXY = (Vector)nodesxy.get(a);
					int ax = Integer.parseInt(aXY.get(0)+"");
					int ay = Integer.parseInt(aXY.get(1)+"");
					Vector bXY = (Vector)nodesxy.get(b);
					int bx = Integer.parseInt(bXY.get(0)+"");
					int by = Integer.parseInt(bXY.get(1)+"");
					graphics.setColor(Color.BLUE);
					graphics.drawLine(ax+20, ay+10, bx+20, by+10);
				}				
				
				graphics.setColor(Color.BLACK);
		
				for (int i = 0; i < list.size(); i++)
				{			
					Element eleNode = (Element) list.get(i);
					int nodeid = Integer.valueOf(eleNode.getChildText("id").substring(3)).intValue();
					com.afunms.polling.base.Node node = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByID(nodeid);			
					if(node==null)
					{
						SysLogger.info("发现一个被删除的节点，ID=" + nodeid);
						continue;
					}
					String alias = eleNode.getChild("alias").getText();
					String ip = eleNode.getChild("ip").getText();			
					String info = eleNode.getChild("info").getText();
					String x = eleNode.getChildText("x");
					x = x.replace("px", "");
					String y = eleNode.getChildText("y");
					y = y.replace("px", "");
					String img = ResourceCenter.getInstance().getSysPath()+"resource/"+eleNode.getChildText("img");
					try { 
						bimg = javax.imageio.ImageIO.read(new java.io.File(img));
						graphics.drawImage(bimg, Integer.parseInt(x), Integer.parseInt(y), null);
						if(flag.equals("0")){
							//别名图
							graphics.drawString(alias, Integer.parseInt(x)-20, Integer.parseInt(y)+40);
						}else{
							//IP图
							graphics.drawString(ip, Integer.parseInt(x)-20, Integer.parseInt(y)+40);
						}
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				graphics.drawString(" 网 络 拓 扑 图", imageWidth/2-10, imageHeight+70);
				graphics.dispose(); 

				createImage(ResourceCenter.getInstance().getSysPath()+"/topology/network/network_topo.jpg"); 
			   
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
			   //SysLogger.error("Error in XmlOperator.init4updateXml(),file=" + fullPath);
		   }

	} 

	public static void main(String[] args) { 
		ChartGraphics cg = new ChartGraphics(); 
		try { 
			//cg.graphicsGeneration("ewew", "1", "12", "C:/7.jpg"); 
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
	} 
} 
