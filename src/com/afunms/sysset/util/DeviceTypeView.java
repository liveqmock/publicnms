/**
 * <p>Description:device type hepler</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-09
 */

package com.afunms.sysset.util;

import java.util.List;

import com.afunms.polling.base.NodeCategory;
import com.afunms.topology.util.NodeHelper;
import com.afunms.sysset.dao.ProducerDao;
import com.afunms.sysset.model.Producer;

public class DeviceTypeView
{
   private List producerList;   
   public DeviceTypeView()
   {	   
	   ProducerDao vDao = new ProducerDao();
	   producerList = vDao.loadAll();
   }
   
   /**
    * 生成 选择"生产商"下拉框
    */
   public String getProducerBox(int index)
   {
       StringBuffer sb = new StringBuffer(300);
       sb.append("<select size=1 name='producer' style='width:150px;'>");
       sb.append("<option value='0'>其他</option>");

       for(int i=0;i<producerList.size();i++)
       {
    	  Producer vo = (Producer)producerList.get(i);
          if(index==vo.getId())
             sb.append("<option value='" + vo.getId() + "' selected>");
          else
             sb.append("<option value='" + vo.getId() + "'>");
          sb.append(vo.getProducer());
          sb.append("</option>");
       }
       sb.append("</select>");
       return sb.toString();
   }
   
   public String getProducerBox()
   {
       return getProducerBox(0);
   }

   /**
    * 生成 选择"设备分类"下拉框
    */
   public String getCategoryBox(int index)
   {
      StringBuffer sb = new StringBuffer(300);
      sb.append("<select size=1 name='category' style='width:150px;'>");

      List categorys = NodeHelper.getAllCategorys();
      for(int i=0;i<categorys.size();i++)
      {
    	  NodeCategory category = (NodeCategory)categorys.get(i);
    	  if(category.getId()==1000) continue;
    	  
          if(index==category.getId())
             sb.append("<option value='" + category.getId() + "' selected>");
          else
             sb.append("<option value='" + category.getId() + "'>");
          sb.append(category.getCnName());
          sb.append("</option>");
      }
      sb.append("</select>");
      return sb.toString();
   }

   public String getCategoryBox()
   {
	   return getCategoryBox(0);
   }

   public String getProducer(int id)
   {
	   if(id==0) return "其他";
	   
	   Producer tmpObj = new Producer(id);
   	   
   	   int index = producerList.indexOf(tmpObj); 
   	   if(index==-1) return "未知";
   	   return ((Producer)producerList.get(index)).getProducer();
   }

   public String getCategory(int id)
   {
   	   return NodeHelper.getNodeCategory(id);
   }
}
