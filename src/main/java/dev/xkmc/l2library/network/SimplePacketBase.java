package dev.xkmc.l2library.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public abstract class SimplePacketBase {

	public abstract void write(FriendlyByteBuf buffer);

	public abstract void handle(Supplier<Context> context);

}
