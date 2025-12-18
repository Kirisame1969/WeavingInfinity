/**
 * 模组物品注册类
 * 
 * 负责注册模组中的所有物品，包括法术核心等。
 * 使用延迟注册机制确保物品在正确的时机被注册。
 */
package com.kirisame1969.weaving_infinity.registries;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.item.SpellCoreItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    // 物品延迟注册器
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, WeavingInfinity.MODID);
    
    // 法术核心物品注册
    public static final Supplier<Item> SPELL_CORE = ITEMS.register("spell_core", 
        () -> new SpellCoreItem(new Item.Properties().stacksTo(1)));
}