package com.ewyboy.quickharvest.harvester;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class TallPlantHarvestHandler implements IHarvestable {
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
        while (top.getY() > bottom.getY()) {
            breakIntoInventory(player, world, top);
            top = top.down();
        }
        replant(player, world, bottom, state.with(BlockStateProperties.AGE_0_15, 0));
    }
}
