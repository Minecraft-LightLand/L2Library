package dev.xkmc.l2library.serial.stack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Optional;

public class EnchantmentStackCodec extends JsonStackCodec {

	@Override
	public Optional<ItemStack> deserialize(JsonElement elem) {
		if (elem.isJsonObject() && elem.getAsJsonObject().has("enchanted_book")) {
			JsonObject book = elem.getAsJsonObject().getAsJsonObject("enchanted_book");
			Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(book.get("id").getAsString()));
			int lvl = book.get("lvl").getAsInt();
			assert ench != null;
			return Optional.of(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, lvl)));
		}
		return Optional.empty();
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public Optional<JsonElement> serialize(ItemStack stack) {
		if (stack.getItem() == Items.ENCHANTED_BOOK) {
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
			if (map.size() == 1) {
				var entry = map.entrySet().stream().findFirst().get();
				JsonObject obj = new JsonObject();
				obj.addProperty("id", ForgeRegistries.ENCHANTMENTS.getKey(entry.getKey()).toString());
				obj.addProperty("lvl", entry.getValue());
				return Optional.of(obj);
			}
		}
		return Optional.empty();
	}
}
