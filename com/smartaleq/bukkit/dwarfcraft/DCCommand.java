package com.smartaleq.bukkit.dwarfcraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.smartaleq.bukkit.dwarfcraft.DCCommandException.Type;

class DCCommand extends Command {
	private final DwarfCraft plugin;
	private CommandSender sender;

	protected DCCommand(final DwarfCraft plugin, String name) {
		super(name);
		this.plugin = plugin;
	}



	/**
	 * This command parses all inputs for commands and sends appropriate objects
	 * to the action or output methods.
	 */
	@Override
	public boolean execute(CommandSender sender, String commandLabel,
			String[] args) {
		if (DwarfCraft.debugMessagesThreshold < 1)
			System.out.println("DC1: started execute");
		String commandName = getName();
		this.sender = sender;

		CommandParser parser = new CommandParser(plugin, sender, args);
		List<Object> desiredArguments = new ArrayList<Object>();
		List<Object> outputList = null;
		try {
						/* first handle 0 arg commands */
			if (commandName.equalsIgnoreCase("dchelp")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'dchelp'");
				plugin.getOut().sendMessage(sender, Messages.GeneralInfo);
				return true;
			}
			if (commandName.equalsIgnoreCase("removenext")) {
				if(plugin.getDataManager().getTrainerRemove().contains((Player) sender)){
					return true;
				}
				else plugin.getDataManager().getTrainerRemove().add((Player) sender);
			}
			if (commandName.equalsIgnoreCase("skillsheet")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'skillsheet'");
				boolean printFull = false;
				if (args.length == 0 && sender instanceof Player) {
					plugin.getOut().printSkillSheet(
							plugin.getDataManager().find((Player) sender),
							sender, ((Player) sender).getName(), printFull);
					return true;
				} else if (args.length == 0)
					throw new DCCommandException(plugin, Type.CONSOLECANNOTUSE);
				if (args[0].equalsIgnoreCase("-f")
						|| args[0].equalsIgnoreCase("full")) {
					printFull = true;
					desiredArguments.add(args[0]);
				}
				DCPlayer dCPlayer = new DCPlayer(plugin, null);
				desiredArguments.add(dCPlayer);
				String displayName = null;
				try {
					outputList = parser.parse(desiredArguments, false);
					if (outputList.get(0) instanceof String)
						dCPlayer = (DCPlayer) outputList.get(1);
					else
						dCPlayer = (DCPlayer) outputList.get(0);
					displayName = dCPlayer.getPlayer().getDisplayName();
				} catch (DCCommandException dce) {
					if (dce.getType() == Type.PARSEDWARFFAIL) {
						if (sender instanceof Player)
							dCPlayer = plugin.getDataManager().find(
									(Player) sender);
						else
							throw new DCCommandException(plugin,
									Type.CONSOLECANNOTUSE);
					} else
						throw dce;
				} catch (NullPointerException e) {
					if (printFull)
						displayName = args[1];
					else
						displayName = args[0];
				}
				plugin.getOut().printSkillSheet(dCPlayer, sender, displayName,
						printFull);
				return true;
			}
			if (commandName.equalsIgnoreCase("tutorial")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'tutorial'");
				int page = 0;
				desiredArguments.add(page);
				try {
					outputList = parser.parse(desiredArguments, false);
					page = (Integer) outputList.get(0);
				} catch (DCCommandException e) {
					if (e.getType() == Type.TOOFEWARGS)
						page = 0;
					else
						throw e;
				}
				if (page < 0 || page > 6)
					throw new DCCommandException(plugin,
							Type.PAGENUMBERNOTFOUND);
				plugin.getOut().tutorial(sender, page);
				return true;
			}
			if (commandName.equalsIgnoreCase("info")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'info'");
				outputList = parser.parse(desiredArguments, false);
				plugin.getOut().info(sender);
				return true;
			} 
			if (commandName.equalsIgnoreCase("rules")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'rules'");
				outputList = parser.parse(desiredArguments, false);
				plugin.getOut().rules(sender);
				return true;
			} 
			/* then check for args */
			if (args.length==0) {
				plugin.getOut().sendMessage(sender, getDescription());
			}
			if (args[0].equalsIgnoreCase("?")){
				plugin.getOut().sendMessage(sender, getUsage());
			}
			/* Then handle commands with arguments */
			if (commandName.equalsIgnoreCase("debug")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'debug'");
				if (!sender.isOp())
					throw new DCCommandException(plugin, Type.NEEDPERMISSIONS);
				Integer i = 0;
				desiredArguments.add(i);
				outputList = parser.parse(desiredArguments, false);
				debug((Integer) outputList.get(0));
				return true;
			} 
			if (commandName.equalsIgnoreCase("dccommands")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'dccommands'");
				return true;
			} 
			if (commandName.equalsIgnoreCase("skillinfo")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'skillinfo'");
				DCPlayer dCPlayer = new DCPlayer(plugin, null);
				Skill skill = new Skill(0, null, 0, null, null, 0, 0, null, 0,
						0, null, 0, 0, null);
				desiredArguments.add(dCPlayer);
				desiredArguments.add(skill);
				try {
					outputList = parser.parse(desiredArguments, false);
					if (args.length > outputList.size())
						throw new DCCommandException(plugin, Type.TOOMANYARGS);
					skill = (Skill) outputList.get(1);
					dCPlayer = (DCPlayer) outputList.get(0);
				} catch (DCCommandException dce) {
					if (dce.getType() == Type.PARSEDWARFFAIL
							|| dce.getType() == Type.TOOFEWARGS) {
						desiredArguments.remove(0);
						outputList = parser.parse(desiredArguments, true);
						skill = (Skill) outputList.get(0);
						if (!(sender instanceof Player))
							throw new DCCommandException(plugin,
									Type.CONSOLECANNOTUSE);
						dCPlayer = plugin.getDataManager().find((Player) sender);
					} else
						throw dce;
				}
				plugin.getOut().printSkillInfo(sender, skill, dCPlayer, 30);
				return true;
			} 
			if (commandName.equalsIgnoreCase("effectinfo")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'effectinfo'");
				DCPlayer dCPlayer = new DCPlayer(plugin, null);
				Effect effect = new Effect(0, 0, 0, 0, 0, 0, false, 0,
						0, 0, 0, null, 0, 0, false, null);
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
						effect = (Effect) outputList.get(0);
						dCPlayer = (DCPlayer) outputList.get(1);
					} else
						throw dce;
				}
				plugin.getOut().effectInfo(sender, dCPlayer, effect);
				return true;
			} 
			if (commandName.equalsIgnoreCase("race")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1#: started command 'race'");
				if (args.length == 0 && sender instanceof Player)
					plugin.getOut().race(sender, (Player)sender);
				DCPlayer dCPlayer = new DCPlayer(plugin, null);
				Race newRace = null;
				Boolean confirmed = false;
				desiredArguments.add(dCPlayer);
				desiredArguments.add(newRace);
				desiredArguments.add(confirmed);
				try {
					outputList = parser.parse(desiredArguments, false);
					dCPlayer = (DCPlayer) outputList.get(0);
					newRace = (Race) outputList.get(1);
					confirmed = (Boolean) outputList.get(2);
					if (sender.isOp())
						race(newRace, confirmed, dCPlayer);
					
				} catch (DCCommandException e) {
					if (e.getType() == Type.TOOFEWARGS) {
						desiredArguments.remove(0);
						desiredArguments.add(dCPlayer);
						outputList = parser.parse(desiredArguments, true);
						dCPlayer = (DCPlayer) outputList.get(2);
						newRace = (Race) outputList.get(0);
						confirmed = (Boolean) outputList.get(1);
					}
					else throw e;
				} catch (IndexOutOfBoundsException f) {
					plugin.getOut().race(sender, (Player) sender);
					return true;
				}
				race(newRace, confirmed, dCPlayer);
			} 
			if (commandName.equalsIgnoreCase("setskill")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'setskill'");
				if (!(sender.isOp()))
					throw new DCCommandException(plugin, Type.NEEDPERMISSIONS);
				DCPlayer dCPlayer = new DCPlayer(plugin, null);
				Skill skill = new Skill(0, null, 0, null, null, 0, 0, null, 0,
						0, null, 0, 0, null);
				int level = 0;
				String name;
				desiredArguments.add(dCPlayer);
				desiredArguments.add(skill);
				desiredArguments.add(level);
				try {
					outputList = parser.parse(desiredArguments, false);
					dCPlayer = (DCPlayer) outputList.get(0);
					skill = (Skill) outputList.get(1);
					level = (Integer) outputList.get(2);
					name = dCPlayer.getPlayer().getName();
				} catch (DCCommandException e) {
					if (e.getType() == Type.TOOFEWARGS) {
						if (sender instanceof Player){
							desiredArguments.remove(0);
							desiredArguments.add(dCPlayer);
							outputList = parser.parse(desiredArguments, true);
							dCPlayer = (DCPlayer) outputList.get(2);
							skill = (Skill) outputList.get(0);
							level = (Integer) outputList.get(1);
							name = ((Player) sender).getName();
						}
						else throw new DCCommandException(plugin, Type.CONSOLECANNOTUSE);
					}
					else throw e;
				}
				if(skill == null){
					for(Skill s:dCPlayer.getSkills().values()){
						setSkill(dCPlayer, name, s, level);
					}
				}
				else setSkill(dCPlayer, name , skill, level);
				return true;
			} else if (commandName.equalsIgnoreCase("creategreeter")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'creategreeter'");

				if (!(sender.isOp()))
					throw new DCCommandException(plugin, Type.NEEDPERMISSIONS);
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
					dCPlayer = (DCPlayer) outputList.get(0);
					uniqueId = (String) outputList.get(1);
					name = (String) outputList.get(2);
					greeterMessage = (String) outputList.get(3);
				} catch (DCCommandException e) {
					if (e.getType() == Type.TOOFEWARGS) {
						if (!(sender instanceof Player))
							throw new DCCommandException(plugin,
									Type.CONSOLECANNOTUSE);
						desiredArguments.remove(0);
						desiredArguments.add(dCPlayer);
						outputList = parser.parse(desiredArguments, false);
						uniqueId = (String) outputList.get(0);
						name = (String) outputList.get(1);
						greeterMessage = (String) outputList.get(2);
						dCPlayer = (DCPlayer) outputList.get(3);;
					} else
						throw e;
				}
				DwarfTrainer d = new DwarfTrainer(plugin, dCPlayer.getPlayer().getLocation(),
						uniqueId, name, null, null, greeterMessage, true);
				plugin.getDataManager().insertTrainer(d);
				return true;
			} else if (commandName.equalsIgnoreCase("createtrainer")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'createtrainer'");
				if (!(sender.isOp()))
					throw new DCCommandException(plugin, Type.NEEDPERMISSIONS);
				String uniqueId = "UniqueIdAdd";
				String name = "Name";
				Skill skill = new Skill(0, null, 0, null, null, 0, 0, null, 0,
						0, null, 0, 0, null);
				Integer maxSkill = 1;
				desiredArguments.add(uniqueId);
				desiredArguments.add(name);
				desiredArguments.add(skill);
				desiredArguments.add(maxSkill);
				try {
					if (!(sender instanceof Player))
							throw new DCCommandException(plugin,
									Type.CONSOLECANNOTUSE);
					outputList = parser.parse(desiredArguments, false);
					uniqueId = (String) outputList.get(0);
					name = (String) outputList.get(1);
					skill = (Skill) outputList.get(2);
					maxSkill = (Integer) outputList.get(3);
				} catch (DCCommandException e) {
					if (e.getType() == Type.TOOFEWARGS) {
						outputList = parser.parse(desiredArguments, true);
						uniqueId = (String) outputList.get(0);
						name = (String) outputList.get(1);
						skill = (Skill) outputList.get(2);
						maxSkill = (Integer) outputList.get(3);
					} else
						throw e;
				}
				Location location = ((Player) sender).getLocation();
				DwarfTrainer d = new DwarfTrainer(plugin, location,
						uniqueId, name, skill.getId(), maxSkill, null, false);
				plugin.getDataManager().insertTrainer(d);
				return true;
			} else if (commandName.equalsIgnoreCase("removetrainer")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'removetrainer'");
				String trainerId = "UniqueIDRmv";
				desiredArguments.add(trainerId);
				outputList = parser.parse(desiredArguments, false);
				trainerId = (String) outputList.get(0);
				plugin.getDataManager().removeTrainer(trainerId);
				return true;
			} else if (commandName.equalsIgnoreCase("listtrainers")) {
				if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: started command 'listtrainers'");
				outputList = parser.parse(desiredArguments, false);
				plugin.getOut().printTrainerList(sender);
				return true;
			}
		} catch (DCCommandException e) {
			e.describe(sender);
			sender.sendMessage(this.usageMessage);
			return false;

		}
		return false;
	}
	
	private void race(Race newRace, boolean confirm, DCPlayer dCPlayer) {
		if (dCPlayer.getRace() == newRace) {
			if (confirm)
				plugin.getOut().resetRace(sender, dCPlayer, newRace);
			else
				plugin.getOut().alreadyRace(sender, dCPlayer, newRace);
		} else {
			if (confirm) {
				plugin.getOut().changedRace(sender, dCPlayer, newRace);
				dCPlayer.changeRace(newRace);
			} 
			else {
				plugin.getOut().confirmRace(sender, dCPlayer, newRace);
			}
		}
	}

	/**
	 * Admin Command to change a player's skill. Syntax: /dc setskill <player>
	 * <skill> <level> <player> is target, <skill> is skill ID or alpha <level>
	 * is desired level in range 0-30
	 */
	private void setSkill(DCPlayer dCPlayer, String name, Skill skill, int skillLevel) {
		skill.setLevel(skillLevel);
		plugin.getOut().sendMessage(
				sender,
				"&aAdmin: &eset skill &b" + skill.getDisplayName()
						+ "&e for player &9" + name + "&e to &3" + skillLevel);
		plugin.getDataManager().saveDwarfData(dCPlayer);
	}

	/**
	 * Changes the level of debug reporting in console
	 * 
	 * @param integer
	 */
	private void debug(Integer debugLevel) {
		DwarfCraft.debugMessagesThreshold = debugLevel;
		System.out.println("*** DC DEBUG LEVEL CHANGED TO " + debugLevel + " ***");
		plugin.getOut().sendBroadcast(
				sender.getServer(),
				"Debug messaging level set to "
						+ DwarfCraft.debugMessagesThreshold);
	}
}
