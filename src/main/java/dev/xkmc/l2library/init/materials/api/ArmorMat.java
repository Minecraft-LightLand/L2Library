package dev.xkmc.l2library.init.materials.api;

import com.google.common.base.Suppliers;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class ArmorMat implements ArmorMaterial {

	private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
	private final String name;
	private final int durabilityMultiplier;
	private final int[] slotProtections;
	private final int enchantmentValue;
	private final SoundEvent sound;
	private final float toughness;
	private final float knockbackResistance;
	private final Supplier<Ingredient> repairIngredient;

	public ArmorMat(String name, int durability, int[] protection, int enchant, SoundEvent sound, float tough, float kb, Supplier<Ingredient> repair) {
		this.name = name;
		this.durabilityMultiplier = durability;
		this.slotProtections = protection;
		this.enchantmentValue = enchant;
		this.sound = sound;
		this.toughness = tough;
		this.knockbackResistance = kb;
		this.repairIngredient = Suppliers.memoize(repair::get);
	}

	public int getDurabilityForType(ArmorItem.Type type) {
		return HEALTH_PER_SLOT[type.getSlot().getIndex()] * this.durabilityMultiplier;
	}

	public int getDefenseForType(ArmorItem.Type type) {
		return this.slotProtections[type.getSlot().getIndex()];
	}

	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	public SoundEvent getEquipSound() {
		return this.sound;
	}

	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}

	public String getName() {
		return this.name;
	}

	public float getToughness() {
		return this.toughness;
	}

	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}
}
