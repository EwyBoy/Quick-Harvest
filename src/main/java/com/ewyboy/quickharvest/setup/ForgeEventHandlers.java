package com.ewyboy.quickharvest.setup;


import com.ewyboy.quickharvest.QuickHarvest;
import com.ewyboy.quickharvest.api.Harvester;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.items.ItemHandlerHelper;

@EventBusSubscriber(bus = Bus.FORGE, modid = QuickHarvest.ID)
public class ForgeEventHandlers {

    @SubscribeEvent
    public static void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {

        if(event.getUseBlock() == Result.DENY || event.getUseItem() == Result.DENY) {
            return;
        }

        final World rawWorld = event.getWorld();

        if(!(rawWorld instanceof ServerWorld)) {
            return;
        }

        final ServerWorld world = (ServerWorld) rawWorld;
        final PlayerEntity player = event.getPlayer();

        if(player.getPose() == Pose.CROUCHING) {
            return;
        }

        final BlockPos pos = event.getPos();
        final BlockState state = world.getBlockState(pos);
        final Hand hand = event.getHand();
        final Direction side = event.getFace();

        for(final Harvester harvester : QuickHarvest.Registries.HARVESTERS.getValues()) {
            if(!harvester.enabled() || !harvester.canHarvest(player, hand, world, pos, state, side)) continue;
            harvester.harvest(player, hand, world, pos, state, side).forEach(stack -> ItemHandlerHelper.giveItemToPlayer(player, stack));
            player.swing(hand, true);
            event.setUseBlock(Result.DENY);
            event.setUseItem(Result.DENY);
            event.setCanceled(true);
            break;
        }
    }

}
