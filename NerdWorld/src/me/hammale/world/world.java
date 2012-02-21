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
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
	}
	
	public void checkWorld(){
		if(getServer().getWorld(getRefreshWorld()) == null){
			getServer().createWorld(new WorldCreator(getRefreshWorld()).environment(World.Environment.NORMAL));
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
		    config.addDefault(path, 720);
		    config.addDefault(path1, "mobs");
		    config.addDefault(path2, 4320);
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
			getServer().broadcastMessage(ChatColor.RED + "Refreshing world...");
			removePlayers(s);
			unloadWorld(getServer().getWorld(s));
			getServer().createWorld(new WorldCreator(s).environment(World.Environment.NORMAL));
			addBackPlayers(s);
			removed.clear();
			addPortal(s);
			getServer().broadcastMessage(ChatColor.GREEN + "Refresh complete!");
	}
	
	public void addPortal(String s){
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
	}
	
	private void addBackPlayers(String s) {
		for(String name : removed){
			Player p = getServer().getPlayer(name);
			if(p.isOnline()){
				p.teleport(getServer().getWorld(s).getSpawnLocation());
				p.sendMessage(ChatColor.BLUE + "Welcome back!");
			}
			removed.remove(s);
		}
	}
	
	private void removePlayers(String s) {
		World w = getServer().getWorld(s);
		for(Player p : w.getPlayers()){
			p.sendMessage(ChatColor.BLUE + "World refreshing...time for you to leave!");
			p.teleport(getServer().getWorlds().get(0).getSpawnLocation());	
			removed.add(p.getName());
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