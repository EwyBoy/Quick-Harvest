package com.ewyboy.quickharvest.harvester;

import com.ewyboy.quickharvest.QuickHarvest;
import com.ewyboy.quickharvest.api.HarvesterImpl;
import com.ewyboy.quickharvest.util.FloodFill;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HugeMushroomBlock;
import net.minecraft.block.SixWayBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.Tags;

import java.util.Arrays;
import java.util.function.Predicate;

public class HugeMushroomHarvester extends HarvesterImpl {

    private static final Predicate<BlockState> IS_BROWN_MUSHROOM = s -> s.getBlock() == Blocks.BROWN_MUSHROOM_BLOCK;
    private static final Predicate<BlockState> IS_RED_MUSHROOM = s -> s.getBlock() == Blocks.RED_MUSHROOM_BLOCK;
    private static final Predicate<BlockState> IS_MUSHROOM_STEM = s -> s.getBlock() == Blocks.MUSHROOM_STEM;
    private static final Predicate<BlockState> IS_MUSHROOM = IS_BROWN_MUSHROOM.or(IS_RED_MUSHROOM).or(IS_MUSHROOM_STEM);

    public HugeMushroomHarvester() {
        super(QuickHarvest.AXE_TAG);
    }

    @Override
    public String getName() {
        return "Huge Mushroom";
    }

    @Override
    public boolean canHarvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        return super.canHarvest(player, hand, world, pos, state)
                && world.getBlockState(pos.down()).canSustainPlant(world, pos, Direction.UP, (IPlantable) Blocks.RED_MUSHROOM)
                && IS_MUSHROOM_STEM.test(state);
    }

    @Override
    public void harvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        FloodFill floodFill = new FloodFill(pos,
                s -> IS_MUSHROOM.test(s) ? Arrays.stream(Direction.values())
                        .filter(off -> !s.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(off)))
                        .toArray(Direction[] :: new) : NO_DIRECTIONS,
                ImmutableSet.of(IS_MUSHROOM, IS_RED_MUSHROOM)
        );
        floodFill.search(world);

        // Handle non-adjacent red mushroom cap sides
        if (!floodFill.getFoundTargets().get(IS_RED_MUSHROOM).isEmpty()) {
            for (Direction horizOff : Direction.Plane.HORIZONTAL) {
                BlockPos offset = floodFill.getLowestPoint().offset(horizOff, 2).up();
                BlockState offsetState = world.getBlockState(offset);
                while (!(offsetState.getBlock() instanceof HugeMushroomBlock) && offset.getY() < floodFill.getLowestPoint().getY() + 10) {
                    offset = offset.up();
                    offsetState = world.getBlockState(offset);
                }
                if (IS_MUSHROOM_STEM.test(state)) {
                    FloodFill newFill = new FloodFill(offset,
                            s -> IS_RED_MUSHROOM.test(s) ? Direction.values() : NO_DIRECTIONS,
                            ImmutableSet.of(IS_MUSHROOM)
                    );
                    newFill.search(world);
                    floodFill.add(newFill);
                }
            }
        }

        NonNullList<ItemStack> drops = NonNullList.create();
        floodFill.getFoundTargets().get(IS_MUSHROOM).forEach(info -> breakBlock(player, world, info.getPos(), drops));
        replant(player, world, floodFill.getLowestPoint(), drops);
        drops.forEach(drop -> giveItemToPlayer(player, drop));
    }

    @Override
    public ItemStack takeReplantable(NonNullList<ItemStack> drops) {
        for (ItemStack stack : drops) {
            if (Tags.Items.MUSHROOMS.func_230235_a_(stack.getItem()) && stack.getCount() >= 1) {
                ItemStack ret = stack.copy();
                stack.shrink(1);
                ret.setCount(1);
                return ret;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean replant(ServerPlayerEntity player, ServerWorld world, BlockPos pos, NonNullList<ItemStack> drops) {
        if (holdingValidTool(player)) {
            ItemStack itemStack = takeReplantable(drops);
            if (!itemStack.isEmpty()) {
                world.setBlockState(pos, ((BlockItem) itemStack.getItem()).getBlock().getDefaultState());
                return true;
            }
        }
        return false;
    }
}
