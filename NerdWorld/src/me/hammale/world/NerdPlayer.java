package me.hammale.world;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Button;

public class NerdPlayer implements Listener {

	 public final world plugin;
	 
	 public NerdPlayer(world plugin){
		 this.plugin = plugin;
		 plugin.getServer().getPluginManager().registerEvents(this, plugin);
	 }
	 
	 @EventHandler
	 public void onPlayerMove(PlayerMoveEvent e){
		 Block b = e.getPlayer().getLocation().getBlock().getRelative(BlockFace.UP, 1);
		 if(b.getTypeId() == 8 || b.getTypeId() == 9){
			   for(String gate : plugin.active){
				   if(gate != null){
					 for(Location l : plugin.readGateCords(gate)){
						   if(l != null && l.getX() == b.getLocation().getX() && l.getY() == b.getLocation().getY() && l.getZ() == b.getLocation().getZ()){
							   e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Teleporting...");
							   String target = plugin.readGateTarget(gate);
							   target = target.replace("TARGET:", "");
							   e.getPlayer().teleport(plugin.readGateLoc(target));
						   }
					   }
				   }
			   }
		 }
	 }
	 
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		final Player p = e.getPlayer();		
	    if ((e.getAction() == Action.RIGHT_CLICK_BLOCK) && (e.getClickedBlock().getType() == Material.STONE_BUTTON && (e.getPlayer().getWorld() == plugin.getServer().getWorld(plugin.getRefreshWorld())) || e.getPlayer().getWorld() == plugin.getServer().getWorld(plugin.getRefreshWorld1()))) {
	    	Button l = (Button) e.getClickedBlock().getState().getData();
	    	Block initial = e.getClickedBlock().getRelative(l.getAttachedFace());
	    	if(initial.getTypeId() == 49){	
	    		Block down = initial.getRelative(BlockFace.DOWN, 1);
	    		if(down.getTypeId() == 49){
	    			Block sign = e.getClickedBlock().getRelative(BlockFace.DOWN, 1);
	    			if(sign.getType() == Material.WALL_SIGN){
	    				org.bukkit.block.Sign s = (org.bukkit.block.Sign) sign.getState();
	    				if(s.getLine(0).equals("[GOTO]") && s.getLine(1).equals("[HOME]")){
	    					p.sendMessage(ChatColor.GREEN + "Teleporting to " + plugin.getServer().getWorlds().get(0).getName() + "...");
	    					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    					    public void run() {
	    	    					p.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
	    					    }
	    					}, 30L);
	    				}	
	    				else if(s.getLine(0).equals("[GOTO]") && s.getLine(1) != null && s.getLine(1).contains("[") && s.getLine(1).contains("]")){ 
	    					String s1 = s.getLine(1).replace("[", "");
	    					final String s2 = s1.replace("]", "");
	    					p.sendMessage(ChatColor.GOLD + "Gate opened!");
	    					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    					    public void run() {
	    	    					p.teleport(plugin.getServer().getWorld(s2).getSpawnLocation());
	    					    }
	    					}, 30L);
	    				}
	    			}
	    		}
	    	}
	    }else if ((e.getAction() == Action.RIGHT_CLICK_BLOCK) && (e.getClickedBlock().getType() == Material.STONE_BUTTON)) {
	    	Button l = (Button) e.getClickedBlock().getState().getData();	
	    	final BlockFace bf = l.getAttachedFace();
	    	BlockFace left = getLeft(bf);
	    	BlockFace right = getRight(bf);
	    	
	    	final Block initial = e.getClickedBlock().getRelative(l.getAttachedFace());
	    	if(initial.getTypeId() == 49){
	    		
	    		Block down = initial.getRelative(BlockFace.DOWN, 1);
	    		if(down.getTypeId() == 49){
	    			Block sign = e.getClickedBlock().getRelative(BlockFace.DOWN, 1);
	    			if(sign.getType() == Material.WALL_SIGN){
	    				org.bukkit.block.Sign s = (org.bukkit.block.Sign) sign.getState();
//	    				if(s.getLine(0).equals("[GOTO]") && s.getLine(1).equals("[HOME]")){
//	    					p.sendMessage(ChatColor.GREEN + "Teleporting to " + plugin.getServer().getWorlds().get(0).getName() + "...");
//	    					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//	    					    public void run() {
//	    	    					p.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
//	    					    }
//	    					}, 30L);
//	    				}	
//	    				else 
	    					if(s.getLine(0) != null && s.getLine(0).contains("[") && s.getLine(0).contains("]") && s.getLine(1).equals("[GOTO]") && s.getLine(2) != null && s.getLine(2).contains("[") && s.getLine(2).contains("]")){ 
	    					String from1 = s.getLine(0).replace("[", "");
	    					final String from = from1.replace("]", "");
	    					String to1 = s.getLine(2).replace("[", "");
	    					final String to = to1.replace("]", "");
	    					if(checkGate(initial, right, left, bf)){
	    						if(!(plugin.active.contains(to))){
		    						if(plugin.isValidGate(from)){
		    							if(plugin.isValidGate(to)){
		    								p.sendMessage(ChatColor.GOLD + "Gate opened!");
		    								addBump1(initial, bf);
					    					addBump1(plugin.readGateLoc(to).getBlock().getRelative(BlockFace.UP).getRelative(getRight(plugin.readGateFace(to)), 3), plugin.readGateFace(to));
					    					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					    							public void run() {
					    								addBump2(initial, bf);
					    								addBump2(plugin.readGateLoc(to).getBlock().getRelative(BlockFace.UP).getRelative(getRight(plugin.readGateFace(to)), 3), plugin.readGateFace(to));
								    					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							    							public void run() {
							    								removeBump2(initial, bf);
							    								removeBump2(plugin.readGateLoc(to).getBlock().getRelative(BlockFace.UP).getRelative(getRight(plugin.readGateFace(to)), 3), plugin.readGateFace(to));
										    					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									    							public void run() {												    					
									    								removeBump1(initial, bf);
									    								removeBump1(plugin.readGateLoc(to).getBlock().getRelative(BlockFace.UP).getRelative(getRight(plugin.readGateFace(to)), 3), plugin.readGateFace(to));
												    					plugin.active.add(to);
												    					plugin.active.add(from);
												    					activateGate(initial, bf);
												    					activateGate(plugin.readGateLoc(to).getBlock().getRelative(BlockFace.UP).getRelative(getRight(plugin.readGateFace(to)), 3), plugin.readGateFace(to));
												    					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
												    					    public void run() {
												    					    	clearGate(initial, bf);
														    					clearGate(plugin.readGateLoc(to).getBlock().getRelative(BlockFace.UP).getRelative(getRight(plugin.readGateFace(to)), 3), plugin.readGateFace(to));
														    					plugin.active.remove(to);
														    					plugin.active.remove(from);
												    					    }
												    					}, 600L);
									    							}
										    					}, 5L);
							    							}
							    					}, 8L);
					    							}
					    					}, 5L);						    					
				    					}else{
			    							p.sendMessage(ChatColor.RED + "Invalid destination gate!");
			    						}
		    						}else if(p.isOp()){
		    							Location gatel = initial.getRelative(BlockFace.DOWN).getRelative(left, 3).getLocation();
		    							plugin.addGate(from, gatel, bf, innerGate(initial, bf), to);
		    							p.sendMessage(ChatColor.GREEN + "Gate successfully created!");
		    							if(plugin.isValidGate(to)){
					    					p.sendMessage(ChatColor.GREEN + "Teleporting to " + to + "...");
					    					addBump1(initial, bf);
					    					addBump1(plugin.readGateLoc(to).getBlock().getRelative(BlockFace.UP).getRelative(getRight(plugin.readGateFace(to)), 3), plugin.readGateFace(to));
					    					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					    							public void run() {
					    								addBump2(initial, bf);
					    								addBump2(plugin.readGateLoc(to).getBlock().getRelative(BlockFace.UP).getRelative(getRight(plugin.readGateFace(to)), 3), plugin.readGateFace(to));
								    					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							    							public void run() {
							    								removeBump2(initial, bf);
							    								removeBump2(plugin.readGateLoc(to).getBlock().getRelative(BlockFace.UP).getRelative(getRight(plugin.readGateFace(to)), 3), plugin.readGateFace(to));
										    					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									    							public void run() {												    					
									    								removeBump1(initial, bf);
									    								removeBump1(plugin.readGateLoc(to).getBlock().getRelative(BlockFace.UP).getRelative(getRight(plugin.readGateFace(to)), 3), plugin.readGateFace(to));
												    					plugin.active.add(to);
												    					plugin.active.add(from);
												    					activateGate(initial, bf);
												    					activateGate(plugin.readGateLoc(to).getBlock().getRelative(BlockFace.UP).getRelative(getRight(plugin.readGateFace(to)), 3), plugin.readGateFace(to));
												    					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
												    					    public void run() {
												    					    	clearGate(initial, bf);
														    					clearGate(plugin.readGateLoc(to).getBlock().getRelative(BlockFace.UP).getRelative(getRight(plugin.readGateFace(to)), 3), plugin.readGateFace(to));
														    					plugin.active.remove(to);
														    					plugin.active.remove(from);
												    					    }
												    					}, 600L);
									    							}
										    					}, 5L);
							    							}
							    					}, 8L);
					    							}
					    					}, 5L);					    				
				    					}else{
			    							p.sendMessage(ChatColor.RED + "Invalid destination gate!");
			    						}
		    						}else{
		    							p.sendMessage(ChatColor.GREEN + "Gate not active!");
		    						}
	    						}else{
		    						p.sendMessage(ChatColor.RED + "Destination gate busy!");
		    					}
	    					}else{
	    						p.sendMessage(ChatColor.RED + "Invalid design!");
	    					}  					
	    				}
	    			}
	    		}
	    	}
	    }
	}
	
	public HashSet<Location> innerGate(Block initial, BlockFace forward){
		HashSet<Location> cords = new HashSet<Location>();
		BlockFace left = getLeft(forward);
		Block start = initial.getRelative(BlockFace.DOWN, 1).getRelative(forward, 3);
		for(int i = 1;i<=4;i++){
			Block b1 = start.getRelative(BlockFace.UP, i);
			cords.add(b1.getLocation());
		}
		Block a1 = start.getRelative(BlockFace.DOWN, 1).getRelative(left, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a1.getRelative(BlockFace.UP, i);
			cords.add(b1.getLocation());
		}
		Block a2 = start.getRelative(left, 2).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a2.getRelative(BlockFace.UP, i);
			cords.add(b1.getLocation());
		}
		Block a3 = start.getRelative(left, 3).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a3.getRelative(BlockFace.UP, i);
			cords.add(b1.getLocation());
		}
		Block a4 = start.getRelative(left, 4).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 =a4.getRelative(BlockFace.UP, i);
			cords.add(b1.getLocation());
		}
		Block a5 = start.getRelative(left, 5);
		for(int i = 1;i<=4;i++){
			Block b1 = a5.getRelative(BlockFace.UP, i);
			cords.add(b1.getLocation());
		}
		return cords;
	}
	
	public void addBump2(Block initial, BlockFace forward){
		BlockFace left = getLeft(forward);
		Block start = initial.getRelative(BlockFace.DOWN, 1).getRelative(forward, 1);
		for(int i = 1;i<=4;i++){
			Block b1 = start.getRelative(BlockFace.UP, i);
			b1.setTypeId(9);
			plugin.stopFlow.add(b1.getLocation());
		}
		Block a1 = start.getRelative(BlockFace.DOWN, 1).getRelative(left, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a1.getRelative(BlockFace.UP, i);
			b1.setTypeId(9);
			plugin.stopFlow.add(b1.getLocation());
		}
		Block a2 = start.getRelative(left, 2).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a2.getRelative(BlockFace.UP, i);
			b1.setTypeId(9);
			plugin.stopFlow.add(b1.getLocation());
		}
		Block a3 = start.getRelative(left, 3).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a3.getRelative(BlockFace.UP, i);
			b1.setTypeId(9);
			plugin.stopFlow.add(b1.getLocation());
		}
		Block a4 = start.getRelative(left, 4).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 =a4.getRelative(BlockFace.UP, i);
			b1.setTypeId(9);
			plugin.stopFlow.add(b1.getLocation());
		}
		Block a5 = start.getRelative(left, 5);
		for(int i = 1;i<=4;i++){
			Block b1 = a5.getRelative(BlockFace.UP, i);
			b1.setTypeId(9);
			plugin.stopFlow.add(b1.getLocation());
		}
	}
	
	public void addBump1(Block initial, BlockFace forward){
		BlockFace left = getLeft(forward);
		Block start = initial.getRelative(BlockFace.DOWN, 1).getRelative(forward, 2);
		for(int i = 1;i<=4;i++){
			Block b1 = start.getRelative(BlockFace.UP, i);
			b1.setTypeId(9);
			plugin.stopFlow.add(b1.getLocation());
		}
		Block a1 = start.getRelative(BlockFace.DOWN, 1).getRelative(left, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a1.getRelative(BlockFace.UP, i);
			b1.setTypeId(9);
			plugin.stopFlow.add(b1.getLocation());
		}
		Block a2 = start.getRelative(left, 2).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a2.getRelative(BlockFace.UP, i);
			b1.setTypeId(9);
			plugin.stopFlow.add(b1.getLocation());
		}
		Block a3 = start.getRelative(left, 3).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a3.getRelative(BlockFace.UP, i);
			b1.setTypeId(9);
			plugin.stopFlow.add(b1.getLocation());
		}
		Block a4 = start.getRelative(left, 4).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 =a4.getRelative(BlockFace.UP, i);
			b1.setTypeId(9);
			plugin.stopFlow.add(b1.getLocation());
		}
		Block a5 = start.getRelative(left, 5);
		for(int i = 1;i<=4;i++){
			Block b1 = a5.getRelative(BlockFace.UP, i);
			b1.setTypeId(9);
			plugin.stopFlow.add(b1.getLocation());
		}
	}
	
	public void removeBump2(Block initial, BlockFace forward){
		BlockFace left = getLeft(forward);
		Block start = initial.getRelative(BlockFace.DOWN, 1).getRelative(forward, 1);
		for(int i = 1;i<=4;i++){
			Block b1 = start.getRelative(BlockFace.UP, i);
			b1.setTypeId(0);
			plugin.stopFlow.remove(b1.getLocation());
		}
		Block a1 = start.getRelative(BlockFace.DOWN, 1).getRelative(left, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a1.getRelative(BlockFace.UP, i);
			b1.setTypeId(0);
			plugin.stopFlow.remove(b1.getLocation());
		}
		Block a2 = start.getRelative(left, 2).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a2.getRelative(BlockFace.UP, i);
			b1.setTypeId(0);
			plugin.stopFlow.remove(b1.getLocation());
		}
		Block a3 = start.getRelative(left, 3).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a3.getRelative(BlockFace.UP, i);
			b1.setTypeId(0);
			plugin.stopFlow.remove(b1.getLocation());
		}
		Block a4 = start.getRelative(left, 4).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 =a4.getRelative(BlockFace.UP, i);
			b1.setTypeId(0);
			plugin.stopFlow.remove(b1.getLocation());
		}
		Block a5 = start.getRelative(left, 5);
		for(int i = 1;i<=4;i++){
			Block b1 = a5.getRelative(BlockFace.UP, i);
			b1.setTypeId(0);
			plugin.stopFlow.remove(b1.getLocation());
		}
	}
	
	public void removeBump1(Block initial, BlockFace forward){
		BlockFace left = getLeft(forward);
		Block start = initial.getRelative(BlockFace.DOWN, 1).getRelative(forward, 2);
		for(int i = 1;i<=4;i++){
			Block b1 = start.getRelative(BlockFace.UP, i);
			b1.setTypeId(0);
			plugin.stopFlow.remove(b1.getLocation());
		}
		Block a1 = start.getRelative(BlockFace.DOWN, 1).getRelative(left, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a1.getRelative(BlockFace.UP, i);
			b1.setTypeId(0);
			plugin.stopFlow.remove(b1.getLocation());
		}
		Block a2 = start.getRelative(left, 2).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a2.getRelative(BlockFace.UP, i);
			b1.setTypeId(0);
			plugin.stopFlow.remove(b1.getLocation());
		}
		Block a3 = start.getRelative(left, 3).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a3.getRelative(BlockFace.UP, i);
			b1.setTypeId(0);
			plugin.stopFlow.remove(b1.getLocation());
		}
		Block a4 = start.getRelative(left, 4).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 =a4.getRelative(BlockFace.UP, i);
			b1.setTypeId(0);
			plugin.stopFlow.remove(b1.getLocation());
		}
		Block a5 = start.getRelative(left, 5);
		for(int i = 1;i<=4;i++){
			Block b1 = a5.getRelative(BlockFace.UP, i);
			b1.setTypeId(0);
			plugin.stopFlow.remove(b1.getLocation());
		}
	}
	
	public void activateGate(Block initial, BlockFace forward){
		BlockFace left = getLeft(forward);
		int x = 9;
		Block start = initial.getRelative(BlockFace.DOWN, 1).getRelative(forward, 3);
		for(int i = 1;i<=4;i++){
			Block b1 = start.getRelative(BlockFace.UP, i);
			b1.setTypeId(x);
		}
		Block a1 = start.getRelative(BlockFace.DOWN, 1).getRelative(left, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a1.getRelative(BlockFace.UP, i);
			b1.setTypeId(x);
		}
		Block a2 = start.getRelative(left, 2).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a2.getRelative(BlockFace.UP, i);
			b1.setTypeId(x);
		}
		Block a3 = start.getRelative(left, 3).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a3.getRelative(BlockFace.UP, i);
			b1.setTypeId(x);
		}
		Block a4 = start.getRelative(left, 4).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 =a4.getRelative(BlockFace.UP, i);
			b1.setTypeId(x);
		}
		Block a5 = start.getRelative(left, 5);
		for(int i = 1;i<=4;i++){
			Block b1 = a5.getRelative(BlockFace.UP, i);
			b1.setTypeId(x);
		}
	}
	
	public void clearGate(Block initial, BlockFace forward){
		BlockFace left = getLeft(forward);
		int x = 0;
		Block start = initial.getRelative(BlockFace.DOWN, 1).getRelative(forward, 3);
		for(int i = 1;i<=4;i++){
			Block b1 = start.getRelative(BlockFace.UP, i);
			b1.setTypeId(x);
		}
		Block a1 = start.getRelative(BlockFace.DOWN, 1).getRelative(left, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a1.getRelative(BlockFace.UP, i);
			b1.setTypeId(x);
		}
		Block a2 = start.getRelative(left, 2).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a2.getRelative(BlockFace.UP, i);
			b1.setTypeId(x);
		}
		Block a3 = start.getRelative(left, 3).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 = a3.getRelative(BlockFace.UP, i);
			b1.setTypeId(x);
		}
		Block a4 = start.getRelative(left, 4).getRelative(BlockFace.DOWN, 1);
		for(int i = 1;i<=6;i++){
			Block b1 =a4.getRelative(BlockFace.UP, i);
			b1.setTypeId(x);
		}
		Block a5 = start.getRelative(left, 5);
		for(int i = 1;i<=4;i++){
			Block b1 = a5.getRelative(BlockFace.UP, i);
			b1.setTypeId(x);
		}
	}
	
	public boolean checkGate(Block initial, BlockFace right, BlockFace left, BlockFace forward){
		Block start = initial.getRelative(BlockFace.DOWN, 1);
		Block one = start.getRelative(forward, 3);
		Block two = one.getRelative(right, 1).getRelative(BlockFace.UP, 1);
		Block three = two.getRelative(BlockFace.UP, 1);
		Block four = two.getRelative(BlockFace.UP, 2);
		Block five = two.getRelative(BlockFace.UP, 3);
		Block six = two.getRelative(BlockFace.UP, 4).getRelative(left, 1);
		Block seven = six.getRelative(BlockFace.UP, 1).getRelative(left, 1);
		Block eight = seven.getRelative(left, 1);
		Block nine = eight.getRelative(left, 2);
		Block ten = seven.getRelative(left, 3);
		Block eleven = ten.getRelative(BlockFace.DOWN, 1).getRelative(left, 1);
		Block twelve = eleven.getRelative(BlockFace.DOWN, 1).getRelative(left, 1);
		Block thirt = twelve.getRelative(BlockFace.DOWN, 1);
		Block fort = twelve.getRelative(BlockFace.DOWN, 2);
		Block fift = twelve.getRelative(BlockFace.DOWN, 3);
		Block sixt = fift.getRelative(BlockFace.DOWN, 1).getRelative(right, 1);
		Block sevent = sixt.getRelative(BlockFace.DOWN, 1).getRelative(right, 1);
		Block eteen = sevent.getRelative(right, 1);
		Block nteen = sevent.getRelative(right, 2);
		Block twenty = sevent.getRelative(right, 3);
		
		if(one.getTypeId() == 49 &&
			two.getTypeId() == 49 &&
			three.getTypeId() == 49 &&
			four.getTypeId() == 49 &&
			five.getTypeId() == 49 &&
			six.getTypeId() == 49 &&
			seven.getTypeId() == 49 &&
			eight.getTypeId() == 49 &&
			nine.getTypeId() == 49 &&
			ten.getTypeId() == 49 &&
			eleven.getTypeId() == 49 &&
			twelve.getTypeId() == 49 &&
			thirt.getTypeId() == 49 &&
			fort.getTypeId() == 49 &&
			fift.getTypeId() == 49 &&
			sixt.getTypeId() == 49 &&
			sevent.getTypeId() == 49 &&
			eteen.getTypeId() == 49 &&
			nteen.getTypeId() == 49 &&
			twenty.getTypeId() == 49){
			return true;
		}else{
			return false;	
		}
	}
	
	public BlockFace getLeft(BlockFace bf){
		if(bf == BlockFace.NORTH){
			return BlockFace.WEST;
		}else if(bf == BlockFace.EAST){
			return BlockFace.NORTH;
		}else if(bf == BlockFace.SOUTH){
			return BlockFace.EAST;
		}else if(bf == BlockFace.WEST){
			return BlockFace.SOUTH;
		}else{
			return null;
		}
	}
	
	public BlockFace getRight(BlockFace bf){
		if(bf == BlockFace.NORTH){
			return BlockFace.EAST;
		}else if(bf == BlockFace.EAST){
			return BlockFace.SOUTH;
		}else if(bf == BlockFace.SOUTH){
			return BlockFace.WEST;
		}else if(bf == BlockFace.WEST){
			return BlockFace.NORTH;
		}else{
			return null;
		}
	}
}