package com.afforess.growbie;

import com.zones.model.ZoneBase;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class GrowbiePlayerListener implements Listener {

    private Growbie plugin;

	public GrowbiePlayerListener(Growbie instance) {
        this.plugin = instance;
		// TODO Auto-generated constructor stub
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
        BlockState before = block.getState();
		
		if (!Gardener.isBonemeal(player.getItemInHand())) {
			return;
		}

        boolean canbuild=true;

        /* if(plugin.getWg()!=null && plugin.getWg().getGlobalConfiguration().get(block.getWorld()).useRegions)
        {
            GlobalRegionManager gm = plugin.getWg().getGlobalRegionManager();
            RegionManager rm = gm.get(event.getClickedBlock().getWorld());
            Location loc = event.getClickedBlock().getLocation();
            Vector vec = new Vector(loc.getX(), loc.getY(), loc.getZ());
            LocalPlayer localplayer = new BukkitPlayer(plugin.getWg(), event.getPlayer());
            canbuild = rm.getApplicableRegions(vec).canBuild(localplayer);
            if(!canbuild)
                event.getPlayer().sendMessage(ChatColor.RED+"You do not have permission to use bonemeal in this region!");
        } */

        if(Growbie.getZones()!=null) {
            ZoneBase zb = Growbie.getZones().getWorldManager(block.getWorld()).getActiveZone(block);
            if(zb!=null) {
                canbuild = zb.getAccess(player).canBuild();
                // canbuild
                // com.zones.model.ZonesAccess zac = ;
                if(!canbuild) {
                    player.sendMessage(ChatColor.RED+"You do not have permission to use bonemeal in this zone!");

                }
            }
        }

        if(!canbuild)
        {
            // player.sendMessage(ChatColor.RED + "You don't have permissions for this region!");
            return;
        }

        // player.sendMessage("Trying growPlants");
		boolean action = Gardener.growPlants(block, player);
		if (!action) {
            // player.sendMessage("Trying growBlocks");
			action = Gardener.growBlocks(block, player);
		}
		if (!action) {
            // player.sendMessage("Trying spreadBlocks");
			action = Gardener.spreadBlocks(block, player);
		}
        if(!action) {
            // player.sendMessage("Trying spreadPlants");
            action = Gardener.spreadPlants(block, player);
        }
		if (!action) {
            // player.sendMessage("Trying growTree");
			action = Gardener.growTree(block, player);
		}

		if (action){
			Gardener.useItem(player);
			event.setCancelled(true);
            // plugin.consumerLog(player, before, );
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
		}
	}
}
