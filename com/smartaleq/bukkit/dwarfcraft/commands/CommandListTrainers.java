package com.smartaleq.bukkit.dwarfcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;

public class CommandListTrainers extends Command {
	private final DwarfCraft plugin;

	public CommandListTrainers(final DwarfCraft plugin) {
		super("ListTrainers");
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		if (DwarfCraft.debugMessagesThreshold < 1)
			System.out.println("DC1: started command 'listtrainers'");
		plugin.getOut().printTrainerList(sender);
		return true;
	}
}
