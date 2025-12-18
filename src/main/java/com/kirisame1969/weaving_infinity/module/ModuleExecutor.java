/**
 * 模块执行器类
 * 
 * 负责按顺序执行法术模块列表。处理基础模块和修饰模块的不同执行逻辑，
 * 并管理模块间的交互和状态传递。
 */
package com.kirisame1969.weaving_infinity.module;

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
        
        // 按顺序执行所有模块
        for (int i = 0; i < modules.size(); i++) {
            // 如果上下文指示停止执行，则退出循环
            if (!context.shouldContinue) break;
            
            // 如果上下文指示跳过下一个模块，则设置标志并继续
            if (context.skipNextModule) {
                context.skipNextModule = false;
                continue;
            }
            
            context.currentModuleIndex = i;
            ISpellModule module = modules.get(i);
            
            try {
                // 如果是修饰模块，先修改上下文和下一个模块
                if (module.getModuleType() == ModuleType.MODIFIER) {
                    module.modifyContext(context);
                    
                    // 如果还有下一个模块，则修改下一个模块的行为
                    if (i + 1 < modules.size()) {
                        module.modifyNextModule(modules.get(i + 1), context);
                    }
                }
                
                // 执行模块的主要功能
                module.execute(context);
                
            } catch (Exception e) {
                WeavingInfinity.LOGGER.error("Error executing module {}: {}", module.getId(), e.getMessage());
            }
        }
    }
}