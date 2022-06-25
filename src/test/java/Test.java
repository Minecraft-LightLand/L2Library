import dev.xkmc.l2library.idea.infmaze.config.GenerationConfig;
import dev.xkmc.l2library.idea.infmaze.dim3d.MazeCell3D;
import dev.xkmc.l2library.idea.infmaze.init.CellLoaderChain;
import dev.xkmc.l2library.idea.infmaze.init.InfiniMaze;
import dev.xkmc.l2library.idea.infmaze.pos.BasePos;

import java.util.Arrays;

public class Test {

	public static void main(String[] args) {
		InfiniMaze maze = new InfiniMaze(new GenerationConfig(5, 0));
		CellLoaderChain chain = maze.getCell(new BasePos(0, 0, -1));
		MazeCell3D cell = chain.load();
		Arrays.stream(cell.walls);
	}


}
