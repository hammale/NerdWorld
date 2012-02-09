package me.hammale.world;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.material.Lever;

public class NerdPlayer extends PlayerListener {

	 public final world plugin;
	 
	 public NerdPlayer(world plugin){
		 this.plugin = plugin;
	 }
	
	public void onPlayerInteract(PlayerInteractEvent e){
		final Player p = e.getPlayer();		
	    if ((e.getAction() == Action.RIGHT_CLICK_BLOCK) && (e.getClickedBlock().getTypeId() == 69)) {
	    	Lever l = (Lever) e.getClickedBlock().getState().getData();
	    	Block initial = e.getClickedBlock().getRelative(l.getAttachedFace());
	    	if(initial.getTypeId() == 49){
	    		
	    		Block down = initial.getRelative(BlockFace.DOWN, 1);
	    		if(down.getTypeId() == 49){
	    			Block sign = e.getClickedBlock().getRelative(BlockFace.DOWN, 1);
	    			if(sign.getType() == Material.WALL_SIGN){
	    				org.bukkit.block.Sign s = (org.bukkit.block.Sign) sign.getState();
	    				if(s.getLine(0).equalsIgnoreCase("[GOTO]") && s.getLine(1).equalsIgnoreCase("[" + plugin.getRefreshWorld() +"]")){
	    					p.sendMessage(ChatColor.GREEN + "Teleporting to " + plugin.getRefreshWorld() + "...");
	    					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    					    public void run() {
	    	    					p.teleport(plugin.getServer().getWorld(plugin.getRefreshWorld()).getSpawnLocation());
	    					    }
	    					}, 60L);
	    				}else if(s.getLine(0).equalsIgnoreCase("[GOTO]") && s.getLine(1).equalsIgnoreCase("[HOME]")){
	    					p.sendMessage(ChatColor.GREEN + "Teleporting to " + plugin.getServer().getWorlds().get(0).getName() + "...");
	    					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    					    public void run() {
	    	    					p.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
	    					    }
	    					}, 60L);
	    				}	
	    			}
	    		}
	    	}
	    }
	}
}
