package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.api.HarvestImpl;
import com.ewyboy.quickharvest.util.FloodFill;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Comparator;
import java.util.function.Predicate;

public class TallPlantHarvester extends HarvestImpl {

    @Override
    public String getName() {
        return "Tall Plant";
    }

    @Override
    public boolean canHarvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        return super.canHarvest(player, hand, world, pos, state) && !player.getHeldItem(hand).getItem().equals(state.getBlock().asItem());
    }

    @Override
    public void harvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        final Predicate<BlockState> isSame = s -> s.getBlock() == state.getBlock();
        FloodFill floodFill = new FloodFill(pos,
                s -> isSame.test(s) ? new Direction[]{Direction.UP, Direction.DOWN} : NO_DIRECTIONS,
                ImmutableSet.of(isSame)
        );
        floodFill.search(world);
        NonNullList<ItemStack> drops = NonNullList.create();
        floodFill.getFoundTargets()
                .get(isSame)
                .stream()
                .filter(info -> !info.getPos().equals(floodFill.getLowestPoint()))
                .sorted(Comparator.comparingInt((CachedBlockInfo i) -> i.getPos().getY()).reversed())
                .forEachOrdered(info -> breakBlock(player, world, info.getPos(), drops));
        drops.forEach(drop -> giveItemToPlayer(player, drop));
    }
}
