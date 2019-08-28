package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbHelper.GNSSDBHelper;
import helper.DispHelper;

public class GetHisDispWithMEMSByEQSt extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("servlet start -- GetHisDispWithMEMSByEQSt ...");
		resp.setContentType("text/html;charset=UTF-8");
//		String eqTime = req.getParameter("eqTime");
		String PT = req.getParameter("PT");
		String stationID = req.getParameter("stationID");
		System.out.println("stationID:"+stationID);
		System.out.println("PT:"+PT);
		Date startT = new Date();
		String mString = DispHelper.getDispByEQSt(PT, stationID);
		Date endT = new Date();
		System.out.println("GetHisDispWithMEMSByEQSt耗时："+(endT.getTime()-startT.getTime())+"ms");
		PrintWriter out = resp.getWriter();
		out.write(mString);
		out.close();
		System.out.println("servlet end -- GetHisDispWithMEMSByEQSt ...");
	}
}
