package com.ewyboy.quickharvest.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.function.BooleanSupplier;

/**
 * An interface for adding right click harvest functionality. Make sure you register whatever is implementing this to the {@link HarvestManager}
 */
public interface IHarvester {

    BooleanSupplier enabled();

    /**
     * Uses ServerWorld as the server is what should be handling this, and it makes using {@link Block#getDrops} easier
     *
     * @param player The player who is right clicking
     * @param hand   The hand the player is using
     * @param world  The world the crops is in
     * @param pos    The position the crop is in
     * @param state  The crop's state
     * @return true if the crop is currently harvestable
     */
    boolean canHarvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state);

    /**
     * Uses ServerWorld as the server is what should be handling this, and it makes using {@link Block#getDrops} easier
     *
     * @param player The player who is right clicking
     * @param hand   The hand the player is using
     * @param world  The world the crops is in
     * @param pos    The position the crop is in
     * @param state  The crop's state
     */
    void harvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state);

}
