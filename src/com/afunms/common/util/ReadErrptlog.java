package com.afunms.common.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Clob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.config.model.Errptlog;
import com.informix.util.dateUtil;

	public class ReadErrptlog {
		
		public List readErrptlog(String filename){
			List list = null;
			StringBuffer stringBuffer = new StringBuffer(100);
			try {
				
				FileReader fileReader = new FileReader(filename);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				
				String str = "";
				while((str = bufferedReader.readLine())!=null){
//					System.out.println(str);
					stringBuffer.append(str);
					stringBuffer.append("\n");
				}
				
				String errptlogContent = "";
				Pattern tmpPt = null;
		    	Matcher mr = null;
				tmpPt = Pattern.compile("(cmdbegin:errpt)(.*)(cmdbegin:end)",Pattern.DOTALL);
				mr = tmpPt.matcher(stringBuffer.toString());
				errptlogContent = mr.group(1);
				System.out.println(errptlogContent);
				list = praseErrptlog(errptlogContent);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(list.size());
			return list;
		}
		
		public List praseErrptlog(String log){
			if(log == null){
				throw new NullPointerException();
			}
			List list = new ArrayList();
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy" , Locale.US);
			String str = "";
			Errptlog errptlog = null;
			String[] str_arr = null;
			String[] logs = log.split("\n");
			String flag = "";
			StringBuffer vpd = null;
			StringBuffer description = null;
			for(int i = 0 ; i < logs.length ; i++){
				try{
				str = logs[i];
				if(str.contains("----------------")){
					if(errptlog!=null){
						errptlog.setVpd(vpd.toString());
						errptlog.setDescriptions(description.toString());
						list.add(errptlog);
					}
					errptlog = new Errptlog();
					vpd = new StringBuffer();
					description = new StringBuffer();
				} else if(str.contains("LABEL:")){
					str_arr = str.split(":");
					if(str_arr.length >1)
						errptlog.setLabels(str_arr[1].trim());
					else
						errptlog.setLabels(str_arr[0].trim());
					flag = "";
				} else if(str.contains("IDENTIFIER:")){
					str_arr = str.split(":");
					if(str_arr.length >1)
						errptlog.setIdentifier(str_arr[1].trim());
					else 
						errptlog.setIdentifier(str_arr[0].trim());
					flag = "";
				} else if(str.contains("Sequence Number:")){
					str_arr = str.split(":");
					if(str_arr.length >1)
						errptlog.setSeqnumber(Integer.parseInt(str_arr[1].trim()));
					else
						errptlog.setSeqnumber(Integer.parseInt(str_arr[0].trim()));
					flag = "";
				} else if(str.contains("Machine Id:")){
					str_arr = str.split(":");
					if(str_arr.length >1)
						errptlog.setMachineid(str_arr[1].trim());
					else
						errptlog.setMachineid(str_arr[0].trim());
					flag = "";
				} else if(str.contains("Node Id:")){
					str_arr = str.split(":");
					if(str_arr.length >1)
						errptlog.setNodeid(str_arr[1].trim());
					else
						errptlog.setNodeid(str_arr[0].trim());
					flag = "";
				} else if(str.contains("Class:") && !str.contains("Resource Class:")){
					str_arr = str.split(":");
					errptlog.setErrptclass(str_arr[1].trim());
					flag = "";
				} else if(str.contains("Type:") && !str.contains("Resource Type:")){
					str_arr = str.split(":");
					errptlog.setErrpttype(str_arr[1].trim());
					flag = "";
				} else if(str.contains("Resource Name:")){
					str_arr = str.split(":");
					errptlog.setResourcename(str_arr[1].trim());
					flag = "";
				} else if(str.contains("Resource Class:")){
					str_arr = str.split(":");
					errptlog.setResourceclass(str_arr[1].trim());
					flag = "";
				} else if(str.contains("Resource Type:")){
					str_arr = str.split(":");
					errptlog.setRescourcetype(str_arr[1].trim());
					flag = "";
				} else if(str.contains("Location:")){
					str_arr = str.split(":");
					errptlog.setLocations(str_arr[1].trim());
					flag = "";
				}  else if(str.contains("Date/Time:")){
					str = str.replace("Date/Time:", "");
					Calendar calendar = Calendar.getInstance();
					try {
						calendar.setTime(sdf.parse(str.trim().replace("BEIST ", "")));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					errptlog.setCollettime(calendar);
					flag = "";
				} else if(str.contains("VPD:")){
					flag = "VPD";
				} else if(str.contains("Description")){
					flag = "Description";
				}
				if("VPD".equals(flag)){
					vpd.append(str);
					vpd.append("\n");
				}
				if("Description".equals(flag)){
					description.append(str);
					description.append("\n");
				}
				}catch(Exception e){
					
				}
			}
			if(errptlog!=null){
				errptlog.setVpd(vpd.toString());
				errptlog.setDescriptions(description.toString());
				list.add(errptlog);
			}
			return list;
		}
		
		/**
		 * @param args
		 */
		public static void main(String[] args) {
			// TODO Auto-generated method stub
			ReadErrptlog readErrptlog = new ReadErrptlog();
			List list = readErrptlog.readErrptlog("D:\\192.168.9.31.log");
			System.out.println(list.size());
//			String s = "Sun Sep 26 17:02:40 BEIST 2010";
//			s = s.replace("BEIST ", "");
//			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy" , Locale.US);
//			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
//			try {
//				Date time = sdf.parse(s);
//				System.out.println(sdf2.format(time));
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			String time1 = sdf.format(new Date());
//			System.out.println(time1);
		}

	}
