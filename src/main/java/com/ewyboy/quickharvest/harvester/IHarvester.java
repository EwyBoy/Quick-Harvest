package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.HarvestManager;
import com.ewyboy.quickharvest.QuickHarvest;
import com.ewyboy.quickharvest.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * An interface for adding right click harvest functionality. Make sure you register whatever is implementing this to the {@link HarvestManager}
 */
public interface IHarvester {
    String HARVEST_ERROR_PROTECTED_KEY = QuickHarvest.ID + ".message.error.protected";

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
    default boolean canHarvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        return isInteratable(player, world, pos);
    }

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

    default boolean isInteratable(PlayerEntity player, ServerWorld world, BlockPos pos) {
        return world.isBlockLoaded(pos) && world.isBlockModifiable(player, pos) && world.canMineBlockBody(player, pos);
    }

    default void breakIntoInventory(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        ItemStack hoe = player.getHeldItem(Hand.MAIN_HAND);
        if (requiresTool() && !(hoe.getItem() instanceof HoeItem)) {
            return;
        }
        if (isInteratable(player, world, pos)) {
            if (requiresTool() && !damageTool(player, hoe, 1)) {
                return;
            }
            Block.getDrops(world.getBlockState(pos), world, pos, world.getTileEntity(pos)).forEach(drop -> ItemHandlerHelper.giveItemToPlayer(player, drop));
            world.destroyBlock(pos, false);
        } else {
            tellPlayerNo(player, HARVEST_ERROR_PROTECTED_KEY);
        }
    }

    default void replant(ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockState state) {
        ItemStack hoe = player.getHeldItem(Hand.MAIN_HAND);
        if (requiresTool() && !(hoe.getItem() instanceof HoeItem)) {
            return;
        }
        if (isInteratable(player, world, pos)) {
            if (requiresTool() && !(damageTool(player, hoe, 1))) {
                return;
            }
            world.setBlockState(pos, state);
        }
    }

    default void tellPlayerNo(PlayerEntity player, String messageKey) {
        player.sendStatusMessage(new TranslationTextComponent(messageKey), true);
    }

    default boolean requiresTool() {
        return Config.SETTINGS.requiresTool();
    }

    default boolean damageTool(ServerPlayerEntity player, ItemStack tool, int amount) {
        if (tool.getMaxDamage() - tool.getDamage() >= amount) {
            return tool.attemptDamageItem(amount, player.getServerWorld().getRandom(), player);
        }
        return false;
    }
}
