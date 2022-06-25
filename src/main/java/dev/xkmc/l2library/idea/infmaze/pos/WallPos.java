package dev.xkmc.l2library.idea.infmaze.pos;

public record WallPos(BasePos pos, int scale, MazeAxis normal) implements IWallPos {

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IWallPos pos) {
			return compareTo(pos) == 0;
		}
		return false;
	}

	@Override
	public WallPos offset(long du, long dv, int ds) {
		return switch (normal) {
			case X -> offset(0, du, dv, ds);
			case Y -> offset(du, 0, dv, ds);
			case Z -> offset(du, dv, 0, ds);
		};
	}

	@Override
	public WallPos offset(long dx, long dy, long dz, int ds) {
		return new WallPos(pos.offset(dx, dy, dz), scale + ds, normal);
	}
}
