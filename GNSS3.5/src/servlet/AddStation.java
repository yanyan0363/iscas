package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import helper.HttpServerHelper;

public class AddStation extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("AddStation begin...");
		System.out.println("stationID:"+req.getParameter("stationID"));
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getOutputStream().println(HttpServerHelper.addStation(req.getParameter("callback"), 
				req.getParameter("stationID"), req.getParameter("stLoc"), Double.parseDouble(req.getParameter("B")), Double.parseDouble(req.getParameter("L"))));
		resp.flushBuffer();
		System.out.println("AddStation end...");
	}
}
