package com.smartaleq.bukkit.dwarfcraft;

final class Messages {

	// String messages fixed for DwarfCraft, and backup messages when loading
	// fails.
	protected enum Fixed {
		INFO(
				"This is a dummy General Help Message, please place a message in your messages.config"), SERVERRULESMESSAGE(
				"This is a dummy Server Rules Message, please place a message in your messages.config"), GENERALHELPMESSAGE(
				"&dWelcome to DwarfCraft. You are a &p with "
						+ "a set of skills that let you do minecraft tasks better."
						+ " When you first start, things may be more difficult"
						+ " than you are used to, but as you level your skills up you "
						+ "will be much more productive than normal players. Each of "
						+ "the skills listed in your skillsheet(&4/skillsheet full&d) "
						+ "has multiple effects. You can find out more about "
						+ "training a skill and its effects with &4/skill "
						+ "<skillname or id>&d."), COMMANDLIST1(""), COMMANDLIST2(
				""),

		TUTORIAL1(
				"&fWelcome to the dwarfcraft tutorial. To get started, type &4/skillsheet full&f."
						+ " Afterwards, type &4/tutorial 2&f to continue."), TUTORIAL2(
				"&fYour Skillshset lists all skills that are affecting you and their level. Lets"
						+ " find out more about the Demolitionist skill. Type &4/skillinfo Demolit&f or"
						+ " &4/skillinfo 63&f. Continue with &4/tutorial 3&f"), TUTORIAL3(
				"&fThe skillinfo shows that your low level demotionist skill allows you to craft"
						+ " a normal amount of TNT. If you increase this skill enough, you'll get 2 or "
						+ "even more TNT per craft. The skill also affects damage you take from explosions. "
						+ "Below that, it shows how to train the skill. Find a nearby trainer and left click "
						+ "them to get more information about the skill. Right click to attempt training"
						+ ", then continue with &4/tutorial 4&f"), TUTORIAL4(
				"&fWhen you tried to train the skill, it showed what training cost was missing. All "
						+ "skills train for a cost in relevant materials. The first few levels cost little,"
						+ "but becoming a master is very challenging. Continue with &4/tutorial 5&f"), TUTORIAL5(
				"&fMost trainers can only take you to a limited level, you'll need to seek out the "
						+ "best trainers in the world to eventually reach level 30 in a skill. Go gather"
						+ "some dirt, stone, or logs and try to train up a relevant skill, using what"
						+ "you have learned, then continue with &4/tutorial 6&f"), TUTORIAL6(
				"&fYou now know the basic commands you need to succeed and develop. "
						+ "To find out more, use &4/dchelp&f and &4/dchelp <command>&f."),

		WELCOME("Welcome to a DwarfCraft world, first time player!")
		;

		private String message;

		private Fixed(String message) {
			this.message = message;
		}

		protected String getMessage() {
			return message;
		}
	}
	// String messages loaded from messages.config
	protected static String GeneralInfo = null;
	protected static String ServerRules = null;
}
