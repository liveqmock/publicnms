package com.afunms.polling.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.afunms.common.util.SysLogger;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.om.Task;

public class TaskXml  {
	private  FileInputStream fi = null;
	private  FileOutputStream fo = null;
	private  SAXBuilder sb;
	private  Document doc;
	private  File file;
	private  String filename;
	private  String path;
	private  String commonPath = ResourceCenter.getInstance().getSysPath()+"task";
	private  Element root;	
	private  List elements;
	private  boolean flag=true;//指定路径文件不存在时为假，否则为真
	
	
	public TaskXml(){
		filename="/task.xml";
		path=commonPath+filename;
		//System.out.println("getSysPath()---"+path);		
	}
	
	public void createDir(){
		File dir=new File(commonPath);
		if(!dir.exists()){//检查Sub目录是否存在
		dir.mkdir();
		System.out.println("Sub目录存在");
		}
	}
	
	public void init(){
		try{
		setFlag(true);	
		fi = new FileInputStream(path);
		SAXBuilder sb = new SAXBuilder();
		doc = sb.build(fi);
		root = doc.getRootElement();
		fi.close();
		elements = root.getChildren();	
		}
			catch(Exception e){
			System.err.println(e+"error");
			}	
	}
	
	public void finish(){
		try{
	    Format format = Format.getCompactFormat();
		format.setEncoding("GBK");
		format.setIndent("   ");
		XMLOutputter outp = new XMLOutputter(format);
		fo=new FileOutputStream(path);
	    outp.output(doc,fo);
	    fo.close();
		}
			catch(Exception e){
			System.err.println(e+"error");
			}	
	}
			
	public void AddXML(Task  t )throws Exception{
		try{
		createDir();
		File file=new File(path);
		if(!file.exists())//文件不存在则创建
		{file.createNewFile();
		 root=new Element("Tasks");
		 elements = root.getChildren();
		 doc = new Document(root);
		}
		else init();//文件存在则读取
		
	Element newelement= new Element("Task");
	Element new1=null;

	new1= new Element("taskname");
	new1.setText(t.getTaskname());
	newelement.addContent(new1);
	
	new1= new Element("startsign");
	new1.setText(t.getStartsign());
	newelement.addContent(new1);
	
	new1= new Element("modify");
	new1.setText("0");
	newelement.addContent(new1);
	
	new1= new Element("polltime");
	new1.setText(t.getPolltime().toString());
	newelement.addContent(new1);

	new1= new Element("polltimeunit");
	new1.setText(t.getPolltimeunit());
	newelement.addContent(new1);
		
	elements.add(newelement);//增加子元素
    
    finish();
	}
	catch(Exception e){
	System.err.println(e+" error in add");
	}
	}
	
	public boolean DelXML(String name)throws Exception{//用返回的布尔值判断文件是否存在。
	try{
		  init();
		  fi.close();
		  Integer k;	
		  if((k=FindXml(name))==null){
			if(!getFlag()){
			return false;//文件不存在返假
		    }
		  }
		  else{				
		      elements.remove(k.intValue());
		      finish();
		  }
	}
	catch(Exception e){
	System.err.println(e+" error in del");
	}
	return true;
	}

	public boolean EditXML(Task  t)throws Exception{//用返回的布尔值判断文件是否存在。
	try{
	 Integer k;	
	 if((k=FindXml(t.getTaskname()))==null){
	 	if(!getFlag()){
	 		return false;//文件不存在返假
	 	}
	 }
	 else{
     init();
	 fi.close();
	 Element editelement=(Element)elements.get(k.intValue());
	 Element edit1=editelement.getChild("startsign");	
	 Element edit2=editelement.getChild("polltime");
	 Element edit4=editelement.getChild("polltimeunit"); 
	 Element edit3=editelement.getChild("modify");

	 if(!(t.getStartsign().equals(edit1.getText()))||
	 !(t.getPolltime().toString().equals(edit2.getText()))||
	 !(t.getPolltimeunit().equals(edit4.getText()))) edit3.setText("1"); 

	 edit1.setText(t.getStartsign());

	 edit2.setText(t.getPolltime().toString());
	 
	 edit4.setText(t.getPolltimeunit());
     finish();
	}
	}
	catch(Exception e){
	System.err.println(e+" error in edit");
	}
	return true;//文件存在返真
	}
	
	private Integer FindXml(String name){
		Integer k=null;
		File file=new File(path);
	  try{
		 if(!file.exists())//文件不存在，设标志为假
			{
		 	//System.out.println("3************************");
		 	//System.out.println("this file hasn't exist.");
				setFlag(false);
			 }
		else{
	    init();
		for(int j=0;j<elements.size();j++)
		{	Element editelement=(Element)elements.get(j);
			String nametemp=editelement.getChildText("taskname");
		    if(name.equals(nametemp)){
			k=new Integer(j);
			break;
		  }
		}
	  fi.close();
	  }	
	 }
		catch(Exception e){
		System.err.println(e+" error in find");
		}	
		return k ;//k为空，则没有查找到指定记录。
	}


   public Task GetXml(String name){//由于有返回值，根据flag来判断文件是否存在
   	Task task=null;
   	Integer k;
	File file=new File(path);
	try{
	    if(!file.exists()){
	    	//文件不存在，设标志为假
	    	setFlag(false);
			SysLogger.info("this file hasn't exist.");
	    }else if((k=FindXml(name))!=null){//文件存在，且找到记录
		    init();
			Element  element=(Element)elements.get(k.intValue()); 
			task=new Task();
			task.setTaskname(element.getChildText("taskname"));
			task.setStartsign(element.getChildText("startsign"));
			task.setModify(element.getChildText("modify"));
			task.setPolltime(new Float(element.getChildText("polltime")));
			task.setPolltimeunit(element.getChildText("polltimeunit"));
			//System.out.println("---in get---"+element.getChildText("polltimeunit"));
			fi.close();
		}
	}catch(Exception e){
		e.printStackTrace();
	}	
    return task;
   }

    public List ListXml(){//由于有返回值，根据flag来判断文件是否存在
    	List list=new ArrayList();
		try{
			File file=new File(path);			
			if(!file.exists())
			{   setFlag(false);			
//				System.out.println("this file "+path+file.getName()+" hasn't exist.");
				System.out.println("this file hasn't exist.");
				}
		else{ 
        init();
        list=new ArrayList();
        for(int j=0;j<elements.size();j++){
			Task t=new Task();
		  	t.setTaskname(((Element)(elements.get(j))).getChildText("taskname"));
		  	t.setStartsign(((Element)(elements.get(j))).getChildText("startsign"));
		  	t.setModify(((Element)(elements.get(j))).getChildText("modify"));
		  	t.setPolltime(new Float(((Element)(elements.get(j))).getChildText("polltime")));
			t.setPolltimeunit(((Element)(elements.get(j))).getChildText("polltimeunit"));
		    list.add(t);
		   } 
		   fi.close();
		  }
	  
		 }
		  catch(Exception e){
		  System.err.println(e+" error in list");
		  }  
		  return list; 	
     }

	/**
	 * @return
	 */
	public boolean getFlag() {
		return flag;
	}

	/**
	 * @param b
	 */
	public void setFlag(boolean b) {
		flag = b;
	}

	public String getPath() {
		return path;
	}

}
