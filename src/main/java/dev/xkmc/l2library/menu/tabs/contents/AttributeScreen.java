package dev.xkmc.l2library.menu.tabs.contents;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2library.init.L2Client;
import dev.xkmc.l2library.menu.tabs.core.TabManager;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

public class AttributeScreen extends BaseTextScreen {

	public record AttributeEntry(Supplier<Attribute> sup, boolean usePercent) {
	}

	public static final List<AttributeEntry> LIST = new ArrayList<>();

	protected AttributeScreen(Component title) {
		super(title, new ResourceLocation("l2library:textures/gui/empty.png"));
	}

	@Override
	public void init() {
		super.init();
		new TabManager(this).init(this::addRenderableWidget, L2Client.TAB_ATTRIBUTE);
	}

	@Override
	public void render(PoseStack stack, int mx, int my, float ptick) {
		super.render(stack, mx, my, ptick);
		Player player = Proxy.getClientPlayer();
		int x = leftPos + 8;
		int y = topPos + 6;
		for (AttributeEntry entry : LIST) {
			double val = player.getAttributeValue(entry.sup().get());
			Component comp = new TranslatableComponent(
					"attribute.modifier.equals." + (entry.usePercent() ? 1 : 0),
					ATTRIBUTE_MODIFIER_FORMAT.format(entry.usePercent() ? val * 100 : val),
					new TranslatableComponent(entry.sup().get().getDescriptionId()));
			this.font.draw(stack, comp, x, y, 0);
			y += 10;
		}


	}
}
