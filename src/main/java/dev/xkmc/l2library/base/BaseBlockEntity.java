package dev.xkmc.l2library.base;

import dev.xkmc.l2library.serial.ExceptionHandler;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.codec.TagCodec;
import dev.xkmc.l2library.util.ServerOnly;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SerialClass
public class BaseBlockEntity extends BlockEntity {

	public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("auto-serial"))
			ExceptionHandler.run(() -> TagCodec.fromTag(tag.getCompound("auto-serial"), getClass(), this, f -> true));
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		CompoundTag ser = ExceptionHandler.get(() -> TagCodec.toTag(new CompoundTag(), getClass(), this, f -> true));
		if (ser != null) tag.put("auto-serial", ser);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		ExceptionHandler.run(() -> TagCodec.fromTag(pkt.getTag(), getClass(), this, SerialClass.SerialField::toClient));
		super.onDataPacket(net, pkt);
	}

	@ServerOnly
	public void sync() {
		if (level != null) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
		}
	}

	/**
	 * Generate data packet from server to client, called from getUpdatePacket()
	 */
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag ans = super.getUpdateTag();
		CompoundTag ser = ExceptionHandler.get(() -> TagCodec.toTag(new CompoundTag(), getClass(), this, SerialClass.SerialField::toClient));
		if (ser != null) ans.put("auto-serial", ser);
		return ans;
	}

}
