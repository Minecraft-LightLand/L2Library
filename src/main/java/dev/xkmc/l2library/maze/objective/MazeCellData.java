package dev.xkmc.l2library.maze.objective;

public abstract class MazeCellData<T extends MazeCellData<T, E>, E extends MazeGeneralData> {

	public int access_direction, x, y, n;

	public abstract void fillData(E global, T[] children);

	public abstract double getResult();

	@SuppressWarnings("unchecked")
	public T getThis() {
		return (T) this;
	}

	public T setAccessDire(int x, int y, int dire, int n) {
		this.x = x;
		this.y = y;
		this.n = n;
		access_direction = dire;
		return getThis();
	}

}