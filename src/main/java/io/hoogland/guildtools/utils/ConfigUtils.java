package io.hoogland.guildtools.utils;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class ConfigUtils {

    private static Map<String, Object> config = null;

    public static Map<String, Object> loadConfiguration() {
        Yaml yaml = new Yaml();
        config = yaml.load(ConfigUtils.class
                .getClassLoader().getResourceAsStream("config.yml"));
        return config;
    }

    public static Map<String, Object> getConfig() {
        return config;
    }
}
