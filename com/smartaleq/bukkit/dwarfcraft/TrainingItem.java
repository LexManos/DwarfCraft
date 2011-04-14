package com.smartaleq.bukkit.dwarfcraft;

import org.bukkit.Material;

public class TrainingItem {

	public final Material Item;
	public final double Base;
	public final int Max;
	
	public TrainingItem(Material item, double base, int max){
		Item = item;
		Base = base;
		Max = max;
	}
}
