package dev.xkmc.l2library.idea.maze.objective;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MazeRegistry {

	public static class Entry<T extends MazeCellData<T, E>, E extends MazeGeneralData> {

		public final Class<T> cls;
		public final Supplier<T> sup;
		public final Supplier<E> gen;
		public final String name;

		public Entry(String name, Class<T> cls, Supplier<T> sup, Supplier<E> gen) {
			this.name = name;
			this.cls = cls;
			this.sup = sup;
			this.gen = gen;
			LIST.add(this);
		}

		public double execute(int[][] ans, int x, int y) {
			return new MazeIterator<>(ans, this).iterate(x, y, -1).getResult();
		}

		public MazeIterator<T, E> generate(int[][] ans, int x, int y) {
			MazeIterator<T, E> itr = new MazeIterator<>(ans, this);
			itr.iterate(x, y, -1);
			return itr;
		}

	}

	public static final List<Entry<?, ?>> LIST = new ArrayList<>();

	public static final Entry<FarpointObjective, MazeGeneralData> FAR = new Entry<>("furthest pair distance",
			FarpointObjective.class, FarpointObjective::new, MazeGeneralData::new);
	public static final Entry<BranchCountObjective, MazeGeneralData> BRANCH_COUNT = new Entry<>("branch count",
			BranchCountObjective.class, BranchCountObjective::new, MazeGeneralData::new);
	public static final Entry<StraightLeafObjective, MazeGeneralData> STRAIGHT = new Entry<>("straight leaf size",
			StraightLeafObjective.class, StraightLeafObjective::new, MazeGeneralData::new);
	public static final Entry<ComplexBranchObjective, MazeGeneralData> COMPLEX = new Entry<>("complexity",
			ComplexBranchObjective.class, ComplexBranchObjective::new, MazeGeneralData::new);
	public static final Entry<LeafMarker, LeafMarker.LeafSetData> MARKER = new Entry<>("leaf marker",
			LeafMarker.class, LeafMarker::new, LeafMarker.LeafSetData::new);
}
