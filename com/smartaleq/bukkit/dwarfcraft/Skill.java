package com.smartaleq.bukkit.dwarfcraft;

import java.util.List;

import org.bukkit.Material;

public class Skill implements Cloneable {

	private final int          mID;
	private final String       mName;
	private int                mLevel;
	private final List<Effect> mEffects;
	private final Material     mHeldItem;
	public final TrainingItem  Item1;
	public final TrainingItem  Item2;
	public final TrainingItem  Item3;

	public Skill(int id, String displayName, int level, List<Effect> effects, 
			TrainingItem item1, TrainingItem item2, TrainingItem item3,
			Material trainerHeldMaterial) {
		mID = id;
		mName = displayName;
		
		Item1 = item1;
		Item2 = item2;
		Item3 = item3;

		mLevel = level;
		mEffects = effects;
		mHeldItem = trainerHeldMaterial;
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
		Skill newSkill = new Skill(mID, mName, mLevel, mEffects, Item1, Item2, Item3, mHeldItem);
		return newSkill;
	}

	public String getDisplayName() {
		return mName;
	}

	public List<Effect> getEffects() {
		return mEffects;
	}

	public int getId() {
		return mID;
	}

	protected int getLevel() {
		return mLevel;
	}

	protected Material getTrainerHeldMaterial() {
		return mHeldItem;
	}

	public void setLevel(int newLevel) {
		mLevel = newLevel;
	}

	@Override
	public String toString() {
		return mName.toUpperCase().replaceAll(" ", "_");
	}
}
