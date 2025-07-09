package net.PaiPain.arcanus.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class BloodMoonItem extends SwordItem {

    public static final Tier BLOOD_MOON_TIER = new Tier() {
        @Override
        public int getUses() {
            return 1000;
        }

        @Override
        public float getSpeed() {
            return 6.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 1.0F;
        }

        @Override
        public int getLevel() {
            return 3;
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    };

    public BloodMoonItem() {
        super(BLOOD_MOON_TIER, 1, -2.4F, new Item.Properties().stacksTo(1).fireResistant());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = attacker.level();
        boolean isNight = !level.isDay();

        if (isNight) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 120, 0));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 1));
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (selected && entity instanceof Player player) {
            boolean isNight = !level.isDay();

            if (!level.isClientSide) {
                if (isNight) {
                    if (!player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0, true, false));
                    }
                    if (!player.hasEffect(MobEffects.DAMAGE_BOOST)) {
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 5, true, false));
                    }
                } else {
                    // DURANTE O DIA: aplica fraqueza
                    if (!player.hasEffect(MobEffects.WEAKNESS)) {
                        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, true, false));
                    }
                }
            }

            double x = player.getX();
            double y = player.getY() + 1.7;
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
        tooltip.add(Component.literal("§7- Applies Wither & Slow (6s)"));
        tooltip.add(Component.literal("§7- Grants Speed I & Strength IV"));
        tooltip.add(Component.literal("§7During the day:"));
        tooltip.add(Component.literal("§7- Deals only 1 damage"));
        tooltip.add(Component.literal("§7- Applies Weakness II"));
    }
}
