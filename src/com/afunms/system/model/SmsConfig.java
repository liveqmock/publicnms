/**
 * <p>Description:mapping table NMS_ROLE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

/**
*
* @author lijun
*/
public class SmsConfig extends BaseVo{
   
   private int id;
   private String objectId;
   private String objectType;
   private String beginTime;
   private String endTime;
   private String userIds;

   public int getId() {
       return id;
   }

   public void setId(int id) {
       this.id = id;
   }

   public String getObjectId() {
       return objectId;
   }

   public void setObjectId(String objectId) {
       this.objectId = objectId;
   }

   public String getObjectType() {
       return objectType;
   }

   public void setObjectType(String objectType) {
       this.objectType = objectType;
   }

   public String getBeginTime() {
       return beginTime;
   }

   public void setBeginTime(String beginTime) {
       this.beginTime = beginTime;
   }

   public String getEndTime() {
       return endTime;
   }

   public void setEndTime(String endTime) {
       this.endTime = endTime;
   }

   public String getUserIds() {
       return userIds;
   }

   public void setUserIds(String userIds) {
       this.userIds = userIds;
   }
}
