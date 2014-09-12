/**
 * <p>Description:father of all Mangager classes</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.base;

import java.awt.Color;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.afunms.common.util.BidSQLUitl;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.User;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;

public class BaseManager
{
   protected HttpServletRequest request;  
   protected HttpServletResponse response;
   protected HttpSession session;   
   protected int errorCode;  //出错信息号;
   private String target;     //目标jsp
   
   public BaseManager()
   {
   }

   public void setRequest(HttpServletRequest req)
   {
      request = req;
      //response.setContentType("text/html;charset=GB2312");
      //response.setContentType("text/html;charset=UTF-8");
      session = request.getSession();
   }
   
   public void setRequest(HttpServletRequest req,HttpServletResponse res)
   {
      request = req;
      response = res;
      response.setContentType("text/html;charset=GB2312");
      //response.setContentType("text/html;charset=UTF-8");
      session = request.getSession();
   }
   
   /**
    * 保存错误号
    */
   public void setErrorCode(int ec)
   {
      errorCode = ec;
      if(ec!=-1)
         SysLogger.error(ErrorMessage.getErrorMessage(ec));
   }

   public int getErrorCode()
   {
      return errorCode;
   }

   //=================子类中用到方法==================
   protected String getParaValue(String para)
   {
      return request.getParameter(para);
   }

   protected String[] getParaArrayValue(String para)
   {
      return request.getParameterValues(para);
   }

   /**
    * 把从页面上传来的参数转成整型 
    */
   protected int getParaIntValue(String para)
   {
      int result = -1;
      String temp = request.getParameter(para);
      if(temp!=null&&!temp.trim().equals(""))
         result = Integer.parseInt(temp);
//      else
//    	 SysLogger.error("BaseManager.getParaIntValue(),参数[" + para + "]不是整数!"); 
      return result;
   }

   /**
    * 得到当前页码
    */
   protected int getCurrentPage()
   {
      int curPage = 1;
      try
      {
         String jp = getParaValue("jp");
         if(jp==null||"".equals(jp))
            jp = (String)session.getAttribute("current_page");
         else
            session.setAttribute("current_page",jp);

         if(jp==null||"".equals(jp))
            curPage = 1;
         else
            curPage = Integer.parseInt(jp);
      }
      catch(NumberFormatException e)
      {
         curPage = 1;
      }
      return curPage;
   }
   
   protected int getPerPagenum()
   {
      int perPage = 15;
      try
      {
         String perpagenum = getParaValue("perpagenum");
         if(perpagenum==null||"".equals(perpagenum)){
        	 perPage = 15;
         }else{
        	 perPage = Integer.parseInt(perpagenum);
         }
      }
      catch(NumberFormatException e)
      {
    	  perPage = 15;
      }
      return perPage;
   }

   protected void setTarget(String target) 
   {
	   this.target = target;
   }

   protected String getTarget() 
   {
	   return target;
   }
   
   /**
    * 分页显示记录
    * targetJsp:目录jsp
    */
   protected String list(DaoInterface dao)
   {
	   String targetJsp = null;
	   	int perpage = getPerPagenum();
       List list = dao.listByPage(getCurrentPage(),perpage);
       if(list==null) return null;
       
       request.setAttribute("page",dao.getPage());
       request.setAttribute("list",list);
       targetJsp = getTarget(); 
	   return targetJsp;
   }
   
   /**
    * 分页显示记录
    * targetJsp:目录jsp
    */
   protected String list(DaoInterface dao,String where)
   {
	   String targetJsp = null;
	   int perpage = getPerPagenum();
       List list = dao.listByPage(getCurrentPage(),where,perpage);
       if(list==null) return null;
       
       request.setAttribute("page",dao.getPage());
       request.setAttribute("list",list);
       targetJsp = getTarget(); 
	   return targetJsp;
   }

   protected String delete(DaoInterface dao)
   {
	   String targetJsp = null; 
       String[] id = getParaArrayValue("checkbox");
       if(dao.delete(id))        	 
          targetJsp = getTarget();
       else
      	  targetJsp = null; 
       return targetJsp;
   }
   
   protected String readyEdit(DaoInterface dao)
   {
	   String targetJsp = null; 
       BaseVo vo = null;
       try{
    	   String ii=getParaValue("id");
    	   vo = dao.findByID(getParaValue("id"));       
       }catch(Exception e){
    	   e.printStackTrace();
       }finally{
    	   
       }
       if(vo!=null)
       {	   
          request.setAttribute("vo",vo);
          targetJsp = getTarget();
       }
       return targetJsp;
   }
   
   protected String save(DaoInterface dao,BaseVo vo)
   {
	   String targetJsp = null; 
       if(dao.save(vo))
          targetJsp = getTarget();
       else
      	  targetJsp = null; 
       return targetJsp;
   }
   
   protected String update(DaoInterface dao,BaseVo vo)
   {
	   String targetJsp = null; 
       if(dao.update(vo))
          targetJsp = getTarget();
       else
      	  targetJsp = null; 
       return targetJsp;
   }   
   
   /**
	 * 设置单元格的格式
	 * 
	 * @param cell
	 *            单元格
	 * @param flag
	 *            是否设置灰色背景色
	 * @return cell
	 */
	protected Cell setCellFormat(Object obj, boolean flag) {
		Cell cell = null;
		Phrase p = null;
		if (obj instanceof Cell) {
			cell = (Cell) obj;
		} else if (obj instanceof Phrase) {
			p = (Phrase) obj;
			try {
				cell = new Cell(p);
			} catch (BadElementException e) {
				SysLogger.error("", e);
			}
		}
		if (cell != null) {
			if (flag) {
				cell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}
		return cell;
	}
	
	/**
	 * 设置表格格式
	 * @param aTable
	 */
	protected void setTableFormat(Table aTable){
		aTable.setWidth(100);
		aTable.setAutoFillEmptyCells(true);
		aTable.setPadding(5);
		aTable.setAlignment(Element.ALIGN_CENTER);
	}

	/**
     * getBidSql:
     * <p>获取 bid
     * 
     * @return  {@link String}
     *          - bid
     * 
     * @since v1.01
     */
    public String getBid() {
        User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
        return user.getBusinessids();
    }

    /**
     * getBidSql:
     * <p>根据指定的 bid 字段名称，生成 Bid SQL 语句
     * 
     * @param   fieldName
     *          - bid 的字段名称
     * @return  {@link String}
     *          - bid 的 SQL 语句
     * 
     * @since v1.01
     */
    public String getBidSql(String fieldName) {
        User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
        String bidSql = null;
        if (user.getRole() != 0) {
            bidSql = getBidSql(user.getBusinessids(), fieldName);
            if (bidSql == null || "".equals(bidSql)) {
                bidSql = "";
            }
        }
        return bidSql;
    }
    
    /**
     * getBidSql:
     * <p>根据指定的 bid 和 bid 字段名称，生成 Bid SQL 语句
     *
     * @param   businessId
     *          - bid
     * @param   fieldName
     *          - bid 的字段名称
     * @return  {@link String}
     *          - bid 的 SQL 语句
     *
     * @since   v1.01
     */
    protected String getBidSql(String businessId, String fieldName) {
        return BidSQLUitl.getBidSQL(businessId, fieldName);
    }
}
