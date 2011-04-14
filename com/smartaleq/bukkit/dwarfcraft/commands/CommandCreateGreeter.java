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
import com.smartaleq.bukkit.dwarfcraft.DwarfTrainer;
import com.smartaleq.bukkit.dwarfcraft.DCCommandException.Type;

public class CommandCreateGreeter extends Command {
	private final DwarfCraft plugin;

	public CommandCreateGreeter(final DwarfCraft plugin) {
		super("CreateGreeter");
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		if (DwarfCraft.debugMessagesThreshold < 1)
			System.out.println("DC1: started command 'creategreeter'");

		if (args.length==0) {
			plugin.getOut().sendMessage(sender, getDescription());
		} else if (args[0].equalsIgnoreCase("?")) {
			plugin.getOut().sendMessage(sender, getUsage());
		}else{
			try{
				if (!(sender.isOp()))
					throw new DCCommandException(plugin, Type.NEEDPERMISSIONS);
				
				CommandParser parser = new CommandParser(plugin, sender, args);
				List<Object> desiredArguments = new ArrayList<Object>();
				List<Object> outputList = null;

				String uniqueId = "UniqueIdAdd";
				String name = "Name";
				String greeterMessage = "GreeterMessage";
				DCPlayer dCPlayer = new DCPlayer(plugin, null);
				desiredArguments.add(dCPlayer);
				desiredArguments.add(uniqueId);
				desiredArguments.add(name);
				desiredArguments.add(greeterMessage);
				try {
					outputList = parser.parse(desiredArguments, false);
					dCPlayer       = (DCPlayer) outputList.get(0);
					uniqueId       = (String) outputList.get(1);
					name           = (String) outputList.get(2);
					greeterMessage = (String) outputList.get(3);
				} catch (DCCommandException e) {
					if (e.getType() == Type.TOOFEWARGS) {
						if (!(sender instanceof Player))
							throw new DCCommandException(plugin, Type.CONSOLECANNOTUSE);
						desiredArguments.remove(0);
						desiredArguments.add(dCPlayer);
						outputList     = parser.parse(desiredArguments, false);
						uniqueId       = (String) outputList.get(0);
						name           = (String) outputList.get(1);
						greeterMessage = (String) outputList.get(2);
						dCPlayer       = (DCPlayer) outputList.get(3);;
					} else
						throw e;
				}
				DwarfTrainer d = new DwarfTrainer(plugin, dCPlayer.getPlayer().getLocation(),
						uniqueId, name, null, null, greeterMessage, true);
				plugin.getDataManager().insertTrainer(d);
			} catch (DCCommandException e) {
				e.describe(sender);
				sender.sendMessage(this.usageMessage);
				return false;		
			}
		}
		return true;		
	}
}
