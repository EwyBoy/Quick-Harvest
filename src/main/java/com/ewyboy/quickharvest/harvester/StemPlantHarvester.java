package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.config.HarvesterConfig;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class StemPlantHarvester extends AbstractHarvester {

    private final Block stem;
    private final Block fruit;

    public StemPlantHarvester(HarvesterConfig config, Block stem, Block fruit) {
        super(config);
        this.stem = stem;
        this.fruit = fruit;
    }

    @Override
    public List<ItemStack> harvest(Player player, InteractionHand hand, ServerLevel world, BlockPos pos, BlockState state, Direction side) {
        final Block block = state.getBlock();

        if(block == stem) {
            final Direction grown = state.getValue(AttachedStemBlock.FACING);
            final BlockPos fruitPos = pos.relative(grown);
            final BlockState fruitState = world.getBlockState(fruitPos);
            return breakFruit(player, hand, world, fruitPos, fruitState, grown.getOpposite());
        } else {
            return breakFruit(player, hand, world, pos, state, side);
        }
    }

    private List<ItemStack> breakFruit(Player player, InteractionHand hand, ServerLevel world, BlockPos pos, BlockState state, Direction side) {
        damageTool(player, hand, 1);
        world.destroyBlock(pos, false);

        return Block.getDrops(state, world, pos, null);
    }

    @Override
    protected boolean isEffectiveOn(BlockState state) {
        final Block block = state.getBlock();
        return block == stem || block == fruit;
    }

}
