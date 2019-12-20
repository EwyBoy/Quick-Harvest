package com.ewyboy.quickharvest.events;

import com.ewyboy.quickharvest.harvester.HarvestManager;
import com.ewyboy.quickharvest.harvester.IHarvestable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

public final class HarvestEvent {

    @SubscribeEvent
    public static void onBlockQuickHarvest(final RightClickBlock event) {
        if (event.getSide().isServer()) {
            BlockPos pos = event.getPos();
            BlockState state = event.getWorld().getBlockState(pos);
            World world = event.getWorld();
            PlayerEntity player = event.getPlayer();
            Hand hand = event.getHand();

            for (IHarvestable handler : HarvestManager.HANDLERS) {
                if (handler.canHarvest(state) && handler.tryHarvest(world, pos, state)) {
                    event.setUseBlock(Event.Result.DENY);
                    event.setUseItem(Event.Result.DENY);
                    event.setCanceled(true);
                    break;
                }
            }
        }
    }

}
