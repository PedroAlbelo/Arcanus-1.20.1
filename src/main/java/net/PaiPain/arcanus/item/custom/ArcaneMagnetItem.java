package net.PaiPain.arcanus.item.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;

public class ArcaneMagnetItem extends Item {

    public ArcaneMagnetItem(Properties properties) {
        super(properties.durability(128)); // Define durabilidade inicial
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (level.isClientSide()) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        ItemStack heldItem = target.getMainHandItem();

        if (!heldItem.isEmpty()) {
            // Remove o item da entidade
            target.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

            // Dropa o item no chão
            ItemEntity dropped = new ItemEntity(level, target.getX(), target.getY() + 1.0, target.getZ(), heldItem.copy());
            level.addFreshEntity(dropped);

            // Perde 1 de durabilidade
            stack.hurtAndBreak(1, player, (p) -> {
                p.broadcastBreakEvent(hand);
            });

            // Gera partículas de encantamento ao redor da entidade
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT, target.getX(), target.getY() + 1.0, target.getZ(),
                        20, 0.5, 0.5, 0.5, 0.1);
            }


            // Animação de swing
            player.swing(hand, true);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
