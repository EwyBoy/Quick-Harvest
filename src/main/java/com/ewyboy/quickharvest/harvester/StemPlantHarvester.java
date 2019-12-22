package com.ewyboy.quickharvest.harvester;

import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StemGrownBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class StemPlantHarvester implements IHarvester {
    @Override
    public boolean canHarvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();

        if (block instanceof AttachedStemBlock) return true;
        if (!(block instanceof StemGrownBlock) || !world.isBlockLoaded(pos)) return false;

        for (Direction off : Direction.Plane.HORIZONTAL) {
            BlockState offState = world.getBlockState(pos.offset(off));
            Block stemMaybe = offState.getBlock();
            if (stemMaybe instanceof AttachedStemBlock) {
                return offState.get(AttachedStemBlock.FACING) == off.getOpposite();
            }
        }
        return false;
    }

    @Override
    public void harvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof AttachedStemBlock) {
            breakIntoInventory(player, world, pos.offset(state.get(AttachedStemBlock.FACING)));
        } else if (block instanceof StemGrownBlock) {
            breakIntoInventory(player, world, pos);
        }
    }
}
