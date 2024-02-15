package dev.xkmc.l2library.serial.advancements;

import com.tterrag.registrate.providers.RegistrateAdvancementProvider;
import dev.xkmc.l2library.compat.patchouli.PatchouliHelper;
import dev.xkmc.l2library.init.reg.L2Registrate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AdvancementGenerator {

	private final RegistrateAdvancementProvider pvd;
	private final String modid;

	public AdvancementGenerator(RegistrateAdvancementProvider pvd, String modid) {
		this.pvd = pvd;
		this.modid = modid;
	}

	public class TabBuilder {

		private final String tab;
		private final ResourceLocation bg;

		private Entry root;

		public TabBuilder(String tab) {
			this.tab = tab;
			this.bg = new ResourceLocation(modid, "textures/gui/advancements/backgrounds/" + tab + ".png");
		}

		public void build() {
			root.build();
		}

		public Entry root(String id, Item item, CriterionBuilder builder, String title, String desc) {
			return root(id, item.getDefaultInstance(), builder, title, desc);
		}

		public Entry root(String id, ItemStack item, CriterionBuilder builder, String title, String desc) {
			if (root == null) {
				root = new Entry(new EntryData(id, item, builder, title, desc), null);
			}
			return root;
		}

		public Entry hidden(String id, CriterionBuilder builder) {
			return new Entry(new EntryData(id, ItemStack.EMPTY, builder, "", ""), null);
		}

		public class Entry {

			private final List<Entry> children = new ArrayList<>();
			private final EntryData data;
			private final ResourceLocation rl;
			private final Entry parent;

			private AdvancementType type = AdvancementType.TASK;
			private boolean showToast = true, announce = true, hidden = false;
			private Advancement result;

			private Entry(EntryData data, @Nullable Entry parent) {
				this.data = data;
				this.parent = parent;
				if (parent == null) {
					showToast = false;
					announce = false;
					rl = bg;
				} else {
					rl = null;
				}
			}

			public Entry create(String id, Item item, CriterionBuilder builder, String title, String desc) {
				return create(id, item.getDefaultInstance(), builder, title, desc);
			}

			public Entry create(String id, ItemStack item, CriterionBuilder builder, String title, String desc) {
				Entry sub = new Entry(new EntryData(id, item, builder, title, desc), this);
				children.add(sub);
				return sub;
			}

			public Entry patchouli(L2Registrate reg, CriterionBuilder builder, ResourceLocation book, String title, String desc) {
				ItemStack stack = PatchouliHelper.getBook(book);
				return create("patchouli", stack, builder, title, desc)
						.add(new ModLoadedAdv("patchouli"))
						.add(new RewardBuilder(reg, 0, book, () -> PatchouliHelper.getBookLoot(book)));
			}

			public Entry root() {
				return root;
			}

			public Entry enter() {
				return children.get(children.size() - 1);
			}

			public Entry type(AdvancementType type) {
				this.type = type;
				return this;
			}

			public Entry type(AdvancementType type, boolean showToast, boolean announce, boolean hidden) {
				this.type = type;
				this.showToast = showToast;
				this.announce = announce;
				this.hidden = hidden;
				return this;
			}

			public Entry add(IAdvBuilder builder) {
				data.builder.add(builder);
				return this;
			}

			public void build() {
				var builder = Advancement.Builder.advancement();
				if (!data.item.isEmpty()) {
					builder.display(data.item,
							pvd.title(modid, "advancements." + tab + "." + data.id, data.title),
							pvd.desc(modid, "advancements." + tab + "." + data.id, data.desc),
							rl, type, showToast, announce, hidden);
				}
				if (parent != null) {
					builder.parent(parent.result);
				}

				String uid = modid + ":" + tab + "/" + data.id;
				for (var e : data.builder) {
					e.modify(uid, builder);
					e.onBuild();
				}
				result = builder.save(r -> AdvProviderWrapper.save(pvd, data.builder, r), uid);
				for (Entry e : children) {
					e.build();
				}
			}

			public void finish() {
				TabBuilder.this.build();
			}

		}

	}

	private record EntryData(String id, ItemStack item, List<IAdvBuilder> builder, String title, String desc) {

		EntryData(String id, ItemStack item, IAdvBuilder builder, String title, String desc) {
			this(id, item, new ArrayList<>(List.of(builder)), title, desc);
		}

	}

}
