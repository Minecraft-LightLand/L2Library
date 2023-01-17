package dev.xkmc.l2library.util.math;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RenderUtils {

	public static void renderItemAbove(ItemStack stack, double height, Level level, float partial, PoseStack matrix, MultiBufferSource buffer, int light, int overlay) {
		float time = Math.floorMod(level.getGameTime(), 80L) + partial;
		if (!stack.isEmpty()) {
			matrix.pushPose();
			double offset = (Math.sin(time * 2 * Math.PI / 40.0) - 3) / 16;
			matrix.translate(0.5, height + offset, 0.5);
			matrix.mulPose(Axis.YP.rotationDegrees(time * 4.5f));
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, light,
					overlay, matrix, buffer, 0);
			matrix.popPose();
		}
	}

}
