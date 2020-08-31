package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.config.HarvesterConfig;
import com.ewyboy.quickharvest.util.FloodFill;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.*;
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

public class ChorusHarvester extends AbstractHarvester {
    private static final Predicate<BlockState> fruit = s -> s.getBlock() instanceof ChorusFlowerBlock;
    private static final Predicate<BlockState> notFruit = s -> s.getBlock() instanceof ChorusPlantBlock;

    public ChorusHarvester(HarvesterConfig config) {
        super(config);
    }

    @Override
    public List<ItemStack> harvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state, Direction side) {
        final List<ItemStack> drops = new ArrayList<>();
        final FloodFill floodFill = new FloodFill(pos, s -> fruit.or(notFruit).test(s) ? Direction.values() : FloodFill.NO_DIRECTIONS, ImmutableSet.of(fruit, notFruit));
        floodFill.search(world);
        int blocksBroken = 0;
        for (Set<CachedBlockInfo> cachedBlockInfos : floodFill.getFoundTargets().values()) {
            blocksBroken += cachedBlockInfos.size();
            for (CachedBlockInfo info : cachedBlockInfos) {
                if (info.getBlockState() == null) continue;
                drops.addAll(Block.getDrops(info.getBlockState(), world, info.getPos(), info.getTileEntity()));
                world.destroyBlock(info.getPos(), false);
            }
        }
        world.setBlockState(floodFill.getLowestPoint(), Blocks.CHORUS_FLOWER.getDefaultState(), 7);
        damageTool(player, hand, blocksBroken);
        takeReplantItem(drops);
        return drops;
    }

    @Override
    protected boolean isEffectiveOn(BlockState state) {
        return fruit.or(notFruit).test(state);
    }
}
