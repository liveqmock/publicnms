/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

/**
 * Title:        电子商务平台
 * Description:
 * 字符串组件： 替换/格式判断
 * Copyright:    Copyright (c) 2001
 * Company:      中泰信能
 * @author
 * @version 1.0
 */


import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
public class FileViewer{ 
	File myDir;        
	File[] contents;        
	Vector vectorList;        
	Iterator currentFileView;        
	File currentFile;        
	String path;    
	
	public FileViewer(){                
		path=new String("");                                        
		vectorList=new Vector(); 
	}
		public FileViewer(String path){                
			this.path=path;                
			vectorList = new Vector();        
			
		}               
		
		/**         
		 * * 设置浏览的路径        */        
		public void setPath(String path){     
			this.path=path;        
			}               
		
		/***         
		 * * 返回当前目录路径        */        
		public String getDirectory(){                
			return myDir.getPath();       
			}              
		
		/**         
		 * * 刷新列表        */        
		public void refreshList(){               
			if(this.path.equals("")) path="c:\\";
                myDir = new File(path); 
                
				vectorList.clear();             
				contents = myDir.listFiles();              
					 //重新装入路径下文件               
					for(int i=0;i<contents.length;i++){
						vectorList.add(contents[i]);  
					} 
			  //System.out.println("文件的个数为:"+contents.length);
		      currentFileView=vectorList.iterator();        
		    }        

		/**        
		 *  * 移动当前文件集合的指针指到下一个条目         
		 *  * @return 成功返回true,否则false        */       
		public boolean nextFile(){               
			while(currentFileView.hasNext()){                        
				currentFile=(File)currentFileView.next();                        
				return true;                }                
			return false;       
			}  
		
		/**         * 返回当前指向的文件对象的文件名称        */      
		public String getFileName(){                
			return currentFile.getName();        
			}  
		
		/**         * 返回当前指向的文件对象的文件尺寸        */        
		public String getFileSize(){                
			return new Long(currentFile.length()).toString();        
			} 
		
		/**         * 返回当前指向的文件对象的最后修改日期        */               
		public String getFileTimeStampString(){                
			return new Date(currentFile.lastModified()).toString();  
			}     
		
		/**         * 返回当前指向的文件对象的最后修改日期        */               
		public Date getFileTimeStampDate(){                
			return new Date(currentFile.lastModified());  
			} 
		
		/**         * 返回当前指向的文件对象是否是一个文件目录        */      
		public boolean getFileType(){                
			return currentFile.isDirectory();        
			}

   }