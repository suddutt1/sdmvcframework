package com.ibm.app.web.frmwk;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet Filter implementation class Loginfilter
 */
public class Loginfilter implements Filter {

	private static final String _ENABLE_LOGIN_PROP = "enableLogin";
	private static final String _LOGIN_ACTION_PATH_PROP = "loginAction";
	private static final String _LOGIN_PAGE_PROP = "loginPage";
	private static final String _AUTH_TOKEN_ATTR_PROP = "authTokenAttribute";
	private static final String _BYPASS_LOGIN_ACTION = "bypassLoginActions";
	

	private boolean enableLogin = true;
	private String loginPath = "login.wss";
	private String loginPage = "login.jsp";
	private String authToken = "user_role";
	private Map<String,String> byPassLoginActionMap = new HashMap<String, String>();
	

	/**
	 * Default constructor.
	 */
	public Loginfilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		if (!this.enableLogin) {
			chain.doFilter(request, response);
		} else {
			boolean redirectToLoginPage = true;
			if (request instanceof HttpServletRequest) {
				HttpServletRequest servletReq = (HttpServletRequest) request;
				String userRole = (String) servletReq.getSession()
						.getAttribute(this.authToken);
				if (userRole != null) {
					chain.doFilter(request, response);
					redirectToLoginPage = false;
				} else {
					// If login is called
					String requestURI = servletReq.getRequestURI();
					int position = requestURI.lastIndexOf("/");
					String actionName = requestURI.substring(position + 1);
					if (actionName.equalsIgnoreCase(this.loginPath)
							|| this.byPassLoginActionMap.containsKey(actionName)) {
						chain.doFilter(request, response);
						redirectToLoginPage = false;
					}
				}
			}
			// pass the request along the filter chain
			if (redirectToLoginPage) {
				request.getRequestDispatcher(this.loginPage).forward(request,
						response);
			}
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {

		String byLoginActionList = fConfig
				.getInitParameter(_BYPASS_LOGIN_ACTION);
		if (byLoginActionList != null && byLoginActionList.trim().length() > 0) {
			String[] actionList = byLoginActionList.trim().split(",");
			if(actionList!=null)
			{
				for(String action: actionList)
				{
					this.byPassLoginActionMap.put(action,action);
				}
			}
		}
		String enableProp = fConfig.getInitParameter(_ENABLE_LOGIN_PROP);
		if (enableProp != null && enableProp.trim().equalsIgnoreCase("FALSE")) {
			this.enableLogin = false;
		}
		String loginUri = fConfig.getInitParameter(_LOGIN_ACTION_PATH_PROP);
		if (loginUri != null && loginUri.trim().length() > 0) {
			this.loginPath = loginUri.trim();
		}
		String loginPageName = fConfig.getInitParameter(_LOGIN_PAGE_PROP);
		if (loginPageName != null && loginPageName.trim().length() > 0) {
			this.loginPage = loginPageName.trim();
		}
		String autheTokenValue = fConfig
				.getInitParameter(_AUTH_TOKEN_ATTR_PROP);
		if (autheTokenValue != null && autheTokenValue.trim().length() > 0) {
			this.authToken = autheTokenValue.trim();
		}

	}

}
