package com.ewyboy.quickharvest;

import com.ewyboy.quickharvest.api.Harvester;
import com.ewyboy.quickharvest.config.Config;
import net.minecraft.resources.ResourceLocation;
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
    private static final Logger LOGGER = LogManager.getLogger();

    public QuickHarvest() {
        ModLoadingContext.get().registerConfig(Type.SERVER, Config.SERVER);
    }

    public static final class Registries {
        public static final IForgeRegistry<Harvester> HARVESTERS = RegistryManager.ACTIVE.getRegistry(new ResourceLocation(ID, "harvesters"));
    }

}
