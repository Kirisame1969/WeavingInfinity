package com.kirisame1969.weaving_infinity.core;

/**
 * 墨水系统计算器
 * 处理魔力预算、复杂度倍率等计算
 */
public class InkCalculator {
    // 墨水等级预算值
    private static final int[] INK_BASE_BUDGETS = {
        100,  // Common
        200,  // Uncommon
        300,  // Rare
        400,  // Epic
        500   // Legendary
    };
    
    /**
     * 墨水计算结果
     */
    public static class InkResult {
        public final int totalBudget;           // 总魔力预算
        public final float complexityMultiplier; // 复杂度倍率
        
        public InkResult(int totalBudget, float complexityMultiplier) {
            this.totalBudget = totalBudget;
            this.complexityMultiplier = complexityMultiplier;
        }
    }
    
    /**
     * 计算墨水提供的魔力预算和复杂度倍率
     * 
     * @param inkLevel 墨水等级 (0=Common, 1=Uncommon, 2=Rare, 3=Epic, 4=Legendary)
     * @param inkCount 墨水数量
     * @return 计算结果
     */
    public static InkResult calculate(int inkLevel, int inkCount) {
        if (inkCount <= 0) {
            return new InkResult(0, 1.0f);
        }
        
        inkLevel = Math.max(0, Math.min(4, inkLevel)); // 限制在 0-4 范围
        int baseBudget = INK_BASE_BUDGETS[inkLevel];
        
        int totalBudget = baseBudget; // 第一瓶全额
        float complexityMult = 1.0f;
        
        // 额外的墨水只提供 1/3 预算，并增加复杂度
        for (int i = 1; i < inkCount; i++) {
            totalBudget += baseBudget / 3;
            complexityMult += 1.0f / 3.0f;
        }
        
        return new InkResult(totalBudget, complexityMult);
    }
    
    /**
     * 获取指定等级的基础预算值
     */
    public static int getBaseBudget(int inkLevel) {
        inkLevel = Math.max(0, Math.min(4, inkLevel));
        return INK_BASE_BUDGETS[inkLevel];
    }
    
    /**
     * 计算剩余预算转换的耐久度
     * 
     * @param totalBudget 总预算
     * @param usedBudget 已使用预算
     * @return 初始耐久度
     */
    public static int calculateInitialDurability(int totalBudget, int usedBudget) {
        return Math.max(0, totalBudget - usedBudget);
    }
    
    /**
     * 计算最大耐久度（等于总预算）
     */
    public static int calculateMaxDurability(int totalBudget) {
        return totalBudget;
    }
}
