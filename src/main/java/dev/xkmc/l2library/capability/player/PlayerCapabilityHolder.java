package dev.xkmc.l2library.capability.player;

import dev.xkmc.l2library.capability.entity.GeneralCapabilityHolder;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class PlayerCapabilityHolder<T extends PlayerCapabilityTemplate<T>> extends GeneralCapabilityHolder<Player, T> {

	public static final Map<ResourceLocation, PlayerCapabilityHolder<?>> INTERNAL_MAP = new ConcurrentHashMap<>();

	public final PlayerCapabilityNetworkHandler<T> network;

	public PlayerCapabilityHolder(ResourceLocation id, Capability<T> capability, Class<T> cls, Supplier<T> sup,
								  Function<PlayerCapabilityHolder<T>, PlayerCapabilityNetworkHandler<T>> network) {
		super(id, capability, cls, sup, Player.class, e -> true);
		this.network = network.apply(this);
		INTERNAL_MAP.put(id, this);
	}

	public T get(Player e) {
		T data;
		var lazyCap = e.getCapability(capability);
		if (lazyCap.resolve().isEmpty()) {
			e.reviveCaps();
			data = e.getCapability(capability).resolve().get().check();
			e.invalidateCaps();
		} else data = lazyCap.resolve().get().check();
		return data;
	}

	@OnlyIn(Dist.CLIENT)
	private CompoundTag revive_cache;

	@OnlyIn(Dist.CLIENT)
	public void cacheSet(CompoundTag tag, boolean force) {
		AbstractClientPlayer pl = Proxy.getClientPlayer();
		if (!force && pl != null && pl.getCapability(capability).cast().resolve().isPresent()) {
			T m = get(pl);
			m.preInject();
			Wrappers.run(() -> TagCodec.fromTag(tag, holder_class, m, f -> true));
			m.init();
		} else revive_cache = tag;
	}

	@OnlyIn(Dist.CLIENT)
	public CompoundTag getCache(Player pl) {
		CompoundTag tag = revive_cache;
		revive_cache = null;
		if (tag == null)
			tag = TagCodec.toTag(new CompoundTag(), get(pl));
		return tag;
	}

	public PlayerCapabilitySerializer<T> generateSerializer(Player player) {
		return new PlayerCapabilitySerializer<>(player, this);
	}

	@OnlyIn(Dist.CLIENT)
	public void updateTracked(CompoundTag tag, @Nullable Player pl) {
		if (!(pl instanceof RemotePlayer player)) return;
		if (player.getCapability(capability).cast().resolve().isPresent()) {
			T m = get(player);
			m.preInject();
			Wrappers.run(() -> TagCodec.fromTag(tag, holder_class, m, SerialClass.SerialField::toTracking));
			m.init();
		}
	}
}
