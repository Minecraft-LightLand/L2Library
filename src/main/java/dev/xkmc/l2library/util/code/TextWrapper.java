package dev.xkmc.l2library.util.code;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class TextWrapper {

	public static List<FormattedCharSequence> wrapText(Font font, List<Component> list, int width) {
		int tooltipTextWidth = list.stream().mapToInt(font::width).max().orElse(0);
		boolean needsWrap = tooltipTextWidth > width;
		if (needsWrap) {
			return list.stream().flatMap(text -> font.split(text, width).stream()).toList();
		}
		return list.stream().map(Component::getVisualOrderText).toList();
	}

}
