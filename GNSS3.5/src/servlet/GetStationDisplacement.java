package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import helper.HttpServerHelper;

public class GetStationDisplacement extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		System.out.println("GetStationDisplacement begin...");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getOutputStream().println(HttpServerHelper.handleStationDisplacement(req.getParameter("callback"), req.getParameter("station")));
		resp.flushBuffer();
//		System.out.println("GetStationDisplacement end...");
	}
}
