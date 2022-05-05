package dev.xkmc.l2library.block.mult;

import dev.xkmc.l2library.block.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface AttackBlockMethod extends MultipleBlockMethod {

	/**
	 * return true to stop further action
	 */
	boolean attack(BlockState state, Level level, BlockPos pos, Player player);
}
