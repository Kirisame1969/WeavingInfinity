package com.kirisame1969.weaving_infinity.core;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

/**
 * 法术核心的属性数据
 */
public class SpellCoreProperties {
    private SpellCoreType coreType;
    private SchoolType preferredSchool;
    private int slots;
    private float schoolBonus;          // 专精学派加成（0.0-1.0）
    private float globalSpellPower;     // 全局法术强度加成
    private float globalCooldownMult;   // 全局冷却倍率（<1.0减少冷却）
    private float globalManaCostMult;   // 全局法力消耗倍率
    
    public SpellCoreProperties() {
        this(SpellCoreType.INSTANT, SchoolRegistry.FIRE.get(), 5, 0.0f, 0.0f, 1.0f, 1.0f);
    }
    
    public SpellCoreProperties(SpellCoreType coreType, SchoolType preferredSchool, int slots,
                               float schoolBonus, float globalSpellPower,
                               float globalCooldownMult, float globalManaCostMult) {
        this.coreType = coreType;
        this.preferredSchool = preferredSchool;
        this.slots = slots;
        this.schoolBonus = schoolBonus;
        this.globalSpellPower = globalSpellPower;
        this.globalCooldownMult = globalCooldownMult;
        this.globalManaCostMult = globalManaCostMult;
    }
    
    // ===== Getters =====
    
    public SpellCoreType getCoreType() {
        return coreType;
    }
    
    public SchoolType getPreferredSchool() {
        return preferredSchool;
    }
    
    public int getSlots() {
        return slots;
    }
    
    public float getSchoolBonus() {
        return schoolBonus;
    }
    
    public float getGlobalSpellPower() {
        return globalSpellPower;
    }
    
    public float getGlobalCooldownMultiplier() {
        return globalCooldownMult;
    }
    
    public float getGlobalManaCostMultiplier() {
        return globalManaCostMult;
    }
    
    // ===== NBT序列化 =====
    
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("core_type", coreType.getId());
        tag.putString("preferred_school", preferredSchool.getId().toString());
        tag.putInt("slots", slots);
        tag.putFloat("school_bonus", schoolBonus);
        tag.putFloat("global_spell_power", globalSpellPower);
        tag.putFloat("global_cooldown_mult", globalCooldownMult);
        tag.putFloat("global_mana_cost_mult", globalManaCostMult);
        return tag;
    }
    
    public static SpellCoreProperties fromNBT(CompoundTag tag) {
        SpellCoreType coreType = SpellCoreType.fromString(tag.getString("core_type"));
        SchoolType school = SchoolRegistry.getSchool(
            ResourceLocation.parse(tag.getString("preferred_school"))
        );
        int slots = tag.getInt("slots");
        float schoolBonus = tag.getFloat("school_bonus");
        float globalSpellPower = tag.getFloat("global_spell_power");
        float globalCooldownMult = tag.getFloat("global_cooldown_mult");
        float globalManaCostMult = tag.getFloat("global_mana_cost_mult");
        
        return new SpellCoreProperties(coreType, school, slots, schoolBonus,
            globalSpellPower, globalCooldownMult, globalManaCostMult);
    }
    
    @Override
    public String toString() {
        return String.format("SpellCore{type=%s, school=%s, slots=%d, bonus=%.2f}",
            coreType.getId(), preferredSchool.getId(), slots, schoolBonus);
    }
}
