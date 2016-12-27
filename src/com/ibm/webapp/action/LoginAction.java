package com.ibm.webapp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.app.web.frmwk.WebActionHandler;
import com.ibm.app.web.frmwk.annotations.RequestMapping;
import com.ibm.app.web.frmwk.bean.ModelAndView;
import com.ibm.app.web.frmwk.bean.ViewType;

public class LoginAction implements WebActionHandler {

	@RequestMapping("login.wss")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mvObj = new ModelAndView(ViewType.GENERIC_NO_RENDER_VIEW,"text/html");
		
		String userId = request.getParameter("uid");
		String password =request.getParameter("password");
		if(password.equals("password"))
		{
			request.getSession().setAttribute("auth_token", System.currentTimeMillis()+"");
			mvObj.setView("app/home.html");
		}
		else
		{
			mvObj.setView("login.html");
		}
		
		return mvObj;
	}
	@RequestMapping("home.wss")
	public ModelAndView home(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mvObj = new ModelAndView(ViewType.GENERIC_NO_RENDER_VIEW,"text/html");
		mvObj.setView("app/home.html");
		
		return mvObj;
	}
}
