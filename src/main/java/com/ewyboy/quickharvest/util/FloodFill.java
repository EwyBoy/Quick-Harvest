package com.ewyboy.quickharvest.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class FloodFill {

    public static final Direction[] NO_DIRECTIONS = new Direction[0];
    private final Function<BlockState, Iterable<Direction>> stateSearchMapper;
    private final Map<Predicate<BlockState>, Set<BlockInWorld>> foundTargets;
    private final Deque<BlockPos> toVisit;
    private final Set<BlockPos> visited;
    private BlockPos lowestPoint;
    private BlockPos highestPoint;

    public FloodFill(BlockPos origin, Function<BlockState, Direction[]> stateSearchMapper, Set<Predicate<BlockState>> targets) {
        this.lowestPoint = origin.immutable();
        this.highestPoint = origin.immutable();
        this.stateSearchMapper = s -> Arrays.asList(stateSearchMapper.apply(s));
        this.foundTargets = new HashMap<>();
        targets.forEach(target -> foundTargets.put(target, new HashSet<>()));
        this.visited = new HashSet<>();
        this.toVisit = new ArrayDeque<BlockPos>() {
            @Override
            public void push(BlockPos pos) {
                if(visited.add(pos)) {
                    super.push(pos);
                }
            }
        };
        toVisit.push(origin);
    }

    public void search(ServerLevel world) {
        while(!toVisit.isEmpty()) {
            final BlockPos pos = toVisit.pollLast();
            final BlockInWorld blockInfo = new BlockInWorld(world, pos, false);
            final BlockState blockState = blockInfo.getState();

            if(blockState == null) continue; // if the block is not loadable

            // Add neighbours to search list
            stateSearchMapper.apply(blockState).forEach(it -> toVisit.push(pos.relative(it)));
            // Add this block to any of the target lists it matches.
            foundTargets.entrySet().stream().filter(it -> it.getKey().test(blockState)).forEach(it -> it.getValue().add(blockInfo));
            // Move lowest and highest point if this is a valid block

            if(foundTargets.keySet().stream().anyMatch(it -> it.test(blockState))) {
                if(pos.getY() < lowestPoint.getY()) {
                    lowestPoint = pos.immutable();
                } else if(pos.getY() > highestPoint.getY()) {
                    highestPoint = pos.immutable();
                }
            }
        }
    }

    public Map<Predicate<BlockState>, Set<BlockInWorld>> getFoundTargets() {
        return foundTargets;
    }

    public BlockPos getLowestPoint() {
        return lowestPoint;
    }

    public BlockPos getHighestPoint() {
        return highestPoint;
    }

    public FloodFill add(FloodFill other) {
        if(other.highestPoint.getY() > this.highestPoint.getY()) this.highestPoint = other.highestPoint;
        if(other.lowestPoint.getY() < this.lowestPoint.getY()) this.lowestPoint = other.lowestPoint;

        this.visited.addAll(other.visited);
        other.getFoundTargets().forEach((key, value) -> this.foundTargets.computeIfAbsent(key, $ -> new HashSet<>()).addAll(value));

        return this;
    }

}
