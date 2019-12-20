package com.ewyboy.quickharvest.harvester;

import net.minecraft.block.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HarvestManager {

    public static final Map<Block, IHarvestable> HARVEST_HANDLER_MAP = new HashMap<>();

    public static final Set<IHarvestable> HANDLERS = new HashSet<>();

    static {
        // Default plant handlers
        HARVEST_HANDLER_MAP.put(Blocks.WHEAT, new DefaultHarvestHandler(CropsBlock.AGE));
        HARVEST_HANDLER_MAP.put(Blocks.CARROTS, new DefaultHarvestHandler(CropsBlock.AGE));
        HARVEST_HANDLER_MAP.put(Blocks.POTATOES, new DefaultHarvestHandler(CropsBlock.AGE));
        HARVEST_HANDLER_MAP.put(Blocks.BEETROOTS, new DefaultHarvestHandler(CropsBlock.AGE));
        HARVEST_HANDLER_MAP.put(Blocks.NETHER_WART, new DefaultHarvestHandler(NetherWartBlock.AGE));
        HARVEST_HANDLER_MAP.put(Blocks.COCOA, new DefaultHarvestHandler(CocoaBlock.AGE));

        // Stem plant handlers
        HARVEST_HANDLER_MAP.put(Blocks.ATTACHED_MELON_STEM, new StemPlantHarvestHandler());
        HARVEST_HANDLER_MAP.put(Blocks.MELON_STEM, new StemPlantHarvestHandler());
        HARVEST_HANDLER_MAP.put(Blocks.ATTACHED_PUMPKIN_STEM, new StemPlantHarvestHandler());
        HARVEST_HANDLER_MAP.put(Blocks.PUMPKIN_STEM, new StemPlantHarvestHandler());

        // Tall plant handlers
        HARVEST_HANDLER_MAP.put(Blocks.SUGAR_CANE, new TallPlantHarvestHandler());
        HARVEST_HANDLER_MAP.put(Blocks.CACTUS, new TallPlantHarvestHandler());

        // TODO: Add chorus fruit handler
    }

}
