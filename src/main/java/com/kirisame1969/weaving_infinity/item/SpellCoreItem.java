/**
 * 法术核心物品类
 * 
 * 这是模组的核心物品，玩家可以通过它来组合不同的法术模块，
 * 创建自定义的法术效果。支持最多3个模块插槽。
 */
package com.kirisame1969.weaving_infinity.item;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.module.ModuleExecutor;
import com.kirisame1969.weaving_infinity.registry.ModuleRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class SpellCoreItem extends Item {
    // NBT标签中存储模块列表的键名
    private static final String MODULES_TAG = "Modules";
    // 法术核心的最大模块槽数量
    private static final int MAX_SLOTS = 3;

    /**
     * 构造函数
     * @param properties 物品属性
     */
    public SpellCoreItem(Properties properties) {
        super(properties);
    }

    /**
     * 当玩家右键使用物品时调用
     * @param level 世界对象
     * @param player 使用物品的玩家
     * @param hand 使用物品的手
     * @return 交互结果
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            // 执行法术模块
            ModuleExecutor.executeModules(getModules(stack), player, CastSource.SPELLBOOK);
        }
        
        return InteractionResultHolder.success(stack);
    }

    /**
     * 添加物品悬停文本提示
     * @param stack 物品堆
     * @param context 上下文信息
     * @param tooltipComponents 工具提示组件列表
     * @param tooltipFlag 工具提示标志
     */
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        
        List<String> modules = getModuleIds(stack);
        if (modules.isEmpty()) {
            tooltipComponents.add(Component.translatable("item.weaving_infinity.spell_core.empty"));
        } else {
            tooltipComponents.add(Component.translatable("item.weaving_infinity.spell_core.modules"));
            for (int i = 0; i < modules.size(); i++) {
                final int index = i; // 创建一个 effectively final 变量供 lambda 表达式使用
                String moduleId = modules.get(i);
                ModuleRegistry.getModule(ResourceLocation.tryParse(moduleId)).ifPresentOrElse(
                    module -> tooltipComponents.add(Component.literal((index + 1) + ". ").append(module.getDisplayName())),
                    () -> tooltipComponents.add(Component.literal((index + 1) + ". " + moduleId))
                );
            }
        }
    }

    /**
     * 添加模块到法术核心
     * @param stack 法术核心物品堆
     * @param moduleId 模块资源位置
     * @return 是否添加成功
     */
    public static boolean addModule(ItemStack stack, ResourceLocation moduleId) {
        List<String> modules = getModuleIds(stack);
        
        if (modules.size() >= MAX_SLOTS) {
            return false; // 槽位已满
        }
        
        modules.add(moduleId.toString());
        saveModuleIds(stack, modules);
        return true;
    }

    /**
     * 清空法术核心中的所有模块
     * @param stack 法术核心物品堆
     */
    public static void clearModules(ItemStack stack) {
        // 使用新的数据组件API替代旧的NBT API
        CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(new CompoundTag())).copyTag();
        tag.remove(MODULES_TAG);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
    }

    /**
     * 获取法术核心中的模块ID列表
     * @param stack 法术核心物品堆
     * @return 模块ID字符串列表
     */
    public static List<String> getModuleIds(ItemStack stack) {
        List<String> modules = new ArrayList<>();
        
        // 使用新的数据组件API替代旧的NBT API
        if (!stack.isEmpty() && stack.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag();
            if (tag.contains(MODULES_TAG, Tag.TAG_LIST)) {
                ListTag modulesList = tag.getList(MODULES_TAG, Tag.TAG_STRING);
                for (int i = 0; i < modulesList.size(); i++) {
                    modules.add(modulesList.getString(i));
                }
            }
        }
        
        return modules;
    }

    /**
     * 保存模块ID列表到物品NBT数据中
     * @param stack 法术核心物品堆
     * @param moduleIds 模块ID列表
     */
    private static void saveModuleIds(ItemStack stack, List<String> moduleIds) {
        // 使用新的数据组件API替代旧的NBT API
        CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(new CompoundTag())).copyTag();
        ListTag modulesList = new ListTag();
        
        for (String moduleId : moduleIds) {
            modulesList.add(StringTag.valueOf(moduleId));
        }
        
        tag.put(MODULES_TAG, modulesList);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
    }

    /**
     * 获取实际模块对象列表
     * @param stack 法术核心物品堆
     * @return 模块对象列表
     */
    public static List<com.kirisame1969.weaving_infinity.module.ISpellModule> getModules(ItemStack stack) {
        List<com.kirisame1969.weaving_infinity.module.ISpellModule> modules = new ArrayList<>();
        List<String> moduleIds = getModuleIds(stack);
        
        for (String moduleId : moduleIds) {
            ResourceLocation rl = ResourceLocation.tryParse(moduleId);
            if (rl != null) {
                ModuleRegistry.getModule(rl).ifPresent(modules::add);
            }
        }
        
        return modules;
    }
}