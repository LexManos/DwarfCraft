package com.smartaleq.bukkit.dwarfcraft;

import java.util.List;

import org.bukkit.Material;

public class Skill implements Cloneable {

	private final int id;
	private final String displayName;
	private int level;
	private final List<Effect> effects;
	private final Material trainerHeldMaterial;
	public final TrainingItem Item1;
	public final TrainingItem Item2;
	public final TrainingItem Item3;

	public Skill(int id, String displayName, int level, List<Effect> effects, 
			TrainingItem item1, TrainingItem item2, TrainingItem item3,
			Material trainerHeldMaterial) {
		this.id = id;
		this.displayName = displayName;
		
		Item1 = item1;
		Item2 = item2;
		Item3 = item3;

		this.level = level;
		this.effects = effects;
		this.trainerHeldMaterial = trainerHeldMaterial;
	}

	/**
	 * My attempt at making a cloneable class.
	 * 
	 * Known issue: it does not clone the effects table or itemStack table. This
	 * is not a problem because effects are 100% final, and ItemStack is never
	 * modified.
	 */
	@Override
	public Skill clone() {
		Skill newSkill = new Skill(this.id, this.displayName, this.level,
				this.effects, Item1, Item2, Item3, this.trainerHeldMaterial);
		return newSkill;
	}

	public String getDisplayName() {
		return displayName;
	}

	public List<Effect> getEffects() {
		return effects;
	}

	public int getId() {
		return id;
	}

	protected int getLevel() {
		return level;
	}

	protected Material getTrainerHeldMaterial() {
		return trainerHeldMaterial;
	}

	public void setLevel(int newLevel) {
		level = newLevel;
	}

	@Override
	public String toString() {
		return displayName.toUpperCase().replaceAll(" ", "_");
	}
}
