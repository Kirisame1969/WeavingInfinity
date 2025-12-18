/**
 * 命令参数类型注册类
 * 
 * 负责注册自定义的命令参数类型，例如模块参数类型。
 * 使命令系统能够正确解析和处理模块ID等自定义参数。
 */
package com.kirisame1969.weaving_infinity.registries;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.command.ModuleArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CommandArgumentRegistry {
    // 命令参数类型延迟注册器
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, WeavingInfinity.MODID);

    // 模块命令参数类型注册
    private static final Supplier<SingletonArgumentInfo<ModuleArgument>> MODULE_COMMAND_ARGUMENT_TYPE = ARGUMENT_TYPES.register("module", () -> ArgumentTypeInfos.registerByClass(ModuleArgument.class, SingletonArgumentInfo.contextFree(ModuleArgument::moduleArgument)));

    /**
     * 注册命令参数类型
     * @param modEventBus 模组事件总线
     */
    public static void register(IEventBus modEventBus) {
        ARGUMENT_TYPES.register(modEventBus);
    }
}