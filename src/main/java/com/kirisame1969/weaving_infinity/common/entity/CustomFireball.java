/**
 * 自定义火球实体
 * 
 * 这个火球实体专为Weaving Infinity模组设计，支持在击中实体或方块时触发特殊效果。
 */
package com.kirisame1969.weaving_infinity.common.entity;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class CustomFireball extends AbstractMagicProjectile {
    public CustomFireball(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
    }

    public CustomFireball(Level pLevel, LivingEntity pShooter) {
        this(com.kirisame1969.weaving_infinity.registries.ModEntities.CUSTOM_FIREBALL.get(), pLevel);
        this.setOwner(pShooter);
    }

    @Override
    public void trailParticles() {
        if (tickCount <= 3) {
            return;
        }
        var pos = position();
        this.level().addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 0, 0, 0);
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        // 添加撞击粒子效果
        for (int i = 0; i < 5; i++) {
            this.level().addParticle(ParticleTypes.LAVA, x, y, z, 
                this.random.nextGaussian() * 0.1,
                this.random.nextGaussian() * 0.1,
                this.random.nextGaussian() * 0.1);
        }
    }

    @Override
    public float getSpeed() {
        return 1.85f;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.of(SoundRegistry.FIRE_IMPACT);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        // 触发实体撞击效果
        this.triggerHitEffects(pResult.getLocation());
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        // 触发方块撞击效果
        this.triggerHitEffects(pResult.getLocation());
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        // 使用discardHelper确保实体被正确销毁
        this.discardHelper(pResult);
    }

    /**
     * 触发撞击效果
     * 当火球击中实体或方块时调用此方法
     * @param hitPos 撞击位置
     */
    private void triggerHitEffects(Vec3 hitPos) {
        // 添加撞击粒子效果
        this.impactParticles(hitPos.x, hitPos.y, hitPos.z);
        
        // 检查是否有特殊标签并触发相应效果
        if (this.getTags().contains("weaving_infinity_fireball") && 
            !this.getTags().contains("weaving_infinity_split_fireball")) {
            // 触发Weaving Infinity模组的特殊效果
            com.kirisame1969.weaving_infinity.event.ModEventHandler.onCustomFireballHit(this, hitPos);
        }
    }

    public void shoot(Vec3 rotation, float inaccuracy) {
        var speed = rotation.length();
        Vec3 offset = getRandomVec3(1).normalize().scale(inaccuracy);
        var motion = rotation.normalize().add(offset).normalize().scale(speed);
        super.shoot(motion);
    }

    private Vec3 getRandomVec3(double multiplier) {
        return new Vec3(
            (this.random.nextDouble() - 0.5) * 2 * multiplier,
            (this.random.nextDouble() - 0.5) * 2 * multiplier,
            (this.random.nextDouble() - 0.5) * 2 * multiplier
        );
    }
}