package com.smartaleq.bukkit.dwarfcraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.FileOutputStream;
import org.bukkit.Material;
import org.jbls.LexManos.CSV.CSVReader;
import org.jbls.LexManos.CSV.CSVRecord;

public final class ConfigManager {

	private final DwarfCraft plugin;

	private final String configDirectory;

	private String configMainFileName;
	private String configSkillsFileName;
	private int configSkillsVersion;
	private String configEffectsFileName;
	private int configEffectsVersion;
	private String configMessagesFileName;
	private String cfgGreeterFile;
	private String dbpath;
	
	private HashMap<Race, HashMap<Integer, Skill>> skillsArray = new HashMap<Race,HashMap<Integer, Skill>>();
	private Race defaultRace;
	private Race optOutRace;
	private HashMap<String, Race> raceMap = new HashMap<String, Race>();
	
	public boolean sendGreeting = false;
	
	protected ConfigManager(DwarfCraft plugin, String directory,
			String paramsFileName) {
		this.plugin = plugin;
		if (!directory.endsWith(File.separator))
			directory += File.separator;
		configDirectory = directory;
		configMainFileName = paramsFileName;
		checkFiles(configDirectory);

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

	
	public HashMap<Integer, Skill> getAllSkills() {
		HashMap<Integer, Skill> newSkillsArray = new HashMap<Integer,Skill>();
		for(Race r:raceMap.values()){
			for (Skill s : skillsArray.get(r).values()) {
				if (newSkillsArray.containsKey(s.getId())) continue;
				newSkillsArray.put(s.getId(), s.clone());
			}
		}
		return newSkillsArray;
	}
	
	public HashMap<Integer, Skill> getAllSkills(Race race) {
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
		return configDirectory + dbpath;
	}

	private void getDefaultValues() {
		if (configSkillsVersion == 0)       configSkillsVersion    = 100;
		if (configEffectsVersion == 0)      configEffectsVersion   = 100;
		if (configSkillsFileName == null)   configSkillsFileName   = "skills.csv";
		if (configEffectsFileName == null)  configEffectsFileName  = "effects.csv";
		if (configMessagesFileName == null) configMessagesFileName = "messages.config";
		if (cfgGreeterFile == null)         cfgGreeterFile = "greeters.config";
		if (dbpath == null)                 dbpath = "dwarfcraft.db";
	}
	private void checkFiles(String path){
		File root = new File(path);
		if (!root.exists())
			root.mkdirs();
		try{
			File file = new File(root, "DwarfCraft.config");
			if (!file.exists()){
				file.createNewFile();
				CopyFile("/default_files/DwarfCraft.config", file);
			}
			
			readConfigFile();
			getDefaultValues();
			
			String[][] mfiles = { {configSkillsFileName,   "skills.csv"},
					              {configEffectsFileName,  "effects.csv"},
					              {configMessagesFileName, "messages.config"},
					              {dbpath,                 "dwarfcraft.db" },
					              {cfgGreeterFile,         "greeters.config"}
								};
			for(String[] mfile : mfiles){
				file = new File(root, mfile[0]);
				if (!file.exists()){
					file.createNewFile();
					CopyFile("/default_files/" + mfile[1], file);
				}
			}
		}catch(Exception e){
			System.out.println("DC: ERROR: Could not verify files: " + e.toString());
			e.printStackTrace();
		}
	}
	private void CopyFile(String name, File toFile) throws Exception{
		InputStream ins = ConfigManager.class.getResourceAsStream(name);
		OutputStream out = new FileOutputStream(toFile);

	      byte[] buf = new byte[1024];
	      int len;
	      while ((len = ins.read(buf)) > 0){
	        out.write(buf, 0, len);
	      }
	      out.flush();
	      ins.close();
	      out.close();
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
					cfgGreeterFile = theline[1].trim();
				if (theline[0].equalsIgnoreCase("Database File Name"))
					dbpath = theline[1].trim();
				if (theline[0].equalsIgnoreCase("Debug Level"))
					DwarfCraft.debugMessagesThreshold = Integer.parseInt(theline[1].trim());
				if (theline[0].equalsIgnoreCase("Send Login Greet"))
					sendGreeting = Boolean.parseBoolean(theline[1].trim());
				
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
		try {
			CSVReader csv = new CSVReader(configDirectory + configEffectsFileName);
			Iterator<CSVRecord> records = csv.getRecords();
			while(records.hasNext()){
				CSVRecord item = records.next();
				Effect effect = new Effect(item);
				for (Race race : raceMap.values()){
					Skill skill = skillsArray.get(race).get(effect.getId()/10);
					if(skill != null) {
						skill.getEffects().add(effect);
					}
				}
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
				+ configDirectory + cfgGreeterFile);
		try {
			getDefaultValues();
			FileReader fr = new FileReader(configDirectory + cfgGreeterFile);
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
			
			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) 
					continue;
				if (line.charAt(0) == '#')
					continue;
				
				if (line.indexOf(":") <= 0)
					continue;
				
				String name = line.substring(0, line.indexOf(":"));
				String message = line.substring(name.length() + 1);
				
				if (name.equalsIgnoreCase("General Info"))
					Messages.GeneralInfo = message;
				if (name.equalsIgnoreCase("Server Rules"))
					Messages.ServerRules = message;

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
		System.out.println("DC Init: Reading skills file: " + configDirectory + configSkillsFileName);
		try {
			CSVReader csv = new CSVReader(configDirectory + configSkillsFileName);
			configSkillsVersion = csv.getVersion();
			Iterator<CSVRecord> records = csv.getRecords();
			while(records.hasNext()){
				CSVRecord item = records.next();
				
				Skill skill = new Skill(
						item.getInt("ID"),
						item.getString("Name"),
						0, new ArrayList<Effect>(),
						new TrainingItem(
								Material.getMaterial(item.getInt("Item1")),
								item.getDouble("Item1Base"), item.getInt("Item1Max")
						),
						new TrainingItem(
								Material.getMaterial(item.getInt("Item2")),
								item.getDouble("Item2Base"), item.getInt("Item2Max")
						),
						new TrainingItem(
								Material.getMaterial(item.getInt("Item3")),
								item.getDouble("Item3Base"), item.getInt("Item3Max")
						),
						Material.getMaterial(item.getInt("Held"))
					);
				
				String[] races = item.getString("Races").split(" ");
				for(String race : races){
					Race rc = findRace(race, true);
					skillsArray.get(rc).put(skill.getId(), skill);
				}
				
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
