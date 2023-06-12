package dev.xkmc.l2library.base.effects;

import dev.xkmc.l2library.capability.entity.GeneralCapabilityHolder;
import dev.xkmc.l2library.capability.entity.GeneralCapabilityTemplate;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@SerialClass
public class ClientEffectCap extends GeneralCapabilityTemplate<LivingEntity, ClientEffectCap> {

	public static final Capability<ClientEffectCap> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static final GeneralCapabilityHolder<LivingEntity, ClientEffectCap> HOLDER =
			new GeneralCapabilityHolder<>(new ResourceLocation(L2Library.MODID, "effects"),
					CAPABILITY, ClientEffectCap.class, ClientEffectCap::new,
					LivingEntity.class, e -> e.level().isClientSide());

	public final Map<MobEffect, Integer> map = new TreeMap<>(Comparator.comparing(MobEffect::getDescriptionId));

	public static void register() {

	}

}
