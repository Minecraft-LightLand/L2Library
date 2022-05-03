package dev.xkmc.l2library.block.one;

import dev.xkmc.l2library.block.type.SingletonBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface GetBlockItemBlockMethod extends SingletonBlockMethod {

	ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state);

}
