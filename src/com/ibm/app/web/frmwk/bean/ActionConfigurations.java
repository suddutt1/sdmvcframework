package com.ibm.app.web.frmwk.bean;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.ibm.app.web.frmwk.WebActionHandler;

/**
 * Class to store action to method and action to class mapping.
 * 
 * @author SUDDUTT1
 * 
 */
public class ActionConfigurations {

	private Map<String, Method> actionToMethodMap;
	private Map<String, Class<? extends WebActionHandler>> actionToHandlerMap;

	/**
	 * Default constructor
	 */
	public ActionConfigurations() {
		this.actionToHandlerMap = new HashMap<String, Class<? extends WebActionHandler>>(
				16);
		this.actionToMethodMap = new HashMap<String, Method>(16);
	}

	/**
	 * Add a new action into the existing list of action and method/class
	 * mapping structure
	 * 
	 * @param actionName
	 *            String
	 * @param actionClass
	 *            WebActionHandler name
	 * @param method
	 *            Method
	 * @return true if same action name is not mapped earlier , false other
	 *         wise. In case of false return value input is ignored.
	 */
	public boolean addAction(String actionName,
			Class<? extends WebActionHandler> actionClass, Method method) {
		// Discard the same actionName for the 2nd time
		if (!this.actionToMethodMap.containsKey(actionName)) {
			this.actionToMethodMap.put(actionName, method);
			this.actionToHandlerMap.put(actionName, actionClass);
			return true;
		}
		return false;
	}

	/**
	 * Returns WebActionHandler class stored in this configuration data
	 * structure
	 * 
	 * @param actionName
	 *            String
	 * @return Class
	 */
	public Class<? extends WebActionHandler> getActionClass(String actionName) {
		return this.actionToHandlerMap.get(actionName);
	}

	/**
	 * Returns method name for a given action name
	 * 
	 * @param actionName
	 *            String
	 * @return Method
	 */
	public Method getMethod(String actionName) {
		return this.actionToMethodMap.get(actionName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ActionConfigurations [actionToMethodMap=");
		builder.append(actionToMethodMap);
		builder.append(", actionToHandlerMap=");
		builder.append(actionToHandlerMap);
		builder.append("]");
		return builder.toString();
	}

}
