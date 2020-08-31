package com.ewyboy.quickharvest.api;

import com.ewyboy.quickharvest.harvester.*;
import net.minecraft.block.*;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
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
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This is where you register harvesters.
 */
public class HarvestManager {

    public static void onBlockQuickHarvest(final PlayerInteractEvent.RightClickBlock event) {

        final World world = event.getWorld();
        final PlayerEntity player = event.getPlayer();

        if (player.getPose() == Pose.CROUCHING) {
            return;
        }

        if (event.getUseBlock() != Event.Result.DENY && event.getUseItem() != Event.Result.DENY && world instanceof ServerWorld) {
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
                    })
            ;
        }
    }

    public static void register(IHarvester handler, Supplier<Block>... validBlocks) {
        HARVEST_HANDLER_MAP.put(new BlockPredicate(validBlocks), handler);
    }

    private static final Map<BlockPredicate, IHarvester> HARVEST_HANDLER_MAP = new HashMap<>();

    public static void forEach(Consumer<IHarvester> con) {
        HARVEST_HANDLER_MAP.values().forEach(con);
    }

    static {
        // Register vanilla harvesters NOTE: DO NOT REPLACE WITH METHOD REFERENCE... THIS IS TO PREVENT CLASS LOADING
        register(new DefaultHarvester(() -> Items.WHEAT_SEEDS, () -> Blocks.WHEAT.getDefaultState(), CropsBlock.AGE), () -> Blocks.WHEAT);
        register(new DefaultHarvester(() -> Items.CARROT, () -> Blocks.CARROTS.getDefaultState(), CarrotBlock.AGE), () -> Blocks.CARROTS);
        register(new DefaultHarvester(() -> Items.POTATO, () -> Blocks.POTATOES.getDefaultState(), PotatoBlock.AGE), () -> Blocks.POTATOES);
        register(new DefaultHarvester(() -> Items.BEETROOT_SEEDS, () -> Blocks.BEETROOTS.getDefaultState(), BeetrootBlock.BEETROOT_AGE), () -> Blocks.BEETROOTS);
        register(new DefaultHarvester(() -> Items.NETHER_WART, () -> Blocks.NETHER_WART.getDefaultState(), NetherWartBlock.AGE), () -> Blocks.NETHER_WART);
        register(new DefaultHarvester(() -> Items.COCOA_BEANS, () -> Blocks.COCOA.getDefaultState(), CocoaBlock.AGE), () -> Blocks.COCOA);
        register(new DefaultHarvester(() -> Items.SWEET_BERRIES, () -> Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, 1), SweetBerryBushBlock.AGE, 2), () -> Blocks.SWEET_BERRY_BUSH);
        register(new StemPlantHarvester(), () -> Blocks.ATTACHED_MELON_STEM, () -> Blocks.MELON);
        register(new StemPlantHarvester(), () -> Blocks.ATTACHED_PUMPKIN_STEM, () -> Blocks.PUMPKIN);
        register(new TallPlantHarvester(), () -> Blocks.SUGAR_CANE, () -> Blocks.CACTUS);
        register(new ChorusHarvester(), () -> Blocks.CHORUS_FLOWER, () -> Blocks.CHORUS_PLANT);
        register(new HugeMushroomHarvester(), () -> Blocks.BROWN_MUSHROOM_BLOCK, () -> Blocks.RED_MUSHROOM_BLOCK, () -> Blocks.MUSHROOM_STEM);
        register(new KelpHarvester(), () -> Blocks.KELP, () -> Blocks.KELP_PLANT);
    }

    private static Optional<IHarvester> getHarvesterFor(BlockState state) {
        return getHarvesterFor(state.getBlock());
    }

    private static Optional<IHarvester> getHarvesterFor(Block block) {
        return HARVEST_HANDLER_MAP.keySet()
                .stream()
                .filter(predicate -> predicate.test(block))
                .map(HARVEST_HANDLER_MAP::get)
                .findFirst()
                ;
    }

    private static class BlockPredicate implements Predicate<Block> {
        private final Supplier<Block>[] blocks;

        @SafeVarargs
        BlockPredicate(Supplier<Block>... blocks) {
            this.blocks = blocks;
        }

        @Override
        public boolean test(Block block) {
            return Arrays.stream(blocks).map(Supplier::get).anyMatch(Predicate.isEqual(block));
        }
    }
}