package dev.xkmc.l2library.serial;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface SerialClass {

	@Documented
	@Retention(RUNTIME)
	@Target(FIELD)
	@interface SerialField {

		boolean toClient() default false;

	}

	@Documented
	@Retention(RUNTIME)
	@Target(METHOD)
	@interface OnInject {

	}

}
