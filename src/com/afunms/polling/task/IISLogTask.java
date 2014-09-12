/*
 * Created on 2005-3-31
 *
 * To change the template for this generated file go tosyst
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.IISLogConfigDao;
import com.afunms.application.model.IISLogConfig;
import com.afunms.common.util.CommonAppUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.initialize.ResourceCenter;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class IISLogTask extends MonitorTask {
	private String filePath;
	private Integer historyRow;
	private Calendar historydate;
	private String historystr;//历史记录中上次读取的日志文件名()。

	private Calendar todaydate;
	private String todaystr;
	
	private boolean endfileflag=true;
	
	
	
	

	//	根据历史纪录找到对应的日志文件
	public boolean init(){
		boolean flag=true;
		try{  
			CreateToday();
			//Conf conf=confmanager.getByTypeName("SYMANTEC_LOG_NAME");//取的是实际日期，以1开始
			//historystr=conf.getValue();

			filePath=CommonAppUtil.getAppName()+"/iislog/"+historystr+".log";

			File file=new File(filePath);
			//循环到今天，且今天的日志文件不存在，返回false			  
			if(!file.exists()){
				System.out.println("today file is not available");
			  	return false;
			}
			//今天或今天以前的某天的日志文件存在，则那一天的文件将被处理，那天的时间赋给类实例变量			  
			else{
				//conf=confmanager.getByTypeName("SYMANTEC_LOG_ROW");
				//historyRow=new Integer(conf.getValue());
				historydate=getCalendar(historystr);
			}
		}catch(Exception e){
			System.out.println("error---"+e.getMessage());
		}
		System.out.println("init return true");
		return true;
	}

	private void CreateToday(){
		todaydate=new GregorianCalendar();//今天
		todaystr=getDateString(todaydate);
	}


	public void importlog(){
		try{
			Vector v=null;
			/*
			 * 如果初始化成功，则可以处理一个存在的日志，否则没找到日志，do nothing。
			 */		
			while(init()){
				v=createVector();
				//		没有新日志记录产生则break	
				if((v.size()==0)&& historystr.equals(todaystr))break;
				//		记录插入数据库失败则break
				//if(!smanager.createSys(v))break;

/*
 * 		当前处理的不是今天的日志，则将日期加1修改日期标志以便继续处理当前日期的第二天日志文件		
 */
				if(!historystr.equals(todaystr)&&isEndfileflag()){
//		天数加1			
					historydate.add(Calendar.DAY_OF_MONTH,1);
					historystr=getDateString(historydate);
//		每读完一天的记录，则把天数加一，读取行数取0。以便读下一个日志文件。		
					updateHis(true);
					this.setEndfileflag(true);
					v=null;
				}
/*
 * 	处理到今天的日志则天数不加1，从历史记录的行数读，处理今天的日志文件中新生成的记录行	
 */	
				else {
					updateHis(false);
				}
		
			}
			//smanager.updateIp();
		}catch(Exception e){
			System.err.println("error2="+e);
		}
	}

	private Vector createVector(){
		Vector vector=new Vector();
		try{
			File file=new File(filePath);
			InputStreamReader read = new InputStreamReader (new FileInputStream(file),"GB2312");

			BufferedReader logFile=new BufferedReader(read);

			String oneRow="";
			int row=0;
			int count=0;
			//	  读取当前日期的日志文件，要根据读取行数定位。	
			while((oneRow = logFile.readLine())!=null){
				row++;
				//从上次读过的行的下一行开始	  
				if(row<=historyRow.intValue()) continue; 
				count++;
				if(count>=10000){
					this.setEndfileflag(false);
					break;
				}
				vector.add(oneRow);
			}
			if(count<10000)this.setEndfileflag(true);
				historyRow=new Integer(row);
				read=null;
				logFile=null;
		}catch(Exception e){
			System.err.println("create Vector error"+e.getMessage());
		}
		return vector;
	}

//更新历史记录，如果是读一个过去的日志则row=0, 如果是读今天的，则 row 从上次记录读
	private boolean updateHis(boolean d){
 		boolean flag=true;
// 		try{
// 			Conf conf=confmanager.getByTypeName("SYMANTEC_LOG_NAME");
// 			conf.setValue(historystr);
// 			confmanager.updateConf(conf);
// 			conf=confmanager.getByTypeName("SYMANTEC_LOG_ROW");
// 			if(d){
// 				conf.setValue("0");
// 			}else conf.setValue(historyRow.toString());
// 			confmanager.updateConf(conf);
// 		}catch(Exception e){
// 			System.out.println("update history error"+e.getMessage());
// 			flag=false;
// 		}
 		return flag;
	}


//将当前日期转化成字符串
	private String getDateString(Calendar date){
		String string="";
		if(date.get(Calendar.MONTH)+1<10)string+="0"+String.valueOf(date.get(Calendar.MONTH)+1);
		else string+=String.valueOf(date.get(Calendar.MONTH)+1);
		if(date.get(Calendar.DAY_OF_MONTH)<10)string+="0"+String.valueOf(date.get(Calendar.DAY_OF_MONTH));
		else string+=String.valueOf(date.get(Calendar.DAY_OF_MONTH));
		string+=String.valueOf(date.get(Calendar.YEAR));
		return string;
	}

//	从历史记录中取得的上次读取日志文件名的字符串，将其转化成calendar,用于与当前日期比较。
	private Calendar getCalendar(String str){
		String temp=str.substring(0,2);
		int month=new Integer(temp).intValue();
		month--;
		temp=str.substring(2,4);
		int day=new Integer(temp).intValue();
		temp=str.substring(4);
		int year=new Integer(temp).intValue();
		Calendar date=new GregorianCalendar(year,month,day);
		return date;
	}



	public  void run(){
		
		IISLogConfigDao configdao = new IISLogConfigDao();
		List<IISLogConfig> iisloglist = new ArrayList<IISLogConfig>();
		
		try{
			try{
				iisloglist = configdao.getIISLogByFlag(new Integer(1));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			if(iisloglist != null && iisloglist.size()>0){
				for (int i = 0; i < iisloglist.size(); i++) {
					
					Calendar date=Calendar.getInstance();
					Date cc = date.getTime();
					long subvalue = cc.getTime()-1000*60*60*24;
					String beforedate = SysUtil.longToTime(subvalue);//yyyy-MM-dd hh:mm:ss
					String[] beforestr = beforedate.split(" ");
					String beforedatestr = beforestr[0];//yyyy-MM-dd
					
					Vector vector = new Vector();
					IISLogConfig iislog = (IISLogConfig)iisloglist.get(i);
					//filePath=CommonAppUtil.getAppName()+"/iislog/"+iislog.getIpaddress()+".log";
					filePath = ResourceCenter.getInstance().getSysPath() + "iislog/"+iislog.getIpaddress()+".log";
					SysLogger.info("======"+filePath);
					File file=new File(filePath);
					//循环到今天，且今天的日志文件不存在，返回false			  
					if(!file.exists()){
						System.out.println("today file is not available");
					  	continue;
					}else{
						SysLogger.info("File "+filePath+" 存在");
					}
					historyRow = iislog.getHistory_row();
					FileInputStream fis = new FileInputStream(filePath);
					InputStreamReader isr=new InputStreamReader(fis);
					BufferedReader br=new BufferedReader(isr);
					String strLine = null;
		    		//读入文件内容
		    		while((strLine=br.readLine())!=null)
		    		{
		    			//fileContent.append(strLine + "\n");
		    			SysLogger.info(strLine);
		    			if(strLine.trim().length()>0 && !strLine.startsWith("#")){
							String[] elements = strLine.split(" ");
							SysLogger.info("elements 长度==========="+elements.length);
							if(elements != null && elements.length==14){
								SysLogger.info(beforedatestr+"===="+elements[0]);
								if(beforedatestr.equalsIgnoreCase(elements[0])){
									//只采集昨天的数据
									vector.add(strLine);
								}
							}
						}
		    			
		    			
		    		}
		    		fis.close();
		    		isr.close();
		    		br.close();
		    		fis= null;
		    		isr=null;
		    		br=null;
					
					InputStreamReader read = new InputStreamReader (new FileInputStream(file),"GB2312");

					BufferedReader logFile=new BufferedReader(read);

					String oneRow="";
					int row=0;
					int count=0;
					//	  读取当前日期的日志文件，要根据读取行数定位。	
					while((oneRow = logFile.readLine())!=null){
						if(oneRow.trim().length()>0 && !oneRow.startsWith("#")){
						String[] elements = oneRow.split(" ");
						if(elements != null && elements.length==14){
							if(beforedatestr.equalsIgnoreCase(elements[0])){
								//只采集昨天的数据
								vector.add(oneRow);
							}
						}
						}
					}
					logFile.close();
					read.close();
					
					
					read=null;
					logFile=null;
					if(count<1000000)this.setEndfileflag(true);
					historyRow=new Integer(row);
					configdao = new IISLogConfigDao();
					try{
						iislog.setHistory_row(historyRow);
						configdao.update(iislog);
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						configdao.close();
					}
					
					//开始插入数据
					if(vector != null && vector.size()>0){
						for(int j=0;j<vector.size();j++){
							String elementstr = (String)vector.get(j);
							//if(elementstr.trim().length()>0 && !elementstr.startsWith("#")){
								//把不是空行和不是以"#"开始的数据插入到表里
								String[] elements = elementstr.split(" ");
								if(elements != null && elements.length==14){
									//只有14个条目的数据才能插入数据库
									//0:date 1:time 2:s-sitename 3:s-ip 4:cs-method
									//5:cs-uri-stem 6:cs-uri-query 7:s-port 8:cs-username
									//9:c-ip 10:cs(User-Agent) 11:sc-status 12:sc-substatus
									//13:sc-win32-status
									//ip = vo.getIpaddress();
									String ip = iislog.getIpaddress();
//				    				String ip1 ="",ip2="",ip3="",ip4="";
//				    				String[] ipdot = ip.split(".");	
//				    				String tempStr = "";
//				    				String allipstr = "";
//				    				if (ip.indexOf(".")>0){
//				    					ip1=ip.substring(0,ip.indexOf("."));
//				    					ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//				    					tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//				    				}
//				    				ip2=tempStr.substring(0,tempStr.indexOf("."));
//				    				ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//				    				allipstr=ip1+ip2+ip3+ip4;
									String allipstr = SysUtil.doip(ip);
									String datetime = elements[0]+" "+elements[1];
									String sql = "insert into iislog"+allipstr+"(recordtime,ssitename,sip,csmethod,csuristem,csuriquery,"+
									"sport,csusername,cip,csagent,scstatus,scsubstatus,scwin32status) values('"+
									datetime+"','"+elements[2]+"','"+elements[3]+"','"+elements[4]+"','"+elements[5]+"','"+elements[6]+"','"+elements[7]+
									"','"+elements[8]+"','"+elements[9]+"','"+elements[10]+"','"+elements[11]+"','"+elements[12]+"','"+elements[13]+"')";
									DBManager conn = new DBManager();
				    		        try{
				    		        	conn.executeUpdate(sql);
				    		        }catch(Exception e){
				    		        	e.printStackTrace();
				    		        }finally{
				    		        	conn.close();
				    		        }
								}
							//}
						}
					}
					try{
						deleteFile(iislog.getIpaddress());
					}catch(Exception e){
						e.printStackTrace();
					}
					
					
					
				}
			}
			//SymantecTask symantecTask=new SymantecTask();
			//symantecTask.importlog();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * @return
	 */
	public boolean isEndfileflag() {
		return endfileflag;
	}

	/**
	 * @param b
	 */
	public void setEndfileflag(boolean b) {
		endfileflag = b;
	}
	
    public void deleteFile(String ipAddress){

		try
		{
			File delFile = new File(CommonAppUtil.getAppName() + "/iislog/" + ipAddress + ".log");
		System.out.println("###开始删除文件："+delFile);
		delFile.delete();
		System.out.println("###成功删除文件："+delFile);
		}
		catch(Exception e)		
		{}
}

}
