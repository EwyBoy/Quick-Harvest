package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.config.HarvesterConfig;
import com.ewyboy.quickharvest.util.FloodFill;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

public class TallPlantHarvester extends AbstractHarvester {

    private final Predicate<BlockState> plantPredicate = this :: isEffectiveOn;
    private final Block plant;

    public TallPlantHarvester(HarvesterConfig config, Block plant) {
        super(config);
        this.plant = plant;
    }

    @Override
    public List<ItemStack> harvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state, Direction side) {
        FloodFill floodFill = new FloodFill(pos, s -> plantPredicate.test(s) ? new Direction[]{Direction.UP, Direction.DOWN} : FloodFill.NO_DIRECTIONS, ImmutableSet.of(plantPredicate));

        floodFill.search(world);
        List<ItemStack> drops = new ArrayList<>();
        final Set<CachedBlockInfo> matches = floodFill.getFoundTargets().get(plantPredicate);

        for(CachedBlockInfo info : matches) {
            if(info.getPos().equals(floodFill.getLowestPoint())) {
                continue;
            } else {
                info.getState();
            }
            drops.addAll(Block.getDrops(info.getState(), world, info.getPos(), info.getEntity()));
            world.destroyBlock(info.getPos(), false);
        }

        damageTool(player, hand, matches.size() - 1);

        return drops;
    }

    @Override
    protected boolean isEffectiveOn(BlockState state) {
        return state.getBlock() == plant;
    }

}
