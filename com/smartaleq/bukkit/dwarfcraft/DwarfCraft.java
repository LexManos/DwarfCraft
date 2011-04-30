package com.smartaleq.bukkit.dwarfcraft;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.Plugin;

import com.smartaleq.bukkit.dwarfcraft.events.*;
import com.smartaleq.bukkit.dwarfcraft.commands.*;

import org.martin.bukkit.npclib.*;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * 
 * DwarfCraft is a RPG-like plugin for minecraft (via Bukkit) that allows
 * players to improve their characters. Players(Dwarfs!) may pay materials to a
 * trainer to improve a skill level, which will provide benefits such as
 * increased weapon damage, decreased tool durability drop, increased drops from
 * blocks or mobs, etc.
 * 
 * Data used for this plugin comes from two places: On each load, a list of
 * skills and effects is pulled from flatfiles. Dwarf's skill levels and world
 * training zones are kept in database (currently supports only sqlite)
 * 
 * @author smartaleq
 * @author RCarretta
 * 
 */
public class DwarfCraft extends JavaPlugin {

	private final DCBlockListener   blockListener   = new DCBlockListener(this);
	private final DCPlayerListener  playerListener  = new DCPlayerListener(this);
	private final DCEntityListener  entityListener  = new DCEntityListener(this);
	private final DCVehicleListener vehicleListener = new DCVehicleListener(this);
	private final DCWorldListener   worldListener   = new DCWorldListener(this);
	private ConfigManager cm;
	private DataManager dm;
	private Out out;
	private NPCManager npcm; 
	private PermissionHandler permissionHandler;
	
	public static int debugMessagesThreshold = 10;

	public ConfigManager getConfigManager() {
		return cm;
	}

	public DataManager getDataManager() {
		return dm;
	}
	public NPCManager getNPCManager(){
		return npcm;
	}

	public Out getOut() {
		return out;
	}
	
	private boolean checkPermission(CommandSender sender, String name, boolean opOnly){
		if (permissionHandler == null)
			return (!opOnly || sender.isOp());
			
		if (sender instanceof Player)
			return permissionHandler.has((Player)sender, ("DwarfCraft." + (opOnly ? "op." : "norm.") + name).toLowerCase());
		
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String commandLabel, String[] args) {
		Command cmd = null;
		String name = command.getName();
		boolean hasNorm = checkPermission(sender, name, false);
		boolean hasOp   = checkPermission(sender, name, true);
		
		if     (hasNorm && name.equalsIgnoreCase("DCHelp"))      cmd = new CommandHelp(this);
		else if(hasNorm && name.equalsIgnoreCase("SkillSheet"))  cmd = new CommandSkillSheet(this);
		else if(hasNorm && name.equalsIgnoreCase("Tutorial"))    cmd = new CommandTutorial(this);
		else if(hasNorm && name.equalsIgnoreCase("Info"))        cmd = new CommandInfo(this);
		else if(hasNorm && name.equalsIgnoreCase("Rules"))       cmd = new CommandRules(this);
		else if(hasNorm && name.equalsIgnoreCase("DCCommands"))  cmd = new CommandDCCommands(this);
		else if(hasNorm && name.equalsIgnoreCase("SkillInfo"))   cmd = new CommandSkillInfo(this);
		else if(hasNorm && name.equalsIgnoreCase("Race"))        cmd = new CommandRace(this);
		else if(hasNorm && name.equalsIgnoreCase("EffectInfo"))  cmd = new CommandEffectInfo(this);
		
		else if(hasOp && name.equalsIgnoreCase("RemoveNext"))    cmd = new CommandRemoveNext(this);
		else if(hasOp && name.equalsIgnoreCase("Debug"))         cmd = new CommandDebug(this);
		else if(hasOp && name.equalsIgnoreCase("ListTrainers"))  cmd = new CommandListTrainers(this);
		else if(hasOp && name.equalsIgnoreCase("RemoveTrainer")) cmd = new CommandRemoveTrainer(this);
		else if(hasOp && name.equalsIgnoreCase("SetSkill"))      cmd = new CommandSetSkill(this);
		else if(hasOp && name.equalsIgnoreCase("CreateGreeter")) cmd = new CommandCreateGreeter(this);
		else if(hasOp && name.equalsIgnoreCase("CreateTrainer")) cmd = new CommandCreateTrainer(this);
		
		else if(hasOp && name.equalsIgnoreCase("DMem"))          cmd = new CommandDMem(this);
			
		if (cmd == null){
			sender.sendMessage("§4You do not have permission to use that.");
			return false;
		}
		return cmd.execute(sender, commandLabel, args);
	}

	/**
	 * Called upon disabling the plugin.
	 */
	@Override
	public void onDisable() {
		List<NPCEntity> npcs = npcm.getNPCs();
		for(NPCEntity npc : npcs){
			npcm.despawn(npc.name);
		}
	}

	/**
	 * Called upon enabling the plugin
	 */
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();

		pm.registerEvent(Event.Type.PLAYER_CHAT,     playerListener,  Priority.Normal,  this);
		pm.registerEvent(Event.Type.PLAYER_JOIN,     playerListener,  Priority.Normal,  this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener,  Priority.Low,     this);
		pm.registerEvent(Event.Type.PLAYER_QUIT,     playerListener,  Priority.Low,     this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener,  Priority.Normal,  this);
		pm.registerEvent(Event.Type.INVENTORY_OPEN,  playerListener,  Priority.Normal,  this);		
		pm.registerEvent(Event.Type.ENTITY_DAMAGE,   entityListener,  Priority.High,    this);
		pm.registerEvent(Event.Type.ENTITY_TARGET,   entityListener,  Priority.High,    this);
		pm.registerEvent(Event.Type.ENTITY_DEATH,    entityListener,  Priority.Low,     this);
		pm.registerEvent(Event.Type.BLOCK_BREAK,     blockListener,   Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGE,    blockListener,   Priority.Normal,  this);
		pm.registerEvent(Event.Type.VEHICLE_ENTER,   vehicleListener, Priority.Normal,  this);
		pm.registerEvent(Event.Type.VEHICLE_EXIT,    vehicleListener, Priority.Normal,  this);
		pm.registerEvent(Event.Type.VEHICLE_DAMAGE,  vehicleListener, Priority.Normal,  this);
		pm.registerEvent(Event.Type.VEHICLE_MOVE,    vehicleListener, Priority.Lowest,  this);
		pm.registerEvent(Event.Type.CHUNK_UNLOAD,    worldListener,   Priority.Low,     this);
		pm.registerEvent(Event.Type.WORLD_LOAD,      worldListener,   Priority.Low,     this);

		npcm = new NPCManager(this);
		cm   = new ConfigManager(this, getDataFolder().getAbsolutePath(), "DwarfCraft.config");
		dm   = new DataManager(this, cm);
		out  = new Out(this);
		
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

		if (this.permissionHandler == null) {
		    if (permissionsPlugin != null) {
		        this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
		    } else {
		        System.out.println("DwarfCraft: Permission system not detected, defaulting to OP");
		    }
		}
	      
		// readGreeterMessagesfile() depends on datamanager existing, so this
		// has to go here 
		if (!getConfigManager().readGreeterMessagesfile()) {
			System.out.println("[SEVERE] Failed to read DwarfCraft Greeter Messages)");
			getServer().getPluginManager().disablePlugin(this);
		}
		
		for(Player player : getServer().getOnlinePlayers()){
			DCPlayer dCPlayer = getDataManager().find(player);
			if (dCPlayer == null) 
				dCPlayer = getDataManager().createDwarf(player);
			if (!getDataManager().getDwarfData(dCPlayer)) 
				getDataManager().createDwarfData(dCPlayer);
		}

		System.out.println(getDescription().getName() + " version " + getDescription().getVersion() + " is enabled!");
	}

}
