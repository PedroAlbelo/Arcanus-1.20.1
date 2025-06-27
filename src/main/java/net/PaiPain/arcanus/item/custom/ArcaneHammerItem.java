package net.PaiPain.arcanus.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ArcaneHammerItem extends Item {
    private static final Map<UUID, BlockPos> startPosMap = new HashMap<>();
    private static final Map<UUID, BlockPos> endPosMap = new HashMap<>();
    public static final Map<UUID, Set<BlockPos>> miningZones = new HashMap<>();
    private static final int MAX_BLOCKS = 50;

    public ArcaneHammerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        InteractionHand hand = context.getHand();

        if (player == null || level.isClientSide()) return InteractionResult.PASS;

        UUID playerId = player.getUUID();
        boolean isShift = player.isShiftKeyDown();

        if (isShift && hand == InteractionHand.MAIN_HAND) {
            startPosMap.put(playerId, clickedPos);
            player.sendSystemMessage(Component.literal("📍 Posição inicial marcada: " + formatPos(clickedPos)).withStyle(ChatFormatting.GOLD));
            return InteractionResult.SUCCESS;
        }

        if (!isShift && hand == InteractionHand.MAIN_HAND) {
            endPosMap.put(playerId, clickedPos);
            player.sendSystemMessage(Component.literal("📍 Posição final marcada: " + formatPos(clickedPos)).withStyle(ChatFormatting.AQUA));
            tryCreateZone(level, player, playerId);
            return InteractionResult.SUCCESS;
        }

        if (hand == InteractionHand.OFF_HAND) {
            startPosMap.remove(playerId);
            endPosMap.remove(playerId);
            miningZones.remove(playerId);
            player.sendSystemMessage(Component.literal("❌ Seleção de mineração apagada.").withStyle(ChatFormatting.RED));
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    private void tryCreateZone(Level level, Player player, UUID playerId) {
        if (!startPosMap.containsKey(playerId) || !endPosMap.containsKey(playerId)) {
            player.sendSystemMessage(Component.literal("⚠️ Defina as duas posições primeiro.").withStyle(ChatFormatting.RED));
            return;
        }

        BlockPos start = startPosMap.get(playerId);
        BlockPos end = endPosMap.get(playerId);

        Set<BlockPos> blocks = new HashSet<>();
        int count = 0;
        for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
            if (++count > MAX_BLOCKS) {
                player.sendSystemMessage(Component.literal("🚫 Limite de " + MAX_BLOCKS + " blocos ultrapassado. Zona resetada.").withStyle(ChatFormatting.DARK_RED));
                startPosMap.remove(playerId);
                endPosMap.remove(playerId);
                return;
            }
            blocks.add(pos.immutable());
            spawnMarkerParticle((ServerLevel) level, pos);
        }

        miningZones.put(playerId, blocks);
        player.sendSystemMessage(Component.literal("✅ Zona de mineração definida com " + blocks.size() + " blocos.").withStyle(ChatFormatting.GREEN));
        level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.7F, 1.3F);
    }

    private void spawnMarkerParticle(ServerLevel level, BlockPos pos) {
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5,
                3, 0.2, 0.2, 0.2, 0.0);
    }

    private String formatPos(BlockPos pos) {
        return "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
    }

    public static Set<BlockPos> getZoneForPlayer(UUID playerId) {
        return miningZones.getOrDefault(playerId, new HashSet<>());
    }
}