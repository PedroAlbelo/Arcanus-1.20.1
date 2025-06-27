// ARCANESLAVEENTITY.JAVA (VERSÃO FINALIZADA)
package net.PaiPain.arcanus.entity.custom;

import net.PaiPain.arcanus.entity.ModEntities;
import net.PaiPain.arcanus.entity.client.MiningAreaManager;
import net.PaiPain.arcanus.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

public class ArcaneSlaveEntity extends TamableAnimal {
    public enum Mode {
        FOLLOW, ATTACK, MINE, REPOUSO
    }

    private static final EntityDataAccessor<Integer> MODE = SynchedEntityData.defineId(ArcaneSlaveEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(ArcaneSlaveEntity.class, EntityDataSerializers.BOOLEAN);

    private BlockPos currentTargetBlock = null;
    private int breakProgress = 0;
    private int cooldown = 0;
    private int mineTickCounter = 0;
    private int miningBoostTicks = 0;

    public ArcaneSlaveEntity(EntityType<? extends ArcaneSlaveEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MODE, Mode.FOLLOW.ordinal());
        this.entityData.define(SITTING, false);
    }

    public Mode getMode() {
        return Mode.values()[entityData.get(MODE)];
    }

    public void setMode(Mode mode) {
        this.entityData.set(MODE, mode.ordinal());
        this.setSitting(mode == Mode.REPOUSO);
        if (mode != Mode.MINE) {
            if (mode == Mode.MINE) {
                this.noActionTime = 0; // evita timeout/teleporte
                this.goalSelector.removeGoal(followGoal);
            } else if (!this.goalSelector.getAvailableGoals().contains(followGoal)) {
                this.goalSelector.addGoal(4, followGoal);
            };
        }
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(SITTING, sitting);
        this.setOrderedToSit(sitting);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag tag) {
        this.setMode(Mode.FOLLOW);
        return super.finalizeSpawn(level, difficulty, reason, spawnData, tag);
    }

    private FollowOwnerGoal followGoal;

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.followGoal = new FollowOwnerGoal(this, 1.0D, 5.0F, 2.0F, false);
        this.goalSelector.addGoal(4, followGoal);
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (miningBoostTicks > 0) miningBoostTicks--;

        System.out.println("[DEBUG] aiStep chamado. Modo atual: " + getMode());

        if (!this.level().isClientSide && this.getMode() == Mode.MINE && !this.isSitting()) {
            System.out.println("[DEBUG] Iniciando lógica de mineração...");

            mineTickCounter++;
            if (mineTickCounter % 5 != 0) return;
            if (cooldown > 0) { cooldown--; return; }

            if (currentTargetBlock == null ||
                    !MiningAreaManager.isBlockMarked(currentTargetBlock) ||
                    this.level().isEmptyBlock(currentTargetBlock)) {
                System.out.println("[DEBUG] Buscando novo bloco alvo...");
                currentTargetBlock = findNextMineBlock();
                breakProgress = 0;
                if (currentTargetBlock == null) {
                    System.out.println("[DEBUG] Nenhum bloco válido encontrado para minerar.");
                    return;
                }
            }

            double dist = this.distanceToSqr(Vec3.atCenterOf(currentTargetBlock));
            System.out.println("[DEBUG] Alvo: " + currentTargetBlock + " | Distância: " + dist);

            if (dist > 16.0D) {
                System.out.println("[DEBUG] Muito longe, movendo até o bloco alvo...");
                this.getNavigation().moveTo(currentTargetBlock.getX() + 0.5, currentTargetBlock.getY(), currentTargetBlock.getZ() + 0.5, 1.0);
                return;
            }

            BlockState state = level().getBlockState(currentTargetBlock);
            float hardness = state.getDestroySpeed(level(), currentTargetBlock);
            if (hardness >= 0 && hardness < 100) {
                breakProgress++;
                if (mineTickCounter % 10 == 0) {
                    this.level().addParticle(ParticleTypes.CRIT,
                            currentTargetBlock.getX() + 0.5,
                            currentTargetBlock.getY() + 0.5,
                            currentTargetBlock.getZ() + 0.5,
                            0, 0.1, 0);
                }
                if (breakProgress >= getBreakTime(state)) {
                    System.out.println("[DEBUG] Quebrando bloco: " + currentTargetBlock);
                    if (level().destroyBlock(currentTargetBlock, true, this)) {
                        this.level().playSound(null, currentTargetBlock,
                                SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                        MiningAreaManager.removeMarkedBlock(currentTargetBlock);
                    }
                    currentTargetBlock = null;
                    breakProgress = 0;
                    cooldown = 10;
                }
            } else {
                System.out.println("[DEBUG] Bloco não minerável. Pulando.");
                MiningAreaManager.removeMarkedBlock(currentTargetBlock);
                currentTargetBlock = null;
                breakProgress = 0;
            }
        }
    }

    private BlockPos findNextMineBlock() {
        Set<BlockPos> blocks = MiningAreaManager.getMarkedBlocks();
        System.out.println("[DEBUG] Total de blocos marcados: " + blocks.size());
        for (BlockPos pos : blocks) {
            System.out.println("[DEBUG] Verificando bloco: " + pos);
            if (!this.level().isEmptyBlock(pos) &&
                    this.level().getBlockState(pos).getDestroySpeed(this.level(), pos) >= 0) {
                return pos;
            }
        }
        return null;
    }

    private int getBreakTime(BlockState state) {
        float hardness = state.getDestroySpeed(level(), BlockPos.ZERO);
        int base = Math.max(5, (int)(5 + hardness * 0.5));
        return miningBoostTicks > 0 ? Math.max(1, base / 5) : base;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        if (!this.level().isClientSide) {
            if (item.is(ModItems.ARCANE_ESSENCE.get()) && !this.isTame()) {
                if (!player.getAbilities().instabuild) item.shrink(1);
                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
                return InteractionResult.SUCCESS;
            }
            if (item.is(Items.DIAMOND) && this.getMode() == Mode.MINE) {
                if (!player.getAbilities().instabuild) item.shrink(1);
                this.miningBoostTicks = 20 * 40;
                player.displayClientMessage(Component.literal("⛏️ Boost de mineração ativado por 40 segundos!"), true);
                this.level().broadcastEntityEvent(this, (byte) 8);
                return InteractionResult.SUCCESS;
            }
            if (item.is(ModItems.Arcane_Whip.get()) && this.isOwnedBy(player)) {
                Mode newMode = switch (this.getMode()) {
                    case FOLLOW -> Mode.ATTACK;
                    case ATTACK -> Mode.MINE;
                    case MINE -> Mode.REPOUSO;
                    case REPOUSO -> Mode.FOLLOW;
                };
                this.setMode(newMode);
                player.displayClientMessage(Component.literal("Modo alterado para: " + newMode.name()), true);
                this.level().broadcastEntityEvent(this, (byte) 7);
                this.level().playSound(null, this.blockPosition(), SoundEvents.GENERIC_HURT, SoundSource.NEUTRAL, 1.0F, 1.5F);
                return InteractionResult.SUCCESS;
            }
            if (player.isShiftKeyDown() && item.isEmpty() && this.isOwnedBy(player)) {
                Mode newMode = (this.getMode() == Mode.REPOUSO) ? Mode.FOLLOW : Mode.REPOUSO;
                this.setMode(newMode);
                player.displayClientMessage(Component.literal("Modo alternado para: " + newMode.name()), true);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player && player.getMainHandItem().is(ModItems.Arcane_Whip.get())) {
            return false;
        }
        return super.hurt(source, amount);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 7) {
            for (int i = 0; i < 5; ++i) {
                double dx = this.random.nextGaussian() * 0.02;
                double dy = this.random.nextGaussian() * 0.02;
                double dz = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART,
                        this.getX() + (this.random.nextFloat() * this.getBbWidth() * 2.0F) - this.getBbWidth(),
                        this.getY() + 0.5D + (this.random.nextFloat() * this.getBbHeight()),
                        this.getZ() + (this.random.nextFloat() * this.getBbWidth() * 2.0F) - this.getBbWidth(),
                        dx, dy, dz);
            }
        } else if (id == 8) {
            for (int i = 0; i < 20; ++i) {
                double dx = this.random.nextGaussian() * 0.02;
                double dy = this.random.nextGaussian() * 0.02;
                double dz = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.EFFECT,
                        this.getX() + this.random.nextFloat(),
                        this.getY() + 0.5D,
                        this.getZ() + this.random.nextFloat(),
                        dx, dy, dz);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public ArcaneSlaveEntity getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ModEntities.ARCANE_SLAVE.get().create(level);
    }
}
