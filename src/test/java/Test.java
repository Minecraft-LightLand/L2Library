import dev.xkmc.l2library.idea.infmaze.init.GenerationConfig;
import dev.xkmc.l2library.idea.infmaze.dim3d.MazeCell3D;
import dev.xkmc.l2library.idea.infmaze.init.CellLoaderChain;
import dev.xkmc.l2library.idea.infmaze.init.InfiniMaze;
import dev.xkmc.l2library.idea.infmaze.pos.BasePos;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Test {

	public static record Rec(int a, List<Set<Test[]>> list){

	}

	public static void main(String[] args) throws Exception{
		Rec a = new Rec(1,new ArrayList<>());
		Field f = Rec.class.getDeclaredFields()[1];
		f.setAccessible(true);
		System.out.println(f.get(a));
	}


}
