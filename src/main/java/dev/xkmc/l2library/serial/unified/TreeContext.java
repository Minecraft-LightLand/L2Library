package dev.xkmc.l2library.serial.unified;

public interface TreeContext<E, O extends E, A extends E> extends UnifiedContext<E, O, A> {

	@Override
	default E getKeyOfEntry(O obj) {
		return retrieve(obj, "_key");
	}

	@Override
	default E getValueOfEntry(O obj) {
		return retrieve(obj, "_val");
	}

	@Override
	default void setKeyOfEntry(O obj, E e) {
		addField(obj, "_key", e);
	}

	@Override
	default void setValueOfEntry(O obj, E e) {
		addField(obj, "_val", e);
	}

}
