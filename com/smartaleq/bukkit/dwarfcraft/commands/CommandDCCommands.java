package com.smartaleq.bukkit.dwarfcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;

public class CommandDCCommands extends Command {
	private final DwarfCraft plugin;

	public CommandDCCommands(final DwarfCraft plugin) {
		super("DCCommands");
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		if (DwarfCraft.debugMessagesThreshold < 1)
			System.out.println("DC1: started command 'dchelp'");
		plugin.getOut().sendMessage(sender, "DwarfCraft commands: debug, dchelp, info, rules, tutorial, " +
				"dccommands, skillsheet, skillinfo, effectinfo, " +
				"race, setskill, creategreeter, createtrainer, removetrainer, listtrainers, removenext");
		return true;
	}
}
