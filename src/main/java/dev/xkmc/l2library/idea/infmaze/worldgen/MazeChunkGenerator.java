package dev.xkmc.l2library.idea.infmaze.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xkmc.l2library.base.worldgen.EmptyChunkGenerator;
import dev.xkmc.l2library.idea.infmaze.init.GenerationConfig;
import dev.xkmc.l2library.idea.infmaze.init.InfiniMaze;
import dev.xkmc.l2library.idea.infmaze.init.LeafManager;
import dev.xkmc.l2library.idea.infmaze.worldgen.leaf.RoomLeafManager;
import dev.xkmc.l2library.util.code.LazyFunction;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MazeChunkGenerator extends EmptyChunkGenerator {

	private static final int CELL_WIDTH = 8, SCALE = 5, HEIGHT = CELL_WIDTH << SCALE;
	private static final ResourceLocation RL = new ResourceLocation("l2library", "maze_chunkgen");
	private static final LeafManager MANAGER = new RoomLeafManager();

	private static final FrameConfig BLOCKS = new FrameConfig(
			Blocks.AIR.defaultBlockState(),
			Blocks.BEDROCK.defaultBlockState(),
			Blocks.OBSIDIAN.defaultBlockState(),
			Blocks.STONE.defaultBlockState()
	);

	public static final Codec<MazeChunkGenerator> CODEC = RecordCodecBuilder.create((e) ->
			commonCodec(e).and(
					RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter(x -> x.biomes)
			).apply(e, e.stable(MazeChunkGenerator::new)));

	private final Registry<Biome> biomes;
	private final LazyFunction<Long, InfiniMaze> maze;
	private final ChunkFiller filler;

	public MazeChunkGenerator(Registry<StructureSet> structures, Registry<Biome> biomes) {
		super(structures, Optional.empty(), new FixedBiomeSource(biomes.getHolder(Biomes.PLAINS).get()));
		this.biomes = biomes;
		maze = LazyFunction.create(seed -> new InfiniMaze(new GenerationConfig(SCALE, seed, null)));
		filler = new ChunkFiller(CELL_WIDTH, SCALE, BLOCKS);
	}

	@Override
	public int getGenDepth() {
		return HEIGHT;
	}

	@Override
	protected Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState random, StructureManager structures, ChunkAccess access) {
		return CompletableFuture.supplyAsync(() -> {
			InfiniMaze maze = this.maze.get(random.legacyLevelSeed());
			ChunkPos pos = access.getPos();
			filler.fillChunk(maze, pos, access, random.getOrCreateRandomFactory(RL).at(pos.getBlockAt(0, 0, 0)));
			return access;
		}, executor);
	}

	@Override
	public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor height, RandomState random) {
		return getGenDepth();
	}

	@Override
	public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
		BlockState[] states = new BlockState[height.getHeight()];
		for (int i = 0; i < height.getHeight(); i++) {
			states[i] = BLOCKS.wall();
		}
		return new NoiseColumn(height.getMinBuildHeight(), states);
	}

}
