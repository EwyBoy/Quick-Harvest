package com.ewyboy.quickharvest;

import com.ewyboy.quickharvest.config.Config;
import com.ewyboy.quickharvest.events.HarvestEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("quickharvest")
public class QuickHarvest {

    private static final Logger LOGGER = LogManager.getLogger();

    public QuickHarvest() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, HarvestEvent :: onBlockQuickHarvest);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.Settings.settingSpec);
    }
}
