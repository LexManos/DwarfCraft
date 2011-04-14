package com.smartaleq.bukkit.dwarfcraft;

import java.util.List;

import org.bukkit.Material;

public class Skill implements Cloneable {

	private final int id;
	private final String displayName;
	private int level;
	private final List<Effect> effects;
	private final Material TrainingItem1Mat;
	private final double TrainingItem1BaseCost;
	private final int TrainingItem1MaxAmount;
	private final Material TrainingItem2Mat;
	private final double TrainingItem2BaseCost;
	private final int TrainingItem2MaxAmount;
	private final Material TrainingItem3Mat;
	private final double TrainingItem3BaseCost;
	private final int TrainingItem3MaxAmount;
	private final Material trainerHeldMaterial;

	protected Skill(int id, String displayName, int level,
			List<Effect> effects, Material TrainingItem1Mat,
			double TrainingItem1BaseCost, int TrainingItem1MaxAmount,
			Material TrainingItem2Mat, double TrainingItem2BaseCost,
			int TrainingItem2MaxAmount, Material TrainingItem3Mat,
			double TrainingItem3BaseCost, int TrainingItem3MaxAmount,
			Material trainerHeldMaterial) {
		this.id = id;
		this.displayName = displayName;
		this.TrainingItem1Mat = TrainingItem1Mat;
		this.TrainingItem2Mat = TrainingItem2Mat;
		this.TrainingItem3Mat = TrainingItem3Mat;
		this.TrainingItem1BaseCost = TrainingItem1BaseCost;
		this.TrainingItem2BaseCost = TrainingItem2BaseCost;
		this.TrainingItem3BaseCost = TrainingItem3BaseCost;
		this.TrainingItem1MaxAmount = TrainingItem1MaxAmount;
		this.TrainingItem2MaxAmount = TrainingItem2MaxAmount;
		this.TrainingItem3MaxAmount = TrainingItem3MaxAmount;

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
				this.effects, this.TrainingItem1Mat,
				this.TrainingItem1BaseCost, this.TrainingItem1MaxAmount,
				this.TrainingItem2Mat, this.TrainingItem2BaseCost,
				this.TrainingItem2MaxAmount, this.TrainingItem3Mat,
				this.TrainingItem3BaseCost, this.TrainingItem3MaxAmount,
				this.trainerHeldMaterial);
		return newSkill;
	}

	protected String getDisplayName() {
		return displayName;
	}

	protected List<Effect> getEffects() {
		return effects;
	}

	protected int getId() {
		return id;
	}

	protected int getLevel() {
		return level;
	}

	protected Material getTrainerHeldMaterial() {
		return trainerHeldMaterial;
	}

	protected double getTrainingItem1BaseCost() {
		return TrainingItem1BaseCost;
	}

	protected Material getTrainingItem1Mat() {
		return TrainingItem1Mat;
	}

	protected final int getTrainingItem1MaxAmount() {
		return TrainingItem1MaxAmount;
	}

	protected double getTrainingItem2BaseCost() {
		return TrainingItem2BaseCost;
	}

	protected Material getTrainingItem2Mat() {
		return TrainingItem2Mat;
	}

	protected final int getTrainingItem2MaxAmount() {
		return TrainingItem2MaxAmount;
	}

	protected double getTrainingItem3BaseCost() {
		return TrainingItem3BaseCost;
	}

	protected Material getTrainingItem3Mat() {
		return TrainingItem3Mat;
	}

	protected final int getTrainingItem3MaxAmount() {
		return TrainingItem3MaxAmount;
	}

	protected void setLevel(int newLevel) {
		level = newLevel;
	}

	@Override
	public String toString() {
		return displayName.toUpperCase().replaceAll(" ", "_");
	}
}
