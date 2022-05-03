package dev.xkmc.l2library.block.mult;

import dev.xkmc.l2library.block.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface OnClickBlockMethod extends MultipleBlockMethod {

	InteractionResult onClick(BlockState state, Level level, BlockPos pos, Player pl, InteractionHand hand, BlockHitResult result);

}
