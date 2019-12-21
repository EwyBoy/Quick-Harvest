package com.ewyboy.quickharvest.events;

import com.ewyboy.quickharvest.harvester.HarvestManager;
import com.ewyboy.quickharvest.harvester.IHarvestable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

public final class HarvestEvent {

    @SubscribeEvent
    public static void onBlockQuickHarvest(final RightClickBlock event) {
        World world = event.getWorld();
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            BlockPos pos = event.getPos();
            BlockState state = event.getWorld().getBlockState(pos);
            PlayerEntity player = event.getPlayer();
            Hand hand = event.getHand();

            IHarvestable handler = HarvestManager.HARVEST_HANDLER_MAP.get(state.getBlock());
            if (handler != null && handler.canHarvest(player, hand, serverWorld, pos, state)) {
                handler.harvest(player, hand, serverWorld, pos, state);
                event.setUseBlock(Event.Result.DENY);
                event.setUseItem(Event.Result.DENY);
                event.setCanceled(true);
            }
        }
    }

}
