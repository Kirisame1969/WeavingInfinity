/**
 * 客户端事件处理类
 *
 * 处理客户端相关的事件，如实体渲染器注册等。
 */
package com.kirisame1969.weaving_infinity.client;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.registries.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEvents {
    
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 注册自定义火球实体的渲染器
        event.registerEntityRenderer(ModEntities.CUSTOM_FIREBALL.get(), CustomFireballRenderer::new);
    }
}