package net.PaiPain.arcanus.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ArcaneDaggerItem extends SwordItem {

    public ArcaneDaggerItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Item.Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Aplica levitação por 9 segundos (9 * 20 ticks = 180 ticks)
        target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 180, 0)); // nível 1
        return super.hurtEnemy(stack, target, attacker);
    }
}
