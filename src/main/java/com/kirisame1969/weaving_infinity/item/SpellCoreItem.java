/**
 * 法术核心物品
 * 
 * 玩家可以在这个物品中插入法术模块，形成自定义的法术组合。
 * 右键使用时会按照顺序执行所有已安装的模块。
 */
package com.kirisame1969.weaving_infinity.item;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.api.module.ISpellModule;
import com.kirisame1969.weaving_infinity.api.module.ModuleExecutionContext;
import com.kirisame1969.weaving_infinity.core.ModuleExecutor;
import com.kirisame1969.weaving_infinity.core.registry.ModuleRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.ArrayList;
import java.util.List;

public class SpellCoreItem extends Item {
    // 物品的最大模块槽数量
    private static final int MAX_SLOTS = 3;
    
    public SpellCoreItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            // 获取已安装的模块列表
            List<ISpellModule> modules = getModules(itemStack);
            
            if (!modules.isEmpty()) {
                // 创建执行上下文
                ModuleExecutionContext context = new ModuleExecutionContext(
                    level, 
                    player, 
                    io.redspace.ironsspellbooks.api.spells.CastSource.SPELLBOOK
                );
                
                // 执行所有模块
                ModuleExecutor.executeModules(modules, context);
            }
        }
        
        return InteractionResultHolder.success(itemStack);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        
        // 显示已安装的模块
        List<ISpellModule> modules = getModules(stack);
        if (modules.isEmpty()) {
            tooltipComponents.add(Component.translatable("item.weaving_infinity.spell_core.no_modules"));
        } else {
            tooltipComponents.add(Component.translatable("item.weaving_infinity.spell_core.modules"));
            for (ISpellModule module : modules) {
                tooltipComponents.add(Component.literal("- ").append(module.getDisplayName()));
            }
        }
        
        // 显示剩余槽位
        int remainingSlots = MAX_SLOTS - modules.size();
        tooltipComponents.add(Component.translatable("item.weaving_infinity.spell_core.remaining_slots", remainingSlots));
    }
    
    /**
     * 获取物品中存储的模块列表
     * @param stack 法术核心物品堆
     * @return 模块列表
     */
    public static List<ISpellModule> getModules(ItemStack stack) {
        List<ISpellModule> modules = new ArrayList<>();
        
        CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        if (tag != null && tag.contains("modules", net.minecraft.nbt.Tag.TAG_LIST)) {
            ListTag moduleList = tag.getList("modules", net.minecraft.nbt.Tag.TAG_STRING);
            for (int i = 0; i < moduleList.size(); i++) {
                String moduleId = moduleList.getString(i);
                ISpellModule module = ModuleRegistry.getModule(moduleId);
                if (module != null) {
                    modules.add(module);
                }
            }
        }
        
        return modules;
    }
    
    /**
     * 在指定位置设置模块
     * @param stack 法术核心物品堆
     * @param index 槽位索引
     * @param moduleId 模块ID
     */
    public static void setModule(ItemStack stack, int index, String moduleId) {
        if (index < 0 || index >= MAX_SLOTS) {
            return;
        }
        
        // 检查模块是否存在
        if (!ModuleRegistry.hasModule(moduleId)) {
            WeavingInfinity.LOGGER.warn("尝试设置不存在的模块: {}", moduleId);
            return;
        }
        
        // 获取或创建NBT数据
        CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        if (tag.isEmpty()) {
            tag = new CompoundTag();
        }
        
        // 获取或创建模块列表
        ListTag moduleList;
        if (tag.contains("modules", net.minecraft.nbt.Tag.TAG_LIST)) {
            moduleList = tag.getList("modules", net.minecraft.nbt.Tag.TAG_STRING);
        } else {
            moduleList = new ListTag();
            tag.put("modules", moduleList);
        }
        
        // 扩展列表到足够长度
        while (moduleList.size() <= index) {
            moduleList.add(StringTag.valueOf(""));
        }
        
        // 设置模块
        moduleList.set(index, StringTag.valueOf(moduleId));
        
        // 更新标签
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
    }
    
    /**
     * 移除指定位置的模块
     * @param stack 法术核心物品堆
     * @param index 槽位索引
     */
    public static void removeModule(ItemStack stack, int index) {
        if (index < 0 || index >= MAX_SLOTS) {
            return;
        }
        
        CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        if (tag != null && tag.contains("modules", net.minecraft.nbt.Tag.TAG_LIST)) {
            ListTag moduleList = tag.getList("modules", net.minecraft.nbt.Tag.TAG_STRING);
            if (index < moduleList.size()) {
                moduleList.set(index, StringTag.valueOf(""));
                stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
            }
        }
    }
    
    /**
     * 清空所有模块
     * @param stack 法术核心物品堆
     */
    public static void clearModules(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        if (tag != null && tag.contains("modules")) {
            tag.remove("modules");
            stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
        }
    }
    
    /**
     * 获取最大模块槽数量
     * @return 最大槽数
     */
    public static int getMaxSlots() {
        return MAX_SLOTS;
    }
}