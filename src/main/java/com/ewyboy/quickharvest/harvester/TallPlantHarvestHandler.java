package com.ewyboy.quickharvest.harvester;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class TallPlantHarvestHandler implements IHarvestable {
    @Override
    public boolean canHarvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        return state.get(BlockStateProperties.AGE_0_15) == 15;
    }

    @Override
    public void harvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        BlockPos bottom = pos;
        BlockPos top = pos;

        while (world.getBlockState(bottom.down()).getBlock() == state.getBlock()) {
            bottom = bottom.down();
        }
        while (world.getBlockState(top.up()).getBlock() == state.getBlock()) {
            top = top.up();
        }
        if (bottom == top) {
            return;
        }
        for (BlockPos breakPos = top; breakPos.getY() > bottom.getY(); breakPos = breakPos.down()) {
            breakIntoInventory(player, world, breakPos);
        }
        replant(player, world, bottom, state.with(BlockStateProperties.AGE_0_15, 0));
    }
}
