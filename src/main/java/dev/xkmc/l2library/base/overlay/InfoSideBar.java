package dev.xkmc.l2library.base.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2library.init.L2LibraryConfig;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.List;

public abstract class InfoSideBar extends SideBar implements IGuiOverlay {

	public InfoSideBar(float duration, float ease) {
		super(duration, ease);
	}

	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
		if (!ease(gui.getGuiTicks() + partialTick))
			return;
		var text = getText();
		if (text.isEmpty()) return;
		int anchor = L2LibraryConfig.CLIENT.infoAnchor.get();
		int y = height * anchor / 2;
		int w = (int) (width * L2LibraryConfig.CLIENT.infoMaxWidth.get());
		var box = new TextBox(width, height, 0, anchor, getXOffset(width), y, w);
		box.renderLongText(gui, poseStack, text);
	}

	protected abstract List<Component> getText();

	@Override
	protected int getXOffset(int width) {
		float progress = (max_ease - ease_time) / max_ease;
		return -Math.round(progress * width / 2) + 8;
	}

}

