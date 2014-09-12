package com.afunms.topology.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class ExcelUtil {
	/**读取Excel文件的内容  
	* @param file  待读取的文件  
	* @return  
	*/  
	public static List readExcel(File file){   
	    StringBuffer sb = new StringBuffer();  
	    
	    List returnList = new ArrayList();
	    
	    Workbook wb = null;   
	    try {   
	        //构造Workbook（工作薄）对象   
	        wb=Workbook.getWorkbook(file);   
	    } catch (BiffException e) {   
	        e.printStackTrace();   
	    } catch (IOException e) {   
	        e.printStackTrace();   
	    }   
	       
	    if(wb==null)   
	        return null;   
	       
	    try {
			//获得了Workbook对象之后，就可以通过它得到Sheet（工作表）对象了   
			Sheet[] sheet = wb.getSheets();   
			   
			if(sheet!=null&&sheet.length>0){   
			    //对每个工作表进行循环   
			    for(int i=0;i<sheet.length;i++){
			        //得到当前工作表的行数   
			        int rowNum = sheet[i].getRows(); 
			        List listNum = new ArrayList();
			        for(int j=0;j<rowNum;j++){
			            //得到当前行的所有单元格   
			            Cell[] cells = sheet[i].getRow(j); 
			            List ListCells = new ArrayList();
			            if(cells!=null&&cells.length>0){   
			                //对每个单元格进行循环   
			                for(int k=0;k<cells.length;k++){ 
			                    //读取当前单元格的值   
			                    String cellValue = cells[k].getContents(); 
			                    
			                    ListCells.add(cellValue);
			                }   
			            } 
			            listNum.add(ListCells);
			        }
			        returnList.add(listNum);
			    }   
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally{
			if(wb!=null){
				//最后关闭资源，释放内存   
			    wb.close();  
			}
		} 
	     
	    return returnList;
	} 
	
	public static void main(String[] args){
		File file = new File("C:/Documents and Settings/hkmw/桌面/新建文件夹 (14)/出租车费用明细表1.xls");
		System.out.println(readExcel(file));
	}
}
