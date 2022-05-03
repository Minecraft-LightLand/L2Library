package dev.xkmc.l2library.serial.codec;

import java.util.List;

public interface AliasCollection<T> {

	List<T> getAsList();

	void clear();

	void set(int n, int i, T elem);

	Class<T> getElemClass();

	@SuppressWarnings({"unchecked", "unsafe"})
	default void setRaw(int n, int i, Object elem) {
		set(n, i, (T) elem);
	}

}
