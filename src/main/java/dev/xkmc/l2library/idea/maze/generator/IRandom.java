package dev.xkmc.l2library.idea.maze.generator;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;

import java.util.Random;
import java.util.function.DoubleSupplier;

public interface IRandom {

	int nextInt(int i);

	double nextDouble();

	record CRandom(Int2IntFunction i, DoubleSupplier sup) implements IRandom {

		@Override
		public int nextInt(int i) {
			return i().applyAsInt(i);
		}

		@Override
		public double nextDouble() {
			return sup.getAsDouble();
		}
	}

	static IRandom parse(Random random) {
		return new CRandom(random::nextInt, random::nextDouble);
	}

}
