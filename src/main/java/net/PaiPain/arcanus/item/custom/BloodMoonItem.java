package net.PaiPain.arcanus.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class BloodMoonItem extends SwordItem {

    public BloodMoonItem() {
        super(Tiers.NETHERITE, 8, -2.4F, new Item.Properties().stacksTo(1).fireResistant());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = attacker.level();
        boolean isNight = !level.isDay();

        if (isNight) {
            target.hurt(level.damageSources().playerAttack((Player) attacker), 20f);
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 120, 0)); // Wither por 6s
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 1)); // Slow II por 6s
        } else {
            target.hurt(level.damageSources().playerAttack((Player) attacker), 1f); // Dano reduzido de dia
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (selected && entity instanceof Player player) {
            boolean isNight = !level.isDay();

            if (!level.isClientSide) {
                // Velocidade durante a noite
                if (isNight && !player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0, true, false));
                }
                return;
            }

            // Partículas acima da espada (lado do cliente)
            double x = player.getX();
            double y = player.getY() + 1.7; // Acima da mão
            double z = player.getZ();

            for (int i = 0; i < 2; i++) {
                level.addParticle(ParticleTypes.ENCHANT,
                        x + (level.random.nextDouble() - 0.5) * 0.2,
                        y + (level.random.nextDouble() * 0.2),
                        z + (level.random.nextDouble() - 0.5) * 0.2,
                        0, 0, 0);
            }

            if (isNight) {
                for (int i = 0; i < 2; i++) {
                    level.addParticle(ParticleTypes.FLAME,
                            x + (level.random.nextDouble() - 0.5) * 0.2,
                            y + 0.2 + (level.random.nextDouble() * 0.2),
                            z + (level.random.nextDouble() - 0.5) * 0.2,
                            0.02, 0.02, 0.02);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§cForged under a crimson eclipse."));
        tooltip.add(Component.literal("§7At night:"));
        tooltip.add(Component.literal("§7- Deals 20 damage"));
        tooltip.add(Component.literal("§7- Applies Wither & Slow (6s)"));
        tooltip.add(Component.literal("§7- Grants Speed I to wielder"));
        tooltip.add(Component.literal("§8During the day: Deals only 1 damage."));
    }
}
