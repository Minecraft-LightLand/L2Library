package dev.xkmc.l2library.util.code;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

public class Wrappers {

	public static <T> T parse(Supplier<T> sup) {
		return sup.get();
	}

	@SuppressWarnings({"unsafe", "unchecked"})
	public static <A, B> B cast(A a) {
		return (B) a;
	}

	public static void run(ExcRun run) {
		try {
			run.get();
		} catch (Throwable e) {
			LogManager.getLogger().throwing(Level.ERROR, e);
		}
	}

	public static <T> T get(ExcSup<T> sup) {
		try {
			return sup.get();
		} catch (Throwable e) {
			LogManager.getLogger().throwing(Level.ERROR, e);
			return null;
		}
	}

	public static <T> T ignore(ExcSup<T> sup) {
		try {
			return sup.get();
		} catch (Throwable e) {
			return null;
		}
	}

	public static void ignore(ExcRun sup) {
		try {
			sup.get();
		} catch (Throwable e) {
		}
	}

	@FunctionalInterface
	public interface ExcRun {

		void get() throws Throwable;

	}

	@FunctionalInterface
	public interface ExcSup<T> {

		T get() throws Throwable;

	}

}
