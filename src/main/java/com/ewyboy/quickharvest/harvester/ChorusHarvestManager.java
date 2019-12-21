package com.ewyboy.quickharvest.harvester;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class ChorusHarvestManager implements IHarvestable {
    @Override
    public void harvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {

        Set<CachedBlockInfo> fruit = new HashSet<>();
        Set<CachedBlockInfo> plant = new HashSet<>();
        BlockPos lowestPoint = pos;

        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> toVisit = new ArrayDeque<BlockPos>() {
            @Override
            public void push(BlockPos o) {
                super.push(o);
                visited.add(o);
            }
        };
        toVisit.add(pos);
        while (!toVisit.isEmpty()) {
            BlockPos q = toVisit.pollLast();
            CachedBlockInfo qInfo = new CachedBlockInfo(world, q, false);
            BlockState qState = qInfo.getBlockState();
            if (state == null) {
                continue;
            }
            Block qBlock = qState.getBlock();
            if (qBlock instanceof ChorusFlowerBlock) {
                fruit.add(qInfo);
            } else if (qBlock instanceof ChorusPlantBlock) {
                plant.add(qInfo);
            } else {
                continue;
            }

            for (Direction s : Direction.values()) {
                BlockPos next = q.offset(s);
                if (visited.contains(next)) {
                    continue;
                }
                toVisit.push(next);
            }

            if (q.getY() < lowestPoint.getY()) {
                lowestPoint = q;
            }
        }
        fruit.forEach(info -> breakIntoInventory(player, world, info.getPos()));
//        plant.forEach(info -> breakIntoInventory(player, world, info.getPos()));
        breakIntoInventory(player, world, lowestPoint.up());
        replant(player, world, lowestPoint, Blocks.CHORUS_FLOWER.getDefaultState());
    }
}
