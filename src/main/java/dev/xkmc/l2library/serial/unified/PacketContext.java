package dev.xkmc.l2library.serial.unified;

import dev.xkmc.l2library.serial.wrapper.ClassCache;
import dev.xkmc.l2library.serial.wrapper.FieldCache;
import dev.xkmc.l2library.serial.handler.Handlers;
import net.minecraft.network.FriendlyByteBuf;

public class PacketContext extends SingletonContext<FriendlyByteBuf> {

	protected PacketContext(FriendlyByteBuf instance) {
		super(instance);
	}

	@Override
	public boolean hasSpecialHandling(Class<?> cls) {
		return Handlers.PACKET_MAP.containsKey(cls);
	}

	@Override
	public Object deserializeSpecial(Class<?> cls, FriendlyByteBuf self) {
		return Handlers.PACKET_MAP.get(cls).fromPacket(instance);
	}

	@Override
	public FriendlyByteBuf serializeSpecial(Class<?> cls, Object obj) {
		Handlers.PACKET_MAP.get(cls).toPacket(instance, obj);
		return instance;
	}

	@Override
	public ClassCache fetchRealClass(FriendlyByteBuf obj, ClassCache def) {
		return null;//TODO
	}

	@Override
	public void writeRealClass(FriendlyByteBuf obj, ClassCache cls) {
		//TODO
	}

	@Override
	public boolean shouldRead(FriendlyByteBuf obj, FieldCache field) {
		return true;
	}

	@Override
	public int getSize(FriendlyByteBuf self) {
		return instance.readInt();
	}

	@Override
	public String getAsString(FriendlyByteBuf self) {
		return instance.readUtf();
	}

	@Override
	public FriendlyByteBuf createList(int size) {
		instance.writeInt(size);
		return instance;
	}

	@Override
	public FriendlyByteBuf fromString(String str) {
		instance.writeUtf(str);
		return instance;
	}
}
