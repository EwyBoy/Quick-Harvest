package com.ewyboy.quickharvest.events;

import net.minecraft.block.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.TriPredicate;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class CropHarvestManager {

    public static final Set<CropHarvestHandler> HANDLERS = new HashSet<>();

    static {
        // Handles Wheat, Carrots, Potato and Beetroot
        HANDLERS.add(new CropHarvestHandler(state -> state.getBlock() instanceof CropsBlock, (world, pos, state) -> {
            CropsBlock block = (CropsBlock) state.getBlock();
            if (block.isMaxAge(state)) {
                world.destroyBlock(pos, true);
                world.setBlockState(pos, block.withAge(0));
                return true;
            }
            return false;
        }));

        // Handles Melons and Pumpkins when stem is clicked
        HANDLERS.add(new CropHarvestHandler(state -> state.getBlock() instanceof AttachedStemBlock, (world, pos, state) -> {
            BlockPos targetPos = pos.offset(state.get(AttachedStemBlock.FACING));
            world.destroyBlock(targetPos, true);
            return true;
        }));

        // Handles Melons and Pumpkins when Fruit is clicked
        HANDLERS.add(new CropHarvestHandler(state -> state.getBlock() instanceof StemGrownBlock, (world, pos, state) -> {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (world.getBlockState(pos.offset(dir)).getBlock() instanceof AttachedStemBlock) {
                    if (world.getBlockState(pos.offset(dir)).get(AttachedStemBlock.FACING) == dir.getOpposite()) {
                        world.destroyBlock(pos, true);
                        return true;
                    }
                }
            }
            return false;
        }));

        // Handles Netherwart
        HANDLERS.add(new CropHarvestHandler(state -> state.getBlock() instanceof NetherWartBlock, (world, pos, state) -> {
            if (state.get(NetherWartBlock.AGE) >= 3) {
                world.destroyBlock(pos, true);
                world.setBlockState(pos, state.with(NetherWartBlock.AGE, 0));
                return true;
            }
            return false;
        }));

        TriPredicate<World, BlockPos, BlockState> cactusAndSugarLogic = (world, pos, state) -> {

            BlockPos bottom = pos;
            BlockPos top = pos;

            while (world.getBlockState(bottom.down()).getBlock() == state.getBlock()) {
                bottom = bottom.down();
            }
            while (world.getBlockState(top.up()).getBlock() == state.getBlock()) {
                top = top.up();
            }
            if (bottom == top) {
                return false;
            }
            for (BlockPos breakPos = top; breakPos.getY() > bottom.getY(); breakPos = breakPos.down()) {
                world.destroyBlock(breakPos, true);
            }
            world.setBlockState(bottom, state.with(BlockStateProperties.AGE_0_15, 0), 7);
            return true;
        };

        // Handles Cactus
        HANDLERS.add(new CropHarvestHandler(state -> state.getBlock() instanceof CactusBlock, cactusAndSugarLogic));

        // Handles Sugar Cane
        HANDLERS.add(new CropHarvestHandler(state -> state.getBlock() instanceof SugarCaneBlock, cactusAndSugarLogic));

        // Handles Cocoa beans
        HANDLERS.add(new CropHarvestHandler(state -> state.getBlock() instanceof CocoaBlock, (world, pos, state) -> {
            if (state.get(CocoaBlock.AGE) == 2) {
                world.destroyBlock(pos, true);
                world.setBlockState(pos, state.with(CocoaBlock.AGE, 0));
                return true;
            }
            return false;
        }));
    }

    public static class CropHarvestHandler {

        private Predicate<BlockState> test;
        private TriPredicate<World, BlockPos, BlockState> action;

        public CropHarvestHandler(Predicate<BlockState> test, TriPredicate<World, BlockPos, BlockState> action) {
            this.test = test;
            this.action = action;
        }

        public boolean canHarvest(BlockState state) {
            return test.test(state);
        }

        public boolean tryHarvest(World world, BlockPos pos, BlockState state) {
            return action.test(world, pos, state);
        }
    }
}
