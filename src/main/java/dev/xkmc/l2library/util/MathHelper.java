package dev.xkmc.l2library.util;

import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.UUID;

public class MathHelper {

	public static double horSq(Vec3 vec3) {
		return vec3.x * vec3.x + vec3.z * vec3.z;
	}


	public static UUID getUUIDfromString(String str) {
		int hash = str.hashCode();
		Random r = new Random(hash);
		long l0 = r.nextLong();
		long l1 = r.nextLong();
		return new UUID(l0, l1);
	}
}
