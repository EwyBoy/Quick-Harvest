package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.config.HarvesterConfig;
import com.ewyboy.quickharvest.util.FloodFill;
import com.google.common.collect.ImmutableSet;
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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ChorusHarvester extends AbstractHarvester {

    private static final Predicate<BlockState> fruit = s -> s.getBlock() instanceof ChorusFlowerBlock;
    private static final Predicate<BlockState> notFruit = s -> s.getBlock() instanceof ChorusPlantBlock;

    public ChorusHarvester(HarvesterConfig config) {
        super(config);
    }

    @Override
    public List<ItemStack> harvest(Player player, InteractionHand hand, ServerLevel world, BlockPos pos, BlockState state, Direction side) {
        final List<ItemStack> drops = new ArrayList<>();
        final FloodFill floodFill = new FloodFill(pos, s -> fruit.or(notFruit).test(s) ? Direction.values() : FloodFill.NO_DIRECTIONS, ImmutableSet.of(fruit, notFruit));
        int blocksBroken = 0;

        floodFill.search(world);

        for(Set<BlockInWorld> cachedBlockInfos : floodFill.getFoundTargets().values()) {
            blocksBroken += cachedBlockInfos.size();
            for(BlockInWorld info : cachedBlockInfos) {
                if(info.getState() == null) continue;
                drops.addAll(Block.getDrops(info.getState(), world, info.getPos(), info.getEntity()));
                world.destroyBlock(info.getPos(), false);
            }
        }

        world.setBlock(floodFill.getLowestPoint(), Blocks.CHORUS_FLOWER.defaultBlockState(), 7);
        damageTool(player, hand, blocksBroken);
        takeReplantItem(drops);

        return drops;
    }

    @Override
    protected boolean isEffectiveOn(BlockState state) {
        return fruit.or(notFruit).test(state);
    }

}
