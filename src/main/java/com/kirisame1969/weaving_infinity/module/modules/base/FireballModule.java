/**
 * 火球术模块
 * 
 * 基础模块，用于发射一个火球投射物。火球击中目标时可以触发修饰模块的效果，
 * 例如分裂成多个小火球。
 */
package com.kirisame1969.weaving_infinity.module.modules.base;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.api.module.ISpellModule;
import com.kirisame1969.weaving_infinity.api.module.ModuleType;
import com.kirisame1969.weaving_infinity.api.module.ModuleExecutionContext;
import com.kirisame1969.weaving_infinity.common.entity.CustomFireball;
import com.kirisame1969.weaving_infinity.common.config.ModuleConfig;
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
        return ModuleConfig.getInstance().fireballModule.manaConsumption;
    }
    
    @Override
    public int getBaseCooldown() {
        return ModuleConfig.getInstance().fireballModule.cooldown;
    }
    
    @Override
    public float getBaseComplexity() {
        return ModuleConfig.getInstance().fireballModule.complexity;
    }
    
    @Override
    public void execute(ModuleExecutionContext context) {
        // 获取配置
        ModuleConfig config = ModuleConfig.getInstance();
        
        // 创建火球投射物
        CustomFireball fireball = new CustomFireball(
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
        fireball.setDamage(config.fireballModule.baseDamage);
        
        // 添加一个事件监听器，当火球击中目标时检查是否需要分裂
        fireball.addTag("weaving_infinity_fireball");
        
        // 将火球加入世界
        context.level.addFreshEntity(fireball);
        
        // 在上下文中记录生成的实体
        context.sharedData.put("last_projectile", fireball);
        // 如果这是主实体，也在上下文中记录作为原始实体
        if (!context.sharedData.containsKey("original_projectile")) {
            context.sharedData.put("original_projectile", fireball);
        }
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
        
        // 获取配置
        ModuleConfig config = ModuleConfig.getInstance();
        
        // 创建火球投射物
        CustomFireball fireball = new CustomFireball(
            context.level, 
            context.caster
        );
        
        // 设置火球的位置
        fireball.setPos(origin.x, origin.y, origin.z);
        
        // 设置火球的速度，inaccuracy设为0以确保精准发射
        fireball.shoot(direction, 0.0f);
        
        // 设置火球的伤害
        fireball.setDamage(config.fireballModule.splitDamage);
        
        // 给分裂的小火球添加标记，避免它们再次触发分裂
        fireball.addTag("weaving_infinity_split_fireball");
        
        // 将火球加入世界
        context.level.addFreshEntity(fireball);
        
        return true;
    }
}