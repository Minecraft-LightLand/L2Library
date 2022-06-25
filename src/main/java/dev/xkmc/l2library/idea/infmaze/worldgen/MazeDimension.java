package dev.xkmc.l2library.idea.infmaze.worldgen;

import dev.xkmc.l2library.init.L2Library;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegisterEvent;

public class MazeDimension {

	public static final ResourceKey<Level> MYSTERIOUS = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(L2Library.MODID, "maze"));

	private static boolean loaded = false;

	public static void register(RegisterEvent event) {
		if (loaded) return;
		loaded = true;
		Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(L2Library.MODID, "maze_chunkgen"),
				MazeChunkGenerator.CODEC);
	}

}
