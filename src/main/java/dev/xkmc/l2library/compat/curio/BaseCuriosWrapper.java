package dev.xkmc.l2library.compat.curio;

import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public abstract class BaseCuriosWrapper {

	public final LivingEntity entity;
	public int total, page;

	public BaseCuriosWrapper( LivingEntity entity) {
		this.entity = entity;
	}

	public abstract int getSize();

	public abstract int getRows();

	@Nullable
	public abstract CuriosSlotWrapper getSlotAtPosition(int i);

}
