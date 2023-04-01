package dev.xkmc.l2library.init.materials.source;

import dev.xkmc.l2library.init.events.attack.AttackListener;
import dev.xkmc.l2library.init.events.attack.CreateSourceEvent;
import dev.xkmc.l2library.init.events.damage.DefaultDamageState;
import dev.xkmc.l2library.init.materials.generic.ExtraToolConfig;
import dev.xkmc.l2library.init.materials.generic.GenericTieredItem;

public class GenericMaterialAttackListener implements AttackListener {

	@Override
	public void onCreateSource(CreateSourceEvent event) {
		if (event.getAttacker().getMainHandItem().getItem() instanceof GenericTieredItem gen) {
			if (event.getRegistry().getHolderOrThrow(event.getOriginal()).is(MaterialDamageTypeMultiplex.MATERIAL_MUX)) {
				ExtraToolConfig config = gen.getExtraConfig();
				if (config.bypassMagic) event.enable(DefaultDamageState.BYPASS_MAGIC);
				if (config.bypassArmor) event.enable(DefaultDamageState.BYPASS_ARMOR);
			}
		}
	}

}
