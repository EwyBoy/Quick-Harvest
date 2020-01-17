package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.api.HarvesterImpl;
import com.ewyboy.quickharvest.util.FloodFill;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.block.ChorusPlantBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Predicate;

public class ChorusHarvester extends HarvesterImpl {

    private static final Predicate<BlockState> fruit = s -> s.getBlock() instanceof ChorusFlowerBlock;
    private static final Predicate<BlockState> notFruit = s -> s.getBlock() instanceof ChorusPlantBlock;

    public ChorusHarvester() {
        super(null, new ItemStack(Items.CHORUS_FLOWER), Blocks.CHORUS_FLOWER.getDefaultState());
    }

    @Override
    public String getName() {
        return "Chorus";
    }

    @Override
    public void harvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        FloodFill floodFill = new FloodFill(pos, s -> fruit.or(notFruit).test(s) ? Direction.values() : NO_DIRECTIONS, ImmutableSet.of(fruit, notFruit));
        floodFill.search(world);
        final NonNullList<ItemStack> drops = NonNullList.create();
        if (floodFill.getFoundTargets()
                .get(fruit)
                .stream()
                .allMatch(info -> breakBlock(player, world, info.getPos(), drops))
        ) {
            floodFill.getFoundTargets()
                    .get(notFruit)
                    .forEach(info -> breakBlock(player, world, info.getPos(), drops));
            replant(player, world, floodFill.getLowestPoint(), drops);
        }
        drops.forEach(drop -> giveItemToPlayer(player, drop));
    }


}
