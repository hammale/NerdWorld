package me.hammale.world;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class world extends JavaPlugin {
	
	public FileConfiguration config;
	
	public HashSet<String> removed = new HashSet<String>();
	public HashSet<Location> stopFlow = new HashSet<Location>();
	public HashSet<String> active = new HashSet<String>();
	
	Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		loadConfiguration();
		log.info("[NerdWorld] Version: " + pdfFile.getVersion() + " Enabled!");
		checkWorld();
		moniterRefresh();
		makeFolders();
		getServer().getPluginManager().registerEvents(new NerdBlock(this), this);
		getServer().getPluginManager().registerEvents(new NerdPlayer(this), this);
		getServer().getPluginManager().registerEvents(new NerdEntity(this), this);
	}
	
	public void checkWorld(){
		if(getServer().getWorld(getRefreshWorld()) == null){
			getServer().createWorld(new WorldCreator(getRefreshWorld()).environment(World.Environment.NORMAL).generator("MineralVein"));
			addPortal(getRefreshWorld());
		}
		if(getServer().getWorld(getRefreshWorld1()) == null){
			getServer().createWorld(new WorldCreator(getRefreshWorld1()).environment(World.Environment.NORMAL));
			addPortal(getRefreshWorld1());
		}
	}
	
	public void loadConfiguration(){
	    if(exists() == false){
		    config = getConfig();
		    config.options().copyDefaults(false);
		    String path = "RefreshRate";
		    String path1 = "RefreshWorld";
		    String path2 = "RefreshRateSecond";
		    String path3 = "RefreshWorldSecond";
		    config.addDefault(path, 10);
		    config.addDefault(path1, "mobs");
		    config.addDefault(path2, 60);
		    config.addDefault(path3, "nomobs");
		    config.options().copyDefaults(true);
		    saveConfig();
	    }
	}
	
	private boolean exists() {	
			try{
			File file = new File("plugins/NerdWorld/config.yml"); 
	        if (file.exists()) { 
	        	return true;
	        }else{
	        	return false;
	        }

			}catch (Exception e){
			  System.err.println("Error: " + e.getMessage());
			  return true;
			}
	}
	

	public void makeFolders(){
		File file = new File("plugins/NerdWorld/gates"); 
        if (!file.exists()) {
        	file.mkdir();
        }
	}
		
	  public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args){
			if(cmd.getName().equalsIgnoreCase("nw")){
				if(args.length == 2){
					if(args[0].equalsIgnoreCase("create")){
						for(World w : getServer().getWorlds()){						
							if(args[1].equalsIgnoreCase(w.getName())){
								sender.sendMessage(ChatColor.RED + "Error! World already existst.");
								return true;
							}
						}
						sender.sendMessage(ChatColor.AQUA + "Creating world....");
						getServer().createWorld(new WorldCreator(args[1]).environment(World.Environment.NORMAL));
						sender.sendMessage(ChatColor.GREEN + "World complete!");
					}else if(args[0].equalsIgnoreCase("unload")){
						if(getServer().getWorld(args[1]) != null){
							getServer().unloadWorld(args[1], false);
							sender.sendMessage(ChatColor.AQUA + "Unloading world...");
							unloadPlayers(args[1]);
							sender.sendMessage(ChatColor.GREEN + "World unloaded!");
						}else{
							sender.sendMessage(ChatColor.RED + "Error! World doesn't exist.");
							return true;
						}
					}else if(args[0].equalsIgnoreCase("unplayer")){
						sender.sendMessage(ChatColor.AQUA + "Removing all players on " + args[1] + "...");
						unloadPlayers(args[1]);
						sender.sendMessage(ChatColor.GREEN + "Players removed!");
					}else if(args[0].equalsIgnoreCase("gate")){
						if(args[1].equalsIgnoreCase("list")){
							sender.sendMessage(ChatColor.GOLD + "<--- " + ChatColor.BLUE + "Gates" + ChatColor.GOLD + " --->");
							HashSet<String> gates = getGates();
							for(String s : gates){
								String status = "Offline";
								ChatColor acolor = ChatColor.RED;
								if(s.contains(".dat")){
									s = s.replace(".dat", "");
								}
								if(active.contains(s)){
									status = "Active";
									acolor = ChatColor.GREEN;
								}
								String target = readGateTarget(s).replace("TARGET:", "");
								sender.sendMessage(ChatColor.DARK_AQUA + s + ChatColor.WHITE + " -> " + ChatColor.DARK_PURPLE + target + ChatColor.WHITE + " - " + acolor + status);
							}
						}
					}
					return true;
			}else if(args.length == 3){
				if(args[0].equalsIgnoreCase("create")){
					for(World w : getServer().getWorlds()){
						if(args[1].equalsIgnoreCase(w.getName	())){
							sender.sendMessage(ChatColor.RED + "Error! World already existst.");
							return true;
						}
					}
					sender.sendMessage(ChatColor.AQUA + "Creating " + args[2] + " world...");
					Environment env = Environment.NORMAL;
					try{
						env = Environment.valueOf(args[2]);
					}catch(IllegalArgumentException e){
						sender.sendMessage(ChatColor.RED + "Error invalid world type! Valid ones are: NORMAL, NETHER, THE_END.");
						return true;
					}
					getServer().createWorld(new WorldCreator(args[1]).environment(env));
					sender.sendMessage(ChatColor.GREEN + "World complete!");
				}else if(args[0].equalsIgnoreCase("gate")){
					if(args[1].equalsIgnoreCase("goto")){
						if(args[2] != null){
							sender.sendMessage(ChatColor.AQUA + "Teleporting to gate " + args[2] + "...");
							
						}
					}
				}
				return true;
			}else if(args.length == 4){
				if(args[0].equalsIgnoreCase("create")){
					for(World w : getServer().getWorlds()){					
						if(args[1].equalsIgnoreCase(w.getName())){
							sender.sendMessage(ChatColor.RED + "Error! World already existst.");
							return true;
						}
					}
					sender.sendMessage(ChatColor.AQUA + "Creating " + args[2] + " world with " + args[3] + " generator...");
					Environment env = Environment.NORMAL;
					try{
						env = Environment.valueOf(args[2]);
					}catch(IllegalArgumentException e){
						sender.sendMessage(ChatColor.RED + "Error invalid world type! Valid ones are: NORMAL, NETHER, THE_END.");
						return true;
					}
					getServer().createWorld(new WorldCreator(args[1]).environment(env).generator(args[3]));
					sender.sendMessage(ChatColor.GREEN + "World complete!");
				}
				return true;
			}else if(args.length == 1){	
					if(args[0].equalsIgnoreCase("list")){
						sender.sendMessage(ChatColor.GOLD + "<--- " + ChatColor.BLUE + "Loaded Worlds" + ChatColor.GOLD + " --->");
						for(World w : getServer().getWorlds()){
							String gen = null;
							if(w.getGenerator() == null){
								gen = "DEFAULT";
							}else{
								gen = w.getGenerator().toString();
							}
							if(w.getEnvironment() == Environment.NORMAL){
								sender.sendMessage(ChatColor.AQUA + w.getName() + ChatColor.WHITE + " - " + ChatColor.DARK_GREEN + "NORMAL" + ChatColor.WHITE + " - " + ChatColor.LIGHT_PURPLE + "Chunk Gen: " + gen);
							}else if(w.getEnvironment() == Environment.NETHER){
								sender.sendMessage(ChatColor.AQUA + w.getName() + ChatColor.WHITE + " - " + ChatColor.RED + "NETHER" + ChatColor.WHITE + " - " + ChatColor.LIGHT_PURPLE + "Chunk Gen: " + gen);
							}else if(w.getEnvironment() == Environment.THE_END){
								sender.sendMessage(ChatColor.AQUA + w.getName() + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "END" + ChatColor.WHITE + " - " + ChatColor.LIGHT_PURPLE + "Chunk Gen: " + gen);
							}
						}
						return true;
					}else if(args[0].equalsIgnoreCase("help")){
						PluginDescriptionFile pdfFile = this.getDescription();
						sender.sendMessage(ChatColor.GOLD + "<--- " + ChatColor.BLUE + "NerdWorld Version: " + pdfFile.getVersion() + ChatColor.GOLD + " --->");
						sender.sendMessage(ChatColor.DARK_GREEN + "/nw list" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Lists all loaded worlds");
						sender.sendMessage(ChatColor.DARK_GREEN + "/nw create <world_name>" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Creates a normal world named <world_name>");
						sender.sendMessage(ChatColor.DARK_GREEN + "/nw create <world_name> [environment]" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Creates an <environment> world named <world_name>");
						sender.sendMessage(ChatColor.DARK_GREEN + "/nw create <world_name> [environment] [generator]" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Creates an <environment> world named <world_name> with a [generator] generator");
						sender.sendMessage(ChatColor.DARK_GREEN + "/nw unload <world_name>" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Unloads <world_name>");
						sender.sendMessage(ChatColor.DARK_GREEN + "/nw unplayer" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Removes all player's from <world_name>");
						sender.sendMessage(ChatColor.DARK_GREEN + "/nw gate list" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Lists all gates and there targets");
						sender.sendMessage(ChatColor.DARK_GREEN + "/nw gate goto <gate_name>" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "TP's you to <gate_name>");
						return true;
					}
				}
				return false;
			}
			return true;
	  }
	
	  public boolean removeWorld(File dir){	 
		  if (dir.isDirectory()) {
				  String[] children = dir.list();
				  for (int i=0; i<children.length; i++) {
					  boolean success = removeWorld(new File(dir, children[i]));
					  if (!success) {
						  return false;
					  }
				  }
		  }else{
			  return false;
		  }
		return false;
	  }
	
	public HashSet<String> getGates(){
		File dir = new File("plugins/NerdWorld/gates");
		HashSet<String> list = new HashSet<String>();
		if (dir.isDirectory()) {
		  String[] files = dir.list();
		  for (String file : files) {
		      list.add(file);
		  }
		}
		return list;
	}
	  
	public void addGate(String s, Location l, BlockFace bf, HashSet<Location> locs, String target) {
		if(l != null){
			try{
			File file = new File("plugins/NerdWorld/gates/" + s + ".dat");
	        Scanner scan = null;  
	        String str = null;  
	  
	        if (file.exists()) {
	            scan = new java.util.Scanner(file);  
	            str = scan.nextLine();  
	            while (scan.hasNextLine()) {
	                str = str.concat("\n" + scan.nextLine());  
	            }  
	        }
	        PrintWriter out = new PrintWriter(new FileWriter(file, true));  
			  int x = (int)l.getX();
			  int y = (int)l.getY();
			  int z = (int)l.getZ();
			  
			  str = (x + "," + y + "," + z + "," + l.getWorld().getName() + "," +  bf.toString());
	        out.println(str);
	        for(Location loc : locs){
	        	Block lb = loc.getBlock();
	        	World locw = loc.getWorld();
	        	int lx = (int)lb.getX();
				int ly = (int)lb.getY();
				int lz = (int)lb.getZ();
				str = (lx + "," + ly + "," + lz + "," + locw.getName());
	        	out.println(str); 
	        }
	        str = ("TARGET:" + target);
        	out.println(str);
	        out.close();
	        if(scan != null){
	        	scan.close();
	        }
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public boolean isValidGate(String s){
		File file = new File("plugins/NerdWorld/gates/" + s + ".dat");
        if (file.exists()) {
        	return true;
        }else{
        	return false;
        }
	}
	
	public String readGateTarget(String s){
		try{
				  FileInputStream fstream = new FileInputStream("plugins/NerdWorld/gates/" + s + ".dat");
				  DataInputStream in = new DataInputStream(fstream);
				  BufferedReader br = new BufferedReader(new InputStreamReader(in));
				  String strLine;
				  while ((strLine = br.readLine()) != null && (!(strLine.contains("TARGET:")))){
				  }
				  in.close();
				  br.close();
				  fstream.close();
				  return strLine;
		}catch (Exception e){
			  System.err.println("Error5: " + e.getMessage());
			  }
		return null;		
	}
	
	public void removeGate(String s){
		File file = new File("plugins/NerdWorld/gates/" + s + ".dat");
        if (file.exists()) {
        	file.delete();
        }
	}
	
	public Location readGateLoc(String s){
		Location l = null;
		try{
				  FileInputStream fstream = new FileInputStream("plugins/NerdWorld/gates/" + s + ".dat");
				  DataInputStream in = new DataInputStream(fstream);
				  BufferedReader br = new BufferedReader(new InputStreamReader(in));
				  String strLine;
				  //while ((strLine = br.readLine()) != null){
				  if ((strLine = br.readLine()) != null){
					  String delims = ",";
					  String[] cords = strLine.split(delims);
		
					  int x = Integer.parseInt(cords[0]);
					  int y = Integer.parseInt(cords[1]);
					  int z = Integer.parseInt(cords[2]);
					  World w = getServer().getWorld(cords[3]);			  
					  l = w.getBlockAt(x, y, z).getLocation();
		
				  }
				  in.close();
				  br.close();
				  fstream.close();
				  return l;
		}catch (Exception e){
			  System.err.println("Error1: " + e.getMessage());
			  }
		return null;		
	}
	
	public HashSet<Location> readGateCords(String s){
		HashSet<Location> cords = new HashSet<Location>();
		try{
				  FileInputStream fstream = new FileInputStream("plugins/NerdWorld/gates/" + s + ".dat");
				  DataInputStream in = new DataInputStream(fstream);
				  BufferedReader br = new BufferedReader(new InputStreamReader(in));
				  String strLine = null;
				  while ((strLine = br.readLine()) != null){
					  String delims = ",";
					  String[] cord = strLine.split(delims);
					  if(!(cord[0].contains("TARGET:"))){
						  int x = Integer.parseInt(cord[0]);
						  int y = Integer.parseInt(cord[1]);
						  int z = Integer.parseInt(cord[2]);
						  World w = getServer().getWorld(cord[3]);				  
						  if(w.getBlockAt(x,y,z).getLocation() != null){
							  cords.add(w.getBlockAt(x,y,z).getLocation());
						  }
					  }	
				  }
				  in.close();
				  br.close();
				  fstream.close();
				  return cords;
		}catch (Exception e){
			  e.printStackTrace();
		}
		return null;		
	}
	
	public BlockFace readGateFace(String s){
		BlockFace bf = null;
		try{
			  FileInputStream fstream = new FileInputStream("plugins/NerdWorld/gates/" + s + ".dat");
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  if ((strLine = br.readLine()) != null){
				  String delims = ",";
				  String[] cords = strLine.split(delims);
				  bf = BlockFace.valueOf(cords[4]);
			  }
			  in.close();
			  br.close();
			  fstream.close();
			  return bf;
		}catch (Exception e){
			  System.err.println("Error3: " + e.getMessage());
			  }
		return null;		
	}
	
	public int getRate(){
	    config = getConfig();
	    int amnt = config.getInt("RefreshRate");
	    amnt = (amnt*60)*20;
	    return amnt;
	}
	
	public String getRefreshWorld(){
	    config = getConfig();
	    String message = config.getString("RefreshWorld"); 
	    return message;
	}
	
	public int getRate1(){
	    config = getConfig();
	    int amnt = config.getInt("RefreshRateSecond");
	    amnt = (amnt*60)*20;
	    return amnt;
	}
	
	public String getRefreshWorld1(){
	    config = getConfig();
	    String message = config.getString("RefreshWorldSecond"); 
	    return message;
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		getServer().getScheduler().cancelTasks(this);
		log.info("[NerdWorld] Version: " + pdfFile.getVersion() + " Disabled!");	
	}
	
	public void moniterRefresh(){
		int time = getRate()-6000;
		if(time <= 1200){
			time = 1200;
		}
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    public void run() {
		        threeMin(getRefreshWorld());
		    }
		}, time, time);
		
		int time2 = getRate1()-6000;
		if(time2 <= 1200){
			time2 = 1200;
		}
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    public void run() {
		        threeMin(getRefreshWorld1());
		    }
		}, time2, time2);
	}
	
	public void threeMin(final String s){
		getServer().broadcastMessage(ChatColor.RED + "Refreshing " + s + " in 3 min!");
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		    public void run() {
				refreshWorld(s);
		    }
		}, 3600L);
	}
	
	public void refreshWorld(String s){
			getServer().broadcastMessage(ChatColor.RED + "Refreshing " + s + "...");
			removePlayers(s);
			unloadWorld(getServer().getWorld(s));
			if(s.equals(getRefreshWorld())){
				getServer().createWorld(new WorldCreator(s).environment(World.Environment.NORMAL).generator("MineralVein"));
			}else{
				getServer().createWorld(new WorldCreator(s).environment(World.Environment.NORMAL));
			}
			addBackPlayers(s);
			removed.clear();
			addPortal(s);
			getServer().broadcastMessage(ChatColor.GREEN + "Refresh complete!");
	}
	
	public void addPortal(final String s){
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		    public void run() {		
				World w = getServer().getWorld(s);
				Block b = w.getSpawnLocation().getBlock();
				Block b1 = b.getRelative(BlockFace.UP, 1);
				Block b2 = b1.getRelative(BlockFace.UP, 1);
				Block b3 = b2.getRelative(BlockFace.WEST, 1);
				Block b4 = b3.getRelative(BlockFace.DOWN, 1);
				b1.setTypeId(49);
				b2.setTypeId(49);
				b3.setTypeId(69);
				b4.setType(Material.WALL_SIGN);
				org.bukkit.block.Sign sign = (org.bukkit.block.Sign) b4.getState();
				byte by = (0x2);
				b3.setData(by);
				sign.setLine(0, "[GOTO]");
				sign.setLine(1, "[HOME]");
				refreshWorld(s);
		    }
		}, 600L);
	}
	
	private void addBackPlayers(String s) {
		if(s != null){
			for(String name : removed){
				Player p = getServer().getPlayer(name);
				if(p.isOnline()){
					p.teleport(getServer().getWorld(s).getSpawnLocation());
					p.sendMessage(ChatColor.BLUE + "Welcome back!");
				}
				if(removed.contains(s)){
					removed.remove(s);
				}
			}
		}
	}
	
	public void unloadPlayers(String s) {
		World w = getServer().getWorld(s);
		if(w!= null){
			for(Player p : w.getPlayers()){
				if(p != null){
					p.sendMessage(ChatColor.BLUE + "World unloading...time for you to leave!");
					p.teleport(getServer().getWorlds().get(0).getSpawnLocation());	
					removed.add(p.getName());
				}
			}
		}
	}
	
	private void removePlayers(String s) {
		World w = getServer().getWorld(s);
		for(Player p : w.getPlayers()){
			if(p != null){
				p.sendMessage(ChatColor.BLUE + "World refreshing...time for you to leave!");
				p.teleport(getServer().getWorlds().get(0).getSpawnLocation());	
				removed.add(p.getName());
			}
		}
	}

	public void unloadWorld(World w){
	       	getServer().unloadWorld(w, false);
			File dir = new File(w.getName());
			deleteWorld(dir);
	}
	
	   public boolean deleteWorld(File dir) {
	        if (dir.isDirectory()) {
	            String[] children = dir.list();
	            for (int i=0; i<children.length; i++) {
	                boolean success = deleteWorld(new File(dir, children[i]));
	                if (!success) {
	                    return false;
	                }
	            }
	        } 
	        return dir.delete();
	    }
}