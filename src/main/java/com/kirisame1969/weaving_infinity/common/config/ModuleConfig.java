/**
 * 模块配置类
 * 
 * 用于存储模块的各种可配置参数，支持JSON格式的配置文件。
 * 这样可以方便地调整模块平衡性而无需重新编译代码。
 */
package com.kirisame1969.weaving_infinity.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ModuleConfig {
    // 火球模块配置
    @SerializedName("fireball_module")
    public FireballModuleConfig fireballModule = new FireballModuleConfig();
    
    // 分裂模块配置
    @SerializedName("split_module")
    public SplitModuleConfig splitModule = new SplitModuleConfig();
    
    // 爆炸模块配置
    @SerializedName("explode_module")
    public ExplodeModuleConfig explodeModule = new ExplodeModuleConfig();
    
    // 配置实例
    private static ModuleConfig INSTANCE;
    
    /**
     * 获取配置实例
     * @return 配置实例
     */
    public static ModuleConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = loadConfig();
        }
        return INSTANCE;
    }
    
    /**
     * 从文件加载配置
     * @return 配置实例
     */
    private static ModuleConfig loadConfig() {
        try {
            Path configPath = Path.of("config/weaving_infinity/modules.json");
            if (Files.exists(configPath)) {
                Gson gson = new Gson();
                try (FileReader reader = new FileReader(configPath.toFile())) {
                    return gson.fromJson(reader, ModuleConfig.class);
                }
            } else {
                // 如果配置文件不存在，创建默认配置
                ModuleConfig defaultConfig = new ModuleConfig();
                saveConfig(defaultConfig);
                return defaultConfig;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ModuleConfig(); // 返回默认配置
        }
    }
    
    /**
     * 保存配置到文件
     * @param config 配置实例
     */
    private static void saveConfig(ModuleConfig config) {
        try {
            Path configPath = Path.of("config/weaving_infinity/modules.json");
            Files.createDirectories(configPath.getParent());
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(configPath.toFile())) {
                gson.toJson(config, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 火球模块配置
     */
    public static class FireballModuleConfig {
        @SerializedName("base_damage")
        public float baseDamage = 6.0f;
        
        @SerializedName("split_damage")
        public float splitDamage = 3.0f;
        
        @SerializedName("mana_consumption")
        public int manaConsumption = 10;
        
        @SerializedName("cooldown")
        public int cooldown = 20;
        
        @SerializedName("complexity")
        public float complexity = 1.0f;
    }
    
    /**
     * 分裂模块配置
     */
    public static class SplitModuleConfig {
        @SerializedName("split_count")
        public int splitCount = 3;
        
        @SerializedName("angle_between_shots")
        public float angleBetweenShots = 120.0f;
        
        @SerializedName("mana_consumption")
        public int manaConsumption = 5;
        
        @SerializedName("cooldown")
        public int cooldown = 10;
        
        @SerializedName("complexity")
        public float complexity = 1.5f;
    }
    
    /**
     * 爆炸模块配置
     */
    public static class ExplodeModuleConfig {
        @SerializedName("explosion_radius")
        public float explosionRadius = 2.0f;
        
        @SerializedName("explosion_damage")
        public float explosionDamage = 10.0f;
        
        @SerializedName("mana_consumption")
        public int manaConsumption = 8;
        
        @SerializedName("cooldown")
        public int cooldown = 15;
        
        @SerializedName("complexity")
        public float complexity = 2.0f;
    }
}