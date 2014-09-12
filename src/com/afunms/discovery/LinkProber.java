/**
 * <p>Description:probe the router table</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-13
 */

package com.afunms.discovery;

import java.util.*;

import com.afunms.common.util.*;

public class LinkProber
{
   private Host node;
   private List allLinks;
   
   public LinkProber(Host node)
   {
      this.node = node;      
   }
   
   /**
    * 确定链路关系
    */
   public List confirmLinks()
   {            	  
	  if(node.getSwitchIds()==null) return null;
	  String[] tempIds = node.getSwitchIds().split(",");	  
	  if(tempIds==null) return null;
	  
	  System.out.print("tempIds.length=" + tempIds.length);
	  int[] ids = new int[tempIds.length];
	  for(int i=0;i<tempIds.length;i++)
		 ids[i] = Integer.parseInt(tempIds[i]);
	  
	  allLinks = new ArrayList();
//	  for(int i=0;i<ids.length;i++)
//	  {		  
//		  Node startNode = DiscoverEngine.getInstance().getHostByID(ids[i]);
//          for(int j=i+1;j<ids.length;j++)
//          {                
//        	  Node endNode = DiscoverEngine.getInstance().getHostByID(ids[j]);
//        	  
//        	  List tempLinks1 = SnmpUtil.getInstance().findLinks
//                  (ids[i],startNode.getIpAddress(),startNode.getCommunity(),ids[j],endNode.getIpAddress(),endNode.getCommunity());
//        	  List tempLinks2 = SnmpUtil.getInstance().findLinks
//                  (ids[j],endNode.getIpAddress(),endNode.getCommunity(),ids[i],startNode.getIpAddress(),startNode.getCommunity());
//
//        	  if(tempLinks1 ==null || tempLinks2 ==null) 
//        	     continue;
//
//    		  for(int i1=0;i1<tempLinks1.size();i1++)
//    		  {
//    			  TemporaryLink tl1 = (TemporaryLink)tempLinks1.get(i1);
//    			  if(tempLinks2.contains(tl1))
//    			  {
//    				  if(!allLinks.contains(tl1))
//    				  {
//    					  allLinks.add(tl1);
//    					  System.out.println("加入新的链路：" + tl1.toString());
//    				  }
//    			  }
//    		  }        			          	          	  
//          }          
//	  }

	  System.out.println("------------------router与switch之间的链路---------------");
      for(int j=0;j<ids.length;j++)
      {                
    	  Node endNode = DiscoverEngine.getInstance().getHostByID(ids[j]);        	  
    	  
    	  List tempLinks = SnmpUtil.getInstance().findLinks
              (node.getId(),node.getIpAddress(),node.getCommunity(),ids[j],endNode.getIpAddress(),endNode.getCommunity());
 
    	  if(tempLinks ==null) continue;
    	  
          for(int k=0;k<tempLinks.size();k++)
          {            	  
        	  TemporaryLink tl = (TemporaryLink)tempLinks.get(k);
        	  if(!allLinks.contains(tl))
        	  {
                  allLinks.add(tl); 
                  System.out.println("加入与Router的新链路：" + tl.toString());
        	  }
          }
      }     
//------------------------------------------
	  
	  //至少有三条链路，才有再分析的必要
	  if(allLinks.size()<3) return allLinks;
	  analyseLinks();
	  return allLinks;
   }
   
   private void analyseLinks()
   {
 	   int loop = 0;
       while(true)
       {
    	   boolean hasFind = false;
    	   int size = allLinks.size();
	       for(int i=0;i<size;i++)
	       { 		  
	    	   TemporaryLink link1 = (TemporaryLink)allLinks.get(i);
	    	   if(link1.isDel()) continue;
	    	 
               for(int j=0;j<size;j++)
	           {
	    	       if(i==j) continue;
            	   TemporaryLink link2 = (TemporaryLink)allLinks.get(j); 	    	 
	    	       if(link2.isDel()) continue;	    	 
	    	       if(!link1.halfEquals(link2)) continue;	    	     
	    	       
    	    	   for(int k=0;k<size;k++)
    	    	   {
    	    		   TemporaryLink tempLink = (TemporaryLink)allLinks.get(k);   
    	    		   if( k==i || k==j ) continue;
    	    		   
    	    		   if(tempLink.halfEquals(link1.getDissimilar()))
    	    		   {
    	    			   if(tempLink.getDissimilar().getId()==link2.getDissimilar().getId()
    	    				&& tempLink.getDissimilar().getIfIndex().equals(link2.getDissimilar().getIfIndex()))
    	    			   hasFind = true;
    	    			   link1.setDel(true);
    	    			   System.out.println(link1.getStart() + "<--->" + link1.getEnd() + " is deleted");
    	    		   }
    	    		   else if(tempLink.halfEquals(link2.getDissimilar()))
    	    		   {
    	    			   if(tempLink.getDissimilar().getId()==link1.getDissimilar().getId()
    	    				&& tempLink.getDissimilar().getIfIndex().equals(link1.getDissimilar().getIfIndex()))
    	    			   hasFind = true;
    	    			   link2.setDel(true);
    	    			   System.out.println(link2.getStart() + "<--->" + link2.getEnd() + " is deleted");
    	    		   }    	    		   
    	    	   }//end_for	    	    	   	    	       
	    	   }
	       }
		   if(!hasFind) break;
		   loop++;
		   System.out.print("size*size=" + size*size);
		   if(loop>size*size) break; //怕出现dead loop
	   }
   }
   
   private TemporaryLink isLinkExist(int id1,int id2)   
   {	   
	   List list = DiscoverEngine.getInstance().getLinkList();
	   
	   /**
	    * 默认路由与交换，或者两交换之间不会出现双链路的情况 
	    */	
	   TemporaryLink tempLink = null;	   
	   for(int i=0;i<list.size();i++)
	   {
		   Link link = (Link)list.get(i);
		   if(id1==link.getStartId() && id2==link.getEndId())
			  tempLink = new TemporaryLink(id1,link.getStartIndex(),id2,link.getEndIndex());
		   else if(id1==link.getEndId() && id2==link.getStartId())
			  tempLink = new TemporaryLink(id2,link.getStartIndex(),id1,link.getEndIndex());
	   }
	   return tempLink;
   }
}