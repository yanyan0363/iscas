package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbHelper.GNSSDBHelper;
import javafx.scene.control.Alert;

public class GetEQStPTMEpiDisServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GetEQStPTMEpiDisServlet begin...");
		resp.setContentType("text/html;charset=UTF-8");
		String reportsJson = GNSSDBHelper.getEQStPTMEpiDis(req.getParameter("eqID"));
		System.out.println(reportsJson);
		PrintWriter writer = resp.getWriter();
		writer.write(reportsJson);
		writer.close();
		System.out.println("GetEQStPTMEpiDisServlet end...");
		
	}
}
