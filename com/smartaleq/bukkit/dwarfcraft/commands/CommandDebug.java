package com.smartaleq.bukkit.dwarfcraft.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.smartaleq.bukkit.dwarfcraft.CommandParser;
import com.smartaleq.bukkit.dwarfcraft.DCCommandException;
import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;

public class CommandDebug extends Command {
	private final DwarfCraft plugin;

	public CommandDebug(final DwarfCraft plugin) {
		super("Debug");
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		if (args.length==0) {
			plugin.getOut().sendMessage(sender, getDescription());
		}else if (args[0].equalsIgnoreCase("?")){
			plugin.getOut().sendMessage(sender, getUsage());
		}else{
			try{
				CommandParser parser = new CommandParser(plugin, sender, args);
				List<Object> desiredArguments = new ArrayList<Object>();
				List<Object> outputList = null;				
	
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'debug'");
								
				Integer i = 0;
				desiredArguments.add(i);
				outputList = parser.parse(desiredArguments, false);

				DwarfCraft.debugMessagesThreshold = (Integer)outputList.get(0);
				System.out.println("*** DC DEBUG LEVEL CHANGED TO " + DwarfCraft.debugMessagesThreshold + " ***");
				plugin.getOut().sendBroadcast(sender.getServer(), "Debug messaging level set to " + DwarfCraft.debugMessagesThreshold);
			} catch (DCCommandException e) {
				e.describe(sender);
				sender.sendMessage(this.usageMessage);
				return false;		
			}
		}
		return true;
	}
}