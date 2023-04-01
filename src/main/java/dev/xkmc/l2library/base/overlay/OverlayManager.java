package dev.xkmc.l2library.base.overlay;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.codec.JsonCodec;
import dev.xkmc.l2library.util.code.Wrappers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SerialClass
public class OverlayManager {

	public static final Map<ResourceLocation, OverlayManager> MAP = new HashMap<>();

	public static OverlayManager get(String modid, String path) {
		OverlayManager old = MAP.get(new ResourceLocation(modid, path));
		if (old != null) return old;
		return new OverlayManager(modid, path);
	}

	private final String name;
	private final ResourceLocation coords, texture;
	@SerialClass.SerialField
	public HashMap<String, Rect> elements;
	public boolean loaded = false;

	private OverlayManager(String mod, String str) {
		name = mod + ":" + str;
		coords = new ResourceLocation(mod, str);
		texture = new ResourceLocation(mod, "textures/gui/widgets/" + str + ".png");
		check();
		MAP.put(new ResourceLocation(mod, str), this);
	}

	public void reset(JsonElement elem) {
		JsonCodec.from(elem.getAsJsonObject(), OverlayManager.class, this);
		loaded = true;
	}

	/**
	 * get the location of the component on the GUI
	 */
	public Rect getComp(String key) {
		check();
		return elements.getOrDefault(key, Rect.ZERO);
	}

	@OnlyIn(Dist.CLIENT)
	public ScreenRenderer getRenderer(ForgeGui gui, PoseStack stack) {
		check();
		return new ScreenRenderer(gui, stack);
	}

	@Override
	public String toString() {
		return name;
	}

	private void check() {
		if (!loaded)
			load();
	}

	private void load() {
		JsonObject jo = Wrappers.get(() -> GsonHelper.parse(new InputStreamReader(Minecraft.getInstance().getResourceManager().getResource(
				new ResourceLocation(coords.getNamespace(), "textures/gui/overlays/" + coords.getPath() + ".json")
		).get().open(), StandardCharsets.UTF_8)));
		JsonCodec.from(jo, OverlayManager.class, this);
		loaded = true;
	}

	@SerialClass
	public static class Rect {

		public static final Rect ZERO = new Rect();

		@SerialClass.SerialField
		public int sx, sy, tx, ty, w, h;

		public Rect() {
		}

	}

	@OnlyIn(Dist.CLIENT)
	public class ScreenRenderer {

		private final int x, y;

		public final ForgeGui gui;
		public final PoseStack stack;
		public final Entity cameraEntity;
		public final LocalPlayer localPlayer;

		public ScreenRenderer(ForgeGui gui, PoseStack stack) {
			this.gui = gui;
			this.stack = stack;
			this.x = gui.screenWidth / 2;
			this.y = gui.screenHeight;
			localPlayer = gui.minecraft.player;
			cameraEntity = gui.minecraft.getCameraEntity();
		}

		public void start() {
			gui.setupOverlayRenderState(true, false, texture);
		}

		public Rect get(String str) {
			return getComp(str);
		}

		/**
		 * Draw a side sprite on the location specified by the component
		 */
		public void draw(String c) {
			Rect cr = getComp(c);
			GuiComponent.blit(stack, x + cr.sx, y + cr.sy, cr.tx, cr.ty, cr.w, cr.h);
		}

		/**
		 * Draw a side sprite on the location specified by the component with an offset
		 */
		public void draw(String c, int dx, int dy) {
			Rect cr = getComp(c);
			GuiComponent.blit(stack, x + cr.sx + dx, y + cr.sy + dy, cr.tx, cr.ty, cr.w, cr.h);
		}

		/**
		 * Draw a side sprite on the location specified by the component with an offset on all variables
		 */
		public void blit(String c, int sx, int sy, int tx, int ty, int dw, int dh) {
			Rect cr = getComp(c);
			GuiComponent.blit(stack, x + cr.sx + sx, y + cr.sy + sy, cr.tx + tx, cr.ty + ty, cr.w + dw, cr.h + dh);
		}

		/**
		 * Draw a side sprite on the location specified by the component. Draw partially
		 * from bottom to top
		 */
		public void drawBottomUp(String c, int prog, int max) {
			if (prog == 0 || max == 0)
				return;
			Rect cr = getComp(c);
			int dh = cr.h * prog / max;
			GuiComponent.blit(stack, x + cr.sx, y + cr.sy + cr.h - dh, cr.tx, cr.ty + cr.h - dh, cr.w, dh);
		}

		/**
		 * Draw a side sprite on the location specified by the component. Draw partially
		 * from left to right
		 */
		public void drawLeftRight(String c, double prog, double max) {
			if (prog == 0 || max == 0)
				return;
			Rect cr = getComp(c);
			prog = Math.min(prog, max);
			int dw = (int) Math.floor(cr.w * prog / max);
			GuiComponent.blit(stack, x + cr.sx, y + cr.sy, cr.tx, cr.ty, dw, cr.h);
		}

		public void drawRightLeft(String c, double prog, double max) {
			if (prog == 0 || max == 0)
				return;
			Rect cr = getComp(c);
			prog = Math.min(prog, max);
			int dw = (int) Math.floor(cr.w * prog / max);
			GuiComponent.blit(stack, x + cr.sx + cr.w - dw, y + cr.sy, cr.tx + cr.w - dw, cr.ty, dw, cr.h);
		}

	}

}
