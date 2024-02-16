package dev.xkmc.l2library.base.entity;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import javax.annotation.ParametersAreNonnullByDefault;

@SerialClass
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BaseEntity extends Entity implements IEntityWithComplexSpawn {

	public BaseEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.put("auto-serial", TagCodec.toTag(new CompoundTag(), this));
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		if (!tag.contains("auto-serial"))
			return;
		Wrappers.run(() -> TagCodec.fromTag(tag.getCompound("auto-serial"), this.getClass(), this, f -> true));
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		PacketCodec.to(buffer, this);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void readSpawnData(FriendlyByteBuf data) {
		PacketCodec.from(data, (Class) this.getClass(), this);
	}

}
