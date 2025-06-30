package net.PaiPain.arcanus.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction; // [INCREMENTADO]
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock; // [INCREMENTADO]
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition; // [INCREMENTADO]
import net.minecraft.world.level.block.state.properties.DirectionProperty; // [INCREMENTADO]
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.item.context.BlockPlaceContext; // [INCREMENTADO]
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PandoraBoxBlock extends HorizontalDirectionalBlock { // [INCREMENTADO]

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Block.box(5, 0, 5, 14, 10, 14);
    }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public PandoraBoxBlock(Properties copy) {
        super(BlockBehaviour.Properties.of().strength(2.0f).requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)); // [INCREMENTADO]
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) { // [INCREMENTADO]
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { // [INCREMENTADO]
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            // Remover o bloco
            level.removeBlock(pos, false);

            // Soltar partículas de desintegração
            ((ServerLevel) level).sendParticles(ParticleTypes.PORTAL,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    20, 0.3, 0.3, 0.3, 0.05);

            // Escolher um evento aleatório (agora até 7)
            RandomSource random = level.getRandom();
            int event = random.nextInt(8); // 0 a 7

            switch (event) {
                case 0 -> {
                    level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 3.0f, Level.ExplosionInteraction.TNT);
                }
                case 1 -> {
                    for (int i = 0; i < 10; i++) {
                        Creeper creeper = EntityType.CREEPER.create(level);
                        if (creeper != null) {
                            creeper.setPos(
                                    pos.getX() + random.nextDouble() * 2 - 1,
                                    pos.getY() + 1,
                                    pos.getZ() + random.nextDouble() * 2 - 1
                            );
                            level.addFreshEntity(creeper);
                        }
                    }
                }
                case 2 -> {
                    ItemEntity drop = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(),
                            Items.DIAMOND.getDefaultInstance().copyWithCount(10));
                    level.addFreshEntity(drop);
                }
                case 3 -> {
                    ItemEntity drop = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(),
                            Items.LEATHER.getDefaultInstance().copyWithCount(5));
                    level.addFreshEntity(drop);
                }
                case 4 -> {
                    level.setBlock(pos, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
                }
                case 5 -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 60, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 60, 1));
                }
                case 6 -> {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 45, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 45, 1));
                }
                case 7 -> {
                    double dx = pos.getX() + random.nextInt(41) - 20;
                    double dz = pos.getZ() + random.nextInt(41) - 20;
                    double dy = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos((int) dx, pos.getY(), (int) dz)).getY();
                    player.teleportTo(dx + 0.5, dy, dz + 0.5);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }
}
