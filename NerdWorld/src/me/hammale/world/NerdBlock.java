package me.hammale.world;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Sign;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;

public class NerdBlock extends BlockListener {

	 public final world plugin;
	 
	 public NerdBlock(world plugin){
		 this.plugin = plugin;
	 }
	
//	 public void onSignChange (SignChangeEvent e) {
//		 Sign s1 = (Sign) e.getBlock().getState().getData();
//		 org.bukkit.block.Sign s = (org.bukkit.block.Sign) e.getBlock().getState();
//		 if(s.getLine(0).equalsIgnoreCase("[GOTO]") && s.getLine(1).equalsIgnoreCase("[" + plugin.getRefreshWorld() +"]")){
//			 Block initial = e.getBlock().getRelative(s1.getAttachedFace());
//			 if(initial.getTypeId() == 49){
//				 Block i1 = initial.getRelative(BlockFace.DOWN, 1);
//				 Block 
//				 Block c2 = initial.getRelative(BlockFace.UP, 1);
//			 }
//		 }
//		}
	 
}
