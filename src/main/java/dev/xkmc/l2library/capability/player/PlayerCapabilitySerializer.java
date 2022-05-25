package dev.xkmc.l2library.capability.player;

import dev.xkmc.l2library.serial.ExceptionHandler;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.codec.TagCodec;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SerialClass
public class PlayerCapabilitySerializer<C extends PlayerCapabilityTemplate<C>> implements ICapabilitySerializable<CompoundTag>  {

	public final Player player;
	public final Level level;
	public final PlayerCapabilityHolder<C> holder;
	public C handler;
	public LazyOptional<C> lo = LazyOptional.of(() -> this.handler);

	public PlayerCapabilitySerializer(Player player, Level w, PlayerCapabilityHolder<C> holder) {
		this.player = player;
		this.level = w;
		this.holder = holder;
		if (w == null)
			LogManager.getLogger().error("level not present in entity");
		handler = holder.sup.get();
		handler.world = w;
		handler.player = player;
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
		ExceptionHandler.get(() -> TagCodec.fromTag(tag, holder.cls, handler, f -> true));
	}

}

