package dev.xkmc.l2library.base.effects;

import dev.xkmc.l2library.capability.attachment.GeneralCapabilityTemplate;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@SerialClass
public class ClientEffectCap extends GeneralCapabilityTemplate<LivingEntity, ClientEffectCap> {

	public final Map<MobEffect, Integer> map = new TreeMap<>(Comparator.comparing(MobEffect::getDescriptionId));

}
