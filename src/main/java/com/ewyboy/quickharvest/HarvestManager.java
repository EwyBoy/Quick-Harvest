package com.ewyboy.quickharvest;

import com.ewyboy.quickharvest.harvester.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class HarvestManager {

    public static void onBlockQuickHarvest(final PlayerInteractEvent.RightClickBlock event) {
        final World world = event.getWorld();
        final PlayerEntity player = event.getPlayer();
        if (world instanceof ServerWorld) {
            final ServerWorld serverWorld = (ServerWorld) world;
            final BlockPos pos = event.getPos();
            final BlockState state = event.getWorld().getBlockState(pos);
            final ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            final Hand hand = event.getHand();

            getHarvesterFor(state)
                    .filter(harvester -> harvester.canHarvest(serverPlayer, hand, serverWorld, pos, state))
                    .ifPresent(harvester -> {
                        harvester.harvest(serverPlayer, hand, serverWorld, pos, state);
                        event.setUseBlock(Event.Result.DENY);
                        event.setUseItem(Event.Result.DENY);
                        event.setCanceled(true);
                    });
        }
    }

    public static void register(IHarvester handler, Block... validBlocks) {
        HARVEST_HANDLER_MAP.put(new BlockPredicate(validBlocks), handler);
    }

    private static final Map<BlockPredicate, IHarvester> HARVEST_HANDLER_MAP = new HashMap<>();

    static {
        // Register vanilla harvesters
        register(new DefaultHarvester(CropsBlock.AGE), Blocks.WHEAT);
        register(new DefaultHarvester(CarrotBlock.AGE), Blocks.CARROTS);
        register(new DefaultHarvester(PotatoBlock.AGE), Blocks.POTATOES);
        register(new DefaultHarvester(BeetrootBlock.BEETROOT_AGE), Blocks.BEETROOTS);
        register(new DefaultHarvester(NetherWartBlock.AGE), Blocks.NETHER_WART);
        register(new DefaultHarvester(CocoaBlock.AGE), Blocks.COCOA);
        register(new StemPlantHarvester(), Blocks.ATTACHED_MELON_STEM, Blocks.MELON);
        register(new StemPlantHarvester(), Blocks.ATTACHED_PUMPKIN_STEM, Blocks.PUMPKIN);
        register(new TallPlantHarvester(), Blocks.SUGAR_CANE, Blocks.CACTUS);
        register(new ChorusHarvester(), Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT);
        register(new HugeMushroomHarvester(), Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.MUSHROOM_STEM);
        register(new KelpHarvester(), Blocks.KELP, Blocks.KELP_PLANT);
    }

    private static Optional<IHarvester> getHarvesterFor(BlockState state) {
        return getHarvesterFor(state.getBlock());
    }

    private static Optional<IHarvester> getHarvesterFor(Block block) {
        return HARVEST_HANDLER_MAP.keySet()
                .stream()
                .filter(predicate -> predicate.test(block))
                .map(HARVEST_HANDLER_MAP::get)
                .findFirst();
    }

    private static class BlockPredicate implements Predicate<Block> {
        private final Block[] blocks;

        BlockPredicate(Block... blocks) {
            this.blocks = blocks;
        }

        @Override
        public boolean test(Block block) {
            return Arrays.stream(blocks).anyMatch(Predicate.isEqual(block));
        }
    }

}
