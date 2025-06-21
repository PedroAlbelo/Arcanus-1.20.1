package net.PaiPain.arcanus.block.custom;

import net.PaiPain.arcanus.item.ModItems;
import net.PaiPain.arcanus.item.custom.ItemVoodoo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.*;

public class ArcanePillarBlock extends Block {

    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 16, 12);

    public ArcanePillarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide) return;
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.9;
        double z = pos.getZ() + 0.5;
        for (int i = 0; i < 2; i++) {
            double dx = (random.nextDouble() - 0.5) * 0.3;
            double dy = random.nextDouble() * 0.2;
            double dz = (random.nextDouble() - 0.5) * 0.3;
            level.addParticle(ParticleTypes.ENCHANT, x, y, z, dx, dy, dz);
        }
        AABB area = new AABB(pos).inflate(1.2);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
        if (!items.isEmpty()) {
            level.playLocalSound(x, y, z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.3f, 1.2f + random.nextFloat() * 0.3f, false);
        }
        for (ItemEntity entity : items) {
            double targetY = y + Math.sin((level.getGameTime() + entity.getId()) * 0.1) * 0.05;
            entity.setPickUpDelay(32767);
            entity.setDeltaMovement(0, 0, 0);
            entity.setNoGravity(true);
            entity.setPos(x, targetY, z);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        AABB area = new AABB(pos).inflate(1.5);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);

        if (player.isShiftKeyDown()) {
            for (ItemEntity entity : items) {
                entity.setPickUpDelay(0);
                entity.setPos(player.getX(), player.getY(), player.getZ());
            }
            player.sendSystemMessage(Component.literal("§7Itens devolvidos pelo altar."));
            return InteractionResult.SUCCESS;
        }

        Map<Item, Integer> itemCount = new HashMap<>();
        Map<Item, Integer> itensConsumidos = new HashMap<>();
        String targetName = null;

        for (ItemEntity entity : items) {
            ItemStack stack = entity.getItem();
            itemCount.put(stack.getItem(), itemCount.getOrDefault(stack.getItem(), 0) + stack.getCount());
            if (stack.getItem() == ModItems.VOODOO.get() && stack.hasTag()) {
                targetName = ItemVoodoo.getLinkedPlayer(stack);
            }
        }

        Player targetPlayer = null;
        Villager targetVillager = null;
        if (targetName != null) {
            for (Player p : level.players()) {
                if (p.getName().getString().equals(targetName)) {
                    targetPlayer = p;
                    break;
                }
            }
            if (targetPlayer == null) {
                for (Villager v : level.getEntitiesOfClass(Villager.class, new AABB(pos).inflate(64))) {
                    if (v.getName().getString().equals(targetName)) {
                        targetVillager = v;
                        break;
                    }
                }
            }
        }

        // ITENS
        Item VOODOO = ModItems.VOODOO.get();
        Item ESSENCE = ModItems.ARCANE_ESSENCE.get();
        Item ZOMBIE = net.minecraft.world.item.Items.ROTTEN_FLESH;
        Item GHAST = net.minecraft.world.item.Items.GHAST_TEAR;
        Item SUN = net.minecraft.world.item.Items.SUNFLOWER;
        Item CACTUS = net.minecraft.world.item.Items.CACTUS;
        Item FEATHER = net.minecraft.world.item.Items.FEATHER;
        Item FERMENTED = net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE;
        Item BONE = net.minecraft.world.item.Items.BONE;
        Item CHICKEN = net.minecraft.world.item.Items.CHICKEN;
        Item DIRT = net.minecraft.world.item.Items.DIRT;
        Item ENDER = net.minecraft.world.item.Items.ENDER_PEARL;

        boolean success = false;
        Component message = Component.literal("§7Nada aconteceu...");

        // 🌒 DEIXAR DE NOITE
        if (itemCount.getOrDefault(ZOMBIE, 0) >= 1 && itemCount.getOrDefault(ESSENCE, 0) >= 22) {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.setDayTime(15000);
            }
            itensConsumidos.put(ZOMBIE, 1);
            itensConsumidos.put(ESSENCE, 22);
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                    player.getAttribute(Attributes.MAX_HEALTH).getBaseValue() - 8
            );
            message = Component.literal("§7Ritual: a noite caiu.");
            success = true;
        }

        // 🌧 FAZER CHOVER
        else if (itemCount.getOrDefault(GHAST, 0) >= 1 && itemCount.getOrDefault(ESSENCE, 0) >= 22) {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.setWeatherParameters(0, 0, true, true); // força começar a chover
            }

            itensConsumidos.put(GHAST, 1);
            itensConsumidos.put(ESSENCE, 22);

            // perde 2 corações (4 de dano)
            player.hurt(player.damageSources().magic(), 4.0F);

            message = Component.literal("§7Ritual: a chuva começou.");
            success = true;
        }


        // ☀️ DEIXAR DE DIA
        else if (itemCount.getOrDefault(SUN, 0) >= 1 && itemCount.getOrDefault(ESSENCE, 0) >= 22) {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.setDayTime(1000);
            }
            itensConsumidos.put(SUN, 1);
            itensConsumidos.put(ESSENCE, 22);
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(player.getAttribute(Attributes.MAX_HEALTH).getBaseValue() - 4);
            message = Component.literal("§7Ritual: o dia nasceu.");
            success = true;
        }

        // 🌤 PARAR DE CHOVER
        else if (itemCount.getOrDefault(CACTUS, 0) >= 1 && itemCount.getOrDefault(ESSENCE, 0) >= 22) {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.setWeatherParameters(0, 0, false, false); // força parar chuva e trovão
            }

            itensConsumidos.put(CACTUS, 1);
            itensConsumidos.put(ESSENCE, 22);

            // perde 2 corações (4 de dano)
            player.hurt(player.damageSources().magic(), 4.0F);

            message = Component.literal("§7Ritual: a chuva cessou.");
            success = true;
        }




        // ⚰️ MATAR PLAYER INSTANTE
        else if (targetPlayer != null && itemCount.getOrDefault(VOODOO, 0) >= 1 && itemCount.getOrDefault(ESSENCE, 0) >= 64) {
            targetPlayer.kill();
            itensConsumidos.put(VOODOO, 1);
            itensConsumidos.put(ESSENCE, 64);
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(player.getAttribute(Attributes.MAX_HEALTH).getBaseValue() - 2);
            message = Component.literal("§4Ritual: " + targetName + " foi sacrificado.");
            success = true;
        }

        // ☠️ MORTE POR QUEDA
        else if ((targetPlayer != null || targetVillager != null)
                && itemCount.getOrDefault(VOODOO, 0) >= 1
                && itemCount.getOrDefault(FEATHER, 0) >= 64) {

            if (targetPlayer != null) {
                targetPlayer.teleportTo(targetPlayer.getX(), 500, targetPlayer.getZ());
            } else {
                targetVillager.teleportTo(targetVillager.getX(), 500, targetVillager.getZ());
            }

            itensConsumidos.put(VOODOO, 1);
            itensConsumidos.put(FEATHER, 64);

            // -1 coração permanente
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                    player.getAttribute(Attributes.MAX_HEALTH).getBaseValue() - 2
            );

            message = Component.literal("§eRitual: " + targetName + " caiu para a morte.");
            success = true;
        }


        // morte por veneno
        if ((targetPlayer != null || targetVillager != null) && itemCount.getOrDefault(VOODOO, 0) >= 1) {
            if (itemCount.getOrDefault(FERMENTED, 0) >= 12) {
                Entity subject = (targetPlayer != null) ? targetPlayer : targetVillager;
                ((LivingEntity) subject).addEffect(new MobEffectInstance(MobEffects.POISON, Integer.MAX_VALUE, 0, false, true));
                message = Component.literal("§2Ritual: veneno eterno lançado sobre " + targetName);
                itensConsumidos.put(VOODOO, 1);
                itensConsumidos.put(FERMENTED, 12);
                success = true;
            } else if (itemCount.getOrDefault(BONE, 0) >= 16) {
                if (level instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 18; i++) {
                        Skeleton skeleton = EntityType.SKELETON.create(serverLevel);
                        if (skeleton != null) {
                            double x = (targetPlayer != null ? targetPlayer.getX() : targetVillager.getX()) + (level.random.nextDouble() - 0.5) * 2;
                            double z = (targetPlayer != null ? targetPlayer.getZ() : targetVillager.getZ()) + (level.random.nextDouble() - 0.5) * 2;
                            skeleton.moveTo(x, pos.getY(), z);
                            serverLevel.addFreshEntity(skeleton);
                        }
                    }
                }
                message = Component.literal("§7Ritual: esqueletos invocados ao redor de " + targetName);
                itensConsumidos.put(VOODOO, 1);
                itensConsumidos.put(BONE, 16);
                success = true;
            } else if (itemCount.getOrDefault(CHICKEN, 0) >= 32) {
                if (level instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 64; i++) {
                        Chicken chicken = EntityType.CHICKEN.create(serverLevel);
                        if (chicken != null) {
                            double x = (targetPlayer != null ? targetPlayer.getX() : targetVillager.getX()) + (level.random.nextDouble() - 0.5) * 2;
                            double z = (targetPlayer != null ? targetPlayer.getZ() : targetVillager.getZ()) + (level.random.nextDouble() - 0.5) * 2;
                            chicken.moveTo(x, pos.getY(), z);
                            serverLevel.addFreshEntity(chicken);
                        }
                    }
                }
                message = Component.literal("§eRitual: 64 galinhas invocadas em volta de " + targetName);
                itensConsumidos.put(VOODOO, 1);
                itensConsumidos.put(CHICKEN, 32);
                success = true;
            } else if (itemCount.getOrDefault(DIRT, 0) >= 64) {
                if (targetPlayer != null) targetPlayer.teleportTo(targetPlayer.getX(), targetPlayer.getY() - 4, targetPlayer.getZ());
                else targetVillager.teleportTo(targetVillager.getX(), targetVillager.getY() - 4, targetVillager.getZ());
                message = Component.literal("§6Ritual: " + targetName + " afundou na terra.");
                itensConsumidos.put(VOODOO, 1);
                itensConsumidos.put(DIRT, 64);
                success = true;
            } else if (itemCount.getOrDefault(ENDER, 0) >= 16) {
                double x = level.random.nextInt(30000000) - 15000000;
                double z = level.random.nextInt(30000000) - 15000000;
                double y = 100;
                if (targetPlayer != null) targetPlayer.teleportTo(x, y, z);
                else targetVillager.teleportTo(x, y, z);
                message = Component.literal("§5Ritual: " + targetName + " foi lançado ao desconhecido.");
                itensConsumidos.put(VOODOO, 1);
                itensConsumidos.put(ENDER, 16);
                success = true;
            }

            if (success && player.getAttribute(Attributes.MAX_HEALTH) != null) {
                player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                        player.getAttribute(Attributes.MAX_HEALTH).getBaseValue() - 2);
            }
        }

        if (success) {
            for (ItemEntity entity : items) {
                ItemStack stack = entity.getItem();
                Item item = stack.getItem();
                if (itensConsumidos.containsKey(item)) {
                    int toRemove = itensConsumidos.get(item);
                    int current = stack.getCount();
                    if (current > toRemove) {
                        stack.setCount(current - toRemove);
                        itensConsumidos.put(item, 0);
                    } else {
                        itensConsumidos.put(item, toRemove - current);
                        entity.discard();
                    }
                }
            }
            level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1f, 1f);
        }

        player.sendSystemMessage(message);
        return InteractionResult.CONSUME;
    }
}
