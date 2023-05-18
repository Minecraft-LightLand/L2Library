package dev.xkmc.l2library.init.events.screen.track;

import dev.xkmc.l2library.init.events.screen.base.MenuCache;

public record MenuProviderTraceData(MenuCache cache)
		implements TrackedEntryData<MenuProviderTraceData> {
}
