package dev.xkmc.l2library.base.worldgen;

import net.minecraft.core.*;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class EmptyChunkGenerator extends ChunkGenerator {

	public EmptyChunkGenerator(Registry<StructureSet> p_207960_, Optional<HolderSet<StructureSet>> p_207961_, BiomeSource p_207962_) {
		super(p_207960_, p_207961_, p_207962_);
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
