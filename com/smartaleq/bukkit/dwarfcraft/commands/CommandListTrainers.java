package com.smartaleq.bukkit.dwarfcraft.commands;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;
import com.smartaleq.bukkit.dwarfcraft.DwarfTrainer;
import com.smartaleq.bukkit.dwarfcraft.Skill;

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
		
		int page = 1;
		if (args.length > 0){
			try{
				Integer.parseInt(args[0]);
			}catch(NumberFormatException e){
				page = 1;
			}			
		}
		Collection<DwarfTrainer> col = plugin.getDataManager().trainerList.values();
		DwarfTrainer[] trainers = new DwarfTrainer[col.size()];
		col.toArray(trainers);
		
		if (trainers.length == 0){
			sender.sendMessage("There are currently no trainers.");
			return true;
		}
		int maxpage = (int)Math.ceil(trainers.length / 10.0);
		Collection<Skill> skills = plugin.getConfigManager().getAllSkills().values();
		
		page = Math.min(page, maxpage);
		page = Math.max(page, 1);
		
		int idx = (page - 1) * 10;
		sender.sendMessage(String.format("Trainers page %d/%d", page, maxpage));				
		
		for(int x = 0; x < 10; x++){
			if (idx + x >= trainers.length)
				return true;
			
			DwarfTrainer trainer = trainers[idx + x];
			Location loc = trainer.getLocation();
			
			if (trainer.isGreeter()){
				sender.sendMessage(String.format("Greeter ID: %s Name: %s (%d, %d, %d)",
						trainer.getUniqueId(), trainer.getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			} else {
				String skillName = "Unknown";
				for(Skill skill : skills){
					if (skill.getId() == trainer.getSkillTrained())
						skillName = skill.getDisplayName();
				}
				sender.sendMessage(String.format("Trainer ID: %s Name: %s Trains: %d %s (%d, %d, %d)",
						trainer.getUniqueId(), trainer.getName(), trainer.getMaxSkill(), skillName, 
						loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			}
		}
		return true;
	}
}
