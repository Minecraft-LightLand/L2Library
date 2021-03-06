package dev.xkmc.l2library.serial.wrapper;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.config.ConfigCollect;
import dev.xkmc.l2library.util.code.LazyExc;

import java.lang.reflect.Field;

public class FieldCache {

	private final Field field;
	private final String name;
	private final LazyExc<SerialClass.SerialField> serial;
	private final LazyExc<ConfigCollect> config;

	FieldCache(Field field) {
		this.field = field;
		this.name = field.getName();
		this.serial = new LazyExc<>(() -> field.getAnnotation(SerialClass.SerialField.class));
		this.config = new LazyExc<>(() -> field.getAnnotation(ConfigCollect.class));
		this.field.setAccessible(true);
	}

	public SerialClass.SerialField getSerialAnnotation() throws Exception {
		return serial.get();
	}

	public ConfigCollect getConfigAnnotation() throws Exception {
		return config.get();
	}

	public String getName() {
		return name;
	}

	public Object get(Object ans) throws Exception {
		return field.get(ans);
	}

	public void set(Object target, Object value) throws Exception {
		field.set(target, value);
	}

	public TypeInfo toType() {
		return TypeInfo.of(field);
	}
}
