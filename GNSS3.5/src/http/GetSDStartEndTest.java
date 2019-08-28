package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import helper.HttpServerHelper;

public class GetSDStartEndTest extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GetSDStartEndTest begin...");
		System.out.println("authorization:"+req.getHeader("authorization"));
		System.out.println("Content-Type:"+req.getHeader("Content-Type"));
		if (req.getHeader("Content-Type").equalsIgnoreCase("application/json;charset=UTF-8")) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
			System.out.println(reader.readLine());
		}else {
			System.out.println("msgName:"+req.getParameter("msgName"));
			System.out.println("stationID:"+req.getParameter("stationID"));
			System.out.println("stationIDlist:"+req.getParameter("stationIDlist"));
			System.out.println("time:"+req.getParameter("time"));
			System.out.println("Lng:"+req.getParameter("Lng"));
			System.out.println("Lat:"+req.getParameter("Lat"));
			System.out.println("Z:"+req.getParameter("Z"));
			System.out.println("eventID:"+req.getParameter("eventID"));
			System.out.println("state:"+req.getParameter("state"));
		}
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getOutputStream().println("{\"status\":\"ok\"}");
		resp.flushBuffer();
		System.out.println("GetSDStartEndTest end  ...");
	}
}
