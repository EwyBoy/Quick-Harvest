package com.ewyboy.quickharvest.harvester;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class HugeMushroomHarvester implements IHarvester {

    @Override
    public boolean canHarvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        return isInteratable(player, world, pos) && world.getBlockState(pos.down()).canSustainPlant(world, pos, Direction.UP, (IPlantable) Blocks.BROWN_MUSHROOM);
    }

    @Override
    public void harvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        Set<CachedBlockInfo> shrooms = new HashSet<>();
        BlockPos lowestPos = pos;
        Color color = Color.NONE;

        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> toVisit = new ArrayDeque<BlockPos>() {
            @Override
            public void push(BlockPos blockPos) {
                super.push(blockPos);
                visited.add(blockPos);
            }
        };
        toVisit.add(pos);

        while (!toVisit.isEmpty()) {
            BlockPos q = toVisit.pollLast();
            CachedBlockInfo qInfo = new CachedBlockInfo(world, q, false);
            BlockState qState = qInfo.getBlockState();
            if (qState == null) {
                continue; // Block not loaded
            }

            Block qBlock = qState.getBlock();
            if (qBlock == Blocks.MUSHROOM_STEM) {
                // NOOP
            } else if (qBlock == Blocks.BROWN_MUSHROOM_BLOCK) {
                if (color == Color.NONE) {
                    color = Color.BROWN;
                }
            } else if (qBlock == Blocks.RED_MUSHROOM_BLOCK) {
                if (color == Color.NONE) {
                    color = Color.RED;
                }
            } else {
                continue;
            }

            for (Direction off : Direction.values()) {
                BlockPos next = q.offset(off);
                if (qState.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(off)) || visited.contains(next)) {
                    continue;
                }
                toVisit.push(next);
            }

            if (q.getY() < lowestPos.getY()) {
                lowestPos = q;
            }

            shrooms.add(qInfo);
        }

        switch (color) {
            case BROWN:
                shrooms.forEach(info -> breakIntoInventory(player, world, info.getPos()));
                replant(player, world, lowestPos, Blocks.BROWN_MUSHROOM.getDefaultState());
                break;
            case RED:
                findRedSides(player, world, lowestPos, shrooms, visited, toVisit);
                shrooms.forEach(info -> breakIntoInventory(player, world, info.getPos()));
                replant(player, world, lowestPos, Blocks.RED_MUSHROOM.getDefaultState());
                break;
            default:
            case NONE:
                break;
        }
    }

    private void findRedSides(PlayerEntity player, ServerWorld world, BlockPos lowestPos, Set<CachedBlockInfo> shrooms, Set<BlockPos> visited, Deque<BlockPos> toVisit) {
        for (Direction horzOff : Direction.Plane.HORIZONTAL) {
            BlockPos offset = lowestPos.offset(horzOff, 2);
            while (world.isAirBlock(offset.up()) && offset.getY() < lowestPos.getY() + 10) {
                offset = offset.up();
            }
            toVisit.add(offset.up());
        }

        while (!toVisit.isEmpty()) {
            BlockPos q = toVisit.pollLast();
            CachedBlockInfo qInfo = new CachedBlockInfo(world, q, false);
            BlockState qState = qInfo.getBlockState();
            if (qState == null || !(qState.getBlock() instanceof HugeMushroomBlock)) {
                continue; // Block not loaded
            }

            for (Direction off : Direction.values()) {
                BlockPos next = q.offset(off);
                if (qState.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(off)) || visited.contains(next)) {
                    continue;
                }
                toVisit.push(next);
            }

            shrooms.add(qInfo);
        }
    }

    private enum Color {
        NONE, BROWN, RED
    }
}
