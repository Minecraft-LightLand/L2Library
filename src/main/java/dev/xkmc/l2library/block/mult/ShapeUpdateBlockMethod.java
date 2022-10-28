package dev.xkmc.l2library.block.mult;

import dev.xkmc.l2library.block.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ShapeUpdateBlockMethod extends MultipleBlockMethod {

	BlockState updateShape(Block self, BlockState selfCurrent, BlockState selfOld, Direction from, BlockState sourceState, LevelAccessor level, BlockPos selfPos, BlockPos sourcePos);

}
