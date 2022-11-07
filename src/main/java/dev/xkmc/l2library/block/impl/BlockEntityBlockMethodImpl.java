package dev.xkmc.l2library.block.impl;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.xkmc.l2library.block.NameSetable;
import dev.xkmc.l2library.block.mult.OnClickBlockMethod;
import dev.xkmc.l2library.block.mult.SetPlacedByBlockMethod;
import dev.xkmc.l2library.block.one.AnalogOutputBlockMethod;
import dev.xkmc.l2library.block.one.BlockEntityBlockMethod;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * To make it tickable, implements TickableBlockEntity <br>
 * To make it show menu, implements MenuProvider <br>
 * To make it menu show name, implements NameSetable <br>
 * To make it drop content when break, implements Container or BlockContainer <br>
 * To make it output redstone signal, implements Container <br>
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockEntityBlockMethodImpl<T extends BlockEntity> implements BlockEntityBlockMethod<T>, OnClickBlockMethod,
		SetPlacedByBlockMethod, AnalogOutputBlockMethod {

	private final BlockEntityEntry<T> type;
	private final Class<T> cls;

	public BlockEntityBlockMethodImpl(BlockEntityEntry<T> type, Class<T> cls) {
		this.type = type;
		this.cls = cls;
	}

	@Override
	public BlockEntity createTileEntity(BlockPos pos, BlockState state) {
		return type.create(pos, state);
	}

	@Override
	public BlockEntityType<T> getType() {
		return type.get();
	}

	@Override
	public Class<T> getEntityClass() {
		return cls;
	}

	@Override
	public InteractionResult onClick(BlockState state, Level level, BlockPos pos, Player pl, InteractionHand hand, BlockHitResult result) {
		BlockEntity te = level.getBlockEntity(pos);
		if (level.isClientSide())
			return te instanceof MenuProvider ? InteractionResult.SUCCESS : InteractionResult.PASS;
		if (te instanceof MenuProvider) {
			pl.openMenu((MenuProvider) te);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			BlockEntity blockentity = level.getBlockEntity(pos);
			if (blockentity instanceof NameSetable be) {
				be.setCustomName(stack.getHoverName());
			}
		}
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		BlockEntity e = worldIn.getBlockEntity(pos);
		if (e != null) {
			if (e instanceof Container) {
				return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(e);
			}
			var lazyCap = e.getCapability(ForgeCapabilities.ITEM_HANDLER);
			if (lazyCap.resolve().isPresent()) {
				var cap = lazyCap.resolve().get();
				return ItemHandlerHelper.calcRedstoneFromInventory(cap);
			}
		}
		return 0;
	}

}
