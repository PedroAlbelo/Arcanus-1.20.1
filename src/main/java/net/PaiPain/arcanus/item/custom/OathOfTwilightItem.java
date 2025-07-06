package net.PaiPain.arcanus.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class OathOfTwilightItem extends SwordItem {

    public OathOfTwilightItem() {
        super(Tiers.NETHERITE, 3, -2.4F, new Item.Properties());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = target.level();

        // Arremessa o inimigo para cima
        target.setDeltaMovement(target.getDeltaMovement().x, 1.1D, target.getDeltaMovement().z);
        target.hasImpulse = true;

        // Partículas mágicas (visível apenas em clientes)
        if (!level.isClientSide()) return super.hurtEnemy(stack, target, attacker);

        for (int i = 0; i < 15; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 0.5;
            double offsetY = level.random.nextDouble() * 1.0;
            double offsetZ = (level.random.nextDouble() - 0.5) * 0.5;

            level.addParticle(
                    ParticleTypes.END_ROD,
                    target.getX() + offsetX,
                    target.getY() + offsetY,
                    target.getZ() + offsetZ,
                    0, 0.05, 0
            );
        }

        return super.hurtEnemy(stack, target, attacker);
    }
}
