/**
 * 模块执行上下文类
 * 
 * 包含模块执行时所需的各种信息和状态，如施法者、世界、位置、方向等。
 * 同时还包含一些可以在模块间传递和修改的状态信息。
 */
package com.kirisame1969.weaving_infinity.module;

import io.redspace.ironsspellbooks.api.spells.CastSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ModuleExecutionContext {
    // 当前世界
    public Level level;
    // 施法者
    public LivingEntity caster;
    // 施法来源
    public CastSource castSource;
    // 施法位置
    public Vec3 position;
    // 施法方向
    public Vec3 direction;
    
    // 伤害倍数修饰符
    public float damageMultiplier = 1.0f;
    // 额外投射物数量
    public int additionalProjectiles = 0;
    
    // 是否继续执行后续模块
    public boolean shouldContinue = true;
    // 是否跳过下一个模块
    public boolean skipNextModule = false;
    // 当前模块索引
    public int currentModuleIndex;
    
    /**
     * 构造函数
     * @param level 当前世界
     * @param caster 施法者
     * @param castSource 施法来源
     */
    public ModuleExecutionContext(Level level, LivingEntity caster, CastSource castSource) {
        this.level = level;
        this.caster = caster;
        this.castSource = castSource;
        this.position = caster.getEyePosition();
        this.direction = caster.getLookAngle();
    }
}