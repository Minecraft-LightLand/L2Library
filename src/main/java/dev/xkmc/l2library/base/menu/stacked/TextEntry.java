package dev.xkmc.l2library.base.menu.stacked;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;

public record TextEntry(PoseStack stack, Component text, int x, int y, int color) {
}
