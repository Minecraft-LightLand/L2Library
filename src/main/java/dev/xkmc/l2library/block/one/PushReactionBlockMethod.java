package dev.xkmc.l2library.block.one;

import dev.xkmc.l2library.block.type.SingletonBlockMethod;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public interface PushReactionBlockMethod extends SingletonBlockMethod {

	PushReaction getPistonPushReaction(BlockState state);

}
