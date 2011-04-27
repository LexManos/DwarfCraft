package com.smartaleq.bukkit.dwarfcraft.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.smartaleq.bukkit.dwarfcraft.CommandParser;
import com.smartaleq.bukkit.dwarfcraft.DCCommandException;
import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;
import com.smartaleq.bukkit.dwarfcraft.DwarfTrainer;
import com.smartaleq.bukkit.dwarfcraft.Skill;
import com.smartaleq.bukkit.dwarfcraft.DCCommandException.Type;

public class CommandCreateTrainer extends Command {
	private final DwarfCraft plugin;

	public CommandCreateTrainer(final DwarfCraft plugin) {
		super("CreateTrainer");
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		if (DwarfCraft.debugMessagesThreshold < 1)
			System.out.println("DC1: started command 'createtrainer'");

		if (args.length==0) {
			plugin.getOut().sendMessage(sender, getDescription());
		} else if (args[0].equalsIgnoreCase("?")) {
			plugin.getOut().sendMessage(sender, getUsage());
		}else{
			try{				
				CommandParser parser = new CommandParser(plugin, sender, args);
				List<Object> desiredArguments = new ArrayList<Object>();
				List<Object> outputList = null;

				String uniqueId = "UniqueIdAdd";
				String name = "Name";
				Skill skill = new Skill(0, null, 0, null, null, null, null, null);
				Integer maxSkill = 1;
				desiredArguments.add(uniqueId);
				desiredArguments.add(name);
				desiredArguments.add(skill);
				desiredArguments.add(maxSkill);
				try {
					if (!(sender instanceof Player))
							throw new DCCommandException(plugin, Type.CONSOLECANNOTUSE);
					outputList = parser.parse(desiredArguments, false);
					uniqueId   = (String)outputList.get(0);
					name       = (String)outputList.get(1);
					skill      = (Skill)outputList.get(2);
					maxSkill   = (Integer)outputList.get(3);
				} catch (DCCommandException e) {
					if (e.getType() == Type.TOOFEWARGS) {
						outputList = parser.parse(desiredArguments, true);
						uniqueId   = (String)outputList.get(0);
						name       = (String)outputList.get(1);
						skill      = (Skill)outputList.get(2);
						maxSkill   = (Integer)outputList.get(3);
					} else
						throw e;
				}
				Location location = ((Player) sender).getLocation();
				DwarfTrainer d = new DwarfTrainer(plugin, location,
						uniqueId, name, skill.getId(), maxSkill, null, false);
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
