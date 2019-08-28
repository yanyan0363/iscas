package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import helper.HttpServerHelper;

public class GetStationsDis2DZ extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		System.out.println("GetStationsDis2DZ begin...");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html");
		resp.setStatus(HttpServletResponse.SC_OK);
//		System.out.println("time::"+req.getParameter("time"));
		resp.getOutputStream().println(HttpServerHelper.getStationsDis2DZ(req.getParameter("callback"), req.getParameter("stations"), req.getParameter("time")));
		resp.flushBuffer();
//		System.out.println("GetStationsDis2DZ end...");
	}
}
