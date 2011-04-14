package com.smartaleq.bukkit.dwarfcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;

public class CommandInfo extends Command {
	private final DwarfCraft plugin;

	public CommandInfo(final DwarfCraft plugin) {
		super("Info");
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		if (DwarfCraft.debugMessagesThreshold < 1)
			System.out.println("DC1: started command 'info'");
		plugin.getOut().info(sender);
		return true;			
	}
}
