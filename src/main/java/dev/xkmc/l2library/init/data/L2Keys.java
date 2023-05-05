package dev.xkmc.l2library.init.data;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public enum L2Keys {
	UP("key.l2library.up", GLFW.GLFW_KEY_UP),
	DOWN("key.l2library.down", GLFW.GLFW_KEY_DOWN),
	LEFT("key.l2library.left", GLFW.GLFW_KEY_LEFT),
	RIGHT("key.l2library.right", GLFW.GLFW_KEY_RIGHT);

	public final String id;
	public final int key;
	public final KeyMapping map;

	L2Keys(String id, int key) {
		this.id = id;
		this.key = key;
		this.map = new KeyMapping(id, key, "key.categories.l2library");
	}
}
