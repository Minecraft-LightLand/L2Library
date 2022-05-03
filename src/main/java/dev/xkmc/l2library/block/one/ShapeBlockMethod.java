package dev.xkmc.l2library.block.one;

import dev.xkmc.l2library.block.type.SingletonBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public interface ShapeBlockMethod extends SingletonBlockMethod {

	@Nullable
	default VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return null;
	}

	@Nullable
	default VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
		return null;
	}

	@Nullable
	default VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return null;
	}

	@Nullable
	default VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return null;
	}

}
