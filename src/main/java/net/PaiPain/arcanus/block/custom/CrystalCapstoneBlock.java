package net.PaiPain.arcanus.block.custom;

import net.PaiPain.arcanus.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class CrystalCapstoneBlock extends Block {

    // Caixa de colisão e seleção personalizada (central menor que um bloco cheio)
    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 16, 12);

    public CrystalCapstoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockPos below = pos.below();
            BlockPos bottom = pos.below(2);

            BlockState middleState = level.getBlockState(below);
            BlockState baseState = level.getBlockState(bottom);

            boolean isStructure =
                    middleState.is(ModBlocks.SIGIL_COLUMN.get()) &&
                            baseState.is(ModBlocks.FOUNDATION_ARCANE.get());

            if (isStructure) {
                if (isStructure) {
                    // Remove os 3 blocos
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    level.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), 3);
                    level.setBlock(pos.below(2), Blocks.AIR.defaultBlockState(), 3);

                    // Coloca o bloco pilar
                    level.setBlock(pos.below(2), ModBlocks.ARCANE_PILLAR.get().defaultBlockState(), 3);

                    player.sendSystemMessage(Component.literal(" Pilar mágico invocado!"));
                    return InteractionResult.SUCCESS;
                }

                // Aqui você pode abrir uma HUD futuramente, ou ativar outra função
                return InteractionResult.SUCCESS;
            } else {
                player.sendSystemMessage(Component.literal(" Estrutura incompleta."));
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}
