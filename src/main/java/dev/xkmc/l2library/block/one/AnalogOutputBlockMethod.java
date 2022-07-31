package dev.xkmc.l2library.block.one;

import dev.xkmc.l2library.block.type.SingletonBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public interface AnalogOutputBlockMethod extends SingletonBlockMethod {


	  int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos);

	default boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}



}
