package dev.xkmc.l2library.block.mult;

import dev.xkmc.l2library.block.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface OnPlaceBlockMethod extends MultipleBlockMethod {

	void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moving);

}