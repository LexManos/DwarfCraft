package com.smartaleq.bukkit.dwarfcraft.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.smartaleq.bukkit.dwarfcraft.CommandParser;
import com.smartaleq.bukkit.dwarfcraft.DCCommandException;
import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;

public class CommandRemoveTrainer extends Command {
	private final DwarfCraft plugin;

	public CommandRemoveTrainer(final DwarfCraft plugin) {
		super("RemoveTrainer");
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		if (DwarfCraft.debugMessagesThreshold < 1)
			System.out.println("DC1: started command 'removetrainer'");

		if (args.length==0) {
			plugin.getOut().sendMessage(sender, getDescription());
		} else if (args[0].equalsIgnoreCase("?")) {
			plugin.getOut().sendMessage(sender, getUsage());
		}else{
			try{
				CommandParser parser = new CommandParser(plugin, sender, args);
				List<Object> desiredArguments = new ArrayList<Object>();
				List<Object> outputList = null;
				
				desiredArguments.add("UniqueIDRmv");
				outputList = parser.parse(desiredArguments, false);
				plugin.getDataManager().removeTrainer((String)outputList.get(0));
				
			} catch (DCCommandException e) {
				e.describe(sender);
				sender.sendMessage(this.usageMessage);
				return false;		
			}
		}
		return true;
		
	}
}
