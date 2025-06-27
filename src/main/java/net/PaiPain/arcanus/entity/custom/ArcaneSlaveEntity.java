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
import net.minecraft.util.RandomSource;
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
        FOLLOW,
        ATTACK,
        MINE,
        REPOUSO
    }

    private static final EntityDataAccessor<Integer> MODE = SynchedEntityData.defineId(ArcaneSlaveEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(ArcaneSlaveEntity.class, EntityDataSerializers.BOOLEAN);

    private BlockPos currentTargetBlock = null;
    private int breakProgress = 0;
    private int cooldown = 0;

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
        if (mode == Mode.REPOUSO) {
            this.setOrderedToSit(true);
        } else {
            this.setOrderedToSit(false);
        }
    }

    @Override
    public void setOrderedToSit(boolean sitting) {
        this.entityData.set(SITTING, sitting);
        super.setOrderedToSit(sitting);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        this.setMode(Mode.FOLLOW);
        return super.finalizeSpawn(world, difficulty, spawnReason, data, tag);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0D, 5.0F, 2.0F, false));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            if (this.cooldown > 0) {
                this.cooldown--;
                return;
            }

            if (this.getMode() == Mode.REPOUSO) {
                this.setOrderedToSit(true);
                this.getNavigation().stop();
                return;
            } else {
                this.setOrderedToSit(false);
            }

            if (this.getMode() == Mode.MINE) {
                if (!this.getMainHandItem().is(Items.DIAMOND_PICKAXE)) {
                    this.setMode(Mode.FOLLOW);
                    return;
                }

                if (currentTargetBlock == null || !MiningAreaManager.markedBlocks.contains(currentTargetBlock) || this.level().isEmptyBlock(currentTargetBlock)) {
                    currentTargetBlock = findNextMineBlock();
                    breakProgress = 0;
                }

                if (currentTargetBlock != null) {
                    double dist = this.distanceToSqr(Vec3.atCenterOf(currentTargetBlock));
                    if (dist > 2.0D) {
                        this.getNavigation().moveTo(currentTargetBlock.getX(), currentTargetBlock.getY(), currentTargetBlock.getZ(), 1.0);
                    } else {
                        BlockState state = level().getBlockState(currentTargetBlock);
                        if (state.getDestroySpeed(level(), currentTargetBlock) < 0) {
                            MiningAreaManager.markedBlocks.remove(currentTargetBlock);
                            currentTargetBlock = null;
                            breakProgress = 0;
                            return;
                        }

                        breakProgress++;
                        this.level().addParticle(ParticleTypes.CRIT,
                                currentTargetBlock.getX() + 0.5,
                                currentTargetBlock.getY() + 0.5,
                                currentTargetBlock.getZ() + 0.5,
                                0, 0.1, 0);

                        if (breakProgress >= getBreakTime(state)) {
                            if (level().destroyBlock(currentTargetBlock, true, this)) {
                                this.level().playSound(null, currentTargetBlock, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                                MiningAreaManager.markedBlocks.remove(currentTargetBlock);
                            }
                            currentTargetBlock = null;
                            breakProgress = 0;
                            this.cooldown = 5;
                        }
                    }
                }
            }
        }
    }

    private BlockPos findNextMineBlock() {
        BlockPos nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (BlockPos pos : new ArrayList<>(MiningAreaManager.markedBlocks)) {
            if (!level().isEmptyBlock(pos)) {
                double dist = this.distanceToSqr(Vec3.atCenterOf(pos));
                if (dist < nearestDist) {
                    nearest = pos;
                    nearestDist = dist;
                }
            }
        }
        return nearest;
    }

    private int getBreakTime(BlockState state) {
        float speed = this.getMainHandItem().getDestroySpeed(state);
        float hardness = state.getDestroySpeed(level(), BlockPos.ZERO);
        return Math.max(20, (int)(30 * hardness / speed));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        if (!this.level().isClientSide) {
            if (item.is(ModItems.ARCANE_ESSENCE.get())) {
                if (!this.isTame()) {
                    if (!player.getAbilities().instabuild) item.shrink(1);
                    if (this.random.nextInt(3) == 0) {
                        this.tame(player);
                        this.level().broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                    return InteractionResult.SUCCESS;
                }
            }

            if (item.is(ModItems.Arcane_Whip.get()) && this.isOwnedBy(player)) {
                this.level().broadcastEntityEvent(this, (byte) 7);
                this.level().playSound(null, this.blockPosition(), SoundEvents.GENERIC_HURT, SoundSource.NEUTRAL, 1.0F, 1.5F);

                Mode newMode = this.getMode();
                switch (this.getMode()) {
                    case FOLLOW -> newMode = Mode.ATTACK;
                    case ATTACK -> newMode = Mode.MINE;
                    case MINE -> newMode = Mode.REPOUSO;
                    case REPOUSO -> newMode = Mode.FOLLOW;
                }

                this.setMode(newMode);
                player.displayClientMessage(Component.literal("Modo alterado para: " + newMode.name()), true);
                return InteractionResult.SUCCESS;
            }

            if (item.is(Items.DIAMOND_PICKAXE) && this.isOwnedBy(player)) {
                this.setItemInHand(InteractionHand.MAIN_HAND, item.copy());
                if (!player.getAbilities().instabuild) item.shrink(1);
                this.setMode(Mode.MINE);
                player.displayClientMessage(Component.literal("Arcane Slave equipado com picareta de diamante e pronto para minerar!"), true);
                return InteractionResult.SUCCESS;
            }

            if (player.isShiftKeyDown() && item.isEmpty() && this.isOwnedBy(player)) {
                ItemStack held = this.getMainHandItem();
                if (!held.isEmpty()) {
                    this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                    this.spawnAtLocation(held);
                    this.setMode(Mode.FOLLOW);
                    player.displayClientMessage(Component.literal("Picareta removida do Arcane Slave"), true);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player) {
            if (player.getMainHandItem().is(ModItems.Arcane_Whip.get())) {
                return false;
            }
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
                this.level().addParticle(ParticleTypes.HEART, this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + 0.5D + (double) (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), dx, dy, dz);
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
