/**
 * 实体注册类
 * 
 * 负责注册模组中的所有实体类型。
 */
package com.kirisame1969.weaving_infinity.registries;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.common.entity.CustomFireball;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, WeavingInfinity.MODID);

    // 注册自定义火球实体
    public static final Supplier<EntityType<CustomFireball>> CUSTOM_FIREBALL = ENTITY_TYPES.register("custom_fireball",
            () -> EntityType.Builder.<CustomFireball>of(CustomFireball::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("custom_fireball"));
}