package dev.xkmc.l2library.util;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2library.init.L2Library;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GoalFix {

	private static final ThreadLocal<Pair<GoalSelector, GoalFix>> TEMP = new ThreadLocal<>();

	public static void start(GoalSelector self) {
		TEMP.set(Pair.of(self, new GoalFix(self)));
	}

	public static void end(GoalSelector self) {
		var pair = TEMP.get();
		TEMP.set(null);
		if (pair == null || pair.getFirst() != self) {
			L2Library.LOGGER.error("Goal Mismatch");
			L2Library.LOGGER.throwing(Level.ERROR, new IllegalStateException("Goal Mismatch"));
			return;
		}
		pair.getSecond().flush();
	}

	public static boolean add(int p, Goal g, GoalSelector self) {
		var pair = TEMP.get();
		if (pair == null) return false;
		if (pair.getFirst() != self) {
			L2Library.LOGGER.error("Goal Mismatch");
			L2Library.LOGGER.throwing(Level.ERROR, new IllegalStateException("Goal Mismatch"));
			return true;
		}
		L2Library.LOGGER.error("Goal added at wrong time");
		L2Library.LOGGER.throwing(Level.ERROR, new IllegalStateException("Goal added at wrong time"));
		pair.getSecond().delayAdd(p, g);
		return true;
	}

	public static boolean remove(Goal g, GoalSelector self) {
		var pair = TEMP.get();
		if (pair == null) return false;
		if (pair.getFirst() != self) {
			L2Library.LOGGER.error("Goal Mismatch");
			L2Library.LOGGER.throwing(Level.ERROR, new IllegalStateException("Goal Mismatch"));
			return true;
		}
		L2Library.LOGGER.error("Goal removed at wrong time");
		L2Library.LOGGER.throwing(Level.ERROR, new IllegalStateException("Goal removed at wrong time"));
		pair.getSecond().delayRemove(g);
		return true;
	}

	public static boolean removeAll(Predicate<Goal> pred, GoalSelector self) {
		var pair = TEMP.get();
		if (pair == null) return false;
		if (pair.getFirst() != self) {
			L2Library.LOGGER.error("Goal Mismatch");
			L2Library.LOGGER.throwing(Level.ERROR, new IllegalStateException("Goal Mismatch"));
			return true;
		}
		L2Library.LOGGER.error("Goal removed at wrong time");
		L2Library.LOGGER.throwing(Level.ERROR, new IllegalStateException("Goal removed at wrong time"));
		pair.getSecond().delayRemoveAll(pred);
		return true;
	}

	private final GoalSelector ins;

	private final List<Runnable> todo = new ArrayList<>();

	public GoalFix(GoalSelector ins) {
		this.ins = ins;
	}

	private void delayAdd(int p, Goal g) {
		todo.add(() -> ins.addGoal(p, g));
	}

	private void delayRemove(Goal g) {
		todo.add(() -> ins.removeGoal(g));
	}

	private void delayRemoveAll(Predicate<Goal> pred) {
		todo.add(() -> ins.removeAllGoals(pred));
	}

	private void flush() {
		for (var e : todo) e.run();
	}

}
