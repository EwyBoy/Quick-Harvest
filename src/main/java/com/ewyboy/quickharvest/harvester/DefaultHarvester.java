package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.QuickHarvest;
import com.ewyboy.quickharvest.api.HarvestImpl;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Supplier;

public class DefaultHarvester extends HarvestImpl {

    private final int harvestAge;
    private final IntegerProperty ageProperty;

    public DefaultHarvester(Supplier<Item> replantItem, Supplier<BlockState> replantState, IntegerProperty ageProperty, int minHarvestAge) {
        super(QuickHarvest.HOE_TAG, () -> new ItemStack(replantItem.get()), replantState);
        this.ageProperty = ageProperty;
        this.harvestAge = minHarvestAge;
    }

    public DefaultHarvester(Supplier<Item> replantItem, Supplier<BlockState> replantState, IntegerProperty ageProperty) {
        this(replantItem, replantState, ageProperty, ageProperty.getAllowedValues().stream().mapToInt(Integer::intValue).max().getAsInt());
    }

    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public boolean canHarvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        return super.canHarvest(player, hand, world, pos, state) && state.func_235901_b_(this.ageProperty) && state.get(this.ageProperty) >= this.harvestAge;
    }

    @Override
    public void harvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        if (!canHarvest(player, hand, world, pos, state)) {
            return;
        }
        NonNullList<ItemStack> drops = NonNullList.create();
        if (breakBlock(player, world, pos, drops)) {
            if (state.func_235901_b_(BlockStateProperties.HORIZONTAL_FACING)) {
                replant(player, world, pos, drops, ImmutableMap.of(CocoaBlock.HORIZONTAL_FACING, state.get(CocoaBlock.HORIZONTAL_FACING)));
            } else {
                replant(player, world, pos, drops);
            }
        }
        drops.forEach(drop -> giveItemToPlayer(player, drop));
    }
}
