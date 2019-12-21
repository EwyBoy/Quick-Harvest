package com.ewyboy.quickharvest.harvester;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * An interface for adding right click harvest functionality. Make sure you register whatever is implementing this to the {@link HarvestManager}
 */
public interface IHarvestable {
    String HARVEST_ERROR_PROTECTED_KEY = "message.harvest.error.protected";

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
    boolean canHarvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state);

    /**
     * Uses ServerWorld as the server is what should be handling this, and it makes using {@link Block#getDrops} easier
     *
     * @param player The player who is right clicking
     * @param hand   The hand the player is using
     * @param world  The world the crops is in
     * @param pos    The position the crop is in
     * @param state  The crop's state
     */
    void harvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state);

    default boolean isInteratable(PlayerEntity player, ServerWorld world, BlockPos pos) {
        return world.isBlockLoaded(pos) && world.isBlockModifiable(player, pos) && world.canMineBlockBody(player, pos);
    }

    default void breakIntoInventory(final PlayerEntity player, ServerWorld world, BlockPos pos) {
        if (isInteratable(player, world, pos)) {
            Block.getDrops(world.getBlockState(pos), world, pos, world.getTileEntity(pos)).forEach(drop -> ItemHandlerHelper.giveItemToPlayer(player, drop));
            world.destroyBlock(pos, false);
        } else {
            tellPlayerNo(player, HARVEST_ERROR_PROTECTED_KEY);
        }
    }

    default void replant(PlayerEntity player, ServerWorld world, BlockPos pos, BlockState state) {
        if (isInteratable(player, world, pos)) {
            world.setBlockState(pos, state);
        }
    }

    default void tellPlayerNo(PlayerEntity player, String messageKey) {
        player.sendStatusMessage(new TranslationTextComponent(messageKey), true);
    }
}
