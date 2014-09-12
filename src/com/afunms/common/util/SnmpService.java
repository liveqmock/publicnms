/**
 * <p>Description:snmp service</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-10
 */

package com.afunms.common.util;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableListener;
import org.snmp4j.util.TableUtils;

import com.afunms.initialize.ResourceCenter;

public class SnmpService implements PDUFactory
{
   private static final int SNMP_VERSION = 0;
   private static final int RETRIES = 2;
   private static final int TIMEOUT = 10000;   
   //private Snmp snmp;
   private Vector vbs = new Vector();
   OID lowerBoundIndex, upperBoundIndex;
   
	public static int default_version = org.snmp4j.mp.SnmpConstants.version1;
	public static int otherversion = org.snmp4j.mp.SnmpConstants.version2c;
	//public static final int default_version = SnmpConstants.version2c;
	public static final int default_retries = 3;
	private Integer default_port = new Integer(161);
	private int default_timeout = 5000;
	private int m_TimeOut = 5000;
	public static String snmpversion = "";
   
   public SnmpService()
   {
   	  try
	  {	  
   		  snmpversion = ResourceCenter.getInstance().getSnmpversion();
   		  //版本有只使用V1(v1),只使用V2(v2),v1&v2(v1优先于v2)、v2&v1(v2优先于v1)
   		  if(snmpversion.equals("v1")){
   			default_version = org.snmp4j.mp.SnmpConstants.version1;
   			otherversion = org.snmp4j.mp.SnmpConstants.version2c;
   		  }else if(snmpversion.equals("v2")){
   			default_version = org.snmp4j.mp.SnmpConstants.version2c;
   			otherversion = org.snmp4j.mp.SnmpConstants.version1;
   		  }else if(snmpversion.equals("v1+v2")){
   			default_version = org.snmp4j.mp.SnmpConstants.version1;
   			otherversion = org.snmp4j.mp.SnmpConstants.version2c;
   		  }else if(snmpversion.equals("v2+v1")){
     			default_version = org.snmp4j.mp.SnmpConstants.version2c;
     			otherversion = org.snmp4j.mp.SnmpConstants.version1;
   		  }
          //TransportMapping transport = new DefaultUdpTransportMapping();
          //snmp = new Snmp(transport);      
          //snmp.listen();
	  }   
      catch(Exception ioe)
      {
    	  SysLogger.error("SnmpService.SnmpService()",ioe); 
      }   
   }
   
   public SnmpService(int version)
   {
   	  try
	  {	  
   		  //snmpversion = version+"";
   		  //版本有只使用V1(v1),只使用V2(v2),v1&v2(v1优先于v2)、v2&v1(v2优先于v1)
   		  if(version == 0){
   			  default_version = version;
   			  otherversion = 1;
   		  }else{
   			  default_version = version;
   			  otherversion = 2; 
   		  }
          TransportMapping transport = new DefaultUdpTransportMapping();
          //snmp = new Snmp(transport);      
          //snmp.listen();
	  }   
      catch(IOException ioe)
      {
    	  SysLogger.error("SnmpService.SnmpService()",ioe); 
      }   
   }

//   private CommunityTarget createTarget(String address,String community)
//   {
//       CommunityTarget target = new CommunityTarget();
//       target.setCommunity(new OctetString(community));
//       //target.setVersion(SNMP_VERSION);
//       SysLogger.info("SNMP VERSION : "+default_version);       
//       target.setVersion(default_version);
//       target.setRetries(RETRIES);
//       target.setAddress(GenericAddress.parse(address + "/161"));
//       target.setTimeout(TIMEOUT);
//      
//       return target;
//   }

   public PDU createPDU(Target target)
   {
      PDU request = new PDU();
      request.setType(PDU.GET);
      return request;
   }

   public List createTable(String address,String community,String[] oids) throws IOException
   {
       List list = null;	
		//begin
       	//Snmp snmp = new Snmp(transport);
       	Snmp snmp = createSnmpSession();
		Target target = createTarget(community);
		target.setVersion(default_version);
		target.setAddress(
			GenericAddress.parse(address + "/" + default_port));
		target.setRetries(default_retries);
		target.setTimeout(default_timeout);
		try{
			snmp.listen();
		}catch(Exception e){
			e.printStackTrace();
			if(snmp != null)snmp.close();
			snmp = null;
			return null;
		}
		TableUtils tableUtils = new TableUtils(snmp, this);
		OID[] columns = new OID[oids.length];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = (new VariableBinding(new OID(oids[i]))).getOid();
		}
		try{
		list =
			tableUtils.getTable(
				target,
				columns,
				null,
				null);
		}catch(Exception e){
			e.printStackTrace();
			list = null;
		}finally{
			if(target != null){
				target = null;
			}
			if(snmp != null){
				snmp.close();
			}
			//snmp.close();
			snmp = null;
		}
		if(snmpversion.equalsIgnoreCase("v1+v2") || snmpversion.equalsIgnoreCase("v2+v1")){
			//混合用V1和V2版本
			if(list == null || (list != null && list.size()>0 && list.get(0) != null && ((TableEvent)list.get(0)).getColumns()==null)){
				//用SNMP不同的版本采集
				snmp = createSnmpSession();
				target = createTarget(community);
				target.setVersion(otherversion);
				target.setAddress(
					GenericAddress.parse(address + "/" + default_port));
				target.setRetries(default_retries);
				target.setTimeout(default_timeout);
				snmp.listen();
				tableUtils = new TableUtils(snmp, this);
				columns = new OID[oids.length];
				for (int i = 0; i < columns.length; i++) {
					columns[i] = (new VariableBinding(new OID(oids[i]))).getOid();
				}
				try{
				list =
					tableUtils.getTable(
						target,
						columns,
						null,
						null);
				}catch(Exception e){
					e.printStackTrace();
					list = null;
				}finally{
					if(target != null){
						target = null;
					}
					if(snmp != null){
						snmp.close();
					}
					snmp = null;
				}
			}
		}

		
		//end
		target = null;
		tableUtils = null;
       return list;
   }
   
   public String getMibValue(String address,String community,String oid)
   { 
		String sResponse=null;
		PDU response = null;
		try {
			Vector _vbs = new Vector();
			VariableBinding vb = new VariableBinding(new OID(oid));
			_vbs.add(vb);
			response = this.send(community, address,_vbs);
			VariableBinding recVB = (VariableBinding)response.getVariableBindings().get(0);
			String operat = response.getVariableBindings().get(0).toString();
			sResponse =
				operat
					.substring(operat.lastIndexOf("=") + 1, operat.length())
					.trim();
			response.clear();
			if (sResponse.equalsIgnoreCase("Null")){
				response.clear();
				return null;
			}
				
		} catch (Exception e) {
			//e.printStackTrace();
			if(response != null)response.clear();
			return null;
		}
		response=null;
		return sResponse;
   }
   
   public String getMibValue(String address,int snmpversion,String community,int securityLevel,String securityName,int v3_ap,String authPassPhrase,int v3_privacy,String privacyPassPhrase,String oid)
   { 
//	   SysLogger.info("address==="+address);
//	   SysLogger.info("snmpversion==="+snmpversion);
//	   SysLogger.info("community==="+community);
//	   SysLogger.info("securityLevel==="+securityLevel);
//	   SysLogger.info("securityName==="+securityName);
//	   SysLogger.info("v3_ap==="+v3_ap);
//	   SysLogger.info("authPassPhrase==="+authPassPhrase);
//	   SysLogger.info("v3_privacy==="+v3_privacy);
//	   SysLogger.info("privacyPassPhrase==="+privacyPassPhrase);
		String sResponse=null;
		PDU response = null;
		try {
			Vector _vbs = new Vector();
			VariableBinding vb = new VariableBinding(new OID(oid));
			_vbs.add(vb);
			response = this.send(community, address,_vbs,snmpversion,securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase);
			VariableBinding recVB = (VariableBinding)response.getVariableBindings().get(0);
			String operat = response.getVariableBindings().get(0).toString();
			sResponse =
				operat
					.substring(operat.lastIndexOf("=") + 1, operat.length())
					.trim();
			response.clear();
			if (sResponse.equalsIgnoreCase("Null")){
				response.clear();
				return null;
			}
				
		} catch (Exception e) {
			//e.printStackTrace();
			if(response != null)response.clear();
			return null;
		}
		response=null;
		return sResponse;
   }
   
   public void setMibValues(String address,String community,int version,String[] oids,String[] mibvalues)
   { 
	   try{
		   TransportMapping transport = new DefaultUdpTransportMapping();
		   Snmp snmp = new Snmp(transport);
		   CommunityTarget target = new CommunityTarget();
			target.setCommunity(new OctetString(community));
			target.setVersion(version);
			//System.out.println(community+"===="+target.getVersion());
			target.setAddress(GenericAddress.parse(address + "/" + new Integer(161))); 
			target.setRetries(1);
			target.setTimeout(5000);
	    	VariableBinding[] values = new VariableBinding[oids.length];
	    	for(int i=0;i<values.length;i++){
	    		values[i] = new VariableBinding(new OID(oids[i]),new OctetString(mibvalues[i]));
	    	}
			
			PDU pdu = new PDU();
			for(int i=0;i<values.length;i++)
			{
			    pdu.add(values[i]);
			}
			pdu.setType(PDU.SET);
			ResponseEvent response = snmp.send(pdu, target); 
			SysLogger.info("************* "+response.getError().getMessage());
			SysLogger.info("修改: 对设备"+address+"进行SMP设置成功!");
	   }catch(Exception e){
		   e.printStackTrace();
		   SysLogger.info("修改: 对设备"+address+"进行SMP设置出现错误!原因为---"+e.getMessage());
	   }
   }
   
   public boolean _setMibValues(String address,String community,int version,String[] oids,String[] mibvalues)
   { 
	   boolean flag = false;
	   try{
		   TransportMapping transport = new DefaultUdpTransportMapping();
		   Snmp snmp = new Snmp(transport);
		   CommunityTarget target = new CommunityTarget();
			target.setCommunity(new OctetString(community));
			target.setVersion(version);
			//System.out.println(community+"===="+target.getVersion());
			target.setAddress(GenericAddress.parse(address + "/" + new Integer(161))); 
			target.setRetries(1);
			target.setTimeout(5000);
	    	VariableBinding[] values = new VariableBinding[oids.length];
	    	for(int i=0;i<values.length;i++){
	    		values[i] = new VariableBinding(new OID(oids[i]),new OctetString(mibvalues[i]));
	    	}
			
			PDU pdu = new PDU();
			for(int i=0;i<values.length;i++)
			{
			    pdu.add(values[i]);
			}
			pdu.setType(PDU.SET);
			ResponseEvent response = snmp.send(pdu, target); 
			SysLogger.info("************* "+response.getError().getMessage());
			SysLogger.info("修改: 对设备"+address+"进行SMP设置成功!");
			flag = true;
	   }catch(Exception e){
		   flag=false;
		   e.printStackTrace();
		   SysLogger.info("修改: 对设备"+address+"进行SMP设置出现错误!原因为---"+e.getMessage());
	   }
	   return flag;
   }

   public String[][] getTableData(String address,String community,String[] columnoids) throws Exception
   {
   	  String[][] tablevalues = null;
      TableEvent row = null;
      VariableBinding[] columnvalues = null;
      VariableBinding columnvalue = null;      
      try
      {   	  
         List rowvalues = null;
         try{
        	 rowvalues = createTable(address,community,columnoids);
         }catch(Exception e){
        	 
         }
         if(rowvalues == null)return tablevalues;
         tablevalues = new String[rowvalues.size()][columnoids.length];

         for (int i = 0; i < rowvalues.size(); i++)
         {
            row = (TableEvent) rowvalues.get(i);
            columnvalues = row.getColumns();
            
            if(columnvalues!=null)
            {
               for (int j = 0; j < columnvalues.length; j++)
               {     
            	  //SysLogger.info(address+" columnvalue==="+columnvalues[j]);
                  columnvalue = columnvalues[j];
                  if(columnvalue == null)continue;
                  
                  String value=columnvalue.toString().substring(columnvalue.toString().indexOf("=")+1,columnvalue.toString().length()).trim();
                  tablevalues[i][j] = value;
                  //SysLogger.info(address+" columnvalue.getOid()=== "+columnvalue.getOid()+" index="+row.getIndex()+" value "+value);
                  //if(columnvalue!=null) tablevalues[i][j] = columnvalue.getVariable().toString();
               }
            }
         }
      }
      catch (Exception e)
      {
    	  e.printStackTrace();
    	  SysLogger.error("Error in getTableData,ip=" + address + ",community=" + community);
     	  tablevalues = null;
      }
      row = null;
      columnvalues = null;
      columnvalue = null;
      return tablevalues;
   }
   
	public List table(String community, String address,String[] columnoids) throws IOException {
		//System.out.println("Start collect data as ip "+address+"   "+community+"    "+default_port);
		
		List list = null;

		Snmp snmp = null;
		try {
			snmp = createSnmpSession();
			Target target = createTarget(community);
			target.setVersion(default_version);
			target.setAddress(
				GenericAddress.parse(address + "/" + default_port));
			target.setRetries(default_retries);
			target.setTimeout(default_timeout);
			snmp.listen();
			TableUtils tableUtils = new TableUtils(snmp, this);

			OID[] columns = new OID[columnoids.length];
			for (int i = 0; i < columns.length; i++) {
				columns[i] = (new VariableBinding(new OID(columnoids[i]))).getOid();
			}

			list =
				tableUtils.getTable(
					target,
					columns,
					lowerBoundIndex,
					upperBoundIndex);
			//System.out.println("collect   memory 2.5");
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println(e.getMessage());
		} finally {
			snmp.close();
		}
		return list;
	}
   
   public String[][] getCpuTableData(String address,String community,String[] columnoids) throws Exception
   {
   	  String[][] tablevalues = null;
      TableEvent row = null;
      VariableBinding[] columnvalues = null;
      VariableBinding columnvalue = null;
      
      try
      {   	  
         List rowvalues = createTable(address,community,columnoids);
         if(rowvalues == null)return tablevalues;
         tablevalues = new String[rowvalues.size()][columnoids.length+1];

         for (int i = 0; i < rowvalues.size(); i++)
         {
            row = (TableEvent) rowvalues.get(i);
            columnvalues = row.getColumns();
            
            if(columnvalues!=null)
            {
               for (int j = 0; j < columnvalues.length; j++)
               {     
            	  //SysLogger.info(address+" columnvalue==="+columnvalues[j]);
                  columnvalue = columnvalues[j];
                  if(columnvalue == null)continue;
                  
                  String value=columnvalue.toString().substring(columnvalue.toString().indexOf("=")+1,columnvalue.toString().length()).trim();
                  tablevalues[i][j] = value;
                  tablevalues[i][j+1] = row.getIndex().toString();
                  //SysLogger.info(address+" columnvalue.getOid()=== "+columnvalue.getOid()+" index="+row.getIndex()+" value "+value);
                  //if(columnvalue!=null) tablevalues[i][j] = columnvalue.getVariable().toString();
               }
            }
         }
      }
      catch (Exception e)
      {
    	  e.printStackTrace();
    	  SysLogger.error("Error in getTableData,ip=" + address + ",community=" + community);
     	  tablevalues = null;
      }
      row = null;
      columnvalues = null;
      columnvalue = null;
      return tablevalues;
   }
   
   public String[][] getTemperatureTableData(String address,String community,String[] columnoids) throws Exception
   {
   	  String[][] tablevalues = null;
      TableEvent row = null;
      VariableBinding[] columnvalues = null;
      VariableBinding columnvalue = null;
      
      try
      {   	  
         List rowvalues = createTable(address,community,columnoids);
         if(rowvalues == null)return tablevalues;
         tablevalues = new String[rowvalues.size()][columnoids.length+1];

         for (int i = 0; i < rowvalues.size(); i++)
         {
            row = (TableEvent) rowvalues.get(i);
            columnvalues = row.getColumns();
            
            if(columnvalues!=null)
            {
               for (int j = 0; j < columnvalues.length; j++)
               {     
            	  //SysLogger.info(address+" columnvalue==="+columnvalues[j]);
                  columnvalue = columnvalues[j];
                  if(columnvalue == null)continue;
                  
                  String value=columnvalue.toString().substring(columnvalue.toString().indexOf("=")+1,columnvalue.toString().length()).trim();
                  tablevalues[i][j] = value;
                  tablevalues[i][columnoids.length] = row.getIndex().toString();
                  //SysLogger.info(address+" columnvalue.getOid()=== "+columnvalue.getOid()+" index="+row.getIndex()+" value "+value);
                  //if(columnvalue!=null) tablevalues[i][j] = columnvalue.getVariable().toString();
               }
            }
         }
      }
      catch (Exception e)
      {
    	  e.printStackTrace();
    	  SysLogger.error("Error in getTableData,ip=" + address + ",community=" + community);
     	  tablevalues = null;
      }
      row = null;
      columnvalues = null;
      columnvalue = null;
      return tablevalues;
   }
   
   public String[][] getCiscoVlanTableData(String address,String community,String[] columnoids) throws Exception
   {
   	  String[][] tablevalues = null;
      try
      {   	  
         List rowvalues = createTable(address,community,columnoids);
         TableEvent row = null;
         VariableBinding[] columnvalues = null;
         VariableBinding columnvalue = null;
         tablevalues = new String[rowvalues.size()][columnoids.length+1];

         for (int i = 0; i < rowvalues.size(); i++)
         {
            row = (TableEvent) rowvalues.get(i);
            columnvalues = row.getColumns();
            if(columnvalues!=null)
            {
               for (int j = 0; j < columnvalues.length; j++)
               {            	   
                  columnvalue = columnvalues[j];
                  String[] strtemp = columnvalue.getOid().toString().split("\\.");
                  String value=columnvalue.toString().substring(columnvalue.toString().indexOf("=")+1,columnvalue.toString().length()).trim();
                  tablevalues[i][j] = value;
                  if(strtemp[strtemp.length-1].equalsIgnoreCase("0")){
                	  //取倒数第二位
                	  tablevalues[i][j+1] = strtemp[strtemp.length-2];
                  }else
                	  tablevalues[i][j+1] = strtemp[strtemp.length-1];//取最后一位
               }
            }
         }
      }
      catch (Exception e)
      {
    	  SysLogger.error("Error in getCiscoVlanTableData,ip=" + address + ",community=" + community);
     	  tablevalues = null;
      }
      return tablevalues;
   }
   
   public String[][] getNDPTableData(String address,String community,String[] columnoids) throws Exception
   {
   	  String[][] tablevalues = null;
      TableEvent row = null;
      VariableBinding[] columnvalues = null;
      VariableBinding columnvalue = null;
      try
      {   	  
         List rowvalues = createTable(address,community,columnoids);
         if(rowvalues == null)return tablevalues;

         tablevalues = new String[rowvalues.size()][columnoids.length+1];

         for (int i = 0; i < rowvalues.size(); i++)
         {
            row = (TableEvent) rowvalues.get(i);
            columnvalues = row.getColumns();
            if(columnvalues!=null)
            {
               for (int j = 0; j < columnvalues.length; j++)
               {            	   
                  columnvalue = columnvalues[j];
                  String[] strtemp = columnvalue.getOid().toString().split("\\.");
                  String value=columnvalue.toString().substring(columnvalue.toString().indexOf("=")+1,columnvalue.toString().length()).trim();
                  tablevalues[i][j] = value;
                  
                  if(strtemp[strtemp.length-1].equalsIgnoreCase("0")){
                	  //取倒数第二位
                	  tablevalues[i][j+1] = strtemp[strtemp.length-2];
                  }else
                	  tablevalues[i][j+1] = strtemp[strtemp.length-1];//取最后一位
                  //SysLogger.info(address+"##########"+tablevalues[i][j]+"##########"+columnvalue.getOid().toString());
               }
               
            }
         }
      }
      catch (Exception e)
      {
    	  SysLogger.error("Error in getCiscoVlanTableData,ip=" + address + ",community=" + community);
     	  tablevalues = null;
      }
      row = null;
      columnvalues = null;
      columnvalue = null;
      return tablevalues;
   }
   
   
	public void setVariableBindings(String[] oids) {
		vbs = new Vector();
		for (int i = 0; i < oids.length; i++) {
			String oid = oids[i];
			VariableBinding vb = new VariableBinding(new OID(oid));
			vbs.add(vb);
		}
	}
	
	public void setVariableBindings(String oid) {
		vbs = new Vector();
		//for (int i = 0; i < oids.length; i++) {
			//String oid = oids[i];
			VariableBinding vb = new VariableBinding(new OID(oid));
			vbs.add(vb);
		//}
	}
	
	public PDU send(String community, String address) throws IOException {

		Snmp snmp = createSnmpSession();
		Target target = createTarget(community);
		target.setVersion(default_version);
		target.setAddress(GenericAddress.parse(address + "/" + default_port));
		target.setRetries(default_retries);
		target.setTimeout(default_timeout);
		snmp.listen();

		PDU request = createPDU(target);

		for (int i = 0; i < vbs.size(); i++) {
			//SysLogger.info(address+"====="+((VariableBinding)vbs.get(i)).toString());
			request.add((VariableBinding) vbs.get(i));
		}

		PDU response = null;
		try {
			//SysLogger.info(request.getVariableBindings().toString());
			response = snmp.send(request, target).getResponse();
			//System.out.println("list "+response.toString());
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			//System.out.println(e.getMessage());
		} finally {
			snmp.close();
			snmp = null;
			response.clear();
			//response = null;
		}
		//SysLogger.info(address+ " version "+default_version +" response===="+response);
		if(snmpversion.equalsIgnoreCase("v1+v2") || snmpversion.equalsIgnoreCase("v2+v1")){
			//混合用V1和V2版本
			if(response == null || response.getErrorIndex() > 0){
				//换一个SNMP版本
				snmp = createSnmpSession();
				target = createTarget(community);
				target.setVersion(otherversion);
				target.setAddress(GenericAddress.parse(address + "/" + default_port));
				target.setRetries(default_retries);
				target.setTimeout(default_timeout);
				snmp.listen();

				request = createPDU(target);

				for (int i = 0; i < vbs.size(); i++) {
					request.add((VariableBinding) vbs.get(i));
				}

				response = null;
				try {

					response = snmp.send(request, target).getResponse();
					//System.out.println("list "+response.toString());
				} catch (IOException e) {
					throw e;
				} catch (Exception e) {
					//System.out.println(e.getMessage());
				} finally {
					snmp.close();
					snmp = null;
					//response.clear();
				}
				//SysLogger.info(address+ " version "+otherversion +" response===="+response);
			}
		}
		snmp=null;
		
		target = null;
		request = null;
		
		return response;
	}
	
	public PDU send(String community, String address,Vector _vbs) throws IOException {

		Snmp snmp = createSnmpSession();
		Target target = createTarget(community);
		target.setVersion(default_version);
		target.setAddress(GenericAddress.parse(address + "/" + default_port));
		target.setRetries(default_retries);
		target.setTimeout(default_timeout);
		snmp.listen();

		PDU request = createPDU(target);

		for (int i = 0; i < _vbs.size(); i++) {
			//SysLogger.info(address+"====="+((VariableBinding)_vbs.get(i)).toString());
			request.add((VariableBinding) _vbs.get(i));
		}

		PDU response = null;
		try {
			//SysLogger.info(request.getVariableBindings().toString());
			response = snmp.send(request, target).getResponse();
			//System.out.println("list "+response.toString());
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			//e.printStackTrace();
			//System.out.println(e.getMessage());
		} finally {
			snmp.close();
			//response = null;
		}
//		SysLogger.info(address+ " version "+default_version +" response===="+response);
		if(snmpversion.equalsIgnoreCase("v1+v2") || snmpversion.equalsIgnoreCase("v2+v1")){
			//混合用V1和V2版本
			if(response == null || response.getErrorIndex() > 0){
				//换一个SNMP版本
				snmp = createSnmpSession();
				target = createTarget(community);
				target.setVersion(otherversion);
				target.setAddress(GenericAddress.parse(address + "/" + default_port));
				target.setRetries(default_retries);
				target.setTimeout(default_timeout);
				snmp.listen();

				request = createPDU(target);

				for (int i = 0; i < _vbs.size(); i++) {
					request.add((VariableBinding) _vbs.get(i));
				}

				response = null;
				try {

					response = snmp.send(request, target).getResponse();
					//System.out.println("list "+response.toString());
				} catch (IOException e) {
					//throw e;
					e.printStackTrace();
				} catch (Exception e) {
					//System.out.println(e.getMessage());
					e.printStackTrace();
				} finally {
					snmp.close();
				}
				//SysLogger.info(address+ " version "+otherversion +" response===="+response);
			}
		}
		//SysLogger.info(address+ " version "+otherversion +" response===="+response);
		//snmp.close();
		snmp=null;
		target = null;
		request = null;		
		return response;
	}
	
	public PDU send(String community, String address,Vector _vbs,int snmpversion,int securityLevel,String securityName,int v3_ap,String authPassPhrase,int v3_privacy,String privacyPassPhrase) throws IOException {
		PDU response = null;
		Snmp snmp = createSnmpSession();
		if(snmpversion == 3){
			//SNMP V3
    		USM usm = new USM(SecurityProtocols.getInstance(),  
    	    		 	new OctetString(MPv3.createLocalEngineID()), 0);  
    	    SecurityModels.getInstance().addSecurityModel(usm);  
    	    OID ap_oid = null;
    	    if(v3_ap == 1){
    	    	//MD5
    	    	ap_oid = AuthMD5.ID;
    	    }else if(v3_ap == 2){
    	    	//SHA
    	    	ap_oid = AuthSHA.ID;
    	    }
    	    OID v3_privacy_oid = null;
    	    if(v3_privacy == 1){
    	    	//DES
    	    	v3_privacy_oid = PrivDES.ID;
    	    }else if(v3_privacy == 2){
    	    	//AES128
    	    	v3_privacy_oid = PrivAES128.ID;
    	    }else if(v3_privacy == 3){
    	    	//AES192
    	    	v3_privacy_oid = PrivAES192.ID;
    	    }else if(v3_privacy == 4){
    	    	//AES256
    	    	v3_privacy_oid = PrivAES256.ID;
    	    }
    	    //add user to the USM   
    	    snmp.getUSM().addUser(new OctetString(securityName),  
    	    				 	new UsmUser(new OctetString(securityName),  
    	    				 	ap_oid, //认证协议  MD5/SHA 
    	    				 	new OctetString(authPassPhrase),  
    	    				 	v3_privacy_oid, //加密协议  DES/AES128/AES192/AES256
    	    				 	new OctetString(privacyPassPhrase)));
   		 	//create the target      
    	    UserTarget usertarget = createUserTarget(address, securityName, securityLevel, default_retries, default_timeout);
    	    snmp.listen();
    	    //create the pdu
    	    PDU pdu = new ScopedPDU(); 
    	    pdu.setType(PDU.GET);
    		for (int i = 0; i < _vbs.size(); i++) {
    			pdu.add((VariableBinding) _vbs.get(i));  
    		}   		
    		try {
    			//SysLogger.info(pdu.getVariableBindings().toString());
    			response = snmp.send(pdu, usertarget).getResponse();
    			//System.out.println("list "+response.toString());
    		} catch (IOException e) {
    			throw e;
    		} catch (Exception e) {
    			//e.printStackTrace();
    			//System.out.println(e.getMessage());
    		} finally {
    			snmp.close();
    			//response = null;
    		}
    		usertarget = null;
    		pdu = null;	
		}else{
			//SNMP V1/V2C
			Target target = createTarget(community);
			target.setVersion(default_version);
			target.setAddress(GenericAddress.parse(address + "/" + default_port));
			target.setRetries(default_retries);
			target.setTimeout(default_timeout);
			snmp.listen();

			PDU request = createPDU(target);

			for (int i = 0; i < _vbs.size(); i++) {
				//SysLogger.info(address+"====="+((VariableBinding)_vbs.get(i)).toString());
				request.add((VariableBinding) _vbs.get(i));
			}
			try {
				//SysLogger.info(request.getVariableBindings().toString());
				response = snmp.send(request, target).getResponse();
				//System.out.println("list "+response.toString());
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				//e.printStackTrace();
				//System.out.println(e.getMessage());
			} finally {
				snmp.close();
				//response = null;
			}
			target = null;
			request = null;	
		}
		snmp=null;		
		return response;
	}
	
	public Snmp createSnmpSession() throws IOException {

		TransportMapping transport = new DefaultUdpTransportMapping();

		Snmp snmp = new Snmp(transport);
		transport.listen();

		return snmp;
	}
	
	protected Target createTarget(String community) {

		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		return target;

	}
	
	public String[][] getCiscoVlanTable1(String address,String community,String[] oid) throws IOException {

		//target.setRetries(3);
		//CommunityTarget target;
		Snmp snmp = createSnmpSession();
		Target target = createTarget(community);
		target.setVersion(default_version);
		target.setAddress(GenericAddress.parse(address + "/" + default_port));
		target.setRetries(default_retries);
		target.setTimeout(default_timeout);
		
		TableUtils tableUtils = new TableUtils(snmp, this);
		tableUtils.setMaxNumRowsPerPDU(10);
		Counter32 counter = new Counter32();

		OID[] columns = new OID[oid.length];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new OID(oid[i]);
		}
		
		TableListener listener = new TextTableListener();
		
//		long startTime = System.currentTimeMillis();

		//java.util.List result = tableUtils.getTable(target, columns, new OID(lowerBoundIndex), new OID(upperBoundIndex) );
		
		try {
			synchronized(counter){
						
				tableUtils.getTable(target, columns, listener, counter, null, null );
				
				counter.wait(m_TimeOut*target.getRetries()+3000);
			}
			
		} catch (InterruptedException e) {
			
		}
		

		int x=((TextTableListener)listener).getResult().size();
		int y=0;
		if( x>0 ){
			y = ((TextTableListener)listener).getResult().get(0).getColumns().length;
		}
		String[][] s = new String[x][y+1];
		
		for( int i=0; i<x; ++i){
			String one = "";
			/*
			for( int j = 0; j<y; ++j ){
				VariableBinding vb = ((TextTableListener)listener).getResult().get(i).getColumns()[j];
				if( vb != null ){
System.out.println("syntax============="+vb.getSyntax()+"==="+vb.getVariable().toString());					
					switch(vb.getSyntax()){
//					case 4:
//						s[i][j] = new String(((OctetString)(vb.getVariable())).getValue());
//						byte[] btemp = s[i][j].getBytes();
//						break;
					case 67:
						s[i][j] = new String(""+((TimeTicks)(vb.getVariable())).getValue());
						break;
					default:
						s[i][j] = vb.getVariable().toString();
					}
					if( 4 == vb.getSyntax() ){
						s[i][j] = new String(((OctetString)(vb.getVariable())).getValue());
					}
					else{
						s[i][j] = vb.getVariable().toString();
					}
					one = s[i][j];
				}
				
			}
			*/
			s[i][y] = ((TextTableListener)listener).getResult().get(i).getIndex().toString();
			//System.out.println(one+"========"+s[i][y]);			
			
		}
//		System.out.println("Table received in "
//				+ (System.currentTimeMillis() - startTime) + " milliseconds.");
//		
		return s;
	}
	
	  private static UserTarget createUserTarget(String address,String securityName,int securityLevel,int retries,int timeout)
	  {
		  //SysLogger.info(address+"==="+community+"==="+version);
		  UserTarget target = new UserTarget();
		  target.setAddress(GenericAddress.parse(address + "/161"));
		  target.setRetries(retries);  
		  target.setTimeout(timeout);  
		  target.setVersion(SnmpConstants.version3);  
		  if(securityLevel == 1){
			  target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
		  }else if(securityLevel == 2){
			  target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
		  }else if(securityLevel == 3){
			  target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
		  }
		    
		  //target.setSecurityName(new OctetString("dhccsnmpv3"));
		  target.setSecurityName(new OctetString(securityName));
	      return target;
	  }
}


