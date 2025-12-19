/**
 * 法术模块接口
 * 
 * 所有法术模块都需要实现此接口。模块分为基础模块和修饰模块两种类型。
 * 基础模块提供核心功能（如发射火球），修饰模块修改其他模块的行为（如分裂效果）。
 */
package com.kirisame1969.weaving_infinity.api.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public interface ISpellModule {
    /**
     * 获取模块的唯一标识符
     * @return 模块的ResourceLocation
     */
    ResourceLocation getId();
    
    /**
     * 获取模块的显示名称
     * @return 显示名称组件
     */
    Component getDisplayName();
    
    /**
     * 获取模块的标签集合
     * @return 标签字符串集合
     */
    Set<String> getTags();
    
    /**
     * 获取模块类型（基础模块或修饰模块）
     * @return 模块类型枚举
     */
    ModuleType getModuleType();
    
    /**
     * 获取基础法力消耗
     * @return 法力消耗值
     */
    int getBaseManaConsumption();
    
    /**
     * 获取基础冷却时间
     * @return 冷却时间（刻）
     */
    int getBaseCooldown();
    
    /**
     * 获取基础复杂度
     * @return 复杂度值
     */
    float getBaseComplexity();
    
    /**
     * 执行模块的主要功能
     * @param context 模块执行上下文
     */
    void execute(ModuleExecutionContext context);
    
    /**
     * 修改执行上下文（修饰模块使用）
     * @param context 模块执行上下文
     */
    default void modifyContext(ModuleExecutionContext context) {}
    
    /**
     * 修改下一个模块的行为（修饰模块使用）
     * @param next 下一个模块
     * @param context 模块执行上下文
     */
    default void modifyNextModule(ISpellModule next, ModuleExecutionContext context) {}
    
    /**
     * 检查模块是否应该在当前上下文中执行
     * @param context 模块执行上下文
     * @return 是否应该执行
     */
    default boolean shouldExecute(ModuleExecutionContext context) {
        return true;
    }
    
    /**
     * 处理模块执行后的回调
     * @param context 模块执行上下文
     */
    default void onPostExecute(ModuleExecutionContext context) {}
    
    /**
     * 克隆投射物（供修饰模块使用）
     * 该方法允许修饰模块复制基础模块的投射物效果
     * @param context 模块执行上下文
     * @param origin 发射原点
     * @param direction 发射方向
     * @return 是否成功克隆投射物
     */
    default boolean cloneProjectile(ModuleExecutionContext context, Vec3 origin, Vec3 direction) {
        // 默认实现返回false，表示该模块不支持投射物克隆
        return false;
    }
}