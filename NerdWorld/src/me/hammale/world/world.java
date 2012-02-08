package me.hammale.world;

import java.io.File;
import java.util.HashSet;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class world extends JavaPlugin {
	
	public FileConfiguration config;
	
	public HashSet<String> removed = new HashSet<String>();
	
//	private final EpicPlayerListener playerListener = new EpicPlayerListener(this);
//	private final EpicEntityListener entityListener = new EpicEntityListener(this);
//	private final EpicBlockListener blockListener = new EpicBlockListener(this);
	
	Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		loadConfiguration();
		log.info("[NerdWorld] Version: " + pdfFile.getVersion() + " Enabled!");
		checkWorld();
		moniterRefresh();
		//PluginManager pm = getServer().getPluginManager();
		//pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		//pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);

	}
	
	public void checkWorld(){
		if(getServer().getWorld(getRefreshWorld()) == null){
			getServer().createWorld(new WorldCreator(getRefreshWorld()).environment(World.Environment.NORMAL));
		}
	}
	
	public void loadConfiguration(){
	    if(exists() == false){
		    config = getConfig();
		    config.options().copyDefaults(false);
		    String path = "RefreshRate";
		    String path1 = "RefreshWorld";
		    config.addDefault(path, 180);
		    config.addDefault(path1, "refreshworld");
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
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		getServer().getScheduler().cancelTasks(this);
		log.info("[NerdWorld] Version: " + pdfFile.getVersion() + " Disabled!");	
	}
	
	public void moniterRefresh(){
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    public void run() {
		        refreshWorld();
		    }
		}, getRate(), getRate());
	}
	
	public void refreshWorld(){
			String s = getRefreshWorld();
			getServer().broadcastMessage(ChatColor.RED + "Refreshing resource world...");
			removePlayers(s);
			unloadWorld(getServer().getWorld(s));
			getServer().createWorld(new WorldCreator(s).environment(World.Environment.NORMAL));
			addBackPlayers(s);
			removed.clear();
			getServer().broadcastMessage(ChatColor.GREEN + "Refresh complete!");
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