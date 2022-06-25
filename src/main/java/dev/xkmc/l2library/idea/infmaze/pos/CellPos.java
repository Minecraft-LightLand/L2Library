package dev.xkmc.l2library.idea.infmaze.pos;

public record CellPos(BasePos pos, int scale) implements ICellPos {

	@Override
	public CellPos offsetChunk(MazeDirection axis, long amount) {
		return new CellPos(pos.offset(axis, amount * scale), scale);
	}

	@Override
	public WallPos getWallPos(MazeDirection axis) {
		if (axis.factor < 0) {
			return new WallPos(pos(), scale(), axis.axis);
		}
		return new WallPos(pos().offset(axis, 1L << scale()), scale(), axis.axis);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ICellPos pos) {
			return compareTo(pos) == 0;
		}
		return false;
	}

}
