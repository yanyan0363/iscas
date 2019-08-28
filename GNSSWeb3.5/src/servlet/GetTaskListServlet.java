package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbHelper.GNSSDBHelper;

public class GetTaskListServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		System.out.println("GetTaskListServlet begin...");
		resp.setContentType("text/html;charset=UTF-8");
//		System.out.println(req.getParameter("limit"));
//		System.out.println(req.getParameter("offset"));
		String eqListJson = GNSSDBHelper.getTaskList(Integer.parseInt(req.getParameter("offset")),Integer.parseInt(req.getParameter("limit")),req.getParameter("order"));
		PrintWriter writer = resp.getWriter();
		writer.write(eqListJson);
		writer.close();
//		System.out.println("GetTaskListServlet end...");
		
	}
}
