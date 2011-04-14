package com.smartaleq.bukkit.dwarfcraft;

import org.bukkit.Material;

public class Effect {

	final private int id;

	// effect value descriptors
	final private double baseValue;
	final private double levelUpMultiplier;
	final private double noviceLevelUpMultiplier;
	final private double minValue;
	final private double maxValue;
	final private boolean hasException;
	final private double exceptionLow;
	final private double exceptionHigh;
	final private double exceptionValue;

	final private int elfEffectLevel;
	final private EffectType effectType;
	final private int initiatorId;
	final private int outputId;
	final private boolean toolRequired;
	final private int[] tools;

	public Effect(int id, double baseValue, double levelUpMultiplier,
			double noviceLevelUpMultiplier, double minValue, double maxValue,
			boolean hasException, double exceptionLow,
			double exceptionHigh, double exceptionValue, int elfEffectLevel,
			EffectType effectType, int initiatorId, int outputId,
			boolean toolRequired, int[] tools) {
		this.id = id;
		this.baseValue = baseValue;
		this.levelUpMultiplier = levelUpMultiplier;
		this.noviceLevelUpMultiplier = noviceLevelUpMultiplier;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.hasException = hasException;
		this.exceptionLow = exceptionLow;
		this.exceptionHigh = exceptionHigh;
		this.exceptionValue = exceptionValue;
		this.elfEffectLevel = elfEffectLevel;
		this.effectType = effectType;
		this.initiatorId = initiatorId;
		this.outputId = outputId;
		this.toolRequired = toolRequired;
		this.tools = tools;
	}

	/**
	 * General description of a benefit including minimum and maximum benefit
	 * 
	 * @return
	 */
	protected String describeGeneral() {
		String description;
		String initiator = Material.getMaterial(initiatorId).toString();
		if (initiator.equalsIgnoreCase("AIR"))
			initiator = "None";
		String output = Material.getMaterial(outputId).toString();
		if (output.equalsIgnoreCase("AIR"))
			output = "None";
		double effectAmountLow = getEffectAmount(0);
		double effectAmountHigh = getEffectAmount(30);
		double elfAmount = getEffectAmount(elfEffectLevel);
		String toolType = toolType();
		description = String.format("Effect Block Trigger: %s Block Output: %s . "
						+ "Effect value ranges from %.2f - %.2f for levels 0 to 30. "
						+ "Elves have the effect %.2f , as if they were level %d . "
						+ "Tools affected: %s. "
						+ (toolRequired ? "Tool needed." : "Tool not needed."),
						initiator, output, effectAmountLow, effectAmountHigh,
						elfAmount, elfEffectLevel, toolType);

		return description;
	}

	/**
	 * Description of a skills effect at a given level
	 * 
	 * @param dCPlayer
	 * @return
	 */
	protected String describeLevel(DCPlayer dCPlayer) {
		if (dCPlayer == null)
			return "Failed"; // TODO add failure code
		String description = "no skill description";
		// Variables used in skill descriptions
		String initiator = Material.getMaterial(initiatorId).toString();
		String output = Material.getMaterial(outputId).toString();
		double effectAmount = getEffectAmount(dCPlayer);
		double elfAmount = getEffectAmount(elfEffectLevel);
		boolean moreThanOne = (effectAmount > 1);
		String effectLevelColor = effectLevelColor(dCPlayer.getSkill(this)
				.getLevel());
		String toolType = toolType();
		switch(effectType){
		case BLOCKDROP:
			description = String.format(
					"&6Break a &2%s &6and %s%.2f &2%s&6 are created",
					initiator, effectLevelColor, effectAmount, output);
			break;
		case MOBDROP:
			if (id == 850 || id == 851) {
				description = String.format(
						"&6Zombies drop about %s%.2f &2%s",
						effectLevelColor, effectAmount, output);break;
			}
			description = String
			.format("&6Enemies that drop &2%s &6leave about %s%.2f&6",
					output, effectLevelColor, effectAmount, output);break;
		case SWORDDURABILITY: 
			description = String.format(
					"&6DISABLED*** Using &2%s &6removes about %s%.2f &6durability",
					toolType, effectLevelColor, effectAmount);break;
		case PVPDAMAGE: 
			description = String.format(
					"&6You do %s%d&6%% &6of normal &2%s &6damage when fighting players",
					effectLevelColor, (int) (effectAmount * 100),toolType);break;
		case PVEDAMAGE: 
			description = String.format(
					"&6You do %s%d&6%% &6of normal &2%s &6damage when fighting mobs",
					effectLevelColor, (int) (effectAmount * 100),toolType);break;
		case EXPLOSIONDAMAGE: 
			if (moreThanOne) {
				description = String.format(
						"&6You take %s%d%% more &6damage from explosions",
						effectLevelColor, (int) (effectAmount * 100 - 100));break;
			} else {
				description = String.format(
						"&6You take %s%d%% less &6damage from explosions",
						effectLevelColor, (int) (effectAmount * 100 - 100));break;
			}
		case FIREDAMAGE:
			if (moreThanOne) {
				description = String.format(
						"&6You take %s%d%% more &6damage from fire",
						effectLevelColor, (int) (effectAmount * 100 - 100));break;
			} else {
				description = String.format(
						"&6You take %s%d%% less &6damage from fire",
						effectLevelColor, (int) (effectAmount * 100 - 100));break;
			}
		case FALLDAMAGE: 
			if (moreThanOne) {
				description = String.format(
						"&6You take %s%d%% more &6damage from falling",
						effectLevelColor, (int) (effectAmount * 100 - 100));break;
			} else {
				description = String.format(
						"&6You take %s%d%% less &6damage from falling",
						effectLevelColor, (int) (effectAmount * 100 - 100));break;
			}
		case FALLTHRESHOLD: 
			description = String.format(
					"&6Fall damage less than %s%d &6does not affect you.",
					effectLevelColor,(int) effectAmount);break;
		case PLOWDURABILITY: 
			description = String.format(
					"&6Using &2%s &6removes about %s%.2f &6durability",
					toolType, effectLevelColor, effectAmount);break;
		case TOOLDURABILITY: 
			description = String.format(
					"&6Using &2%s &6removes about %s%.2f &6durability",
					toolType, effectLevelColor, effectAmount);break;
		case EAT: 
			description = String.format(
					"&6You gain %s%.2f hearts (not &e%.2f) when you eat &2%s",
					effectLevelColor, effectAmount, elfAmount, initiator);break;
		case CRAFT: 
			description = String.format(
					"&6You craft %s%.0f &2%s instead of &e%.0f",
					effectLevelColor, effectAmount, output, elfAmount);break;
		case PLOW: 
			description = String.format(
					"&6You gain %s%.2f &6seeds instead of &e%.2f &6when you plow grass",
					effectLevelColor, effectAmount, elfAmount);break;
		case DIGTIME: 
			description = String.format(
					"&a%.0f%%&6 of the time &2%s &6break &2%s &6instantly ",
					effectAmount * 100, toolType,
					Material.getMaterial(this.initiatorId).toString());break;
		case BOWATTACK: 
			description = String.format(
					"&6Your Arrows do %s%.0f &6hp damage (half hearts)",
					effectLevelColor, effectAmount);break;
		case VEHICLEDROP: 
			description = String.format(
					"&6DISABLED: When you break a boat &6approx. %s%.2f &2%s&6 are created",
							effectLevelColor, effectAmount, output);break;
		case VEHICLEMOVE: 
			description = String.format(
					"&6Your boat travels %s%d%% &6faster than normal",
					effectLevelColor, (int) (effectAmount * 100 - 100));break;
		case CITIZENBLOCKS: 
			description = String.format(
					"&6You contribute %s%.2f &6to max town size",
					effectLevelColor, effectAmount);break;
		case TOWNBLOCKS: 
			description = String.format(
					"&6As a town mayor your town can claim no more than %s%.2f &6blocks, " +
					"or the sum of your residents' citizen skills",
					effectLevelColor, effectAmount);break;
		case BOWDEFEND: 			
			description = String.format(
				"&6Each piece of &2Leather Armor&6 gives %s%.0f%% &6protection against projectiles",
				effectLevelColor,effectAmount * 25);break;
		case SUFFOCATEDEFEND:
			description = String.format(
					"&6Each piece of &2Iron Armor&6 gives %s%.0f%% &6protection against suffocation",
					effectLevelColor,effectAmount * 25);break;
		case LAVADEFEND: 
			description = String.format(
					"&6Each piece of &2Gold Armor&6 gives %s%.0f%% &6protection against lava",
					effectLevelColor,effectAmount * 25);break;
		case DROWNDEFEND:
			description = String.format(
					"&6Each piece of &2Diamond Armor&6 gives %s%.0f%% &6protection against drowning",
					effectLevelColor,effectAmount * 25);break;
		case SPECIAL: 
		default:
			description = "&6This Effect description is not yet implemented: "
					+ effectType.toString();
		}

		return description;
	}

	private String effectLevelColor(int skillLevel) {
		if (skillLevel > elfEffectLevel)
			return "&a";
		else if (skillLevel == elfEffectLevel)
			return "&e";
		else
			return "&c";
	}

	/**
	 * Returns an effect Amount for a particular Dwarf
	 * 
	 * @param dCPlayer
	 * @return
	 */
	public double getEffectAmount(DCPlayer dCPlayer) {
		double effectAmount = baseValue;
		int skillLevel;
		skillLevel = dCPlayer.skillLevel(this.id / 10);
		if (dCPlayer.isElf() || skillLevel == -1)
			skillLevel = this.elfEffectLevel;
		effectAmount += skillLevel * levelUpMultiplier;
		effectAmount += Math.min(skillLevel, 5) * noviceLevelUpMultiplier;
		effectAmount = Math.min(effectAmount, maxValue);
		effectAmount = Math.max(effectAmount, minValue);
		if (hasException && skillLevel <= exceptionHigh
				&& skillLevel >= exceptionLow)
			effectAmount = exceptionValue;
		if (DwarfCraft.debugMessagesThreshold < 1)
			System.out.println("DC1: GetEffectAmount Id: " + id
					+ " level checked: " + skillLevel + " base: " + baseValue
					+ " LevelUp multi:  " + levelUpMultiplier + " Novice:  "
					+ noviceLevelUpMultiplier + " Max:  " + maxValue + " Min: "
					+ minValue + " Exception: " + hasException + " ExcLow: "
					+ exceptionLow + " ExcHigh: " + exceptionHigh
					+ " Excvalue:  " + exceptionValue);
		return effectAmount;
	}

	/**
	 * Used for getting the effect amount at a particular skill level. Where
	 * possible use getEffectAmount(Dwarf), which checks for Dwarf vs. Elf.
	 */
	private double getEffectAmount(int skillLevel) {
		double effectAmount = baseValue;
		effectAmount += skillLevel * levelUpMultiplier;
		effectAmount += Math.min(skillLevel, 5) * noviceLevelUpMultiplier;
		effectAmount = Math.min(effectAmount, maxValue);
		effectAmount = Math.max(effectAmount, minValue);
		if (hasException && skillLevel <= exceptionHigh
				&& skillLevel >= exceptionLow)
			effectAmount = exceptionValue;
		if (DwarfCraft.debugMessagesThreshold < 1)
			System.out.println("DC1: GetEffectAmount Id: " + id + " base: "
					+ baseValue + " LevelUp multi:  " + levelUpMultiplier
					+ " Novice:  " + noviceLevelUpMultiplier + " Max:  "
					+ maxValue + " Min: " + minValue + " Exception: "
					+ hasException + " ExcLow: " + exceptionLow + " ExcHigh: "
					+ exceptionHigh + " Excvalue:  " + exceptionValue);
		return effectAmount;
	}

	public EffectType getEffectType() {
		return effectType;
	}

	protected int getElfEffectLevel() {
		return elfEffectLevel;
	}

	public int getId() {
		return id;
	}

	public int getInitiatorId() {
		return initiatorId;
	}

	public int getOutputId() {
		return outputId;
	}

	public boolean getToolRequired() {
		return toolRequired;
	}

	public int[] getTools() {
		return tools;
	}

	/**
	 * Tool to string parser for effect descriptions
	 * 
	 * @return
	 */
	private String toolType() {
		for (int toolId : tools) {
			if (toolId == 267)
				return "swords";
			if (toolId == 292)
				return "hoes";
			if (toolId == 258)
				return "axes";
			if (toolId == 270)
				return "pickaxes";
			if (toolId == 257)
				return "most picks";
			if (toolId == 278)
				return "high picks";
			if (toolId == 256)
				return "shovels";
		}
		return "any tool";
	}
	
	public boolean checkTool(int toolID){
		for (int id : tools)
			if (id == toolID)
				return true;
		return false;
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

}
