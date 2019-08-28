package http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import helper.HttpServerHelper;

public class GetAccessTokenTest extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GetAccessTokenTest begin...");
//		System.out.println("grant_type:"+req.getParameter("grant_type"));
//		System.out.println("password:"+req.getParameter("password"));
//		System.out.println("client_secret:"+req.getParameter("client_secret"));
//		System.out.println("client_id:"+req.getParameter("client_id"));
//		System.out.println("username:"+req.getParameter("username"));
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getOutputStream().println("{\"access_token\":\"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJPMVc2Nm5hRExlUnRaNEt3RzdpVFZ1VDJOVy1YZnhmNVpRZmxZNXdZQ3RRIn0.eyJqdGkiOiJmYTc3MjczOS1hMzAwLTQxNDktOGM4ZS1iZjUwZjA4MGJkZDYiLCJleHAiOjE1MjcxMzA0MTEsIm5iZiI6MCwiaWF0IjoxNTI3MTMwMTExLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvbmhwdCIsImF1ZCI6Im5ocHQtc3lzdGVtIiwic3ViIjoiZDA5OTEyMTAtZWFlNy00YTEyLTliYjEtMjAzNTkwZjk4NDkwIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibmhwdC1zeXN0ZW0iLCJhdXRoX3RpbWUiOjAsInNlc3Npb25fc3RhdGUiOiJmOGJjYjNiYS1mNTU4LTQwOTctODMxMS00YTllZjllNmUwMmQiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwicHJlZmVycmVkX3VzZXJuYW1lIjoiam9obmRvZSIsImhlbGxvIjoiYmFyIn0.NTa59F_SWZ7KOQkpSCL293OwnZLCM2d2NR39b1KBLVRA7puK_2hAPMZRSF2DrUgRWF7HJ0eWtpFXtC5BASiQw9mxB66XB1OKEaZIItWmncorEm33DbxfIlFPV8EHeenJ0l6xLFVcWBzJGsUERYMg5vtjxCzIxCcM-thRgdKvelNw_My1tT689MNFGWzMRDlNVHZonvb7EQVANXfjHy64Vaiv2TinaClhsYn3Sls3oQyRoEpAjZ-h9C6moZzS8_bLdEwgbj-t77-MBPZ1dHd3VJIekfNGZhXfqNl7KKCiK4evjgLyF0hLNIKoYGEM4qhjhm3o8o16EFYX9AY7IUSLvQ\"}");
		resp.flushBuffer();
		System.out.println("GetAccessTokenTest end  ...");
	}
}
