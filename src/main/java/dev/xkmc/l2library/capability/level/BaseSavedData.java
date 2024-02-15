package dev.xkmc.l2library.capability.level;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

@SerialClass
public class BaseSavedData extends SavedData {

	@Override
	public CompoundTag save(CompoundTag tag) {
		TagCodec.toTag(tag, this);
		return tag;
	}

	@Override
	public boolean isDirty() {
		return true;
	}



}
