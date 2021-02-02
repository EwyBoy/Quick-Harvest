package com.ewyboy.quickharvest.api;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.function.Predicate;

public abstract class Harvester extends ForgeRegistryEntry<Harvester> implements IForgeRegistryEntry<Harvester> {

    /**
     * Used to check if a harvester is enabled via config.
     *
     * @return true if the harvester should be used.
     */
    public abstract boolean enabled();

    /**
     * Used to check if the player is holding a blacklisted item.
     * <br>
     * e.g. holding Shears while attempting to harvest a pumpkin.
     *
     * @param player The player trying to quick harvest
     * @param hand The hand being used
     * @return true when the item is blacklisted.
     */
    public abstract boolean isHoldingBlacklistedItem(PlayerEntity player, Hand hand);

    /**
     * Used to check if a harvester is effective on a given crop, and that that player has permission to edit that block
     * <p>
     * Note: this should also do tool checks.
     *
     * @param player The player trying to quick harvest
     * @param hand   The hand being used
     * @param world  The world the crop exists in
     * @param pos    The position of the crop
     * @param state  The state of the crop
     * @param side   The side the player clicked
     *
     * @return true if quick harvesting this block is possible.
     */
    public abstract boolean canHarvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state, Direction side);

    /**
     * Handles drops, taking replant item, breaking, replanting and damaging the tool.
     *
     * @param player The player trying to quick harvest
     * @param hand   The hand being used
     * @param world  The world the crop exists in
     * @param pos    The position of the crop
     * @param state  The state of the crop
     * @param side   The side the player clicked.
     *
     * @return A list of items that are dropped by the crop when it is broken.
     */
    public abstract List<ItemStack> harvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state, Direction side);

    protected abstract boolean isEffectiveOn(BlockState state);

    protected abstract boolean canPlayerEdit(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state, Direction side);

    protected abstract boolean requiresTool();

    protected abstract ToolType requiredTool();

    protected abstract boolean takesReplantItem();

    protected abstract Predicate<ItemStack> replantItem();

}