/**
 * 模块注册表
 * 
 * 负责注册和管理所有内置的法术模块。
 */
package com.kirisame1969.weaving_infinity.core.registry;

import com.kirisame1969.weaving_infinity.api.module.ISpellModule;
import com.kirisame1969.weaving_infinity.module.modules.base.FireballModule;
import com.kirisame1969.weaving_infinity.module.modules.modifier.SplitOnHitModifier;
import com.kirisame1969.weaving_infinity.module.modules.modifier.ExplodeOnHitModifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModuleRegistry {
    private static final Map<String, ISpellModule> MODULES = new HashMap<>();
    
    /**
     * 注册所有内置模块
     */
    public static void registerBuiltinModules() {
        // 注册基础模块
        registerModule(new FireballModule());
        
        // 注册修饰模块
        registerModule(new SplitOnHitModifier());
        registerModule(new ExplodeOnHitModifier());
    }
    
    /**
     * 注册单个模块
     * @param module 要注册的模块
     */
    private static void registerModule(ISpellModule module) {
        MODULES.put(module.getId().toString(), module);
    }
    
    /**
     * 根据ID获取模块
     * @param id 模块ID
     * @return 对应的模块，如果未找到则返回null
     */
    public static ISpellModule getModule(String id) {
        return MODULES.get(id);
    }
    
    /**
     * 获取所有已注册模块的ID集合
     * @return 模块ID集合
     */
    public static Set<String> getAllModuleIds() {
        return MODULES.keySet();
    }
    
    /**
     * 检查是否存在指定ID的模块
     * @param id 模块ID
     * @return 是否存在该模块
     */
    public static boolean hasModule(String id) {
        return MODULES.containsKey(id);
    }
}