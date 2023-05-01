package dev.xkmc.l2library.init;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.base.effects.EffectToClient;
import dev.xkmc.l2library.base.ingredients.EnchantmentIngredient;
import dev.xkmc.l2library.base.ingredients.MobEffectIngredient;
import dev.xkmc.l2library.base.ingredients.PotionIngredient;
import dev.xkmc.l2library.base.overlay.select.SetSelectedToServer;
import dev.xkmc.l2library.base.tabs.contents.AttributeEntry;
import dev.xkmc.l2library.capability.conditionals.ConditionalData;
import dev.xkmc.l2library.capability.player.PlayerCapToClient;
import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.init.data.L2ConfigManager;
import dev.xkmc.l2library.init.events.attack.AttackEventHandler;
import dev.xkmc.l2library.init.events.click.SlotClickToServer;
import dev.xkmc.l2library.init.events.damage.DamageTypeRoot;
import dev.xkmc.l2library.init.events.listeners.GeneralAttackListener;
import dev.xkmc.l2library.init.events.listeners.GeneralEventHandler;
import dev.xkmc.l2library.init.materials.source.MaterialDamageTypeMultiplex;
import dev.xkmc.l2library.serial.handler.Handlers;
import dev.xkmc.l2library.serial.network.PacketHandler;
import dev.xkmc.l2library.serial.network.PacketHandlerWithConfig;
import dev.xkmc.l2library.serial.network.SyncPacket;
import dev.xkmc.l2library.util.raytrace.TargetSetPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(L2Library.MODID)
@Mod.EventBusSubscriber(modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class L2Library {

	public static final String MODID = "l2library";
	public static final Logger LOGGER = LogManager.getLogger();

	public static final PacketHandlerWithConfig PACKET_HANDLER = new PacketHandlerWithConfig(new ResourceLocation(MODID, "main"), 1,
			"l2library_config",
			e -> e.create(SyncPacket.class, PLAY_TO_CLIENT),
			e -> e.create(EffectToClient.class, PLAY_TO_CLIENT),
			e -> e.create(PlayerCapToClient.class, PLAY_TO_CLIENT),
			e -> e.create(TargetSetPacket.class, PLAY_TO_SERVER),
			e -> e.create(SlotClickToServer.class, PLAY_TO_SERVER),
			e -> e.create(SetSelectedToServer.class, PLAY_TO_SERVER));

	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);
	public static final RegistryEntry<Attribute> CRIT_RATE = REGISTRATE.simple("crit_rate", ForgeRegistries.ATTRIBUTES.getRegistryKey(), () -> new RangedAttribute("attribute.name.crit_rate", 0, 0, 1).setSyncable(true));
	public static final RegistryEntry<Attribute> CRIT_DMG = REGISTRATE.simple("crit_damage", ForgeRegistries.ATTRIBUTES.getRegistryKey(), () -> new RangedAttribute("attribute.name.crit_damage", 0.5, 0, 1000).setSyncable(true));
	public static final RegistryEntry<Attribute> BOW_STRENGTH = REGISTRATE.simple("bow_strength", ForgeRegistries.ATTRIBUTES.getRegistryKey(), () -> new RangedAttribute("attribute.name.bow_strength", 1, 0, 1000).setSyncable(true));

	public L2Library() {
		Handlers.register();
		FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
		IEventBus bus = ctx.getModEventBus();
		MinecraftForge.EVENT_BUS.register(GeneralEventHandler.class);
		bus.register(L2Library.class);
		bus.addListener(PacketHandler::setup);
		L2LibraryConfig.init();
		L2ConfigManager.register();;
		ConditionalData.register();
		MaterialDamageTypeMultiplex.register();
		REGISTRATE.addDataGenerator(ProviderType.LANG, LangData::genLang);
	}

	@SubscribeEvent
	public static void registerCaps(RegisterCapabilitiesEvent event) {
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			event.register(holder.cls);
		}
	}

	@SubscribeEvent
	public static void modifyAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, CRIT_RATE.get());
		event.add(EntityType.PLAYER, CRIT_DMG.get());
		event.add(EntityType.PLAYER, BOW_STRENGTH.get());
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		MaterialDamageTypeMultiplex.gatherData(event);
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event) {
		DamageTypeRoot.generateAll();
		event.enqueueWork(() -> {
			AttackEventHandler.LISTENERS.put(0, new GeneralAttackListener());
			AttributeEntry.add(() -> Attributes.MAX_HEALTH, false, 1000);
			AttributeEntry.add(() -> Attributes.ARMOR, false, 2000);
			AttributeEntry.add(() -> Attributes.ARMOR_TOUGHNESS, false, 3000);
			AttributeEntry.add(() -> Attributes.KNOCKBACK_RESISTANCE, false, 4000);
			AttributeEntry.add(() -> Attributes.MOVEMENT_SPEED, false, 5000);
			AttributeEntry.add(() -> Attributes.ATTACK_DAMAGE, false, 6000);
			AttributeEntry.add(() -> Attributes.ATTACK_SPEED, false, 7000);
			AttributeEntry.add(ForgeMod.REACH_DISTANCE, false, 8000);
			AttributeEntry.add(ForgeMod.ATTACK_RANGE, false, 9000);
			AttributeEntry.add(() -> Attributes.LUCK, false, 10000);
			AttributeEntry.add(CRIT_RATE, true, 11000);
			AttributeEntry.add(CRIT_DMG, true, 12000);
			AttributeEntry.add(BOW_STRENGTH, true, 12000);
		});
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(RegisterEvent event) {
		if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
			CraftingHelper.register(EnchantmentIngredient.INSTANCE.id(), EnchantmentIngredient.INSTANCE);
			CraftingHelper.register(PotionIngredient.INSTANCE.id(), PotionIngredient.INSTANCE);
			CraftingHelper.register(MobEffectIngredient.INSTANCE.id(), MobEffectIngredient.INSTANCE);
		}
	}

}
