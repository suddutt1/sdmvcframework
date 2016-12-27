package com.ibm.app.web.frmwk.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Model and View class to represent view type and models added by the action
 * class. This class is container to transfer Model and view information from
 * action class to view.
 * 
 * @author SUDDUTT1
 * 
 */
public class ModelAndView {

	private ViewType viewType;
	private Map<String, Object> modelMap;
	private Object view;
	private String contentType;

	/**
	 * Constructor with ViewType
	 * @param vw ViewType
	 */
	public ModelAndView(ViewType vw) {
		this.viewType = vw;
		this.modelMap = new HashMap<String, Object>(16);
	}

	/**
	 * Constructor with ViewType and contentType
	 * @param vw ViewType
	 */
	public ModelAndView(ViewType vw,String contentType) {
		this.viewType = vw;
		this.modelMap = new HashMap<String, Object>(16);
		this.contentType=contentType;
	}
	/**
	 * @return viewType
	 */
	public ViewType getViewType() {
		return this.viewType;
	}

	/**
	 * Add a model element 
	 * @param key String model key
	 * @param value Object
	 */
	public void addModel(String key, Object value) {
		this.modelMap.put(key, value);
	}

	/**
	 * @return Returns all the models set
	 */
	public Map<String, Object> getModelMap() {
		return this.modelMap;
	}

	/**
	 * @return view 
	 */
	public Object getView() {
		return this.view;
	}

	/**
	 * Sets view
	 * @param view
	 */
	public void setView(Object view) {
		this.view = view;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
