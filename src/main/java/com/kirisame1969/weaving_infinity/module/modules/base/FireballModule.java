/**
 * 火球术模块
 * 
 * 基础模块，用于发射一个火球投射物。火球击中目标时可以触发修饰模块的效果，
 * 例如分裂成多个小火球。
 */
package com.kirisame1969.weaving_infinity.module.modules.base;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.module.ISpellModule;
import com.kirisame1969.weaving_infinity.module.ModuleType;
import com.kirisame1969.weaving_infinity.module.ModuleExecutionContext;
import io.redspace.ironsspellbooks.entity.spells.fireball.SmallMagicFireball;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

public class FireballModule implements ISpellModule {
    
    // 火球的基础伤害值
    private static final float BASE_DAMAGE = 6.0f;
    // 分裂火球的伤害值
    private static final float SPLIT_DAMAGE = 3.0f;
    
    @Override
    public ResourceLocation getId() {
        return WeavingInfinity.id("fireball");
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("module.weaving_infinity.fireball");
    }
    
    @Override
    public Set<String> getTags() {
        return Set.of("base", "projectile", "fire");
    }
    
    @Override
    public ModuleType getModuleType() {
        return ModuleType.BASE;
    }
    
    @Override
    public int getBaseManaConsumption() {
        return 10;
    }
    
    @Override
    public int getBaseCooldown() {
        return 20;
    }
    
    @Override
    public float getBaseComplexity() {
        return 1.0f;
    }
    
    @Override
    public void execute(ModuleExecutionContext context) {
        // 创建火球投射物
        SmallMagicFireball fireball = new SmallMagicFireball(
            context.level, 
            context.caster
        );
        
        // 设置火球的位置和方向
        Vec3 lookAngle = context.direction;
        fireball.setPos(
            context.caster.getX(),
            context.caster.getEyeY(),
            context.caster.getZ()
        );
        
        // 设置火球的速度，inaccuracy设为0以确保精准发射
        fireball.shoot(lookAngle, 0.0f);
        
        // 设置火球的伤害
        fireball.setDamage(BASE_DAMAGE);
        
        // 添加一个事件监听器，当火球击中目标时检查是否需要分裂
        fireball.addTag("weaving_infinity_fireball");
        
        // 将火球加入世界
        context.level.addFreshEntity(fireball);
    }
    
    /**
     * 克隆火球投射物
     * 实现ISpellModule接口的cloneProjectile方法，允许修饰模块复制火球效果
     * @param context 模块执行上下文
     * @param origin 发射原点
     * @param direction 发射方向
     * @return 是否成功克隆投射物
     */
    @Override
    public boolean cloneProjectile(ModuleExecutionContext context, Vec3 origin, Vec3 direction) {
        // 检查是否为服务器端环境
        if (!(context.level instanceof ServerLevel)) {
            return false;
        }
        
        // 创建火球投射物
        SmallMagicFireball fireball = new SmallMagicFireball(
            context.level, 
            context.caster
        );
        
        // 设置火球的位置
        fireball.setPos(origin.x, origin.y, origin.z);
        
        // 设置火球的速度，inaccuracy设为0以确保精准发射
        fireball.shoot(direction, 0.0f);
        
        // 设置火球的伤害
        fireball.setDamage(SPLIT_DAMAGE);
        
        // 给分裂的小火球添加标记，避免它们再次触发分裂
        fireball.addTag("weaving_infinity_split_fireball");
        
        // 将火球加入世界
        context.level.addFreshEntity(fireball);
        
        return true;
    }
}