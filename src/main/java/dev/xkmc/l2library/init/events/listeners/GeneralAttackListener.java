package dev.xkmc.l2library.init.events.listeners;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.data.L2DamageTypes;
import dev.xkmc.l2library.init.events.attack.AttackCache;
import dev.xkmc.l2library.init.events.attack.AttackListener;
import dev.xkmc.l2library.init.events.attack.CreateSourceEvent;
import dev.xkmc.l2library.init.events.attack.PlayerAttackCache;
import dev.xkmc.l2library.init.events.damage.DefaultDamageState;
import dev.xkmc.l2library.init.materials.generic.ExtraToolConfig;
import dev.xkmc.l2library.init.materials.generic.GenericTieredItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;

public class GeneralAttackListener implements AttackListener {

	@Override
	public boolean onCriticalHit(PlayerAttackCache cache, CriticalHitEvent event) {
		Player player = event.getEntity();
		double cr = player.getAttributeValue(L2Library.CRIT_RATE.get());
		double cd = player.getAttributeValue(L2Library.CRIT_DMG.get());
		if (event.isVanillaCritical()) {
			event.setDamageModifier((float) (event.getDamageModifier() + cd - 0.5));
		} else if (player.getRandom().nextDouble() < cr) {
			event.setDamageModifier((float) (event.getDamageModifier() + cd - 0.5));
			event.setResult(Event.Result.ALLOW);
			return true;
		}
		return false;
	}

	@Override
	public void onCreateSource(CreateSourceEvent event) {
		if (event.getAttacker().getMainHandItem().getItem() instanceof GenericTieredItem gen) {
			if (event.getRegistry().getHolderOrThrow(event.getOriginal()).is(L2DamageTypes.MATERIAL_MUX)) {
				ExtraToolConfig config = gen.getExtraConfig();
				if (config.bypassMagic) event.enable(DefaultDamageState.BYPASS_MAGIC);
				if (config.bypassArmor) event.enable(DefaultDamageState.BYPASS_ARMOR);
			}
		}
	}

	@Override
	public void onHurt(AttackCache cache, ItemStack weapon) {
		assert cache.getLivingHurtEvent() != null;
		if (cache.getLivingHurtEvent().getSource().getDirectEntity() == cache.getAttacker()) {
			if (weapon.getItem() instanceof GenericTieredItem item) {
				item.getExtraConfig().onDamage(cache, weapon);
			}
		}
	}

}
