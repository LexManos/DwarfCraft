package com.smartaleq.bukkit.dwarfcraft.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.smartaleq.bukkit.dwarfcraft.CommandParser;
import com.smartaleq.bukkit.dwarfcraft.DCCommandException;
import com.smartaleq.bukkit.dwarfcraft.DCPlayer;
import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;
import com.smartaleq.bukkit.dwarfcraft.Effect;
import com.smartaleq.bukkit.dwarfcraft.DCCommandException.Type;

public class CommandEffectInfo extends Command {
	private final DwarfCraft plugin;

	public CommandEffectInfo(final DwarfCraft plugin) {
		super("EffectInfo");
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		if (DwarfCraft.debugMessagesThreshold < 1)
			System.out.println("DC1: started command 'effectinfo'");

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

				DCPlayer dCPlayer = new DCPlayer(plugin, null);
				Effect effect = new Effect(null);
				desiredArguments.add(dCPlayer);
				desiredArguments.add(effect);
				try {
					outputList = parser.parse(desiredArguments, false);
					effect = (Effect) outputList.get(1);
					dCPlayer = (DCPlayer) outputList.get(0);
				} catch (DCCommandException dce) {
					if (dce.getType() == Type.PARSEDWARFFAIL
							|| dce.getType() == Type.TOOFEWARGS) {
						desiredArguments.remove(0);
						desiredArguments.add(dCPlayer);
						outputList = parser.parse(desiredArguments, true);
						effect     = (Effect)outputList.get(0);
						dCPlayer   = (DCPlayer)outputList.get(1);
					} else
						throw dce;
				}
				plugin.getOut().effectInfo(sender, dCPlayer, effect);
			} catch (DCCommandException e) {
				e.describe(sender);
				sender.sendMessage(this.usageMessage);
				return false;		
			}
		}
		return true;		
	}
}
