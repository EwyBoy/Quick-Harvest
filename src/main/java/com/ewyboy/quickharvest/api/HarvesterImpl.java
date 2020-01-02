package com.ewyboy.quickharvest.api;

import com.ewyboy.quickharvest.QuickHarvest;
import com.ewyboy.quickharvest.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;
import java.util.function.BooleanSupplier;

public abstract class HarvesterImpl implements IHarvester {
    public static final String HARVEST_ERROR_PROTECTED_KEY = QuickHarvest.ID + ".message.error.protected";
    protected static final Direction[] NO_DIRECTIONS = new Direction[0];
    private static BooleanSupplier requiresTool = Config.SETTINGS::requiresTool;
    private static BooleanSupplier damagesTool = Config.SETTINGS::damagesTool;
    private final Tag<Item> validTool;
    private final ItemStack replant;
    private final BlockState replantState;
    protected final BooleanSupplier isReplantable;

    public HarvesterImpl() {
        this(null, null);
    }

    public HarvesterImpl(Tag<Item> validTool) {
        this(validTool, null);
    }

    public HarvesterImpl(Tag<Item> validTool, BlockState replantState) {
        this(validTool, ItemStack.EMPTY, replantState);
    }

    public HarvesterImpl(Tag<Item> validTool, ItemStack replant, BlockState replantState) {
        this.validTool = validTool;
        this.replant = replant;
        this.replantState = replantState;
        this.isReplantable = () -> !replant.isEmpty();
    }

    public boolean isBlockModifiable(PlayerEntity player, ServerWorld world, BlockPos pos) {
        return world.isBlockLoaded(pos) && world.isBlockModifiable(player, pos) && world.canMineBlockBody(player, pos);
    }

    public boolean holdingValidTool(ServerPlayerEntity playerEntity) {
        if (requiresTool.getAsBoolean()) {
            ItemStack heldItem = playerEntity.getHeldItem(Hand.MAIN_HAND);
            return validTool != null && validTool.contains(heldItem.getItem());
        }
        return true;
    }

    public boolean breakBlock(ServerPlayerEntity playerEntity, ServerWorld world, BlockPos pos, NonNullList<ItemStack> drops) {
        if (holdingValidTool(playerEntity) && isBlockModifiable(playerEntity, world, pos)) {
            BlockState blockState = world.getBlockState(pos);
            TileEntity tileEntity = world.getTileEntity(pos);
            ItemStack heldItem = playerEntity.getHeldItem(Hand.MAIN_HAND);

            drops.addAll(Block.getDrops(blockState, world, pos, tileEntity, playerEntity, heldItem));

            world.destroyBlock(pos, false);

            damageTool(playerEntity, heldItem, 1);

            return true;
        }
        return false;
    }

    public ItemStack takeReplantable(NonNullList<ItemStack> drops) {
        for (ItemStack stack : drops) {
            if (stack.isItemEqual(replant) && stack.getCount() >= replant.getCount()) {
                stack.shrink(replant.getCount());
                return replant.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean replant(ServerPlayerEntity player, ServerWorld world, BlockPos pos, NonNullList<ItemStack> drops) {
        return replant(player, world, pos, drops, null);
    }

    public <T extends Comparable<T>> boolean replant(ServerPlayerEntity player, ServerWorld world, BlockPos pos, NonNullList<ItemStack> drops, Map<IProperty<T>, T> stateMapper) {
        if (!isReplantable.getAsBoolean() || replantState == null) {
            return true;
        }
        if (holdingValidTool(player)) {
            ItemStack itemStack = takeReplantable(drops);
            if (!itemStack.isEmpty()) {
                BlockState state = replantState;
                if (stateMapper != null) for (Map.Entry<IProperty<T>, T> entry : stateMapper.entrySet()) {
                    state = state.with(entry.getKey(), entry.getValue());
                }
                world.setBlockState(pos, state);
                return true;
            }
        }
        return false;
    }

    public void dropStackAt(ServerPlayerEntity playerEntity, ServerWorld world, BlockPos pos, ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        itemEntity.setMotion(0, 0, 0);
        itemEntity.setOwnerId(playerEntity.getUniqueID());
        itemEntity.setNoPickupDelay();
        world.addEntity(itemEntity);
    }

    public void giveItemToPlayer(ServerPlayerEntity playerEntity, ItemStack stack) {
        if (!playerEntity.addItemStackToInventory(stack)) {
            playerEntity.dropItem(stack, false);
        }
    }

    public void tellPlayerNo(PlayerEntity player, String messageKey) {
        player.sendStatusMessage(new TranslationTextComponent(messageKey), true);
    }

    public boolean damageTool(ServerPlayerEntity player, ItemStack tool, int amount) {
        if (!damagesTool.getAsBoolean()) {
            return true;
        }
        if (tool.getMaxDamage() - tool.getDamage() >= amount) {
            tool.damageItem(amount, player, p -> p.sendBreakAnimation(Hand.MAIN_HAND));
            return true;
        }
        return false;
    }

    @Override
    public boolean canHarvest(ServerPlayerEntity player, Hand hand, ServerWorld world, BlockPos pos, BlockState state) {
        return holdingValidTool(player) && isBlockModifiable(player, world, pos);
    }
}
