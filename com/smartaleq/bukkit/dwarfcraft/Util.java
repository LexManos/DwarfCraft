package com.smartaleq.bukkit.dwarfcraft;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Coal;
import org.bukkit.material.Crops;
import org.bukkit.material.Dye;
import org.bukkit.material.Step;
import org.bukkit.material.Tree;
import org.bukkit.material.Wool;

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
		else if ("abcdeghjmnopqrsuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ1234567890\\/#?$%-=_+&^".indexOf(x) != -1)
			return 6;
		else if ("@~".indexOf(x) != -1)
			return 7;
		else if (x == ' ')
			return 4;
		else
			return -1;
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

	public static int randomAmount(double input) {
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
	
	public static ItemStack parseItem(String info){
		String[] pts = info.split(":");
		int data = (pts.length == 1 ? -1 : Integer.parseInt(pts[1]));
		int item = -1;
		
		try{
			item = Integer.parseInt(pts[0]);
		}catch(NumberFormatException e){
			Material mat = Material.getMaterial(pts[0]);
			if (mat == null){
				System.out.println("DC ERROR: Could not parse material: " + info);
				return null;
			}
			item = mat.getId();
		}
		return new ItemStack(item, 0, (short)0, (data == -1 ? (byte)-1 : (byte)(data & 0xFF)));
	}
	
	public static String getCleanName(ItemStack item){
		if (item == null)
			return "NULL";
		if (item.getData() == null || item.getData().getData() == -1)
			return item.getType().toString();
		
		switch(item.getType()){
			case SAPLING:     return ((Tree)item.getData()).getSpecies()  + " Sapling";
			case LOG:         return ((Tree)item.getData()).getSpecies()  + " Log";
			case LEAVES:      return ((Tree)item.getData()).getSpecies()  + " Leaves";
			case WOOL:        return ((Wool)item.getData()).getColor()    + " Wool";
			case DOUBLE_STEP: return ((Step)item.getData()).getMaterial() + " Double Slab";
			case STEP:        return ((Step)item.getData()).getMaterial() + " Slab";
			case CROPS:       return ((Crops)item.getData()).getState()   + " Crops";
			case COAL:        return ((Coal)item.getData()).getType().toString();
			case INK_SACK:  
				switch(((Dye)item.getData()).getColor()){
					case WHITE:      return "Bone Meal";
					case ORANGE:     return "Orange Dye";
					case MAGENTA:    return "Magenta Dye";
					case LIGHT_BLUE: return "Light Blue Dye";
					case YELLOW:     return "Dandelion Yellow";
					case LIME:       return "Lime Dye";
					case PINK:       return "Pink Dye";
					case GRAY:       return "Gray Dye";
					case SILVER:     return "Light Gray Dye";
					case CYAN:       return "Cyan Dye";
					case PURPLE:     return "Purple Dye";
					case BLUE:       return "Lapis Lazuli";
					case BROWN:      return "Cocoa Beans";
					case GREEN:      return "Cactus Green";
					case RED:        return "Rose Red";
					case BLACK:      return "Ink Sac";
					default: return String.format("Unknown Dye(%d)", item.getData().getData());
				}
			default: return item.getType().toString();
		}
	}

}
