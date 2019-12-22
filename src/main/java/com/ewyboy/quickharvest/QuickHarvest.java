package com.ewyboy.quickharvest;

import com.ewyboy.quickharvest.config.Config;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(QuickHarvest.ID)
public class QuickHarvest {

    public static final String ID = "quickharvest";

    public QuickHarvest() {
        MinecraftForge.EVENT_BUS.addListener(HarvestManager::onBlockQuickHarvest);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.settingSpec);
    }
}
