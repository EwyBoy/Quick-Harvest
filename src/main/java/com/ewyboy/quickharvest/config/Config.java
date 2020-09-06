package com.ewyboy.quickharvest.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ToolType;

public class Config {

    public static final ForgeConfigSpec SERVER;

    public static final HarvesterConfig DEFAULT;
    public static final HarvesterConfig WHEAT;
    public static final HarvesterConfig CARROTS;
    public static final HarvesterConfig POTATOES;
    public static final HarvesterConfig BEETROOTS;
    public static final HarvesterConfig CHORUS;
    public static final HarvesterConfig COCOA;
    public static final HarvesterConfig KELP;
    public static final HarvesterConfig SUGAR_CANE;
    public static final HarvesterConfig CACTUS;
    public static final HarvesterConfig MELON;
    public static final HarvesterConfig PUMPKIN;
    public static final HarvesterConfig NETHER_WART;
    public static final HarvesterConfig BERRY_BUSH;
    public static final HarvesterConfig TWISTING_VINES;
    public static final HarvesterConfig WEEPING_VINES;

    static {
        Builder serverSpec = new Builder();

        DEFAULT = new HarvesterConfig("default", serverSpec, false, false, false, null, "minecraft:air");
        WHEAT = new HarvesterConfig("wheat", serverSpec, true, false, true, ToolType.HOE, "minecraft:wheat_seeds");
        CARROTS = new HarvesterConfig("carrots", serverSpec, true, false, true, ToolType.HOE, "minecraft:carrot");
        POTATOES = new HarvesterConfig("potatoes", serverSpec, true, false, true, ToolType.HOE, "minecraft:potato");
        BEETROOTS = new HarvesterConfig("beetroots", serverSpec, true, false, true, ToolType.HOE, "minecraft:beetroot_seeds");
        CHORUS = new HarvesterConfig("chorus", serverSpec, true, false, true, ToolType.AXE, "minecraft:chorus_flower");
        COCOA = new HarvesterConfig("cocoa", serverSpec, true, false, true, ToolType.AXE, "minecraft:cocoa_beans");
        KELP = new HarvesterConfig("kelp", serverSpec, true, false, false, null, "minecraft:air");
        SUGAR_CANE = new HarvesterConfig("sugar_cane", serverSpec, true, false, false, null, "minecraft:air");
        CACTUS = new HarvesterConfig("cactus", serverSpec, true, false, false, ToolType.AXE, "minecraft:air");
        MELON = new HarvesterConfig("melon", serverSpec, true, false, false, ToolType.AXE, "minecraft:air");
        PUMPKIN = new HarvesterConfig("pumpkin", serverSpec, true, false, false, ToolType.AXE, "minecraft:air");
        NETHER_WART = new HarvesterConfig("nether_wart", serverSpec, true, false, true, ToolType.HOE, "minecraft:nether_wart");
        BERRY_BUSH = new HarvesterConfig("berry_bush", serverSpec, true, false, true, null, "minecraft:sweet_berries");
        TWISTING_VINES = new HarvesterConfig("twisting_vines", serverSpec, true, false, false, null, "minecraft:air");
        WEEPING_VINES = new HarvesterConfig("weeping_vines", serverSpec, true, false, false, null, "minecraft:air");

        SERVER = serverSpec.build();
    }
}
