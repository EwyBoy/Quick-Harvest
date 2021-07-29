package com.ewyboy.quickharvest.setup;


import com.ewyboy.quickharvest.QuickHarvest;
import com.ewyboy.quickharvest.api.Harvester;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
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

        final Level rawWorld = event.getWorld();

        if(!(rawWorld instanceof ServerLevel)) {
            return;
        }

        final ServerLevel world = (ServerLevel) rawWorld;
        final Player player = event.getPlayer();

        if(player.getPose() == Pose.CROUCHING) {
            return;
        }

        final BlockPos pos = event.getPos();
        final BlockState state = world.getBlockState(pos);
        final InteractionHand hand = event.getHand();
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
