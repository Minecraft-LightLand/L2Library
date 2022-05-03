package dev.xkmc.l2library.block.one;

import dev.xkmc.l2library.block.type.SingletonBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface EntityInsideBlockMethod extends SingletonBlockMethod {

	void entityInside(BlockState state, Level level, BlockPos pos, Entity entity);

}
