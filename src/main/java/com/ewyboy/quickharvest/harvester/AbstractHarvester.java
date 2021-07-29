package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.api.Harvester;
import com.ewyboy.quickharvest.config.Config;
import com.ewyboy.quickharvest.config.HarvesterConfig;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
    public boolean canHarvest(Player player, InteractionHand hand, ServerLevel world, BlockPos pos, BlockState state, Direction side) {
        return isEffectiveOn(state)  // This harvester works on the block
            && canPlayerEdit(player, hand, world, pos, state, side)  // The player has permission to edit the block
            && !isHoldingBlacklistedItem(player, hand)
            && (!requiresTool() ||  // No tool is required or
            player.getMainHandItem().getToolTypes().contains(requiredTool())); // the player is holding the correct tool
    }

    @Override
    public boolean isHoldingBlacklistedItem(Player player, InteractionHand hand) {
        final ItemStack heldStack = player.getItemInHand(hand);
        return config.getBlacklist().contains(heldStack.getItem());
    }

    @Override
    public boolean canPlayerEdit(Player player, InteractionHand hand, ServerLevel world, BlockPos pos, BlockState state, Direction side) {
        return world.hasChunkAt(pos) // Block is loaded
            && world.mayInteract(player, pos); // Player has permissions to edit the block
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

    protected void damageTool(Player playerEntity, InteractionHand hand, int amount) {
        if (!requiresTool()) return;
        playerEntity.getItemInHand(hand).hurtAndBreak(amount, playerEntity, it -> it.broadcastBreakEvent(hand));
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
