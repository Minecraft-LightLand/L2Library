package dev.xkmc.l2library.init.events.listeners;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.xkmc.l2library.base.effects.EffectToClient;
import dev.xkmc.l2library.base.effects.api.ClientRenderEffect;
import dev.xkmc.l2library.base.effects.api.FirstPlayerRenderEffect;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEffectRenderEvents {

	public static final Map<UUID, Map<MobEffect, Integer>> EFFECT_MAP = new HashMap<>();
	private static final ArrayList<DelayedEntityRender> ICONS = new ArrayList<>();

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		AbstractClientPlayer player = Proxy.getClientPlayer();
		if (player != null) {
			for (Map.Entry<MobEffect, MobEffectInstance> entry : player.getActiveEffectsMap().entrySet()) {
				if (entry.getKey() instanceof FirstPlayerRenderEffect effect) {
					effect.onClientLevelRender(player, entry.getValue());
				}
			}
		} else {
			EFFECT_MAP.clear();
		}
	}

	@SubscribeEvent
	public static void levelRenderLast(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;
		LevelRenderer renderer = event.getLevelRenderer();
		MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		PoseStack stack = event.getPoseStack();

		// cache the previous stack
		PoseStack posestack = RenderSystem.getModelViewStack();
		var last = posestack.last();
		posestack.popPose();
		RenderSystem.applyModelViewMatrix();

		RenderSystem.disableDepthTest();
		for (DelayedEntityRender icon : ICONS) {
			renderIcon(icon.entity(), stack, buffers, icon.rl(), event.getPartialTick(), camera, renderer.entityRenderDispatcher);
		}
		buffers.endBatch();

		// restore the previous stack
		posestack.pushPose();
		posestack.setIdentity();
		posestack.last().pose().mul(last.pose());
		posestack.last().normal().mul(last.normal());
		RenderSystem.applyModelViewMatrix();

		RenderSystem.enableDepthTest();

		ICONS.clear();
	}

	@SubscribeEvent
	public static void onLivingEntityRender(RenderLivingEvent.Post<?, ?> event) {
		LivingEntity entity = event.getEntity();
		if (EFFECT_MAP.containsKey(entity.getUUID())) {
			Map<MobEffect, Integer> map = EFFECT_MAP.get(entity.getUUID());
			for (Map.Entry<MobEffect, Integer> entry : map.entrySet()) {
				if (entry.getKey() instanceof ClientRenderEffect effect) {
					int lv = entry.getValue();
					effect.render(entity, lv, rl -> ICONS.add(new DelayedEntityRender(entity, rl)));
				}
			}
		}
	}

	private static void renderIcon(LivingEntity entity, PoseStack matrix, MultiBufferSource buffer, ResourceLocation rl,
								   float partial, Camera camera, EntityRenderDispatcher dispatcher) {
		float f = entity.getBbHeight() / 2;

		double x0 = Mth.lerp(partial, entity.xOld, entity.getX());
		double y0 = Mth.lerp(partial, entity.yOld, entity.getY());
		double z0 = Mth.lerp(partial, entity.zOld, entity.getZ());
		Vec3 offset = dispatcher.getRenderer(entity).getRenderOffset(entity, partial);
		Vec3 cam_pos = camera.getPosition();
		double d2 = x0 - cam_pos.x + offset.x();
		double d3 = y0 - cam_pos.y + offset.y();
		double d0 = z0 - cam_pos.z + offset.z();

		matrix.pushPose();
		matrix.translate(d2, d3 + f, d0);
		matrix.mulPose(camera.rotation());
		PoseStack.Pose entry = matrix.last();
		VertexConsumer ivertexbuilder = buffer.getBuffer(get2DIcon(rl));
		iconVertex(entry, ivertexbuilder, 0.5f, -0.5f, 0, 1);
		iconVertex(entry, ivertexbuilder, -0.5f, -0.5f, 1, 1);
		iconVertex(entry, ivertexbuilder, -0.5f, 0.5f, 1, 0);
		iconVertex(entry, ivertexbuilder, 0.5f, 0.5f, 0, 0);
		matrix.popPose();
	}

	private static void iconVertex(PoseStack.Pose entry, VertexConsumer builder, float x, float y, float u, float v) {
		builder.vertex(entry.pose(), x, y, 0)
				.uv(u, v)
				.normal(entry.normal(), 0.0F, 1.0F, 0.0F)
				.endVertex();
	}

	public static RenderType get2DIcon(ResourceLocation rl) {
		return RenderType.create(
				"entity_body_icon",
				DefaultVertexFormat.POSITION_TEX,
				VertexFormat.Mode.QUADS, 256, false, true,
				RenderType.CompositeState.builder()
						.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_SHADER)
						.setTextureState(new RenderStateShard.TextureStateShard(rl, false, false))
						.setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
						.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
						.createCompositeState(false)
		);
	}

	public static void sync(EffectToClient eff) {
		Map<MobEffect, Integer> set = EFFECT_MAP.get(eff.entity);
		if (eff.exist) {
			if (set == null) {
				EFFECT_MAP.put(eff.entity, set = new HashMap<>());
			}
			set.put(eff.effect, eff.level);
		} else if (set != null) {
			set.remove(eff.effect);
		}
	}

	private record DelayedEntityRender(LivingEntity entity, ResourceLocation rl) {

	}
}
