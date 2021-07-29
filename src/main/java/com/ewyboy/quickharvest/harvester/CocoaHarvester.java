package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.config.HarvesterConfig;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class CocoaHarvester extends AbstractHarvester {

    public CocoaHarvester(HarvesterConfig config) {
        super(config);
    }

    @Override
    public List<ItemStack> harvest(Player player, InteractionHand hand, ServerLevel world, BlockPos pos, BlockState state, Direction side) {
        List<ItemStack> drops = Block.getDrops(state, world, pos, null);
        world.destroyBlock(pos, false);
        world.setBlockAndUpdate(pos, state.setValue(CocoaBlock.AGE, 0));
        damageTool(player, hand, 1);
        takeReplantItem(drops);

        return drops;
    }

    @Override
    protected boolean isEffectiveOn(BlockState state) {
        return state.getBlock() == Blocks.COCOA && state.getValue(CocoaBlock.AGE) == 2;
    }

}
