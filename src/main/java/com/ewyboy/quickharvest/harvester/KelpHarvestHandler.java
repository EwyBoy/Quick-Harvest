package com.ewyboy.quickharvest.harvester;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class KelpHarvestHandler implements IHarvestable {
    @Override
    public void harvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        BlockPos top = pos;
        BlockPos bottom = pos;

        while (isKelp(world.getBlockState(bottom.down()))) {
            bottom = bottom.down();
        }
        while (isKelp(world.getBlockState(top.up()))) {
            top = top.up();
        }
        while (top.getY() > bottom.getY()) {
            breakIntoInventory(player, world, top);
            top = top.down();
        }
        breakIntoInventory(player, world, bottom);
        replant(player, world, bottom, Blocks.KELP.getDefaultState());
    }

    private boolean isKelp(BlockState state) {
        Block block = state.getBlock();
        return block instanceof KelpBlock || block instanceof KelpTopBlock;
    }
}
