package net.PaiPain.arcanus.entity.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class OwnerHurtByTargetGoal extends Goal {
    private final ArcaneSlaveEntity slave;
    private LivingEntity attacker;

    public OwnerHurtByTargetGoal(ArcaneSlaveEntity slave) {
        this.slave = slave;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!slave.isTame() || slave.getMode() != ArcaneSlaveEntity.Mode.ATTACK) return false;

        LivingEntity owner = slave.getOwner();
        if (owner == null) return false;

        this.attacker = owner.getLastHurtByMob();
        return attacker != null && attacker != slave;
    }

    @Override
    public void start() {
        if (attacker != null) {
            slave.setTarget(attacker);
        }
        super.start();
    }
}
