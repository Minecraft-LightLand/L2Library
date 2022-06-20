package dev.xkmc.l2library.util.code;

public class Lock {

	private boolean recursive = false;

	public void execute(Runnable runnable) {
		if (recursive) return;
		recursive = true;
		runnable.run();
		recursive = false;
	}

}
