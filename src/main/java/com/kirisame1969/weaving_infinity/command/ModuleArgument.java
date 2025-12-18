/**
 * 模块参数类型类
 * 
 * 自定义命令参数类型，用于处理模块ID的输入和自动补全。
 * 支持带命名空间和不带命名空间的模块ID输入。
 */
package com.kirisame1969.weaving_infinity.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.registry.ModuleRegistry;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModuleArgument implements ArgumentType<String> {
    // 示例值，用于命令帮助信息
    private static final List<String> EXAMPLES = Arrays.asList("fireball", "weaving_infinity:fireball");

    /**
     * 创建模块参数实例的工厂方法
     * @return 新的模块参数实例
     */
    public static ModuleArgument moduleArgument() {
        return new ModuleArgument();
    }

    /**
     * 解析命令参数
     * @param reader 字符串读取器
     * @return 解析得到的模块ID字符串
     * @throws CommandSyntaxException 解析失败时抛出
     */
    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();

        // 读取合法的资源位置字符
        while (reader.canRead() && ResourceLocation.isAllowedInResourceLocation(reader.peek())) {
            reader.skip();
        }

        return reader.getString().substring(i, reader.getCursor());
    }

    /**
     * 提供命令参数的自动补全建议
     * @param context 命令上下文
     * @param builder 建议构建器
     * @param <S> 命令源类型
     * @return 异步建议结果
     */
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        // 为模块注册表中的所有模块提供自动补全建议
        return SharedSuggestionProvider.suggest(ModuleRegistry.MODULES.keySet().stream()
            .map(rl -> rl.getNamespace().equals(WeavingInfinity.MODID) ? rl.getPath() : rl.toString())
            .toArray(String[]::new), builder);
    }

    /**
     * 获取示例值
     * @return 示例值列表
     */
    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}