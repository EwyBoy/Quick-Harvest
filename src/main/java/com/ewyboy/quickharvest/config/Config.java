package com.ewyboy.quickharvest.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static class Settings {

        Settings(ForgeConfigSpec.Builder builder) {
            builder.comment("Config file for Quick Harvest").push("SETTINGS");
        }

        public static final ForgeConfigSpec settingSpec;
        public static final Settings SETTINGS;

        static {
            final Pair<Settings, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Settings :: new);
            settingSpec = specPair.getRight();
            SETTINGS = specPair.getLeft();
        }
    }
}
