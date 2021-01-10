package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.config.HarvesterConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class NetherWartHarvester extends AbstractHarvester {

    public NetherWartHarvester(HarvesterConfig config) {
        super(config);
    }

    @Override
    public List<ItemStack> harvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state, Direction side) {
        final List<ItemStack> drops = Block.getDrops(state, world, pos, null, player, player.getHeldItem(hand));
        world.destroyBlock(pos, false);
        world.setBlockState(pos, state.with(NetherWartBlock.AGE, 0));
        damageTool(player, hand, 1);
        takeReplantItem(drops);

        return drops;
    }

    @Override
    protected boolean isEffectiveOn(BlockState state) {
        return state.getBlock() == Blocks.NETHER_WART && state.get(NetherWartBlock.AGE) == 3;
    }

}
