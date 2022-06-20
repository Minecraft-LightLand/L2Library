package dev.xkmc.l2library.block;

import dev.xkmc.l2library.block.mult.*;
import dev.xkmc.l2library.block.one.*;
import dev.xkmc.l2library.block.type.BlockMethod;
import dev.xkmc.l2library.block.type.MultipleBlockMethod;
import dev.xkmc.l2library.block.type.SingletonBlockMethod;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation"})
public class DelegateBlockImpl extends DelegateBlock {

	private static final ThreadLocal<BlockImplementor> TEMP = new ThreadLocal<>();

	BlockImplementor impl;

	protected DelegateBlockImpl(DelegateBlockProperties p, BlockMethod... impl) {
		super(handler(construct(p).addImpls(impl)));
		registerDefaultState(this.impl.execute(DefaultStateBlockMethod.class).reduce(defaultBlockState(),
				(state, def) -> def.getDefaultState(state), (a, b) -> a));
	}

	public static BlockImplementor construct(DelegateBlockProperties bb) {
		return new BlockImplementor(bb.getProps());
	}

	private static Properties handler(BlockImplementor bi) {
		if (TEMP.get() != null)
			throw new RuntimeException("concurrency error");
		TEMP.set(bi);
		return bi.props;
	}

	@Override
	public final boolean isSignalSource(BlockState bs) {
		return impl.one(BlockPowerBlockMethod.class).isPresent();
	}

	@Override
	public final int getLightEmission(BlockState bs, BlockGetter w, BlockPos pos) {
		return impl.one(LightBlockMethod.class).map(e -> e.getLightValue(bs, w, pos))
				.orElse(super.getLightEmission(bs, w, pos));
	}

	@Override
	public final BlockState getStateForPlacement(BlockPlaceContext context) {
		return impl.execute(PlacementBlockMethod.class).reduce(defaultBlockState(),
				(state, impl) -> impl.getStateForPlacement(state, context), (a, b) -> a);
	}

	@Override
	public final int getSignal(BlockState bs, BlockGetter r, BlockPos pos, Direction d) {
		return impl.one(BlockPowerBlockMethod.class)
				.map(e -> e.getSignal(bs, r, pos, d))
				.orElse(0);
	}

	@Override
	public final BlockState mirror(BlockState state, Mirror mirrorIn) {
		return impl.one(MirrorRotateBlockMethod.class).map(e -> e.mirror(state, mirrorIn)).orElse(state);
	}

	@Override
	public final InteractionResult use(BlockState bs, Level w, BlockPos pos, Player pl, InteractionHand h, BlockHitResult r) {
		return impl.execute(OnClickBlockMethod.class)
				.map(e -> e.onClick(bs, w, pos, pl, h, r))
				.filter(e -> e != InteractionResult.PASS)
				.findFirst().orElse(InteractionResult.PASS);
	}

	@Override
	public final void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (impl.one(BlockEntityBlockMethod.class).isPresent() && state.getBlock() != newState.getBlock()) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if (tileentity != null) {
				if (tileentity instanceof Container) {
					Containers.dropContents(worldIn, pos, (Container) tileentity);
					worldIn.updateNeighbourForOutputSignal(pos, this);
				} else if (tileentity instanceof BlockContainer blockContainer) {
					for (Container c : blockContainer.getContainers())
						Containers.dropContents(worldIn, pos, c);
					worldIn.updateNeighbourForOutputSignal(pos, this);
				}
				worldIn.removeBlockEntity(pos);
			}
		}
		impl.execute(OnReplacedBlockMethod.class).forEach(e -> e.onReplaced(state, worldIn, pos, newState, isMoving));
	}

	@Override
	public final BlockState rotate(BlockState state, Rotation rot) {
		return impl.one(MirrorRotateBlockMethod.class).map(e -> e.rotate(state, rot)).orElse(state);
	}

	@Override
	protected final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		impl = TEMP.get();
		TEMP.set(null);
		impl.execute(CreateBlockStateBlockMethod.class).forEach(e -> e.createBlockStateDefinition(builder));
	}

	@Override
	public final void neighborChanged(BlockState state, Level world, BlockPos pos, Block nei_block, BlockPos nei_pos, boolean moving) {
		impl.execute(NeighborUpdateBlockMethod.class).forEach(e -> e.neighborChanged(this, state, world, pos, nei_block, nei_pos, moving));
		super.neighborChanged(state, world, pos, nei_block, nei_pos, moving);
	}

	@Override
	public final void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		impl.execute(RandomTickBlockMethod.class).forEach(e -> e.randomTick(state, world, pos, random));
	}

	@Override
	public final void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		impl.execute(ScheduleTickBlockMethod.class).forEach(e -> e.tick(state, world, pos, random));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public final void animateTick(BlockState state, Level world, BlockPos pos, RandomSource r) {
		impl.execute(AnimateTickBlockMethod.class).forEach(e -> e.animateTick(state, world, pos, r));
	}

	@Override
	public final void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		impl.one(EntityInsideBlockMethod.class).ifPresent(e -> e.entityInside(state, level, pos, entity));
	}

	@Override
	public final VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return impl.one(ShapeBlockMethod.class).map(e -> e.getCollisionShape(state, level, pos, ctx))
				.orElseGet(() -> super.getCollisionShape(state, level, pos, ctx));
	}

	@Override
	public final VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
		return impl.one(ShapeBlockMethod.class).map(e -> e.getBlockSupportShape(state, level, pos))
				.orElseGet(() -> super.getBlockSupportShape(state, level, pos));
	}

	@Override
	public final VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return impl.one(ShapeBlockMethod.class).map(e -> e.getVisualShape(state, level, pos, ctx))
				.orElseGet(() -> super.getVisualShape(state, level, pos, ctx));
	}

	@Override
	public final VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return impl.one(ShapeBlockMethod.class).map(e -> e.getShape(state, level, pos, ctx))
				.orElseGet(() -> super.getShape(state, level, pos, ctx));
	}

	@Override
	public final void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float height) {
		if (impl.execute(FallOnBlockMethod.class).reduce(true, (a, e) -> a & e.fallOn(level, state, pos, entity, height), (a, b) -> a & b)) {
			super.fallOn(level, state, pos, entity, height);
		}
	}

	@Override
	public final PushReaction getPistonPushReaction(BlockState state) {
		return impl.one(PushReactionBlockMethod.class).map(e -> e.getPistonPushReaction(state))
				.orElse(super.getPistonPushReaction(state));
	}

	@Override
	public final ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		return impl.one(GetBlockItemBlockMethod.class).map(e -> e.getCloneItemStack(world, pos, state))
				.orElse(super.getCloneItemStack(world, pos, state));
	}

	@Override
	public final List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		return impl.one(SpecialDropBlockMethod.class).map(e -> e.getDrops(state, builder))
				.orElse(super.getDrops(state, builder));
	}

	@Override
	public final void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		impl.execute(SetPlacedByBlockMethod.class).forEach(e -> e.setPlacedBy(level, pos, state, entity, stack));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public final RenderShape getRenderShape(BlockState state) {
		return impl.one(RenderShapeBlockMethod.class).map(e -> e.getRenderShape(state)).orElseGet(() -> super.getRenderShape(state));
	}

	@Override
	public final void attack(BlockState state, Level level, BlockPos pos, Player player) {
		impl.execute(AttackBlockMethod.class).filter(u -> u.attack(state, level, pos, player)).findFirst();
	}

	public final BlockImplementor getImpl() {
		return impl;
	}

	public static class BlockImplementor {

		private final BlockBehaviour.Properties props;
		private final List<MultipleBlockMethod> list = new ArrayList<>();
		private final HashMap<Class<?>, SingletonBlockMethod> map = new HashMap<>();

		public BlockImplementor(BlockBehaviour.Properties p) {
			props = p;
		}

		public BlockImplementor addImpls(BlockMethod... impls) {
			for (BlockMethod impl : impls) {
				if (impl instanceof MultipleBlockMethod)
					list.add((MultipleBlockMethod) impl);
				if (impl instanceof SingletonBlockMethod one) {
					List<Class<?>> list = new ArrayList<>();
					addOneImpl(one.getClass(), list);
					for (Class<?> cls : list) {
						if (map.containsKey(cls)) {
							throw new RuntimeException("class " + cls + " is implemented twice with " + map.get(cls) + " and " + impl);
						} else {
							map.put(cls, one);
						}
					}
				}
			}
			return this;
		}

		private void addOneImpl(Class<?> cls, List<Class<?>> list) {
			for (Class<?> ci : cls.getInterfaces()) {
				if (ci == SingletonBlockMethod.class) {
					throw new RuntimeException("class " + cls + " should not implement IOneImpl directly");
				}
				if (SingletonBlockMethod.class.isAssignableFrom(ci)) {
					Class<?>[] arr = ci.getInterfaces();
					if (arr.length == 1 && arr[0] == SingletonBlockMethod.class) {
						list.add(ci);
					} else {
						addOneImpl(ci, list);
					}
				}
			}
		}

		@SuppressWarnings("unchecked")
		public <T extends MultipleBlockMethod> Stream<T> execute(Class<T> cls) {
			return list.stream().filter(cls::isInstance).map(e -> (T) e);
		}

		@SuppressWarnings("unchecked")
		public <T extends SingletonBlockMethod> Optional<T> one(Class<T> cls) {
			return Optional.ofNullable((T) map.get(cls));
		}

	}

}
