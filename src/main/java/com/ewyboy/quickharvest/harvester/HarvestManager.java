package com.ewyboy.quickharvest.harvester;

import net.minecraft.block.*;

import java.util.HashMap;
import java.util.Map;

public class HarvestManager {

    public static final Map<Block, IHarvestable> HARVEST_HANDLER_MAP = new HashMap<>();

    static {
        // Default plant handlers
        HARVEST_HANDLER_MAP.put(Blocks.WHEAT, new DefaultHarvestHandler(CropsBlock.AGE));
        HARVEST_HANDLER_MAP.put(Blocks.CARROTS, new DefaultHarvestHandler(CarrotBlock.AGE));
        HARVEST_HANDLER_MAP.put(Blocks.POTATOES, new DefaultHarvestHandler(PotatoBlock.AGE));
        HARVEST_HANDLER_MAP.put(Blocks.BEETROOTS, new DefaultHarvestHandler(BeetrootBlock.BEETROOT_AGE));
        HARVEST_HANDLER_MAP.put(Blocks.NETHER_WART, new DefaultHarvestHandler(NetherWartBlock.AGE));
        HARVEST_HANDLER_MAP.put(Blocks.COCOA, new DefaultHarvestHandler(CocoaBlock.AGE));

        // Stem plant handlers
        HARVEST_HANDLER_MAP.put(Blocks.ATTACHED_MELON_STEM, new StemPlantHarvestHandler());
        HARVEST_HANDLER_MAP.put(Blocks.MELON, new StemPlantHarvestHandler());
        HARVEST_HANDLER_MAP.put(Blocks.ATTACHED_PUMPKIN_STEM, new StemPlantHarvestHandler());
        HARVEST_HANDLER_MAP.put(Blocks.PUMPKIN, new StemPlantHarvestHandler());

        // Tall plant handlers
        HARVEST_HANDLER_MAP.put(Blocks.SUGAR_CANE, new TallPlantHarvestHandler());
        HARVEST_HANDLER_MAP.put(Blocks.CACTUS, new TallPlantHarvestHandler());

        // Chorus plant handler
        HARVEST_HANDLER_MAP.put(Blocks.CHORUS_FLOWER, new ChorusHarvestManager());
        HARVEST_HANDLER_MAP.put(Blocks.CHORUS_PLANT, new ChorusHarvestManager());

        // TODO Huge Mushrooms

        // Kelp plant handler
        HARVEST_HANDLER_MAP.put(Blocks.KELP, new KelpHarvestHandler());
        HARVEST_HANDLER_MAP.put(Blocks.KELP_PLANT, new KelpHarvestHandler());
    }

}
