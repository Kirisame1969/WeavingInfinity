/**
 * 模块类型枚举
 * 
 * 定义法术模块的两种基本类型：
 * 1. BASE（基础模块）：提供核心功能
 * 2. MODIFIER（修饰模块）：修改其他模块的行为
 */
package com.kirisame1969.weaving_infinity.api.module;

public enum ModuleType {
    BASE,
    MODIFIER
}