package dev.xkmc.l2library.capability.player;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.codec.TagCodec;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2library.util.code.Wrappers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class PlayerCapabilityHolder<T extends PlayerCapabilityTemplate<T>> {

	public static final Map<ResourceLocation, PlayerCapabilityHolder<?>> INTERNAL_MAP = new ConcurrentHashMap<>();

	public final Capability<T> capability;
	public final ResourceLocation id;
	public final Class<T> cls;
	public final Supplier<T> sup;
	public final PlayerCapabilityNetworkHandler<T> network;

	public PlayerCapabilityHolder(ResourceLocation id, Capability<T> capability, Class<T> cls, Supplier<T> sup,
								  Function<PlayerCapabilityHolder<T>, PlayerCapabilityNetworkHandler<T>> network) {
		this.id = id;
		this.capability = capability;
		this.cls = cls;
		this.sup = sup;
		this.network = network.apply(this);
		INTERNAL_MAP.put(id, this);
	}

	public T get(Player e) {
		T data;
		var lazyCap = e.getCapability(capability);
		if (!lazyCap.isPresent()) {
			e.reviveCaps();
			data = lazyCap.resolve().get().check();
			e.invalidateCaps();
		} else data = lazyCap.resolve().get().check();
		return data;
	}

	public boolean isProper(Player player) {
		return player.getCapability(capability).isPresent();
	}

	@OnlyIn(Dist.CLIENT)
	private CompoundTag revive_cache;

	@OnlyIn(Dist.CLIENT)
	public void cacheSet(CompoundTag tag, boolean force) {
		AbstractClientPlayer pl = Proxy.getClientPlayer();
		if (!force && pl != null && pl.getCapability(capability).cast().resolve().isPresent()) {
			T m = get(pl);
			m.preInject();
			Wrappers.run(() -> TagCodec.fromTag(tag, cls, m, f -> true));
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

	public PlayerCapabilitySerializer<T> generateSerializer(Player player, Level level) {
		return new PlayerCapabilitySerializer<>(player, level, this);
	}

	@OnlyIn(Dist.CLIENT)
	public void updateTracked(CompoundTag tag, @Nullable Player pl) {
		if (!(pl instanceof RemotePlayer player)) return;
		if (player.getCapability(capability).cast().resolve().isPresent()) {
			T m = get(player);
			m.preInject();
			Wrappers.run(() -> TagCodec.fromTag(tag, cls, m, SerialClass.SerialField::toTracking));
			m.init();
		}
	}
}
