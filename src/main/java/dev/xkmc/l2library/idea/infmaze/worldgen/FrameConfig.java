package dev.xkmc.l2library.idea.infmaze.worldgen;

import net.minecraft.world.level.block.state.BlockState;

public record FrameConfig(BlockState air, BlockState bedrock, BlockState hard, BlockState wall) {
}
