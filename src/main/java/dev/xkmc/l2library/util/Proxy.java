package dev.xkmc.l2library.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Optional;

public class Proxy {

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static LocalPlayer getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static ClientLevel getClientWorld() {
		return Minecraft.getInstance().level;
	}

	public static Optional<MinecraftServer> getServer() {
		return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer());
	}

}
