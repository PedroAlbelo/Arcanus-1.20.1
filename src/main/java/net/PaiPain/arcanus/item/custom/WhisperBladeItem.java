package net.PaiPain.arcanus.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

public class WhisperBladeItem extends SwordItem {

    public WhisperBladeItem() {
        super(Tiers.NETHERITE, 3, -2.2f, new Item.Properties()); // dano base: 3, velocidade de ataque reduzida
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            // Aplica efeitos ao inimigo
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0)); // 2s Slow
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0)); // 2s Cegueira

            // Cura o jogador (lifesteal de 50%)
            float damage = 4.0f; // valor fixo ou personalizado
            player.heal(damage * 0.5f);

            // Gasta durabilidade
            stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(attacker.getUsedItemHand()));
        }

        return true;
    }
    }
