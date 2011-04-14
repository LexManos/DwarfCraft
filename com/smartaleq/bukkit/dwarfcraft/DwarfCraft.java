package com.smartaleq.bukkit.dwarfcraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

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
	
	protected static int debugMessagesThreshold = 10;

	protected ConfigManager getConfigManager() {
		return cm;
	}

	protected DataManager getDataManager() {
		return dm;
	}

	// TODO: deprecate this, there has to be a better way - move Out to Dwarf?
	protected Out getOut() {
		return out;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String commandLabel, String[] args) {
		return (new DCCommand(this, command.getName())).execute(sender, commandLabel, args);
	}

	/**
	 * Called upon disabling the plugin.
	 */
	@Override
	public void onDisable() {
	}

	/**
	 * Called upon enabling the plugin
	 */
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();

		pm.registerEvent(Event.Type.PLAYER_CHAT,     playerListener,  Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN,     playerListener,  Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener,  Priority.Low,    this);
		pm.registerEvent(Event.Type.PLAYER_QUIT,     playerListener,  Priority.Low,    this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener,  Priority.Normal, this);		
		pm.registerEvent(Event.Type.ENTITY_DAMAGE,   entityListener,  Priority.High,   this);
		pm.registerEvent(Event.Type.ENTITY_TARGET,   entityListener,  Priority.High,   this);
		pm.registerEvent(Event.Type.ENTITY_DEATH,    entityListener,  Priority.Low,    this);
		pm.registerEvent(Event.Type.BLOCK_BREAK,     blockListener,   Priority.High,   this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGE,    blockListener,   Priority.Normal, this);
		pm.registerEvent(Event.Type.VEHICLE_ENTER,   vehicleListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.VEHICLE_EXIT,    vehicleListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.VEHICLE_DAMAGE,  vehicleListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.VEHICLE_MOVE,    vehicleListener, Priority.Lowest, this);
		pm.registerEvent(Event.Type.CHUNK_UNLOAD,    worldListener,   Priority.Low,    this);
		pm.registerEvent(Event.Type.WORLD_LOAD,      worldListener,   Priority.Low,    this);

		cm = new ConfigManager(this, getDataFolder().getAbsolutePath(), "DwarfCraft.config");
		dm = new DataManager(this, cm);
		out = new Out(this);
		
		// readGreeterMessagesfile() depends on datamanager existing, so this
		// has to go here 
		if (!getConfigManager().readGreeterMessagesfile()) {
			System.out.println("[SEVERE] Failed to read DwarfCraft Greeter Messages)");
			getServer().getPluginManager().disablePlugin(this);
		}


		System.out.println(getDescription().getName() + " version " + getDescription().getVersion() + " is enabled!");
	}

}
