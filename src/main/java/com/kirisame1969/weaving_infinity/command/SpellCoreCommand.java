/**
 * 法术核心命令类
 * 
 * 提供与法术核心物品交互的命令，包括添加模块、清空模块和列出模块等功能。
 * 玩家可以通过这些命令来自定义他们的法术组合。
 */
package com.kirisame1969.weaving_infinity.command;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.item.SpellCoreItem;
import com.kirisame1969.weaving_infinity.core.registry.ModuleRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SpellCoreCommand {
    
    /**
     * 注册法术核心命令
     * @param dispatcher 命令调度器
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("spellcore")
                .requires(source -> source.hasPermission(2)) // 需要权限等级2才能使用
                .then(Commands.literal("add")
                    .then(Commands.argument("module", ModuleArgument.moduleArgument())
                        .executes(context -> addModule(context, context.getArgument("module", String.class)))
                    )
                )
                .then(Commands.literal("clear")
                    .executes(SpellCoreCommand::clearModules)
                )
                .then(Commands.literal("list")
                    .executes(SpellCoreCommand::listModules)
                )
        );
    }

    /**
     * 添加模块到法术核心的命令处理函数
     * @param context 命令上下文
     * @param moduleId 模块ID
     * @return 命令执行结果
     */
    private static int addModule(CommandContext<CommandSourceStack> context, String moduleId) {
        CommandSourceStack source = context.getSource();
        try {
            Player player = source.getPlayerOrException();
            ItemStack heldItem = player.getMainHandItem();

            // 检查玩家是否手持法术核心物品
            if (!(heldItem.getItem() instanceof SpellCoreItem)) {
                source.sendFailure(Component.translatable("command.spellcore.not_holding_core"));
                return 0;
            }

            // 如果moduleId不包含命名空间，则默认使用weaving_infinity命名空间
            if (!moduleId.contains(":")) {
                moduleId = WeavingInfinity.MODID + ":" + moduleId;
            }

            ResourceLocation rl = ResourceLocation.tryParse(moduleId);
            if (rl == null) {
                source.sendFailure(Component.translatable("command.spellcore.invalid_module_id"));
                return 0;
            }

            // 检查模块是否存在
            if (!ModuleRegistry.hasModule(moduleId)) {
                source.sendFailure(Component.translatable("command.spellcore.module_not_found"));
                return 0;
            }

            // 尝试添加模块到法术核心
            SpellCoreItem.setModule(heldItem, SpellCoreItem.getModules(heldItem).size(), moduleId);
            String finalModuleId = moduleId; // 创建一个 effectively final 变量供 lambda 表达式使用
            source.sendSuccess(() -> Component.translatable("command.spellcore.module_added", finalModuleId), true);
            return 1;
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.translatable("command.spellcore.player_only"));
            return 0;
        }
    }

    /**
     * 清空法术核心中所有模块的命令处理函数
     * @param context 命令上下文
     * @return 命令执行结果
     */
    private static int clearModules(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            Player player = source.getPlayerOrException();
            ItemStack heldItem = player.getMainHandItem();

            // 检查玩家是否手持法术核心物品
            if (!(heldItem.getItem() instanceof SpellCoreItem)) {
                source.sendFailure(Component.translatable("command.spellcore.not_holding_core"));
                return 0;
            }

            // 清空所有模块
            SpellCoreItem.clearModules(heldItem);
            source.sendSuccess(() -> Component.translatable("command.spellcore.modules_cleared"), true);
            return 1;
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.translatable("command.spellcore.player_only"));
            return 0;
        }
    }

    /**
     * 列出法术核心中所有模块的命令处理函数
     * @param context 命令上下文
     * @return 命令执行结果
     */
    private static int listModules(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            Player player = source.getPlayerOrException();
            ItemStack heldItem = player.getMainHandItem();

            // 检查玩家是否手持法术核心物品
            if (!(heldItem.getItem() instanceof SpellCoreItem)) {
                source.sendFailure(Component.translatable("command.spellcore.not_holding_core"));
                return 0;
            }

            // 获取模块列表并显示
            var modules = SpellCoreItem.getModules(heldItem);
            if (modules.isEmpty()) {
                source.sendSuccess(() -> Component.translatable("command.spellcore.no_modules"), true);
            } else {
                source.sendSuccess(() -> Component.translatable("command.spellcore.modules_list"), true);
                for (int i = 0; i < modules.size(); i++) {
                    final int index = i; // 创建一个 effectively final 变量供 lambda 表达式使用
                    String moduleId = modules.get(index).getId().toString();
                    source.sendSuccess(() -> Component.literal((index + 1) + ". " + moduleId), false);
                }
            }

            return modules.size();
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.translatable("command.spellcore.player_only"));
            return 0;
        }
    }
}