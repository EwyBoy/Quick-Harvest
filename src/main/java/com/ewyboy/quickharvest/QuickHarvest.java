package com.ewyboy.quickharvest;

import com.ewyboy.quickharvest.api.HarvestManager;
import com.ewyboy.quickharvest.config.Config;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(QuickHarvest.ID)
public class QuickHarvest {

    public static final String ID = "quickharvest";
    public static final Tag<Item> AXE_TAG = new ItemTags.Wrapper(new ResourceLocation("forge", "tools/axe"));
    public static final Tag<Item> HOE_TAG = new ItemTags.Wrapper(new ResourceLocation("forge", "tools/hoe"));

    public QuickHarvest() {
        MinecraftForge.EVENT_BUS.addListener(HarvestManager :: onBlockQuickHarvest);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.settingSpec);
    }
}
