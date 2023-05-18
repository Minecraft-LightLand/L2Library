package dev.xkmc.l2library.init.events.screen.source;

import dev.xkmc.l2serial.serialization.SerialClass;

@SerialClass
public abstract class ItemSourceData<T extends ItemSourceData<T>> {

	public abstract boolean canReplace(T other);

}
