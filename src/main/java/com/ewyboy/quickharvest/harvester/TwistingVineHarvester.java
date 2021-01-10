package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.config.HarvesterConfig;
import com.ewyboy.quickharvest.util.FloodFill;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class TwistingVineHarvester extends AbstractHarvester {

    private static final Predicate<BlockState> IS_VINES = target -> target.getBlock() == Blocks.TWISTING_VINES_PLANT || target.getBlock() == Blocks.TWISTING_VINES;

    public TwistingVineHarvester(HarvesterConfig config) {
        super(config);
    }

    @Override
    public List<ItemStack> harvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state, Direction side) {
        FloodFill floodFill = new FloodFill(pos, target -> target.getBlock() == Blocks.TWISTING_VINES_PLANT ? new Direction[]{Direction.UP, Direction.DOWN} : target.getBlock() == Blocks.TWISTING_VINES ? new Direction[]{Direction.DOWN} : FloodFill.NO_DIRECTIONS, ImmutableSet.of(IS_VINES));

        floodFill.search(world);
        List<ItemStack> drops = new ArrayList<>();
        final Set<CachedBlockInfo> vineBlocks = floodFill.getFoundTargets().get(IS_VINES);

        for(CachedBlockInfo info : vineBlocks) {
            if(info.getPos().equals(floodFill.getLowestPoint())) {
                continue;
            } else {
                info.getBlockState();
            }
            drops.addAll(Block.getDrops(info.getBlockState(), world, info.getPos(), info.getTileEntity()));
            world.destroyBlock(info.getPos(), false);
        }

        damageTool(player, hand, vineBlocks.size() - 1);
        takeReplantItem(drops);

        return drops;
    }

    @Override
    protected boolean isEffectiveOn(BlockState state) {
        return state.getBlock() == Blocks.TWISTING_VINES_PLANT || state.getBlock() == Blocks.TWISTING_VINES;
    }

}
