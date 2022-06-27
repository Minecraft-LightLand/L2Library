package dev.xkmc.l2library.serial.wrapper;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.util.code.LazyExc;

import java.lang.reflect.Method;

public class MethodCache {

	private final Method method;
	private final LazyExc<SerialClass.OnInject> serial;

	MethodCache(Method method) {
		this.method = method;
		serial = new LazyExc<>(() -> method.getAnnotation(SerialClass.OnInject.class));
	}

	public SerialClass.OnInject getInjectAnnotation() throws Exception {
		return serial.get();
	}

	public void invoke(Object ans) throws Exception {
		method.invoke(ans);
	}
}
