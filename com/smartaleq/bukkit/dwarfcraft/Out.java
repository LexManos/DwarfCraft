package com.smartaleq.bukkit.dwarfcraft;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Out {

	public enum Color {
		DC(FixedColor.Gold), 
		COMMAND(FixedColor.DarkRed), 
		HELP(FixedColor.Pink), 
		BADAMOUNT(FixedColor.Red), 
		GOODAMOUNT(FixedColor.Green), 
		NORMALAMOUNT(FixedColor.Yellow),
		ITEM(FixedColor.DarkGreen),
		SKILLID(FixedColor.DarkTeal), 
		SKILLLEVEL(FixedColor.Teal), 
		EFFECT(FixedColor.Purple),
		DWARF(FixedColor.Blue), 
		ELF(FixedColor.White),
		TRAINER(FixedColor.Gray);
		FixedColor fcolor;

		Color(FixedColor fixedcolor) {
			this.fcolor = fixedcolor;
		}

		public String toString() {
			return fcolor.toString();
		}
	}
	public enum FixedColor {
		DarkRed("§4"), Red("§c"), Yellow("§e"), Gold("§6"), DarkGreen("§2"), 
		Green("§a"), Teal("§b"), DarkTeal("§3"), Blue("§9"), Pink("§d"), 
		Purple("§5"), White("§f"), Gray("§7"), DarkGray("§8"), Black("§0");

		String color;

		FixedColor(String color) {
			this.color = color;
		}

		public String toString() {
			return color;
		}

	}
	/*
	 * Messaging Statics
	 */
	private final int lineLength = 320; // pixels

	private final int maxLines = 10;

	private final DwarfCraft plugin;

	protected Out(final DwarfCraft plugin) {
		this.plugin = plugin;
	}

	private String consoleLinePrinter(CommandSender sender, String line,
			String prefix) {
		System.out.print(prefix.concat(line));
		return null;
	}

	public boolean effectInfo(CommandSender sender, DCPlayer dCPlayer,
			Effect effect) {
		sendMessage(sender, effect.describeLevel(dCPlayer),
				"&6[&5" + effect.getId() + "&6] ");
		sendMessage(sender, effect.describeGeneral(), "&6[&5" + effect.getId()
				+ "&6] ");
		return true;
	}

	protected void generalInfo(CommandSender sender) {
		sendMessage(sender, "&d" + Messages.GeneralInfo, "&6[&d?&6] ");
	}

	public void info(CommandSender sender) {
		sendMessage(sender, Messages.GeneralInfo, "&6[&dInfo&6] ");
	}

	private String lastColor(String currentLine) {
		String lastColor = "";
		int lastIndex = currentLine.lastIndexOf("§");
		if (lastIndex == currentLine.length())
			return "§";
		if (lastIndex != -1) {
			lastColor = currentLine.substring(lastIndex, lastIndex + 2);
		}
		;
		return lastColor;
	}

	/**
	 * Removes carriage returns from strings and passes separate
	 * 
	 * @param player
	 * @param message
	 * @param prefix
	 */
	private void messagePrinter(CommandSender sender, String message,
			String prefix) {
		String[] lines = message.split("/n");
		String lastColor = "";
		for (String line : lines)
			lastColor = consoleLinePrinter(sender, lastColor.concat(line),
					prefix);
	}

	/**
	 * Removes carriage returns from strings and passes separate
	 * 
	 * @param player
	 * @param message
	 * @param prefix
	 */
	private void messagePrinter(Player player, String message, String prefix) {
		String[] lines = message.split("/n");
		String lastColor = "";
		for (String line : lines)
			lastColor = playerLinePrinter(player, lastColor.concat(line),
					prefix);
	}

	/**
	 * Finds &0-F in a string and replaces it with the color symbol
	 */
	String parseColors(String message) {
		if (message == null) {
			if (DwarfCraft.debugMessagesThreshold < 2)
				System.out.println("DC2: parsing null message!");
			return null;
		}
		if (DwarfCraft.debugMessagesThreshold < -1)
			System.out.println("DC-1: parsing colors for: " + message);
		for (int i = 0; i < message.length(); i++) {
			if (message.charAt(i) == '&') {
				if (message.charAt(i + 1) == '0')
					message = message.replace("&0", "§0");
				else if (message.charAt(i + 1) == '1')
					message = message.replace("&1", "§1");
				else if (message.charAt(i + 1) == '2')
					message = message.replace("&2", "§2");
				else if (message.charAt(i + 1) == '3')
					message = message.replace("&3", "§3");
				else if (message.charAt(i + 1) == '4')
					message = message.replace("&4", "§4");
				else if (message.charAt(i + 1) == '5')
					message = message.replace("&5", "§5");
				else if (message.charAt(i + 1) == '6')
					message = message.replace("&6", "§6");
				else if (message.charAt(i + 1) == '7')
					message = message.replace("&7", "§7");
				else if (message.charAt(i + 1) == '8')
					message = message.replace("&8", "§8");
				else if (message.charAt(i + 1) == '9')
					message = message.replace("&9", "§9");
				else if (message.charAt(i + 1) == 'a')
					message = message.replace("&a", "§a");
				else if (message.charAt(i + 1) == 'b')
					message = message.replace("&b", "§b");
				else if (message.charAt(i + 1) == 'c')
					message = message.replace("&c", "§c");
				else if (message.charAt(i + 1) == 'd')
					message = message.replace("&d", "§d");
				else if (message.charAt(i + 1) == 'e')
					message = message.replace("&e", "§e");
				else if (message.charAt(i + 1) == 'f')
					message = message.replace("&f", "§f");
				else
					message = message.replaceFirst("&", " AND ");
			}
		}
		message = message.replaceAll(" AND ", "&");
		return message;
	}

	/**
	 * Used to parse and send multiple line messages Sends actual output
	 * commands
	 */
	private String playerLinePrinter(Player player, String message,
			String prefix) {
		int messageSectionLength = lineLength - Util.msgLength(prefix);
		String currentLine = "";
		String words[] = message.split(" ");
		String lastColor = "";
		int lineTotal = 0;
		for (String word : words) {
			if (Util.msgLength(currentLine) + Util.msgLength(word) <= messageSectionLength) {
				currentLine = currentLine.concat(word + " ");
			} else {
				player.sendMessage(prefix.concat(lastColor + currentLine)
						.trim());
				lineTotal++;
				if (lineTotal >= maxLines)
					return lastColor;
				lastColor = lastColor(lastColor + currentLine);
				currentLine = word + " ";
			}
		}
		player.sendMessage(prefix.concat(lastColor + currentLine).trim());
		lastColor = lastColor(lastColor + currentLine);
		return lastColor;
	}

	public boolean printSkillInfo(CommandSender sender, Skill skill, DCPlayer dCPlayer, int maxTrainLevel) {
		// general line
		sendMessage(
				sender,
				"&6  Skillinfo for &b" + skill.getDisplayName() + "&6 [&b"
						+ skill.getId() + "&6] || Your level &3"
						+ skill.getLevel());
		// effects lines
		sendMessage(sender, "&6[&5EffectID&6]&f------&6[Effect]&f------");
		for (Effect effect : skill.getEffects()) {
			if (effect != null)
				sendMessage(sender, effect.describeLevel(dCPlayer), "&6[&5"
						+ effect.getId() + "&6] ");
		}
		// training lines
		if (skill.getLevel() == 30) {
			sendMessage(sender,
					"&6---This skill is maximum level, no training available---");
			return true;
		}
		if (skill.getLevel() > maxTrainLevel) {
			sendMessage(sender,
					"&6---You're as skilled as me, you need a more advanced trainer!--");
			return true;
		}
		sendMessage(sender, "&6---Train costs for level &3"
				+ (skill.getLevel() + 1));
		List<ItemStack> costs = dCPlayer.calculateTrainingCost(skill);
		for (ItemStack item : costs) {
			if (item != null)
				sendMessage(sender,
						" &2" + item.getAmount() + " " + item.getType()
								+ "&6  --", " &6-- ");
		}
		return true;
	}

	public void printSkillSheet(DCPlayer dCPlayer, CommandSender sender,
			String displayName, boolean printFull) {
		String message1;
		String message2 = "";
		String prefix1 = "&6[&dSS&6] ";

		String prefix2 = "&6[&dSS&6] ";
		message1 = ("&6Printing Skill Sheet for &9"
				+ (displayName == null ? dCPlayer.getPlayer().getName()
						: displayName) + " Dwarf &6Level is &3" + dCPlayer
				.getDwarfLevel());
		sendMessage(sender, message1, prefix1);

		if (dCPlayer.isElf()) {
			message2 = ("&fElves &6don't have skills, numbskull");
			sendMessage(sender, message2, prefix2);
			return;
		}
		boolean odd = true;
		String untrainedSkills = "&6Untrained Skills: ";
		for (Skill s : dCPlayer.getSkills().values()) {
			if (s.getLevel() == 0) {
				untrainedSkills = untrainedSkills.concat("|&7"
						+ s.getDisplayName() + "&6| ");
				continue;
			}
			odd = !odd;
			// the goal here is for every skill sheet line to be 60 characters
			// long.
			// each skill should take 30 characters - no more, no less
			String interim;
			if (s.getLevel() < 10)
				interim = String.format("&6[&30%d&6] &b%.18s", s.getLevel(),
						s.getDisplayName());
			else
				interim = String.format("&6[&3%d&6] &b%.18s", s.getLevel(),
						s.getDisplayName());

			if (!odd) {
				int interimLen = Util.msgLength(interim);
				int numSpaces = ((124 - interimLen) / 4) - 1;
				for (int i = 0; i < numSpaces; i++)
					interim = interim.concat(" ");
				interimLen = 124 - interimLen - numSpaces * 4;
				// 4 possible cases - need 4, 5, 6, or 7
				if (interimLen == 4)
					interim = interim.concat("&0.| &b");
				else if (interimLen == 5)
					interim = interim.concat("&0'| &b");
				else if (interimLen == 6)
					interim = interim.concat("&0 | &b");
				else if (interimLen == 7)
					interim = interim.concat("&0'.| &b");
			}

			message2 = message2.concat(interim);
			if (odd) {
				sendMessage(sender, message2, prefix2);
				message2 = "";
			}

		}
		if (!message2.equals(""))
			sendMessage(sender, message2, prefix2);
		if (printFull)
			sendMessage(sender, untrainedSkills, prefix2);
	}

	public void printTrainerList(CommandSender sender) {
		if (plugin.getDataManager().trainerList.isEmpty()) {
			sendMessage(sender, "There are currently no trainers.");
		} else {
			for (Iterator<Map.Entry<String, DwarfTrainer>> i = plugin
					.getDataManager().trainerList.entrySet().iterator(); i
					.hasNext();) {
				Map.Entry<String, DwarfTrainer> pairs = i.next();
				DwarfTrainer d = (DwarfTrainer) (pairs.getValue());
				if (d.isGreeter()) {
					sendMessage(sender, "Greeter ID: " + d.getUniqueId()
							+ " Name: " + d.getName());
				} else {
					String skillName = null;
					for (Skill s : plugin.getConfigManager().getAllSkills().values())
						if (s.getId() == d.getSkillTrained())
							skillName = s.getDisplayName();
					sendMessage(
							sender,
							"Trainer ID: " + d.getUniqueId() + " Name: "
									+ d.getName() + " Trains: ("
									+ d.getSkillTrained() + ") " + skillName);
				}
			}
		}
	}



	public void rules(CommandSender sender) {
		sendMessage(sender, Messages.ServerRules, "&6[&dRules&6] ");
	}

	/**
	 * Used to send messages to all players on a server
	 */
	public void sendBroadcast(Server server, String message) {
		sendBroadcast(server, message, "");
	}

	/**
	 * Used to send messages to all players on a server with a prefix
	 */
	protected void sendBroadcast(Server server, String message, String prefix) {
		Player[] playerArray = server.getOnlinePlayers();
		sendMessage(playerArray, message, prefix);
	}

	/**
	 * Used to send messages to one player or console
	 */
	public void sendMessage(CommandSender sender, String message) {
		sendMessage(sender, message, "");
	}

	/**
	 * Used to send messages to one player with a prefix
	 * 
	 * @return
	 */
	protected void sendMessage(CommandSender sender, String message,
			String prefix) {
		if (sender instanceof Player) {
			message = parseColors(message);
			prefix = parseColors(prefix);
			messagePrinter((Player) sender, message, prefix);
		} else {
			message = stripColors(message);
			prefix = stripColors(prefix);
			messagePrinter(sender, message, prefix);
		}
	}

	/**
	 * Dwarf version
	 */
	protected void sendMessage(DCPlayer dCPlayer, String message) {
		sendMessage(dCPlayer.getPlayer(), message);
	}

	/**
	 * Used to send messages to many players
	 */
	protected void sendMessage(Player[] playerArray, String message) {
		sendMessage(playerArray, message, "");
	}

	/**
	 * Used to send messages to many players with a prefix
	 */
	protected void sendMessage(Player[] playerArray, String message,
			String prefix) {
		for (Player p : playerArray)
			sendMessage(p, message, prefix);
	}

	private String stripColors(String message) {
		if (message == null) {
			if (DwarfCraft.debugMessagesThreshold < 2)
				System.out.println("DC2: stripping colors from null message!");
			return null;
		}
		if (DwarfCraft.debugMessagesThreshold < -1)
			System.out.println("DC-1: stripping colors from: " + message);
		for (int i = 0; i < message.length(); i++) {
			if (message.charAt(i) == '&') {
				if (message.charAt(i + 1) == '0')
					message = message.replace("&0", "§0");
				else if (message.charAt(i + 1) == '1')
					message = message.replace("&1", "");
				else if (message.charAt(i + 1) == '2')
					message = message.replace("&2", "");
				else if (message.charAt(i + 1) == '3')
					message = message.replace("&3", "");
				else if (message.charAt(i + 1) == '4')
					message = message.replace("&4", "");
				else if (message.charAt(i + 1) == '5')
					message = message.replace("&5", "");
				else if (message.charAt(i + 1) == '6')
					message = message.replace("&6", "");
				else if (message.charAt(i + 1) == '7')
					message = message.replace("&7", "");
				else if (message.charAt(i + 1) == '8')
					message = message.replace("&8", "");
				else if (message.charAt(i + 1) == '9')
					message = message.replace("&9", "");
				else if (message.charAt(i + 1) == 'a')
					message = message.replace("&a", "");
				else if (message.charAt(i + 1) == 'b')
					message = message.replace("&b", "");
				else if (message.charAt(i + 1) == 'c')
					message = message.replace("&c", "");
				else if (message.charAt(i + 1) == 'd')
					message = message.replace("&d", "");
				else if (message.charAt(i + 1) == 'e')
					message = message.replace("&e", "");
				else if (message.charAt(i + 1) == 'f')
					message = message.replace("&f", "");
				else
					message = message.replaceFirst("&", " AND ");
			}
		}
		message = message.replaceAll(" AND ", "&");
		return message;
	}

	public void tutorial(CommandSender sender, int i) {
		switch(i) {
			case 1: sendMessage(sender, Messages.Fixed.TUTORIAL1.getMessage(), "&6[&dDC&6] "); break;
			case 2: sendMessage(sender, Messages.Fixed.TUTORIAL2.getMessage(), "&6[&dDC&6] "); break;
			case 3: sendMessage(sender, Messages.Fixed.TUTORIAL3.getMessage(), "&6[&dDC&6] "); break;
			case 4: sendMessage(sender, Messages.Fixed.TUTORIAL4.getMessage(), "&6[&dDC&6] "); break;
			case 5: sendMessage(sender, Messages.Fixed.TUTORIAL5.getMessage(), "&6[&dDC&6] "); break;
			case 6: sendMessage(sender, Messages.Fixed.TUTORIAL6.getMessage(), "&6[&dDC&6] "); break;
		}
	}

	/**
	 * Sends a welcome message based on race of player joining. Broadcasts to
	 * the whole server
	 * 
	 * @param server
	 * @param dCPlayer
	 */
	public void welcome(Server server, DCPlayer dCPlayer) {
		try {
			sendBroadcast(server, "&fWelcome, &9" + dCPlayer.getRace().getName() + " &6" + dCPlayer.getPlayer().getName(), "&6[DC]         ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void race(CommandSender sender, Player player) {
		sendMessage(sender, "You are a "+plugin.getDataManager().find(player).getRace());
	}
	
	public void alreadyRace(CommandSender sender, DCPlayer dCPlayer, Race newRace) {
		sendMessage(sender, "You are already a "+newRace);		
	}

	public void resetRace(CommandSender sender, DCPlayer dCPlayer, Race newRace) {
		sendMessage(sender, "You are once again a fresh new "+newRace);
	}

	public void changedRace(CommandSender sender, DCPlayer dCPlayer, Race newRace) {
		sendMessage(sender, "You are now a "+newRace);
	}

	public void confirmRace(CommandSender sender, DCPlayer dCPlayer, Race newRace) {
		sendMessage(sender, "You need to confirm this command with confirm at the end");
	}

}
