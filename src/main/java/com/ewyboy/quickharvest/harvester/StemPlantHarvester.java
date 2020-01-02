package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.QuickHarvest;
import com.ewyboy.quickharvest.api.HarvesterImpl;
import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StemGrownBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class StemPlantHarvester extends HarvesterImpl {

    public StemPlantHarvester() {
        super(QuickHarvest.AXE_TAG);
    }

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
        NonNullList<ItemStack> drops = NonNullList.create();
        Block block = state.getBlock();
        if (block instanceof AttachedStemBlock) {
            breakBlock(player, world, pos.offset(state.get(AttachedStemBlock.FACING)), drops);
        } else if (block instanceof StemGrownBlock) {
            breakBlock(player, world, pos, drops);
        }
        drops.forEach(drop -> giveItemToPlayer(player, drop));
    }
}
