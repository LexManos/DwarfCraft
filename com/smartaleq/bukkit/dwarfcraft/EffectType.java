package com.smartaleq.bukkit.dwarfcraft;

public enum EffectType {
	// IMPLEMENTATION PRIORITY ORDER
	BLOCKDROP, 
	MOBDROP, 
	SWORDDURABILITY, 
	PVPDAMAGE, 
	PVEDAMAGE, 
	EXPLOSIONDAMAGE, 
	FIREDAMAGE, 
	FALLDAMAGE, 
	FALLTHRESHOLD, 
	PLOWDURABILITY, 
	TOOLDURABILITY, 
	EAT, 
	CRAFT, 
	PLOW, 
	DIGTIME, 
	BOWATTACK, 
	VEHICLEDROP, 
	VEHICLEMOVE, 
	CITIZENBLOCKS, 
	TOWNBLOCKS,
	SPECIAL, 
	BOWDEFEND, 
	SUFFOCATEDEFEND,
	LAVADEFEND, 
	DROWNDEFEND,
	;
	

	protected static EffectType getEffectType(String name) {
		for (EffectType effectType : EffectType.values()) {
			if (effectType.toString().equalsIgnoreCase(name))
				return effectType;
		}
		return null;
	}

}
