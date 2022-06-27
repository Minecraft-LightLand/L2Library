package dev.xkmc.l2library.serial.unified;

public abstract class TreeContext<E, O extends E, A extends E> extends UnifiedContext<E, O, A> {

	@Override
	public E getKeyOfEntry(O obj) {
		return retrieve(obj, "_key");
	}

	@Override
	public E getValueOfEntry(O obj) {
		return retrieve(obj, "_val");
	}

	@Override
	public void setKeyOfEntry(O obj, E e) {
		addField(obj, "_key", e);
	}

	@Override
	public void setValueOfEntry(O obj, E e) {
		addField(obj, "_val", e);
	}

}
