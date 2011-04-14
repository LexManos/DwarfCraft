package com.smartaleq.bukkit.dwarfcraft;

import org.bukkit.command.CommandSender;

class DCCommandException extends Throwable {

	public enum Type {
		TOOFEWARGS("You did not provide enough arguments for that command"), 
		TOOMANYARGS("You gave too many arguments for that command"), 
		PARSEDWARFFAIL("Could not locate the %p you named"), 
		PARSELEVELFAIL("Could not understand the skill level as a number"), 
		PARSESKILLFAIL("Could not find the skill name or ID you provided"), 
		PARSEEFFECTFAIL("Could not understand your effect input (Use an ID)"), 
		EMPTYPLAYER("Player argument was empty"), 
		COMMANDUNRECOGNIZED("Could not understand what command you were trying to use"),  
		LEVELOUTOFBOUNDS("Skill level must be between -1 and 30"), 
		PARSEINTFAIL("Could not understand some input as a number"), 
		PAGENUMBERNOTFOUND("Could not find the page number provided"),  
		CONSOLECANNOTUSE("Either the console cannot use this command, or a player must be provided as a target."), 
		NEEDPERMISSIONS("You must be an op to use this command."), 
		NOGREETERMESSAGE("Could not find that greeter message. Add it to greetermessages.config"), 
		NPCIDINUSE("You can't use this ID for a trainer, it is already used."), 
		PARSEPLAYERFAIL("Could not locate the player you named"),  
		NPCIDNOTFOUND("You must specifiy the exact ID for the trainer, the one provided was not found."), 
		PARSERACEFAIL("Could not understand the race name you used."),;
		
		String errorMsg;
		Type(String errorMsg){
			this.errorMsg = errorMsg;
		}
	}
	private Type type;
	private final DwarfCraft plugin;
	private static final long serialVersionUID = 7319961775971310701L;

	protected DCCommandException(final DwarfCraft plugin) {
		this.plugin = plugin;
	}

	protected DCCommandException(final DwarfCraft plugin, Type type) {
		this.plugin = plugin;
		this.type = type;
	}

	protected void describe(CommandSender sender) {
		plugin.getOut().sendMessage(sender,type.errorMsg);
	}

	protected Type getType() {
		return type;
	}

}
