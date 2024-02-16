package dev.xkmc.l2library.capability.player;

import dev.xkmc.l2library.capability.attachment.GeneralCapabilityHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class PlayerCapabilityHolder<T extends PlayerCapabilityTemplate<T>> extends GeneralCapabilityHolder<Player, T> {

	public static final Map<ResourceLocation, PlayerCapabilityHolder<?>> INTERNAL_MAP = new ConcurrentHashMap<>();

	public final PlayerCapabilityNetworkHandler<T> network;

	public PlayerCapabilityHolder(ResourceLocation id, Class<T> cls, Supplier<T> sup, NetworkFactory<T> network) {
		super(id, cls, sup, Player.class, e -> true);
		this.network = network.create(this);
		INTERNAL_MAP.put(id, this);
	}

	protected boolean copyOnDeath() {
		return true;
	}

	public interface NetworkFactory<T extends PlayerCapabilityTemplate<T>> {

		PlayerCapabilityNetworkHandler<T> create(PlayerCapabilityHolder<T> holder);

	}

}
