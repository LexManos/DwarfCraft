package com.smartaleq.bukkit.dwarfcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;
import com.smartaleq.bukkit.dwarfcraft.Messages;

public class CommandHelp extends Command {
	private final DwarfCraft plugin;

	public CommandHelp(final DwarfCraft plugin) {
		super("DCHelp");
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		if (DwarfCraft.debugMessagesThreshold < 1)
			System.out.println("DC1: started command 'dchelp'");
		plugin.getOut().sendMessage(sender, Messages.GeneralInfo);
		return true;
	}
}
