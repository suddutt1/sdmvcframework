package com.ibm.app.web.frmwk;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.app.web.frmwk.annotations.RequestMapping;
import com.ibm.app.web.frmwk.bean.ActionConfigurations;
import com.ibm.app.web.frmwk.bean.ModelAndView;
import com.ibm.app.web.frmwk.bean.ViewType;

/**
 * Framework helper class to provide various utility method to the dispatcher
 * class
 * 
 * @author SUDDUTT1
 * 
 */
public class MobileFrameworkHelper {

	private final static Logger LOGGER = Logger
			.getLogger(MobileFrameworkHelper.class.getName());
	

	/**
	 * Private constructor to avoid external initialization
	 */
	private MobileFrameworkHelper() {
		super();
	}

	/**
	 * Loads the mapping of action to method for input class names.
	 * 
	 * @param classNames
	 *            String[] list of action class names
	 * @return ActionConfigurations
	 */
	public static ActionConfigurations loadActionConfigs(String[] classNames) {
		String mvClassType = ModelAndView.class.getName();
		ActionConfigurations actionConfig = new ActionConfigurations();
		if (isSafe(classNames)) {
			for (String className : classNames) {
				try {
					Class<? extends WebActionHandler> actionHanlder = Class
							.forName(className).asSubclass(
									WebActionHandler.class);
					// Now we will try to load method annotations
					Method[] methods = actionHanlder.getDeclaredMethods();
					if (isSafe(methods)) {
						for (Method method : methods) {
							// Will consider only public modifiers
							// And method that returns a ModelAndView object
							if (Modifier.isPublic(method.getModifiers())
									&& method.getReturnType().getName()
											.equals(mvClassType)) {
								Annotation methdAnnotation = method
										.getAnnotation(RequestMapping.class);
								if (methdAnnotation != null) {
									String actionName = ((RequestMapping) methdAnnotation)
											.value();
									if (actionName != null
											&& actionName.trim().length() > 0) {

										boolean isAddSuccessfull = actionConfig
												.addAction(actionName,
														actionHanlder, method);
										if (!isAddSuccessfull) {
											LOGGER.log(Level.WARNING,
													"CONFIG_LOG|Duplication action definition in the  class <"
															+ className + "> "
															+ actionName
															+ " method "
															+ method.getName()
															+ " entry ignored.");
										}

									}
								}
							}
						}
					}
				} catch (ClassNotFoundException cnfEx) {
					LOGGER.log(Level.WARNING, "CONFIG_LOG|Action class <"
							+ className + "> is not found", cnfEx);
				} catch (ClassCastException ccEx) {
					LOGGER.log(Level.WARNING, "CONFIG_LOG|Action class <"
							+ className + "> is not a WebActionHandler", ccEx);
				}
			}
		}
		return actionConfig;
	}

	/**
	 * Builds a common model and view object where application can not handle the error
	 * @param errorObj Object ( Could be a throwable or String message)
	 * @param viewType ViewType 
	 * @return ModelAndView
	 */
	public static ModelAndView buildErrrModelAndView(Object errorObj,
			ViewType viewType) {
		ModelAndView mvObject = new ModelAndView(viewType);
		// TODO:Need to handle other than JSP view types also
		mvObject.setView(MobileFrameworkDispatcher.DEFAULT_ERROR_JSP);
		if (errorObj != null) {
			if (errorObj instanceof Throwable) {
				mvObject.addModel("_frmworkError",
						((Throwable) errorObj).getMessage());
				StringWriter strWriter = new StringWriter();
				((Throwable) errorObj).printStackTrace(new PrintWriter(
						strWriter));
				mvObject.addModel("_frmworkErrorStackTrace",
						strWriter.toString());
			} else {
				mvObject.addModel("_frmworkError", errorObj.toString());
			}

		} else {
			mvObject.addModel("_frmworkError",
					"Error !!! No error object specified");
		}

		return mvObject;
	}
	
	/**
	 * Checks if the input array is a 'safe' ( not null and non zero length) or
	 * not.
	 * 
	 * @param arry
	 *            Object[] array to test
	 * @return boolean returns true if 'safe' false otherwise.
	 */
	private static final boolean isSafe(Object[] arry) {
		return ((arry != null && arry.length > 0) ? true : false);
	}

}
