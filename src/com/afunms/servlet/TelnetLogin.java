package com.afunms.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.net.telnet.TelnetClient;

import com.afunms.common.util.WebTelnetUtil;

public class TelnetLogin extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        int removeTCHashCode = 0;
        if (req.getParameter("tcHashCode") != null) {
            removeTCHashCode = new Integer(req.getParameter("tcHashCode")).intValue();
        }

        String server = req.getParameter("server");
        String port = req.getParameter("port");
        String terminalType = req.getParameter("terminalType");

        WebTelnetUtil util = WebTelnetUtil.getInstance();
        TelnetClient tc;

        // 当点击login的时候,删除上一次telnet回话
        tc = util.sessionHashtable.get(removeTCHashCode);
        if (tc != null) {
            tc.disconnect();
            util.sessionHashtable.remove(removeTCHashCode);
        }

        // 重新建立回话
        int tcHashCode = util.openSession(server, new Integer(port).intValue(), terminalType);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tc = util.sessionHashtable.get(tcHashCode);

        PrintWriter out = res.getWriter();
        JSONObject json = new JSONObject();
        json.put("tcHashCode", tc.hashCode() + "");
        json.put("serverOutputInfo", util.serverOutputInfo.get(tc.hashCode()).toString());
        out.println(json.toString());
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
