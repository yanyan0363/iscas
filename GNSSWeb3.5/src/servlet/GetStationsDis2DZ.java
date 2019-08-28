package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbHelper.GNSSDBHelper;

public class GetStationsDis2DZ extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GetStationsDis2DZ begin...");
		resp.setContentType("text/html;charset=UTF-8");
		String stations = req.getParameter("stations");
		String time = req.getParameter("time");
//		System.out.println(stations);
		PrintWriter out = resp.getWriter();
		out.write(GNSSDBHelper.getStationsDis2DZ(stations, time));
		out.close();
		System.out.println("GetStationsDis2DZ end...");
	}
}
