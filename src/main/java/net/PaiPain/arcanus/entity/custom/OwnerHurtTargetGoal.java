package net.PaiPain.arcanus.entity.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class OwnerHurtTargetGoal extends Goal {
    private final ArcaneSlaveEntity slave;
    private LivingEntity target;

    public OwnerHurtTargetGoal(ArcaneSlaveEntity slave) {
        this.slave = slave;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!slave.isTame() || slave.getMode() != ArcaneSlaveEntity.Mode.ATTACK) return false;

        LivingEntity owner = slave.getOwner();
        if (owner == null) return false;

        target = owner.getLastHurtMob();
        return target != null && target != slave;
    }

    @Override
    public void start() {
        slave.setTarget(target);
        super.start();
    }
}
