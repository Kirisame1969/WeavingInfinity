/**
 * Weaving Infinity 主类
 * 
 * 这是模组的入口点，负责初始化和注册各种组件。
 * 包括物品、命令、模块等的注册和初始化工作。
 */
package com.kirisame1969.weaving_infinity;

import com.kirisame1969.weaving_infinity.core.registry.ModuleRegistry;
import com.kirisame1969.weaving_infinity.registries.ModItems;
import com.kirisame1969.weaving_infinity.registries.CommandArgumentRegistry;
import com.kirisame1969.weaving_infinity.registries.ModEntities;
import com.kirisame1969.weaving_infinity.common.config.ModuleConfig;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import com.kirisame1969.weaving_infinity.command.SpellCoreCommand;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(WeavingInfinity.MODID)
public class WeavingInfinity {
    // 模组ID，用于标识模组
    public static final String MODID = "weaving_infinity";
    // 日志记录器，用于输出调试和错误信息
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    /**
     * 模组构造函数，在模组加载时调用
     * @param modEventBus 模组事件总线，用于注册各种组件
     */
    public WeavingInfinity(IEventBus modEventBus) {
        // 注册物品
        ModItems.ITEMS.register(modEventBus);
        
        // 注册实体
        ModEntities.ENTITY_TYPES.register(modEventBus);
        
        // 注册命令参数类型
        CommandArgumentRegistry.register(modEventBus);
        
        // 注册模块
        ModuleRegistry.registerBuiltinModules();
        
        modEventBus.addListener(this::commonSetup);
        
        // 注册命令
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        
        // 客户端初始化
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(com.kirisame1969.weaving_infinity.client.ClientEvents::registerRenderers);
        }
    }

    /**
     * 通用设置阶段回调函数
     * @param event 通用设置事件
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Hello from Weaving Infinity!");
        
        // 初始化配置系统
        ModuleConfig.getInstance();
    }
    
    /**
     * 注册命令回调函数
     * @param event 命令注册事件
     */
    private void registerCommands(RegisterCommandsEvent event) {
        SpellCoreCommand.register(event.getDispatcher());
    }

    /**
     * 创建ResourceLocation的便捷方法
     * @param path 资源路径
     * @return 完整的ResourceLocation对象
     */
    public static net.minecraft.resources.ResourceLocation id(String path) {
        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}