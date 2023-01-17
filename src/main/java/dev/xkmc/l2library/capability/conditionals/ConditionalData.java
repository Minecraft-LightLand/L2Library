package dev.xkmc.l2library.capability.conditionals;

import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.capability.player.PlayerCapabilityNetworkHandler;
import dev.xkmc.l2library.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.util.code.Wrappers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import javax.annotation.Nullable;
import java.util.HashMap;

@SerialClass
public class ConditionalData extends PlayerCapabilityTemplate<ConditionalData> {

	public static final Capability<ConditionalData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static final PlayerCapabilityHolder<ConditionalData> HOLDER = new PlayerCapabilityHolder<>(
			new ResourceLocation(L2Library.MODID, "conditionals"), CAPABILITY,
			ConditionalData.class, ConditionalData::new, PlayerCapabilityNetworkHandler::new);

	public static void register() {
	}

	@SerialClass.SerialField
	public HashMap<TokenKey<?>, ConditionalToken> data = new HashMap<>();

	public <T extends ConditionalToken, C extends Context> T getOrCreateData(TokenProvider<T, C> setEffect, C ent) {
		return Wrappers.cast(data.computeIfAbsent(setEffect.getKey(), e -> setEffect.getData(ent)));
	}

	@Nullable
	public <T extends ConditionalToken> T getData(TokenKey<T> setEffect) {
		return Wrappers.cast(data.get(setEffect));
	}

	@Override
	public void tick() {
		data.entrySet().removeIf(e -> e.getValue().tick(player));
	}

	public boolean hasData(TokenKey<?> eff) {
		return data.containsKey(eff);
	}

}
