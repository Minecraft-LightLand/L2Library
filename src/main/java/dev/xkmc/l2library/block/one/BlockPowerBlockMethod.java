package dev.xkmc.l2library.block.one;

import dev.xkmc.l2library.block.type.SingletonBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockPowerBlockMethod extends SingletonBlockMethod {

	int getSignal(BlockState bs, BlockGetter r, BlockPos pos, Direction d);
}