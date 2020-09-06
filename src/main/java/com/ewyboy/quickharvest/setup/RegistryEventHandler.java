package com.ewyboy.quickharvest.setup;

import com.ewyboy.quickharvest.QuickHarvest;
import com.ewyboy.quickharvest.api.Harvester;
import com.ewyboy.quickharvest.config.Config;
import com.ewyboy.quickharvest.harvester.*;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber(bus = Bus.MOD, modid = QuickHarvest.ID)
public class RegistryEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onNewRegistry(final RegistryEvent.NewRegistry event) {
        LOGGER.debug("Creating Harvester Registry");
        new RegistryBuilder<Harvester>()
                .setName(new ResourceLocation(QuickHarvest.ID, "harvesters"))
                .setType(Harvester.class)
                .create();
    }

    @SubscribeEvent
    public static void onRegisterHarvesters(final Register<Harvester> event) {
        LOGGER.debug("Registering Harvesters.");
        event.getRegistry().registerAll(
                new CropHarvester(Config.WHEAT, Blocks.WHEAT).setRegistryName(QuickHarvest.ID, Config.WHEAT.getName()),
                new CropHarvester(Config.CARROTS, Blocks.CARROTS).setRegistryName(QuickHarvest.ID, Config.CARROTS.getName()),
                new CropHarvester(Config.POTATOES, Blocks.POTATOES).setRegistryName(QuickHarvest.ID, Config.POTATOES.getName()),
                new CropHarvester(Config.BEETROOTS, Blocks.BEETROOTS).setRegistryName(QuickHarvest.ID, Config.BEETROOTS.getName()),
                new ChorusHarvester(Config.CHORUS).setRegistryName(QuickHarvest.ID, Config.CHORUS.getName()),
                new CocoaHarvester(Config.COCOA).setRegistryName(QuickHarvest.ID, Config.COCOA.getName()),
                new KelpHarvester(Config.KELP).setRegistryName(QuickHarvest.ID, Config.KELP.getName()),
                new TallPlantHarvester(Config.SUGAR_CANE, Blocks.SUGAR_CANE).setRegistryName(QuickHarvest.ID, Config.SUGAR_CANE.getName()),
                new TallPlantHarvester(Config.CACTUS, Blocks.CACTUS).setRegistryName(QuickHarvest.ID, Config.CACTUS.getName()),
                new StemPlantHarvester(Config.MELON, Blocks.ATTACHED_MELON_STEM, Blocks.MELON).setRegistryName(QuickHarvest.ID, Config.MELON.getName()),
                new StemPlantHarvester(Config.PUMPKIN, Blocks.ATTACHED_PUMPKIN_STEM, Blocks.PUMPKIN).setRegistryName(QuickHarvest.ID, Config.PUMPKIN.getName()),
                new NetherWartHarvester(Config.NETHER_WART).setRegistryName(QuickHarvest.ID, Config.NETHER_WART.getName()),
                new BerryBushHarvester(Config.BERRY_BUSH).setRegistryName(QuickHarvest.ID, Config.BERRY_BUSH.getName()),
                new TwistingVineHarvester(Config.TWISTING_VINES).setRegistryName(QuickHarvest.ID, Config.TWISTING_VINES.getName()),
                new WeepingVineHarvester(Config.WEEPING_VINES).setRegistryName(QuickHarvest.ID, Config.WEEPING_VINES.getName())
        );
    }
}
