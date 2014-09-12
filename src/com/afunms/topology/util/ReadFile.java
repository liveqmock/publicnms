package com.afunms.topology.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadFile {
  public ReadFile() {}

  /**
   * 删除某个文件夹下的所有文件夹和文件
   * @param delpath String
   * @throws FileNotFoundException
   * @throws IOException
   * @return boolean
   */
  public static boolean deletefile(String delpath) throws FileNotFoundException,
      IOException {
    try {

      File file = new File(delpath);
      if (!file.isDirectory()) {
        System.out.println("1");
        file.delete();
      }
      else if (file.isDirectory()) {
        System.out.println("2");
        String[] filelist = file.list();
        for (int i = 0; i < filelist.length; i++) {
          File delfile = new File(delpath + "/" + filelist[i]);
          if (!delfile.isDirectory()) {
            System.out.println("path=" + delfile.getPath());
            System.out.println("absolutepath=" + delfile.getAbsolutePath());
            System.out.println("name=" + delfile.getName());
            delfile.delete();
            System.out.println("删除文件成功");
          }
          else if (delfile.isDirectory()) {
            deletefile(delpath + "/" + filelist[i]);
          }
        }
        file.delete();

      }

    }
    catch (FileNotFoundException e) {
      System.out.println("deletefile()   Exception:" + e.getMessage());
    }
    return true;
  }

  /**
   * 删除某个文件夹下的所有文件夹和文件
   * @param delpath String
   * @throws FileNotFoundException
   * @throws IOException
   * @return boolean
   */
  public List<String> readfile(String filepath) throws FileNotFoundException,
      IOException {
	  List list = new ArrayList();
    try {
      File file = new File(filepath);
      if (!file.isDirectory()) {
        System.out.println("文件");
        System.out.println("path=" + file.getPath());
        System.out.println("absolutepath=" + file.getAbsolutePath());
        System.out.println("name=" + file.getName());

      }
      else if (file.isDirectory()) {
        System.out.println("文件夹");
        String[] filelist = file.list();
        for (int i = 0; i < filelist.length; i++) {
          File readfile = new File(filepath + "/" + filelist[i]);
          if (!readfile.isDirectory()) {
            System.out.println("path=" + readfile.getPath());
            System.out.println("absolutepath=" + readfile.getAbsolutePath());
            System.out.println("name=" + readfile.getName());
            if(!"Thumbs.db".equals(readfile.getName())){
            	list.add(readfile.getName());
            }
            
          }
          else if (readfile.isDirectory()) {
            readfile(filepath + "/" + filelist[i]);
          }
        }

      }

    }
    catch (FileNotFoundException e) {
      System.out.println("readfile()   Exception:" + e.getMessage());
    }
    return list;
  }

  public static void main(String[] args) {
//    File directory = new File("");
//	//System.out.println(System.getProperty("user.dir"));
//	try {
//		readfile(directory.getCanonicalPath().toString()+"\\WebRoot\\resource\\image\\bg");
//	} catch (FileNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	//deletefile("D:/file");
	  
    System.out.println("ok");
  }

}
