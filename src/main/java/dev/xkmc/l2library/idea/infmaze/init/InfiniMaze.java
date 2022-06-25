package dev.xkmc.l2library.idea.infmaze.init;

import dev.xkmc.l2library.idea.infmaze.config.GenerationConfig;
import dev.xkmc.l2library.idea.infmaze.dim3d.GenerationHelper;
import dev.xkmc.l2library.idea.infmaze.dim3d.MazeCell3D;
import dev.xkmc.l2library.idea.infmaze.dim3d.MazeWall3D;
import dev.xkmc.l2library.idea.infmaze.pos.*;

import java.util.TreeMap;

public class InfiniMaze {

	public final GenerationConfig config;

	public GenerationHelper helper;

	private final TreeMap<BasePos, MazeCell3D> cellMap = new TreeMap<>();
	private final TreeMap<WallPos, MazeWall3D> wallMap = new TreeMap<>();

	public InfiniMaze(GenerationConfig config) {
		this.config = config;
		this.helper = new GenerationHelper(config);
	}

	public int getMaxScale() {
		return config.maxScale();
	}

	public void clearCache() {
		helper = new GenerationHelper(config);
		cellMap.clear();
		wallMap.clear();
	}

	public CellLoaderChain getCell(BasePos pos) {
		if (helper.cellCount > config.cacheSize()) {
			clearCache();
		}
		return new CellLoaderChain(getOrGenerateRootCell(pos), pos);
	}

	private MazeCell3D getOrGenerateRootCell(BasePos pos) {
		int scale = getMaxScale();
		BasePos raw = new BasePos(pos.x() >> scale, pos.y() >> scale, pos.z() >> scale);
		long seed = helper.getRootCellSeed(config.seed(), raw);
		return cellMap.computeIfAbsent(raw, k -> generateCell(raw, seed));
	}

	private MazeCell3D generateCell(BasePos raw, long seed) {
		int scale = getMaxScale();
		MazeWall3D[] ans = new MazeWall3D[6];
		BasePos cell = new BasePos(raw.x() << scale, raw.y() << scale, raw.z() << scale);
		CellPos pos = new CellPos(cell, scale);
		for (MazeDirection dire : MazeDirection.values()) {
			WallPos wallPos = pos.getWallPos(dire);
			ans[dire.ordinal()] = getOrGenerateRootWall(wallPos.pos(), wallPos.normal());
		}
		return new MazeCell3D(helper, pos, seed, ans);
	}

	private MazeWall3D getOrGenerateRootWall(BasePos pos, MazeAxis axis) {
		int scale = getMaxScale();
		BasePos raw = new BasePos(pos.x() >> scale, pos.y() >> scale, pos.z() >> scale);
		WallPos key = new WallPos(raw, 0, axis);
		return wallMap.computeIfAbsent(key, k -> generateWall(raw, axis));
	}

	private MazeWall3D generateWall(BasePos raw, MazeAxis axis) {
		int scale = getMaxScale();
		BasePos cell = new BasePos(raw.x() << scale, raw.y() << scale, raw.z() << scale);
		long seed = helper.getRootWallSeed(config.seed(), raw, axis);
		return new MazeWall3D(helper, new WallPos(cell, scale, axis), axis != MazeAxis.Y, seed);
	}

}
