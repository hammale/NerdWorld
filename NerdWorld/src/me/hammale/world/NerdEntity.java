package me.hammale.world;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class NerdEntity implements Listener {

	 public final world plugin;
	 
	 public NerdEntity(world plugin){
		 this.plugin = plugin;
		 plugin.getServer().getPluginManager().registerEvents(this, plugin);
	 }
	 
	 @EventHandler
	 public void onCreatureSpawn(CreatureSpawnEvent e){
		 if(e.getEntity().getWorld().getName().equals(plugin.getRefreshWorld1())){
			 e.setCancelled(true);
		 }
	 }
}
