package com.afforess.growbie;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.zones.Zones;
import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


/**
 * Growbie for Bukkit
 * 
 * @author Afforess, Cyklo, UnixSystem
 */
public class Growbie extends JavaPlugin {

	private final GrowbiePlayerListener playerListener = new GrowbiePlayerListener(this);
	public static Growbie instance;
	public static File growbie;
    public static WorldGuardPlugin wg = null;
    public static Zones zones = null;
    private static Consumer consumer = null;

	public void onDisable() {
		instance = null;
		System.out.println(getDescription().getName() +" disabled.");
	}

	public void onEnable() {
		instance = this;
		growbie = this.getFile();
        // setupWorldGuard();
        setupZones();
		//getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvents(playerListener, this);
		System.out.println( getDescription().getName() + " version " + getDescription().getVersion() + " enabled.");
		GrowbieConfiguration.checkConfigFile();
        Plugin p = this.getServer().getPluginManager().getPlugin("LogBlock");
        if(p!=null && p instanceof LogBlock)
            consumer = ((LogBlock)p).getConsumer();
	}

    protected void consumerLog(Player p, BlockState from, BlockState to) {
        if(consumer==null) return;
        consumer.queueBlockReplace(p.getName(), from, to);
        // p.sendMessage("Changing "+from.getType().toString()+" to "+to.getType().toString());
    }


    protected void setupWorldGuard()
    {
        Plugin p = this.getServer().getPluginManager().getPlugin("WorldGuard");
        if(wg==null)
            if(p!=null)
                wg = (WorldGuardPlugin) p;
    }

    public static WorldGuardPlugin getWg() {
        return wg;
    }

    protected void setupZones()
    {
        Plugin p = this.getServer().getPluginManager().getPlugin("Zones");
        if(zones==null)
            if(p!=null)
                zones = (Zones) p;
    }

    public static Zones getZones() {
        return zones;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) return true;
        if(command.getName().equals("growbie")) {
            if(args.length>0 && args[0].equals("reload")) {
                sender.sendMessage("Reloading config...");
                GrowbieConfiguration.checkConfigFile();
            }
        }
        return true;
    }
}
