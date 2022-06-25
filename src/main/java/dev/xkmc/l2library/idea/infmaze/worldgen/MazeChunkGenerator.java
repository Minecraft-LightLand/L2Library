package dev.xkmc.l2library.idea.infmaze.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xkmc.l2library.idea.infmaze.config.GenerationConfig;
import dev.xkmc.l2library.idea.infmaze.init.InfiniMaze;
import dev.xkmc.l2library.util.code.LazyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public class MazeChunkGenerator extends ChunkGenerator {

	private static final int CELL_WIDTH = 8, SCALE = 5, HEIGHT = CELL_WIDTH << SCALE;

	private static final BlockState AIR = Blocks.AIR.defaultBlockState();
	private static final BlockState BASE = Blocks.STONE.defaultBlockState();

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
		maze = LazyFunction.create(seed -> new InfiniMaze(new GenerationConfig(SCALE, seed)));
		filler = new ChunkFiller(CELL_WIDTH, SCALE, AIR, BASE);
	}

	@Override
	public int getGenDepth() {
		return HEIGHT;
	}

	@Override
	public int getSeaLevel() {
		return 0;
	}

	@Override
	public int getMinY() {
		return 0;
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
			filler.fillChunk(maze, pos, access);
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
			states[i] = BASE;
		}
		return new NoiseColumn(height.getMinBuildHeight(), states);
	}

	@Override
	public void spawnOriginalMobs(WorldGenRegion p_62167_) {

	}

	@Override
	public void addDebugScreenInfo(List<String> list, RandomState random, BlockPos pos) {

	}

	@Override
	public void applyCarvers(WorldGenRegion p_223043_, long p_223044_, RandomState p_223045_, BiomeManager p_223046_, StructureManager p_223047_, ChunkAccess p_223048_, GenerationStep.Carving p_223049_) {

	}

	@Override
	public void buildSurface(WorldGenRegion p_223050_, StructureManager p_223051_, RandomState p_223052_, ChunkAccess p_223053_) {

	}

	@Override
	public void createStructures(RegistryAccess p_223165_, RandomState p_223166_, StructureManager p_223167_, ChunkAccess p_223168_, StructureTemplateManager p_223169_, long p_223170_) {
	}

	@Override
	public Stream<Holder<StructureSet>> possibleStructureSets() {
		return Stream.empty();
	}

	@Override
	public void applyBiomeDecoration(WorldGenLevel p_223087_, ChunkAccess p_223088_, StructureManager p_223089_) {
	}

}
