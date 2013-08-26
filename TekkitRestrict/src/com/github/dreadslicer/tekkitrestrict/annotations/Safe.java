package com.github.dreadslicer.tekkitrestrict.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates that this method is safe to use and should not throw any exceptions.<br><br>
 * 
 * Allownull: If false, indicates that this method <b>can</b> throw exceptions if any or multiple of the parameters is null.
 */
@Target(ElementType.METHOD)
public @interface Safe {
	/**
	 * If false, indicates that this method <b>can</b> throw exceptions if any or multiple of the parameters is null.
	 */
	boolean allownull() default true;
}
