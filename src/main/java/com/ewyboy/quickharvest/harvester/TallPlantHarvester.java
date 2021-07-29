package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.config.HarvesterConfig;
import com.ewyboy.quickharvest.util.FloodFill;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

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
    public List<ItemStack> harvest(Player player, InteractionHand hand, ServerLevel world, BlockPos pos, BlockState state, Direction side) {
        FloodFill floodFill = new FloodFill(pos, s -> plantPredicate.test(s) ? new Direction[]{Direction.UP, Direction.DOWN} : FloodFill.NO_DIRECTIONS, ImmutableSet.of(plantPredicate));

        floodFill.search(world);
        List<ItemStack> drops = new ArrayList<>();
        final Set<BlockInWorld> matches = floodFill.getFoundTargets().get(plantPredicate);

        for(BlockInWorld info : matches) {
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
