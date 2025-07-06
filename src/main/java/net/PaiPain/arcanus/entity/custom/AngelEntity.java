package net.PaiPain.arcanus.entity.custom;

import net.PaiPain.arcanus.item.ModItems;
import net.PaiPain.arcanus.sound.ModSounds;
import net.PaiPain.arcanus.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AngelEntity extends PathfinderMob {

    private boolean isHostile = true;
    private int messageCooldown = 0;
    public int beamCooldown = 0;

    public AngelEntity(EntityType<? extends AngelEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AngelFloatAndShootGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);

        this.spawnAtLocation(new ItemStack(Items.ENDER_EYE));

        if (this.random.nextFloat() < 0.8F + (0.1F * looting)) {
            this.spawnAtLocation(new ItemStack(ModItems.ANGEL_TEAR.get()));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && isHostile && messageCooldown-- <= 0) {
            Player nearestPlayer = this.level().getNearestPlayer(this, 16);
            if (nearestPlayer != null && this.getTarget() == nearestPlayer) {
                double distance = this.distanceTo(nearestPlayer);
                if (distance < 6) {
                    nearestPlayer.sendSystemMessage(Component.literal("GOD IS HERE"));
                    nearestPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 1200, 0)); // 1 minuto
                    nearestPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 1)); // 1 minuto
                } else if (distance < 12) {
                    nearestPlayer.sendSystemMessage(Component.literal("GOD APPROACHES"));
                }
                messageCooldown = 200; // 10 segundos
            }
        }
    }

    public boolean isHostile() {
        return isHostile;
    }

    public void setHostile(boolean hostile) {
        this.isHostile = hostile;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FLYING_SPEED, 0.9D);
    }

    // 🔊 SOM AMBIENTE
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ANGEL_IDLE.get();
    }

    // 🔊 SOM AO SER ATINGIDO
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ANGEL_HURT.get();
    }

    // 🔊 SOM DE MORTE
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ANGEL_DEATH.get();
    }

    public static class AngelFloatAndShootGoal extends Goal {
        private final AngelEntity angel;
        private final double speed;

        public AngelFloatAndShootGoal(AngelEntity angel, double speed) {
            this.angel = angel;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return angel.getTarget() != null && angel.getTarget().isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = angel.getTarget();
            if (target != null) {
                double distance = angel.distanceTo(target);
                Vec3 direction = target.position().subtract(angel.position()).normalize();

                if (distance < 3.5) {
                    angel.setDeltaMovement(direction.scale(-speed));
                } else if (distance > 4.5) {
                    angel.setDeltaMovement(direction.scale(speed));
                } else {
                    angel.setDeltaMovement(Vec3.ZERO);

                    if (--angel.beamCooldown <= 0 && angel.level() instanceof ServerLevel serverLevel) {
                        target.hurt(serverLevel.damageSources().magic(), 6.0F);

                        Vec3 from = angel.position().add(0, angel.getBbHeight() / 2, 0);
                        Vec3 to = target.position().add(0, target.getBbHeight() / 2, 0);
                        Vec3 beam = to.subtract(from);
                        int particles = 20;

                        for (int i = 0; i < particles; i++) {
                            double progress = i / (double) particles;
                            Vec3 pos = from.add(beam.scale(progress));
                            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                                    pos.x, pos.y, pos.z,
                                    1, 0, 0, 0, 0.0);
                        }

                        serverLevel.playSound(null, target.blockPosition(), SoundEvents.GUARDIAN_ATTACK,
                                angel.getSoundSource(), 1.0F, 1.0F);

                        angel.beamCooldown = 60;
                    }
                }

                angel.setYRot((float)(Mth.atan2(direction.z, direction.x) * (180F / Math.PI)) - 90.0F);
                angel.yBodyRot = angel.getYRot();
            }
        }
    }
}
