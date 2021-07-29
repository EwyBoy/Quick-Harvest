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

public class KelpHarvester extends AbstractHarvester {

    private static final Predicate<BlockState> IS_KELP_BLOCK = s -> s.getBlock() == Blocks.KELP_PLANT;
    private static final Predicate<BlockState> IS_KELP_TOP = s -> s.getBlock() == Blocks.KELP;
    private static final Predicate<BlockState> KELP = IS_KELP_BLOCK.or(IS_KELP_TOP);

    public KelpHarvester(HarvesterConfig config) {
        super(config);
    }

    @Override
    public List<ItemStack> harvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state, Direction side) {
        FloodFill floodFill = new FloodFill(pos, kelpMe -> KELP.test(kelpMe) ? new Direction[]{Direction.UP, Direction.DOWN} : FloodFill.NO_DIRECTIONS, ImmutableSet.of(KELP));

        floodFill.search(world);
        List<ItemStack> drops = new ArrayList<>();
        final Set<CachedBlockInfo> kelpBlocks = floodFill.getFoundTargets().get(KELP);

        for(CachedBlockInfo info : kelpBlocks) {
            if(info.getPos().equals(floodFill.getLowestPoint())) {
                continue;
            } else {
                info.getState();
            }
            drops.addAll(Block.getDrops(info.getState(), world, info.getPos(), info.getEntity()));
            world.destroyBlock(info.getPos(), false);
        }

        damageTool(player, hand, kelpBlocks.size() - 1);
        takeReplantItem(drops);

        return drops;
    }

    @Override
    protected boolean isEffectiveOn(BlockState state) {
        return KELP.test(state);
    }

}
