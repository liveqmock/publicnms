package com.afunms.application.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.afunms.common.util.SysLogger;
import com.afunms.application.dao.CicsConfigDao;
import com.ibm.ctg.epi.AID;
import com.ibm.ctg.epi.EPIException;
import com.ibm.ctg.epi.EPIGateway;
import com.ibm.ctg.epi.EPIRequestException;
import com.ibm.ctg.epi.Screen;
import com.ibm.ctg.epi.Session;
import com.ibm.ctg.epi.Terminal;
import com.ibm.ctg.epi.TerminalInterface;

public class CicsHelper {

    private final int WAIT_COUNT = 10;
    private final int SLEEP_PERIOD = 1000;
    private final int DEFAULT_PORT = 0;
    private final String DEFAULT_URL = "local:";
    private final int INSTALL_TIMEOUT = 0;
    private static final AID AID_KEY = AID.PF3;
    private final String ATI_TRANSACTION = "ep03";


    // Variables

    private EPIGateway gateway;
    private Terminal cicsTerminal = null;
    private Screen screen = null;
    private ReplyHandler replyHandler = null;
    private String devType = null;
    private String netName = null;
    private String userID = null;
    private String password = null;
    private int readTimeOut = 0;
    private String encoding = null;

    public String displayData(String urlStr,String strServer,String transactionId){
    	
    	String displayText = "";
    	try {
    		if(openGateway(urlStr)){
    			displayText = runTransaction(strServer,transactionId);
        	} else {
        		System.out.println("error:Gateway closed...");
        	}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeGateway();
		}
		return displayText;
    }
    
    public void findServer(String urlStr){
    	if(openGateway(urlStr)){
    		getServers();
    	} else {
    		SysLogger.info("error...");
    	}
    	closeGateway();
    }
    /* Open gateway

       Note that this cics includes an example of using the security cicss.
       However not all applications would require this. */
    
    public boolean openGateway(String args) {
    	gateway = new EPIGateway();
    	try{
    		if(args.equals("local")){
            	gateway.setURL(DEFAULT_URL);
                gateway.setPort(DEFAULT_PORT);
            } else {
            	gateway.setURL(args);
            }
            //SysLogger.info("Gateway: " + gateway.getURL());
            //SysLogger.info("Opening gateway to " + gateway.getAddress());
    		gateway.open();
    	} catch (IOException e) {
    		SysLogger.info("Gateway Open failed:"+e.toString());
    		return false;
    	} finally {
    		
    	}
    	SysLogger.info("Gateway has Opened... ");
		return true;
    }


    /* Get servers
       Retrieves a list of those servers listed in the ctg.ini file of the
       gateway. */

    public void getServers() {
        int totalServers;
        String serverName[];
        CicsConfigDao dao = new CicsConfigDao();
		try {
			totalServers = gateway.serverCount();
			if (totalServers == 0) {
				SysLogger.info("No servers are listed in the ctg.ini file" +
	                               " of this transaction gateway");
	            gateway.close();
	        }
			serverName = new String[totalServers];
	        for (int i = 0; i < totalServers; i++) {
	            int counter = i+1;
	            SysLogger.info(counter + ": " + gateway.serverName(counter) + gateway.getURL());
	            serverName[i] = gateway.serverName(counter);
	        }
	        if(dao.delete()){
	        	if(dao.save(serverName,gateway.getURL())){
	        		SysLogger.info("save successed...");
	            } else {
	            	SysLogger.info("save failed...");
	            }
	        }
	        
		} catch (IOException e) {
			SysLogger.info("save failed:"+e.toString());
		} catch (EPIException e) {
			SysLogger.info("save failed:"+e.toString());
		} finally {
			dao.close();
		}
    }

    private String runTransaction(String server,String transactionId) throws IOException, Exception
    , InterruptedException
    {

        /* Initialise the terminal object.
           The terminal is extended and signon incapable. */

        cicsTerminal = new Terminal(
                                   gateway,
                                   server,
                                   devType,
                                   netName,
                                   Terminal.EPI_SIGNON_INCAPABLE,
                                   userID,
                                   password,
                                   readTimeOut,
                                   encoding);

        SysLogger.info("\n\tGateway \t= \t"
                           + gateway.getURL()
                           + " \n\tServer \t\t= \t"
                           + cicsTerminal.getServerName()
                           + " \n\tExtended \t= \t"
                           + cicsTerminal.isExtendedTerminal());
        SysLogger.info("\nState before connecting = " + this.getStateString(cicsTerminal.getState()));
        // Connect - synchronously
        SysLogger.info("Connecting.");
        boolean connected = false;
        int attempts = 0;
        while (!connected) {  
            try {
                cicsTerminal.connect(INSTALL_TIMEOUT);
                connected = true;
            } catch (EPIRequestException requestException) {

                //check if the connection has failed because of a security error
                if (requestException.getErrorCode() == EPIRequestException.EPI_ERR_SECURITY) {

                    //increment the number of attempts to connect
                    attempts++;
                    if (attempts <= 3) {
                        System.out.println("Validation Failed\nPlease reenter your CICS details"); 
                        //set them in the Terminal and try to reconnect
                        cicsTerminal.setUserid(userID);
                        cicsTerminal.setPassword(password);
                    } else {
                        //There have been too many unsucessful attempts
                        System.out.println("Unable to log you onto the terminal");
                        System.exit(1);
                    }
                } else {

                    //rethrow the exception
                    throw requestException;
                }
            }
        }
        SysLogger.info("\n\tTerminal Id \t= \t" + cicsTerminal.getTermid());
        /* Before sending the transaction the inner class that handles the
           asynchronous replies to the client application is initialised and
           provided as a parameter to the send method. Also if the
           transaction is EC03 the terminal is ATI enabled.
           The transaction is then sent and the client application waits for
           the reply. */

        SysLogger.info("\nState before sending transaction = " + this.getStateString(cicsTerminal.getState()));
        SysLogger.info("Sending transaction " + transactionId);
        replyHandler = new ReplyHandler();
        /* Note that before the Terminal is ATI enabled the session object
           must be set. */
        if (transactionId.equalsIgnoreCase(ATI_TRANSACTION)) {
            cicsTerminal.setSession(replyHandler);
            cicsTerminal.setATI(true);
            cicsTerminal.send(transactionId, null);
        } else {
            cicsTerminal.send(replyHandler, transactionId, null);
        }
        replyHandler.waitForReply();
        //Loop to process the results of the transaction
        boolean processTransaction = true;
        do {
            switch (cicsTerminal.getState()) {
            case Terminal.client:

                /* The server is expecting a reply.
                   In this cics the PF3 key is sent. As stated above the
                   transaction must be capable of changing the terminal to idle
                   on receipt of PF3. */

                System.out.println("Server is expecting input - " +
                                   "Display current screen and send " + AID_KEY
                                   + " key:");

                try {
                    screen = cicsTerminal.getScreen();
                    return this.displayScreen().toString();
                } catch (UnsupportedEncodingException e) {
                	SysLogger.info("Encoding is not supported:"+e.toString());
                }
                screen.setAID(AID_KEY);
                cicsTerminal.send();
                replyHandler.waitForReply();
                break;

            case Terminal.server:
                // State has returned to server - continue waiting.
                break;
            case Terminal.error:

                // Break out of the loop on error

                System.out.println("ERROR");
                throw new Exception("Terminal error reported");

            case Terminal.idle:


                try {
                    /* Test to see if there is an Automated Transaction in
                       process. If there is then wait for a reply, otherwise
                       attempt to disconnect. */

                    if (transactionId.equalsIgnoreCase(ATI_TRANSACTION)) {
                        System.out.println("ATI enabled = "
                                           + cicsTerminal.queryATI());
                        try {
                            replyHandler.waitForReply();
                        } catch (InterruptedException e) {
                        	SysLogger.info("\n" + e.toString());
                        }
                    }

                    /* The server is finished, so the results are displayed and the
                       terminal disconnected. */
                    SysLogger.info("Display final output and attempt disconnect");
                    try {
                        this.displayScreen();
                    } catch (UnsupportedEncodingException e) {
                    	SysLogger.info("Encoding is not supported:"+e.toString());
                    }

                    cicsTerminal.disconnect();
                    processTransaction = false;
                    break;
                } catch (EPIException e) {
                    throw e;
                }

            case Terminal.discon:

                // Break out of the loop if the terminal is disconnected

                processTransaction = false;
                break;

            case Terminal.failed:

                // Throw an exception if Terminal fails

                System.out.println("FAILED");
                throw new Exception("Terminal failed reported");

            default:

                /* Other case are not required by this cics. Should any occur
                   then throw them as an exception. */

                System.out.println("State = "
                                   + this.getStateString(cicsTerminal.getState()));
                throw new Exception("Unexpected Terminal state reported");
            }
        }while (processTransaction);
		return "";
    }


    // Close the EPIGateway

    public void closeGateway() {
        try {
            if (gateway!=null) {
                if (gateway.isOpen()) {
                    gateway.close();
                }
            }
        } catch (Exception e) {
        	SysLogger.info("Exception closing gateway:"+e.toString());
        }
    }


    // Static method to return the Terminal state as a string

    public static String getStateString(int state) {
        switch (state) {
        case Terminal.start:
            return "start";
        case Terminal.idle:
            return "idle";
        case Terminal.client:
            return "client";
        case Terminal.server:
            return "server";
        case Terminal.discon:
            return "disconnected";
        case Terminal.error:
            return "error";
        case Terminal.failed:
            return "failed";
        default:
            return "state not found";
        }
    }


    // Display the contents of the current screen object

    private String displayScreen() throws UnsupportedEncodingException
    {
        screen = cicsTerminal.getScreen();
        String str="";
        for (int i = 1; i <= screen.fieldCount(); i++) {
            if ((screen.field(i)).textLength() > 0) {
            	str=str+screen.field(i).getText()+",";
            }
        }
        String Arr[]=str.split(",");
        if(Arr[0].equals("CST1")){
        	return format_CST1(Arr);
        }
        if(Arr[0].equals("CST2")){
        	return format_CST2(Arr);
        }
        if(Arr[0].equals("CST3")){
        	return format_CST3(Arr);
        }
        if(Arr[0].equals("CST4")){
        	return format_CST4(Arr);
        }
        if(Arr[0].equals("CST5")){
        	int page = Integer.parseInt(Arr[10])/10;
        	String[] screenStr = pageMethod(str, page);
        	return format_CST5(screenStr);
        }
        if(Arr[0].equals("CST6")){
        	int page = Integer.parseInt(Arr[10])/10;
        	String[] screenStr = pageMethod(str, page);
        	return format_CST6(screenStr);
        }
        if(Arr[0].equals("CST7")){
        	int page = Integer.parseInt(Arr[10])/30;
        	String[] screenStr = pageMethod(str, page);
        	return format_CST7(screenStr);
        }
        if(Arr[0].equals("CST8")){
        	int page = Integer.parseInt(Arr[10])/30;
        	String[] screenStr = pageMethod(str, page);
        	return format_CST8(screenStr);
        }
        if(Arr[0].equals("CST9")){
        	return format_CST9(Arr);
        }
        if(Arr[0].equals("CSTA")){
        	int page = Integer.parseInt(Arr[10])/30;
        	String[] screenStr = pageMethod(str, page);
        	return format_CSTA(screenStr);
        }
        if(Arr[0].equals("CSTB")){
        	return format_CSTB(Arr);
        }
		return format_CST1(Arr);
    }

	private String[] pageMethod(String str,int page) throws UnsupportedEncodingException {
		String screenStr[] = new String[page+1];
		screenStr[0]=str;
		if(page>0){
			for(int i=1;i<page+1;i++){
				screen.setAID(AID.PF8);
		        try {
					cicsTerminal.send();
				} catch (EPIException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(1000);//
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int j = 1; j <= screen.fieldCount(); j++) {
		            if ((screen.field(j)).textLength() > 0) {
		            	screenStr[i] = screenStr[i]+screen.field(j).getText()+",";
		            }
		        }
				screenStr[i] = screenStr[i] + "null";//CST7和CST8如果有翻页需要加，CST5和CST6不确定
			}
		}
		return screenStr;
	}

    private String format_CST1(String Arr[]) {
    	String cst1_Str = "";
    	cst1_Str = "<table cellspacing=1 cellpadding=0 width=100%><tr><td>"+Arr[0]+"&nbsp;"+Arr[1]
    	           +"&nbsp;"+Arr[2]+"</td><td>"+Arr[3]+"</td><td>"+Arr[4]+"</td><td>" +
    	           "<div align=right>"+Arr[5]+Arr[6]+"</div></td></tr></table>&nbsp;&nbsp;" +
    	           
    	           "<table cellspacing=1 cellpadding=0 width=100%><caption align=top><strong>"+
    	           Arr[7]+"</strong></caption><tr class=microsoftLook0 height=28><td width=25%>&nbsp;</td>" +
    	           "<td><div align=center width=25%>"+Arr[8]+"</div></td><td width=25%><div align=center>"+
    	           Arr[9]+"</div></td><td width=25%><div align=center>"+Arr[10]+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[11]+"</div>" +
    	           "</td><td><div align=center>"+Long.valueOf(Arr[12])+"</div></td><td><div align=center>"+
    	           Long.valueOf(Arr[13])+"</div>" + "</td><td><div align=center>"+Long.valueOf(Arr[14])+
    	           "</div></td></tr><tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+
    	           Arr[15]+ "</div></td><td><div align=center>"+Long.valueOf(Arr[16])+"</div></td><td><div align=center>"+
    	           Long.valueOf(Arr[17])+"</div></td><td><div align=center>"+Long.valueOf(Arr[18])+"</div></td></tr>" +
    	           "</table>&nbsp;&nbsp;"+
    	           
    	           "<table cellspacing=1 cellpadding=0 width=100%>" +
    	           "<tr class=microsoftLook0 height=28><td width=25%><div align=right>"+Arr[19]+"</div></td><td bgcolor=DEEBF7 align=center width=25%>"+Long.valueOf(Arr[20])+"</td><td width=25%><div align=right>"+Arr[21]+"</div></td><td bgcolor=DEEBF7 align=center width=25%>"+Long.valueOf(Arr[22])+"</td></tr>"+
    	           "<tr class=microsoftLook0 height=28><td><div align=right>"+Arr[23]+"</div></td><td bgcolor=DEEBF7 align=center>"+Long.valueOf(Arr[24])+"</td><td><div align=right>"+Arr[25]+"</div></td><td bgcolor=DEEBF7 align=center>"+Long.valueOf(Arr[26])+"</td></tr>"+
    	           "<tr class=microsoftLook0 height=28><td><div align=right>"+Arr[27]+"</div></td><td bgcolor=DEEBF7 align=center>"+Long.valueOf(Arr[28])+"</td><td><div align=right>"+Arr[29]+"</div></td><td bgcolor=DEEBF7 align=center>"+Long.valueOf(Arr[30])+"</td></tr>"+
    	           "<tr class=microsoftLook0 height=28><td><div align=right>"+Arr[31]+"</div></td><td bgcolor=DEEBF7 align=center>"+Long.valueOf(Arr[32])+"</td><td><div align=right>"+Arr[33]+"</div></td><td bgcolor=DEEBF7 align=center>"+Long.valueOf(Arr[34])+"</td></tr>"+
    	           "<tr class=microsoftLook0 height=28><td><div align=right>"+Arr[35]+"</div></td><td bgcolor=DEEBF7 align=center>"+Long.valueOf(Arr[36])+"</td><td><div align=right>"+Arr[37]+"</div></td><td bgcolor=DEEBF7 align=center>"+Long.valueOf(Arr[38])+"</td></tr>"+
    	           "<tr class=microsoftLook0 height=28><td><div align=right>"+Arr[39]+"</div></td><td bgcolor=DEEBF7 align=center>"+Long.valueOf(Arr[40])+"</td><td><div align=right>"+Arr[41]+"</div></td><td bgcolor=DEEBF7 align=center>"+Long.valueOf(Arr[42])+"</td></tr>"+
    	           "</table>"+
    	           "<table cellspacing=1 cellpadding=0 width=100%>" +
    	           "<caption align=top><strong>"+Arr[43]+"</strong></caption>"+
    	           "<tr class=microsoftLook0 height=28><td width=30%><div align=right>&nbsp;</div></td><td><div align=center>"+Arr[44]+"</div></td><td><div align=center>"+Arr[45]+"</div></td></tr>"+
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[46]+"</div></td><td><div align=center>"+Long.valueOf(Arr[47])+"</div></td><td><div align=center>"+Long.valueOf(Arr[48])+"</div></td></tr>"+
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[49]+"</div></td><td><div align=center>"+Long.valueOf(Arr[50])+"</div></td><td><div align=center>"+Long.valueOf(Arr[51])+"</div></td></tr>"+
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[52]+"</div></td><td><div align=center>"+Long.valueOf(Arr[53])+"</div></td><td><div align=center>"+Long.valueOf(Arr[54])+"</div></td></tr>"+
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[55]+"</div></td><td><div align=center>"+Long.valueOf(Arr[56])+"</div></td><td><div align=center>"+Long.valueOf(Arr[57])+"</div></td></tr>"+
    	           "</table>";
		return cst1_Str;
    	
    }
    
    private String format_CST2(String Arr[]) {
    	String cst2_Str = "";
    	cst2_Str = "<table cellspacing=1 cellpadding=0 width=100%><tr><td>"+Arr[0]+"&nbsp;"+Arr[1]
    	           +"&nbsp;"+Arr[2]+"</td><td>"+Arr[3]+"</td><td>"+Arr[4]+"</td><td>" +
    	           "<div align=right>"+Arr[5]+Arr[6]+"</div></td></tr></table>&nbsp;&nbsp;" +
    	           
    	           "<table cellspacing=1 cellpadding=0 width=100%><caption align=top><strong>" +
    	           Arr[7]+"</strong></caption><tr class=microsoftLook0 height=28><td>&nbsp;</td>" +
    	           "<td><div align=center>"+Arr[8]+"</div></td><td><div align=center>"+Arr[9]+
    	           "</div></td><td><div align=center>"+Arr[10]+"</div></td><td><div align=center>"+
    	           Arr[11]+"</div></td><td><div align=center>"+Arr[12]+"</div></td><td><div align=center>"+
    	           Arr[13]+"</div></td></tr><tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+
    	           Arr[14]+"</div></td><td align=center>"+Long.valueOf(Arr[15])+"</td><td align=center>"+Long.valueOf(Arr[16])+
    	           "</td><td align=center>"+Long.valueOf(Arr[17])+"</td><td align=center>"+Long.valueOf(Arr[18])+
    	           "</td><td align=center>"+Long.valueOf(Arr[19])+"</td><td align=center>"+Long.valueOf(Arr[20])+"</td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[21]+
    	           "</div></td><td align=center>"+Long.valueOf(Arr[22])+"</td><td align=center>"+Long.valueOf(Arr[23])+
    	           "</td><td align=center>"+Long.valueOf(Arr[24])+"</td><td align=center>"+Long.valueOf(Arr[25])+
    	           "</td><td align=center>"+Long.valueOf(Arr[26])+"</td><td align=center>"+Long.valueOf(Arr[27])+"</td></tr>" +
    	           "</table>&nbsp;&nbsp;"+
    	           
    	           "<table cellspacing=1 cellpadding=0 width=100%>" +
    	           "<tr class=microsoftLook0 height=28><td width=25%>&nbsp;</td><td colspan=2><div align=center>"+
    	           Arr[28]+"</div></td><td colspan=2><div align=center>" + Arr[29]+"</div></td><td></td></tr>" +
    	           "<tr class=microsoftLook0 height=28><td  width=25%>&nbsp;</td><td  width=15%><div align=center>"+
    	           Arr[30]+"</div></td><td width=15%><div align=center>"+Arr[31]+"</div></td><td width=15%><div align=center>"+
    	           Arr[32]+"</div></td><td width=15%><div align=center>"+Arr[33]+"</div></td><td width=15%><div align=center>"+
    	           Arr[34]+"</div></td></tr><tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[35]+
    	           "</div></td><td align=center>" +Long.valueOf(Arr[36])+"</td><td align=center>"+Long.valueOf(Arr[37])+
    	           "</td><td align=center>"+Long.valueOf(Arr[38])+"</td><td align=center>"+Long.valueOf(Arr[39])+
    	           "</td><td align=center>"+Long.valueOf(Arr[40])+"</td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[41]+
    	           "</div></td><td align=center>"+Long.valueOf(Arr[42])+"</td><td align=center>"+Long.valueOf(Arr[43])+
    	           "</td><td align=center>"+Long.valueOf(Arr[44])+"</td><td align=center>"+Long.valueOf(Arr[45])+
    	           "</td><td align=center>"+Long.valueOf(Arr[46])+"</td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25>" +
    	           "<div align=right>"+Arr[47]+"</div></td><td align=center>"+Long.valueOf(Arr[48])+
    	           "</td><td align=center>"+Long.valueOf(Arr[49])+"</td><td align=center>" +
    	           Long.valueOf(Arr[50])+"</td><td align=center>&nbsp;</td><td align=center>"+Long.valueOf(Arr[51])+"</td></tr>" +
    	           "</table>"+
    	           
    	           "<table cellspacing=1 cellpadding=0 width=100%><tr><td><div align=right>" +Arr[52]+
    	           "</div></td><td height=25><div align=left>"+Long.valueOf(Arr[53])+"</div></td></tr>" +
    	           "<tr><td><div align=right>" +Arr[54]+"</div></td><td height=25><div align=left>"+
    	           Long.valueOf(Arr[55])+"</div></td></tr></table>"
    	           ;
		return cst2_Str;
    	
    }
    private String format_CST3(String Arr[]) {
    	String cst3_Str = "";
    	cst3_Str = "<table cellspacing=1 cellpadding=0 width=100%><tr><td>"+Arr[0]+"&nbsp;"+Arr[1]
    	           +"&nbsp;"+Arr[2]+"</td><td>"+Arr[3]+"</td><td>"+Arr[4]+"</td><td>" +
    	           "<div align=right>"+Arr[5]+Arr[6]+"</div></td></tr></table>&nbsp;&nbsp;" +
    	           
    	           "<table cellspacing=1 cellpadding=0 width=100%><caption align=top><strong>"+Arr[7]+
    	           "</strong></caption>" +
    	           "<tr bgcolor=DEEBF7><td width=50% class=microsoftLook0 height=25><div align=right>"+Arr[8]+"</div></td><td width=50%><div align=center>"+Long.valueOf(Arr[9])+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[10]+"</div></td><td><div align=center>"+Long.valueOf(Arr[11])+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[12]+"</div></td><td><div align=center>"+Long.valueOf(Arr[13])+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[14]+"</div></td><td><div align=center>"+Long.valueOf(Arr[15])+"</div></td></tr>" +
    	           "</table>&nbsp;&nbsp;"+
    	           
    	           "<table cellspacing=1 cellpadding=0 width=100%><tr class=microsoftLook0 height=28 align=center><td colspan=2>"+Arr[16]+"</td></tr>" +
    	           "<tr bgcolor=DEEBF7><td width=50% class=microsoftLook0 height=25><div align=right>"+Arr[17]+"</div></td><td><div align=center>"+Long.valueOf(Arr[18])+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[19]+"</div></td><td><div align=center>"+Long.valueOf(Arr[20])+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[21]+"</div></td><td><div align=center>"+Long.valueOf(Arr[22])+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[23]+"</div></td><td><div align=center>"+Long.valueOf(Arr[24])+"</div></td></tr>" +
    	           "</table>&nbsp;&nbsp;"+
    	           
    	           "<table cellspacing=1 cellpadding=0 width=100%>" +
    	           "<tr bgcolor=DEEBF7><td width=50% class=microsoftLook0 height=25><div align=right>"+Arr[25]+"</div></td><td><div align=center>"+Long.valueOf(Arr[26])+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[27]+"</div></td><td><div align=center>"+Long.valueOf(Arr[28])+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[29]+"</div></td><td><div align=center>"+Long.valueOf(Arr[30])+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[31]+"</div></td><td><div align=center>"+Long.valueOf(Arr[32])+"</div></td></tr>"+
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[33]+"</div></td><td><div align=center>"+Long.valueOf(Arr[34])+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[35]+"</div></td><td><div align=center>"+Long.valueOf(Arr[36])+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[37]+"</div></td><td><div align=center>"+Long.valueOf(Arr[38])+"</div></td></tr>" +
    	           "</table>";
		return cst3_Str;
    	
    }
    private String format_CST4(String Arr[]) {
    	String cst4_Str = "";
    	cst4_Str = "<table cellspacing=1 cellpadding=0 width=100%><tr><td>"+Arr[0]+"&nbsp;"+Arr[1]
    	           +"&nbsp;"+Arr[2]+"</td><td>"+Arr[3]+"</td><td>"+Arr[4]+"</td><td>" +
    	           "<div align=right>"+Arr[5]+Arr[6]+"</div></td></tr></table>&nbsp;&nbsp;" +
    	           
    	           "<table cellspacing=1 cellpadding=0 width=100%><caption align=top><strong>"+Arr[7]+"</strong></caption>" +
    	           "<tr><td colspan=4 height=24><div align=right>"+Arr[9]+"&nbsp;"+Arr[10]+"&nbsp;"+Arr[11]+Arr[12]+"</div></td></tr>"+
    	           "<tr class=microsoftLook0 height=28><td width=25%>&nbsp;</td><td width=25%><div align=center>"+Arr[13]+"</div></td><td width=25%><div align=center>"+Arr[14]+"</div></td><td width=25%><div align=center>"+Arr[15]+
    	           "</div></td></tr>"+"<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[16]+"</div></td><td><div align=center>"+Long.valueOf(Arr[17])+"</div></td><td><div align=center>"+Long.valueOf(Arr[18])+"</div></td><td><div align=center>"+Long.valueOf(Arr[19])+
    	           "</div></td></tr>"+"<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[20]+"</div></td><td><div align=center>"+Long.valueOf(Arr[21])+"</div></td><td><div align=center>"+Long.valueOf(Arr[22])+"</div></td><td><div align=center>"+Long.valueOf(Arr[23])+
    	           "</div></td></tr>"+"<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[24]+"</div></td><td><div align=center>"+Long.valueOf(Arr[25])+"</div></td><td><div align=center>"+Long.valueOf(Arr[26])+"</div></td><td><div align=center>"+Long.valueOf(Arr[27])+
    	           "</div></td></tr>"+"<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[28]+"</div></td><td><div align=center>"+Long.valueOf(Arr[29])+"</div></td><td><div align=center>"+Long.valueOf(Arr[30])+"</div></td><td><div align=center>"+Long.valueOf(Arr[31])+
    	           "</div></td></tr>"+"<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[32]+"</div></td><td><div align=center>"+Long.valueOf(Arr[33])+"</div></td><td><div align=center>"+Long.valueOf(Arr[34])+"</div></td><td><div align=center>"+Long.valueOf(Arr[35])+
    	           "</div></td></tr>"+"<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[36]+"</div></td><td><div align=center>"+Long.valueOf(Arr[37])+"</div></td><td><div align=center>"+Long.valueOf(Arr[38])+"</div></td><td><div align=center>"+Long.valueOf(Arr[39])+
    	           "</div></td></tr>"+"<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[40]+"</div></td><td><div align=center>"+Long.valueOf(Arr[41])+"</div></td><td><div align=center>"+Long.valueOf(Arr[42])+"</div></td><td>&nbsp;"+
    	           "</div></td></tr>"+"<tr bgcolor=DEEBF7><td class=microsoftLook0 height=25><div align=right>"+Arr[43]+"</div></td><td>&nbsp;</td><td><div align=center>"+Long.valueOf(Arr[44])+"</td><td>&nbsp;"+
    	           "</div></td></tr></table>&nbsp;&nbsp;"+
    	           
    	           "<table cellspacing=1 cellpadding=0 width=100%><caption align=top><strong>"+Arr[45]+"</strong></caption>"+
    	           "<tr bgcolor=DEEBF7><td width=50% class=microsoftLook0 height=25><div align=right>"+Arr[46]+"</div></td><td><div align=center>"+Long.valueOf(Arr[47])+"</div></td></tr>"+
    	           "<tr bgcolor=DEEBF7><td width=50% class=microsoftLook0 height=25><div align=right>"+Arr[48]+"</div></td><td><div align=center>"+Long.valueOf(Arr[49])+"</div></td></tr>"+
    	           "<tr bgcolor=DEEBF7><td width=50% class=microsoftLook0 height=25><div align=right>"+Arr[50]+"</div></td><td><div align=center>"+Long.valueOf(Arr[51])+"</div></td></tr>"+
    	           "<tr bgcolor=DEEBF7><td width=50% class=microsoftLook0 height=25><div align=right>"+Arr[52]+"</div></td><td><div align=center>"+Long.valueOf(Arr[53])+"</div></td></tr>"+
    	           "<tr bgcolor=DEEBF7><td width=50% class=microsoftLook0 height=25><div align=right>"+Arr[54]+"</div></td><td><div align=center>"+Long.valueOf(Arr[55])+"</div></td></tr>"+
    	           "</table>";
		return cst4_Str;
    	
    }
    private String format_CST5(String screenStr[]) {
    	String cst5_Str = "";
    	String cst5_headStr = "";
    	String cst5_bodyStr = "";
    	String cst5_footStr = "";
    	for(int i = 0;i < screenStr.length; i++){
         	String Arr[] = screenStr[i].split(",");
         	if(i==0){
         		cst5_headStr = "<table cellspacing=1 cellpadding=0 width=100%><tr><td>"+Arr[0]+"&nbsp;"+Arr[1]
         		               +"&nbsp;"+Arr[2]+"</td><td>"+Arr[3]+"</td><td>"+Arr[4]+"</td><td>" +
         		               "<div align=right>"+Arr[5]+Arr[6]+"</div></td></tr></table>&nbsp;&nbsp;" +
         		               
                	           "<table cellspacing=1 cellpadding=0 width=100%><caption align=top><strong>"+Arr[7]+
                	           Arr[8]+Arr[9]+Arr[10]+Arr[11]+"</strong></caption>"+
                	           "<tr class=microsoftLook0 height=28><td width=12%><div align=center>"+Arr[12]+
                	           "</div></td><td width=12%><div align=center>"+Arr[16]+
                	           "</div></td><td width=12%><div align=center>"+Arr[13]+"</div></td><td width=12%><div align=center>"+Arr[17]+
                	           "</div></td><td width=12%><div align=center>"+Arr[14]+"</div></td><td width=12%><div align=center>"+Arr[18]+
                	           "</div></td><td width=12%><div align=center>"+Arr[15]+"</div></td><td width=12%><div align=center>"+Arr[19]+
                	           "</div></td></tr>";
         		cst5_footStr = "</table>&nbsp;&nbsp;<table cellspacing=1 cellpadding=0 width=100%><tr class=microsoftLook0 height=28><td colspan=8>" +
         				       "<div align=center>"+Arr[Arr.length-18]+"</div></td></tr>" +
         		               "<tr  bgcolor=DEEBF7 height=25><td width=12% align=center>"+Long.valueOf(Arr[Arr.length-17])+"</td><td width=12% align=center>"+
         		            		  Long.valueOf(Arr[Arr.length-16])+"</td><td width=12% align=center>"+Long.valueOf(Arr[Arr.length-15])+
         		               "</td><td width=12% align=center>"+Long.valueOf(Arr[Arr.length-14])+"</td><td width=12% align=center>"+Long.valueOf(Arr[Arr.length-13])+
         		               "</td><td width=12% align=center>"+Long.valueOf(Arr[Arr.length-12])+
         		               "</td><td width=12% align=center>"+Long.valueOf(Arr[Arr.length-11])+"</td><td width=12% align=center>"+Long.valueOf(Arr[Arr.length-10])+
 	                           "</td></tr></table>";
         	}
         	for(int j=20;j<Arr.length-25;j=j+8){
        		cst5_bodyStr = cst5_bodyStr+"<tr bgcolor=DEEBF7 height=25><td align=center>"+Arr[j]+"</td><td align=center>"+Long.valueOf(Arr[j+1])+
        		               "</td><td align=center>"+Long.valueOf(Arr[j+2])+"</td><td align=center>"+Long.valueOf(Arr[j+3])+"</td><td align=center>"+Long.valueOf(Arr[j+4])+
        		               "</td><td align=center>"+Long.valueOf(Arr[j+5])+"</td><td align=center>"+Long.valueOf(Arr[j+6])+"</td><td align=center>"
        		               +Long.valueOf(Arr[j+7])+"</td><tr>";
        	}
    	}
    	cst5_Str = cst5_headStr + cst5_bodyStr + cst5_footStr;
		return cst5_Str;
    	
    }
    private String format_CST6(String screenStr[]) {
    	String cst6_Str = "";
    	String cst6_headStr = "";
    	String cst6_bodyStr = "";
    	String cst6_footStr = "";
    	for(int i = 0;i < screenStr.length; i++){
         	String Arr[] = screenStr[i].split(",");
         	if(i==0){
         		cst6_headStr = "<table cellspacing=1 cellpadding=0 width=100%><tr><td>"+Arr[0]+"&nbsp;"+Arr[1]
         		               +"&nbsp;"+Arr[2]+"</td><td>"+Arr[3]+"</td><td>"+Arr[4]+"</td><td>" +
         		               "<div align=right>"+Arr[5]+Arr[6]+"</div></td></tr></table>&nbsp;&nbsp;" +
         		               
         		               "<table cellspacing=1 cellpadding=0 width=100%><caption align=top><strong>"+Arr[7]+
	               	           Arr[8]+Arr[9]+Arr[10]+Arr[11]+"</strong></caption>"+
	               	           "<tr class=microsoftLook0 height=28><td width=16% align=center>"+Arr[13]+
	               	           "</td><td width=16% align=center>"+Arr[14]+"</td><td width=16% align=center>"+Arr[15]+
	               	           "</td><td width=16% align=center>"+Arr[16]+"</td><td width=16% align=center>"+Arr[17]+
	               	           "</td><td width=16% align=center>"+Arr[18]+
	               	           "</td></tr>";
         		
         		cst6_footStr = "</table>&nbsp;&nbsp;<table cellspacing=1 cellpadding=0 width=100%><tr class=microsoftLook0 height=28><td colspan=8>" +
			                   "<div align=center>"+Arr[Arr.length-16]+"</div></td></tr>" +
	                           "<tr bgcolor=DEEBF7 height=25><td width=12% align=center>"+Long.valueOf(Arr[Arr.length-15])+
	                           "</td><td width=12% align=center>"+Long.valueOf(Arr[Arr.length-14])+
	                           "</td><td width=12% align=center>"+Long.valueOf(Arr[Arr.length-13])+"</td><td width=12% align=center>"+Long.valueOf(Arr[Arr.length-12])+
	                           "</td><td width=12% align=center>"+Long.valueOf(Arr[Arr.length-11])+"</td><td width=12% align=center>"+Long.valueOf(Arr[Arr.length-10])+
                               "</td></tr></table>";
         	}
         	for(int j=19;j<Arr.length-21;j=j+6){
        		cst6_bodyStr = cst6_bodyStr+"<tr bgcolor=DEEBF7 height=25><td align=center>"+Arr[j]+"</td><td align=center>"+Long.valueOf(Arr[j+1])+
        		               "</td><td align=center>"+Long.valueOf(Arr[j+2])+"</td><td align=center>"
        		               +Long.valueOf(Arr[j+3])+"</td><td align=center>"+Long.valueOf(Arr[j+4])+"</td><td align=center>"+Long.valueOf(Arr[j+5])+"</td><tr>";
        	}
    	}
    	cst6_Str = cst6_headStr + cst6_bodyStr + cst6_footStr;
		return cst6_Str;
    	
    }
    private String format_CST7(String screenStr[]) {
    	String cst7_Str = "";
    	String cst7_headStr = "";
    	String cst7_bodyStr = "";
    	String cst7_footStr = "";
        for(int i = 0;i < screenStr.length; i++) {
        	String Arr[] = screenStr[i].split(",");
        	if(i==0) {
        		cst7_headStr = "<table cellspacing=1 cellpadding=0 width=100%><tr><td>"+Arr[0]+"&nbsp;"+Arr[1]
        		               +"&nbsp;"+Arr[2]+"</td><td>"+Arr[3]+"</td><td>"+Arr[4]+"</td><td>" +
        		               "<div align=right>"+Arr[5]+Arr[6]+"</div></td></tr></table>&nbsp;&nbsp;" +
        		               
        		               "<table cellspacing=1 cellpadding=0 width=100%><caption align=top><strong>"+Arr[7]+
        		               Arr[8]+Arr[9]+Arr[10]+Arr[11]+"</strong></caption>"+"<tr class=microsoftLook0 height=28>" +
        		               "<td align=center>"+Arr[12]+"</td><td align=center>"+Arr[15]+"</td><td align=center>"+Arr[13]+
        		               "</td><td align=center>"+ Arr[16]+"</td><td align=center>"+Arr[14]+"</td><td align=center>"+Arr[17]+
        		               "</td></tr><tr bgcolor=DEEBF7 height=25>";
        		
        		cst7_footStr = "</tr><tr height=28><td colspan=6 align=center>"+Arr[Arr.length-13]+"&nbsp;"+Long.valueOf(Arr[Arr.length-12])+
        		               "&nbsp;&nbsp;&nbsp;"+Arr[Arr.length-11]+"&nbsp;"+Long.valueOf(Arr[Arr.length-10])+"</td></tr></table>";
 	
        	}
        	int k=0;
        	for(int j=18;j<Arr.length-14;j=j+2){
        		cst7_bodyStr = cst7_bodyStr+"<td align=center>"+Arr[j]+"</td><td align=center>"+Long.valueOf(Arr[j+1])+"</td>";// +
//        				"<td align=center>"+Arr[j+2]+"</td><td align=center>"+Long.valueOf(Arr[j+3])+"</td>" +
//        			    "<td align=center>"+Arr[j+4]+"</td><td align=center>"+ Long.valueOf(Arr[j+5])+"</td></tr>";
        		k++;
        		if(k == 3){
        		    cst7_bodyStr = cst7_bodyStr + "</tr><tr bgcolor=DEEBF7 height=25>";
            	    k=0;
        			
        		}
        	}
        }
        
    	cst7_Str = cst7_headStr + cst7_bodyStr + cst7_footStr;
    	
		return cst7_Str;
    	
    }
    private String format_CST8(String screenStr[]) {
    	String cst8_Str = "";
    	String cst8_headStr = "";
    	String cst8_bodyStr = "";
    	String cst8_footStr = "";
    	for(int i = 0;i < screenStr.length; i++) {
    		String Arr[] = screenStr[i].split(",");
    		if(i==0) {
    			cst8_headStr = "<table cellspacing=1 cellpadding=0 width=100%><tr><td>"+Arr[0]+"&nbsp;"+Arr[1]
    			               +"&nbsp;"+Arr[2]+"</td><td>"+Arr[3]+"</td><td>"+Arr[4]+"</td><td>" +
    			               "<div align=right>"+Arr[5]+Arr[6]+"</div></td></tr></table>&nbsp;&nbsp;" +
    			               
    			               "<table cellspacing=1 cellpadding=0 width=100%><caption align=top><strong>"+Arr[7]+
        		               Arr[8]+Arr[9]+Arr[10]+Arr[11]+"</strong></caption>"+
        		               "<tr class=microsoftLook0 height=28><td align=center>"+Arr[15]+"</td><td align=center>"+Arr[12]+
        		               "</td><td align=center>"+Arr[16]+"</td><td align=center>"+Arr[17]+"</td><td align=center>"+Arr[13]+
        		               "</td><td align=center>"+Arr[18]+"</td></tr><tr bgcolor=DEEBF7 height=25>";
    			
    			cst8_footStr = "</tr><tr height=28><td colspan=6 align=center>"+Arr[Arr.length-15]+"&nbsp;"+Long.valueOf(Arr[Arr.length-14])+"&nbsp;&nbsp;&nbsp;"+
    			               Arr[Arr.length-13]+"&nbsp;"+Long.valueOf(Arr[Arr.length-12])+"&nbsp;&nbsp;&nbsp;"+
                               Arr[Arr.length-11]+"&nbsp;"+Long.valueOf(Arr[Arr.length-10])+"</td></tr></table>";
    		}
    		int k=0;
    		for(int j=21;j<Arr.length-16;j=j+3){
        		cst8_bodyStr = cst8_bodyStr+"<td align=center>"+Arr[j]+"</td><td align=center>"+
        		               Long.valueOf(Arr[j+1])+"</td><td align=center>"+Long.valueOf(Arr[j+2])+"</td>" ;//+
//        		               "<td align=center>"+Arr[j+3]+"</td><td align=center>"+
//        		               Long.valueOf(Arr[j+4])+"</td><td align=center>"+Long.valueOf(Arr[j+5])+"</td></tr>";
        		k++;
        		if(k == 2){
        			cst8_bodyStr = cst8_bodyStr + "</tr><tr bgcolor=DEEBF7 height=25>";
            	    k=0;
        			
        		}
        	}
    	}
    	
    	cst8_Str = cst8_headStr + cst8_bodyStr + cst8_footStr;
    	
		return cst8_Str;
    	
    }
private String format_CST9(String Arr[]) {
    String cst9_Str = "";
	String cst9_headStr = "";
	String cst9_bodyStr = "";
	String cst9_footStr = "";
	cst9_headStr = "<table cellspacing=1 cellpadding=0 width=100%><tr><td>"+Arr[0]+"&nbsp;"+Arr[1]
	               +"&nbsp;"+Arr[2]+"</td><td>"+Arr[3]+"</td><td>"+Arr[4]+"</td><td>" +
	               "<div align=right>"+Arr[5]+Arr[6]+"</div></td></tr></table>&nbsp;&nbsp;" +
	               "<table cellspacing=1 cellpadding=0 width=100%><caption align=top><strong>"+Arr[7]+
	               "</strong></caption><tr class=microsoftLook0 height=28><td>&nbsp;</td><td align=center>"+Arr[9]+
	               "</td><td align=center>"+Arr[12]+"</td><td align=center>"+Arr[10]+"</td><td align=center>"+
	               Arr[13]+"</td><td align=center>"+Arr[11]+"</td><td align=center>"+Arr[14]+"</td></tr>";
	
    for(int i=15;i<Arr.length-8;i=i+7){
	    cst9_bodyStr = cst9_bodyStr + "<tr bgcolor=DEEBF7 height=25><td align=center>"+Arr[i]+
	                   "</td><td align=center>"+Long.valueOf(Arr[i+1])+"</td><td align=center>"+
	                   Long.valueOf(Arr[i+2])+"</td><td align=center>"+Long.valueOf(Arr[i+3])+
	                   "</td><td align=center>"+Long.valueOf(Arr[i+4])+"</td><td align=center>"+
	                   Long.valueOf(Arr[i+5])+"</td><td align=center>"+Long.valueOf(Arr[i+6])+"</td></tr>" ;
    }
    
	cst9_footStr = "</table>";
	
	cst9_Str = cst9_headStr + cst9_bodyStr + cst9_footStr;
	
	return cst9_Str;
	
}
    private String format_CSTA(String screenStr[]) {
    	String csta_Str = "";
    	String csta_headStr = "";
    	String csta_bodyStr = "";
    	String csta_footStr = "";
    	for(int i = 0;i < screenStr.length; i++){
         	String Arr[] = screenStr[i].split(",");
         	if(i==0){
         		csta_headStr = "<table cellspacing=1 cellpadding=0 width=100%><tr><td>"+Arr[0]+"&nbsp;"+Arr[1]
         		               +"&nbsp;"+Arr[2]+"</td><td>"+Arr[3]+"</td><td>"+Arr[4]+"</td><td>" +
         		               "<div align=right>"+Arr[5]+Arr[6]+"</div></td></tr></table>&nbsp;&nbsp;" +
         		               
         		               "<table cellspacing=1 cellpadding=0 width=100%><caption align=top><strong>"+Arr[7]+
	               	           Arr[8]+Arr[9]+Arr[10]+Arr[11]+"</strong></caption>"+
	               	           "<tr class=microsoftLook0 height=28><td width=90 align=center>"+Arr[15]+"</td>" +
	               	           "<td width=90 align=center>"+Arr[12]+"</td><td width=90 align=center>"+Arr[16]+
	               	           "</td><td width=90 align=center>"+Arr[13]+"</td><td width=90 align=center>"+Arr[17]+
	               	           "</td><td width=100 align=center>"+Arr[14]+
	               	           "</td></tr>";
         		csta_footStr = "</table>&nbsp;&nbsp;<table wcellspacing=1 cellpadding=0 width=100%><tr class=microsoftLook0 height=28>" +
         				       "<td colspan=8>" + "<div align=center>"+Arr[Arr.length-13]+"</div></td></tr>" +
	                           "<tr bgcolor=DEEBF7 height=25><td width=90 align=center>"+Long.valueOf(Arr[Arr.length-12])+"</td><td width=90 align=center>&nbsp;"+
	                           "</td><td width=90 align=center>&nbsp;</td><td width=90 align=center>"+Long.valueOf(Arr[Arr.length-11])+
	                           "</td><td width=90 align=center>"+Long.valueOf(Arr[Arr.length-10])+"</td><td width=100 align=center>"+
	                           Long.valueOf(Arr[Arr.length-9])+"</td></tr></table>";
         	}
         	for(int j=18;j<Arr.length-18;j=j+6){
        		csta_bodyStr = csta_bodyStr+"<tr bgcolor=DEEBF7 height=25><td align=center>"+Arr[j]+"</td><td align=center>"+
        		               Arr[j+1]+"</td><td align=center>"+Arr[j+2]+"</td><td align=center>"+
        		               Long.valueOf(Arr[j+3])+"</td><td align=center>"+Long.valueOf(Arr[j+4])+"</td><td align=center>"+
        		               Long.valueOf(Arr[j+5])+"</td><tr>";
        	}
    	}
    	csta_Str = csta_headStr + csta_bodyStr + csta_footStr;
		return csta_Str;
    	
    }
    private String format_CSTB(String Arr[]) {
    	String cstb_Str = "";
    	cstb_Str = "<table cellspacing=1 cellpadding=0 width=100%><tr><td>"+Arr[0]+"&nbsp;"+Arr[1]
    	           +"&nbsp;"+Arr[2]+"</td><td>"+Arr[3]+"</td><td>"+Arr[4]+"</td></tr></table>&nbsp;&nbsp;" +
    	           
    	           "<table cellspacing=1 cellpadding=0 width=100%><caption align=top><strong>"+Arr[5]+
    	           "</strong></caption>"+
    	           "<tr class=microsoftLook0 align=center><td width=20%>"+Arr[7]+"</td><td width=20%>"+Arr[8]+
    	           "</td><td bgcolor=DEEBF7 height=25 width=20%>"+Long.valueOf(Arr[9])+
    	           "</td><td width=20%>"+Arr[10]+"</td><td bgcolor=DEEBF7 height=25>"+
    	           Long.valueOf(Arr[11])+"</td></tr></table>&nbsp;&nbsp;"+
    	           
    	           "<table cellspacing=1 cellpadding=0 width=100%>"+
    	           "<tr class=microsoftLook0><td colspan=4 height=28><div align=center>"+Arr[12]+"&nbsp;"+Arr[13]+"</div></td></tr>" +
    	           "<tr class=microsoftLook0><td colspan=2 height=28><div align=center>"+Arr[14]+"</div></td><td colspan=2>" +
    	           "<div align=center>"+Arr[15]+"</div></td></tr>" +
    	           "<tr class=microsoftLook0 height=28><td align=center width=24%>"+Arr[16]+"</td><td align=center width=24%>"+Arr[17]+
    	           "</td><td align=center width=24%>"+Arr[18]+ "</td><td align=center width=24%>"+Arr[19]+"</td></tr>" +
    	           "<tr bgcolor=DEEBF7 height=25 align=center><td>"+Long.valueOf(Arr[20])+"</td><td>"+Arr[21]+
    	           "</td><td>"+Long.valueOf(Arr[22])+"</td><td>"+Arr[23]+"</td></tr>" +
    	           "</table>&nbsp;&nbsp;"+
    	           
    	           "<table cellspacing=1 cellpadding=0 width=100%>"+
    	           "<tr class=microsoftLook0 height=28><td colspan=4><div align=center>"+Arr[24]+"&nbsp;"+Arr[25]+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7 height=25><td colspan=2><div align=center>"+Arr[26]+"</div></td><td colspan=2><div align=center>"+Arr[27]+"</div></td></tr>" +
    	           "<tr bgcolor=DEEBF7 height=25 align=center><td width=24%>"+Arr[28]+"</td><td width=24%>"+Arr[29]+"</td><td width=24%>"+Arr[30]+"</td><td width=24%>"+Arr[31]+"</td></tr>" +
    	           "<tr bgcolor=DEEBF7 height=25 align=center><td>"+Arr[32]+"</td><td>"+Arr[33]+"</td><td>"+Arr[34]+"</td><td>"+Arr[35]+"</td></tr>" +
    	           "</table>"+
    	           "";
		return cstb_Str;
    	
    }

    /* Inner class to handle replies
    
       This implemets Session by implementing two methods: getSyncType and
       handleReply .In this cics getSyncType sets the Session to
       Asynchronous. This means that when the cics application sends the
       transaction it is free to continue its own thread, although all that is
       implemented here is a wait for reply which in effect simulates a
       synchronous session. On reply the method handleReply is run in its own
       thread which notifies the cics application thread a reply has been
       recieved. Upon notification the cics thread breaks out of the loop in
       waitForEvent (see below).
    
       In addition to the two methods this class also implements
       handleException which is called in the event of an exception. */
    private class ReplyHandler implements Session {
        private boolean reply = false;
        // Get sync type
        public int getSyncType() {
            return Session.async;
        }
        // Handle the reply
        public void handleReply(TerminalInterface terminal) {
            System.out.println("\nHandle reply called.");
            cicsTerminal = (Terminal) terminal;
            System.out.println("Current state of terminal = "
                               + CicsHelper.getStateString(cicsTerminal.getState()));
            /* Continue wating if the state is Terminal server as more data
               is to follow. */
            if (cicsTerminal.getState() != Terminal.server) {
                if (this.getSyncType()==Session.async) {
                    synchronized(this) {
                        reply = true;
                        this.notify();
                    }
                }
            }
        }
        // Handle any exceptions
        public void handleException(TerminalInterface terminal, Exception e) {
            System.out.println("Handle exception called");
            cicsTerminal = (Terminal)terminal;
            System.out.println("Current state of terminal = "
                               + CicsHelper.getStateString(cicsTerminal.getState()));
            if (cicsTerminal.getState() != Terminal.server) {
                if (this.getSyncType()==Session.async) {
                    synchronized(this) {
                        reply = true;
                        this.notify();
                    }
                }
            }
        }
        /* Method to wait for the reply from the server
           This causes the cics application thread to wait for the length of
           time determined by the SLEEP_PERIOD constant or until a notify
           has been received. The thread then checks to see if a reply has
           been received and if not it loops round until the WAIT_COUNT
           is exceeded. */
        public synchronized void waitForReply() throws InterruptedException
        {
            System.out.println("\nWaiting for a reply");
            int loopCounter = 0;

            try {
                if (!reply) {
                    boolean loop = true;
                    do {
                        if (!reply) {
                            this.wait(SLEEP_PERIOD);
                        }
                        if (!reply) {
                            loopCounter++;
                            if (loopCounter == WAIT_COUNT) {
                                throw new InterruptedException("Reply not"
                                                               + " received within wait period");
                            }
                            System.out.print(".");
                        } else {
                            loop = false;
                            reply=false;
                        }
                    }while (loop);
                }
                reply=false;
            } catch (InterruptedException e) {
                reply = false;
                throw e;
            }
        }
    }
}
