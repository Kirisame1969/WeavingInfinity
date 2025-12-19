/**
 * 模块执行上下文
 * 
 * 包含模块执行所需的各种信息，如施法者、世界、法术来源等。
 * 同时包含模块执行过程中的状态控制字段。
 */
package com.kirisame1969.weaving_infinity.api.module;

import io.redspace.ironsspellbooks.api.spells.CastSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.Map;

public class ModuleExecutionContext {
    public final Level level;
    public final LivingEntity caster;
    public final CastSource source;
    public final Vec3 direction;
    
    // 空间数据：记录效果生成位置、飞行方向、目标位置等空间参考信息
    public Vec3 originPosition;     // 效果生成位置
    public Vec3 targetPosition;     // 目标位置
    
    // 控制执行流程的字段
    public boolean shouldContinue = true;  // 是否继续执行后续模块
    public boolean skipNextModule = false; // 是否跳过下一个模块
    public int skipModuleCount = 0;        // 跳过模块的数量
    public int currentModuleIndex = 0;     // 当前模块索引
    
    // 共享数据池：存储模块执行成果、修改器标记等
    public final Map<String, Object> sharedData = new HashMap<>();
    
    public ModuleExecutionContext(Level level, LivingEntity caster, CastSource source) {
        this.level = level;
        this.caster = caster;
        this.source = source;
        this.direction = caster.getLookAngle();
        this.originPosition = caster.position();
    }
    
    public ModuleExecutionContext(Level level, LivingEntity caster, CastSource source, Vec3 direction) {
        this.level = level;
        this.caster = caster;
        this.source = source;
        this.direction = direction;
        this.originPosition = caster.position();
    }
    
    /**
     * 重置上下文状态（用于子实体隔离）
     * 子实体生成时调用，重置修改器生效状态，避免递归触发重复逻辑
     */
    public void resetForSubEntity() {
        // 重置流程控制标记
        this.shouldContinue = true;
        this.skipNextModule = false;
        this.skipModuleCount = 0;
        this.currentModuleIndex = 0;
        
        // 保留某些关键数据，但移除可能导致递归的数据
        Object originalProjectile = this.sharedData.get("original_projectile");
        this.sharedData.clear();
        if (originalProjectile != null) {
            this.sharedData.put("original_projectile", originalProjectile);
        }
    }
}