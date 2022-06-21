package dev.xkmc.l2library.util.raytrace;

import dev.xkmc.l2library.util.Proxy;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class EntityTarget {

	public static final ArrayList<EntityTarget> LIST = new ArrayList<>();

	public final double max_distance, max_angle;
	public final int max_time;
	public int time;
	public Entity target;

	public EntityTarget(double max_distance, double max_angle, int max_time) {
		this.max_distance = max_distance;
		this.max_angle = max_angle;
		this.max_time = max_time;

		LIST.add(this);
	}

	public void updateTarget(@Nullable Entity entity) {
		if (target != entity) {
			onChange(entity);
		}
		target = entity;
		time = 0;
	}

	public void onChange(@Nullable Entity entity) {

	}

	@OnlyIn(Dist.CLIENT)
	public void tickRender() {
		if (target == null) {
			return;
		}
		Player player = Proxy.getClientPlayer();
		if (player == null) {
			updateTarget(null);
			return;
		}
		ItemStack stack = player.getMainHandItem();
		int distance = 0;
		if (stack.getItem() instanceof IGlowingTarget glow) {
			distance = glow.getDistance(stack);
		}
		if (distance == 0) {
			updateTarget(null);
			return;
		}

		Vec3 pos_a = player.getEyePosition();
		Vec3 vec = player.getViewVector(1);
		Vec3 pos_b = target.getPosition(1);
		Vec3 diff = pos_b.subtract(pos_a);
		double dot = diff.dot(vec);
		double len_d = diff.length();
		double len_v = vec.length();
		double angle = Math.acos(dot / len_d / len_v);
		double dist = Math.sin(angle) * len_d;
		if (angle > max_angle && dist > max_distance) {
			updateTarget(null);
		}
		time++;
		if (time >= max_time) {
			updateTarget(null);
		}
	}
}
