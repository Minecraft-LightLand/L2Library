package dev.xkmc.l2library.init.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import dev.xkmc.l2library.base.effects.ClientEffectCap;
import dev.xkmc.l2library.base.effects.EffectToClient;
import dev.xkmc.l2library.base.effects.api.ClientRenderEffect;
import dev.xkmc.l2library.base.effects.api.DelayedEntityRender;
import dev.xkmc.l2library.base.effects.api.FirstPlayerRenderEffect;
import dev.xkmc.l2library.base.effects.api.IconRenderRegion;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.L2LibraryConfig;
import dev.xkmc.l2library.init.L2LibraryConfig.IconRenderMode;
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
import net.minecraft.world.entity.Entity;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEffectRenderEvents {

	private static final ArrayList<DelayedEntityRender> ICONS = new ArrayList<>();
	private static final Map<ResourceLocation, RenderType> DEFAULT_RENDER_CACHE = new HashMap<>();

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		AbstractClientPlayer player = Proxy.getClientPlayer();
		if (player != null) {
			for (Map.Entry<MobEffect, MobEffectInstance> entry : player.getActiveEffectsMap().entrySet()) {
				if (entry.getKey() instanceof FirstPlayerRenderEffect effect) {
					effect.onClientLevelRender(player, entry.getValue());
				}
			}
		}
	}

	@SubscribeEvent
	public static void levelRenderLast(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;
		if (ICONS.isEmpty()) return;
		IconRenderMode mode = L2LibraryConfig.CLIENT.iconRenderMode.get();
		if (mode == IconRenderMode.OFF) {
			ICONS.clear();
			return;
		}
		LevelRenderer renderer = event.getLevelRenderer();
		MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		PoseStack stack = event.getPoseStack();

		// cache the previous handler
		PoseStack posestack = RenderSystem.getModelViewStack();
		var last = posestack.last();
		posestack.popPose();
		RenderSystem.applyModelViewMatrix();

		if (mode == IconRenderMode.DEFAULT) {
			RenderSystem.disableDepthTest();
			for (DelayedEntityRender icon : ICONS) {
				renderIconDefault(stack, buffers, icon, event.getPartialTick(), camera, renderer.entityRenderDispatcher);
			}
			buffers.endBatch();
			RenderSystem.enableDepthTest();
		} else {
			renderIcons(mode, stack, buffers, event.getPartialTick(), camera, renderer.entityRenderDispatcher);
			buffers.endBatch();
		}

		// restore the previous handler
		posestack.pushPose();
		posestack.setIdentity();
		posestack.last().pose().mul(last.pose());
		posestack.last().normal().mul(last.normal());
		RenderSystem.applyModelViewMatrix();

		ICONS.clear();
	}

	private static void renderIcons(IconRenderMode mode, PoseStack stack, MultiBufferSource.BufferSource buffers,
	                                float partial, Camera camera, EntityRenderDispatcher dispatcher) {
		Map<UUID, Integer> counts = new HashMap<>();
		for (DelayedEntityRender icon : ICONS) {
			counts.merge(icon.entity().getUUID(), 1, Integer::sum);
		}
		BiConsumer<DelayedEntityRender, IndexInfo> iconRenderer;
		boolean depthTestDisabled = false;
		switch (mode) {
			case ORBIT:
				iconRenderer = (icon, info) -> renderIconOrbit(stack, buffers, icon, partial, camera, dispatcher, info.index, info.total);
				break;
			case GROUND:
				iconRenderer = (icon, info) -> renderIconGround(stack, buffers, icon, partial, camera, dispatcher, info.index, info.total);
				depthTestDisabled = true;
				break;
			case OVERHEAD:
				iconRenderer = (icon, info) -> renderIconOverhead(stack, buffers, icon, partial, camera, dispatcher, info.index, info.total);
				break;
			case HALO:
				iconRenderer = (icon, info) -> renderIconHalo(stack, buffers, icon, partial, camera, dispatcher, info.index, info.total);
				break;
			default:
				return;
		}
		if (depthTestDisabled) RenderSystem.disableDepthTest();
		Map<UUID, Integer> currentIdx = new HashMap<>();
		for (DelayedEntityRender icon : ICONS) {
			UUID id = icon.entity().getUUID();
			int total = counts.getOrDefault(id, 1);
			int index = currentIdx.getOrDefault(id, 0);
			iconRenderer.accept(icon, new IndexInfo(index, total));
			currentIdx.put(id, index + 1);
		}
		if (depthTestDisabled) RenderSystem.enableDepthTest();
	}

	@SubscribeEvent
	public static void onLivingEntityRender(RenderLivingEvent.Post<?, ?> event) {
		onLivingRenderEvents(event.getEntity());
	}

	public static void onLivingRenderEvents(LivingEntity entity) {
		if (!ClientEffectCap.HOLDER.isProper(entity)) return;
		if (entity.getTags().contains("ClientOnly")) return;
		IconRenderMode mode = L2LibraryConfig.CLIENT.iconRenderMode.get();
		if (mode == IconRenderMode.OFF) return;
		ClientEffectCap cap = ClientEffectCap.HOLDER.get(entity);
		if (mode == IconRenderMode.DEFAULT) {
			collectIconsDefault(cap, entity);
		} else {
			collectIconsSimple(cap, entity);
		}
	}

	private static void collectIconsDefault(ClientEffectCap cap, LivingEntity entity) {
		List<Pair<ClientRenderEffect, Integer>> l0 = new ArrayList<>();
		for (Map.Entry<MobEffect, Integer> entry : cap.map.entrySet()) {
			if (entry.getKey() instanceof ClientRenderEffect effect) {
				l0.add(Pair.of(effect, entry.getValue()));
			}
		}
		if (l0.isEmpty()) return;
		List<DelayedEntityRender> icons = new ArrayList<>();
		for (var e : l0) {
			e.getFirst().render(entity, e.getSecond(), icons::add);
		}
		int n = icons.size();
		if (n == 0) return;
		int w = (int) Math.ceil(Math.sqrt(n));
		int h = (int) Math.ceil(n * 1d / w);
		for (int i = 0; i < icons.size(); i++) {
			DelayedEntityRender icon = icons.get(i);
			int iy = i / w, iw = Math.min(w, n - iy * w), ix = i - iy * w;
			ICONS.add(icon.resize(IconRenderRegion.of(w, ix, iy, iw, h)));
		}
	}

	private static void collectIconsSimple(ClientEffectCap cap, LivingEntity entity) {
		for (Map.Entry<MobEffect, Integer> entry : cap.map.entrySet()) {
			if (entry.getKey() instanceof ClientRenderEffect effect) {
				effect.render(entity, entry.getValue(), ICONS::add);
			}
		}
	}

	private static Vec3 getRenderPos(LivingEntity entity, float partial, Camera camera, EntityRenderDispatcher dispatcher) {
		double x0 = Mth.lerp(partial, entity.xOld, entity.getX());
		double y0 = Mth.lerp(partial, entity.yOld, entity.getY());
		double z0 = Mth.lerp(partial, entity.zOld, entity.getZ());
		Vec3 offset = dispatcher.getRenderer(entity).getRenderOffset(entity, partial);
		Vec3 camPos = camera.getPosition();
		return new Vec3(x0 - camPos.x + offset.x(), y0 - camPos.y + offset.y(), z0 - camPos.z + offset.z());
	}

	private static void renderIconQuad(PoseStack.Pose entry, VertexConsumer builder, DelayedEntityRender icon) {
		float u0 = icon.tx(), v0 = icon.ty(), u1 = icon.tx() + icon.tw(), v1 = icon.ty() + icon.th();
		float ix0 = -0.5f + icon.region().x(), ix1 = ix0 + icon.region().scale();
		float iy0 = -0.5f + icon.region().y(), iy1 = iy0 + icon.region().scale();
		iconVertex(entry, builder, ix1, iy0, u0, v1);
		iconVertex(entry, builder, ix0, iy0, u1, v1);
		iconVertex(entry, builder, ix0, iy1, u1, v0);
		iconVertex(entry, builder, ix1, iy1, u0, v0);
	}

	private static void renderIconDefault(PoseStack pose, MultiBufferSource buffer, DelayedEntityRender icon, float partial, Camera camera, EntityRenderDispatcher dispatcher) {
		LivingEntity entity = icon.entity();
		float f = entity.getBbHeight() / 2;
		Vec3 pos = getRenderPos(entity, partial, camera, dispatcher);

		float bbWidth = entity.getBbWidth();
		float bbHeight = entity.getBbHeight();
		float effectiveSize = Math.min(bbWidth, bbHeight);
		float bodyScale = 0.1f + effectiveSize;

		float ix0 = -0.5f + icon.region().x(), ix1 = ix0 + icon.region().scale();
		float iy0 = -0.5f + icon.region().y(), iy1 = iy0 + icon.region().scale();

		pose.pushPose();
		pose.translate(pos.x, pos.y + f, pos.z);
		pose.mulPose(camera.rotation());
		pose.scale(bodyScale, bodyScale, bodyScale);

		PoseStack.Pose entry = pose.last();
		VertexConsumer builder = buffer.getBuffer(get2DIconDefault(icon.rl()));

		float u0 = icon.tx(), v0 = icon.ty(), u1 = icon.tx() + icon.tw(), v1 = icon.ty() + icon.th();

		iconVertex(entry, builder, ix1, iy0, u0, v1);
		iconVertex(entry, builder, ix0, iy0, u1, v1);
		iconVertex(entry, builder, ix0, iy1, u1, v0);
		iconVertex(entry, builder, ix1, iy1, u0, v0);
		pose.popPose();
	}

	private static void renderIconGround(PoseStack pose, MultiBufferSource buffer, DelayedEntityRender icon, float partial, Camera camera, EntityRenderDispatcher dispatcher, int index, int total) {
		LivingEntity entity = icon.entity();
		Vec3 pos = getRenderPos(entity, partial, camera, dispatcher);

		float bbWidth = entity.getBbWidth();
		float baseScale = 0.1f + (bbWidth * 1.5f);
		float countFactor = (total > 1) ? Math.max(0.25f, 1.0f - (total * 0.125f)) : 1.0f;
		float finalScale = baseScale * countFactor;

		pose.pushPose();
		pose.translate(pos.x, pos.y + 0.0001, pos.z);
		pose.mulPose(Axis.XP.rotationDegrees(-90));

		float speed = 2.5f;
		float timeAngle = (Minecraft.getInstance().level.getGameTime() + partial) * speed;
		float currentAngleDeg;

		if (total > 1) {
			currentAngleDeg = timeAngle + (index * (360.0f / total));
			float angleRad = currentAngleDeg * ((float) Math.PI / 180F);
			float radius = baseScale * (0.5f + total * 0.0025f);
			pose.translate((float) Math.cos(angleRad) * radius, (float) Math.sin(angleRad) * radius, 0);
		} else {
			currentAngleDeg = timeAngle;
		}

		pose.mulPose(Axis.ZP.rotationDegrees(currentAngleDeg - 90));

		float renderSize = icon.region().scale() * finalScale;
		pose.scale(renderSize, renderSize, renderSize);

		PoseStack.Pose entry = pose.last();
		VertexConsumer builder = buffer.getBuffer(get2DIconStandard(icon.rl()));
		renderIconQuad(entry, builder, icon);
		pose.popPose();
	}

	private static void renderIconOverhead(PoseStack pose, MultiBufferSource buffer, DelayedEntityRender icon, float partial, Camera camera, EntityRenderDispatcher dispatcher, int index, int total) {
		LivingEntity entity = icon.entity();
		Vec3 pos = getRenderPos(entity, partial, camera, dispatcher);

		float bbWidth = entity.getBbWidth();
		float bbHeight = entity.getBbHeight();
		float baseScale = 0.1f + (bbWidth * 0.25f) + (bbHeight * 0.25f);
		float countFactor = (total > 1) ? Math.max(0.5f, 1.0f - (total * 0.15f)) : 1.0f;
		float finalScale = baseScale * countFactor;

		pose.pushPose();
		pose.translate(pos.x, pos.y + bbHeight + 1, pos.z);
		pose.mulPose(camera.rotation());

		float spacing = finalScale * 1.2f;
		float xOffset = -total * spacing / 2 + spacing / 2 + index * spacing;
		pose.translate(xOffset, 0, 0);

		float renderSize = icon.region().scale() * finalScale;
		pose.scale(renderSize, renderSize, renderSize);

		PoseStack.Pose entry = pose.last();
		VertexConsumer builder = buffer.getBuffer(get2DIconStandard(icon.rl()));
		renderIconQuad(entry, builder, icon);
		pose.popPose();
	}

	private static void renderIconHalo(PoseStack pose, MultiBufferSource buffer, DelayedEntityRender icon, float partial, Camera camera, EntityRenderDispatcher dispatcher, int index, int total) {
		LivingEntity entity = icon.entity();
		Vec3 pos = getRenderPos(entity, partial, camera, dispatcher);

		float headYaw = entity.getViewYRot(partial);
		float bbWidth = entity.getBbWidth();
		float bbHeight = entity.getBbHeight();
		float baseScale = 0.1f + bbWidth + (bbHeight * 0.25f);
		float countFactor = (total > 1) ? Math.max(0.25f, 1.0f - (total * 0.125f)) : 1.0f;
		float finalScale = baseScale * countFactor;
		double backwardDist = 0.1 + (bbWidth * 0.7);
		float headRad = headYaw * ((float) Math.PI / 180F);

		pose.pushPose();
		pose.translate(pos.x + Math.sin(headRad) * backwardDist,
				pos.y + entity.getEyeHeight() + 0.4,
				pos.z - Math.cos(headRad) * backwardDist);
		pose.mulPose(Axis.YP.rotationDegrees(180.0F - headYaw));

		if (total > 1) {
			float speed = 2.5f;
			float timeAngle = (Minecraft.getInstance().level.getGameTime() + partial) * speed;
			float currentAngleDeg = timeAngle + (index * (360.0f / total));
			float angleRad = currentAngleDeg * ((float) Math.PI / 180F);
			float radius = baseScale * (0.5f + total * 0.0025f);
			pose.translate((float) Math.cos(angleRad) * radius, (float) Math.sin(angleRad) * radius, 0);
		}

		float renderSize = icon.region().scale() * finalScale;
		pose.scale(renderSize, renderSize, renderSize);

		PoseStack.Pose entry = pose.last();
		VertexConsumer builder = buffer.getBuffer(get2DIconStandard(icon.rl()));
		renderIconQuad(entry, builder, icon);
		pose.popPose();
	}

	private static void renderIconOrbit(PoseStack pose, MultiBufferSource buffer, DelayedEntityRender icon, float partial, Camera camera, EntityRenderDispatcher dispatcher, int index, int total) {
		LivingEntity entity = icon.entity();
		Vec3 pos = getRenderPos(entity, partial, camera, dispatcher);

		float bbWidth = entity.getBbWidth();
		float bbHeight = entity.getBbHeight();
		float baseScale = 0.5f + (bbWidth * 0.5f);
		float countFactor = (total > 1) ? Math.max(0.5f, 1.0f - (total * 0.05f)) : 1.0f;
		float finalScale = baseScale * countFactor;

		pose.pushPose();
		pose.translate(pos.x, pos.y + bbHeight * 0.5, pos.z);

		float speed = 2.5f;
		float timeAngle = (Minecraft.getInstance().level.getGameTime() + partial) * speed;
		float currentAngleDeg = timeAngle + (index * (360.0f / total));
		float angleRad = currentAngleDeg * ((float) Math.PI / 180F);
		float minRadius = bbWidth * 0.75f + finalScale * 0.6f;
		float dynamicRadius = baseScale * (0.75f + total * 0.025f);
		float radius = Math.max(minRadius, dynamicRadius);
		pose.translate((float) Math.cos(angleRad) * radius, 0, (float) Math.sin(angleRad) * radius);

		pose.mulPose(camera.rotation());

		float renderSize = icon.region().scale() * finalScale;
		pose.scale(renderSize, renderSize, renderSize);

		PoseStack.Pose entry = pose.last();
		VertexConsumer builder = buffer.getBuffer(get2DIconStandard(icon.rl()));
		renderIconQuad(entry, builder, icon);
		pose.popPose();
	}

	private static void iconVertex(PoseStack.Pose entry, VertexConsumer builder, float x, float y, float u, float v) {
		builder.vertex(entry.pose(), x, y, 0).uv(u, v).normal(entry.normal(), 0.0F, 1.0F, 0.0F).endVertex();
	}

	public static RenderType get2DIconDefault(ResourceLocation rl) {
		return RenderType.create("entity_body_icon",
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

	public static RenderType get2DIcon(ResourceLocation rl) {
		return get2DIconDefault(rl);
	}

	public static RenderType get2DIconStandard(ResourceLocation rl) {
		return RenderType.create("entity_body_icon", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, true,
				RenderType.CompositeState.builder()
						.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_SHADER)
						.setTextureState(new RenderStateShard.TextureStateShard(rl, false, false))
						.setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
						.setCullState(new RenderStateShard.CullStateShard(false))
						.setDepthTestState(new RenderStateShard.DepthTestStateShard("<=", 515))
						.createCompositeState(false));
	}

	public static void sync(EffectToClient eff) {
		if (Minecraft.getInstance().level == null) return;
		Entity e = Minecraft.getInstance().level.getEntity(eff.entity);
		if (e instanceof LivingEntity le && ClientEffectCap.HOLDER.isProper(le)) {
			ClientEffectCap cap = ClientEffectCap.HOLDER.get(le);
			if (eff.exist) cap.map.put(eff.effect, eff.level);
			else cap.map.remove(eff.effect);
		}
	}

	private record IndexInfo(int index, int total) {}
}
