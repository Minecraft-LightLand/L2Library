package dev.xkmc.l2library.init.events.click.quickaccess;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.BiFunction;

public record DummyHandler(Player player) implements ContainerLevelAccess {

	@Override
	public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> func) {
		func.apply(player.getLevel(), player.getOnPos());
		return Optional.empty();
	}

}
