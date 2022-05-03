package dev.xkmc.l2library.block.mult;

import dev.xkmc.l2library.block.type.MultipleBlockMethod;
import net.minecraft.world.level.block.state.BlockState;

public interface DefaultStateBlockMethod extends MultipleBlockMethod {
	BlockState getDefaultState(BlockState state);
}
