package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.api.HarvestImpl;
import com.ewyboy.quickharvest.util.FloodFill;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.KelpBlock;
import net.minecraft.block.KelpTopBlock;
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

public class KelpHarvester extends HarvestImpl {

    private static final Predicate<BlockState> IS_KELP_BLOCK = s -> s.getBlock() instanceof KelpBlock;
    private static final Predicate<BlockState> IS_KELP_TOP = s -> s.getBlock() instanceof KelpTopBlock;
    private static final Predicate<BlockState> IS_KELP = IS_KELP_BLOCK.or(IS_KELP_TOP);

    @Override
    public String getName() {
        return "Kelp";
    }

    @Override
    public void harvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        FloodFill floodFill = new FloodFill(pos,
                s -> IS_KELP.test(s) ? new Direction[]{Direction.UP, Direction.DOWN} : NO_DIRECTIONS,
                ImmutableSet.of(IS_KELP)
        );

        floodFill.search(world);
        NonNullList<ItemStack> drops = NonNullList.create();
        floodFill.getFoundTargets()
                .get(IS_KELP)
                .stream()
                .filter(info -> !info.getPos().equals(floodFill.getLowestPoint()))
                .sorted(Comparator.comparingInt((CachedBlockInfo i) -> i.getPos().getY()).reversed())
                .forEachOrdered(info -> breakBlock(player, world, info.getPos(), drops));
        drops.forEach(drop -> giveItemToPlayer(player, drop));
    }
}
