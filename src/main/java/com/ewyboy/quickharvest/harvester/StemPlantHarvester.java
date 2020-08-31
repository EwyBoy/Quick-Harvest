package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.config.HarvesterConfig;
import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class StemPlantHarvester extends AbstractHarvester {
    private final Block stem;
    private final Block friut;

    public StemPlantHarvester(HarvesterConfig config, Block stem, Block friut) {
        super(config);
        this.stem = stem;
        this.friut = friut;
    }

    @Override
    public List<ItemStack> harvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state, Direction side) {
        final Block block = state.getBlock();
        if (block == stem) {
            final Direction grown = state.get(AttachedStemBlock.FACING);
            final BlockPos fruitPos = pos.offset(grown);
            final BlockState fruitState = world.getBlockState(fruitPos);
            return breakFruit(player, hand, world, fruitPos, fruitState, grown.getOpposite());
        } else {
            return breakFruit(player, hand, world, pos, state, side);
        }
    }

    private List<ItemStack> breakFruit(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state, Direction side) {
        damageTool(player, hand, 1);
        world.destroyBlock(pos, false);
        return Block.getDrops(state, world, pos, null);
    }

    @Override
    protected boolean isEffectiveOn(BlockState state) {
        final Block block = state.getBlock();
        return block == stem || block == friut;
    }
}
