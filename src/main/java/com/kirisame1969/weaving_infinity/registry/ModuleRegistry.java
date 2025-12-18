/**
 * 模块注册表类
 * 
 * 管理所有已注册的法术模块，提供模块的注册、查询和获取功能。
 * 在模组初始化时注册内置模块。
 */
package com.kirisame1969.weaving_infinity.registry;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.module.ISpellModule;
import com.kirisame1969.weaving_infinity.module.modules.base.FireballModule;
import com.kirisame1969.weaving_infinity.module.modules.modifier.SplitOnHitModifier;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModuleRegistry {
    // 模块映射表，存储所有已注册的模块
    public static final Map<ResourceLocation, ISpellModule> MODULES = new HashMap<>();
    
    /**
     * 注册模块
     * @param id 模块的资源位置ID
     * @param module 模块实例
     */
    public static void register(ResourceLocation id, ISpellModule module) {
        if (MODULES.containsKey(id)) {
            WeavingInfinity.LOGGER.warn("Duplicate module registration: {}", id);
            return;
        }
        MODULES.put(id, module);
        WeavingInfinity.LOGGER.debug("Registered module: {}", id);
    }
    
    /**
     * 根据ID获取模块
     * @param id 模块的资源位置ID
     * @return 模块实例的Optional包装
     */
    public static Optional<ISpellModule> getModule(ResourceLocation id) {
        return Optional.ofNullable(MODULES.get(id));
    }
    
    /**
     * 检查模块是否存在
     * @param id 模块的资源位置ID
     * @return 是否存在该模块
     */
    public static boolean hasModule(ResourceLocation id) {
        return MODULES.containsKey(id);
    }
    
    /**
     * 注册内置模块
     * 在模组初始化时调用，注册所有内置的法术模块
     */
    public static void registerBuiltinModules() {
        WeavingInfinity.LOGGER.info("Registering builtin modules...");
        
        register(WeavingInfinity.id("fireball"), new FireballModule());
        register(WeavingInfinity.id("split_on_hit"), new SplitOnHitModifier());
        
        WeavingInfinity.LOGGER.info("Registered {} modules", MODULES.size());
    }
}