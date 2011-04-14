package com.smartaleq.bukkit.dwarfcraft.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.smartaleq.bukkit.dwarfcraft.CommandParser;
import com.smartaleq.bukkit.dwarfcraft.DCCommandException;
import com.smartaleq.bukkit.dwarfcraft.DCPlayer;
import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;
import com.smartaleq.bukkit.dwarfcraft.DCCommandException.Type;

public class CommandSkillSheet extends Command {
	private final DwarfCraft plugin;

	public CommandSkillSheet(final DwarfCraft plugin) {
		super("SkillSheet");
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		try{
			if (DwarfCraft.debugMessagesThreshold < 1)
				System.out.println("DC1: started command 'skillsheet'");
			
			boolean printFull = false;
			
			if (args.length == 0 && sender instanceof Player) {
				plugin.getOut().printSkillSheet(plugin.getDataManager().find((Player) sender), sender, ((Player) sender).getName(), printFull);
				return true;
			} else {
				if (args.length == 0)
					throw new DCCommandException(plugin, Type.CONSOLECANNOTUSE);
			}
	
			CommandParser parser = new CommandParser(plugin, sender, args);
			List<Object> desiredArguments = new ArrayList<Object>();
			List<Object> outputList = null;
			
			if (args[0].equalsIgnoreCase("-f") || args[0].equalsIgnoreCase("full")) {
				printFull = true;
				desiredArguments.add(args[0]);
			}
			
			DCPlayer dCPlayer = new DCPlayer(plugin, null);
			desiredArguments.add(dCPlayer);
			String displayName = null;
		
			try {
				outputList = parser.parse(desiredArguments, false);
				if (outputList.get(0) instanceof String)
					dCPlayer = (DCPlayer) outputList.get(1);
				else
					dCPlayer = (DCPlayer) outputList.get(0);
				if(dCPlayer.getPlayer() == null)
					displayName = (printFull ? args[1] : args[0]);
				else
					displayName = dCPlayer.getPlayer().getDisplayName();
			} catch (DCCommandException dce) {
				if (dce.getType() == Type.PARSEDWARFFAIL) {
					if (sender instanceof Player)
						dCPlayer = plugin.getDataManager().find((Player) sender);
					else
						throw new DCCommandException(plugin, Type.CONSOLECANNOTUSE);
				} else
					throw dce;
			}
			plugin.getOut().printSkillSheet(dCPlayer, sender, displayName, printFull);
			return true;
		} catch (DCCommandException e) {
			e.describe(sender);
			sender.sendMessage(this.usageMessage);
			return false;		
		}
	}
}
