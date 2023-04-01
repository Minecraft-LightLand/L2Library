package dev.xkmc.l2library.util.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public class TeleportTool {

	public static void teleportHome(ServerLevel world, ServerPlayer player) {
		ServerLevel targetWorld = world.getServer().getLevel(player.getRespawnDimension());
		BlockPos blockpos = player.getRespawnPosition();
		float f = player.getRespawnAngle();
		boolean flag = player.isRespawnForced();
		Optional<Vec3> optional;
		if (targetWorld != null && blockpos != null) {
			optional = Player.findRespawnPositionAndUseSpawnBlock(targetWorld, blockpos, f, flag, true);
		} else {
			optional = Optional.empty();
		}
		Vec3 pos;
		if (targetWorld == null || optional.isEmpty()) {
			targetWorld = world.getServer().overworld();
			BlockPos bpos = targetWorld.getSharedSpawnPos();
			pos = new Vec3(bpos.getX() + 0.5, bpos.getY() + 1, bpos.getZ() + 0.5);
		} else {
			pos = optional.get();
		}
		if (world == targetWorld) {
			player.teleportTo(pos.x, pos.y, pos.z);
		} else {
			performTeleport(player, targetWorld, pos.x, pos.y, pos.z, player.getRespawnAngle(), player.getYHeadRot());
		}
	}

	public static void performTeleport(Entity entity, ServerLevel level, double x, double y, double z, float xrot, float yrot) {
		Set<RelativeMovement> set = EnumSet.noneOf(RelativeMovement.class);
		float f = Mth.wrapDegrees(xrot);
		float f1 = Mth.wrapDegrees(yrot);
		if (entity instanceof ServerPlayer player) {
			ChunkPos chunkpos = new ChunkPos(BlockPos.containing(x, y, z));
			level.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, player.getId());
			player.stopRiding();
			if (player.isSleeping()) {
				player.stopSleepInBed(true, true);
			}
			if (level == player.level) {
				player.connection.teleport(x, y, z, f, f1, set);
			} else {
				player.teleportTo(level, x, y, z, f, f1);
			}
			player.setYHeadRot(f);
		} else {
			float f2 = Mth.clamp(f1, -90.0F, 90.0F);
			if (level == entity.level) {
				entity.moveTo(x, y, z, f, f2);
				entity.setYHeadRot(f);
			} else {
				entity.unRide();
				Entity newEntity = entity.getType().create(level);
				if (newEntity == null) {
					return;
				}
				newEntity.restoreFrom(entity);
				newEntity.moveTo(x, y, z, f, f2);
				newEntity.setYHeadRot(f);
				entity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
				level.addDuringTeleport(newEntity);
			}
		}
		if (!(entity instanceof LivingEntity) || !((LivingEntity) entity).isFallFlying()) {
			entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
			entity.setOnGround(true);
		}
		if (entity instanceof PathfinderMob) {
			((PathfinderMob) entity).getNavigation().stop();
		}
	}


}
