package com.kirisame1969.weaving_infinity.module;

import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

/**
 * 法术模块接口 - 所有法术模块的基础接口
 * 
 * 模块分为两类：
 * - BASE（基础模块）：具有独立效果，如火球术、冰箭
 * - MODIFIER（修改器）：依附于其他模块，修改其行为，如击中分裂、穿透
 */
public interface ISpellModule {
    
    // ===== 基础信息 =====
    
    /**
     * 获取模块的唯一ID
     */
    ResourceLocation getId();
    
    /**
     * 获取显示名称
     */
    Component getDisplayName();
    
    /**
     * 获取模块的标签集合
     * 标签用于模块间的交互和属性计算
     * 常用标签: "damage", "fire", "ice", "projectile", "aoe", "buff", "debuff"等
     */
    Set<String> getTags();
    
    /**
     * 获取模块所属学派
     */
    SchoolType getSchool();
    
    // ===== 消耗与属性 =====
    
    /**
     * 获取基础魔力消耗
     * 实际消耗会受到修改器和核心属性的影响
     */
    int getBaseManaConsumption();
    
    /**
     * 获取基础冷却贡献（tick）
     * 1秒 = 20 tick
     */
    int getBaseCooldown();
    
    /**
     * 获取基础复杂度贡献
     */
    float getBaseComplexity();
    
    // ===== 执行逻辑 =====
    
    /**
     * 获取模块类型
     */
    ModuleType getModuleType();
    
    /**
     * 执行模块的主要逻辑
     * @param context 执行上下文，包含施法者、目标、位置等信息
     */
    void execute(ModuleExecutionContext context);
    
    // ===== 修改器能力（可选） =====
    
    /**
     * 修改执行上下文
     * 修改器模块可以在执行前修改上下文数据
     * 
     * @param context 当前执行上下文
     */
    default void modifyContext(ModuleExecutionContext context) {
        // 默认不做任何修改
    }
    
    /**
     * 修改下一个模块
     * 修改器可以影响紧随其后的模块
     * 
     * @param next 下一个模块
     * @param context 当前上下文
     */
    default void modifyNextModule(ISpellModule next, ModuleExecutionContext context) {
        // 默认不做任何修改
    }
    
    // ===== 属性计算（可选） =====
    
    /**
     * 获取此模块对伤害的贡献
     * 
     * @param context 执行上下文
     * @return 伤害值
     */
    default float getDamageContribution(ModuleExecutionContext context) {
        return 0.0f;
    }
    
    /**
     * 获取此模块的法力消耗倍率
     * 用于动态调整消耗
     * 
     * @param context 执行上下文
     * @return 倍率（1.0 = 100%）
     */
    default float getManaCostMultiplier(ModuleExecutionContext context) {
        return 1.0f;
    }
    
    /**
     * 获取此模块的冷却倍率
     * 
     * @param context 执行上下文
     * @return 倍率（1.0 = 100%）
     */
    default float getCooldownMultiplier(ModuleExecutionContext context) {
        return 1.0f;
    }
}
