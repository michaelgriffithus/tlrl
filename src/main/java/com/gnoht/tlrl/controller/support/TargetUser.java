package com.gnoht.tlrl.controller.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gnoht.tlrl.domain.User;

/**
 * Annotation which indicates the method parameter resolves to a {@link User}
 * in our system, identified by the bound URI template variable.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetUser {
	
	/** The URI template variable to bind to. */
	String value() default "";
}
