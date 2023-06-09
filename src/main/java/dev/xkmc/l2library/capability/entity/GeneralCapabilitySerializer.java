package dev.xkmc.l2library.capability.entity;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SerialClass
public class GeneralCapabilitySerializer<E extends ICapabilityProvider, C extends GeneralCapabilityTemplate<E, C>> implements ICapabilitySerializable<CompoundTag> {

	public final GeneralCapabilityHolder<E, C> holder;
	public C handler;
	public LazyOptional<C> lo = LazyOptional.of(() -> this.handler);

	public GeneralCapabilitySerializer(GeneralCapabilityHolder<E, C> holder) {
		this.holder = holder;
		handler = holder.sup.get();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction direction) {
		if (capability == holder.capability)
			return lo.cast();
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		return TagCodec.toTag(new CompoundTag(), lo.resolve().get());
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		Wrappers.get(() -> TagCodec.fromTag(tag, holder.cls, handler, f -> true));
	}

}

