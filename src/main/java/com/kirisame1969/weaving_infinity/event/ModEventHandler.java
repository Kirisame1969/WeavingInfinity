/**
 * 模组事件处理器
 * 
 * 处理游戏中的各种事件，例如弹射物撞击事件。
 * 主要用于触发法术模块的特殊效果，如火球击中目标时的分裂效果。
 */
package com.kirisame1969.weaving_infinity.event;

import com.kirisame1969.weaving_infinity.WeavingInfinity;
import com.kirisame1969.weaving_infinity.item.SpellCoreItem;
import com.kirisame1969.weaving_infinity.module.modules.modifier.SplitOnHitModifier;
import com.kirisame1969.weaving_infinity.module.ISpellModule;
import io.redspace.ironsspellbooks.entity.spells.fireball.SmallMagicFireball;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.minecraft.world.level.Level;

@EventBusSubscriber(modid = WeavingInfinity.MODID)
public class ModEventHandler {
    
    /**
     * 监听弹射物撞击事件
     * 当火球击中目标时，检查是否需要触发分裂效果
     * @param event 弹射物撞击事件
     */
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        // 检查是否是我们模组的火球，并且不是分裂产生的火球
        if (event.getProjectile() instanceof SmallMagicFireball fireball && 
            fireball.getTags().contains("weaving_infinity_fireball") &&
            !fireball.getTags().contains("weaving_infinity_split_fireball")) {
            
            // 触发火球击中效果
            Level level = fireball.level();
            var owner = fireball.getOwner();
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel && owner instanceof LivingEntity livingOwner) {
                // 检查施法者是否持有法术核心物品
                var mainHandItem = livingOwner.getMainHandItem();
                if (mainHandItem.getItem() instanceof SpellCoreItem) {
                    // 获取模块列表并触发火球击中事件
                    var modules = SpellCoreItem.getModules(mainHandItem);
                    
                    // 查找第一个基础模块作为原始模块
                    ISpellModule originalModule = null;
                    for (ISpellModule module : modules) {
                        if (module.getModuleType() == com.kirisame1969.weaving_infinity.module.ModuleType.BASE) {
                            originalModule = module;
                            break;
                        }
                    }
                    
                    // 如果找到了基础模块且有分裂修饰模块，则触发分裂效果
                    if (originalModule != null && hasSplitModule(modules)) {
                        // 创建模块执行上下文
                        var context = new com.kirisame1969.weaving_infinity.module.ModuleExecutionContext(
                            level, livingOwner, io.redspace.ironsspellbooks.api.spells.CastSource.SPELLBOOK);
                        
                        // 触发通用的分裂效果
                        SplitOnHitModifier.createSplitEffect(
                            serverLevel, 
                            livingOwner, 
                            event.getRayTraceResult() instanceof net.minecraft.world.phys.EntityHitResult entityResult ? 
                                entityResult.getEntity() : null,
                            originalModule,
                            context
                        );
                    }
                }
            }
        }
    }
    
    /**
     * 检查模块列表中是否包含分裂模块
     * @param modules 模块列表
     * @return 是否包含分裂模块
     */
    private static boolean hasSplitModule(java.util.List<ISpellModule> modules) {
        for (ISpellModule module : modules) {
            if (module.getModuleType() == com.kirisame1969.weaving_infinity.module.ModuleType.MODIFIER && 
                module.getId().toString().equals("weaving_infinity:split_on_hit")) {
                return true;
            }
        }
        return false;
    }
}