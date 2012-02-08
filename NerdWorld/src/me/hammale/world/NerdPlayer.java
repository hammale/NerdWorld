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
		Player p = e.getPlayer();		
	    if ((e.getAction() == Action.RIGHT_CLICK_BLOCK) && (e.getClickedBlock().getTypeId() == 69)) {
	    	Lever l = (Lever) e.getClickedBlock();
	    	Block initial = e.getClickedBlock().getRelative(l.getAttachedFace());
	    	if(initial.getTypeId() == 49){
	    		Block down = initial.getRelative(BlockFace.DOWN, 1);
	    		if(down.getTypeId() == 49){
	    			Block sign = initial.getRelative(BlockFace.DOWN, 1);
	    			if(sign.getType() == Material.SIGN){
	    				org.bukkit.block.Sign s = (org.bukkit.block.Sign) sign.getState().getData();
	    				if(s.getLine(0).equalsIgnoreCase("[GOTO]") && s.getLine(1).equalsIgnoreCase("[" + plugin.getRefreshWorld() +"]")){
	    					p.sendMessage(ChatColor.GREEN + "Teleporting to " + plugin.getRefreshWorld() + "...");
	    					p.teleport(plugin.getServer().getWorld(plugin.getRefreshWorld()).getSpawnLocation());
	    				}	
	    			}
	    		}
	    	}
	    }
	}
}
