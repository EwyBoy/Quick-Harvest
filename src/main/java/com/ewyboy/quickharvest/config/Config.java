package com.ewyboy.quickharvest.config;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.Items;
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

        DEFAULT = new HarvesterConfig("default", serverSpec, false, false, false, null, "minecraft:air", ImmutableList.of());
        WHEAT = new HarvesterConfig("wheat", serverSpec, true, false, true, ToolType.HOE, "minecraft:wheat_seeds", ImmutableList.of());
        CARROTS = new HarvesterConfig("carrots", serverSpec, true, false, true, ToolType.HOE, "minecraft:carrot", ImmutableList.of());
        POTATOES = new HarvesterConfig("potatoes", serverSpec, true, false, true, ToolType.HOE, "minecraft:potato", ImmutableList.of());
        BEETROOTS = new HarvesterConfig("beetroots", serverSpec, true, false, true, ToolType.HOE, "minecraft:beetroot_seeds", ImmutableList.of());
        CHORUS = new HarvesterConfig("chorus", serverSpec, true, false, true, ToolType.AXE, "minecraft:chorus_flower", ImmutableList.of());
        COCOA = new HarvesterConfig("cocoa", serverSpec, true, false, true, ToolType.AXE, "minecraft:cocoa_beans", ImmutableList.of());
        KELP = new HarvesterConfig("kelp", serverSpec, true, false, false, null, "minecraft:air", ImmutableList.of());
        SUGAR_CANE = new HarvesterConfig("sugar_cane", serverSpec, true, false, false, null, "minecraft:air", ImmutableList.of());
        CACTUS = new HarvesterConfig("cactus", serverSpec, true, false, false, ToolType.AXE, "minecraft:air", ImmutableList.of());
        MELON = new HarvesterConfig("melon", serverSpec, true, false, false, ToolType.AXE, "minecraft:air", ImmutableList.of());
        PUMPKIN = new HarvesterConfig("pumpkin", serverSpec, true, false, false, ToolType.AXE, "minecraft:air", ImmutableList.of(Items.SHEARS));
        NETHER_WART = new HarvesterConfig("nether_wart", serverSpec, true, false, true, ToolType.HOE, "minecraft:nether_wart", ImmutableList.of());
        BERRY_BUSH = new HarvesterConfig("berry_bush", serverSpec, true, false, true, null, "minecraft:sweet_berries", ImmutableList.of());
        TWISTING_VINES = new HarvesterConfig("twisting_vines", serverSpec, true, false, false, null, "minecraft:air", ImmutableList.of());
        WEEPING_VINES = new HarvesterConfig("weeping_vines", serverSpec, true, false, false, null, "minecraft:air", ImmutableList.of());

        SERVER = serverSpec.build();
    }
}
