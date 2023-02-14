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

		/**
		 * default false. If this is false, value will not be serialized to NBT that is sent to client
		 */
		boolean toClient() default false;

		/**
		 * default false. For Capability only. If this is false, value will not be serialized to NBT that is sent to other clients
		 */
		boolean toTracking() default false;

	}

	@Documented
	@Retention(RUNTIME)
	@Target(METHOD)
	@interface OnInject {

	}

}
