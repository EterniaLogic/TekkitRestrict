package nl.taico.tekkitrestrict.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates this method is called Asynchronously
 * @author Taico
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Async {
	/**
	 * Indicates if this method HAS to be called Asynchronously.
	 */
	boolean mandatory() default false;
}
