/**
 * <p>Description: symantec_stat记录</p>
 * <p>Company: 北京东华合创数码科技股份有限公司</p>
 * @author 王福民
 * @project 阿福网管
 * @date 2005-3-15
 */

package com.afunms.security.model;

public class SymantecStat
{
   private String begindate;
   private String content;
   private int total;

   public void setBeginDate(String newBegindate)
   {
      begindate = newBegindate;
   }

   public String getBeginDate()
   {
      return begindate;
   }

   public void setContent(String newContent)
   {
      content = newContent;
   }

   public String getContent()
   {
      return content;
   }

   public void setTotal(int newTotal)
   {
      total = newTotal;
   }

   public int getTotal()
   {
      return total;
   }
}
