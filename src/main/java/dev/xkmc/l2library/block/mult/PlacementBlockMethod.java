package dev.xkmc.l2library.block.mult;

import dev.xkmc.l2library.block.type.MultipleBlockMethod;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public interface PlacementBlockMethod extends MultipleBlockMethod {

	BlockState getStateForPlacement(BlockState def, BlockPlaceContext context);

}