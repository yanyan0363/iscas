package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbHelper.GNSSDBHelper;

public class GetWarnPSDTServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GetWarnPSDTServlet begin...");
		resp.setContentType("text/html;charset=UTF-8");
		String eqID = req.getParameter("eqID");
		String mString = GNSSDBHelper.getWarnPSDTServlet(eqID);
		System.out.println(mString);
		System.out.println("GetWarnPSDTServlet end...");
		PrintWriter out = resp.getWriter();
		out.write(mString);
		out.close();
	}
}
