package com.ewyboy.quickharvest.harvester;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class DefaultHarvester implements IHarvester {
    private final IntegerProperty ageProperty;
    private final int maxAge;
    private final int resetAge;

    public DefaultHarvester(IntegerProperty ageProperty) {
        this(ageProperty, ageProperty.getAllowedValues().stream().mapToInt(value -> value).filter(value -> value >= 0).max().orElse(0), 0);
    }

    public DefaultHarvester(IntegerProperty ageProperty, int maxAge, int resetAge) {
        this.ageProperty = ageProperty;
        this.maxAge = maxAge;
        this.resetAge = resetAge;
    }

    @Override
    public boolean canHarvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        return state.has(this.ageProperty) && state.get(this.ageProperty) == this.maxAge;
    }

    @Override
    public void harvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        breakIntoInventory(player, world, pos);
        replant(player, world, pos, state.with(ageProperty, this.resetAge));
    }
}
