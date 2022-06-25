package dev.xkmc.l2library.idea.infmaze.config;

public final class GenerationConfig {

	public int maxScale;
	public long seed;

	public double wallExtra = 0;
	public double cellExtra = 0;
	public int cacheSize = 16384;

	public LeafManager leaf = new LeafManager();

	public GenerationConfig(int maxScale, long seed) {
		this.maxScale = maxScale;
		this.seed = seed;
	}

	public int maxScale() {
		return maxScale;
	}

	public long seed() {
		return seed;
	}

	public double wallExtra() {
		return wallExtra;
	}

	public double cellExtra() {
		return cellExtra;
	}

	public int cacheSize() {
		return cacheSize;
	}

	public double leafChance(int scale) {
		return leaf.leafChance(scale);
	}

}
