// ========== ItemVoodoo.java ==========
package net.PaiPain.arcanus.item.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ItemVoodoo extends Item {
    public ItemVoodoo(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (!level.isClientSide && player != null) {
            if (player.isShiftKeyDown()) {
                ItemStack stack = context.getItemInHand();
                setLinkedPlayer(stack, player.getName().getString());
                player.displayClientMessage(Component.literal("Voodoo vinculado a si mesmo."), true);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, net.minecraft.world.entity.LivingEntity target, InteractionHand hand) {
        if (!player.level().isClientSide) {
            String name = target.getName().getString();
            setLinkedPlayer(stack, name);
            player.displayClientMessage(Component.literal("Voodoo vinculado a " + name), true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private void setLinkedPlayer(ItemStack stack, String playerName) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("LinkedPlayer", playerName);
    }

    public static String getLinkedPlayer(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("LinkedPlayer") ? tag.getString("LinkedPlayer") : null;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        String linked = getLinkedPlayer(stack);
        if (linked != null) {
            tooltip.add(Component.literal("Vinculado a: " + linked).withStyle(ChatFormatting.DARK_PURPLE));
        } else {
            tooltip.add(Component.literal("[Não vinculado]").withStyle(ChatFormatting.GRAY));
        }
    }
}
