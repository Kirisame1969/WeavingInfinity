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
import com.kirisame1969.weaving_infinity.module.modules.modifier.ExplodeOnHitModifier;
import com.kirisame1969.weaving_infinity.api.module.ISpellModule;
import io.redspace.ironsspellbooks.entity.spells.fireball.SmallMagicFireball;
import com.kirisame1969.weaving_infinity.common.entity.CustomFireball;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.minecraft.world.level.Level;

@EventBusSubscriber(modid = WeavingInfinity.MODID)
public class ModEventHandler {
    
    /**
     * 监听弹射物撞击事件
     * 当火球击中目标时，检查是否需要触发分裂效果或爆炸效果
     * @param event 弹射物撞击事件
     */
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        // 检查是否是我们模组的火球，并且不是分裂产生的火球
        Projectile projectile = event.getProjectile();
        
        // 处理SmallMagicFireball（向后兼容）
        if (projectile instanceof SmallMagicFireball fireball && 
            fireball.getTags().contains("weaving_infinity_fireball") &&
            !fireball.getTags().contains("weaving_infinity_split_fireball")) {
            
            processFireballImpact(fireball.level(), fireball.getOwner(), event.getRayTraceResult());
        }
        // 处理自定义火球
        else if (projectile instanceof CustomFireball customFireball &&
                 customFireball.getTags().contains("weaving_infinity_fireball") &&
                 !customFireball.getTags().contains("weaving_infinity_split_fireball")) {
            
            // 自定义火球通过实体内部方法处理，不需要在这里重复处理
        }
    }
    
    /**
     * 处理自定义火球的撞击事件（由CustomFireball实体直接调用）
     * @param fireball 自定义火球实体
     * @param hitPos 撞击位置
     */
    public static void onCustomFireballHit(CustomFireball fireball, Vec3 hitPos) {
        processFireballImpact(fireball.level(), fireball.getOwner(), hitPos);
    }
    
    /**
     * 统一处理火球撞击的通用逻辑
     * @param level 世界
     * @param owner 火球拥有者
     * @param hitResult 撞击结果
     */
    private static void processFireballImpact(Level level, net.minecraft.world.entity.Entity owner, HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.MISS) {
            return;
        }
        
        Vec3 hitPos = getHitPosition(hitResult);
        processFireballImpact(level, owner, hitPos);
    }
    
    /**
     * 统一处理火球撞击的通用逻辑（重载版本，直接接受撞击位置）
     * @param level 世界
     * @param owner 火球拥有者
     * @param hitPos 撞击位置
     */
    private static void processFireballImpact(Level level, net.minecraft.world.entity.Entity owner, Vec3 hitPos) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            // 检查施法者是否持有法术核心物品
            LivingEntity livingOwner = null;
            if (owner instanceof LivingEntity) {
                livingOwner = (LivingEntity) owner;
            }
            
            var mainHandItem = livingOwner != null ? livingOwner.getMainHandItem() : null;
            if (mainHandItem != null && mainHandItem.getItem() instanceof SpellCoreItem) {
                // 获取模块列表并触发火球击中事件
                var modules = SpellCoreItem.getModules(mainHandItem);
                
                // 查找第一个基础模块作为原始模块
                ISpellModule originalModule = null;
                for (ISpellModule module : modules) {
                    if (module.getModuleType() == com.kirisame1969.weaving_infinity.api.module.ModuleType.BASE) {
                        originalModule = module;
                        break;
                    }
                }
                
                // 检查是否有爆炸模块，如果有则触发爆炸效果
                if (hasExplodeModule(modules)) {
                    ExplodeOnHitModifier.createExplosionEffect(serverLevel, hitPos);
                }
                
                // 如果找到了基础模块且有分裂修饰模块，则触发分裂效果
                if (originalModule != null && hasSplitModule(modules)) {
                    // 如果施法者为null，使用火球的位置作为备用
                    if (livingOwner == null) {
                        livingOwner = getNearbyLivingEntity(serverLevel, hitPos);
                    }
                    
                    // 确保我们有有效的施法者
                    if (livingOwner != null) {
                        // 创建模块执行上下文
                        var context = new com.kirisame1969.weaving_infinity.api.module.ModuleExecutionContext(
                            level, livingOwner, io.redspace.ironsspellbooks.api.spells.CastSource.SPELLBOOK);
                        
                        // 触发通用的分裂效果
                        SplitOnHitModifier.createSplitEffect(
                            serverLevel, 
                            livingOwner, 
                            hitPos,
                            originalModule,
                            context
                        );
                    }
                }
            }
        }
    }
    
    /**
     * 获取撞击位置
     * @param hitResult 撞击结果
     * @return 撞击位置
     */
    private static Vec3 getHitPosition(HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHitResult) {
            return entityHitResult.getEntity().position();
        } else if (hitResult instanceof BlockHitResult blockHitResult) {
            return blockHitResult.getBlockPos().getCenter();
        } else {
            return Vec3.ZERO;
        }
    }
    
    /**
     * 在指定位置附近查找活着的实体作为备用施法者
     * @param level 服务器世界
     * @param pos 位置
     * @return 附近的活体实体
     */
    private static LivingEntity getNearbyLivingEntity(net.minecraft.server.level.ServerLevel level, Vec3 pos) {
        // 查找附近2格范围内的玩家作为备用施法者
        var players = level.getEntitiesOfClass(net.minecraft.world.entity.player.Player.class, 
            net.minecraft.world.phys.AABB.ofSize(pos, 2, 2, 2));
        if (!players.isEmpty()) {
            return players.get(0);
        }
        return null;
    }
    
    /**
     * 检查模块列表中是否包含分裂模块
     * @param modules 模块列表
     * @return 是否包含分裂模块
     */
    private static boolean hasSplitModule(java.util.List<ISpellModule> modules) {
        for (ISpellModule module : modules) {
            if (module.getModuleType() == com.kirisame1969.weaving_infinity.api.module.ModuleType.MODIFIER && 
                module.getId().toString().equals("weaving_infinity:split_on_hit")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查模块列表中是否包含爆炸模块
     * @param modules 模块列表
     * @return 是否包含爆炸模块
     */
    private static boolean hasExplodeModule(java.util.List<ISpellModule> modules) {
        for (ISpellModule module : modules) {
            if (module.getModuleType() == com.kirisame1969.weaving_infinity.api.module.ModuleType.MODIFIER && 
                module.getId().toString().equals("weaving_infinity:explode_on_hit")) {
                return true;
            }
        }
        return false;
    }
}