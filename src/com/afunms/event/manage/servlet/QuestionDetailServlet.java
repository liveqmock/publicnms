package com.afunms.event.manage.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.afunms.event.service.QuestionService;

public class QuestionDetailServlet extends HttpServlet {
	QuestionService service = new QuestionService();
	public QuestionDetailServlet() {
		super();
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		List<String> list = new ArrayList<String>();
		String question = request.getParameter("question");
		if (null != question && "全部问题".equals(question)) {
			list = service.loadQuestionDetailAll();
		}else{
			list = service.loadQuestionDetail(question);
		}
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < list.size(); i++) {
			s.append("\"").append(list.get(i)).append("\"");
			if(i<list.size()-1)s.append(",");
		}
		s.append("]");
		response.setContentType("text/html; charset=utf-8");
		response.getWriter().println(s);
		//System.out.println(s);		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	public void init() throws ServletException {
	}

}
