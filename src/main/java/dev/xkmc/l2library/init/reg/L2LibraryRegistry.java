package dev.xkmc.l2library.init.reg;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.reg.simple.*;
import dev.xkmc.l2library.serial.conditions.*;
import dev.xkmc.l2library.serial.ingredients.EnchantmentIngredient;
import dev.xkmc.l2library.serial.ingredients.MobEffectIngredient;
import dev.xkmc.l2library.serial.ingredients.PotionIngredient;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class L2LibraryRegistry {

	public static final Reg REG = new Reg(L2Library.MODID);

	public static final IngReg INGREDIENT = REG.ingredient();
	public static final IngVal<EnchantmentIngredient> ING_ENCH = INGREDIENT.reg("enchantment", EnchantmentIngredient.class);
	public static final IngVal<PotionIngredient> ING_POTION = INGREDIENT.reg("potion", PotionIngredient.class);
	public static final IngVal<MobEffectIngredient> ING_EFF = INGREDIENT.reg("mob_effect", MobEffectIngredient.class);


	public static final CdcReg<ICondition> CONDITION = REG.codec(NeoForgeRegistries.CONDITION_SERIALIZERS);
	public static final CdcVal<BooleanValueCondition> CONDITION_BOOL = CONDITION.reg("bool_config", BooleanValueCondition.class);
	public static final CdcVal<IntValueCondition> CONDITION_INT = CONDITION.reg("int_config", IntValueCondition.class);
	public static final CdcVal<DoubleValueCondition> CONDITION_DOUBLE = CONDITION.reg("double_config", DoubleValueCondition.class);
	public static final CdcVal<StringValueCondition> CONDITION_STR = CONDITION.reg("string_config", StringValueCondition.class);
	public static final CdcVal<ListStringValueCondition> CONDITION_LIST_STR = CONDITION.reg("string_list_config", ListStringValueCondition.class);

	static {
		REG.simple(NeoForgeRegistries.ATTACHMENT_TYPES);
	}

	public static void register(){

	}

}
