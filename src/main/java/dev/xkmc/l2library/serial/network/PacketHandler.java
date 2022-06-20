package dev.xkmc.l2library.serial.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {

	private static final List<PacketHandler> LIST = new ArrayList<>();

	public static void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			for (PacketHandler handler : LIST) {
				handler.registerPackets();
			}
		});
	}

	public final ResourceLocation CHANNEL_NAME;
	public final int NETWORK_VERSION;
	public final String NETWORK_VERSION_STR;
	public SimpleChannel channel;

	private final Function<PacketHandler, LoadedPacket<?>>[] values;

	@SafeVarargs
	public PacketHandler(ResourceLocation id, int version, Function<PacketHandler, LoadedPacket<?>>... values) {
		CHANNEL_NAME = id;
		NETWORK_VERSION = version;
		NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);
		this.values = values;
		LIST.add(this);
	}

	public <T extends SimplePacketBase> LoadedPacket<T> create(Class<T> type, Function<FriendlyByteBuf, T> factory,
															   NetworkDirection direction) {
		return new LoadedPacket<>(type, factory, direction);
	}

	public <T extends SerialPacketBase> LoadedPacket<T> create(Class<T> type, NetworkDirection direction) {
		return new LoadedPacket<>(type, (buf) -> SerialPacketBase.serial(type, buf), direction);
	}


	public void toServer(SimplePacketBase packet) {
		channel.sendToServer(packet);
	}

	public void toTrackingPlayers(SimplePacketBase packet, Entity e) {
		channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> e), packet);
	}

	public void toClientPlayer(SimplePacketBase packet, ServerPlayer e) {
		channel.sendTo(packet, e.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
	}

	public void toAllClient(SimplePacketBase packet) {
		channel.send(PacketDistributor.ALL.noArg(), packet);
	}


	public void sendToNear(Level world, BlockPos pos, int range, Object message) {
		channel.send(PacketDistributor.NEAR
				.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), range, world.dimension())), message);
	}

	private void registerPackets() {
		channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
				.serverAcceptedVersions(NETWORK_VERSION_STR::equals)
				.clientAcceptedVersions(NETWORK_VERSION_STR::equals)
				.networkProtocolVersion(() -> NETWORK_VERSION_STR)
				.simpleChannel();
		for (Function<PacketHandler, LoadedPacket<?>> packet : values)
			packet.apply(this).register();
	}

	public class LoadedPacket<T extends SimplePacketBase> {
		private static int index = 0;

		private final BiConsumer<T, FriendlyByteBuf> encoder;
		private final Function<FriendlyByteBuf, T> decoder;
		private final BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
		private final Class<T> type;
		private final NetworkDirection direction;

		private LoadedPacket(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
			encoder = T::write;
			decoder = factory;
			handler = T::handle;
			this.type = type;
			this.direction = direction;
		}

		private void register() {
			channel.messageBuilder(type, index++, direction)
					.encoder(encoder)
					.decoder(decoder)
					.consumer(handler)
					.add();
		}
	}

}
