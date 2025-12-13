package com.kirisame1969.weaving_infinity.core;

import io.redspace.ironsspellbooks.api.spells.CastType;

/**
 * 法术核心类型 - 决定施法方式
 */
public enum SpellCoreType {
    /** 瞬发核心 - 无施法时间 */
    INSTANT("instant", CastType.INSTANT, 5, 1.0f, 1.0f),
    
    /** 蓄力核心 - 需要蓄力后释放 */
    CHARGE("charge", CastType.LONG, 7, 0.8f, 1.2f),
    
    /** 持续核心 - 持续施法 */
    CONTINUOUS("continuous", CastType.CONTINUOUS, 6, 1.2f, 0.9f),
    
    /** 多段核心 - 支持多段独立施法 */
    MULTICAST("multicast", CastType.INSTANT, 8, 1.1f, 1.1f);

    private final String id;
    private final CastType castType;
    private final int baseSlots;           // 基础槽位数
    private final float cooldownMult;      // 冷却倍率
    private final float spellPowerMult;    // 法术强度倍率

    SpellCoreType(String id, CastType castType, int baseSlots, float cooldownMult, float spellPowerMult) {
        this.id = id;
        this.castType = castType;
        this.baseSlots = baseSlots;
        this.cooldownMult = cooldownMult;
        this.spellPowerMult = spellPowerMult;
    }

    public String getId() {
        return id;
    }

    public CastType getCastType() {
        return castType;
    }

    public int getBaseSlots() {
        return baseSlots;
    }

    public float getCooldownMultiplier() {
        return cooldownMult;
    }

    public float getSpellPowerMultiplier() {
        return spellPowerMult;
    }

    public static SpellCoreType fromString(String id) {
        for (SpellCoreType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return INSTANT;
    }
}
