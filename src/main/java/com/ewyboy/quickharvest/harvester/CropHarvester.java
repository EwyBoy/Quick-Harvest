package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.config.HarvesterConfig;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class CropHarvester extends AbstractHarvester {

    private final CropBlock effectiveOn;

    public CropHarvester(HarvesterConfig config, Block effectiveOn) {
        super(config);
        assert effectiveOn instanceof CropBlock : "Attempting to create a crops harvester for a block that does not extend CropsBlock";
        this.effectiveOn = (CropBlock) effectiveOn;
    }

    @Override
    public boolean isEffectiveOn(BlockState state) {
        return state.getBlock() == effectiveOn && effectiveOn.isMaxAge(state);
    }

    @Override
    public List<ItemStack> harvest(Player player, InteractionHand hand, ServerLevel world, BlockPos pos, BlockState state, Direction side) {
        final List<ItemStack> drops = Block.getDrops(state, world, pos, null, player, requiresTool() ? player.getMainHandItem() : ItemStack.EMPTY);
        world.destroyBlock(pos, false);
        world.setBlock(pos, effectiveOn.getStateForAge(0), 7);
        damageTool(player, hand, 1);
        takeReplantItem(drops);

        return drops;
    }

}
