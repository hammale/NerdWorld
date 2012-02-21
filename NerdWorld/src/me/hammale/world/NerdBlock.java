package me.hammale.world;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;

public class NerdBlock implements Listener {

	 public final world plugin;
	 
	 public NerdBlock(world plugin){
		 this.plugin = plugin;
		 plugin.getServer().getPluginManager().registerEvents(this, plugin);
	 }
	 
	 @EventHandler
	 public void onBlockBreak(BlockBreakEvent e){	 
		 if(e.getBlock().getType() == Material.WALL_SIGN){
			 org.bukkit.block.Sign s = (org.bukkit.block.Sign) e.getBlock().getState();
			 if(s.getLine(1).equalsIgnoreCase("[GOTO]") && s.getLine(0) != null && s.getLine(2) != null){
				 String name = s.getLine(0);
				 name = name.replace("[", "");
				 name = name.replace("]", "");
				 if(plugin.isValidGate(name)){
					 if(e.getPlayer().isOp() == true){
						 if(plugin.active.contains(name)){
							 e.getPlayer().sendMessage(ChatColor.RED + "You can't close an open gate!");
							 e.setCancelled(true);
							 s.update();
						 }else{
							 plugin.removeGate(name);
							 e.getPlayer().sendMessage(ChatColor.AQUA + "Gate removed.");
						 }
					 }else{
						 e.getPlayer().sendMessage(ChatColor.RED + "You dont have permission to close this gate!");
						 e.setCancelled(true);
						 s.update();
					 }
				 }
			 }
		 }
	 }
	 
	 @EventHandler
	   public void onBlockFromTo(BlockFromToEvent e) {
		   Block b = e.getBlock();
		   if(b.getTypeId() == 8 || b.getTypeId() == 9){
			   for(String gate : plugin.active){
				   if(gate != null){
					 for(Location l : plugin.readGateCords(gate)){
						   if(l != null && l.getX() == b.getLocation().getX() && l.getY() == b.getLocation().getY() && l.getZ() == b.getLocation().getZ()){
							   e.setCancelled(true);
						   }
					   }
				   }
			   }
			   for(Location l : plugin.stopFlow){
				   if(l != null && l.getX() == b.getLocation().getX() && l.getY() == b.getLocation().getY() && l.getZ() == b.getLocation().getZ()){
					   e.setCancelled(true);
				   }
			   }
		   }
	    }
	 
}