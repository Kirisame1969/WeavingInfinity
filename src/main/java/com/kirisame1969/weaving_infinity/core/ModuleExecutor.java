/**
 * 模块执行器
 * 
 * 法术执行的"调度核心"，负责统筹模块遍历、执行逻辑触发、流程控制与状态同步，
 * 是模块化组合的核心驱动。
 */
package com.kirisame1969.weaving_infinity.core;

import com.kirisame1969.weaving_infinity.api.module.ISpellModule;
import com.kirisame1969.weaving_infinity.api.module.ModuleExecutionContext;
import com.kirisame1969.weaving_infinity.api.module.ModuleType;
import com.kirisame1969.weaving_infinity.WeavingInfinity;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import java.util.List;

public class ModuleExecutor {
    
    /**
     * 执行模块列表
     * @param modules 模块列表
     * @param caster 施法者
     * @param castSource 施法来源
     */
    public static void executeModules(List<ISpellModule> modules, LivingEntity caster, CastSource castSource) {
        if (modules == null || modules.isEmpty()) {
            WeavingInfinity.LOGGER.warn("Attempted to execute empty module list");
            return;
        }
        
        Level level = caster.level();
        ModuleExecutionContext context = new ModuleExecutionContext(level, caster, castSource);
        
        executeModules(modules, context);
    }
    
    /**
     * 执行模块列表（核心版本）
     * @param modules 模块列表
     * @param context 执行上下文
     */
    public static void executeModules(List<ISpellModule> modules, ModuleExecutionContext context) {
        // 按顺序执行所有模块
        for (int i = 0; i < modules.size(); i++) {
            // 检查流程控制标记
            if (!context.shouldContinue) {
                // 如果上下文指示停止执行，则退出循环
                break;
            }
            
            // 处理跳过模块的逻辑
            if (context.skipNextModule) {
                // 如果上下文指示跳过下一个模块，则跳过并重置标记
                context.skipNextModule = false;
                continue;
            }
            
            if (context.skipModuleCount > 0) {
                // 如果需要跳过多个模块
                context.skipModuleCount--;
                continue;
            }
            
            context.currentModuleIndex = i;
            ISpellModule module = modules.get(i);
            
            try {
                // 执行模块的主要功能
                module.execute(context);
                
            } catch (Exception e) {
                WeavingInfinity.LOGGER.error("Error executing module {}: {}", module.getId(), e.getMessage());
            }
        }
    }
}