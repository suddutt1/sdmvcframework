/**
 * 
 */
package com.ibm.app.web.frmwk.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Request mapping annotation definition.To be added to action class method to
 * map web action to the annotated method.
 * 
 * @author SUDDUTT1
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestMapping {

	String value();
}
