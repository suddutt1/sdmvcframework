package com.ibm.app.web.frmwk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.app.web.frmwk.bean.ActionConfigurations;
import com.ibm.app.web.frmwk.bean.ModelAndView;
import com.ibm.app.web.frmwk.bean.ViewType;

/**
 * Servlet implementation class MobileFrameworDispatcher
 */
public class MobileFrameworkDispatcher extends HttpServlet {
	private static final long serialVersionUID = -2555722429871207913L;
	public static final String DEFAULT_ERROR_JSP = "/frmk/framework_error.jsp";
	private static final Logger LOGGER = Logger
			.getLogger(MobileFrameworkDispatcher.class.getName());
	private static final String _SECURE_MODEL_DATA_ACTION = "secureModelAction";
	
	private static final Gson _SERIALIZER = new GsonBuilder()
			.setPrettyPrinting().serializeNulls().create();

	private ActionConfigurations actionConfig;
	private String lastModelDataAction = "_get_last_model_data.wss";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MobileFrameworkDispatcher() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {    
		// TODO Change this later
		String lastModelAction = config
				.getInitParameter(_SECURE_MODEL_DATA_ACTION); 
		if (lastModelAction != null && lastModelAction.trim().length() > 0 
				&& lastModelAction.trim().endsWith(".wss"))

		{
			this.lastModelDataAction = lastModelAction;
		}
		String[] listOfClasses = loadActionClassNames(config
				.getInitParameter("applicationConfigLocation"));

		if (listOfClasses != null && listOfClasses.length > 0) {
			LOGGER.log(Level.INFO,
					"|MOBILE_FRMWK_DISPATCHER|List of classes read . Size is :"
							+ listOfClasses.length);
			this.actionConfig = MobileFrameworkHelper
					.loadActionConfigs(listOfClasses);
		} else {
			LOGGER.log(Level.WARNING,
					"|MOBILE_FRMWK_DISPATCHER|List of classes is empty");
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		performProcessing(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		performProcessing(request, response);
	}

	/**
	 * Performs the action processing
	 * 
	 * @param request
	 *            HttpServletRequest passed from doGet/doPost
	 * @param response
	 *            HttpServletResponse passed from doGet/doPost
	 * @throws ServletException
	 * @throws IOException
	 */
	private void performProcessing(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String finalJSPOutput = null;
		ModelAndView result = null;
		try {
			if (this.actionConfig != null) {
				String requestURI = request.getRequestURI();
				int position = requestURI.lastIndexOf("/");
				String actionName = requestURI.substring(position + 1);
				if (actionName.equals(this.lastModelDataAction)) {
					// This is like a ajax action
					result = new ModelAndView(ViewType.AJAX_VIEW);
					Object lastModel = request.getSession().getAttribute(
							_SECURE_MODEL_DATA_ACTION);
					//Remove after first use;
					request.getSession().removeAttribute(_SECURE_MODEL_DATA_ACTION);
					String lastModelString = (lastModel != null ? _SERIALIZER
							.toJson(lastModel) : "{}");
					result.setView(lastModelString);
					LOGGER.fine("|MOBILE_FRMWK_DISPATCHER|Returning the last model for GENERIC_NO_RENDER_VIEW "
							+ lastModelString);
				} else {
					// Invoke action
					Method method = this.actionConfig.getMethod(actionName);
					Class<? extends WebActionHandler> actionClass = this.actionConfig
							.getActionClass(actionName);

					WebActionHandler actionInstance = getObjectInstance(actionClass);
					result = (ModelAndView) method.invoke(actionInstance,
							new Object[] { request, response });
				}
				if (result != null) {
					response.setHeader("Expires", "0");
					response.setHeader("Pragma", "no-cache");
					switch (result.getViewType()) {
					case JSP_VIEW:
						placeModelInRequestScope(result.getModelMap(), request);
						finalJSPOutput = (String) result.getView();
						break;
					case AJAX_VIEW:
						// response.setContentType("");
						String ajaxOutput = (String) result.getView();
						PrintWriter printWriter = new PrintWriter(
								response.getOutputStream());
						printWriter.write(ajaxOutput);
						printWriter.flush();
						break;
					case FORWARD_ACTION_VIEW:
						placeModelInRequestScope(result.getModelMap(), request);
						String forwardActionPath = (String) result.getView();
						request.getRequestDispatcher(forwardActionPath)
								.forward(request, response);
						break;
					case NO_VIEW:
						// Do nothing. The action class has commited in the
						// input stream
						break;
					case REDIRECT_VIEW:
						placeModelInRequestScope(result.getModelMap(), request);
						String redirectPath = (String) result.getView();
						// request.getRequestDispatcher(redirectPath)
						response.sendRedirect(redirectPath);
					case GENERIC_NO_RENDER_VIEW:
						String viewPath = (String) result.getView();
						response.setContentType(result.getContentType());
						request.getSession()
								.setAttribute(_SECURE_MODEL_DATA_ACTION,
										result.getModelMap());
						request.getRequestDispatcher(viewPath).forward(request,
								response);
						break;
					default:
						setError("Invalid view type returned", request);
						finalJSPOutput = DEFAULT_ERROR_JSP;
					}
				} else {
					setError(
							"Invalid or null model view object returned by action method",
							request);
					finalJSPOutput = DEFAULT_ERROR_JSP;
				}
			} else {
				setError("No/Invalid action configuration specified", request);
				finalJSPOutput = DEFAULT_ERROR_JSP;

			}
			LOGGER.log(Level.INFO,
					"|MOBILE_FRMWK_DISPATCHER| final jsp output "
							+ finalJSPOutput);
		} catch (Exception ex) {
			LOGGER.log(
					Level.WARNING,
					"|MOBILE_FRMWK_DISPATCHER|Error in perform processing of the request:",
					ex);
			setError(ex, request);
			finalJSPOutput = DEFAULT_ERROR_JSP;
		} finally {
			if (finalJSPOutput != null) {
				request.getRequestDispatcher(finalJSPOutput).forward(request,
						response);
			}

		}

	}

	/**
	 * Place the model objects in request scope
	 * 
	 * @param modelMap
	 *            Map<String, Object>
	 * @param request
	 *            HttpServletRequest
	 */
	private void placeModelInRequestScope(Map<String, Object> modelMap,
			HttpServletRequest request) {
		if (modelMap != null) {
			for (String key : modelMap.keySet()) {
				request.setAttribute(key, modelMap.get(key));
			}
		}
	}

	/**
	 * Sets the global error so that framework error page view the same.
	 * 
	 * @param errorObj
	 *            Error object
	 * @param request
	 *            HttpServletRequest
	 */
	private void setError(Object errorObj, HttpServletRequest request) {
		if (errorObj != null) {
			if (errorObj instanceof Throwable) {
				request.setAttribute("_frmworkError",
						((Throwable) errorObj).getMessage());
				StringWriter strWriter = new StringWriter();
				((Throwable) errorObj).printStackTrace(new PrintWriter(
						strWriter));
				request.setAttribute("_frmworkErrorStackTrace",
						strWriter.toString());
			} else {
				request.setAttribute("_frmworkError", errorObj.toString());
			}
		}
	}

	/**
	 * Get action class instance
	 * 
	 * @param currClss
	 *            Class to instantiate
	 * @return instance of the input class
	 * @throws Exception
	 */
	private WebActionHandler getObjectInstance(
			Class<? extends WebActionHandler> currClss) throws Exception {
		// TODO: Should be utilizing the cache
		WebActionHandler handler = currClss.newInstance();
		return handler;
	}

	/**
	 * Reads the action class names
	 * 
	 * @param propertityFile
	 *            Path to property file
	 * @return String[] with list of class names
	 */
	private String[] loadActionClassNames(String propertityFile) {
		String[] listOfActionClasses = null;
		String line = null;
		try {
			InputStream stream = this.getClass().getClassLoader()
					.getResourceAsStream(propertityFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream));
			List<String> lineList = new ArrayList<String>(16);
			while ((line = reader.readLine()) != null) {
				lineList.add(line.trim());
			}
			reader.close();
			listOfActionClasses = lineList.toArray(new String[1]);

		} catch (Exception ex) {
			LOGGER.log(Level.WARNING,
					"|MOBILE_FRMWK_DISPATCHER|Error in loading action classes from "
							+ propertityFile, ex);
		}
		return listOfActionClasses;
	}

}
