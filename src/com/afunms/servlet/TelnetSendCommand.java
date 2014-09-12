package com.afunms.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.telnet.TelnetClient;

import com.afunms.common.util.WebTelnetUtil;

public class TelnetSendCommand extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        int tcHashCode = 0;
        if (req.getParameter("tcHashCode") != null) {
            tcHashCode = new Integer(req.getParameter("tcHashCode")).intValue();
        }

        String sendCommand = req.getParameter("commandText");

        WebTelnetUtil util = WebTelnetUtil.getInstance();
        TelnetClient tc = util.sessionHashtable.get(tcHashCode);
        util.sender(tc, sendCommand);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PrintWriter out = res.getWriter();
        String serverString = util.serverOutputInfo.get(tc.hashCode()).toString();
        // h3c
        serverString = serverString.replace("  ---- More ----", "").replace("  ", "");
        // cisco
        serverString = serverString.replace(" --More-- ", "");
        out.println(serverString);
        out.close();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

}
