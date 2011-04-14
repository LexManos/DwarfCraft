package com.smartaleq.bukkit.dwarfcraft;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Util {

	// Stolen from nossr50
	private static int charLength(char x) {
		if ("i.:,;|!".indexOf(x) != -1)
			return 2;
		else if ("l'".indexOf(x) != -1)
			return 3;
		else if ("tI[]".indexOf(x) != -1)
			return 4;
		else if ("fk{}<>\"*()".indexOf(x) != -1)
			return 5;
		else if ("abcdeghjmnopqrsuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ1234567890\\/#?$%-=_+&^"
				.indexOf(x) != -1)
			return 6;
		else if ("@~".indexOf(x) != -1)
			return 7;
		else if (x == ' ')
			return 4;
		else
			return -1;
	}

	/**
	 * Drops blocks at a block based on a specific effect(and level)
	 * 
	 * @param e
	 *            Effect causing a block to drop
	 * @param effectAmount
	 *            Double number of blocks to drop
	 * @param dropNaturally
	 *            item naturally or not
	 * @param dmgValue
	 * @param loc
	 *            Location of item drop
	 */
	protected static void dropBlockEffect(Location loc, Effect e,
			double effectAmount, boolean dropNaturally, short dmgValue) {
		int outputId = e.getOutputId();
		ItemStack item;
		if (outputId == 35 || outputId == 44 || outputId == 17) 
			item = new ItemStack(e.getOutputId(),
				Util.randomAmount(effectAmount), dmgValue);
		else item = new ItemStack(e.getOutputId(),
				Util.randomAmount(effectAmount));
		if (item.getAmount() == 0) {
			if (DwarfCraft.debugMessagesThreshold < 6)
				System.out.println("Debug: dropped " + item.toString());
			return;
		}
		if(dropNaturally) loc.getWorld().dropItemNaturally(loc, item);
		else loc.getWorld().dropItem(loc, item);
		if (DwarfCraft.debugMessagesThreshold < 5)
			System.out.println("Debug: dropped " + item.toString());
	}

	protected static int msgLength(String str) {
		int len = 0;

		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '&') {
				i++;
				continue; // increment by 2 for colors, as in the case of "&3"
			}
			len += charLength(str.charAt(i));
		}
		return len;
	}

	protected static int randomAmount(double input) {
		double rand = Math.random();
		if (rand > input % 1)
			return (int) Math.floor(input);
		else
			return (int) Math.ceil(input);
	}

	protected static String sanitize(String str) {
		String retval = "";
		for (int i = 0; i < str.length(); i++) {
			if ("abcdefghijlmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_"
					.indexOf(str.charAt(i)) != -1)
				retval = retval + str.charAt(i);
		}
		return retval;
	}

	protected static boolean toolChecker(Player player) {
		Inventory inv = player.getInventory();
		ItemStack[] contents = inv.getContents();
		ItemStack[] newContents = contents.clone();
		boolean removedSomething = false;
		for (int i = 0; i < contents.length;i++) {
			if (contents[i] == null)
				continue;
			ItemStack item = contents[i];
			int damage = item.getDurability();
			int maxDamage = item.getType().getMaxDurability();
			if (damage >= maxDamage && damage > 17 ) {
				newContents[i] = null;
				removedSomething = true;
			}
		}
		inv.setContents(newContents);
		return removedSomething;
	}

}
