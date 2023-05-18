package dev.xkmc.l2library.init.events.screen.base;

import dev.xkmc.l2library.init.L2LibraryConfig;
import dev.xkmc.l2library.init.events.screen.track.MenuProviderTraceData;
import dev.xkmc.l2library.init.events.screen.track.TrackedEntry;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public record MenuCache(AbstractContainerMenu menu, MenuProvider pvd, Component title,
						MenuTriggerType type, @Nullable FriendlyByteBuf buf) {

	public static MenuCache of(AbstractContainerMenu menu, MenuProvider next, MenuTriggerType type, @Nullable Consumer<FriendlyByteBuf> writer) {
		FriendlyByteBuf buf = null;
		if (writer != null) {
			buf = new FriendlyByteBuf(Unpooled.buffer());
			writer.accept(buf);
		}
		if (L2LibraryConfig.COMMON.tabSafeMode.get()) {
			if (buf != null && buf.writerIndex() > 0) {
				type = MenuTriggerType.DISABLED;
			} else {
				type = MenuTriggerType.NETWORK_HOOK_SIMPLE;
			}
			buf = null;
		}
		return new MenuCache(menu, next, next.getDisplayName(), type, buf);
	}

	public TrackedEntry<?> constructEntry() {
		return TrackedEntry.of(ScreenTrackerRegistry.TE_MENU_PROVIDER.get(), new MenuProviderTraceData(this));
	}

	public boolean restore(ServerPlayer player) {
		return type.restore(player, pvd, buf);
	}

}
