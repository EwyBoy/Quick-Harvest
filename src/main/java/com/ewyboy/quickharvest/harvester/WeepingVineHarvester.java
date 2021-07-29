package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.config.HarvesterConfig;
import com.ewyboy.quickharvest.util.FloodFill;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
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

public class WeepingVineHarvester extends AbstractHarvester {

    private static final Predicate<BlockState> IS_VINES = target -> target.getBlock() == Blocks.WEEPING_VINES_PLANT || target.getBlock() == Blocks.WEEPING_VINES;

    public WeepingVineHarvester(HarvesterConfig config) {
        super(config);
    }

    @Override
    public List<ItemStack> harvest(Player player, InteractionHand hand, ServerLevel world, BlockPos pos, BlockState state, Direction side) {
        FloodFill floodFill = new FloodFill(pos, target -> target.getBlock() == Blocks.WEEPING_VINES_PLANT ? new Direction[]{Direction.UP, Direction.DOWN} : target.getBlock() == Blocks.WEEPING_VINES ? new Direction[]{Direction.DOWN} : FloodFill.NO_DIRECTIONS, ImmutableSet.of(IS_VINES));

        floodFill.search(world);
        List<ItemStack> drops = new ArrayList<>();
        final Set<BlockInWorld> vineBlocks = floodFill.getFoundTargets().get(IS_VINES);

        for(BlockInWorld info : vineBlocks) {
            if(info.getPos().equals(floodFill.getHighestPoint())) {
                continue;
            } else {info.getState();}
            drops.addAll(Block.getDrops(info.getState(), world, info.getPos(), info.getEntity()));
            world.destroyBlock(info.getPos(), false);
        }

        damageTool(player, hand, vineBlocks.size() - 1);
        takeReplantItem(drops);

        return drops;
    }

    @Override
    protected boolean isEffectiveOn(BlockState state) {
        return state.getBlock() == Blocks.WEEPING_VINES_PLANT || state.getBlock() == Blocks.WEEPING_VINES;
    }

}
