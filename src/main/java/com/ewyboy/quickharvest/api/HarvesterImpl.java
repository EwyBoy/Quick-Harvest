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

/**
 * This is what you extend if you need to add specialized harvesters for your crops.
 */
public abstract class HarvesterImpl implements IHarvester {

    public static final String HARVEST_ERROR_PROTECTED_KEY = QuickHarvest.ID + ".message.error.protected";
    protected static final Direction[] NO_DIRECTIONS = new Direction[0];

    private static BooleanSupplier requiresTool = Config.SETTINGS :: requiresTool;
    private static BooleanSupplier damagesTool = Config.SETTINGS :: damagesTool;
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

    /**
     * This is the full constructor for creating a Harvester
     *
     * @param validTool    A tag which contains any tools that will be required if the configs require a tool. Leave it null if you never want to require a tool.
     * @param replant      An itemstack which will be taken out of the dropped items when the crop is replanted by the harvester.
     * @param replantState A state to use for replanting crops. If you do not like how this system replants blocks you can override the {@link #replant(ServerPlayerEntity, ServerWorld, BlockPos, NonNullList)} method
     */
    public HarvesterImpl(Tag<Item> validTool, ItemStack replant, BlockState replantState) {
        this.validTool = validTool;
        this.replant = replant;
        this.replantState = replantState;
        this.isReplantable = () -> !replant.isEmpty();
    }

    /**
     * This method is used to check if a block is modifiable by a player before letting them harvest it.
     *
     * @param player The player trying to modify the block
     * @param world  The world the block is in
     * @param pos    The position of the block
     * @return true if the block is modifiable
     */
    public boolean isBlockModifiable(PlayerEntity player, ServerWorld world, BlockPos pos) {
        return world.isBlockLoaded(pos) && world.isBlockModifiable(player, pos) && world.canMineBlockBody(player, pos);
    }

    /**
     * @param playerEntity The player
     * @return true if what the player is holding is valid for harvesting the crop
     */
    public boolean holdingValidTool(ServerPlayerEntity playerEntity) {
        if (requiresTool.getAsBoolean()) {
            ItemStack heldItem = playerEntity.getHeldItem(Hand.MAIN_HAND);
            return validTool != null && validTool.contains(heldItem.getItem());
        }
        return true;
    }

    /**
     * @param playerEntity The player breaking the block
     * @param world        The world the block is in
     * @param pos          The position of the block
     * @param drops        A list which the drops of this block will be added to
     * @return true if the block is broken
     */
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

    /**
     * @param drops A list of drops to search for the replantable itemstack
     * @return A stack containing the replantable itemblock if it was in the drops
     */
    public ItemStack takeReplantable(NonNullList<ItemStack> drops) {
        for (ItemStack stack : drops) {
            if (stack.isItemEqual(replant) && stack.getCount() >= replant.getCount()) {
                stack.shrink(replant.getCount());
                return replant.copy();
            }
        }
        return ItemStack.EMPTY;
    }


    /**
     * See {@link #replant(ServerPlayerEntity, ServerWorld, BlockPos, NonNullList, Map)}
     */
    public boolean replant(ServerPlayerEntity player, ServerWorld world, BlockPos pos, NonNullList<ItemStack> drops) {
        return replant(player, world, pos, drops, null);
    }

    /**
     * @param player      The player trying to replant the crop
     * @param world       The world to replant in
     * @param pos         The position to replant at
     * @param drops       A list of drops to take a seed from
     * @param stateMapper A map of property-value pairs to replace in the planted state
     * @return true if the crop is replanted
     */
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

    /**
     * @param playerEntity The player dropping the stack
     * @param world        The world to spawn the stack in
     * @param pos          The position to spawn the stack at
     * @param stack        The stack to drop
     */
    public void dropStackAt(ServerPlayerEntity playerEntity, ServerWorld world, BlockPos pos, ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        itemEntity.setMotion(0, 0, 0);
        itemEntity.setOwnerId(playerEntity.getUniqueID());
        itemEntity.setNoPickupDelay();
        world.addEntity(itemEntity);
    }

    /**
     * <strong>Note:</strong> This method will first try adding the stack to the players inventory. If this attempt is
     * unsuccessful it will instead thrown the item in the world as though the player has dropped the item.
     *
     * @param playerEntity The player to give the stack to
     * @param stack        The stack to give to the player
     */
    public void giveItemToPlayer(ServerPlayerEntity playerEntity, ItemStack stack) {
        if (!playerEntity.addItemStackToInventory(stack)) {
            playerEntity.dropItem(stack, false);
        }
    }

    /**
     * This method will tell the player a message using a translation key.
     * <strong>Note:</strong> You will need to add the translation to your lang file.
     *
     * @param player     The player to send the message to
     * @param messageKey A translation key for the message to tell the player
     */
    public void tellPlayerNo(PlayerEntity player, String messageKey) {
        player.sendStatusMessage(new TranslationTextComponent(messageKey), true);
    }

    /**
     * TODO ADD SHIT HERE
     * @return supplier
     */
    @Override
    public BooleanSupplier enabled() {
        return () -> true;
    }

    /**
     * @param player The player using the tool
     * @param tool   The tool to damage
     * @param amount The amount of damage to do
     * @return true if the damage was done.
     */
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
