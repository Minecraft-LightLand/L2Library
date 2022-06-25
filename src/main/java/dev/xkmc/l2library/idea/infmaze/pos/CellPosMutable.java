package dev.xkmc.l2library.idea.infmaze.pos;

public class CellPosMutable implements ICellPos {

	public IBasePos pos;
	public int scale;

	public CellPosMutable(IBasePos pos, int scale) {
		this.pos = pos;
		this.scale = scale;
	}

	@Override
	public IBasePos pos() {
		return pos;
	}

	@Override
	public int scale() {
		return scale;
	}

	@Override
	public CellPosMutable offsetChunk(MazeDirection axis, long amount) {
		pos = pos.offset(axis, amount * scale);
		return this;
	}

	@Override
	public WallPos getWallPos(MazeDirection axis) {
		if (axis.factor < 0) {
			return new WallPos(new BasePos(pos), scale(), axis.axis);
		}
		return new WallPos(new BasePos(pos).offset(axis, scale()), scale(), axis.axis);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ICellPos pos) {
			return compareTo(pos) == 0;
		}
		return false;
	}

}
