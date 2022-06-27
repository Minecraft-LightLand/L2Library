package dev.xkmc.l2library.serial.unified;

import dev.xkmc.l2library.serial.wrapper.TypeInfo;

public abstract class SingletonContext<E> extends UnifiedContext<E, E, E> {

	public final E instance;

	protected SingletonContext(E instance) {
		this.instance = instance;
	}

	@Override
	public E retrieve(E obj, String field) {
		return obj;
	}

	@Override
	public E castAsList(E e) {
		return e;
	}

	@Override
	public E castAsMap(E e) {
		return e;
	}

	@Override
	public E getElement(E arr, int i) {
		return arr;
	}

	@Override
	public boolean isListFormat(E e) {
		return true;
	}

	@Override
	public void addField(E obj, String str, E e) {

	}

	@Override
	public boolean canBeString(E e) {
		return false;
	}

	@Override
	public void addListItem(E arr, E e) {

	}

	@Override
	public E getKeyOfEntry(E obj) {
		return obj;
	}

	@Override
	public E getValueOfEntry(E obj) {
		return obj;
	}

	@Override
	public void setKeyOfEntry(E obj, E e) {
	}

	@Override
	public void setValueOfEntry(E obj, E e) {
	}

	@Override
	public E createMap() {
		return instance;
	}

	@Override
	public Object deserializeEfficientMap(E e, TypeInfo ckey, TypeInfo cval, Object ans) throws Exception {
		throw new IllegalStateException("efficient map not implemented");
	}


}
