package com.ewyboy.quickharvest.harvester;

import net.minecraft.block.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class HarvestManager {

    public static final Map<Predicate<Block>, IHarvestable> HARVEST_HANDLER_MAP = new HashMap<>();

    //TODO: Create a register method and make the harvest handler map private
    static {
        // Default plant handlers
        HARVEST_HANDLER_MAP.put(new MatchBlockPredicate(Blocks.WHEAT), new DefaultHarvestHandler(CropsBlock.AGE));
        HARVEST_HANDLER_MAP.put(new MatchBlockPredicate(Blocks.CARROTS), new DefaultHarvestHandler(CarrotBlock.AGE));
        HARVEST_HANDLER_MAP.put(new MatchBlockPredicate(Blocks.POTATOES), new DefaultHarvestHandler(PotatoBlock.AGE));
        HARVEST_HANDLER_MAP.put(new MatchBlockPredicate(Blocks.BEETROOTS), new DefaultHarvestHandler(BeetrootBlock.BEETROOT_AGE));
        HARVEST_HANDLER_MAP.put(new MatchBlockPredicate(Blocks.NETHER_WART), new DefaultHarvestHandler(NetherWartBlock.AGE));
        HARVEST_HANDLER_MAP.put(new MatchBlockPredicate(Blocks.COCOA), new DefaultHarvestHandler(CocoaBlock.AGE));

        // Stem plant handlers
        HARVEST_HANDLER_MAP.put(new MatchBlockPredicate(Blocks.ATTACHED_MELON_STEM, Blocks.MELON), new StemPlantHarvestHandler());
        HARVEST_HANDLER_MAP.put(new MatchBlockPredicate(Blocks.ATTACHED_PUMPKIN_STEM, Blocks.PUMPKIN), new StemPlantHarvestHandler());

        // Tall plant handlers
        HARVEST_HANDLER_MAP.put(new MatchBlockPredicate(Blocks.SUGAR_CANE, Blocks.CACTUS), new TallPlantHarvestHandler());

        // Chorus plant handler
        HARVEST_HANDLER_MAP.put(new MatchBlockPredicate(Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT), new ChorusHarvestManager());

        // Huge mushroom handler
        HARVEST_HANDLER_MAP.put(new MatchBlockPredicate(Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.MUSHROOM_STEM), new HugeMushroomHarvestHandler());

        // Kelp plant handler
        HARVEST_HANDLER_MAP.put(new MatchBlockPredicate(Blocks.KELP, Blocks.KELP_PLANT), new KelpHarvestHandler());
    }

    static class MatchBlockPredicate implements Predicate<Block> {
        private final Block[] blocks;

        MatchBlockPredicate(Block... blocks) {
            this.blocks = blocks;
        }

        @Override
        public boolean test(Block block) {
            for (Block t : blocks) {
                if (t == block) {
                    return true;
                }
            }
            return false;
        }
    }

}
