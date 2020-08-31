package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.config.HarvesterConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class CropHarvester extends AbstractHarvester {

    private final CropsBlock effectiveOn;

    public CropHarvester(HarvesterConfig config, Block effectiveOn) {
        super(config);
        assert effectiveOn instanceof CropsBlock : "Attempting to create a crops harvester for a block that does not extend CropsBlock";
        this.effectiveOn = (CropsBlock) effectiveOn;
    }

    @Override
    public boolean isEffectiveOn(BlockState state) {
        return state.getBlock() == effectiveOn && effectiveOn.isMaxAge(state);
    }

    @Override
    public List<ItemStack> harvest(PlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state, Direction side) {
        final List<ItemStack> drops = Block.getDrops(state, world, pos, null, player, requiresTool() ? player.getHeldItemMainhand() : ItemStack.EMPTY);
        world.destroyBlock(pos, false);
        world.setBlockState(pos, effectiveOn.withAge(0), 7);
        damageTool(player, hand, 1);
        takeReplantItem(drops);

        return drops;
    }
}
