package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.api.Harvester;
import com.ewyboy.quickharvest.config.Config;
import com.ewyboy.quickharvest.config.HarvesterConfig;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractHarvester extends Harvester {

    private final HarvesterConfig config;

    protected AbstractHarvester() {
        this(Config.DEFAULT);
    }

    protected AbstractHarvester(HarvesterConfig config) {
        this.config = config;
    }

    @Override
    public boolean canHarvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state, Direction side) {
        return isEffectiveOn(state)  // This harvester works on the block
            && canPlayerEdit(player, hand, world, pos, state, side)  // The player has permission to edit the block
            && !isHoldingBlacklistedItem(player, hand)
            && (!requiresTool() ||  // No tool is required or
            player.getHeldItemMainhand().getToolTypes().contains(requiredTool())); // the player is holding the correct tool
    }

    @Override
    public boolean isHoldingBlacklistedItem(PlayerEntity player, Hand hand) {
        final ItemStack heldStack = player.getHeldItem(hand);
        return config.getBlacklist().contains(heldStack.getItem());
    }

    @Override
    public boolean canPlayerEdit(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state, Direction side) {
        return world.isBlockLoaded(pos) // Block is loaded
            && world.isBlockModifiable(player, pos); // Player has permissions to edit the block
    }

    @Override
    public boolean enabled() {
        return config.isEnabled();
    }

    @Override
    public boolean requiresTool() {
        return config.requiresTool();
    }

    @Override
    public ToolType requiredTool() {
        return config.getToolType();
    }

    @Override
    protected boolean takesReplantItem() {
        return config.takesReplantItem();
    }

    @Override
    public Predicate<ItemStack> replantItem() {
        return stack -> stack.getItem() == config.getReplantItem();
    }

    protected void damageTool(PlayerEntity playerEntity, Hand hand, int amount) {
        if (!requiresTool()) return;
        playerEntity.getHeldItem(hand).damageItem(amount, playerEntity, it -> it.sendBreakAnimation(hand));
    }

    protected void takeReplantItem(List<ItemStack> drops) {
        if (!takesReplantItem()) return;
        final Predicate<ItemStack> replantPredicate = replantItem();
        for (ItemStack drop : drops) {
            if (replantPredicate.test(drop)) {
                drop.shrink(1);
                return;
            }
        }
    }
}
