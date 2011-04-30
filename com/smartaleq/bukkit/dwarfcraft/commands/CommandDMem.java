package com.smartaleq.bukkit.dwarfcraft.commands;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;
import org.bukkit.*;
import org.bukkit.entity.*;

public class CommandDMem extends Command {
	public CommandDMem(final DwarfCraft plugin) {
		super("DMem");
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
	    sender.sendMessage("Maximum memory: " + Runtime.getRuntime().maxMemory() / 1024L / 1024L + " MB");
	    sender.sendMessage("Free memory: " + Runtime.getRuntime().freeMemory() / 1024L / 1024L + " MB");

	    for (World w : sender.getServer().getWorlds())
	    {
	      HashMap<String, Integer> map = new HashMap<String, Integer>();
	      for(Entity e : w.getEntities()){
	    	  
	    	  String name = e.getClass().getName();
	    	  if (name.contains("."))
	    		  name = name.substring(name.lastIndexOf(".") + 1);
	    	  
	    	  if (!map.containsKey(name))
	    		  map.put(name, 1);
	    	  else{
	    		  Integer cnt = map.get(name);
	    		  cnt++;
	    		  map.put(name, cnt);
	    	  }
	    		  
	      }
	      for(Entry<String, Integer> i : map.entrySet())
	    	  sender.sendMessage(String.format("%s:\t\t\t%3d", i.getKey(), i.getValue()));   
	    }
		return true;
	}
}