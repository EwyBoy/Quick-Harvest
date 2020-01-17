package com.ewyboy.quickharvest.config;

import com.ewyboy.quickharvest.QuickHarvest;
import com.ewyboy.quickharvest.api.HarvestManager;
import com.ewyboy.quickharvest.api.HarvesterImpl;
import com.ewyboy.quickharvest.api.IHarvester;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static final ForgeConfigSpec settingSpec;
    public static final Settings SETTINGS;

    static {
        final Pair<Settings, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Settings :: new);
        settingSpec = specPair.getRight();
        SETTINGS = specPair.getLeft();
    }

    public static class Settings {

        private final BooleanValue requiresTool;

        Settings(ForgeConfigSpec.Builder builder) {

            builder.comment("Config file for Quick Harvest")
                    .push("general")
            ;

            HarvestManager.forEach(iHarvester -> {
                if (iHarvester instanceof HarvesterImpl) {

                    builder.comment("").push(((HarvesterImpl) iHarvester).getName());

                    ((HarvesterImpl) iHarvester).setEnabled(
                            builder.comment("Disable " +  ((HarvesterImpl) iHarvester).getName() + " harvester.").
                            define(((HarvesterImpl) iHarvester).getName(), true)
                    );

                    builder.pop();
                }
            });

            requiresTool = builder.comment("If set to true, this will require a player to be holding a hoe to quick harvest.")
                    .translation(key("requiresTool"))
                    .define("requiresTool", false)
            ;
        }

        public boolean requiresTool() {
            return requiresTool.get();
        }

        private String key(String key) {
            return String.format("%s.config.%s", QuickHarvest.ID, key);
        }

        public boolean damagesTool() {
            return false; // TODO: add
        }
    }
}
