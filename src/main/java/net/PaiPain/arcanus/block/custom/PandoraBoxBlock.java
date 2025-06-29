package net.PaiPain.arcanus.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PandoraBoxBlock extends Block {

    public PandoraBoxBlock() {
        super(BlockBehaviour.Properties.of().strength(2.0f).requiresCorrectToolForDrops());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            // Remover o bloco
            level.removeBlock(pos, false);

            // Soltar partículas de desintegração
            ((ServerLevel) level).sendParticles(ParticleTypes.EXPLOSION,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    20, 0.3, 0.3, 0.3, 0.05);

            // Escolher um evento aleatório
            RandomSource random = level.getRandom();
            int event = random.nextInt(5); // 0 a 4

            switch (event) {
                case 0 -> { // Explosão
                    level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 3.0f, Level.ExplosionInteraction.TNT);
                }
                case 1 -> { // 10 Creepers
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
                case 2 -> { // 10 Diamantes
                    ItemEntity drop = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(),
                            Items.DIAMOND.getDefaultInstance().copyWithCount(10));
                    level.addFreshEntity(drop);
                }
                case 3 -> { // 5 Couros
                    ItemEntity drop = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(),
                            Items.LEATHER.getDefaultInstance().copyWithCount(5));
                    level.addFreshEntity(drop);
                }
                case 4 -> { // Bloco de Diamante invocado
                    level.setBlock(pos, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }
}
