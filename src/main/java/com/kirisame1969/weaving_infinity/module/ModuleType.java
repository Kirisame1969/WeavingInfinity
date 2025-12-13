package com.kirisame1969.weaving_infinity.module;

/**
 * 模块类型
 */
public enum ModuleType {
    /** 基础模块 - 具有独立效果的模块 (如火球术、冰箭) */
    BASE,
    
    /** 修改器模块 - 必须依附于其他模块的修改器 (如击中分裂、穿透) */
    MODIFIER
}
