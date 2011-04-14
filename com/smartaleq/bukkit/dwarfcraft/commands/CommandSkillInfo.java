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
import com.smartaleq.bukkit.dwarfcraft.Skill;
import com.smartaleq.bukkit.dwarfcraft.DCCommandException.Type;

public class CommandSkillInfo extends Command {
	private final DwarfCraft plugin;

	public CommandSkillInfo(final DwarfCraft plugin) {
		super("SkillInfo");
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		if (DwarfCraft.debugMessagesThreshold < 1)
			System.out.println("DC1: started command 'skillinfo'");

		if (args.length==0) {
			plugin.getOut().sendMessage(sender, getDescription());
		} else if (args[0].equalsIgnoreCase("?")) {
			plugin.getOut().sendMessage(sender, getUsage());
		}else{
			try{
				CommandParser parser = new CommandParser(plugin, sender, args);
				List<Object> desiredArguments = new ArrayList<Object>();
				List<Object> outputList = null;
				
				DCPlayer dCPlayer = new DCPlayer(plugin, null);
				Skill skill = new Skill(0, null, 0, null, null, null, null, null);
				desiredArguments.add(dCPlayer);
				desiredArguments.add(skill);
				
				try {
					outputList = parser.parse(desiredArguments, false);
					if (args.length > outputList.size())
						throw new DCCommandException(plugin, Type.TOOMANYARGS);
					
					skill = (Skill) outputList.get(1);
					dCPlayer = (DCPlayer) outputList.get(0);
				} catch (DCCommandException dce) {
					if (dce.getType() == Type.PARSEDWARFFAIL || dce.getType() == Type.TOOFEWARGS) {
						desiredArguments.remove(0);
						outputList = parser.parse(desiredArguments, true);
						skill = (Skill) outputList.get(0);
						if (!(sender instanceof Player))
							throw new DCCommandException(plugin, Type.CONSOLECANNOTUSE);
						dCPlayer = plugin.getDataManager().find((Player) sender);
					} else
						throw dce;
				}
				plugin.getOut().printSkillInfo(sender, skill, dCPlayer, 30);
				return true;
				
			} catch (DCCommandException e) {
				e.describe(sender);
				sender.sendMessage(this.usageMessage);
				return false;		
			}
		}
		return true;
		
	}
}
