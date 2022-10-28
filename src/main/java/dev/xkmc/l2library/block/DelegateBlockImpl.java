package dev.xkmc.l2library.block;

import dev.xkmc.l2library.block.mult.*;
import dev.xkmc.l2library.block.one.*;
import dev.xkmc.l2library.block.type.BlockMethod;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation"})
public class DelegateBlockImpl extends DelegateBlock {

	private static final ThreadLocal<BlockImplementor> TEMP = new ThreadLocal<>();

	BlockImplementor impl;

	protected DelegateBlockImpl(DelegateBlockProperties p, BlockMethod... impl) {
		super(handler(construct(p).addImpls(impl)));
		registerDefaultState(this.impl.reduce(DefaultStateBlockMethod.class, defaultBlockState(),
				(state, def) -> def.getDefaultState(state)));
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
	public final int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		return impl.one(AnalogOutputBlockMethod.class).map(e -> e.getAnalogOutputSignal(blockState, worldIn, pos)).orElse(0);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return impl.one(AnalogOutputBlockMethod.class).map(e -> e.hasAnalogOutputSignal(state)).orElse(false);
	}


	@Override
	public final int getLightEmission(BlockState bs, BlockGetter w, BlockPos pos) {
		return impl.one(LightBlockMethod.class).map(e -> e.getLightValue(bs, w, pos))
				.orElse(super.getLightEmission(bs, w, pos));
	}

	@Override
	public final BlockState getStateForPlacement(BlockPlaceContext context) {
		return impl.reduce(PlacementBlockMethod.class, defaultBlockState(),
				(state, impl) -> impl.getStateForPlacement(state, context));
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
		impl.forEach(OnReplacedBlockMethod.class, e -> e.onReplaced(state, worldIn, pos, newState, isMoving));
		if (impl.one(BlockEntityBlockMethod.class).isPresent() && state.getBlock() != newState.getBlock()) {
			BlockEntity entity = worldIn.getBlockEntity(pos);
			if (entity != null) {
				if (entity instanceof Container) {
					Containers.dropContents(worldIn, pos, (Container) entity);
					worldIn.updateNeighbourForOutputSignal(pos, this);
				} else if (entity instanceof BlockContainer blockContainer) {
					for (Container c : blockContainer.getContainers())
						Containers.dropContents(worldIn, pos, c);
					worldIn.updateNeighbourForOutputSignal(pos, this);
				}
				worldIn.removeBlockEntity(pos);
			}
		}
	}

	@Override
	public final BlockState rotate(BlockState state, Rotation rot) {
		return impl.one(MirrorRotateBlockMethod.class).map(e -> e.rotate(state, rot)).orElse(state);
	}

	@Override
	protected final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		impl = TEMP.get();
		TEMP.set(null);
		impl.forEach(CreateBlockStateBlockMethod.class, e -> e.createBlockStateDefinition(builder));
	}

	@Override
	public final void neighborChanged(BlockState state, Level world, BlockPos pos, Block nei_block, BlockPos nei_pos, boolean moving) {
		impl.forEach(NeighborUpdateBlockMethod.class, e -> e.neighborChanged(this, state, world, pos, nei_block, nei_pos, moving));
		super.neighborChanged(state, world, pos, nei_block, nei_pos, moving);
	}

	@Override
	public final void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		impl.forEach(RandomTickBlockMethod.class, e -> e.randomTick(state, world, pos, random));
	}

	@Override
	public final void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		impl.forEach(ScheduleTickBlockMethod.class, e -> e.tick(state, world, pos, random));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public final void animateTick(BlockState state, Level world, BlockPos pos, RandomSource r) {
		impl.forEach(AnimateTickBlockMethod.class, e -> e.animateTick(state, world, pos, r));
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
		if (impl.reduce(FallOnBlockMethod.class, true, (a, e) -> a & e.fallOn(level, state, pos, entity, height))) {
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
		impl.forEach(SetPlacedByBlockMethod.class, e -> e.setPlacedBy(level, pos, state, entity, stack));
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

	@Override
	public final BlockState updateShape(BlockState selfState, Direction from, BlockState sourceState, LevelAccessor level, BlockPos selfPos, BlockPos sourcePos) {
		return impl.reduce(ShapeUpdateBlockMethod.class, selfState, (currentState, e) -> e.updateShape(this, currentState, selfState, from, sourceState, level, selfPos, sourcePos));
	}

	public final BlockImplementor getImpl() {
		return impl;
	}

}
