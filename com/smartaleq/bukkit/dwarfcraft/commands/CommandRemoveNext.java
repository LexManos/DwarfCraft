package com.smartaleq.bukkit.dwarfcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;

public class CommandRemoveNext extends Command {
	private final DwarfCraft plugin;

	public CommandRemoveNext(final DwarfCraft plugin) {
		super("RemoveNext");
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		if(!plugin.getDataManager().getTrainerRemove().contains((Player) sender))
			plugin.getDataManager().getTrainerRemove().add((Player) sender);
		return true;
	}
}
