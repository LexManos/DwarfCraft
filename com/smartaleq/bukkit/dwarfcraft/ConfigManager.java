package com.smartaleq.bukkit.dwarfcraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;

final class ConfigManager {

	private final DwarfCraft plugin;

	private final String configDirectory;

	private String configMainFileName;
	private String configSkillsFileName;
	private int configSkillsVersion;
	private String configEffectsFileName;
	private int configEffectsVersion;
	private String configMessagesFileName;
	private String configGreeterMessagesFileName;
	private String dbpath;
	
	private HashMap<Race, HashMap<Integer, Skill>> skillsArray = new HashMap<Race,HashMap<Integer, Skill>>();
	private Race defaultRace;
	private Race optOutRace;
	private HashMap<String, Race> raceMap = new HashMap<String, Race>();
	
	protected ConfigManager(DwarfCraft plugin, String directory,
			String paramsFileName) {
		this.plugin = plugin;
		if (!directory.endsWith(File.separator))
			directory += File.separator;
		configDirectory = directory;
		configMainFileName = paramsFileName;

		try{
		if (!readConfigFile() || !readSkillsFile() || !readEffectsFile() || !readMessagesFile()) {
			System.out.println("[SEVERE] Failed to Enable DwarfCraft Skills and Effects)");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("[SEVERE] Failed to Enable DwarfCraft Skills and Effects)");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}

	}

	
	protected HashMap<Integer, Skill> getAllSkills() {
		HashMap<Integer, Skill> newSkillsArray = new HashMap<Integer,Skill>();
		for(Race r:raceMap.values()){
			for (Skill s : skillsArray.get(r).values()) {
				if (newSkillsArray.containsKey(s.getId())) continue;
				newSkillsArray.put(s.getId(), s.clone());
			}
		}
		return newSkillsArray;
	}
	
	protected HashMap<Integer, Skill> getAllSkills(Race race) {
		HashMap<Integer, Skill> newSkillsArray = new HashMap<Integer, Skill>();
		HashMap<Integer, Skill> raceList = skillsArray.get(race); 
		for (Skill s : raceList.values()) {
			newSkillsArray.put(s.getId(),s.clone());
		}
		return newSkillsArray;
	}
	
	protected Skill getGenericSkill(int skillId){
		for (Race race:raceMap.values()){
			for (Skill s:skillsArray.get(race).values()){
				if(s.getId() == skillId) return s.clone(); 
			}
		}	
		return null;
	}

	protected int getConfigSkillsVersion() {
		return configSkillsVersion;
	}

	protected String getDbPath() {
		return dbpath;
	}

	private void getDefaultValues() {
		if (configSkillsVersion == 0)              configSkillsVersion    = 100;
		if (configEffectsVersion == 0)             configEffectsVersion   = 100;
		if (configSkillsFileName == null)          configSkillsFileName   = "skills.config";
		if (configEffectsFileName == null)         configEffectsFileName  = "effects.config";
		if (configMessagesFileName == null)        configMessagesFileName = "messages.config";
		if (configGreeterMessagesFileName == null) configGreeterMessagesFileName = "greeters.config";
		if (dbpath == null)                        dbpath = "dwarfcraft.db";
	}

	private boolean readConfigFile() {
		try {
			System.out.println("DC Init: Reading Config File: " + configDirectory + configMainFileName);
			getDefaultValues();
			FileReader fr = new FileReader(configDirectory + configMainFileName);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				if (line.length() == 0) {
					line = br.readLine();
					continue;
				}
				if (line.charAt(0) == '#') {
					line = br.readLine();
					continue;
				}
				String[] theline = line.split(":");
				if (theline.length > 2) {
					line = br.readLine();
					continue;
				}
				if (theline[0].equalsIgnoreCase("Skills File Name"))
					configSkillsFileName = theline[1].trim();
				if (theline[0].equalsIgnoreCase("Effects File Name"))
					configEffectsFileName = theline[1].trim();
				if (theline[0].equalsIgnoreCase("Messages File Name"))
					configMessagesFileName = theline[1].trim();
				if (theline[0].equalsIgnoreCase("Greeter Messages File Name"))
					configGreeterMessagesFileName = theline[1].trim();
				if (theline[0].equalsIgnoreCase("Database File Name"))
					dbpath = configDirectory + theline[1].trim();
				if (theline[0].equalsIgnoreCase("Debug Level"))
					DwarfCraft.debugMessagesThreshold = Integer.parseInt(theline[1].trim());
				/*if (theline[0].equalsIgnoreCase("Default Race")){
					defaultRace = new Race(theline[1].trim());
					raceMap.put(defaultRace.getName(), defaultRace);}
				if (theline[0].equalsIgnoreCase("Default Race")){
					optOutRace = new Race(theline[1].trim());
					raceMap.put(optOutRace.getName(), optOutRace);}
					*/
				line = br.readLine();
			}
			defaultRace = new Race("Dwarf");
			raceMap.put(defaultRace.getName(), defaultRace);
			skillsArray.put(defaultRace, new HashMap<Integer, Skill>());

		} catch (FileNotFoundException fN) {
			fN.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean readEffectsFile() {
		System.out.println("DC Init: Reading effects file: " + configDirectory + configEffectsFileName);
		String line = "";
		try {
			FileReader fr = new FileReader(configDirectory + configEffectsFileName);
			BufferedReader br = new BufferedReader(fr);
			line = br.readLine();
			while (line != null) {
				if (line.length() == 0) {
					line = br.readLine();
					continue;
				}
				if (line.charAt(0) == '#') {
					line = br.readLine();
					continue;
				}
				if (line.charAt(0) == '^') {
					configEffectsVersion = Integer.parseInt(line.substring(2));
					line = br.readLine();
					continue;
				}
				String[] theline = line.split(",");
				if (theline.length < 20) {
					line = br.readLine();
					continue;
				}

				int idx = 0;
				int     effectId          = Integer.parseInt(theline[idx++]);
				double  baseValue         = Double.parseDouble(theline[idx++]);
				double  levelUpMultiplier = Double.parseDouble(theline[idx++]);
				double  noviceLevelUpMultiplier = Double.parseDouble(theline[idx++]);
				double  minValue          = Double.parseDouble(theline[idx++]);
				double  maxValue          = Double.parseDouble(theline[idx++]);
				boolean hasException      = (theline[idx++].equalsIgnoreCase("TRUE"));
				idx++;
				int     exceptionLow      = Integer.parseInt(theline[idx++]);
				int     exceptionHigh     = Integer.parseInt(theline[idx++]);
				double  exceptionValue    = Double.parseDouble(theline[idx++]);
				int     elfLevel          = Integer.parseInt(theline[idx++]);
				EffectType effectType     = EffectType.getEffectType(theline[idx++]);
				int     initiator         = Integer.parseInt(theline[idx++]);
				int     output            = Integer.parseInt(theline[idx++]);
				boolean toolRequired      = (theline[idx++].equalsIgnoreCase("TRUE"));

				int[] tooltable = { Integer.parseInt(theline[idx++]),
						            Integer.parseInt(theline[idx++]),
						            Integer.parseInt(theline[idx++]),
						            Integer.parseInt(theline[idx++]),
						            Integer.parseInt(theline[idx++]) 
					              };
				for (Race race:raceMap.values()){
					Skill skill = skillsArray.get(race).get(effectId/10);
					if(skill!=null) skill.getEffects().add(
								new Effect(effectId, baseValue,
										levelUpMultiplier,
										noviceLevelUpMultiplier, minValue,
										maxValue, hasException,
										exceptionLow, exceptionHigh,
										exceptionValue, elfLevel, effectType,
										initiator, output, toolRequired,
										tooltable));
				}
				line = br.readLine();
			}
			return true;
		} catch (FileNotFoundException fN) {
			fN.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected boolean readGreeterMessagesfile() {
		System.out.println("DC Init: Reading greeter messages file: "
				+ configDirectory + configGreeterMessagesFileName);
		try {
			getDefaultValues();
			FileReader fr = new FileReader(configDirectory + configGreeterMessagesFileName);
			BufferedReader br = new BufferedReader(fr);
			String messageId = br.readLine();
			while (messageId != null) {
				messageId = messageId.trim();
				String leftClick, rightClick;
				if (messageId.length() == 0) {
					messageId = br.readLine();
					continue;
				}
				if (messageId.charAt(0) == '#') {
					messageId = br.readLine();
					continue;
				}
				leftClick = br.readLine().trim();
				rightClick = br.readLine().trim();

				plugin.getDataManager().insertGreeterMessage(messageId,
						new GreeterMessage(leftClick, rightClick));
				messageId = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return true;

	}

	private boolean readMessagesFile() {
		System.out.println("DC Init: Reading general messages file: " + configDirectory + configMessagesFileName);
		try {
			getDefaultValues();
			FileReader fr = new FileReader(configDirectory + configMessagesFileName);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				if (line.length() == 0) {
					line = br.readLine();
					continue;
				}
				if (line.charAt(0) == '#') {
					line = br.readLine();
					continue;
				}
				String[] theline = line.split(":");
				if (theline.length > 2) {
					line = br.readLine();
					continue;
				}

				if (theline[0].equalsIgnoreCase("General Info"))
					Messages.GeneralInfo = theline[1].trim();
				if (theline[0].equalsIgnoreCase("Server Rules"))
					Messages.ServerRules = theline[1].trim();

				line = br.readLine();
			}

		} catch (FileNotFoundException fN) {
			fN.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Default to enum values if not found
			if (Messages.GeneralInfo == null)
				Messages.GeneralInfo = Messages.Fixed.GENERALHELPMESSAGE.getMessage();
			if (Messages.ServerRules == null)
				Messages.ServerRules = Messages.Fixed.SERVERRULESMESSAGE.getMessage();
		}
		return true;
	}

	private boolean readSkillsFile() {
		String line = "";
		System.out.println("DC Init: Reading skills file: " + configDirectory + configSkillsFileName);
		try {
			FileReader fr = new FileReader(configDirectory + configSkillsFileName);
			BufferedReader br = new BufferedReader(fr);
			line = br.readLine();
			while (line != null) {
				if (line.length() == 0) {
					line = br.readLine();
					continue;
				}
				if (line.charAt(0) == '#') {
					line = br.readLine();
					continue;
				}
				if (line.charAt(0) == '^') {
					configSkillsVersion = Integer.parseInt(line.substring(2));
					line = br.readLine();
					continue;
				}
				String[] theline = (line + ",Dwarf").split(",");
				if (theline.length < 12) {
					continue;
				}
				// Creating a new Skill
				Material trainerHeldMaterial = Material.AIR;

				int id = Integer.parseInt(theline[0]);
				String displayName = theline[1];
				// New skill initialized with level 0
				int level = 0;

				// Training cost stack array created, including "empty"
				// itemstacks of type 0 qty 0
				Material TrainingItem1Mat        = Material.getMaterial(Integer.parseInt(theline[2]));
				double   TrainingItem1BaseCost   = Double.parseDouble(theline[3]);
				int      TrainingItem1MaxAmount  = Integer.parseInt(theline[4]);
				Material TrainingItem2Mat        = Material.getMaterial(Integer.parseInt(theline[5]));
				double   TrainingItem2BaseCost   = Double.parseDouble(theline[6]);
				int      TrainingItem2MaxAmount  = Integer.parseInt(theline[7]);
				Material TrainingItem3Mat        = Material.getMaterial(Integer.parseInt(theline[8]));
				double   TrainingItem3BaseCost   = Double.parseDouble(theline[9]);
				int      TrainingItem3MaxAmount  = Integer.parseInt(theline[10]);
				trainerHeldMaterial = Material.getMaterial(Integer.parseInt(theline[11]));
				// Effects generated from effects file
				List<Effect> effects = new ArrayList<Effect>();
				// create the new skill in the skillsarray
				
				
				for (int i = 12;i<theline.length;i++){
					Race race = findRace(theline[i], true);
					skillsArray.get(race).put(id,new Skill(id, displayName, level, effects,
							TrainingItem1Mat, TrainingItem1BaseCost,
							TrainingItem1MaxAmount, TrainingItem2Mat,
							TrainingItem2BaseCost, TrainingItem2MaxAmount,
							TrainingItem3Mat, TrainingItem3BaseCost,
							TrainingItem3MaxAmount, trainerHeldMaterial));
				}
				

				line = br.readLine();
			}
			return true;
		} catch (FileNotFoundException fN) {
			fN.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected Race findRace(String string, boolean addNew) {
		for(String s: raceMap.keySet())
			if (s.equalsIgnoreCase(string)) return raceMap.get(s);
		if (!addNew) return null;
		Race newRace= new Race(string);
		raceMap.put(string, newRace);
		skillsArray.put(newRace, new HashMap<Integer, Skill>());
		return newRace;
	}

	public void setDefaultRace(Race defaultRace) {
		this.defaultRace = defaultRace;
	}

	public Race getDefaultRace() {
		return defaultRace;
	}

	public void setRaceMap(HashMap<String, Race> raceMap) {
		this.raceMap = raceMap;
	}

	public HashMap<String, Race> getRaceMap() {
		return raceMap;
	}


	public void setOptOutRace(Race optOutRace) {
		this.optOutRace = optOutRace;
	}

	public Race getOptOutRace() {
		return optOutRace;
	}

}
