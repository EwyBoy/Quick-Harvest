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

public class KelpHarvester extends AbstractHarvester {

    private static final Predicate<BlockState> IS_KELP_BLOCK = s -> s.getBlock() == Blocks.KELP_PLANT;
    private static final Predicate<BlockState> IS_KELP_TOP = s -> s.getBlock() == Blocks.KELP;
    private static final Predicate<BlockState> KELP = IS_KELP_BLOCK.or(IS_KELP_TOP);

    public KelpHarvester(HarvesterConfig config) {
        super(config);
    }

    @Override
    public List<ItemStack> harvest(Player player, InteractionHand hand, ServerLevel world, BlockPos pos, BlockState state, Direction side) {
        FloodFill floodFill = new FloodFill(pos, kelpMe -> KELP.test(kelpMe) ? new Direction[]{Direction.UP, Direction.DOWN} : FloodFill.NO_DIRECTIONS, ImmutableSet.of(KELP));

        floodFill.search(world);
        List<ItemStack> drops = new ArrayList<>();
        final Set<BlockInWorld> kelpBlocks = floodFill.getFoundTargets().get(KELP);

        for(BlockInWorld info : kelpBlocks) {
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
