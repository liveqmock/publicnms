package com.afunms.application.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

public class ConvertVideoUtil {
	public String Convert(String path,String fname){
		if(!checkfile(path + fname)){  
			System.out.println(path+" is not file");  
			return "";  
		}   
		return process(path,fname);
	}
	
	private String process(String path,String fname){   
		int type = checkContentType( path + fname);  
		String status = "";  
		if(type == 0){  
			status = processFLV(path,fname);// 直接将文件转为flv文件

		}else if(type == 1){  
			String avifilepath = processAVI(path,fname);
			avifilepath = avifilepath.substring(0,avifilepath.lastIndexOf("\\"));
			String avifilename = avifilepath.substring(avifilepath.lastIndexOf("\\"));
			System.out.println("##########"+avifilepath+"###"+avifilename);
			if(avifilepath == null){
				System.out.println("avi文件没有得到");
				return "";// avi文件没有得到  
			}
	  		status = processFLV(avifilepath,avifilename);// 将avi转为flv  
		}
		return status;  
	}  
	
	private static int checkContentType(String path) {  
		String type = path.substring(path.lastIndexOf(".") + 1,path.length()).toLowerCase();  
		//ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）  
		if(type.equals("avi")) {  
			return 0;  
		}else if(type.equals("mpg")) {  
			return 0;  
		}else if(type.equals("wmv")) {  
			return 0;  
		}else if(type.equals("3gp")) {  
			return 0;  
		}else if(type.equals("mov")) {  
			return 0;  
		}else if(type.equals("mp4")) {  
			return 0;  
		}else if(type.equals("asf")) {  
			return 0;  
		}else if(type.equals("asx")) {  
			return 0;  
		}else if(type.equals("flv")) {  
			return 0;  
		}  
		//对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等), 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.  
		else if(type.equals("wmv9")) {  
			return 1;  
		}else if(type.equals("rm")) {  
			return 1;  
		}else if(type.equals("rmvb")) {  
			return 1;  
		}   
		return 9;  
	}  
	   
	private static boolean checkfile(String path){  
		File file=new File(path);  
		if(!file.isFile()){  
			return false;  
		}  
		return true;  
	}  
	
	// 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等), 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.  
	private static String processAVI(String path,String fname) {  
		List <String> commend=new java.util.ArrayList <String>();
		String back = path + fname.split(".") + ".avi";
		System.out.println("转成AVI格式...");
		commend.add( path + "ffmpeg\\mencoder");  
		commend.add(path);  
		commend.add("-oac");  
		commend.add("lavc");  
		commend.add("-lavcopts");  
		commend.add("acodec=mp3:abitrate=64");  
		commend.add("-ovc");  
		commend.add("xvid");  
		commend.add("-xvidencopts");  
		commend.add("bitrate=600");  
		commend.add("-of");  
		commend.add("avi");  
		commend.add("-o");  
		commend.add(back);  
		try{  
			ProcessBuilder builder = new ProcessBuilder();  
			builder.command(commend);  
			builder.redirectErrorStream(true); 
			Process p = builder.start();
			InputStreamReader isr = new  InputStreamReader(p.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String lineRead;     
			while ((lineRead = br.readLine()) != null) { 
				// swallow the line, or print it out - System.out.println(lineRead);     } 
			}
			if(p.waitFor() != 0){
				System.out.println("AVI格式转换异常结束...");
				return back;
			}else{
				System.out.println("AVI格式转换完成");
				return back;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;  
		}
	}  
	
	// ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）  
	private static String processFLV(String oldfilepath,String fname) {  
		System.out.println("转成FLV格式...");
		if(!checkfile(oldfilepath + fname)){  
			System.out.println(oldfilepath+" is not file");  
			return "";  
		}   
		List <String> commend=new java.util.ArrayList <String>();  
		commend.add( oldfilepath + "ffmpeg\\ffmpeg");
		commend.add("-i");
		commend.add(oldfilepath + fname);
		commend.add("-ab");
		commend.add("56");
		commend.add("-ar");
		commend.add("22050");
		commend.add("-qscale");
		commend.add("8");
		commend.add("-r");
		commend.add("15");
		commend.add("-s");
		commend.add("600x500");
		System.out.println(oldfilepath + fname.substring(0,fname.lastIndexOf(".")) + ".flv");
		commend.add(oldfilepath + fname.substring(0,fname.lastIndexOf(".")) + ".flv");
		try {
//			Runtime runtime = Runtime.getRuntime();
//			Process proce = null;
//			String cmd = "";
//			String cut = "D:\\ffmpeg\\ffmpeg.exe -i "
//					+ oldfilepath
//					+ " -y -f image2 -ss 8 -t 0.001 -s 600x500 D:\\ffmpeg\\output\\"
//					+ "b.jpg";
//			String cutCmd = cmd + cut;
//			proce = runtime.exec(cutCmd);
			ProcessBuilder builder = new ProcessBuilder(commend);
			builder.command(commend);
			builder.redirectErrorStream(true); 
			Process p = builder.start();
			InputStreamReader isr = new  InputStreamReader(p.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String lineRead;     
			while ((lineRead = br.readLine()) != null) { 
				// swallow the line, or print it out - System.out.println(lineRead);     } 
			}
			if(p.waitFor() != 0){
				System.out.println("FLV格式转换异常");
				return "";
			}
			else{
				System.out.println("FLV格式转换完成");
				return fname.substring(0,fname.lastIndexOf(".")) + ".flv";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
