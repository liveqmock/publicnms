package com.afunms.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.telnet.TelnetClient;

import com.afunms.common.util.WebTelnetUtil;

public class TelnetEndSession extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        int removeTCHashCode = 0;
        if (req.getParameter("tcHashCode") != null) {
            removeTCHashCode = new Integer(req.getParameter("tcHashCode")).intValue();
        }

        WebTelnetUtil util = WebTelnetUtil.getInstance();
        TelnetClient tc = util.sessionHashtable.get(removeTCHashCode);
        if (tc != null) {
            tc.disconnect();
            util.sessionHashtable.remove(removeTCHashCode);
        }
        util.sessionHashtable.remove(removeTCHashCode);
        PrintWriter out = res.getWriter();
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
