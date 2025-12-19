/**
 * 击中时爆炸模块
 * 
 * 修饰模块，当基础模块（如火球）击中目标时，会产生爆炸效果。
 * 这个模块具有通用性，可以适用于任何支持克隆投射物的基础模块。
 */
package com.kirisame1969.weaving_infinity.module.modules.modifier;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.api.module.ISpellModule;
import com.kirisame1969.weaving_infinity.api.module.ModuleType;
import com.kirisame1969.weaving_infinity.common.config.ModuleConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Set;

/**
 * 击中时爆炸模块 - 当基础模块击中目标时，会产生爆炸效果
 */
public class ExplodeOnHitModifier implements ISpellModule {
    
    @Override
    public ResourceLocation getId() {
        return WeavingInfinity.id("explode_on_hit");
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("module.weaving_infinity.explode_on_hit");
    }
    
    @Override
    public Set<String> getTags() {
        return Set.of("modifier", "explode", "projectile");
    }
    
    @Override
    public ModuleType getModuleType() {
        return ModuleType.MODIFIER;
    }
    
    @Override
    public int getBaseManaConsumption() {
        return ModuleConfig.getInstance().explodeModule.manaConsumption;
    }
    
    @Override
    public int getBaseCooldown() {
        return ModuleConfig.getInstance().explodeModule.cooldown;
    }
    
    @Override
    public float getBaseComplexity() {
        return ModuleConfig.getInstance().explodeModule.complexity;
    }
    
    @Override
    public void execute(com.kirisame1969.weaving_infinity.api.module.ModuleExecutionContext context) {
        // 这是一个修饰模块，它不应该直接执行，而是修改前面模块的行为
    }
    
    /**
     * 创建爆炸效果
     * 该方法由事件处理器调用，当检测到带有特定标签的投射物击中目标时触发
     * @param level 服务器世界
     * @param hitPos 击中位置
     */
    public static void createExplosionEffect(ServerLevel level, net.minecraft.world.phys.Vec3 hitPos) {
        // 获取配置
        ModuleConfig config = ModuleConfig.getInstance();
        
        // 创建视觉爆炸效果，参考Iron's Spells n Spellbooks中的实现
        level.explode(
                null,
                null,
                null,
                hitPos.x,
                hitPos.y,
                hitPos.z,
                0,
                false,
                Level.ExplosionInteraction.NONE,
                ParticleTypes.GUST_EMITTER_SMALL,
                ParticleTypes.GUST_EMITTER_LARGE,
                SoundEvents.GENERIC_EXPLODE
        );
        
        // 创建实际的爆炸，会对范围内的实体造成伤害
        level.explode(
                null,
                hitPos.x,
                hitPos.y,
                hitPos.z,
                config.explodeModule.explosionRadius,
                Level.ExplosionInteraction.MOB
        );
    }
}