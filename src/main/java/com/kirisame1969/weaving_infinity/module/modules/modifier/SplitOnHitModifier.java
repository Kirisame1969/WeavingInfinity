/**
 * 击中时分裂模块
 * 
 * 修饰模块，当基础模块（如火球）击中目标时，会产生额外的投射物。
 * 这个模块具有通用性，可以适用于任何支持克隆投射物的基础模块。
 */
package com.kirisame1969.weaving_infinity.module.modules.modifier;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.api.module.ISpellModule;
import com.kirisame1969.weaving_infinity.api.module.ModuleType;
import com.kirisame1969.weaving_infinity.api.module.ModuleExecutionContext;
import com.kirisame1969.weaving_infinity.common.config.ModuleConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

/**
 * 击中时分裂模块 - 当基础模块击中目标时，会产生额外的投射物
 */
public class SplitOnHitModifier implements ISpellModule {
    
    @Override
    public ResourceLocation getId() {
        return WeavingInfinity.id("split_on_hit");
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("module.weaving_infinity.split_on_hit");
    }
    
    @Override
    public Set<String> getTags() {
        return Set.of("modifier", "split", "projectile");
    }
    
    @Override
    public ModuleType getModuleType() {
        return ModuleType.MODIFIER;
    }
    
    @Override
    public int getBaseManaConsumption() {
        return ModuleConfig.getInstance().splitModule.manaConsumption;
    }
    
    @Override
    public int getBaseCooldown() {
        return ModuleConfig.getInstance().splitModule.cooldown;
    }
    
    @Override
    public float getBaseComplexity() {
        return ModuleConfig.getInstance().splitModule.complexity;
    }
    
    @Override
    public void execute(ModuleExecutionContext context) {
        // 这是一个修饰模块，它不应该直接执行，而是修改前面模块的行为
        // 实际的分裂逻辑应该在基础模块(如火球模块)中检测到这个修饰模块后触发
        // 在上下文中标记需要分裂效果
        context.sharedData.put("split_on_hit", true);
        
        // 同时增加分裂计数，允许多个分裂模块叠加
        Integer currentCount = (Integer) context.sharedData.getOrDefault("split_count", 0);
        context.sharedData.put("split_count", currentCount + 1);
    }
    
    /**
     * 创建分裂效果
     * 该方法由事件处理器调用，当检测到带有特定标签的投射物击中目标时触发
     * @param level 服务器世界
     * @param caster 施法者
     * @param hitPos 击中位置
     * @param originalModule 原始模块（用于克隆投射物）
     * @param context 模块执行上下文
     */
    public static void createSplitEffect(ServerLevel level, Entity caster, Vec3 hitPos, ISpellModule originalModule, ModuleExecutionContext context) {
        // 获取配置
        ModuleConfig config = ModuleConfig.getInstance();
        
        // 确保必要的参数不为null
        if (caster == null || hitPos == null || originalModule == null || context == null) {
            WeavingInfinity.LOGGER.warn("创建分裂效果时参数为空: caster={}, hitPos={}, originalModule={}, context={}", 
                caster, hitPos, originalModule, context);
            return;
        }
        
        // 为子实体创建新的上下文
        ModuleExecutionContext subContext = new ModuleExecutionContext(level, (LivingEntity) caster, context.source, context.direction);
        subContext.resetForSubEntity(); // 重置上下文以避免递归触发
        
        // 创建分裂的投射物，分别朝不同方向
        for (int i = 0; i < config.splitModule.splitCount; i++) {
            // 计算分散角度，以被击中实体为中心水平发射
            float yaw = i * config.splitModule.angleBetweenShots;
            
            // 将角度转换为水平方向向量 (pitch为0表示水平方向)
            double yawRadians = Math.toRadians(yaw);
            
            double x = -Math.sin(yawRadians);
            double y = 0; // 水平发射，所以y方向速度为0
            double z = Math.cos(yawRadians);
            
            Vec3 direction = new Vec3(x, y, z).normalize();
            
            // 使用原始模块克隆投射物
            originalModule.cloneProjectile(subContext, hitPos, direction);
        }
    }
}