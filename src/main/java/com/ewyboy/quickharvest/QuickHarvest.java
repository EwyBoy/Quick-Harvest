package com.ewyboy.quickharvest;

import com.ewyboy.quickharvest.api.Harvester;
import com.ewyboy.quickharvest.config.Config;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(QuickHarvest.ID)
public class QuickHarvest {
    public static final String ID = "quickharvest";
    public static final ITag.INamedTag<Item> AXE_TAG = ItemTags.makeWrapperTag("forge:tools/axe");
    public static final ITag.INamedTag<Item> HOE_TAG = ItemTags.makeWrapperTag("forge:tools/hoe");
    private static final Logger LOGGER = LogManager.getLogger();

    public QuickHarvest() {
        LOGGER.debug("Registering Configs");
        ModLoadingContext.get().registerConfig(Type.SERVER, Config.SERVER);
    }

    public static final class Registries {
        public static final IForgeRegistry<Harvester> HARVESTERS = RegistryManager.ACTIVE.getRegistry(new ResourceLocation(ID, "harvesters"));
    }
}
